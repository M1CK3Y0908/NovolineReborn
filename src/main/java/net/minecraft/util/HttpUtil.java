package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   public static final ListeningExecutorService DOWNLOAD_EXECUTOR = MoreExecutors.listeningDecorator(
      Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build())
   );
   private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();

   public static String buildPostString(Map<String, Object> data) {
      StringBuilder stringBuilder = new StringBuilder();

      for (Entry entry : data.entrySet()) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append('&');
         }

         try {
            stringBuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
         }

         if (entry.getValue() != null) {
            stringBuilder.append('=');

            try {
               stringBuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException unsupportedEncodingException2) {
               unsupportedEncodingException2.printStackTrace();
            }
         }
      }

      return stringBuilder.toString();
   }

   private static String createString2(URL url2, String text) {
      try {
         Proxy proxy2 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();
         if (proxy2 == null) {
            proxy2 = Proxy.NO_PROXY;
         }

         HttpURLConnection httpURLConnection2 = (HttpURLConnection)url2.openConnection(proxy2);
         httpURLConnection2.setRequestMethod("POST");
         httpURLConnection2.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpURLConnection2.setRequestProperty("Content-Length", text.getBytes().length + "");
         httpURLConnection2.setRequestProperty("Content-Language", "en-US");
         httpURLConnection2.setUseCaches(false);
         httpURLConnection2.setDoInput(true);
         httpURLConnection2.setDoOutput(true);
         DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection2.getOutputStream());
         dataOutputStream.writeBytes(text);
         dataOutputStream.flush();
         dataOutputStream.close();
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection2.getInputStream()));
         StringBuffer stringBuffer = new StringBuffer();

         String text2;
         while ((text2 = bufferedReader.readLine()) != null) {
            stringBuffer.append(text2);
            stringBuffer.append('\r');
         }

         bufferedReader.close();
         return stringBuffer.toString();
      } catch (Exception exception) {
         return "";
      }
   }

   public static String get(URL url) throws IOException {
      HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
      httpURLConnection.setRequestMethod("GET");
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
      StringBuilder stringBuilder = new StringBuilder();

      String text;
      while ((text = bufferedReader.readLine()) != null) {
         stringBuilder.append(text);
         stringBuilder.append('\r');
      }

      bufferedReader.close();
      return stringBuilder.toString();
   }

   public static String createString(URL url2, Map map) {
      return createString2(url2, buildPostString(map));
   }

   public static ListenableFuture<Object> downloadResourcePack(
      final File saveFile,
      final String packUrl,
      final Map<String, String> requestHeaders,
      final int maxSize,
      final IProgressUpdate progressUpdate,
      final Proxy proxy
   ) {
      return (ListenableFuture<Object>)(ListenableFuture)DOWNLOAD_EXECUTOR.submit(new Runnable() {
         @Override
         public void run() {
            HttpURLConnection httpURLConnection2 = null;
            InputStream inputStream = null;
            DataOutputStream dataOutputStream = null;
            if (progressUpdate != null) {
               progressUpdate.resetProgressAndMessage("Downloading Resource Pack");
               progressUpdate.displayLoadingString("Making Request...");
            }

            try {
               byte[] bArray = new byte[4096];
               URL url2 = new URL(packUrl);
               httpURLConnection2 = (HttpURLConnection)url2.openConnection(proxy);
               float f2 = 0.0F;
               float size2 = requestHeaders.entrySet().size();

               for (Entry entry : requestHeaders.entrySet()) {
                  httpURLConnection2.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
                  if (progressUpdate != null) {
                     progressUpdate.setLoadingProgress((int)(++f2 / size2 * 100.0F));
                  }
               }

               inputStream = httpURLConnection2.getInputStream();
               size2 = httpURLConnection2.getContentLength();
               int contentLength = httpURLConnection2.getContentLength();
               if (progressUpdate != null) {
                  progressUpdate.displayLoadingString(String.format("Downloading file (%.2f MB)...", size2 / 1000.0F / 1000.0F));
               }

               if (saveFile.exists()) {
                  long length2 = saveFile.length();
                  if (length2 == contentLength) {
                     if (progressUpdate != null) {
                        progressUpdate.setDoneWorking();
                     }

                     return;
                  }

                  long l = length2;
                  HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + contentLength + " vs our " + l + ").");
                  FileUtils.deleteQuietly(saveFile);
               } else if (saveFile.getParentFile() != null) {
                  saveFile.getParentFile().mkdirs();
               }

               dataOutputStream = new DataOutputStream(new FileOutputStream(saveFile));
               if (maxSize > 0 && size2 > maxSize) {
                  if (progressUpdate != null) {
                     progressUpdate.setDoneWorking();
                  }

                  throw new IOException("Filesize is bigger than maximum allowed (file is " + f2 + ", limit is " + maxSize + ")");
               }

               while (true) {
                  int i;
                  if ((i = inputStream.read(bArray)) < 0) {
                     if (progressUpdate == null) {
                        return;
                     }

                     progressUpdate.setDoneWorking();
                     return;
                  }

                  f2 += i;
                  if (progressUpdate != null) {
                     progressUpdate.setLoadingProgress((int)(f2 / size2 * 100.0F));
                  }

                  if (maxSize > 0 && f2 > maxSize) {
                     if (progressUpdate != null) {
                        progressUpdate.setDoneWorking();
                     }

                     throw new IOException("Filesize was bigger than maximum allowed (got >= " + f2 + ", limit was " + maxSize + ")");
                  }

                  if (Thread.interrupted()) {
                     HttpUtil.logger.error("INTERRUPTED");
                     if (progressUpdate != null) {
                        progressUpdate.setDoneWorking();
                     }
                     break;
                  }

                  dataOutputStream.write(bArray, 0, i);
               }
            } catch (Throwable throwable) {
               throwable.printStackTrace();
               if (httpURLConnection2 != null) {
                  InputStream errorStream = httpURLConnection2.getErrorStream();

                  try {
                     HttpUtil.logger.error(IOUtils.toString(errorStream));
                  } catch (IOException ioException) {
                     ioException.printStackTrace();
                  }
               }

               if (progressUpdate != null) {
                  progressUpdate.setDoneWorking();
                  return;
               }

               return;
            } finally {
               IOUtils.closeQuietly(inputStream);
               IOUtils.closeQuietly(dataOutputStream);
            }
         }
      });
   }

   public static int getSuitableLanPort() throws IOException {
      ServerSocket serverSocket = null;
      boolean flag = false;

      int i;
      try {
         flag = true;
         serverSocket = new ServerSocket(0);
         i = serverSocket.getLocalPort();
         flag = false;
      } finally {
         if (flag) {
            try {
               if (serverSocket != null) {
                  serverSocket.close();
               }
            } catch (IOException ioException) {
            }
         }
      }

      try {
         serverSocket.close();
      } catch (IOException ioException2) {
      }

      return i;
   }
}
