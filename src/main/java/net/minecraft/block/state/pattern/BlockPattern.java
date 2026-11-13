package net.minecraft.block.state.pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
   private final Predicate<BlockWorldState>[][][] blockMatches;
   private final int fingerLength;
   private final int thumbLength;
   private final int palmLength;

   public int getPalmLength() {
      return this.palmLength;
   }

   public BlockPattern.PatternHelper match(World worldIn, BlockPos pos) {
      LoadingCache loadingCache = createLoadingCache(worldIn, false);
      int i = Math.max(Math.max(this.palmLength, this.thumbLength), this.fingerLength);

      for (BlockPos blockPos : BlockPos.getAllInBox(pos, pos.add(i - 1, i - 1, i - 1))) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            for (EnumFacing enumFacing2 : EnumFacing.values()) {
               if (enumFacing2 != enumFacing && enumFacing2 != enumFacing.getOpposite()) {
                  BlockPattern.PatternHelper patternHelper = this.checkPatternAt(blockPos, enumFacing, enumFacing2, loadingCache);
                  if (patternHelper != null) {
                     return patternHelper;
                  }
               }
            }
         }
      }

      return null;
   }

   public int getThumbLength() {
      return this.thumbLength;
   }

   protected static BlockPos translateOffset(BlockPos pos, EnumFacing finger, EnumFacing thumb, int palmOffset, int thumbOffset, int fingerOffset) {
      if (finger != thumb && finger != thumb.getOpposite()) {
         Vec3i vec3i = new Vec3i(finger.getFrontOffsetX(), finger.getFrontOffsetY(), finger.getFrontOffsetZ());
         Vec3i vec3i2 = new Vec3i(thumb.getFrontOffsetX(), thumb.getFrontOffsetY(), thumb.getFrontOffsetZ());
         Vec3i vec3i4 = vec3i.crossProduct(vec3i2);
         return pos.add(
            vec3i2.getX() * -thumbOffset + vec3i4.getX() * palmOffset + vec3i.getX() * fingerOffset,
            vec3i2.getY() * -thumbOffset + vec3i4.getY() * palmOffset + vec3i.getY() * fingerOffset,
            vec3i2.getZ() * -thumbOffset + vec3i4.getZ() * palmOffset + vec3i.getZ() * fingerOffset
         );
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   private BlockPattern.PatternHelper checkPatternAt(BlockPos pos, EnumFacing finger, EnumFacing thumb, LoadingCache<BlockPos, BlockWorldState> lcache) {
      for (int index = 0; index < this.palmLength; index++) {
         for (int innerIndex = 0; innerIndex < this.thumbLength; innerIndex++) {
            for (int nestedIndex = 0; nestedIndex < this.fingerLength; nestedIndex++) {
               if (!this.blockMatches[nestedIndex][innerIndex][index]
                  .apply((BlockWorldState)lcache.getUnchecked(translateOffset(pos, finger, thumb, index, innerIndex, nestedIndex)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(pos, finger, thumb, lcache, this.palmLength, this.thumbLength, this.fingerLength);
   }

   public BlockPattern(Predicate<BlockWorldState>[][][] predicatesIn) {
      this.blockMatches = predicatesIn;
      this.fingerLength = predicatesIn.length;
      if (this.fingerLength > 0) {
         this.thumbLength = predicatesIn[0].length;
         if (this.thumbLength > 0) {
            this.palmLength = predicatesIn[0][0].length;
         } else {
            this.palmLength = 0;
         }
      } else {
         this.thumbLength = 0;
         this.palmLength = 0;
      }
   }

   public static LoadingCache<BlockPos, BlockWorldState> createLoadingCache(World worldIn, boolean forceState) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(worldIn, forceState));
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, BlockWorldState> {
      private final World world;
      private final boolean forceState;

      public CacheLoader(World worldIn, boolean forceStateIn) {
         this.world = worldIn;
         this.forceState = forceStateIn;
      }

      public BlockWorldState load(BlockPos pos) throws Exception {
         return new BlockWorldState(this.world, pos, this.forceState);
      }
   }

   public static class PatternHelper {
      private final BlockPos pos;
      private final EnumFacing finger;
      private final EnumFacing thumb;
      private final LoadingCache<BlockPos, BlockWorldState> lcache;
      private final int palmLength;
      private final int thumbLength;
      private final int fingerLength;

      @Override
      public String toString() {
         return Objects.toStringHelper(this).add("up", this.thumb).add("forwards", this.finger).add("frontTopLeft", this.pos).toString();
      }

      public BlockPos getPos() {
         return this.pos;
      }

      public EnumFacing getFinger() {
         return this.finger;
      }

      public int getPalmLength() {
         return this.palmLength;
      }

      public int getThumbLength() {
         return this.thumbLength;
      }

      public EnumFacing getThumb() {
         return this.thumb;
      }

      public PatternHelper(
         BlockPos posIn,
         EnumFacing fingerIn,
         EnumFacing thumbIn,
         LoadingCache<BlockPos, BlockWorldState> lcacheIn,
         int palmLengthIn,
         int thumbLengthIn,
         int fingerLengthIn
      ) {
         this.pos = posIn;
         this.finger = fingerIn;
         this.thumb = thumbIn;
         this.lcache = lcacheIn;
         this.palmLength = palmLengthIn;
         this.thumbLength = thumbLengthIn;
         this.fingerLength = fingerLengthIn;
      }

      public BlockWorldState translateOffset(int palmOffset, int thumbOffset, int fingerOffset) {
         return (BlockWorldState)this.lcache
            .getUnchecked(BlockPattern.translateOffset(this.pos, this.getFinger(), this.getThumb(), palmOffset, thumbOffset, fingerOffset));
      }
   }
}
