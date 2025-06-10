package com.eden.eden_crm_sec_crm_back.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
public class MessageUtil {

    private static MessageSource messageSource;

    // Constructor injection of MessageSource
    public MessageUtil(MessageSource messageSource) {
        MessageUtil.messageSource = messageSource;
    }

    /**
     * Retrieve a localized message based on the key and the current locale.
     *
     * @param key the message key
     * @return the localized message
     */
    public static String getMessage(String key) {
        try {
            Locale locale = LocaleContextHolder.getLocale(); // Detect current locale
            return messageSource.getMessage(key, null, locale);
        } catch (Exception e) {
            log.error("[MessageUtil::getMessage] can't get message by key, error: {}", e.getMessage());
        }
        return key;
    }

    public static String getMessage(String key, @Nullable Object[] args) {
        try {
            Locale locale = LocaleContextHolder.getLocale(); // Detect current locale
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            log.error("[MessageUtil::getMessage] can't get message by key & args, error: {}", e.getMessage());
        }
        return key;
    }
}
