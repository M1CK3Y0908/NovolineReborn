package net.minecraft.command.server;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CommandTeleport extends CommandBase {
   @Override
   public String getCommandName() {
      return "tp";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.tp.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.tp.usage");
      }

      byte b = 0;
      Entity entity2;
      if (args.length != 2 && args.length != 4 && args.length != 6) {
         entity2 = getCommandSenderAsPlayer(sender);
      } else {
         entity2 = getEntity(sender, args[0]);
         b = 1;
      }

      if (args.length != 1 && args.length != 2) {
         if (args.length < b + 3) {
            throw new WrongUsageException("commands.tp.usage");
         }

         if (entity2.worldObj != null) {
            int i = b + 1;
            net.minecraft.command.CommandBase.CoordinateArg coordinateArg = parseCoordinate(entity2.posX, args[b], true);
            net.minecraft.command.CommandBase.CoordinateArg coordinateArg2 = parseCoordinate(entity2.posY, args[i++], 0, 0, false);
            net.minecraft.command.CommandBase.CoordinateArg coordinateArg3 = parseCoordinate(entity2.posZ, args[i++], true);
            net.minecraft.command.CommandBase.CoordinateArg coordinateArg4 = parseCoordinate(entity2.rotationYaw, args.length > i ? args[i++] : "~", false);
            net.minecraft.command.CommandBase.CoordinateArg coordinateArg5 = parseCoordinate(entity2.rotationPitch, args.length > i ? args[i] : "~", false);
            if (entity2 instanceof EntityPlayerMP) {
               EnumSet enumSet = EnumSet.noneOf(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.class);
               if (coordinateArg.isRelative()) {
                  enumSet.add(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X);
               }

               if (coordinateArg2.isRelative()) {
                  enumSet.add(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y);
               }

               if (coordinateArg3.isRelative()) {
                  enumSet.add(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Z);
               }

               if (coordinateArg5.isRelative()) {
                  enumSet.add(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X_ROT);
               }

               if (coordinateArg4.isRelative()) {
                  enumSet.add(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y_ROT);
               }

               float f = (float)coordinateArg4.getAmount();
               if (!coordinateArg4.isRelative()) {
                  f = MathHelper.wrapAngleTo180_float(f);
               }

               float f2 = (float)coordinateArg5.getAmount();
               if (!coordinateArg5.isRelative()) {
                  f2 = MathHelper.wrapAngleTo180_float(f2);
               }

               if (f2 > 90.0F || f2 < -90.0F) {
                  f2 = MathHelper.wrapAngleTo180_float(180.0F - f2);
                  f = MathHelper.wrapAngleTo180_float(f + 180.0F);
               }

               entity2.mountEntity((Entity)null);
               ((EntityPlayerMP)entity2).playerNetServerHandler.setPlayerLocation(coordinateArg.getAmount(), coordinateArg2.getAmount(), coordinateArg3.getAmount(), f, f2, enumSet);
               entity2.setRotationYawHead(f);
            } else {
               float f3 = (float)MathHelper.wrapAngleTo180_double(coordinateArg4.getResult());
               float f4 = (float)MathHelper.wrapAngleTo180_double(coordinateArg5.getResult());
               if (f4 > 90.0F || f4 < -90.0F) {
                  f4 = MathHelper.wrapAngleTo180_float(180.0F - f4);
                  f3 = MathHelper.wrapAngleTo180_float(f3 + 180.0F);
               }

               entity2.setLocationAndAngles(coordinateArg.getResult(), coordinateArg2.getResult(), coordinateArg3.getResult(), f3, f4);
               entity2.setRotationYawHead(f3);
            }

            notifyOperators(sender, this, "commands.tp.success.coordinates", entity2.getName(), coordinateArg.getResult(), coordinateArg2.getResult(), coordinateArg3.getResult());
         }
      } else {
         Entity entity3 = getEntity(sender, args[args.length - 1]);
         if (entity3.worldObj != entity2.worldObj) {
            throw new CommandException("commands.tp.notSameDimension");
         }

         entity2.mountEntity((Entity)null);
         if (entity2 instanceof EntityPlayerMP) {
            ((EntityPlayerMP)entity2).playerNetServerHandler.setPlayerLocation(entity3.posX, entity3.posY, entity3.posZ, entity3.rotationYaw, entity3.rotationPitch);
         } else {
            entity2.setLocationAndAngles(entity3.posX, entity3.posY, entity3.posZ, entity3.rotationYaw, entity3.rotationPitch);
         }

         notifyOperators(sender, this, "commands.tp.success", entity2.getName(), entity3.getName());
      }
   }
}
