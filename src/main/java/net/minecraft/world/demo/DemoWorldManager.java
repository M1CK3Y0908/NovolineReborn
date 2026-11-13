package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {
   private int intValue;
   private boolean flag;
   private boolean demoTimeExpired;
   private int intValue2;

   @Override
   public void onBlockClicked(BlockPos pos, EnumFacing side) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
      } else {
         super.onBlockClicked(pos, side);
      }
   }

   private void sendDemoReminder() {
      if (this.intValue2 > 100) {
         this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.reminder"));
         this.intValue2 = 0;
      }
   }

   @Override
   public boolean tryUseItem(EntityPlayer player, World worldIn, ItemStack stack) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.tryUseItem(player, worldIn, stack);
      }
   }

   public DemoWorldManager(World worldIn) {
      super(worldIn);
   }

   @Override
   public void blockRemoving(BlockPos pos) {
      if (!this.demoTimeExpired) {
         super.blockRemoving(pos);
      }
   }

   @Override
   public boolean activateBlockOrUseItem(
      EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ
   ) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.activateBlockOrUseItem(player, worldIn, stack, pos, side, offsetX, offsetY, offsetZ);
      }
   }

   @Override
   public boolean tryHarvestBlock(BlockPos pos) {
      return this.demoTimeExpired ? false : super.tryHarvestBlock(pos);
   }

   @Override
   public void updateBlockRemoving() {
      super.updateBlockRemoving();
      this.intValue++;
      long i = this.theWorld.getTotalWorldTime();
      long j = i / 24000L + 1L;
      if (!this.flag && this.intValue > 20) {
         this.flag = true;
         this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 0.0F));
      }

      this.demoTimeExpired = i > 120500L;
      if (this.demoTimeExpired) {
         this.intValue2++;
      }

      if (i % 24000L == 500L) {
         if (j <= 6L) {
            this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day." + j));
         }
      } else if (j == 1L) {
         if (i == 100L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 101.0F));
         } else if (i == 175L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 102.0F));
         } else if (i == 250L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 103.0F));
         }
      } else if (j == 5L && i % 24000L == 22000L) {
         this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day.warning"));
      }
   }
}
