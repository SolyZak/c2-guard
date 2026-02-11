package com.eden.eden_crm_sec_crm_back.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

public final class TenantRoleKeycloakName {

    private TenantRoleKeycloakName() {}

    /**
     * Keycloak realm role technical name:
     * C{customerId}__{roleSlug}__{randomSuffix}
     */
    public static String build(Long customerId, String roleDisplayName) {
        if (customerId == null) throw new IllegalArgumentException("customerId is required");
        String slug = slugify(roleDisplayName);

        // short random suffix (12 hex chars) to ensure uniqueness and allow renames/reuse
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        return "C" + customerId + "__" + slug + "__" + suffix;
    }

    /**
     * Unicode-friendly slug:
     * - keeps letters/digits from any language (\p{L}\p{N})
     * - replaces whitespace with underscore
     * - keeps underscore, dash, colon
     * - removes other symbols
     */
    public static String slugify(String input) {
        if (input == null) throw new IllegalArgumentException("role name is required");

        String s = input.trim();
        if (s.isBlank()) throw new IllegalArgumentException("role name is required");

        // Normalize (helps with weird unicode forms)
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);

        // Lowercase in a stable way
        s = s.toLowerCase(Locale.ROOT);

        // whitespace -> underscore
        s = s.replaceAll("\\s+", "_");

        // keep unicode letters/digits plus _ : -
        s = s.replaceAll("[^\\p{L}\\p{N}_:\\-]", "");

        // avoid starting/ending with separators (optional)
        s = s.replaceAll("^[_:\\-]+", "");
        s = s.replaceAll("[_:\\-]+$", "");

        if (s.isBlank()) {
            // fallback if name was only symbols
            s = "role";
        }
        return s;
    }
}