package net.optifine.override;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class PlayerControllerOF extends PlayerControllerMP {
   private boolean acting = false;
   private BlockPos lastClickBlockPos = null;
   private Entity lastClickEntity = null;

   @Override
   public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity target, MovingObjectPosition ray) {
      this.lastClickEntity = target;
      return super.isPlayerRightClickingOnEntity(player, target, ray);
   }

   @Override
   public boolean clickBlock(BlockPos blockPos, EnumFacing enumFacing) {
      this.acting = true;
      this.lastClickBlockPos = blockPos;
      boolean flag = super.clickBlock(blockPos, enumFacing);
      this.acting = false;
      return flag;
   }

   @Override
   public boolean interactWithEntitySendPacket(EntityPlayer player, Entity target) {
      this.lastClickEntity = target;
      return super.interactWithEntitySendPacket(player, target);
   }

   @Override
   public boolean performRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack stack, BlockPos pos, EnumFacing facing, Vec3 hitVec) {
      this.acting = true;
      this.lastClickBlockPos = pos;
      boolean conditionMet = super.performRightClick(player, worldIn, stack, pos, facing, hitVec);
      this.acting = false;
      return conditionMet;
   }

   public Entity getLastClickEntity() {
      return this.lastClickEntity;
   }

   public BlockPos getLastClickBlockPos() {
      return this.lastClickBlockPos;
   }

   public boolean isActing() {
      return this.acting;
   }

   public PlayerControllerOF(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      super(mcIn, netHandler);
   }

   @Override
   public boolean sendUseItem(EntityPlayer player, World worldIn, ItemStack stack) {
      this.acting = true;
      boolean conditionMet = super.sendUseItem(player, worldIn, stack);
      this.acting = false;
      return conditionMet;
   }

   @Override
   public boolean onPlayerDamageBlock(BlockPos blockPos, EnumFacing enumFacing) {
      this.acting = true;
      this.lastClickBlockPos = blockPos;
      boolean flag = super.onPlayerDamageBlock(blockPos, enumFacing);
      this.acting = false;
      return flag;
   }
}
