package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
   public byte[] colors = new byte[16384];
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   private Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   public byte dimension;
   public byte scale;
   public int xCenter;
   public int zCenter;
   public Map<String, Vec4b> mapDecorations = Maps.newLinkedHashMap();

   public MapData(String mapname) {
      super(mapname);
   }

   public MapData.MapInfo getMapInfo(EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      if (mapdata$mapinfo == null) {
         mapdata$mapinfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapdata$mapinfo);
         this.playersArrayList.add(mapdata$mapinfo);
      }

      return mapdata$mapinfo;
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setByte("dimension", this.dimension);
      nbt.setInteger("xCenter", this.xCenter);
      nbt.setInteger("zCenter", this.zCenter);
      nbt.setByte("scale", this.scale);
      nbt.setShort("width", (short)128);
      nbt.setShort("height", (short)128);
      nbt.setByteArray("colors", this.colors);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.dimension = nbt.getByte("dimension");
      this.xCenter = nbt.getInteger("xCenter");
      this.zCenter = nbt.getInteger("zCenter");
      this.scale = nbt.getByte("scale");
      this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
      int shortValue = nbt.getShort("width");
      int shortValue2 = nbt.getShort("height");
      if (shortValue == 128 && shortValue2 == 128) {
         this.colors = nbt.getByteArray("colors");
      } else {
         byte[] byteArray = nbt.getByteArray("colors");
         this.colors = new byte[16384];
         int i = (128 - shortValue) / 2;
         int i2 = (128 - shortValue2) / 2;

         for (int index = 0; index < shortValue2; index++) {
            int i3 = index + i2;
            if (i3 >= 0 || i3 < 128) {
               for (int nestedIndex = 0; nestedIndex < shortValue; nestedIndex++) {
                  int i4 = nestedIndex + i;
                  if (i4 >= 0 || i4 < 128) {
                     this.colors[i4 + i3 * 128] = byteArray[nestedIndex + index * shortValue];
                  }
               }
            }
         }
      }
   }

   public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player) {
      MapData.MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
      return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
   }

   private void updateDecorations(int type, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation) {
      int i = 1 << this.scale;
      float f = (float)(worldX - this.xCenter) / i;
      float f2 = (float)(worldZ - this.zCenter) / i;
      byte b1 = (byte)(f * 2.0F + 0.5);
      int i2 = (byte)(f2 * 2.0F + 0.5);
      int i3;
      if (f >= -63 && f2 >= -63 && f <= 63 && f2 <= 63) {
         rotation += rotation < 0.0 ? -8.0 : 8.0;
         i3 = (byte)(rotation * 16.0 / 360.0);
         if (this.dimension < 0) {
            int i4 = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
            i3 = (byte)(i4 * i4 * 34187121 + i4 * 121 >> 15 & 15);
         }
      } else {
         if (Math.abs(f) >= 320.0F || Math.abs(f2) >= 320.0F) {
            this.mapDecorations.remove(entityIdentifier);
            return;
         }

         type = 6;
         i3 = 0;
         if (f <= -63) {
            b1 = (byte)(126 + 2.5);
         }

         if (f2 <= -63) {
            i2 = (byte)(126 + 2.5);
         }

         if (f >= 63) {
            b1 = 127;
         }

         if (f2 >= 63) {
            i2 = 127;
         }
      }

      this.mapDecorations.put(entityIdentifier, new Vec4b((byte)type, b1, (byte)i2, (byte)i3));
   }

   public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
      if (!this.playersHashMap.containsKey(player)) {
         MapData.MapInfo nBTTagList = new MapData.MapInfo(player);
         this.playersHashMap.put(player, nBTTagList);
         this.playersArrayList.add(nBTTagList);
      }

      if (!player.inventory.hasItemStack(mapStack)) {
         this.mapDecorations.remove(player.getName());
      }

      for (int index = 0; index < this.playersArrayList.size(); index++) {
         MapData.MapInfo mapdata$mapinfo1 = this.playersArrayList.get(index);
         if (!mapdata$mapinfo1.entityplayerObj.isDead && (mapdata$mapinfo1.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
            if (!mapStack.isOnItemFrame() && mapdata$mapinfo1.entityplayerObj.dimension == this.dimension) {
               this.updateDecorations(
                  0,
                  mapdata$mapinfo1.entityplayerObj.worldObj,
                  mapdata$mapinfo1.entityplayerObj.getName(),
                  mapdata$mapinfo1.entityplayerObj.posX,
                  mapdata$mapinfo1.entityplayerObj.posZ,
                  mapdata$mapinfo1.entityplayerObj.rotationYaw
               );
            }
         } else {
            this.playersHashMap.remove(mapdata$mapinfo1.entityplayerObj);
            this.playersArrayList.remove(mapdata$mapinfo1);
         }
      }

      if (mapStack.isOnItemFrame()) {
         EntityItemFrame entityItemFrame = mapStack.getItemFrame();
         BlockPos blockPos = entityItemFrame.getHangingPosition();
         this.updateDecorations(
            1,
            player.worldObj,
            "frame-" + entityItemFrame.getEntityId(),
            blockPos.getX(),
            blockPos.getZ(),
            entityItemFrame.facingDirection.getHorizontalIndex() * 90
         );
      }

      if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
         NBTTagList nBTTagList = mapStack.getTagCompound().getTagList("Decorations", 10);

         for (int innerIndex = 0; innerIndex < nBTTagList.tagCount(); innerIndex++) {
            NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(innerIndex);
            if (!this.mapDecorations.containsKey(nBTTagCompound.getString("id"))) {
               this.updateDecorations(
                  nBTTagCompound.getByte("type"),
                  player.worldObj,
                  nBTTagCompound.getString("id"),
                  nBTTagCompound.getDouble("x"),
                  nBTTagCompound.getDouble("z"),
                  nBTTagCompound.getDouble("rot")
               );
            }
         }
      }
   }

   public void updateMapData(int x, int y) {
      super.markDirty();
      Iterator iterator2 = this.playersArrayList.iterator();

      while (iterator2.hasNext()) {
         ((MapData.MapInfo)iterator2.next()).update(x, y);
      }
   }

   public void calculateMapCenter(double x, double z, int mapScale) {
      int i = 128 * (1 << mapScale);
      int i2 = MathHelper.floor_double((x + 64.0) / i);
      int i3 = MathHelper.floor_double((z + 64.0) / i);
      this.xCenter = i2 * i + i / 2 - 64;
      this.zCenter = i3 * i + i / 2 - 64;
   }

   public class MapInfo {
      private boolean dirty = true;
      public int mapUpdateTick;
      private int minX = 0;
      private int minY = 0;
      private int maxX = 127;
      private int maxY = 127;
      private int emptyPacketTick;
      public final EntityPlayer entityplayerObj;

      public Packet getPacket(ItemStack stack) {
         if (this.dirty) {
            this.dirty = false;
            return new S34PacketMaps(
               stack.getMetadata(),
               MapData.this.scale,
               MapData.this.mapDecorations.values(),
               MapData.this.colors,
               this.minX,
               this.minY,
               this.maxX + 1 - this.minX,
               this.maxY + 1 - this.minY
            );
         } else {
            return this.emptyPacketTick++ % 5 == 0
               ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0)
               : null;
         }
      }

      public MapInfo(EntityPlayer player) {
         this.entityplayerObj = player;
      }

      public void update(int x, int y) {
         if (this.dirty) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
         } else {
            this.dirty = true;
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
         }
      }
   }
}
