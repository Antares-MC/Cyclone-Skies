package dev.valence.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Bakes the vanilla-format model files shipped in this mod's own jar into triangles the cosmetic
 * renderer can draw. The JSON files are copies of ItemsAdder / Oraxen / CosmeticsCore cosmetics
 * (see {@code assets/valence_cord/cosmetics}) with their texture references pointed at this mod's
 * namespace, so the same loader also drives any model dropped in later.
 *
 * <p>Model space is the usual blockbench layout: 0..16 across the body with the origin of X/Z at
 * the spine, +Y up, and north (the face of the model) at Z=0. It is converted to cosmetic space
 * with {@code x=(x-8)*s, y=y*s+dy, z=(8-z)*s} so the face opening looks the way the player does.
 * {@code scale}/{@code dy} per model are tuned so the piece lands around the head or the shoulders.
 */
@Environment(EnvType.CLIENT)
public final class CosmeticModels {
   private static final String ROOT = "/assets/valence_cord/cosmetics/models/";
   private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("CycloneSkies");
   private static final String[] FACES = new String[]{"north", "east", "south", "west", "up", "down"};
   /**
    * Models address their UVs in the classic 16-unit block space; the packs ship their textures at
    * whatever resolution they like (32, 40, 80, 512...) and expect the UV rectangle to stretch over
    * the whole image. Dividing by a model's {@code texture_size} instead would crop the art to a
    * corner, which is why a few of these pieces came out half empty.
    */
   private static final float UV_SPACE = 16.0F;

   /** Frame count and frame time (in ticks) for textures that are stacked animation sheets. */
   private static final Map<String, float[]> ANIM = Map.ofEntries(
      Map.entry("emissary_glow", new float[]{10.0F, 2.0F}),
      Map.entry("necros_glow_helm", new float[]{5.0F, 2.0F}),
      Map.entry("necros_glow_wings", new float[]{5.0F, 2.0F}),
      Map.entry("twilight_glow", new float[]{6.0F, 3.0F}),
      Map.entry("sr_glow1_green", new float[]{14.0F, 1.0F}),
      Map.entry("sr_glow2_green", new float[]{16.0F, 1.0F}),
      Map.entry("sr_glow1_purple", new float[]{14.0F, 1.0F}),
      Map.entry("sr_glow2_purple", new float[]{16.0F, 1.0F}),
      Map.entry("sr_glow1_red", new float[]{14.0F, 1.0F}),
      Map.entry("sr_glow2_red", new float[]{16.0F, 1.0F})
   );

   public static final List<CosmeticModels.Def> HATS = List.of(
      new CosmeticModels.Def("fairyset_helmet", 0.056F, 1.07F),
      new CosmeticModels.Def("emissary_helmet", 0.052F, 1.22F),
      new CosmeticModels.Def("necros_helmet", 0.044F, 1.21F),
      new CosmeticModels.Def("twilight_hat", 0.038F, 1.16F),
      new CosmeticModels.Def("sr_helmet_green", 0.058F, 0.94F),
      new CosmeticModels.Def("sr_helmet_purple", 0.058F, 0.94F),
      new CosmeticModels.Def("sr_helmet_red", 0.058F, 0.94F)
   );

   public static final List<CosmeticModels.Def> WINGS = List.of(
      new CosmeticModels.Def("emissary_wings", 0.040F, 0.80F, -0.30F),
      new CosmeticModels.Def("necros_wings", 0.040F, 0.75F, -0.32F),
      new CosmeticModels.Def("twilight_wings", 0.070F, 0.80F, -0.26F),
      new CosmeticModels.Def("sr_wing_green", 0.072F, 0.35F, -0.34F),
      new CosmeticModels.Def("sr_wing_purple", 0.072F, 0.35F, -0.34F),
      new CosmeticModels.Def("sr_wing_red", 0.072F, 0.35F, -0.34F)
   );

   private static final CosmeticModels.Mesh[] HAT_MESH = new CosmeticModels.Mesh[HATS.size()];
   private static final CosmeticModels.Mesh[] WING_MESH = new CosmeticModels.Mesh[WINGS.size()];
   private static final Map<String, CosmeticTexture> TEXTURES = new HashMap<>();

   private CosmeticModels() {
   }

   public static void emitHat(int index, CosmeticGeometry geo) {
      CosmeticModels.Mesh mesh = mesh(HAT_MESH, HATS, index);
      if (mesh != null) {
         mesh.emit(geo);
      }
   }

   public static void emitWing(int index, CosmeticGeometry geo) {
      CosmeticModels.Mesh mesh = mesh(WING_MESH, WINGS, index);
      if (mesh != null) {
         mesh.emit(geo);
      }
   }

   private static CosmeticModels.Mesh mesh(CosmeticModels.Mesh[] cache, List<CosmeticModels.Def> defs, int index) {
      if (index < 0 || index >= defs.size()) {
         return null;
      }

      CosmeticModels.Mesh mesh = cache[index];
      if (mesh == null) {
         mesh = bake(defs.get(index));
         cache[index] = mesh;
      }

      return mesh;
   }

