package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;

public class BridgeAssist extends Module {
   private final NumberSetting bridgeAssist = settingManager.register(new NumberSetting(this, "Ground sneak ticks", 5, 1, 10));
   private final NumberSetting airSneakTicks = settingManager.register(new NumberSetting(this, "Air sneak ticks", 5, 1, 10));
   private final CheckboxSetting airSafeWalk = settingManager.register(new CheckboxSetting(this, "Air safe walk", false));
   private int sneakTicks = 0;
   private boolean sneakActive = false;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      BlockPos blockPos = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 1.0, minecraft.thePlayer.posZ);
      Block block2 = minecraft.theWorld.getBlockState(blockPos).getBlock();
      float f = !minecraft.thePlayer.onGround && !minecraft.gameSettings.keyBindJump.isPressed()
         ? this.airSneakTicks.getCurrentValue()
         : this.bridgeAssist.getCurrentValue();
      if (minecraft.gameSettings.keyBindUseItem.isKeyDown() && minecraft.thePlayer.rotationPitch >= 65.0F) {
         if (block2 == Blocks.air) {
            this.setSneakKeyDown(true);
            this.sneakActive = true;
         } else if (this.sneakActive && ++this.sneakTicks >= f) {
            this.setSneakKeyDown(false);
            this.sneakTicks = 0;
            this.sneakActive = false;
         }
      } else if (this.sneakActive) {
         this.setSneakKeyDown(false);
         this.sneakActive = false;
         this.sneakTicks = 0;
      }
   };
   @EventLink
   private final Listener<MoveEntityEvent> onMoveEntity = moveEntityEvent -> {
      if (minecraft.gameSettings.keyBindUseItem.isKeyDown() && (this.airSafeWalk.isEnabled() || minecraft.thePlayer.onGround)) {
         moveEntityEvent.cancel();
      }
   };

   private void setSneakKeyDown(boolean flag) {
      KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSneak.getKeyCode(), flag);
   }

   public BridgeAssist() {
      super(Category.WORLD, "BridgeAssist", "");
   }
}
