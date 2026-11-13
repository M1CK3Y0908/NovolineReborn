package net.optifine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.ConnectedParser;
import net.optifine.config.Matches;
import net.optifine.config.RangeListInt;
import net.optifine.render.Blender;
import net.optifine.util.NumUtils;
import net.optifine.util.SmoothFloat;
import net.optifine.util.TextureUtils;

public class CustomSkyLayer {
   public String source = null;
   private static final String CLEAR = "clear";
   private int startFadeIn = -1;
   private int endFadeIn = -1;
   private int startFadeOut = -1;
   private int endFadeOut = -1;
   private int blend = 1;
   private static final String RAIN = "rain";
   private boolean rotate = false;
   private float speed = 1.0F;
   private static final String THUNDER = "thunder";
   private float[] axis;
   private RangeListInt days;
   private int daysLoop;
   private boolean weatherClear;
   private boolean weatherRain;
   private boolean weatherThunder;
   public BiomeGenBase[] biomes;
   public static final float[] DEFAULT_AXIS = new float[]{1.0F, 0.0F, 0.0F};
   public RangeListInt heights;
   private float transition;
   private SmoothFloat smoothPositionBrightness;
   public int textureId;
   private World lastWorld;

   private float[] parseAxis(String str, float[] defVal) {
      if (str == null) {
         return defVal;
      }

      String[] texts = Config.tokenize(str, " ");
      if (texts.length != 3) {
         Config.warn("Invalid axis: " + str);
         return defVal;
      }

      float[] fArray = new float[3];

      for (int i = 0; i < texts.length; i++) {
         fArray[i] = Config.parseFloat(texts[i], Float.MIN_VALUE);
         if (fArray[i] == Float.MIN_VALUE) {
            Config.warn("Invalid axis: " + str);
            return defVal;
         }

         if (fArray[i] < -1.0F || fArray[i] > 1.0F) {
            Config.warn("Invalid axis values: " + str);
            return defVal;
         }
      }

      float f = fArray[0];
      float f2 = fArray[1];
      float f3 = fArray[2];
      if (f * f + f2 * f2 + f3 * f3 < 1.0E-5F) {
         Config.warn("Invalid axis values: " + str);
         return defVal;
      } else {
         return new float[]{f3, f2, -f};
      }
   }

   private float getPositionBrightnessRaw(World world) {
      Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
      if (entity == null) {
         return 0.0F;
      }

      BlockPos blockPos = entity.getPosition();
      if (this.biomes != null) {
         BiomeGenBase biomeGenBase = world.getBiomeGenForCoords(blockPos);
         if (biomeGenBase == null) {
            return 0.0F;
         }

         if (!Matches.biome(biomeGenBase, this.biomes)) {
            return 0.0F;
         }
      }

      return this.heights != null && !this.heights.isInRange(blockPos.getY()) ? 0.0F : 1.0F;
   }

   public void render(World world, int timeOfDay, float celestialAngle, float rainStrength, float thunderStrength) {
      float positionBrightness = this.getPositionBrightness(world);
      float weatherBrightness = this.getWeatherBrightness(rainStrength, thunderStrength);
      float fadeBrightness = this.getFadeBrightness(timeOfDay);
      float f = positionBrightness * weatherBrightness * fadeBrightness;
      f = Config.limit(f, 0.0F, 1.0F);
      if (f >= 1.0E-4F) {
         GlStateManager.bindTexture(this.textureId);
         Blender.setupBlend(this.blend, f);
         GlStateManager.pushMatrix();
         if (this.rotate) {
            float f2 = 0.0F;
            if (this.speed != Math.round(this.speed)) {
               long l = (world.getWorldTime() + 18000L) / 24000L;
               double d = this.speed % 1.0F;
               double d2 = l * d;
               f2 = (float)(d2 % 1.0);
            }

            GlStateManager.rotate(360.0F * (f2 + celestialAngle * this.speed), this.axis[0], this.axis[1], this.axis[2]);
         }

         Tessellator instance = Tessellator.getInstance();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(instance, 4);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(instance, 1);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         this.renderSide(instance, 0);
         GlStateManager.popMatrix();
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(instance, 5);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(instance, 2);
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         this.renderSide(instance, 3);
         GlStateManager.popMatrix();
      }
   }

