package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import wtf.novoline.enums.ViaProtocol;
import wtf.novoline.gui.ProxyManagerScreen;
import wtf.novoline.gui.ViaSettingsScreen;
import wtf.novoline.platform.ViaMCP;

public class GuiMultiplayer extends GuiScreen implements GuiYesNoCallback {
   private boolean initialized;
   private String hoveringText;
   private net.minecraft.client.network.LanServerDetector.ThreadLanServerFind lanServerDetector;
   private boolean editingServer;
   private net.minecraft.client.network.LanServerDetector.LanServerList lanServerList;
   private static final Logger logger = LogManager.getLogger();
   private GuiButton btnDeleteServer;
   private GuiButton btnEditServer;
   private boolean directConnect;
   private ServerSelectionList serverListSelector;
   private final OldServerPinger oldServerPinger = new OldServerPinger();
   private GuiScreen parentScreen;
   private GuiButton btnSelectServer;
   private boolean deletingServer;
   private ServerData selectedServer;
   private boolean addingServer;
   private ServerList savedServerList;

   private void refreshServerList() {
      this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedSlotIndex() < 0
            ? null
            : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());
         if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            String text = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData().serverName;
            if (text != null) {
               this.deletingServer = true;
               String text2 = I18n.format("selectServer.deleteQuestion");
               String text5 = "'" + text + "' " + I18n.format("selectServer.deleteWarning");
               String text3 = I18n.format("selectServer.deleteButton");
               String text4 = I18n.format("gui.cancel");
               GuiYesNo guiYesNo = new GuiYesNo(this, text2, text5, text3, text4, this.serverListSelector.getSelectedSlotIndex());
               this.mc.displayGuiScreen(guiYesNo);
            }
         } else if (button.id == 1) {
            this.connectToSelected();
         } else if (button.id == 4) {
            this.directConnect = true;
            this.mc.displayGuiScreen(new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 3) {
            this.addingServer = true;
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
         } else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.editingServer = true;
            ServerData serverData = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
            this.selectedServer = new ServerData(serverData.serverName, serverData.serverIP, false);
            this.selectedServer.copyFrom(serverData);
            this.mc.displayGuiScreen(new GuiScreenAddServer(this, this.selectedServer));
         } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 8) {
            this.refreshServerList();
         } else if (button.id == 9) {
            this.mc.displayGuiScreen(new ViaSettingsScreen(this));
         } else if (button.id == 10) {
            this.mc.displayGuiScreen(new ProxyManagerScreen(this));
         }
      }
   }

   public boolean canMoveServerDown(ServerListEntryNormal serverListEntryNormal, int i) {
      return i < this.savedServerList.countServers() - 1;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      if (!this.initialized) {
         this.initialized = true;
         this.savedServerList = new ServerList(this.mc);
         this.savedServerList.loadServerList();
         this.lanServerList = new net.minecraft.client.network.LanServerDetector.LanServerList();

         try {
            this.lanServerDetector = new net.minecraft.client.network.LanServerDetector.ThreadLanServerFind(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception exception) {
            logger.warn("Unable to start LAN server detection: " + exception.getMessage());
         }

         this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
         this.serverListSelector.updateServerList(this.savedServerList);
      } else {
         this.serverListSelector.setSelected(this.width, this.height, this.height - 64);
      }

      this.createButtons();
   }

   private void connectToServer(ServerData server) {
      this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
   }

   public void setHoveringText(String text5) {
      this.hoveringText = text5;
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      super.mouseReleased(i, i2, i3);
      this.serverListSelector.checkInt(i, i2);
   }

   public void selectServer(int index) {
      this.serverListSelector.setSelectedSlotIndex(index);
      GuiListExtended.IGuiListEntry guilistextended$iguilistentry = index < 0 ? null : this.serverListSelector.getListEntry(index);
      this.btnSelectServer.enabled = false;
      this.btnEditServer.enabled = false;
      this.btnDeleteServer.enabled = false;
      if (guilistextended$iguilistentry != null && !(guilistextended$iguilistentry instanceof ServerListEntryLanScan)) {
         this.btnSelectServer.enabled = true;
         if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.btnEditServer.enabled = true;
            this.btnDeleteServer.enabled = true;
         }
      }
   }

   public void moveServerDown(ServerListEntryNormal serverListEntryNormal, int i, boolean flag) {
      int i2 = flag ? this.savedServerList.countServers() - 1 : i + 1;
      this.savedServerList.swapServers(i, i2);
      if (this.serverListSelector.getSelectedSlotIndex() == i) {
         this.selectServer(i2);
      }

      this.serverListSelector.updateServerList(this.savedServerList);
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedSlotIndex() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());
      if (this.deletingServer) {
         this.deletingServer = false;
         if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            this.savedServerList.removeServerData(this.serverListSelector.getSelectedSlotIndex());
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.updateServerList(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.directConnect) {
         this.directConnect = false;
         if (result) {
            this.connectToServer(this.selectedServer);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.addingServer) {
         this.addingServer = false;
         if (result) {
            this.savedServerList.addServerData(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.updateServerList(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.editingServer) {
         this.editingServer = false;
         if (result && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
            ServerData serverData = ((ServerListEntryNormal)guilistextended$iguilistentry).getServerData();
            serverData.serverName = this.selectedServer.serverName;
            serverData.serverIP = this.selectedServer.serverIP;
            serverData.copyFrom(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.updateServerList(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      super.mouseClicked(i, i2, i3);
      this.serverListSelector.mouseClicked(i, i2, i3);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (this.lanServerList.getWasUpdated()) {
         List<net.minecraft.client.network.LanServerDetector.LanServer> list = this.lanServerList.getLanServers();
         this.lanServerList.setWasNotUpdated();
         this.serverListSelector.updateLanServers(list);
      }

      this.oldServerPinger.pingPendingNetworks();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.serverListSelector.handleMouseInput();
   }

   public ServerList getServerList() {
      return this.savedServerList;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.hoveringText = null;
      this.drawDefaultBackground();
      this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hoveringText != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      int selectedSlotIndex = this.serverListSelector.getSelectedSlotIndex();
      GuiListExtended.IGuiListEntry guilistextended$iguilistentry = selectedSlotIndex < 0 ? null : this.serverListSelector.getListEntry(selectedSlotIndex);
      if (keyCode == 63) {
         this.refreshServerList();
      } else if (selectedSlotIndex >= 0) {
         if (keyCode == 200) {
            if (isShiftKeyDown()) {
               if (selectedSlotIndex > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                  this.savedServerList.swapServers(selectedSlotIndex, selectedSlotIndex - 1);
                  this.selectServer(this.serverListSelector.getSelectedSlotIndex() - 1);
                  this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  this.serverListSelector.updateServerList(this.savedServerList);
               }
            } else if (selectedSlotIndex > 0) {
               this.selectServer(this.serverListSelector.getSelectedSlotIndex() - 1);
               this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.getSelectedSlotIndex() > 0) {
                     this.selectServer(this.serverListSelector.getSize() - 1);
                     this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode == 208) {
            if (isShiftKeyDown()) {
               if (selectedSlotIndex < this.savedServerList.countServers() - 1) {
                  this.savedServerList.swapServers(selectedSlotIndex, selectedSlotIndex + 1);
                  this.selectServer(selectedSlotIndex + 1);
                  this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  this.serverListSelector.updateServerList(this.savedServerList);
               }
            } else if (selectedSlotIndex < this.serverListSelector.getSize()) {
               this.selectServer(this.serverListSelector.getSelectedSlotIndex() + 1);
               this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
               if (this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex()) instanceof ServerListEntryLanScan) {
                  if (this.serverListSelector.getSelectedSlotIndex() < this.serverListSelector.getSize() - 1) {
                     this.selectServer(this.serverListSelector.getSize() + 1);
                     this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  } else {
                     this.selectServer(-1);
                  }
               }
            } else {
               this.selectServer(-1);
            }
         } else if (keyCode != 28 && keyCode != 156) {
            super.keyTyped(typedChar, keyCode);
         } else {
            this.actionPerformed(this.buttonList.get(2));
         }
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   public void moveServerUp(ServerListEntryNormal serverListEntryNormal, int i, boolean flag) {
      int i2 = flag ? 0 : i - 1;
      this.savedServerList.swapServers(i, i2);
      if (this.serverListSelector.getSelectedSlotIndex() == i) {
         this.selectServer(i2);
      }

      this.serverListSelector.updateServerList(this.savedServerList);
   }

   public void createButtons() {
      this.buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
      this.buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
      this.buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
      this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
      this.buttonList
         .add(new GuiButton(9, this.width / 2 + 55, 6, 98, 20, "Protocol: " + ViaProtocol.createProtocolVersion(ViaMCP.getViaMCP().getVersion()).getName()));
      this.buttonList.add(new GuiButton(10, this.width / 2 - 154, 6, 98, 20, "Proxy"));
      this.selectServer(this.serverListSelector.getSelectedSlotIndex());
   }

   public OldServerPinger getOldServerPinger() {
      return this.oldServerPinger;
   }

   public void connectToSelected() {
      GuiListExtended.IGuiListEntry guilistextended$iguilistentry = this.serverListSelector.getSelectedSlotIndex() < 0
         ? null
         : this.serverListSelector.getListEntry(this.serverListSelector.getSelectedSlotIndex());
      if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
         this.connectToServer(((ServerListEntryNormal)guilistextended$iguilistentry).getServerData());
      } else if (guilistextended$iguilistentry instanceof ServerListEntryLanDetected) {
         net.minecraft.client.network.LanServerDetector.LanServer lanserverdetector$lanserver = ((ServerListEntryLanDetected)guilistextended$iguilistentry).getLanServer();
         this.connectToServer(new ServerData(lanserverdetector$lanserver.getServerMotd(), lanserverdetector$lanserver.getServerIpPort(), true));
      }
   }

   public boolean canMoveServerUp(ServerListEntryNormal serverListEntryNormal, int i) {
      return i > 0;
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.oldServerPinger.clearPendingNetworks();
   }

   public GuiMultiplayer(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
   }
}
