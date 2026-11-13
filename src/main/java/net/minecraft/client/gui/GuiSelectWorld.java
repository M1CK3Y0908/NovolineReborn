package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
   private String conversionLabel;
   private final DateFormat dateFormatter = new SimpleDateFormat();
   private GuiButton recreateButton;
   private GuiButton selectButton;
   private boolean confirmingDelete;
   protected String screenTitle = "Select world";
   private GuiButton deleteButton;
   private int selectedIndex;
   private String[] gameModeNames = new String[4];
   private java.util.List<SaveFormatComparator> saveList;
   private boolean selectWorldInProgress;
   private GuiSelectWorld.List availableWorlds;
   private GuiButton renameButton;
   private static final Logger logger = LogManager.getLogger();
   private String worldLabel;
   protected GuiScreen parentScreen;

   public GuiSelectWorld(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            String text = this.getSaveDisplayName(this.selectedIndex);
            if (text != null) {
               this.confirmingDelete = true;
               GuiYesNo guiYesNo = makeDeleteWorldYesNo(this, text, this.selectedIndex);
               this.mc.displayGuiScreen(guiYesNo);
            }
         } else if (button.id == 1) {
            this.selectWorld(this.selectedIndex);
         } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiCreateWorld(this));
         } else if (button.id == 6) {
            this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getSaveFileName(this.selectedIndex)));
         } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 7) {
            GuiCreateWorld guiCreateWorld = new GuiCreateWorld(this);
            ISaveHandler saveHandler = this.mc.getSaveLoader().getSaveLoader(this.getSaveFileName(this.selectedIndex), false);
            WorldInfo worldInfo = saveHandler.loadWorldInfo();
            saveHandler.flush();
            guiCreateWorld.recreateFromExistingWorld(worldInfo);
            this.mc.displayGuiScreen(guiCreateWorld);
         } else {
            this.availableWorlds.actionPerformed(button);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected String getSaveDisplayName(int index) {
      String text = this.saveList.get(index).getDisplayName();
      if (StringUtils.isEmpty(text)) {
         text = I18n.format("selectWorld.world") + " " + (index + 1);
      }

      return text;
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (this.confirmingDelete) {
         this.confirmingDelete = false;
         if (result) {
            ISaveFormat saveFormat = this.mc.getSaveLoader();
            saveFormat.flushCache();
            saveFormat.deleteWorldDirectory(this.getSaveFileName(id));

            try {
               this.loadLevelList();
            } catch (AnvilConverterException anvilConverterException) {
               logger.error("Couldn't load level list", anvilConverterException);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   public void selectWorld(int index) {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.selectWorldInProgress) {
         this.selectWorldInProgress = true;
         String text = this.getSaveFileName(index);
         if (text == null) {
            text = "World" + index;
         }

         String text2 = this.getSaveDisplayName(index);
         if (text2 == null) {
            text2 = "World" + index;
         }

         if (this.mc.getSaveLoader().canLoadWorld(text)) {
            this.mc.launchIntegratedServer(text, text2, (WorldSettings)null);
         }
      }
   }

   public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id) {
      String text2 = I18n.format("selectWorld.deleteQuestion");
      String text3 = "'" + name + "' " + I18n.format("selectWorld.deleteWarning");
      String text4 = I18n.format("selectWorld.deleteButton");
      String text5 = I18n.format("gui.cancel");
      return new GuiYesNo(selectWorld, text2, text3, text4, text5, id);
   }

   private void loadLevelList() throws AnvilConverterException {
      ISaveFormat saveFormat = this.mc.getSaveLoader();
      this.saveList = saveFormat.getSaveList();
      Collections.sort(this.saveList);
      this.selectedIndex = -1;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("selectWorld.title");

      try {
         this.loadLevelList();
      } catch (AnvilConverterException anvilConverterException) {
         logger.error("Couldn't load level list", anvilConverterException);
         this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", anvilConverterException.getMessage()));
         return;
      }

      this.worldLabel = I18n.format("selectWorld.world");
      this.conversionLabel = I18n.format("selectWorld.conversion");
      this.gameModeNames[net.minecraft.world.WorldSettings.GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
      this.gameModeNames[net.minecraft.world.WorldSettings.GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
      this.gameModeNames[net.minecraft.world.WorldSettings.GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
      this.gameModeNames[net.minecraft.world.WorldSettings.GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
      this.availableWorlds = new GuiSelectWorld.List(this.mc);
      this.availableWorlds.registerScrollButtons(4, 5);
      this.addWorldSelectionButtons();
   }

   public void addWorldSelectionButtons() {
      this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
      this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
      this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
      this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.recreateButton.enabled = false;
   }

   protected String getSaveFileName(int index) {
      return this.saveList.get(index).getFileName();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.availableWorlds.handleMouseInput();
   }

   class List extends GuiSlot {
      @Override
      protected void drawSlot(int entryID, int i, int i2, int i3, int mouseXIn, int mouseYIn) {
         SaveFormatComparator saveFormatComparator = GuiSelectWorld.this.saveList.get(entryID);
         String displayName = saveFormatComparator.getDisplayName();
         if (StringUtils.isEmpty(displayName)) {
            displayName = GuiSelectWorld.this.worldLabel + " " + (entryID + 1);
         }

         String fileName = saveFormatComparator.getFileName();
         fileName = fileName + " (" + GuiSelectWorld.this.dateFormatter.format(new Date(saveFormatComparator.getLastTimePlayed()));
         fileName = fileName + ")";
         String text3;
         if (saveFormatComparator.requiresConversion()) {
            text3 = GuiSelectWorld.this.conversionLabel + " ";
         } else {
            text3 = GuiSelectWorld.this.gameModeNames[saveFormatComparator.getEnumGameType().getID()];
            if (saveFormatComparator.isHardcoreModeEnabled()) {
               text3 = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore") + EnumChatFormatting.RESET;
            }

            if (saveFormatComparator.getCheatsEnabled()) {
               text3 = text3 + ", " + I18n.format("selectWorld.cheats");
            }
         }

         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, displayName, i + 2, i2 + 1, 16777215);
         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, fileName, i + 2, i2 + 12, 8421504);
         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, text3, i + 2, i2 + 12 + 10, 8421504);
      }

      @Override
      protected int getSize() {
         return GuiSelectWorld.this.saveList.size();
      }

      @Override
      protected void drawBackground() {
         GuiSelectWorld.this.drawDefaultBackground();
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         GuiSelectWorld.this.selectedIndex = slotIndex;
         boolean flag = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
         GuiSelectWorld.this.selectButton.enabled = flag;
         GuiSelectWorld.this.deleteButton.enabled = flag;
         GuiSelectWorld.this.renameButton.enabled = flag;
         GuiSelectWorld.this.recreateButton.enabled = flag;
         if (isDoubleClick && flag) {
            GuiSelectWorld.this.selectWorld(slotIndex);
         }
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return slotIndex == GuiSelectWorld.this.selectedIndex;
      }

      public List(Minecraft mcIn) {
         super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
      }

      @Override
      protected int getContentHeight() {
         return GuiSelectWorld.this.saveList.size() * 36;
      }
   }
}
