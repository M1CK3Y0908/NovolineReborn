package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.model.BlockModelUtils;
import net.optifine.util.PropertiesOrdered;

public class BetterGrass {
   private static final String BLOCKS_GRASS_SIDE = "blocks/grass_side";
   private static final String BLOCKS_GRASS_TOP = "blocks/grass_top";
   private static final String BLOCKS_DIRT_PODZOL_TOP = "blocks/dirt_podzol_top";
   private static final String BLOCKS_MYCELIUM_TOP = "blocks/mycelium_top";
   private static final String BLOCKS_SNOW = "blocks/snow";
   private static boolean betterGrass = true;
   private static boolean betterMycelium = true;
   private static boolean betterPodzol = true;
   private static boolean betterGrassSnow = true;
   private static boolean betterMyceliumSnow = true;
   private static boolean betterPodzolSnow = true;
   private static boolean grassMultilayer = false;
   private static TextureAtlasSprite spriteGrass = null;
   private static TextureAtlasSprite spriteGrassSide = null;
   private static TextureAtlasSprite spriteMycelium = null;
   private static TextureAtlasSprite spritePodzol = null;
   private static TextureAtlasSprite spriteSnow = null;
   private static boolean spritesLoaded = false;
   private static IBakedModel modelCubeGrass = null;
   private static IBakedModel modelCubeMycelium = null;
   private static IBakedModel modelCubePodzol = null;
   private static IBakedModel modelCubeSnow = null;
   private static boolean modelsLoaded = false;

