package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;
import wtf.novoline.utils.config.CloudScript.ConfigType;

@Route(value = "/resources", httpMethod = HttpMethod.GET)
@Query(QueryType.QUERY)
public class GetResourcesRequest2 extends AnnotatedApiRequest {
   @PathParam("type")
   private final ConfigType configType;
   @PathParam("searchQuery")
   private final String searchQuery;
   @PathParam("sort")
   private final GetResourcesRequest2.ByUpdateDateType byUpdateDateType;
   @PathParam("direction")
   private final GetResourcesRequest2.AscendingType ascendingType;
   @PathParam("offset")
   private final long offset;

   GetResourcesRequest2(ConfigType configType2, String text, GetResourcesRequest2.ByUpdateDateType byUpdateDateType2, GetResourcesRequest2.AscendingType ascendingType2, long l) {
      this.configType = configType2;
      this.searchQuery = text;
      this.byUpdateDateType = byUpdateDateType2;
      this.ascendingType = ascendingType2;
      this.offset = l;
   }

   public static GetResourcesRequest2.ResourceListRequestBuilder builder() {
      return new GetResourcesRequest2.ResourceListRequestBuilder();
   }

   public enum AscendingType {
      ASCENDING,
      DESCENDING;
   }

   public enum ByUpdateDateType {
      BY_UPDATE_DATE,
      BY_CREATE_DATE,
      BY_RATING;
   }

   public static class ResourceListRequestBuilder {
      private long offset;
      private ConfigType type;
      private String searchQuery;
      private GetResourcesRequest2.AscendingType direction;
      private GetResourcesRequest2.ByUpdateDateType sort;

      public GetResourcesRequest2.ResourceListRequestBuilder type(ConfigType configType2) {
         this.type = configType2;
         return this;
      }

      public GetResourcesRequest2.ResourceListRequestBuilder direction(GetResourcesRequest2.AscendingType ascendingType2) {
         this.direction = ascendingType2;
         return this;
      }

      public GetResourcesRequest2.ResourceListRequestBuilder searchQuery(String text) {
         this.searchQuery = text;
         return this;
      }

      public GetResourcesRequest2.ResourceListRequestBuilder offset(long l) {
         this.offset = l;
         return this;
      }

      ResourceListRequestBuilder() {
      }

      @Override
      public String toString() {
         long offset2 = this.offset;
         return "ResourceListRequest.ResourceListRequestBuilder(type="
            + this.type
            + ", searchQuery="
            + this.searchQuery
            + ", sort="
            + this.sort
            + ", direction="
            + this.direction
            + ", offset="
            + offset2
            + ")";
      }

      public GetResourcesRequest2.ResourceListRequestBuilder sort(GetResourcesRequest2.ByUpdateDateType byUpdateDateType2) {
         this.sort = byUpdateDateType2;
         return this;
      }

      public GetResourcesRequest2 build() {
         return new GetResourcesRequest2(this.type, this.searchQuery, this.sort, this.direction, this.offset);
      }
   }
}
