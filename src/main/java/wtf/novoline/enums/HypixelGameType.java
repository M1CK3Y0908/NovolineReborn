package wtf.novoline.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public enum HypixelGameType {
   UNKNOWN,
   LOBBY(
      "MMLobby",
      "skyLobby",
      "buildBattleLobby",
      "uhcLobby",
      "TNTScoreboard",
      "duelsLobby",
      "MainScoreboard",
      "battleLobby",
      "MegaScoreboard",
      "classic",
      "Prototype"
   ),
   PRE_GAME("PreScoreboard"),
   ARCADE("ARCADE"),
   ARENA_BRAWL("uhc"),
   BED_WARS("BForeboard"),
   BLITZ_SURVIVAL_GAMES("Blitz SG"),
   BLOCKING_DEAD("Blocking Dead"),
   BOUNTY_HUNTERS("BOUNTY"),
   BUILD_BATTLE("BuildBattle"),
   CAPTURE_THE_WOOL("Pvpboard"),
   COPS_AND_CRIMS("MC-GO"),
   CREEPER_ATTACK("Creeper Attack"),
   DUELS("Duels"),
   FARM_HUNT("Farm Hunt"),
   FOOTBALL("Football"),
   GALAXY_WARS("GALACTIC"),
   HIDE_AND_SEEK("HideAndSeek"),
   HOLE_IN_THE_WALL("HoITW"),
   HOUSING("Housing"),
   HYPIXEL_SAYS("SantaSays"),
   MEGA_WALLS("Mw"),
   MINI_WALLS("Mini Walls"),
   MURDER_MYSTERY("MurderMystery"),
   PAINTBALL("Paintball"),
   PARTY_GAMES("PartyGames"),
   QUAKECRAFT("Quakecraft"),
   SKY_BLOCK("SBScoreboard"),
   SKY_WARS("SForeboard"),
   SPEED_UHC("SWScoreboard"),
   SUPER_SMASH("supersmash"),
   THE_PIT("Pit"),
   THE_WALLS("W2Scoreboard"),
   THROW_OUT("THROWOUT"),
   TNT_GAMES("TNT"),
   TOWER_WARS("tw_mainsb"),
   TURBO_KART_RACERS("gingerbread"),
   UHC("§e§lHYPIXEL"),
   VAMPIRE_Z("vampireZ"),
   WARLORDS("WARLORDS"),
   ZOMBIES("ZScoreboard");

   private final Set<String> scoreboardNames;

   HypixelGameType(String text) {
      this.scoreboardNames = Set.of(text);
   }

   HypixelGameType(String... texts) {
      this.scoreboardNames = Set.of(texts);
   }

   public Set<String> getScoreboardNames() {
      return this.scoreboardNames;
   }

   public static HypixelGameType getByScoreName(String text) {
      return Arrays.stream(values()).filter(hypixelGameType -> hypixelGameType.getScoreboardNames().contains(text)).findFirst().orElse(null);
   }

   HypixelGameType() {
      this.scoreboardNames = Collections.emptySet();
   }
}
