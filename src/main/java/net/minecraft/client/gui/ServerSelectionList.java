package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;

public class ServerSelectionList extends GuiListExtended {
   private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
   private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
   private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
   private int selectedSlotIndex = -1;
   private final GuiMultiplayer owner;

   public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.owner = ownerIn;
   }

   public void updateLanServers(List<net.minecraft.client.network.LanServerDetector.LanServer> lanServers) {
      this.serverListLan.clear();

      for (net.minecraft.client.network.LanServerDetector.LanServer lanServer : lanServers) {
         this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanServer));
      }
   }

   @Override
   protected int getSize() {
      return this.serverListInternet.size() + 1 + this.serverListLan.size();
   }

   public void updateServerList(ServerList serverList) {
      this.serverListInternet.clear();

      for (int index = 0; index < serverList.countServers(); index++) {
         this.serverListInternet.add(new ServerListEntryNormal(this.owner, serverList.getServerData(index)));
      }
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 85;
   }

   @Override
   protected boolean isSelected(int slotIndex) {
      return slotIndex == this.selectedSlotIndex;
   }

   @Override
   protected int getScrollBarX() {
      return super.getScrollBarX() + 30;
   }

   public int getSelectedSlotIndex() {
      return this.selectedSlotIndex;
   }

   public void setSelectedSlotIndex(int selectedSlotIndexIn) {
      this.selectedSlotIndex = selectedSlotIndexIn;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      if (index < this.serverListInternet.size()) {
         return this.serverListInternet.get(index);
      }

      index -= this.serverListInternet.size();
      return index == 0 ? this.lanScanEntry : this.serverListLan.get(--index);
   }
}
