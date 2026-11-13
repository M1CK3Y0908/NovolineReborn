package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase {
   private int profileStartTick;
   private long profileStartTime;
   private static final Logger logger = LogManager.getLogger();

   private void saveProfileResults(long timeSpan, int tickSpan) {
      File file = new File(
         MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
      );
      file.getParentFile().mkdirs();

      try {
         FileWriter fileWriter = new FileWriter(file);
         fileWriter.write(this.getProfileResults(timeSpan, tickSpan));
         fileWriter.close();
      } catch (Throwable throwable) {
         logger.error("Could not save profiler results to " + file, throwable);
      }
   }

   private String getProfileResults(long timeSpan, int tickSpan) {
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append("---- Minecraft Profiler Results ----\n");
      stringBuilder2.append("// ");
      stringBuilder2.append(getWittyComment());
      stringBuilder2.append("\n\n");
      stringBuilder2.append("Time span: ").append(timeSpan).append(" ms\n");
      stringBuilder2.append("Tick span: ").append(tickSpan).append(" ticks\n");
      stringBuilder2.append("// This is approximately ")
         .append(String.format("%.2f", tickSpan / ((float)timeSpan / 1000.0F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      stringBuilder2.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.appendProfilerSection(0, "root", stringBuilder2);
      stringBuilder2.append("--- END PROFILE DUMP ---\n\n");
      return stringBuilder2.toString();
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   private void appendProfilerSection(int depth, String sectionName, StringBuilder stringBuilder) {
      List<net.minecraft.profiler.Profiler.Result> list = MinecraftServer.getServer().theProfiler.getProfilingData(sectionName);
      if (list != null && list.size() >= 3) {
         for (int index = 1; index < list.size(); index++) {
            net.minecraft.profiler.Profiler.Result result = list.get(index);
            stringBuilder.append(String.format("[%02d] ", depth));

            for (int innerIndex = 0; innerIndex < depth; innerIndex++) {
               stringBuilder.append(" ");
            }

            stringBuilder.append(result.sectionName)
               .append(" - ")
               .append(String.format("%.2f", result.usePercentage))
               .append("%/")
               .append(String.format("%.2f", result.totalUsePercentage))
               .append("%\n");
            if (!result.sectionName.equals("unspecified")) {
               try {
                  this.appendProfilerSection(depth + 1, sectionName + "." + result.sectionName, stringBuilder);
               } catch (Exception exception) {
                  stringBuilder.append("[[ EXCEPTION ").append(exception).append(" ]]");
               }
            }
         }
      }
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.debug.usage";
   }

   @Override
   public String getCommandName() {
      return "debug";
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, "start", "stop") : null;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.debug.usage");
      }

      if (args[0].equals("start")) {
         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         notifyOperators(sender, this, "commands.debug.start");
         MinecraftServer.getServer().enableProfiling();
         this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
         this.profileStartTick = MinecraftServer.getServer().getTickCounter();
      } else {
         if (!args[0].equals("stop")) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
            throw new CommandException("commands.debug.notStarted");
         }

         long currentTimeMillis = MinecraftServer.getCurrentTimeMillis();
         int tickCounter = MinecraftServer.getServer().getTickCounter();
         long l = currentTimeMillis - this.profileStartTime;
         int i = tickCounter - this.profileStartTick;
         this.saveProfileResults(l, i);
         MinecraftServer.getServer().theProfiler.profilingEnabled = false;
         notifyOperators(sender, this, "commands.debug.stop", (float)l / 1000.0F, i);
      }
   }

   private static String getWittyComment() {
      String[] texts = new String[]{
         "Shiny numbers!",
         "Am I not running fast enough? :(",
         "I'm working as hard as I can!",
         "Will I ever be good enough for you? :(",
         "Speedy. Zoooooom!",
         "Hello world",
         "40% better than a crash report.",
         "Now with extra numbers",
         "Now with less numbers",
         "Now with the same numbers",
         "You should add flames to things, it makes them go faster!",
         "Do you feel the need for... optimization?",
         "*cracks redstone whip*",
         "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
      };

      try {
         return texts[(int)(System.nanoTime() % texts.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }
}
