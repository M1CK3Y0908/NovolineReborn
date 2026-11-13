package wtf.novoline.utils;

import net.minecraft.client.Minecraft;

public class ChatMacro {
   private final String name;
   private final int keyCode;
   private final String message;

   public String getMessage() {
      return this.message;
   }

   public ChatMacro(String text, int i, String text2) {
      this.name = text;
      this.keyCode = i;
      this.message = text2;
   }

   public String getName() {
      return this.name;
   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public void send() {
      Minecraft.getMinecraft().thePlayer.sendChatMessage(this.message);
   }
}
