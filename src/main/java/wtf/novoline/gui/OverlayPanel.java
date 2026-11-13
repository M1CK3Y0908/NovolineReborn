package wtf.novoline.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.RoundedPolygonRenderer;
import wtf.novoline.utils.time.Stopwatch;

public abstract class OverlayPanel<L extends OverlayPanel<L, E>, E extends OverlayPanel.Entry<L, E>> extends ArrayList<E> {
   private final Stopwatch clickTimer = new Stopwatch();
   private int lastMoveDirection = -1;
   protected final GuiScreen guiScreen;
   protected E selectedItem;
   protected final BlurRenderer blurRenderer;
   protected int visibleRows;
   protected List<E> visibleEntries;
   protected int dragStartIndex;
   protected int x;
   protected int trackHeight;
   protected int dragStartMouseY;
   protected int y;
   protected int width;
   protected boolean draggingScrollbar;
   protected int firstVisibleIndex;
   protected int height;

   public void stopScrollbarDrag() {
      this.draggingScrollbar = false;
   }

   private int computeVisibleRows() {
      int i = this.size() == 0 ? 1 : this.get(0).getHeight();
      return MathHelper.floor_float(this.getHeight() / (i + 2.5F));
   }

   private void scrollTo(int i) {
      this.setFirstVisibleIndex(i);
      this.refreshVisibleEntries();
   }

   public boolean isDraggingScrollbar() {
      return this.draggingScrollbar;
   }

   public int getDragStartMouseY() {
      return this.dragStartMouseY;
   }

   public int getY() {
      return this.y;
   }

   public void setVisibleEntries(List<E> list) {
      this.visibleEntries = list;
   }

   public void updateLayout() {
      this.x = this.guiScreen.width / 2 - this.getWidth() / 2;
      this.y = 40;
      int visibleRows2 = this.visibleRows;
      this.visibleRows = this.computeVisibleRows();
      if (visibleRows2 < this.visibleRows && this.size() - this.firstVisibleIndex < this.visibleRows) {
         this.scrollTo(this.size() - this.visibleRows);
      }

      this.refreshVisibleEntries();
      this.firstVisibleIndex = Math.max(0, this.firstVisibleIndex);
      this.trackHeight = this.getHeight();
   }

   public GuiScreen getGuiScreen() {
      return this.guiScreen;
   }

   public int getTrackHeight() {
      return this.trackHeight;
   }

   private float[] getScrollbarThumb() {
      float f = (float)this.trackHeight / Math.max(1, this.size());
      float f2 = this.y + f * this.firstVisibleIndex;
      float f3 = Math.min(f * this.visibleRows, this.trackHeight);
      return new float[]{f2, f3};
   }

   public int getHeight() {
      return this.height;
   }

   public void mouseClicked(int i, int i2, int i3) {
      if (i3 == 0) {
         OverlayPanel.Entry selectedItem2 = this.selectedItem;
         this.visibleEntries.stream().filter(entry -> entry.containsPoint(i, i2)).findFirst().ifPresent(this::setEntry);
         if (this.selectedItem != null) {
            this.selectedItem.onClick();
            if (!this.clickTimer.hasElapsed(500L) && selectedItem2 != null && selectedItem2.equals(this.selectedItem) && this.selectedItem.containsPoint(i, i2)) {
               this.selectedItem.onDoubleClick();
            }
         }

         this.clickTimer.reset();
         int i4 = this.x + this.getWidth();
         float[] scrollbarThumb = this.getScrollbarThumb();
         boolean flag = i >= i4 + 5 && i <= i4 + 10;
         if (this.visibleRows < this.size()) {
            if (flag && i2 >= scrollbarThumb[0] && i2 <= scrollbarThumb[0] + scrollbarThumb[1]) {
               this.draggingScrollbar = true;
               this.dragStartMouseY = i2;
               this.dragStartIndex = this.firstVisibleIndex;
            } else if (flag) {
               int i5 = i2 >= scrollbarThumb[0] ? 1 : -1;
               this.scrollTo(this.firstVisibleIndex + MathHelper.ceiling_double_int(this.size() / 5.0 * i5));
            }
         }
      }
   }

   public int getDragStartIndex() {
      return this.dragStartIndex;
   }

   public Stopwatch getStopwatch() {
      return this.clickTimer;
   }

   public void refreshVisibleEntries() {
      int size2 = this.size();
      if (size2 - this.firstVisibleIndex < this.visibleRows) {
         this.firstVisibleIndex = size2 - this.visibleRows;
      }

      this.visibleEntries = new CopyOnWriteArrayList<>(
         this.subList(
            MathHelper.clamp_int(this.firstVisibleIndex, 0, size2), MathHelper.clamp_int(this.firstVisibleIndex + this.computeVisibleRows() + 1, 0, size2)
         )
      );
   }

