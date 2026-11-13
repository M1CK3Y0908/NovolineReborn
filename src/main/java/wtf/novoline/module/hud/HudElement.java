package wtf.novoline.module.hud;

import net.minecraft.client.gui.ScaledResolution;

public abstract class HudElement {
   private final String name;
   protected float x;
   protected float y;
   private final boolean draggable;
   private boolean dragging;
   private float dragOffsetX;
   private float dragOffsetY;
   private final boolean visibleInDebugScreen;

   public String getName() {
      return this.name;
   }

   public float getDragOffsetX() {
      return this.dragOffsetX;
   }

   public HudElement setDragOffsetX(float f) {
      this.dragOffsetX = f;
      return this;
   }

   public abstract void draw(ScaledResolution scaledResolution, float f, boolean flag);

   public HudElement(String text, float f, float f2) {
      this(text, f, f2, false);
   }

   public float getX() {
      return this.x;
   }

   public void updateDragging(ScaledResolution scaledResolution, float f, float f2) {
      if (this.dragging) {
         float x2 = this.x;
         float y2 = this.y;
         this.setX(this.dragOffsetX + f);
         this.setY(this.dragOffsetY + f2);
         float f3 = (float)scaledResolution.getScaledWidth_double();
         float f4 = (float)scaledResolution.getScaledHeight_double();
         if (this.getX() < 0.0F) {
            this.setX(x2);
         }

         if (this.getY() < 0.0F) {
            this.setY(y2);
         }

         if (this.getX() + this.getWidth() > f3) {
            this.setX(x2);
         }

         if (this.getY() + this.getHeight() > f4) {
            this.setY(y2);
         }
      }
   }

   public boolean isDraggable() {
      return this.draggable;
   }

   public HudElement setDragOffsetY(float f) {
      this.dragOffsetY = f;
      return this;
   }

   public boolean handleMouseClick(float f, float f2, int i) {
      if (this.draggable && f >= this.getX() && f <= this.getX() + this.getWidth() && f2 >= this.getY() && f2 <= this.getY() + this.getHeight()) {
         if (i == 0) {
            this.dragOffsetX = this.x - f;
            this.dragOffsetY = this.y - f2;
            this.dragging = true;
         }

         return false;
      } else {
         return true;
      }
   }

   public HudElement(String text, float f, float f2, boolean flag) {
      this.name = text;
      this.x = f;
      this.y = f2;
      this.draggable = flag;
      this.visibleInDebugScreen = true;
   }

   public abstract float getWidth();

   public boolean isDragging() {
      return this.dragging;
   }

   public HudElement setX(float f) {
      this.x = f;
      return this;
   }

   public boolean handleMouseRelease(float f, float f2) {
      this.dragging = false;
      return true;
   }

   public float getDragOffsetY() {
      return this.dragOffsetY;
   }

   public HudElement setY(float f) {
      this.y = f;
      return this;
   }

   public HudElement(String text, float f, float f2, boolean flag, boolean flag2) {
      this.name = text;
      this.x = f;
      this.y = f2;
      this.draggable = flag;
      this.visibleInDebugScreen = flag2;
   }

   public float getLeft() {
      return this.x;
   }

   public boolean isVisibleInDebugScreen() {
      return this.visibleInDebugScreen;
   }

   public abstract float getHeight();

   public float getY() {
      return this.y;
   }

   public float getTop() {
      return this.y;
   }
}
