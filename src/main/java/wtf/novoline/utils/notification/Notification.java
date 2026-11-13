package wtf.novoline.utils.notification;

public class Notification {
   public float offsetX;
   private final long spawnedTime = System.currentTimeMillis();
   private final NotificationType type;
   public float offsetY;
   private final String title;
   private final String subText;
   private final long lifeTime;

   public long getLifeTime() {
      return this.lifeTime;
   }

   public NotificationType getType() {
      return this.type;
   }

   public String getSubText() {
      return this.subText;
   }

   public String getTitle() {
      return this.title;
   }

   @Override
   public String toString() {
      long spawnedTime2 = this.getSpawnedTime();
      NotificationType type2 = this.getType();
      String title2 = this.getTitle();
      String subText2 = this.getSubText();
      long lifeTime2 = this.getLifeTime();
      float offsetY2 = this.getOffsetY();
      float offsetX2 = this.getOffsetX();
      long l = lifeTime2;
      String text2 = subText2;
      String text3 = title2;
      NotificationType notificationType = type2;
      long l2 = spawnedTime2;
      return "Notification(spawnedTime="
         + l2
         + ", type="
         + notificationType
         + ", text="
         + text3
         + ", subText="
         + text2
         + ", lifeTime="
         + l
         + ", offsetX="
         + offsetX2
         + ", offsetY="
         + offsetY2
         + ")";
   }

   public Notification(NotificationType notificationType, String text2, String text3, long l) {
      this.type = notificationType;
      this.title = text2;
      this.subText = text3;
      this.lifeTime = l;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Notification notification2)) {
         return false;
      } else {
         if (!notification2.canEqual(this)) {
            return false;
         }

         if (this.getSpawnedTime() != notification2.getSpawnedTime()) {
            return false;
         }

         if (this.getLifeTime() != notification2.getLifeTime()) {
            return false;
         }

         if (Float.compare(this.getOffsetX(), notification2.getOffsetX()) != 0) {
            return false;
         }

         if (Float.compare(this.getOffsetY(), notification2.getOffsetY()) != 0) {
            return false;
         }

         NotificationType type2 = this.getType();
         NotificationType type3 = notification2.getType();
         if (type2 == null ? type3 == null : type2.equals(type3)) {
            String title2 = this.getTitle();
            String title3 = notification2.getTitle();
            if (title2 == null ? title3 == null : title2.equals(title3)) {
               String subText2 = this.getSubText();
               String subText3 = notification2.getSubText();
               return subText2 == null ? subText3 == null : subText2.equals(subText3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public long getSpawnedTime() {
      return this.spawnedTime;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Notification;
   }

   public void setOffsetY(float f) {
      this.offsetY = f;
   }

   public float getOffsetY() {
      return this.offsetY;
   }

   public boolean hasExpired() {
      return this.spawnedTime + this.lifeTime + 500L < System.currentTimeMillis();
   }

   public boolean isFinished() {
      return this.spawnedTime + this.lifeTime < System.currentTimeMillis();
   }

   public void setOffsetX(float f) {
      this.offsetX = f;
   }

   @Override
   public int hashCode() {
      long spawnedTime2 = this.getSpawnedTime();
      int i = 59 + (int)(spawnedTime2 >>> 32 ^ spawnedTime2);
      long lifeTime2 = this.getLifeTime();
      i = i * 59 + (int)(lifeTime2 >>> 32 ^ lifeTime2);
      i = i * 59 + Float.floatToIntBits(this.getOffsetX());
      i = i * 59 + Float.floatToIntBits(this.getOffsetY());
      NotificationType type2 = this.getType();
      i = i * 59 + (type2 == null ? 43 : type2.hashCode());
      String title2 = this.getTitle();
      i = i * 59 + (title2 == null ? 43 : title2.hashCode());
      String subText2 = this.getSubText();
      return i * 59 + (subText2 == null ? 43 : subText2.hashCode());
   }

   public float getOffsetX() {
      return this.offsetX;
   }
}
