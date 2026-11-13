package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.Session;
import wtf.novoline.Novoline;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.GameStartEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.event.ServerConnectEvent;
import wtf.novoline.event.ServerDisconnectEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.hypixel.HypixelApiClient;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.hypixel.PunishmentStats;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.time.Stopwatch;

public class HypixelStats extends Module {
   public final CheckboxSetting hypixelStats = settingManager.register(new CheckboxSetting(this, "Check Staff bans", true));
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show play time", true));
   private PunishmentStats punishmentStats;
   private final NumberSetting delaySeconds = settingManager.register(new NumberSetting(this, "Delay (seconds)", 120, 30, 600));
   private final ComboSetting notify = settingManager.register(new ComboSetting(this, "Notify", 0, "Always", "When active"));
   private final Stopwatch playTimeTimer = new Stopwatch();
   private final Stopwatch statsRequestTimer = new Stopwatch();
   private boolean offHypixel = false;
   @EventLink
   private final Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> {
      if (!HypixelState.isServerType(ServerType.HYPIXEL)) {
         this.offHypixel = true;
      } else {
         this.offHypixel = false;
         if (this.session == null || !minecraft.session.getUsername().equals(this.session.getUsername())) {
            this.getHypixelApiClient().getHypixelApi().setApiKey(null);
            this.listeningForChat = true;
            this.session = minecraft.session;
         }

         this.punishmentStats = null;
         novoline.getHypixelApiClient().getCompletableFuture().exceptionally(throwable -> {
            novoline.sendChatMessage(Language.translate("feature.hypixelstats.error.initial_ban_check_failed"));
            Novoline.taggedLogger.error(throwable, "Couldn't complete initial punishment stats request");
            return null;
         }).thenAccept(punishmentStats2 -> {
            if (punishmentStats2 != null) {
               this.punishmentStats = punishmentStats2;
            }
         });
      }
   };
   @EventLink
   private final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      long l = (long)this.delaySeconds.getCurrentValue();
      if (this.hypixelStats.isEnabled()
         && this.playTimeTimer.hasElapsed(l * 1000L)
         && HypixelState.isServerType(ServerType.HYPIXEL)
         && this.getHypixelApiClient().getHypixelApi().getApiKey() != null) {
         this.playTimeTimer.reset();
         novoline.getHypixelApiClient()
            .getCompletableFuture()
            .exceptionally(throwable -> {
               novoline.sendChatMessage(Language.translate("feature.hypixelstats.error.ban_check_failed", throwable.getMessage()));
               Novoline.taggedLogger.error(throwable, "Couldn't complete punishment stats request");
               return null;
            })
            .thenAccept(
               punishmentStats2 -> {
                  if (punishmentStats2 != null) {
                     if (this.punishmentStats != null) {
                        int i = punishmentStats2.getStaffTotal() - this.punishmentStats.getStaffTotal();
                        String text2 = this.delaySeconds.getCurrentValue() + "s";
                        boolean flag = i < 0;
                        if (i <= 0 && !flag) {
                           if (this.notify.getCurrentElement() == 0) {
                              this.showNotification(
                                 NotificationType.INFO,
                                 Language.translate("feature.hypixelstats.notification.ban_status.title"),
                                 Language.translate("feature.hypixelstats.notification.ban_status.not_changed", text2),
                                 4000L
                              );
                           }
                        } else {
                           if (flag) {
                              i = -i;
                           }

                           this.showNotification(
                              flag ? NotificationType.INFO : NotificationType.WARNING,
                              Language.translate("feature.hypixelstats.notification.ban_status.title"),
                              flag
                                 ? Language.translate("feature.hypixelstats.notification.ban_status.unbanned", i, text2)
                                 : Language.translate("feature.hypixelstats.notification.ban_status.banned", i, text2),
                              6000L
                           );
                           if (!flag) {
                              this.addToStat(HypixelStats.PlayTimeType.STAFF_BANS_SINCE_LOGIN, i);
                           }
                        }
                     }

                     this.punishmentStats = punishmentStats2;
                  }
               }
            );
      }

