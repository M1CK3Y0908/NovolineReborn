package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TileEntityEnchantmentTableRenderer extends TileEntitySpecialRenderer<TileEntityEnchantmentTable> {
   private ModelBook bookModel = new ModelBook();
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");

   public void renderTileEntityAt(TileEntityEnchantmentTable tileEntityEnchantmentTable, double d, double d2, double d3, float f, int unused5) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)d + 0.5F, (float)d2 + 0.75F, (float)d3 + 0.5F);
      float f2 = tileEntityEnchantmentTable.tickCount + f;
      GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f2 * 0.1F) * 0.01F, 0.0F);
      float f3 = tileEntityEnchantmentTable.bookRotation - tileEntityEnchantmentTable.bookRotationPrev;

      while (f3 >= (float) Math.PI) {
         f3 -= (float) (Math.PI * 2);
      }

      while (f3 < (float) -Math.PI) {
         f3 += (float) (Math.PI * 2);
      }

      float f4 = tileEntityEnchantmentTable.bookRotationPrev + f3 * f;
      GlStateManager.rotate(-f4 * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f5 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * f + 0.25F;
      float f6 = tileEntityEnchantmentTable.pageFlipPrev + (tileEntityEnchantmentTable.pageFlip - tileEntityEnchantmentTable.pageFlipPrev) * f + 0.75F;
      f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
      f6 = (f6 - MathHelper.truncateDoubleToInt(f6)) * 1.6F - 0.3F;
      if (f5 < 0.0F) {
         f5 = 0.0F;
      }

      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 1.0F) {
         f5 = 1.0F;
      }

      if (f6 > 1.0F) {
         f6 = 1.0F;
      }

      float f7 = tileEntityEnchantmentTable.bookSpreadPrev + (tileEntityEnchantmentTable.bookSpread - tileEntityEnchantmentTable.bookSpreadPrev) * f;
      GlStateManager.enableCull();
      this.bookModel.render((Entity)null, f2, f5, f6, f7, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
