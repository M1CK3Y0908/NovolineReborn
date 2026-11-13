package net.optifine.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.charset.Charset;

public class HttpPipelineSender extends Thread {
   private static final String text = "\r\n";
   private HttpPipelineConnection httpPipelineConnection = null;
   private static Charset ASCII = Charset.forName("ASCII");

   @Override
   public void run() {
      HttpPipelineRequest httpPipelineRequest = null;

      try {
         this.connect();

         while (!Thread.interrupted()) {
            httpPipelineRequest = this.httpPipelineConnection.getNextRequestSend();
            HttpRequest httpRequest = httpPipelineRequest.getHttpRequest();
            OutputStream outputStream = this.httpPipelineConnection.getOutputStream();
            this.writeRequest(httpRequest, outputStream);
            this.httpPipelineConnection.onRequestSent(httpPipelineRequest);
         }
      } catch (InterruptedException interruptedException) {
         return;
      } catch (Exception exception) {
         this.httpPipelineConnection.onExceptionSend(httpPipelineRequest, exception);
      }
   }

   public HttpPipelineSender(HttpPipelineConnection httpPipelineConnection) {
      super("HttpPipelineSender");
      this.httpPipelineConnection = httpPipelineConnection;
   }

   private void write(OutputStream out, String str) throws IOException {
      byte[] byteArray = str.getBytes(ASCII);
      out.write(byteArray);
   }

   private void writeRequest(HttpRequest req, OutputStream out) throws IOException {
      this.write(out, req.getMethod() + " " + req.getFile() + " " + req.getHttp() + "\r\n");

      for (String text : req.getHeaders().keySet()) {
         String text2 = req.getHeaders().get(text);
         this.write(out, text + ": " + text2 + "\r\n");
      }

      this.write(out, "\r\n");
   }

   private void connect() throws IOException {
      String text = this.httpPipelineConnection.getHost();
      int port = this.httpPipelineConnection.getPort();
      Proxy proxy = this.httpPipelineConnection.getProxy();
      Socket socket = new Socket(proxy);
      socket.connect(new InetSocketAddress(text, port), 5000);
      this.httpPipelineConnection.setSocket(socket);
   }
}
