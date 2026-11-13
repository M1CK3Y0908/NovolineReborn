package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {
   private int getNumberOfTeams(List<Entity> entities) {
      HashSet hashSet = Sets.newHashSet();

      for (Entity entity2 : entities) {
         if (entity2 instanceof EntityPlayer) {
            hashSet.add(((EntityPlayer)entity2).getTeam());
         } else {
            hashSet.add(null);
         }
      }

      return hashSet.size();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.spreadplayers.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 6) {
         throw new WrongUsageException("commands.spreadplayers.usage");
      }

      int i = 0;
      BlockPos blockPos = sender.getPosition();
      double x2 = blockPos.getX();
      i++;
      double xCoordinate = parseDouble(x2, args[0], true);
      x2 = blockPos.getZ();
      i++;
      double zCoordinate = parseDouble(x2, args[1], true);
      i++;
      double d = parseDouble(args[2], 0.0);
      i++;
      double d2 = parseDouble(args[3], d + 1.0);
      i++;
      boolean conditionMet = parseBoolean(args[4]);
      List<Entity> list = Lists.newArrayList();

      while (i < args.length) {
         String text = args[i++];
         if (PlayerSelector.hasArguments(text)) {
            List<Entity> list2 = PlayerSelector.matchEntities(sender, text, Entity.class);
            if (list2.size() == 0) {
               throw new EntityNotFoundException();
            }

            list.addAll(list2);
         } else {
            EntityPlayer entityPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(text);
            if (entityPlayer == null) {
               throw new PlayerNotFoundException();
            }

            list.add(entityPlayer);
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, list.size());
      if (list.isEmpty()) {
         throw new EntityNotFoundException();
      }

      sender.addChatMessage(
         new ChatComponentTranslation(
            "commands.spreadplayers.spreading." + (conditionMet ? "teams" : "players"), list.size(), d2, xCoordinate, zCoordinate, d
         )
      );
      this.spread(sender, list, new CommandSpreadPlayers.Position(xCoordinate, zCoordinate), d, d2, list.get(0).worldObj, conditionMet);
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length >= 1 && args.length <= 2 ? getCoordinateXZTabCompletion(args, 0, pos) : null;
   }

   @Override
   public String getCommandName() {
      return "spreadplayers";
   }

   private void spread(
      ICommandSender sender,
      List<Entity> entities,
      CommandSpreadPlayers.Position center,
      double spreadDistance,
      double maxRange,
      World worldIn,
      boolean respectTeams
   ) throws CommandException {
      Random random2 = new Random();
      double d = center.x - maxRange;
      double d2 = center.z - maxRange;
      double d3 = center.x + maxRange;
      double d4 = center.z + maxRange;
      int i = respectTeams ? this.getNumberOfTeams(entities) : entities.size();
      double d5 = d4;
      double d6 = d3;
      double d7 = d2;
      double d8 = d;
      CommandSpreadPlayers.Position[] initialPositions = this.createInitialPositions(random2, i, d8, d7, d6, d5);
      int i2 = this.spreadPositions(center, spreadDistance, worldIn, random2, d, d2, d3, d4, initialPositions, respectTeams);
      double d9 = this.setPlayerPositions(entities, worldIn, initialPositions, respectTeams);
      notifyOperators(sender, this, "commands.spreadplayers.success." + (respectTeams ? "teams" : "players"), initialPositions.length, center.x, center.z);
      if (initialPositions.length > 1) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
            "commands.spreadplayers.info." + (respectTeams ? "teams" : "players"), String.format("%.2f", d9), i2
         );
         sender.addChatMessage(chatComponentTranslation);
      }
   }

   private int spreadPositions(
      CommandSpreadPlayers.Position center,
      double spreadDistance,
      World worldIn,
      Random random,
      double minX,
      double minZ,
      double maxX,
      double maxZ,
      CommandSpreadPlayers.Position[] positions,
      boolean respectTeams
   ) throws CommandException {
      boolean flag = true;
      double d = Float.MAX_VALUE;

      int i;
      for (i = 0; i < 10000 && flag; i++) {
         flag = false;
         d = Float.MAX_VALUE;

         for (int i2 = 0; i2 < positions.length; i2++) {
            CommandSpreadPlayers.Position position = positions[i2];
            int i3 = 0;
            CommandSpreadPlayers.Position position2 = new CommandSpreadPlayers.Position();

            for (int i4 = 0; i4 < positions.length; i4++) {
               if (i2 != i4) {
                  CommandSpreadPlayers.Position position3 = positions[i4];
                  double distance = position.getDistance(position3);
                  d = Math.min(distance, d);
                  if (distance < spreadDistance) {
                     i3++;
                     position2.x = position2.x + (position3.x - position.x);
                     position2.z = position2.z + (position3.z - position.z);
                  }
               }
            }

            if (i3 > 0) {
               position2.x /= i3;
               position2.z /= i3;
               double length2 = position2.getLength();
               if (length2 > 0.0) {
                  position2.normalize();
                  position.moveAwayFrom(position2);
               } else {
                  position.randomize(random, minX, minZ, maxX, maxZ);
               }

               flag = true;
            }

            if (position.clampToBounds(minX, minZ, maxX, maxZ)) {
               flag = true;
            }
         }

         if (!flag) {
            for (CommandSpreadPlayers.Position position4 : positions) {
               if (!position4.isSafe(worldIn)) {
                  position4.randomize(random, minX, minZ, maxX, maxZ);
                  flag = true;
               }
            }
         }
      }

      if (i >= 10000) {
         throw new CommandException(
            "commands.spreadplayers.failure." + (respectTeams ? "teams" : "players"), positions.length, center.x, center.z, String.format("%.2f", d)
         );
      } else {
         return i;
      }
   }

   private double setPlayerPositions(List<Entity> entities, World worldIn, CommandSpreadPlayers.Position[] positions, boolean respectTeams) {
      double d = 0.0;
      int i = 0;
      HashMap hashMap = Maps.newHashMap();

      for (int i2 = 0; i2 < entities.size(); i2++) {
         Entity entity2 = entities.get(i2);
         CommandSpreadPlayers.Position position;
         if (respectTeams) {
            Team team = entity2 instanceof EntityPlayer ? ((EntityPlayer)entity2).getTeam() : null;
            if (!hashMap.containsKey(team)) {
               hashMap.put(team, positions[i++]);
            }

            position = (CommandSpreadPlayers.Position)hashMap.get(team);
         } else {
            position = positions[i++];
         }

         entity2.setPositionAndUpdate(MathHelper.floor_double(position.x) + 0.5F, position.getSpawnY(worldIn), MathHelper.floor_double(position.z) + 0.5);
         double d2 = Double.MAX_VALUE;

         for (int i3 = 0; i3 < positions.length; i3++) {
            if (position != positions[i3]) {
               double distance = position.getDistance(positions[i3]);
               d2 = Math.min(distance, d2);
            }
         }

         d += d2;
      }

      return d / entities.size();
   }

   private CommandSpreadPlayers.Position[] createInitialPositions(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
      CommandSpreadPlayers.Position[] acommandspreadplayers$position = new CommandSpreadPlayers.Position[count];

      for (int index = 0; index < acommandspreadplayers$position.length; index++) {
         CommandSpreadPlayers.Position position = new CommandSpreadPlayers.Position();
         position.randomize(random, minX, minZ, maxX, maxZ);
         acommandspreadplayers$position[index] = position;
      }

      return acommandspreadplayers$position;
   }

   static class Position {
      double x;
      double z;

      public int getSpawnY(World worldIn) {
         BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            if (worldIn.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
               return blockPos.getY() + 1;
            }
         }

         return 257;
      }

      Position(double x, double z) {
         this.x = x;
         this.z = z;
      }

      void normalize() {
         double length2 = this.getLength();
         this.x /= length2;
         this.z /= length2;
      }

      public void moveAwayFrom(CommandSpreadPlayers.Position other) {
         this.x = this.x - other.x;
         this.z = this.z - other.z;
      }

      public boolean clampToBounds(double minX, double minZ, double maxX, double maxZ) {
         boolean conditionMet = false;
         if (this.x < minX) {
            this.x = minX;
            conditionMet = true;
         } else if (this.x > maxX) {
            this.x = maxX;
            conditionMet = true;
         }

         if (this.z < minZ) {
            this.z = minZ;
            conditionMet = true;
         } else if (this.z > maxZ) {
            this.z = maxZ;
            conditionMet = true;
         }

         return conditionMet;
      }

      public void randomize(Random random, double minX, double minZ, double maxX, double maxZ) {
         this.x = MathHelper.getRandomDoubleInRange(random, minX, maxX);
         this.z = MathHelper.getRandomDoubleInRange(random, minZ, maxZ);
      }

      Position() {
      }

      public boolean isSafe(World worldIn) {
         BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
            if (material != Material.air) {
               return !material.isLiquid() && material != Material.fire;
            }
         }

         return false;
      }

      float getLength() {
         return MathHelper.sqrt_double(this.x * this.x + this.z * this.z);
      }

      double getDistance(CommandSpreadPlayers.Position other) {
         double d = this.x - other.x;
         double d2 = this.z - other.z;
         return Math.sqrt(d * d + d2 * d2);
      }
   }
}
