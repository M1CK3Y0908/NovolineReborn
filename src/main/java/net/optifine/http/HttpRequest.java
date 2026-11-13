package net.optifine.http;

import java.net.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
   public static final String HEAD = "HEAD";
   public static final String HTTP_1_0 = "HTTP/1.0";
   private String host = null;
   private int port = 0;
   private Proxy proxy = Proxy.NO_PROXY;
   private String method = null;
   public static final String HTTP_1_1 = "HTTP/1.1";
   public static final String POST = "POST";
   private String file = null;
   public static final String GET = "GET";
   private String http = null;
   private Map<String, String> headers = new LinkedHashMap<>();
   private byte[] body = null;
   private int redirects = 0;

   public String getHttp() {
      return this.http;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public int getPort() {
      return this.port;
   }

   public Map<String, String> getHeaders() {
      return this.headers;
   }

   public String getFile() {
      return this.file;
   }

   public String getMethod() {
      return this.method;
   }

   public void setRedirects(int redirects) {
      this.redirects = redirects;
   }

   public int getRedirects() {
      return this.redirects;
   }

   public byte[] getBody() {
      return this.body;
   }

   public String getHost() {
      return this.host;
   }

   public HttpRequest(String host, int port, Proxy proxy, String method, String file, String http, Map<String, String> headers, byte[] body) {
      this.host = host;
      this.port = port;
      this.proxy = proxy;
      this.method = method;
      this.file = file;
      this.http = http;
      this.headers = headers;
      this.body = body;
   }
}
