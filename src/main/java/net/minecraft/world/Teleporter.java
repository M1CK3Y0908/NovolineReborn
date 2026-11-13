package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;

public class Teleporter {
   private final LongHashMap<Teleporter.PortalPosition> destinationCoordinateCache = new LongHashMap<>();
   private final List<Long> destinationCoordinateKeys = Lists.newArrayList();
   private final WorldServer worldServerInstance;
   private final Random random;

   public void placeInPortal(Entity entityIn, float rotationYaw) {
      if (this.worldServerInstance.provider.getDimensionId() != 1) {
         if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
            this.makePortal(entityIn);
            this.placeInExistingPortal(entityIn, rotationYaw);
         }
      } else {
         int i = MathHelper.floor_double(entityIn.posX);
         int i2 = MathHelper.floor_double(entityIn.posY) - 1;
         int i3 = MathHelper.floor_double(entityIn.posZ);

         for (int i4 = -2; i4 <= 2; i4++) {
            for (int i5 = -2; i5 <= 2; i5++) {
               for (int i6 = -1; i6 < 3; i6++) {
                  int i7 = i + i5 * 1 + i4 * 0;
                  int i8 = i2 + i6;
                  int i9 = i3 + i5 * 0 - i4 * 1;
                  boolean flag = i6 < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(i7, i8, i9), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }

         entityIn.setLocationAndAngles(i, i2, i3, entityIn.rotationYaw, 0.0F);
         entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0;
      }
   }

   public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
      double d = -1.0;
      int i = MathHelper.floor_double(entityIn.posX);
      int i2 = MathHelper.floor_double(entityIn.posZ);
      boolean flag = true;
      BlockPos blockPos = BlockPos.ORIGIN;
      long l = ChunkCoordIntPair.chunkXZ2Int(i, i2);
      if (this.destinationCoordinateCache.containsItem(l)) {
         Teleporter.PortalPosition valueByKey = this.destinationCoordinateCache.getValueByKey(l);
         d = 0.0;
         blockPos = valueByKey;
         valueByKey.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
         flag = false;
      } else {
         BlockPos blockPos2 = new BlockPos(entityIn);

         for (int i3 = -128; i3 <= 128; i3++) {
            for (int i4 = -128; i4 <= 128; i4++) {
               BlockPos blockPos3 = blockPos2.add(i3, this.worldServerInstance.getActualHeight() - 1 - blockPos2.getY(), i4);

               while (blockPos3.getY() >= 0) {
                  BlockPos blockPos4 = blockPos3.down();
                  if (this.worldServerInstance.getBlockState(blockPos3).getBlock() == Blocks.portal) {
                     while (this.worldServerInstance.getBlockState(blockPos4 = blockPos3.down()).getBlock() == Blocks.portal) {
                        blockPos3 = blockPos4;
                     }

                     double d2 = blockPos3.distanceSq(blockPos2);
                     if (d < 0.0 || d2 < d) {
                        d = d2;
                        blockPos = blockPos3;
                     }
                  }

                  blockPos3 = blockPos4;
               }
            }
         }
      }

      if (d >= 0.0) {
         if (flag) {
            this.destinationCoordinateCache.add(l, new Teleporter.PortalPosition(blockPos, this.worldServerInstance.getTotalWorldTime()));
            this.destinationCoordinateKeys.add(l);
         }

         double d3 = blockPos.getX() + 0.5;
         blockPos.getY();
         double d4 = blockPos.getZ() + 0.5;
         net.minecraft.block.state.pattern.BlockPattern.PatternHelper portalPattern = Blocks.portal.getPortalPattern(this.worldServerInstance, blockPos);
         boolean flag2 = portalPattern.getFinger().rotateY().getAxisDirection() == net.minecraft.util.EnumFacing.AxisDirection.NEGATIVE;
         double d5 = portalPattern.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X ? portalPattern.getPos().getZ() : portalPattern.getPos().getX();
         double d6 = portalPattern.getPos().getY() + 1 - entityIn.getLastPortalVec().yCoord * portalPattern.getThumbLength();
         if (flag2) {
            d5++;
         }

         if (portalPattern.getFinger().getAxis() == net.minecraft.util.EnumFacing.Axis.X) {
            d4 = d5 + (1.0 - entityIn.getLastPortalVec().xCoord) * portalPattern.getPalmLength() * portalPattern.getFinger().rotateY().getAxisDirection().getOffset();
         } else {
            d3 = d5 + (1.0 - entityIn.getLastPortalVec().xCoord) * portalPattern.getPalmLength() * portalPattern.getFinger().rotateY().getAxisDirection().getOffset();
         }

         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         if (portalPattern.getFinger().getOpposite() == entityIn.getTeleportDirection()) {
            f = 1.0F;
            f2 = 1.0F;
         } else if (portalPattern.getFinger().getOpposite() == entityIn.getTeleportDirection().getOpposite()) {
            f = -1.0F;
            f2 = -1.0F;
         } else if (portalPattern.getFinger().getOpposite() == entityIn.getTeleportDirection().rotateY()) {
            f3 = 1.0F;
            f4 = -1.0F;
         } else {
            f3 = -1.0F;
            f4 = 1.0F;
         }

         double motionX2 = entityIn.motionX;
         double motionZ2 = entityIn.motionZ;
         entityIn.motionX = motionX2 * f + motionZ2 * f4;
         entityIn.motionZ = motionX2 * f3 + motionZ2 * f2;
         entityIn.rotationYaw = rotationYaw
            - entityIn.getTeleportDirection().getOpposite().getHorizontalIndex() * 90
            + portalPattern.getFinger().getHorizontalIndex() * 90;
         entityIn.setLocationAndAngles(d3, d6, d4, entityIn.rotationYaw, entityIn.rotationPitch);
         return true;
      } else {
         return false;
      }
   }

