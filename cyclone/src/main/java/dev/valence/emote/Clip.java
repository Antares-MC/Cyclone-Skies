package dev.valence.emote;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Clip {
   private final float length;
   private final List<Float> times = new ArrayList<>();
   private final List<float[]> keys = new ArrayList<>();
   private final EmotePose tmp = new EmotePose();
   private final float[] out = new float[57];

   private Clip(float length) {
      this.length = length;
   }

   public static Clip loop(float seconds) {
      return new Clip(seconds);
   }

   public float length() {
      return this.length;
   }

   public Clip key(float t, Consumer<EmotePose> pose) {
      this.tmp.reset();
      pose.accept(this.tmp);
      this.times.add(t);
      this.keys.add(this.tmp.toArray(new float[57]));
      return this;
   }

   public void sample(float t, EmotePose p) {
      int n = this.keys.size();
      if (n != 0) {
         if (n == 1) {
            p.fromArray(this.keys.get(0));
         } else {
            float lt = t % this.length;
            if (lt < 0.0F) {
               lt += this.length;
            }

            int i = n - 1;

            for (int k = 0; k < n; k++) {
               if (this.times.get(k) > lt) {
                  i = k - 1;
                  break;
               }
            }

            if (i < 0) {
               i = n - 1;
            }

            int i0 = (i - 1 + n) % n;
            int i2 = (i + 1) % n;
            int i3 = (i + 2) % n;
            float t1 = this.times.get(i);
            float t2 = this.times.get(i2);
            if (t2 <= t1) {
               t2 += this.length;
            }

            float lt2 = lt < t1 ? lt + this.length : lt;
            float t0 = this.times.get(i0);
            if (t0 >= t1) {
               t0 -= this.length;
            }

            float t3 = this.times.get(i3);

            while (t3 <= t2) {
               t3 += this.length;
            }

            float dt = t2 - t1;
            float u = dt <= 0.0F ? 0.0F : (lt2 - t1) / dt;
            float u2 = u * u;
            float u3 = u2 * u;
            float h00 = 2.0F * u3 - 3.0F * u2 + 1.0F;
            float h10 = u3 - 2.0F * u2 + u;
            float h01 = -2.0F * u3 + 3.0F * u2;
            float h11 = u3 - u2;
            float[] p0 = this.keys.get(i0);
            float[] p1 = this.keys.get(i);
            float[] p2 = this.keys.get(i2);
            float[] p3 = this.keys.get(i3);
            float s1 = dt / Math.max(1.0E-4F, t2 - t0);
            float s2 = dt / Math.max(1.0E-4F, t3 - t1);

            for (int k = 0; k < 57; k++) {
               float m1 = (p2[k] - p0[k]) * s1;
               float m2 = (p3[k] - p1[k]) * s2;
               this.out[k] = h00 * p1[k] + h10 * m1 + h01 * p2[k] + h11 * m2;
            }

            p.fromArray(this.out);
         }
      }
   }
}
