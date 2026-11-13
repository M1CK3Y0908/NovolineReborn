package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.RelationType;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.NameUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.render.BloomRenderer;
import wtf.novoline.utils.render.BlurFramebuffer;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class PlayerESP extends Module {
   public final ComboSetting playerESP = settingManager.register(new ComboSetting(this, "Mode", 3, "Vanilla", "Shader", "3D Box", "2D Box"));
   private BlurFramebuffer blurFramebuffer;
   private final MultiComboSetting elements = settingManager.register((new MultiComboSetting(this, "Elements") {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3;
      }
   }).add("Name", true).add("Box", true).add("Health bar", true).add("Health text", true).add("Item in hand", true));
   private final MultiComboSetting overrideFriend = settingManager.register(new PlayerESP.MultiComboSettingChild(this, "Override friend"))
      .add("Name", true)
      .add("Box", false)
      .add("Health text", false);
   private final MultiComboSetting overrideTarget = settingManager.register(new PlayerESP.MultiComboSettingChild2(this, "Override target"))
      .add("Name", true)
      .add("Box", false)
      .add("Health text", false);
   private final MultiComboSetting ignoreRules = settingManager.register(
      new MultiComboSetting(this, "Ignore rules").add("Self", false).add("Invisible", false).add("Bots", true).add("Team", false)
   );
   public static boolean renderingEsp;
   public final ComboSetting boxType = settingManager.register(new ComboSetting(this, "Box type", 3, "Colored", "Health based", "Rainbow", "Astolfo") {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3 && PlayerESP.this.elements.getElements().get(1).isSelected();
      }
   });
   public final ComboSetting healthType = settingManager.register(
      new ComboSetting(this, "Health type", 5, "Colored", "Health based", "Health gradient", "Custom gradient", "Rainbow", "Astolfo") {
         @Override
         public boolean shouldShow() {
            return PlayerESP.this.playerESP.getCurrentElement() == 3 && PlayerESP.this.elements.getElements().get(2).isSelected();
         }
      }
   );
   private final ColorSetting name = settingManager.register(new ColorSetting(this, "Name", -1) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3 && PlayerESP.this.elements.getElements().get(0).isSelected();
      }
   });
   private final ColorSetting box = settingManager.register(new ColorSetting(this, "Box", -1) {
      @Override
      public boolean shouldShow() {
         int currentElement = PlayerESP.this.playerESP.getCurrentElement();
         int currentElement2 = PlayerESP.this.boxType.getCurrentElement();
         return currentElement == 3 && currentElement2 == 0 && PlayerESP.this.elements.getElements().get(1).isSelected();
      }
   });
   private final ColorSetting healthText = settingManager.register(new ColorSetting(this, "Health text", -1) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3 && PlayerESP.this.elements.getElements().get(3).isSelected();
      }
   });
   private final ColorSetting itemInHand = settingManager.register(new ColorSetting(this, "Item in hand", -4144960) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3 && PlayerESP.this.elements.getElements().get(4).isSelected();
      }
   });
   private final ColorSetting health = settingManager.register(new ColorSetting(this, "Health", -12657535) {
      @Override
      public boolean shouldShow() {
         int currentElement = PlayerESP.this.playerESP.getCurrentElement();
         int currentElement2 = PlayerESP.this.healthType.getCurrentElement();
         return currentElement == 3 && currentElement2 == 0 && PlayerESP.this.elements.getElements().get(2).isSelected();
      }
   });
   private final ColorSetting gradientUp = settingManager.register(new ColorSetting(this, "Gradient up", -12657535) {
      @Override
      public boolean shouldShow() {
         int currentElement = PlayerESP.this.playerESP.getCurrentElement();
         int currentElement2 = PlayerESP.this.healthType.getCurrentElement();
         return currentElement == 3 && currentElement2 == 3 && PlayerESP.this.elements.getElements().get(2).isSelected();
      }
   });
   private final ColorSetting gradientDown = settingManager.register(new ColorSetting(this, "Gradient down", -12657535) {
      @Override
      public boolean shouldShow() {
         int currentElement = PlayerESP.this.playerESP.getCurrentElement();
         int currentElement2 = PlayerESP.this.healthType.getCurrentElement();
         return currentElement == 3 && currentElement2 == 3 && PlayerESP.this.elements.getElements().get(2).isSelected();
      }
   });
   private final ColorSetting main = settingManager.register(new ColorSetting(this, "Main", -14506000) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() != 3;
      }
   });
   public static final boolean SHADERS_SUPPORTED = OpenGlHelper.shadersSupported;
   private final ColorSetting friend = settingManager.register(
      new ColorSetting(this, "Friend", -12657535) {
         @Override
         public boolean shouldShow() {
            return PlayerESP.this.playerESP.getCurrentElement() != 3
               || PlayerESP.this.overrideFriend.getElements().get(0).isSelected()
               || PlayerESP.this.overrideFriend.getElements().get(1).isSelected()
               || PlayerESP.this.overrideFriend.getElements().get(2).isSelected();
         }
      }
   );
   private final ColorSetting target = settingManager.register(
      new ColorSetting(this, "Target", -2341314) {
         @Override
         public boolean shouldShow() {
            return PlayerESP.this.playerESP.getCurrentElement() != 3
               || PlayerESP.this.overrideTarget.isSelected(0)
               || PlayerESP.this.overrideTarget.isSelected(1)
               || PlayerESP.this.overrideTarget.isSelected(2);
         }
      }
   );
   private final NumberSetting shaderRadius = settingManager.register(new NumberSetting(this, "Shader radius", 20, 1, 40) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 1;
      }
   });
   private final NumberSetting shaderSigma = settingManager.register(new NumberSetting(this, "Shader sigma", 1.0F, 0.01F, 20.0F) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 1;
      }
   });
   private final NumberSetting shaderExposure = settingManager.register(new NumberSetting(this, "Shader exposure", 2.5F, 0.0F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 1;
      }
   });
   private final NumberSetting shaderGamma = settingManager.register(new NumberSetting(this, "Shader gamma", 1.0F, 0.0F, 10.0F) {
      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 1;
      }
   });
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> this.setSuffix(this.playerESP.getCurrentElementAsString());
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      int currentElement = this.playerESP.getCurrentElement();
      if (currentElement == 1 && !SHADERS_SUPPORTED) {
         this.setEnabled(false);
         Novoline.getNovoline().sendChatMessage("§cWarning! Shader mode is not supported on this PC!");
      }

      if (currentElement == 1 && SHADERS_SUPPORTED && this.renderedTargets) {
         GlStateManager.pushMatrix();
         this.bloomRenderer
            .composeBloom(
               minecraft.getFramebuffer(),
               this.blurFramebuffer,
               renderScreenEvent.scaledResolution,
               "Player ESP shader",
               (int)this.shaderRadius.getCurrentValue(),
               this.shaderSigma.getCurrentValue(),
               0.0F,
               this.shaderExposure.getCurrentValue(),
               this.shaderGamma.getCurrentValue()
            );
         GlStateManager.popMatrix();
      }

      if (currentElement == 3) {
         for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
            if (!this.isIgnored(entityPlayer)) {
               RelationType relationType = novoline.getRelationManager().get(entityPlayer.getName());
               double d = (entityPlayer.posX - entityPlayer.prevPosX) * (renderScreenEvent.partialTicks - 1.0F);
               double d2 = (entityPlayer.posY - entityPlayer.prevPosY) * (renderScreenEvent.partialTicks - 1.0F);
               double d3 = (entityPlayer.posZ - entityPlayer.prevPosZ) * (renderScreenEvent.partialTicks - 1.0F);
               AxisAlignedBB axisAlignedBB = entityPlayer.getEntityBoundingBox()
                  .offset(d, d2, d3)
                  .offset(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
               double d4 = 0.0;
               if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(entityPlayer)) {
                  int currentElement2 = ModuleManager.customModel.customModel.getCurrentElement();
                  if (currentElement2 == 0) {
                     d4 = 0.4F;
                     if (entityPlayer.isSneaking()) {
                        d4 = 0.15F;
                     }
                  }
               }

               axisAlignedBB = new AxisAlignedBB(axisAlignedBB.minX - 0.1F, axisAlignedBB.minY, axisAlignedBB.minZ - 0.1F, axisAlignedBB.maxX + 0.1F, axisAlignedBB.maxY + 0.1F - d4, axisAlignedBB.maxZ + 0.1F);
               Vec3[] vec3s = new Vec3[]{
                  new Vec3(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ),
                  new Vec3(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ),
                  new Vec3(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ),
                  new Vec3(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ),
                  new Vec3(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ),
                  new Vec3(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ),
                  new Vec3(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ),
                  new Vec3(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ)
               };
               float[] fArray = RenderUtils.worldToScreen(vec3s[3]);
               float[] fArray2 = RenderUtils.worldToScreen(vec3s[5]);
               float[] fArray3 = RenderUtils.worldToScreen(vec3s[0]);
               float[] fArray4 = RenderUtils.worldToScreen(vec3s[4]);
               float[] fArray5 = RenderUtils.worldToScreen(vec3s[2]);
               float[] fArray6 = RenderUtils.worldToScreen(vec3s[1]);
               float[] fArray7 = RenderUtils.worldToScreen(vec3s[6]);
               float[] fArray8 = RenderUtils.worldToScreen(vec3s[7]);
               if (fArray != null && fArray2 != null && fArray3 != null && fArray4 != null && fArray5 != null && fArray6 != null && fArray7 != null && fArray8 != null) {
                  float[][] fArrays = new float[][]{fArray, fArray2, fArray3, fArray4, fArray5, fArray6, fArray7, fArray8};
                  float f = fArray[0];
                  float f2 = fArray[1];
                  float f3 = fArray[0];
                  float f4 = fArray[1];

                  for (int i = 1; i < 8; i++) {
                     if (f > fArrays[i][0]) {
                        f = fArrays[i][0];
                     }

                     if (f2 < fArrays[i][1]) {
                        f2 = fArrays[i][1];
                     }

                     if (f3 < fArrays[i][0]) {
                        f3 = fArrays[i][0];
                     }

                     if (f4 > fArrays[i][1]) {
                        f4 = fArrays[i][1];
                     }
                  }

                  boolean flag = false;
                  if (ModuleManager.antibot.isEnabled()) {
                     flag = ModuleManager.antibot.isPlayerBot(entityPlayer);
                  }

                  if (this.elements.getElements().get(1).isSelected()) {
                     RenderUtils.drawLine(f - 0.5F, f4 - 0.5F, f - 0.5F, f2 + 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f - 0.5F, f2 + 0.5F, f3 + 0.5F, f2 + 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f3 + 0.5F, f2 + 0.5F, f3 + 0.5F, f4 - 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f3 + 0.5F, f4 - 0.5F, f - 0.5F, f4 - 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f + 0.5F, f4 + 0.5F, f + 0.5F, f2 - 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f + 0.5F, f2 - 0.5F, f3 - 0.5F, f2 - 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f3 - 0.5F, f2 - 0.5F, f3 - 0.5F, f4 + 0.5F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f3 - 0.5F, f4 + 0.5F, f + 0.5F, f4 + 0.5F, Integer.MIN_VALUE, false);
                     if (relationType == null && !entityPlayer.equals(ModuleManager.bowAimbot.getEntity())) {
                        int currentElement3 = this.boxType.getCurrentElement();
                        if (currentElement3 != 2 && currentElement3 != 3) {
                           int i2 = -1;
                           if (currentElement3 == 0) {
                              i2 = this.box.getColor();
                           } else if (currentElement3 == 1) {
                              i2 = this.box.getColor();
                           }

                           if (flag) {
                              i2 = -138329;
                           }

                           RenderUtils.drawLine(f, f4, f, f2, i2, false);
                           RenderUtils.drawLine(f, f2, f3, f2, i2, false);
                           RenderUtils.drawLine(f3, f2, f3, f4, i2, false);
                           RenderUtils.drawLine(f3, f4, f, f4, i2, false);
                        } else {
                           int i3 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 1, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, 2500L);
                           int i4 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 8, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 8, 0.6F, 1.0F, 2500L);
                           int i5 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 16, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 16, 0.6F, 1.0F, 2500L);
                           int i6 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 32, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 32, 0.6F, 1.0F, 2500L);
                           int i7 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 64, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 64, 0.6F, 1.0F, 2500L);
                           int i8 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 128, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 128, 0.6F, 1.0F, 2500L);
                           int i9 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 256, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 256, 0.6F, 1.0F, 2500L);
                           int i10 = currentElement3 == 2
                              ? RenderUtils.indexedHsbColor(1, 512, 0.6F, 1.0F, 2500L)
                              : RenderUtils.indexedHsbColorMirrored(1, 512, 0.6F, 1.0F, 2500L);
                           RenderUtils.drawGradientLine(f, f4, f, f2, 1.0F, i3, i4, false);
                           RenderUtils.drawGradientLine(f, f2, f3, f2, 1.0F, i5, i6, false);
                           RenderUtils.drawGradientLine(f3, f2, f3, f4, 1.0F, i7, i8, false);
                           RenderUtils.drawGradientLine(f3, f4, f, f4, 1.0F, i9, i10, false);
                        }
                     } else {
                        int entityColor = this.getEntityColor(entityPlayer);
                        RenderUtils.drawLine(f, f4, f, f2, entityColor, false);
                        RenderUtils.drawLine(f, f2, f3, f2, entityColor, false);
                        RenderUtils.drawLine(f3, f2, f3, f4, entityColor, false);
                        RenderUtils.drawLine(f3, f4, f, f4, entityColor, false);
                     }
                  }

                  CFontRenderer cFontRenderer = Fonts.pixelSize8;
                  float f5 = f - 2.5F;
                  float f6 = (f2 - f4) / entityPlayer.getMaxHealth();
                  if (this.elements.getElements().get(2).isSelected()) {
                     RenderUtils.drawLine(f5 - 0.5F, f4 - 0.5F, f5 - 0.5F, f2 + 0.5F, 2.0F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f5 + 0.5F, f4 - 0.5F, f5 + 0.5F, f2 + 0.5F, 2.0F, Integer.MIN_VALUE, false);
                     RenderUtils.drawLine(f5, f4 - 0.5F, f5, f2 + 0.5F, 2.0F, Integer.MIN_VALUE, false);
                     int currentElement4 = this.healthType.getCurrentElement();
                     if (currentElement4 == 3 || currentElement4 == 4 || currentElement4 == 5) {
                        int i11 = -1;
                        int i12 = -1;
                        if (currentElement4 == 3) {
                           i11 = this.gradientUp.getColor();
                           i12 = this.gradientDown.getColor();
                        } else if (currentElement4 == 4) {
                           i11 = RenderUtils.indexedHsbColor(1, 1, 0.6F, 1.0F, 2500L);
                           i12 = RenderUtils.indexedHsbColor(1, 8, 0.6F, 1.0F, 2500L);
                        } else if (currentElement4 == 5) {
                           i11 = RenderUtils.indexedHsbColorMirrored(1, 1, 0.6F, 1.0F, 2500L);
                           i12 = RenderUtils.indexedHsbColorMirrored(1, 8, 0.6F, 1.0F, 2500L);
                        }

                        RenderUtils.drawGradientLine(f5, f4, f5, f2, 2.0F, i11, i12, false);
                     } else if (currentElement4 == 2) {
                        RenderUtils.drawGradientLine(f5, f4, f5, f4 + f6 * entityPlayer.getMaxHealth() / 2.0F, 2.0F, -12657535, -12469, false);
                        RenderUtils.drawGradientLine(f5, f4 + f6 * entityPlayer.getMaxHealth() / 2.0F, f5, f2, 2.0F, -12469, -1618884, false);
                     } else {
                        int i13 = -1;
                        if (currentElement4 == 0) {
                           i13 = this.health.getColor();
                        } else if (currentElement4 == 1) {
                           i13 = this.health.getColor();
                        }

                        RenderUtils.drawLine(f5, f4, f5, f2, 2.0F, i13, false);
                     }

                     RenderUtils.drawLine(f5, f4, f5, f4 + f6 * (entityPlayer.getMaxHealth() - entityPlayer.getHealth()), 2.0F, -16777216, false);
                  }

                  if (this.elements.getElements().get(3).isSelected()) {
                     String text2 = (int)entityPlayer.getHealth() + " HP";
                     int color = this.healthText.getColor();
                     if (this.overrideFriend.isSelected(2) && relationType == RelationType.FRIEND) {
                        color = this.friend.getColor();
                     } else if (this.overrideTarget.isSelected(2) && relationType == RelationType.TARGET) {
                        color = this.target.getColor();
                     }

                     cFontRenderer.drawStringWithNiceShadow(
                        text2, f5 - cFontRenderer.getStringWidth(text2) - 1.5F, f4 + f6 * (entityPlayer.getMaxHealth() - entityPlayer.getHealth()) + 3.0F, color
                     );
                  }

                  String text3 = NameUtils.stripFormatting(entityPlayer.getName());
                  if (flag) {
                     text3 = "§e[BOT] §r" + text3;
                  }

                  NameProtect nameProtect2 = ModuleManager.nameProtect;
                  if (nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(2)) {
                     text3 = nameProtect2.protectText(text3);
                  }

                  String text4 = "";
                  ItemStack heldItem = entityPlayer.getHeldItem();
                  if (heldItem != null && heldItem.getItem() != null) {
                     text4 = NameUtils.stripFormatting(heldItem.getDisplayName());
                  }

                  if (this.elements.getElements().get(0).isSelected()) {
                     float f7 = (f + f3) / 2.0F;
                     float f8 = f4 - 3.0F;
                     int color2 = this.healthText.getColor();
                     if (this.overrideFriend.isSelected(0) && relationType == RelationType.FRIEND) {
                        color2 = this.friend.getColor();
                     } else if (this.overrideTarget.isSelected(0) && relationType == RelationType.TARGET) {
                        color2 = this.target.getColor();
                     }

                     cFontRenderer.drawStringWithNiceShadowNoColor(text3, f7 - cFontRenderer.getStringWidth(text3) / 2.0F, f8 - cFontRenderer.getHeight(), color2);
                  }

                  if (this.elements.getElements().get(4).isSelected() && !text4.isEmpty()) {
                     float f9 = (f + f3) / 2.0F;
                     float f10 = f2 + 5.0F;
                     cFontRenderer.drawStringWithNiceShadow(text4, f9 - cFontRenderer.getStringWidth(text4) / 2.0F, f10 - cFontRenderer.getHeight(), this.itemInHand.getColor());
                  }
               }
            }
         }
      }

      if (SHADERS_SUPPORTED) {
      }
   };
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      int currentElement = this.playerESP.getCurrentElement();
      if (currentElement == 1 && SHADERS_SUPPORTED && !minecraft.gameSettings.hideGUI) {
         this.blurFramebuffer = Novoline.framebufferManager.acquire("PlayerESP target");
         this.blurFramebuffer.bindFramebuffer();
         this.renderedTargets = this.renderPlayers(renderWorldEvent.partialTicks);
         minecraft.getFramebuffer().bindFramebuffer(true);
         if (!this.renderedTargets) {
            this.blurFramebuffer.release();
         }
      }

      if (currentElement == 2) {
         for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
            if (!this.isIgnored(entityPlayer)) {
               double d = (entityPlayer.posX - entityPlayer.prevPosX) * (renderWorldEvent.partialTicks - 1.0F);
               double d2 = (entityPlayer.posY - entityPlayer.prevPosY) * (renderWorldEvent.partialTicks - 1.0F);
               double d3 = (entityPlayer.posZ - entityPlayer.prevPosZ) * (renderWorldEvent.partialTicks - 1.0F);
               int entityColor = this.getEntityColor(entityPlayer);
               RenderUtils.drawBoxOutlineAtRenderPos(entityPlayer.getEntityBoundingBox().offset(d, d2, d3), entityColor, renderWorldEvent.partialTicks);
            }
         }
      }
   };
   private boolean renderedTargets;
   public static boolean renderingEspModel;
   private BloomRenderer bloomRenderer;

   public PlayerESP() {
      super(Category.VISUAL, "PlayerESP", "Applies some visuals to players");
      if (SHADERS_SUPPORTED) {
         this.bloomRenderer = new BloomRenderer(Novoline.framebufferManager);
      } else {
         novoline.sendChatMessage("Warning! Shaders are not supported!");
      }
   }

   private boolean isIgnored(EntityPlayer entityPlayer) {
      if (entityPlayer != minecraft.thePlayer || !this.ignoreRules.getElements().get(0).isSelected() && minecraft.gameSettings.thirdPersonView != 0) {
         if (entityPlayer.isInvisible() && this.ignoreRules.getElements().get(1).isSelected()) {
            return true;
         } else {
            return this.ignoreRules.getElements().get(2).isSelected() && ModuleManager.antibot.isEnabled() && ModuleManager.antibot.isPlayerBot(entityPlayer)
               ? true
               : this.ignoreRules.isSelected(3) && PlayerUtils.isSameTeam(minecraft.thePlayer, entityPlayer);
         }
      } else {
         return true;
      }
   }

   private int getEntityColor(Entity entity2) {
      RelationType relationType = novoline.getRelationManager().get(entity2.getName());
      int color = this.main.getColor();
      if (relationType == RelationType.FRIEND) {
         color = this.friend.getColor();
      } else if (relationType == RelationType.TARGET) {
         color = this.target.getColor();
      } else if (entity2.equals(ModuleManager.bowAimbot.getEntity())) {
         color = -65536;
      }

      return color;
   }

   @Override
   protected void onDisable() {
      if (SHADERS_SUPPORTED) {
      }
   }

   @Override
   protected void onEnable() {
   }

   private boolean renderPlayers(float f) {
      boolean flag = false;
      GlStateManager.pushAttrib();

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (!this.isIgnored(entityPlayer)) {
            int entityColor = this.getEntityColor(entityPlayer);
            GlStateManager.enableColorMaterial();
            RenderUtils.setTextureEnvColor(entityColor);
            RenderManager renderManager = minecraft.getRenderManager();
            double d = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * f;
            double d2 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * f;
            double d3 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * f;
            float f2 = entityPlayer.prevRotationYaw + (entityPlayer.rotationYaw - entityPlayer.prevRotationYaw) * f;
            Render entityRenderObject = minecraft.getRenderManager().getEntityRenderObject(entityPlayer);
            if (entityRenderObject != null) {
               renderingEspModel = true;
               renderingEsp = true;
               entityRenderObject.doRender2(entityPlayer, d - renderManager.viewerPosX, d2 - renderManager.viewerPosY, d3 - renderManager.viewerPosZ, f2, f);
               renderingEspModel = false;
               renderingEsp = false;
            }

            GlStateManager.disableColorMaterial();
            RenderUtils.setTextureEnvModulate();
            flag = true;
         }
      }

      GlStateManager.popAttrib();
      return flag;
   }

   class MultiComboSettingChild extends MultiComboSetting {
      MultiComboSettingChild(Module module2, String text2) {
         super(module2, text2);
      }

      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3;
      }
   }

   class MultiComboSettingChild2 extends MultiComboSetting {
      MultiComboSettingChild2(Module module2, String text2) {
         super(module2, text2);
      }

      @Override
      public boolean shouldShow() {
         return PlayerESP.this.playerESP.getCurrentElement() == 3;
      }
   }
}
