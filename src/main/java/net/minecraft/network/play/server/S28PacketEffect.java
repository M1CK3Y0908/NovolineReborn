package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;

public class S28PacketEffect implements Packet<INetHandlerPlayClient> {
   private int soundType;
   private BlockPos soundPos;
   private int soundData;
   private boolean serverWide;

   public boolean isSoundServerwide() {
      return this.serverWide;
   }

   public int getSoundData() {
      return this.soundData;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.soundType = buf.readInt();
      this.soundPos = buf.readBlockPos();
      this.soundData = buf.readInt();
      this.serverWide = buf.readBoolean();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.soundType);
      buf.writeBlockPos(this.soundPos);
      buf.writeInt(this.soundData);
      buf.writeBoolean(this.serverWide);
   }

   public int getSoundType() {
      return this.soundType;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEffect(this);
   }

   public S28PacketEffect(int soundTypeIn, BlockPos soundPosIn, int soundDataIn, boolean serverWideIn) {
      this.soundType = soundTypeIn;
      this.soundPos = soundPosIn;
      this.soundData = soundDataIn;
      this.serverWide = serverWideIn;
   }

   public BlockPos getSoundPos() {
      return this.soundPos;
   }

   public S28PacketEffect() {
   }
}