      if (HypixelState.isServerType(ServerType.HYPIXEL) && this.listeningForChat && minecraft.thePlayer != null && this.statsRequestTimer.hasElapsed(2500L)) {
         minecraft.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage("/api new"));
         this.statsRequestTimer.reset();
      }
   };
   private Session session;
   private boolean visible;
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> this.checkServer();
   private boolean listeningForChat;
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (packetReceiveEvent.packet instanceof S02PacketChat s02PacketChat && this.listeningForChat) {
         String unformattedText = s02PacketChat.getChatComponent().getUnformattedText();
         if (unformattedText.startsWith("Your new API key is ")) {
            String text2 = unformattedText.substring("Your new API key is ".length());
            if (!text2.isEmpty()) {
               this.getHypixelApiClient().getHypixelApi().setApiKey(text2);
               this.listeningForChat = false;
               packetReceiveEvent.cancel();
            }
         }
      }
   };
   private final HypixelStats.Component stats = new HypixelStats.Component();

   static Minecraft access$000() {
      return minecraft;
   }

   private void checkServer() {
      if (minecraft.isSingleplayer() || !HypixelState.isServerType(ServerType.HYPIXEL)) {
         this.offHypixel = true;
      }

      this.setVisible(!this.offHypixel);
      if (this.offHypixel) {
         novoline.getTaskScheduler()
            .schedule(
               new ScheduledTask(500) {
                  @Override
                  public void run() {
                     if (HypixelStats.access$200().thePlayer != null) {
                        String text2 = Language.translate(
                           "feature.hypixelstats.notification.not_available.text",
                           !HypixelStats.access$400().isSingleplayer() && HypixelStats.access$100().getCurrentServerData() != null
                              ? Language.translate(
                                 "feature.hypixelstats.notification.not_available.server", HypixelStats.access$500().getCurrentServerData().serverIP
                              )
                              : Language.translate("feature.hypixelstats.notification.not_available.singleplayer")
                        );
                        HypixelStats.access$300(HypixelStats.this, NotificationType.WARNING, "HypixelStats", text2);
                     }
                  }
               }
            );
         this.offHypixel = false;
      }
   }

   private void addToStat(HypixelStats.PlayTimeType playTimeType, int i) {
      int i2 = Integer.parseInt(this.stats.getValues().get(playTimeType));
      this.stats.getValues().put(playTimeType, String.valueOf(i2 + i));
   }

   public HypixelStats() {
      super(Category.MISC, "HypixelStats", "Displays your stats and notifies about Staff bans");
      novoline.getBus().subscribe(this.stats);
   }

   static Minecraft access$100() {
      return minecraft;
   }

   public boolean isVisible() {
      return this.visible;
   }

   static Minecraft access$200() {
      return minecraft;
   }

   public HypixelStats.Component getStats() {
      return this.stats;
   }

   @Override
   protected void onEnable() {
      this.checkServer();
   }

   static void access$300(HypixelStats hypixelStats2, NotificationType notificationType, String text2, String text3) {
      hypixelStats2.showNotification(notificationType, text2, text3, 3000L);
   }

   private HypixelApiClient getHypixelApiClient() {
      return novoline.getHypixelApiClient();
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   private void increment(HypixelStats.PlayTimeType playTimeType) {
      this.addToStat(playTimeType, 1);
   }

   private void resetStats() {
      for (HypixelStats.PlayTimeType playTimeType : HypixelStats.PlayTimeType.values()) {
         this.stats.getValues().put(playTimeType, "0");
      }
   }

   static Minecraft access$400() {
      return minecraft;
   }

   static Minecraft access$500() {
      return minecraft;
   }

   public final class Component {
      private final Map<HypixelStats.PlayTimeType, String> values = new EnumMap<>(HypixelStats.PlayTimeType.class);
      private final Stopwatch timer = new Stopwatch();
      @EventLink
      private final Listener<PlayerKillEvent> onPlayerKill = playerKillEvent -> HypixelStats.this.increment(HypixelStats.PlayTimeType.KILLS);
      @EventLink
      private final Listener<GameStartEvent> onGameStart = gameStartEvent -> HypixelStats.this.increment(HypixelStats.PlayTimeType.GAMES_PLAYED);
      @EventLink
      private final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
         if (gameEndEvent.win) {
            HypixelStats.this.increment(HypixelStats.PlayTimeType.WINS);
         }
      };
      @EventLink
      private final Listener<RunTickEvent> onRunTick = runTickEvent -> {
         if (this.timer.hasElapsed(500L) && HypixelState.getSessionStart() != null) {
            Duration duration = Duration.between(HypixelState.getSessionStart(), Instant.now());
            String text2 = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
            this.values.put(HypixelStats.PlayTimeType.PLAY_TIME, text2);
            this.timer.reset();
         }
      };
      @EventLink
      private final Listener<ServerDisconnectEvent> onServerDisconnect = serverDisconnectEvent -> HypixelStats.this.resetStats();
      @EventLink
      private final Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> HypixelStats.this.resetStats();
      @EventLink
      private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
         if (HypixelStats.access$000().isSingleplayer()) {
            HypixelStats.this.resetStats();
         }
      };

      public Map<HypixelStats.PlayTimeType, String> getValues() {
         return this.values;
      }
   }

   public enum PlayTimeType {
      PLAY_TIME,
      KILLS,
      WINS,
      GAMES_PLAYED,
      STAFF_BANS_SINCE_LOGIN;

      public String getDisplayName() {
         return Language.translate("feature.hypixelstats." + this.name().toLowerCase(Locale.ROOT));
      }
   }
}
