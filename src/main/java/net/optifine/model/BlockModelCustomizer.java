package net.optifine.model;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.optifine.BetterGrass;
import net.optifine.ConnectedTextures;
import net.optifine.NaturalTextures;
import net.optifine.SmartLeaves;
import net.optifine.render.RenderEnv;

public class BlockModelCustomizer {
   private static final List<BakedQuad> NO_QUADS = ImmutableList.of();

   public static List createList(List list, IBlockAccess iBlockAccess, IBlockState iBlockState, BlockPos blockPos, EnumFacing enumFacing, EnumWorldBlockLayer enumWorldBlockLayer, RenderEnv renderEnv2) {
      if (enumFacing != null) {
         if (renderEnv2.isSmartLeaves() && SmartLeaves.isSameLeaves(iBlockAccess.getBlockState(blockPos.offset(enumFacing)), iBlockState)) {
            return NO_QUADS;
         }

         if (!renderEnv2.isBreakingAnimation(list) && Config.isBetterGrass()) {
            list = BetterGrass.getFaceQuads(iBlockAccess, iBlockState, blockPos, enumFacing, list);
         }
      }

      List listQuadsCustomizer = renderEnv2.getListQuadsCustomizer();
      listQuadsCustomizer.clear();

      for (int i = 0; i < list.size(); i++) {
         BakedQuad bakedQuad = (BakedQuad)list.get(i);
         BakedQuad[] array = createArray(bakedQuad, iBlockAccess, iBlockState, blockPos, enumFacing, renderEnv2);
         if (i == 0 && list.size() == 1 && array.length == 1 && array[0] == bakedQuad && bakedQuad.getQuadEmissive() == null) {
            return list;
         }

         for (int i2 = 0; i2 < array.length; i2++) {
            BakedQuad bakedQuad2 = array[i2];
            listQuadsCustomizer.add(bakedQuad2);
            if (bakedQuad2.getQuadEmissive() != null) {
               renderEnv2.getListQuadsOverlay(getEmissiveLayer(enumWorldBlockLayer)).addQuad(bakedQuad2.getQuadEmissive(), iBlockState);
               renderEnv2.setOverlaysRendered(true);
            }
         }
      }

      return listQuadsCustomizer;
   }

   private static BakedQuad[] createArray(BakedQuad bakedQuad, IBlockAccess iBlockAccess, IBlockState iBlockState, BlockPos blockPos, EnumFacing enumFacing, RenderEnv renderEnv2) {
      if (renderEnv2.isBreakingAnimation(bakedQuad)) {
         return renderEnv2.getArrayQuadsCtm(bakedQuad);
      }

      BakedQuad bakedQuad2 = bakedQuad;
      if (Config.isConnectedTextures()) {
         BakedQuad[] connectedTexture = ConnectedTextures.getConnectedTexture(iBlockAccess, iBlockState, blockPos, bakedQuad, renderEnv2);
         if (connectedTexture.length != 1 || connectedTexture[0] != bakedQuad) {
            return connectedTexture;
         }
      }

      if (Config.isNaturalTextures()) {
         bakedQuad = NaturalTextures.getNaturalTexture(blockPos, bakedQuad);
         if (bakedQuad != bakedQuad2) {
            return renderEnv2.getArrayQuadsCtm(bakedQuad);
         }
      }

      return renderEnv2.getArrayQuadsCtm(bakedQuad);
   }

   public static IBakedModel getRenderModel(IBakedModel modelIn, IBlockState stateIn, RenderEnv renderEnv) {
      if (renderEnv.isSmartLeaves()) {
         modelIn = SmartLeaves.getLeavesModel(modelIn, stateIn);
      }

      return modelIn;
   }

   private static EnumWorldBlockLayer getEmissiveLayer(EnumWorldBlockLayer layer) {
      return layer != null && layer != EnumWorldBlockLayer.SOLID ? layer : EnumWorldBlockLayer.CUTOUT_MIPPED;
   }
}
