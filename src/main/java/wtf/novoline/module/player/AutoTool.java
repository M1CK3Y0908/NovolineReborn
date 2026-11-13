package wtf.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;

public class AutoTool extends Module {
   private int breakTicks;
   private final CheckboxSetting autoTool = settingManager.register(new CheckboxSetting(this, "Switch back", true));
   private final CheckboxSetting silentSwitch = settingManager.register(new CheckboxSetting(this, "Silent switch", true) {
      @Override
      public boolean shouldShow() {
         return AutoTool.this.autoTool.isEnabled();
      }
   });
   private int previousSlot = -1;
   public int toolSlot = -1;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (minecraft.gameSettings.keyBindAttack.isKeyDown()
         && minecraft.thePlayer.capabilities.allowEdit
         && minecraft.objectMouseOver != null
         && minecraft.objectMouseOver.typeOfHit != null
         && minecraft.objectMouseOver.typeOfHit.equals(MovingObjectType.BLOCK)) {
         this.breakTicks++;
         IBlockState blockState = minecraft.theWorld.getBlockState(minecraft.objectMouseOver.getBlockPos());
         int i = -1;
         float f = 1.0F;
         ItemStack heldItem = minecraft.thePlayer.getHeldItem();
         if (heldItem != null && heldItem.getItem() != null) {
            f = heldItem.getStrVsBlock(blockState.getBlock());
         }

         for (int i2 = 0; i2 < 9; i2++) {
            ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[i2 % 9];
            if (itemStack != null && itemStack.getItem() != null) {
               float strVsBlock = itemStack.getStrVsBlock(blockState.getBlock());
               if (strVsBlock > f) {
                  f = strVsBlock;
                  i = i2;
               }
            }
         }

         if (i != -1 && minecraft.thePlayer.inventory.currentItem != i && this.toolSlot != i) {
            this.toolSlot = i;
            this.previousSlot = minecraft.thePlayer.inventory.currentItem;
            if (!this.silentSwitch.isEnabled()) {
               minecraft.thePlayer.inventory.currentItem = i;
            }

            this.sendPacketUnregistered(new C09PacketHeldItemChange(i));
         }
      } else if (this.breakTicks > 0) {
         if (this.previousSlot != -1) {
            if (!this.silentSwitch.isEnabled()) {
               minecraft.thePlayer.inventory.currentItem = this.previousSlot;
            }

            this.sendPacketUnregistered(new C09PacketHeldItemChange(this.previousSlot));
            this.previousSlot = -1;
            this.toolSlot = -1;
         }

         this.breakTicks = 0;
      }
   };

   @Override
   protected void onToggled() {
      this.previousSlot = -1;
      this.toolSlot = -1;
   }

   public AutoTool() {
      super(Category.PLAYER, "AutoTool", "");
   }
}
