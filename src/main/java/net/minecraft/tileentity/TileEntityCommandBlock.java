package net.minecraft.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntityCommandBlock extends TileEntity {
   private final CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
      @Override
      public void updateCommand() {
         TileEntityCommandBlock.this.getWorld().markBlockForUpdate(TileEntityCommandBlock.this.pos);
      }

      @Override
      public void setCommand(String command) {
         super.setCommand(command);
         TileEntityCommandBlock.this.markDirty();
      }

      @Override
      public void writeCommandBlockData(ByteBuf buffer) {
         buffer.writeInt(TileEntityCommandBlock.this.pos.getX());
         buffer.writeInt(TileEntityCommandBlock.this.pos.getY());
         buffer.writeInt(TileEntityCommandBlock.this.pos.getZ());
      }

      @Override
      public Vec3 getPositionVector() {
         return new Vec3(
            TileEntityCommandBlock.this.pos.getX() + 0.5, TileEntityCommandBlock.this.pos.getY() + 0.5, TileEntityCommandBlock.this.pos.getZ() + 0.5
         );
      }

      @Override
      public World getEntityWorld() {
         return TileEntityCommandBlock.this.getWorld();
      }

      @Override
      public BlockPos getPosition() {
         return TileEntityCommandBlock.this.pos;
      }

      @Override
      public int getCommandBlockType() {
         return 0;
      }

      @Override
      public Entity getCommandSenderEntity() {
         return null;
      }
   };

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 2, nbtTagCompound);
   }

   public CommandResultStats getCommandResultStats() {
      return this.commandBlockLogic.getCommandResultStats();
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.commandBlockLogic.readDataFromNBT(compound);
   }

   @Override
   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      this.commandBlockLogic.writeDataToNBT(compound);
   }
}
