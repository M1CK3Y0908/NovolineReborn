package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   private final MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
      @Override
      public void setRandomEntity(WeightedRandomMinecart weightedRandomMinecart) {
         super.setRandomEntity(weightedRandomMinecart);
         if (this.getSpawnerWorld() != null) {
            this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.pos);
         }
      }

      @Override
      public World getSpawnerWorld() {
         return TileEntityMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return TileEntityMobSpawner.this.pos;
      }

      @Override
      public void update() {
         TileEntityMobSpawner.this.worldObj.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.mob_spawner, 1, 0);
      }
   };

   @Override
   public boolean onlyOpsCanSetNbt() {
      return true;
   }

   public MobSpawnerBaseLogic getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      nbtTagCompound.removeTag("SpawnPotentials");
      return new S35PacketUpdateTileEntity(this.pos, 1, nbtTagCompound);
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      return this.spawnerLogic.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      this.spawnerLogic.writeToNBT(compound);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.spawnerLogic.readFromNBT(compound);
   }

   @Override
   public void update() {
      this.spawnerLogic.updateSpawner();
   }
}
