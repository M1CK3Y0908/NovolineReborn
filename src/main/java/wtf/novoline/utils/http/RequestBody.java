package wtf.novoline.utils.http;


public interface RequestBody {
   String getBodyText();

   default java.net.http.HttpRequest.BodyPublisher bodyPublisher() {
      return java.net.http.HttpRequest.BodyPublishers.ofString(this.getBodyText());
   }

   String getContentType();
}
