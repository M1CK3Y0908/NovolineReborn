package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.time.Stopwatch;

public class Breaker extends Module {
   private final MultiComboSetting breaker = settingManager.register(new MultiComboSetting(this, "Targets").add("Bed", true).add("Egg", true));
   private final NumberSetting radius = settingManager.register(new NumberSetting(this, "Radius", 4, 0, 6));
   private BlockPos blockPos;
   private final NumberSetting breakDelay = settingManager.register(new NumberSetting(this, "Break delay", 200, 0, 5000));
   private final NumberSetting cooldownDelay = settingManager.register(new NumberSetting(this, "Cooldown delay", 100, 0, 5000));
   private final Stopwatch breakTimer = new Stopwatch();
   private final Stopwatch cooldownTimer = new Stopwatch();
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.blockPos != null) {
         if (this.cooldownTimer.hasElapsed((long)this.cooldownDelay.getCurrentValue())) {
            float[] rotationsTo = RotationUtils.getRotationsTo(new Vec3(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()));
            preUpdateEvent.rotationYaw = rotationsTo[0];
            preUpdateEvent.rotationPitch = rotationsTo[1];
            if (this.breakTimer.hasElapsed((long)this.breakDelay.getCurrentValue())) {
               this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, this.blockPos, EnumFacing.UP));
               this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, this.blockPos, EnumFacing.UP));
               this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, this.blockPos, EnumFacing.UP));
               minecraft.thePlayer.swingItemNoPacket();
               this.breakTimer.reset();
               this.cooldownTimer.reset();
               this.blockPos = null;
            }
         } else {
            this.breakTimer.reset();
         }
      } else {
         int i = (int)this.radius.getCurrentValue();

         for (int i2 = -i; i2 <= i; i2++) {
            for (int i3 = -i; i3 <= i; i3++) {
               for (int i4 = -i; i4 <= i; i4++) {
                  BlockPos blockPos2 = new BlockPos(minecraft.thePlayer.posX + i2, minecraft.thePlayer.posY + i3, minecraft.thePlayer.posZ + i4);
                  if (this.isTargetBlock(minecraft.theWorld.getBlockState(blockPos2).getBlock())) {
                     this.blockPos = blockPos2;
                     this.breakTimer.reset();
                     this.cooldownTimer.reset();
                     break;
                  }
               }
            }
         }
      }
   };

   public Breaker() {
      super(Category.WORLD, "Breaker", "");
   }

   private boolean isTargetBlock(Block block2) {
      return this.breaker.isSelected(0) && block2 instanceof BlockBed ? true : this.breaker.isSelected(1) && block2 instanceof BlockDragonEgg;
   }

   @Override
   protected void onEnable() {
      this.blockPos = null;
   }
}
