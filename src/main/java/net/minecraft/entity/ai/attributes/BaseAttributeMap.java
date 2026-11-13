package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
   protected final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap<>();
   protected final Multimap<IAttribute, IAttribute> descendantAttributes = HashMultimap.create();

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public IAttributeInstance getAttributeInstanceByName(String attributeName) {
      return this.attributesByName.get(attributeName);
   }

   protected abstract IAttributeInstance createAttributeInstance(IAttribute iAttribute);

   public IAttributeInstance registerAttribute(IAttribute attribute) {
      if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      }

      IAttributeInstance attributeInstance = this.createAttributeInstance(attribute);
      this.attributesByName.put(attribute.getAttributeUnlocalizedName(), attributeInstance);
      this.attributes.put(attribute, attributeInstance);

      for (IAttribute attribute2 = attribute.getParentAttribute(); attribute2 != null; attribute2 = attribute2.getParentAttribute()) {
         this.descendantAttributes.put(attribute2, attribute);
      }

      return attributeInstance;
   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
         if (attributeInstanceByName != null) {
            attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
            attributeInstanceByName.applyModifier((AttributeModifier)entry.getValue());
         }
      }
   }

   public IAttributeInstance getAttributeInstance(IAttribute attribute) {
      return this.attributes.get(attribute);
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance attributeInstanceByName = this.getAttributeInstanceByName((String)entry.getKey());
         if (attributeInstanceByName != null) {
            attributeInstanceByName.removeModifier((AttributeModifier)entry.getValue());
         }
      }
   }

   public void onAttributeModified(IAttributeInstance instance) {
   }
}
