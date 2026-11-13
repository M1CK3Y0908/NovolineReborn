package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;
import net.minecraft.world.ColorizerGrass;

public class ItemDoublePlant extends ItemMultiTexture {
   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      net.minecraft.block.BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = net.minecraft.block.BlockDoublePlant.EnumPlantType.byMetadata(stack.getMetadata());
      return blockdoubleplant$enumplanttype != net.minecraft.block.BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplant$enumplanttype != net.minecraft.block.BlockDoublePlant.EnumPlantType.FERN
         ? super.getColorFromItemStack(stack, renderPass)
         : ColorizerGrass.getGrassColor(0.5, 1.0);
   }

   public ItemDoublePlant(Block block, Block block3, Function<ItemStack, String> nameFunction) {
      super(block, block3, nameFunction);
   }
}
