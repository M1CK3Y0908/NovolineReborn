package net.optifine.render;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkVisibility {
   public static final EnumFacing[][] enumFacingArrays = makeEnumFacingArrays(false);
   public static final EnumFacing[][] enumFacingOppositeArrays = makeEnumFacingArrays(true);
   private static int counter = 0;
   private static int iMaxStatic = -1;
   public static final int MASK_FACINGS = 63;
   private static int iMaxStaticFinal = 16;
   private static World worldLast = null;
   private static int pcxLast = Integer.MIN_VALUE;
   private static int pczLast = Integer.MIN_VALUE;

   public static int getMaxChunkY(World world, Entity viewEntity, int renderDistanceChunks) {
      int i = MathHelper.floor_double(viewEntity.posX) >> 4;
      int i2 = MathHelper.floor_double(viewEntity.posY) >> 4;
      int minSection = MathHelper.floor_double(viewEntity.posZ) >> 4;
      Chunk chunkFromChunkCoords = world.getChunkFromChunkCoords(i, minSection);
      int index = i - renderDistanceChunks;
      int index2 = i + renderDistanceChunks;
      int i3 = minSection - renderDistanceChunks;
      int i4 = minSection + renderDistanceChunks;
      if (world != worldLast || i != pcxLast || minSection != pczLast) {
         counter = 0;
         iMaxStaticFinal = 16;
         worldLast = world;
         pcxLast = i;
         pczLast = minSection;
      }

      if (counter == 0) {
         iMaxStatic = -1;
      }

      int iMaxStatic2 = iMaxStatic;
      switch (counter) {
         case 0:
            index2 = i;
            i4 = minSection;
            break;
         case 1:
            index = i;
            i4 = minSection;
            break;
         case 2:
            index2 = i;
            i3 = minSection;
            break;
         case 3:
            index = i;
            i3 = minSection;
      }

      for (int index3 = index; index3 < index2; index3++) {
         for (int i5 = i3; i5 < i4; i5++) {
            Chunk chunkFromChunkCoords2 = world.getChunkFromChunkCoords(index3, i5);
            if (!chunkFromChunkCoords2.isEmpty()) {
               ExtendedBlockStorage[] blockStorageArray = chunkFromChunkCoords2.getBlockStorageArray();

               for (int i6 = blockStorageArray.length - 1; i6 > iMaxStatic2; i6--) {
                  ExtendedBlockStorage extendedBlockStorage = blockStorageArray[i6];
                  if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
                     if (i6 > iMaxStatic2) {
                        iMaxStatic2 = i6;
                     }
                     break;
                  }
               }

               try {
                  Map tileEntityMap = chunkFromChunkCoords2.getTileEntityMap();
                  if (!tileEntityMap.isEmpty()) {
                     for (BlockPos blockPos : (Iterable<BlockPos>) tileEntityMap.keySet()) {
                        int i7 = blockPos.getY() >> 4;
                        if (i7 > iMaxStatic2) {
                           iMaxStatic2 = i7;
                        }
                     }
                  }
               } catch (ConcurrentModificationException concurrentModificationException) {
               }

               ClassInheritanceMultiMap[] entityLists = chunkFromChunkCoords2.getEntityLists();

               for (int i8 = entityLists.length - 1; i8 > iMaxStatic2; i8--) {
                  ClassInheritanceMultiMap classInheritanceMultiMap = entityLists[i8];
                  if (!classInheritanceMultiMap.isEmpty() && (chunkFromChunkCoords2 != chunkFromChunkCoords || i8 != i2 || classInheritanceMultiMap.size() != 1)) {
                     if (i8 > iMaxStatic2) {
                        iMaxStatic2 = i8;
                     }
                     break;
                  }
               }
            }
         }
      }

      if (counter < 3) {
         iMaxStatic = iMaxStatic2;
         iMaxStatic2 = iMaxStaticFinal;
      } else {
         iMaxStaticFinal = iMaxStatic2;
         iMaxStatic = -1;
      }

      counter = (counter + 1) % 4;
      return iMaxStatic2 << 4;
   }

   private static EnumFacing[][] makeEnumFacingArrays(boolean opposite) {
      EnumFacing[][] aenumfacing = new EnumFacing[64][];

      for (int innerIndex = 0; innerIndex < 64; innerIndex++) {
         ArrayList arrayList = new ArrayList();

         for (int nestedIndex = 0; nestedIndex < EnumFacing.VALUES.length; nestedIndex++) {
            EnumFacing enumFacing = EnumFacing.VALUES[nestedIndex];
            EnumFacing facing2 = opposite ? enumFacing.getOpposite() : enumFacing;
            int i = 1 << facing2.ordinal();
            if ((innerIndex & i) != 0) {
               arrayList.add(enumFacing);
            }
         }

         EnumFacing[] enumFacings = (EnumFacing[]) arrayList.toArray(new EnumFacing[arrayList.size()]);
         aenumfacing[innerIndex] = enumFacings;
      }

      return aenumfacing;
   }

   public static boolean isFinished() {
      return counter == 0;
   }

   public static void reset() {
      worldLast = null;
   }

   public static EnumFacing[] getFacingsNotOpposite(int setDisabled) {
      int i = ~setDisabled & 63;
      return enumFacingOppositeArrays[i];
   }
}
