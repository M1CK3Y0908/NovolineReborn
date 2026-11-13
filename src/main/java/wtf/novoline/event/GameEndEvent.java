package wtf.novoline.event;

public class GameEndEvent extends Event {
   public final String gameType;
   public final boolean win;

   public GameEndEvent(String text, boolean flag) {
      this.gameType = text;
      this.win = flag;
   }
}
