package wtf.novoline.gui;

import net.minecraft.client.gui.GuiScreen;
import wtf.novoline.Novoline;
import wtf.novoline.utils.account.Account;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.render.BlurRenderer;

public class AccountListPanel extends OverlayPanel<AccountListPanel, AccountEntry> {
   private final AltManagerScreen altManagerScreen = Novoline.getNovoline().getAltManagerScreen();
   private final AccountManager accountManager = Novoline.getNovoline().getAccountManager();

   public AltManagerScreen getAltManagerScreen() {
      return this.altManagerScreen;
   }

   public void saveAccounts() {
      try {
         this.accountManager.save();
      } catch (Throwable throwable) {
         Novoline.taggedLogger.error(throwable, "Couldn't save accounts");
      }
   }

   @Override
   public int getWidth() {
      return Math.min(this.guiScreen.width - 30, 400);
   }

   @Override
   public int getHeight() {
      return this.guiScreen.height - 30 - 50;
   }

   public void addEntry(Account account2) {
      this.add(new AccountEntry(this, account2, this.size()));
   }

   public AccountListPanel(GuiScreen guiScreen2, BlurRenderer blurRenderer) {
      super(guiScreen2, blurRenderer);
      this.accountManager.forEach(this::addEntry);
      int selectedIndex = this.accountManager.getSelectedIndex();
      if (selectedIndex != -1 && selectedIndex < this.size()) {
         this.get(selectedIndex).setCurrent(true);
      }
   }

   public AccountManager getAccountManager() {
      return this.accountManager;
   }
}
