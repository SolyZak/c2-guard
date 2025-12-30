package com.eden.eden_crm_sec_crm_back.utils;

import com.eden.eden_crm_sec_crm_back.enums.CustomTimezone;

import java.time.*;
import java.util.Map;

public class DateUtils {

    public static ZoneId getTimeWithTimezone(CustomTimezone timezone) {
        final Map<CustomTimezone, ZoneId> ZONE_MAP = Map.of(
                CustomTimezone.EGYPT, ZoneId.of("Africa/Cairo"),
                CustomTimezone.SAUDI_ARABIA, ZoneId.of("Asia/Riyadh"),
                CustomTimezone.EMIRATES, ZoneId.of("Asia/Dubai"),
                CustomTimezone.UTC, ZoneId.of("UTC")
        );
        return ZONE_MAP.get(timezone);
    }

    public static OffsetTime now(CustomTimezone timezone) {
        return OffsetDateTime.now(getTimeWithTimezone(timezone)).toOffsetTime();
    }

    public static OffsetDateTime nowDateTime(CustomTimezone timezone) {
        return OffsetDateTime.now(getTimeWithTimezone(timezone));
    }

    public static OffsetTime withTimeZone(CustomTimezone timezone, OffsetTime offsetTime) {
        if (offsetTime == null) return null;
        ZoneId zoneId = getTimeWithTimezone(timezone);
        OffsetDateTime originalDateTime = LocalDate.now().atTime(offsetTime);
        ZoneOffset offset = zoneId.getRules().getOffset(Instant.now());
        ZonedDateTime targetZoned = originalDateTime.atZoneSameInstant(zoneId);

        return targetZoned.toOffsetDateTime().toOffsetTime().withOffsetSameInstant(offset);
    }

    public static OffsetTime toLocalTime(CustomTimezone timezone, LocalTime time) {
        if (time == null) return null;
        ZoneId zoneId = getTimeWithTimezone(timezone);
        ZoneOffset offset = zoneId.getRules().getOffset(Instant.now());

        return OffsetTime.of(time, offset);
    }

    public static LocalTime toLocalTime(CustomTimezone targetTimezone, OffsetTime time) {
        if (time == null) return null;
        ZoneId targetZoneId = getTimeWithTimezone(targetTimezone);

        OffsetDateTime originalDateTime = time.atDate(LocalDate.now());
        ZonedDateTime targetZoned = originalDateTime.atZoneSameInstant(targetZoneId);

        return targetZoned.toLocalTime();
    }
}
