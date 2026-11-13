package wtf.novoline.command;

import java.util.Arrays;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.IrcConnection;
import wtf.novoline.client.PutRoomsMessagesRequest;
import wtf.novoline.client.discord.GatewayClient;
import wtf.novoline.client.discord.Snowflake;

public class ChatCommand extends Command {
   public ChatCommand() {
      super("send a message to the public chat", "c", "chat");
   }

   @Override
   public void execute(String[] texts) {
      if (texts.length > 1) {
         String text = String.join(" ", Arrays.copyOfRange(texts, 1, texts.length));
         IrcConnection ircConnection = this.novoline.getIrcConnection();
         GatewayClient gatewayClient = ircConnection.getGatewayClient();
         ApiClient apiClient = ircConnection.getApiClient();
         Snowflake snowflake = gatewayClient.getRooms().keySet().stream().findFirst().orElseThrow();
         apiClient.sendRoomMessage(PutRoomsMessagesRequest.createPutRoomsMessagesRequest(snowflake, text)).whenComplete((gatewayChatMessage, throwable) -> {
            if (throwable != null) {
               String text2 = throwable.getCause() == null ? throwable.getMessage() : throwable.getCause().getMessage();
               this.sendMessage(EnumChatFormatting.RED + "Error sending message: " + text2);
            }
         });
      } else {
         this.sendLocalized("command.wrong_arguments");
         this.sendMessage(".chat <message>");
      }
   }
}