   public int getLastMoveDirection() {
      return this.lastMoveDirection;
   }

   public void keyTyped(char c, int i) {
      switch (i) {
         case 200:
         case 208:
            if (this.getSelectedItem() == null) {
               this.selectIndex(0);
            } else {
               int i2 = i == 200 ? -1 : 1;
               this.selectIndex(MathHelper.clamp_int(this.getSelectedItem().getIndex() + i2, 0, this.size() - 1));
               int firstVisibleIndex2 = this.getFirstVisibleIndex();
               int visibleRows2 = this.getVisibleRows();
               int index2 = this.getSelectedItem().getIndex();
               if (firstVisibleIndex2 + visibleRows2 == index2 || firstVisibleIndex2 - 1 == index2) {
                  this.setFirstVisibleIndex(this.getFirstVisibleIndex() + i2);
                  this.refreshVisibleEntries();
               }

               this.lastMoveDirection = i2;
            }
            break;
         case 201:
         case 209:
            int i3 = i == 209 ? 1 : -1;
            this.setFirstVisibleIndex(this.getFirstVisibleIndex() + i3 * this.getVisibleRows());
            this.selectIndex(this.getFirstVisibleIndex());
         case 202:
         case 203:
         case 204:
         case 205:
         case 206:
         case 207:
      }
   }

   public void renumberEntries() {
      for (int i = 0; i < this.size(); i++) {
         this.get(i).setIndex(i);
      }
   }

   public int getWidth() {
      return this.width;
   }

   public void selectNeighbour() {
      if (this.size() > 0) {
         int i = this.selectedItem.getIndex() + this.getLastMoveDirection();
         this.selectIndex(MathHelper.clamp_int(i, 0, this.size() - 1));
      } else {
         this.setEntry(null);
      }

      this.refresh();
   }

   public BlurRenderer getBlurRenderer() {
      return this.blurRenderer;
   }

   private void handleMouseWheel(int i) {
      int dWheel = Mouse.getDWheel();
      if (dWheel != 0) {
         int i2;
         if (dWheel < 0) {
            i2 = this.firstVisibleIndex + 1;
         } else {
            i2 = this.firstVisibleIndex - 1;
         }

         if (i2 >= 0 && i2 <= i - this.visibleRows) {
            this.scrollTo(i2);
         }
      }
   }

   public void selectIndex(int i) {
      this.selectedItem = this.get(i);
      this.selectedItem.setSelected(true);
   }

   public void draw(int i, float f) {
      this.blurRenderer.bindResultTexture();
      RenderUtils.enableScissor(this.x, this.y, this.getWidth(), this.getHeight());
      RoundedPolygonRenderer roundedPolygonRenderer = new RoundedPolygonRenderer(this.guiScreen.width, this.guiScreen.height);
      roundedPolygonRenderer.putRoundedRect(this.x, this.y, this.getWidth(), this.getHeight(), 5.0F, -1);
      roundedPolygonRenderer.draw();
      FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
      filledPolygonRenderer.putRoundedRect(this.x, this.y, this.getWidth(), this.getHeight(), 5.0F, 1426063360);
      filledPolygonRenderer.draw();
      if (this.draggingScrollbar && this.size() > 0 && this.trackHeight > 0) {
         double d = Math.max((double)this.trackHeight / this.size(), 1.0);
         this.scrollTo((int)(this.dragStartIndex + (i - this.dragStartMouseY) / d));
      }

      int i2 = 0;
      if (!this.isEmpty()) {
         if (this.size() > this.visibleRows) {
            this.handleMouseWheel(this.size());
         }

         for (OverlayPanel.Entry entry2 : this.visibleEntries) {
            entry2.setX(this.x);
            entry2.setY(this.y + i2);
            i2 = (int)(i2 + (entry2.getHeight() + 2.5));
            if (this.selectedItem != null) {
               entry2.setSelected(entry2.getIndex() == this.selectedItem.getIndex());
            }

            entry2.drawEntry(f);
         }
      }

      RenderUtils.disableScissor();
      int i3 = this.x + this.getWidth();
      float[] scrollbarThumb = this.getScrollbarThumb();
      this.blurRenderer.bindResultTexture();
      roundedPolygonRenderer = new RoundedPolygonRenderer(this.guiScreen.width, this.guiScreen.height);
      roundedPolygonRenderer.putRoundedRect(i3 + 5, this.y, 5.0F, this.trackHeight, 2.5F, 0);
      roundedPolygonRenderer.draw();
      FilledPolygonRenderer filledPolygonRenderer2 = new FilledPolygonRenderer();
      filledPolygonRenderer2.putRoundedRect(i3 + 5, this.y, 5.0F, this.trackHeight, 2.5F, 1426063360);
      filledPolygonRenderer2.draw();
      FilledPolygonRenderer filledPolygonRenderer3 = new FilledPolygonRenderer();
      filledPolygonRenderer3.putRoundedRect(i3 + 5, (int)scrollbarThumb[0], 5.0F, (int)scrollbarThumb[1], 2.5F, -1);
      filledPolygonRenderer3.draw();
   }

