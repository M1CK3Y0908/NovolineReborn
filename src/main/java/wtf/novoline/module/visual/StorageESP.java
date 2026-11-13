package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.utils.render.RenderUtils;

public class StorageESP extends Module {
   private final CheckboxSetting storageESP = settingManager.register(new CheckboxSetting(this, "Chest", true));
   private final CheckboxSetting enderChest = settingManager.register(new CheckboxSetting(this, "Ender chest", true));
   private final CheckboxSetting trappedChest = settingManager.register(new CheckboxSetting(this, "Trapped chest", true));
   private final CheckboxSetting furnace = settingManager.register(new CheckboxSetting(this, "Furnace", false));
   private final CheckboxSetting hopper = settingManager.register(new CheckboxSetting(this, "Hopper", false));
   private final CheckboxSetting dropper = settingManager.register(new CheckboxSetting(this, "Dropper", false));
   private final ColorSetting chestColor = settingManager.register(new ColorSetting(this, "Chest", -19829));
   private final ColorSetting enderChestColor = settingManager.register(new ColorSetting(this, "Ender chest", -3235612));
   private final ColorSetting trappedChestColor = settingManager.register(new ColorSetting(this, "Trapped chest", -29572));
   private final ColorSetting furnaceColor = settingManager.register(new ColorSetting(this, "Furnace", -6250336));
   private final ColorSetting hopperColor = settingManager.register(new ColorSetting(this, "Hopper", -3092272));
   private final ColorSetting dropperColor = settingManager.register(new ColorSetting(this, "Dropper", -8355712));
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      for (TileEntity tileEntity : minecraft.theWorld.loadedTileEntityList) {
         if (tileEntity.getPos() != null && tileEntity.getWorld() != null && tileEntity.getBlockType() != Blocks.air) {
            BlockPos pos = tileEntity.getPos();
            boolean flag = false;
            int i = -1;
            if ((this.storageESP.isEnabled() || this.trappedChest.isEnabled()) && tileEntity instanceof TileEntityChest tileEntityChest) {
               if (tileEntityChest.getChestType() == 0 && this.storageESP.isEnabled()) {
                  flag = true;
                  i = this.chestColor.getColor();
               }

               if (tileEntityChest.getChestType() == 1 && this.trappedChest.isEnabled()) {
                  flag = true;
                  i = this.trappedChestColor.getColor();
               }
            }

            if (this.enderChest.isEnabled() && tileEntity instanceof TileEntityEnderChest) {
               flag = true;
               i = this.enderChestColor.getColor();
            }

            if (this.furnace.isEnabled() && tileEntity instanceof TileEntityFurnace) {
               flag = true;
               i = this.furnaceColor.getColor();
            }

            if (this.hopper.isEnabled() && tileEntity instanceof TileEntityHopper) {
               flag = true;
               i = this.hopperColor.getColor();
            }

            if (this.dropper.isEnabled() && tileEntity instanceof TileEntityDropper) {
               flag = true;
               i = this.dropperColor.getColor();
            }

            if (flag) {
               RenderUtils.drawBoxFilledAndOutlined(
                  minecraft.theWorld.getBlockState(pos).getBlock().getSelectedBoundingBox(minecraft.theWorld, pos), i, renderWorldEvent.partialTicks
               );
            }
         }
      }
   };

   public StorageESP() {
      super(Category.VISUAL, "StorageESP", "");
   }
}
