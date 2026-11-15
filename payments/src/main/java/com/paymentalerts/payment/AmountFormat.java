package com.paymentalerts.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class AmountFormat {
    private static final String[] SUFFIXES = {"", "K", "M", "B", "T"};

    private AmountFormat() {
    }

    public static String compact(double d) {
        double d2 = d;
        int i = 0;
        while (d2 >= 1000.0d && i < SUFFIXES.length - 1) {
            d2 /= 1000.0d;
            i++;
        }
        return "$" + BigDecimal.valueOf(d2).setScale(d2 >= 100.0d ? 0 : d2 >= 10.0d ? 1 : 2, RoundingMode.DOWN).stripTrailingZeros().toPlainString() + SUFFIXES[i];
    }
}
