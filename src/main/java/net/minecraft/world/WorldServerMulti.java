package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   private WorldServer delegate;

   @Override
   protected void saveLevel() throws MinecraftException {
   }

   public WorldServerMulti(MinecraftServer server, ISaveHandler saveHandlerIn, int dimensionId, WorldServer delegate, Profiler profilerIn) {
      super(server, saveHandlerIn, new DerivedWorldInfo(delegate.getWorldInfo()), dimensionId, profilerIn);
      this.delegate = delegate;
      delegate.getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onCenterChanged(WorldBorder border, double x, double z) {
            WorldServerMulti.this.getWorldBorder().setCenter(x, z);
         }

         @Override
         public void onDamageAmountChanged(WorldBorder worldBorder, double d) {
            WorldServerMulti.this.getWorldBorder().setDamageAmount(d);
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder worldBorder2, int i) {
            WorldServerMulti.this.getWorldBorder().setWarningDistance(i);
         }

         @Override
         public void onDamageBufferChanged(WorldBorder worldBorder3, double d2) {
            WorldServerMulti.this.getWorldBorder().setDamageBuffer(d2);
         }

         @Override
         public void onWarningTimeChanged(WorldBorder worldBorder4, int i2) {
            WorldServerMulti.this.getWorldBorder().setWarningTime(i2);
         }

         @Override
         public void onSizeChanged(WorldBorder worldBorder5, double d3) {
            WorldServerMulti.this.getWorldBorder().setTransition(d3);
         }

         @Override
         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            WorldServerMulti.this.getWorldBorder().setTransition(oldSize, newSize, time);
         }
      });
   }

   @Override
   public World init() {
      this.mapStorage = this.delegate.getMapStorage();
      this.worldScoreboard = this.delegate.getScoreboard();
      String text = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection villageCollection = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, text);
      if (villageCollection == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData(text, this.villageCollectionObj);
      } else {
         this.villageCollectionObj = villageCollection;
         this.villageCollectionObj.setWorldsForAll(this);
      }

      return this;
   }
}
