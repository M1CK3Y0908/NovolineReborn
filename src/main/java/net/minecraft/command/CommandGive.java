package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandGive extends CommandBase {
   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.give.usage";
   }

   protected String[] getPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.give.usage");
      }

      EntityPlayer entityPlayer = getPlayer(sender, args[0]);
      Item item = getItemByText(sender, args[1]);
      int count = args.length >= 3 ? parseInt(args[2], 1, 64) : 1;
      int count2 = args.length >= 4 ? parseInt(args[3]) : 0;
      ItemStack itemStack = new ItemStack(item, count, count2);
      if (args.length >= 5) {
         String text = getChatComponentFromNthArg(sender, args, 4).getUnformattedText();

         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(text));
         } catch (NBTException nbtException) {
            throw new CommandException("commands.give.tagError", nbtException.getMessage());
         }
      }

      boolean conditionMet = entityPlayer.inventory.addItemStackToInventory(itemStack);
      if (conditionMet) {
         entityPlayer.worldObj
            .playSoundAtEntity(entityPlayer, "random.pop", 0.2F, ((entityPlayer.getRNG().nextFloat() - entityPlayer.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         entityPlayer.inventoryContainer.detectAndSendChanges();
      }

      if (conditionMet && itemStack.stackSize <= 0) {
         itemStack.stackSize = 1;
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);
         EntityItem entityItem = entityPlayer.getEntityItem3(itemStack);
         if (entityItem != null) {
            entityItem.makeFakeItem();
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count - itemStack.stackSize);
         EntityItem entityItem2 = entityPlayer.getEntityItem3(itemStack);
         if (entityItem2 != null) {
            entityItem2.setNoPickupDelay();
            entityItem2.setOwner(entityPlayer.getName());
         }
      }

      notifyOperators(sender, this, "commands.give.success", itemStack.getChatComponent(), count, entityPlayer.getName());
   }

   @Override
   public String getCommandName() {
      return "give";
   }
}
