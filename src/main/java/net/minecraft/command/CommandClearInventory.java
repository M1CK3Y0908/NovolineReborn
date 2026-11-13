package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase {
   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandName() {
      return "clear";
   }

   protected String[] getPlayerNames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getPlayerNames())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      EntityPlayerMP entityPlayerMP = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
      Item item = args.length >= 2 ? getItemByText(sender, args[1]) : null;
      int count = args.length >= 3 ? parseInt(args[2], -1) : -1;
      int count2 = args.length >= 4 ? parseInt(args[3], -1) : -1;
      NBTTagCompound nBTTagCompound = null;
      if (args.length >= 5) {
         try {
            nBTTagCompound = JsonToNBT.getTagFromJson(buildString(args, 4));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.clear.tagError", nbtException.getMessage());
         }
      }

      if (args.length >= 2 && item == null) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      int i = entityPlayerMP.inventory.clearMatchingItems(item, count, count2, nBTTagCompound);
      entityPlayerMP.inventoryContainer.detectAndSendChanges();
      if (!entityPlayerMP.capabilities.isCreativeMode) {
         entityPlayerMP.updateHeldItem();
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, i);
      if (i == 0) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      if (count2 == 0) {
         sender.addChatMessage(new ChatComponentTranslation("commands.clear.testing", entityPlayerMP.getName(), i));
      } else {
         notifyOperators(sender, this, "commands.clear.success", entityPlayerMP.getName(), i);
      }
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clear.usage";
   }
}
