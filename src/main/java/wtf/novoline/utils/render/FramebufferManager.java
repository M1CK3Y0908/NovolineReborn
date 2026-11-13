package wtf.novoline.utils.render;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringJoiner;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferManager {
   private ArrayDeque<Framebuffer> arrayDeque = new ArrayDeque<>();
   private final Set<BlurFramebuffer> acquired = new HashSet<>();

   public BlurFramebuffer acquire(String text, int i) {
      Framebuffer framebuffer2;
      if (this.arrayDeque.isEmpty()) {
         framebuffer2 = RenderUtils.ensureFramebuffer(null);
      } else {
         framebuffer2 = this.arrayDeque.removeLast();
      }

      float[] fArray = RenderUtils.unpackColorNormalized(i);
      framebuffer2.setFramebufferColor(fArray[0], fArray[1], fArray[2], fArray[3]);
      framebuffer2.framebufferClear();
      BlurFramebuffer blurFramebuffer = new BlurFramebuffer(this, framebuffer2, text);
      this.acquired.add(blurFramebuffer);
      return blurFramebuffer;
   }

   public void release(BlurFramebuffer blurFramebuffer) {
      if (!this.acquired.remove(blurFramebuffer)) {
         throw new IllegalStateException("Given framebuffer is not obtained from this pool");
      }

      this.arrayDeque.addLast(blurFramebuffer.getFramebuffer());
   }

   public int getFreeCount() {
      return this.arrayDeque.size();
   }

   public int getAcquiredCount() {
      return this.acquired.size();
   }

   public void assertAllReleased() {
      if (!this.acquired.isEmpty()) {
         StringJoiner stringJoiner = new StringJoiner(",");

         for (BlurFramebuffer blurFramebuffer : this.acquired) {
            stringJoiner.add(blurFramebuffer.getLabel());
         }

         throw new IllegalStateException("Pool has some obtained framebuffers: " + stringJoiner.toString());
      }
   }

   public int getTotalCount() {
      return this.getAcquiredCount() + this.getFreeCount();
   }

   public BlurFramebuffer acquire(String text) {
      return this.acquire(text, 16777215);
   }

   public BlurFramebuffer reuseOrAcquire(BlurFramebuffer blurFramebuffer, String text) {
      if (blurFramebuffer != null) {
         blurFramebuffer.clear();
         blurFramebuffer.setLabel(text);
         return blurFramebuffer;
      } else {
         return this.acquire(text, 16777215);
      }
   }

   public void deleteFreeFramebuffers() {
      this.assertAllReleased();
      Iterator iterator2 = this.arrayDeque.iterator();

      while (iterator2.hasNext()) {
         ((Framebuffer)iterator2.next()).deleteFramebuffer();
      }

      this.arrayDeque.clear();
   }

   public void resizeFreeFramebuffers() {
      this.assertAllReleased();
      ArrayDeque arrayDeque2 = this.arrayDeque;
      this.arrayDeque = new ArrayDeque<>();

      for (Framebuffer framebuffer2 : (Iterable<Framebuffer>) arrayDeque2) {
         this.arrayDeque.add(RenderUtils.ensureFramebuffer(framebuffer2));
      }
   }
}
