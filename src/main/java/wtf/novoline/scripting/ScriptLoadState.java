package wtf.novoline.scripting;

public final class ScriptLoadState {
   private final ScriptLoadState.LoadedType loadedType;
   private final Throwable throwable;

   public ScriptLoadState(ScriptLoadState.LoadedType loadedType2, Throwable throwable2) {
      this.loadedType = loadedType2;
      this.throwable = throwable2;
   }

   public ScriptLoadState.LoadedType getLoadedType() {
      return this.loadedType;
   }

   public static ScriptLoadState unloaded() {
      return new ScriptLoadState(ScriptLoadState.LoadedType.UNLOADED, null);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public static ScriptLoadState failed(Throwable throwable2) {
      return new ScriptLoadState(ScriptLoadState.LoadedType.UNLOADED_WITH_ERROR, throwable2);
   }

   public static ScriptLoadState loaded() {
      return new ScriptLoadState(ScriptLoadState.LoadedType.LOADED, null);
   }

   public enum LoadedType {
      LOADED,
      UNLOADED,
      UNLOADED_WITH_ERROR;
   }
}
