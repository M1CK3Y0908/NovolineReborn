package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger logger = LogManager.getLogger();
   public static final IAttribute maxHealth = new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0, 0.0, 1024.0)
      .setDescription("Max Health")
      .getBaseAttribute();
   public static final IAttribute followRange = new RangedAttribute((IAttribute)null, "generic.followRange", 32.0, 0.0, 2048.0).setDescription("Follow Range");
   public static final IAttribute knockbackResistance = new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
      .setDescription("Knockback Resistance");
   public static final IAttribute movementSpeed = new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.7F, 0.0, 1024.0)
      .setDescription("Movement Speed")
      .getBaseAttribute();
   public static final IAttribute attackDamage = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0, 0.0, 2048.0);

   public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound compound) {
      UUID uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));

      try {
         return new AttributeModifier(uuid, compound.getString("Name"), compound.getDouble("Amount"), compound.getInteger("Operation"));
      } catch (Exception exception) {
         logger.warn("Unable to create attribute: " + exception.getMessage());
         return null;
      }
   }

   private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance instance) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      IAttribute attribute2 = instance.getAttribute();
      nbtTagCompound.setString("Name", attribute2.getAttributeUnlocalizedName());
      nbtTagCompound.setDouble("Base", instance.getBaseValue());
      Collection modifiers = instance.getModifiers();
      if (modifiers != null && !modifiers.isEmpty()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (AttributeModifier attributeModifier3 : (Iterable<AttributeModifier>) modifiers) {
            if (attributeModifier3.isSaved()) {
               nbtTagList.appendTag(writeAttributeModifierToNBT(attributeModifier3));
            }
         }

         nbtTagCompound.setTag("Modifiers", nbtTagList);
      }

      return nbtTagCompound;
   }

   private static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier modifier) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setString("Name", modifier.getName());
      nbtTagCompound.setDouble("Amount", modifier.getAmount());
      nbtTagCompound.setInteger("Operation", modifier.getOperation());
      nbtTagCompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
      nbtTagCompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
      return nbtTagCompound;
   }

   public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap map) {
      NBTTagList nbtTagList = new NBTTagList();

      for (IAttributeInstance iAttributeInstance : map.getAllAttributes()) {
         nbtTagList.appendTag(writeAttributeInstanceToNBT(iAttributeInstance));
      }

      return nbtTagList;
   }

   private static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound) {
      instance.setBaseValue(compound.getDouble("Base"));
      if (compound.hasKey("Modifiers", 9)) {
         NBTTagList nBTTagList = compound.getTagList("Modifiers", 10);

         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            AttributeModifier attributeModifier = readAttributeModifierFromNBT(nBTTagList.getCompoundTagAt(index));
            if (attributeModifier != null) {
               AttributeModifier attributeModifier2 = instance.getModifier(attributeModifier.getID());
               if (attributeModifier2 != null) {
                  instance.removeModifier(attributeModifier2);
               }

               instance.applyModifier(attributeModifier);
            }
         }
      }
   }

   public static void setAttributeModifiers(BaseAttributeMap map, NBTTagList list) {
      for (int index = 0; index < list.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = list.getCompoundTagAt(index);
         IAttributeInstance attributeInstance = map.getAttributeInstanceByName(nBTTagCompound.getString("Name"));
         if (attributeInstance != null) {
            applyModifiersToAttributeInstance(attributeInstance, nBTTagCompound);
         } else {
            logger.warn("Ignoring unknown attribute '" + nBTTagCompound.getString("Name") + "'");
         }
      }
   }
}
