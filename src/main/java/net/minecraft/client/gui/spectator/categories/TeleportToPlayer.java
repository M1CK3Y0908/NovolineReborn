package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> playerItems = Lists.newArrayList();
   private static final Ordering<NetworkPlayerInfo> PLAYER_ORDERING = Ordering.from(new Comparator<NetworkPlayerInfo>() {
      public int compare(NetworkPlayerInfo left, NetworkPlayerInfo right) {
         return ComparisonChain.start().compare(left.getGameProfile().getId(), right.getGameProfile().getId()).result();
      }
   });

   @Override
   public void renderIcon(float brightness, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.SPECTATOR_WIDGETS);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   public TeleportToPlayer() {
      this(PLAYER_ORDERING.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()));
   }

   @Override
   public List<ISpectatorMenuObject> getItems() {
      return this.playerItems;
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to player");
   }

   @Override
   public void selectItem(SpectatorMenu menu) {
      menu.selectCategory(this);
   }

   @Override
   public IChatComponent getPrompt() {
      return new ChatComponentText("Select a player to teleport to");
   }

   @Override
   public boolean isEnabled() {
      return !this.playerItems.isEmpty();
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> players) {
      for (NetworkPlayerInfo networkPlayerInfo : PLAYER_ORDERING.sortedCopy(players)) {
         if (networkPlayerInfo.getGameType() != net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
            this.playerItems.add(new PlayerMenuObject(networkPlayerInfo.getGameProfile()));
         }
      }
   }
}
