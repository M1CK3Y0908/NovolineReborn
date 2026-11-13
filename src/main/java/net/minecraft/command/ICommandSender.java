package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public interface ICommandSender {
   IChatComponent getDisplayName();

   Entity getCommandSenderEntity();

   boolean canCommandSenderUseCommand(int i, String text);

   String getName();

   void setCommandStat(CommandResultStats.Type type, int i);

   Vec3 getPositionVector();

   void addChatMessage(IChatComponent iChatComponent);

   World getEntityWorld();

   boolean sendCommandFeedback();

   BlockPos getPosition();
}
