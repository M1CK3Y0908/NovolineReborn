package wtf.novoline.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.OverlayPanel.Entry;
import wtf.novoline.gui.overlay.AddProxyListOverlay;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.proxy.ProxyEntry;
import wtf.novoline.utils.proxy.ProxyList;
import wtf.novoline.utils.proxy.ProxyEntry.Authentication;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class ProxyListPanel extends OverlayPanel<ProxyListPanel, ProxyListPanel.EntryChild> {
   private final ProxyList proxyList = Novoline.getNovoline().getProxyList();

   @Override
   public int getWidth() {
      return Math.min(this.guiScreen.width - 30, 400);
   }

   public ProxyListPanel(GuiScreen guiScreen2, BlurRenderer blurRenderer) {
      super(guiScreen2, blurRenderer);
      this.proxyList.forEach(this::addEntry);
      if (this.proxyList.isEnabled() && this.proxyList.getCurrent() != null) {
         this.stream().filter(entryChild -> entryChild.getProxyEntry().equals(this.proxyList.getCurrent())).findFirst().ifPresent(entryChild -> entryChild.setCurrent(true));
      }
   }

   public void addEntry(ProxyEntry proxyEntry2) {
      this.add(new ProxyListPanel.EntryChild(this, proxyEntry2, this.size()));
   }

   @Override
   public int getHeight() {
      return this.guiScreen.height - 30 - 50;
   }

   static GuiScreen access$000(ProxyListPanel proxyListPanel) {
      return proxyListPanel.guiScreen;
   }

   @Override
   public void keyTyped(char c, int i) {
      if (i == 47 && GuiScreen.isCtrlKeyDown()) {
         ArrayList arrayList = new ArrayList();

         for (String text : GuiScreen.getClipboardString().split("\n")) {
            if (text.contains(":")) {
               String[] texts = text.split(":");
               arrayList.add(texts);
            }
         }

         if (arrayList.size() > 0 && this.guiScreen instanceof ProxyManagerScreen proxyManagerScreen) {
            proxyManagerScreen.displayOverlay(new AddProxyListOverlay(this.guiScreen, arrayList));
         }
      }

      super.keyTyped(c, i);
   }

   public static final class EntryChild extends Entry<ProxyListPanel, ProxyListPanel.EntryChild> {
      private final ProxyEntry proxyEntry;

      @Override
      public void onDoubleClick() {
         this.overlayPanel.proxyList.setEnabled(true);
         this.overlayPanel.proxyList.setCurrent(this.proxyEntry);
         this.overlayPanel.forEach(entryChild -> entryChild.setCurrent(entryChild.equals(this)));
         ProxyListPanel.access$000(this.overlayPanel).initGui();
         Novoline.getNovoline()
            .getNotificationManager()
            .show(
               new Notification(
                  NotificationType.SUCCESS,
                  Language.translate("proxy_manager.gui.proxy_activated.notification.title"),
                  Language.translate("proxy_manager.gui.proxy_activated.notification.body", this.proxyEntry.getAddress(), this.proxyEntry.getPort()),
                  3000L
               )
            );
      }

      @Override
      public int getHeight() {
         return 30;
      }

      @Override
      public int getWidth() {
         return this.overlayPanel.getWidth();
      }

      public ProxyEntry getProxyEntry() {
         return this.proxyEntry;
      }

      public EntryChild(ProxyListPanel proxyListPanel, ProxyEntry proxyEntry2, int i) {
         super(proxyListPanel, i);
         this.proxyEntry = proxyEntry2;
      }

      @Override
      public void drawEntry(float f) {
         int i = this.selected ? 352321535 : 838860800;
         if (this.isCurrent()) {
            i = this.selected ? 1073807104 : 838926080;
         }

         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(this.x, this.y, this.getWidth(), this.getHeight(), 5.0F, i);
         filledPolygonRenderer.draw();
         Fonts.rubikSemiboldSize16.drawStringWithShadow(this.proxyEntry.getProxyType().name().replace("_", " "), this.x + 5, this.y + 8, -1);
         Fonts.rubikRegularSize16.drawStringWithShadow(this.proxyEntry.getAddress() + ":" + this.proxyEntry.getPort(), this.x + 5, this.y + 18, -1);
         Authentication authentication = this.proxyEntry.getAuthentication();
         if (authentication != null) {
            String text = Language.translate("proxy_manager.gui.list.username", authentication.getUsername());
            Fonts.rubikRegularSize16.drawString(text, this.x + this.getWidth() - Fonts.rubikRegularSize16.getStringWidth(text) - 5, this.y + 8, -1);
            text = Language.translate("proxy_manager.gui.list.password", "*".repeat(authentication.getPassword().length()));
            Fonts.rubikRegularSize16.drawString(text, this.x + this.getWidth() - Fonts.rubikRegularSize16.getStringWidth(text) - 5, this.y + 18, -1);
         }
      }
   }
}
