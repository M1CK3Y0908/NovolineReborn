package net.optifine.http;

public class HttpPipelineRequest {
   private HttpRequest httpRequest = null;
   private HttpListener httpListener = null;
   private boolean closed = false;

   public HttpListener getHttpListener() {
      return this.httpListener;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public HttpRequest getHttpRequest() {
      return this.httpRequest;
   }

   public void update() {
      this.closed = true;
   }

   public HttpPipelineRequest(HttpRequest httpRequest, HttpListener httpListener) {
      this.httpRequest = httpRequest;
      this.httpListener = httpListener;
   }
}
