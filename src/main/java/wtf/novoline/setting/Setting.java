package wtf.novoline.setting;

public abstract class Setting {
   private final Setting.FeatureType settingType;
   private final String name;
   private final String signature;

   public Setting.FeatureType getSettingType() {
      return this.settingType;
   }

   public String getSignature() {
      return this.signature;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Setting setting2)) {
         return false;
      } else {
         if (!setting2.canEqual(this)) {
            return false;
         }

         Setting.FeatureType settingType2 = this.getSettingType();
         Setting.FeatureType settingType3 = setting2.getSettingType();
         if (settingType2 == null ? settingType3 == null : settingType2.equals(settingType3)) {
            String name2 = this.getName();
            String name3 = setting2.getName();
            if (name2 == null ? name3 == null : name2.equals(name3)) {
               String signature2 = this.getSignature();
               String signature3 = setting2.getSignature();
               return signature2 == null ? signature3 == null : signature2.equals(signature3);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public Setting(Setting.FeatureType featureType, String text, String text2) {
      this.settingType = featureType;
      this.name = text;
      this.signature = text2;
   }

   @Override
   public String toString() {
      return "Setting(settingType=" + this.getSettingType() + ", name=" + this.getName() + ", signature=" + this.getSignature() + ")";
   }

   @Override
   public int hashCode() {
      Setting.FeatureType settingType2 = this.getSettingType();
      int i = 59 + (settingType2 == null ? 43 : settingType2.hashCode());
      String name2 = this.getName();
      i = i * 59 + (name2 == null ? 43 : name2.hashCode());
      String signature2 = this.getSignature();
      return i * 59 + (signature2 == null ? 43 : signature2.hashCode());
   }

   protected boolean canEqual(Object object) {
      return object instanceof Setting;
   }

   public enum FeatureType {
      FEATURE;
   }
}
