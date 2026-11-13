package net.optifine;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.optifine.model.ModelUtils;

public class SmartLeaves {
   private static IBakedModel modelLeavesCullAcacia = null;
   private static IBakedModel modelLeavesCullBirch = null;
   private static IBakedModel modelLeavesCullDarkOak = null;
   private static IBakedModel modelLeavesCullJungle = null;
   private static IBakedModel modelLeavesCullOak = null;
   private static IBakedModel modelLeavesCullSpruce = null;
   private static List generalQuadsCullAcacia = null;
   private static List generalQuadsCullBirch = null;
   private static List generalQuadsCullDarkOak = null;
   private static List generalQuadsCullJungle = null;
   private static List generalQuadsCullOak = null;
   private static List generalQuadsCullSpruce = null;
   private static IBakedModel modelLeavesDoubleAcacia = null;
   private static IBakedModel modelLeavesDoubleBirch = null;
   private static IBakedModel modelLeavesDoubleDarkOak = null;
   private static IBakedModel modelLeavesDoubleJungle = null;
   private static IBakedModel modelLeavesDoubleOak = null;
   private static IBakedModel modelLeavesDoubleSpruce = null;

   public static boolean isSameLeaves(IBlockState state1, IBlockState state2) {
      if (state1 == state2) {
         return true;
      }

      Block block = state1.getBlock();
      Block block2 = state2.getBlock();
      return block != block2
         ? false
         : (
            block instanceof BlockOldLeaf
               ? state1.getValue(BlockOldLeaf.VARIANT).equals(state2.getValue(BlockOldLeaf.VARIANT))
               : (block instanceof BlockNewLeaf ? state1.getValue(BlockNewLeaf.VARIANT).equals(state2.getValue(BlockNewLeaf.VARIANT)) : false)
         );
   }

   public static IBakedModel getLeavesModel(IBakedModel model, IBlockState stateIn) {
      if (!Config.isTreesSmart()) {
         return model;
      }

      List list = model.getGeneralQuads();
      return list == generalQuadsCullAcacia
         ? modelLeavesDoubleAcacia
         : (
            list == generalQuadsCullBirch
               ? modelLeavesDoubleBirch
               : (
                  list == generalQuadsCullDarkOak
                     ? modelLeavesDoubleDarkOak
                     : (
                        list == generalQuadsCullJungle
                           ? modelLeavesDoubleJungle
                           : (list == generalQuadsCullOak ? modelLeavesDoubleOak : (list == generalQuadsCullSpruce ? modelLeavesDoubleSpruce : model))
                     )
               )
         );
   }

   public static void updateLeavesModels() {
      ArrayList arrayList = new ArrayList();
      modelLeavesCullAcacia = getModelCull("acacia", arrayList);
      modelLeavesCullBirch = getModelCull("birch", arrayList);
      modelLeavesCullDarkOak = getModelCull("dark_oak", arrayList);
      modelLeavesCullJungle = getModelCull("jungle", arrayList);
      modelLeavesCullOak = getModelCull("oak", arrayList);
      modelLeavesCullSpruce = getModelCull("spruce", arrayList);
      generalQuadsCullAcacia = getGeneralQuadsSafe(modelLeavesCullAcacia);
      generalQuadsCullBirch = getGeneralQuadsSafe(modelLeavesCullBirch);
      generalQuadsCullDarkOak = getGeneralQuadsSafe(modelLeavesCullDarkOak);
      generalQuadsCullJungle = getGeneralQuadsSafe(modelLeavesCullJungle);
      generalQuadsCullOak = getGeneralQuadsSafe(modelLeavesCullOak);
      generalQuadsCullSpruce = getGeneralQuadsSafe(modelLeavesCullSpruce);
      modelLeavesDoubleAcacia = getModelDoubleFace(modelLeavesCullAcacia);
      modelLeavesDoubleBirch = getModelDoubleFace(modelLeavesCullBirch);
      modelLeavesDoubleDarkOak = getModelDoubleFace(modelLeavesCullDarkOak);
      modelLeavesDoubleJungle = getModelDoubleFace(modelLeavesCullJungle);
      modelLeavesDoubleOak = getModelDoubleFace(modelLeavesCullOak);
      modelLeavesDoubleSpruce = getModelDoubleFace(modelLeavesCullSpruce);
      if (arrayList.size() > 0) {
         Config.dbg("Enable face culling: " + Config.arrayToString(arrayList.toArray()));
      }
   }

