package com.paymentalerts.stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paymentalerts.PaymentAlertsClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;

public final class PaymentStats {
    private static final int MAX_SESSIONS = 300;
    private static final long SAVE_EVERY_MS = 10000;
    private static final long HEARTBEAT_MS = 60000;
    public long since;
    public double total;
    public int count;
    public double biggestAmount;
    public String biggestFrom;
    public long biggestAt;
    public Map<String, Payer> payers = new LinkedHashMap();
    public List<Session> sessions = new ArrayList();
    public long lastSeen;
    private transient Session active;
    private transient boolean dirty;
    private transient long lastSave;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("paymentalerts-stats.json");
    private static PaymentStats current = new PaymentStats();

    public static final class Payer {
        public String name;
        public double total;
        public int count;
        public long last;
    }

    public static final class Session {
        public String server;
        public String address;
        public long joined;
        public long left;
        public double total;
        public int count;
        public double biggestAmount;
        public String biggestFrom;
        public Map<String, Payer> payers = new LinkedHashMap();

        public boolean isOpen() {
            return this.left == 0;
        }

        public long end(long j) {
            return this.left == 0 ? j : this.left;
        }
    }

    public static PaymentStats get() {
        return current;
    }

    public static Session activeSession() {
        return current.active;
    }

    public static void load() {
        PaymentStats paymentStats = null;
        if (Files.exists(FILE, new LinkOption[0])) {
            try {
                BufferedReader newBufferedReader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8);
                try {
                    paymentStats = (PaymentStats) GSON.fromJson(newBufferedReader, PaymentStats.class);
                    if (newBufferedReader != null) {
                        newBufferedReader.close();
                    }
                } finally {
                }
            } catch (Exception e) {
                PaymentAlertsClient.LOGGER.error("Could not read {}, starting fresh stats (the old file is kept as .broken)", FILE, e);
                try {
                    Files.copy(FILE, FILE.resolveSibling("paymentalerts-stats.json.broken"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e2) {
                }
            }
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (paymentStats == null) {
            paymentStats = new PaymentStats();
            paymentStats.since = currentTimeMillis;
        }
        paymentStats.sanitize();
        for (Session session : paymentStats.sessions) {
            if (session.isOpen()) {
                session.left = Math.max(session.joined, paymentStats.lastSeen > 0 ? paymentStats.lastSeen : session.joined);
            }
        }
        current = paymentStats;
        save();
    }

    private void sanitize() {
        if (this.payers == null) {
            this.payers = new LinkedHashMap();
        }
        if (this.sessions == null) {
            this.sessions = new ArrayList();
        }
        this.sessions.removeIf(session -> {
            return session == null;
        });
        for (Session session2 : this.sessions) {
            if (session2.payers == null) {
                session2.payers = new LinkedHashMap();
            }
            if (session2.server == null) {
                session2.server = "Unknown server";
            }
        }
        if (this.since <= 0) {
            this.since = this.sessions.isEmpty() ? System.currentTimeMillis() : this.sessions.get(0).joined;
        }
    }

    public static void startSession(String str, String str2) {
        long currentTimeMillis = System.currentTimeMillis();
        endSession(currentTimeMillis);
        Session session = new Session();
        session.server = str;
        session.address = str2;
        session.joined = currentTimeMillis;
        current.sessions.add(session);
        while (current.sessions.size() > MAX_SESSIONS) {
            current.sessions.remove(0);
        }
        current.active = session;
        current.lastSeen = currentTimeMillis;
        save();
    }

    public static void endSession() {
        endSession(System.currentTimeMillis());
    }

    private static void endSession(long j) {
        if (current.active != null) {
            current.active.left = j;
            current.active = null;
            current.lastSeen = j;
            save();
        }
    }

    public static void record(String str, double d) {
        if (str == null || str.isEmpty() || d <= 0.0d || !Double.isFinite(d)) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        PaymentStats paymentStats = current;
        paymentStats.total += d;
        paymentStats.count++;
        if (d > paymentStats.biggestAmount) {
            paymentStats.biggestAmount = d;
            paymentStats.biggestFrom = str;
            paymentStats.biggestAt = currentTimeMillis;
        }
        add(paymentStats.payers, str, d, currentTimeMillis);
        Session session = paymentStats.active;
        if (session != null) {
            session.total += d;
            session.count++;
            if (d > session.biggestAmount) {
                session.biggestAmount = d;
                session.biggestFrom = str;
            }
            add(session.payers, str, d, currentTimeMillis);
        }
        paymentStats.lastSeen = currentTimeMillis;
        paymentStats.dirty = true;
    }

    private static void add(Map<String, Payer> map, String str, double d, long j) {
        Payer computeIfAbsent = map.computeIfAbsent(str.toLowerCase(Locale.ROOT), str2 -> {
            return new Payer();
        });
        computeIfAbsent.name = str;
        computeIfAbsent.total += d;
        computeIfAbsent.count++;
        computeIfAbsent.last = j;
    }

    public static void tick() {
        long currentTimeMillis = System.currentTimeMillis();
        PaymentStats paymentStats = current;
        if (paymentStats.active != null && currentTimeMillis - paymentStats.lastSeen >= HEARTBEAT_MS) {
            paymentStats.lastSeen = currentTimeMillis;
            paymentStats.dirty = true;
        }
        if (paymentStats.dirty && currentTimeMillis - paymentStats.lastSave >= SAVE_EVERY_MS) {
            save();
        }
    }

    public static List<Payer> top(Map<String, Payer> map) {
        List<Payer> arrayList = new ArrayList<>(map.values());
        arrayList.sort(Comparator.comparingDouble((Payer payer) -> payer.total).reversed().thenComparing((Payer payer2) -> payer2.name, String.CASE_INSENSITIVE_ORDER));
        return arrayList;
    }

    public static List<Session> newestFirst() {
        ArrayList arrayList = new ArrayList(current.sessions);
        Collections.reverse(arrayList);
        return arrayList;
    }

    public static void reset() {
        Session session = current.active;
        PaymentStats paymentStats = new PaymentStats();
        long currentTimeMillis = System.currentTimeMillis();
        paymentStats.since = currentTimeMillis;
        paymentStats.lastSeen = currentTimeMillis;
        if (session != null) {
            Session session2 = new Session();
            session2.server = session.server;
            session2.address = session.address;
            session2.joined = currentTimeMillis;
            paymentStats.sessions.add(session2);
            paymentStats.active = session2;
        }
        current = paymentStats;
        save();
    }

    public static void save() {
        PaymentStats paymentStats = current;
        paymentStats.lastSave = System.currentTimeMillis();
        paymentStats.dirty = false;
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            Path resolveSibling = FILE.resolveSibling("paymentalerts-stats.json.tmp");
            BufferedWriter newBufferedWriter = Files.newBufferedWriter(resolveSibling, StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                GSON.toJson(paymentStats, newBufferedWriter);
                if (newBufferedWriter != null) {
                    newBufferedWriter.close();
                }
                try {
                    Files.move(resolveSibling, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    Files.move(resolveSibling, FILE, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
            }
        } catch (IOException e2) {
            PaymentAlertsClient.LOGGER.error("Could not write {}", FILE, e2);
        }
    }
}
