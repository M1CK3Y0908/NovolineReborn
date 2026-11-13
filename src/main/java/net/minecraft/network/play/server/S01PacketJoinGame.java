package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame implements Packet<INetHandlerPlayClient> {
   private WorldType worldType;
   private EnumDifficulty difficulty;
   private net.minecraft.world.WorldSettings.GameType gameType;
   private boolean reducedDebugInfo;
   private int entityId;
   private int dimension;
   private int maxPlayers;
   private boolean hardcoreMode;

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readInt();
      int unsignedByte = buf.readUnsignedByte();
      this.hardcoreMode = (unsignedByte & 8) == 8;
      unsignedByte &= -9;
      this.gameType = net.minecraft.world.WorldSettings.GameType.getByID(unsignedByte);
      this.dimension = buf.readByte();
      this.difficulty = EnumDifficulty.getDifficultyEnum(buf.readUnsignedByte());
      this.maxPlayers = buf.readUnsignedByte();
      this.worldType = WorldType.parseWorldType(buf.readStringFromBuffer(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.reducedDebugInfo = buf.readBoolean();
   }

   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public int getDimension() {
      return this.dimension;
   }

   public S01PacketJoinGame() {
   }

   public S01PacketJoinGame(
      int entityIdIn,
      net.minecraft.world.WorldSettings.GameType gameTypeIn,
      boolean hardcoreModeIn,
      int dimensionIn,
      EnumDifficulty difficultyIn,
      int maxPlayersIn,
      WorldType worldTypeIn,
      boolean reducedDebugInfoIn
   ) {
      this.entityId = entityIdIn;
      this.dimension = dimensionIn;
      this.difficulty = difficultyIn;
      this.gameType = gameTypeIn;
      this.maxPlayers = maxPlayersIn;
      this.hardcoreMode = hardcoreModeIn;
      this.worldType = worldTypeIn;
      this.reducedDebugInfo = reducedDebugInfoIn;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleJoinGame(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.entityId);
      int id = this.gameType.getID();
      if (this.hardcoreMode) {
         id |= 8;
      }

      buf.writeByte(id);
      buf.writeByte(this.dimension);
      buf.writeByte(this.difficulty.getDifficultyId());
      buf.writeByte(this.maxPlayers);
      buf.writeString(this.worldType.getWorldTypeName());
      buf.writeBoolean(this.reducedDebugInfo);
   }
}
