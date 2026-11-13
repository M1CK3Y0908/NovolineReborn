package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryDefault;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;

public class GuiScreenResourcePacks extends GuiScreen {
   private GuiResourcePackAvailable availableResourcePacksList;
   private static final Logger logger = LogManager.getLogger();
   private boolean changed = false;
   private List<ResourcePackListEntry> availableResourcePacks;
   private GuiResourcePackSelected selectedResourcePacksList;
   private List<ResourcePackListEntry> selectedResourcePacks;
   private final GuiScreen parentScreen;

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("resourcePack.openFolder")));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")));
      if (!this.changed) {
         this.availableResourcePacks = Lists.newArrayList();
         this.selectedResourcePacks = Lists.newArrayList();
         ResourcePackRepository resourcePackRepository = this.mc.getResourcePackRepository();
         resourcePackRepository.updateRepositoryEntriesAll();
         ArrayList arrayList = Lists.newArrayList(resourcePackRepository.getRepositoryEntriesAll());
         arrayList.removeAll(resourcePackRepository.getRepositoryEntries());

         for (net.minecraft.client.resources.ResourcePackRepository.Entry entry : (Iterable<ResourcePackRepository.Entry>) arrayList) {
            this.availableResourcePacks.add(new ResourcePackListEntryFound(this, entry));
         }

         for (net.minecraft.client.resources.ResourcePackRepository.Entry entry2 : Lists.reverse(resourcePackRepository.getRepositoryEntries())) {
            this.selectedResourcePacks.add(new ResourcePackListEntryFound(this, entry2));
         }

         this.selectedResourcePacks.add(new ResourcePackListEntryDefault(this));
      }

      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, this.height, this.availableResourcePacks);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      this.availableResourcePacksList.registerScrollButtons(7, 8);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, this.height, this.selectedResourcePacks);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      this.selectedResourcePacksList.registerScrollButtons(7, 8);
   }

   public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry resourcePackListEntry) {
      return this.hasResourcePackEntry(resourcePackListEntry) ? this.selectedResourcePacks : this.availableResourcePacks;
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.selectedResourcePacksList.handleMouseInput();
      this.availableResourcePacksList.handleMouseInput();
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.availableResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
      this.selectedResourcePacksList.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.update5();
      this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            File dirResourcepacks = this.mc.getResourcePackRepository().getDirResourcepacks();
            String absolutePath = dirResourcepacks.getAbsolutePath();
            if (Util.getOSType() == net.minecraft.util.Util.EnumOS.OSX) {
               try {
                  logger.info(absolutePath);
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                  return;
               } catch (IOException ioException) {
                  logger.error("Couldn't open file", ioException);
               }
            } else if (Util.getOSType() == net.minecraft.util.Util.EnumOS.WINDOWS) {
               String text = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);

               try {
                  Runtime.getRuntime().exec(text);
                  return;
               } catch (IOException ioException2) {
                  logger.error("Couldn't open file", ioException2);
               }
            }

            boolean flag = false;

            try {
               Class clazz = Class.forName("java.awt.Desktop");
               Object object = clazz.getMethod("getDesktop").invoke(null);
               clazz.getMethod("browse", URI.class).invoke(object, dirResourcepacks.toURI());
            } catch (Throwable throwable) {
               logger.error("Couldn't open link", throwable);
               flag = true;
            }

            if (flag) {
               logger.info("Opening via system class!");
               Sys.openURL("file://" + absolutePath);
            }
         } else if (button.id == 1) {
            if (this.changed) {
               ArrayList arrayList = Lists.newArrayList();

               for (ResourcePackListEntry resourcePackListEntry : this.selectedResourcePacks) {
                  if (resourcePackListEntry instanceof ResourcePackListEntryFound) {
                     arrayList.add(((ResourcePackListEntryFound)resourcePackListEntry).getResourcePackEntry());
                  }
               }

               Collections.reverse(arrayList);
               this.mc.getResourcePackRepository().setRepositories(arrayList);
               this.mc.gameSettings.resourcePacks.clear();
               this.mc.gameSettings.incompatibleResourcePacks.clear();

               for (net.minecraft.client.resources.ResourcePackRepository.Entry entry : (Iterable<ResourcePackRepository.Entry>) arrayList) {
                  this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                  if (entry.getCompatibility() != 1) {
                     this.mc.gameSettings.incompatibleResourcePacks.add(entry.getResourcePackName());
                  }
               }

               this.mc.gameSettings.saveOptions();
               this.mc.refreshResources();
            }

            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
   }

   public GuiScreenResourcePacks(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   public List<ResourcePackListEntry> getAvailableResourcePacks() {
      return this.availableResourcePacks;
   }

   public void markChanged() {
      this.changed = true;
   }

   public boolean hasResourcePackEntry(ResourcePackListEntry resourcePackListEntry) {
      return this.selectedResourcePacks.contains(resourcePackListEntry);
   }

   public List<ResourcePackListEntry> getSelectedResourcePacks() {
      return this.selectedResourcePacks;
   }
}
