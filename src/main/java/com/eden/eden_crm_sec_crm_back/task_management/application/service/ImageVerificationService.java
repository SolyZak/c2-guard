package com.eden.eden_crm_sec_crm_back.task_management.application.service;

import com.eden.eden_crm_sec_crm_back.clients.ImageComparisonFeignClient;
import com.eden.eden_crm_sec_crm_back.clients.dto.PcdAiVerifyRequest;
import com.eden.eden_crm_sec_crm_back.clients.dto.PcdAiVerifyResponse;
import com.eden.eden_crm_sec_crm_back.exception.BusinessException;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.request.ImageVerifyRequest;
import com.eden.eden_crm_sec_crm_back.task_management.application.dto.response.ImageVerifyResponse;
import com.eden.eden_crm_sec_crm_back.task_management.domain.valueobject.ImageQualityIssue;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageVerificationService {

    private final ImageComparisonFeignClient imageComparisonFeignClient;

    /**
     * Calls the PCD-AI verify endpoint synchronously and maps the response
     * into an {@link ImageVerifyResponse} with derived {@code missingQuality}.
     *
     * <p>Mapping rules:
     * <ul>
     *   <li>{@code sameObject = false} → OBJECT issue; other fields are {@code null}</li>
     *   <li>{@code sameObject = true}  → evaluate each quality flag:
     *     <ul>
     *       <li>{@code isBlurry = true}   → BLURRING issue</li>
     *       <li>{@code isBright = false}  → BRIGHTNESS issue</li>
     *       <li>{@code isAligned = false} → FRAMING issue</li>
     *     </ul>
     *   </li>
     *   <li>If no issues are detected → {@code missingQuality} is {@code null} (perfect quality)</li>
     * </ul>
     */
    public ImageVerifyResponse verify(ImageVerifyRequest request) {

        // ── Validate inputs ──────────────────────────────────────────────
        validateRequest(request);

        // ── Call external AI service ─────────────────────────────────────
        PcdAiVerifyResponse aiResponse = callAiService(request);

        // ── Validate AI response ─────────────────────────────────────────
        validateAiResponse(aiResponse);

        // ── Map to domain response ───────────────────────────────────────
        return mapToVerifyResponse(aiResponse);
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Input validation
    // ═══════════════════════════════════════════════════════════════════════

    private void validateRequest(ImageVerifyRequest request) {
        if (request.getImageUrl1() == null || request.getImageUrl1().isBlank()) {
            throw new BusinessException("imageUrl1 must not be blank", HttpStatus.BAD_REQUEST);
        }

        if (request.getImageBase64_2() == null || request.getImageBase64_2().isBlank()) {
            throw new BusinessException("imageBase64_2 must not be blank", HttpStatus.BAD_REQUEST);
        }

        // Guard against obviously malformed URLs
        String url = request.getImageUrl1().trim().toLowerCase();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new BusinessException("imageUrl1 must be a valid HTTP or HTTPS URL", HttpStatus.BAD_REQUEST);
        }

        // Guard against excessively large base64 payloads (>20 MB raw ≈ ~27 MB base64)
        if (request.getImageBase64_2().length() > 27_000_000) {
            throw new BusinessException("imageBase64_2 exceeds the maximum allowed size", HttpStatus.BAD_REQUEST);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  External AI service call with comprehensive error handling
    // ═══════════════════════════════════════════════════════════════════════

    private PcdAiVerifyResponse callAiService(ImageVerifyRequest request) {
        PcdAiVerifyRequest aiRequest = PcdAiVerifyRequest.builder()
                .imageUrl1(request.getImageUrl1())
                .imageBase64_2(request.getImageBase64_2())
                .build();

        try {
            log.info("Calling PCD-AI verify endpoint for imageUrl1={}", request.getImageUrl1());

            PcdAiVerifyResponse response = imageComparisonFeignClient.verify(aiRequest);

            log.info("PCD-AI verify responded successfully for imageUrl1={}", request.getImageUrl1());

            return response;

        } catch (FeignException.BadRequest e) {
            log.error("PCD-AI verify returned 400 Bad Request: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service rejected the request — verify that the image URL is accessible and the base64 image is valid",
                    HttpStatus.BAD_REQUEST);

        } catch (FeignException.Unauthorized | FeignException.Forbidden e) {
            log.error("PCD-AI verify returned {}: {}", e.status(), e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service authentication failed — please contact system administrator",
                    HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (FeignException.NotFound e) {
            log.error("PCD-AI verify endpoint not found: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI verification service is not available — endpoint not found",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (FeignException.UnprocessableEntity e) {
            log.error("PCD-AI verify returned 422 Unprocessable Entity: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service could not process the provided images — ensure images are valid and not corrupted",
                    HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (FeignException.TooManyRequests e) {
            log.warn("PCD-AI verify returned 429 Too Many Requests: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service is rate-limited — please try again later",
                    HttpStatus.TOO_MANY_REQUESTS);

        } catch (FeignException.InternalServerError e) {
            log.error("PCD-AI verify returned 500 Internal Server Error: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service encountered an internal error — please try again later",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (FeignException.BadGateway e) {
            log.error("PCD-AI verify returned 502 Bad Gateway: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service is temporarily unreachable — please try again later",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (FeignException.ServiceUnavailable e) {
            log.error("PCD-AI verify returned 503 Service Unavailable: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service is currently unavailable — please try again later",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (FeignException.GatewayTimeout e) {
            log.error("PCD-AI verify returned 504 Gateway Timeout: {}", e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service timed out — the images may be too large or the service is under heavy load",
                    HttpStatus.GATEWAY_TIMEOUT);

        } catch (RetryableException e) {
            log.error("PCD-AI verify connection failed (timeout/connection refused): {}", e.getMessage(), e);
            throw new BusinessException(
                    "Unable to connect to the AI service — please verify the service is running and try again",
                    HttpStatus.SERVICE_UNAVAILABLE);

        } catch (FeignException e) {
            log.error("PCD-AI verify returned unexpected HTTP status {}: {}", e.status(), e.contentUTF8(), e);
            throw new BusinessException(
                    "AI service returned an unexpected error (HTTP " + e.status() + ") — please try again later",
                    HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("Unexpected error calling PCD-AI verify: {}", e.getMessage(), e);
            throw new BusinessException(
                    "An unexpected error occurred while calling the AI verification service",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  AI response validation
    // ═══════════════════════════════════════════════════════════════════════

    private void validateAiResponse(PcdAiVerifyResponse aiResponse) {
        if (aiResponse == null) {
            log.error("PCD-AI verify returned null response body");
            throw new BusinessException(
                    "AI service returned an empty response — please try again later",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (aiResponse.getSameObject() == null) {
            log.error("PCD-AI verify returned null sameObject field: {}", aiResponse);
            throw new BusinessException(
                    "AI service returned an invalid response — sameObject field is missing",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // When sameObject is true, the quality fields should not all be null
        if (Boolean.TRUE.equals(aiResponse.getSameObject())) {
            if (aiResponse.getIsBlurry() == null
                    && aiResponse.getIsBright() == null
                    && aiResponse.getIsAligned() == null) {
                log.warn("PCD-AI verify returned sameObject=true but all quality fields are null — "
                        + "treating as valid response with unknown quality");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Response mapping — AI booleans → ImageQualityIssue list
    // ═══════════════════════════════════════════════════════════════════════

    private ImageVerifyResponse mapToVerifyResponse(PcdAiVerifyResponse aiResponse) {
        boolean sameObject = Boolean.TRUE.equals(aiResponse.getSameObject());

        // Case 1: Different object — skip quality checks
        if (!sameObject) {
            List<ImageQualityIssue> issues = new ArrayList<>();
            issues.add(ImageQualityIssue.OBJECT);

            return ImageVerifyResponse.builder()
                    .sameObject(false)
                    .isBlurry(null)
                    .isBright(null)
                    .isAligned(null)
                    .missingQuality(issues)
                    .build();
        }

        // Case 2: Same object — evaluate each quality dimension
        List<ImageQualityIssue> issues = new ArrayList<>();

        // isBlurry: true = blurry (bad) → BLURRING issue
        if (Boolean.TRUE.equals(aiResponse.getIsBlurry())) {
            issues.add(ImageQualityIssue.BLURRING);
        }

        // isBright: false = not bright enough (bad) → BRIGHTNESS issue
        if (Boolean.FALSE.equals(aiResponse.getIsBright())) {
            issues.add(ImageQualityIssue.BRIGHTNESS);
        }

        // isAligned: false = not aligned (bad) → FRAMING issue
        if (Boolean.FALSE.equals(aiResponse.getIsAligned())) {
            issues.add(ImageQualityIssue.FRAMING);
        }

        // Case 3: Same object with no issues → null (perfect quality)
        List<ImageQualityIssue> missingQuality = issues.isEmpty() ? null : issues;

        return ImageVerifyResponse.builder()
                .sameObject(true)
                .isBlurry(aiResponse.getIsBlurry())
                .isBright(aiResponse.getIsBright())
                .isAligned(aiResponse.getIsAligned())
                .missingQuality(missingQuality)
                .build();
    }
}