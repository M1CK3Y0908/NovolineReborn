package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreDummyCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import net.optifine.CustomColors;
import wtf.novoline.Novoline;
import wtf.novoline.event.RenderGameOverlayEvent;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.hud.HudRenderer;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.utils.NameUtils;
import wtf.novoline.utils.render.RenderUtils;
import java.util.Collection;

public class GuiIngame extends Gui {
   private final Random rand = new Random();
   private int remainingHighlightTicks;
   private String recordPlaying = "";
   private int recordPlayingUpFor;
   private int titlesTimer;
   private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
   public float prevVignetteBrightness = 1.0F;
   private String displayedTitle = "";
   private String displayedSubTitle = "";
   private int titleFadeIn;
   private int playerHealth = 0;
   private int lastPlayerHealth = 0;
   private ItemStack highlightingItemStack;
   private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
   private boolean recordIsPlaying;
   private int titleFadeOut;
   private long lastSystemTime = 0L;
   private long healthUpdateCounter = 0L;
   private int updateCounter;
   private final Minecraft mc;
   private int titleDisplayTime;
   private final RenderItem itemRenderer;
   private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final GuiOverlayDebug overlayDebug;
   private final GuiSpectator spectatorGui;
   private final GuiNewChat persistantChatGUI;
   private final GuiStreamIndicator guiStreamIndicator;
   private final GuiPlayerTabOverlay overlayPlayerList;

   public void displayTitle(String title, String subTitle, int timeFadeIn, int displayTime, int timeFadeOut) {
      if (title == null && subTitle == null && timeFadeIn < 0 && displayTime < 0 && timeFadeOut < 0) {
         this.displayedTitle = "";
         this.displayedSubTitle = "";
         this.titlesTimer = 0;
      } else if (title != null) {
         this.displayedTitle = title;
         this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
      } else if (subTitle != null) {
         this.displayedSubTitle = subTitle;
      } else {
         if (timeFadeIn >= 0) {
            this.titleFadeIn = timeFadeIn;
         }

         if (displayTime >= 0) {
            this.titleDisplayTime = displayTime;
         }

         if (timeFadeOut >= 0) {
            this.titleFadeOut = timeFadeOut;
         }

         if (this.titlesTimer > 0) {
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
         }
      }
   }

