package wtf.novoline.utils.config;

import com.google.gson.annotations.SerializedName;
import java.time.Instant;

public class ConfigComment {
   @SerializedName("content")
   private String content;
   @SerializedName("editTimestamp")
   private Instant editTimestamp;
   @SerializedName("author")
   private String author;
   @SerializedName("createTimestamp")
   private Instant createTimestamp;
   @SerializedName("id")
   private String id;

   public void setAuthor(String text) {
      this.author = text;
   }

   public Instant getCreateTimestamp() {
      return this.createTimestamp;
   }

   public void setContent(String text) {
      this.content = text;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ConfigComment configComment)) {
         return false;
      } else {
         if (!configComment.canEqual(this)) {
            return false;
         }

         String id2 = this.getId();
         String id3 = configComment.getId();
         if (id2 == null ? id3 == null : id2.equals(id3)) {
            String author2 = this.getAuthor();
            String author3 = configComment.getAuthor();
            if (author2 == null ? author3 == null : author2.equals(author3)) {
               String content2 = this.getContent();
               String content3 = configComment.getContent();
               if (content2 == null ? content3 == null : content2.equals(content3)) {
                  Instant createTimestamp2 = this.getCreateTimestamp();
                  Instant createTimestamp3 = configComment.getCreateTimestamp();
                  if (createTimestamp2 == null ? createTimestamp3 == null : createTimestamp2.equals(createTimestamp3)) {
                     Instant editTimestamp2 = this.getEditTimestamp();
                     Instant editTimestamp3 = configComment.getEditTimestamp();
                     return editTimestamp2 == null ? editTimestamp3 == null : editTimestamp2.equals(editTimestamp3);
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

   protected boolean canEqual(Object object) {
      return object instanceof ConfigComment;
   }

   @Override
   public String toString() {
      return "Comment(id="
         + this.getId()
         + ", author="
         + this.getAuthor()
         + ", content="
         + this.getContent()
         + ", createTimestamp="
         + this.getCreateTimestamp()
         + ", editTimestamp="
         + this.getEditTimestamp()
         + ")";
   }

   public Instant getEditTimestamp() {
      return this.editTimestamp;
   }

   public void setCreateTimestamp(Instant instant) {
      this.createTimestamp = instant;
   }

   public String getId() {
      return this.id;
   }

   @Override
   public int hashCode() {
      String id2 = this.getId();
      int i = 59 + (id2 == null ? 43 : id2.hashCode());
      String author2 = this.getAuthor();
      i = i * 59 + (author2 == null ? 43 : author2.hashCode());
      String content2 = this.getContent();
      i = i * 59 + (content2 == null ? 43 : content2.hashCode());
      Instant createTimestamp2 = this.getCreateTimestamp();
      i = i * 59 + (createTimestamp2 == null ? 43 : createTimestamp2.hashCode());
      Instant editTimestamp2 = this.getEditTimestamp();
      return i * 59 + (editTimestamp2 == null ? 43 : editTimestamp2.hashCode());
   }

   public String getAuthor() {
      return this.author;
   }

   public String getContent() {
      return this.content;
   }

   public void setId(String text) {
      this.id = text;
   }

   public void setEditTimestamp(Instant instant) {
      this.editTimestamp = instant;
   }
}
