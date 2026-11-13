package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer {
   private GuiMerchant.MerchantButton nextButton;
   private IMerchant merchant;
   private IChatComponent chatComponent;
   private static final Logger logger = LogManager.getLogger();
   private int selectedMerchantRecipe;
   private GuiMerchant.MerchantButton previousButton;
   private static final ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

   @Override
   public void updateScreen() {
      super.updateScreen();
      MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
      if (merchantRecipeList != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < merchantRecipeList.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }
   }

   public IMerchant getMerchant() {
      return this.merchant;
   }

   @Override
   public void initGui() {
      super.initGui();
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, i + 120 + 27, i2 + 24 - 1, true));
      this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, i + 36 - 19, i2 + 24 - 1, false));
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String text = this.chatComponent.getUnformattedText();
      this.fontRendererObj.getInt(text, this.xSize / 2 - this.fontRendererObj.getInt3(text) / 2, 6, 4210752);
      this.fontRendererObj.getInt(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList recipes = this.merchant.getRecipes(this.mc.thePlayer);
      if (recipes != null && !recipes.isEmpty()) {
         int selectedMerchantRecipe2 = this.selectedMerchantRecipe;
         if (selectedMerchantRecipe2 < 0 || selectedMerchantRecipe2 >= recipes.size()) {
            return;
         }

         if (recipes.get(selectedMerchantRecipe2).isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
      if (merchantRecipeList != null && !merchantRecipeList.isEmpty()) {
         int i = (this.width - this.xSize) / 2;
         int i2 = (this.height - this.ySize) / 2;
         int selectedMerchantRecipe2 = this.selectedMerchantRecipe;
         MerchantRecipe merchantRecipe = merchantRecipeList.get(selectedMerchantRecipe2);
         ItemStack itemStack = merchantRecipe.getItemToBuy();
         ItemStack itemStack2 = merchantRecipe.getSecondItemToBuy();
         ItemStack itemStack3 = merchantRecipe.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(itemStack, i + 36, i2 + 24);
         this.itemRender.handleFontRenderer(this.fontRendererObj, itemStack, i + 36, i2 + 24);
         if (itemStack2 != null) {
            this.itemRender.renderItemAndEffectIntoGUI(itemStack2, i + 62, i2 + 24);
            this.itemRender.handleFontRenderer(this.fontRendererObj, itemStack2, i + 62, i2 + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(itemStack3, i + 120, i2 + 24);
         this.itemRender.handleFontRenderer(this.fontRendererObj, itemStack3, i + 120, i2 + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && itemStack != null) {
            this.renderToolTip(itemStack, mouseX, mouseY);
         } else if (itemStack2 != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY) && itemStack2 != null) {
            this.renderToolTip(itemStack2, mouseX, mouseY);
         } else if (itemStack3 != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY) && itemStack3 != null) {
            this.renderToolTip(itemStack3, mouseX, mouseY);
         } else if (merchantRecipe.isRecipeDisabled()
            && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY))) {
            this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated"), mouseX, mouseY);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      boolean conditionMet = false;
      if (button == this.nextButton) {
         this.selectedMerchantRecipe++;
         MerchantRecipeList merchantRecipeList = this.merchant.getRecipes(this.mc.thePlayer);
         if (merchantRecipeList != null && this.selectedMerchantRecipe >= merchantRecipeList.size()) {
            this.selectedMerchantRecipe = merchantRecipeList.size() - 1;
         }

         conditionMet = true;
      } else if (button == this.previousButton) {
         this.selectedMerchantRecipe--;
         if (this.selectedMerchantRecipe < 0) {
            this.selectedMerchantRecipe = 0;
         }

         conditionMet = true;
      }

      if (conditionMet) {
         ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.selectedMerchantRecipe);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", packetBuffer));
      }
   }

   public GuiMerchant(InventoryPlayer inventoryPlayer, IMerchant iMerchant, World worldIn) {
      super(new ContainerMerchant(inventoryPlayer, iMerchant, worldIn));
      this.merchant = iMerchant;
      this.chatComponent = iMerchant.getDisplayName();
   }

   static class MerchantButton extends GuiButton {
      private final boolean isNextButton;

      @Override
      public void drawButton(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            mc.getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            int i = 0;
            int i2 = 176;
            if (!this.enabled) {
               i2 = 176 + this.width * 2;
            } else if (flag) {
               i2 = 176 + this.width;
            }

            if (!this.isNextButton) {
               i = 0 + this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, i2, i, this.width, this.height);
         }
      }

      public MerchantButton(int buttonID, int x, int y, boolean isNextButtonIn) {
         super(buttonID, x, y, 12, 19, "");
         this.isNextButton = isNextButtonIn;
      }
   }
}
