package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
   private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   private boolean needsUpdate = true;
   private final BaseAttributeMap attributeMap;
   private double cachedValue;
   private final IAttribute genericAttribute;
   private double baseValue;

   @Override
   public void removeModifier(AttributeModifier modifier) {
      for (int i = 0; i < 3; i++) {
         this.mapByOperation.get(i).remove(modifier);
      }

      Set set2 = this.mapByName.get(modifier.getName());
      if (set2 != null) {
         set2.remove(modifier);
         if (set2.isEmpty()) {
            this.mapByName.remove(modifier.getName());
         }
      }

      this.mapByUUID.remove(modifier.getID());
      this.flagForUpdate();
   }

   @Override
   public void setBaseValue(double baseValue) {
      if (baseValue != this.getBaseValue()) {
         this.baseValue = baseValue;
         this.flagForUpdate();
      }
   }

   @Override
   public AttributeModifier getModifier(UUID uUID) {
      return this.mapByUUID.get(uUID);
   }

   @Override
   public double getAttributeValue() {
      if (this.needsUpdate) {
         this.cachedValue = this.computeValue();
         this.needsUpdate = false;
      }

      return this.cachedValue;
   }

   @Override
   public void removeAllModifiers() {
      Collection modifiers = this.getModifiers();
      if (modifiers != null) {
         for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>) Lists.newArrayList(modifiers)) {
            this.removeModifier(attributeModifier);
         }
      }
   }

   @Override
   public boolean hasModifier(AttributeModifier modifier) {
      return this.mapByUUID.get(modifier.getID()) != null;
   }

   @Override
   public void applyModifier(AttributeModifier modifier) {
      if (this.getModifier(modifier.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      }

      Set<AttributeModifier> set = this.mapByName.get(modifier.getName());
      if (set == null) {
         set = Sets.newHashSet();
         this.mapByName.put(modifier.getName(), set);
      }

      this.mapByOperation.get(modifier.getOperation()).add(modifier);
      set.add(modifier);
      this.mapByUUID.put(modifier.getID(), modifier);
      this.flagForUpdate();
   }

   @Override
   public IAttribute getAttribute() {
      return this.genericAttribute;
   }

   @Override
   public Collection<AttributeModifier> getModifiers() {
      Set<AttributeModifier> set = Sets.newHashSet();

      for (int index = 0; index < 3; index++) {
         set.addAll(this.getModifiersByOperation(index));
      }

      return set;
   }

   private Collection<AttributeModifier> getModifiersForOperation(int operation) {
      Set<AttributeModifier> set = Sets.newHashSet(this.getModifiersByOperation(operation));

      for (IAttribute attribute = this.genericAttribute.getParentAttribute(); attribute != null; attribute = attribute.getParentAttribute()) {
         IAttributeInstance attributeInstance = this.attributeMap.getAttributeInstance(attribute);
         if (attributeInstance != null) {
            set.addAll(attributeInstance.getModifiersByOperation(operation));
         }
      }

      return set;
   }

   @Override
   public double getBaseValue() {
      return this.baseValue;
   }

   private double computeValue() {
      double baseValue2 = this.getBaseValue();

      for (AttributeModifier attributeModifier : this.getModifiersForOperation(0)) {
         baseValue2 += attributeModifier.getAmount();
      }

      double d = baseValue2;

      for (AttributeModifier attributeModifier2 : this.getModifiersForOperation(1)) {
         d += baseValue2 * attributeModifier2.getAmount();
      }

      for (AttributeModifier attributeModifier3 : this.getModifiersForOperation(2)) {
         d *= 1.0 + attributeModifier3.getAmount();
      }

      return this.genericAttribute.clampValue(d);
   }

   protected void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.onAttributeModified(this);
   }

   public ModifiableAttributeInstance(BaseAttributeMap attributeMapIn, IAttribute genericAttributeIn) {
      this.attributeMap = attributeMapIn;
      this.genericAttribute = genericAttributeIn;
      this.baseValue = genericAttributeIn.getDefaultValue();

      for (int index = 0; index < 3; index++) {
         this.mapByOperation.put(index, Sets.newHashSet());
      }
   }

   @Override
   public Collection<AttributeModifier> getModifiersByOperation(int operation) {
      return this.mapByOperation.get(operation);
   }
}
