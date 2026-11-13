package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimationMetadataSection implements IMetadataSection {
   private final List<AnimationFrame> animationFrames;
   private final int frameWidth;
   private final int frameHeight;
   private final int frameTime;
   private final boolean interpolate;

   public int getFrameHeight() {
      return this.frameHeight;
   }

   public AnimationMetadataSection(List<AnimationFrame> list, int i, int i2, int i3, boolean flag) {
      this.animationFrames = list;
      this.frameWidth = i;
      this.frameHeight = i2;
      this.frameTime = i3;
      this.interpolate = flag;
   }

   public int getFrameWidth() {
      return this.frameWidth;
   }

   public int getFrameTimeSingle(int i) {
      AnimationFrame animationFrame = this.getAnimationFrame(i);
      return animationFrame.hasNoTime() ? this.frameTime : animationFrame.getFrameTime();
   }

   private AnimationFrame getAnimationFrame(int i) {
      return this.animationFrames.get(i);
   }

   public boolean isInterpolate() {
      return this.interpolate;
   }

   public int getFrameTime() {
      return this.frameTime;
   }

   public int getFrameCount() {
      return this.animationFrames.size();
   }

   public int getFrameIndex(int i) {
      return this.animationFrames.get(i).getFrameIndex();
   }

   public boolean frameHasTime(int i) {
      return !this.animationFrames.get(i).hasNoTime();
   }

   public Set<Integer> getFrameIndexSet() {
      HashSet hashSet = Sets.newHashSet();

      for (AnimationFrame animationFrame2 : this.animationFrames) {
         hashSet.add(animationFrame2.getFrameIndex());
      }

      return hashSet;
   }
}
