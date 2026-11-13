package net.optifine.http;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import net.minecraft.src.Config;

public class HttpPipelineReceiver extends Thread {
   private static final String CONTENT_LENGTH = "Content-Length";
   private HttpPipelineConnection httpPipelineConnection = null;
   private static final char LF = '\n';
   private static final char CR = '\r';
   private static final Charset ASCII = Charset.forName("ASCII");

   private HttpResponse readResponse(InputStream in) throws IOException {
      String line2 = this.readLine(in);
      String[] texts = Config.tokenize(line2, " ");
      if (texts.length < 3) {
         throw new IOException("Invalid status line: " + line2);
      }

      String text2 = texts[0];
      int i = Config.parseInt(texts[1], 0);
      text2 = texts[2];
      LinkedHashMap linkedHashMap = new LinkedHashMap();

      while (true) {
         String line3 = this.readLine(in);
         if (line3.length() <= 0) {
            byte[] bArray = null;
            String text3 = (String)linkedHashMap.get("Content-Length");
            if (text3 != null) {
               int i2 = Config.parseInt(text3, -1);
               if (i2 > 0) {
                  bArray = new byte[i2];
                  this.readFull(bArray, in);
               }
            } else {
               String text4 = (String)linkedHashMap.get("Transfer-Encoding");
               if (Config.equals(text4, "chunked")) {
                  bArray = this.readContentChunked(in);
               }
            }

            return new HttpResponse(i, line2, linkedHashMap, bArray);
         }

         int i3 = line3.indexOf(":");
         if (i3 > 0) {
            String text5 = line3.substring(0, i3).trim();
            String text6 = line3.substring(i3 + 1).trim();
            linkedHashMap.put(text5, text6);
         }
      }
   }

   @Override
   public void run() {
      while (!Thread.interrupted()) {
         HttpPipelineRequest httpPipelineRequest = null;

         try {
            httpPipelineRequest = this.httpPipelineConnection.getNextRequestReceive();
            InputStream inputStream = this.httpPipelineConnection.getInputStream();
            HttpResponse response = this.readResponse(inputStream);
            this.httpPipelineConnection.onResponseReceived(httpPipelineRequest, response);
         } catch (InterruptedException interruptedException) {
            return;
         } catch (Exception exception) {
            this.httpPipelineConnection.onExceptionReceive(httpPipelineRequest, exception);
         }
      }
   }

   private void readFull(byte[] buf, InputStream in) throws IOException {
      int index = 0;

      while (index < buf.length) {
         int i = in.read(buf, index, buf.length - index);
         if (i < 0) {
            throw new EOFException();
         }

         index += i;
      }
   }

   private String readLine(InputStream in) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int i = -1;
      boolean conditionMet = false;

      while (true) {
         int i2 = in.read();
         if (i2 < 0) {
            break;
         }

         byteArrayOutputStream.write(i2);
         if (i == 13 && i2 == 10) {
            conditionMet = true;
            break;
         }

         i = i2;
      }

      byte[] byteArray = byteArrayOutputStream.toByteArray();
      String text = new String(byteArray, ASCII);
      if (conditionMet) {
         text = text.substring(0, text.length() - 2);
      }

      return text;
   }

   public HttpPipelineReceiver(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineReceiver");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   private byte[] readContentChunked(InputStream in) throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      int i;
      do {
         i = Integer.parseInt(Config.tokenize(this.readLine(in), "; ")[0], 16);
         byte[] bArray = new byte[i];
         this.readFull(bArray, in);
         byteArrayOutputStream.write(bArray);
         this.readLine(in);
      } while (i != 0);

      return byteArrayOutputStream.toByteArray();
   }
}
