package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments/{commentId}", httpMethod = HttpMethod.PATCH)
@Query(QueryType.JSON)
public class PatchResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @PathParam(value = "commentId", enabled = true)
   private String commentId;
   @SerializedName("content")
   @Expose
   private String content;

   public static PatchResourcesCommentsRequest.EditCommentRequestBuilder builder() {
      return new PatchResourcesCommentsRequest.EditCommentRequestBuilder();
   }

   public static class EditCommentRequestBuilder {
      private String content;
      private String commentId;
      private String resourceId;

      public PatchResourcesCommentsRequest.EditCommentRequestBuilder content(String text) {
         this.content = text;
         return this;
      }

      public PatchResourcesCommentsRequest.EditCommentRequestBuilder resourceId(String text) {
         this.resourceId = text;
         return this;
      }

      @Override
      public String toString() {
         return "EditCommentRequest.EditCommentRequestBuilder(resourceId="
            + this.resourceId
            + ", commentId="
            + this.commentId
            + ", content="
            + this.content
            + ")";
      }

      EditCommentRequestBuilder() {
      }

      public PatchResourcesCommentsRequest.EditCommentRequestBuilder commentId(String text) {
         this.commentId = text;
         return this;
      }
   }
}
