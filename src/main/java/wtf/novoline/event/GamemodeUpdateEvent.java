package wtf.novoline.event;

import wtf.novoline.enums.HypixelGameType;

public class GamemodeUpdateEvent extends Event {
   public final HypixelGameType hypixelGameType;

   public GamemodeUpdateEvent(HypixelGameType hypixelGameType2) {
      this.hypixelGameType = hypixelGameType2;
   }
}
