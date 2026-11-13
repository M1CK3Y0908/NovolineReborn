package wtf.novoline.util;

public class ViaSettings {
   private boolean ignoreProtocolErrors = false;

   @Override
   public int hashCode() {
      return 59 + (this.isIgnoreProtocolErrors() ? 79 : 97);
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ViaSettings viaSettings2)) {
         return false;
      } else {
         return !viaSettings2.canEqual(this) ? false : this.isIgnoreProtocolErrors() == viaSettings2.isIgnoreProtocolErrors();
      }
   }

   public boolean isIgnoreProtocolErrors() {
      return this.ignoreProtocolErrors;
   }

   protected boolean canEqual(Object object) {
      return object instanceof ViaSettings;
   }

   public void setIgnoreProtocolErrors(boolean flag) {
      this.ignoreProtocolErrors = flag;
   }

   @Override
   public String toString() {
      return "ViaSettings(ignoreProtocolErrors=" + this.isIgnoreProtocolErrors() + ")";
   }

   public static ViaSettings getViaSettings() {
      return ViaSettings.InstanceType.INSTANCE.viaSettings;
   }

   public enum InstanceType {
      INSTANCE;

      private final ViaSettings viaSettings = new ViaSettings();
   }
}
