package com.paymentalerts.skin;

import com.paymentalerts.fx.HudFireworks;
import com.paymentalerts.skin.DollModel;
import java.util.List;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.resources.Identifier;

public abstract class DollRenderState implements PictureInPictureRenderState {
    public static final int HUD_SLOTS = 6;
    public static final int SCREEN_SLOTS = 3;
    final DollModel model;
    final DollModel.Pose pose;
    final Identifier texture;
    final float rotationX;
    final float rotationY;
    final float pivotY;
    private final float scale;
    private final int x0;
    private final int y0;
    private final int x1;
    private final int y1;
    private final ScreenRectangle scissor;
    private final ScreenRectangle bounds;
    public static final List<Class<? extends DollRenderState>> SLOT_CLASSES = List.of(Slot0.class, Slot1.class, Slot2.class, Slot3.class, Slot4.class, Slot5.class, Slot6.class, Slot7.class, Slot8.class);

    static final record Params(DollModel model, DollModel.Pose pose, Identifier texture, float rotationX, float rotationY, float pivotY, float scale, int x0, int y0, int x1, int y1, ScreenRectangle scissor) {


        

        

        












    }

    DollRenderState(Params params) {
        this.model = params.model();
        this.pose = params.pose();
        this.texture = params.texture();
        this.rotationX = params.rotationX();
        this.rotationY = params.rotationY();
        this.pivotY = params.pivotY();
        this.scale = params.scale();
        this.x0 = params.x0();
        this.y0 = params.y0();
        this.x1 = params.x1();
        this.y1 = params.y1();
        this.scissor = params.scissor();
        this.bounds = PictureInPictureRenderState.getBounds(this.x0, this.y0, this.x1, this.y1, this.scissor);
    }

    public int x0() {
        return this.x0;
    }

    public int y0() {
        return this.y0;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public float scale() {
        return this.scale;
    }

    public ScreenRectangle scissorArea() {
        return this.scissor;
    }

    public ScreenRectangle bounds() {
        return this.bounds;
    }

    static DollRenderState create(int i, Params params) {
        switch (i) {
            case HudFireworks.CLASSIC /* 0 */:
                return new Slot0(params);
            case HudFireworks.GOLD /* 1 */:
                return new Slot1(params);
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                return new Slot2(params);
            case 3:
                return new Slot3(params);
            case 4:
                return new Slot4(params);
            case 5:
                return new Slot5(params);
            case HUD_SLOTS /* 6 */:
                return new Slot6(params);
            case 7:
                return new Slot7(params);
            default:
                return new Slot8(params);
        }
    }

    static final class Slot0 extends DollRenderState {
        Slot0(Params params) {
            super(params);
        }
    }

    static final class Slot1 extends DollRenderState {
        Slot1(Params params) {
            super(params);
        }
    }

    static final class Slot2 extends DollRenderState {
        Slot2(Params params) {
            super(params);
        }
    }

    static final class Slot3 extends DollRenderState {
        Slot3(Params params) {
            super(params);
        }
    }

    static final class Slot4 extends DollRenderState {
        Slot4(Params params) {
            super(params);
        }
    }

    static final class Slot5 extends DollRenderState {
        Slot5(Params params) {
            super(params);
        }
    }

    static final class Slot6 extends DollRenderState {
        Slot6(Params params) {
            super(params);
        }
    }

    static final class Slot7 extends DollRenderState {
        Slot7(Params params) {
            super(params);
        }
    }

    static final class Slot8 extends DollRenderState {
        Slot8(Params params) {
            super(params);
        }
    }
}
