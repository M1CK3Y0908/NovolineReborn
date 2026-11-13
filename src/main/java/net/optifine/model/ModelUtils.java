package net.optifine.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

public class ModelUtils {
   public static List duplicateQuadList(List lists) {
      ArrayList arrayList = new ArrayList();
      Iterator iterator2 = lists.iterator();

      while (iterator2.hasNext()) {
         BakedQuad bakedQuad = duplicateQuad((BakedQuad)iterator2.next());
         arrayList.add(bakedQuad);
      }

      return arrayList;
   }

   public static BakedQuad duplicateQuad(BakedQuad quad) {
      return new BakedQuad((int[])quad.getVertexData().clone(), quad.getTintIndex(), quad.getFace(), quad.getSprite());
   }

   public static IBakedModel duplicateModel(IBakedModel model) {
      List list = duplicateQuadList(model.getGeneralQuads());
      EnumFacing[] enumFacings = EnumFacing.VALUES;
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < enumFacings.length; i++) {
         EnumFacing enumFacing = enumFacings[i];
         List list2 = duplicateQuadList(model.getFaceQuads(enumFacing));
         arrayList.add(list2);
      }

      return new SimpleBakedModel(list, arrayList, model.isAmbientOcclusion(), model.isGui3d(), model.getParticleTexture(), model.getItemCameraTransforms());
   }
}
