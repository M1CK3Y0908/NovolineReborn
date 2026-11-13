package wtf.novoline.platform;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import wtf.novoline.logging.ViaLoggerAdapter;

public class ViaMCP {
   private final Logger logger = new ViaLoggerAdapter(LogManager.getLogger("ViaMCP"));
   private File file;
   public static final int NATIVE_VERSION = 47;
   private final CompletableFuture<Void> completableFuture = new CompletableFuture<>();
   private int version;
   private String lastServer;
   private EventLoop eventLoop;
   private ExecutorService executorService;
   private static final ViaMCP viaMCP = new ViaMCP();

   public Logger getLogger() {
      return this.logger;
   }

   public CompletableFuture<Void> getCompletableFuture() {
      return this.completableFuture;
   }

   public ExecutorService getExecutorService() {
      return this.executorService;
   }

   public void start() {
      ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaMCP-%d").build();
      this.executorService = Executors.newFixedThreadPool(8, threadFactory);
      this.eventLoop = new LocalEventLoopGroup(1, threadFactory).next();
      this.eventLoop.submit(this.completableFuture::join);
      this.setVersion(47);
      this.file = new File("ViaMCP");
      if (this.file.mkdir()) {
         this.getLogger().info("Creating ViaMCP Folder");
      }

      Via.init(ViaManagerImpl.builder().injector(new ViaInjectorImpl()).loader(new ViaPlatformLoaderImpl()).platform(new ViaPlatformImpl(this.file)).build());
      MappingDataLoader.enableMappingsCache();
      ((ViaManagerImpl)Via.getManager()).init();
      new ViaBackwardsPlatformImpl(this.file);
      new ViaRewindPlatformImpl(this.file);
      this.completableFuture.complete(null);
   }

   public void setVersion(int i) {
      this.version = i;
   }

   public File getFile() {
      return this.file;
   }

   public void setLastServer(String text) {
      this.lastServer = text;
   }

   public void setFile(File file2) {
      this.file = file2;
   }

   public EventLoop getEventLoop() {
      return this.eventLoop;
   }

   public static ViaMCP getViaMCP() {
      return viaMCP;
   }

   public int getVersion() {
      return this.version;
   }

   public String getLastServer() {
      return this.lastServer;
   }
}
