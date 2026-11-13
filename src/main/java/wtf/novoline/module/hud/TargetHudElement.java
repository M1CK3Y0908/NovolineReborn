package wtf.novoline.module.hud;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.menu.MenuClickGuiScreen;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.combat.Killaura;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.animation.Easing;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shape.GradientRectRenderer;
import wtf.novoline.utils.render.shape.RoundedRectRenderer;

public class TargetHudElement extends HudElement {
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   public boolean shown;
   private final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);
   private ResourceLocation skin;
   private float nameWidth;
   private ScaledResolution scaledResolution;
   private float animatedArmor;
   private EntityPlayer entityPlayer;
   private final Animation animation = new Animation();
   private float animatedHealth;

   @Override
   public float getWidth() {
      return ModuleManager.targetHUD.targetHUD.getCurrentElement() == 2 ? Math.max(130.0F, 38.0F + this.nameWidth) : Math.max(100.0F, 30.0F + this.nameWidth);
   }

   @Override
   public float getX() {
      return this.x + this.scaledResolution.getScaledWidth() / 2.0F - this.getWidth() / 2.0F;
   }

   @Override
   public void draw(ScaledResolution scaledResolution2, float f2, boolean flag) {
      this.scaledResolution = scaledResolution2;
      if (ModuleManager.targetHUD.isEnabled()) {
         Killaura killaura2 = ModuleManager.killaura;
         boolean flag2 = killaura2.isEnabled() && killaura2.entityLivingBase instanceof EntityPlayer;
         if (flag2) {
            this.entityPlayer = (EntityPlayer)killaura2.entityLivingBase;
         }

         if (flag) {
            this.entityPlayer = minecraft.thePlayer;
         }

         if (!flag2 && !flag) {
            if (this.shown) {
               this.shown = false;
               this.animation.unlock();
               this.animation.start(this.animation.getValue(), 0.0, 250L, Easing.backInOut);
               if (this.entityPlayer == minecraft.thePlayer) {
                  this.animation.stop();
               }
            }
         } else if (!this.shown) {
            this.shown = true;
            this.animation.unlock();
            this.animation.start(this.animation.getValue(), 100.0, 250L, Easing.backInOut);
            if (this.entityPlayer == minecraft.thePlayer) {
               this.animation.stop();
            }
         }

         this.animation.update();
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.getX() + this.getWidth() / 2.0F, this.getY() + this.getHeight() / 2.0F, 0.0F);
         double d2 = Math.max(0.0, this.animation.getValue() / 100.0);
         GlStateManager.scale(d2, d2, 0.0);
         GlStateManager.translate(-(this.getX() + this.getWidth() / 2.0F), -(this.getY() + this.getHeight() / 2.0F), 0.0F);
         if (this.entityPlayer != null) {
            NetworkPlayerInfo playerInfo = minecraft.thePlayer.sendQueue.getPlayerInfo(this.entityPlayer.getUniqueID());
            if (playerInfo != null) {
               this.skin = playerInfo.getLocationSkin();
            }

            String name = this.entityPlayer.getName();
            NameProtect nameProtect2 = ModuleManager.nameProtect;
            if (nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(1)) {
               name = nameProtect2.protectText(name);
            }

            if (ModuleManager.targetHUD.addons.isSelected(0)) {
               this.blurRenderer
                  .blur(
                     "Panel",
                     minecraft.getFramebuffer(),
                     scaledResolution2,
                     (int)ModuleManager.targetHUD.blurRadius.getCurrentValue(),
                     ModuleManager.targetHUD.blurSigma.getCurrentValue(),
                     1
                  );
               minecraft.getFramebuffer().bindFramebuffer(true);
               this.blurRenderer.bindResultTexture();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GradientRectRenderer gradientRectRenderer = new GradientRectRenderer((float)scaledResolution2.getScaledWidth_double(), (float)scaledResolution2.getScaledHeight_double());
               gradientRectRenderer.addRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1440735200);
               gradientRectRenderer.draw();
               this.blurRenderer.releaseResult();
               minecraft.getTextureManager().bindTexture(MenuClickGuiScreen.NOISE_TEXTURE);
               GlStateManager.alphaFunc(516, 0.001F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 0.08F);
               RoundedRectRenderer roundedRectRenderer = new RoundedRectRenderer(128.0F, 128.0F);
               roundedRectRenderer.addRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1440735200);
               roundedRectRenderer.draw();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (ModuleManager.targetHUD.addons.isSelected(1)) {
               RenderUtils.drawShadow(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 2.0F, 1073741824);
            }

            int currentElement = ModuleManager.targetHUD.targetHUD.getCurrentElement();
            if (currentElement == 0) {
               this.nameWidth = Fonts.rubikSemiboldSize18.getStringWidth(name);
               float maxHealth = this.entityPlayer.getMaxHealth();
               this.animatedHealth = SmoothingUtils.approachWithMinimumStep(this.animatedHealth, this.entityPlayer.getHealth(), 10.0F, 0.01F);
               this.animatedArmor = SmoothingUtils.approachWithMinimumStep(this.animatedArmor, this.entityPlayer.getTotalArmorValue(), 10.0F, 0.01F);
               RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -2061426399);
               if (this.skin != null) {
                  GlStateManager.enableBlend();
                  GlStateManager.enableAlpha();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  minecraft.getTextureManager().bindTexture(this.skin);
                  RenderUtils.drawScaledTexturedRect(this.getX() + 2.0F, this.getY() + 2.0F, 8.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
                  if (this.entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                     RenderUtils.drawScaledTexturedRect(this.getX() + 2.0F, this.getY() + 2.0F, 40.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
                  }

                  GlStateManager.disableBlend();
                  GlStateManager.enableAlpha();
               }

               Fonts.rubikSemiboldSize18.drawString(name, this.getX() + 28.0F, this.getY() + 4.0F, -1);
               Fonts.rubikRegularSize14
                  .drawString(String.format(Locale.ENGLISH, "Health: %.1f", this.animatedHealth), this.getX() + 28.0F, this.getY() + 13.5F, -986896);
               Fonts.rubikRegularSize14
                  .drawString(
                     String.format(Locale.ENGLISH, "Distance: %.1fm", this.entityPlayer.getDistanceToEntity(minecraft.thePlayer)),
                     this.getX() + 28.0F,
                     this.getY() + 21.0F,
                     -986896
                  );
               float f3 = this.getWidth() - 2.0F;
               float f4 = f3 * (this.animatedHealth / maxHealth);
               float f5 = f3 * (this.animatedArmor / 20.0F);
               RenderUtils.drawRect(
                  this.getX() + 2.0F - 0.5F,
                  this.getY() + 29.0F - 0.5F,
                  this.getX() + this.getWidth() - 2.0F + 0.5F,
                  this.getY() + 29.0F + 1.5F + 0.5F,
                  1342177280
               );
               RenderUtils.drawHorizontalGradientRect(
                  this.getX() + 2.0F, this.getY() + 29.0F, this.getX() + f4, this.getY() + 29.0F + 1.5F, -16737215, -7405631
               );
               RenderUtils.drawRect(
                  this.getX() + 2.0F - 0.5F,
                  this.getY() + 29.0F + 3.0F - 0.5F,
                  this.getX() + this.getWidth() - 2.0F + 0.5F,
                  this.getY() + 29.0F + 3.0F + 1.5F + 0.5F,
                  1342177280
               );
               RenderUtils.drawHorizontalGradientRect(
                  this.getX() + 2.0F, this.getY() + 29.0F + 3.0F, this.getX() + f5, this.getY() + 29.0F + 3.0F + 1.5F, -16750672, -12986881
               );
            } else if (currentElement == 1) {
               this.nameWidth = Fonts.rubikSemiboldSize18.getStringWidth(name);
               float maxHealth2 = this.entityPlayer.getMaxHealth();
               this.animatedHealth = SmoothingUtils.approachWithMinimumStep(this.animatedHealth, this.entityPlayer.getHealth(), 10.0F, 0.01F);
               this.animatedArmor = SmoothingUtils.approachWithMinimumStep(this.animatedArmor, this.entityPlayer.getTotalArmorValue(), 10.0F, 0.01F);
               RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -2061426399);
               if (this.skin != null) {
                  GlStateManager.enableBlend();
                  GlStateManager.enableAlpha();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  minecraft.getTextureManager().bindTexture(this.skin);
                  RenderUtils.drawScaledTexturedRect(this.getX() + 2.0F, this.getY() + 2.0F, 8.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
                  if (this.entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                     RenderUtils.drawScaledTexturedRect(this.getX() + 2.0F, this.getY() + 2.0F, 40.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
                  }

                  GlStateManager.disableBlend();
                  GlStateManager.enableAlpha();
               }

               Fonts.rubikSemiboldSize18.drawString(name, this.getX() + 28.0F, this.getY() + 4.0F, -1);
               Fonts.rubikRegularSize14
                  .drawString(
                     String.format(Locale.ENGLISH, "%d HP - %dm", (int)this.animatedHealth, (int)this.entityPlayer.getDistanceToEntity(minecraft.thePlayer)),
                     this.getX() + 28.0F,
                     this.getY() + 13.5F,
                     -986896
                  );
               float f6 = this.getWidth() - 28.0F - 2.0F;
               float f7 = f6 * (this.animatedHealth / maxHealth2);
               RenderUtils.drawRect(
                  this.getX() + 28.0F - 0.5F,
                  this.getY() + 21.0F - 0.5F,
                  this.getX() + this.getWidth() - 2.0F + 0.5F,
                  this.getY() + 24.0F + 1.5F + 0.5F,
                  1342177280
               );
               RenderUtils.drawHorizontalGradientRect(
                  this.getX() + 28.0F, this.getY() + 21.0F, this.getX() + 28.0F + f7, this.getY() + 24.0F + 1.5F, -3527576, -33608
               );
            } else if (currentElement == 2) {
               this.nameWidth = Fonts.rubikSemiboldSize18.getStringWidth(name);
               float maxHealth3 = this.entityPlayer.getMaxHealth();
               this.animatedHealth = SmoothingUtils.approachWithMinimumStep(this.animatedHealth, this.entityPlayer.getHealth(), 10.0F, 0.01F);
               this.animatedArmor = SmoothingUtils.approachWithMinimumStep(this.animatedArmor, this.entityPlayer.getTotalArmorValue(), 10.0F, 0.01F);
               RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -2061426399);
               if (this.skin != null) {
                  GlStateManager.enableBlend();
                  GlStateManager.enableAlpha();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  minecraft.getTextureManager().bindTexture(this.skin);
                  RenderUtils.drawScaledTexturedRect(this.getX() + 3.0F, this.getY() + 3.0F, 8.0F, 8.0F, 8.0F, 8.0F, 28.0F, 28.0F, 64.0F, 64.0F);
                  if (this.entityPlayer.isWearing(EnumPlayerModelParts.HAT)) {
                     RenderUtils.drawScaledTexturedRect(this.getX() + 3.0F, this.getY() + 3.0F, 40.0F, 8.0F, 8.0F, 8.0F, 28.0F, 28.0F, 64.0F, 64.0F);
                  }

                  GlStateManager.disableBlend();
                  GlStateManager.enableAlpha();
               }

               Fonts.rubikSemiboldSize18.drawString(name, this.getX() + 34.0F, this.getY() + 7.0F, -1);
               Fonts.rubikRegularSize14
                  .drawString(
                     String.format(Locale.ENGLISH, "%d HP - %dm", (int)this.animatedHealth, (int)this.entityPlayer.getDistanceToEntity(minecraft.thePlayer)),
                     this.getX() + 34.0F,
                     this.getY() + 16.5F,
                     -986896
                  );
               float f8 = this.getWidth() - 34.0F - 4.0F;
               float f9 = f8 * (this.animatedHealth / maxHealth3);
               RenderUtils.drawRect(this.getX() + 34.0F, this.getY() + 24.5F, this.getX() + this.getWidth() - 4.0F, this.getY() + 26.5F + 1.5F, -1876942816);
               RenderUtils.drawShadow(
                  this.getX() + 34.0F,
                  this.getY() + 24.5F,
                  this.getX() + 34.0F + f9,
                  this.getY() + 26.5F + 1.5F,
                  2.0F,
                  RenderUtils.withAlphaFraction(-14039553, 0.4F)
               );
               RenderUtils.drawRect(this.getX() + 34.0F, this.getY() + 24.5F, this.getX() + 34.0F + f9, this.getY() + 26.5F + 1.5F, -14039553);
            }
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public float getHeight() {
      int currentElement = ModuleManager.targetHUD.targetHUD.getCurrentElement();
      if (currentElement == 0) {
         return 35.5F;
      } else if (currentElement == 1) {
         return 28.0F;
      } else {
         return currentElement == 2 ? 34.0F : 0.0F;
      }
   }

   public TargetHudElement() {
      super("target-hud", 0.0F, 0.0F, true);
   }

   @Override
   public float getY() {
      return this.y + this.scaledResolution.getScaledHeight() / 2.0F + this.getHeight();
   }
}
