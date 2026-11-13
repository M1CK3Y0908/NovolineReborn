package wtf.novoline.utils.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import wtf.novoline.enums.HttpMethod2;
import wtf.novoline.support.SneakyThrow;

public abstract class AuthRequest {
   protected static final HttpBody httpBody = null;
   private final HttpMethod2 requestMethod;
   private final URI uRI;

   public HttpMethod2 getRequestMethod() {
      return this.requestMethod;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof AuthRequest authRequest)) {
         return false;
      } else if (!authRequest.canEqual(this)) {
         return false;
      } else {
         HttpMethod2 requestMethod2 = this.getRequestMethod();
         HttpMethod2 requestMethod3 = authRequest.getRequestMethod();
         if (requestMethod2 == null ? requestMethod3 == null : requestMethod2.equals(requestMethod3)) {
            URI uri = this.getURI();
            URI uri2 = authRequest.getURI();
            return uri == null ? uri2 == null : uri.equals(uri2);
         } else {
            return false;
         }
      }
   }

   public java.net.http.HttpRequest.BodyPublisher getBodyPublisher() {
      if (this.requestMethod == HttpMethod2.GET) {
         return null;
      } else {
         return this.getHttpBody() != null ? java.net.http.HttpRequest.BodyPublishers.ofString(this.getHttpBody().getBodyText()) : java.net.http.HttpRequest.BodyPublishers.noBody();
      }
   }

   @Override
   public int hashCode() {
      HttpMethod2 requestMethod2 = this.getRequestMethod();
      int i = 59 + (requestMethod2 == null ? 43 : requestMethod2.hashCode());
      URI uri = this.getURI();
      return i * 59 + (uri == null ? 43 : uri.hashCode());
   }

   public void onFailedResponse(HttpResponse httpResponse) {
      try {
      int i = httpResponse.statusCode();
      throw new IOException("Server returned status code" + i);
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public AuthRequest(HttpMethod2 httpMethod2, URI uri) {
      this.requestMethod = httpMethod2;
      this.uRI = uri;
   }

   public List getHeaders() {
      return Collections.emptyList();
   }

   @Override
   public String toString() {
      return "Request(requestMethod=" + this.getRequestMethod() + ", requestUri=" + this.getURI() + ")";
   }

   public URI getURI() {
      return this.requestMethod == HttpMethod2.GET && this.getHttpBody() != null ? URI.create(this.uRI.toString() + "?" + this.getHttpBody()) : this.uRI;
   }

   protected boolean canEqual(Object object) {
      return object instanceof AuthRequest;
   }

   public HttpBody getHttpBody() {
      return httpBody;
   }
}