   public OverlayPanel(GuiScreen guiScreen2, BlurRenderer blurRenderer2) {
      this.guiScreen = guiScreen2;
      this.blurRenderer = blurRenderer2;
      this.visibleEntries = new CopyOnWriteArrayList<>();
      this.x = 0;
      this.y = 0;
      this.width = 0;
      this.height = 0;
   }

   public void setEntry(E value) {
      this.selectedItem = (E)value;
   }

   public void setFirstVisibleIndex(int i) {
      this.firstVisibleIndex = MathHelper.clamp_int(i, 0, Math.max(0, this.size() - this.visibleRows));
   }

   public int getX() {
      return this.x;
   }

   public void refresh() {
      this.renumberEntries();
      this.updateLayout();
   }

   public int getFirstVisibleIndex() {
      return this.firstVisibleIndex;
   }

   public int getVisibleRows() {
      return this.visibleRows;
   }

   public E getSelectedItem() {
      return this.selectedItem;
   }

   public List<E> getVisibleEntries() {
      return this.visibleEntries;
   }

   public abstract static class Entry<L extends OverlayPanel<L, E>, E extends OverlayPanel.Entry<L, E>> {
      protected boolean current;
      protected final L overlayPanel;
      protected boolean selected;
      protected int index;
      protected int y;
      protected int x;
      private transient int cachedHashCode;

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof OverlayPanel.Entry entry2)) {
            return false;
         } else {
            if (!entry2.canEqual(this)) {
               return false;
            }

            if (this.getIndex() != entry2.getIndex()) {
               return false;
            }

            if (this.getX() != entry2.getX()) {
               return false;
            }

            if (this.getY() != entry2.getY()) {
               return false;
            }

            if (this.isSelected() != entry2.isSelected()) {
               return false;
            }

            if (this.isCurrent() != entry2.isCurrent()) {
               return false;
            }

            OverlayPanel overlayPanel2 = this.getOverlayPanel();
            OverlayPanel overlayPanel3 = entry2.getOverlayPanel();
            return overlayPanel2 == null ? overlayPanel3 == null : overlayPanel2.equals(overlayPanel3);
         }
      }

      public int getY() {
         return this.y;
      }

      public L getOverlayPanel() {
         return this.overlayPanel;
      }

      public boolean isSelected() {
         return this.selected;
      }

      @Override
      public int hashCode() {
         if (this.cachedHashCode != 0) {
            return this.cachedHashCode;
         }

         int i = 59 + this.getIndex();
         i = i * 59 + this.getX();
         i = i * 59 + this.getY();
         i = i * 59 + (this.isSelected() ? 79 : 97);
         i = i * 59 + (this.isCurrent() ? 79 : 97);
         OverlayPanel overlayPanel2 = this.getOverlayPanel();
         i = i * 59 + (overlayPanel2 == null ? 43 : overlayPanel2.hashCode());
         if (i == 0) {
            i = Integer.MIN_VALUE;
         }

         this.cachedHashCode = i;
         return i;
      }

      public boolean isCurrent() {
         return this.current;
      }

      public abstract void drawEntry(float f);

      public void setCurrent(boolean flag) {
         this.current = flag;
      }

      protected boolean canEqual(Object object) {
         return object instanceof OverlayPanel.Entry;
      }

      public boolean containsPoint(int i, int i2) {
         return i >= this.x && i <= this.x + this.getWidth() && i2 >= this.y && i2 <= this.y + this.getHeight();
      }

      public void onClick() {
      }

      public int getX() {
         return this.x;
      }

      public abstract int getHeight();

      public void setSelected(boolean flag) {
         this.selected = flag;
      }

      public void onDoubleClick() {
      }

      public void setY(int i) {
         this.y = i;
      }

      public Entry(L value, int i) {
         this.overlayPanel = (L)value;
         this.index = i;
      }

      public void setX(int i) {
         this.x = i;
      }

      public int getIndex() {
         return this.index;
      }

      public void setIndex(int i) {
         this.index = i;
      }

      public abstract int getWidth();
   }
}
