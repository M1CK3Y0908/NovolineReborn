package wtf.novoline.gui.overlay;

import java.util.ArrayList;
import java.util.List;
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
import wtf.novoline.utils.render.RenderUtils;

public class AddProxyListOverlay extends Overlay {
   private NovolineButton novolineButton;
   private TextSelection usernameField;
   private TextSelection passwordField;
   private Socks4Type socks4Type = Socks4Type.SOCKS_5;
   private final List<String[]> proxies;

   @Override
   public void mouseClicked(int i, int i2, int i3) {
      this.usernameField.updateFocus(i, i2);
      this.passwordField.updateFocus(i, i2);
      super.mouseClicked(i, i2, i3);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
      this.blurRenderer.blur("add-proxy-overlay", this.minecraft.getFramebuffer(), scaledResolution, 10, 0.6F, 1);
      this.minecraft.getFramebuffer().bindFramebuffer(true);
      this.blurRenderer.drawResult(scaledResolution);
      Fonts.rubikSemiboldSize24
         .drawCenteredStringWithShadow(
            Language.translate("proxy_manager.gui.add_proxy_list.title"), this.guiScreen.width / 2.0F, this.guiScreen.height / 2.0F - 67.0F, -1
         );
      Fonts.rubikRegularSize16
         .drawCenteredStringWithShadow(
            Language.translate("proxy_manager.gui.add_proxy_list.subtitle", this.proxies.size()),
            this.guiScreen.width / 2.0F,
            this.guiScreen.height / 2.0F - 47.0F,
            -1
         );
      Fonts.rubikRegularSize14
         .drawString(Language.translate("proxy_manager.gui.add_proxy.username_hint"), this.usernameField.getX(), this.usernameField.getY() - 7, -1);
      this.usernameField.draw(f);
      Fonts.rubikRegularSize14
         .drawString(Language.translate("proxy_manager.gui.add_proxy.password_hint"), this.passwordField.getX(), this.passwordField.getY() - 7, -1);
      this.passwordField.draw(f);
      RenderUtils.setColor(-1);
      super.drawScreen(i, i2, f);
      this.blurRenderer.releaseResult();
   }

   @Override
   public void init() {
      this.usernameField = new TextSelection(Fonts.rubikRegularSize16, this.blurRenderer, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 - 21);
      this.passwordField = new TextSelection(Fonts.rubikRegularSize16, this.blurRenderer, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 11);
      this.guiScreen
         .buttonList
         .add(
            this.novolineButton = new NovolineButton(
               this.blurRenderer, -1, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 40, this.getTypeButtonText(this.socks4Type)
            )
         );
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.blurRenderer,
               -2,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 65,
               Language.translate("proxy_manager.gui.add_proxy_list.import_button")
            )
         );
      super.init();
   }

   private String getTypeButtonText(Socks4Type socks4Type2) {
      return Language.translate("proxy_manager.gui.add_proxy_list.proxy_type_button", socks4Type2.name().replace("_", " "));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      switch (guiButton.id) {
         case -2:
            ArrayList arrayList = new ArrayList();
            Authentication authentication2 = null;
            String text = this.usernameField.getTextBuffer().getText();
            String text2 = this.passwordField.getTextBuffer().getText();
            if (!text.isEmpty() && !text2.isEmpty()) {
               authentication2 = Authentication.createAuthentication(text, text2);
            }

            for (String[] texts : this.proxies) {
               try {
                  arrayList.add(ProxyEntry.builder().proxyType(this.socks4Type).address(texts[0]).port(Integer.parseInt(texts[1])).authentication(authentication2).build());
               } catch (NumberFormatException numberFormatException) {
                  Novoline.taggedLogger.error(numberFormatException, "Failed to parse proxy {}:{}", texts.length);
               }
            }

            if (arrayList.size() > 0) {
               this.showNotification(
                  NotificationType.SUCCESS,
                  Language.translate("proxy_manager.gui.add_proxy_list.added.notification.title"),
                  Language.translate("proxy_manager.gui.add_proxy_list.added.notification.body", arrayList.size()),
                  3000
               );
            }

            if (this.guiScreen instanceof ProxyManagerScreen proxyManagerScreen) {
               arrayList.forEach(proxyEntry -> {
                  proxyManagerScreen.getProxyListPanel().addEntry((ProxyEntry) proxyEntry);
                  proxyManagerScreen.getProxyList().add((ProxyEntry) proxyEntry);
               });
               proxyManagerScreen.getProxyListPanel().refresh();
               proxyManagerScreen.closeOverlay();
            }
            break;
         case -1:
            Socks4Type[] socks4Types = Socks4Type.values();
            int i = this.socks4Type.ordinal();
            if (++i == socks4Types.length) {
               i = 0;
            }

            this.socks4Type = socks4Types[i];
            this.novolineButton.displayString = this.getTypeButtonText(this.socks4Type);
      }
   }

   public AddProxyListOverlay(GuiScreen guiScreen2, List list) {
      super(guiScreen2);
      this.proxies = list;
   }

   @Override
   public void keyTyped(char c, int i) {
      this.usernameField.keyTyped(c, i);
      this.passwordField.keyTyped(c, i);
      super.keyTyped(c, i);
   }
}
