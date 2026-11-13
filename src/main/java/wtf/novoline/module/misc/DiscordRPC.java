package wtf.novoline.module.misc;

import de.jcm.discordgamesdk.ActivityManager;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityAssets;
import de.jcm.discordgamesdk.activity.ActivityTimestamps;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import wtf.novoline.Novoline;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.NativeLoader;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;

public class DiscordRPC extends Module {
   private static final Instant CLIENT_START = Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());
   private boolean nativesLoaded;
   private final ComboSetting discordRPC = settingManager.register(new ComboSetting(this, "Estimated time", 1, "Start", "Server"));
   private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
   private ScheduledFuture<?> scheduledFuture;
   private static final long UNUSED_APPLICATION_ID = 894229640590262322L;
   private Core core;

   private Core getCore() {
      if (this.core == null) {
         CreateParams createParams = new CreateParams();
         createParams.setClientID(894229640590262322L);
         createParams.setFlags(de.jcm.discordgamesdk.CreateParams.Flags.NO_REQUIRE_DISCORD);
         return this.core = new Core(createParams);
      } else {
         return this.core;
      }
   }

   private DiscordRPC.LogoType getLogoType() {
      if (minecraft.theWorld == null) {
         return null;
      }

      if (!HypixelState.isServerType(ServerType.HYPIXEL)) {
         return DiscordRPC.LogoType.LOGO;
      }

      HypixelGameType hypixelGameType = HypixelState.getHypixelGameType();
      if (hypixelGameType == null) {
         return DiscordRPC.LogoType.HYPIXEL_UNANNOUNCED;
      }

      return switch (hypixelGameType) {
         case SKY_WARS -> DiscordRPC.LogoType.HYPIXEL_SKY_WARS;
         case ARCADE -> DiscordRPC.LogoType.HYPIXEL_ARCADE_LOBBY;
         case ARENA_BRAWL -> DiscordRPC.LogoType.HYPIXEL_ARENA_BRAWL;
         case BED_WARS -> DiscordRPC.LogoType.HYPIXEL_BED_WARS;
         case UHC -> DiscordRPC.LogoType.HYPIXEL_UHC;
         case BLITZ_SURVIVAL_GAMES -> DiscordRPC.LogoType.HYPIXEL_SURVIVAL_GAMES;
         case MEGA_WALLS -> DiscordRPC.LogoType.HYPIXEL_MEGA_WALLS;
         case BLOCKING_DEAD -> DiscordRPC.LogoType.HYPIXEL_BLOCKING_DEAD;
         case BOUNTY_HUNTERS -> DiscordRPC.LogoType.HYPIXEL_BOUNTY_HUNTERS;
         case BUILD_BATTLE -> DiscordRPC.LogoType.HYPIXEL_BUILD_BATTLE;
         case CAPTURE_THE_WOOL -> DiscordRPC.LogoType.HYPIXEL_CAPTURE_THE_WOOL;
         case COPS_AND_CRIMS -> DiscordRPC.LogoType.HYPIXEL_COPS_AND_CRIMS;
         case CREEPER_ATTACK -> DiscordRPC.LogoType.HYPIXEL_CREEPER_ATTACK;
         case DUELS -> DiscordRPC.LogoType.HYPIXEL_DUELS;
         case THE_PIT -> DiscordRPC.LogoType.HYPIXEL_PIT;
         case LOBBY -> DiscordRPC.LogoType.HYPIXEL_LOBBY;
         case MURDER_MYSTERY -> DiscordRPC.LogoType.HYPIXEL_MURDER_MYSTERY;
         case FARM_HUNT -> DiscordRPC.LogoType.HYPIXEL_FARM_HUNT;
         case FOOTBALL -> DiscordRPC.LogoType.HYPIXEL_FOOTBALL;
         case GALAXY_WARS -> DiscordRPC.LogoType.HYPIXEL_GALAXY_WARS;
         case HIDE_AND_SEEK -> DiscordRPC.LogoType.HYPIXEL_HIDE_AND_SEEK;
         case HOLE_IN_THE_WALL -> DiscordRPC.LogoType.HYPIXEL_HOLE_IN_THE_WALL;
         case HOUSING -> DiscordRPC.LogoType.HYPIXEL_HOUSING;
         case HYPIXEL_SAYS -> DiscordRPC.LogoType.HYPIXEL_HYPIXEL_SAYS;
         case MINI_WALLS -> DiscordRPC.LogoType.HYPIXEL_MINI_WALLS;
         case PAINTBALL -> DiscordRPC.LogoType.HYPIXEL_PAINTBALL;
         case PARTY_GAMES -> DiscordRPC.LogoType.HYPIXEL_PARTY_GAMES;
         case QUAKECRAFT -> DiscordRPC.LogoType.HYPIXEL_QUAKECRAFT;
         case SKY_BLOCK -> DiscordRPC.LogoType.HYPIXEL_SKY_BLOCK;
         case SPEED_UHC -> DiscordRPC.LogoType.HYPIXEL_SPEED_UHC;
         case SUPER_SMASH -> DiscordRPC.LogoType.HYPIXEL_SMASH_HEROES;
         case THE_WALLS -> DiscordRPC.LogoType.HYPIXEL_THE_WALLS;
         case THROW_OUT -> DiscordRPC.LogoType.HYPIXEL_THROW_OUT;
         case TNT_GAMES -> DiscordRPC.LogoType.HYPIXEL_TNT_GAMES;
         case TOWER_WARS -> DiscordRPC.LogoType.HYPIXEL_TOWER_WARS;
         case TURBO_KART_RACERS -> DiscordRPC.LogoType.HYPIXEL_TURBO_KART_RACERS;
         case VAMPIRE_Z -> DiscordRPC.LogoType.HYPIXEL_VAMPIRE_Z;
         case WARLORDS -> DiscordRPC.LogoType.HYPIXEL_WARLORDS;
         case ZOMBIES -> DiscordRPC.LogoType.HYPIXEL_ZOMBIES;
         default -> DiscordRPC.LogoType.HYPIXEL_UNANNOUNCED;
      };
   }

   @Override
   protected void onDisable() {
      this.cancelUpdateTask();
      if (this.core != null) {
         try {
            this.core.activityManager().clearActivity();
            this.core.runCallbacks();
            this.core.close();
         } catch (Exception exception) {
            Novoline.taggedLogger.error(exception, "Couldn't close connection");
         } finally {
            this.core = null;
         }
      }

      this.scheduledFuture = null;
   }

   private void updateActivity(Core core2, ActivityManager activityManager2) {
      try (Activity activity2 = new Activity()) {
         activity2.setDetails("User ID: " + Novoline.userId);
         activity2.setState("Version: " + getVersionText());
         ActivityTimestamps activityTimestamps = activity2.timestamps();
         activityTimestamps.setStart(this.getStartTimestamp());
         ActivityAssets activityAssets = activity2.assets();
         DiscordRPC.LargeType.LARGE.applyAssets(activityAssets, DiscordRPC.LogoType.LOGO);
         DiscordRPC.LargeType.SMALL.applyAssets(activityAssets, this.getLogoType());
         activityManager2.updateActivity(activity2);
      }

      core2.runCallbacks();
   }

   private void onConnectionLost() {
      this.showNotification(NotificationType.ERROR, "DiscordRPC", Language.translate("feature.discordrpc.notification.no_connection"), 5000L);
      this.disable();
   }

   private void cancelUpdateTask() {
      if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
         this.scheduledFuture.cancel(true);
      }
   }

   private Path loadNativeLibrary(String text2) throws IOException, UnsatisfiedLinkError {
      EnumOS osType = Util.getOSType();
      String property = System.getProperty("os.arch");

      return NativeLoader.loadLibrary(switch (osType) {
         case OSX -> "/native/macos/" + property + "/lib" + text2 + ".dylib";
         case LINUX -> "/native/linux/" + property + "/lib" + text2 + ".so";
         case WINDOWS -> "/native/windows/" + property + "/" + text2 + ".dll";
         default -> throw new IllegalStateException("Unsupported OS: " + osType);
      });
   }

   private static Instant getInstant() {
      return CLIENT_START;
   }

   private void updateActivityEvery(TimeUnit timeUnit) {
      this.cancelUpdateTask();
      this.scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(() -> {
         try {
            this.updateActivity(this.core, this.core.activityManager());
         } catch (Exception exception) {
            this.onConnectionLost();
            Novoline.taggedLogger.error(exception, "Couldn't update activity");
         }
      }, 0L, 3L, timeUnit);
   }

   private boolean ensureNativesLoaded() {
      if (!this.nativesLoaded) {
         try {
            this.loadNativeLibrary("discord_game_sdk");
            Path path = this.loadNativeLibrary("discord_game_sdk_jni");
            Core.initDiscordNative(path.toAbsolutePath().toString());
            this.nativesLoaded = true;
         } catch (Exception exception) {
            Novoline.taggedLogger.error(exception, "Couldn't initialize Discord RPC natives");
            this.nativesLoaded = false;
         }
      }

      return this.nativesLoaded;
   }

   private static String getVersionText() {
      return "3.0 (030122)";
   }

   @Override
   protected void onEnable() {
      if (!this.ensureNativesLoaded()) {
         this.onConnectionLost();
      } else {
         try {
            final Core core2 = this.getCore();
            final ActivityManager activityManager2 = core2.activityManager();
            this.scheduledFuture = this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
               private int intValue = 0;

               @Override
               public void run() {
                  try {
                     DiscordRPC.this.updateActivity(core2, activityManager2);
                     if (++this.intValue >= 100) {
                        DiscordRPC.this.updateActivityEvery(TimeUnit.SECONDS);
                     }
                  } catch (Exception exception) {
                     DiscordRPC.this.onConnectionLost();
                     Novoline.taggedLogger.error(exception, "Couldn't update initial activity");
                  }
               }
            }, 0L, 50L, TimeUnit.MILLISECONDS);
         } catch (GameSDKException gameSDKException) {
            Novoline.taggedLogger.error(gameSDKException, "GameSDK failed to initialize!");
            this.onConnectionLost();
         }
      }
   }

   private Instant getStartTimestamp() {
      return switch (this.discordRPC.getCurrentElement()) {
         case 0 -> getInstant();
         case 1 -> {
            Instant sessionStart = HypixelState.getSessionStart();
            yield Minecraft.getMinecraft().theWorld != null && sessionStart != null ? sessionStart : getInstant();
         }
         default -> throw new IllegalArgumentException("Unknown estimated time: " + this.discordRPC.getCurrentElementAsString());
      };
   }

   public DiscordRPC() {
      super(Category.MISC, "DiscordRPC", "Shows everyone on Discord that you're playing on Novoline");
   }

   public enum HypixelArcadeType {
      HYPIXEL_ARCADE("hypixel-arcade"),
      HYPIXEL_ARENA_BRAWL("hypixel-arena-brawl"),
      HYPIXEL_BED_WARS("hypixel-bed-wars"),
      HYPIXEL_BUILD_BATTLE("hypixel-build-battle"),
      HYPIXEL_CLASSIC("hypixel-classic"),
      HYPIXEL_CVC("hypixel-cvc"),
      HYPIXEL_DUELS("hypixel-duels"),
      HYPIXEL_HOUSING("hypixel-housing"),
      HYPIXEL_MEGA_WALLS("hypixel-mega-walls"),
      HYPIXEL_MURDER_MYSTERY("hypixel-murder-mystery"),
      HYPIXEL_PAINTBALL("hypixel-paintball"),
      HYPIXEL_PIT("hypixel-pit"),
      HYPIXEL_PROTOTYPE("hypixel-prototype"),
      HYPIXEL_QUAKECRAFT("hypixel-quakecraft"),
      HYPIXEL_SKY_BLOCK("hypixel-sky-block"),
      HYPIXEL_SKY_WARS("hypixel-sky-wars"),
      HYPIXEL_SMASH_HEROES("hypixel-smash-heroes"),
      HYPIXEL_SURVIVAL_GAMES("hypixel-survival-games"),
      HYPIXEL_TNT("hypixel-tnt"),
      HYPIXEL_TURBO_KART_RACERS("hypixel-turbo-kart-racers"),
      HYPIXEL_UHC("hypixel-uhc"),
      HYPIXEL_UNANNOUNCED("hypixel-unannounced"),
      HYPIXEL_VAMPIRE_Z("hypixel-vampire-z"),
      HYPIXEL_WALLS("hypixel-walls"),
      HYPIXEL_WARLORDS("hypixel-walls"),
      LOGO("i1pvmbv80dg9jrn705kbttfqo1339m0h"),
      MINECRAFT("166fbad351ecdd02d11a3b464748f66b");

      private final String assetKey;

      public String getAssetKey() {
         return this.assetKey;
      }

      HypixelArcadeType(String text2) {
         this.assetKey = text2;
      }
   }

   public enum LargeType {
      LARGE {
         @Override
         protected void applyImage(ActivityAssets activityAssets, DiscordRPC.LogoType logoType) {
            activityAssets.setLargeImage(logoType.hypixelArcadeType.getAssetKey());
            activityAssets.setLargeText("novoline.wtf");
         }
      },
      SMALL {
         @Override
         protected void applyImage(ActivityAssets activityAssets, DiscordRPC.LogoType logoType) {
            activityAssets.setSmallImage(logoType.hypixelArcadeType.getAssetKey());
            activityAssets.setSmallText(logoType.hint);
         }
      };

      protected abstract void applyImage(ActivityAssets activityAssets, DiscordRPC.LogoType logoType);

      public void applyAssets(ActivityAssets activityAssets, DiscordRPC.LogoType logoType) {
         if (logoType != null) {
            this.applyImage(activityAssets, logoType);
         }
      }
   }

   public enum LogoType {
      LOGO(DiscordRPC.HypixelArcadeType.LOGO, DiscordRPC.getVersionText(), "Playing Novoline"),
      FAKE_HYPIXEL(DiscordRPC.HypixelArcadeType.HYPIXEL_UNANNOUNCED, "In lobby", "Playing on Hypixel"),
      HYPIXEL_UNANNOUNCED(DiscordRPC.HypixelArcadeType.HYPIXEL_UNANNOUNCED, "Mode: Unknown", "Playing on Hypixel"),
      HYPIXEL_SKY_WARS(DiscordRPC.HypixelArcadeType.HYPIXEL_SKY_WARS, "Mode: SkyWars", "Playing on Hypixel"),
      HYPIXEL_BED_WARS(DiscordRPC.HypixelArcadeType.HYPIXEL_BED_WARS, "Mode: BedWars", "Playing on Hypixel"),
      HYPIXEL_UHC(DiscordRPC.HypixelArcadeType.HYPIXEL_UHC, "Mode: UHC", "Playing on Hypixel"),
      HYPIXEL_SURVIVAL_GAMES(DiscordRPC.HypixelArcadeType.HYPIXEL_SURVIVAL_GAMES, "Mode: Survival Games", "Playing on Hypixel"),
      HYPIXEL_MEGA_WALLS(DiscordRPC.HypixelArcadeType.HYPIXEL_MEGA_WALLS, "Mode: MegaWalls", "Playing on Hypixel"),
      HYPIXEL_DUELS(DiscordRPC.HypixelArcadeType.HYPIXEL_DUELS, "Mode: Duels", "Playing on Hypixel"),
      HYPIXEL_PIT(DiscordRPC.HypixelArcadeType.HYPIXEL_PIT, "Mode: The Pit", "Playing on Hypixel"),
      HYPIXEL_LOBBY(DiscordRPC.HypixelArcadeType.HYPIXEL_UNANNOUNCED, "In lobby", "Playing on Hypixel"),
      HYPIXEL_MURDER_MYSTERY(DiscordRPC.HypixelArcadeType.HYPIXEL_MURDER_MYSTERY, "Mode: Murder Mystery", "Playing on Hypixel"),
      HYPIXEL_ARCADE_LOBBY(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "In lobby", "Playing on Hypixel"),
      HYPIXEL_ARENA_BRAWL(DiscordRPC.HypixelArcadeType.HYPIXEL_ARENA_BRAWL, "Mode: Arena Brawl", "Playing on Hypixel"),
      HYPIXEL_BUILD_BATTLE(DiscordRPC.HypixelArcadeType.HYPIXEL_BUILD_BATTLE, "Mode: Build Battle", "Playing on Hypixel"),
      HYPIXEL_BLOCKING_DEAD(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Blocking Dead", "Playing on Hypixel"),
      HYPIXEL_BOUNTY_HUNTERS(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Bounty Hunters", "Playing on Hypixel"),
      HYPIXEL_CAPTURE_THE_WOOL(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Capture the Wool", "Playing on Hypixel"),
      HYPIXEL_CLASSIC_LOBBY(DiscordRPC.HypixelArcadeType.HYPIXEL_CLASSIC, "In lobby", "Playing on Hypixel"),
      HYPIXEL_COPS_AND_CRIMS(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Cops and Crims", "Playing on Hypixel"),
      HYPIXEL_CREEPER_ATTACK(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Creeper Attack", "Playing on Hypixel"),
      HYPIXEL_FARM_HUNT(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Farm Hunt", "Playing on Hypixel"),
      HYPIXEL_FOOTBALL(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Football", "Playing on Hypixel"),
      HYPIXEL_GALAXY_WARS(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Galaxy Wars", "Playing on Hypixel"),
      HYPIXEL_HIDE_AND_SEEK(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Hide and Seek", "Playing on Hypixel"),
      HYPIXEL_HOLE_IN_THE_WALL(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Hole in the Wall", "Playing on Hypixel"),
      HYPIXEL_HOUSING(DiscordRPC.HypixelArcadeType.HYPIXEL_HOUSING, "In housing", "Playing on Hypixel"),
      HYPIXEL_HYPIXEL_SAYS(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Hypixel Says", "Playing on Hypixel"),
      HYPIXEL_MINI_WALLS(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Mini Walls", "Playing on Hypixel"),
      HYPIXEL_PAINTBALL(DiscordRPC.HypixelArcadeType.HYPIXEL_PAINTBALL, "Mode: Paintball", "Playing on Hypixel"),
      HYPIXEL_PARTY_GAMES(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Party Games", "Playing on Hypixel"),
      HYPIXEL_PROTOTYPE_LOBBY(DiscordRPC.HypixelArcadeType.HYPIXEL_PROTOTYPE, "In lobby", "Playing on Hypixel"),
      HYPIXEL_QUAKECRAFT(DiscordRPC.HypixelArcadeType.HYPIXEL_QUAKECRAFT, "Mode: Quakecraft", "Playing on Hypixel"),
      HYPIXEL_SKY_BLOCK(DiscordRPC.HypixelArcadeType.HYPIXEL_SKY_BLOCK, "Mode: SkyBlock", "Playing on Hypixel"),
      HYPIXEL_SPEED_UHC(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: SpeedUHC", "Playing on Hypixel"),
      HYPIXEL_SMASH_HEROES(DiscordRPC.HypixelArcadeType.HYPIXEL_SMASH_HEROES, "Mode: Smash Heroes", "Playing on Hypixel"),
      HYPIXEL_THE_WALLS(DiscordRPC.HypixelArcadeType.HYPIXEL_WALLS, "Mode: The Walls", "Playing on Hypixel"),
      HYPIXEL_THROW_OUT(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Throw Out", "Playing on Hypixel"),
      HYPIXEL_TNT_GAMES(DiscordRPC.HypixelArcadeType.HYPIXEL_TNT, "Mode: The TNT Games", "Playing on Hypixel"),
      HYPIXEL_TOWER_WARS(DiscordRPC.HypixelArcadeType.HYPIXEL_PROTOTYPE, "Mode: Tower Wars", "Playing on Hypixel"),
      HYPIXEL_TURBO_KART_RACERS(DiscordRPC.HypixelArcadeType.HYPIXEL_TURBO_KART_RACERS, "Mode: Turbo Kart Racers", "Playing on Hypixel"),
      HYPIXEL_VAMPIRE_Z(DiscordRPC.HypixelArcadeType.HYPIXEL_VAMPIRE_Z, "Mode: VampireZ", "Playing on Hypixel"),
      HYPIXEL_WARLORDS(DiscordRPC.HypixelArcadeType.HYPIXEL_WARLORDS, "Mode: Warlords", "Playing on Hypixel"),
      HYPIXEL_ZOMBIES(DiscordRPC.HypixelArcadeType.HYPIXEL_ARCADE, "Mode: Zombies", "Playing on Hypixel");

      private final DiscordRPC.HypixelArcadeType hypixelArcadeType;
      private final String hint;
      private final String details;

      public DiscordRPC.HypixelArcadeType getHypixelArcadeType() {
         return this.hypixelArcadeType;
      }

      public String getDetails() {
         return this.details;
      }

      LogoType(DiscordRPC.HypixelArcadeType hypixelArcadeType2, String text2, String text3) {
         if (hypixelArcadeType2 == null) {
            throw new IllegalArgumentException("No asset");
         }

         if (text2 == null) {
            throw new IllegalArgumentException("No hint");
         }

         if (text3 == null) {
            throw new IllegalArgumentException("No text");
         }

         this.hypixelArcadeType = hypixelArcadeType2;
         this.hint = text2;
         this.details = text3;
      }

      public String getHint() {
         return this.hint;
      }
   }
}
