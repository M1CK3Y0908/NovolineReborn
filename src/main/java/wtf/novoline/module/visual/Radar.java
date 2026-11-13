package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import wtf.novoline.enums.RelationType;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.render.RenderUtils;

public class Radar extends Module {
   private final ComboSetting radar = settingManager.register(new ComboSetting(this, "Rotation type", 0, "Auto", "Manual"));
   private final ComboSetting arrowType = settingManager.register(new ComboSetting(this, "Arrow type", 1, "2D", "3D"));
   private final ComboSetting arrowColorType = settingManager.register(new ComboSetting(this, "Arrow color type", 2, "Custom", "Rainbow", "Astolfo"));
   private final ComboSetting directionColorType = settingManager.register(new ComboSetting(this, "Direction color type", 0, "Custom", "Rainbow", "Astolfo"));
   private final ComboSetting circleColorType = settingManager.register(new ComboSetting(this, "Circle color type", 0, "Custom", "Rainbow", "Astolfo"));
   private final MultiComboSetting ignoreRules = settingManager.register(
      new MultiComboSetting(this, "Ignore rules").add("Self", true).add("Invisible", false).add("Bots", true).add("Team", false)
   );
   private final NumberSetting rotation = settingManager.register(new NumberSetting(this, "Rotation", 20, 0, 90) {
      @Override
      public boolean shouldShow() {
         return Radar.this.radar.getCurrentElement() == 1;
      }
   });
   private final NumberSetting radius = settingManager.register(new NumberSetting(this, "Radius", 40, 10, 100));
   private final NumberSetting yOffset = settingManager.register(new NumberSetting(this, "Y-Offset", 90, 0, 500));
   private final CheckboxSetting direction = settingManager.register(new CheckboxSetting(this, "Direction", true));
   private final CheckboxSetting circle = settingManager.register(new CheckboxSetting(this, "Circle", true));
   private final CheckboxSetting circleOutline = settingManager.register(new CheckboxSetting(this, "Circle outline", true) {
      @Override
      public boolean shouldShow() {
         return Radar.this.circle.isEnabled();
      }
   });
   private final ColorSetting arrowColor = settingManager.register(new ColorSetting(this, "Arrow color", -14506000) {
      @Override
      public boolean shouldShow() {
         return Radar.this.arrowColorType.getCurrentElement() == 0;
      }
   });
   private final ColorSetting directionColor = settingManager.register(new ColorSetting(this, "Direction color", -1) {
      @Override
      public boolean shouldShow() {
         return Radar.this.directionColorType.getCurrentElement() == 0;
      }
   });
   private final ColorSetting circleColor = settingManager.register(new ColorSetting(this, "Circle color", -1) {
      @Override
      public boolean shouldShow() {
         return Radar.this.circleColorType.getCurrentElement() == 0;
      }
   });
   private final ColorSetting friendColor = settingManager.register(new ColorSetting(this, "Friend color", -12657535));
   private final ColorSetting targetColor = settingManager.register(new ColorSetting(this, "Target color", -2341314));
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      double d = renderScreenEvent.scaledResolution.getScaledWidth_double() / 2.0;
      double d2 = renderScreenEvent.scaledResolution.getScaledHeight_double() - this.yOffset.getCurrentValue();
      int currentElement = this.radar.getCurrentElement();
      double d3 = 0.0;
      if (currentElement == 0) {
         d3 = Math.sin(Math.toRadians(Math.abs(10.0F + Math.max(0.0F, minecraft.thePlayer.rotationPitch))));
      } else if (currentElement == 1) {
         d3 = Math.sin(Math.toRadians(this.rotation.getCurrentValue()));
      }

