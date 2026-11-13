package net.minecraft.item.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
   public void addRecipes(CraftingManager craftingManager) {
      for (int index = 0; index < 16; index++) {
         craftingManager.addShapelessRecipe(
            new ItemStack(Blocks.wool, 1, index), new ItemStack(Items.dye, 1, 15 - index), new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0)
         );
         craftingManager.addRecipe(
            new ItemStack(Blocks.stained_hardened_clay, 8, 15 - index),
            "###",
            "#X#",
            "###",
            '#',
            new ItemStack(Blocks.hardened_clay),
            'X',
            new ItemStack(Items.dye, 1, index)
         );
         craftingManager.addRecipe(
            new ItemStack(Blocks.stained_glass, 8, 15 - index), "###", "#X#", "###", '#', new ItemStack(Blocks.glass), 'X', new ItemStack(Items.dye, 1, index)
         );
         craftingManager.addRecipe(new ItemStack(Blocks.stained_glass_pane, 16, index), "###", "###", '#', new ItemStack(Blocks.stained_glass, 1, index));
      }

      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage()),
         new ItemStack(Blocks.yellow_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.DANDELION.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.POPPY.getMeta())
      );
      craftingManager.addShapelessRecipe(new ItemStack(Items.dye, 3, EnumDyeColor.WHITE.getDyeDamage()), Items.bone);
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.ORANGE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.YELLOW.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.LIME.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.GRAY.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.SILVER.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GRAY.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 3, EnumDyeColor.SILVER.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLACK.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.LIGHT_BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.CYAN.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.GREEN.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PURPLE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PURPLE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 3, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 4, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()),
         new ItemStack(Items.dye, 1, EnumDyeColor.WHITE.getDyeDamage())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.LIGHT_BLUE.getDyeDamage()),
         new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.BLUE_ORCHID.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.ALLIUM.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.HOUSTONIA.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.RED_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.ORANGE.getDyeDamage()),
         new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.ORANGE_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.WHITE_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.PINK_TULIP.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 1, EnumDyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.red_flower, 1, net.minecraft.block.BlockFlower.EnumFlowerType.OXEYE_DAISY.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.YELLOW.getDyeDamage()),
         new ItemStack(Blocks.double_plant, 1, net.minecraft.block.BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.MAGENTA.getDyeDamage()),
         new ItemStack(Blocks.double_plant, 1, net.minecraft.block.BlockDoublePlant.EnumPlantType.SYRINGA.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.RED.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, net.minecraft.block.BlockDoublePlant.EnumPlantType.ROSE.getMeta())
      );
      craftingManager.addShapelessRecipe(
         new ItemStack(Items.dye, 2, EnumDyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.double_plant, 1, net.minecraft.block.BlockDoublePlant.EnumPlantType.PAEONIA.getMeta())
      );

      for (int innerIndex = 0; innerIndex < 16; innerIndex++) {
         craftingManager.addRecipe(new ItemStack(Blocks.carpet, 3, innerIndex), "##", '#', new ItemStack(Blocks.wool, 1, innerIndex));
      }
   }
}
