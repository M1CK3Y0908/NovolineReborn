package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.utils.render.RenderUtils;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {
   private static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/widgets.png");
   private long lastSelectionTime;
   private SpectatorMenu menu;
   public static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");
   private final Minecraft mc;

   public void onMiddleClick() {
      this.lastSelectionTime = Minecraft.getSystemTime();
      if (this.isMenuActive()) {
         int selectedSlot = this.menu.getSelectedSlot();
         if (selectedSlot != -1) {
            this.menu.selectSlot(selectedSlot);
         }
      } else {
         this.menu = new SpectatorMenu(this);
      }
   }

   public void renderSelectedItem(ScaledResolution resolution) {
      int i = (int)(this.getHotbarAlpha() * 255.0F);
      if (i > 3 && this.menu != null) {
         ISpectatorMenuObject spectatorMenuObject = this.menu.getSelectedItem();
         String text = spectatorMenuObject != SpectatorMenu.EMPTY_SLOT
            ? spectatorMenuObject.getSpectatorName().getFormattedText()
            : this.menu.getCurrentGroup().getPrompt().getFormattedText();
         if (text != null) {
            int i2 = (resolution.getScaledWidth() - this.mc.fontRendererObj.getInt3(text)) / 2;
            int i3 = resolution.getScaledHeight() - 35;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            this.mc.fontRendererObj.drawStringWithShadow(text, i2, i3, 16777215 + (i << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public void onSpectatorMenuClosed(SpectatorMenu menuIn) {
      this.menu = null;
      this.lastSelectionTime = 0L;
   }

   public boolean isMenuActive() {
      return this.menu != null;
   }

   public void onHotbarSelected(int slot) {
      this.lastSelectionTime = Minecraft.getSystemTime();
      if (this.menu != null) {
         this.menu.selectSlot(slot);
      } else {
         this.menu = new SpectatorMenu(this);
      }
   }

   public void scroll(int direction) {
      int i = this.menu.getSelectedSlot() + direction;

      while (i >= 0 && i <= 8 && (this.menu.getItem(i) == SpectatorMenu.EMPTY_SLOT || !this.menu.getItem(i).isEnabled())) {
         i += direction;
      }

      if (i >= 0 && i <= 8) {
         this.menu.selectSlot(i);
         this.lastSelectionTime = Minecraft.getSystemTime();
      }
   }

   private void renderSlot(int slot, int x, float y, float alpha, ISpectatorMenuObject menuObject) {
      this.mc.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
      if (menuObject != SpectatorMenu.EMPTY_SLOT) {
         int i = (int)(alpha * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(x, y, 0.0F);
         float f = menuObject.isEnabled() ? 1.0F : 0.25F;
         GlStateManager.color(f, f, f, alpha);
         menuObject.renderIcon(f, i);
         GlStateManager.popMatrix();
         String text = String.valueOf(GameSettings.getKeyDisplayString(this.mc.gameSettings.keyBindsHotbar[slot].getKeyCode()));
         if (i > 3 && menuObject.isEnabled()) {
            this.mc
               .fontRendererObj
               .drawStringWithShadow(text, x + 19 - 2 - this.mc.fontRendererObj.getInt3(text), y + 6.0F + 3.0F, 16777215 + (i << 24));
         }
      }
   }

   public GuiSpectator(Minecraft mcIn) {
      this.mc = mcIn;
   }

   private float getHotbarAlpha() {
      long l = this.lastSelectionTime - Minecraft.getSystemTime() + 5000L;
      return MathHelper.clamp_float((float)l / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(ScaledResolution resolution, float partialTicks) {
      if (this.menu != null) {
         float hotbarAlpha = this.getHotbarAlpha();
         if (hotbarAlpha <= 0.0F) {
            this.menu.exit();
         } else {
            int i = resolution.getScaledWidth() / 2;
            float zLevel2 = this.zLevel;
            this.zLevel = -90.0F;
            float f = resolution.getScaledHeight() - 22.0F * hotbarAlpha;
            SpectatorDetails spectatorDetails = this.menu.getCurrentDetails();
            this.renderPage(resolution, hotbarAlpha, i, f, spectatorDetails);
            this.zLevel = zLevel2;
         }
      }
   }

   protected void renderPage(ScaledResolution resolution, float alpha, int centerX, float y, SpectatorDetails details) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
      this.mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
      this.drawTexturedModalRect(centerX - 91, y, 0, 0, 182, 22);
      if (details.getSelectedSlot() >= 0) {
         this.drawTexturedModalRect(centerX - 91 - 1 + details.getSelectedSlot() * 20, y - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.enableGUIStandardItemLighting();

      for (int index = 0; index < 9; index++) {
         this.renderSlot(index, resolution.getScaledWidth() / 2 - 90 + index * 20 + 2, y + 3.0F, alpha, details.getObject(index));
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
   }
}
