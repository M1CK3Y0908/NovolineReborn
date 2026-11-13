package net.optifine.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;

public class HttpUtils {
   public static final String HTTP_S_OPTIFINE_NET = "http://s.optifine.net";
   public static final String HTTP_OPTIFINE_NET = "http://optifine.net";
   private static String playerItemsUrl = null;

   public static synchronized String getPlayerItemsUrl() {
      if (playerItemsUrl == null) {
         try {
            boolean flag = Config.parseBoolean(System.getProperty("player.models.local"), false);
            if (flag) {
               File mcDataDir2 = Minecraft.getMinecraft().mcDataDir;
               playerItemsUrl = new File(mcDataDir2, "playermodels").toURI().toURL().toExternalForm();
            }
         } catch (Exception exception) {
            Config.warn(exception.getClass().getName() + ": " + exception.getMessage());
         }

         if (playerItemsUrl == null) {
            playerItemsUrl = "http://s.optifine.net";
         }
      }

      return playerItemsUrl;
   }

   public static String post(String urlStr, Map headers, byte[] content) throws IOException {
      HttpURLConnection httpURLConnection = null;

      String text;
      try {
         httpURLConnection = (HttpURLConnection)new URL(urlStr).openConnection(Minecraft.getMinecraft().getProxy());
         httpURLConnection.setRequestMethod("POST");
         if (headers != null) {
            for (Object headerKey : headers.keySet()) {
               String text2 = (String)headerKey;
               String text3 = headers.get(text2) + "";
               httpURLConnection.setRequestProperty(text2, text3);
            }
         }

         httpURLConnection.setRequestProperty("Content-Type", "text/plain");
         httpURLConnection.setRequestProperty("Content-Length", content.length + "");
         httpURLConnection.setRequestProperty("Content-Language", "en-US");
         httpURLConnection.setUseCaches(false);
         httpURLConnection.setDoInput(true);
         httpURLConnection.setDoOutput(true);
         OutputStream outputStream = httpURLConnection.getOutputStream();
         outputStream.write(content);
         outputStream.flush();
         outputStream.close();
         InputStream inputStream = httpURLConnection.getInputStream();
         InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ASCII");
         BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
         StringBuffer stringBuffer = new StringBuffer();

         String text4;
         while ((text4 = bufferedReader.readLine()) != null) {
            stringBuffer.append(text4);
            stringBuffer.append('\r');
         }

         bufferedReader.close();
         text = stringBuffer.toString();
      } finally {
         if (httpURLConnection != null) {
            httpURLConnection.disconnect();
         }
      }

      return text;
   }
}
