package wtf.novoline.utils.config;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;

public class CloudScript {
   @SerializedName("negativeRatings")
   private long negativeRatings;
   @SerializedName("createTime")
   private Instant createTime;
   @SerializedName("uploader")
   private String uploader;
   @SerializedName("positiveRatings")
   private long positiveRatings;
   @SerializedName("longDescription")
   private String longDescription;
   @SerializedName("totalRatings")
   private long totalRatings;
   @SerializedName("ownRatingPower")
   private int ownRatingPower;
   @SerializedName("shortDescription")
   private String shortDescription;
   @SerializedName("updateTime")
   private Instant updateTime;
   @SerializedName("id")
   private String id;
   @SerializedName("type")
   private CloudScript.ConfigType type;
   @SerializedName("name")
   private String name;

   public String getUploader() {
      return this.uploader;
   }

   public Instant getUpdateTime() {
      return this.updateTime;
   }

   public void setType(CloudScript.ConfigType configType) {
      this.type = configType;
   }

   public void setUpdateTime(Instant instant) {
      this.updateTime = instant;
   }

   public void setShortDescription(String text) {
      this.shortDescription = text;
   }

   public void setName(String text) {
      this.name = text;
   }

   @Override
   public String toString() {
      String id2 = this.getId();
      CloudScript.ConfigType type2 = this.getType();
      String uploader2 = this.getUploader();
      String name2 = this.getName();
      String shortDescription2 = this.getShortDescription();
      String longDescription2 = this.getLongDescription();
      Instant createTime2 = this.getCreateTime();
      Instant updateTime2 = this.getUpdateTime();
      long totalRatings2 = this.getTotalRatings();
      long positiveRatings2 = this.getPositiveRatings();
      int ownRatingPower2 = this.getOwnRatingPower();
      long negativeRatings2 = this.getNegativeRatings();
      long l = positiveRatings2;
      long l2 = totalRatings2;
      return "Resource(id="
         + id2
         + ", type="
         + type2
         + ", uploader="
         + uploader2
         + ", name="
         + name2
         + ", shortDescription="
         + shortDescription2
         + ", longDescription="
         + longDescription2
         + ", createTime="
         + createTime2
         + ", updateTime="
         + updateTime2
         + ", totalRatings="
         + l2
         + ", positiveRatings="
         + l
         + ", negativeRatings="
         + negativeRatings2
         + ", ownRatingPower="
         + ownRatingPower2
         + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof CloudScript;
   }

   public void setUploader(String text) {
      this.uploader = text;
   }

   public void setId(String text) {
      this.id = text;
   }

   public String getShortDescription() {
      return this.shortDescription;
   }

   public long getTotalRatings() {
      return this.totalRatings;
   }

   public void setLongDescription(String text) {
      this.longDescription = text;
   }

   public Instant getCreateTime() {
      return this.createTime;
   }

   public String getLongDescription() {
      return this.longDescription;
   }

   public long getPositiveRatings() {
      return this.positiveRatings;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof CloudScript cloudScript)) {
         return false;
      } else {
         if (!cloudScript.canEqual(this)) {
            return false;
         }

         if (this.getTotalRatings() != cloudScript.getTotalRatings()) {
            return false;
         }

         if (this.getPositiveRatings() != cloudScript.getPositiveRatings()) {
            return false;
         }

         if (this.getNegativeRatings() != cloudScript.getNegativeRatings()) {
            return false;
         }

         if (this.getOwnRatingPower() != cloudScript.getOwnRatingPower()) {
            return false;
         }

         String id2 = this.getId();
         String id3 = cloudScript.getId();
         if (id2 == null ? id3 == null : id2.equals(id3)) {
            CloudScript.ConfigType type2 = this.getType();
            CloudScript.ConfigType type3 = cloudScript.getType();
            if (type2 == null ? type3 == null : type2.equals(type3)) {
               String uploader2 = this.getUploader();
               String uploader3 = cloudScript.getUploader();
               if (uploader2 == null ? uploader3 == null : uploader2.equals(uploader3)) {
                  String name2 = this.getName();
                  String name3 = cloudScript.getName();
                  if (name2 == null ? name3 == null : name2.equals(name3)) {
                     String shortDescription2 = this.getShortDescription();
                     String shortDescription3 = cloudScript.getShortDescription();
                     if (shortDescription2 == null ? shortDescription3 == null : shortDescription2.equals(shortDescription3)) {
                        String longDescription2 = this.getLongDescription();
                        String longDescription3 = cloudScript.getLongDescription();
                        if (longDescription2 == null ? longDescription3 == null : longDescription2.equals(longDescription3)) {
                           Instant createTime2 = this.getCreateTime();
                           Instant createTime3 = cloudScript.getCreateTime();
                           if (createTime2 == null ? createTime3 == null : createTime2.equals(createTime3)) {
                              Instant updateTime2 = this.getUpdateTime();
                              Instant updateTime3 = cloudScript.getUpdateTime();
                              return updateTime2 == null ? updateTime3 == null : updateTime2.equals(updateTime3);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
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

   public String getName() {
      return this.name;
   }

   public void setCreateTime(Instant instant) {
      this.createTime = instant;
   }

   public CloudScript.ConfigType getType() {
      return this.type;
   }

   public long getNegativeRatings() {
      return this.negativeRatings;
   }

   public String getId() {
      return this.id;
   }

   public int getOwnRatingPower() {
      return this.ownRatingPower;
   }

   @Override
   public int hashCode() {
      long totalRatings2 = this.getTotalRatings();
      int i = 59 + (int)(totalRatings2 >>> 32 ^ totalRatings2);
      long positiveRatings2 = this.getPositiveRatings();
      i = i * 59 + (int)(positiveRatings2 >>> 32 ^ positiveRatings2);
      long negativeRatings2 = this.getNegativeRatings();
      i = i * 59 + (int)(negativeRatings2 >>> 32 ^ negativeRatings2);
      i = i * 59 + this.getOwnRatingPower();
      String id2 = this.getId();
      i = i * 59 + (id2 == null ? 43 : id2.hashCode());
      CloudScript.ConfigType type2 = this.getType();
      i = i * 59 + (type2 == null ? 43 : type2.hashCode());
      String uploader2 = this.getUploader();
      i = i * 59 + (uploader2 == null ? 43 : uploader2.hashCode());
      String name2 = this.getName();
      i = i * 59 + (name2 == null ? 43 : name2.hashCode());
      String shortDescription2 = this.getShortDescription();
      i = i * 59 + (shortDescription2 == null ? 43 : shortDescription2.hashCode());
      String longDescription2 = this.getLongDescription();
      i = i * 59 + (longDescription2 == null ? 43 : longDescription2.hashCode());
      Instant createTime2 = this.getCreateTime();
      i = i * 59 + (createTime2 == null ? 43 : createTime2.hashCode());
      Instant updateTime2 = this.getUpdateTime();
      return i * 59 + (updateTime2 == null ? 43 : updateTime2.hashCode());
   }

   public enum ConfigType {
      CONFIG,
      SCRIPT;
   }
}
