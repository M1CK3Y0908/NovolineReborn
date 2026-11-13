package wtf.novoline.client.discord;

import com.google.gson.annotations.SerializedName;
import java.util.logging.Logger;

public abstract class GatewayPacket implements GatewayMessage {
   @SerializedName("op")
   private final int op;
   protected static final Logger logger = Logger.getLogger("GatewayPacketHandler");

   public int getOp() {
      return this.op;
   }

   public GatewayPacket(int i) {
      this.op = i;
   }
}