   private static List getGeneralQuadsSafe(IBakedModel model) {
      return model == null ? null : model.getGeneralQuads();
   }

   private static IBakedModel getModelDoubleFace(IBakedModel model) {
      if (model == null) {
         return null;
      }

      if (model.getGeneralQuads().size() > 0) {
         Config.warn("SmartLeaves: Model is not cube, general quads: " + model.getGeneralQuads().size() + ", model: " + model);
         return model;
      }

      EnumFacing[] enumFacings = EnumFacing.VALUES;

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List faceQuads = model.getFaceQuads(enumFacing);
         if (faceQuads.size() != 1) {
            Config.warn("SmartLeaves: Model is not cube, side: " + enumFacing + ", quads: " + faceQuads.size() + ", model: " + model);
            return model;
         }
      }

      IBakedModel iBakedModel = ModelUtils.duplicateModel(model);

      for (EnumFacing enumFacing2 : enumFacings) {
         List faceQuads2 = iBakedModel.getFaceQuads(enumFacing2);
         BakedQuad bakedQuad = (BakedQuad)faceQuads2.get(0);
         BakedQuad bakedQuad2 = new BakedQuad((int[])bakedQuad.getVertexData().clone(), bakedQuad.getTintIndex(), bakedQuad.getFace(), bakedQuad.getSprite());
         int[] vertexData = bakedQuad2.getVertexData();
         int[] iArray = (int[])vertexData.clone();
         int i2 = vertexData.length / 4;
         System.arraycopy(vertexData, 0 * i2, iArray, 3 * i2, i2);
         System.arraycopy(vertexData, 1 * i2, iArray, 2 * i2, i2);
         System.arraycopy(vertexData, 2 * i2, iArray, 1 * i2, i2);
         System.arraycopy(vertexData, 3 * i2, iArray, 0 * i2, i2);
         System.arraycopy(iArray, 0, vertexData, 0, iArray.length);
         faceQuads2.add(bakedQuad2);
      }

      return iBakedModel;
   }

   static IBakedModel getModelCull(String type, List updatedTypes) {
      ModelManager modelManager = Config.getModelManager();
      if (modelManager == null) {
         return null;
      }

      if (Config.getDefiningResourcePack(new ResourceLocation("blockstates/" + type + "_leaves.json")) != Config.getDefaultResourcePack()) {
         return null;
      }

      if (Config.getDefiningResourcePack(new ResourceLocation("models/block/" + type + "_leaves.json")) != Config.getDefaultResourcePack()) {
         return null;
      }

      ModelResourceLocation modelResourceLocation = new ModelResourceLocation(type + "_leaves", "normal");
      IBakedModel model2 = modelManager.getModel(modelResourceLocation);
      if (model2 != null && model2 != modelManager.getMissingModel()) {
         List generalQuads = model2.getGeneralQuads();
         if (generalQuads.size() == 0) {
            return model2;
         }

         if (generalQuads.size() != 6) {
            return null;
         }

         for (BakedQuad bakedQuad : (Iterable<BakedQuad>) generalQuads) {
            List faceQuads = model2.getFaceQuads(bakedQuad.getFace());
            if (faceQuads.size() > 0) {
               return null;
            }

            faceQuads.add(bakedQuad);
         }

         generalQuads.clear();
         updatedTypes.add(type + "_leaves");
         return model2;
      } else {
         return null;
      }
   }
}
