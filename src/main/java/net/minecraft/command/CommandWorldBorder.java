package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class CommandWorldBorder extends CommandBase {
   protected WorldBorder getWorldBorder() {
      return MinecraftServer.getServer().worldServers[0].getWorldBorder();
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.worldborder.usage");
      }

      WorldBorder worldBorder = this.getWorldBorder();
      if (args[0].equals("set")) {
         if (args.length != 2 && args.length != 3) {
            throw new WrongUsageException("commands.worldborder.set.usage");
         }

         double targetSize = worldBorder.getTargetSize();
         double d = parseDouble(args[1], 1.0, 6.0E7);
         long l = args.length > 2 ? createLong(args[2]) * 1000L : 0L;
         if (l > 0L) {
            worldBorder.setTransition(targetSize, d, l);
            if (targetSize > d) {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", d),
                  String.format("%.1f", targetSize),
                  Long.toString(l / 1000L)
               );
            } else {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", d),
                  String.format("%.1f", targetSize),
                  Long.toString(l / 1000L)
               );
            }
         } else {
            worldBorder.setTransition(d);
            notifyOperators(sender, this, "commands.worldborder.set.success", String.format("%.1f", d), String.format("%.1f", targetSize));
         }
      } else if (args[0].equals("add")) {
         if (args.length != 2 && args.length != 3) {
            throw new WrongUsageException("commands.worldborder.add.usage");
         }

         double diameter = worldBorder.getDiameter();
         double d2 = diameter + parseDouble(args[1], -diameter, 6.0E7 - diameter);
         long l2 = worldBorder.getTimeUntilTarget() + (args.length > 2 ? createLong(args[2]) * 1000L : 0L);
         if (l2 > 0L) {
            worldBorder.setTransition(diameter, d2, l2);
            if (diameter > d2) {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.shrink.success",
                  String.format("%.1f", d2),
                  String.format("%.1f", diameter),
                  Long.toString(l2 / 1000L)
               );
            } else {
               notifyOperators(
                  sender,
                  this,
                  "commands.worldborder.setSlowly.grow.success",
                  String.format("%.1f", d2),
                  String.format("%.1f", diameter),
                  Long.toString(l2 / 1000L)
               );
            }
         } else {
            worldBorder.setTransition(d2);
            notifyOperators(sender, this, "commands.worldborder.set.success", String.format("%.1f", d2), String.format("%.1f", diameter));
         }
      } else if (args[0].equals("center")) {
         if (args.length != 3) {
            throw new WrongUsageException("commands.worldborder.center.usage");
         }

         BlockPos blockPos = sender.getPosition();
         double xCoordinate = parseDouble(blockPos.getX() + 0.5, args[1], true);
         double zCoordinate = parseDouble(blockPos.getZ() + 0.5, args[2], true);
         worldBorder.setCenter(xCoordinate, zCoordinate);
         notifyOperators(sender, this, "commands.worldborder.center.success", xCoordinate, zCoordinate);
      } else if (args[0].equals("damage")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.worldborder.damage.usage");
         }

         if (args[1].equals("buffer")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.buffer.usage");
            }

            double d3 = parseDouble(args[2], 0.0);
            double damageBuffer = worldBorder.getDamageBuffer();
            worldBorder.setDamageBuffer(d3);
            notifyOperators(
               sender, this, "commands.worldborder.damage.buffer.success", String.format("%.1f", d3), String.format("%.1f", damageBuffer)
            );
         } else if (args[1].equals("amount")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.damage.amount.usage");
            }

            double d4 = parseDouble(args[2], 0.0);
            double damageAmount = worldBorder.getDamageAmount();
            worldBorder.setDamageAmount(d4);
            notifyOperators(
               sender, this, "commands.worldborder.damage.amount.success", String.format("%.2f", d4), String.format("%.2f", damageAmount)
            );
         }
      } else if (args[0].equals("warning")) {
         if (args.length < 2) {
            throw new WrongUsageException("commands.worldborder.warning.usage");
         }

         int i = parseInt(args[2], 0);
         if (args[1].equals("time")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.time.usage");
            }

            int warningTime = worldBorder.getWarningTime();
            worldBorder.setWarningTime(i);
            notifyOperators(sender, this, "commands.worldborder.warning.time.success", i, warningTime);
         } else if (args[1].equals("distance")) {
            if (args.length != 3) {
               throw new WrongUsageException("commands.worldborder.warning.distance.usage");
            }

            int warningDistance = worldBorder.getWarningDistance();
            worldBorder.setWarningDistance(i);
            notifyOperators(sender, this, "commands.worldborder.warning.distance.success", i, warningDistance);
         }
      } else {
         if (!args[0].equals("get")) {
            throw new WrongUsageException("commands.worldborder.usage");
         }

         double diameter2 = worldBorder.getDiameter();
         sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, MathHelper.floor_double(diameter2 + 0.5));
         sender.addChatMessage(new ChatComponentTranslation("commands.worldborder.get.success", String.format("%.0f", diameter2)));
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "set", "center", "damage", "warning", "add", "get")
         : (
            args.length == 2 && args[0].equals("damage")
               ? getListOfStringsMatchingLastWord(args, "buffer", "amount")
               : (
                  args.length >= 2 && args.length <= 3 && args[0].equals("center")
                     ? getCoordinateXZTabCompletion(args, 1, pos)
                     : (args.length == 2 && args[0].equals("warning") ? getListOfStringsMatchingLastWord(args, "time", "distance") : null)
               )
         );
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.worldborder.usage";
   }

   @Override
   public String getCommandName() {
      return "worldborder";
   }
}
