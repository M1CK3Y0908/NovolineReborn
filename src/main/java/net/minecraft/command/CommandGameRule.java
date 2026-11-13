package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
   private GameRules getGameRules() {
      return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
      } else {
         return args.length == 2 && this.getGameRules().areSameType(args[0], net.minecraft.world.GameRules.ValueType.BOOLEAN_VALUE)
            ? getListOfStringsMatchingLastWord(args, "true", "false")
            : null;
      }
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.gamerule.usage";
   }

   @Override
   public String getCommandName() {
      return "gamerule";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      GameRules gameRules = this.getGameRules();
      String text = args.length > 0 ? args[0] : "";
      String text2 = args.length > 1 ? buildString(args, 1) : "";
      switch (args.length) {
         case 0:
            sender.addChatMessage(new ChatComponentText(joinNiceString(gameRules.getRules())));
            break;
         case 1:
            if (!gameRules.hasRule(text)) {
               throw new CommandException("commands.gamerule.norule", text);
            }

            String text3 = gameRules.getString(text);
            sender.addChatMessage(new ChatComponentText(text).appendText(" = ").appendText(text3));
            sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameRules.getInt(text));
            break;
         default:
            if (gameRules.areSameType(text, net.minecraft.world.GameRules.ValueType.BOOLEAN_VALUE) && !"true".equals(text2) && !"false".equals(text2)) {
               throw new CommandException("commands.generic.boolean.invalid", text2);
            }

            gameRules.setOrCreateGameRule(text, text2);
            notifyGameRuleChange(gameRules, text);
            notifyOperators(sender, this, "commands.gamerule.success");
      }
   }

   public static void notifyGameRuleChange(GameRules rules, String gameRuleKey) {
      if ("reducedDebugInfo".equals(gameRuleKey)) {
         byte b = (byte)(rules.getBoolean(gameRuleKey) ? 22 : 23);

         for (EntityPlayerMP entityPlayerMP : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            entityPlayerMP.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(entityPlayerMP, b));
         }
      }
   }
}
