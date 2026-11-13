package wtf.novoline.client.discord;

public class ConnectionClosedEvent implements GatewayEvent {
   private Exception exception;
   private final int code;
   private final String reason;
   private final boolean remote;

   public Exception getException() {
      return this.exception;
   }

   public void setException(Exception exception2) {
      this.exception = exception2;
   }

   public String getReason() {
      return this.reason;
   }

   private ConnectionClosedEvent(int i, String text, boolean flag) {
      this.code = i;
      this.reason = text;
      this.remote = flag;
   }

   public int getCode() {
      return this.code;
   }

   public static ConnectionClosedEvent createConnectionClosedEvent(int i, String text, boolean flag) {
      return new ConnectionClosedEvent(i, text, flag);
   }

   public boolean isRemote() {
      return this.remote;
   }
}
