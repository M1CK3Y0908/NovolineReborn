package wtf.novoline.utils.hypixel;

import java.util.concurrent.CompletableFuture;
import wtf.novoline.support.SneakyThrow;

public class HypixelApiClient {
   private final HypixelApi hypixelApi = new HypixelApi();

   public HypixelApi getHypixelApi() {
      return this.hypixelApi;
   }

   public CompletableFuture<HypixelPlayer> getPlayer(String text) {
      return CompletableFuture.supplyAsync(() -> this.getHypixelPlayer(text));
   }

   private PunishmentStats getPunishmentStats() {
      try {
      return this.hypixelApi.getPunishmentStats();
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }

   public CompletableFuture<PunishmentStats> getCompletableFuture() {
      return CompletableFuture.supplyAsync(this::getPunishmentStats);
   }

   private HypixelPlayer getHypixelPlayer(String text) {
      try {
      return this.hypixelApi.getHypixelPlayer(text);
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }
}
