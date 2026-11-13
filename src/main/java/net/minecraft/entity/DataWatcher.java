package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Rotations;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.commons.lang3.ObjectUtils;

public class DataWatcher {
   private static final Map<Class<?>, Integer> dataTypes = Maps.newHashMap();
   private boolean isBlank = true;
   private final Map<Integer, DataWatcher.WatchableObject> watchedObjects = Maps.newHashMap();
   private ReadWriteLock lock = new ReentrantReadWriteLock();
   public BiomeGenBase spawnBiome = BiomeGenBase.plains;
   public BlockPos spawnPosition = BlockPos.ORIGIN;
   private final Entity owner;
   private boolean objectChanged;

   public Rotations getWatchableObjectRotations(int id) {
      return (Rotations)this.getWatchedObject(id).getObject();
   }

   public short getWatchableObjectShort(int id) {
      return (Short)this.getWatchedObject(id).getObject();
   }

   public boolean hasObjectChanged() {
      return this.objectChanged;
   }

   public String getWatchableObjectString(int id) {
      return (String)this.getWatchedObject(id).getObject();
   }

   public <T> void addObject(int id, T t) {
      Integer integer = dataTypes.get(t.getClass());
      if (integer == null) {
         throw new IllegalArgumentException("Unknown data type: " + t.getClass());
      }

      if (id > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is 31)");
      }

      if (this.watchedObjects.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id value for " + id + "!");
      }

      DataWatcher.WatchableObject watchableObject2 = new DataWatcher.WatchableObject(integer, id, t);
      this.lock.writeLock().lock();
      this.watchedObjects.put(id, watchableObject2);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   public List<DataWatcher.WatchableObject> getChanged() {
      ArrayList arrayList = null;
      if (this.objectChanged) {
         this.lock.readLock().lock();

         for (DataWatcher.WatchableObject watchableObject2 : this.watchedObjects.values()) {
            if (watchableObject2.isWatched()) {
               watchableObject2.setWatched(false);
               if (arrayList == null) {
                  arrayList = Lists.newArrayList();
               }

               arrayList.add(watchableObject2);
            }
         }

         this.lock.readLock().unlock();
      }

      this.objectChanged = false;
      return arrayList;
   }

   public void setObjectWatched(int id) {
      this.getWatchedObject(id).watched = true;
      this.objectChanged = true;
   }

   public ItemStack getWatchableObjectItemStack(int id) {
      return (ItemStack)this.getWatchedObject(id).getObject();
   }

   public void updateWatchedObjectsFromList(List<DataWatcher.WatchableObject> list2) {
      this.lock.writeLock().lock();

      for (DataWatcher.WatchableObject watchableObject2 : list2) {
         DataWatcher.WatchableObject watchableObject3 = this.watchedObjects.get(watchableObject2.getDataValueId());
         if (watchableObject3 != null) {
            watchableObject3.setObject(watchableObject2.getObject());
            this.owner.onDataWatcherUpdate(watchableObject2.getDataValueId());
         }
      }

      this.lock.writeLock().unlock();
      this.objectChanged = true;
   }

   public DataWatcher(Entity owner) {
      this.owner = owner;
   }

