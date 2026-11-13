package wtf.novoline.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import wtf.novoline.enums.HttpMethod;

public class ApiHttpDispatcher {
   private final ApiClient apiClient;
   private final ScheduledExecutorService scheduledExecutorService;
   private final HttpClient httpClient;

   public static ApiHttpDispatcher createApiHttpDispatcher(ApiClient apiClient2) {
      return new ApiHttpDispatcher(apiClient2);
   }

   public <T> CompletableFuture<HttpResponse<T>> sendAsync(ApiRequest apiRequest2, java.net.http.HttpResponse.BodyHandler<T> bodyHandler2) {
      CompletableFuture completableFuture2 = new CompletableFuture();
      this.scheduledExecutorService.execute(new ApiHttpDispatcher.RequestTask(apiRequest2, bodyHandler2, completableFuture2));
      return completableFuture2;
   }

   public ApiHttpDispatcher(ApiClient apiClient2) {
      this.apiClient = apiClient2;
      this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
      this.httpClient = HttpClient.newHttpClient();
   }

   public final class RequestTask<T> implements Runnable {
      private final ApiRequest apiRequest;
      private final java.net.http.HttpResponse.BodyHandler<T> bodyHandler;
      private final CompletableFuture<HttpResponse<T>> completableFuture;

      public RequestTask(ApiRequest apiRequest2, java.net.http.HttpResponse.BodyHandler<T> bodyHandler2, CompletableFuture<HttpResponse<T>> completableFuture2) {
         this.apiRequest = apiRequest2;
         this.bodyHandler = bodyHandler2;
         this.completableFuture = completableFuture2;
      }

      @Override
      public void run() {
         if (this.apiRequest.getAuthentication() != null) {
            this.apiRequest.getAuthentication().addBearerHeader(this.apiRequest);
         }

         Builder builder = HttpRequest.newBuilder(URI.create(ApiHttpDispatcher.this.apiClient.getRestClientOptions().getBaseUri() + this.apiRequest.getPath()))
            .header("User-Agent", "Novoline-HTTP-Client/1.0");
         if (this.apiRequest.getRequestMethod() == HttpMethod.GET) {
            builder.GET();
         } else {
            builder.method(this.apiRequest.getRequestMethod().name(), this.apiRequest.getBodyPublisher());
         }

         if (this.apiRequest.getRequestMethod() != HttpMethod.GET && this.apiRequest.getRequestBody() != null) {
            String contentType = this.apiRequest.getRequestBody().getContentType();
            builder.header("Content-Type", contentType);
         }

         for (ApiHeader apiHeader : this.apiRequest.getHeaders()) {
            builder.header(apiHeader.getName(), apiHeader.getValue());
         }

         try {
            HttpRequest httpRequest = builder.build();
            HttpResponse httpResponse = ApiHttpDispatcher.this.httpClient.send(httpRequest, this.bodyHandler);
            int i = httpResponse.statusCode() / 100;
            if (i == 4 || i == 5) {
               this.apiRequest.checkResponse(httpResponse);
            }

            this.completableFuture.complete(httpResponse);
         } catch (Exception exception) {
            this.completableFuture.completeExceptionally(exception);
         }
      }
   }
}
