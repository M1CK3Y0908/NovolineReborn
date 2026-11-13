package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import wtf.novoline.Novoline;
import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.module.ModuleManager;

public class PlayerControllerMP {
   private float curBlockDamageMP;
   public int blockHitDelay;
   private BlockPos currentBlock = new BlockPos(-1, -1, -1);
   private ItemStack currentItemHittingBlock;
   private float stepSoundTickCounter;
   private net.minecraft.world.WorldSettings.GameType currentGameType = net.minecraft.world.WorldSettings.GameType.SURVIVAL;
   private boolean isHittingBlock;
   private int currentPlayerItem;
   private final Minecraft mc;
   private final NetHandlerPlayClient netClientHandler;

   public boolean isRidingHorse() {
      return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public void sendPacketDropItem(ItemStack itemStackIn) {
      if (this.currentGameType.isCreative() && itemStackIn != null) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
      }
   }

   private void syncCurrentPlayItem() {
      int currentItem2 = this.mc.thePlayer.inventory.currentItem;
      SyncCurrentSlotEvent syncCurrentSlotEvent = new SyncCurrentSlotEvent(this.currentPlayerItem, currentItem2);
      Novoline.getNovoline().getBus().post(syncCurrentSlotEvent);
      if (!syncCurrentSlotEvent.isCancelled()) {
         if (currentItem2 != this.currentPlayerItem) {
            this.currentPlayerItem = currentItem2;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
         }
      }
   }

   public void flipPlayer(EntityPlayer playerIn) {
      playerIn.rotationYaw = -180.0F;
   }

