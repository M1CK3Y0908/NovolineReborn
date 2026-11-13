package wtf.novoline.api;

public interface Contract1 {
   Contract1 contract1 = new Contract1.Contract1Impl();

   int getFocusGlowColor();

   int getWindowFillColor();

   int getSelectionColor();

   int getContentFillColor();

   int getWindowGlowColor();

   int getPanelFillColor();

   int getDividerColor();

   int getSearchFillColor();

   class Contract1Impl implements Contract1 {
      private final int contentFillColor = -16250872;
      private final int windowGlowColor = -301463544;
      private final int windowFillColor = -569109227;
      private final int panelFillColor = -16053490;
      private final int selectionColor = -583780552;
      private final int dividerColor = -13355465;
      private final int searchFillColor = -15921907;
      private final int focusGlowColor = -15395563;

      @Override
      public int getSelectionColor() {
         return -583780552;
      }

      @Override
      public int getDividerColor() {
         return -13355465;
      }

      @Override
      public int getSearchFillColor() {
         return -15921907;
      }

      @Override
      public int getContentFillColor() {
         return -16250872;
      }

      @Override
      public int getFocusGlowColor() {
         return -15395563;
      }

      @Override
      public int getPanelFillColor() {
         return -16053490;
      }

      @Override
      public int getWindowFillColor() {
         return -569109227;
      }

      @Override
      public int getWindowGlowColor() {
         return -301463544;
      }
   }

   class Contract1Impl2 implements Contract1 {
      private final int contentFillColor = -16185331;
      private final int windowGlowColor = -300407760;
      private final int windowFillColor = -872018407;
      private final int panelFillColor = -16577254;
      private final int selectionColor = 1879074736;
      private final int dividerColor = 1342203824;
      private final int searchFillColor = -15983056;
      private final int focusGlowColor = -1439942055;

      @Override
      public int getPanelFillColor() {
         return -16577254;
      }

      @Override
      public int getSelectionColor() {
         return 1879074736;
      }

      @Override
      public int getSearchFillColor() {
         return -15983056;
      }

      @Override
      public int getContentFillColor() {
         return -16185331;
      }

      @Override
      public int getWindowFillColor() {
         return -872018407;
      }

      @Override
      public int getWindowGlowColor() {
         return -300407760;
      }

      @Override
      public int getDividerColor() {
         return 1342203824;
      }

      @Override
      public int getFocusGlowColor() {
         return -1439942055;
      }
   }
}
