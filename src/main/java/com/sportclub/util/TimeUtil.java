package com.sportclub.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    private static final TimeZone GMT_PLUS_7 = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
    private static final ZoneId ZONE_GMT_PLUS_7 = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now(ZONE_GMT_PLUS_7));
    }

    public static Timestamp createTimestamp(String dateTime) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, FORMATTER);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_GMT_PLUS_7);
        return Timestamp.from(zonedDateTime.toInstant());
    }

    public static String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(GMT_PLUS_7);
        return sdf.format(new Date(timestamp.getTime()));
    }

    public static Timestamp createTimestamp(int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZONE_GMT_PLUS_7);
        return Timestamp.from(zonedDateTime.toInstant());
    }

    public static Timestamp addHours(Timestamp timestamp, int hours) {
        return new Timestamp(timestamp.getTime() + (hours * 3600000L));
    }

    public static Timestamp addMinutes(Timestamp timestamp, int minutes) {
        return new Timestamp(timestamp.getTime() + (minutes * 60000L));
    }

    public static String getCurrentTimeZoneInfo() {
        return GMT_PLUS_7.getDisplayName() + " (" + GMT_PLUS_7.getID() + ")";
    }
}