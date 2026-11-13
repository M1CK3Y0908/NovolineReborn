package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses {
   List<Entity> seenEntities = Lists.newArrayList();
   List<Entity> unseenEntities = Lists.newArrayList();
   EntityLiving entityObj;

   public void clearSensingCache() {
      this.seenEntities.clear();
      this.unseenEntities.clear();
   }

   public EntitySenses(EntityLiving entityObjIn) {
      this.entityObj = entityObjIn;
   }

   public boolean canSee(Entity entityIn) {
      if (this.seenEntities.contains(entityIn)) {
         return true;
      }

      if (this.unseenEntities.contains(entityIn)) {
         return false;
      }

      this.entityObj.worldObj.theProfiler.startSection("canSee");
      boolean canEntityBeSeen = this.entityObj.canEntityBeSeen(entityIn);
      this.entityObj.worldObj.theProfiler.endSection();
      if (canEntityBeSeen) {
         this.seenEntities.add(entityIn);
      } else {
         this.unseenEntities.add(entityIn);
      }

      return canEntityBeSeen;
   }
}
