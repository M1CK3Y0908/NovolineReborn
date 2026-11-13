package wtf.novoline.utils.account;

public class MicrosoftClientId {
   private String clientId = "00000000402b5328";

   public MicrosoftClientId clientId(String text) {
      this.clientId = text;
      return this;
   }

   @Override
   public String toString() {
      return "MicrosoftServiceConfig(clientId=" + this.getClientId() + ")";
   }

   protected boolean canEqual(Object object) {
      return object instanceof MicrosoftClientId;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof MicrosoftClientId microsoftClientId)) {
         return false;
      } else {
         if (!microsoftClientId.canEqual(this)) {
            return false;
         }

         String clientId2 = this.getClientId();
         String clientId3 = microsoftClientId.getClientId();
         return clientId2 == null ? clientId3 == null : clientId2.equals(clientId3);
      }
   }

   @Override
   public int hashCode() {
      String clientId2 = this.getClientId();
      return 59 + (clientId2 == null ? 43 : clientId2.hashCode());
   }

   public String getClientId() {
      return this.clientId;
   }
}
