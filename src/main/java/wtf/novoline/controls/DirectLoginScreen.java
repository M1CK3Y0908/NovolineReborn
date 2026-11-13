package wtf.novoline.controls;

import net.minecraft.client.gui.GuiScreen;
import wtf.novoline.enums.AccountType;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.overlay.AccountCredentialsOverlay;
import wtf.novoline.utils.account.LoginHandler;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;

public class DirectLoginScreen extends AccountCredentialsOverlay {
   @Override
   public void firstButtonClicked() {
      if (this.guiScreen instanceof AltManagerScreen altManagerScreen) {
         String text = this.textSelection.getTextBuffer().getText();
         String text2 = this.textSelection2.getTextBuffer().getText();
         if (text.trim().isEmpty()) {
            this.showNotification(
               NotificationType.ERROR,
               Language.translate("account_manager.login_handling.notification.title"),
               Language.translate("account_manager.notification.login.import_error.body"),
               5000
            );
            return;
         }

         AccountType accountType = this.getAccountType(text, text2);
         this.accountManager.createAccount(text, text2, accountType).getCompletableFuture().exceptionally(throwable -> {
            LoginHandler.reportLoginFailure(throwable);
            return null;
         }).thenAccept(session -> {
            if (session != null) {
               LoginHandler.onLoggedIn(session);
               altManagerScreen.closeOverlay();
            }
         });
      }
   }

   public DirectLoginScreen(GuiScreen guiScreen2) {
      super(guiScreen2, Language.translate("account_manager.gui.direct_login.title"), Language.translate("account_manager.gui.direct_login.login"));
   }
}
