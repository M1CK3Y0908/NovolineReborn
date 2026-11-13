package net.minecraft.client.resources.data;

public class AnimationFrame {
   private final int frameIndex;
   private final int frameTime;

   public AnimationFrame(int i) {
      this(i, -1);
   }

   public AnimationFrame(int i, int i2) {
      this.frameIndex = i;
      this.frameTime = i2;
   }

   public boolean hasNoTime() {
      return this.frameTime == -1;
   }

   public int getFrameIndex() {
      return this.frameIndex;
   }

   public int getFrameTime() {
      return this.frameTime;
   }
}
