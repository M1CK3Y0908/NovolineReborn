package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience implements Packet<INetHandlerPlayClient> {
   private int totalExperience;
   private float experienceProgress;
   private int level;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.experienceProgress);
      buf.writeVarIntToBuffer(this.level);
      buf.writeVarIntToBuffer(this.totalExperience);
   }

   public int getLevel() {
      return this.level;
   }

   public S1FPacketSetExperience() {
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSetExperience(this);
   }

   public int getTotalExperience() {
      return this.totalExperience;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.experienceProgress = buf.readFloat();
      this.level = buf.readVarIntFromBuffer();
      this.totalExperience = buf.readVarIntFromBuffer();
   }

   public float getExperienceProgress() {
      return this.experienceProgress;
   }

   public S1FPacketSetExperience(float experienceProgressIn, int totalExperienceIn, int levelIn) {
      this.experienceProgress = experienceProgressIn;
      this.totalExperience = totalExperienceIn;
      this.level = levelIn;
   }
}
