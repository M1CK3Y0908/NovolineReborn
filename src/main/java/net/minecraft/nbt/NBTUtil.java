package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil {
   public static boolean areNBTEquals(NBTBase expected, NBTBase actual, boolean ignoreListOrder) {
      if (expected == actual) {
         return true;
      }

      if (expected == null) {
         return true;
      }

      if (actual == null) {
         return false;
      }

      if (!expected.getClass().equals(actual.getClass())) {
         return false;
      }

      if (expected instanceof NBTTagCompound nbtTagCompound) {
         NBTTagCompound nbtTagCompound2 = (NBTTagCompound)actual;

         for (String text : nbtTagCompound.getKeySet()) {
            NBTBase tag = nbtTagCompound.getTag(text);
            if (!areNBTEquals(tag, nbtTagCompound2.getTag(text), ignoreListOrder)) {
               return false;
            }
         }

         return true;
      } else if (expected instanceof NBTTagList && ignoreListOrder) {
         NBTTagList nbtTagList = (NBTTagList)expected;
         NBTTagList nbtTagList2 = (NBTTagList)actual;
         if (nbtTagList.tagCount() == 0) {
            return nbtTagList2.tagCount() == 0;
         }

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTBase nbtBase = nbtTagList.get(i);
            boolean flag = false;

            for (int i2 = 0; i2 < nbtTagList2.tagCount(); i2++) {
               if (areNBTEquals(nbtBase, nbtTagList2.get(i2), ignoreListOrder)) {
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               return false;
            }
         }

         return true;
      } else {
         return expected.equals(actual);
      }
   }

   public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile) {
      if (!StringUtils.isNullOrEmpty(profile.getName())) {
         tagCompound.setString("Name", profile.getName());
      }

      if (profile.getId() != null) {
         tagCompound.setString("Id", profile.getId().toString());
      }

      if (!profile.getProperties().isEmpty()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();

         for (String text : profile.getProperties().keySet()) {
            NBTTagList nbtTagList = new NBTTagList();

            for (Property property : profile.getProperties().get(text)) {
               NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
               nbtTagCompound2.setString("Value", property.getValue());
               if (property.hasSignature()) {
                  nbtTagCompound2.setString("Signature", property.getSignature());
               }

               nbtTagList.appendTag(nbtTagCompound2);
            }

            nbtTagCompound.setTag(text, nbtTagList);
         }

         tagCompound.setTag("Properties", nbtTagCompound);
      }

      return tagCompound;
   }

   public static GameProfile readGameProfileFromNBT(NBTTagCompound compound) {
      String text = null;
      String text2 = null;
      if (compound.hasKey("Name", 8)) {
         text = compound.getString("Name");
      }

      if (compound.hasKey("Id", 8)) {
         text2 = compound.getString("Id");
      }

      if (StringUtils.isNullOrEmpty(text) && StringUtils.isNullOrEmpty(text2)) {
         return null;
      }

      UUID uuid;
      try {
         uuid = UUID.fromString(text2);
      } catch (Throwable throwable) {
         uuid = null;
      }

      GameProfile gameProfile = new GameProfile(uuid, text);
      if (compound.hasKey("Properties", 10)) {
         NBTTagCompound compoundTag = compound.getCompoundTag("Properties");

         for (String text3 : compoundTag.getKeySet()) {
            NBTTagList tagList = compoundTag.getTagList(text3, 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
               NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
               String string = compoundTagAt.getString("Value");
               if (compoundTagAt.hasKey("Signature", 8)) {
                  gameProfile.getProperties().put(text3, new Property(text3, string, compoundTagAt.getString("Signature")));
               } else {
                  gameProfile.getProperties().put(text3, new Property(text3, string));
               }
            }
         }
      }

      return gameProfile;
   }
}
