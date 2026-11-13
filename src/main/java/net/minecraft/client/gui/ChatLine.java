package net.minecraft.client.gui;

import net.minecraft.util.IChatComponent;

public class ChatLine {
   private final IChatComponent lineString;
   private final int updateCounterCreated;
   private final int chatLineID;

   public IChatComponent getChatComponent() {
      return this.lineString;
   }

   public ChatLine(int updateCounterCreated, IChatComponent lineString, int chatLineID) {
      this.lineString = lineString;
      this.updateCounterCreated = updateCounterCreated;
      this.chatLineID = chatLineID;
   }

   public int getChatLineID() {
      return this.chatLineID;
   }

   public int getUpdatedCounter() {
      return this.updateCounterCreated;
   }
}
