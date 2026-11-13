package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt extends CommandBase {
   @Override
   public String getCommandName() {
      return "execute";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (
            args.length > 1 && args.length <= 4
               ? getCoordinateTabCompletion(args, 1, pos)
               : (
                  args.length > 5 && args.length <= 8 && "detect".equals(args[4])
                     ? getCoordinateTabCompletion(args, 5, pos)
                     : (args.length == 9 && "detect".equals(args[4]) ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)
               )
         );
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.execute.usage";
   }

   @Override
   public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 5) {
         throw new WrongUsageException("commands.execute.usage");
      }

      final Entity entity2 = getEntity(sender, args[0], Entity.class);
      final double d = parseDouble(entity2.posX, args[1], false);
      final double d2 = parseDouble(entity2.posY, args[2], false);
      final double d3 = parseDouble(entity2.posZ, args[3], false);
      final BlockPos blockPos = new BlockPos(d, d2, d3);
      byte b = 4;
      if ("detect".equals(args[4]) && args.length > 10) {
         World entityWorld = entity2.getEntityWorld();
         double d4 = parseDouble(d, args[5], false);
         double d5 = parseDouble(d2, args[6], false);
         double d6 = parseDouble(d3, args[7], false);
         Block blockByText = getBlockByText(sender, args[8]);
         int i = parseInt(args[9], -1, 15);
         BlockPos blockPos2 = new BlockPos(d4, d5, d6);
         IBlockState blockState = entityWorld.getBlockState(blockPos2);
         if (blockState.getBlock() != blockByText || i >= 0 && blockState.getBlock().getMetaFromState(blockState) != i) {
            throw new CommandException("commands.execute.failed", "detect", entity2.getName());
         }

         b = 10;
      }

      String text = buildString(args, b);
      ICommandSender iCommandSender = new ICommandSender() {
         @Override
         public void addChatMessage(IChatComponent component) {
            sender.addChatMessage(component);
         }

         @Override
         public BlockPos getPosition() {
            return blockPos;
         }

         @Override
         public boolean sendCommandFeedback() {
            MinecraftServer minecraftServer = MinecraftServer.getServer();
            return minecraftServer == null || minecraftServer.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
         }

         @Override
         public Entity getCommandSenderEntity() {
            return entity2;
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return sender.canCommandSenderUseCommand(permLevel, commandName);
         }

         @Override
         public IChatComponent getDisplayName() {
            return entity2.getDisplayName();
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(d, d2, d3);
         }

         @Override
         public String getName() {
            return entity2.getName();
         }

         @Override
         public void setCommandStat(Type type, int amount) {
            entity2.setCommandStat(type, amount);
         }

         @Override
         public World getEntityWorld() {
            return entity2.worldObj;
         }
      };
      ICommandManager commandManager = MinecraftServer.getServer().getCommandManager();

      try {
         int i2 = commandManager.executeCommand(iCommandSender, text);
         if (i2 < 1) {
            throw new CommandException("commands.execute.allInvocationsFailed", text);
         }
      } catch (Throwable throwable) {
         throw new CommandException("commands.execute.failed", text, entity2.getName());
      }
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
