package com.eden.eden_crm_sec_crm_back.utils;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
        Locale locale = LocaleContextHolder.getLocale(); // Detect current locale
        return messageSource.getMessage(key, null, locale);
    }

    public static String getMessage(String key, @Nullable Object[] args) {
        Locale locale = LocaleContextHolder.getLocale(); // Detect current locale
        return messageSource.getMessage(key, args, locale);
    }
}
