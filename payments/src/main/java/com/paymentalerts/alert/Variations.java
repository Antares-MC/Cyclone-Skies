package com.paymentalerts.alert;

import java.util.Random;

final class Variations {
    private static final Random RANDOM = new Random();

    private Variations() {
    }

    static int pick(int i, int i2) {
        if (i >= 0) {
            return i % i2;
        }
        return RANDOM.nextInt(i2);
    }

    static boolean chance(float f) {
        return RANDOM.nextFloat() < f;
    }
}
