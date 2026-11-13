package net.minecraft.client.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.utils.render.RenderUtils;

public class GuiPlayerTabOverlay extends Gui {
   public static final Ordering<NetworkPlayerInfo> PLAYER_ORDERING = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
   private boolean isBeingRendered;
   private final Minecraft mc;
   private IChatComponent header;
   private long lastTimeOpened;
   private IChatComponent footer;
   private final GuiIngame guiIngame;

   public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
      NetHandlerPlayClient sendQueue2 = this.mc.thePlayer.sendQueue;
      List list = PLAYER_ORDERING.sortedCopy(sendQueue2.getPlayerInfoMap());
      int i = 0;
      int i2 = 0;

      for (NetworkPlayerInfo networkPlayerInfo : (Iterable<NetworkPlayerInfo>) list) {
         int intValue = this.mc.fontRendererObj.getInt3(this.getPlayerName(networkPlayerInfo));
         if (this.checkNetworkPlayerInfo(networkPlayerInfo)) {
            intValue += 16;
         }

         i = Math.max(i, intValue);
         if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            intValue = this.mc
               .fontRendererObj
               .getInt3(" " + scoreboardIn.getValueFromObjective(networkPlayerInfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
            i2 = Math.max(i2, intValue);
         }
      }

      list = list.subList(0, Math.min(list.size(), 80));
      int size2 = list.size();
      int i3 = size2;

      int i4;
      for (i4 = 1; i3 > 20; i3 = (size2 + i4 - 1) / i4) {
         i4++;
      }

      boolean flag = this.mc.isIntegratedServerRunning() || this.mc.getNetHandler().getNetworkManager().getIsencrypted();
      int i5;
      if (scoreObjectiveIn != null) {
         if (scoreObjectiveIn.getRenderType() == net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            i5 = 90;
         } else {
            i5 = i2;
         }
      } else {
         i5 = 0;
      }

      int i6 = Math.min(i4 * ((flag ? 9 : 0) + i + i5 + 13), width - 50) / i4;
      int i7 = width / 2 - (i6 * i4 + (i4 - 1) * 5) / 2;
      int i8 = 10;
      int i9 = i6 * i4 + (i4 - 1) * 5;
      List list2 = null;
      List list3 = null;
      if (this.header != null) {
         list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);

         for (String text : (Iterable<String>) list2) {
            i9 = Math.max(i9, this.mc.fontRendererObj.getInt3(text));
         }
      }

      if (this.footer != null) {
         list3 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);

