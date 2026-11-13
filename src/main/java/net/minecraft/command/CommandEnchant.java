package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandEnchant extends CommandBase {
   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.getRegisteredResourceLocations()) : null);
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.enchant.usage");
      }

      EntityPlayerMP player2 = getPlayer(sender, args[0]);
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

      int i;
      try {
         i = parseInt(args[1], 0);
      } catch (NumberInvalidException numberInvalidException) {
         Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(args[1]);
         if (enchantmentByLocation == null) {
            throw numberInvalidException;
         }

         i = enchantmentByLocation.effectId;
      }

      int i2 = 1;
      ItemStack currentEquippedItem = player2.getCurrentEquippedItem();
      if (currentEquippedItem == null) {
         throw new CommandException("commands.enchant.noItem");
      }

      Enchantment enchantmentById = Enchantment.getEnchantmentById(i);
      if (enchantmentById == null) {
         throw new NumberInvalidException("commands.enchant.notFound", i);
      }

      if (!enchantmentById.canApply(currentEquippedItem)) {
         throw new CommandException("commands.enchant.cantEnchant");
      }

      if (args.length >= 3) {
         i2 = parseInt(args[2], enchantmentById.getMinLevel(), enchantmentById.getMaxLevel());
      }

      if (currentEquippedItem.hasTagCompound()) {
         NBTTagList enchantmentTagList = currentEquippedItem.getEnchantmentTagList();
         if (enchantmentTagList != null) {
            for (int i3 = 0; i3 < enchantmentTagList.tagCount(); i3++) {
               short shortValue = enchantmentTagList.getCompoundTagAt(i3).getShort("id");
               if (Enchantment.getEnchantmentById(shortValue) != null) {
                  Enchantment enchantmentById2 = Enchantment.getEnchantmentById(shortValue);
                  if (!enchantmentById2.canApplyTogether(enchantmentById)) {
                     throw new CommandException(
                        "commands.enchant.cantCombine", enchantmentById.getTranslatedName(i2), enchantmentById2.getTranslatedName(enchantmentTagList.getCompoundTagAt(i3).getShort("lvl"))
                     );
                  }
               }
            }
         }
      }

      currentEquippedItem.addEnchantment(enchantmentById, i2);
      notifyOperators(sender, this, "commands.enchant.success");
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
   }

   protected String[] getListOfPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public String getCommandName() {
      return "enchant";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.enchant.usage";
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
