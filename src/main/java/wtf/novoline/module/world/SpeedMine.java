package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;

public class SpeedMine extends Module {
   private BlockPos blockPos;
   private float breakProgress;
   private EnumFacing enumFacing;
   private final NumberSetting speedMine = settingManager.register(new NumberSetting(this, "Acceleration factor", 1.4F, 1.0F, 2.0F));
   private boolean breaking = false;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (minecraft.playerController != null
         && !minecraft.playerController.getCurrentGameType().isCreative()
         && !minecraft.playerController.extendedReach()
         && packetSendEvent.packet instanceof C07PacketPlayerDigging c07PacketPlayerDigging) {
         switch (c07PacketPlayerDigging.status) {
            case START_DESTROY_BLOCK:
               this.breaking = true;
               this.breakProgress = 0.0F;
               this.blockPos = c07PacketPlayerDigging.position;
               this.enumFacing = c07PacketPlayerDigging.facing;
               break;
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
               this.breaking = false;
               this.breakProgress = 0.0F;
               this.blockPos = null;
               this.enumFacing = null;
         }
      }
   };
   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (minecraft.playerController != null && !minecraft.playerController.getCurrentGameType().isCreative()) {
         if (minecraft.playerController.extendedReach()) {
            minecraft.playerController.blockHitDelay = 0;
         } else if (this.breaking && minecraft.playerController.getIsHittingBlock()) {
            BlockPos blockPos2 = minecraft.objectMouseOver.getBlockPos();
            if (blockPos2 == null) {
               return;
            }

            Block block2 = minecraft.theWorld.getBlockState(blockPos2).getBlock();
            if (minecraft.thePlayer.canHarvestBlock(block2)) {
               float currentValue = this.speedMine.getCurrentValue();
               this.breakProgress = this.breakProgress + block2.getPlayerRelativeBlockHardness(minecraft.thePlayer, minecraft.theWorld, this.blockPos) * currentValue;
               if (this.breakProgress >= 1.0F) {
                  minecraft.theWorld.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                  this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.blockPos, this.enumFacing));
               }
            }
         }
      }
   };

   public SpeedMine() {
      super(Category.WORLD, "SpeedMine", "Allows you to mine blocks faster");
   }

   @Override
   protected void onDisable() {
      this.breakProgress = 0.0F;
      this.breaking = false;
      this.blockPos = null;
      this.enumFacing = null;
   }
}
