package com.paymentalerts.fx;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Arrays;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix3x2fc;

public final class Draw2D {
    private static GuiGraphics owner;
    private static int vertices;
    private static float minX;
    private static float minY;
    private static float maxX;
    private static float maxY;
    private static final Matrix3x2fc IDENTITY = new Matrix3x2f();
    private static float[] xy = new float[4096];
    private static int[] colors = new int[2048];

    private Draw2D() {
    }

    private static final record ShapeBatch(float[] xy, int[] colors, int count, ScreenRectangle bounds) implements GuiElementRenderState {


        

        

        





        public void buildVertices(VertexConsumer vertexConsumer) {
            for (int i = 0; i < this.count; i++) {
                vertexConsumer.addVertexWith2DPose(Draw2D.IDENTITY, this.xy[i * 2], this.xy[(i * 2) + 1]).setColor(this.colors[i]);
            }
        }

        public RenderPipeline pipeline() {
            return RenderPipelines.GUI;
        }

        public TextureSetup textureSetup() {
            return TextureSetup.noTexture();
        }

        public ScreenRectangle scissorArea() {
            return null;
        }
    }

    public static int argb(float f, int i) {
        return (Math.round(Math.max(0.0f, Math.min(1.0f, f)) * 255.0f) << 24) | (i & 16777215);
    }

    public static int lerpRgb(int i, int i2, float f) {
        float max = Math.max(0.0f, Math.min(1.0f, f));
        return (Math.round(((i >> 16) & 255) + ((((i2 >> 16) & 255) - ((i >> 16) & 255)) * max)) << 16) | (Math.round(((i >> 8) & 255) + ((((i2 >> 8) & 255) - ((i >> 8) & 255)) * max)) << 8) | Math.round((i & 255) + (((i2 & 255) - (i & 255)) * max));
    }

    public static int scaleRgb(int i, float f) {
        return (Math.min(255, Math.round(((i >> 16) & 255) * f)) << 16) | (Math.min(255, Math.round(((i >> 8) & 255) * f)) << 8) | Math.min(255, Math.round((i & 255) * f));
    }

    public static int hsv(float f, float f2, float f3) {
        float f4;
        float f5;
        float f6;
        float floor = (f - ((float) Math.floor(f))) * 6.0f;
        int i = (int) floor;
        float f7 = floor - i;
        float f8 = f3 * (1.0f - f2);
        float f9 = f3 * (1.0f - (f2 * f7));
        float f10 = f3 * (1.0f - (f2 * (1.0f - f7)));
        switch (i) {
            case HudFireworks.CLASSIC /* 0 */:
                f4 = f3;
                f5 = f10;
                f6 = f8;
                break;
            case HudFireworks.GOLD /* 1 */:
                f4 = f9;
                f5 = f3;
                f6 = f8;
                break;
            case HudFireworks.RAINBOW_FINALE /* 2 */:
                f4 = f8;
                f5 = f3;
                f6 = f10;
                break;
            case 3:
                f4 = f8;
                f5 = f9;
                f6 = f3;
                break;
            case 4:
                f4 = f10;
                f5 = f8;
                f6 = f3;
                break;
            default:
                f4 = f3;
                f5 = f8;
                f6 = f9;
                break;
        }
        return (Math.round(f4 * 255.0f) << 16) | (Math.round(f5 * 255.0f) << 8) | Math.round(f6 * 255.0f);
    }

    public static void flush(GuiGraphics guiGraphics) {
        if (vertices == 0 || owner == null) {
            owner = null;
            vertices = 0;
            return;
        }
        int floor = (int) Math.floor(minX);
        int floor2 = (int) Math.floor(minY);
        ((com.paymentalerts.mixin.GuiGraphicsAccessor) owner).paymentalerts$guiRenderState().submitGuiElement(new ShapeBatch(Arrays.copyOf(xy, vertices * 2), Arrays.copyOf(colors, vertices), vertices, new ScreenRectangle(floor, floor2, Math.max(1, ((int) Math.ceil(maxX)) - floor), Math.max(1, ((int) Math.ceil(maxY)) - floor2))));
        owner = null;
        vertices = 0;
    }

    public static void text(GuiGraphics guiGraphics, Font font, Component component, int i, int i2, int i3) {
        flush(guiGraphics);
        if ((i3 >>> 24) != 0) {
            guiGraphics.drawString(font, component, i, i2, i3);
        }
    }

    public static void fill(GuiGraphics guiGraphics, int i, int i2, int i3, int i4, int i5) {
        flush(guiGraphics);
        guiGraphics.fill(i, i2, i3, i4, i5);
    }

    private static void triangleSetup(GuiGraphics guiGraphics) {
        if (owner != guiGraphics) {
            if (owner != null) {
                flush(owner);
            }
            owner = guiGraphics;
            vertices = 0;
            minY = Float.MAX_VALUE;
            minX = Float.MAX_VALUE;
            maxY = -3.4028235E38f;
            maxX = -3.4028235E38f;
        }
    }

