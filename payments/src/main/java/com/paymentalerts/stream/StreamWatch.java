package com.paymentalerts.stream;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.config.PaymentAlertsConfig;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Turns a chat line into a stream alert.
 *
 * <p>The alert can be triggered by a chat announcement naming Lumindev or Cyclone and
 * indicating that the channel is live, or by a confirmed live-state transition from a
 * channel notifier. Chat announcements may be a channel link or text such as "is live".
 *
 * <p>Duplicate announcements are suppressed for five minutes per identical line, with a
 * ninety-second cooldown per channel so a chat notice and live poll cannot double-fire.
 */
public final class StreamWatch {
    private static final long SAME_LINE_MS = 300_000L;
    private static final long COOLDOWN_MS = 90_000L;

    private static String lastLine = "";
    private static long lastLineAt;
    private static final Map<String, Long> LAST_CHANNEL_AT = new HashMap<>();

    private StreamWatch() {
    }

    /** True when the line fired an alert. */
    public static boolean handle(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        PaymentAlertsConfig config = PaymentAlertsConfig.get();
        if (!config.streamChatDetect) {
            return false;
        }
        String line = raw.toLowerCase(Locale.ROOT);
        String channel = mentionedChannel(line);
        if (channel == null || !looksLive(line)) {
            return false;
        }
        return announce(channel, titleFrom(raw), line);
    }

    /** Fires from a confirmed live-state transition (as opposed to a chat message). */
    public static boolean announce(String channel, String title) {
        if (channel == null || channel.isBlank()) {
            return false;
        }
        return announce(channel, title, "live-event:" + channel.toLowerCase(Locale.ROOT) + ":" +
                (title == null ? "" : title.toLowerCase(Locale.ROOT)));
    }

    private static synchronized boolean announce(String channel, String title, String dedupeKey) {
        long now = System.currentTimeMillis();
        if (dedupeKey.equals(lastLine) && now - lastLineAt < SAME_LINE_MS) {
            return false;
        }
        String channelKey = channel.toLowerCase(Locale.ROOT);
        Long lastChannelAt = LAST_CHANNEL_AT.get(channelKey);
        if (lastChannelAt != null && now - lastChannelAt < COOLDOWN_MS) {
            return false;
        }
        lastLine = dedupeKey;
        lastLineAt = now;
        LAST_CHANNEL_AT.put(channelKey, now);
        AlertManager.stream(channel, title);
        PaymentAlertsClient.LOGGER.info("Stream alert: {} — {}", channel, title);
        return true;
    }

    private static String mentionedChannel(String line) {
        if (line.contains("lumindevdsmp") || line.contains("lumindev")) {
            return "Lumindev";
        }
        if (line.contains("cyclonelwk") || line.contains("cyclone skies")
                || line.contains("cyclone is live") || line.contains("cyclone's live")) {
            return "Cyclone";
        }
        return null;
    }

    private static boolean looksLive(String line) {
        boolean link = line.contains("youtube.com/") || line.contains("youtu.be/")
                || line.contains("twitch.tv/");
        boolean liveWord = line.contains("live") || line.contains("stream")
                || line.contains("watching") || line.contains("started")
                || line.contains("is on") || line.contains("now playing");
        return link || liveWord;
    }

    /** The announcement without the link, trimmed down to something readable. */
    private static String titleFrom(String raw) {
        String text = raw.replaceAll("https?://\\S+", "").replaceAll("\\s{2,}", " ").trim();
        if (text.length() > 96) {
            text = text.substring(0, 95).trim() + "\u2026";
        }
        return text;
    }
}
