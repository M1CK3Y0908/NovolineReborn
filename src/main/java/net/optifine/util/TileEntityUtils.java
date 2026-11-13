package net.optifine.util;

import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.optifine.reflect.Reflector;

public class TileEntityUtils {
   public static void updateTileEntityName(TileEntity te) {
      BlockPos blockPos = te.getPos();
      if (getTileEntityRawName(te) == null) {
         String text = getServerTileEntityRawName(blockPos);
         text = Config.normalize(text);
         checkTileEntity(te, text);
      }
   }

   public static String getServerTileEntityRawName(BlockPos blockPos) {
      TileEntity tileEntity = IntegratedServerUtils.getTileEntity(blockPos);
      return tileEntity == null ? null : getTileEntityRawName(tileEntity);
   }

   public static String getTileEntityRawName(TileEntity te) {
      if (te instanceof TileEntityBeacon) {
         return (String)Reflector.createObject6(te, Reflector.reflectorField64);
      } else if (te instanceof TileEntityBrewingStand) {
         return (String)Reflector.createObject6(te, Reflector.reflectorField65);
      } else if (te instanceof TileEntityEnchantmentTable) {
         return (String)Reflector.createObject6(te, Reflector.reflectorField68);
      } else if (te instanceof TileEntityFurnace) {
         return (String)Reflector.createObject6(te, Reflector.reflectorField71);
      } else {
         return te instanceof IWorldNameable worldNameable && worldNameable.hasCustomName() ? worldNameable.getName() : null;
      }
   }

   public static String getTileEntityName(TileEntity te) {
      if (!(te instanceof IWorldNameable iWorldNameable)) {
         return null;
      } else {
         updateTileEntityName(te);
         return !iWorldNameable.hasCustomName() ? null : iWorldNameable.getName();
      }
   }

   public static boolean checkTileEntity(TileEntity tileEntity2, String text2) {
      if (tileEntity2 instanceof TileEntityBeacon) {
         return Reflector.checkObject4(tileEntity2, Reflector.reflectorField64, text2);
      } else if (tileEntity2 instanceof TileEntityBrewingStand) {
         return Reflector.checkObject4(tileEntity2, Reflector.reflectorField65, text2);
      } else if (tileEntity2 instanceof TileEntityEnchantmentTable) {
         return Reflector.checkObject4(tileEntity2, Reflector.reflectorField68, text2);
      } else if (tileEntity2 instanceof TileEntityFurnace) {
         return Reflector.checkObject4(tileEntity2, Reflector.reflectorField71, text2);
      } else if (tileEntity2 instanceof TileEntityChest) {
         ((TileEntityChest)tileEntity2).setCustomName(text2);
         return true;
      } else if (tileEntity2 instanceof TileEntityDispenser) {
         ((TileEntityDispenser)tileEntity2).setCustomName(text2);
         return true;
      } else if (tileEntity2 instanceof TileEntityHopper) {
         ((TileEntityHopper)tileEntity2).setCustomName(text2);
         return true;
      } else {
         return false;
      }
   }

   public static String getTileEntityName(IBlockAccess blockAccess, BlockPos blockPos) {
      return getTileEntityName(blockAccess.getTileEntity(blockPos));
   }
}
