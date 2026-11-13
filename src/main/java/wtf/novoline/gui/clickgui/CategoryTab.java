package wtf.novoline.gui.clickgui;

import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.RenderUtils;

public class CategoryTab implements ClickGuiComponent {
   private String searchText = "";
   private final String title;
   private final ResourceLocation icon;
   private boolean focused;
   private float x;
   private float y;
   private float width;
   private float height;

   public void setHeight(float f) {
      this.height = f;
   }

   public CategoryTab(String text, ResourceLocation resourceLocation, float f, float f2, float f3, float f4) {
      this.title = text;
      this.icon = resourceLocation;
      this.x = f;
      this.y = f2;
      this.width = f3;
      this.height = f4;
   }

   @Override
   public void handleKeyboardPress(char c, int i) {
      if (this.focused) {
         if (i == 14) {
            if (!this.searchText.isEmpty()) {
               this.searchText = this.searchText.substring(0, this.searchText.length() - 1);
               clickGuiScreen.updateSearch();
            }
         } else if (i == 57) {
            this.searchText = this.searchText + " ";
            clickGuiScreen.updateSearch();
         } else if (i == 28) {
            this.focused = false;
         } else if (i >= 2 && i <= 11 || i >= 16 && i <= 25 || i >= 30 && i <= 38 || i >= 44 && i <= 50 || i == 12 || i == 52) {
            this.searchText = this.searchText + c;
            clickGuiScreen.updateSearch();
         }
      }
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public void setY(float f) {
      this.y = f;
   }

   public String getSearchText() {
      return this.searchText;
   }

   public float getHeight() {
      return this.height;
   }

   public void draw(float f, float f2, float f3) {
      RenderUtils.drawShadow(this.x, this.y, this.x + this.width, this.y + this.height, 1.0F, -1439942055);
      RenderUtils.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -15983056);
      if (!this.focused && this.searchText.isEmpty()) {
         boolean flag = this.icon != null;
         if (flag) {
            RenderUtils.drawTextureWithAlpha(this.x + 5.0F, this.y + this.height / 2.0F - 4.0F, 8.0F, 8.0F, 1.0F, this.icon);
         }

         float f4 = this.x + 5.0F + (flag ? 13.0F : 0.0F);
         float f5 = this.y + this.height / 2.0F - 2.0F;
         float f6 = f4;
         Fonts.museoSans500Size14Tab.drawString(this.title, f6, f5, -6969946);
      } else {
         Fonts.museoSans500Size14Tab.drawString(this.searchText, this.x + 5.0F, this.y + this.height / 2.0F - 2.0F, -6969946);
      }
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      this.focused = f >= this.x && f <= this.x + this.width && f2 >= this.y && f2 <= this.y + this.height;
      return true;
   }

   public void setWidth(float f) {
      this.width = f;
   }

   public String getTitle() {
      return this.title;
   }

   public float getWidth() {
      return this.width;
   }

   public void setX(float f) {
      this.x = f;
   }

   public ResourceLocation getIcon() {
      return this.icon;
   }

   public void setSearchText(String text) {
      this.searchText = text;
   }

   public boolean isFocused() {
      return this.focused;
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }
}
