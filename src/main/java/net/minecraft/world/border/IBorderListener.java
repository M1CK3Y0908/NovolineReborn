package net.minecraft.world.border;

public interface IBorderListener {
   void onTransitionStarted(WorldBorder worldBorder, double d, double d2, long l);

   void onSizeChanged(WorldBorder worldBorder, double d);

   void onWarningTimeChanged(WorldBorder worldBorder, int i);

   void onDamageAmountChanged(WorldBorder worldBorder, double d);

   void onDamageBufferChanged(WorldBorder worldBorder, double d);

   void onCenterChanged(WorldBorder worldBorder, double d, double d2);

   void onWarningDistanceChanged(WorldBorder worldBorder, int i);
}
