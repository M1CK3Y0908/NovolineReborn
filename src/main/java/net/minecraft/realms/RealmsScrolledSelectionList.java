package net.minecraft.realms;

import net.minecraft.client.gui.GuiSlotRealmsProxy;

public class RealmsScrolledSelectionList {
   private GuiSlotRealmsProxy guiSlotRealmsProxy;

   public int getInt3() {
      return this.guiSlotRealmsProxy.getWidth();
   }

   public void noop(int i, int i2, int i3, float f, int i4) {
   }

   public int getInt() {
      return this.guiSlotRealmsProxy.getMouseY();
   }

   public void noop3() {
   }

   public int getInt6() {
      return this.guiSlotRealmsProxy.getMouseX();
   }

   public int getInt4() {
      return this.guiSlotRealmsProxy.getWidth() / 2 + 124;
   }

   protected void noop4(Tezzelator tezzelator2) {
   }

   public boolean alwaysFalse() {
      return false;
   }

   public void noop5(Tezzelator tezzelator2) {
   }

   public int getInt2() {
      return 0;
   }

   public int getInt7() {
      return this.guiSlotRealmsProxy.getAmountScrolled();
   }

   public int getInt5() {
      return 0;
   }

   public void noop2() {
   }

   public void update() {
      this.noop4(Tezzelator.tezzelator);
   }
}
