package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class S33PacketUpdateSign implements Packet<INetHandlerPlayClient> {
   private BlockPos blockPos;
   private World world;
   private IChatComponent[] lines;

   public BlockPos getPos() {
      return this.blockPos;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleUpdateSign(this);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBlockPos(this.blockPos);

      for (int index = 0; index < 4; index++) {
         buf.writeChatComponent(this.lines[index]);
      }
   }

   public S33PacketUpdateSign() {
   }

   public IChatComponent[] getLines() {
      return this.lines;
   }

   public S33PacketUpdateSign(World worldIn, BlockPos blockPosIn, IChatComponent[] linesIn) {
      this.world = worldIn;
      this.blockPos = blockPosIn;
      this.lines = new IChatComponent[]{linesIn[0], linesIn[1], linesIn[2], linesIn[3]};
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.blockPos = buf.readBlockPos();
      this.lines = new IChatComponent[4];

      for (int index = 0; index < 4; index++) {
         this.lines[index] = buf.readChatComponent();
      }
   }
}
