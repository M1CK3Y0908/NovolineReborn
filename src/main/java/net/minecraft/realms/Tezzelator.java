package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;

public class Tezzelator {
   public static Tessellator tessellator = Tessellator.getInstance();
   public static final Tezzelator tezzelator = new Tezzelator();

   public void handleDouble(double d, double d2, double d3) {
      tessellator.getWorldRenderer().setTranslation(d, d2, d3);
   }
}
