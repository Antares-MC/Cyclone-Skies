package com.paymentalerts.fx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.paymentalerts.config.PaymentAlertsConfig;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public final class ScreenShake {
    private static final List<Impulse> IMPULSES = new ArrayList();
    private static final float[] PHASE = new float[9];
    private static final long EPOCH = System.nanoTime();
    private static final float MAX_PITCH_YAW = 3.2f;
    private static final float MAX_ROLL = 2.2f;
    private static final float MAX_HUD_OFFSET = 7.0f;

    static {
        Random random = new Random();
        for (int i = 0; i < PHASE.length; i++) {
            PHASE[i] = random.nextFloat() * 6.2831855f;
        }
    }

    private ScreenShake() {
    }

    private static final record Impulse(long startNanos, long durationNanos, float strength, boolean rising) {


        

        

        




    }

    public static void add(float f, float f2, float f3) {
        add(f, f2, f3, false);
    }

    public static void addRising(float f, float f2, float f3) {
        add(f, f2, f3, true);
    }

    private static void add(float f, float f2, float f3, boolean z) {
        IMPULSES.add(new Impulse(System.nanoTime() + ((long) (f * 1.0E9d)), (long) (f2 * 1.0E9d), f3, z));
    }

    public static void clear() {
        IMPULSES.clear();
    }

    public static boolean isActive() {
        return !IMPULSES.isEmpty();
    }

    public static float intensity() {
        float min;
        float f;
        if (IMPULSES.isEmpty()) {
            return 0.0f;
        }
        long nanoTime = System.nanoTime();
        float f2 = 0.0f;
        Iterator<Impulse> it = IMPULSES.iterator();
        while (it.hasNext()) {
            Impulse next = it.next();
            long j = nanoTime - next.startNanos;
            if (j >= 0) {
                if (j >= next.durationNanos) {
                    it.remove();
                } else {
                    float f3 = j / 1.0E9f;
                    float f4 = j / next.durationNanos;
                    if (next.rising) {
                        min = f4;
                        f = f4;
                    } else {
                        min = Math.min(1.0f, f3 / 0.04f) * (1.0f - f4);
                        f = 1.0f - f4;
                    }
                    f2 += next.strength * min * f;
                }
            }
        }
        PaymentAlertsConfig paymentAlertsConfig = PaymentAlertsConfig.get();
        if (paymentAlertsConfig.screenShakeEnabled) {
            return Math.min(1.6f, f2) * paymentAlertsConfig.shakeStrength;
        }
        return 0.0f;
    }

    public static void applyToCamera(PoseStack poseStack) {
        float intensity = intensity();
        if (intensity <= 5.0E-4f) {
            return;
        }
        float time = time();
        poseStack.mulPose(Axis.XP.rotationDegrees(intensity * MAX_PITCH_YAW * noise(time, 0)));
        poseStack.mulPose(Axis.YP.rotationDegrees(intensity * MAX_PITCH_YAW * noise(time, 3)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(intensity * MAX_ROLL * noise(time, 6)));
    }

    public static float hudOffsetX() {
        return intensity() * MAX_HUD_OFFSET * noise(time() + 11.3f, 3);
    }

    public static float hudOffsetY() {
        return intensity() * MAX_HUD_OFFSET * noise(time() + 5.7f, 0);
    }

    private static float time() {
        return (System.nanoTime() - EPOCH) / 1.0E9f;
    }

    private static float noise(float f, int i) {
        return (0.55f * ((float) Math.sin((f * 45.9f) + PHASE[i]))) + (0.3f * ((float) Math.sin((f * 82.3f) + PHASE[i + 1]))) + (0.15f * ((float) Math.sin((f * 148.9f) + PHASE[i + 2])));
    }
}
