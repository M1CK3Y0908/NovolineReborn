package net.minecraft.util;

public abstract class LazyLoadBase<T> {
   private boolean isLoaded = false;
   private T value;

   protected abstract T load();

   public T getValue() {
      if (!this.isLoaded) {
         this.isLoaded = true;
         this.value = this.load();
      }

      return this.value;
   }
}
