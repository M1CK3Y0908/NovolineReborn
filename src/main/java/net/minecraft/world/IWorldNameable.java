package net.minecraft.world;

import net.minecraft.util.IChatComponent;

public interface IWorldNameable {
   boolean hasCustomName();

   IChatComponent getDisplayName();

   String getName();
}
