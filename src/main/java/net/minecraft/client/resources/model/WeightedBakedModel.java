package net.minecraft.client.resources.model;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;

public class WeightedBakedModel implements IBakedModel {
   private final List<WeightedBakedModel.MyWeighedRandomItem> models;
   private final int totalWeight;
   private final IBakedModel baseModel;

   @Override
   public TextureAtlasSprite getParticleTexture() {
      return this.baseModel.getParticleTexture();
   }

   @Override
   public List<BakedQuad> getGeneralQuads() {
      return this.baseModel.getGeneralQuads();
   }

   @Override
   public boolean isAmbientOcclusion() {
      return this.baseModel.isAmbientOcclusion();
   }

   @Override
   public boolean isBuiltInRenderer() {
      return this.baseModel.isBuiltInRenderer();
   }

   public WeightedBakedModel(List<WeightedBakedModel.MyWeighedRandomItem> list) {
      this.models = list;
      this.totalWeight = WeightedRandom.getTotalWeight(list);
      this.baseModel = list.get(0).model;
   }

   @Override
   public List<BakedQuad> getFaceQuads(EnumFacing facing) {
      return this.baseModel.getFaceQuads(facing);
   }

   @Override
   public ItemCameraTransforms getItemCameraTransforms() {
      return this.baseModel.getItemCameraTransforms();
   }

   @Override
   public boolean isGui3d() {
      return this.baseModel.isGui3d();
   }

   public IBakedModel getAlternativeModel(long l) {
      return WeightedRandom.getRandomItem(this.models, Math.abs((int)l >> 16) % this.totalWeight).model;
   }

   public static class Builder {
      private List<WeightedBakedModel.MyWeighedRandomItem> listItems = Lists.newArrayList();

      public IBakedModel first() {
         return this.listItems.get(0).model;
      }

      public WeightedBakedModel.Builder add(IBakedModel iBakedModel, int i) {
         this.listItems.add(new WeightedBakedModel.MyWeighedRandomItem(iBakedModel, i));
         return this;
      }

      public WeightedBakedModel build() {
         Collections.sort(this.listItems);
         return new WeightedBakedModel(this.listItems);
      }
   }

   static class MyWeighedRandomItem extends net.minecraft.util.WeightedRandom.Item implements Comparable<WeightedBakedModel.MyWeighedRandomItem> {
      protected final IBakedModel model;

      protected int getCountQuads() {
         int size2 = this.model.getGeneralQuads().size();

         for (EnumFacing enumFacing : EnumFacing.values()) {
            size2 += this.model.getFaceQuads(enumFacing).size();
         }

         return size2;
      }

      @Override
      public String toString() {
         return "MyWeighedRandomItem{weight=" + this.itemWeight + ", model=" + this.model + "}";
      }

      public MyWeighedRandomItem(IBakedModel iBakedModel, int i) {
         super(i);
         this.model = iBakedModel;
      }

      public int compareTo(WeightedBakedModel.MyWeighedRandomItem arg1) {
         return ComparisonChain.start().compare(arg1.itemWeight, this.itemWeight).compare(this.getCountQuads(), arg1.getCountQuads()).result();
      }
   }
}
