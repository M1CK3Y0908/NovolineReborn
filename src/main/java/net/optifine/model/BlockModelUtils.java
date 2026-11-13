package net.optifine.model;

import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.BlockPartRotation;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

public class BlockModelUtils {
   private static final float VERTEX_COORD_ACCURACY = 1.0E-6F;

   public static BakedQuad makeBakedQuad(EnumFacing facing, TextureAtlasSprite sprite, int tintIndex) {
      Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
      Vector3f vector3f2 = new Vector3f(16.0F, 16.0F, 16.0F);
      BlockFaceUV blockFaceUV = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      BlockPartFace blockPartFace = new BlockPartFace(facing, tintIndex, "#" + facing.getName(), blockFaceUV);
      ModelRotation modelRotation = ModelRotation.X0_Y0;
      Object object = null;
      return new FaceBakery().getBakedQuad(vector3f, vector3f2, blockPartFace, sprite, facing, modelRotation, (BlockPartRotation)object, true);
   }

   public static void snapVertexPosition(Vector3f pos) {
      pos.setX(snapVertexCoord(pos.getX()));
      pos.setY(snapVertexCoord(pos.getY()));
      pos.setZ(snapVertexCoord(pos.getZ()));
   }

   public static IBakedModel joinModelsCube(IBakedModel modelBase, IBakedModel modelAdd) {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(modelBase.getGeneralQuads());
      arrayList.addAll(modelAdd.getGeneralQuads());
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList arrayList2 = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         ArrayList arrayList3 = new ArrayList();
         arrayList3.addAll(modelBase.getFaceQuads(enumFacing));
         arrayList3.addAll(modelAdd.getFaceQuads(enumFacing));
         arrayList2.add(arrayList3);
      }

      boolean ambientOcclusion = modelBase.isAmbientOcclusion();
      boolean builtInRenderer = modelBase.isBuiltInRenderer();
      TextureAtlasSprite particleTexture = modelBase.getParticleTexture();
      ItemCameraTransforms itemCameraTransforms = modelBase.getItemCameraTransforms();
      return new SimpleBakedModel(arrayList, arrayList2, ambientOcclusion, builtInRenderer, particleTexture, itemCameraTransforms);
   }

   public static IBakedModel makeModelCube(TextureAtlasSprite sprite, int tintIndex) {
      ArrayList arrayList = new ArrayList();
      EnumFacing[] aenumfacing = EnumFacing.VALUES;
      ArrayList arrayList2 = new ArrayList();

      for (int index = 0; index < aenumfacing.length; index++) {
         EnumFacing enumFacing = aenumfacing[index];
         ArrayList arrayList3 = new ArrayList();
         arrayList3.add(makeBakedQuad(enumFacing, sprite, tintIndex));
         arrayList2.add(arrayList3);
      }

      return new SimpleBakedModel(arrayList, arrayList2, true, true, sprite, ItemCameraTransforms.DEFAULT);
   }

   private static float snapVertexCoord(float x) {
      return x > -1.0E-6F && x < 1.0E-6F ? 0.0F : (x > 0.999999F && x < 1.000001F ? 1.0F : x);
   }

   public static AxisAlignedBB getOffsetBoundingBox(AxisAlignedBB aabb, net.minecraft.block.Block.EnumOffsetType offsetType, BlockPos pos) {
      int xCoordinate = pos.getX();
      int zCoordinate = pos.getZ();
      long l = xCoordinate * 3129871 ^ zCoordinate * 116129781L;
      l = l * l * 42317861L + l * 11L;
      double d = ((float)(l >> 16 & 15L) / 15.0F - 0.5) * 0.5;
      double d2 = ((float)(l >> 24 & 15L) / 15.0F - 0.5) * 0.5;
      double d3 = 0.0;
      if (offsetType == net.minecraft.block.Block.EnumOffsetType.XYZ) {
         d3 = ((float)(l >> 20 & 15L) / 15.0F - 1.0) * 0.2;
      }

      return aabb.offset(d, d3, d2);
   }
}
