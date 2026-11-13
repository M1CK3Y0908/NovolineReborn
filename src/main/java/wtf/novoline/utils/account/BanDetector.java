package wtf.novoline.utils.account;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import wtf.novoline.Novoline;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.hypixel.HypixelProfile.Punishment;
import wtf.novoline.utils.hypixel.HypixelProfile.Statistics;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;

public class BanDetector {
   @EventLink
   private final Listener<PlayerKillEvent> onPlayerKill = playerKillEvent -> {
      if (this.accountManager.getAccount() != null) {
         Statistics statistics = this.accountManager.getAccount().getHypixelProfile().getStatistics();
         statistics.setKills(statistics.getKills() + 1L);
      }
   };
   private static final Pattern pattern = Pattern.compile(
      "^(((?!-))(xn--|_)?[a-z0-9-]{0,61}[a-z0-9]\\.)*(xn--)?([a-z0-9][a-z0-9\\-]{0,60}|[a-z0-9-]{1,30}\\.[a-z]{2,})$"
   );
   @EventLink
   private final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.accountManager.getAccount() != null) {
         Statistics statistics = this.accountManager.getAccount().getHypixelProfile().getStatistics();
         statistics.setWins(statistics.getWins() + 1L);
      }
   };
   @EventLink(4)
   private final Listener<ServerDisconnectEvent> onServerDisconnect = serverDisconnectEvent -> {
      if (this.accountManager.getAccount() != null && HypixelState.getSessionStart() != null) {
         Duration duration = Duration.between(HypixelState.getSessionStart(), Instant.now());
         Account account2 = this.accountManager.getAccount();
         Statistics statistics = account2.getHypixelProfile().getStatistics();
         statistics.setPlayTime(statistics.getPlayTime() + duration.toMillis());
         if (this.recordPunishment(account2, serverDisconnectEvent.getMessage()) != null) {
            String text = Language.translate("account_manager.login_handling.notification.title");
            String text2 = Language.translate("account_manager.marked_as_banned.notification.body");
            Notification notification2 = new Notification(NotificationType.ERROR, text, text2, 5000L);
            Novoline.getNovoline().getNotificationManager().show(notification2);
         }

         try {
            this.accountManager.save();
         } catch (IOException ioException) {
            Novoline.taggedLogger.error(ioException, "Couldn't save accounts");
         }
      }
   };
   private AccountManager accountManager;

   public BanDetector(AccountManager accountManager2) {
      this.accountManager = accountManager2;
   }

   private Punishment getPunishment(IChatComponent iChatComponent) {
      String[] texts = iChatComponent.getFormattedText().split("\n");
      if (texts.length < 3) {
         return null;
      } else {
         String text = texts[0];
         String text2 = texts[2].substring("§r§7Reason: §r§f".length());
         if (text.startsWith("§r§cYou are temporarily banned for ")) {
            String text3 = StringUtils.substringBetween(text, "§r§f", "§r§c");
            Instant instant = Instant.now().plusMillis(this.parseDuration(text3));
            return new Punishment(text2, instant);
         } else {
            return text.startsWith("§r§cYou are permanently banned") ? new Punishment(text2, null) : null;
         }
      }
   }

   private Punishment recordPunishment(Account account2, IChatComponent iChatComponent) {
      ServerData serverData = Novoline.getNovoline().getServerData();
      if (serverData == null) {
         return null;
      }

      String serverIP2 = serverData.serverIP;
      if (serverIP2.endsWith(".")) {
         serverIP2 = serverIP2.substring(0, serverIP2.length() - 1);
      }

      Matcher matcher2 = pattern.matcher(serverIP2);
      if (matcher2.matches()) {
         String text = matcher2.group(1) + matcher2.group(5);
         text = text.toLowerCase(Locale.ROOT);

         Punishment punishment = switch (text) {
            case "hypixel.net", "hypixel.io" -> this.getPunishment(iChatComponent);
            default -> null;
         };
         if (punishment != null) {
            if (account2.getHypixelProfile().getPunishments() == null) {
               account2.getHypixelProfile().setPunishments(new HashMap<>());
            }

            account2.getHypixelProfile().getPunishments().put(text, punishment);
         }

         return punishment;
      } else {
         return null;
      }
   }

   private long parseDuration(String text) {
      text = text.replaceAll("\\s+", "").replaceFirst("(\\d+d)", "P$1T");
      text = text.charAt(0) != 'P' ? "PT" + text.replace("min", "m") : text.replace("min", "m");
      return Duration.parse(text).toMillis();
   }
}
