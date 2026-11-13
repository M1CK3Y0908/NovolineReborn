package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public interface IWorldAccess {
   void notifyLightSet(BlockPos blockPos);

   void onEntityRemoved(Entity entity2);

   void playSound(String text, double d, double d2, double d3, float f, float f2);

   void spawnParticle(int i, boolean flag, double d, double d2, double d3, double d4, double d5, double d6, int... iArray);

   void playRecord(String text, BlockPos blockPos);

   void onEntityAdded(Entity entity2);

   void markBlockForUpdate(BlockPos blockPos);

   void playAuxSFX(EntityPlayer entityPlayer, int i, BlockPos blockPos, int i2);

   void markBlockRangeForRenderUpdate(int i, int i2, int i3, int i4, int i5, int i6);

   void sendBlockBreakProgress(int i, BlockPos blockPos, int i2);

   void playSoundToNearExcept(EntityPlayer entityPlayer, String text, double d, double d2, double d3, float f, float f2);

   void broadcastSound(int i, BlockPos blockPos, int i2);
}
