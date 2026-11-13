package wtf.novoline.module.hud;

import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.world.Scaffold;
import wtf.novoline.utils.render.FilledPolygonRenderer;

public class BlockCounterElement extends HudElement {
   private final Scaffold scaffold;
   private float halfWidth;
   private final Minecraft minecraft;

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      if (this.scaffold.isEnabled() && this.scaffold.blockCounter.isEnabled()) {
         float f2 = scaledResolution.getFloat() / 2.0F;
         float f3 = scaledResolution.getScaledHeight() - 72 - (scaledResolution.getScaleFactor() != 2 ? 20.0F : 0.0F);
         long l = Arrays.stream(this.minecraft.thePlayer.inventory.mainInventory).filter(Objects::nonNull).filter(itemStack -> {
            Item item2 = itemStack.getItem();
            return item2 instanceof ItemBlock && ((ItemBlock)item2).getBlock().isFullCube();
         }).mapToInt(itemStack -> itemStack.stackSize).sum();
         String text = l + " blocks";
         this.halfWidth = Fonts.rubikSemiboldSize16.getStringWidth(text) / 2.0F + 6.0F;
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(f2 - this.halfWidth, f3, this.halfWidth * 2.0F, 20.0F, 8.0F, Integer.MIN_VALUE);
         filledPolygonRenderer.draw();
         int i;
         if (l > 64L) {
            i = -13710223;
         } else if (l > 16L) {
            i = -12469;
         } else {
            i = -566196;
         }

         Fonts.rubikSemiboldSize16
            .drawString(
               text,
               f2 - Fonts.rubikSemiboldSize16.getStringWidth(text) / 2.0F,
               f3 + 10.0F - Fonts.rubikSemiboldSize16.getHeight() / 2.0F + 1.0F,
               i
            );
      }
   }

   @Override
   public float getX() {
      return this.x - this.getWidth() / 2.0F;
   }

   @Override
   public float getWidth() {
      return this.halfWidth * 2.0F;
   }

   @Override
   public float getHeight() {
      return 20.0F;
   }

   public BlockCounterElement() {
      super("block-counter", 0.0F, 0.0F, false, true);
      this.scaffold = ModuleManager.scaffold;
      this.minecraft = Minecraft.getMinecraft();
   }
}