   public boolean isValid(String path) {
      if (this.source == null) {
         Config.warn("No source texture: " + path);
         return false;
      }

      this.source = TextureUtils.fixResourcePath(this.source, TextureUtils.getBasePath(path));
      if (this.startFadeIn >= 0 && this.endFadeIn >= 0 && this.endFadeOut >= 0) {
         int i = this.normalizeTime(this.endFadeIn - this.startFadeIn);
         if (this.startFadeOut < 0) {
            this.startFadeOut = this.normalizeTime(this.endFadeOut - i);
            if (this.timeBetween(this.startFadeOut, this.startFadeIn, this.endFadeIn)) {
               this.startFadeOut = this.endFadeIn;
            }
         }

         int i2 = this.normalizeTime(this.startFadeOut - this.endFadeIn);
         int i3 = this.normalizeTime(this.endFadeOut - this.startFadeOut);
         int i4 = this.normalizeTime(this.startFadeIn - this.endFadeOut);
         int i5 = i + i2 + i3 + i4;
         if (i5 != 24000) {
            Config.warn("Invalid fadeIn/fadeOut times, sum is not 24h: " + i5);
            return false;
         } else if (this.speed < 0.0F) {
            Config.warn("Invalid speed: " + this.speed);
            return false;
         } else if (this.daysLoop <= 0) {
            Config.warn("Invalid daysLoop: " + this.daysLoop);
            return false;
         } else {
            return true;
         }
      } else {
         Config.warn("Invalid times, required are: startFadeIn, endFadeIn and endFadeOut.");
         return false;
      }
   }

   public boolean isActive(World world, int timeOfDay) {
      if (world != this.lastWorld) {
         this.lastWorld = world;
         this.smoothPositionBrightness = null;
      }

      if (this.timeBetween(timeOfDay, this.endFadeOut, this.startFadeIn)) {
         return false;
      }

      if (this.days != null) {
         long worldTime = world.getWorldTime();
         long l = worldTime - this.startFadeIn;

         while (l < 0L) {
            l += 24000 * this.daysLoop;
         }

         int i = (int)(l / 24000L) % this.daysLoop;
         if (!this.days.isInRange(i)) {
            return false;
         }
      }

      return true;
   }

   private int parseTime(String str) {
      if (str == null) {
         return -1;
      }

      String[] stringArray = Config.tokenize(str, ":");
      if (stringArray.length != 2) {
         Config.warn("Invalid time: " + str);
         return -1;
      }

      String text = stringArray[0];
      String text2 = stringArray[1];
      int i = Config.parseInt(text, -1);
      int i2 = Config.parseInt(text2, -1);
      if (i >= 0 && i <= 23 && i2 >= 0 && i2 <= 59) {
         i -= 6;
         if (i < 0) {
            i += 24;
         }

         return i * 1000 + (int)(i2 / 60.0 * 1000.0);
      } else {
         Config.warn("Invalid time: " + str);
         return -1;
      }
   }

   private int normalizeTime(int timeMc) {
      while (timeMc >= 24000) {
         timeMc -= 24000;
      }

      while (timeMc < 0) {
         timeMc += 24000;
      }

      return timeMc;
   }

   private float getPositionBrightness(World world) {
      if (this.biomes == null && this.heights == null) {
         return 1.0F;
      }

      float positionBrightnessRaw = this.getPositionBrightnessRaw(world);
      if (this.smoothPositionBrightness == null) {
         this.smoothPositionBrightness = new SmoothFloat(positionBrightnessRaw, this.transition);
      }

      return this.smoothPositionBrightness.getSmoothValue(positionBrightnessRaw);
   }

   public CustomSkyLayer(Properties props, String defSource) {
      this.axis = DEFAULT_AXIS;
      this.days = null;
      this.daysLoop = 8;
      this.weatherClear = true;
      this.weatherRain = false;
      this.weatherThunder = false;
      this.biomes = null;
      this.heights = null;
      this.transition = 1.0F;
      this.smoothPositionBrightness = null;
      this.textureId = -1;
      this.lastWorld = null;
      ConnectedParser connectedParser = new ConnectedParser("CustomSky");
      this.source = props.getProperty("source", defSource);
      this.startFadeIn = this.parseTime(props.getProperty("startFadeIn"));
      this.endFadeIn = this.parseTime(props.getProperty("endFadeIn"));
      this.startFadeOut = this.parseTime(props.getProperty("startFadeOut"));
      this.endFadeOut = this.parseTime(props.getProperty("endFadeOut"));
      this.blend = Blender.parseBlend(props.getProperty("blend"));
      this.rotate = this.checkString(props.getProperty("rotate"));
      this.speed = this.parseFloat(props.getProperty("speed"), 1.0F);
      this.axis = this.parseAxis(props.getProperty("axis"), DEFAULT_AXIS);
      this.days = connectedParser.parseRangeListInt(props.getProperty("days"));
      this.daysLoop = connectedParser.parseInt(props.getProperty("daysLoop"), 8);
      List<String> list = this.parseWeatherList(props.getProperty("weather", "clear"));
      this.weatherClear = list.contains("clear");
      this.weatherRain = list.contains("rain");
      this.weatherThunder = list.contains("thunder");
      this.biomes = connectedParser.parseBiomes(props.getProperty("biomes"));
      this.heights = connectedParser.parseRangeListInt(props.getProperty("heights"));
      this.transition = this.parseFloat(props.getProperty("transition"), 1.0F);
   }

