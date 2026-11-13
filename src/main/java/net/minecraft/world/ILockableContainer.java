package net.minecraft.world;

import net.minecraft.inventory.IInventory;

public interface ILockableContainer extends IInventory, IInteractionObject {
   LockCode getLockCode();

   boolean isLocked();

   void setLockCode(LockCode lockCode);
}
