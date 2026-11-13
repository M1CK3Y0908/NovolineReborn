package net.minecraft.client.gui;

import java.io.IOException;
import java.io.Serializable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IngestServerTester;
import net.minecraft.util.EnumChatFormatting;
import tv.twitch.broadcast.IngestServer;

public class GuiIngestServers extends GuiScreen {
   private String text;
   private final GuiScreen guiScreen;
   private GuiIngestServers.GuiSlotChild guiSlotChild;

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      if (guiButton.enabled) {
         if (guiButton.id == 1) {
            this.mc.displayGuiScreen(this.guiScreen);
         } else {
            this.mc.gameSettings.streamPreferredServer = "";
            this.mc.gameSettings.saveOptions();
         }
      }
   }

   static FontRenderer access$000(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   public GuiIngestServers(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;
   }

   static FontRenderer access$100(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   static FontRenderer access$200(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   static FontRenderer access$300(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   static FontRenderer access$400(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   static FontRenderer access$500(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   static FontRenderer access$600(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   @Override
   public void initGui() {
      this.text = I18n.format("options.stream.ingest.title");
      this.guiSlotChild = new GuiIngestServers.GuiSlotChild(this.mc);
      if (!this.mc.getIStream().isActive4()) {
         this.mc.getIStream().update13();
      }

      this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 24 - 6, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 24 - 6, 150, 20, I18n.format("options.stream.ingest.reset")));
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.guiSlotChild.handleMouseInput();
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.drawDefaultBackground();
      this.guiSlotChild.drawScreen(i, i2, f);
      this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, 20, 16777215);
      super.drawScreen(i, i2, f);
   }

   @Override
   public void startClosing() {
      if (this.mc.getIStream().isActive4()) {
         this.mc.getIStream().getIngestServerTester().update5();
      }
   }

   static FontRenderer access$700(GuiIngestServers guiIngestServers) {
      return guiIngestServers.fontRendererObj;
   }

   class GuiSlotChild extends GuiSlot {
      @Override
      protected void elementClicked(int i, boolean flag, int i2, int i3) {
         this.mc.gameSettings.streamPreferredServer = this.mc.getIStream().getArray()[i].serverUrl;
         this.mc.gameSettings.saveOptions();
      }

      @Override
      protected boolean isSelected(int i) {
         return this.mc.getIStream().getArray()[i].serverUrl.equals(this.mc.gameSettings.streamPreferredServer);
      }

      @Override
      protected void drawSlot(int i, int i2, int i3, int i4, int i5, int i6) {
         IngestServer ingestServer = this.mc.getIStream().getArray()[i];
         String text2 = ingestServer.serverUrl.replaceAll("\\{stream_key\\}", "");
         String text3 = (int)ingestServer.bitrateKbps + " kbps";
         String text4 = null;
         IngestServerTester ingestServerTester = this.mc.getIStream().getIngestServerTester();
         if (ingestServerTester != null) {
            if (ingestServer == ingestServerTester.getIngestServer()) {
               text2 = EnumChatFormatting.GREEN + text2;
               text3 = (int)(ingestServerTester.getFloatValue2() * 100.0F) + "%";
            } else if (i < ingestServerTester.getIntValue()) {
               if (ingestServer.bitrateKbps == 0.0F) {
                  text3 = EnumChatFormatting.RED + "Down!";
               }
            } else {
               text3 = EnumChatFormatting.OBFUSCATED + "1234" + EnumChatFormatting.RESET + " kbps";
            }
         } else if (ingestServer.bitrateKbps == 0.0F) {
            text3 = EnumChatFormatting.RED + "Down!";
         }

         i2 -= 15;
         if (this.isSelected(i)) {
            text4 = EnumChatFormatting.BLUE + "(Preferred)";
         } else if (ingestServer.defaultServer) {
            text4 = EnumChatFormatting.GREEN + "(Default)";
         }

         GuiIngestServers.this.drawString(GuiIngestServers.access$700(GuiIngestServers.this), ingestServer.serverName, i2 + 2, i3 + 5, 16777215);
         GuiIngestServers.this.drawString(
            GuiIngestServers.access$500(GuiIngestServers.this),
            (String) text2,
            i2 + 2,
            i3 + GuiIngestServers.access$300(GuiIngestServers.this).FONT_HEIGHT + 5 + 3,
            3158064
         );
         GuiIngestServers.this.drawString(
            GuiIngestServers.access$400(GuiIngestServers.this),
            text3,
            this.getScrollBarX() - 5 - GuiIngestServers.access$200(GuiIngestServers.this).getInt3(text3),
            i3 + 5,
            8421504
         );
         if (text4 != null) {
            GuiIngestServers.this.drawString(
               GuiIngestServers.access$000(GuiIngestServers.this),
               text4,
               this.getScrollBarX() - 5 - GuiIngestServers.access$600(GuiIngestServers.this).getInt3(text4),
               i3 + 5 + 3 + GuiIngestServers.access$100(GuiIngestServers.this).FONT_HEIGHT,
               8421504
            );
         }
      }

      public GuiSlotChild(Minecraft minecraft2) {
         super(
            minecraft2,
            GuiIngestServers.this.width,
            GuiIngestServers.this.height,
            32,
            GuiIngestServers.this.height - 35,
            (int)(minecraft2.fontRendererObj.FONT_HEIGHT * 3.5)
         );
         this.update();
      }

      @Override
      protected void drawBackground() {
      }

      @Override
      protected int getScrollBarX() {
         return super.getScrollBarX() + 15;
      }

      @Override
      protected int getSize() {
         return this.mc.getIStream().getArray().length;
      }
   }
}
