package wtf.novoline.platform;

import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.PlatformTask;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.File;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import wtf.novoline.logging.ViaLoggerAdapter;
import wtf.novoline.viaversion.ViaAPIImpl;

public class ViaPlatformImpl implements ViaPlatform<UUID> {
   private final Logger logger = new ViaLoggerAdapter(LogManager.getLogger("ViaVersion"));
   private final ViaConfig viaConfig;
   private final File file;
   private final ViaAPI<UUID> viaAPI;

   @Override
   public void onReload() {
      this.logger.info("ViaVersion was reloaded? (How did that happen)");
   }

   @Override
   public PlatformTask runSync(Runnable runnable, long l) {
      return new ViaPlatformTask(
         ViaMCP.getViaMCP().getEventLoop().schedule(() -> this.runSync(runnable), l * 50L, TimeUnit.MILLISECONDS).addListener(this.errorLogger())
      );
   }

   @Override
   public boolean isPluginEnabled() {
      return true;
   }

   public ViaPlatformTask runSync(Runnable runnable) {
      return new ViaPlatformTask(ViaMCP.getViaMCP().getEventLoop().submit(runnable).addListener(this.errorLogger()));
   }

   public static String legacyToJson(String text2) {
      return GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(text2));
   }

   public ViaPlatformImpl(File file2) {
      Path path = file2.toPath().resolve("ViaVersion");
      this.viaConfig = new ViaConfig(path.resolve("viaversion.yml").toFile());
      this.file = path.toFile();
      this.viaAPI = new ViaAPIImpl();
   }

   @Override
   public String getPluginVersion() {
      return "4.1.0";
   }

   @Override
   public File getDataFolder() {
      return this.file;
   }

   @Override
   public void sendMessage(UUID uuid, String text2) {
   }

   public ViaPlatformTask runAsync(Runnable runnable) {
      return new ViaPlatformTask(CompletableFuture.runAsync(runnable, ViaMCP.getViaMCP().getExecutorService()).exceptionally(throwable -> {
         if (!(throwable instanceof CancellationException)) {
            throwable.printStackTrace();
         }

         return null;
      }));
   }

   @Override
   public ViaCommandSender[] getOnlinePlayers() {
      return new ViaCommandSender[1337];
   }

   @Override
   public Logger getLogger() {
      return this.logger;
   }

   @Override
   public String getPlatformVersion() {
      return String.valueOf(47);
   }

   @Override
   public String getPlatformName() {
      return "ViaMCP";
   }

   @Override
   public ViaAPI<UUID> getApi() {
      return this.viaAPI;
   }

   @Override
   public JsonObject getDump() {
      return new JsonObject();
   }

   private ViaCommandSender[] getArray() {
      return new ViaCommandSender[1337];
   }

   @Override
   public boolean kickPlayer(UUID uuid, String text2) {
      return false;
   }

   @Override
   public ViaVersionConfig getConf() {
      return this.viaConfig;
   }

   @Override
   public ConfigurationProvider getConfigurationProvider() {
      return this.viaConfig;
   }

   private <T extends Future<?>> GenericFutureListener<T> errorLogger() {
      return future -> {
         if (!future.isCancelled() && future.cause() != null) {
            future.cause().printStackTrace();
         }
      };
   }

   @Override
   public boolean isOldClientsAllowed() {
      return true;
   }

   @Override
   public PlatformTask runRepeatingSync(Runnable runnable, long l) {
      return new ViaPlatformTask(
         ViaMCP.getViaMCP().getEventLoop().scheduleAtFixedRate(() -> this.runSync(runnable), 0L, l * 50L, TimeUnit.MILLISECONDS).addListener(this.errorLogger())
      );
   }
}
