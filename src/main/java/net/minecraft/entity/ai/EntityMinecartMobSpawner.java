package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
   private final MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
      @Override
      public void update() {
         EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte)1);
      }

      @Override
      public World getSpawnerWorld() {
         return EntityMinecartMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.mob_spawner.getDefaultState();
   }

   @Override
   public void handleStatusUpdate(byte id) {
      this.mobSpawnerLogic.setDelayToMin(id);
   }

   public EntityMinecartMobSpawner(World worldIn) {
      super(worldIn);
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      super.writeEntityToNBT(nbtTagCompound);
      this.mobSpawnerLogic.writeToNBT(nbtTagCompound);
   }

   @Override
   public net.minecraft.entity.item.EntityMinecart.EnumMinecartType getMinecartType() {
      return net.minecraft.entity.item.EntityMinecart.EnumMinecartType.SPAWNER;
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      super.readEntityFromNBT(nbtTagCompound);
      this.mobSpawnerLogic.readFromNBT(nbtTagCompound);
   }

   public MobSpawnerBaseLogic getMobSpawnerLogic() {
      return this.mobSpawnerLogic;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      this.mobSpawnerLogic.updateSpawner();
   }
}
