package dev.valence.emote;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class EmotePose {
   public final float[] head = new float[3];
   public final float[] body = new float[3];
   public final float[] rArm = new float[3];
   public final float[] lArm = new float[3];
   public final float[] rLeg = new float[3];
   public final float[] lLeg = new float[3];
   public final float[] rArmOff = new float[3];
   public final float[] lArmOff = new float[3];
   public final float[] rLegOff = new float[3];
   public final float[] lLegOff = new float[3];
   public final float[] headOff = new float[3];
   public final float[] bend = new float[4];
   public final float[] bendAxis = new float[4];
   public float lift;
   public float forward;
   public float side;
   public float pitch;
   public float yaw;
   public float roll;
   public float pivot = 0.9F;
   public float sx = 1.0F;
   public float sy = 1.0F;
   public float sz = 1.0F;
   public float headScale = 1.0F;
   public float headSX = 1.0F;
   public float headSY = 1.0F;
   public final float[] propPos = new float[3];
   public int prop;
   public float propA;
   public float propB;
   public static final int SIZE = 57;
   private float[][] parts;

   public float[] toArray(float[] a) {
      int i = 0;

      for (float[] v : this.parts()) {
         a[i++] = v[0];
         a[i++] = v[1];
         a[i++] = v[2];
      }

      for (int k = 0; k < 4; k++) {
         a[i++] = this.bend[k];
         a[i++] = this.bendAxis[k];
      }

      a[i++] = this.lift;
      a[i++] = this.forward;
      a[i++] = this.side;
      a[i++] = this.pitch;
      a[i++] = this.yaw;
      a[i++] = this.roll;
      a[i++] = this.sx;
      a[i++] = this.sy;
      a[i++] = this.sz;
      a[i++] = this.headScale;
      a[i++] = this.headSX;
      a[i++] = this.headSY;
      a[i++] = this.propPos[0];
      a[i++] = this.propPos[1];
      a[i++] = this.propPos[2];
      a[i] = this.propA;
      return a;
   }

   public void fromArray(float[] a) {
      int i = 0;

      for (float[] v : this.parts()) {
         v[0] = a[i++];
         v[1] = a[i++];
         v[2] = a[i++];
      }

      for (int k = 0; k < 4; k++) {
         this.bend[k] = a[i++];
         this.bendAxis[k] = a[i++];
      }

      this.lift = a[i++];
      this.forward = a[i++];
      this.side = a[i++];
      this.pitch = a[i++];
      this.yaw = a[i++];
      this.roll = a[i++];
      this.sx = a[i++];
      this.sy = a[i++];
      this.sz = a[i++];
      this.headScale = a[i++];
      this.headSX = a[i++];
      this.headSY = a[i++];
      this.propPos[0] = a[i++];
      this.propPos[1] = a[i++];
      this.propPos[2] = a[i++];
      this.propA = a[i];
   }

   private float[][] parts() {
      if (this.parts == null) {
         this.parts = new float[][]{
            this.head, this.body, this.rArm, this.lArm, this.rLeg, this.lLeg, this.rArmOff, this.lArmOff, this.rLegOff, this.lLegOff, this.headOff
         };
      }

      return this.parts;
   }

   public EmotePose head(float x, float y, float z) {
      set(this.head, x, y, z);
      return this;
   }

   public EmotePose rArm(float x, float y, float z) {
      set(this.rArm, x, y, z);
      return this;
   }

   public EmotePose lArm(float x, float y, float z) {
      set(this.lArm, x, y, z);
      return this;
   }

   public EmotePose rLeg(float x, float y, float z) {
      set(this.rLeg, x, y, z);
      return this;
   }

   public EmotePose lLeg(float x, float y, float z) {
      set(this.lLeg, x, y, z);
      return this;
   }

   public EmotePose root(float lift, float side, float pitch, float yaw, float roll) {
      this.lift = lift;
      this.side = side;
      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;
      return this;
   }

   public EmotePose bend(float rArm, float lArm, float rLeg, float lLeg) {
      this.bend[0] = rArm;
      this.bend[1] = lArm;
      this.bend[2] = rLeg;
      this.bend[3] = lLeg;
      return this;
   }

   public EmotePose axis(float rArm, float lArm, float rLeg, float lLeg) {
      this.bendAxis[0] = rArm;
      this.bendAxis[1] = lArm;
      this.bendAxis[2] = rLeg;
      this.bendAxis[3] = lLeg;
      return this;
   }

   public EmotePose ball(float x, float y, float z) {
      this.propPos[0] = x;
      this.propPos[1] = y;
      this.propPos[2] = z;
      return this;
   }

   public EmotePose headSize(float all, float sx, float sy) {
      this.headScale = all;
      this.headSX = sx;
      this.headSY = sy;
      return this;
   }

   public EmotePose reset() {
      for (float[] a : this.parts()) {
         a[0] = 0.0F;
         a[1] = 0.0F;
         a[2] = 0.0F;
      }

      for (int k = 0; k < 4; k++) {
         this.bend[k] = 0.0F;
         this.bendAxis[k] = 0.0F;
      }

      this.lift = this.forward = this.side = this.pitch = this.yaw = this.roll = 0.0F;
      this.pivot = 0.9F;
      this.sx = this.sy = this.sz = 1.0F;
      this.headScale = 1.0F;
      this.headSX = 1.0F;
      this.headSY = 1.0F;
      this.propPos[0] = this.propPos[1] = this.propPos[2] = 0.0F;
      this.prop = 0;
      this.propA = this.propB = 0.0F;
      return this;
   }

   static void set(float[] a, float x, float y, float z) {
      a[0] = x;
      a[1] = y;
      a[2] = z;
   }
}
