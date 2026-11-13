package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase {
   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.summon.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.summon.usage");
      }

      String text = args[0];
      BlockPos position = sender.getPosition();
      Vec3 positionVector = sender.getPositionVector();
      double xCoord2 = positionVector.xCoord;
      double yCoord2 = positionVector.yCoord;
      double zCoord2 = positionVector.zCoord;
      if (args.length >= 4) {
         xCoord2 = parseDouble(xCoord2, args[1], true);
         yCoord2 = parseDouble(yCoord2, args[2], false);
         zCoord2 = parseDouble(zCoord2, args[3], true);
         position = new BlockPos(xCoord2, yCoord2, zCoord2);
      }

      World entityWorld = sender.getEntityWorld();
      if (!entityWorld.isBlockLoaded(position)) {
         throw new CommandException("commands.summon.outOfWorld");
      }

      if ("LightningBolt".equals(text)) {
         entityWorld.addWeatherEffect(new EntityLightningBolt(entityWorld, xCoord2, yCoord2, zCoord2));
         notifyOperators(sender, this, "commands.summon.success");
      } else {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         boolean flag = false;
         if (args.length >= 5) {
            IChatComponent chatComponentFromNthArg = getChatComponentFromNthArg(sender, args, 4);

            try {
               nbtTagCompound = JsonToNBT.getTagFromJson(chatComponentFromNthArg.getUnformattedText());
               flag = true;
            } catch (NBTException nbtException) {
               throw new CommandException("commands.summon.tagError", nbtException.getMessage());
            }
         }

         nbtTagCompound.setString("id", text);

         Entity entity2;
         try {
            entity2 = EntityList.createEntityFromNBT(nbtTagCompound, entityWorld);
         } catch (RuntimeException runtimeException) {
            throw new CommandException("commands.summon.failed");
         }

         if (entity2 == null) {
            throw new CommandException("commands.summon.failed");
         }

         entity2.setLocationAndAngles(xCoord2, yCoord2, zCoord2, entity2.rotationYaw, entity2.rotationPitch);
         if (!flag && entity2 instanceof EntityLiving) {
            ((EntityLiving)entity2).onInitialSpawn(entityWorld.getDifficultyForLocation(new BlockPos(entity2)), (IEntityLivingData)null);
         }

         entityWorld.spawnEntityInWorld(entity2);
         Entity entity3 = entity2;

         for (NBTTagCompound nbtTagCompound2 = nbtTagCompound; entity3 != null && nbtTagCompound2.hasKey("Riding", 10); nbtTagCompound2 = nbtTagCompound2.getCompoundTag("Riding")) {
            Entity entityFromNBT = EntityList.createEntityFromNBT(nbtTagCompound2.getCompoundTag("Riding"), entityWorld);
            if (entityFromNBT != null) {
               entityFromNBT.setLocationAndAngles(xCoord2, yCoord2, zCoord2, entityFromNBT.rotationYaw, entityFromNBT.rotationPitch);
               entityWorld.spawnEntityInWorld(entityFromNBT);
               entity3.mountEntity(entityFromNBT);
            }

            entity3 = entityFromNBT;
         }

         notifyOperators(sender, this, "commands.summon.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList())
         : (args.length > 1 && args.length <= 4 ? getCoordinateTabCompletion(args, 1, pos) : null);
   }

   @Override
   public String getCommandName() {
      return "summon";
   }
}