   public static List<DataWatcher.WatchableObject> readWatchedListFromPacketBuffer(PacketBuffer buffer) throws IOException {
      List<DataWatcher.WatchableObject> list = null;

      for (int index = buffer.readByte(); index != 127; index = buffer.readByte()) {
         if (list == null) {
            list = Lists.newArrayList();
         }

         int i = (index & 224) >> 5;
         int i2 = index & 31;
         DataWatcher.WatchableObject watchableObject2 = null;
         switch (i) {
            case 0:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readByte());
               break;
            case 1:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readShort());
               break;
            case 2:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readInt());
               break;
            case 3:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readFloat());
               break;
            case 4:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readStringFromBuffer(32767));
               break;
            case 5:
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, buffer.readItemStackFromBuffer());
               break;
            case 6:
               int intValue = buffer.readInt();
               int intValue2 = buffer.readInt();
               int intValue3 = buffer.readInt();
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, new BlockPos(intValue, intValue2, intValue3));
               break;
            case 7:
               float floatValue = buffer.readFloat();
               float floatValue2 = buffer.readFloat();
               float floatValue3 = buffer.readFloat();
               watchableObject2 = new DataWatcher.WatchableObject(i, i2, new Rotations(floatValue, floatValue2, floatValue3));
         }

         list.add(watchableObject2);
      }

      return list;
   }

   public static void writeWatchedListToPacketBuffer(List<DataWatcher.WatchableObject> objectsList, PacketBuffer buffer) throws IOException {
      if (objectsList != null) {
         for (DataWatcher.WatchableObject watchableObject2 : objectsList) {
            writeWatchableObjectToPacketBuffer(buffer, watchableObject2);
         }
      }

      buffer.writeByte(127);
   }

   static {
      dataTypes.put(Byte.class, 0);
      dataTypes.put(Short.class, 1);
      dataTypes.put(Integer.class, 2);
      dataTypes.put(Float.class, 3);
      dataTypes.put(String.class, 4);
      dataTypes.put(ItemStack.class, 5);
      dataTypes.put(BlockPos.class, 6);
      dataTypes.put(Rotations.class, 7);
   }

   public int getWatchableObjectInt(int id) {
      return (Integer)this.getWatchedObject(id).getObject();
   }

   public void handleInt(int i) {
      DataWatcher.WatchableObject watchableObject2 = new DataWatcher.WatchableObject(5, i, null);
      this.lock.writeLock().lock();
      this.watchedObjects.put(i, watchableObject2);
      this.lock.writeLock().unlock();
      this.isBlank = false;
   }

   private static void writeWatchableObjectToPacketBuffer(PacketBuffer buffer, DataWatcher.WatchableObject watchableObject) throws IOException {
      int i = (watchableObject.getObjectType() << 5 | watchableObject.getDataValueId() & 31) & 0xFF;
      buffer.writeByte(i);
      switch (watchableObject.getObjectType()) {
         case 0:
            buffer.writeByte((Byte)watchableObject.getObject());
            break;
         case 1:
            buffer.writeShort((Short)watchableObject.getObject());
            break;
         case 2:
            buffer.writeInt((Integer)watchableObject.getObject());
            break;
         case 3:
            buffer.writeFloat((Float)watchableObject.getObject());
            break;
         case 4:
            buffer.writeString((String)watchableObject.getObject());
            break;
         case 5:
            ItemStack itemStack = (ItemStack)watchableObject.getObject();
            buffer.writeItemStackToBuffer(itemStack);
            break;
         case 6:
            BlockPos blockPos = (BlockPos)watchableObject.getObject();
            buffer.writeInt(blockPos.getX());
            buffer.writeInt(blockPos.getY());
            buffer.writeInt(blockPos.getZ());
            break;
         case 7:
            Rotations rotations = (Rotations)watchableObject.getObject();
            buffer.writeFloat(rotations.getX());
            buffer.writeFloat(rotations.getY());
            buffer.writeFloat(rotations.getZ());
      }
   }

   public boolean getIsBlank() {
      return this.isBlank;
   }

   public float getWatchableObjectFloat(int id) {
      return (Float)this.getWatchedObject(id).getObject();
   }

   private DataWatcher.WatchableObject getWatchedObject(int id) {
      this.lock.readLock().lock();

      DataWatcher.WatchableObject watchableObject2;
      try {
         watchableObject2 = this.watchedObjects.get(id);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
         crashReport.makeCategory("Synched entity data").addCrashSection("Data ID", id);
         throw new ReportedException(crashReport);
      }

      this.lock.readLock().unlock();
      return watchableObject2;
   }

   public List<DataWatcher.WatchableObject> getAllWatched() {
      ArrayList arrayList = null;
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject2 : this.watchedObjects.values()) {
         if (arrayList == null) {
            arrayList = Lists.newArrayList();
         }

         arrayList.add(watchableObject2);
      }

      this.lock.readLock().unlock();
      return arrayList;
   }

   public void writeTo(PacketBuffer buffer) throws IOException {
      this.lock.readLock().lock();

      for (DataWatcher.WatchableObject watchableObject2 : this.watchedObjects.values()) {
         writeWatchableObjectToPacketBuffer(buffer, watchableObject2);
      }

      this.lock.readLock().unlock();
      buffer.writeByte(127);
   }

   public void resetObjectChangedFlag() {
      this.objectChanged = false;
   }

   public <T> void updateObject(int id, T newData) {
      DataWatcher.WatchableObject datawatcher$watchableobject = this.getWatchedObject(id);
      if (ObjectUtils.notEqual(newData, datawatcher$watchableobject.getObject())) {
         datawatcher$watchableobject.setObject(newData);
         this.owner.onDataWatcherUpdate(id);
         datawatcher$watchableobject.setWatched(true);
         this.objectChanged = true;
      }
   }

   public byte getWatchableObjectByte(int id) {
      return (Byte)this.getWatchedObject(id).getObject();
   }

   public static class WatchableObject {
      private final int dataValueId;
      private Object watchedObject;
      private final int objectType;
      private boolean watched;

      public int getObjectType() {
         return this.objectType;
      }

      public boolean isWatched() {
         return this.watched;
      }

      public Object getObject() {
         return this.watchedObject;
      }

      public WatchableObject(int type, int id, Object watchedValue) {
         this.dataValueId = id;
         this.watchedObject = watchedValue;
         this.objectType = type;
         this.watched = true;
      }

      public int getDataValueId() {
         return this.dataValueId;
      }

      public void setWatched(boolean watched) {
         this.watched = watched;
      }

      public void setObject(Object watchedValue) {
         this.watchedObject = watchedValue;
      }
   }
}