    private static void vertex(float f, float f2, int i) {
        if ((vertices * 2) + 2 > xy.length) {
            xy = Arrays.copyOf(xy, xy.length * 2);
            colors = Arrays.copyOf(colors, colors.length * 2);
        }
        xy[vertices * 2] = f;
        xy[(vertices * 2) + 1] = f2;
        colors[vertices] = i;
        vertices++;
        if (f < minX) {
            minX = f;
        }
        if (f > maxX) {
            maxX = f;
        }
        if (f2 < minY) {
            minY = f2;
        }
        if (f2 > maxY) {
            maxY = f2;
        }
    }

    public static void triangle(GuiGraphics guiGraphics, float f, float f2, int i, float f3, float f4, int i2, float f5, float f6, int i3) {
        if ((i >>> 24) == 0 && (i2 >>> 24) == 0 && (i3 >>> 24) == 0) {
            return;
        }
        triangleSetup(guiGraphics);
        Matrix3x2fStack pose = guiGraphics.pose();
        float m00 = (pose.m00() * f) + (pose.m10() * f2) + pose.m20();
        float m01 = (pose.m01() * f) + (pose.m11() * f2) + pose.m21();
        float m002 = (pose.m00() * f3) + (pose.m10() * f4) + pose.m20();
        float m012 = (pose.m01() * f3) + (pose.m11() * f4) + pose.m21();
        float m003 = (pose.m00() * f5) + (pose.m10() * f6) + pose.m20();
        float m013 = (pose.m01() * f5) + (pose.m11() * f6) + pose.m21();
        if (((m002 - m00) * (m013 - m012)) - ((m012 - m01) * (m003 - m002)) > 0.0f) {
            vertex(m00, m01, i);
            vertex(m003, m013, i3);
            vertex(m002, m012, i2);
            vertex(m002, m012, i2);
            return;
        }
        vertex(m00, m01, i);
        vertex(m002, m012, i2);
        vertex(m003, m013, i3);
        vertex(m003, m013, i3);
    }

    public static void convexQuad(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i) {
        if ((i >>> 24) == 0) {
            return;
        }
        triangleSetup(guiGraphics);
        Matrix3x2fStack pose = guiGraphics.pose();
        float m00 = (pose.m00() * f) + (pose.m10() * f2) + pose.m20();
        float m01 = (pose.m01() * f) + (pose.m11() * f2) + pose.m21();
        float m002 = (pose.m00() * f3) + (pose.m10() * f4) + pose.m20();
        float m012 = (pose.m01() * f3) + (pose.m11() * f4) + pose.m21();
        float m003 = (pose.m00() * f5) + (pose.m10() * f6) + pose.m20();
        float m013 = (pose.m01() * f5) + (pose.m11() * f6) + pose.m21();
        float m004 = (pose.m00() * f7) + (pose.m10() * f8) + pose.m20();
        float m014 = (pose.m01() * f7) + (pose.m11() * f8) + pose.m21();
        if (((m002 - m00) * (m013 - m012)) - ((m012 - m01) * (m003 - m002)) > 0.0f) {
            vertex(m00, m01, i);
            vertex(m004, m014, i);
            vertex(m003, m013, i);
            vertex(m002, m012, i);
            return;
        }
        vertex(m00, m01, i);
        vertex(m002, m012, i);
        vertex(m003, m013, i);
        vertex(m004, m014, i);
    }

    public static void quad(GuiGraphics guiGraphics, float f, float f2, int i, float f3, float f4, int i2, float f5, float f6, int i3, float f7, float f8, int i4) {
        triangle(guiGraphics, f, f2, i, f3, f4, i2, f5, f6, i3);
        triangle(guiGraphics, f, f2, i, f5, f6, i3, f7, f8, i4);
    }

    public static void rect(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i) {
        quad(guiGraphics, f, f2, i, f, f4, i, f3, f4, i, f3, f2, i);
    }

    public static void gradientV(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i, int i2) {
        quad(guiGraphics, f, f2, i, f, f4, i2, f3, f4, i2, f3, f2, i);
    }

    public static void gradientH(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i, int i2) {
        quad(guiGraphics, f, f2, i, f, f4, i, f3, f4, i2, f3, f2, i2);
    }

    public static void ellipse(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i, int i2) {
        if (f3 <= 0.05f || f4 <= 0.05f) {
            return;
        }
        int max = Math.max(12, Math.min(56, ((int) (Math.max(f3, f4) * 0.9f)) + 10));
        float f5 = f + f3;
        float f6 = f2;
        for (int i3 = 1; i3 <= max; i3++) {
            double d = (6.283185307179586d * i3) / max;
            float cos = f + (((float) Math.cos(d)) * f3);
            float sin = f2 + (((float) Math.sin(d)) * f4);
            triangle(guiGraphics, f, f2, i, f5, f6, i2, cos, sin, i2);
            f5 = cos;
            f6 = sin;
        }
    }

    public static void circle(GuiGraphics guiGraphics, float f, float f2, float f3, int i, int i2) {
        ellipse(guiGraphics, f, f2, f3, f3, i, i2);
    }

    public static void glow(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, int i) {
        circle(guiGraphics, f, f2, f3, argb(f4, i), argb(0.0f, i));
    }

