package net.optifine;

import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ClearWater {
   public static void updateWaterOpacity(GameSettings settings, World world) {
      if (settings != null) {
         byte b = 3;
         if (settings.ofClearWater) {
            b = 1;
         }

         BlockAir.setLightOpacity(Blocks.water, b);
         BlockAir.setLightOpacity(Blocks.flowing_water, b);
      }

      if (world != null) {
         IChunkProvider chunkProvider = world.getChunkProvider();
         if (chunkProvider != null) {
            Entity renderViewEntity = Config.getMinecraft().getRenderViewEntity();
            if (renderViewEntity != null) {
               int index4 = (int)renderViewEntity.posX / 16;
               int i = (int)renderViewEntity.posZ / 16;
               int index5 = index4 - 512;
               int i2 = index4 + 512;
               int i3 = i - 512;
               int i4 = i + 512;
               int i5 = 0;

               for (int i6 = index5; i6 < i2; i6++) {
                  for (int i7 = i3; i7 < i4; i7++) {
                     if (chunkProvider.chunkExists(i6, i7)) {
                        Chunk chunk2 = chunkProvider.provideChunk(i6, i7);
                        if (chunk2 != null && !(chunk2 instanceof EmptyChunk)) {
                           int i8 = i6 << 4;
                           int i9 = i7 << 4;
                           int i10 = i8 + 16;
                           int i11 = i9 + 16;
                           BlockPosM blockPosM = new BlockPosM(0, 0, 0);
                           BlockPosM blockPosM2 = new BlockPosM(0, 0, 0);

                           for (int i12 = i8; i12 < i10; i12++) {
                              for (int i13 = i9; i13 < i11; i13++) {
                                 blockPosM.setXyz(i12, 0, i13);
                                 BlockPos precipitationHeight = world.getPrecipitationHeight(blockPosM);

                                 for (int i14 = 0; i14 < precipitationHeight.getY(); i14++) {
                                    blockPosM2.setXyz(i12, i14, i13);
                                    if (world.getBlockState(blockPosM2).getBlock().getMaterial() == Material.water) {
                                       world.markBlocksDirtyVertical(i12, i13, blockPosM2.getY(), precipitationHeight.getY());
                                       i5++;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (i5 > 0) {
                  String text = "server";
                  if (Config.isMinecraftThread()) {
                     text = "client";
                  }

                  Config.dbg("ClearWater (" + text + ") relighted " + i5 + " chunks");
               }
            }
         }
      }
   }
}
