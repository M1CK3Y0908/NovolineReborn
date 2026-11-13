package wtf.novoline.scripting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.tinylog.Logger;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.scripting.ScriptLoadState.LoadedType;

public class ScriptWatcherThread extends Thread {
   private final MutableMap<Path, WatchKey> mutableMap = Maps.mutable.empty();
   private final WatchService watchService;
   private final ScriptManager scriptManager;

   public MutableMap<Path, WatchKey> getMutableMap() {
      return this.mutableMap;
   }

   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   @Override
   public void run() {
      while (!Thread.interrupted()) {
         WatchKey watchKey;
         try {
            watchKey = this.watchService.take();
         } catch (InterruptedException interruptedException) {
            return;
         }

         for (WatchEvent watchEvent : watchKey.pollEvents()) {
            WatchEvent watchEvent2 = watchEvent;
            Path path = Directories.SCRIPTS_DIR.resolve((Path)watchEvent2.context());
            if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
               try {
                  this.scriptManager.stream().filter(script -> script.getLoadingStatus().getLoadedType() != LoadedType.UNLOADED).filter(script -> {
                     try {
                        return Files.isSameFile(script.getPath(), path);
                     } catch (IOException ioException) {
                        return false;
                     }
                  }).findFirst().ifPresent(script -> {
                     try {
                        script.setCode(Files.readString(path));
                        this.scriptManager.unloadScript(script);
                        this.scriptManager.loadScript(script, false);
                        Logger.info("Script {} was reloaded due to a Filesystem change", new Object[]{script.getDisplayName()});
                     } catch (Throwable throwable) {
                        ScriptErrorReporter.reportLoadFailure(script, throwable);
                        Logger.error(throwable, "Unable to reload Script {}", script.getDisplayName());
                     }
                  });
               } catch (Throwable throwable2) {
                  throwable2.printStackTrace();
               }
            } else if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
               try {
                  Script script2 = this.scriptManager.getScript(path);
                  script2.setLoadingStatus(ScriptLoadState.unloaded());
                  this.scriptManager.add(script2);
               } catch (IOException ioException2) {
                  Logger.error(ioException2, "Script {} couldn't be parsed", path.getFileName());
               }
            } else if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
               this.scriptManager.removeIf(script -> {
                  try {
                     return Files.isSameFile(script.getPath(), path);
                  } catch (IOException ioException3) {
                     Logger.warn(ioException3, "Files#isSameFile returned exception ({}/{})", script.getPath(), path);
                     return false;
                  }
               });
            }
         }

         watchKey.reset();
      }
   }

   public ScriptWatcherThread(WatchService watchService2, ScriptManager scriptManager2) {
      this.watchService = watchService2;
      this.scriptManager = scriptManager2;
      this.setName("Script-FS-Watcher");
   }

   public WatchService getWatchService() {
      return this.watchService;
   }
}
