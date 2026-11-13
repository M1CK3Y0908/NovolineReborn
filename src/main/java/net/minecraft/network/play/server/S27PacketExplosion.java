package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
   private double posX;
   private float strength;
   private float sourceX;
   private float sourceZ;
   private double posY;
   private List<BlockPos> affectedBlockPositions;
   private float sourceY;
   private double posZ;

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat((float)this.posX);
      buf.writeFloat((float)this.posY);
      buf.writeFloat((float)this.posZ);
      buf.writeFloat(this.strength);
      buf.writeInt(this.affectedBlockPositions.size());
      int i = (int)this.posX;
      int i2 = (int)this.posY;
      int i3 = (int)this.posZ;

      for (BlockPos blockPos : this.affectedBlockPositions) {
         int yCoordinate = blockPos.getX() - i;
         int zCoordinate = blockPos.getY() - i2;
         int i4 = blockPos.getZ() - i3;
         buf.writeByte(yCoordinate);
         buf.writeByte(zCoordinate);
         buf.writeByte(i4);
      }

      buf.writeFloat(this.sourceX);
      buf.writeFloat(this.sourceY);
      buf.writeFloat(this.sourceZ);
   }

   public S27PacketExplosion() {
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.posX = buf.readFloat();
      this.posY = buf.readFloat();
      this.posZ = buf.readFloat();
      this.strength = buf.readFloat();
      int intValue = buf.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(intValue);
      int i = (int)this.posX;
      int i2 = (int)this.posY;
      int i3 = (int)this.posZ;

      for (int index = 0; index < intValue; index++) {
         int i4 = buf.readByte() + i;
         int i5 = buf.readByte() + i2;
         int i6 = buf.readByte() + i3;
         this.affectedBlockPositions.add(new BlockPos(i4, i5, i6));
      }

      this.sourceX = buf.readFloat();
      this.sourceY = buf.readFloat();
      this.sourceZ = buf.readFloat();
   }

   public float getMotionZ() {
      return this.sourceZ;
   }

   public float getMotionX() {
      return this.sourceX;
   }

   public float getMotionY() {
      return this.sourceY;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleExplosion(this);
   }

   public double getY() {
      return this.posY;
   }

   public float getStrength() {
      return this.strength;
   }

   public double getX() {
      return this.posX;
   }

   public S27PacketExplosion(double x, double y, double z, float strengthIn, List<BlockPos> affectedBlocksIn, Vec3 motion) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      this.strength = strengthIn;
      this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);
      if (motion != null) {
         this.sourceX = (float)motion.xCoord;
         this.sourceY = (float)motion.yCoord;
         this.sourceZ = (float)motion.zCoord;
      }
   }

   public double getZ() {
      return this.posZ;
   }
}
