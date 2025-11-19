package com.sportclub.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for handling time operations with GMT+7 timezone
 */
public class TimeUtil {

    private static final TimeZone GMT_PLUS_7 = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private static final ZoneId ZONE_GMT_PLUS_7 = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get current timestamp in GMT+7 timezone
     */
    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now(ZONE_GMT_PLUS_7));
    }

    /**
     * Create timestamp from date and time strings in GMT+7
     * 
     * @param dateTime format: "yyyy-MM-dd HH:mm:ss"
     */
    public static Timestamp createTimestamp(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, FORMATTER);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_GMT_PLUS_7);
        return Timestamp.from(zonedDateTime.toInstant());
    }

    /**
     * Format timestamp to readable string in GMT+7 timezone
     */
    public static String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(GMT_PLUS_7);
        return sdf.format(new Date(timestamp.getTime()));
    }

    /**
     * Create timestamp from individual date/time components
     */
    public static Timestamp createTimestamp(int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_GMT_PLUS_7);
        return Timestamp.from(zonedDateTime.toInstant());
    }

    /**
     * Add hours to a timestamp
     */
    public static Timestamp addHours(Timestamp timestamp, int hours) {
        return new Timestamp(timestamp.getTime() + (hours * 3600000L));
    }

    /**
     * Add minutes to a timestamp
     */
    public static Timestamp addMinutes(Timestamp timestamp, int minutes) {
        return new Timestamp(timestamp.getTime() + (minutes * 60000L));
    }

    /**
     * Get current time zone display name
     */
    public static String getCurrentTimeZoneInfo() {
        return GMT_PLUS_7.getDisplayName() + " (" + GMT_PLUS_7.getID() + ")";
    }
}