package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

public class ItemModelGenerator {
   public static final List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   private List<ItemModelGenerator.Span> getSpans(TextureAtlasSprite sprite) {
      int iconWidth = sprite.getIconWidth();
      int iconHeight = sprite.getIconHeight();
      List<ItemModelGenerator.Span> list = Lists.newArrayList();

      for (int nestedIndex = 0; nestedIndex < sprite.getFrameCount(); nestedIndex++) {
         int[] iArray = sprite.getFrameTextureData(nestedIndex)[0];

         for (int outerIndex = 0; outerIndex < iconHeight; outerIndex++) {
            for (int index = 0; index < iconWidth; index++) {
               boolean flag = !this.isTransparent(iArray, index, outerIndex, iconWidth, iconHeight);
               this.checkTransition(ItemModelGenerator.SpanFacing.UP, list, iArray, index, outerIndex, iconWidth, iconHeight, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.DOWN, list, iArray, index, outerIndex, iconWidth, iconHeight, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.LEFT, list, iArray, index, outerIndex, iconWidth, iconHeight, flag);
               this.checkTransition(ItemModelGenerator.SpanFacing.RIGHT, list, iArray, index, outerIndex, iconWidth, iconHeight, flag);
            }
         }
      }

      return list;
   }

   private void checkTransition(
      ItemModelGenerator.SpanFacing spanFacing, List<ItemModelGenerator.Span> spans, int[] pixels, int x, int y, int width, int height, boolean solid
   ) {
      if (this.isTransparent(pixels, x + spanFacing.getXOffset(), y + spanFacing.getYOffset(), width, height) && solid) {
         this.createOrExpandSpan(spans, spanFacing, x, y);
      }
   }

   private void createOrExpandSpan(List<ItemModelGenerator.Span> spans, ItemModelGenerator.SpanFacing spanFacing, int x, int y) {
      ItemModelGenerator.Span span = null;

      for (ItemModelGenerator.Span span2 : spans) {
         if (span2.getFacing() == spanFacing) {
            int i = spanFacing.isHorizontal() ? y : x;
            if (span2.getAnchor() == i) {
               span = span2;
               break;
            }
         }
      }

      int i2 = spanFacing.isHorizontal() ? y : x;
      int i3 = spanFacing.isHorizontal() ? x : y;
      if (span == null) {
         spans.add(new ItemModelGenerator.Span(spanFacing, i3, i2));
      } else {
         span.expand(i3);
      }
   }

