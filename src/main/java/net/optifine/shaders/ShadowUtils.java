package net.optifine.shaders;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class ShadowUtils {
   public static Iterator<RenderChunk> makeShadowChunkIterator(
      WorldClient world, double partialTicks, Entity viewEntity, int renderDistanceChunks, ViewFrustum viewFrustum
   ) {
      float shadowRenderDistance = Shaders.getShadowRenderDistance();
      if (shadowRenderDistance > 0.0F && shadowRenderDistance < (renderDistanceChunks - 1) * 16) {
         int i = MathHelper.ceiling_float_int(shadowRenderDistance / 16.0F) + 1;
         float celestialAngleRadians = world.getCelestialAngleRadians((float)partialTicks);
         float f = Shaders.sunPathRotation * MathHelper.deg2Rad;
         float f2 = celestialAngleRadians > MathHelper.PId2 && celestialAngleRadians < 3.0F * MathHelper.PId2 ? celestialAngleRadians + MathHelper.PI : celestialAngleRadians;
         float f3 = -MathHelper.sin(f2);
         float f4 = MathHelper.cos(f2) * MathHelper.cos(f);
         float f5 = -MathHelper.cos(f2) * MathHelper.sin(f);
         BlockPos blockPos = new BlockPos(
            MathHelper.floor_double(viewEntity.posX) >> 4, MathHelper.floor_double(viewEntity.posY) >> 4, MathHelper.floor_double(viewEntity.posZ) >> 4
         );
         BlockPos blockPos2 = blockPos.add(-f3 * i, -f4 * i, -f5 * i);
         BlockPos blockPos3 = blockPos.add(f3 * renderDistanceChunks, f4 * renderDistanceChunks, f5 * renderDistanceChunks);
         return new IteratorRenderChunks(viewFrustum, blockPos2, blockPos3, i, i);
      } else {
         List list = Arrays.asList(viewFrustum.renderChunks);
         return list.iterator();
      }
   }
}
