package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase {
   public static MapData loadMapData(int mapId, World worldIn) {
      String text = "map_" + mapId;
      MapData mapData = (MapData)worldIn.loadItemData(MapData.class, text);
      if (mapData == null) {
         mapData = new MapData(text);
         worldIn.setItemData(text, mapData);
      }

      return mapData;
   }

   protected ItemMap() {
      this.getItem();
   }

   @Override
   public void noop(ItemStack itemStack, World world2, Entity entity2, boolean flag) {
      if (!world2.isRemote) {
         MapData mapData2 = this.getMapData(itemStack, world2);
         if (entity2 instanceof EntityPlayer entityPlayer) {
            mapData2.updateVisiblePlayers(entityPlayer, itemStack);
         }

         if (flag) {
            this.updateMapData(world2, entity2, mapData2);
         }
      }
   }

   public void updateMapData(World worldIn, Entity viewer, MapData data) {
      if (worldIn.provider.getDimensionId() == data.dimension && viewer instanceof EntityPlayer) {
         int i = 1 << data.scale;
         int xCenter2 = data.xCenter;
         int zCenter2 = data.zCenter;
         int i2 = MathHelper.floor_double(viewer.posX - xCenter2) / i + 64;
         int i3 = MathHelper.floor_double(viewer.posZ - zCenter2) / i + 64;
         int i4 = 128 / i;
         if (worldIn.provider.getHasNoSky()) {
            i4 /= 2;
         }

         net.minecraft.world.storage.MapData.MapInfo mapInfo = data.getMapInfo((EntityPlayer)viewer);
         mapInfo.mapUpdateTick++;
         int index = 0;

         for (int i5 = i2 - i4 + 1; i5 < i2 + i4; i5++) {
            if ((i5 & 15) == (mapInfo.mapUpdateTick & 15) || index != 0) {
               index = 0;
               double d = 0.0;

               for (int i6 = i3 - i4 - 1; i6 < i3 + i4; i6++) {
                  if (i5 >= 0 && i6 >= -1 && i5 < 128 && i6 < 128) {
                     int i7 = i5 - i2;
                     int i8 = i6 - i3;
                     boolean flag = i7 * i7 + i8 * i8 > (i4 - 2) * (i4 - 2);
                     int i9 = (xCenter2 / i + i5 - 64) * i;
                     int i10 = (zCenter2 / i + i6 - 64) * i;
                     HashMultiset hashMultiset = HashMultiset.create();
                     Chunk chunkFromBlockCoords = worldIn.getChunkFromBlockCoords(new BlockPos(i9, 0, i10));
                     if (!chunkFromBlockCoords.isEmpty()) {
                        int i11 = i9 & 15;
                        int i12 = i10 & 15;
                        int i13 = 0;
                        double d2 = 0.0;
                        if (worldIn.provider.getHasNoSky()) {
                           int i14 = i9 + i10 * 231871;
                           i14 = i14 * i14 * 31287121 + i14 * 11;
                           if ((i14 >> 20 & 1) == 0) {
                              hashMultiset.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, net.minecraft.block.BlockDirt.DirtType.DIRT)), 10);
                           } else {
                              hashMultiset.add(
                                 Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE)), 100
                              );
                           }

                           d2 = 100.0;
                        } else {
                           net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

                           for (int i15 = 0; i15 < i; i15++) {
                              for (int i16 = 0; i16 < i; i16++) {
                                 int i17 = chunkFromBlockCoords.getHeightValue(i15 + i11, i16 + i12) + 1;
                                 IBlockState defaultState = Blocks.air.getDefaultState();
                                 if (i17 > 1) {
                                    do {
                                       defaultState = chunkFromBlockCoords.getBlockState(mutableBlockPos.set(i15 + i11, --i17, i16 + i12));
                                    } while (defaultState.getBlock().getMapColor(defaultState) == MapColor.airColor && i17 > 0);

                                    if (i17 > 0 && defaultState.getBlock().getMaterial().isLiquid()) {
                                       int i18 = i17 - 1;

                                       Block block2;
                                       do {
                                          block2 = chunkFromBlockCoords.getBlock(i15 + i11, i18--, i16 + i12);
                                          i13++;
                                       } while (i18 > 0 && block2.getMaterial().isLiquid());
                                    }
                                 }

                                 d2 += (double)i17 / (i * i);
                                 hashMultiset.add(defaultState.getBlock().getMapColor(defaultState));
                              }
                           }
                        }

                        i13 /= i * i;
                        double d3 = (d2 - d) * 4.0 / (i + 4) + ((i5 + i6 & 1) - 0.5) * 0.4;
                        byte b = 1;
                        if (d3 > 0.6) {
                           b = 2;
                        }

                        if (d3 < -0.6) {
                           b = 0;
                        }

                        MapColor mapColor = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(hashMultiset), MapColor.airColor);
                        if (mapColor == MapColor.waterColor) {
                           d3 = i13 * 0.1 + (i5 + i6 & 1) * 0.2;
                           b = 1;
                           if (d3 < 0.5) {
                              b = 2;
                           }

                           if (d3 > 0.9) {
                              b = 0;
                           }
                        }

                        d = d2;
                        if (i6 >= 0 && i7 * i7 + i8 * i8 < i4 * i4 && (!flag || (i5 + i6 & 1) != 0)) {
                           byte b2 = data.colors[i5 + i6 * 128];
                           byte b3 = (byte)(mapColor.colorIndex * 4 + b);
                           if (b2 != b3) {
                              data.colors[i5 + i6 * 128] = b3;
                              data.updateMapData(i5, i6);
                              index = 1;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      MapData mapData = this.getMapData(stack, playerIn.worldObj);
      if (advanced) {
         if (mapData == null) {
            tooltip.add("Unknown map");
         } else {
            tooltip.add("Scaling at 1:" + (1 << mapData.scale));
            tooltip.add("(Level " + mapData.scale + "/4)");
         }
      }
   }

   @Override
   public Packet createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
      return this.getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
   }

   @Override
   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("map_is_scaling")) {
         MapData mapData = Items.filled_map.getMapData(stack, worldIn);
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         MapData mapData2 = new MapData("map_" + stack.getMetadata());
         mapData2.scale = (byte)(mapData.scale + 1);
         if (mapData2.scale > 4) {
            mapData2.scale = 4;
         }

         mapData2.calculateMapCenter(mapData.xCenter, mapData.zCenter, mapData2.scale);
         mapData2.dimension = mapData.dimension;
         mapData2.markDirty();
         worldIn.setItemData("map_" + stack.getMetadata(), mapData2);
      }
   }

   public MapData getMapData(ItemStack stack, World worldIn) {
      String text = "map_" + stack.getMetadata();
      MapData mapData2 = (MapData)worldIn.loadItemData(MapData.class, text);
      if (mapData2 == null && !worldIn.isRemote) {
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         text = "map_" + stack.getMetadata();
         mapData2 = new MapData(text);
         mapData2.scale = 3;
         mapData2.calculateMapCenter(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), mapData2.scale);
         mapData2.dimension = (byte)worldIn.provider.getDimensionId();
         mapData2.markDirty();
         worldIn.setItemData(text, mapData2);
      }

      return mapData2;
   }
}