         for (String text2 : (Iterable<String>) list3) {
            i9 = Math.max(i9, this.mc.fontRendererObj.getInt3(text2));
         }
      }

      if (list2 != null) {
         handleInt(width / 2 - i9 / 2 - 1, 9, width / 2 + i9 / 2 + 1, 10 + list2.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

         for (String text3 : (Iterable<String>) list2) {
            int intValue2 = this.mc.fontRendererObj.getInt3(text3);
            this.mc.fontRendererObj.drawStringWithShadow(text3, width / 2 - intValue2 / 2, i8, -1);
            i8 += this.mc.fontRendererObj.FONT_HEIGHT;
         }

         i8++;
      }

      handleInt(width / 2 - i9 / 2 - 1, i8 - 1, width / 2 + i9 / 2 + 1, i8 + i3 * 9, Integer.MIN_VALUE);

      for (int i10 = 0; i10 < size2; i10++) {
         int i11 = i10 / i3;
         int i12 = i10 % i3;
         int i13 = i7 + i11 * i6 + i11 * 5;
         int i14 = i8 + i12 * 9;
         handleInt(i13, i14, i13 + i6, i14 + 8, 553648127);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         if (i10 < list.size()) {
            NetworkPlayerInfo networkPlayerInfo2 = (NetworkPlayerInfo)list.get(i10);
            String playerName = this.getPlayerName(networkPlayerInfo2);
            GameProfile gameProfile = networkPlayerInfo2.getGameProfile();
            if (flag) {
               EntityPlayer playerEntityByUUID = this.mc.theWorld.getPlayerEntityByUUID(gameProfile.getId());
               boolean flag2 = playerEntityByUUID != null
                  && playerEntityByUUID.isWearing(EnumPlayerModelParts.CAPE)
                  && (gameProfile.getName().equals("Dinnerbone") || gameProfile.getName().equals("Grumm"));
               this.mc.getTextureManager().bindTexture(networkPlayerInfo2.getLocationSkin());
               int i15 = 8 + (flag2 ? 8 : 0);
               int i16 = 8 * (flag2 ? -1 : 1);
               Gui.handleInt3(i13, i14, 8.0F, i15, i16, 8, 8, 64.0F, 64.0F);
               if (playerEntityByUUID != null && playerEntityByUUID.isWearing(EnumPlayerModelParts.HAT)) {
                  int i17 = 8 + (flag2 ? 8 : 0);
                  int i18 = 8 * (flag2 ? -1 : 1);
                  Gui.handleInt3(i13, i14, 40.0F, i17, i18, 8, 8, 64.0F, 64.0F);
               }

               i13 += 9;
            }

            boolean flag3 = this.checkNetworkPlayerInfo(networkPlayerInfo2);
            float f = flag3 ? 9.0F : 0.0F;
            if (flag3) {
               float f2 = 16.0F / this.mc.gameSettings.guiScale;
               RenderUtils.drawColoredTexture(i13, i14, f2, f2, -1, new ResourceLocation("novoline", "images/novoline_16x16.png"));
            }

            if (networkPlayerInfo2.getGameType() == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
               playerName = EnumChatFormatting.ITALIC + playerName;
               this.mc.fontRendererObj.drawStringWithShadow(playerName, i13 + f, i14, -1862270977);
            } else {
               this.mc.fontRendererObj.drawStringWithShadow(playerName, i13 + f, i14, -1);
            }

            if (scoreObjectiveIn != null && networkPlayerInfo2.getGameType() != net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
               int i19 = i13 + i + 1;
               int i20 = i19 + i5;
               if (i20 - i19 > 5) {
                  this.drawScoreboardValues(scoreObjectiveIn, i14, gameProfile.getName(), i19, i20, networkPlayerInfo2);
               }
            }

            this.drawPing(i6, i13 - (flag ? 9 : 0), i14, networkPlayerInfo2);
         }
      }

      if (list3 != null) {
         i8 = i8 + i3 * 9 + 1;
         handleInt(
            width / 2 - i9 / 2 - 1, i8 - 1, width / 2 + i9 / 2 + 1, i8 + list3.size() * this.mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE
         );

         for (String text4 : (Iterable<String>) list3) {
            int intValue3 = this.mc.fontRendererObj.getInt3(text4);
            this.mc.fontRendererObj.drawStringWithShadow(text4, width / 2 - intValue3 / 2, i8, -1);
            i8 += this.mc.fontRendererObj.FONT_HEIGHT;
         }
      }
   }

   public void setHeader(IChatComponent headerIn) {
      this.header = headerIn;
   }

   public void resetFooterHeader() {
      this.header = null;
      this.footer = null;
   }

   public GuiPlayerTabOverlay(Minecraft mcIn, GuiIngame guiIngameIn) {
      this.mc = mcIn;
      this.guiIngame = guiIngameIn;
   }

   private void drawScoreboardValues(ScoreObjective scoreObjective, int i, String text, int i2, int i3, NetworkPlayerInfo networkPlayerInfo) {
      int scorePoints = scoreObjective.getScoreboard().getValueFromObjective(text, scoreObjective).getScorePoints();
      if (scoreObjective.getRenderType() == net.minecraft.scoreboard.IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
         this.mc.getTextureManager().bindTexture(icons);
         if (this.lastTimeOpened == networkPlayerInfo.getRenderVisibilityId()) {
            if (scorePoints < networkPlayerInfo.getLastHealth()) {
               networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
               networkPlayerInfo.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 20);
            } else if (scorePoints > networkPlayerInfo.getLastHealth()) {
               networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
               networkPlayerInfo.setHealthBlinkTime(this.guiIngame.getUpdateCounter() + 10);
            }
         }

         if (Minecraft.getSystemTime() - networkPlayerInfo.getLastHealthTime() > 1000L || this.lastTimeOpened != networkPlayerInfo.getRenderVisibilityId()) {
            networkPlayerInfo.setLastHealth(scorePoints);
            networkPlayerInfo.setDisplayHealth(scorePoints);
            networkPlayerInfo.setLastHealthTime(Minecraft.getSystemTime());
         }

         networkPlayerInfo.setRenderVisibilityId(this.lastTimeOpened);
         networkPlayerInfo.setLastHealth(scorePoints);
         int i4 = MathHelper.ceiling_float_int(Math.max(scorePoints, networkPlayerInfo.getDisplayHealth()) / 2.0F);
         int i5 = Math.max(MathHelper.ceiling_float_int(scorePoints / 2), Math.max(MathHelper.ceiling_float_int(networkPlayerInfo.getDisplayHealth() / 2), 10));
         boolean flag = networkPlayerInfo.getHealthBlinkTime() > this.guiIngame.getUpdateCounter()
            && (networkPlayerInfo.getHealthBlinkTime() - this.guiIngame.getUpdateCounter()) / 3L % 2L == 1L;
         if (i4 > 0) {
            float f = Math.min((float)(i3 - i2 - 4) / i5, 9.0F);
            if (f > 3.0F) {
               for (int i6 = i4; i6 < i5; i6++) {
                  float f2 = i2 + i6 * f;
                  float f3 = i;
                  int i7 = flag ? 25 : 16;
                  float f4 = f3;
                  float f5 = f2;
                  this.drawTexturedModalRect((float)f5, (float)f4, i7, 0, 9, 9);
               }

               for (int i8 = 0; i8 < i4; i8++) {
                  float f6 = i2 + i8 * f;
                  float f7 = i;
                  int i9 = flag ? 25 : 16;
                  float f8 = f7;
                  float f9 = f6;
                  this.drawTexturedModalRect((float)f9, (float)f8, i9, 0, 9, 9);
                  if (flag) {
                     if (i8 * 2 + 1 < networkPlayerInfo.getDisplayHealth()) {
                        this.drawTexturedModalRect(i2 + i8 * f, i, 70, 0, 9, 9);
                     }

                     if (i8 * 2 + 1 == networkPlayerInfo.getDisplayHealth()) {
                        this.drawTexturedModalRect(i2 + i8 * f, i, 79, 0, 9, 9);
                     }
                  }

                  if (i8 * 2 + 1 < scorePoints) {
                     f6 = i2 + i8 * f;
                     f7 = i;
                     i9 = i8 >= 10 ? 160 : 52;
                     f8 = f7;
                     f9 = f6;
                     this.drawTexturedModalRect((float)f9, (float)f8, i9, 0, 9, 9);
                  }

                  if (i8 * 2 + 1 == scorePoints) {
                     f6 = i2 + i8 * f;
                     f7 = i;
                     i9 = i8 >= 10 ? 169 : 61;
                     f8 = f7;
                     f9 = f6;
                     this.drawTexturedModalRect((float)f9, (float)f8, i9, 0, 9, 9);
                  }
               }
            } else {
               float f10 = MathHelper.clamp_float(scorePoints / 20.0F, 0.0F, 1.0F);
               int i10 = (int)((1.0F - f10) * 255.0F) << 16 | (int)(f10 * 255.0F) << 8;
               String text2 = scorePoints / 2.0F + "";
               if (i3 - this.mc.fontRendererObj.getInt3(text2 + "hp") >= i2) {
                  text2 = text2 + "hp";
               }

               this.mc.fontRendererObj.drawStringWithShadow(text2, (i3 + i2) / 2 - this.mc.fontRendererObj.getInt3(text2) / 2, i, i10);
            }
         }
      } else {
         String text3 = "" + EnumChatFormatting.YELLOW + scorePoints;
         this.mc.fontRendererObj.drawStringWithShadow(text3, i3 - this.mc.fontRendererObj.getInt3(text3), i, 16777215);
      }
   }

   public void updatePlayerList(boolean willBeRendered) {
      if (willBeRendered && !this.isBeingRendered) {
         this.lastTimeOpened = Minecraft.getSystemTime();
      }

      this.isBeingRendered = willBeRendered;
   }

   public void setFooter(IChatComponent footerIn) {
      this.footer = footerIn;
   }

   private boolean checkNetworkPlayerInfo(NetworkPlayerInfo networkPlayerInfo) {
      String name2 = networkPlayerInfo.getGameProfile().getName();
      return name2.equals(this.mc.session.getUsername())
         || Novoline.getNovoline().getIrcConnection().getGatewayClient().getGatewayUserList().getByName(name2) != null;
   }

   public String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      String name = networkPlayerInfoIn.getDisplayName() != null
         ? networkPlayerInfoIn.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
      NameProtect nameProtect2 = ModuleManager.nameProtect;
      if (nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(0)) {
         name = nameProtect2.protectText(name);
      }

      return name;
   }

   protected void drawPing(int i, int i2, int i3, NetworkPlayerInfo networkPlayerInfoIn) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(icons);
      byte b;
      if (networkPlayerInfoIn.getResponseTime() < 0) {
         b = 5;
      } else if (networkPlayerInfoIn.getResponseTime() < 150) {
         b = 0;
      } else if (networkPlayerInfoIn.getResponseTime() < 300) {
         b = 1;
      } else if (networkPlayerInfoIn.getResponseTime() < 600) {
         b = 2;
      } else if (networkPlayerInfoIn.getResponseTime() < 1000) {
         b = 3;
      } else {
         b = 4;
      }

      this.zLevel += 100.0F;
      this.drawTexturedModalRect(i2 + i - 11, i3, 0, 176 + b * 8, 10, 8);
      this.zLevel -= 100.0F;
   }

   static class PlayerComparator implements Comparator<NetworkPlayerInfo> {
      public int compare(NetworkPlayerInfo arg1, NetworkPlayerInfo arg2) {
         ScorePlayerTeam scorePlayerTeam = arg1.getPlayerTeam();
         ScorePlayerTeam scorePlayerTeam2 = arg2.getPlayerTeam();
         return ComparisonChain.start()
            .compareTrueFirst(arg1.getGameType() != net.minecraft.world.WorldSettings.GameType.SPECTATOR, arg2.getGameType() != net.minecraft.world.WorldSettings.GameType.SPECTATOR)
            .compare(scorePlayerTeam != null ? scorePlayerTeam.getRegisteredName() : "", scorePlayerTeam2 != null ? scorePlayerTeam2.getRegisteredName() : "")
            .compare(arg1.getGameProfile().getName(), arg2.getGameProfile().getName())
            .result();
      }

      private PlayerComparator() {
      }
   }
}
