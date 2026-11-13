package net.minecraft.world.pathfinder;

import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public abstract class NodeProcessor {
   protected int entitySizeZ;
   protected IBlockAccess blockaccess;
   protected IntHashMap<PathPoint> pointMap = new IntHashMap<>();
   protected int entitySizeX;
   protected int entitySizeY;

   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      this.blockaccess = iblockaccessIn;
      this.pointMap.clearMap();
      this.entitySizeX = MathHelper.floor_float(entityIn.width + 1.0F);
      this.entitySizeY = MathHelper.floor_float(entityIn.height + 1.0F);
      this.entitySizeZ = MathHelper.floor_float(entityIn.width + 1.0F);
   }

   protected PathPoint openPoint(int x, int y, int z) {
      int i = PathPoint.makeHash(x, y, z);
      PathPoint pathPoint = this.pointMap.lookup(i);
      if (pathPoint == null) {
         pathPoint = new PathPoint(x, y, z);
         this.pointMap.addKey(i, pathPoint);
      }

      return pathPoint;
   }

   public abstract int findPathOptions(PathPoint[] pathPoints, Entity entity2, PathPoint pathPoint2, PathPoint pathPoint3, float f);

   public abstract PathPoint getPathPointTo(Entity entity2);

   public void postProcess() {
   }

   public abstract PathPoint getPathPointToCoords(Entity entity2, double d, double d2, double d3);
}
