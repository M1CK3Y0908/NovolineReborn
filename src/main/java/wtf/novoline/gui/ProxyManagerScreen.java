package wtf.novoline.gui;

import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.ProxyListPanel.EntryChild;
import wtf.novoline.gui.overlay.AddProxyOverlay;
import wtf.novoline.gui.overlay.Overlay;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.proxy.ProxyList;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.MenuBackgroundShader;
import wtf.novoline.utils.render.RenderUtils;

public class ProxyManagerScreen extends GuiScreen {
   private final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);
   private final ProxyListPanel proxyListPanel = new ProxyListPanel(this, this.blurRenderer);
   private final ProxyList proxyList = Novoline.getNovoline().getProxyList();
   private final GuiScreen guiScreen;
   private MenuBackgroundShader menuBackgroundShader;
   private Overlay overlay;

   @Override
   protected void actionPerformed(GuiButton guiButton2) throws IOException {
      if (this.overlay != null) {
         this.overlay.actionPerformed(guiButton2);
      } else {
         switch (guiButton2.id) {
            case 0:
               this.displayOverlay(new AddProxyOverlay(this));
               break;
            case 1:
               EntryChild selectedItem = this.proxyListPanel.getSelectedItem();
               if (selectedItem != null && this.proxyListPanel.remove(selectedItem) && this.proxyList.remove(selectedItem.getProxyEntry())) {
                  this.proxyList.setEnabled(false);
                  this.proxyListPanel.selectNeighbour();
                  this.initGui();
               }
               break;
            case 2:
               if (this.proxyListPanel.size() > 0) {
                  int i = ThreadLocalRandom.current().nextInt(this.proxyListPanel.size());
                  this.proxyListPanel.setEntry(this.proxyListPanel.get(i));
                  this.proxyListPanel.getSelectedItem().onDoubleClick();
               }
               break;
            case 3:
               this.proxyList.setEnabled(!this.proxyList.isEnabled());
               if (!this.proxyList.isEnabled()) {
                  this.proxyListPanel.forEach(entryChild -> entryChild.setCurrent(false));
                  this.proxyList.setCurrent(null);
               }

               this.initGui();
         }
      }
   }

   public ProxyListPanel getProxyListPanel() {
      return this.proxyListPanel;
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      if (this.overlay != null) {
         this.overlay.mouseReleased();
      } else {
         this.proxyListPanel.stopScrollbarDrag();
      }

      super.mouseReleased(i, i2, i3);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      int i = (int)this.getPanelX();
      int i2 = 40 + this.proxyListPanel.getHeight() + 5;
      int i3 = (this.proxyListPanel.getWidth() - 6) / 4;
      this.buttonList.add(new NovolineButton(this.blurRenderer, 0, i, i2, i3, 20, Language.translate("proxy_manager.gui.add_proxy")));
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 1, i + i3 + 2, i2, i3, 20, Language.translate("proxy_manager.gui.remove_proxy")));
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 2, i + i3 * 2 + 4, i2, i3, 20, Language.translate("proxy_manager.gui.random_proxy")));
      this.buttonList
         .add(
            new NovolineButton(
               this.blurRenderer,
               3,
               i + i3 * 3 + 6,
               i2,
               i3 + 2,
               20,
               this.proxyList.isEnabled()
                  ? Language.translate("proxy_manager.gui.proxy_state.enabled")
                  : Language.translate("proxy_manager.gui.proxy_state.disabled")
            )
         );
      this.proxyListPanel.updateLayout();
      this.menuBackgroundShader = new MenuBackgroundShader();
      if (this.overlay != null) {
         this.buttonList.forEach(guiButton -> guiButton.enabled = false);
         this.overlay.init();
      }

      Keyboard.enableRepeatEvents(true);
      super.initGui();
   }

   public void closeOverlay() {
      this.overlay.onClose();
      this.overlay = null;
      this.buttonList.removeIf(guiButton -> guiButton.id < 0);
      this.buttonList.forEach(guiButton -> guiButton.enabled = true);
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      GlStateManager.disableBlend();
      GlStateManager.disableAlpha();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      this.menuBackgroundShader.draw(scaledResolution);
      this.blurRenderer.blur("proxy-manager-blur", this.mc.getFramebuffer(), scaledResolution, 10, 0.6F, 2);
      this.mc.getFramebuffer().bindFramebuffer(true);
      Fonts.rubikSemiboldSize24.drawCenteredStringWithShadow(Language.translate("proxy_manager.gui.title"), this.width / 2.0F, 20.0F, -1);
      this.proxyListPanel.draw(i2, f);
      RenderUtils.setColor(-1);
      super.drawScreen(i, i2, f);
      if (this.overlay != null) {
         RenderUtils.drawRect(0.0F, 0.0F, this.width, this.height, new Color(255, 255, 255, 20).getRGB());
         this.overlay.drawScreen(i, i2, f);
      }

      this.blurRenderer.releaseResult();
      SmoothingUtils.updateDelta();
      Novoline.getNovoline().getNotificationManager().renderAll(scaledResolution);
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      if (this.overlay != null) {
         this.overlay.mouseClicked(i, i2, i3);
      } else {
         this.proxyListPanel.mouseClicked(i, i2, i3);
      }

      super.mouseClicked(i, i2, i3);
   }

   public BlurRenderer getBlurRenderer() {
      return this.blurRenderer;
   }

   public ProxyManagerScreen(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;
   }

   public void displayOverlay(Overlay overlay2) {
      this.overlay = overlay2;
      this.buttonList.forEach(guiButton -> guiButton.enabled = false);
      overlay2.init();
   }

   public ProxyList getProxyList() {
      return this.proxyList;
   }

   @Override
   protected void keyTyped(char c, int i) throws IOException {
      if (this.overlay != null) {
         if (i == 1) {
            this.closeOverlay();
         } else {
            this.overlay.keyTyped(c, i);
         }
      } else {
         if (i == 211) {
            this.actionPerformed(this.buttonList.get(1));
         } else {
            if (i == 1) {
               this.mc.displayGuiScreen(this.guiScreen);
               this.startClosing();
               return;
            }

            this.proxyListPanel.keyTyped(c, i);
         }

         super.keyTyped(c, i);
      }
   }

   private float getPanelX() {
      return this.width / 2.0F - this.proxyListPanel.getWidth() / 2.0F;
   }
}
