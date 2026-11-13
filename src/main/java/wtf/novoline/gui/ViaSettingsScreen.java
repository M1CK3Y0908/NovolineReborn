package wtf.novoline.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.enums.ViaProtocol;
import wtf.novoline.platform.ViaMCP;
import wtf.novoline.util.ViaSettings;

public class ViaSettingsScreen extends GuiScreen {
   private final GuiScreen guiScreen;
   public ViaSettingsScreen.GuiSlotChild guiSlotChild;
   private final ViaSettings viaSettings;

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      this.guiSlotChild.actionPerformed(guiButton);
      switch (guiButton.id) {
         case 0:
            this.viaSettings.setIgnoreProtocolErrors(!this.viaSettings.isIgnoreProtocolErrors());
            this.initGui();
            break;
         case 1:
            this.mc.displayGuiScreen(this.guiScreen);
      }
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      this.buttonList
         .add(
            new GuiButton(0, this.width / 2 + 2, this.height - 27, 150, 20, "Ignore errors" + (this.viaSettings.isIgnoreProtocolErrors() ? " [ON]" : " [OFF]"))
         );
      this.buttonList.add(new GuiButton(1, this.width / 2 - 152, this.height - 27, 150, 20, "Back"));
      this.guiSlotChild = new ViaSettingsScreen.GuiSlotChild(this.mc, this.width, this.height, 32, this.height - 32);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      this.guiSlotChild.drawScreen(i, i2, f);
      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0, 2.0, 2.0);
      this.drawCenteredString(this.fontRendererObj, EnumChatFormatting.BOLD + "Select version", this.width / 4, 5, 16777215);
      GlStateManager.popMatrix();
      super.drawScreen(i, i2, f);
   }

   public ViaSettingsScreen(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;
      this.viaSettings = ViaSettings.getViaSettings();
   }

   @Override
   public void handleMouseInput() throws IOException {
      this.guiSlotChild.handleMouseInput();
      super.handleMouseInput();
   }

   class GuiSlotChild extends GuiSlot {
      @Override
      protected void elementClicked(int i, boolean flag, int i2, int i3) {
         ViaMCP.getViaMCP().setVersion(ViaProtocol.values()[i].getProtocolVersion().getVersion());
      }

      public GuiSlotChild(Minecraft minecraft2, int i, int i2, int i3, int i4) {
         super(minecraft2, i, i2, i3, i4, 18);
      }

      @Override
      protected void drawBackground() {
         ViaSettingsScreen.this.drawDefaultBackground();
      }

      @Override
      protected int getSize() {
         return ViaProtocol.values().length;
      }

      @Override
      protected boolean isSelected(int i) {
         return false;
      }

      @Override
      protected void drawSlot(int i, int i2, int i3, int i4, int i5, int i6) {
         ViaSettingsScreen.this.drawCenteredString(
            this.mc.fontRendererObj,
            (
                  ViaMCP.getViaMCP().getVersion() == ViaProtocol.values()[i].getProtocolVersion().getVersion()
                     ? EnumChatFormatting.GREEN.toString() + EnumChatFormatting.BOLD
                     : EnumChatFormatting.GRAY.toString()
               )
               + ViaProtocol.createProtocolVersion(ViaProtocol.values()[i].getProtocolVersion().getVersion()).getName(),
            this.width / 2,
            i3 + 2,
            -1
         );
         GlStateManager.pushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         ViaSettingsScreen.this.drawCenteredString(
            this.mc.fontRendererObj,
            "Ver: " + ViaProtocol.createProtocolVersion(ViaProtocol.values()[i].getProtocolVersion().getVersion()).getVersion(),
            this.width,
            (i3 + 2) * 2 + 20,
            -1
         );
         GlStateManager.popMatrix();
      }
   }
}
