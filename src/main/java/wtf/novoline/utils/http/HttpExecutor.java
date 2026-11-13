package wtf.novoline.utils.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import wtf.novoline.enums.HttpMethod2;
import wtf.novoline.gson.InstantStringDeserializer2;

public class HttpExecutor {
   private final HttpClient httpClient = HttpClient.newHttpClient();
   private static final Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantStringDeserializer2()).create();

   public HttpResponse getHttpResponse(AuthRequest authRequest, java.net.http.HttpResponse.BodyHandler bodyHandler) throws IOException, InterruptedException {
      java.net.http.HttpRequest.Builder builder = HttpRequest.newBuilder(authRequest.getURI()).header("User-Agent", "Novoline-HTTP-Client/1.0");
      if (authRequest.getRequestMethod() == HttpMethod2.GET) {
         builder.GET();
      } else {
         builder.method(authRequest.getRequestMethod().name(), authRequest.getBodyPublisher());
      }

      if (authRequest.getRequestMethod() != HttpMethod2.GET && authRequest.getHttpBody() != null) {
         String contentType = authRequest.getHttpBody().getContentType();
         builder.header("Content-Type", contentType);
      }

      for (HttpHeader httpHeader : (Iterable<HttpHeader>) authRequest.getHeaders()) {
         builder.header(httpHeader.getName(), httpHeader.getValue());
      }

      HttpRequest httpRequest = builder.build();
      HttpResponse httpResponse = this.httpClient.send(httpRequest, bodyHandler);
      int i = httpResponse.statusCode() / 100;
      if (i == 4 || i == 5) {
         authRequest.onFailedResponse(httpResponse);
      }

      return httpResponse;
   }

   public Object executeJson(AuthRequest authRequest, Class clazz) throws IOException, InterruptedException {
      String text = (String)this.getHttpResponse(authRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
      return gson.fromJson(text, clazz);
   }
}
