package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> teamItems = Lists.newArrayList();

   @Override
   public boolean isEnabled() {
      Iterator iterator2 = this.teamItems.iterator();

      while (iterator2.hasNext()) {
         if (((ISpectatorMenuObject)iterator2.next()).isEnabled()) {
            return true;
         }
      }

      return false;
   }

   public TeleportToTeam() {
      for (ScorePlayerTeam scorePlayerTeam : Minecraft.getMinecraft().theWorld.getScoreboard().getTeams()) {
         this.teamItems.add(new TeleportToTeam.TeamSelectionObject(scorePlayerTeam));
      }
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to team member");
   }

   @Override
   public void selectItem(SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   @Override
   public List<ISpectatorMenuObject> getItems() {
      return this.teamItems;
   }

   @Override
   public void renderIcon(float brightness, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public IChatComponent getPrompt() {
      return new ChatComponentText("Select a team to teleport to");
   }

   class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam team;
      private final List<NetworkPlayerInfo> players;
      private final ResourceLocation texture;

      @Override
      public void renderIcon(float brightness, int alpha) {
         int i = -1;
         String text = FontRenderer.getFormatFromString(this.team.getColorPrefix());
         if (text.length() >= 2) {
            i = Minecraft.getMinecraft().fontRendererObj.getColorCode(text.charAt(1));
         }

         if (i >= 0) {
            float f = (i >> 16 & 0xFF) / 255.0F;
            float f2 = (i >> 8 & 0xFF) / 255.0F;
            float f3 = (i & 0xFF) / 255.0F;
            Gui.handleInt(1, 1, 15, 15, MathHelper.rgb(f * brightness, f2 * brightness, f3 * brightness) | alpha << 24);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
         GlStateManager.color(brightness, brightness, brightness, alpha / 255.0F);
         Gui.handleInt3(2, 2, 8.0F, 8.0F, 8, 12, 12, 64.0F, 64.0F);
         Gui.handleInt3(2, 2, 40.0F, 8.0F, 8, 12, 12, 64.0F, 64.0F);
      }

      @Override
      public void selectItem(SpectatorMenu menu) {
         menu.selectCategory(new TeleportToPlayer(this.players));
      }

      public TeamSelectionObject(ScorePlayerTeam teamIn) {
         this.team = teamIn;
         this.players = Lists.newArrayList();

         for (String text2 : teamIn.getMembershipCollection()) {
            NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(text2);
            if (playerInfo != null) {
               this.players.add(playerInfo);
            }
         }

         if (!this.players.isEmpty()) {
            String name = this.players.get(new Random().nextInt(this.players.size())).getGameProfile().getName();
            this.texture = AbstractClientPlayer.getLocationSkin(name);
            AbstractClientPlayer.getDownloadImageSkin(this.texture, name);
         } else {
            this.texture = DefaultPlayerSkin.getDefaultSkinLegacy();
         }
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText(this.team.getTeamName());
      }

      @Override
      public boolean isEnabled() {
         return !this.players.isEmpty();
      }
   }
}