      double d4 = this.radius.getCurrentValue() + 15.0F;
      double d5 = d4 * (0.3 + 0.7 * d3);
      int i = -1;
      int currentElement2 = this.arrowColorType.getCurrentElement();
      if (currentElement2 == 0) {
         i = this.arrowColor.getColor();
      } else if (currentElement2 == 1) {
         i = RenderUtils.indexedHsbColor(1, 1, 0.6F, 1.0F, 2000L);
      } else if (currentElement2 == 2) {
         i = RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, 2000L);
      }

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if ((!this.ignoreRules.isSelected(0) || entityPlayer != minecraft.thePlayer)
            && (!this.ignoreRules.isSelected(1) || !minecraft.thePlayer.isInvisible())
            && (!this.ignoreRules.isSelected(2) || !ModuleManager.antibot.isPlayerBot(entityPlayer))
            && (!this.ignoreRules.isSelected(3) || !PlayerUtils.isSameTeam(minecraft.thePlayer, entityPlayer))) {
            double d6 = entityPlayer.prevPosX
               + (entityPlayer.posX - entityPlayer.prevPosX) * renderScreenEvent.partialTicks
               - (minecraft.thePlayer.prevPosX + (minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX) * renderScreenEvent.partialTicks);
            double d7 = entityPlayer.prevPosZ
               + (entityPlayer.posZ - entityPlayer.prevPosZ) * renderScreenEvent.partialTicks
               - (minecraft.thePlayer.prevPosZ + (minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ) * renderScreenEvent.partialTicks);
            double d8 = Math.atan2(d7, d6) - Math.toRadians(minecraft.thePlayer.rotationYaw) + Math.PI;
            int currentElement3 = this.arrowType.getCurrentElement();
            GlStateManager.pushMatrix();
            GlStateManager.translate(d + d4 * Math.cos(d8), d2 + d5 * Math.sin(d8), 0.0);
            GlStateManager.rotate(180.0F + (float)Math.toDegrees(d8), 0.0F, 0.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            RenderUtils.applyBlendFunc();
            double d9 = 5.0;
            double d10 = d9 * (1.0 - 0.5 * (Math.abs(Math.sin(d8)) * (1.0 - d3)));
            GL11.glEnable(2881);
            GL11.glBegin(4);
            RelationType relationType = novoline.getRelationManager().get(entityPlayer.getName());
            int i2;
            if (relationType == null) {
               i2 = i;
            } else {
               switch (relationType) {
                  case TARGET:
                     i2 = this.targetColor.getColor();
                     break;
                  case FRIEND:
                     i2 = this.friendColor.getColor();
                     break;
                  default:
                     throw new IncompatibleClassChangeError();
               }
            }

            int i3 = i2;
            if (currentElement3 == 0) {
               RenderUtils.setColor(i3);
               GL11.glVertex2d(-d10, 0.0);
               GL11.glVertex2d(d10, d9);
               GL11.glVertex2d(d10, -d9);
            } else if (currentElement3 == 1) {
               RenderUtils.setColor(RenderUtils.lerpColor(i3, -16777216, 0.5F));
               GL11.glVertex2d(d10 / 2.0, 0.0);
               GL11.glVertex2d(d10, d9);
               GL11.glVertex2d(d10, -d9);
               RenderUtils.setColor(i3);
               GL11.glVertex2d(-d10, 0.0);
               GL11.glVertex2d(d10, d9);
               GL11.glVertex2d(d10 / 2.0, 0.0);
               RenderUtils.setColor(RenderUtils.lerpColor(i3, -1, 0.3F));
               GL11.glVertex2d(-d10, 0.0);
               GL11.glVertex2d(d10 / 2.0, 0.0);
               GL11.glVertex2d(d10, -d9);
            }

            GL11.glEnd();
            GL11.glDisable(2881);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      if (this.circle.isEnabled() || this.direction.isEnabled()) {
         d4 = this.radius.getCurrentValue();
         d5 = d4 * (0.2 + 0.8 * d3);
         int i4 = -1;
         int currentElement4 = this.circleColorType.getCurrentElement();
         if (currentElement4 == 0) {
            i4 = this.circleColor.getColor();
         } else if (currentElement4 == 1) {
            i4 = RenderUtils.indexedHsbColor(1, 1, 0.6F, 1.0F, 2000L);
         } else if (currentElement4 == 2) {
            i4 = RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, 2000L);
         }

         for (int i5 = 0; i5 <= 3; i5++) {
            EnumFacing enumFacing2 = this.byHorizontalIndex(i5);
            double d11 = Math.toRadians(this.getFacingYaw(enumFacing2) - minecraft.thePlayer.rotationYaw - 90.0);
            if (this.circle.isEnabled()) {
               GlStateManager.enableBlend();
               GlStateManager.disableTexture2D();
               RenderUtils.applyBlendFunc();
               GL11.glEnable(2848);
               GL11.glLineWidth(2.0F);
               if (this.circleOutline.isEnabled()) {
                  GL11.glBegin(3);

                  for (int i6 = 0; i6 <= 8; i6++) {
                     double d12 = d11 + Math.toRadians(10.0 + 70.0 * i6 / 8.0);
                     RenderUtils.setColor(RenderUtils.indexedHsbColorMirrored(i6, 32, 0.6F, 1.0F, 2000L));
                     GL11.glVertex2d(d + (d4 - 1.5) * Math.cos(d12), d2 + (d5 - 1.5) * Math.sin(d12));
                  }

                  GL11.glEnd();
               }

               RenderUtils.setColor(i4);
               GL11.glBegin(3);

               for (int i7 = 0; i7 <= 8; i7++) {
                  double d13 = d11 + Math.toRadians(10.0 + 70.0 * i7 / 8.0);
                  GL11.glVertex2d(d + d4 * Math.cos(d13), d2 + d5 * Math.sin(d13));
               }

               GL11.glEnd();
               GL11.glDisable(2848);
               GlStateManager.enableTexture2D();
               GlStateManager.disableBlend();
            }

            if (this.direction.isEnabled()) {
               int i8 = -1;
               int currentElement5 = this.directionColorType.getCurrentElement();
               if (currentElement5 == 0) {
                  i8 = this.directionColor.getColor();
               } else if (currentElement5 == 1) {
                  i8 = RenderUtils.indexedHsbColor(1, 1, 0.6F, 1.0F, 2000L);
               } else if (currentElement5 == 2) {
                  i8 = RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, 2000L);
               }

               String text = String.valueOf(enumFacing2.getName().charAt(0)).toUpperCase(Locale.ROOT);
               GlStateManager.pushMatrix();
               GlStateManager.translate(d + d4 * Math.cos(d11), d2 + d5 * Math.sin(d11), 0.0);
               minecraft.fontRendererObj.drawStringWithShadow(text, -2.5F, -4.5F, i8);
               GlStateManager.popMatrix();
            }
         }
      }
   };

   public Radar() {
      super(Category.VISUAL, "Radar", "Shows arrows to players");
   }

   private EnumFacing byHorizontalIndex(int i) {
      return Arrays.stream(EnumFacing.values()).filter(enumFacing -> enumFacing.getHorizontalIndex() == i).findFirst().orElse(null);
   }

   private float getFacingYaw(EnumFacing enumFacing2) {
      return (enumFacing2.getHorizontalIndex() & 3) * 90.0F;
   }
}
