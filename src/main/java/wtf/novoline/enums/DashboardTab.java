package wtf.novoline.enums;

public enum DashboardTab {
   CONFIGS("Configs"),
   SCRIPTS("Scripts");

   private final String label;

   public String getLabel() {
      return this.label;
   }

   DashboardTab(String text) {
      this.label = text;
   }
}
