package com.paymentalerts.skin;

import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.skin.SkinFetcher;

public final class SkinDoll {
    public static final float DANCE_BEAT = 0.25f;
    private final SkinFetcher.Skin skin;
    private boolean crouching;

    SkinDoll(SkinFetcher.Skin skin) {
        this.skin = skin;
    }

    public void dance(float f) {
        dance(f, 0);
    }

    public void dance(float f, int i) {
        boolean z;
        if (f <= 0.0f) {
            this.crouching = false;
            return;
        }
        switch (i) {
            case HudFireworks.GOLD /* 1 */:
                if (((int) (f / 0.12f)) % 2 != 1) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                float f2 = f % 0.9f;
                if (f2 >= 0.12f && (f2 < 0.24f || f2 >= 0.36f)) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            case 3:
                if (((int) (f / 0.45f)) % 2 != 1) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
            default:
                if (((int) (f / 0.25f)) % 2 != 1) {
                    z = false;
                    break;
                } else {
                    z = true;
                    break;
                }
        }
        this.crouching = z;
    }

    public void setCrouching(boolean z) {
        this.crouching = z;
    }

    public boolean isCrouching() {
        return this.crouching;
    }


}
