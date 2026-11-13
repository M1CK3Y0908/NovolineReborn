package net.optifine;

import java.util.HashMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.src.Config;
import net.optifine.http.FileUploadThread;
import net.optifine.http.IFileUploadListener;
import net.optifine.shaders.Shaders;

public class CrashReporter {
   public static void onCrashReport(CrashReport crashReport, CrashReportCategory category) {
      try {
         Throwable crashCause = crashReport.getCrashCause();
         if (crashCause == null) {
            return;
         }

         if (crashCause.getClass().getName().contains(".fml.client.SplashProgress")) {
            return;
         }

         if (crashCause.getClass() == Throwable.class) {
            return;
         }

         extendCrashReport(category);
         GameSettings gameSettings = Config.getGameSettings();
         if (gameSettings == null) {
            return;
         }

         if (!gameSettings.fullScreen) {
            return;
         }

         byte[] bytes = makeReport(crashReport).getBytes("ASCII");
         IFileUploadListener iFileUploadListener = new IFileUploadListener() {
            @Override
            public void fileUploadFinished(String url, byte[] content, Throwable exception) {
            }
         };
         HashMap hashMap = new HashMap();
         hashMap.put("OF-Version", Config.getVersion());
         hashMap.put("OF-Summary", makeSummary(crashReport));
         FileUploadThread fileUploadThread = new FileUploadThread("http://optifine.net/crashReport", hashMap, bytes, iFileUploadListener);
         fileUploadThread.setPriority(10);
         fileUploadThread.start();
         Thread.sleep(1000L);
      } catch (Exception exception2) {
         Config.dbg(exception2.getClass().getName() + ": " + exception2.getMessage());
      }
   }

   private static String makeSummary(CrashReport crashReport) {
      Throwable throwable = crashReport.getCrashCause();
      if (throwable == null) {
         return "Unknown";
      }

      StackTraceElement[] astacktraceelement = throwable.getStackTrace();
      String message = "unknown";
      if (astacktraceelement.length > 0) {
         message = astacktraceelement[0].toString().trim();
      }

      return throwable.getClass().getName() + ": " + throwable.getMessage() + " (" + crashReport.getDescription() + ") [" + message + "]";
   }

   private static String makeReport(CrashReport crashReport) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("OptiFineVersion: " + Config.getVersion() + "\n");
      stringBuffer.append("Summary: " + makeSummary(crashReport) + "\n");
      stringBuffer.append("\n");
      stringBuffer.append(crashReport.getCompleteReport());
      stringBuffer.append("\n");
      return stringBuffer.toString();
   }

   public static void extendCrashReport(CrashReportCategory cat) {
      cat.addCrashSection("OptiFine Version", Config.getVersion());
      cat.addCrashSection("OptiFine Build", Config.getBuild());
      if (Config.getGameSettings() != null) {
         cat.addCrashSection("Render Distance Chunks", Config.getChunkViewDistance() + "");
         cat.addCrashSection("Mipmaps", Config.getMipmapLevels() + "");
         cat.addCrashSection("Anisotropic Filtering", Config.getAnisotropicFilterLevel() + "");
         cat.addCrashSection("Antialiasing", Config.getAntialiasingLevel() + "");
         cat.addCrashSection("Multitexture", Config.isMultiTexture() + "");
      }

      cat.addCrashSection("Shaders", Shaders.getShaderPackName() + "");
      cat.addCrashSection("OpenGlVersion", Config.openGlVersion + "");
      cat.addCrashSection("OpenGlRenderer", Config.openGlRenderer + "");
      cat.addCrashSection("OpenGlVendor", Config.openGlVendor + "");
      cat.addCrashSection("CpuCount", Config.getAvailableProcessors() + "");
   }
}
