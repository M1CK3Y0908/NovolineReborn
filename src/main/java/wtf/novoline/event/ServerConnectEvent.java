package wtf.novoline.event;

public class ServerConnectEvent extends Event {
   public String connectingHostname;
   public String connectingIp;

   public ServerConnectEvent(String text, String text2) {
      this.connectingHostname = text;
      this.connectingIp = text2;
   }
}
