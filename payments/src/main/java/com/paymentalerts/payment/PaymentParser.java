package com.paymentalerts.payment;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PaymentParser {
    public static final String DEFAULT_PATTERN = "^[^A-Za-z0-9_\\[<.*]{0,4}\\s*(?:\\[[^\\]]{0,40}\\]\\s*)?(?<name>[.*]?[A-Za-z0-9_]{1,16})\\s+(?:has\\s+|just\\s+)?paid\\s+you\\s*:?\\s*\\$?\\s*(?<amount>\\d[\\d,]*(?:\\.\\d+)?)\\s*(?<suffix>thousand|mil(?:lion)?|bil(?:lion)?|tril(?:lion)?|[kmbt])?(?![a-z0-9])";
    private static final Pattern LEGACY_FORMATTING = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private final Pattern pattern;
    private final boolean usingDefault;

    public PaymentParser(String str) {
        Pattern compile;
        boolean z = false;
        try {
            compile = Pattern.compile(str, 66);
        } catch (IllegalArgumentException e) {
            compile = Pattern.compile(DEFAULT_PATTERN, 66);
            z = true;
        }
        if (!compile.namedGroups().containsKey("name") || !compile.namedGroups().containsKey("amount")) {
            throw new IllegalArgumentException("pattern needs (?<name>...) and (?<amount>...) groups");
        }
        this.pattern = compile;
        this.usingDefault = z || DEFAULT_PATTERN.equals(str);
    }

    public boolean isUsingDefault() {
        return this.usingDefault;
    }

    public Optional<Payment> parse(String str) {
        if (str == null || str.isEmpty()) {
            return Optional.empty();
        }
        Matcher matcher = this.pattern.matcher(LEGACY_FORMATTING.matcher(str).replaceAll("").strip());
        if (!matcher.find()) {
            return Optional.empty();
        }
        String group = matcher.group("name");
        OptionalDouble amount = toAmount(matcher.group("amount"), this.pattern.namedGroups().containsKey("suffix") ? matcher.group("suffix") : null);
        if (group == null || group.isEmpty() || amount.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Payment(group, amount.getAsDouble()));
    }

    public static OptionalDouble parseAmount(String str) {
        if (str == null) {
            return OptionalDouble.empty();
        }
        Matcher matcher = Pattern.compile("^\\s*\\$?\\s*(\\d[\\d,]*(?:\\.\\d+)?)\\s*(thousand|mil(?:lion)?|bil(?:lion)?|tril(?:lion)?|[kmbt])?\\s*$", 2).matcher(str);
        if (!matcher.matches()) {
            return OptionalDouble.empty();
        }
        return toAmount(matcher.group(1), matcher.group(2));
    }

    private static OptionalDouble toAmount(String str, String str2) {
        if (str == null) {
            return OptionalDouble.empty();
        }
        try {
            double doubleValue = new BigDecimal(str.replace(",", "")).doubleValue() * multiplier(str2);
            return (doubleValue <= 0.0d || !Double.isFinite(doubleValue)) ? OptionalDouble.empty() : OptionalDouble.of(doubleValue);
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    private static double multiplier(String str) {
        if (str == null || str.isEmpty()) {
            return 1.0d;
        }
        String lowerCase = str.toLowerCase(Locale.ROOT);
        if (lowerCase.startsWith("tril") || lowerCase.equals("t")) {
            return 1.0E12d;
        }
        if (lowerCase.startsWith("bil") || lowerCase.equals("b")) {
            return 1.0E9d;
        }
        if (lowerCase.startsWith("mil") || lowerCase.equals("m")) {
            return 1000000.0d;
        }
        if (lowerCase.equals("thousand") || lowerCase.equals("k")) {
            return 1000.0d;
        }
        return 1.0d;
    }
}
