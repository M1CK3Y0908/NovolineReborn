package net.minecraft.item;

import com.google.common.base.Function;
import net.minecraft.block.Block;

public class ItemMultiTexture extends ItemBlock {
   protected final Block theBlock;
   protected final Function<ItemStack, String> nameFunction;

   @Override
   public int getMetadata(int damage) {
      return damage;
   }

   public ItemMultiTexture(Block block, Block block3, final String[] namesByMeta) {
      this(block, block3, new Function<ItemStack, String>() {
         public String apply(ItemStack arg1) {
            int metadata = arg1.getMetadata();
            if (metadata < 0 || metadata >= namesByMeta.length) {
               metadata = 0;
            }

            return namesByMeta[metadata];
         }
      });
   }

   public ItemMultiTexture(Block block, Block block3, Function<ItemStack, String> nameFunction) {
      super(block);
      this.theBlock = block3;
      this.nameFunction = nameFunction;
      this.setMaxDamage(0);
      this.getItem();
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + (String)this.nameFunction.apply(stack);
   }
}
