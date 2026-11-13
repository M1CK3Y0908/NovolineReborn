package wtf.novoline.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import java.util.concurrent.Future;

public class ViaPlatformTask implements PlatformTask<Future<?>> {
   private final Future<?> future;

   public Future<?> getObject() {
      return this.future;
   }

   public ViaPlatformTask(Future<?> future2) {
      this.future = future2;
   }

   @Override
   public void cancel() {
      this.future.cancel(false);
   }
}
