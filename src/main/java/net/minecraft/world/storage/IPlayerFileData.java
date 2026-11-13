package net.minecraft.world.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IPlayerFileData {
   String[] getAvailablePlayerDat();

   NBTTagCompound readPlayerData(EntityPlayer entityPlayer);

   void writePlayerData(EntityPlayer entityPlayer);
}
