package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemInWorldManager {
   private int initialDamage;
   private net.minecraft.world.WorldSettings.GameType gameType = net.minecraft.world.WorldSettings.GameType.NOT_SET;
   private BlockPos destroyingBlockPos = BlockPos.ORIGIN;
   public EntityPlayerMP thisPlayerMP;
   private BlockPos delayedDestroyBlockPos = BlockPos.ORIGIN;
   private boolean receivedFinishDiggingPacket;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;
   private int curblockDamage;
   private boolean isDestroyingBlock;
   public World theWorld;

   public boolean tryHarvestBlock(BlockPos pos) {
      if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      IBlockState blockState = this.theWorld.getBlockState(pos);
      TileEntity tileEntity = this.theWorld.getTileEntity(pos);
      if (this.gameType.isAdventure()) {
         if (this.gameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.thisPlayerMP.isAllowEdit()) {
            ItemStack itemStack = this.thisPlayerMP.getCurrentEquippedItem();
            if (itemStack == null) {
               return false;
            }

            if (!itemStack.canDestroy(blockState.getBlock())) {
               return false;
            }
         }
      }

      this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, pos, Block.getStateId(blockState));
      boolean conditionMet = this.removeBlock(pos);
      if (this.isCreative()) {
         this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, pos));
      } else {
         ItemStack itemStack2 = this.thisPlayerMP.getCurrentEquippedItem();
         boolean canHarvestBlock = this.thisPlayerMP.canHarvestBlock(blockState.getBlock());
         if (itemStack2 != null) {
            itemStack2.onBlockDestroyed(this.theWorld, blockState.getBlock(), pos, this.thisPlayerMP);
            if (itemStack2.stackSize == 0) {
               this.thisPlayerMP.destroyCurrentEquippedItem();
            }
         }

         if (conditionMet && canHarvestBlock) {
            blockState.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, pos, blockState, tileEntity);
         }
      }

      return conditionMet;
   }

   public boolean tryUseItem(EntityPlayer player, World worldIn, ItemStack stack) {
      if (this.gameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         return false;
      }

      int stackSize2 = stack.stackSize;
      int metadata = stack.getMetadata();
      ItemStack itemStack = stack.useItemRightClick(worldIn, player);
      if (itemStack != stack
         || itemStack != null && (itemStack.stackSize != stackSize2 || itemStack.getMaxItemUseDuration() > 0 || itemStack.getMetadata() != metadata)) {
         player.inventory.mainInventory[player.inventory.currentItem] = itemStack;
         if (this.isCreative()) {
            itemStack.stackSize = stackSize2;
            if (itemStack.isItemStackDamageable()) {
               itemStack.setItemDamage(metadata);
            }
         }

         if (itemStack.stackSize == 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
         }

         if (!player.isUsingItem()) {
            ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
         }

         return true;
      } else {
         return false;
      }
   }

   public void setGameType(net.minecraft.world.WorldSettings.GameType type) {
      this.gameType = type;
      type.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
      this.thisPlayerMP.sendPlayerAbilities();
      this.thisPlayerMP
         .mcServer
         .getConfigurationManager()
         .sendPacketToAllPlayers(new S38PacketPlayerListItem(net.minecraft.network.play.server.S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, this.thisPlayerMP));
   }

   public void blockRemoving(BlockPos pos) {
      if (pos.equals(this.destroyingBlockPos)) {
         int i = this.curblockDamage - this.initialDamage;
         Block block = this.theWorld.getBlockState(pos).getBlock();
         if (block.getMaterial() != Material.air) {
            float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos) * (i + 1);
            if (f >= 0.7F) {
               this.isDestroyingBlock = false;
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, -1);
               this.tryHarvestBlock(pos);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.delayedDestroyBlockPos = pos;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }
   }

   public void onBlockClicked(BlockPos pos, EnumFacing side) {
      if (this.isCreative()) {
         if (!this.theWorld.extinguishFire((EntityPlayer)null, pos, side)) {
            this.tryHarvestBlock(pos);
         }
      } else {
         Block block = this.theWorld.getBlockState(pos).getBlock();
         if (this.gameType.isAdventure()) {
            if (this.gameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
               return;
            }

            if (!this.thisPlayerMP.isAllowEdit()) {
               ItemStack itemStack = this.thisPlayerMP.getCurrentEquippedItem();
               if (itemStack == null) {
                  return;
               }

               if (!itemStack.canDestroy(block)) {
                  return;
               }
            }
         }

         this.theWorld.extinguishFire((EntityPlayer)null, pos, side);
         this.initialDamage = this.curblockDamage;
         float f = 1.0F;
         if (block.getMaterial() != Material.air) {
            block.onBlockClicked(this.theWorld, pos, this.thisPlayerMP);
            f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos);
         }

         if (block.getMaterial() != Material.air && f >= 1.0F) {
            this.tryHarvestBlock(pos);
         } else {
            this.isDestroyingBlock = true;
            this.destroyingBlockPos = pos;
            int i = (int)(f * 10.0F);
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, i);
            this.durabilityRemainingOnBlock = i;
         }
      }
   }

   public ItemInWorldManager(World worldIn) {
      this.theWorld = worldIn;
   }

   private boolean removeBlock(BlockPos pos) {
      IBlockState blockState = this.theWorld.getBlockState(pos);
      blockState.getBlock().onBlockHarvested(this.theWorld, pos, blockState, this.thisPlayerMP);
      boolean conditionMet = this.theWorld.setBlockToAir(pos);
      if (conditionMet) {
         blockState.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, blockState);
      }

      return conditionMet;
   }

   public boolean activateBlockOrUseItem(
      EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ
   ) {
      if (this.gameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof ILockableContainer) {
            Block block = worldIn.getBlockState(pos).getBlock();
            ILockableContainer lockableContainer = (ILockableContainer)tileEntity;
            if (lockableContainer instanceof TileEntityChest && block instanceof BlockChest) {
               lockableContainer = ((BlockChest)block).getLockableContainer(worldIn, pos);
            }

            if (lockableContainer != null) {
               player.displayGUIChest(lockableContainer);
               return true;
            }
         } else if (tileEntity instanceof IInventory) {
            player.displayGUIChest((IInventory)tileEntity);
            return true;
         }

         return false;
      } else {
         if (!player.isSneaking() || player.getHeldItem() == null) {
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock().onBlockActivated(worldIn, pos, blockState, player, side, offsetX, offsetY, offsetZ)) {
               return true;
            }
         }

         if (stack == null) {
            return false;
         } else if (this.isCreative()) {
            int metadata = stack.getMetadata();
            int stackSize2 = stack.stackSize;
            boolean conditionMet = stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
            stack.setItemDamage(metadata);
            stack.stackSize = stackSize2;
            return conditionMet;
         } else {
            return stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
         }
      }
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   public void initializeGameType(net.minecraft.world.WorldSettings.GameType type) {
      if (this.gameType == net.minecraft.world.WorldSettings.GameType.NOT_SET) {
         this.gameType = type;
      }

      this.setGameType(this.gameType);
   }

   public void updateBlockRemoving() {
      this.curblockDamage++;
      if (this.receivedFinishDiggingPacket) {
         int i = this.curblockDamage - this.initialBlockDamage;
         Block block = this.theWorld.getBlockState(this.delayedDestroyBlockPos).getBlock();
         if (block.getMaterial() == Material.air) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.delayedDestroyBlockPos)
               * (i + 1);
            int i2 = (int)(f * 10.0F);
            if (i2 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.delayedDestroyBlockPos, i2);
               this.durabilityRemainingOnBlock = i2;
            }

            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.delayedDestroyBlockPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         Block block2 = this.theWorld.getBlockState(this.destroyingBlockPos).getBlock();
         if (block2.getMaterial() == Material.air) {
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyingBlockPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int i3 = this.curblockDamage - this.initialDamage;
            float f2 = block2.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.delayedDestroyBlockPos)
               * (i3 + 1);
            int i4 = (int)(f2 * 10.0F);
            if (i4 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyingBlockPos, i4);
               this.durabilityRemainingOnBlock = i4;
            }
         }
      }
   }

   public void cancelDestroyingBlock() {
      this.isDestroyingBlock = false;
      this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.destroyingBlockPos, -1);
   }

   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public void setWorld(WorldServer serverWorld) {
      this.theWorld = serverWorld;
   }
}
