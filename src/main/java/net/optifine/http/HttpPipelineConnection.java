package net.optifine.http;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.src.Config;

public class HttpPipelineConnection {
   private String host = null;
   private static final Pattern patternFullUrl = Pattern.compile("^[a-zA-Z]+://.*");
   private int port = 0;
   private Proxy proxy = Proxy.NO_PROXY;
   private List<HttpPipelineRequest> listRequests = new LinkedList<>();
   private List<HttpPipelineRequest> listRequestsSend = new LinkedList<>();
   private Socket socket = null;
   private InputStream inputStream = null;
   private OutputStream outputStream = null;
   private static final String LF = "\n";
   private HttpPipelineSender httpPipelineSender = null;
   private HttpPipelineReceiver httpPipelineReceiver = null;
   private int countRequests = 0;
   public static final int intValue = 5000;
   private boolean responseReceived = false;
   private long keepaliveTimeoutMs = 5000L;
   private int keepaliveMaxCount = 1000;
   private long timeLastActivityMs = System.currentTimeMillis();
   public static final int intValue2 = 5000;
   private boolean terminated = false;

   private HttpPipelineRequest getNextRequest(List<HttpPipelineRequest> list, boolean remove) throws InterruptedException {
      while (list.size() <= 0) {
         this.checkTimeout();
         this.wait(1000L);
      }

      this.onActivity();
      return remove ? list.remove(0) : list.get(0);
   }

   public synchronized InputStream getInputStream() throws IOException, InterruptedException {
      while (this.inputStream == null) {
         this.checkTimeout();
         this.wait(1000L);
      }

      return this.inputStream;
   }

   public int getCountRequests() {
      return this.countRequests;
   }

   private void onActivity() {
      this.timeLastActivityMs = System.currentTimeMillis();
   }

   private void checkResponseHeader(HttpResponse resp) {
      String text = resp.getHeader("Connection");
      if (text != null && !text.toLowerCase().equals("keep-alive")) {
         this.terminate(new EOFException("Connection not keep-alive"));
      }

      String text2 = resp.getHeader("Keep-Alive");
      if (text2 != null) {
         String[] stringArray = Config.tokenize(text2, ",;");

         for (int index = 0; index < stringArray.length; index++) {
            String text3 = stringArray[index];
            String[] stringArray1 = this.getArray(text3);
            if (stringArray1.length >= 2) {
               if (stringArray1[0].equals("timeout")) {
                  int i = Config.parseInt(stringArray1[1], -1);
                  if (i > 0) {
                     this.keepaliveTimeoutMs = i * 1000;
                  }
               }

               if (stringArray1[0].equals("max")) {
                  int i2 = Config.parseInt(stringArray1[1], -1);
                  if (i2 > 0) {
                     this.keepaliveMaxCount = i2;
                  }
               }
            }
         }
      }
   }

   public synchronized void onExceptionReceive(HttpPipelineRequest pr, Exception exception) {
      this.terminate(exception);
   }

   public synchronized void onExceptionSend(HttpPipelineRequest pr, Exception exception) {
      this.terminate(exception);
   }

   public synchronized HttpPipelineRequest getNextRequestSend() throws InterruptedException, IOException {
      if (this.listRequestsSend.size() <= 0 && this.outputStream != null) {
         this.outputStream.flush();
      }

      return this.getNextRequest(this.listRequestsSend, true);
   }

   public synchronized boolean isClosed() {
      return this.terminated ? true : this.countRequests >= this.keepaliveMaxCount;
   }

   private void checkTimeout() {
      if (this.socket != null) {
         long keepaliveTimeoutMs2 = this.keepaliveTimeoutMs;
         if (this.listRequests.size() > 0) {
            keepaliveTimeoutMs2 = 5000L;
         }

         long l = System.currentTimeMillis();
         if (l > this.timeLastActivityMs + keepaliveTimeoutMs2) {
            this.terminate(new InterruptedException("Timeout " + keepaliveTimeoutMs2));
         }
      }
   }

   private String normalizeUrl(String url, HttpRequest hr) {
      if (patternFullUrl.matcher(url).matches()) {
         return url;
      }

      if (url.startsWith("//")) {
         return "http:" + url;
      }

      String text = hr.getHost();
      if (hr.getPort() != 80) {
         text = text + ":" + hr.getPort();
      }

      if (url.startsWith("/")) {
         return "http://" + text + url;
      }

      String text2 = hr.getFile();
      int i = text2.lastIndexOf("/");
      return i >= 0 ? "http://" + text + text2.substring(0, i + 1) + url : "http://" + text + "/" + url;
   }

