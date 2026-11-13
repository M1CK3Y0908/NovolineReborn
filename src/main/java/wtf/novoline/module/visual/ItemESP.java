package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.util.vector.Vector3f;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.render.RenderUtils;

public class ItemESP extends Module {
   private final ComboSetting itemESP = settingManager.register(new ComboSetting(this, "Mode", 0, "2D", "3D"));
   private final ComboSetting setting2dMode = settingManager.register(new ComboSetting(this, "2D Mode", 0, "Box", "Circle") {
      @Override
      public boolean shouldShow() {
         return ItemESP.this.itemESP.getCurrentElement() == 0;
      }
   });
   private final CheckboxSetting itemName = settingManager.register(new CheckboxSetting(this, "Item name", false));
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -12986881));
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (this.itemESP.getCurrentElement() == 1) {
         for (Entity entity2 : minecraft.theWorld.loadedEntityList) {
            if (entity2 instanceof EntityItem) {
               double d = (entity2.posX - entity2.prevPosX) * (renderWorldEvent.partialTicks - 1.0F);
               double d2 = (entity2.posY - entity2.prevPosY) * (renderWorldEvent.partialTicks - 1.0F);
               double d3 = (entity2.posZ - entity2.prevPosZ) * (renderWorldEvent.partialTicks - 1.0F);
               RenderUtils.drawBoxOutlineAtRenderPos(entity2.getEntityBoundingBox().offset(d, d2, d3), this.color.getColor(), renderWorldEvent.partialTicks);
            }
         }
      }
   };
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      int currentElement = this.itemESP.getCurrentElement();
      Vector3f vector3f = new Vector3f();
      Vector3f vector3f2 = new Vector3f();

      for (Entity entity2 : minecraft.theWorld.loadedEntityList) {
         if (entity2 instanceof EntityItem) {
            float f = (float)(
               entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerX
            );
            float f2 = (float)(
               entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerY
            );
            float f3 = (float)(
               entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerZ
            );
            float f4 = 0.2F;
            f2 += f4 + 0.2F;
            vector3f.set(f, f2, f3);
            float[] fArray = RenderUtils.worldToScreen(vector3f.x, vector3f.y, vector3f.z);
            if (fArray != null) {
               f2 -= f4;
               vector3f2.set(f, f2, f3);
               float[] fArray2 = RenderUtils.worldToScreen(vector3f2.x, vector3f2.y, vector3f2.z);
               if (fArray2 != null) {
                  float f5 = Math.abs(fArray[1] - fArray2[1]);
                  int color2 = this.color.getColor();
                  if (currentElement == 0) {
                     if (this.setting2dMode.getCurrentElement() == 0) {
                        RenderUtils.drawBorderedRect(
                           fArray[0] - f5 / 2.0F - 1.0F,
                           fArray[1] - 1.0F,
                           fArray[0] - f5 / 2.0F + f5 + 1.0F,
                           fArray[1] + f5 + 1.0F,
                           1.5F,
                           0,
                           -1442840576
                        );
                        RenderUtils.drawBorderedRect(
                           fArray[0] - f5 / 2.0F - 0.5F, fArray[1] - 0.5F, fArray[0] - f5 / 2.0F + f5 + 0.5F, fArray[1] + f5 + 0.5F, 0.5F, 0, color2
                        );
                     } else if (this.setting2dMode.getCurrentElement() == 1) {
                        RenderUtils.drawArc(fArray[0], fArray[1], f5 * 0.7F + 0.5F, 1.0F, -1442840576, 360);
                        RenderUtils.drawArc(fArray[0], fArray[1], f5 * 0.7F - 0.5F, 1.0F, -1442840576, 360);
                        RenderUtils.drawArc(fArray[0], fArray[1], f5 * 0.7F, 1.0F, this.color.getColor(), 360);
                     }
                  }

                  if (this.itemName.isEnabled()) {
                     String displayName = ((EntityItem)entity2).getEntityItem().getDisplayName();
                     Fonts.rubikRegularSize14
                        .drawStringWithShadow(displayName, fArray[0] - Fonts.rubikRegularSize14.getStringWidth(displayName) / 2.0F, fArray[1] + f5 + 3.0F, color2);
                  }
               }
            }
         }
      }
   };

   public ItemESP() {
      super(Category.VISUAL, "ItemESP", "Подсвечивает предеметы на земле");
   }
}
