package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements extends GuiScreen implements IProgressMeter {
   private static final int minDisplayColumn = AchievementList.minDisplayColumn * 24 - 112;
   protected int guiWidth = 256;
   protected int guiHeight = 202;
   private static final int minDisplayRow = AchievementList.minDisplayRow * 24 - 112;
   protected float scale = 1.0F;
   private boolean loadingAchievements = true;
   protected GuiScreen parentScreen;
   private StatFileWriter statFileWriter;
   protected double targetDisplayX;
   protected double displayX;
   protected int lastMouseX;
   private int dragState;
   protected double prevDisplayX;
   protected double targetDisplayY;
   private static final int maxDisplayColumn = AchievementList.maxDisplayColumn * 24 - 77;
   protected double displayY;
   private static final int maxDisplayRow = AchievementList.maxDisplayRow * 24 - 77;
   protected int lastMouseY;
   private static final ResourceLocation ACHIEVEMENT_BACKGROUND = new ResourceLocation("textures/gui/achievement/achievement_background.png");
   protected double prevDisplayY;

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.loadingAchievements) {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         if (Mouse.isButtonDown(0)) {
            int i = (this.width - this.guiWidth) / 2;
            int i2 = (this.height - this.guiHeight) / 2;
            int i3 = i + 8;
            int i4 = i2 + 17;
            if ((this.dragState == 0 || this.dragState == 1) && mouseX >= i3 && mouseX < i3 + 224 && mouseY >= i4 && mouseY < i4 + 155) {
               if (this.dragState == 0) {
                  this.dragState = 1;
               } else {
                  this.displayX = this.displayX - (mouseX - this.lastMouseX) * this.scale;
                  this.displayY = this.displayY - (mouseY - this.lastMouseY) * this.scale;
                  this.targetDisplayX = this.prevDisplayX = this.displayX;
                  this.targetDisplayY = this.prevDisplayY = this.displayY;
               }

               this.lastMouseX = mouseX;
               this.lastMouseY = mouseY;
            }
         } else {
            this.dragState = 0;
         }

         int dWheel = Mouse.getDWheel();
         float scale2 = this.scale;
         if (dWheel < 0) {
            this.scale += 0.25F;
         } else if (dWheel > 0) {
            this.scale -= 0.25F;
         }

         this.scale = MathHelper.clamp_float(this.scale, 1.0F, 2.0F);
         if (this.scale != scale2) {
            float f = scale2 * this.guiWidth;
            float f2 = scale2 * this.guiHeight;
            float f3 = this.scale * this.guiWidth;
            float f4 = this.scale * this.guiHeight;
            this.displayX -= (f3 - f) * 0.5F;
            this.displayY -= (f4 - f2) * 0.5F;
            this.targetDisplayX = this.prevDisplayX = this.displayX;
            this.targetDisplayY = this.prevDisplayY = this.displayY;
         }

         if (this.targetDisplayX < minDisplayColumn) {
            this.targetDisplayX = minDisplayColumn;
         }

         if (this.targetDisplayY < minDisplayRow) {
            this.targetDisplayY = minDisplayRow;
         }

         if (this.targetDisplayX >= maxDisplayColumn) {
            this.targetDisplayX = maxDisplayColumn - 1;
         }

         if (this.targetDisplayY >= maxDisplayRow) {
            this.targetDisplayY = maxDisplayRow - 1;
         }

         this.drawDefaultBackground();
         this.drawAchievementScreen(mouseX, mouseY, partialTicks);
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         this.drawTitle();
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
      }
   }

   protected void drawTitle() {
      int i = (this.width - this.guiWidth) / 2;
      int i2 = (this.height - this.guiHeight) / 2;
      this.fontRendererObj.getInt(I18n.format("gui.achievements"), i + 15, i2 + 5, 4210752);
   }

   @Override
   public void initGui() {
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.REQUEST_STATS));
      this.buttonList.clear();
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done")));
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void doneLoading() {
      if (this.loadingAchievements) {
         this.loadingAchievements = false;
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (!this.loadingAchievements && button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.loadingAchievements;
   }

   private TextureAtlasSprite getBlockTexture(Block blockIn) {
      return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockIn.getDefaultState());
   }

   @Override
   public void updateScreen() {
      if (!this.loadingAchievements) {
         this.prevDisplayX = this.displayX;
         this.prevDisplayY = this.displayY;
         double d = this.targetDisplayX - this.displayX;
         double d2 = this.targetDisplayY - this.displayY;
         if (d * d + d2 * d2 < 4.0) {
            this.displayX += d;
            this.displayY += d2;
         } else {
            this.displayX += d * 0.85;
            this.displayY += d2 * 0.85;
         }
      }
   }

   public GuiAchievements(GuiScreen parentScreenIn, StatFileWriter statFileWriterIn) {
      this.parentScreen = parentScreenIn;
      this.statFileWriter = statFileWriterIn;
      this.prevDisplayX = this.displayX = this.targetDisplayX = AchievementList.openInventory.displayColumn * 24 - 70 - 12;
      this.prevDisplayY = this.displayY = this.targetDisplayY = AchievementList.openInventory.displayRow * 24 - 70;
   }

   protected void drawAchievementScreen(int mouseX, int mouseY, float partialTicks) {
      int i = MathHelper.floor_double(this.prevDisplayX + (this.displayX - this.prevDisplayX) * partialTicks);
      int i2 = MathHelper.floor_double(this.prevDisplayY + (this.displayY - this.prevDisplayY) * partialTicks);
      if (i < minDisplayColumn) {
         i = minDisplayColumn;
      }

      if (i2 < minDisplayRow) {
         i2 = minDisplayRow;
      }

      if (i >= maxDisplayColumn) {
         i = maxDisplayColumn - 1;
      }

      if (i2 >= maxDisplayRow) {
         i2 = maxDisplayRow - 1;
      }

      int i3 = (this.width - this.guiWidth) / 2;
      int i4 = (this.height - this.guiHeight) / 2;
      int i5 = i3 + 16;
      int i6 = i4 + 17;
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(518);
      GlStateManager.pushMatrix();
      GlStateManager.translate(i5, i6, -200.0F);
      GlStateManager.scale(1.0F / this.scale, 1.0F / this.scale, 0.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      int i7 = i + 288 >> 4;
      int i8 = i2 + 288 >> 4;
      int i9 = (i + 288) % 16;
      int i10 = (i2 + 288) % 16;
      Random random = new Random();
      float f = 16.0F / this.scale;
      float f2 = 16.0F / this.scale;

      for (int i11 = 0; i11 * f - i10 < 155.0F; i11++) {
         float f3 = 0.6F - (i8 + i11) / 25.0F * 0.3F;
         GlStateManager.color(f3, f3, f3, 1.0F);

         for (int i12 = 0; i12 * f2 - i9 < 224.0F; i12++) {
            random.setSeed(this.mc.getSession().getPlayerID().hashCode() + i7 + i12 + (i8 + i11) * 16);
            int i13 = random.nextInt(1 + i8 + i11) + (i8 + i11) / 2;
            TextureAtlasSprite blockTexture = this.getBlockTexture(Blocks.sand);
            if (i13 > 37 || i8 + i11 == 35) {
               Block bedrock2 = Blocks.bedrock;
               blockTexture = this.getBlockTexture(bedrock2);
            } else if (i13 == 22) {
               if (random.nextInt(2) == 0) {
                  blockTexture = this.getBlockTexture(Blocks.diamond_ore);
               } else {
                  blockTexture = this.getBlockTexture(Blocks.redstone_ore);
               }
            } else if (i13 == 10) {
               blockTexture = this.getBlockTexture(Blocks.iron_ore);
            } else if (i13 == 8) {
               blockTexture = this.getBlockTexture(Blocks.coal_ore);
            } else if (i13 > 4) {
               blockTexture = this.getBlockTexture(Blocks.stone);
            } else if (i13 > 0) {
               blockTexture = this.getBlockTexture(Blocks.dirt);
            }

            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.handleInt4(i12 * 16 - i9, i11 * 16 - i10, blockTexture);
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);

      for (int i14 = 0; i14 < AchievementList.achievementList.size(); i14++) {
         Achievement achievement2 = AchievementList.achievementList.get(i14);
         if (achievement2.parentAchievement != null) {
            int i15 = achievement2.displayColumn * 24 - i + 11;
            int i16 = achievement2.displayRow * 24 - i2 + 11;
            int i17 = achievement2.parentAchievement.displayColumn * 24 - i + 11;
            int i18 = achievement2.parentAchievement.displayRow * 24 - i2 + 11;
            boolean achievementUnlocked = this.statFileWriter.hasAchievementUnlocked(achievement2);
            boolean unlockAchievement = this.statFileWriter.canUnlockAchievement(achievement2);
            int distanceToUnlock = this.statFileWriter.getDistanceToUnlock(achievement2);
            if (distanceToUnlock <= 4) {
               int i19 = -16777216;
               if (achievementUnlocked) {
                  i19 = -6250336;
               } else if (unlockAchievement) {
                  i19 = -16711936;
               }

               this.drawHorizontalLine(i15, i17, i16, i19);
               this.drawVerticalLine(i17, i16, i18, i19);
               if (i15 > i17) {
                  this.drawTexturedModalRect(i15 - 11 - 7, i16 - 5, 114, 234, 7, 11);
               } else if (i15 < i17) {
                  this.drawTexturedModalRect(i15 + 11, i16 - 5, 107, 234, 7, 11);
               } else if (i16 > i18) {
                  this.drawTexturedModalRect(i15 - 5, i16 - 11 - 7, 96, 234, 11, 7);
               } else if (i16 < i18) {
                  this.drawTexturedModalRect(i15 - 5, i16 + 11, 96, 241, 11, 7);
               }
            }
         }
      }

      Achievement achievement3 = null;
      float f4 = (mouseX - i5) * this.scale;
      float f5 = (mouseY - i6) * this.scale;
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();

      for (int i20 = 0; i20 < AchievementList.achievementList.size(); i20++) {
         Achievement achievement4 = AchievementList.achievementList.get(i20);
         int i21 = achievement4.displayColumn * 24 - i;
         int i22 = achievement4.displayRow * 24 - i2;
         if (i21 >= -24 && i22 >= -24 && i21 <= 224.0F * this.scale && i22 <= 155.0F * this.scale) {
            int distanceToUnlock2 = this.statFileWriter.getDistanceToUnlock(achievement4);
            if (this.statFileWriter.hasAchievementUnlocked(achievement4)) {
               float f6 = 0.75F;
               GlStateManager.color(f6, f6, f6, 1.0F);
            } else if (this.statFileWriter.canUnlockAchievement(achievement4)) {
               float f7 = 1.0F;
               GlStateManager.color(f7, f7, f7, 1.0F);
            } else if (distanceToUnlock2 < 3) {
               float f8 = 0.3F;
               GlStateManager.color(f8, f8, f8, 1.0F);
            } else if (distanceToUnlock2 == 3) {
               float f9 = 0.2F;
               GlStateManager.color(f9, f9, f9, 1.0F);
            } else {
               if (distanceToUnlock2 != 4) {
                  continue;
               }

               float f10 = 0.1F;
               GlStateManager.color(f10, f10, f10, 1.0F);
            }

            this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
            if (achievement4.getSpecial()) {
               this.drawTexturedModalRect(i21 - 2, i22 - 2, 26, 202, 26, 26);
            } else {
               this.drawTexturedModalRect(i21 - 2, i22 - 2, 0, 202, 26, 26);
            }

            if (!this.statFileWriter.canUnlockAchievement(achievement4)) {
               float f11 = 0.1F;
               GlStateManager.color(f11, f11, f11, 1.0F);
               this.itemRender.isNotRenderingEffectsInGUI(false);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            this.itemRender.renderItemAndEffectIntoGUI(achievement4.theItemStack, i21 + 3, i22 + 3);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableLighting();
            if (!this.statFileWriter.canUnlockAchievement(achievement4)) {
               this.itemRender.isNotRenderingEffectsInGUI(true);
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (f4 >= i21 && f4 <= i21 + 22 && f5 >= i22 && f5 <= i22 + 22) {
               achievement3 = achievement4;
            }
         }
      }

      GlStateManager.disableDepth();
      GlStateManager.enableBlend();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ACHIEVEMENT_BACKGROUND);
      this.drawTexturedModalRect(i3, i4, 0, 0, this.guiWidth, this.guiHeight);
      this.zLevel = 0.0F;
      GlStateManager.depthFunc(515);
      GlStateManager.disableDepth();
      GlStateManager.enableTexture2D();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (achievement3 != null) {
         String unformattedText = achievement3.getStatName().getUnformattedText();
         String description = achievement3.getDescription();
         int i23 = mouseX + 12;
         int i24 = mouseY - 4;
         int distanceToUnlock3 = this.statFileWriter.getDistanceToUnlock(achievement3);
         if (this.statFileWriter.canUnlockAchievement(achievement3)) {
            int i25 = Math.max(this.fontRendererObj.getInt3(unformattedText), 120);
            int i26 = this.fontRendererObj.splitStringWidth(description, i25);
            if (this.statFileWriter.hasAchievementUnlocked(achievement3)) {
               i26 += 12;
            }

            this.drawGradientRect(i23 - 3, i24 - 3, i23 + i25 + 3, i24 + i26 + 3 + 12, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(description, i23, i24 + 12, i25, -6250336);
            if (this.statFileWriter.hasAchievementUnlocked(achievement3)) {
               this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken"), i23, i24 + i26 + 4, -7302913);
            }
         } else if (distanceToUnlock3 == 3) {
            unformattedText = I18n.format("achievement.unknown");
            int i27 = Math.max(this.fontRendererObj.getInt3(unformattedText), 120);
            String unformattedText2 = new ChatComponentTranslation("achievement.requires", achievement3.parentAchievement.getStatName()).getUnformattedText();
            int i28 = this.fontRendererObj.splitStringWidth(unformattedText2, i27);
            this.drawGradientRect(i23 - 3, i24 - 3, i23 + i27 + 3, i24 + i28 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(unformattedText2, i23, i24 + 12, i27, -9416624);
         } else if (distanceToUnlock3 < 3) {
            int i29 = Math.max(this.fontRendererObj.getInt3(unformattedText), 120);
            String unformattedText3 = new ChatComponentTranslation("achievement.requires", achievement3.parentAchievement.getStatName()).getUnformattedText();
            int i30 = this.fontRendererObj.splitStringWidth(unformattedText3, i29);
            this.drawGradientRect(i23 - 3, i24 - 3, i23 + i29 + 3, i24 + i30 + 12 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(unformattedText3, i23, i24 + 12, i29, -9416624);
         } else {
            unformattedText = null;
         }

         if (unformattedText != null) {
            this.fontRendererObj
               .drawStringWithShadow(
                  unformattedText,
                  i23,
                  i24,
                  this.statFileWriter.canUnlockAchievement(achievement3) ? (achievement3.getSpecial() ? -128 : -1) : (achievement3.getSpecial() ? -8355776 : -8355712)
               );
         }
      }

      GlStateManager.enableDepth();
      GlStateManager.enableLighting();
      RenderHelper.disableStandardItemLighting();
   }
}
