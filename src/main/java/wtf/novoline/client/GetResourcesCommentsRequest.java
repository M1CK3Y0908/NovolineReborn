package wtf.novoline.client;

import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments", httpMethod = HttpMethod.GET)
@Query(QueryType.QUERY)
public class GetResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @PathParam("offset")
   private int offset;
   @PathParam("sortDirection")
   private GetResourcesCommentsRequest.AscendingType ascendingType;

   GetResourcesCommentsRequest(String text, int i, GetResourcesCommentsRequest.AscendingType ascendingType2) {
      this.resourceId = text;
      this.offset = i;
      this.ascendingType = ascendingType2;
   }

   public static GetResourcesCommentsRequest.CommentListRequestBuilder builder() {
      return new GetResourcesCommentsRequest.CommentListRequestBuilder();
   }

   public enum AscendingType {
      ASCENDING,
      DESCENDING;
   }

   public static class CommentListRequestBuilder {
      private int offset;
      private String resourceId;
      private GetResourcesCommentsRequest.AscendingType sortingDirection;

      CommentListRequestBuilder() {
      }

      @Override
      public String toString() {
         return "CommentListRequest.CommentListRequestBuilder(resourceId="
            + this.resourceId
            + ", offset="
            + this.offset
            + ", sortingDirection="
            + this.sortingDirection
            + ")";
      }

      public GetResourcesCommentsRequest.CommentListRequestBuilder resourceId(String text) {
         this.resourceId = text;
         return this;
      }

      public GetResourcesCommentsRequest build() {
         return new GetResourcesCommentsRequest(this.resourceId, this.offset, this.sortingDirection);
      }

      public GetResourcesCommentsRequest.CommentListRequestBuilder sortingDirection(GetResourcesCommentsRequest.AscendingType ascendingType2) {
         this.sortingDirection = ascendingType2;
         return this;
      }
   }
}
