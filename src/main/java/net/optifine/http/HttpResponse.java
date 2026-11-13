package net.optifine.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {
   private int status = 0;
   private String statusLine = null;
   private Map<String, String> headers = new LinkedHashMap<>();
   private byte[] body = null;

   public String getHeader(String key) {
      return this.headers.get(key);
   }

   public HttpResponse(int status, String statusLine, Map headers, byte[] body) {
      this.status = status;
      this.statusLine = statusLine;
      this.headers = headers;
      this.body = body;
   }

   public String getStatusLine() {
      return this.statusLine;
   }

   public byte[] getBody() {
      return this.body;
   }

   public int getStatus() {
      return this.status;
   }

   public Map getHeaders() {
      return this.headers;
   }
}
