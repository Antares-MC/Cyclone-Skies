package com.paymentalerts.stats;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class StatsFormat {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.getDefault());
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault());

    private StatsFormat() {
    }

    public static String dateTime(long j) {
        return DATE_TIME.format(Instant.ofEpochMilli(j).atZone(ZoneId.systemDefault()));
    }

    public static String time(long j) {
        return TIME.format(Instant.ofEpochMilli(j).atZone(ZoneId.systemDefault()));
    }

    public static String date(long j) {
        return DATE.format(Instant.ofEpochMilli(j).atZone(ZoneId.systemDefault()));
    }

    public static String span(long j, long j2, boolean z) {
        String dateTime = dateTime(j);
        if (z) {
            return dateTime + " - now";
        }
        return dateTime + " - " + (Instant.ofEpochMilli(j).atZone(ZoneId.systemDefault()).toLocalDate().equals(Instant.ofEpochMilli(j2).atZone(ZoneId.systemDefault()).toLocalDate()) ? time(j2) : dateTime(j2));
    }

    public static String duration(long j) {
        long max = Math.max(0L, j) / 60000;
        if (max < 1) {
            return "<1m";
        }
        if (max < 60) {
            return max + "m";
        }
        long j2 = max / 60;
        if (j2 < 24) {
            String.format(Locale.ROOT, "%02d", Long.valueOf(max % 60));
            return j2 + "h " + j2 + "m";
        }
        long j3 = j2 / 24;
        long j4 = j2 % 24;
        return j3 + "d " + j3 + "h";
    }

    public static String payments(int i) {
        return i + (i == 1 ? " payment" : " payments");
    }
}
