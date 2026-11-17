package com.paymentalerts.stream;

import com.paymentalerts.PaymentAlertsClient;
import com.paymentalerts.alert.AlertManager;
import com.paymentalerts.config.PaymentAlertsConfig;
import java.util.Locale;

/**
 * Turns a chat line into a stream alert.
 *
 * <p>There is no client-side way to ask YouTube whether a channel is live without an API
 * key and a network round trip on the render thread, so the alert is driven by what the
 * client can actually see: a chat line that names the channel and says it is live, or a
 * link to it. That is also the shape server announcement plugins use, which is where the
 * message normally comes from.
 *
 * <p>Repeats are suppressed: the same line will not fire twice inside five minutes, and
 * nothing fires twice inside ninety seconds at all.
 */
public final class StreamWatch {
    private static final long SAME_LINE_MS = 300_000L;
    private static final long COOLDOWN_MS = 90_000L;

    private static String lastLine = "";
    private static long lastLineAt;
    private static long lastAnyAt;

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
        if (!looksLive(line)) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (line.equals(lastLine) && now - lastLineAt < SAME_LINE_MS) {
            return false;
        }
        if (now - lastAnyAt < COOLDOWN_MS) {
            return false;
        }
        lastLine = line;
        lastLineAt = now;
        lastAnyAt = now;
        String channel = config.streamChannel == null || config.streamChannel.isBlank()
                ? "Lumindev"
                : config.streamChannel;
        AlertManager.stream(channel, titleFrom(raw));
        PaymentAlertsClient.LOGGER.info("Stream alert: {}", raw);
        return true;
    }

    private static boolean looksLive(String line) {
        String handle = "lumindevdsmp";
        boolean names = line.contains(handle) || line.contains("lumindev");
        if (!names) {
            return false;
        }
        boolean link = line.contains("youtube.com/") || line.contains("youtu.be/")
                || line.contains("twitch.tv/");
        // A bare link to the channel is an announcement on its own; a name only counts
        // when the line also says something is live.
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