    public static void ring(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, int i) {
        ringEllipse(guiGraphics, f, f2, f3, f3, f4, f5, i);
    }

    public static void ringEllipse(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6, int i) {
        ringArc(guiGraphics, f, f2, f3, f4, f5, f6, i, 0.0f, 6.2831855f);
    }

    public static void ringArc(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6, int i, float f7, float f8) {
        if (f6 <= 0.003f || f3 <= 0.5f) {
            return;
        }
        float f9 = f8 - f7;
        float f10 = f5 / 2.0f;
        int argb = argb(f6, i);
        int argb2 = argb(0.0f, i);
        int max = Math.max(8, (int) ((Math.max(24, Math.min(96, ((int) (Math.max(f3, f4) * 0.6f)) + 24)) * f9) / 6.283185307179586d));
        float f11 = f4 / f3;
        for (int i2 = 0; i2 < max; i2++) {
            double d = f7 + ((f9 * i2) / max);
            double d2 = f7 + ((f9 * (i2 + 1)) / max);
            float cos = (float) Math.cos(d);
            float sin = (float) Math.sin(d);
            float cos2 = (float) Math.cos(d2);
            float sin2 = (float) Math.sin(d2);
            float max2 = Math.max(0.0f, f3 - f10);
            float f12 = f3 + f10;
            quad(guiGraphics, f + (cos * max2), f2 + (sin * max2 * f11), argb2, f + (cos * f3), f2 + (sin * f3 * f11), argb, f + (cos2 * f3), f2 + (sin2 * f3 * f11), argb, f + (cos2 * max2), f2 + (sin2 * max2 * f11), argb2);
            quad(guiGraphics, f + (cos * f3), f2 + (sin * f3 * f11), argb, f + (cos * f12), f2 + (sin * f12 * f11), argb2, f + (cos2 * f12), f2 + (sin2 * f12 * f11), argb2, f + (cos2 * f3), f2 + (sin2 * f3 * f11), argb);
        }
    }

    public static void rotatedRect(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, int i) {
        float cos = (float) Math.cos(f5);
        float sin = (float) Math.sin(f5);
        float f6 = f3 / 2.0f;
        float f7 = f4 / 2.0f;
        float f8 = ((-f6) * cos) + (f7 * sin);
        float f9 = ((-f6) * sin) - (f7 * cos);
        float f10 = (f6 * cos) + (f7 * sin);
        float f11 = (f6 * sin) - (f7 * cos);
        quad(guiGraphics, f + f8, f2 + f9, i, f + f10, f2 + f11, i, f - f8, f2 - f9, i, f - f10, f2 - f11, i);
    }

    public static void streak(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6, int i) {
        float f7 = f3 - f;
        float f8 = f4 - f2;
        float sqrt = (float) Math.sqrt((f7 * f7) + (f8 * f8));
        if (sqrt < 0.01f) {
            glow(guiGraphics, f, f2, f5, f6, i);
            return;
        }
        float f9 = (((-f8) / sqrt) * f5) / 2.0f;
        float f10 = ((f7 / sqrt) * f5) / 2.0f;
        int argb = argb(f6, i);
        int argb2 = argb(f6 * 0.15f, i);
        int argb3 = argb(0.0f, i);
        quad(guiGraphics, f + f9, f2 + f10, argb3, f, f2, argb2, f3, f4, argb, f3 + f9, f4 + f10, argb3);
        quad(guiGraphics, f, f2, argb2, f - f9, f2 - f10, argb3, f3 - f9, f4 - f10, argb3, f3, f4, argb);
    }

    public static void softLine(GuiGraphics guiGraphics, float f, float f2, float f3, float f4, float f5, float f6, int i) {
        float f7 = f3 - f;
        float f8 = f4 - f2;
        float sqrt = (float) Math.sqrt((f7 * f7) + (f8 * f8));
        if (sqrt < 0.01f || f6 <= 0.003f) {
            return;
        }
        float f9 = (((-f8) / sqrt) * f5) / 2.0f;
        float f10 = ((f7 / sqrt) * f5) / 2.0f;
        int argb = argb(f6, i);
        int argb2 = argb(0.0f, i);
        quad(guiGraphics, f + f9, f2 + f10, argb2, f, f2, argb, f3, f4, argb, f3 + f9, f4 + f10, argb2);
        quad(guiGraphics, f, f2, argb, f - f9, f2 - f10, argb2, f3 - f9, f4 - f10, argb2, f3, f4, argb);
    }

    public static void vignette(GuiGraphics guiGraphics, int i, int i2, float f, float f2, int i3) {
        int argb = argb(f2, i3);
        int argb2 = argb(0.0f, i3);
        float min = Math.min(i, i2) * f;
        gradientV(guiGraphics, 0.0f, 0.0f, i, min, argb, argb2);
        gradientV(guiGraphics, 0.0f, i2 - min, i, i2, argb2, argb);
        gradientH(guiGraphics, 0.0f, 0.0f, min, i2, argb, argb2);
        gradientH(guiGraphics, i - min, 0.0f, i, i2, argb2, argb);
    }
}
