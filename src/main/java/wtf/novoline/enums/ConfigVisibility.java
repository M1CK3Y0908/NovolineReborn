package wtf.novoline.enums;

public enum ConfigVisibility {
   LOCAL("Local"),
   PUBLIC("Public");

   private final String label;

   ConfigVisibility(String text) {
      this.label = text;
   }

   public String getLabel() {
      return this.label;
   }
}