   private List<BlockPart> getSideElements(TextureAtlasSprite sprite, String texture, int tintIndex) {
      float iconWidth = sprite.getIconWidth();
      float iconHeight = sprite.getIconHeight();
      ArrayList arrayList = Lists.newArrayList();

      for (ItemModelGenerator.Span span : this.getSpans(sprite)) {
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 0.0F;
         float min2 = span.getMin();
         float max2 = span.getMax();
         float anchor2 = span.getAnchor();
         ItemModelGenerator.SpanFacing facing2 = span.getFacing();
         switch (facing2) {
            case UP:
               f5 = min2;
               f = min2;
               f3 = f6 = max2 + 1.0F;
               f7 = anchor2;
               f2 = anchor2;
               f8 = anchor2;
               f4 = anchor2;
               f9 = 16.0F / iconWidth;
               f10 = 16.0F / (iconHeight - 1.0F);
               break;
            case DOWN:
               f8 = anchor2;
               f7 = anchor2;
               f5 = min2;
               f = min2;
               f3 = f6 = max2 + 1.0F;
               f2 = anchor2 + 1.0F;
               f4 = anchor2 + 1.0F;
               f9 = 16.0F / iconWidth;
               f10 = 16.0F / (iconHeight - 1.0F);
               break;
            case LEFT:
               f5 = anchor2;
               f = anchor2;
               f6 = anchor2;
               f3 = anchor2;
               f8 = min2;
               f2 = min2;
               f4 = f7 = max2 + 1.0F;
               f9 = 16.0F / (iconWidth - 1.0F);
               f10 = 16.0F / iconHeight;
               break;
            case RIGHT:
               f6 = anchor2;
               f5 = anchor2;
               f = anchor2 + 1.0F;
               f3 = anchor2 + 1.0F;
               f8 = min2;
               f2 = min2;
               f4 = f7 = max2 + 1.0F;
               f9 = 16.0F / (iconWidth - 1.0F);
               f10 = 16.0F / iconHeight;
         }

         float f11 = 16.0F / iconWidth;
         float f12 = 16.0F / iconHeight;
         f *= f11;
         f3 *= f11;
         f2 *= f12;
         f4 *= f12;
         f2 = 16.0F - f2;
         f4 = 16.0F - f4;
         f5 *= f9;
         f6 *= f9;
         f7 *= f10;
         f8 *= f10;
         HashMap hashMap = Maps.newHashMap();
         hashMap.put(facing2.getFacing(), new BlockPartFace((EnumFacing)null, tintIndex, texture, new BlockFaceUV(new float[]{f5, f7, f6, f8}, 0)));
         switch (facing2) {
            case UP:
               arrayList.add(new BlockPart(new Vector3f(f, f2, 7.5F), new Vector3f(f3, f2, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case DOWN:
               arrayList.add(new BlockPart(new Vector3f(f, f4, 7.5F), new Vector3f(f3, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case LEFT:
               arrayList.add(new BlockPart(new Vector3f(f, f2, 7.5F), new Vector3f(f, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
               break;
            case RIGHT:
               arrayList.add(new BlockPart(new Vector3f(f3, f2, 7.5F), new Vector3f(f3, f4, 8.5F), hashMap, (BlockPartRotation)null, true));
         }
      }

      return arrayList;
   }

   public ModelBlock makeItemModel(TextureMap textureMapIn, ModelBlock blockModel) {
      Map<String, String> map = Maps.newHashMap();
      List<BlockPart> list = Lists.newArrayList();

      for (int index = 0; index < LAYERS.size(); index++) {
         String text = LAYERS.get(index);
         if (!blockModel.isTexturePresent(text)) {
            break;
         }

         String text2 = blockModel.resolveTextureName(text);
         map.put(text, text2);
         TextureAtlasSprite textureAtlasSprite = textureMapIn.getAtlasSprite(new ResourceLocation(text2).toString());
         list.addAll(this.getLayerParts(index, text, textureAtlasSprite));
      }

      if (list.isEmpty()) {
         return null;
      }

      map.put("particle", blockModel.isTexturePresent("particle") ? blockModel.resolveTextureName("particle") : map.get("layer0"));
      return new ModelBlock(list, map, false, false, blockModel.getAllTransforms());
   }

   private List<BlockPart> getLayerParts(int tintIndex, String texture, TextureAtlasSprite sprite) {
      Map<EnumFacing, BlockPartFace> map = Maps.newHashMap();
      map.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, tintIndex, texture, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      map.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, tintIndex, texture, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      List<BlockPart> list = Lists.newArrayList();
      list.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), map, (BlockPartRotation)null, true));
      list.addAll(this.getSideElements(sprite, texture, tintIndex));
      return list;
   }

   private boolean isTransparent(int[] pixels, int x, int y, int width, int height) {
      return x >= 0 && y >= 0 && x < width && y < height ? (pixels[y * width + x] >> 24 & 0xFF) == 0 : true;
   }

   static class Span {
      private final ItemModelGenerator.SpanFacing spanFacing;
      private int min;
      private int max;
      private final int anchor;

      public Span(ItemModelGenerator.SpanFacing spanFacingIn, int position, int anchor) {
         this.spanFacing = spanFacingIn;
         this.min = position;
         this.max = position;
         this.anchor = anchor;
      }

      public void expand(int position) {
         if (position < this.min) {
            this.min = position;
         } else if (position > this.max) {
            this.max = position;
         }
      }

      public int getAnchor() {
         return this.anchor;
      }

      public int getMin() {
         return this.min;
      }

      public ItemModelGenerator.SpanFacing getFacing() {
         return this.spanFacing;
      }

      public int getMax() {
         return this.max;
      }
   }

   enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      private final EnumFacing facing;
      private final int xOffset;
      private final int yOffset;

      public int getYOffset() {
         return this.yOffset;
      }

      private boolean isHorizontal() {
         return this == DOWN || this == UP;
      }

      public int getXOffset() {
         return this.xOffset;
      }

      SpanFacing(EnumFacing enumFacing, int i, int i2) {
         this.facing = enumFacing;
         this.xOffset = i;
         this.yOffset = i2;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }
   }
}
