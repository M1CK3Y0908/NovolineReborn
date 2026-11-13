package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase {
   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 8) {
         throw new WrongUsageException("commands.particle.usage");
      }

      boolean flag = false;
      EnumParticleTypes enumParticleTypes = null;

      for (EnumParticleTypes enumParticleTypes2 : EnumParticleTypes.values()) {
         if (enumParticleTypes2.hasArguments()) {
            if (args[0].startsWith(enumParticleTypes2.getParticleName())) {
               flag = true;
               enumParticleTypes = enumParticleTypes2;
               break;
            }
         } else if (args[0].equals(enumParticleTypes2.getParticleName())) {
            flag = true;
            enumParticleTypes = enumParticleTypes2;
            break;
         }
      }

      if (!flag) {
         throw new CommandException("commands.particle.notFound", args[0]);
      }

      String text = args[0];
      Vec3 positionVector = sender.getPositionVector();
      double d = (float)parseDouble(positionVector.xCoord, args[1], true);
      double d2 = (float)parseDouble(positionVector.yCoord, args[2], true);
      double d3 = (float)parseDouble(positionVector.zCoord, args[3], true);
      double d4 = (float)parseDouble(args[4]);
      double d5 = (float)parseDouble(args[5]);
      double d6 = (float)parseDouble(args[6]);
      double d7 = (float)parseDouble(args[7]);
      int i = 0;
      if (args.length > 8) {
         i = parseInt(args[8], 0);
      }

      boolean flag2 = false;
      if (args.length > 9 && "force".equals(args[9])) {
         flag2 = true;
      }

      if (sender.getEntityWorld() instanceof WorldServer worldServer) {
         int[] iArray = new int[enumParticleTypes.getArgumentCount()];
         if (enumParticleTypes.hasArguments()) {
            String[] texts = args[0].split("_", 3);

            for (int i2 = 1; i2 < texts.length; i2++) {
               try {
                  iArray[i2 - 1] = Integer.parseInt(texts[i2]);
               } catch (NumberFormatException numberFormatException) {
                  throw new CommandException("commands.particle.notFound", args[0]);
               }
            }
         }

         worldServer.spawnParticle(enumParticleTypes, flag2, d, d2, d3, i, d4, d5, d6, d7, iArray);
         notifyOperators(sender, this, "commands.particle.success", text, Math.max(i, 1));
      }
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.particle.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandName() {
      return "particle";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames())
         : (
            args.length > 1 && args.length <= 4
               ? getCoordinateTabCompletion(args, 1, pos)
               : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "normal", "force") : null)
         );
   }
}
