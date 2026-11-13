package net.minecraft.profiler;

public interface IPlayerUsage {
   boolean isSnooperEnabled();

   void addServerStatsToSnooper(PlayerUsageSnooper playerUsageSnooper);

   void addServerTypeToSnooper(PlayerUsageSnooper playerUsageSnooper);
}
