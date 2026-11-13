package net.optifine.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.src.Config;

public class HttpPipeline {
   public static final String LOCATION = "Location";
   public static final String CONNECTION = "Connection";
   public static final String HOST = "Host";
   public static final String CHUNKED = "chunked";
   public static final String KEEP_ALIVE = "keep-alive";
   public static final String TRANSFER_ENCODING = "Transfer-Encoding";
   public static final String USER_AGENT = "User-Agent";
   public static final String ACCEPT = "Accept";
   public static final String KEEP_ALIVE2 = "Keep-Alive";
   private static Map<String, HttpPipelineConnection> mapConnections = new HashMap<>();

   public static HttpResponse executeRequest(HttpRequest req) throws IOException {
      final HashMap hashMap = new HashMap();
      HttpListener httpListener = new HttpListener() {
         @Override
         public void failed(HttpRequest req, Exception exception) {
            synchronized (hashMap) {
               hashMap.put("Exception", exception);
               hashMap.notifyAll();
            }
         }

         @Override
         public void finished(HttpRequest req, HttpResponse resp) {
            synchronized (hashMap) {
               hashMap.put("Response", resp);
               hashMap.notifyAll();
            }
         }
      };
      synchronized (hashMap) {
         addRequest(new HttpPipelineRequest(req, httpListener));

         try {
            hashMap.wait();
         } catch (InterruptedException interruptedException) {
            throw new InterruptedIOException("Interrupted");
         }

         Exception exception2 = (Exception)hashMap.get("Exception");
         if (exception2 != null) {
            if (exception2 instanceof IOException) {
               throw (IOException)exception2;
            } else if (exception2 instanceof RuntimeException) {
               throw (RuntimeException)exception2;
            } else {
               throw new RuntimeException(exception2.getMessage(), exception2);
            }
         } else {
            HttpResponse httpResponse = (HttpResponse)hashMap.get("Response");
            if (httpResponse == null) {
               throw new IOException("Response is null");
            } else {
               return httpResponse;
            }
         }
      }
   }

   private static synchronized HttpPipelineConnection getConnection(String host, int port, Proxy proxy) {
      String text = makeConnectionKey(host, port, proxy);
      HttpPipelineConnection httpPipelineConnection = mapConnections.get(text);
      if (httpPipelineConnection == null) {
         httpPipelineConnection = new HttpPipelineConnection(host, port, proxy);
         mapConnections.put(text, httpPipelineConnection);
      }

      return httpPipelineConnection;
   }

   public static void addRequest(HttpPipelineRequest pr) {
      HttpRequest httpRequest = pr.getHttpRequest();

      for (HttpPipelineConnection httpPipelineConnection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy());
         !httpPipelineConnection.addRequest(pr);
         httpPipelineConnection = getConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy())
      ) {
         removeConnection(httpRequest.getHost(), httpRequest.getPort(), httpRequest.getProxy(), httpPipelineConnection);
      }
   }

   public static byte[] get(String urlStr, Proxy proxy) throws IOException {
      if (urlStr.startsWith("file:")) {
         InputStream inputStream = new URL(urlStr).openStream();
         return Config.readAll(inputStream);
      } else {
         HttpResponse httpResponse = executeRequest(makeRequest(urlStr, proxy));
         if (httpResponse.getStatus() / 100 != 2) {
            throw new IOException("HTTP response: " + httpResponse.getStatus());
         } else {
            return httpResponse.getBody();
         }
      }
   }

   private static String makeConnectionKey(String host, int port, Proxy proxy) {
      return host + ":" + port + "-" + proxy;
   }

   private static synchronized void removeConnection(String host, int port, Proxy proxy, HttpPipelineConnection hpc) {
      String text3 = makeConnectionKey(host, port, proxy);
      if (mapConnections.get(text3) == hpc) {
         mapConnections.remove(text3);
      }
   }

   public static HttpRequest makeRequest(String urlStr, Proxy proxy) throws IOException {
      URL url = new URL(urlStr);
      if (!url.getProtocol().equals("http")) {
         throw new IOException("Only protocol http is supported: " + url);
      }

      String text = url.getFile();
      String text2 = url.getHost();
      int port2 = url.getPort();
      if (port2 <= 0) {
         port2 = 80;
      }

      LinkedHashMap linkedHashMap = new LinkedHashMap();
      linkedHashMap.put("User-Agent", "Java/" + System.getProperty("java.version"));
      linkedHashMap.put("Host", text2);
      linkedHashMap.put("Accept", "text/html, image/gif, image/png");
      linkedHashMap.put("Connection", "keep-alive");
      byte[] byteArray = new byte[0];
      return new HttpRequest(text2, port2, proxy, "GET", text, "HTTP/1.1", linkedHashMap, byteArray);
   }
}
