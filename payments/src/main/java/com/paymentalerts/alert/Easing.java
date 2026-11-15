package com.paymentalerts.alert;

final class Easing {
    private Easing() {
    }

    static float clamp01(float f) {
        if (f < 0.0f) {
            return 0.0f;
        }
        return Math.min(1.0f, f);
    }

    static float outCubic(float f) {
        float clamp01 = 1.0f - clamp01(f);
        return 1.0f - ((clamp01 * clamp01) * clamp01);
    }

    static float inCubic(float f) {
        float clamp01 = clamp01(f);
        return clamp01 * clamp01 * clamp01;
    }

    static float outBack(float f) {
        float clamp01 = clamp01(f) - 1.0f;
        return 1.0f + ((1.70158f + 1.0f) * clamp01 * clamp01 * clamp01) + (1.70158f * clamp01 * clamp01);
    }

    static int argb(float f, int i) {
        return (Math.round(clamp01(f) * 255.0f) << 24) | (i & 16777215);
    }

    static int lerpRgb(int i, int i2, float f) {
        float clamp01 = clamp01(f);
        return (Math.round(((i >> 16) & 255) + ((((i2 >> 16) & 255) - ((i >> 16) & 255)) * clamp01)) << 16) | (Math.round(((i >> 8) & 255) + ((((i2 >> 8) & 255) - ((i >> 8) & 255)) * clamp01)) << 8) | Math.round((i & 255) + (((i2 & 255) - (i & 255)) * clamp01));
    }
}
