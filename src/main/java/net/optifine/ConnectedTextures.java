package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.Matches;
import net.optifine.model.BlockModelUtils;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TileEntityUtils;

public class ConnectedTextures {
   private static Map[] spriteQuadMaps = null;
   protected static final int intValue = 0;
   protected static final int UNKNOWN = -1;
   protected static final int Z_POS_SOUTH = 3;
   private static Map[] spriteQuadFullMaps = null;
   private static Map[][] spriteQuadCompactMaps = (Map[][])null;
   private static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
   private static final int intValue2 = 2;
   private static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
   protected static final int X_NEG_WEST = 4;
   protected static final int X_POS_EAST = 5;
   private static boolean multipass = false;
   public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
   protected static final int intValue3 = 2;
   private static TextureAtlasSprite emptySprite = null;
   private static final BlockDir[] SIDES_Y_NEG_DOWN = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.NORTH, BlockDir.SOUTH};
   private static final int intValue4 = 1;
   private static final BlockDir[] SIDES_Y_POS_UP = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.SOUTH, BlockDir.NORTH};
   private static final BlockDir[] SIDES_Z_NEG_NORTH = new BlockDir[]{BlockDir.EAST, BlockDir.WEST, BlockDir.DOWN, BlockDir.UP};
   private static final BlockDir[] SIDES_Z_POS_SOUTH = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.DOWN, BlockDir.UP};
   private static final BlockDir[] SIDES_X_NEG_WEST = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.DOWN, BlockDir.UP};
   private static final BlockDir[] SIDES_X_POS_EAST = new BlockDir[]{BlockDir.SOUTH, BlockDir.NORTH, BlockDir.DOWN, BlockDir.UP};
   protected static final int intValue5 = 1;
   private static final BlockDir[] SIDES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.UP, BlockDir.DOWN};
   private static final BlockDir[] SIDES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.UP, BlockDir.DOWN};
   private static final BlockDir[] EDGES_Y_NEG_DOWN = new BlockDir[]{BlockDir.NORTH_EAST, BlockDir.NORTH_WEST, BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST};
   private static final BlockDir[] EDGES_Y_POS_UP = new BlockDir[]{BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST, BlockDir.NORTH_EAST, BlockDir.NORTH_WEST};
   private static final int intValue6 = 0;
   private static final BlockDir[] EDGES_Z_NEG_NORTH = new BlockDir[]{BlockDir.DOWN_WEST, BlockDir.DOWN_EAST, BlockDir.UP_WEST, BlockDir.UP_EAST};
   private static final BlockDir[] EDGES_Z_POS_SOUTH = new BlockDir[]{BlockDir.DOWN_EAST, BlockDir.DOWN_WEST, BlockDir.UP_EAST, BlockDir.UP_WEST};
   private static final BlockDir[] EDGES_X_NEG_WEST = new BlockDir[]{BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH, BlockDir.UP_SOUTH, BlockDir.UP_NORTH};
   private static final BlockDir[] EDGES_X_POS_EAST = new BlockDir[]{BlockDir.DOWN_NORTH, BlockDir.DOWN_SOUTH, BlockDir.UP_NORTH, BlockDir.UP_SOUTH};
   private static final BlockDir[] EDGES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.UP_EAST, BlockDir.UP_WEST, BlockDir.DOWN_EAST, BlockDir.DOWN_WEST};
   private static final BlockDir[] EDGES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.UP_SOUTH, BlockDir.UP_NORTH, BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH};
   public static final TextureAtlasSprite SPRITE_DEFAULT = new TextureAtlasSprite("<default>", 87996829185329L);

   public static void updateIcons(TextureMap textureMap) {
      blockProperties = (ConnectedProperties[][])null;
      tileProperties = (ConnectedProperties[][])null;
      spriteQuadMaps = null;
      spriteQuadCompactMaps = (Map[][])null;
      if (Config.isConnectedTextures()) {
         IResourcePack[] resourcePackArray = Config.getResourcePacks();

         for (int index = resourcePackArray.length - 1; index >= 0; index--) {
            IResourcePack iResourcePack = resourcePackArray[index];
            updateIcons(textureMap, iResourcePack);
         }

         updateIcons(textureMap, Config.getDefaultResourcePack());
         ResourceLocation resourceLocation = new ResourceLocation("mcpatcher/ctm/default/empty");
         emptySprite = textureMap.registerSprite(resourceLocation);
         spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
         spriteQuadFullMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
         spriteQuadCompactMaps = new Map[textureMap.getCountRegisteredSprites() + 1][];
         if (blockProperties.length <= 0) {
            blockProperties = (ConnectedProperties[][])null;
         }

         if (tileProperties.length <= 0) {
            tileProperties = (ConnectedProperties[][])null;
         }
      }
   }

   private static BakedQuad[] getConnectedTextureOverlay(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      BakedQuad quad,
      int metadata,
      RenderEnv renderEnv
   ) {
      if (!quad.isFullQuad()) {
         return null;
      }

      TextureAtlasSprite sprite2 = quad.getSprite();
      BlockDir[] sideDirections = getSideDirections(side, vertAxis);
      boolean[] borderFlags = renderEnv.getBorderFlags();

      for (int i = 0; i < 4; i++) {
         borderFlags[i] = isNeighbourOverlay(cp, blockAccess, blockState, sideDirections[i].offset(blockPos), side, sprite2, metadata);
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(cp.layer);

      Object object;
      try {
         if (!borderFlags[0] || !borderFlags[1] || !borderFlags[2] || !borderFlags[3]) {
            if (borderFlags[0] && borderFlags[1] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[5], quad, cp.tintIndex), cp.tintBlockState);
               object = null;
               return (BakedQuad[])object;
            }

            if (borderFlags[0] && borderFlags[2] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[6], quad, cp.tintIndex), cp.tintBlockState);
               object = null;
               return (BakedQuad[])object;
            }

            if (borderFlags[1] && borderFlags[2] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[12], quad, cp.tintIndex), cp.tintBlockState);
               object = null;
               return (BakedQuad[])object;
            }

            if (borderFlags[0] && borderFlags[1] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[13], quad, cp.tintIndex), cp.tintBlockState);
               object = null;
               return (BakedQuad[])object;
            }

            BlockDir[] edgeDirections = getEdgeDirections(side, vertAxis);
            boolean[] borderFlags2 = renderEnv.getBorderFlags2();

            for (int i2 = 0; i2 < 4; i2++) {
               borderFlags2[i2] = isNeighbourOverlay(cp, blockAccess, blockState, edgeDirections[i2].offset(blockPos), side, sprite2, metadata);
            }

            if (borderFlags[1] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[3], quad, cp.tintIndex), cp.tintBlockState);
               if (borderFlags2[3]) {
                  listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[16], quad, cp.tintIndex), cp.tintBlockState);
               }

               Object object2 = null;
               return (BakedQuad[])object2;
            }

            if (borderFlags[0] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[4], quad, cp.tintIndex), cp.tintBlockState);
               if (borderFlags2[2]) {
                  listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[14], quad, cp.tintIndex), cp.tintBlockState);
               }

               Object object3 = null;
               return (BakedQuad[])object3;
            }

            if (borderFlags[1] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[10], quad, cp.tintIndex), cp.tintBlockState);
               if (borderFlags2[1]) {
                  listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[2], quad, cp.tintIndex), cp.tintBlockState);
               }

               Object object4 = null;
               return (BakedQuad[])object4;
            }

            if (borderFlags[0] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[11], quad, cp.tintIndex), cp.tintBlockState);
               if (borderFlags2[0]) {
                  listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[0], quad, cp.tintIndex), cp.tintBlockState);
               }

               Object object5 = null;
               return (BakedQuad[])object5;
            }

            boolean[] borderFlags3 = renderEnv.getBorderFlags3();

            for (int i3 = 0; i3 < 4; i3++) {
               borderFlags3[i3] = checkConnectedProperties(cp, blockAccess, blockState, sideDirections[i3].offset(blockPos), side, sprite2);
            }

            if (borderFlags[0]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[9], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags[1]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[7], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[1], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[15], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags2[0] && (borderFlags3[1] || borderFlags3[2]) && !borderFlags[1] && !borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[0], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags2[1] && (borderFlags3[0] || borderFlags3[2]) && !borderFlags[0] && !borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[2], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags2[2] && (borderFlags3[1] || borderFlags3[3]) && !borderFlags[1] && !borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[14], quad, cp.tintIndex), cp.tintBlockState);
            }

            if (borderFlags2[3] && (borderFlags3[0] || borderFlags3[3]) && !borderFlags[0] && !borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[16], quad, cp.tintIndex), cp.tintBlockState);
            }

            Object object6 = null;
            return (BakedQuad[])object6;
         }

         listQuadsOverlay.addQuad(getQuadFull(cp.tileIcons[8], quad, cp.tintIndex), cp.tintBlockState);
         object = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])object;
   }

   private static EnumFacing getFacing(int side) {
      switch (side) {
         case 0:
            return EnumFacing.DOWN;
         case 1:
            return EnumFacing.UP;
         case 2:
            return EnumFacing.NORTH;
         case 3:
            return EnumFacing.SOUTH;
         case 4:
            return EnumFacing.WEST;
         case 5:
            return EnumFacing.EAST;
         default:
            return EnumFacing.UP;
      }
   }

   private static TextureAtlasSprite getConnectedTextureHorizontal(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      boolean conditionMet;
      boolean conditionMet2;
      conditionMet = false;
      conditionMet2 = false;
      label46:
      switch (vertAxis) {
         case 0:
            switch (side) {
               case 0:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 1:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 2:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  break label46;
               case 3:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 4:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break label46;
               case 5:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
               default:
                  break label46;
            }
         case 1:
            switch (side) {
               case 0:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  break label46;
               case 1:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 2:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 3:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                  break label46;
               case 4:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  break label46;
               case 5:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
               default:
                  break label46;
            }
         case 2:
            switch (side) {
               case 0:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  break;
               case 1:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break;
               case 2:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  break;
               case 3:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                  break;
               case 4:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                  break;
               case 5:
                  conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                  conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            }
      }

      int i;
      if (conditionMet) {
         if (conditionMet2) {
            i = 1;
         } else {
            i = 2;
         }
      } else if (conditionMet2) {
         i = 0;
      } else {
         i = 3;
      }

      return cp.tileIcons[i];
   }

   private static BakedQuad[] getConnectedTextureMultiPass(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, BakedQuad quad, RenderEnv renderEnv
   ) {
      BakedQuad[] connectedTextureSingle = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, quad, true, 0, renderEnv);
      if (!multipass) {
         return connectedTextureSingle;
      }

      if (connectedTextureSingle.length == 1 && connectedTextureSingle[0] == quad) {
         return connectedTextureSingle;
      }

      List listQuadsCtmMultipass = renderEnv.getListQuadsCtmMultipass(connectedTextureSingle);

      for (int i = 0; i < listQuadsCtmMultipass.size(); i++) {
         BakedQuad bakedQuad3 = (BakedQuad)listQuadsCtmMultipass.get(i);

         for (int i2 = 0; i2 < 3; i2++) {
            BakedQuad[] connectedTextureSingle2 = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, bakedQuad3, false, i2 + 1, renderEnv);
            if (connectedTextureSingle2.length != 1 || connectedTextureSingle2[0] == bakedQuad3) {
               break;
            }

            bakedQuad3 = connectedTextureSingle2[0];
         }

         listQuadsCtmMultipass.set(i, bakedQuad3);
      }

      for (int i3 = 0; i3 < connectedTextureSingle.length; i3++) {
         connectedTextureSingle[i3] = (BakedQuad)listQuadsCtmMultipass.get(i3);
      }

      return connectedTextureSingle;
   }

   private static boolean isFullCubeModel(IBlockState state) {
      if (state.getBlock().isFullCube()) {
         return true;
      }

      Block block = state.getBlock();
      return block instanceof BlockGlass ? true : block instanceof BlockStainedGlass;
   }

   private static TextureAtlasSprite getNeighbourIcon(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, IBlockState neighbourState, int side) {
      neighbourState = neighbourState.getBlock().getActualState(neighbourState, blockAccess, blockPos);
      IBakedModel bakedModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(neighbourState);
      if (bakedModel == null) {
         return null;
      }

      if (Reflector.reflectorMethod25.isActive()) {
         neighbourState = (IBlockState)Reflector.createObject4(neighbourState.getBlock(), Reflector.reflectorMethod25, neighbourState, blockAccess, blockPos);
      }

      EnumFacing facing = getFacing(side);
      List list = bakedModel.getFaceQuads(facing);
      if (list == null) {
         return null;
      }

      if (Config.isBetterGrass()) {
         list = BetterGrass.getFaceQuads(blockAccess, neighbourState, blockPos, facing, list);
      }

      if (list.size() > 0) {
         BakedQuad bakedQuad = (BakedQuad)list.get(0);
         return bakedQuad.getSprite();
      }

      List list2 = bakedModel.getGeneralQuads();
      if (list2 == null) {
         return null;
      }

      for (int index = 0; index < list2.size(); index++) {
         BakedQuad bakedQuad2 = (BakedQuad)list2.get(index);
         if (bakedQuad2.getFace() == facing) {
            return bakedQuad2.getSprite();
         }
      }

      return null;
   }

   private static void addToTileList(ConnectedProperties cp, List tileList) {
      if (cp.matchTileIcons != null) {
         for (int index = 0; index < cp.matchTileIcons.length; index++) {
            TextureAtlasSprite textureAtlasSprite3 = cp.matchTileIcons[index];
            if (!(textureAtlasSprite3 instanceof TextureAtlasSprite)) {
               Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureAtlasSprite3 + ", name: " + textureAtlasSprite3.getIconName());
            } else {
               int indexInMap = textureAtlasSprite3.getIndexInMap();
               if (indexInMap < 0) {
                  Config.warn("Invalid tile ID: " + indexInMap + ", icon: " + textureAtlasSprite3.getIconName());
               } else {
                  addToList(cp, tileList, indexInMap);
               }
            }
         }
      }
   }

   private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite) {
      int[] iArray = (int[])quad.getVertexData().clone();
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();

      for (int index = 0; index < 4; index++) {
         fixVertex(iArray, index, textureAtlasSprite, sprite);
      }

      return new BakedQuad(iArray, quad.getTintIndex(), quad.getFace(), sprite);
   }

   private static synchronized BakedQuad[] getConnectedTextureCtmCompact(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      BakedQuad quad,
      int metadata,
      RenderEnv renderEnv
   ) {
      TextureAtlasSprite sprite2 = quad.getSprite();
      return ConnectedTexturesCompact.getConnectedTextureCtmCompact(
         getConnectedTextureCtmIndex(cp, blockAccess, blockState, blockPos, vertAxis, side, sprite2, metadata, renderEnv), cp, side, quad, renderEnv
      );
   }

   private static int createInt2(int i) {
      switch ((i & 12) >> 2) {
         case 1:
            return 2;
         case 2:
            return 1;
         default:
            return 0;
      }
   }

   private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp) {
      return cp.tileIcons[0];
   }

   private static boolean skipConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv) {
      Block block2 = blockState.getBlock();
      if (block2 instanceof BlockPane) {
         TextureAtlasSprite sprite2 = quad.getSprite();
         if (sprite2.getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
            IBlockState blockState4 = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
            return blockState4 == blockState;
         }
      }

      if (block2 instanceof BlockPane) {
         EnumFacing face = quad.getFace();
         if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
            return false;
         }

         if (!quad.isFaceQuad()) {
            return false;
         }

         BlockPos blockPos14 = blockPos.offset(quad.getFace());
         IBlockState blockState5 = blockAccess.getBlockState(blockPos14);
         if (blockState5.getBlock() != block2) {
            return false;
         }

         if (block2 == Blocks.stained_glass_pane && blockState5.getValue(BlockStainedGlassPane.COLOR) != blockState.getValue(BlockStainedGlassPane.COLOR)) {
            return false;
         }

         blockState5 = blockState5.getBlock().getActualState(blockState5, blockAccess, blockPos14);
         double midX = quad.getMidX();
         if (midX < 0.4) {
            if (blockState5.getValue(BlockPane.WEST)) {
               return true;
            }
         } else if (midX > 0.6) {
            if (blockState5.getValue(BlockPane.EAST)) {
               return true;
            }
         } else {
            double midZ = quad.getMidZ();
            if (midZ < 0.4) {
               if (blockState5.getValue(BlockPane.NORTH)) {
                  return true;
               }
            } else {
               if (midZ <= 0.6) {
                  return true;
               }

               if (blockState5.getValue(BlockPane.SOUTH)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static BlockDir[] getSideDirections(int side, int vertAxis) {
      switch (side) {
         case 0:
            return SIDES_Y_NEG_DOWN;
         case 1:
            return SIDES_Y_POS_UP;
         case 2:
            if (vertAxis == 1) {
               return SIDES_Z_NEG_NORTH_Z_AXIS;
            }

            return SIDES_Z_NEG_NORTH;
         case 3:
            return SIDES_Z_POS_SOUTH;
         case 4:
            return SIDES_X_NEG_WEST;
         case 5:
            if (vertAxis == 2) {
               return SIDES_X_POS_EAST_X_AXIS;
            }

            return SIDES_X_POS_EAST;
         default:
            throw new IllegalArgumentException("Unknown side: " + side);
      }
   }

   private static int getConnectedTextureCtmIndex(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata,
      RenderEnv renderEnv
   ) {
      boolean[] booleanArray = renderEnv.getBorderFlags();
      switch (side) {
         case 0:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos2 = blockPos.down();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos2.west(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos2.east(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos2.north(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos2.south(), side, icon, metadata);
            }
            break;
         case 1:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos3 = blockPos.up();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos3.west(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos3.east(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos3.south(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos3.north(), side, icon, metadata);
            }
            break;
         case 2:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos4 = blockPos.north();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos4.east(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos4.west(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos4.down(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos4.up(), side, icon, metadata);
            }

            if (vertAxis == 1) {
               switchValues(0, 1, booleanArray);
               switchValues(2, 3, booleanArray);
            }
            break;
         case 3:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos5 = blockPos.south();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos5.west(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos5.east(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos5.down(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos5.up(), side, icon, metadata);
            }
            break;
         case 4:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos6 = blockPos.west();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos6.north(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos6.south(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos6.down(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos6.up(), side, icon, metadata);
            }
            break;
         case 5:
            booleanArray[0] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            booleanArray[1] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            booleanArray[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            booleanArray[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos7 = blockPos.east();
               booleanArray[0] = booleanArray[0] && !isNeighbour(cp, blockAccess, blockState, blockPos7.south(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] && !isNeighbour(cp, blockAccess, blockState, blockPos7.north(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] && !isNeighbour(cp, blockAccess, blockState, blockPos7.down(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] && !isNeighbour(cp, blockAccess, blockState, blockPos7.up(), side, icon, metadata);
            }

            if (vertAxis == 2) {
               switchValues(0, 1, booleanArray);
               switchValues(2, 3, booleanArray);
            }
      }

      int i = 0;
      if (booleanArray[0] & !booleanArray[1] & !booleanArray[2] & !booleanArray[3]) {
         i = 3;
      } else if (!booleanArray[0] & booleanArray[1] & !booleanArray[2] & !booleanArray[3]) {
         i = 1;
      } else if (!booleanArray[0] & !booleanArray[1] & booleanArray[2] & !booleanArray[3]) {
         i = 12;
      } else if (!booleanArray[0] & !booleanArray[1] & !booleanArray[2] & booleanArray[3]) {
         i = 36;
      } else if (booleanArray[0] & booleanArray[1] & !booleanArray[2] & !booleanArray[3]) {
         i = 2;
      } else if (!booleanArray[0] & !booleanArray[1] & booleanArray[2] & booleanArray[3]) {
         i = 24;
      } else if (booleanArray[0] & !booleanArray[1] & booleanArray[2] & !booleanArray[3]) {
         i = 15;
      } else if (booleanArray[0] & !booleanArray[1] & !booleanArray[2] & booleanArray[3]) {
         i = 39;
      } else if (!booleanArray[0] & booleanArray[1] & booleanArray[2] & !booleanArray[3]) {
         i = 13;
      } else if (!booleanArray[0] & booleanArray[1] & !booleanArray[2] & booleanArray[3]) {
         i = 37;
      } else if (!booleanArray[0] & booleanArray[1] & booleanArray[2] & booleanArray[3]) {
         i = 25;
      } else if (booleanArray[0] & !booleanArray[1] & booleanArray[2] & booleanArray[3]) {
         i = 27;
      } else if (booleanArray[0] & booleanArray[1] & !booleanArray[2] & booleanArray[3]) {
         i = 38;
      } else if (booleanArray[0] & booleanArray[1] & booleanArray[2] & !booleanArray[3]) {
         i = 14;
      } else if (booleanArray[0] & booleanArray[1] & booleanArray[2] & booleanArray[3]) {
         i = 26;
      }

      if (i == 0) {
         return i;
      }

      if (!Config.isConnectedTexturesFancy()) {
         return i;
      }

      switch (side) {
         case 0:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos8 = blockPos.down();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos8.east().north(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos8.west().north(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos8.east().south(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos8.west().south(), side, icon, metadata);
            }
            break;
         case 1:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos9 = blockPos.up();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos9.east().south(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos9.west().south(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos9.east().north(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos9.west().north(), side, icon, metadata);
            }
            break;
         case 2:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos10 = blockPos.north();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos10.west().down(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos10.east().down(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos10.west().up(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos10.east().up(), side, icon, metadata);
            }

            if (vertAxis == 1) {
               switchValues(0, 3, booleanArray);
               switchValues(1, 2, booleanArray);
            }
            break;
         case 3:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos11 = blockPos.south();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos11.east().down(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos11.west().down(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos11.east().up(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos11.west().up(), side, icon, metadata);
            }
            break;
         case 4:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos12 = blockPos.west();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos12.down().south(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos12.down().north(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos12.up().south(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos12.up().north(), side, icon, metadata);
            }
            break;
         case 5:
            booleanArray[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
            booleanArray[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
            booleanArray[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
            booleanArray[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
            if (cp.innerSeams) {
               BlockPos blockPos13 = blockPos.east();
               booleanArray[0] = booleanArray[0] || isNeighbour(cp, blockAccess, blockState, blockPos13.down().north(), side, icon, metadata);
               booleanArray[1] = booleanArray[1] || isNeighbour(cp, blockAccess, blockState, blockPos13.down().south(), side, icon, metadata);
               booleanArray[2] = booleanArray[2] || isNeighbour(cp, blockAccess, blockState, blockPos13.up().north(), side, icon, metadata);
               booleanArray[3] = booleanArray[3] || isNeighbour(cp, blockAccess, blockState, blockPos13.up().south(), side, icon, metadata);
            }

            if (vertAxis == 2) {
               switchValues(0, 3, booleanArray);
               switchValues(1, 2, booleanArray);
            }
      }

      if (i == 13 && booleanArray[0]) {
         i = 4;
      } else if (i == 15 && booleanArray[1]) {
         i = 5;
      } else if (i == 37 && booleanArray[2]) {
         i = 16;
      } else if (i == 39 && booleanArray[3]) {
         i = 17;
      } else if (i == 14 && booleanArray[0] && booleanArray[1]) {
         i = 7;
      } else if (i == 25 && booleanArray[0] && booleanArray[2]) {
         i = 6;
      } else if (i == 27 && booleanArray[3] && booleanArray[1]) {
         i = 19;
      } else if (i == 38 && booleanArray[3] && booleanArray[2]) {
         i = 18;
      } else if (i == 14 && !booleanArray[0] && booleanArray[1]) {
         i = 31;
      } else if (i == 25 && booleanArray[0] && !booleanArray[2]) {
         i = 30;
      } else if (i == 27 && !booleanArray[3] && booleanArray[1]) {
         i = 41;
      } else if (i == 38 && booleanArray[3] && !booleanArray[2]) {
         i = 40;
      } else if (i == 14 && booleanArray[0] && !booleanArray[1]) {
         i = 29;
      } else if (i == 25 && !booleanArray[0] && booleanArray[2]) {
         i = 28;
      } else if (i == 27 && booleanArray[3] && !booleanArray[1]) {
         i = 43;
      } else if (i == 38 && !booleanArray[3] && booleanArray[2]) {
         i = 42;
      } else if (i == 26 && booleanArray[0] && booleanArray[1] && booleanArray[2] && booleanArray[3]) {
         i = 46;
      } else if (i == 26 && !booleanArray[0] && booleanArray[1] && booleanArray[2] && booleanArray[3]) {
         i = 9;
      } else if (i == 26 && booleanArray[0] && !booleanArray[1] && booleanArray[2] && booleanArray[3]) {
         i = 21;
      } else if (i == 26 && booleanArray[0] && booleanArray[1] && !booleanArray[2] && booleanArray[3]) {
         i = 8;
      } else if (i == 26 && booleanArray[0] && booleanArray[1] && booleanArray[2] && !booleanArray[3]) {
         i = 20;
      } else if (i == 26 && booleanArray[0] && booleanArray[1] && !booleanArray[2] && !booleanArray[3]) {
         i = 11;
      } else if (i == 26 && !booleanArray[0] && !booleanArray[1] && booleanArray[2] && booleanArray[3]) {
         i = 22;
      } else if (i == 26 && !booleanArray[0] && booleanArray[1] && !booleanArray[2] && booleanArray[3]) {
         i = 23;
      } else if (i == 26 && booleanArray[0] && !booleanArray[1] && booleanArray[2] && !booleanArray[3]) {
         i = 10;
      } else if (i == 26 && booleanArray[0] && !booleanArray[1] && !booleanArray[2] && booleanArray[3]) {
         i = 34;
      } else if (i == 26 && !booleanArray[0] && booleanArray[1] && booleanArray[2] && !booleanArray[3]) {
         i = 35;
      } else if (i == 26 && booleanArray[0] && !booleanArray[1] && !booleanArray[2] && !booleanArray[3]) {
         i = 32;
      } else if (i == 26 && !booleanArray[0] && booleanArray[1] && !booleanArray[2] && !booleanArray[3]) {
         i = 33;
      } else if (i == 26 && !booleanArray[0] && !booleanArray[1] && booleanArray[2] && !booleanArray[3]) {
         i = 44;
      } else if (i == 26 && !booleanArray[0] && !booleanArray[1] && !booleanArray[2] && booleanArray[3]) {
         i = 45;
      }

      return i;
   }

   private static boolean isNeighbourOverlay(
      ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata
   ) {
      IBlockState blockState2 = blockAccess.getBlockState(blockPos);
      if (!isFullCubeModel(blockState2)) {
         return false;
      }

      if (cp.connectBlocks != null) {
         BlockStateBase blockStateBase = (BlockStateBase)blockState2;
         if (!Matches.block(blockStateBase.getBlockId(), blockStateBase.getMetadata(), cp.connectBlocks)) {
            return false;
         }
      }

      if (cp.connectTileIcons != null) {
         TextureAtlasSprite textureAtlasSprite = getNeighbourIcon(blockAccess, blockState, blockPos, blockState2, side);
         if (!Config.isSameOne(textureAtlasSprite, cp.connectTileIcons)) {
            return false;
         }
      }

      IBlockState blockState3 = blockAccess.getBlockState(blockPos.offset(getFacing(side)));
      return blockState3.getBlock().isOpaqueCube()
         ? false
         : (
            side == 1 && blockState3.getBlock() == Blocks.snow_layer
               ? false
               : !isNeighbour(cp, blockAccess, blockState, blockPos, blockState2, side, icon, metadata)
         );
   }

   private static BakedQuad[] getConnectedTextureOverlayRepeat(ConnectedProperties cp, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv) {
      if (!quad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(cp.layer);

      Object object;
      try {
         TextureAtlasSprite connectedTextureRepeat = getConnectedTextureRepeat(cp, blockPos, side);
         if (connectedTextureRepeat != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureRepeat, quad, cp.tintIndex), cp.tintBlockState);
         }

         object = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])object;
   }

   private static BlockDir[] getEdgeDirections(int side, int vertAxis) {
      switch (side) {
         case 0:
            return EDGES_Y_NEG_DOWN;
         case 1:
            return EDGES_Y_POS_UP;
         case 2:
            if (vertAxis == 1) {
               return EDGES_Z_NEG_NORTH_Z_AXIS;
            }

            return EDGES_Z_NEG_NORTH;
         case 3:
            return EDGES_Z_POS_SOUTH;
         case 4:
            return EDGES_X_NEG_WEST;
         case 5:
            if (vertAxis == 2) {
               return EDGES_X_POS_EAST_X_AXIS;
            }

            return EDGES_X_POS_EAST;
         default:
            throw new IllegalArgumentException("Unknown side: " + side);
      }
   }

   private static boolean isNeighbour(
      ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata
   ) {
      IBlockState blockState2 = blockAccess.getBlockState(blockPos);
      return isNeighbour(cp, blockAccess, blockState, blockPos, blockState2, side, icon, metadata);
   }

   private static void updateIconEmpty(TextureMap textureMap) {
   }

   private static void addToBlockList(ConnectedProperties cp, List blockList) {
      if (cp.matchBlocks != null) {
         for (int index = 0; index < cp.matchBlocks.length; index++) {
            int blockId = cp.matchBlocks[index].getBlockId();
            if (blockId < 0) {
               Config.warn("Invalid block ID: " + blockId);
            } else {
               addToList(cp, blockList, blockId);
            }
         }
      }
   }

   private static BakedQuad[] getConnectedTextureOverlayFixed(ConnectedProperties cp, BakedQuad quad, RenderEnv renderEnv) {
      if (!quad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(cp.layer);

      Object object;
      try {
         TextureAtlasSprite connectedTextureFixed = getConnectedTextureFixed(cp);
         if (connectedTextureFixed != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureFixed, quad, cp.tintIndex), cp.tintBlockState);
         }

         object = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])object;
   }

   private static boolean checkConnectedProperties(
      ConnectedProperties connectedProperties, IBlockAccess iBlockAccess, IBlockState iBlockState, BlockPos blockPos14, int i, TextureAtlasSprite textureAtlasSprite3
   ) {
      IBlockState blockState4 = iBlockAccess.getBlockState(blockPos14);
      if (blockState4 == AIR_DEFAULT_STATE) {
         return false;
      } else if (connectedProperties.matchBlocks != null && blockState4 instanceof BlockStateBase blockStateBase2 && !connectedProperties.matchesBlock(blockStateBase2.getBlockId(), blockStateBase2.getMetadata())) {
         return false;
      } else {
         if (connectedProperties.matchTileIcons != null) {
            TextureAtlasSprite neighbourIcon = getNeighbourIcon(iBlockAccess, iBlockState, blockPos14, blockState4, i);
            if (neighbourIcon != textureAtlasSprite3) {
               return false;
            }
         }

         IBlockState blockState5 = iBlockAccess.getBlockState(blockPos14.offset(getFacing(i)));
         return blockState5.getBlock().isOpaqueCube() ? false : i != 1 || blockState5.getBlock() != Blocks.snow_layer;
      }
   }

   private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
      TextureAtlasSprite textureAtlasSprite = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      if (textureAtlasSprite != null && textureAtlasSprite != icon && textureAtlasSprite != atextureatlassprite[3]) {
         return textureAtlasSprite;
      }

      TextureAtlasSprite textureAtlasSprite2 = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      return textureAtlasSprite2 == atextureatlassprite[0]
         ? atextureatlassprite[4]
         : (
            textureAtlasSprite2 == atextureatlassprite[1]
               ? atextureatlassprite[5]
               : (textureAtlasSprite2 == atextureatlassprite[2] ? atextureatlassprite[6] : textureAtlasSprite2)
         );
   }

   private static BakedQuad[] getConnectedTexture(
      ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, int pass, RenderEnv renderEnv
   ) {
      int i = 0;
      int metadata2 = blockState.getMetadata();
      int i2 = metadata2;
      Block block = blockState.getBlock();
      if (block instanceof BlockRotatedPillar) {
         i = createInt2(metadata2);
         if (cp.getMetadataMax() <= 3) {
            i2 = metadata2 & 3;
         }
      }

      if (block instanceof BlockQuartz) {
         i = createInt(metadata2);
         if (cp.getMetadataMax() <= 2 && i2 > 2) {
            i2 = 2;
         }
      }

      if (!cp.matchesBlock(blockState.getBlockId(), i2)) {
         return null;
      }

      if (side >= 0 && cp.faces != 63) {
         int side2 = side;
         if (i != 0) {
            side2 = fixSideByAxis(side, i);
         }

         if ((1 << side2 & cp.faces) == 0) {
            return null;
         }
      }

      int yCoordinate = blockPos.getY();
      if (cp.heights != null && !cp.heights.isInRange(yCoordinate)) {
         return null;
      }

      if (cp.biomes != null) {
         BiomeGenBase biomeGenBase = blockAccess.getBiomeGenForCoords(blockPos);
         if (!cp.matchesBiome(biomeGenBase)) {
            return null;
         }
      }

      if (cp.nbtName != null) {
         String text = TileEntityUtils.getTileEntityName(blockAccess, blockPos);
         if (!cp.nbtName.matchesValue(text)) {
            return null;
         }
      }

      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      switch (cp.method) {
         case 1:
            return getQuads(
               getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2, renderEnv), quad, renderEnv
            );
         case 2:
            return getQuads(
               getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2), quad, renderEnv
            );
         case 3:
            return getQuads(getConnectedTextureTop(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2), quad, renderEnv);
         case 4:
            return getQuads(getConnectedTextureRandom(cp, blockAccess, blockState, blockPos, side), quad, renderEnv);
         case 5:
            return getQuads(getConnectedTextureRepeat(cp, blockPos, side), quad, renderEnv);
         case 6:
            return getQuads(
               getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2), quad, renderEnv
            );
         case 7:
            return getQuads(getConnectedTextureFixed(cp), quad, renderEnv);
         case 8:
            return getQuads(
               getConnectedTextureHorizontalVertical(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2),
               quad,
               renderEnv
            );
         case 9:
            return getQuads(
               getConnectedTextureVerticalHorizontal(cp, blockAccess, blockState, blockPos, i, side, textureAtlasSprite, metadata2),
               quad,
               renderEnv
            );
         case 10:
            if (pass == 0) {
               return getConnectedTextureCtmCompact(cp, blockAccess, blockState, blockPos, i, side, quad, metadata2, renderEnv);
            }
         default:
            return null;
         case 11:
            return getConnectedTextureOverlay(cp, blockAccess, blockState, blockPos, i, side, quad, metadata2, renderEnv);
         case 12:
            return getConnectedTextureOverlayFixed(cp, quad, renderEnv);
         case 13:
            return getConnectedTextureOverlayRandom(cp, blockAccess, blockState, blockPos, side, quad, renderEnv);
         case 14:
            return getConnectedTextureOverlayRepeat(cp, blockPos, side, quad, renderEnv);
         case 15:
            return getConnectedTextureOverlayCtm(cp, blockAccess, blockState, blockPos, i, side, quad, metadata2, renderEnv);
      }
   }

   private static synchronized BakedQuad getQuad(TextureAtlasSprite sprite, BakedQuad quadIn) {
      if (spriteQuadMaps == null) {
         return quadIn;
      }

      int indexInMap = sprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < spriteQuadMaps.length) {
         Map map = spriteQuadMaps[indexInMap];
         if (map == null) {
            map = new IdentityHashMap(1);
            spriteQuadMaps[indexInMap] = map;
         }

         BakedQuad bakedQuad = (BakedQuad)map.get(quadIn);
         if (bakedQuad == null) {
            bakedQuad = makeSpriteQuad(quadIn, sprite);
            map.put(quadIn, bakedQuad);
         }

         return bakedQuad;
      } else {
         return quadIn;
      }
   }

   protected static BakedQuad[] getQuads(TextureAtlasSprite sprite, BakedQuad quadIn, RenderEnv renderEnv) {
      if (sprite == null) {
         return null;
      }

      if (sprite == SPRITE_DEFAULT) {
         return renderEnv.getArrayQuadsCtm(quadIn);
      }

      BakedQuad quad2 = getQuad(sprite, quadIn);
      return renderEnv.getArrayQuadsCtm(quad2);
   }

   private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp, BlockPos blockPos, int side) {
      if (cp.tileIcons.length == 1) {
         return cp.tileIcons[0];
      }

      int xCoordinate = blockPos.getX();
      int yCoordinate = blockPos.getY();
      int zCoordinate = blockPos.getZ();
      int i = 0;
      int i2 = 0;
      switch (side) {
         case 0:
            i = xCoordinate;
            i2 = -zCoordinate - 1;
            break;
         case 1:
            i = xCoordinate;
            i2 = zCoordinate;
            break;
         case 2:
            i = -xCoordinate - 1;
            i2 = -yCoordinate;
            break;
         case 3:
            i = xCoordinate;
            i2 = -yCoordinate;
            break;
         case 4:
            i = zCoordinate;
            i2 = -yCoordinate;
            break;
         case 5:
            i = -zCoordinate - 1;
            i2 = -yCoordinate;
      }

      i %= cp.width;
      i2 %= cp.height;
      if (i < 0) {
         i += cp.width;
      }

      if (i2 < 0) {
         i2 += cp.height;
      }

      int i3 = i2 * cp.width + i;
      return cp.tileIcons[i3];
   }

   private static int fixSideByAxis(int side, int vertAxis) {
      switch (vertAxis) {
         case 0:
            return side;
         case 1:
            switch (side) {
               case 0:
                  return 2;
               case 1:
                  return 3;
               case 2:
                  return 1;
               case 3:
                  return 0;
               default:
                  return side;
            }
         case 2:
            switch (side) {
               case 0:
                  return 4;
               case 1:
                  return 5;
               case 2:
               case 3:
               default:
                  return side;
               case 4:
                  return 1;
               case 5:
                  return 0;
            }
         default:
            return side;
      }
   }

   private static String[] getDefaultCtmPaths() {
      ArrayList arrayList = new ArrayList();
      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
         arrayList.add("mcpatcher/ctm/default/glass.properties");
         arrayList.add("mcpatcher/ctm/default/glasspane.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
         arrayList.add("mcpatcher/ctm/default/bookshelf.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
         arrayList.add("mcpatcher/ctm/default/sandstone.properties");
      }

      String[] texts = new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
      };

      for (int i = 0; i < texts.length; i++) {
         String text2 = texts[i];
         if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + text2 + ".png"))) {
            arrayList.add("mcpatcher/ctm/default/" + i + "_glass_" + text2 + "/glass_" + text2 + ".properties");
            arrayList.add("mcpatcher/ctm/default/" + i + "_glass_" + text2 + "/glass_pane_" + text2 + ".properties");
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   private static ConnectedProperties[][] propertyListToArray(List list) {
      ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[list.size()][];

      for (int index = 0; index < list.size(); index++) {
         List sublist = (List)list.get(index);
         if (sublist != null) {
            ConnectedProperties[] connectedPropertiesArray = (ConnectedProperties[]) sublist.toArray(new ConnectedProperties[sublist.size()]);
            aconnectedproperties[index] = connectedPropertiesArray;
         }
      }

      return aconnectedproperties;
   }

   private static TextureAtlasSprite getConnectedTextureHorizontalVertical(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
      TextureAtlasSprite textureAtlasSprite = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      if (textureAtlasSprite != null && textureAtlasSprite != icon && textureAtlasSprite != atextureatlassprite[3]) {
         return textureAtlasSprite;
      }

      TextureAtlasSprite textureAtlasSprite2 = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      return textureAtlasSprite2 == atextureatlassprite[0]
         ? atextureatlassprite[4]
         : (
            textureAtlasSprite2 == atextureatlassprite[1]
               ? atextureatlassprite[5]
               : (textureAtlasSprite2 == atextureatlassprite[2] ? atextureatlassprite[6] : textureAtlasSprite2)
         );
   }

   private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo) {
      int i = data.length / 4 * vertex;
      float f = Float.intBitsToFloat(data[i + 4]);
      float f2 = Float.intBitsToFloat(data[i + 4 + 1]);
      double spriteU = spriteFrom.getSpriteU16(f);
      double spriteV = spriteFrom.getSpriteV16(f2);
      data[i + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(spriteU));
      data[i + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(spriteV));
   }

   private static TextureAtlasSprite getConnectedTextureVertical(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      boolean conditionMet = false;
      boolean conditionMet2 = false;
      switch (vertAxis) {
         case 0:
            if (side == 1) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            } else if (side == 0) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            } else {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            }
            break;
         case 1:
            if (side == 3) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            } else if (side == 2) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            } else {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
            }
            break;
         case 2:
            if (side == 5) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
            } else if (side == 4) {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            } else {
               conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
               conditionMet2 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
            }
      }

      int i;
      if (conditionMet) {
         if (conditionMet2) {
            i = 1;
         } else {
            i = 2;
         }
      } else if (conditionMet2) {
         i = 0;
      } else {
         i = 3;
      }

      return cp.tileIcons[i];
   }

   private static boolean detectMultipass() {
      ArrayList arrayList = new ArrayList();

      for (int index = 0; index < tileProperties.length; index++) {
         ConnectedProperties[] connectedPropertiesArray = tileProperties[index];
         if (connectedPropertiesArray != null) {
            arrayList.addAll(Arrays.asList(connectedPropertiesArray));
         }
      }

      for (int nestedIndex = 0; nestedIndex < blockProperties.length; nestedIndex++) {
         ConnectedProperties[] connectedPropertiesArray2 = blockProperties[nestedIndex];
         if (connectedPropertiesArray2 != null) {
            arrayList.addAll(Arrays.asList(connectedPropertiesArray2));
         }
      }

      ConnectedProperties[] connectedPropertiesArray3 = (ConnectedProperties[]) arrayList.toArray(new ConnectedProperties[arrayList.size()]);
      HashSet hashSet = new HashSet();
      HashSet hashSet2 = new HashSet();

      for (int innerIndex = 0; innerIndex < connectedPropertiesArray3.length; innerIndex++) {
         ConnectedProperties connectedProperties = connectedPropertiesArray3[innerIndex];
         if (connectedProperties.matchTileIcons != null) {
            hashSet.addAll(Arrays.asList(connectedProperties.matchTileIcons));
         }

         if (connectedProperties.tileIcons != null) {
            hashSet2.addAll(Arrays.asList(connectedProperties.tileIcons));
         }
      }

      hashSet.retainAll(hashSet2);
      return !hashSet.isEmpty();
   }

   private static BakedQuad[] getConnectedTextureOverlayRandom(
      ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, BakedQuad quad, RenderEnv renderEnv
   ) {
      if (!quad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(cp.layer);

      Object object;
      try {
         TextureAtlasSprite connectedTextureRandom = getConnectedTextureRandom(cp, blockAccess, blockState, blockPos, side);
         if (connectedTextureRandom != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureRandom, quad, cp.tintIndex), cp.tintBlockState);
         }

         object = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])object;
   }

   private static TextureAtlasSprite getConnectedTextureCtm(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata,
      RenderEnv renderEnv
   ) {
      int connectedTextureCtmIndex = getConnectedTextureCtmIndex(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata, renderEnv);
      return cp.tileIcons[connectedTextureCtmIndex];
   }

   private static TextureAtlasSprite getConnectedTextureTop(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      boolean conditionMet = false;
      switch (vertAxis) {
         case 0:
            if (side == 1 || side == 0) {
               return null;
            }

            conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
            break;
         case 1:
            if (side == 3 || side == 2) {
               return null;
            }

            conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
            break;
         case 2:
            if (side == 5 || side == 4) {
               return null;
            }

            conditionMet = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
      }

      return conditionMet ? cp.tileIcons[0] : null;
   }

   protected static Map[][] getSpriteQuadCompactMaps() {
      return spriteQuadCompactMaps;
   }

   private static List makePropertyList(ConnectedProperties[][] propsArr) {
      ArrayList arrayList = new ArrayList();
      if (propsArr != null) {
         for (int index = 0; index < propsArr.length; index++) {
            ConnectedProperties[] connectedPropertiesArray = propsArr[index];
            ArrayList arrayList2 = null;
            if (connectedPropertiesArray != null) {
               arrayList2 = new ArrayList<>(Arrays.asList(connectedPropertiesArray));
            }

            arrayList.add(arrayList2);
         }
      }

      return arrayList;
   }

   public static BakedQuad[] getConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv) {
      if (quad.getSprite() == null) {
         return renderEnv.getArrayQuadsCtm(quad);
      } else {
         blockState.getBlock();
         if (skipConnectedTexture(blockAccess, blockState, blockPos, quad, renderEnv)) {
            quad = getQuad(emptySprite, quad);
            return renderEnv.getArrayQuadsCtm(quad);
         } else {
            EnumFacing face = quad.getFace();
            return getConnectedTextureMultiPass(blockAccess, blockState, blockPos, face, quad, renderEnv);
         }
      }
   }

   private static void switchValues(int ix1, int ix2, boolean[] arr) {
      boolean flag = arr[ix1];
      arr[ix1] = arr[ix2];
      arr[ix2] = flag;
   }

   private static synchronized BakedQuad getQuadFull(TextureAtlasSprite sprite, BakedQuad quadIn, int tintIndex) {
      if (spriteQuadFullMaps == null) {
         return null;
      }

      if (sprite == null) {
         return null;
      }

      int indexInMap = sprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < spriteQuadFullMaps.length) {
         Map map = spriteQuadFullMaps[indexInMap];
         if (map == null) {
            map = new EnumMap<>(EnumFacing.class);
            spriteQuadFullMaps[indexInMap] = map;
         }

         EnumFacing facing = quadIn.getFace();
         BakedQuad bakedQuad = (BakedQuad)map.get(facing);
         if (bakedQuad == null) {
            bakedQuad = BlockModelUtils.makeBakedQuad(facing, sprite, tintIndex);
            map.put(facing, bakedQuad);
         }

         return bakedQuad;
      } else {
         return null;
      }
   }

   public static BakedQuad[] getConnectedTextureSingle(
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      EnumFacing facing,
      BakedQuad quad,
      boolean checkBlocks,
      int pass,
      RenderEnv renderEnv
   ) {
      blockState.getBlock();
      if (!(blockState instanceof BlockStateBase blockStateBase2)) {
         return renderEnv.getArrayQuadsCtm(quad);
      } else {
         TextureAtlasSprite sprite2 = quad.getSprite();
         if (tileProperties != null) {
            int indexInMap = sprite2.getIndexInMap();
            if (indexInMap >= 0 && indexInMap < tileProperties.length) {
               ConnectedProperties[] connectedPropertiesArray = tileProperties[indexInMap];
               if (connectedPropertiesArray != null) {
                  int side2 = getSide(facing);

                  for (int i = 0; i < connectedPropertiesArray.length; i++) {
                     ConnectedProperties connectedProperties = connectedPropertiesArray[i];
                     if (connectedProperties != null && connectedProperties.matchesBlockId(blockStateBase2.getBlockId())) {
                        BakedQuad[] connectedTexture = getConnectedTexture(connectedProperties, blockAccess, blockStateBase2, blockPos, side2, quad, pass, renderEnv);
                        if (connectedTexture != null) {
                           return connectedTexture;
                        }
                     }
                  }
               }
            }
         }

         if (blockProperties != null && checkBlocks) {
            int blockId = renderEnv.getBlockId();
            if (blockId >= 0 && blockId < blockProperties.length) {
               ConnectedProperties[] connectedPropertiesArray2 = blockProperties[blockId];
               if (connectedPropertiesArray2 != null) {
                  int side3 = getSide(facing);

                  for (int i2 = 0; i2 < connectedPropertiesArray2.length; i2++) {
                     ConnectedProperties connectedProperties2 = connectedPropertiesArray2[i2];
                     if (connectedProperties2 != null && connectedProperties2.matchesIcon(sprite2)) {
                        BakedQuad[] connectedTexture2 = getConnectedTexture(connectedProperties2, blockAccess, blockStateBase2, blockPos, side3, quad, pass, renderEnv);
                        if (connectedTexture2 != null) {
                           return connectedTexture2;
                        }
                     }
                  }
               }
            }
         }

         return renderEnv.getArrayQuadsCtm(quad);
      }
   }

   private static boolean isNeighbour(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      IBlockState neighbourState,
      int side,
      TextureAtlasSprite icon,
      int metadata
   ) {
      if (blockState == neighbourState) {
         return true;
      }

      if (cp.connect == 2) {
         if (neighbourState == null) {
            return false;
         }

         if (neighbourState == AIR_DEFAULT_STATE) {
            return false;
         }

         TextureAtlasSprite textureAtlasSprite = getNeighbourIcon(blockAccess, blockState, blockPos, neighbourState, side);
         return textureAtlasSprite == icon;
      } else if (cp.connect == 3) {
         return neighbourState == null
            ? false
            : (neighbourState == AIR_DEFAULT_STATE ? false : neighbourState.getBlock().getMaterial() == blockState.getBlock().getMaterial());
      } else if (!(neighbourState instanceof BlockStateBase blockStateBase)) {
         return false;
      } else {
         Block block = blockStateBase.getBlock();
         int metadata2 = blockStateBase.getMetadata();
         return block == blockState.getBlock() && metadata2 == metadata;
      }
   }

   public static int getSide(EnumFacing facing) {
      if (facing == null) {
         return -1;
      }

      switch (facing) {
         case DOWN:
            return 0;
         case UP:
            return 1;
         case EAST:
            return 5;
         case WEST:
            return 4;
         case NORTH:
            return 2;
         case SOUTH:
            return 3;
         default:
            return -1;
      }
   }

   public static void updateIcons(TextureMap textureMap, IResourcePack rp) {
      String[] texts = ResUtils.collectFiles(rp, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
      Arrays.sort(texts);
      List list3 = makePropertyList(tileProperties);
      List list4 = makePropertyList(blockProperties);

      for (int i = 0; i < texts.length; i++) {
         String text2 = texts[i];
         Config.dbg("ConnectedTextures: " + text2);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(text2);
            InputStream inputStream = rp.getInputStream(resourceLocation);
            if (inputStream == null) {
               Config.warn("ConnectedTextures file not found: " + text2);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               ConnectedProperties connectedProperties = new ConnectedProperties(propertiesOrdered, text2);
               if (connectedProperties.isValid(text2)) {
                  connectedProperties.updateIcons(textureMap);
                  addToTileList(connectedProperties, list3);
                  addToBlockList(connectedProperties, list4);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("ConnectedTextures file not found: " + text2);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }

      blockProperties = propertyListToArray(list4);
      tileProperties = propertyListToArray(list3);
      multipass = detectMultipass();
      Config.dbg("Multipass connected textures: " + multipass);
   }

   private static TextureAtlasSprite getConnectedTextureRandom(
      ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side
   ) {
      if (cp.tileIcons.length == 1) {
         return cp.tileIcons[0];
      }

      int i = side / cp.symmetry * cp.symmetry;
      if (cp.linked) {
         BlockPos blockPos2 = blockPos.down();

         for (IBlockState blockState2 = blockAccess.getBlockState(blockPos2);
            blockState2.getBlock() == blockState.getBlock();
            blockState2 = blockAccess.getBlockState(blockPos2)
         ) {
            blockPos = blockPos2;
            blockPos2 = blockPos2.down();
            if (blockPos2.getY() < 0) {
               break;
            }
         }
      }

      int i2 = Config.getRandom(blockPos, i) & 2147483647;

      for (int index = 0; index < cp.randomLoops; index++) {
         i2 = Config.intHash(i2);
      }

      int i3 = 0;
      if (cp.weights == null) {
         i3 = i2 % cp.tileIcons.length;
      } else {
         int i4 = i2 % cp.sumAllWeights;
         int[] intArray = cp.sumWeights;

         for (int nestedIndex = 0; nestedIndex < intArray.length; nestedIndex++) {
            if (i4 < intArray[nestedIndex]) {
               i3 = nestedIndex;
               break;
            }
         }
      }

      return cp.tileIcons[i3];
   }

   private static int createInt(int i) {
      switch (i) {
         case 3:
            return 2;
         case 4:
            return 1;
         default:
            return 0;
      }
   }

   private static void addToList(ConnectedProperties cp, List lists, int id) {
      while (id >= lists.size()) {
         lists.add(null);
      }

      List list = (List)lists.get(id);
      if (list == null) {
         list = new ArrayList();
         lists.set(id, list);
      }

      list.add(cp);
   }

   private static BakedQuad[] getConnectedTextureOverlayCtm(
      ConnectedProperties cp,
      IBlockAccess blockAccess,
      IBlockState blockState,
      BlockPos blockPos,
      int vertAxis,
      int side,
      BakedQuad quad,
      int metadata,
      RenderEnv renderEnv
   ) {
      if (!quad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(cp.layer);

      Object object;
      try {
         TextureAtlasSprite connectedTextureCtm = getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, vertAxis, side, quad.getSprite(), metadata, renderEnv);
         if (connectedTextureCtm != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureCtm, quad, cp.tintIndex), cp.tintBlockState);
         }

         object = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])object;
   }
}