   private void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player) {
      ItemStack itemStack2 = player.inventory.mainInventory[index];
      if (itemStack2 != null) {
         float f = itemStack2.animationsToGo - partialTicks;
         if (f > 0.0F) {
            GlStateManager.pushMatrix();
            float f2 = 1.0F + f / 5.0F;
            GlStateManager.translate(xPos + 8, yPos + 12, 0.0F);
            GlStateManager.scale(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translate(-(xPos + 8), -(yPos + 12), 0.0F);
         }

         this.itemRenderer.renderItemAndEffectIntoGUI(itemStack2, xPos, yPos);
         if (f > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.handleFontRenderer(this.mc.fontRendererObj, itemStack2, xPos, yPos);
      }
   }

   public GuiIngame(Minecraft mcIn) {
      this.mc = mcIn;
      this.itemRenderer = mcIn.getRenderItem();
      this.overlayDebug = new GuiOverlayDebug(mcIn);
      this.spectatorGui = new GuiSpectator(mcIn);
      this.persistantChatGUI = new GuiNewChat(mcIn);
      this.guiStreamIndicator = new GuiStreamIndicator(mcIn);
      this.overlayPlayerList = new GuiPlayerTabOverlay(mcIn, this);
      this.setDefaultTitlesTimes();
   }

   public void handleScaledResolution2(ScaledResolution scaledResolution) {
      this.guiStreamIndicator.handleInt(scaledResolution.getScaledWidth() - 10);
   }

   public void renderExpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("expBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      if (this.mc.thePlayer.xpBarCap() > 0) {
         int i = (int)(this.mc.thePlayer.experience * 183);
         int i2 = scaledRes.getScaledHeight() - 32 + 3;
         this.drawTexturedModalRect(x, i2, 0, 64, 182, 5);
         if (i > 0) {
            this.drawTexturedModalRect(x, i2, 0, 69, i, 5);
         }
      }

      this.mc.mcProfiler.endSection();
      if (this.mc.thePlayer.experienceLevel > 0) {
         this.mc.mcProfiler.startSection("expLevel");
         int i3 = 8453920;
         if (Config.isCustomColors()) {
            i3 = CustomColors.getInt2();
         }

         String text = this.mc.thePlayer.experienceLevel + "";
         int i4 = (scaledRes.getScaledWidth() - this.getFontRenderer().getInt3(text)) / 2;
         int i5 = scaledRes.getScaledHeight() - 31 - 4;
         this.getFontRenderer().getInt(text, i4 + 1, i5, 0);
         this.getFontRenderer().getInt(text, i4 - 1, i5, 0);
         this.getFontRenderer().getInt(text, i4, i5 + 1, 0);
         this.getFontRenderer().getInt(text, i4, i5 - 1, 0);
         this.getFontRenderer().getInt(text, i4, i5, i3);
         this.mc.mcProfiler.endSection();
      }
   }

   public void handleScaledResolution(ScaledResolution scaledResolution) {
      this.mc.mcProfiler.startSection("demo");
      String text;
      if (this.mc.theWorld.getTotalWorldTime() >= 120500L) {
         text = I18n.format("demo.demoExpired");
      } else {
         text = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime())));
      }

      int intValue = this.getFontRenderer().getInt3(text);
      this.getFontRenderer().drawStringWithShadow(text, scaledResolution.getScaledWidth() - intValue - 10, 5.0F, 16777215);
      this.mc.mcProfiler.endSection();
   }

   private Score getScore(Scoreboard scoreboard2, String text) {
      return new Score(scoreboard2, new ScoreObjective(scoreboard2, this.mc.thePlayer.getName(), new ScoreDummyCriteria("dummy")), text);
   }

   public void setRecordPlaying(String message, boolean isPlaying) {
      this.recordPlaying = message;
      this.recordPlayingUpFor = 60;
      this.recordIsPlaying = isPlaying;
   }

   public void updateTick() {
      if (this.recordPlayingUpFor > 0) {
         this.recordPlayingUpFor--;
      }

      if (this.titlesTimer > 0) {
         this.titlesTimer--;
         if (this.titlesTimer <= 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
         }
      }

      this.updateCounter++;
      this.guiStreamIndicator.update();
      if (this.mc.thePlayer != null) {
         ItemStack itemStack = this.mc.thePlayer.inventory.getCurrentItem();
         if (itemStack == null) {
            this.remainingHighlightTicks = 0;
         } else if (this.highlightingItemStack != null
            && itemStack.getItem() == this.highlightingItemStack.getItem()
            && ItemStack.areItemStackTagsEqual(itemStack, this.highlightingItemStack)
            && (itemStack.isItemStackDamageable() || itemStack.getMetadata() == this.highlightingItemStack.getMetadata())) {
            if (this.remainingHighlightTicks > 0) {
               this.remainingHighlightTicks--;
            }
         } else {
            this.remainingHighlightTicks = 40;
         }

         this.highlightingItemStack = itemStack;
      }
   }

   public void resetPlayersOverlayFooterHeader() {
      this.overlayPlayerList.resetFooterHeader();
   }

   public FontRenderer getFontRenderer() {
      return this.mc.fontRendererObj;
   }

   public void handleIChatComponent(IChatComponent iChatComponent) {
      this.setRecordPlaying(iChatComponent.getUnformattedText(), false);
   }

   public GuiSpectator getSpectatorGui() {
      return this.spectatorGui;
   }

   protected void renderTooltip(ScaledResolution sr, float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(widgetsTexPath);
         EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = sr.getScaledWidth() / 2;
         float zLevel2 = this.zLevel;
         this.zLevel = -90.0F;
         this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
         this.drawTexturedModalRect(i - 91 - 1 + entityPlayer.inventory.currentItem * 20, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
         this.zLevel = zLevel2;
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         RenderHelper.enableGUIStandardItemLighting();

         for (int innerIndex = 0; innerIndex < 9; innerIndex++) {
            int i2 = sr.getScaledWidth() / 2 - 90 + innerIndex * 20 + 2;
            int i3 = sr.getScaledHeight() - 16 - 3;
            this.renderHotbarItem(innerIndex, i2, i3, partialTicks, entityPlayer);
         }

         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }
   }

   private String getString(String text) {
      if (text.contains(".")) {
         return "www.novoline.wtf";
      }

      NameProtect nameProtect2 = ModuleManager.nameProtect;
      return nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(3) ? nameProtect2.protectText(text) : text;
   }

   private void renderScoreboard(ScoreObjective objective, ScaledResolution scaledRes) {
      if (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.scoreboard.isEnabled()) {
         Scoreboard scoreboard2 = objective.getScoreboard();
         ArrayList arrayList = (ArrayList) (Collection) (scoreboard2.getSortedScores(objective));
         ArrayList arrayList2 = Lists.newArrayList(Iterables.filter(arrayList, new Predicate<Score>() {
            public boolean apply(Score arg1) {
               return arg1.getPlayerName() != null && !arg1.getPlayerName().startsWith("#");
            }
         }));
         if (!arrayList2.isEmpty()) {
            Score score = (Score)arrayList2.get(0);
            ScorePlayerTeam playersTeam = scoreboard2.getPlayersTeam(score.getPlayerName());
            String text = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName());
            if (!text.contains(".")) {
               arrayList2.add(0, this.getScore(scoreboard2, "www.novoline.wtf"));
               arrayList2.add(1, this.getScore(scoreboard2, "§a§l"));
            }
         }

         if (arrayList2.size() > 17) {
            arrayList = Lists.newArrayList(Iterables.skip(arrayList2, arrayList.size() - 15));
         } else {
            arrayList = arrayList2;
         }

         int intValue = this.getFontRenderer().getInt3(objective.getDisplayName());

         for (Score score2 : (Iterable<Score>) arrayList) {
            String text2 = ScorePlayerTeam.formatPlayerName(scoreboard2.getPlayersTeam(score2.getPlayerName()), this.getString(score2.getPlayerName()))
               + ": "
               + EnumChatFormatting.RED
               + score2.getScorePoints();
            intValue = Math.max(intValue, this.getFontRenderer().getInt3(text2));
         }

         int i = arrayList.size() * this.getFontRenderer().FONT_HEIGHT;
         int i2 = scaledRes.getScaledHeight() / 2 + i / 3;
         int i3 = scaledRes.getScaledWidth() - intValue - 3;
         int i4 = 0;

         for (Score score3 : (Iterable<Score>) arrayList) {
            i4++;
            String text3 = ScorePlayerTeam.formatPlayerName(scoreboard2.getPlayersTeam(score3.getPlayerName()), score3.getPlayerName());
            if (i4 == 1) {
               text3 = this.getString(text3);
            }

            String text4 = "" + EnumChatFormatting.RED + score3.getScorePoints();
            int i5 = i2 - i4 * this.getFontRenderer().FONT_HEIGHT;
            int i6 = scaledRes.getScaledWidth() - 3 + 2;
            handleInt(i3 - 2, i5, i6, i5 + this.getFontRenderer().FONT_HEIGHT, 1342177280);
            if (i4 == 1) {
               this.mc
                  .fontRendererObj
                  .drawStringWithShadow(
                     NameUtils.stripFormatting(text3),
                     i3,
                     i5,
                     RenderUtils.pulseBrightness(ModuleManager.watermark.main.getColor(), 0.7F, 1.0F, 1, 1, 3000L)
                  );
            } else {
               this.getFontRenderer().getInt(text3, i3, i5, -1);
            }

            this.getFontRenderer().getInt(text4, i6 - this.getFontRenderer().getInt3(text4), i5, -1);
            if (i4 == arrayList.size()) {
               String displayName = objective.getDisplayName();
               handleInt(i3 - 2, i5 - this.getFontRenderer().FONT_HEIGHT - 1, i6, i5 - 1, 1610612736);
               handleInt(i3 - 2, i5 - 1, i6, i5, 1342177280);
               this.getFontRenderer()
                  .getInt(displayName, i3 + intValue / 2 - this.getFontRenderer().getInt3(displayName) / 2, i5 - this.getFontRenderer().FONT_HEIGHT, -1);
            }
         }
      }
   }

   public GuiNewChat getChatGUI() {
      return this.persistantChatGUI;
   }

   private void renderPortal(float timeInPortal, ScaledResolution scaledRes) {
      if (timeInPortal < 1.0F) {
         timeInPortal *= timeInPortal;
         timeInPortal *= timeInPortal;
         timeInPortal = timeInPortal * 0.8F + 0.2F;
      }

      GlStateManager.disableAlpha();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      RenderUtils.applyBlendFunc();
      GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      TextureAtlasSprite textureAtlasSprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
      float minU = textureAtlasSprite.getMinU();
      float minV = textureAtlasSprite.getMinV();
      float maxU = textureAtlasSprite.getMaxU();
      float maxV = textureAtlasSprite.getMaxV();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(minU, maxV).endVertex();
      worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(maxU, maxV).endVertex();
      worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(maxU, minV).endVertex();
      worldRenderer.pos(0.0, 0.0, -90.0).tex(minU, minV).endVertex();
      tessellator.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public GuiPlayerTabOverlay getTabList() {
      return this.overlayPlayerList;
   }

   public void renderSelectedItem(ScaledResolution scaledRes) {
      this.mc.mcProfiler.startSection("selectedItemName");
      if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
         String displayName = this.highlightingItemStack.getDisplayName();
         if (this.highlightingItemStack.hasDisplayName()) {
            displayName = EnumChatFormatting.ITALIC + displayName;
         }

         int i = (scaledRes.getScaledWidth() - this.getFontRenderer().getInt3((String) displayName)) / 2;
         int i2 = scaledRes.getScaledHeight() - 59;
         if (!this.mc.playerController.shouldDrawHUD()) {
            i2 += 14;
         }

         int i3 = (int)(this.remainingHighlightTicks * 256.0F / 10.0F);
         if (i3 > 255) {
            i3 = 255;
         }

         if (i3 > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            this.getFontRenderer().drawStringWithShadow((String) displayName, i, i2, 16777215 + (i3 << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.mc.mcProfiler.endSection();
   }

   private void renderVignette(float lightLevel, ScaledResolution scaledRes) {
      if (!Config.isVignetteEnabled()) {
         GlStateManager.enableDepth();
         RenderUtils.applyBlendFunc();
      } else {
         lightLevel = 1.0F - lightLevel;
         lightLevel = MathHelper.clamp_float(lightLevel, 0.0F, 1.0F);
         WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
         float f = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
         double d = Math.min(worldBorder.getResizeSpeed() * worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
         double d2 = Math.max(worldBorder.getWarningDistance(), d);
         if (f < d2) {
            f = 1.0F - (float)(f / d2);
         } else {
            f = 0.0F;
         }

         this.prevVignetteBrightness = (float)(this.prevVignetteBrightness + (lightLevel - this.prevVignetteBrightness) * 0.01);
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
         if (f > 0.0F) {
            GlStateManager.color(0.0F, f, f, 1.0F);
         } else {
            GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
         }

         this.mc.getTextureManager().bindTexture(vignetteTexPath);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance.getWorldRenderer();
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer2.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         worldRenderer2.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         worldRenderer2.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         worldRenderer2.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         instance.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderUtils.applyBlendFunc();
      }
   }

   public void setRecordPlayingMessage(String recordName) {
      this.setRecordPlaying(I18n.format("record.nowPlaying", recordName), true);
   }

   private void renderPlayerStats(ScaledResolution scaledRes) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer entityPlayer2 = (EntityPlayer)this.mc.getRenderViewEntity();
         int i = MathHelper.ceiling_float_int(entityPlayer2.getHealth());
         boolean flag = this.healthUpdateCounter > this.updateCounter && (this.healthUpdateCounter - this.updateCounter) / 3L % 2L == 1L;
         if (i < this.playerHealth && entityPlayer2.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 20;
         } else if (i > this.playerHealth && entityPlayer2.hurtResistantTime > 0) {
            this.lastSystemTime = Minecraft.getSystemTime();
            this.healthUpdateCounter = this.updateCounter + 10;
         }

         if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
            this.playerHealth = i;
            this.lastPlayerHealth = i;
            this.lastSystemTime = Minecraft.getSystemTime();
         }

         this.playerHealth = i;
         int lastPlayerHealth2 = this.lastPlayerHealth;
         this.rand.setSeed(this.updateCounter * 312871);
         FoodStats foodStats = entityPlayer2.getFoodStats();
         int foodLevel = foodStats.getFoodLevel();
         foodStats.getPrevFoodLevel();
         IAttributeInstance entityAttribute = entityPlayer2.getEntityAttribute(SharedMonsterAttributes.maxHealth);
         int i2 = scaledRes.getScaledWidth() / 2 - 91;
         int i3 = scaledRes.getScaledWidth() / 2 + 91;
         int i4 = scaledRes.getScaledHeight() - 39;
         float f = (float)entityAttribute.getAttributeValue();
         float absorptionAmount = entityPlayer2.getAbsorptionAmount();
         int i5 = MathHelper.ceiling_float_int((f + absorptionAmount) / 2.0F / 10.0F);
         int i6 = Math.max(10 - (i5 - 2), 3);
         int i7 = i4 - (i5 - 1) * i6 - 10;
         float f2 = absorptionAmount;
         int totalArmorValue = entityPlayer2.getTotalArmorValue();
         int i8 = -1;
         if (entityPlayer2.isPotionActive(Potion.regeneration)) {
            i8 = this.updateCounter % MathHelper.ceiling_float_int(f + 5.0F);
         }

         this.mc.mcProfiler.startSection("armor");

         for (int i9 = 0; i9 < 10; i9++) {
            if (totalArmorValue > 0) {
               int i10 = i2 + i9 * 8;
               if (i9 * 2 + 1 < totalArmorValue) {
                  this.drawTexturedModalRect(i10, i7, 34, 9, 9, 9);
               }

               if (i9 * 2 + 1 == totalArmorValue) {
                  this.drawTexturedModalRect(i10, i7, 25, 9, 9, 9);
               }

               if (i9 * 2 + 1 > totalArmorValue) {
                  this.drawTexturedModalRect(i10, i7, 16, 9, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endStartSection("health");

         for (int i11 = MathHelper.ceiling_float_int((f + absorptionAmount) / 2.0F) - 1; i11 >= 0; i11--) {
            int i12 = 16;
            if (entityPlayer2.isPotionActive(Potion.poison)) {
               i12 += 36;
            } else if (entityPlayer2.isPotionActive(Potion.wither)) {
               i12 += 72;
            }

            byte b = 0;
            if (flag) {
               b = 1;
            }

            int i13 = MathHelper.ceiling_float_int((i11 + 1) / 10.0F) - 1;
            int i14 = i2 + i11 % 10 * 8;
            int i15 = i4 - i13 * i6;
            if (i <= 4) {
               i15 += this.rand.nextInt(2);
            }

            if (i11 == i8) {
               i15 -= 2;
            }

            byte b2 = 0;
            if (entityPlayer2.worldObj.getWorldInfo().isHardcoreModeEnabled()) {
               b2 = 5;
            }

            this.drawTexturedModalRect(i14, i15, 16 + b * 9, 9 * b2, 9, 9);
            if (flag) {
               if (i11 * 2 + 1 < lastPlayerHealth2) {
                  this.drawTexturedModalRect(i14, i15, i12 + 54, 9 * b2, 9, 9);
               }

               if (i11 * 2 + 1 == lastPlayerHealth2) {
                  this.drawTexturedModalRect(i14, i15, i12 + 63, 9 * b2, 9, 9);
               }
            }

            if (f2 <= 0.0F) {
               if (i11 * 2 + 1 < i) {
                  this.drawTexturedModalRect(i14, i15, i12 + 36, 9 * b2, 9, 9);
               }

               if (i11 * 2 + 1 == i) {
                  this.drawTexturedModalRect(i14, i15, i12 + 45, 9 * b2, 9, 9);
               }
            } else {
               if (f2 == absorptionAmount && absorptionAmount % 2.0F == 1.0F) {
                  this.drawTexturedModalRect(i14, i15, i12 + 153, 9 * b2, 9, 9);
               } else {
                  this.drawTexturedModalRect(i14, i15, i12 + 144, 9 * b2, 9, 9);
               }

               f2 -= 2.0F;
            }
         }

         Entity ridingEntity2 = entityPlayer2.ridingEntity;
         if (ridingEntity2 == null) {
            this.mc.mcProfiler.endStartSection("food");

            for (int i16 = 0; i16 < 10; i16++) {
               int i17 = i4;
               int i18 = 16;
               byte b3 = 0;
               if (entityPlayer2.isPotionActive(Potion.hunger)) {
                  i18 += 36;
                  b3 = 13;
               }

               if (entityPlayer2.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (foodLevel * 3 + 1) == 0) {
                  i17 = i4 + (this.rand.nextInt(3) - 1);
               }

               int i19 = i3 - i16 * 8 - 9;
               this.drawTexturedModalRect(i19, i17, 16 + b3 * 9, 27, 9, 9);
               if (i16 * 2 + 1 < foodLevel) {
                  this.drawTexturedModalRect(i19, i17, i18 + 36, 27, 9, 9);
               }

               if (i16 * 2 + 1 == foodLevel) {
                  this.drawTexturedModalRect(i19, i17, i18 + 45, 27, 9, 9);
               }
            }
         } else if (ridingEntity2 instanceof EntityLivingBase) {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase entityLivingBase = (EntityLivingBase)ridingEntity2;
            int i20 = (int)Math.ceil(entityLivingBase.getHealth());
            float maxHealth2 = entityLivingBase.getMaxHealth();
            int i21 = (int)(maxHealth2 + 0.5F) / 2;
            if (i21 > 30) {
               i21 = 30;
            }

            int i22 = i4;

            for (int i23 = 0; i21 > 0; i23 += 20) {
               int i24 = Math.min(i21, 10);
               i21 -= i24;

               for (int i25 = 0; i25 < i24; i25++) {
                  int i26 = i3 - i25 * 8 - 9;
                  this.drawTexturedModalRect(i26, i22, 52, 9, 9, 9);
                  if (i25 * 2 + 1 + i23 < i20) {
                     this.drawTexturedModalRect(i26, i22, 88, 9, 9, 9);
                  }

                  if (i25 * 2 + 1 + i23 == i20) {
                     this.drawTexturedModalRect(i26, i22, 97, 9, 9, 9);
                  }
               }

               i22 -= 10;
            }
         }

         this.mc.mcProfiler.endStartSection("air");
         if (entityPlayer2.isInsideOfMaterial(Material.water)) {
            int air2 = this.mc.thePlayer.getAir();
            int i27 = MathHelper.ceiling_double_int((air2 - 2) * 10.0 / 300.0);
            int i28 = MathHelper.ceiling_double_int(air2 * 10.0 / 300.0) - i27;

            for (int i29 = 0; i29 < i27 + i28; i29++) {
               if (i29 < i27) {
                  this.drawTexturedModalRect(i3 - i29 * 8 - 9, i7, 16, 18, 9, 9);
               } else {
                  this.drawTexturedModalRect(i3 - i29 * 8 - 9, i7, 25, 18, 9, 9);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public int getUpdateCounter() {
      return this.updateCounter;
   }

   private void renderBossHealth() {
      if (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.bossBar.isEnabled()) {
         if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            BossStatus.statusBarTime--;
            int scaledWidth = new ScaledResolution(this.mc).getScaledWidth();
            int i = scaledWidth / 2 - 91;
            int i2 = (int)(BossStatus.healthScale * 183);
            this.drawTexturedModalRect(i, 12, 0, 74, 182, 5);
            this.drawTexturedModalRect(i, 12, 0, 74, 182, 5);
            if (i2 > 0) {
               this.drawTexturedModalRect(i, 12, 0, 79, i2, 5);
            }

            String bossName2 = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(bossName2, scaledWidth / 2 - this.getFontRenderer().getInt3(bossName2) / 2, 2, 16777215);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
         }
      }
   }

   private void renderPumpkinOverlay(ScaledResolution scaledRes) {
      if (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.noRender.isEnabled()) {
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         RenderUtils.applyBlendFunc();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableAlpha();
         this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(0.0, scaledRes.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(scaledRes.getScaledWidth(), scaledRes.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(scaledRes.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.enableAlpha();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   protected boolean showCrosshair() {
      if (ModuleManager.crosshair.isEnabled()) {
         return false;
      }

      if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
         return false;
      }

      if (this.mc.playerController.isSpectator()) {
         if (this.mc.pointedEntity != null) {
            return true;
         }

         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            if (this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void setDefaultTitlesTimes() {
      this.titleFadeIn = 10;
      this.titleDisplayTime = 70;
      this.titleFadeOut = 20;
   }

   public void renderGameOverlay(float partialTicks) {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int scaledWidth = scaledResolution.getScaledWidth();
      int scaledHeight = scaledResolution.getScaledHeight();
      this.mc.entityRenderer.setupOverlayRendering();
      GlStateManager.enableBlend();
      if (Config.isVignetteEnabled()) {
         this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledResolution);
      } else {
         RenderUtils.applyBlendFunc();
      }

      GlStateManager.pushMatrix();
      ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
      float scaleFactor = scaledResolution2.getScaleFactor();
      GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
      double d = scaledResolution2.getScaledWidth_double();
      double d2 = scaledResolution2.getScaledHeight_double();
      scaledResolution2.handleDouble2(d / 2.0 * scaleFactor);
      scaledResolution2.handleDouble(d2 / 2.0 * scaleFactor);
      Novoline.getNovoline().getBus().post(new RenderGameOverlayEvent(scaledResolution2, partialTicks));
      GlStateManager.popMatrix();
      ItemStack itemStack2 = this.mc.thePlayer.inventory.armorItemInSlot(3);
      if (this.mc.gameSettings.thirdPersonView == 0 && itemStack2 != null && itemStack2.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         this.renderPumpkinOverlay(scaledResolution);
      }

      if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
         scaleFactor = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
         if (scaleFactor > 0.0F) {
            this.renderPortal(scaleFactor, scaledResolution);
         }
      }

      if (this.mc.playerController.isSpectator()) {
         this.spectatorGui.renderTooltip(scaledResolution, partialTicks);
      } else {
         this.renderTooltip(scaledResolution, partialTicks);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      GlStateManager.enableBlend();
      if (this.showCrosshair()) {
         GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
         GlStateManager.enableAlpha();
         this.drawTexturedModalRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0, 0, 16, 16);
      }

      GlStateManager.enableAlpha();
      RenderUtils.applyBlendFunc();
      this.mc.mcProfiler.startSection("bossHealth");
      this.renderBossHealth();
      this.mc.mcProfiler.endSection();
      if (this.mc.playerController.shouldDrawHUD()) {
         this.renderPlayerStats(scaledResolution);
      }

      GlStateManager.disableBlend();
      if (this.mc.thePlayer.getSleepTimer() > 0) {
         this.mc.mcProfiler.startSection("sleep");
         GlStateManager.disableDepth();
         GlStateManager.disableAlpha();
         int sleepTimer = this.mc.thePlayer.getSleepTimer();
         float f = sleepTimer / 100.0F;
         if (f > 1.0F) {
            f = 1.0F - (sleepTimer - 100) / 10.0F;
         }

         int i = (int)(220.0F * f) << 24 | 1052704;
         handleInt(0, 0, scaledWidth, scaledHeight, i);
         GlStateManager.enableAlpha();
         GlStateManager.enableDepth();
         this.mc.mcProfiler.endSection();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      int i2 = scaledWidth / 2 - 91;
      if (this.mc.thePlayer.isRidingHorse()) {
         this.renderHorseJumpBar(scaledResolution, i2);
      } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
         this.renderExpBar(scaledResolution, i2);
      }

      if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
         this.renderSelectedItem(scaledResolution);
      } else if (this.mc.thePlayer.isSpectator()) {
         this.spectatorGui.renderSelectedItem(scaledResolution);
      }

      if (this.mc.isDemo()) {
         this.handleScaledResolution(scaledResolution);
      }

      if (this.mc.gameSettings.showDebugInfo) {
         this.overlayDebug.renderDebugInfo(scaledResolution);
      }

      if (this.recordPlayingUpFor > 0) {
         this.mc.mcProfiler.startSection("overlayMessage");
         float f2 = this.recordPlayingUpFor - partialTicks;
         int i3 = (int)(f2 * 255.0F / 20.0F);
         if (i3 > 255) {
            i3 = 255;
         }

         if (i3 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(scaledWidth / 2, scaledHeight - 68, 0.0F);
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            int i4 = 16777215;
            if (this.recordIsPlaying) {
               i4 = MathHelper.hsvToRGB(f2 / 50.0F, 0.7F, 0.6F) & 16777215;
            }

            this.getFontRenderer().getInt(this.recordPlaying, -this.getFontRenderer().getInt3(this.recordPlaying) / 2, -4, i4 + (i3 << 24 & 0xFF000000));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      if (this.titlesTimer > 0 && (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.title.isEnabled())) {
         this.mc.mcProfiler.startSection("titleAndSubtitle");
         float f3 = this.titlesTimer - partialTicks;
         int i5 = 255;
         if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
            float f4 = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut - f3;
            i5 = (int)(f4 * 255.0F / this.titleFadeIn);
         }

         if (this.titlesTimer <= this.titleFadeOut) {
            i5 = (int)(f3 * 255.0F / this.titleFadeOut);
         }

         i5 = MathHelper.clamp_int(i5, 0, 255);
         if (i5 > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(scaledWidth / 2, scaledHeight / 2, 0.0F);
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 4.0F);
            int i6 = i5 << 24 & 0xFF000000;
            this.getFontRenderer().drawString(this.displayedTitle, -this.getFontRenderer().getInt3(this.displayedTitle) / 2, -10.0F, 16777215 | i6, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            this.getFontRenderer()
               .drawString(this.displayedSubTitle, -this.getFontRenderer().getInt3(this.displayedSubTitle) / 2, 5.0F, 16777215 | i6, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }

         this.mc.mcProfiler.endSection();
      }

      Scoreboard scoreboard2 = this.mc.theWorld.getScoreboard();
      ScoreObjective scoreObjective = null;
      ScorePlayerTeam playersTeam = scoreboard2.getPlayersTeam(this.mc.thePlayer.getName());
      if (playersTeam != null) {
         int colorIndex = playersTeam.getChatFormat().getColorIndex();
         if (colorIndex >= 0) {
            scoreObjective = scoreboard2.getObjectiveInDisplaySlot(3 + colorIndex);
         }
      }

      ScoreObjective scoreObjective2 = scoreObjective != null ? scoreObjective : scoreboard2.getObjectiveInDisplaySlot(1);
      if (scoreObjective2 != null) {
         this.renderScoreboard(scoreObjective2, scaledResolution);
      }

      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GlStateManager.disableAlpha();
      GlStateManager.pushMatrix();
      this.mc.mcProfiler.startSection("chat");
      this.persistantChatGUI.drawChat(this.updateCounter);
      this.mc.mcProfiler.endSection();
      GlStateManager.popMatrix();
      scoreObjective2 = scoreboard2.getObjectiveInDisplaySlot(0);
      if (!this.mc.gameSettings.keyBindChat.isKeyDown()
         || this.mc.isIntegratedServerRunning() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreObjective2 == null) {
         this.overlayPlayerList.updatePlayerList(false);
      } else {
         this.overlayPlayerList.updatePlayerList(true);
         this.overlayPlayerList.renderPlayerlist(scaledWidth, scoreboard2, scoreObjective2);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlpha();
      if (!(this.mc.currentScreen instanceof GuiChat)) {
         GlStateManager.disableDepth();
         HudRenderer hudRenderer = Novoline.getNovoline().getHudManager().getHudRenderer();
         ScaledResolution scaledResolution3 = new ScaledResolution(this.mc);
         float partialTicks2 = partialTicks;
         hudRenderer.renderElements(scaledResolution3, partialTicks2);
         GlStateManager.enableDepth();
      }
   }

   public void renderHorseJumpBar(ScaledResolution scaledRes, int x) {
      this.mc.mcProfiler.startSection("jumpBar");
      this.mc.getTextureManager().bindTexture(Gui.icons);
      float horseJumpPower = this.mc.thePlayer.getHorseJumpPower();
      int i = (int)(horseJumpPower * 183);
      int i2 = scaledRes.getScaledHeight() - 32 + 3;
      this.drawTexturedModalRect(x, i2, 0, 84, 182, 5);
      if (i > 0) {
         this.drawTexturedModalRect(x, i2, 0, 89, i, 5);
      }

      this.mc.mcProfiler.endSection();
   }
}
