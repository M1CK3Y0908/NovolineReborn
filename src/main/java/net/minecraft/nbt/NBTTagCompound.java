package net.minecraft.nbt;

import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class NBTTagCompound extends NBTBase {
   private Map<String, NBTBase> tagMap = Maps.newHashMap();

   public void merge(NBTTagCompound other) {
      for (String text2 : other.tagMap.keySet()) {
         NBTBase nbtBase = other.tagMap.get(text2);
         if (nbtBase.getId() == 10) {
            if (this.hasKey(text2, 10)) {
               this.getCompoundTag(text2).merge((NBTTagCompound)nbtBase);
            } else {
               this.setTag(text2, nbtBase.copy());
            }
         } else {
            this.setTag(text2, nbtBase.copy());
         }
      }
   }

   public NBTTagList getTagList(String key, int type) {
      try {
         if (this.getTagId(key) != 9) {
            return new NBTTagList();
         }

         NBTTagList nBTTagList = (NBTTagList)this.tagMap.get(key);
         return nBTTagList.tagCount() > 0 && nBTTagList.getTagType() != type ? new NBTTagList() : nBTTagList;
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 9, classCastException));
      }
   }

   private static String readKey(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readUTF();
   }

   public void setInteger(String key, int value) {
      this.tagMap.put(key, new NBTTagInt(value));
   }

   public void setIntArray(String key, int[] value) {
      this.tagMap.put(key, new NBTTagIntArray(value));
   }

   public Set<String> getKeySet() {
      return this.tagMap.keySet();
   }

   public byte getTagId(String key) {
      NBTBase nBTBase = this.tagMap.get(key);
      return nBTBase != null ? nBTBase.getId() : 0;
   }

   @Override
   public NBTBase copy() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (String text2 : this.tagMap.keySet()) {
         nbtTagCompound.setTag(text2, this.tagMap.get(text2).copy());
      }

      return nbtTagCompound;
   }

   public void setByteArray(String key, byte[] value) {
      this.tagMap.put(key, new NBTTagByteArray(value));
   }

   static NBTBase readNBT(byte id, String key, DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      NBTBase newByType = NBTBase.createNewByType(id);

      try {
         newByType.read(input, depth, sizeTracker);
         return newByType;
      } catch (IOException ioException) {
         CrashReport crashReport2 = CrashReport.makeCrashReport(ioException, "Loading NBT data");
         CrashReportCategory crashReportCategory2 = crashReport2.makeCategory("NBT Tag");
         crashReportCategory2.addCrashSection("Tag name", key);
         crashReportCategory2.addCrashSection("Tag type", id);
         throw new ReportedException(crashReport2);
      }
   }

   private static byte readType(DataInput input, NBTSizeTracker sizeTracker) throws IOException {
      return input.readByte();
   }

   public byte[] getByteArray(String key) {
      try {
         return !this.hasKey(key, 7) ? new byte[0] : ((NBTTagByteArray)this.tagMap.get(key)).getByteArray();
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 7, classCastException));
      }
   }

   public void setString(String key, String value) {
      this.tagMap.put(key, new NBTTagString(value));
   }

   public int getInteger(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getInt();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   public void setDouble(String key, double value) {
      this.tagMap.put(key, new NBTTagDouble(value));
   }

   @Override
   public boolean hasNoTags() {
      return this.tagMap.isEmpty();
   }

   public double getDouble(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getDouble();
      } catch (ClassCastException classCastException) {
         return 0.0;
      }
   }

   public short getShort(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getShort();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   @Override
   void write(DataOutput output) throws IOException {
      for (String text2 : this.tagMap.keySet()) {
         NBTBase nbtBase = this.tagMap.get(text2);
         writeEntry(text2, nbtBase, output);
      }

      output.writeByte(0);
   }

   public void setShort(String key, short value) {
      this.tagMap.put(key, new NBTTagShort(value));
   }

   public byte getByte(String key) {
      try {
         return !this.hasKey(key, 99) ? 0 : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getByte();
      } catch (ClassCastException classCastException) {
         return 0;
      }
   }

   @Override
   public boolean equals(Object other) {
      if (super.equals(other)) {
         NBTTagCompound nBTTagCompound = (NBTTagCompound)other;
         return this.tagMap.entrySet().equals(nBTTagCompound.tagMap.entrySet());
      } else {
         return false;
      }
   }

   public boolean getBoolean(String key) {
      return this.getByte(key) != 0;
   }

   public void setTag(String key, NBTBase value) {
      this.tagMap.put(key, value);
   }

   public NBTTagCompound getCompoundTag(String key) {
      try {
         return !this.hasKey(key, 10) ? new NBTTagCompound() : (NBTTagCompound)this.tagMap.get(key);
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 10, classCastException));
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.tagMap.hashCode();
   }

   public boolean hasKey(String key, int type) {
      int tagId = this.getTagId(key);
      if (tagId == type) {
         return true;
      }

      if (type != 99) {
         if (tagId > 0) {
         }

         return false;
      } else {
         return tagId == 1 || tagId == 2 || tagId == 3 || tagId == 4 || tagId == 5 || tagId == 6;
      }
   }

   public void setLong(String key, long value) {
      this.tagMap.put(key, new NBTTagLong(value));
   }

   @Override
   public byte getId() {
      return 10;
   }

   private static void writeEntry(String name, NBTBase data, DataOutput output) throws IOException {
      output.writeByte(data.getId());
      if (data.getId() != 0) {
         output.writeUTF(name);
         data.write(output);
      }
   }

   public void setFloat(String key, float value) {
      this.tagMap.put(key, new NBTTagFloat(value));
   }

   @Override
   void read(DataInput input, int depth, NBTSizeTracker sizeTracker) throws IOException {
      sizeTracker.read(384L);
      if (depth > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      }

      this.tagMap.clear();

      byte b;
      while ((b = readType(input, sizeTracker)) != 0) {
         String text = readKey(input, sizeTracker);
         sizeTracker.read(224 + 16 * text.length());
         NBTBase nBTBase = readNBT(b, text, input, depth + 1, sizeTracker);
         if (this.tagMap.put(text, nBTBase) != null) {
            sizeTracker.read(288L);
         }
      }
   }

   private CrashReport createCrashReport(final String key, final int expectedType, ClassCastException ex) {
      CrashReport crashReport = CrashReport.makeCrashReport(ex, "Reading NBT data");
      CrashReportCategory crashReportCategory = crashReport.getCrashReportCategory("Corrupt NBT tag");
      crashReportCategory.addCrashSectionCallable("Tag type found", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[NBTTagCompound.this.tagMap.get(key).getId()];
         }
      });
      crashReportCategory.addCrashSectionCallable("Tag type expected", new Callable<String>() {
         public String call() throws Exception {
            return NBTBase.NBT_TYPES[expectedType];
         }
      });
      crashReportCategory.addCrashSection("Tag name", key);
      return crashReport;
   }

   public void setBoolean(String key, boolean value) {
      this.setByte(key, (byte)(value ? 1 : 0));
   }

   public void removeTag(String key) {
      this.tagMap.remove(key);
   }

   public NBTBase getTag(String key) {
      return this.tagMap.get(key);
   }

   public int[] getIntArray(String key) {
      try {
         return !this.hasKey(key, 11) ? new int[0] : ((NBTTagIntArray)this.tagMap.get(key)).getIntArray();
      } catch (ClassCastException classCastException) {
         throw new ReportedException(this.createCrashReport(key, 11, classCastException));
      }
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder("{");

      for (Entry entry : this.tagMap.entrySet()) {
         if (stringBuilder.length() != 1) {
            stringBuilder.append(',');
         }

         stringBuilder.append((String)entry.getKey()).append(':').append(entry.getValue());
      }

      return stringBuilder.append('}').toString();
   }

   public float getFloat(String key) {
      try {
         return !this.hasKey(key, 99) ? 0.0F : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getFloat();
      } catch (ClassCastException classCastException) {
         return 0.0F;
      }
   }

   public void setByte(String key, byte value) {
      this.tagMap.put(key, new NBTTagByte(value));
   }

   public String getString(String key) {
      try {
         return !this.hasKey(key, 8) ? "" : this.tagMap.get(key).getString();
      } catch (ClassCastException classCastException) {
         return "";
      }
   }

   public long getLong(String key) {
      try {
         return !this.hasKey(key, 99) ? 0L : ((NBTBase.NBTPrimitive)this.tagMap.get(key)).getLong();
      } catch (ClassCastException classCastException) {
         return 0L;
      }
   }

   public boolean hasKey(String key) {
      return this.tagMap.containsKey(key);
   }
}
