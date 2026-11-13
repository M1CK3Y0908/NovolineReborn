package net.minecraft.client.resources.model;

import net.minecraft.item.ItemStack;

public interface ISmartItemModel extends IBakedModel {
   IBakedModel getIBakedModel(ItemStack itemStack);
}
