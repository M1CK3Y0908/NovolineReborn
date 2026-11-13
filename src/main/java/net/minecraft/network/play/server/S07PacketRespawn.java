package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;

public class S07PacketRespawn implements Packet<INetHandlerPlayClient> {
   private int dimensionID;
   private WorldType worldType;
   private EnumDifficulty difficulty;
   private net.minecraft.world.WorldSettings.GameType gameType;

   public S07PacketRespawn() {
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleRespawn(this);
   }

   public S07PacketRespawn(int dimensionIDIn, EnumDifficulty difficultyIn, WorldType worldTypeIn, net.minecraft.world.WorldSettings.GameType gameTypeIn) {
      this.dimensionID = dimensionIDIn;
      this.difficulty = difficultyIn;
      this.gameType = gameTypeIn;
      this.worldType = worldTypeIn;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public int getDimensionID() {
      return this.dimensionID;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.dimensionID);
      buf.writeByte(this.difficulty.getDifficultyId());
      buf.writeByte(this.gameType.getID());
      buf.writeString(this.worldType.getWorldTypeName());
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.dimensionID = buf.readInt();
      this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
      this.gameType = net.minecraft.world.WorldSettings.GameType.getByID(buf.readUnsignedByte());
      this.worldType = WorldType.parseWorldType(buf.readStringFromBuffer(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }
   }
}