   private boolean checkString(String text) {
      if (text == null) {
         return true;
      }

      if (text.toLowerCase().equals("true")) {
         return true;
      }

      if (text.toLowerCase().equals("false")) {
         return false;
      }

      Config.warn("Unknown boolean: " + text);
      return true;
   }

   @Override
   public String toString() {
      return this.source + ", " + this.startFadeIn + "-" + this.endFadeIn + " " + this.startFadeOut + "-" + this.endFadeOut;
   }

   private List<String> parseWeatherList(String str) {
      List<String> list = Arrays.asList("clear", "rain", "thunder");
      ArrayList arrayList = new ArrayList();
      String[] stringArray = Config.tokenize(str, " ");

      for (int index = 0; index < stringArray.length; index++) {
         String text = stringArray[index];
         if (!list.contains(text)) {
            Config.warn("Unknown weather: " + text);
         } else {
            arrayList.add(text);
         }
      }

      return arrayList;
   }

   private float getWeatherBrightness(float rainStrength, float thunderStrength) {
      float f = 1.0F - rainStrength;
      float f2 = rainStrength - thunderStrength;
      float f3 = 0.0F;
      if (this.weatherClear) {
         f3 += f;
      }

      if (this.weatherRain) {
         f3 += f2;
      }

      if (this.weatherThunder) {
         f3 += thunderStrength;
      }

      return NumUtils.limit(f3, 0.0F, 1.0F);
   }

   private float parseFloat(String str, float defVal) {
      if (str == null) {
         return defVal;
      } else {
         float f = Config.parseFloat(str, Float.MIN_VALUE);
         if (f == Float.MIN_VALUE) {
            Config.warn("Invalid value: " + str);
            return defVal;
         } else {
            return f;
         }
      }
   }

   private boolean timeBetween(int timeOfDay, int timeStart, int timeEnd) {
      return timeStart <= timeEnd ? timeOfDay >= timeStart && timeOfDay <= timeEnd : timeOfDay >= timeStart || timeOfDay <= timeEnd;
   }

   private float getFadeBrightness(int timeOfDay) {
      if (this.timeBetween(timeOfDay, this.startFadeIn, this.endFadeIn)) {
         int i = this.normalizeTime(this.endFadeIn - this.startFadeIn);
         int i2 = this.normalizeTime(timeOfDay - this.startFadeIn);
         return (float)i2 / i;
      } else if (this.timeBetween(timeOfDay, this.endFadeIn, this.startFadeOut)) {
         return 1.0F;
      } else if (this.timeBetween(timeOfDay, this.startFadeOut, this.endFadeOut)) {
         int i3 = this.normalizeTime(this.endFadeOut - this.startFadeOut);
         int i4 = this.normalizeTime(timeOfDay - this.startFadeOut);
         return 1.0F - (float)i4 / i3;
      } else {
         return 0.0F;
      }
   }

   private void renderSide(Tessellator tess, int side) {
      WorldRenderer worldRenderer = tess.getWorldRenderer();
      double d = side % 3 / 3.0;
      double d2 = side / 3 / 2.0;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-100.0, -100.0, -100.0).tex(d, d2).endVertex();
      worldRenderer.pos(-100.0, -100.0, 100.0).tex(d, d2 + 0.5).endVertex();
      worldRenderer.pos(100.0, -100.0, 100.0).tex(d + 0.3333333333333333, d2 + 0.5).endVertex();
      worldRenderer.pos(100.0, -100.0, -100.0).tex(d + 0.3333333333333333, d2).endVertex();
      tess.draw();
   }
}
