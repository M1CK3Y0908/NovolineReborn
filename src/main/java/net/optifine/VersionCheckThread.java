package net.optifine;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.src.Config;

public class VersionCheckThread extends Thread {
   public VersionCheckThread() {
      super("VersionCheck");
   }

   @Override
   public void run() {
      try {
         Config.dbg("Checking for new version");
         URL url = new URL("http://optifine.net/version/1.8.9/HD_U.txt");
         HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
         if (Config.getGameSettings().fullScreen) {
            httpURLConnection.setRequestProperty("OF-MC-Version", "1.8.9");
            httpURLConnection.setRequestProperty("OF-MC-Brand", ClientBrandRetriever.getClientModName() + "");
            httpURLConnection.setRequestProperty("OF-Edition", "HD_U");
            httpURLConnection.setRequestProperty("OF-Release", "M5");
            httpURLConnection.setRequestProperty("OF-Java-Version", System.getProperty("java.version") + "");
            httpURLConnection.setRequestProperty("OF-CpuCount", Config.getAvailableProcessors() + "");
            httpURLConnection.setRequestProperty("OF-OpenGL-Version", Config.openGlVersion + "");
            httpURLConnection.setRequestProperty("OF-OpenGL-Vendor", Config.openGlVendor + "");
         }

         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(false);
         httpURLConnection.connect();

         try {
            InputStream inputStream = httpURLConnection.getInputStream();
            String inputStream2 = Config.readInputStream(inputStream);
            inputStream.close();
            String[] texts = Config.tokenize(inputStream2, "\n\r");
            if (texts.length < 1) {
               return;
            }

            String text = texts[0].trim();
            Config.dbg("Version found: " + text);
            if (Config.compareRelease(text, "M5") > 0) {
               Config.setNewRelease(text);
               return;
            }
         } finally {
            if (httpURLConnection != null) {
               httpURLConnection.disconnect();
            }
         }
      } catch (Exception exception) {
         Config.dbg(exception.getClass().getName() + ": " + exception.getMessage());
      }
   }
}
