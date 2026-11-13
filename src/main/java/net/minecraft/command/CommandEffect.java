package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getAllUsernames())
         : (
            args.length == 2
               ? getListOfStringsMatchingLastWord(args, Potion.getPotionLocations())
               : (args.length == 5 ? getListOfStringsMatchingLastWord(args, "true", "false") : null)
         );
   }

   @Override
   public String getCommandName() {
      return "effect";
   }

   protected String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.effect.usage");
      }

      EntityLivingBase entity2 = getEntity(sender, args[0], EntityLivingBase.class);
      if (args[1].equals("clear")) {
         if (entity2.getActivePotionEffects().isEmpty()) {
            throw new CommandException("commands.effect.failure.notActive.all", entity2.getName());
         }

         entity2.clearActivePotions();
         notifyOperators(sender, this, "commands.effect.success.removed.all", entity2.getName());
      } else {
         int i;
         try {
            i = parseInt(args[1], 1);
         } catch (NumberInvalidException numberInvalidException) {
            Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(args[1]);
            if (potionFromResourceLocation == null) {
               throw numberInvalidException;
            }

            i = potionFromResourceLocation.id;
         }

         int i2 = 600;
         int i3 = 30;
         int i4 = 0;
         if (i < 0 || i >= Potion.potionTypes.length || Potion.potionTypes[i] == null) {
            throw new NumberInvalidException("commands.effect.notFound", i);
         }

         Potion potion2 = Potion.potionTypes[i];
         if (args.length >= 3) {
            i3 = parseInt(args[2], 0, 1000000);
            if (potion2.isInstant()) {
               i2 = i3;
            } else {
               i2 = i3 * 20;
            }
         } else if (potion2.isInstant()) {
            i2 = 1;
         }

         if (args.length >= 4) {
            i4 = parseInt(args[3], 0, 255);
         }

         boolean flag = true;
         if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
            flag = false;
         }

         if (i3 > 0) {
            PotionEffect potionEffect = new PotionEffect(i, i2, i4, false, flag);
            entity2.addPotionEffect(potionEffect);
            notifyOperators(sender, this, "commands.effect.success", new ChatComponentTranslation(potionEffect.getEffectName()), i, i4, entity2.getName(), i3);
         } else {
            if (!entity2.isPotionActive(i)) {
               throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(potion2.getName()), entity2.getName());
            }

            entity2.removePotionEffect(i);
            notifyOperators(sender, this, "commands.effect.success.removed", new ChatComponentTranslation(potion2.getName()), entity2.getName());
         }
      }
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.effect.usage";
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