   public synchronized void onRequestSent(HttpPipelineRequest pr) {
      if (!this.terminated) {
         this.onActivity();
      }
   }

   private synchronized void terminate(Exception exception) {
      if (!this.terminated) {
         this.terminated = true;
         this.terminateRequests(exception);
         if (this.httpPipelineSender != null) {
            this.httpPipelineSender.interrupt();
         }

         if (this.httpPipelineReceiver != null) {
            this.httpPipelineReceiver.interrupt();
         }

         try {
            if (this.socket != null) {
               this.socket.close();
            }
         } catch (IOException ioException) {
         }

         this.socket = null;
         this.inputStream = null;
         this.outputStream = null;
      }
   }

   private void addRequest(HttpPipelineRequest pr, List<HttpPipelineRequest> list) {
      list.add(pr);
      this.notifyAll();
   }

   public synchronized HttpPipelineRequest getNextRequestReceive() throws InterruptedException {
      return this.getNextRequest(this.listRequests, false);
   }

   public synchronized void onResponseReceived(HttpPipelineRequest pr, HttpResponse resp) {
      if (!this.terminated) {
         this.responseReceived = true;
         this.onActivity();
         if (this.listRequests.size() <= 0 || this.listRequests.get(0) != pr) {
            throw new IllegalArgumentException("Response out of order: " + pr);
         }

         this.listRequests.remove(0);
         pr.update();
         String text = resp.getHeader("Location");
         if (resp.getStatus() / 100 == 3 && text != null && pr.getHttpRequest().getRedirects() < 5) {
            try {
               text = this.normalizeUrl(text, pr.getHttpRequest());
               HttpRequest httpRequest = HttpPipeline.makeRequest(text, pr.getHttpRequest().getProxy());
               httpRequest.setRedirects(pr.getHttpRequest().getRedirects() + 1);
               HttpPipeline.addRequest(new HttpPipelineRequest(httpRequest, pr.getHttpListener()));
            } catch (IOException ioException) {
               pr.getHttpListener().failed(pr.getHttpRequest(), ioException);
            }
         } else {
            HttpListener httpListener = pr.getHttpListener();
            httpListener.finished(pr.getHttpRequest(), resp);
         }

         this.checkResponseHeader(resp);
      }
   }

   private void terminateRequests(Exception exception) {
      if (this.listRequests.size() > 0) {
         if (!this.responseReceived) {
            HttpPipelineRequest httpPipelineRequest = this.listRequests.remove(0);
            httpPipelineRequest.getHttpListener().failed(httpPipelineRequest.getHttpRequest(), exception);
            httpPipelineRequest.update();
         }

         while (this.listRequests.size() > 0) {
            HttpPipelineRequest httpPipelineRequest2 = this.listRequests.remove(0);
            HttpPipeline.addRequest(httpPipelineRequest2);
         }
      }
   }

   public synchronized boolean addRequest(HttpPipelineRequest pr) {
      if (this.isClosed()) {
         return false;
      }

      this.addRequest(pr, this.listRequests);
      this.addRequest(pr, this.listRequestsSend);
      this.countRequests++;
      return true;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   private String[] getArray(String text3) {
      int i = text3.indexOf(61);
      if (i < 0) {
         return new String[]{text3};
      }

      String text4 = text3.substring(0, i);
      String text5 = text3.substring(i + 1);
      return new String[]{text4, text5};
   }

   public synchronized void setSocket(Socket socket) throws IOException {
      if (!this.terminated) {
         if (this.socket != null) {
            throw new IllegalArgumentException("Already connected");
         }

         this.socket = socket;
         this.socket.setTcpNoDelay(true);
         this.inputStream = this.socket.getInputStream();
         this.outputStream = new BufferedOutputStream(this.socket.getOutputStream());
         this.onActivity();
         this.notifyAll();
      }
   }

   public HttpPipelineConnection(String host, int port, Proxy proxy) {
      this.host = host;
      this.port = port;
      this.proxy = proxy;
      this.httpPipelineSender = new HttpPipelineSender(this);
      this.httpPipelineSender.start();
      this.httpPipelineReceiver = new HttpPipelineReceiver(this);
      this.httpPipelineReceiver.start();
   }

   public synchronized OutputStream getOutputStream() throws IOException, InterruptedException {
      while (this.outputStream == null) {
         this.checkTimeout();
         this.wait(1000L);
      }

      return this.outputStream;
   }
}