   private static CosmeticTexture tex(String ref) {
      return TEXTURES.computeIfAbsent(ref, r -> {
         int cut = r.indexOf(58);
         String ns = cut < 0 ? "minecraft" : r.substring(0, cut);
         String path = cut < 0 ? r : r.substring(cut + 1);
         String name = path.substring(path.lastIndexOf(47) + 1);
         float[] anim = ANIM.get(name);
         int frames = anim == null ? 1 : (int)anim[0];
         float frametime = anim == null ? 1.0F : anim[1];
         return new CosmeticTexture(
            class_2960.method_60655(ns, path),
            "/assets/" + ns + "/textures/" + path + ".png",
            frames,
            frametime,
            name.contains("glow")
         );
      });
   }

   private static CosmeticModels.Mesh bake(CosmeticModels.Def def) {
      try (InputStream in = CosmeticModels.class.getResourceAsStream(ROOT + def.id() + ".json")) {
         if (in == null) {
            LOGGER.warn("[cosmetics] missing model {}", def.id());
            return CosmeticModels.Mesh.EMPTY;
         }

         JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
         Map<String, CosmeticTexture> textures = new HashMap<>();
         if (root.has("textures")) {
            for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("textures").entrySet()) {
               textures.put(entry.getKey(), tex(entry.getValue().getAsString()));
            }
         }

         CosmeticModels.Builder out = new CosmeticModels.Builder();
         JsonArray elements = root.getAsJsonArray("elements");
         if (elements != null) {
            for (JsonElement element : elements) {
               element(out, element.getAsJsonObject(), textures, def);
            }
         }

         return out.build(def.id());
      } catch (Throwable var16) {
         LOGGER.warn("[cosmetics] could not bake {}", def.id(), var16);
         return CosmeticModels.Mesh.EMPTY;
      }
   }

   private static void element(
      CosmeticModels.Builder out,
      JsonObject element,
      Map<String, CosmeticTexture> textures,
      CosmeticModels.Def def
   ) {
      if (!element.has("from") || !element.has("to") || !element.has("faces")) {
         return;
      }

      JsonArray from = element.getAsJsonArray("from");
      JsonArray to = element.getAsJsonArray("to");
      float x0 = from.get(0).getAsFloat();
      float y0 = from.get(1).getAsFloat();
      float z0 = from.get(2).getAsFloat();
      float x1 = to.get(0).getAsFloat();
      float y1 = to.get(1).getAsFloat();
      float z1 = to.get(2).getAsFloat();
      Matrix4f rot = rotation(element);
      JsonObject faces = element.getAsJsonObject("faces");

      for (String dir : FACES) {
         if (!faces.has(dir)) {
            continue;
         }

         JsonObject face = faces.getAsJsonObject(dir);
         if (!face.has("uv") || !face.has("texture")) {
            continue;
         }

         CosmeticTexture texture = textures.get(strip(face.get("texture").getAsString()));
         if (texture == null) {
            continue;
         }

         JsonArray uv = face.getAsJsonArray("uv");
         float u0 = uv.get(0).getAsFloat() / UV_SPACE;
         float v0 = uv.get(1).getAsFloat() / UV_SPACE;
         float u1 = uv.get(2).getAsFloat() / UV_SPACE;
         float v1 = uv.get(3).getAsFloat() / UV_SPACE;
         float[] quad = CosmeticModels.quad(dir, x0, y0, z0, x1, y1, z1);
         float[] corners = new float[]{u0, v0, u0, v1, u1, v1, u1, v0};
         int shift = 0;
         if (face.has("rotation")) {
            int quarter = Math.floorMod(Math.round(face.get("rotation").getAsFloat() / 90.0F), 4);
            shift = (4 - quarter) % 4;
         }

         float[] w = new float[12];
         for (int k = 0; k < 4; k++) {
            Vector3f p = new Vector3f(quad[k * 3], quad[k * 3 + 1], quad[k * 3 + 2]);
            if (rot != null) {
               rot.transformPosition(p);
            }

            w[k * 3] = (p.x - 8.0F) * def.scale();
            w[k * 3 + 1] = p.y * def.scale() + def.yOff();
            w[k * 3 + 2] = (8.0F - p.z) * def.scale() + def.zOff();
         }

         float[] t = new float[8];
         for (int k = 0; k < 4; k++) {
            int src = (k + shift) % 4;
            t[k * 2] = corners[src * 2];
            t[k * 2 + 1] = corners[src * 2 + 1];
         }

         out.quad(w, t, texture);
      }
   }

   private static String strip(String ref) {
      return ref.startsWith("#") ? ref.substring(1) : ref;
   }

   private static Matrix4f rotation(JsonObject element) {
      if (!element.has("rotation")) {
         return null;
      }

      JsonObject r = element.getAsJsonObject("rotation");
      float angle = (float)Math.toRadians(r.get("angle").getAsFloat());
      JsonArray origin = r.getAsJsonArray("origin");
      float ox = origin.get(0).getAsFloat();
      float oy = origin.get(1).getAsFloat();
      float oz = origin.get(2).getAsFloat();
      Matrix4f m = new Matrix4f().translate(ox, oy, oz);
      switch (r.get("axis").getAsString()) {
         case "x":
            m.rotateX(angle);
            break;
         case "y":
            m.rotateY(angle);
            break;
         default:
            m.rotateZ(angle);
      }

      return m.translate(-ox, -oy, -oz);
   }

   /** Four corners of one cube face, in the winding the matching texture corner expects. */
   private static float[] quad(String dir, float x0, float y0, float z0, float x1, float y1, float z1) {
      return switch (dir) {
         case "north" -> new float[]{x1, y1, z0, x1, y0, z0, x0, y0, z0, x0, y1, z0};
         case "south" -> new float[]{x0, y1, z1, x0, y0, z1, x1, y0, z1, x1, y1, z1};
         case "west" -> new float[]{x0, y1, z0, x0, y0, z0, x0, y0, z1, x0, y1, z1};
         case "east" -> new float[]{x1, y1, z1, x1, y0, z1, x1, y0, z0, x1, y1, z0};
         case "up" -> new float[]{x0, y1, z0, x1, y1, z0, x1, y1, z1, x0, y1, z1};
         default -> new float[]{x0, y0, z1, x1, y0, z1, x1, y0, z0, x0, y0, z0};
      };
   }

   /** One baked piece: {@code scale} maps model units to blocks, {@code yOff}/{@code zOff} place it. */
   @Environment(EnvType.CLIENT)
   public record Def(String id, float scale, float yOff, float zOff) {
      public Def(String id, float scale, float yOff) {
         this(id, scale, yOff, 0.0F);
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Builder {
      private float[] pos = new float[576];
      private float[] uv = new float[384];
      private CosmeticTexture[] tex = new CosmeticTexture[64];
      private int count;

      void quad(float[] w, float[] t, CosmeticTexture texture) {
         this.tri(w, 0, 1, 2, t, 0, 1, 2, texture);
         this.tri(w, 0, 2, 3, t, 0, 2, 3, texture);
      }

      private void tri(float[] w, int a, int b, int c, float[] t, int ta, int tb, int tc, CosmeticTexture texture) {
         this.grow();
         int p = this.count * 9;
         int q = this.count * 6;
         this.place(p, w, a);
         this.place(p + 3, w, b);
         this.place(p + 6, w, c);
         this.uv[q] = t[ta * 2];
         this.uv[q + 1] = t[ta * 2 + 1];
         this.uv[q + 2] = t[tb * 2];
         this.uv[q + 3] = t[tb * 2 + 1];
         this.uv[q + 4] = t[tc * 2];
         this.uv[q + 5] = t[tc * 2 + 1];
         this.tex[this.count] = texture;
         this.count++;
      }

      private void place(int p, float[] w, int corner) {
         this.pos[p] = w[corner * 3];
         this.pos[p + 1] = w[corner * 3 + 1];
         this.pos[p + 2] = w[corner * 3 + 2];
      }

      private void grow() {
         if ((this.count + 1) * 9 > this.pos.length) {
            float[] p = new float[this.pos.length * 2];
            System.arraycopy(this.pos, 0, p, 0, this.pos.length);
            this.pos = p;
         }

         if ((this.count + 1) * 6 > this.uv.length) {
            float[] u = new float[this.uv.length * 2];
            System.arraycopy(this.uv, 0, u, 0, this.uv.length);
            this.uv = u;
         }

         if (this.count + 1 > this.tex.length) {
            CosmeticTexture[] t = new CosmeticTexture[this.tex.length * 2];
            System.arraycopy(this.tex, 0, t, 0, this.tex.length);
            this.tex = t;
         }
      }

      CosmeticModels.Mesh build(String id) {
         if (this.count == 0) {
            LOGGER.warn("[cosmetics] {} has no drawable faces", id);
            return CosmeticModels.Mesh.EMPTY;
         }

         return new CosmeticModels.Mesh(this.pos, this.uv, this.tex, this.count);
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Mesh {
      static final CosmeticModels.Mesh EMPTY = new CosmeticModels.Mesh(new float[0], new float[0], new CosmeticTexture[0], 0);
      private final float[] pos;
      private final float[] uv;
      private final CosmeticTexture[] tex;
      private final int count;

      Mesh(float[] pos, float[] uv, CosmeticTexture[] tex, int count) {
         this.pos = pos;
         this.uv = uv;
         this.tex = tex;
         this.count = count;
      }

      void emit(CosmeticGeometry geo) {
         for (int i = 0; i < this.count; i++) {
            int p = i * 9;
            int q = i * 6;
            CosmeticTexture texture = this.tex[i];
            geo.triTex(
               this.pos[p],
               this.pos[p + 1],
               this.pos[p + 2],
               this.uv[q],
               this.uv[q + 1],
               this.pos[p + 3],
               this.pos[p + 4],
               this.pos[p + 5],
               this.uv[q + 2],
               this.uv[q + 3],
               this.pos[p + 6],
               this.pos[p + 7],
               this.pos[p + 8],
               this.uv[q + 4],
               this.uv[q + 5],
               texture,
               -1,
               texture.glow()
            );
         }
      }
   }
}
