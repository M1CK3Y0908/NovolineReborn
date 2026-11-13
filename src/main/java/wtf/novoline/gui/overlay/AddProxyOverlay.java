package wtf.novoline.gui.overlay;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.NovolineButton;
import wtf.novoline.gui.ProxyManagerScreen;
import wtf.novoline.gui.TextSelection;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.proxy.ProxyEntry;
import wtf.novoline.utils.proxy.ProxyEntry.Authentication;
import wtf.novoline.utils.proxy.ProxyEntry.Socks4Type;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class AddProxyOverlay extends Overlay {
   private final BlurRenderer overlayBlur = new BlurRenderer(Novoline.framebufferManager);
   private NovolineButton novolineButton;
   private TextSelection passwordField;
   private TextSelection usernameField;
   private TextSelection comboField;
   private Socks4Type socks4Type = Socks4Type.SOCKS_5;

   @Override
   public void keyTyped(char c, int i) {
      this.comboField.keyTyped(c, i);
      this.usernameField.keyTyped(c, i);
      this.passwordField.keyTyped(c, i);
      super.keyTyped(c, i);
   }

   private String getTypeLabel(Socks4Type socks4Type2) {
      return Language.translate("proxy_manager.gui.add_proxy.proxy_type", socks4Type2.name().replace("_", " "));
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
      this.overlayBlur.blur("add-proxy-overlay", this.minecraft.getFramebuffer(), scaledResolution, 10, 0.6F, 1);
      this.minecraft.getFramebuffer().bindFramebuffer(true);
      this.overlayBlur.drawResult(scaledResolution);
      Fonts.rubikSemiboldSize24
         .drawCenteredStringWithShadow(
            Language.translate("proxy_manager.gui.add_proxy.title"), this.guiScreen.width / 2.0F, this.guiScreen.height / 2.0F - 85.0F, -1
         );
      Fonts.rubikRegularSize14.drawString(Language.translate("proxy_manager.gui.add_proxy.combo_hint"), this.comboField.getX(), this.comboField.getY() - 8, -1);
      this.comboField.draw(f);
      Fonts.rubikRegularSize14
         .drawString(Language.translate("proxy_manager.gui.add_proxy.username_hint"), this.usernameField.getX(), this.usernameField.getY() - 8, -1);
      this.usernameField.draw(f);
      Fonts.rubikRegularSize14
         .drawString(Language.translate("proxy_manager.gui.add_proxy.password_hint"), this.passwordField.getX(), this.passwordField.getY() - 8, -1);
      this.passwordField.draw(f);
      RenderUtils.setColor(-1);
      super.drawScreen(i, i2, f);
      this.overlayBlur.releaseResult();
   }

   @Override
   public void init() {
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.overlayBlur,
               -1,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 55,
               Language.translate("proxy_manager.gui.add_proxy.add_button")
            )
         );
      this.guiScreen
         .buttonList
         .add(
            this.novolineButton = new NovolineButton(
               this.overlayBlur, -2, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 80, this.getTypeLabel(this.socks4Type)
            )
         );
      this.comboField = new TextSelection(Fonts.rubikRegularSize16, this.overlayBlur, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 - 40);
      this.usernameField = new TextSelection(Fonts.rubikRegularSize16, this.overlayBlur, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 - 5);
      this.passwordField = new TextSelection(Fonts.rubikRegularSize16, this.overlayBlur, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 30)
         .withMask("*", (textSelection) -> !((TextSelection) textSelection).isFocused());
   }

   public AddProxyOverlay(GuiScreen guiScreen2) {
      super(guiScreen2);
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      switch (guiButton.id) {
         case -2:
            Socks4Type[] socks4Types = Socks4Type.values();
            int i = this.socks4Type.ordinal();
            if (++i == socks4Types.length) {
               i = 0;
            }

            this.socks4Type = socks4Types[i];
            this.novolineButton.displayString = this.getTypeLabel(this.socks4Type);
            break;
         case -1:
            String text = this.usernameField.getTextBuffer().getText();
            String text2 = this.passwordField.getTextBuffer().getText();
            if (text.isEmpty() && !text2.isEmpty() || !text.isEmpty() && text2.isEmpty()) {
               this.showNotification(
                  NotificationType.ERROR,
                  Language.translate("proxy_manager.gui.add_proxy.auth_fields_not_filled.notification.title"),
                  Language.translate("proxy_manager.gui.add_proxy.auth_fields_not_filled.notification.body"),
                  3500
               );
               return;
            }

            String text3 = this.comboField.getTextBuffer().getText();
            String[] texts = text3.split(":");
            if (!text3.contains(":")) {
               this.showNotification(
                  NotificationType.ERROR,
                  Language.translate("proxy_manager.gui.add_proxy.invalid_combo.notification.title"),
                  Language.translate("proxy_manager.gui.add_proxy.invalid_combo.notification.body"),
                  3500
               );
               return;
            }

            Authentication authentication2 = null;
            if (!text.isEmpty()) {
               authentication2 = Authentication.createAuthentication(text, text2);
            }

            ProxyEntry proxyEntry = ProxyEntry.builder().address(texts[0]).port(Integer.parseInt(texts[1])).authentication(authentication2).proxyType(this.socks4Type).build();
            if (this.guiScreen instanceof ProxyManagerScreen proxyManagerScreen) {
               proxyManagerScreen.getProxyList().add(proxyEntry);
               proxyManagerScreen.getProxyListPanel().addEntry(proxyEntry);
               proxyManagerScreen.getProxyListPanel().refresh();
               proxyManagerScreen.closeOverlay();
            }
      }
   }

   @Override
   public void mouseClicked(int i, int i2, int i3) {
      this.comboField.updateFocus(i, i2);
      this.usernameField.updateFocus(i, i2);
      this.passwordField.updateFocus(i, i2);
      super.mouseClicked(i, i2, i3);
   }
}
