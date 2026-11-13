package wtf.novoline.utils.config;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CloudListResult {
   @SerializedName("amount")
   private long amount;
   @SerializedName("resources")
   private List<CloudScript> resources;
   @SerializedName("remaining")
   private long remaining;
   @SerializedName("overallCount")
   private long overallCount;

   @Override
   public int hashCode() {
      long overallCount2 = this.getOverallCount();
      int i = 59 + (int)(overallCount2 >>> 32 ^ overallCount2);
      long amount2 = this.getAmount();
      i = i * 59 + (int)(amount2 >>> 32 ^ amount2);
      long remaining2 = this.getRemaining();
      i = i * 59 + (int)(remaining2 >>> 32 ^ remaining2);
      List resources2 = this.getResources();
      return i * 59 + (resources2 == null ? 43 : resources2.hashCode());
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof CloudListResult cloudListResult)) {
         return false;
      } else {
         if (!cloudListResult.canEqual(this)) {
            return false;
         }

         if (this.getOverallCount() != cloudListResult.getOverallCount()) {
            return false;
         }

         if (this.getAmount() != cloudListResult.getAmount()) {
            return false;
         }

         if (this.getRemaining() != cloudListResult.getRemaining()) {
            return false;
         }

         List resources2 = this.getResources();
         List resources3 = cloudListResult.getResources();
         return resources2 == null ? resources3 == null : resources2.equals(resources3);
      }
   }

   public long getAmount() {
      return this.amount;
   }

   protected boolean canEqual(Object object) {
      return object instanceof CloudListResult;
   }

   public List<CloudScript> getResources() {
      return this.resources;
   }

   @Override
   public String toString() {
      List resources2 = this.getResources();
      long overallCount2 = this.getOverallCount();
      long remaining2 = this.getRemaining();
      long amount2 = this.getAmount();
      long l = overallCount2;
      return "ResourceListResponse(resources=" + resources2 + ", overallCount=" + l + ", amount=" + amount2 + ", remaining=" + remaining2 + ")";
   }

   public long getRemaining() {
      return this.remaining;
   }

   public long getOverallCount() {
      return this.overallCount;
   }
}
