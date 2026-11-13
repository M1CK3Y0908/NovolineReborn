package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

public class MapStorage {
   protected Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
   private List<WorldSavedData> loadedDataList = Lists.newArrayList();
   private Map<String, Short> idCounts = Maps.newHashMap();
   private ISaveHandler saveHandler;

   private void loadIdCounts() {
      try {
         this.idCounts.clear();
         if (this.saveHandler == null) {
            return;
         }

         File file = this.saveHandler.getMapFileFromName("idcounts");
         if (file != null && file.exists()) {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            NBTTagCompound nBTTagCompound = CompressedStreamTools.read(dataInputStream);
            dataInputStream.close();

            for (String text : nBTTagCompound.getKeySet()) {
               NBTBase tag = nBTTagCompound.getTag(text);
               if (tag instanceof NBTTagShort) {
                  short shortValue = ((NBTTagShort)tag).getShort();
                  this.idCounts.put(text, shortValue);
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void saveAllData() {
      for (int index = 0; index < this.loadedDataList.size(); index++) {
         WorldSavedData worldSavedData = this.loadedDataList.get(index);
         if (worldSavedData.isDirty()) {
            this.saveData(worldSavedData);
            worldSavedData.setDirty(false);
         }
      }
   }

   public WorldSavedData loadData(Class<? extends WorldSavedData> clazz, String dataIdentifier) {
      WorldSavedData worldSavedData = this.loadedDataMap.get(dataIdentifier);
      if (worldSavedData != null) {
         return worldSavedData;
      }

      if (this.saveHandler != null) {
         try {
            File file = this.saveHandler.getMapFileFromName(dataIdentifier);
            if (file != null && file.exists()) {
               try {
                  worldSavedData = clazz.getConstructor(String.class).newInstance(dataIdentifier);
               } catch (Exception exception) {
                  throw new RuntimeException("Failed to instantiate " + clazz.toString(), exception);
               }

               FileInputStream fileInputStream = new FileInputStream(file);
               NBTTagCompound nBTTagCompound = CompressedStreamTools.readCompressed(fileInputStream);
               fileInputStream.close();
               worldSavedData.readFromNBT(nBTTagCompound.getCompoundTag("data"));
            }
         } catch (Exception exception2) {
            exception2.printStackTrace();
         }
      }

      if (worldSavedData != null) {
         this.loadedDataMap.put(dataIdentifier, worldSavedData);
         this.loadedDataList.add(worldSavedData);
      }

      return worldSavedData;
   }

   public MapStorage(ISaveHandler saveHandlerIn) {
      this.saveHandler = saveHandlerIn;
      this.loadIdCounts();
   }

   public int getUniqueDataId(String key) {
      Short sValue = this.idCounts.get(key);
      if (sValue == null) {
         sValue = (short)0;
      } else {
         sValue = (short)(sValue + 1);
      }

      this.idCounts.put(key, sValue);
      if (this.saveHandler == null) {
         return sValue;
      }

      try {
         File mapFileFromName = this.saveHandler.getMapFileFromName("idcounts");
         if (mapFileFromName != null) {
            NBTTagCompound nbtTagCompound = new NBTTagCompound();

            for (String text : this.idCounts.keySet()) {
               short s = this.idCounts.get(text);
               nbtTagCompound.setShort(text, s);
            }

            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(mapFileFromName));
            CompressedStreamTools.write(nbtTagCompound, dataOutputStream);
            dataOutputStream.close();
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return sValue;
   }

   private void saveData(WorldSavedData worldSavedData2) {
      if (this.saveHandler != null) {
         try {
            File file = this.saveHandler.getMapFileFromName(worldSavedData2.mapName);
            if (file != null) {
               NBTTagCompound nbtTagCompound = new NBTTagCompound();
               worldSavedData2.writeToNBT(nbtTagCompound);
               NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
               nbtTagCompound2.setTag("data", nbtTagCompound);
               FileOutputStream fileOutputStream = new FileOutputStream(file);
               CompressedStreamTools.writeCompressed(nbtTagCompound2, fileOutputStream);
               fileOutputStream.close();
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public void setData(String dataIdentifier, WorldSavedData data) {
      if (this.loadedDataMap.containsKey(dataIdentifier)) {
         this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
      }

      this.loadedDataMap.put(dataIdentifier, data);
      this.loadedDataList.add(data);
   }
}
