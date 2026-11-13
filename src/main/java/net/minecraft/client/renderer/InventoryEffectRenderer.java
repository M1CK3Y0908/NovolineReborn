package net.minecraft.client.renderer;

import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer {
   private boolean hasActivePotionEffects;

   @Override
   public void initGui() {
      super.initGui();
      this.updateActivePotionEffects();
   }

   public InventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }
   }

   private void drawActivePotionEffects() {
      int i = this.guiLeft - 124;
      int guiTop2 = this.guiTop;
      Collection activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
      if (!activePotionEffects.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int i2 = 33;
         if (activePotionEffects.size() > 5) {
            i2 = 132 / (activePotionEffects.size() - 1);
         }

         for (PotionEffect potionEffect : this.mc.thePlayer.getActivePotionEffects()) {
            Potion potion2 = Potion.potionTypes[potionEffect.getPotionID()];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(inventoryBackground);
            this.drawTexturedModalRect(i, guiTop2, 0, 166, 140, 32);
            if (potion2.hasStatusIcon()) {
               int statusIconIndex = potion2.getStatusIconIndex();
               this.drawTexturedModalRect(i + 6, guiTop2 + 7, 0 + statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
            }

            String text = I18n.format(potion2.getName());
            if (potionEffect.getAmplifier() == 1) {
               text = text + " " + I18n.format("enchantment.level.2");
            } else if (potionEffect.getAmplifier() == 2) {
               text = text + " " + I18n.format("enchantment.level.3");
            } else if (potionEffect.getAmplifier() == 3) {
               text = text + " " + I18n.format("enchantment.level.4");
            }

            this.fontRendererObj.drawStringWithShadow(text, i + 10 + 18, guiTop2 + 6, 16777215);
            String durationString = Potion.getDurationString(potionEffect);
            this.fontRendererObj.drawStringWithShadow(durationString, i + 10 + 18, guiTop2 + 6 + 10, 8355711);
            guiTop2 += i2;
         }
      }
   }

   protected void updateActivePotionEffects() {
      if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
         this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
         this.hasActivePotionEffects = true;
      } else {
         this.guiLeft = (this.width - this.xSize) / 2;
         this.hasActivePotionEffects = false;
      }
   }
}
