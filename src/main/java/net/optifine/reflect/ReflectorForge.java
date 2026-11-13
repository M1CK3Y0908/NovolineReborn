package net.optifine.reflect;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ReflectorForge {
   public static Object object = Reflector.createObject(Reflector.reflectorField8);
   public static Object object2 = Reflector.createObject(Reflector.reflectorField7);
   public static Object object3 = Reflector.createObject(Reflector.reflectorField9);

   public static boolean checkIBlockState(IBlockState iBlockState) {
      Block block2 = iBlockState.getBlock();
      return !Reflector.reflectorMethod28.isActive() ? block2.hasTileEntity() : Reflector.checkObject(block2, Reflector.reflectorMethod28, iBlockState);
   }

   public static boolean checkItemArmor(ItemArmor itemArmor, ItemStack itemStack) {
      return itemArmor.getColor(itemStack) != -1;
   }

   public static boolean checkRenderGlobal(RenderGlobal renderGlobal, float f, int i) {
      return !Reflector.reflectorMethod70.isActive() ? false : Reflector.checkReflectorMethod(Reflector.reflectorMethod70, renderGlobal, f, i);
   }

   public static void handleString(String text, Object object4) {
      Map map = (Map)Reflector.createObject(Reflector.reflectorField14);
      if (map != null) {
         map.put(text, object4);
      }
   }

   public static boolean checkEntityLiving(EntityLiving entityLiving, World world2, float f, int i, float f2) {
      return Reflector.reflectorMethod39.isActive() ? Reflector.checkReflectorMethod(Reflector.reflectorMethod39, entityLiving, world2, f, i, f2) : false;
   }

   public static boolean checkItemStack(ItemStack itemStack) {
      return !Reflector.reflectorMethod79.isActive() ? itemStack.isItemDamaged() : Reflector.checkObject(itemStack.getItem(), Reflector.reflectorMethod79, itemStack);
   }

   public static void handleResourceLocation(ResourceLocation resourceLocation) {
      if (!Reflector.reflectorMethod11.isActive()) {
         Reflector.createObject4(Reflector.createObject2(Reflector.reflectorMethod7), Reflector.reflectorMethod11, resourceLocation);
      }
   }

   public static String[] getArray() {
      if (!Reflector.reflectorClass38.isActive()) {
         return new String[0];
      }

      List list = (List)Reflector.createObject4(Reflector.createObject2(Reflector.reflectorMethod99), Reflector.reflectorMethod98);
      if (list == null) {
         return new String[0];
      }

      ArrayList arrayList = new ArrayList();

      for (Object object4 : list) {
         if (Reflector.reflectorClass41.checkObject(object4)) {
            String string = Reflector.createString2(object4, Reflector.reflectorMethod102);
            if (string != null) {
               arrayList.add(string);
            }
         }
      }

      return (String[]) arrayList.toArray(new String[arrayList.size()]);
   }

   public static InputStream createInputStream(String text) {
      if (!Reflector.reflectorField59.isActive()) {
         return null;
      }

      Object object4 = Reflector.createObject(Reflector.reflectorField59);
      if (object4 == null) {
         return null;
      }

      if (text.startsWith("/")) {
         text = text.substring(1);
      }

      byte[] bArray = (byte[])Reflector.createObject4(object4, Reflector.reflectorMethod107, text);
      return bArray == null ? null : new ByteArrayInputStream(bArray);
   }

   public static void handleResourceLocation2(ResourceLocation resourceLocation, String text) {
      if (!Reflector.reflectorMethod10.isActive()) {
         Reflector.createObject4(Reflector.createObject2(Reflector.reflectorMethod7), Reflector.reflectorMethod10, resourceLocation, text);
      }
   }

   public static boolean checkEntityLiving2(EntityLiving entityLiving, World world2, float f, float f2, float f3) {
      Object object4 = Reflector.createObject2(Reflector.reflectorMethod38, entityLiving, world2, f, f2, f3);
      return object4 == object || object4 == object3 && entityLiving.getCanSpawnHere() && entityLiving.isNotColliding();
   }
}
