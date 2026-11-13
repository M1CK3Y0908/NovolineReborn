package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double centerX = 0.0;
   private long startTime;
   private double centerZ = 0.0;
   private double startDiameter = 6.0E7;
   private long endTime;
   private double endDiameter = this.startDiameter;
   private int worldSize = 29999984;
   private double damageAmount = 0.2;
   private double damageBuffer = 5.0;
   private int warningTime = 15;
   private int warningDistance = 5;

   public double getDiameter() {
      if (this.getStatus() != EnumBorderStatus.STATIONARY) {
         double d = (float)(System.currentTimeMillis() - this.startTime) / (float)(this.endTime - this.startTime);
         if (d < 1.0) {
            return this.startDiameter + (this.endDiameter - this.startDiameter) * d;
         }

         this.setTransition(this.endDiameter);
      }

      return this.startDiameter;
   }

   public void addListener(IBorderListener listener) {
      this.listeners.add(listener);
   }

   public void setCenter(double x, double z) {
      this.centerX = x;
      this.centerZ = z;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onCenterChanged(this, x, z);
      }
   }

   public void setTransition(double oldSize, double newSize, long time) {
      this.startDiameter = oldSize;
      this.endDiameter = newSize;
      this.startTime = System.currentTimeMillis();
      this.endTime = this.startTime + time;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onTransitionStarted(this, oldSize, newSize, time);
      }
   }

   public void setTransition(double newSize) {
      this.startDiameter = newSize;
      this.endDiameter = newSize;
      this.endTime = System.currentTimeMillis();
      this.startTime = this.endTime;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onSizeChanged(this, newSize);
      }
   }

   public void setWarningTime(int warningTime) {
      this.warningTime = warningTime;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onWarningTimeChanged(this, warningTime);
      }
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public boolean contains(BlockPos pos) {
      return pos.getX() + 1 > this.minX() && pos.getX() < this.maxX() && pos.getZ() + 1 > this.minZ() && pos.getZ() < this.maxZ();
   }

   public double getClosestDistance(double x, double z) {
      double d = z - this.minZ();
      double d2 = this.maxZ() - z;
      double d3 = x - this.minX();
      double d4 = this.maxX() - x;
      double d5 = Math.min(d3, d4);
      d5 = Math.min(d5, d);
      return Math.min(d5, d2);
   }

   public double minX() {
      double d = this.getCenterX() - this.getDiameter() / 2.0;
      if (d < -this.worldSize) {
         d = -this.worldSize;
      }

      return d;
   }

   public double maxX() {
      double d = this.getCenterX() + this.getDiameter() / 2.0;
      if (d > this.worldSize) {
         d = this.worldSize;
      }

      return d;
   }

   public void setDamageBuffer(double bufferSize) {
      this.damageBuffer = bufferSize;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onDamageBufferChanged(this, bufferSize);
      }
   }

   public double maxZ() {
      double d = this.getCenterZ() + this.getDiameter() / 2.0;
      if (d > this.worldSize) {
         d = this.worldSize;
      }

      return d;
   }

   public double getResizeSpeed() {
      return this.endTime == this.startTime ? 0.0 : Math.abs(this.startDiameter - this.endDiameter) / (this.endTime - this.startTime);
   }

   public int getSize() {
      return this.worldSize;
   }

   public EnumBorderStatus getStatus() {
      return this.endDiameter < this.startDiameter
         ? EnumBorderStatus.SHRINKING
         : (this.endDiameter > this.startDiameter ? EnumBorderStatus.GROWING : EnumBorderStatus.STATIONARY);
   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public double getTargetSize() {
      return this.endDiameter;
   }

   protected List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void setWarningDistance(int warningDistance) {
      this.warningDistance = warningDistance;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onWarningDistanceChanged(this, warningDistance);
      }
   }

   public long getTimeUntilTarget() {
      return this.getStatus() != EnumBorderStatus.STATIONARY ? this.endTime - System.currentTimeMillis() : 0L;
   }

   public void setSize(int size) {
      this.worldSize = size;
   }

   public double getDamageAmount() {
      return this.damageAmount;
   }

   public double minZ() {
      double d = this.getCenterZ() - this.getDiameter() / 2.0;
      if (d < -this.worldSize) {
         d = -this.worldSize;
      }

      return d;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public boolean contains(AxisAlignedBB bb) {
      return bb.maxX > this.minX() && bb.minX < this.maxX() && bb.maxZ > this.minZ() && bb.minZ < this.maxZ();
   }

   public void setDamageAmount(double newAmount) {
      this.damageAmount = newAmount;
      Iterator iterator2 = this.getListeners().iterator();

      while (iterator2.hasNext()) {
         ((IBorderListener)iterator2.next()).onDamageAmountChanged(this, newAmount);
      }
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getClosestDistance(Entity entityIn) {
      return this.getClosestDistance(entityIn.posX, entityIn.posZ);
   }

   public boolean contains(ChunkCoordIntPair range) {
      return range.getXEnd() > this.minX() && range.getXStart() < this.maxX() && range.getZEnd() > this.minZ() && range.getZStart() < this.maxZ();
   }
}
