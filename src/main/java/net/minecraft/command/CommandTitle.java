package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase {
   private static final Logger LOGGER = LogManager.getLogger();

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.title.usage");
      }

      if (args.length < 3) {
         if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.title");
         }

         if ("times".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.times");
         }
      }

      EntityPlayerMP player2 = getPlayer(sender, args[0]);
      net.minecraft.network.play.server.S45PacketTitle.Type type = net.minecraft.network.play.server.S45PacketTitle.Type.byName(args[1]);
      if (type == net.minecraft.network.play.server.S45PacketTitle.Type.CLEAR || type == net.minecraft.network.play.server.S45PacketTitle.Type.RESET) {
         if (args.length != 2) {
            throw new WrongUsageException("commands.title.usage");
         }

         S45PacketTitle s45PacketTitle = new S45PacketTitle(type, (IChatComponent)null);
         player2.playerNetServerHandler.sendPacket(s45PacketTitle);
         notifyOperators(sender, this, "commands.title.success");
      } else if (type == net.minecraft.network.play.server.S45PacketTitle.Type.TIMES) {
         if (args.length != 5) {
            throw new WrongUsageException("commands.title.usage");
         }

         int i = parseInt(args[2]);
         int i2 = parseInt(args[3]);
         int i3 = parseInt(args[4]);
         S45PacketTitle s45PacketTitle2 = new S45PacketTitle(i, i2, i3);
         player2.playerNetServerHandler.sendPacket(s45PacketTitle2);
         notifyOperators(sender, this, "commands.title.success");
      } else {
         if (args.length < 3) {
            throw new WrongUsageException("commands.title.usage");
         }

         String text = buildString(args, 2);

         IChatComponent iChatComponent;
         try {
            iChatComponent = net.minecraft.util.IChatComponent.Serializer.jsonToComponent(text);
         } catch (JsonParseException jsonParseException) {
            Throwable rootCause = ExceptionUtils.getRootCause(jsonParseException);
            throw new SyntaxErrorException("commands.tellraw.jsonException", rootCause == null ? "" : rootCause.getMessage());
         }

         S45PacketTitle s45PacketTitle3 = new S45PacketTitle(type, ChatComponentProcessor.processComponent(sender, iChatComponent, player2));
         player2.playerNetServerHandler.sendPacket(s45PacketTitle3);
         notifyOperators(sender, this, "commands.title.success");
      }
   }

   @Override
   public String getCommandName() {
      return "title";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.title.usage";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, net.minecraft.network.play.server.S45PacketTitle.Type.getNames()) : null);
   }
}
