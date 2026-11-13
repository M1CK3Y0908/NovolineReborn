package wtf.novoline.utils.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MultipartRequestBody implements RequestBody {
   private final String boundary = "-".repeat(24) + "KhTmLbOuNdArY";
   private final List<MultipartRequestBody.Component> parts;
   private static final String CRLF = "\r\n";

   @Override
   public String getBodyText() {
      throw new UnsupportedOperationException();
   }

   @Override
   public java.net.http.HttpRequest.BodyPublisher bodyPublisher() {
      ArrayList arrayList = new ArrayList();
      String text2 = "--" + this.boundary + "\r\nContent-Disposition: form-data; name=";
      this.parts.forEach(component -> {
         arrayList.add(this.toUtf8Bytes(text2));
         if (component instanceof MultipartRequestBody.Holder holder) {
            arrayList.add(this.toUtf8Bytes("\"" + component.getName() + "\"; filename=\"" + holder.getFileName() + "\"\r\nContent-Type: text/plain" + "\r\n".repeat(2)));
         } else {
            arrayList.add(this.toUtf8Bytes("\"" + component.getName() + "\"" + "\r\n".repeat(2)));
         }

         arrayList.add(component.getByteArray());
         arrayList.add(this.toUtf8Bytes("\r\n"));
      });
      arrayList.add(this.toUtf8Bytes("--" + this.boundary + "--"));
      ByteBuffer byteBuffer = ByteBuffer.allocate(arrayList.stream().mapToInt((byteArray2) -> ((byte[]) byteArray2).length).sum());

      for (byte[] bArray : (Iterable<byte[]>) arrayList) {
         byteBuffer.put(bArray);
      }

      return java.net.http.HttpRequest.BodyPublishers.ofByteArray(byteBuffer.array());
   }

   private byte[] toUtf8Bytes(String text2) {
      return text2.getBytes(StandardCharsets.UTF_8);
   }

   public MultipartRequestBody(List list) {
      this.parts = list;
   }

   @Override
   public String getContentType() {
      return "multipart/form-data; boundary=" + this.boundary;
   }

   public abstract static class Component {
      private final String name;
      private final byte[] byteArray;

      public Component(String text2, byte[] bArray) {
         this.name = text2;
         this.byteArray = bArray;
      }

      public byte[] getByteArray() {
         return this.byteArray;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class ComponentChild extends MultipartRequestBody.Component {
      public ComponentChild(String text2, String text3) {
         super(text2, text3.getBytes(StandardCharsets.UTF_8));
      }
   }

   public static class Holder extends MultipartRequestBody.Component {
      public static final String TEXT_PLAIN = "text/plain";
      private final String fileName;

      public Holder(String text2, String text3, byte[] bArray) {
         super(text2, bArray);
         this.fileName = text3;
      }

      public String getFileName() {
         return this.fileName;
      }
   }
}
