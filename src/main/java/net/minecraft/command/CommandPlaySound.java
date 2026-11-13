package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CommandPlaySound extends CommandBase {
   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 2
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length > 2 && args.length <= 5 ? getCoordinateTabCompletion(args, 2, pos) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException(this.getCommandUsage(sender));
      }

      int i = 0;
      i++;
      String text = args[0];
      i++;
      EntityPlayerMP entityPlayerMP = getPlayer(sender, args[1]);
      Vec3 vec3 = sender.getPositionVector();
      double xCoord2 = vec3.xCoord;
      if (args.length > 2) {
         i++;
         xCoord2 = parseDouble(xCoord2, args[2], true);
      }

      double yCoord2 = vec3.yCoord;
      if (args.length > i) {
         yCoord2 = parseDouble(yCoord2, args[i++], 0, 0, false);
      }

      double zCoord2 = vec3.zCoord;
      if (args.length > i) {
         zCoord2 = parseDouble(zCoord2, args[i++], true);
      }

      double d = 1.0;
      if (args.length > i) {
         d = parseDouble(args[i++], 0.0, Float.MAX_VALUE);
      }

      double d2 = 1.0;
      if (args.length > i) {
         d2 = parseDouble(args[i++], 0.0, 2.0);
      }

      double d3 = 0.0;
      if (args.length > i) {
         d3 = parseDouble(args[i], 0.0, 1.0);
      }

      double d4 = d > 1.0 ? d * 16.0 : 16.0;
      double distance = entityPlayerMP.getDistance(xCoord2, yCoord2, zCoord2);
      if (distance > d4) {
         if (d3 <= 0.0) {
            throw new CommandException("commands.playsound.playerTooFar", entityPlayerMP.getName());
         }

         double xCoordinate = xCoord2 - entityPlayerMP.posX;
         double yCoordinate = yCoord2 - entityPlayerMP.posY;
         double zCoordinate = zCoord2 - entityPlayerMP.posZ;
         double d5 = Math.sqrt(xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate);
         if (d5 > 0.0) {
            xCoord2 = entityPlayerMP.posX + xCoordinate / d5 * 2.0;
            yCoord2 = entityPlayerMP.posY + yCoordinate / d5 * 2.0;
            zCoord2 = entityPlayerMP.posZ + zCoordinate / d5 * 2.0;
         }

         d = d3;
      }

      entityPlayerMP.playerNetServerHandler
         .sendPacket(new S29PacketSoundEffect(text, xCoord2, yCoord2, zCoord2, (float)d, (float)d2));
      notifyOperators(sender, this, "commands.playsound.success", text, entityPlayerMP.getName());
   }

   @Override
   public String getCommandName() {
      return "playsound";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.playsound.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }
}
