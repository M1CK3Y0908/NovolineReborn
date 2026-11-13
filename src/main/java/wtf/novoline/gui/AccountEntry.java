package wtf.novoline.gui;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.AccountType;
import wtf.novoline.gui.OverlayPanel.Entry;
import wtf.novoline.utils.account.Account;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.account.LoginHandler;
import wtf.novoline.utils.hypixel.HypixelPlayer;
import wtf.novoline.utils.hypixel.HypixelProfile;
import wtf.novoline.utils.hypixel.HypixelProfile.Punishment;
import wtf.novoline.utils.hypixel.HypixelProfile.Statistics;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class AccountEntry extends Entry<AccountListPanel, AccountEntry> {
   private final Account account;

   private String maskPassword(String text) {
      if (text.contains(":")) {
         int i = text.indexOf(":");
         text = text.substring(0, i) + ":" + "*".repeat(text.substring(i).length());
      }

      return text;
   }

   @Override
   public void onDoubleClick() {
      if (!this.account.getHypixelProfile().isValid()) {
         this.showNotification(
            NotificationType.ERROR,
            Language.translate("account_manager.login_handling.notification.title"),
            Language.translate("account_manager.login_handling.invalid_account.notification.body")
         );
      } else {
         this.showNotification(
            NotificationType.INFO,
            Language.translate("account_manager.login_handling.notification.title"),
            Language.translate("account_manager.login_handling.logging_in.notification.body")
         );
         this.account.getCompletableFuture().exceptionally(throwable -> {
            if (LoginHandler.reportLoginFailure(throwable)) {
               this.account.getHypixelProfile().invalidate();
               this.overlayPanel.saveAccounts();
            }

            return null;
         }).thenAccept(session -> {
            if (session != null) {
               LoginHandler.onLoggedIn(session);
               AccountManager accountManager = this.overlayPanel.getAccountManager();
               this.overlayPanel.forEach(accountEntry -> accountEntry.setCurrent(false));
               accountManager.setAccount(this.getAccount());

               for (int i = 0; i < accountManager.size(); i++) {
                  Account account2 = accountManager.get(i);
                  if (this.getAccount().getAccountId().equals(account2.getAccountId())) {
                     accountManager.setIntValue(i);
                     break;
                  }
               }

               this.setCurrent(true);
               this.updateProfile(session);
            }
         });
      }
   }

   public Account getAccount() {
      return this.account;
   }

   private void updateProfile(Session session2) {
      HypixelProfile hypixelProfile = this.account.getHypixelProfile();
      hypixelProfile.setUsername(session2.getUsername());
      if (hypixelProfile.getStatistics() == null) {
         hypixelProfile.setStatistics(new Statistics());
      }

      if (this.account.getType() != AccountType.CRACKED) {
         try {
            HypixelPlayer hypixelPlayer = Novoline.getNovoline().getHypixelApiClient().getPlayer(session2.getUsername()).get(30L, TimeUnit.SECONDS);
            hypixelProfile.setHypixelProfile(hypixelPlayer);
         } catch (Throwable throwable2) {
            Novoline.taggedLogger.error(throwable2, "Couldn't fetch Hypixel profile for {}", this.account);
         }
      }

      this.overlayPanel.saveAccounts();
   }

   @Override
   public void drawEntry(float f) {
      int i = this.selected ? 352321535 : 838860800;
      HypixelProfile hypixelProfile = this.account.getHypixelProfile();
      boolean valid = hypixelProfile.isValid();
      Map punishments = hypixelProfile.getPunishments();
      boolean flag = punishments != null && !punishments.isEmpty();
      if (flag) {
         i = this.selected ? 1090453504 : 855572480;
      } else if (this.isCurrent()) {
         i = this.selected ? 1073807104 : 838926080;
      }

      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.x, this.y, this.getWidth(), this.getHeight(), 5.0F, i);
      filledPolygonRenderer.draw();
      String text = (!valid ? EnumChatFormatting.STRIKETHROUGH : "")
         + (hypixelProfile.getUsername() == null ? Language.translate("account_manager.gui.entry.unknown_name") : hypixelProfile.getUsername());
      Fonts.rubikSemiboldSize16.drawStringWithShadow(text, this.x + 5, this.y + 8, -1);
      Fonts.rubikRegularSize14
         .drawStringWithShadow((!valid ? EnumChatFormatting.STRIKETHROUGH : "") + this.maskPassword(this.account.toString()), this.x + 5, this.y + 18, -1);
      HypixelPlayer hypixelProfile2 = hypixelProfile.getHypixelProfile();
      String text2 = null;
      if (hypixelProfile2 != null) {
         String rankText = hypixelProfile2.getRankText();
         rankText = rankText != null && rankText.isEmpty() ? "Default" : rankText;
         text2 = Language.translate("account_manager.gui.entry.top_stats", rankText, (int)hypixelProfile2.getNetworkLevel());
      } else if (hypixelProfile.getStatistics() != null) {
         text2 = Language.translate("account_manager.gui.entry.loading_profile");
      }

      if (text2 != null) {
         Fonts.rubikRegularSize14.drawStringWithShadow(text2, this.x + this.getWidth() - Fonts.rubikRegularSize14.getStringWidth(text2) - 5, this.y + 8, -1);
      }

      Statistics statistics = hypixelProfile.getStatistics();
      if (statistics != null) {
         Duration duration = Duration.ofMillis(statistics.getPlayTime());
         long kills = statistics.getKills();
         long wins = statistics.getWins();
         String text3 = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
         long l = wins;
         long l2 = kills;
         String text4 = "K: " + l2 + "  W: " + l + "  PT: " + text3;
         Fonts.rubikRegularSize14.drawStringWithShadow(text4, this.x + this.getWidth() - Fonts.rubikRegularSize14.getStringWidth(text4) - 5, this.y + 18, -1);
      }

      if (flag) {
         java.util.Map.Entry entry2 = (Map.Entry) punishments.entrySet().toArray(java.util.Map.Entry[]::new)[0];
         Fonts.rubikRegularSize14
            .drawCenteredStringWithShadow(
               Language.translate("account_manager.gui.entry.banned_on", entry2.getKey()), this.x + this.getWidth() / 2.0F, this.y + 9, -1
            );
         String text5 = Language.translate("account_manager.gui.entry.permanently_banned");
         Instant expiresAt = ((Punishment)entry2.getValue()).getExpiresAt();
         if (expiresAt != null) {
            Duration duration2 = Duration.between(Instant.now(), expiresAt);
            if (duration2.isNegative()) {
               punishments.remove(entry2.getKey());
               return;
            }

            text5 = Language.translate(
               "account_manager.gui.entry.unbanned_in", duration2.toDays(), duration2.toHoursPart(), duration2.toMinutesPart(), duration2.toSecondsPart()
            );
         }

         Fonts.rubikRegularSize14.drawCenteredStringWithShadow(text5, this.x + this.getWidth() / 2.0F, this.y + 17, -1);
      }
   }

   @Override
   public int getHeight() {
      return 30;
   }

   public AccountEntry(AccountListPanel accountListPanel, Account account2, int i) {
      super(accountListPanel, i);
      this.account = account2;
   }

   private void showNotification(NotificationType notificationType, String text, String text2) {
      Novoline.getNovoline().getNotificationManager().show(new Notification(notificationType, text, text2, 5000L));
   }

   @Override
   public int getWidth() {
      return this.overlayPanel.getWidth();
   }
}
