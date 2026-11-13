package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.novoline.event.BlockCollisionEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.player.PlayerUtils;

public class Jesus extends Module {
   private final ComboSetting jesus = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Solid", "NCP", "Matrix", "MatrixBoost", "MatrixJump", "MatrixGround", "MatrixZoom")
   );
   private final NumberSetting boostValue = settingManager.register(
      new NumberSetting(this, "Boost value", 0.6F, 0.0F, 2.0F) {
         @Override
         public boolean shouldShow() {
            return Jesus.this.jesus.getCurrentElement() == 2
               || Jesus.this.jesus.getCurrentElement() == 3
               || Jesus.this.jesus.getCurrentElement() == 4
               || Jesus.this.jesus.getCurrentElement() == 5;
         }
      }
   );
   private final NumberSetting zoomValue = settingManager.register(new NumberSetting(this, "Zoom value", 3.0F, 0.0F, 15.0F) {
      @Override
      public boolean shouldShow() {
         return Jesus.this.jesus.getCurrentElement() == 6;
      }
   });
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.jesus.getCurrentElementAsString());
      int currentElement = this.jesus.getCurrentElement();
      if (currentElement == 1) {
         BlockPos blockPos2 = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-13, minecraft.thePlayer.posZ);
         if (minecraft.theWorld.getBlockState(blockPos2).getBlock() instanceof BlockLiquid && !minecraft.thePlayer.onGround) {
            minecraft.thePlayer.motionY = 0.08;
         }
      } else if (currentElement == 2 || currentElement == 3) {
         BlockPos blockPos3 = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-13, minecraft.thePlayer.posZ);
         if (minecraft.theWorld.getBlockState(blockPos3).getBlock() instanceof BlockLiquid) {
            preUpdateEvent.onGround = false;
            if (minecraft.thePlayer.onGround) {
               minecraft.thePlayer.jump();
               if (currentElement == 3 && (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F)) {
                  PlayerUtils.setMotionInMoveDirection(this.boostValue.getCurrentValue());
               }
            }
         }
      } else if (currentElement == 4) {
         BlockPos blockPos4 = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.9, minecraft.thePlayer.posZ);
         if (minecraft.theWorld.getBlockState(blockPos4).getBlock() instanceof BlockLiquid) {
            this.boostTicks = 4;
         }

         if (this.boostTicks > 0) {
            preUpdateEvent.onGround = false;
            minecraft.thePlayer.jump();
            this.boostTicks--;
            if (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F) {
               PlayerUtils.setMotionInMoveDirection(this.boostValue.getCurrentValue());
            }
         }
      } else if (currentElement == 5) {
         BlockPos blockPos5 = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-13, minecraft.thePlayer.posZ);
         if (minecraft.theWorld.getBlockState(blockPos5).getBlock() instanceof BlockLiquid) {
            preUpdateEvent.onGround = false;
            preUpdateEvent.posY = minecraft.thePlayer.ticksExisted % 2 == 0 ? preUpdateEvent.posY + 0.02 : preUpdateEvent.posY - 0.02;
            if (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F) {
               PlayerUtils.setMotionInMoveDirection(this.boostValue.getCurrentValue());
            }
         }
      } else if (currentElement == 6) {
         if ((minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F)
            && !minecraft.thePlayer.onGround
            && minecraft.theWorld.getBlockState(new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 0.2, minecraft.thePlayer.posZ)).getBlock() instanceof BlockLiquid
            )
          {
            PlayerUtils.setMotionInMoveDirection(this.zoomValue.getCurrentValue());
         }

         if (minecraft.theWorld.getBlockState(new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-7, minecraft.thePlayer.posZ)).getBlock() instanceof BlockLiquid
            )
          {
            minecraft.thePlayer.fallDistance = 0.0F;
            minecraft.thePlayer.motionX = 0.0;
            minecraft.thePlayer.motionY = 0.06F;
            minecraft.thePlayer.jumpMovementFactor = 0.01F;
            minecraft.thePlayer.motionZ = 0.0;
         }
      }
   };
   private int boostTicks;
   @EventLink
   public final Listener<BlockCollisionEvent> onBlockCollision = blockCollisionEvent -> {
      int currentElement = this.jesus.getCurrentElement();
      if ((currentElement == 0 || currentElement == 2 || currentElement == 3 || currentElement == 5)
         && blockCollisionEvent.iBlockState.getBlock() == Blocks.water
         && blockCollisionEvent.iBlockState.getBlock() != Blocks.waterlily) {
         double d;
         if (currentElement == 0 || currentElement == 5) {
            d = 0.02;
         } else if (currentElement == 3) {
            d = 0.45;
         } else {
            d = 0.02;
         }

         blockCollisionEvent.axisAlignedBB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
            .contract(0.0, d, 0.0)
            .offset(blockCollisionEvent.blockPos.getX(), blockCollisionEvent.blockPos.getY(), blockCollisionEvent.blockPos.getZ());
      }
   };

   public Jesus() {
      super(Category.MOVEMENT, "Jesus", "Даёт возможность ходить по воде");
   }
}
