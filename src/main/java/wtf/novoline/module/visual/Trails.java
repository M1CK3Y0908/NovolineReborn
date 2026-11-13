package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.render.RenderUtils;

public class Trails extends Module {
   private final ComboSetting trails = settingManager.register(new ComboSetting(this, "Color type", 4, "Colored", "Fade", "Gradient", "Rainbow", "Astolfo"));
   private final CheckboxSetting applyForAll = settingManager.register(new CheckboxSetting(this, "Apply for all", true));
   private final CheckboxSetting hideInFirstPerson = settingManager.register(new CheckboxSetting(this, "Hide in first person", false));
   private final NumberSetting maxDistance = settingManager.register(new NumberSetting(this, "Max distance", 3, 1, 15));
   private final NumberSetting step = settingManager.register(new NumberSetting(this, "Step", 1, 1, 10) {
      @Override
      public boolean shouldShow() {
         int currentElement = Trails.this.trails.getCurrentElement();
         return currentElement == 1 || currentElement == 2 || currentElement == 3 || currentElement == 4;
      }
   });
   private final NumberSetting cycleTime = settingManager.register(new NumberSetting(this, "Cycle time", 1000, 1, 20000) {
      @Override
      public boolean shouldShow() {
         int currentElement = Trails.this.trails.getCurrentElement();
         return currentElement == 1 || currentElement == 2 || currentElement == 3 || currentElement == 4;
      }
   });
   private final ColorSetting main = settingManager.register(new ColorSetting(this, "Main", -14039553) {
      @Override
      public boolean shouldShow() {
         int currentElement = Trails.this.trails.getCurrentElement();
         return currentElement == 0 || currentElement == 1;
      }
   });
   private static final ResourceLocation SHIELD_BASE_TEXTURE = new ResourceLocation("novoline", "images/trail.png");
   private final ColorSetting gradientFirst = settingManager.register(new ColorSetting(this, "Gradient first", -14039553) {
      @Override
      public boolean shouldShow() {
         return Trails.this.trails.getCurrentElement() == 2;
      }
   });
   private final ColorSetting gradientSecond = settingManager.register(new ColorSetting(this, "Gradient second", -12469) {
      @Override
      public boolean shouldShow() {
         return Trails.this.trails.getCurrentElement() == 2;
      }
   });
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      long l = (long)this.cycleTime.getCurrentValue();
      int currentElement = this.trails.getCurrentElement();

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         double d = 0.0;
         if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(entityPlayer)) {
            int currentElement2 = ModuleManager.customModel.customModel.getCurrentElement();
            if (currentElement2 == 0) {
               d = 0.4F;
            }
         }

         if ((this.applyForAll.isEnabled() || entityPlayer == minecraft.thePlayer)
            && !entityPlayer.isInvisible()
            && (entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0 || !this.hideInFirstPerson.isEnabled())) {
            float partialTicks2 = renderWorldEvent.partialTicks;
            double d2 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks2;
            double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * partialTicks2;
            double d4 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks2;
            Vec3 vec3 = new Vec3(d2, d3, d4);
            double d5 = entityPlayer.getEntityBoundingBox().maxY - entityPlayer.getEntityBoundingBox().minY - d;
            double currentValue = this.maxDistance.getCurrentValue();
            float f = 0.0F;
            int i = 0;
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.shadeModel(7425);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
            float f2 = 0.703125F;
            minecraft.getTextureManager().bindTexture(SHIELD_BASE_TEXTURE);
            GL11.glBegin(7);

            RenderUtils.setColor(RenderUtils.withAlpha(switch (currentElement) {
               case 0 -> this.main.getColor();
               case 1 -> RenderUtils.pulseBrightness(this.main.getColor(), 0.4F, 1.0F, 1, entityPlayer.arrayDeque.size(), l);
               case 2 -> RenderUtils.pulseBetweenColors(this.gradientFirst.getColor(), this.gradientSecond.getColor(), 1, entityPlayer.arrayDeque.size(), l);
               case 3 -> RenderUtils.timedHsbColor(1.0, 0.6F, 1.0F, l);
               case 4 -> RenderUtils.timedHsbColorMirrored(1.0, 0.6F, 1.0F, l);
               default -> throw new IllegalArgumentException("Unexpected value: " + currentElement);
            }, 0));
            GL11.glTexCoord2f(f, 0.703125F);
            GL11.glVertex3d(d2, d3, d4);
            GL11.glTexCoord2f(f, 0.0F);
            GL11.glVertex3d(d2, d3 + d5, d4);
            double d6 = 0.0;
            double d7 = 0.0;

            for (Vec3 vec32 : entityPlayer.arrayDeque) {
               if (++i == entityPlayer.arrayDeque.size()) {
                  Vec3 vec33 = vec32.subtract(vec3);
                  vec33 = new Vec3(vec33.xCoord * partialTicks2, vec33.yCoord * partialTicks2, vec33.zCoord * partialTicks2);
                  vec32 = vec32.subtract(vec33);
               }

               double d8 = vec32.distanceTo(vec3);
               if (d7 + d8 > currentValue) {
                  Vec3 vec34 = vec32.subtract(vec3).normalize();
                  double d9 = currentValue - d7;
                  vec34 = new Vec3(vec34.xCoord * d9, vec34.yCoord * d9, vec34.zCoord * d9);
                  d7 = currentValue;
                  vec32 = vec3.add(vec34);
               } else {
                  d7 += d8;
                  if (entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0) {
                     d6 += d8;
                  } else if (i > 2) {
                     d6 += d8;
                  }
               }

               float f3 = (float)(d7 / currentValue) + f;

               RenderUtils.setColor(
                  RenderUtils.withAlphaFraction(
                     switch (currentElement) {
                        case 0 -> this.main.getColor();
                        case 1 -> RenderUtils.pulseBrightness(
                           this.main.getColor(), 0.4F, 1.0F, (int)(i * this.step.getCurrentValue()), entityPlayer.arrayDeque.size(), l
                        );
                        case 2 -> RenderUtils.pulseBetweenColors(
                           this.gradientFirst.getColor(), this.gradientSecond.getColor(), i, entityPlayer.arrayDeque.size(), l
                        );
                        case 3 -> RenderUtils.timedHsbColor(-f3 / this.step.getCurrentValue(), 0.6F, 1.0F, l);
                        case 4 -> RenderUtils.timedHsbColorMirrored(-f3 / this.step.getCurrentValue(), 0.6F, 1.0F, l);
                        default -> throw new IllegalArgumentException("Unexpected value: " + currentElement);
                     },
                     (float)(d6 / 0.3)
                  )
               );
               GL11.glTexCoord2f(f3, f2);
               GL11.glVertex3d(vec32.xCoord, vec32.yCoord + d5, vec32.zCoord);
               GL11.glTexCoord2f(f3, 0.0F);
               GL11.glVertex3d(vec32.xCoord, vec32.yCoord, vec32.zCoord);
               if (i != entityPlayer.arrayDeque.size()) {
                  GL11.glTexCoord2f(f3, 0.0F);
                  GL11.glVertex3d(vec32.xCoord, vec32.yCoord, vec32.zCoord);
                  GL11.glTexCoord2f(f3, f2);
                  GL11.glVertex3d(vec32.xCoord, vec32.yCoord + d5, vec32.zCoord);
               }

               vec3 = vec32;
               if (d7 >= currentValue) {
                  break;
               }
            }

            GL11.glEnd();
            RenderUtils.resetColor();
            GlStateManager.shadeModel(7424);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      }
   };

   public Trails() {
      super(Category.VISUAL, "Trails", "");
   }
}
