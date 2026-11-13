package wtf.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import wtf.novoline.api.annotation.PathParam;
import wtf.novoline.api.annotation.Query;
import wtf.novoline.api.annotation.Route;
import wtf.novoline.enums.HttpMethod;
import wtf.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/ratings", httpMethod = HttpMethod.POST)
@Query(QueryType.JSON)
public class PostResourcesRatingsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;
   @SerializedName("ratingType")
   @Expose
   private final PostResourcesRatingsRequest.PositiveType ratingType;

   public static PostResourcesRatingsRequest createPostResourcesRatingsRequest(String text, PostResourcesRatingsRequest.PositiveType positiveType) {
      return new PostResourcesRatingsRequest(text, positiveType);
   }

   private PostResourcesRatingsRequest(String text, PostResourcesRatingsRequest.PositiveType positiveType) {
      this.resourceId = text;
      this.ratingType = positiveType;
   }

   public enum PositiveType {
      POSITIVE,
      NEGATIVE;
   }
}