   public boolean makePortal(Entity entityIn) {
      double d = -1.0;
      int i = MathHelper.floor_double(entityIn.posX);
      int i2 = MathHelper.floor_double(entityIn.posY);
      int i3 = MathHelper.floor_double(entityIn.posZ);
      int i4 = i;
      int i5 = i2;
      int i6 = i3;
      int i7 = 0;
      int i8 = this.random.nextInt(4);
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i9 = i - 16; i9 <= i + 16; i9++) {
         double d2 = i9 + 0.5 - entityIn.posX;

         for (int i10 = i3 - 16; i10 <= i3 + 16; i10++) {
            double d3 = i10 + 0.5 - entityIn.posZ;

            label294:
            for (int i11 = this.worldServerInstance.getActualHeight() - 1; i11 >= 0; i11--) {
               if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i9, i11, i10))) {
                  while (i11 > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i9, i11 - 1, i10))) {
                     i11--;
                  }

                  for (int i12 = i8; i12 < i8 + 4; i12++) {
                     int i13 = i12 % 2;
                     int i14 = 1 - i13;
                     if (i12 % 4 >= 2) {
                        i13 = -i13;
                        i14 = -i14;
                     }

                     for (int i15 = 0; i15 < 3; i15++) {
                        for (int i16 = 0; i16 < 4; i16++) {
                           for (int i17 = -1; i17 < 4; i17++) {
                              int i18 = i9 + (i16 - 1) * i13 + i15 * i14;
                              int i19 = i11 + i17;
                              int i20 = i10 + (i16 - 1) * i14 - i15 * i13;
                              mutableBlockPos.set(i18, i19, i20);
                              if (i17 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i17 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label294;
                              }
                           }
                        }
                     }

                     double d4 = i11 + 0.5 - entityIn.posY;
                     double d5 = d2 * d2 + d4 * d4 + d3 * d3;
                     if (d < 0.0 || d5 < d) {
                        d = d5;
                        i4 = i9;
                        i5 = i11;
                        i6 = i10;
                        i7 = i12 % 4;
                     }
                  }
               }
            }
         }
      }

      if (d < 0.0) {
         for (int i21 = i - 16; i21 <= i + 16; i21++) {
            double d6 = i21 + 0.5 - entityIn.posX;

            for (int i22 = i3 - 16; i22 <= i3 + 16; i22++) {
               double d7 = i22 + 0.5 - entityIn.posZ;

               label231:
               for (int i23 = this.worldServerInstance.getActualHeight() - 1; i23 >= 0; i23--) {
                  if (this.worldServerInstance.isAirBlock(mutableBlockPos.set(i21, i23, i22))) {
                     while (i23 > 0 && this.worldServerInstance.isAirBlock(mutableBlockPos.set(i21, i23 - 1, i22))) {
                        i23--;
                     }

                     for (int i24 = i8; i24 < i8 + 2; i24++) {
                        int i25 = i24 % 2;
                        int i26 = 1 - i25;

                        for (int i27 = 0; i27 < 4; i27++) {
                           for (int i28 = -1; i28 < 4; i28++) {
                              int i29 = i21 + (i27 - 1) * i25;
                              int i30 = i23 + i28;
                              int i31 = i22 + (i27 - 1) * i26;
                              mutableBlockPos.set(i29, i30, i31);
                              if (i28 < 0 && !this.worldServerInstance.getBlockState(mutableBlockPos).getBlock().getMaterial().isSolid()
                                 || i28 >= 0 && !this.worldServerInstance.isAirBlock(mutableBlockPos)) {
                                 continue label231;
                              }
                           }
                        }

                        double d8 = i23 + 0.5 - entityIn.posY;
                        double d9 = d6 * d6 + d8 * d8 + d7 * d7;
                        if (d < 0.0 || d9 < d) {
                           d = d9;
                           i4 = i21;
                           i5 = i23;
                           i6 = i22;
                           i7 = i24 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int i32 = i4;
      int i33 = i5;
      int i34 = i6;
      int i35 = i7 % 2;
      int i36 = 1 - i35;
      if (i7 % 4 >= 2) {
         i35 = -i35;
         i36 = -i36;
      }

      if (d < 0.0) {
         i5 = MathHelper.clamp_int(i5, 70, this.worldServerInstance.getActualHeight() - 10);
         i33 = i5;

         for (int i37 = -1; i37 <= 1; i37++) {
            for (int i38 = 1; i38 < 3; i38++) {
               for (int i39 = -1; i39 < 3; i39++) {
                  int i40 = i32 + (i38 - 1) * i35 + i37 * i36;
                  int i41 = i33 + i39;
                  int i42 = i34 + (i38 - 1) * i36 - i37 * i35;
                  boolean flag = i39 < 0;
                  this.worldServerInstance
                     .setBlockState(new BlockPos(i40, i41, i42), flag ? Blocks.obsidian.getDefaultState() : Blocks.air.getDefaultState());
               }
            }
         }
      }

      IBlockState iBlockState = Blocks.portal.getDefaultState().withProperty(BlockPortal.AXIS, i35 != 0 ? net.minecraft.util.EnumFacing.Axis.X : net.minecraft.util.EnumFacing.Axis.Z);

      for (int i43 = 0; i43 < 4; i43++) {
         for (int i44 = 0; i44 < 4; i44++) {
            for (int i45 = -1; i45 < 4; i45++) {
               int i46 = i32 + (i44 - 1) * i35;
               int i47 = i33 + i45;
               int i48 = i34 + (i44 - 1) * i36;
               boolean flag2 = i44 == 0 || i44 == 3 || i45 == -1 || i45 == 3;
               this.worldServerInstance.setBlockState(new BlockPos(i46, i47, i48), flag2 ? Blocks.obsidian.getDefaultState() : iBlockState, 2);
            }
         }

         for (int i49 = 0; i49 < 4; i49++) {
            for (int i50 = -1; i50 < 4; i50++) {
               int i51 = i32 + (i49 - 1) * i35;
               int i52 = i33 + i50;
               int i53 = i34 + (i49 - 1) * i36;
               BlockPos blockPos = new BlockPos(i51, i52, i53);
               this.worldServerInstance.notifyNeighborsOfStateChange(blockPos, this.worldServerInstance.getBlockState(blockPos).getBlock());
            }
         }
      }

      return true;
   }

   public void removeStalePortalLocations(long worldTime) {
      if (worldTime % 100L == 0L) {
         Iterator<Long> iterator = this.destinationCoordinateKeys.iterator();
         long l = worldTime - 300L;

         while (iterator.hasNext()) {
            Long olong = iterator.next();
            Teleporter.PortalPosition teleporter$portalposition = this.destinationCoordinateCache.getValueByKey(olong);
            if (teleporter$portalposition == null || teleporter$portalposition.lastUpdateTime < l) {
               iterator.remove();
               this.destinationCoordinateCache.remove(olong);
            }
         }
      }
   }

   public Teleporter(WorldServer worldIn) {
      this.worldServerInstance = worldIn;
      this.random = new Random(worldIn.getSeed());
   }

   public class PortalPosition extends BlockPos {
      public long lastUpdateTime;

      public PortalPosition(BlockPos pos, long lastUpdate) {
         super(pos.getX(), pos.getY(), pos.getZ());
         this.lastUpdateTime = lastUpdate;
      }
   }
}
