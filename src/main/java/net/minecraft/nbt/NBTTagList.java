package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase {
   private List<NBTBase> tagList = Lists.newArrayList();
   private byte tagType = 0;
   private static final Logger LOGGER = LogManager.getLogger();

   public float getFloatAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nBTBase = this.tagList.get(i);
         return nBTBase.getId() == 5 ? ((NBTTagFloat)nBTBase).getFloat() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public int[] getIntArrayAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nBTBase = this.tagList.get(i);
         return nBTBase.getId() == 11 ? ((NBTTagIntArray)nBTBase).getIntArray() : new int[0];
      } else {
         return new int[0];
      }
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder("[");

      for (int index = 0; index < this.tagList.size(); index++) {
         if (index != 0) {
            stringBuilder.append(',');
         }

         stringBuilder.append(index).append(':').append(this.tagList.get(index));
      }

      return stringBuilder.append(']').toString();
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(296L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagType = input.readByte();
      int intValue = input.readInt();
      if (this.tagType == 0 && intValue > 0) {
         throw new RuntimeException("Missing type on ListTag");
      }

      sizeTracker.read(32L * intValue);
      this.tagList = Lists.newArrayListWithCapacity(intValue);

      for (int innerIndex = 0; innerIndex < intValue; innerIndex++) {
         NBTBase nBTBase = NBTBase.createNewByType(this.tagType);
         nBTBase.read(input, depth + 1, sizeTracker);
         this.tagList.add(nBTBase);
      }
   }

   public void appendTag(NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.add(nbt);
      }
   }

   public NBTTagCompound getCompoundTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nBTBase = this.tagList.get(i);
         return nBTBase.getId() == 10 ? (NBTTagCompound)nBTBase : new NBTTagCompound();
      } else {
         return new NBTTagCompound();
      }
   }

   @Override
   void write(DataOutput output) throws IOException {
      if (!this.tagList.isEmpty()) {
         this.tagType = this.tagList.get(0).getId();
      } else {
         this.tagType = 0;
      }

      output.writeByte(this.tagType);
      output.writeInt(this.tagList.size());

      for (int index = 0; index < this.tagList.size(); index++) {
         this.tagList.get(index).write(output);
      }
   }

   public NBTBase removeTag(int i) {
      return this.tagList.remove(i);
   }

   public int tagCount() {
      return this.tagList.size();
   }

   @Override
   public boolean hasNoTags() {
      return this.tagList.isEmpty();
   }

   public void set(int idx, NBTBase nbt) {
      if (nbt.getId() == 0) {
         LOGGER.warn("Invalid TagEnd added to ListTag");
      } else if (idx >= 0 && idx < this.tagList.size()) {
         if (this.tagType == 0) {
            this.tagType = nbt.getId();
         } else if (this.tagType != nbt.getId()) {
            LOGGER.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.tagList.set(idx, nbt);
      } else {
         LOGGER.warn("index out of bounds to set tag in tag list");
      }
   }

   public double getDoubleAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nBTBase = this.tagList.get(i);
         return nBTBase.getId() == 6 ? ((NBTTagDouble)nBTBase).getDouble() : 0.0;
      } else {
         return 0.0;
      }
   }

   public int getTagType() {
      return this.tagType;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagList.hashCode();
   }

   public String getStringTagAt(int i) {
      if (i >= 0 && i < this.tagList.size()) {
         NBTBase nBTBase = this.tagList.get(i);
         return nBTBase.getId() == 8 ? nBTBase.getString() : nBTBase.toString();
      } else {
         return "";
      }
   }

   @Override
   public NBTBase copy() {
      NBTTagList nbtTagList = new NBTTagList();
      nbtTagList.tagType = this.tagType;
      Iterator iterator2 = this.tagList.iterator();

      while (iterator2.hasNext()) {
         NBTBase nBTBase2 = ((NBTBase)iterator2.next()).copy();
         nbtTagList.tagList.add(nBTBase2);
      }

      return nbtTagList;
   }

   public NBTBase get(int idx) {
      return idx >= 0 && idx < this.tagList.size() ? this.tagList.get(idx) : new NBTTagEnd();
   }

   @Override
   public boolean equals(Object other) {
      if (super.equals(other)) {
         NBTTagList nBTTagList = (NBTTagList)other;
         if (this.tagType == nBTTagList.tagType) {
            return this.tagList.equals(nBTTagList.tagList);
         }
      }

      return false;
   }

   @Override
   public byte getId() {
      return 9;
   }
}