   private static List getFaceQuadsMycelium(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
      Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
      boolean flag = block == Blocks.snow || block == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterMyceliumSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(facing);
            }
         } else if (betterMycelium && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.mycelium) {
            return modelCubeMycelium.getFaceQuads(facing);
         }
      } else if (flag) {
         if (betterMyceliumSnow) {
            return modelCubeSnow.getFaceQuads(facing);
         }
      } else if (betterMycelium) {
         return modelCubeMycelium.getFaceQuads(facing);
      }

      return quads;
   }

   public static List getFaceQuads(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
      if (facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
         return quads;
      }

      if (!modelsLoaded) {
         return quads;
      }

      Block block = blockState.getBlock();
      return block instanceof BlockMycelium
         ? getFaceQuadsMycelium(blockAccess, blockState, blockPos, facing, quads)
         : (
            block instanceof BlockDirt
               ? getFaceQuadsDirt(blockAccess, blockState, blockPos, facing, quads)
               : (block instanceof BlockGrass ? getFaceQuadsGrass(blockAccess, blockState, blockPos, facing, quads) : quads)
         );
   }

   private static List getFaceQuadsDirt(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
      Block block = getBlockAt(blockPos, EnumFacing.UP, blockAccess);
      if (blockState.getValue(BlockDirt.VARIANT) != net.minecraft.block.BlockDirt.DirtType.PODZOL) {
         return quads;
      }

      boolean flag = block == Blocks.snow || block == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterPodzolSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(facing);
            }
         } else if (betterPodzol) {
            BlockPos blockPos2 = blockPos.down().offset(facing);
            IBlockState blockState2 = blockAccess.getBlockState(blockPos2);
            if (blockState2.getBlock() == Blocks.dirt && blockState2.getValue(BlockDirt.VARIANT) == net.minecraft.block.BlockDirt.DirtType.PODZOL) {
               return modelCubePodzol.getFaceQuads(facing);
            }
         }
      } else if (flag) {
         if (betterPodzolSnow) {
            return modelCubeSnow.getFaceQuads(facing);
         }
      } else if (betterPodzol) {
         return modelCubePodzol.getFaceQuads(facing);
      }

      return quads;
   }

   private static Block getBlockAt(BlockPos blockPos, EnumFacing facing, IBlockAccess blockAccess) {
      BlockPos blockPos3 = blockPos.offset(facing);
      return blockAccess.getBlockState(blockPos3).getBlock();
   }

   private static void loadProperties(TextureMap textureMap) {
      betterGrass = true;
      betterMycelium = true;
      betterPodzol = true;
      betterGrassSnow = true;
      betterMyceliumSnow = true;
      betterPodzolSnow = true;
      spriteGrass = textureMap.registerSprite(new ResourceLocation("blocks/grass_top"));
      spriteGrassSide = textureMap.registerSprite(new ResourceLocation("blocks/grass_side"));
      spriteMycelium = textureMap.registerSprite(new ResourceLocation("blocks/mycelium_top"));
      spritePodzol = textureMap.registerSprite(new ResourceLocation("blocks/dirt_podzol_top"));
      spriteSnow = textureMap.registerSprite(new ResourceLocation("blocks/snow"));
      spritesLoaded = true;

      try {
         ResourceLocation resourceLocation = new ResourceLocation("optifine/bettergrass.properties");
         if (!Config.hasResource(resourceLocation)) {
            return;
         }

         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return;
         }

         if (Config.isFromDefaultResourcePack(resourceLocation)) {
            Config.dbg("BetterGrass: Parsing default configuration " + "optifine/bettergrass.properties");
         } else {
            Config.dbg("BetterGrass: Parsing configuration " + "optifine/bettergrass.properties");
         }

         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         betterGrass = getBoolean(propertiesOrdered, "grass", true);
         betterMycelium = getBoolean(propertiesOrdered, "mycelium", true);
         betterPodzol = getBoolean(propertiesOrdered, "podzol", true);
         betterGrassSnow = getBoolean(propertiesOrdered, "grass.snow", true);
         betterMyceliumSnow = getBoolean(propertiesOrdered, "mycelium.snow", true);
         betterPodzolSnow = getBoolean(propertiesOrdered, "podzol.snow", true);
         grassMultilayer = getBoolean(propertiesOrdered, "grass.multilayer", false);
         spriteGrass = registerSprite(propertiesOrdered, "texture.grass", "blocks/grass_top", textureMap);
         spriteGrassSide = registerSprite(propertiesOrdered, "texture.grass_side", "blocks/grass_side", textureMap);
         spriteMycelium = registerSprite(propertiesOrdered, "texture.mycelium", "blocks/mycelium_top", textureMap);
         spritePodzol = registerSprite(propertiesOrdered, "texture.podzol", "blocks/dirt_podzol_top", textureMap);
         spriteSnow = registerSprite(propertiesOrdered, "texture.snow", "blocks/snow", textureMap);
      } catch (IOException ioException) {
         Config.warn("Error reading: optifine/bettergrass.properties, " + ioException.getClass().getName() + ": " + ioException.getMessage());
      }
   }

   public static void update() {
      if (spritesLoaded) {
         modelCubeGrass = BlockModelUtils.makeModelCube(spriteGrass, 0);
         if (grassMultilayer) {
            modelCubeGrass = BlockModelUtils.joinModelsCube(BlockModelUtils.makeModelCube(spriteGrassSide, -1), modelCubeGrass);
         }

         modelCubeMycelium = BlockModelUtils.makeModelCube(spriteMycelium, -1);
         modelCubePodzol = BlockModelUtils.makeModelCube(spritePodzol, 0);
         modelCubeSnow = BlockModelUtils.makeModelCube(spriteSnow, -1);
         modelsLoaded = true;
      }
   }

   private static List getFaceQuadsGrass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, List quads) {
      Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
      boolean flag = block == Blocks.snow || block == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterGrassSnow && getBlockAt(blockPos, facing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(facing);
            }
         } else if (betterGrass && getBlockAt(blockPos.down(), facing, blockAccess) == Blocks.grass) {
            return modelCubeGrass.getFaceQuads(facing);
         }
      } else if (flag) {
         if (betterGrassSnow) {
            return modelCubeSnow.getFaceQuads(facing);
         }
      } else if (betterGrass) {
         return modelCubeGrass.getFaceQuads(facing);
      }

      return quads;
   }

   private static TextureAtlasSprite registerSprite(Properties props, String key, String textureDefault, TextureMap textureMap) {
      String property = props.getProperty(key);
      if (property == null) {
         property = textureDefault;
      }

      ResourceLocation resourceLocation = new ResourceLocation("textures/" + property + ".png");
      if (!Config.hasResource(resourceLocation)) {
         Config.warn("BetterGrass texture not found: " + resourceLocation);
         property = textureDefault;
      }

      ResourceLocation resourceLocation2 = new ResourceLocation(property);
      return textureMap.registerSprite(resourceLocation2);
   }

   public static void updateIcons(TextureMap textureMap) {
      spritesLoaded = false;
      modelsLoaded = false;
      loadProperties(textureMap);
   }

   private static boolean getBoolean(Properties props, String key, boolean def) {
      String propertyValue = props.getProperty(key);
      return propertyValue == null ? def : Boolean.parseBoolean(propertyValue);
   }
}
