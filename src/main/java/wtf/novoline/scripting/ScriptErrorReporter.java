package wtf.novoline.scripting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.Novoline;

public class ScriptErrorReporter {
   private static final Pattern pattern = Pattern.compile("L:([0-9]*)");

   private static void printTraceback(String text, Script script, Throwable throwable) {
      Novoline novoline2 = Novoline.getNovoline();
      novoline2.sendChatMessage(EnumChatFormatting.GRAY + "Traceback (most recent call last):");
      if (throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      List list = Arrays.stream(throwable.getStackTrace()).filter(stackTraceElement -> stackTraceElement.getClassName().contains("Script$")).map(stackTraceElement -> {
         ScriptErrorReporter.SourceLocation sourceLocation = createSourceLocation(script, stackTraceElement.getLineNumber());
         return "M: " + translateLineNumbers(stackTraceElement.getMethodName(), script) + ", F: " + sourceLocation.getFile() + ", L: " + sourceLocation.getLine();
      }).collect(Collectors.toList());
      Collections.reverse(list);
      list.forEach(string -> novoline2.sendChatMessage(EnumChatFormatting.GRAY + "- " + EnumChatFormatting.RESET + string));
   }

   public static void reportLoadFailure(Script script, Throwable throwable) {
      String displayName = script.getDisplayName();
      Novoline.getNovoline()
         .sendChatMessage(EnumChatFormatting.GRAY + "---" + EnumChatFormatting.RED + " SCRIPT LOAD FAILED " + EnumChatFormatting.GRAY + "---");
      if (throwable.getClass() == RuntimeException.class && throwable.getCause() != null) {
         throwable = throwable.getCause();
      }

      printFailure(displayName, throwable);
      printTraceback(displayName, script, throwable);
   }

   private static ScriptErrorReporter.SourceLocation createSourceLocation(Script script, int i) {
      int i2 = 0;

      for (ScriptSource scriptSource : script.getImports()) {
         int i3 = i2;
         i2 += scriptSource.getResolvedImport().split("\n").length;
         if (i <= i2) {
            int i4 = i - i3;
            return ScriptErrorReporter.SourceLocation.createSourceLocation(scriptSource.toString(), i4);
         }
      }

      return ScriptErrorReporter.SourceLocation.createSourceLocation(script.getPath().getFileName().toString(), i - i2);
   }

   private static String translateLineNumbers(String text, Script script) {
      Matcher matcher2 = pattern.matcher(text);

      while (matcher2.find()) {
         String text2 = matcher2.group().substring("L:".length());
         int line2 = createSourceLocation(script, Integer.parseInt(text2)).getLine();
         text = text.replace(text2, String.valueOf(line2));
      }

      return text;
   }

   public static void reportEventFailure(ScriptEventHandler scriptEventHandler, Throwable throwable) {
      String name = scriptEventHandler.getModule().getName();
      Novoline.getNovoline()
         .sendChatMessage(EnumChatFormatting.GRAY + "---" + EnumChatFormatting.RED + " EVENT EXECUTION FAILED " + EnumChatFormatting.GRAY + "---");
      Novoline.getNovoline().sendChatMessage("Failing Script: " + EnumChatFormatting.RESET + scriptEventHandler.getScript().getDisplayName());
      printFailure(name, throwable);
      printTraceback(name, scriptEventHandler.getScript(), throwable);
   }

   private static void printFailure(String text, Throwable throwable) {
      Novoline novoline2 = Novoline.getNovoline();
      novoline2.sendChatMessage(EnumChatFormatting.GRAY + "Failing Feature: " + EnumChatFormatting.RESET + text);
      novoline2.sendChatMessage(EnumChatFormatting.GRAY + "Exception Class: " + EnumChatFormatting.RESET + throwable.getClass().getName());
      novoline2.sendChatMessage(EnumChatFormatting.GRAY + "Exception: " + EnumChatFormatting.RESET + throwable.getMessage());
   }

   record SourceLocation(String file, int line) {
      public String getFile() {
         return this.file;
      }

      public int getLine() {
         return this.line;
      }

      public static ScriptErrorReporter.SourceLocation createSourceLocation(String text, int i) {
         return new ScriptErrorReporter.SourceLocation(text, i);
      }
   }
}
