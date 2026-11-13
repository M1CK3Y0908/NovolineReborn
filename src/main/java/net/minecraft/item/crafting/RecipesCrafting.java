package net.minecraft.item.crafting;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

public class RecipesCrafting {
   public void addRecipes(CraftingManager craftingManager) {
      craftingManager.addRecipe(new ItemStack(Blocks.chest), "###", "# #", "###", '#', Blocks.planks);
      craftingManager.addRecipe(new ItemStack(Blocks.trapped_chest), "#-", '#', Blocks.chest, '-', Blocks.tripwire_hook);
      craftingManager.addRecipe(new ItemStack(Blocks.ender_chest), "###", "#E#", "###", '#', Blocks.obsidian, 'E', Items.ender_eye);
      craftingManager.addRecipe(new ItemStack(Blocks.furnace), "###", "# #", "###", '#', Blocks.cobblestone);
      craftingManager.addRecipe(new ItemStack(Blocks.crafting_table), "##", "##", '#', Blocks.planks);
      craftingManager.addRecipe(new ItemStack(Blocks.sandstone), "##", "##", '#', new ItemStack(Blocks.sand, 1, net.minecraft.block.BlockSand.EnumType.SAND.getMetadata()));
      craftingManager.addRecipe(new ItemStack(Blocks.red_sandstone), "##", "##", '#', new ItemStack(Blocks.sand, 1, net.minecraft.block.BlockSand.EnumType.RED_SAND.getMetadata()));
      craftingManager.addRecipe(
         new ItemStack(Blocks.sandstone, 4, net.minecraft.block.BlockSandStone.EnumType.SMOOTH.getMetadata()),
         "##",
         "##",
         '#',
         new ItemStack(Blocks.sandstone, 1, net.minecraft.block.BlockSandStone.EnumType.DEFAULT.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.red_sandstone, 4, net.minecraft.block.BlockRedSandstone.EnumType.SMOOTH.getMetadata()),
         "##",
         "##",
         '#',
         new ItemStack(Blocks.red_sandstone, 1, net.minecraft.block.BlockRedSandstone.EnumType.DEFAULT.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.sandstone, 1, net.minecraft.block.BlockSandStone.EnumType.CHISELED.getMetadata()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.stone_slab, 1, net.minecraft.block.BlockStoneSlab.EnumType.SAND.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.red_sandstone, 1, net.minecraft.block.BlockRedSandstone.EnumType.CHISELED.getMetadata()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.stone_slab2, 1, net.minecraft.block.BlockStoneSlabNew.EnumType.RED_SANDSTONE.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.quartz_block, 1, net.minecraft.block.BlockQuartz.EnumType.CHISELED.getMetadata()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.stone_slab, 1, net.minecraft.block.BlockStoneSlab.EnumType.QUARTZ.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.quartz_block, 2, net.minecraft.block.BlockQuartz.EnumType.LINES_Y.getMetadata()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.quartz_block, 1, net.minecraft.block.BlockQuartz.EnumType.DEFAULT.getMetadata())
      );
      craftingManager.addRecipe(new ItemStack(Blocks.stonebrick, 4), "##", "##", '#', new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.STONE.getMetadata()));
      craftingManager.addRecipe(
         new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.CHISELED_META),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.stone_slab, 1, net.minecraft.block.BlockStoneSlab.EnumType.SMOOTHBRICK.getMetadata())
      );
      craftingManager.addShapelessRecipe(new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.MOSSY_META), Blocks.stonebrick, Blocks.vine);
      craftingManager.addShapelessRecipe(new ItemStack(Blocks.mossy_cobblestone, 1), Blocks.cobblestone, Blocks.vine);
      craftingManager.addRecipe(new ItemStack(Blocks.iron_bars, 16), "###", "###", '#', Items.iron_ingot);
      craftingManager.addRecipe(new ItemStack(Blocks.glass_pane, 16), "###", "###", '#', Blocks.glass);
      craftingManager.addRecipe(new ItemStack(Blocks.redstone_lamp, 1), " R ", "RGR", " R ", 'R', Items.redstone, 'G', Blocks.glowstone);
      craftingManager.addRecipe(new ItemStack(Blocks.beacon, 1), "GGG", "GSG", "OOO", 'G', Blocks.glass, 'S', Items.nether_star, 'O', Blocks.obsidian);
      craftingManager.addRecipe(new ItemStack(Blocks.nether_brick, 1), "NN", "NN", 'N', Items.netherbrick);
      craftingManager.addRecipe(new ItemStack(Blocks.stone, 2, net.minecraft.block.BlockStone.EnumType.DIORITE.getMetadata()), "CQ", "QC", 'C', Blocks.cobblestone, 'Q', Items.quartz);
      craftingManager.addShapelessRecipe(
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.GRANITE.getMetadata()),
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.DIORITE.getMetadata()),
         Items.quartz
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Blocks.stone, 2, net.minecraft.block.BlockStone.EnumType.ANDESITE.getMetadata()),
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.DIORITE.getMetadata()),
         Blocks.cobblestone
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.dirt, 4, net.minecraft.block.BlockDirt.DirtType.COARSE_DIRT.getMetadata()),
         "DG",
         "GD",
         'D',
         new ItemStack(Blocks.dirt, 1, net.minecraft.block.BlockDirt.DirtType.DIRT.getMetadata()),
         'G',
         Blocks.gravel
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.stone, 4, net.minecraft.block.BlockStone.EnumType.DIORITE_SMOOTH.getMetadata()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.DIORITE.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.stone, 4, net.minecraft.block.BlockStone.EnumType.GRANITE_SMOOTH.getMetadata()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.GRANITE.getMetadata())
      );
      craftingManager.addRecipe(
         new ItemStack(Blocks.stone, 4, net.minecraft.block.BlockStone.EnumType.ANDESITE_SMOOTH.getMetadata()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.stone, 1, net.minecraft.block.BlockStone.EnumType.ANDESITE.getMetadata())
      );
      craftingManager.addRecipe(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.ROUGH_META), "SS", "SS", 'S', Items.prismarine_shard);
      craftingManager.addRecipe(new ItemStack(Blocks.prismarine, 1, BlockPrismarine.BRICKS_META), "SSS", "SSS", "SSS", 'S', Items.prismarine_shard);
      craftingManager.addRecipe(
         new ItemStack(Blocks.prismarine, 1, BlockPrismarine.DARK_META),
         "SSS",
         "SIS",
         "SSS",
         'S',
         Items.prismarine_shard,
         'I',
         new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage())
      );
      craftingManager.addRecipe(new ItemStack(Blocks.sea_lantern, 1, 0), "SCS", "CCC", "SCS", 'S', Items.prismarine_shard, 'C', Items.prismarine_crystals);
   }
}
