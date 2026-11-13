package net.optifine;

import java.util.Comparator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

public class ChunkPosComparator implements Comparator<ChunkCoordIntPair> {
   private int chunkPosX;
   private int chunkPosZ;
   private double yawRad;
   private double pitchNorm;

   public ChunkPosComparator(int chunkPosX, int chunkPosZ, double yawRad, double pitchRad) {
      this.chunkPosX = chunkPosX;
      this.chunkPosZ = chunkPosZ;
      this.yawRad = yawRad;
      this.pitchNorm = 1.0 - MathHelper.clamp_double(Math.abs(pitchRad) / (Math.PI / 2), 0.0, 1.0);
   }

   private int getDistSq(ChunkCoordIntPair cp) {
      int i = cp.chunkXPos - this.chunkPosX;
      int i2 = cp.chunkZPos - this.chunkPosZ;
      int i3 = i * i + i2 * i2;
      double d = MathHelper.atan2(i2, i);
      double d2 = Math.abs(d - this.yawRad);
      if (d2 > Math.PI) {
         d2 = (Math.PI * 2) - d2;
      }

      return (int)(i3 * 1000.0 * this.pitchNorm * d2 * d2);
   }

   public int compare(ChunkCoordIntPair cp1, ChunkCoordIntPair cp2) {
      int distSq = this.getDistSq(cp1);
      int distSq2 = this.getDistSq(cp2);
      return distSq - distSq2;
   }
}
