package wtf.novoline.client;

import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.LinkedList;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.utils.http.HttpResponseUtils;
import wtf.novoline.utils.http.RequestBody;
import wtf.novoline.support.SneakyThrow;

public abstract class ApiRequest {
   private String path;
   private HttpMethod requestMethod;
   protected static final RequestBody requestBody = null;
   private Collection<ApiHeader> headers = new LinkedList<>();
   private ApiRequestHandler authentication;

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ApiRequest apiRequest)) {
         return false;
      } else {
         if (!apiRequest.canEqual(this)) {
            return false;
         }

         HttpMethod requestMethod2 = this.getRequestMethod();
         HttpMethod requestMethod3 = apiRequest.getRequestMethod();
         if (requestMethod2 == null ? requestMethod3 == null : requestMethod2.equals(requestMethod3)) {
            String path2 = this.getPath();
            String path3 = apiRequest.getPath();
            if (path2 == null ? path3 == null : path2.equals(path3)) {
               Collection headers2 = this.getHeaders();
               Collection headers3 = apiRequest.getHeaders();
               if (headers2 == null ? headers3 == null : headers2.equals(headers3)) {
                  ApiRequestHandler authentication2 = this.getAuthentication();
                  ApiRequestHandler authentication3 = apiRequest.getAuthentication();
                  return authentication2 == null ? authentication3 == null : authentication2.equals(authentication3);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public int hashCode() {
      HttpMethod requestMethod2 = this.getRequestMethod();
      int i = 59 + (requestMethod2 == null ? 43 : requestMethod2.hashCode());
      String path2 = this.getPath();
      i = i * 59 + (path2 == null ? 43 : path2.hashCode());
      Collection headers2 = this.getHeaders();
      i = i * 59 + (headers2 == null ? 43 : headers2.hashCode());
      ApiRequestHandler authentication2 = this.getAuthentication();
      return i * 59 + (authentication2 == null ? 43 : authentication2.hashCode());
   }

   public String getPath() {
      return this.requestMethod == HttpMethod.GET && this.getRequestBody() != null ? this.path + "?" + this.getRequestBody().getBodyText() : this.path;
   }

   public RequestBody getRequestBody() {
      return requestBody;
   }

   public Collection<ApiHeader> getHeaders() {
      return this.headers;
   }

   protected boolean canEqual(Object object) {
      return object instanceof ApiRequest;
   }

   public ApiRequest() {
   }

   @Override
   public String toString() {
      return "Request(requestMethod="
         + this.getRequestMethod()
         + ", requestEndpoint="
         + this.getPath()
         + ", headers="
         + this.getHeaders()
         + ", authentication="
         + this.getAuthentication()
         + ")";
   }

   public ApiRequest headers(Collection<ApiHeader> collection) {
      this.headers = collection;
      return this;
   }

   public ApiRequest(HttpMethod httpMethod, String text) {
      this.requestMethod = httpMethod;
      this.path = text;
   }

   public ApiRequest withPath(String text) {
      this.path = text;
      return this;
   }

   public HttpMethod getRequestMethod() {
      return this.requestMethod;
   }

   public ApiRequestHandler getAuthentication() {
      return this.authentication;
   }

   public void checkResponse(HttpResponse httpResponse) {
      try {
      HttpResponseUtils.throwIfError(httpResponse);
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public ApiRequest requestMethod(HttpMethod httpMethod) {
      this.requestMethod = httpMethod;
      return this;
   }

   public ApiRequest authentication(ApiRequestHandler apiRequestHandler) {
      this.authentication = apiRequestHandler;
      return this;
   }

   public java.net.http.HttpRequest.BodyPublisher getBodyPublisher() {
      if (this.requestMethod == HttpMethod.GET) {
         return null;
      } else {
         return this.getRequestBody() != null ? this.getRequestBody().bodyPublisher() : java.net.http.HttpRequest.BodyPublishers.noBody();
      }
   }
}
