package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer {
   private static final Logger logger = LogManager.getLogger();
   private boolean buttonsCreated;
   private GuiBeacon.ConfirmButton confirmButton;
   private static final ResourceLocation beaconGuiTextures = new ResourceLocation("textures/gui/container/beacon.png");
   private IInventory tileBeacon;

   public GuiBeacon(InventoryPlayer playerInventory, IInventory tileBeaconIn) {
      super(new ContainerBeacon(playerInventory, tileBeaconIn));
      this.tileBeacon = tileBeaconIn;
      this.xSize = 230;
      this.ySize = 219;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      int field = this.tileBeacon.getField(0);
      int field2 = this.tileBeacon.getField(1);
      int field3 = this.tileBeacon.getField(2);
      if (this.buttonsCreated && field >= 0) {
         this.buttonsCreated = false;

         for (int i = 0; i <= 2; i++) {
            int length2 = TileEntityBeacon.effectsList[i].length;
            int i2 = length2 * 22 + (length2 - 1) * 2;

            for (int i3 = 0; i3 < length2; i3++) {
               int id2 = TileEntityBeacon.effectsList[i][i3].id;
               GuiBeacon.PowerButton powerButton = new GuiBeacon.PowerButton(
                  i << 8 | id2, this.guiLeft + 76 + i3 * 24 - i2 / 2, this.guiTop + 22 + i * 25, id2, i
               );
               this.buttonList.add(powerButton);
               if (i >= field) {
                  powerButton.enabled = false;
               } else if (id2 == field2) {
                  powerButton.update2();
               }
            }
         }

         int i4 = TileEntityBeacon.effectsList[3].length + 1;
         int i5 = i4 * 22 + (i4 - 1) * 2;

         for (int i6 = 0; i6 < i4 - 1; i6++) {
            int id3 = TileEntityBeacon.effectsList[3][i6].id;
            GuiBeacon.PowerButton powerButton2 = new GuiBeacon.PowerButton(768 | id3, this.guiLeft + 167 + i6 * 24 - i5 / 2, this.guiTop + 47, id3, 3);
            this.buttonList.add(powerButton2);
            if (3 >= field) {
               powerButton2.enabled = false;
            } else if (id3 == field3) {
               powerButton2.update2();
            }
         }

         if (field2 > 0) {
            GuiBeacon.PowerButton powerButton3 = new GuiBeacon.PowerButton(
               768 | field2, this.guiLeft + 167 + (i4 - 1) * 24 - i5 / 2, this.guiTop + 47, field2, 3
            );
            this.buttonList.add(powerButton3);
            if (3 >= field) {
               powerButton3.enabled = false;
            } else if (field2 == field3) {
               powerButton3.update2();
            }
         }
      }

      this.confirmButton.enabled = this.tileBeacon.getStackInSlot(0) != null && field2 > 0;
   }

   @Override
   public void initGui() {
      super.initGui();
      this.buttonList.add(this.confirmButton = new GuiBeacon.ConfirmButton(-1, this.guiLeft + 164, this.guiTop + 107));
      this.buttonList.add(new GuiBeacon.CancelButton(-2, this.guiLeft + 190, this.guiTop + 107));
      this.buttonsCreated = true;
      this.confirmButton.enabled = false;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      RenderHelper.disableStandardItemLighting();
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.fontRendererObj, I18n.format("tile.beacon.secondary"), 169, 10, 14737632);

      for (GuiButton guiButton : this.buttonList) {
         if (guiButton.isMouseOver()) {
            guiButton.drawButtonForegroundLayer(mouseX - this.guiLeft, mouseY - this.guiTop);
            break;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == -2) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button.id == -1) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.tileBeacon.getField(1));
         packetBuffer.writeInt(this.tileBeacon.getField(2));
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|Beacon", packetBuffer));
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (button instanceof GuiBeacon.PowerButton) {
         if (((GuiBeacon.PowerButton)button).isSelected()) {
            return;
         }

         int id2 = button.id;
         int i = id2 & 0xFF;
         if (id2 >> 8 < 3) {
            this.tileBeacon.setField(1, i);
         } else {
            this.tileBeacon.setField(2, i);
         }

         this.buttonList.clear();
         this.initGui();
         this.updateScreen();
      }
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(beaconGuiTextures);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.emerald), i + 42, i2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.diamond), i + 42 + 22, i2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.gold_ingot), i + 42 + 44, i2 + 109);
      this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.iron_ingot), i + 42 + 66, i2 + 109);
      this.itemRender.zLevel = 0.0F;
   }

   static class Button extends GuiButton {
      private boolean selected;
      private final ResourceLocation texture;
      private final int textureX;
      private final int textureY;

      public void update2() {
         this.selected = true;
      }

      @Override
      public void drawButton(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            mc.getTextureManager().bindTexture(GuiBeacon.beaconGuiTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            int i = 0;
            if (!this.enabled) {
               i = 0 + this.width * 2;
            } else if (this.selected) {
               i = 0 + this.width * 1;
            } else if (this.hovered) {
               i = 0 + this.width * 3;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, i, 219, this.width, this.height);
            if (!GuiBeacon.beaconGuiTextures.equals(this.texture)) {
               mc.getTextureManager().bindTexture(this.texture);
            }

            this.drawTexturedModalRect(this.xPosition + 2, this.yPosition + 2, this.textureX, this.textureY, 18, 18);
         }
      }

      protected Button(int id, int x, int y, ResourceLocation texture, int textureX, int textureY) {
         super(id, x, y, 22, 22, "");
         this.texture = texture;
         this.textureX = textureX;
         this.textureY = textureY;
      }

      public boolean isSelected() {
         return this.selected;
      }
   }

   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int i, int i2, int i3) {
         super(i, i2, i3, GuiBeacon.beaconGuiTextures, 112, 220);
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.cancel"), mouseX, mouseY);
      }
   }

   class ConfirmButton extends GuiBeacon.Button {
      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         GuiBeacon.this.drawCreativeTabHoveringText(I18n.format("gui.done"), mouseX, mouseY);
      }

      public ConfirmButton(int i, int i2, int i3) {
         super(i, i2, i3, GuiBeacon.beaconGuiTextures, 90, 220);
      }
   }

   class PowerButton extends GuiBeacon.Button {
      private final int effectId;
      private final int tier;

      public PowerButton(int id, int x, int y, int effectId, int tier) {
         super(
            id,
            x,
            y,
            GuiContainer.inventoryBackground,
            0 + Potion.potionTypes[effectId].getStatusIconIndex() % 8 * 18,
            198 + Potion.potionTypes[effectId].getStatusIconIndex() / 8 * 18
         );
         this.effectId = effectId;
         this.tier = tier;
      }

      @Override
      public void drawButtonForegroundLayer(int mouseX, int mouseY) {
         String name = I18n.format(Potion.potionTypes[this.effectId].getName());
         if (this.tier >= 3 && this.effectId != Potion.regeneration.id) {
            name = name + " II";
         }

         GuiBeacon.this.drawCreativeTabHoveringText(name, mouseX, mouseY);
      }
   }
}
