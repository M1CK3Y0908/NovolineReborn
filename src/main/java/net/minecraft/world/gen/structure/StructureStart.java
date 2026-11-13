package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart {
   protected LinkedList<StructureComponent> components = new LinkedList<>();
   protected StructureBoundingBox boundingBox;
   private int chunkPosX;
   private int chunkPosZ;

   public int getChunkPosX() {
      return this.chunkPosX;
   }

   public boolean isSizeableStructure() {
      return true;
   }

   public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb) {
      Iterator<StructureComponent> iterator = this.components.iterator();

      while (iterator.hasNext()) {
         StructureComponent structureComponent = iterator.next();
         if (structureComponent.getBoundingBox().intersectsWith(structurebb) && !structureComponent.addComponentParts(worldIn, rand, structurebb)) {
            iterator.remove();
         }
      }
   }

   public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setString("id", MapGenStructureIO.getStructureStartName(this));
      nbtTagCompound.setInteger("ChunkX", chunkX);
      nbtTagCompound.setInteger("ChunkZ", chunkZ);
      nbtTagCompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
      NBTTagList nbtTagList = new NBTTagList();

      for (StructureComponent structureComponent2 : this.components) {
         nbtTagList.appendTag(structureComponent2.createStructureBaseNBT());
      }

      nbtTagCompound.setTag("Children", nbtTagList);
      this.writeToNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   public StructureStart(int chunkX, int chunkZ) {
      this.chunkPosX = chunkX;
      this.chunkPosZ = chunkZ;
   }

   public boolean isValidForPostProcess(ChunkCoordIntPair pair) {
      return true;
   }

   public void writeToNBT(NBTTagCompound tagCompound) {
   }

   public LinkedList<StructureComponent> getComponents() {
      return this.components;
   }

   public StructureBoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   protected void handleWorld(World world2, Random random) {
      int i = world2.getSeaLevel() - 10;
      int i2 = this.boundingBox.getYSize() + 1;
      if (i2 < i) {
         i2 += random.nextInt(i - i2);
      }

      int i3 = i2 - this.boundingBox.maxY;
      this.boundingBox.offset(0, i3, 0);
      Iterator iterator2 = this.components.iterator();

      while (iterator2.hasNext()) {
         ((StructureComponent)iterator2.next()).handleInt(i3);
      }
   }

   public void setChunkProcessed(ChunkCoordIntPair pair) {
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
   }

   public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound) {
      this.chunkPosX = tagCompound.getInteger("ChunkX");
      this.chunkPosZ = tagCompound.getInteger("ChunkZ");
      if (tagCompound.hasKey("BB")) {
         this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
      }

      NBTTagList nBTTagList = tagCompound.getTagList("Children", 10);

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         this.components.add(MapGenStructureIO.getStructureComponent(nBTTagList.getCompoundTagAt(index), worldIn));
      }

      this.readFromNBT(tagCompound);
   }

   protected void updateBoundingBox() {
      this.boundingBox = StructureBoundingBox.getNewBoundingBox();

      for (StructureComponent structureComponent2 : this.components) {
         this.boundingBox.expandTo(structureComponent2.getBoundingBox());
      }
   }

   protected void handleWorld2(World world2, Random random) {
      int i = 23 - this.boundingBox.getYSize();
      int i2;
      if (i > 1) {
         i2 = 48 + random.nextInt(i);
      } else {
         i2 = 48;
      }

      int i3 = i2 - this.boundingBox.minY;
      this.boundingBox.offset(0, i3, 0);
      Iterator iterator2 = this.components.iterator();

      while (iterator2.hasNext()) {
         ((StructureComponent)iterator2.next()).handleInt(i3);
      }
   }

   public StructureStart() {
   }

   public int getChunkPosZ() {
      return this.chunkPosZ;
   }
}
