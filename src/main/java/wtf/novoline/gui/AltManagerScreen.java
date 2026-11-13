package wtf.novoline.gui;

import com.thealtening.api.TheAltening;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.controls.AccountActionScreen;
import wtf.novoline.controls.DirectLoginScreen;
import wtf.novoline.gui.overlay.AlteningTokenOverlay;
import wtf.novoline.gui.overlay.Overlay;
import wtf.novoline.utils.account.AlteningAccount;
import wtf.novoline.utils.account.MojangAccount;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.MenuBackgroundShader;
import wtf.novoline.utils.render.RenderUtils;

public class AltManagerScreen extends GuiScreen {
   private final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);
   private Overlay overlay;
   private final AccountListPanel accountListPanel = new AccountListPanel(this, this.blurRenderer);
   private final GuiScreen guiScreen;
   private MenuBackgroundShader menuBackgroundShader;

   private float getPanelX() {
      return this.width / 2.0F - this.accountListPanel.getWidth() / 2.0F;
   }

   private void showNotification(NotificationType notificationType, String text, String text2) {
      Novoline.getNovoline().getNotificationManager().show(new Notification(notificationType, text, text2, 5000L));
   }

   public AccountListPanel getAccountListPanel() {
      return this.accountListPanel;
   }

   public BlurRenderer getBlurRenderer() {
      return this.blurRenderer;
   }

   public AltManagerScreen() {
      this(null);
   }

   @Override
   protected void actionPerformed(GuiButton guiButton2) throws IOException {
      if (this.overlay != null) {
         this.overlay.actionPerformed(guiButton2);
      } else {
         switch (guiButton2.id) {
            case 0:
               if (this.accountListPanel.getSelectedItem() != null) {
                  this.accountListPanel.getSelectedItem().onDoubleClick();
               }
               break;
            case 1:
               this.displayOverlay(new AccountActionScreen(this));
               break;
            case 2:
               AccountEntry selectedItem = this.accountListPanel.getSelectedItem();
               if (selectedItem != null && this.accountListPanel.remove(selectedItem) && this.accountListPanel.getAccountManager().remove(selectedItem.getAccount())) {
                  this.accountListPanel.selectNeighbour();
               }
               break;
            case 3:
               this.displayOverlay(new DirectLoginScreen(this));
               break;
            case 4:
               this.showNotification(
                  NotificationType.INFO,
                  Language.translate("account_manager.generating_account.notification.title"),
                  Language.translate("account_manager.generating_account.notification.body")
               );
               String alteningToken = this.accountListPanel.getAccountManager().getAlteningToken();
               if (alteningToken == null) {
                  this.showNotification(
                     NotificationType.ERROR,
                     Language.translate("account_manager.thealtening.invalid_token.notification.title"),
                     Language.translate("account_manager.thealtening.invalid_token.notification.body")
                  );
                  return;
               }

               new Thread(
                     () -> TheAltening.newAsyncRetriever(alteningToken)
                        .getAccountDataAsync()
                        .exceptionally(
                           throwable -> {
                              this.showNotification(
                                 NotificationType.ERROR,
                                 Language.translate("account_manager.thealtening.unable_to_generate.notification.title"),
                                 Language.translate("account_manager.thealtening.unable_to_generate.notification.body")
                              );
                              return null;
                           }
                        )
                        .thenAccept(
                           account2 -> {
                              AlteningAccount alteningAccount = new AlteningAccount(account2.getToken());
                              this.accountListPanel.addEntry(alteningAccount);
                              this.accountListPanel.getAccountManager().add(alteningAccount);
                              this.accountListPanel.saveAccounts();
                              this.accountListPanel.updateLayout();
                              this.showNotification(
                                 NotificationType.SUCCESS,
                                 Language.translate("account_manager.thealtening.generated.notification.title"),
                                 Language.translate("account_manager.thealtening.generated.notification.body", alteningAccount.toString())
                              );
                           }
                        ),
                     "Altening-Generate-Thread"
                  )
                  .start();
               break;
            case 5:
               this.displayOverlay(new AlteningTokenOverlay(this));
         }
      }
   }

   public void closeOverlay() {
      this.overlay.onClose();
      this.overlay = null;
      this.buttonList.removeIf(guiButton -> guiButton.id < 0);
      this.buttonList.forEach(guiButton -> guiButton.enabled = true);
   }

   public void displayOverlay(Overlay overlay2) {
      this.overlay = overlay2;
      this.buttonList.forEach(guiButton -> guiButton.enabled = false);
      overlay2.init();
   }

   @Override
   public void startClosing() {
      try {
         this.accountListPanel.getAccountManager().save();
      } catch (Throwable throwable2) {
         Novoline.taggedLogger.error(throwable2, "Couldn't save accounts");
      }

      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      int i = (int)this.getPanelX();
      int i2 = 40 + this.accountListPanel.getHeight() + 5;
      int i3 = this.accountListPanel.getWidth() / 5 - 2;
      this.buttonList.add(new NovolineButton(this.blurRenderer, 0, i, i2, i3, 20, Language.translate("account_manager.gui.log_in.button")));
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 1, i + i3 + 2, i2, i3, 20, Language.translate("account_manager.gui.add_alt.button")));
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 2, i + i3 * 2 + 6, i2, i3, 20, Language.translate("account_manager.gui.remove_alt.button")));
      this.buttonList
         .add(
            new NovolineButton(this.blurRenderer, 3, i + i3 * 3 + 8, i2, i3, 20, Language.translate("account_manager.gui.direct_login.button"))
         );
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 4, i + i3 * 4 + 10, i2, i3, 20, Language.translate("account_manager.gui.generate.button")));
      this.buttonList
         .add(
            new NovolineButton(
               this.blurRenderer, 5, i + this.accountListPanel.getWidth() - 100, 15, 100, 20, Language.translate("account_manager.gui.thealtening.button")
            )
         );
      this.accountListPanel.updateLayout();
      this.menuBackgroundShader = new MenuBackgroundShader();
      if (this.overlay != null) {
         this.buttonList.forEach(guiButton -> guiButton.enabled = false);
         this.overlay.init();
      }

      Keyboard.enableRepeatEvents(true);
      super.initGui();
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      if (this.overlay != null) {
         this.overlay.mouseClicked(i, i2, i3);
      } else {
         this.accountListPanel.mouseClicked(i, i2, i3);
      }

      super.mouseClicked(i, i2, i3);
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      GlStateManager.disableBlend();
      GlStateManager.disableAlpha();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      this.menuBackgroundShader.draw(scaledResolution);
      this.blurRenderer.blur("acc-manager-blur", this.mc.getFramebuffer(), scaledResolution, 10, 0.6F, 2);
      this.mc.getFramebuffer().bindFramebuffer(true);
      Fonts.rubikSemiboldSize24.drawStringWithShadow(Language.translate("account_manager.gui.header"), this.getPanelX(), 20.0, -1);
      this.accountListPanel.draw(i2, f);
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
   protected void keyTyped(char c, int i) throws IOException {
      if (this.overlay != null) {
         if (i == 1) {
            this.closeOverlay();
         } else {
            this.overlay.keyTyped(c, i);
         }
      } else {
         if (i == 211) {
            this.actionPerformed(this.buttonList.get(2));
         } else if (i == 1) {
            if (this.guiScreen != null) {
               this.mc.displayGuiScreen(this.guiScreen);
               this.startClosing();
               return;
            }
         } else if (i == 47 && isCtrlKeyDown()) {
            try {
               Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
               Arrays.stream(((String)systemClipboard.getContents(this).getTransferData(DataFlavor.stringFlavor)).split("\n"))
                  .filter(string -> string.contains(":"))
                  .map(string -> string.split(":"))
                  .map(stringArray -> new MojangAccount(stringArray[0], stringArray[1]))
                  .filter(mojangAccount -> !this.accountListPanel.getAccountManager().contains(mojangAccount))
                  .distinct()
                  .forEach(mojangAccount -> {
                     this.accountListPanel.addEntry(mojangAccount);
                     this.accountListPanel.getAccountManager().add(mojangAccount);
                  });
               this.accountListPanel.refresh();
            } catch (IOException | UnsupportedFlavorException exception) {
               Novoline.taggedLogger.error(exception, "Couldn't get data from clipboard");
            }
         } else {
            this.accountListPanel.keyTyped(c, i);
         }

         super.keyTyped(c, i);
      }
   }

   public AltManagerScreen(GuiScreen guiScreen2) {
      this.guiScreen = guiScreen2;
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      if (this.overlay != null) {
         this.overlay.mouseReleased();
      } else {
         this.accountListPanel.stopScrollbarDrag();
      }

      super.mouseReleased(i, i2, i3);
   }
}
