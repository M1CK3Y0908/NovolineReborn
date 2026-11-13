package net.minecraft.client.renderer.entity;

import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderPotion extends RenderSnowball<EntityPotion> {
   public ItemStack getStackToRender(EntityPotion entityIn) {
      return new ItemStack(this.item, 1, entityIn.getPotionDamage());
   }

   public RenderPotion(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn, Items.potionitem, itemRendererIn);
   }
}
