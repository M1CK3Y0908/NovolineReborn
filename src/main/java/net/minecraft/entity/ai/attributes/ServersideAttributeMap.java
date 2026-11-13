package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
   private final Set<IAttributeInstance> attributeInstanceSet = Sets.newHashSet();
   protected final Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap<>();

   public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
   }

   @Override
   protected IAttributeInstance createAttributeInstance(IAttribute attribute) {
      return new ModifiableAttributeInstance(this, attribute);
   }

   public Set<IAttributeInstance> getAttributeInstanceSet() {
      return this.attributeInstanceSet;
   }

   @Override
   public void onAttributeModified(IAttributeInstance instance) {
      if (instance.getAttribute().getShouldWatch()) {
         this.attributeInstanceSet.add(instance);
      }

      for (IAttribute iAttribute : this.descendantAttributes.get(instance.getAttribute())) {
         ModifiableAttributeInstance attributeInstance2 = this.getAttributeInstance(iAttribute);
         if (attributeInstance2 != null) {
            attributeInstance2.flagForUpdate();
         }
      }
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String attributeName) {
      IAttributeInstance attributeInstance = super.getAttributeInstanceByName(attributeName);
      if (attributeInstance == null) {
         attributeInstance = this.descriptionToAttributeInstanceMap.get(attributeName);
      }

      return (ModifiableAttributeInstance)attributeInstance;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      HashSet hashSet = Sets.newHashSet();

      for (IAttributeInstance iAttributeInstance : this.getAllAttributes()) {
         if (iAttributeInstance.getAttribute().getShouldWatch()) {
            hashSet.add(iAttributeInstance);
         }
      }

      return hashSet;
   }

   @Override
   public IAttributeInstance registerAttribute(IAttribute attribute) {
      IAttributeInstance attributeInstance = super.registerAttribute(attribute);
      if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null) {
         this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), attributeInstance);
      }

      return attributeInstance;
   }
}
