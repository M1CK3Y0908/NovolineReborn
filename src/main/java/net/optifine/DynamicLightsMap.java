package net.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicLightsMap {
   private Map<Integer, DynamicLight> map = new HashMap<>();
   private List<DynamicLight> list = new ArrayList<>();
   private boolean dirty = false;

   public DynamicLight remove(int id) {
      DynamicLight dynamicLight = this.map.remove(id);
      if (dynamicLight != null) {
         this.setDirty();
      }

      return dynamicLight;
   }

   public int size() {
      return this.map.size();
   }

   private void setDirty() {
      this.dirty = true;
   }

   public DynamicLight put(int id, DynamicLight dynamicLight) {
      DynamicLight dynamicLight2 = this.map.put(id, dynamicLight);
      this.setDirty();
      return dynamicLight2;
   }

   public DynamicLight get(int id) {
      return this.map.get(id);
   }

   public List<DynamicLight> valueList() {
      if (this.dirty) {
         this.list.clear();
         this.list.addAll(this.map.values());
         this.dirty = false;
      }

      return this.list;
   }

   public void clear() {
      this.map.clear();
      this.list.clear();
      this.setDirty();
   }
}
