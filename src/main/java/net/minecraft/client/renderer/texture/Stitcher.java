package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher {
   private final Set<Stitcher.Holder> setStitchHolders = Sets.newHashSetWithExpectedSize(256);
   private final List<Stitcher.Slot> stitchSlots = Lists.newArrayListWithCapacity(256);
   private final int mipmapLevelStitcher;
   private int currentHeight;
   private final int maxWidth;
   private int currentWidth;
   private final int maxHeight;
   private final boolean forcePowerOf2;
   private final int maxTileDimension;

   public void doStitch() {
      Stitcher.Holder[] holders = this.setStitchHolders.toArray(new Stitcher.Holder[this.setStitchHolders.size()]);
      Arrays.sort(holders);

      for (Stitcher.Holder holder2 : holders) {
         if (!this.allocateSlot(holder2)) {
            String text = String.format(
               "Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?",
               holder2.getAtlasSprite().getIconName(),
               holder2.getAtlasSprite().getIconWidth(),
               holder2.getAtlasSprite().getIconHeight(),
               this.currentWidth,
               this.currentHeight,
               this.maxWidth,
               this.maxHeight
            );
            throw new StitcherException(holder2, text);
         }
      }

      if (this.forcePowerOf2) {
         this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
      }
   }

   public void addSprite(TextureAtlasSprite sprite) {
      Stitcher.Holder holder2 = new Stitcher.Holder(sprite, this.mipmapLevelStitcher);
      if (this.maxTileDimension > 0) {
         holder2.setNewDimension(this.maxTileDimension);
      }

      this.setStitchHolders.add(holder2);
   }

   public List<TextureAtlasSprite> getStichSlots() {
      ArrayList arrayList = Lists.newArrayList();

      for (Stitcher.Slot slot : this.stitchSlots) {
         slot.getAllStitchSlots(arrayList);
      }

      ArrayList arrayList2 = Lists.newArrayList();

      for (Stitcher.Slot slot2 : (Iterable<Stitcher.Slot>) arrayList) {
         Stitcher.Holder stitchHolder = slot2.getStitchHolder();
         TextureAtlasSprite atlasSprite = stitchHolder.getAtlasSprite();
         atlasSprite.initSprite(this.currentWidth, this.currentHeight, slot2.getOriginX(), slot2.getOriginY(), stitchHolder.isRotated());
         arrayList2.add(atlasSprite);
      }

      return arrayList2;
   }

   private boolean expandAndAllocateSlot(Stitcher.Holder holder) {
      int i = Math.min(holder.getWidth(), holder.getHeight());
      boolean flag = this.currentWidth == 0 && this.currentHeight == 0;
      boolean flag2;
      if (this.forcePowerOf2) {
         int i2 = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
         int i3 = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
         int i4 = MathHelper.roundUpToPowerOfTwo(this.currentWidth + i);
         int i5 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + i);
         boolean flag3 = i4 <= this.maxWidth;
         boolean flag4 = i5 <= this.maxHeight;
         if (!flag3 && !flag4) {
            return false;
         }

         boolean flag5 = i2 != i4;
         boolean flag6 = i3 != i5;
         if (flag5 ^ flag6) {
            flag2 = !flag5;
         } else {
            flag2 = flag3 && i2 <= i3;
         }
      } else {
         boolean flag7 = this.currentWidth + i <= this.maxWidth;
         boolean flag8 = this.currentHeight + i <= this.maxHeight;
         if (!flag7 && !flag8) {
            return false;
         }

         flag2 = flag7 && (flag || this.currentWidth <= this.currentHeight);
      }

      int i6 = Math.max(holder.getWidth(), holder.getHeight());
      if (MathHelper.roundUpToPowerOfTwo((!flag2 ? this.currentHeight : this.currentWidth) + i6) > (!flag2 ? this.maxHeight : this.maxWidth)) {
         return false;
      }

      Stitcher.Slot slot;
      if (flag2) {
         if (holder.getWidth() > holder.getHeight()) {
            holder.rotate();
         }

         if (this.currentHeight == 0) {
            this.currentHeight = holder.getHeight();
         }

         slot = new Stitcher.Slot(this.currentWidth, 0, holder.getWidth(), this.currentHeight);
         this.currentWidth = this.currentWidth + holder.getWidth();
      } else {
         slot = new Stitcher.Slot(0, this.currentHeight, this.currentWidth, holder.getHeight());
         this.currentHeight = this.currentHeight + holder.getHeight();
      }

      slot.addSlot(holder);
      this.stitchSlots.add(slot);
      return true;
   }

   public int getCurrentWidth() {
      return this.currentWidth;
   }

   private static int getMipmapDimension(int dimension, int mipmapLevel) {
      return (dimension >> mipmapLevel) + ((dimension & (1 << mipmapLevel) - 1) == 0 ? 0 : 1) << mipmapLevel;
   }

   public Stitcher(int i, int i2, int i3) {
      this.mipmapLevelStitcher = i3;
      this.maxWidth = i;
      this.maxHeight = i2;
      this.forcePowerOf2 = true;
      this.maxTileDimension = 0;
   }

   public int getCurrentHeight() {
      return this.currentHeight;
   }

   private static StitcherException createStitcherException(StitcherException stitcherException) {
      return stitcherException;
   }

   private boolean allocateSlot(Stitcher.Holder holder) {
      for (int index = 0; index < this.stitchSlots.size(); index++) {
         if (this.stitchSlots.get(index).addSlot(holder)) {
            return true;
         }

         holder.rotate();
         if (this.stitchSlots.get(index).addSlot(holder)) {
            return true;
         }

         holder.rotate();
      }

      return this.expandAndAllocateSlot(holder);
   }

   public static class Holder implements Comparable<Stitcher.Holder> {
      private float scaleFactor = 1.0F;
      private final TextureAtlasSprite theTexture;
      private final int width;
      private final int height;
      private final int mipmapLevelHolder;
      private boolean rotated;

      public TextureAtlasSprite getAtlasSprite() {
         return this.theTexture;
      }

      public int getWidth() {
         return this.rotated
            ? Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder)
            : Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder);
      }

      @Override
      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + "}";
      }

      public Holder(TextureAtlasSprite sprite, int mipmapLevel) {
         this.theTexture = sprite;
         this.width = sprite.getIconWidth();
         this.height = sprite.getIconHeight();
         this.mipmapLevelHolder = mipmapLevel;
         this.rotated = Stitcher.getMipmapDimension(this.height, mipmapLevel) > Stitcher.getMipmapDimension(this.width, mipmapLevel);
      }

      public int getHeight() {
         return this.rotated
            ? Stitcher.getMipmapDimension((int)(this.width * this.scaleFactor), this.mipmapLevelHolder)
            : Stitcher.getMipmapDimension((int)(this.height * this.scaleFactor), this.mipmapLevelHolder);
      }

      public int compareTo(Stitcher.Holder other) {
         int i;
         if (this.getHeight() == other.getHeight()) {
            if (this.getWidth() == other.getWidth()) {
               if (this.theTexture.getIconName() == null) {
                  return other.theTexture.getIconName() == null ? 0 : -1;
               }

               return this.theTexture.getIconName().compareTo(other.theTexture.getIconName());
            }

            i = this.getWidth() < other.getWidth() ? 1 : -1;
         } else {
            i = this.getHeight() < other.getHeight() ? 1 : -1;
         }

         return i;
      }

      public void rotate() {
         this.rotated = !this.rotated;
      }

      public boolean isRotated() {
         return this.rotated;
      }

      public void setNewDimension(int maxDimension) {
         if (this.width > maxDimension && this.height > maxDimension) {
            this.scaleFactor = (float)maxDimension / Math.min(this.width, this.height);
         }
      }
   }

   public static class Slot {
      private Stitcher.Holder holder;
      private List<Stitcher.Slot> subSlots;
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;

      @Override
      public String toString() {
         return "Slot{originX="
            + this.originX
            + ", originY="
            + this.originY
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ", texture="
            + this.holder
            + ", subSlots="
            + this.subSlots
            + "}";
      }

      public void getAllStitchSlots(List<Stitcher.Slot> slots) {
         if (this.holder != null) {
            slots.add(this);
         } else if (this.subSlots != null) {
            Iterator iterator2 = this.subSlots.iterator();

            while (iterator2.hasNext()) {
               ((Stitcher.Slot)iterator2.next()).getAllStitchSlots(slots);
            }
         }
      }

      public boolean addSlot(Stitcher.Holder holderIn) {
         if (this.holder != null) {
            return false;
         }

         int width2 = holderIn.getWidth();
         int height2 = holderIn.getHeight();
         if (width2 <= this.width && height2 <= this.height) {
            if (width2 == this.width && height2 == this.height) {
               this.holder = holderIn;
               return true;
            }

            if (this.subSlots == null) {
               this.subSlots = Lists.newArrayListWithCapacity(1);
               this.subSlots.add(new Stitcher.Slot(this.originX, this.originY, width2, height2));
               int i = this.width - width2;
               int i2 = this.height - height2;
               if (i2 > 0 && i > 0) {
                  int i3 = Math.max(this.height, i);
                  int i4 = Math.max(this.width, i2);
                  if (i3 >= i4) {
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height2, width2, i2));
                     this.subSlots.add(new Stitcher.Slot(this.originX + width2, this.originY, i, this.height));
                  } else {
                     this.subSlots.add(new Stitcher.Slot(this.originX + width2, this.originY, i, height2));
                     this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height2, this.width, i2));
                  }
               } else if (i == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX, this.originY + height2, width2, i2));
               } else if (i2 == 0) {
                  this.subSlots.add(new Stitcher.Slot(this.originX + width2, this.originY, i, height2));
               }
            }

            for (Stitcher.Slot slot : this.subSlots) {
               if (slot.addSlot(holderIn)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      }

      public Slot(int originXIn, int originYIn, int widthIn, int heightIn) {
         this.originX = originXIn;
         this.originY = originYIn;
         this.width = widthIn;
         this.height = heightIn;
      }

      public Stitcher.Holder getStitchHolder() {
         return this.holder;
      }

      public int getOriginX() {
         return this.originX;
      }

      public int getOriginY() {
         return this.originY;
      }
   }
}
