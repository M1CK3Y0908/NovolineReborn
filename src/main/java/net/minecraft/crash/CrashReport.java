package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger logger = LogManager.getLogger();
   private File crashReportFile;
   private final CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
   private final List<CrashReportCategory> crashReportSections = Lists.newArrayList();
   private boolean firstCategoryInCrashReport = true;
   private StackTraceElement[] stacktrace = new StackTraceElement[0];
   private boolean flag = false;
   private final String description;
   private final Throwable cause;

   public Throwable getCrashCause() {
      return this.cause;
   }

   public boolean saveToFile(File toFile) {
      if (this.crashReportFile != null) {
         return false;
      }

      if (toFile.getParentFile() != null) {
         toFile.getParentFile().mkdirs();
      }

      try {
         FileWriter fileWriter = new FileWriter(toFile);
         fileWriter.write(this.getCompleteReport());
         fileWriter.close();
         this.crashReportFile = toFile;
         return true;
      } catch (Throwable throwable) {
         logger.error("Could not save crash report to " + toFile, throwable);
         return false;
      }
   }

   public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
      CrashReport crashReport;
      if (causeIn instanceof ReportedException) {
         crashReport = ((ReportedException)causeIn).getCrashReport();
      } else {
         crashReport = new CrashReport(descriptionIn, causeIn);
      }

      return crashReport;
   }

   public CrashReportCategory getCategory() {
      return this.theReportCategory;
   }

   public CrashReportCategory getCrashReportCategory(String text2) {
      CrashReportCategory crashReportCategory = new CrashReportCategory(this, text2);
      if (this.firstCategoryInCrashReport) {
         int intValue = crashReportCategory.getInt();
         StackTraceElement[] stackTrace = this.cause.getStackTrace();
         StackTraceElement stackTraceElement = null;
         StackTraceElement stackTraceElement2 = null;
         int i = stackTrace.length - intValue;
         if (i < 0) {
            System.out.println("Negative index in crash report handler (" + stackTrace.length + "/" + intValue + ")");
         }

         if (stackTrace != null && 0 <= i && i < stackTrace.length) {
            stackTraceElement = stackTrace[i];
            if (stackTrace.length + 1 - intValue < stackTrace.length) {
               stackTraceElement2 = stackTrace[stackTrace.length + 1 - intValue];
            }
         }

         this.firstCategoryInCrashReport = crashReportCategory.firstTwoElementsOfStackTraceMatch(stackTraceElement, stackTraceElement2);
         if (intValue > 0 && !this.crashReportSections.isEmpty()) {
            this.crashReportSections.get(this.crashReportSections.size() - 1).trimStackTraceEntriesFromBottom(intValue);
         } else if (stackTrace != null && stackTrace.length >= intValue && 0 <= i && i < stackTrace.length) {
            this.stacktrace = new StackTraceElement[i];
            System.arraycopy(stackTrace, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
         }
      }

      this.crashReportSections.add(crashReportCategory);
      return crashReportCategory;
   }

   public CrashReport(String descriptionIn, Throwable causeThrowable) {
      this.description = descriptionIn;
      this.cause = causeThrowable;
      this.populateEnvironment();
   }

   public void getSectionsInStringBuilder(StringBuilder builder) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
         this.stacktrace = ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         builder.append("-- Head --\n");
         builder.append("Stacktrace:\n");

         for (StackTraceElement stackTraceElement : this.stacktrace) {
            builder.append("\t").append("at ").append(stackTraceElement.toString());
            builder.append("\n");
         }

         builder.append("\n");
      }

      for (CrashReportCategory crashReportCategory : this.crashReportSections) {
         crashReportCategory.appendToStringBuilder(builder);
         builder.append("\n\n");
      }

      this.theReportCategory.appendToStringBuilder(builder);
   }

   public File getFile() {
      return this.crashReportFile;
   }

   public String getCauseStackTraceOrString() {
      StringWriter stringWriter = null;
      PrintWriter printWriter = null;
      Throwable cause2 = this.cause;
      if (cause2.getMessage() == null) {
         if (cause2 instanceof NullPointerException) {
            cause2 = new NullPointerException(this.description);
         } else if (cause2 instanceof StackOverflowError) {
            cause2 = new StackOverflowError(this.description);
         } else if (cause2 instanceof OutOfMemoryError) {
            cause2 = new OutOfMemoryError(this.description);
         }

         cause2.setStackTrace(this.cause.getStackTrace());
      }

      cause2.toString();

      String text2;
      try {
         stringWriter = new StringWriter();
         printWriter = new PrintWriter(stringWriter);
         cause2.printStackTrace(printWriter);
         text2 = stringWriter.toString();
      } finally {
         IOUtils.closeQuietly(stringWriter);
         IOUtils.closeQuietly(printWriter);
      }

      return text2;
   }

   public String getCompleteReport() {
      if (!this.flag) {
         this.flag = true;
         CrashReporter.onCrashReport(this, this.theReportCategory);
      }

      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("---- Minecraft Crash Report ----\n");
      Reflector.createObject2(Reflector.reflectorMethod, stringBuilder);
      Reflector.createObject2(Reflector.reflectorMethod2, stringBuilder);
      stringBuilder.append("// ");
      stringBuilder.append(getWittyComment());
      stringBuilder.append("\n\n");
      stringBuilder.append("Time: ");
      stringBuilder.append(new SimpleDateFormat().format(new Date()));
      stringBuilder.append("\n");
      stringBuilder.append("Description: ");
      stringBuilder.append(this.description);
      stringBuilder.append("\n\n");
      stringBuilder.append(this.getCauseStackTraceOrString());
      stringBuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for (int index = 0; index < 87; index++) {
         stringBuilder.append("-");
      }

      stringBuilder.append("\n\n");
      this.getSectionsInStringBuilder(stringBuilder);
      return stringBuilder.toString();
   }

   private static String getWittyComment() {
      String[] texts = new String[]{
         "Who set us up the TNT?",
         "Everything's going to plan. No, really, that was supposed to happen.",
         "Uh... Did I do that?",
         "Oops.",
         "Why did you do that?",
         "I feel sad now :(",
         "My bad.",
         "I'm sorry, Dave.",
         "I let you down. Sorry :(",
         "On the bright side, I bought you a teddy bear!",
         "Daisy, daisy...",
         "Oh - I know what I did wrong!",
         "Hey, that tickles! Hehehe!",
         "I blame Dinnerbone.",
         "You should try our sister game, Minceraft!",
         "Don't be sad. I'll do better next time, I promise!",
         "Don't be sad, have a hug! <3",
         "I just don't know what went wrong :(",
         "Shall we play a game?",
         "Quite honestly, I wouldn't worry myself about that.",
         "I bet Cylons wouldn't have this problem.",
         "Sorry :(",
         "Surprise! Haha. Well, this is awkward.",
         "Would you like a cupcake?",
         "Hi. I'm Minecraft, and I'm a crashaholic.",
         "Ooh. Shiny.",
         "This doesn't make any sense!",
         "Why is it breaking :(",
         "Don't do that.",
         "Ouch. That hurt :(",
         "You're mean.",
         "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
         "There are four lights!",
         "But it works on my machine."
      };

      try {
         return texts[(int)(System.nanoTime() % texts.length)];
      } catch (Throwable throwable) {
         return "Witty comment unavailable :(";
      }
   }

   public String getDescription() {
      return this.description;
   }

   public CrashReportCategory makeCategory(String name) {
      return this.getCrashReportCategory(name);
   }

   private void populateEnvironment() {
      this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>() {
         public String call() {
            return "1.8.9";
         }
      });
      this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>() {
         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>() {
         public String call() {
            Runtime runtime = Runtime.getRuntime();
            long l = runtime.maxMemory();
            long l2 = runtime.totalMemory();
            long l3 = runtime.freeMemory();
            long l4 = l / 1024L / 1024L;
            long l5 = l2 / 1024L / 1024L;
            long l6 = l3 / 1024L / 1024L;
            long l7 = l4;
            long l8 = l;
            long l9 = l5;
            long l10 = l2;
            long l11 = l6;
            return l3 + " bytes (" + l11 + " MB) / " + l10 + " bytes (" + l9 + " MB) up to " + l8 + " bytes (" + l7 + " MB)";
         }
      });
      this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>() {
         public String call() {
            List inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
            int i = 0;
            StringBuilder stringBuilder = new StringBuilder();

            for (String text2 : (Iterable<String>) inputArguments) {
               if (text2.startsWith("-X")) {
                  if (i++ > 0) {
                     stringBuilder.append(" ");
                  }

                  stringBuilder.append(text2);
               }
            }

            return String.format("%d total; %s", i, stringBuilder.toString());
         }
      });
      this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>() {
         public String call() throws Exception {
            return IntCache.getCacheSizes();
         }
      });
      if (Reflector.reflectorMethod13.isActive()) {
         Reflector.createString2(Reflector.createObject2(Reflector.reflectorMethod17), Reflector.reflectorMethod13, this, this.theReportCategory);
      }
   }
}