   public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
      if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, pos, facing)) {
         playerController.onPlayerDestroyBlock(pos, facing);
      }
   }

   private boolean isHittingPosition(BlockPos pos) {
      ItemStack itemStack = this.mc.thePlayer.getHeldItem();
      boolean conditionMet = this.currentItemHittingBlock == null && itemStack == null;
      if (this.currentItemHittingBlock != null && itemStack != null) {
         conditionMet = itemStack.getItem() == this.currentItemHittingBlock.getItem()
            && ItemStack.areItemStackTagsEqual(itemStack, this.currentItemHittingBlock)
            && (itemStack.isItemStackDamageable() || itemStack.getMetadata() == this.currentItemHittingBlock.getMetadata());
      }

      return pos.equals(this.currentBlock) && conditionMet;
   }

   public void onStoppedUsingItem(EntityPlayer playerIn) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      playerIn.stopUsingItem();
   }

   public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      this.mc = mcIn;
      this.netClientHandler = netHandler;
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         this.blockHitDelay--;
         return true;
      }

      if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock)) {
         this.blockHitDelay = 5;
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
         clickBlockCreative(this.mc, this, posBlock, directionFacing);
         return true;
      }

      if (this.isHittingPosition(posBlock)) {
         Block block = this.mc.theWorld.getBlockState(posBlock).getBlock();
         if (block.getMaterial() == Material.air) {
            this.isHittingBlock = false;
            return false;
         }

         this.curBlockDamageMP = this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock);
         if (this.stepSoundTickCounter % 4.0F == 0.0F) {
            this.mc
               .getSoundHandler()
               .playSound(
                  new PositionedSoundRecord(
                     new ResourceLocation(block.stepSound.getStepSound()),
                     (block.stepSound.getVolume() + 1.0F) / 8.0F,
                     block.stepSound.getFrequency() * 0.5F,
                     posBlock.getX() + 0.5F,
                     posBlock.getY() + 0.5F,
                     posBlock.getZ() + 0.5F
                  )
               );
         }

         this.stepSoundTickCounter++;
         if (this.curBlockDamageMP >= 1.0F) {
            this.isHittingBlock = false;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
            this.onPlayerDestroyBlock(posBlock, directionFacing);
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.blockHitDelay = 5;
         }

         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         return true;
      } else {
         return this.clickBlock(posBlock, directionFacing);
      }
   }

   public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
      short nextTransactionID = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
      ItemStack itemStack3 = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
      this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, itemStack3, nextTransactionID));
      return itemStack3;
   }

   public boolean sendUseItem(EntityPlayer entityPlayer, World world2, ItemStack itemStack3) {
      if (this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         return false;
      }

      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(entityPlayer.inventory.getCurrentItem()));
      int stackSize2 = itemStack3.stackSize;
      ItemStack itemStack4 = itemStack3.useItemRightClick(world2, entityPlayer);
      if (itemStack4 != itemStack3 || itemStack4 != null && itemStack4.stackSize != stackSize2) {
         entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = itemStack4;
         if (itemStack4.stackSize == 0) {
            entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public net.minecraft.world.WorldSettings.GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.netClientHandler
            .addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
      }
   }

   public void updateController() {
      this.syncCurrentPlayItem();
      if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
         this.netClientHandler.getNetworkManager().processReceivedPackets();
      } else {
         this.netClientHandler.getNetworkManager().checkDisconnected();
      }
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }

   public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT));
      return this.currentGameType != net.minecraft.world.WorldSettings.GameType.SPECTATOR && playerIn.interactWith(targetEntity);
   }

   public void setPlayerCapabilities(EntityPlayer player) {
      this.currentGameType.configurePlayerCapabilities(player.capabilities);
   }

   public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity entityIn, MovingObjectPosition movingObject) {
      this.syncCurrentPlayItem();
      Vec3 vec3 = new Vec3(movingObject.hitVec.xCoord - entityIn.posX, movingObject.hitVec.yCoord - entityIn.posY, movingObject.hitVec.zCoord - entityIn.posZ);
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entityIn, vec3));
      return this.currentGameType != net.minecraft.world.WorldSettings.GameType.SPECTATOR && entityIn.interactAt(player, vec3);
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            ItemStack itemStack = this.mc.thePlayer.getCurrentEquippedItem();
            if (itemStack == null) {
               return false;
            }

            if (!itemStack.canDestroy(block)) {
               return false;
            }
         }
      }

      if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      World world = this.mc.theWorld;
      IBlockState blockState = world.getBlockState(pos);
      Block block2 = blockState.getBlock();
      if (block2.getMaterial() == Material.air) {
         return false;
      }

      world.playAuxSFX(2001, pos, Block.getStateId(blockState));
      boolean conditionMet = world.setBlockToAir(pos);
      if (conditionMet) {
         block2.onBlockDestroyedByPlayer(world, pos, blockState);
      }

      this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
      if (!this.currentGameType.isCreative()) {
         ItemStack itemStack2 = this.mc.thePlayer.getCurrentEquippedItem();
         if (itemStack2 != null) {
            itemStack2.onBlockDestroyed(world, block2, pos, this.mc.thePlayer);
            if (itemStack2.stackSize == 0) {
               this.mc.thePlayer.destroyCurrentEquippedItem();
            }
         }
      }

      return conditionMet;
   }

   public float getBlockReachDistance() {
      return this.currentGameType.isCreative() ? 5.0F : (ModuleManager.reach.isEnabled() ? ModuleManager.reach.getBlockReach().getCurrentValue() : 4.0F);
   }

   public void sendEnchantPacket(int windowID, int button) {
      this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(windowID, button));
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
      }
   }

   public EntityPlayerSP createPlayer(World worldIn, StatFileWriter statWriter) {
      return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, statWriter);
   }

   public boolean clickBlock(BlockPos loc, EnumFacing face) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(loc).getBlock();
            ItemStack itemStack = this.mc.thePlayer.getCurrentEquippedItem();
            if (itemStack == null) {
               return false;
            }

            if (!itemStack.canDestroy(block)) {
               return false;
            }
         }
      }

      if (!this.mc.theWorld.getWorldBorder().contains(loc)) {
         return false;
      }

      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         clickBlockCreative(this.mc, this, loc, face);
         this.blockHitDelay = 5;
      } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
         if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
         }

         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         Block block2 = this.mc.theWorld.getBlockState(loc).getBlock();
         boolean flag = block2.getMaterial() != Material.air;
         if (flag && this.curBlockDamageMP == 0.0F) {
            block2.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
         }

         if (flag && block2.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F) {
            this.onPlayerDestroyBlock(loc, face);
         } else {
            this.isHittingBlock = true;
            this.currentBlock = loc;
            this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         }
      }

      return true;
   }

   public boolean isSpectator() {
      return this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR;
   }

   public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK));
      if (this.currentGameType != net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         playerIn.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   public boolean performRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec) {
      this.syncCurrentPlayItem();
      float f = (float)(hitVec.xCoord - hitPos.getX());
      float f2 = (float)(hitVec.yCoord - hitPos.getY());
      float f3 = (float)(hitVec.zCoord - hitPos.getZ());
      boolean conditionMet = false;
      if (!this.mc.theWorld.getWorldBorder().contains(hitPos)) {
         return false;
      }

      if (this.currentGameType != net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         IBlockState blockState = worldIn.getBlockState(hitPos);
         if ((!player.isSneaking() || player.getHeldItem() == null)
            && blockState.getBlock().onBlockActivated(worldIn, hitPos, blockState, player, side, f, f2, f3)) {
            conditionMet = true;
         }

         if (!conditionMet && heldStack != null && heldStack.getItem() instanceof ItemBlock) {
            ItemBlock itemBlock = (ItemBlock)heldStack.getItem();
            if (!itemBlock.canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack)) {
               return false;
            }
         }
      }

      this.netClientHandler
         .addToSendQueue(new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(), player.inventory.getCurrentItem(), f, f2, f3));
      if (conditionMet || this.currentGameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         return true;
      } else if (heldStack == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int metadata = heldStack.getMetadata();
         int stackSize2 = heldStack.stackSize;
         boolean conditionMet2 = heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
         heldStack.setItemDamage(metadata);
         heldStack.stackSize = stackSize2;
         return conditionMet2;
      } else {
         return heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
      }
   }

   public void setGameType(net.minecraft.world.WorldSettings.GameType type) {
      this.currentGameType = type;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }
}
