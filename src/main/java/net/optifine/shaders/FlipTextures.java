package net.optifine.shaders;

import java.nio.IntBuffer;
import java.util.Arrays;

public class FlipTextures {
   private IntBuffer textures;
   private int indexFlipped;
   private boolean[] flips;
   private boolean[] changed;

   public FlipTextures(IntBuffer intBuffer) {
      this.textures = intBuffer;
      this.indexFlipped = 8;
      this.flips = new boolean[intBuffer.capacity()];
      this.changed = new boolean[intBuffer.capacity()];
   }

   public void flip(int index) {
      this.flips[index] = !this.flips[index];
      this.changed[index] = true;
   }

   public int getA(int index) {
      return this.get(index, this.flips[index]);
   }

   public int getB(int index) {
      return this.get(index, !this.flips[index]);
   }

   public boolean isChanged(int index) {
      return this.changed[index];
   }

   private int get(int index, boolean flipped) {
      int i = flipped ? this.indexFlipped : 0;
      return this.textures.get(i + index);
   }

   public void reset() {
      Arrays.fill(this.flips, false);
      Arrays.fill(this.changed, false);
   }
}
