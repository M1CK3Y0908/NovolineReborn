package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/resources", httpMethod = HttpMethod.PUT)
@Query(QueryType.JSON)
public class PutResourcesRequest extends AnnotatedApiRequest {
   @SerializedName("type")
   @Expose
   private final PutResourcesRequest.ConfigType type;
   @SerializedName("name")
   @Expose
   private final String name;
   @SerializedName("shortDescription")
   @Expose
   private final String shortDescription;
   @SerializedName("longDescription")
   @Expose
   private final String longDescription;

   PutResourcesRequest(PutResourcesRequest.ConfigType configType, String text, String text2, String text3) {
      this.type = configType;
      this.name = text;
      this.shortDescription = text2;
      this.longDescription = text3;
   }

   public static PutResourcesRequest.AddResourceRequestBuilder builder() {
      return new PutResourcesRequest.AddResourceRequestBuilder();
   }

   public static class AddResourceRequestBuilder {
      private String name;
      private String shortDescription;
      private PutResourcesRequest.ConfigType type;
      private String longDescription;

      public PutResourcesRequest.AddResourceRequestBuilder shortDescription(String text) {
         this.shortDescription = text;
         return this;
      }

      public PutResourcesRequest.AddResourceRequestBuilder name(String text) {
         this.name = text;
         return this;
      }

      public PutResourcesRequest.AddResourceRequestBuilder type(PutResourcesRequest.ConfigType configType) {
         this.type = configType;
         return this;
      }

      @Override
      public String toString() {
         return "AddResourceRequest.AddResourceRequestBuilder(type="
            + this.type
            + ", name="
            + this.name
            + ", shortDescription="
            + this.shortDescription
            + ", longDescription="
            + this.longDescription
            + ")";
      }

      public PutResourcesRequest build() {
         return new PutResourcesRequest(this.type, this.name, this.shortDescription, this.longDescription);
      }

      AddResourceRequestBuilder() {
      }

      public PutResourcesRequest.AddResourceRequestBuilder longDescription(String text) {
         this.longDescription = text;
         return this;
      }
   }

   public enum ConfigType {
      CONFIG,
      SCRIPT;
   }
}
