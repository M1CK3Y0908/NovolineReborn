package wtf.novoline.util;

import com.google.gson.annotations.SerializedName;

public class MCProfileResponse {
   @SerializedName("name")
   private String name;
   @SerializedName("id")
   private String id;

   public void setId(String text) {
      this.id = text;
   }

   public String getId() {
      return this.id;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MCProfileResponse mcProfileResponse)) {
         return false;
      } else if (!mcProfileResponse.canEqual(this)) {
         return false;
      } else {
         String id2 = this.getId();
         String id3 = mcProfileResponse.getId();
         if (id2 == null ? id3 == null : id2.equals(id3)) {
            String name2 = this.getName();
            String name3 = mcProfileResponse.getName();
            return name2 == null ? name3 == null : name2.equals(name3);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object object) {
      return object instanceof MCProfileResponse;
   }

   @Override
   public int hashCode() {
      String id2 = this.getId();
      int i = 59 + (id2 == null ? 43 : id2.hashCode());
      String name2 = this.getName();
      return i * 59 + (name2 == null ? 43 : name2.hashCode());
   }

   public String getName() {
      return this.name;
   }

   @Override
   public String toString() {
      return "MCProfileResponse(id=" + this.getId() + ", name=" + this.getName() + ")";
   }

   public void setName(String text) {
      this.name = text;
   }
}
