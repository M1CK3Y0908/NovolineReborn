package net.minecraft.world;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   public final String mapName;
   private boolean dirty;

   public boolean isDirty() {
      return this.dirty;
   }

   public void markDirty() {
      this.setDirty(true);
   }

   public abstract void writeToNBT(NBTTagCompound nbtTagCompound);

   public WorldSavedData(String name) {
      this.mapName = name;
   }

   public void setDirty(boolean isDirty) {
      this.dirty = isDirty;
   }

   public abstract void readFromNBT(NBTTagCompound nbtTagCompound);
}
