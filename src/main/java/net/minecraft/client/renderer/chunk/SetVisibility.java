package net.minecraft.client.renderer.chunk;

import java.util.Set;
import net.minecraft.util.EnumFacing;

public class SetVisibility {
   private static final int COUNT_FACES = EnumFacing.values().length;
   private long bits;

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.getBit(facing.ordinal() + facing2.ordinal() * COUNT_FACES);
   }

   public void setManyVisible(Set<EnumFacing> set) {
      for (EnumFacing enumFacing : set) {
         for (EnumFacing enumFacing2 : set) {
            this.handleEnumFacing(enumFacing, enumFacing2);
         }
      }
   }

   private void handleInt2(int i) {
      this.bits |= 1 << i;
   }

   private boolean getBit(int arg1) {
      return (this.bits & 1 << arg1) != 0L;
   }

   public void handleEnumFacing(EnumFacing enumFacing, EnumFacing enumFacing2) {
      this.handleInt(enumFacing.ordinal() + enumFacing2.ordinal() * COUNT_FACES);
      this.handleInt(enumFacing2.ordinal() + enumFacing.ordinal() * COUNT_FACES);
   }

   private void handleInt3(int i) {
      this.bits &= ~(1 << i);
   }

   private void handleInt(int i) {
      this.handleInt2(i);
   }

   @Override
   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(' ');

      for (EnumFacing enumFacing : EnumFacing.values()) {
         stringBuilder.append(' ').append(enumFacing.toString().toUpperCase().charAt(0));
      }

      stringBuilder.append('\n');

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         stringBuilder.append(enumFacing2.toString().toUpperCase().charAt(0));

         for (EnumFacing enumFacing3 : EnumFacing.values()) {
            if (enumFacing2 == enumFacing3) {
               stringBuilder.append("  ");
            } else {
               boolean visible2 = this.isVisible(enumFacing2, enumFacing3);
               stringBuilder.append(' ').append((char)(visible2 ? 'Y' : 'n'));
            }
         }

         stringBuilder.append('\n');
      }

      return stringBuilder.toString();
   }

   public void setAllVisible(boolean visible) {
      if (visible) {
         this.bits = -1L;
      } else {
         this.bits = 0L;
      }
   }
}
