package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.input.Mouse;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class AimBot extends Module {
   private final ComboSetting aimBot = settingManager.register(new ComboSetting(this, "Works on", 0, "Always", "LMB"));
   private final ComboSetting sortBy = settingManager.register(new ComboSetting(this, "Sort by", 0, "FOV", "Health", "Random"));
   private EntityPlayer entityPlayer;
   private final MultiComboSetting addons = settingManager.register(
      new MultiComboSetting(this, "Addons")
         .add("Save target", true)
         .add("Draw FOV", true)
         .add("Through walls", true)
         .add("Custom speed", false)
         .add("Assist", false)
   );
   private final MultiComboSetting ignoreRules = settingManager.register(
      new MultiComboSetting(this, "Ignore rules").add("Dead", true).add("Invisible", false).add("Naked", false).add("Bots", true)
   );
   private final NumberSetting fov = settingManager.register(new NumberSetting(this, "FOV", 60, 0, 180));
   private final NumberSetting maxDistance = settingManager.register(new NumberSetting(this, "Max distance", 1000, 0, 1000));
   private final NumberSetting speed = settingManager.register(new NumberSetting(this, "Speed", 15, 0, 360));
   private final NumberSetting assistDelta = settingManager.register(new NumberSetting(this, "Assist delta", 1, 3, 15) {
      @Override
      public boolean shouldShow() {
         return AimBot.this.addons.isSelected(4);
      }
   });
   private final CheckboxSetting smooth = settingManager.register(new CheckboxSetting(this, "Smooth", true));
   private final CheckboxSetting visibleCheck = settingManager.register(new CheckboxSetting(this, "Visible check", true));
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      if (this.addons.isSelected(1)) {
         float f = renderScreenEvent.scaledResolution.getScaledWidth() / 2.0F;
         float f2 = renderScreenEvent.scaledResolution.getScaledHeight() / 2.0F;
         float f3 = this.fov.getCurrentValue()
            / 360.0F
            * (float)Math.hypot(renderScreenEvent.scaledResolution.getScaledWidth(), renderScreenEvent.scaledResolution.getScaledHeight());
         RenderUtils.drawArc(f, f2, f3, 2.0F, -1, 360);
      }
   };
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.aimBot.getCurrentElement() != 1 || minecraft.currentScreen == null && Mouse.isButtonDown(0)) {
         if (this.entityPlayer == null
            || !minecraft.theWorld.loadedEntityList.contains(this.entityPlayer)
            || !this.isValidTarget(this.entityPlayer)
            || !this.addons.isSelected(0)) {
            List list = minecraft.theWorld.loadedEntityList.stream().filter(this::isValidTarget).collect(Collectors.toList());
            if (list.size() > 0) {
               int currentElement = this.sortBy.getCurrentElement();
               if (currentElement == 0) {
                  ScaledResolution scaledResolution2 = new ScaledResolution(minecraft);
                  float f = scaledResolution2.getScaledWidth() / 2.0F;
                  float f2 = scaledResolution2.getScaledHeight() / 2.0F;
                  Entity entity4 = null;
                  float f3 = this.fov.getCurrentValue() / 360.0F * (float)Math.hypot(scaledResolution2.getScaledWidth(), scaledResolution2.getScaledHeight());

                  for (Entity entity5 : (Iterable<Entity>) list) {
                     float[] fArray = RenderUtils.worldToScreen(
                        (float)entity5.posX - (float)TileEntityRendererDispatcher.staticPlayerX,
                        (float)entity5.posY + entity5.getEyeHeight() - (float)TileEntityRendererDispatcher.staticPlayerY,
                        (float)entity5.posZ - (float)TileEntityRendererDispatcher.staticPlayerZ
                     );
                     if (fArray != null) {
                        float f4 = f - fArray[0];
                        float f5 = f2 - fArray[1];
                        float f6 = (float)Math.abs(Math.sqrt(f4 * f4 + f5 * f5));
                        if (f3 >= f6) {
                           f3 = f6;
                           entity4 = entity5;
                        }
                     }
                  }

                  if (entity4 != null) {
                     this.entityPlayer = (EntityPlayer)entity4;
                  } else {
                     this.entityPlayer = null;
                  }
               } else if (currentElement == 1) {
                  list.sort((entity2, entity3) -> Float.compare(((EntityLivingBase)entity2).getHealth(), ((EntityLivingBase)entity3).getHealth()));
                  this.entityPlayer = (EntityPlayer)list.get(0);
               } else if (currentElement == 2) {
                  Collections.shuffle(list);
                  this.entityPlayer = (EntityPlayer)list.get(0);
               }
            } else {
               this.entityPlayer = null;
            }
         }

         if (this.addons.isSelected(4)) {
            float currentValue = this.assistDelta.getCurrentValue();
            float[][] fArrays = new float[][]{
               {minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch},
               {minecraft.thePlayer.rotationYaw - currentValue, minecraft.thePlayer.rotationPitch - currentValue},
               {minecraft.thePlayer.rotationYaw + currentValue, minecraft.thePlayer.rotationPitch + currentValue},
               {minecraft.thePlayer.rotationYaw - currentValue, minecraft.thePlayer.rotationPitch},
               {minecraft.thePlayer.rotationYaw + currentValue, minecraft.thePlayer.rotationPitch},
               {minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch - currentValue},
               {minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch + currentValue}
            };

            for (float[] fArray2 : fArrays) {
               MovingObjectPosition movingObjectPosition = RotationUtils.rayTraceEntities(minecraft.thePlayer, this.maxDistance.getCurrentValue(), fArray2[0], fArray2[1]);
               if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.ENTITY && movingObjectPosition.entityHit.equals(this.entityPlayer)) {
                  return;
               }
            }
         }

         if (this.entityPlayer != null) {
            float[] rotationsToEntityExact = RotationUtils.getRotationsToEntityExact(this.entityPlayer, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
            if (this.addons.isSelected(3)) {
               if (this.smooth.isEnabled()) {
                  minecraft.thePlayer.rotationYaw = SmoothingUtils.approach(minecraft.thePlayer.rotationYaw, rotationsToEntityExact[0], this.speed.getCurrentValue());
                  minecraft.thePlayer.rotationPitch = MathHelper.clamp_float(
                     SmoothingUtils.approach(minecraft.thePlayer.rotationPitch, rotationsToEntityExact[1], this.speed.getCurrentValue()), -90.0F, 90.0F
                  );
               } else {
                  minecraft.thePlayer.rotationYaw = RotationUtils.limitAngleChange(minecraft.thePlayer.rotationYaw, rotationsToEntityExact[0], this.speed.getCurrentValue());
                  minecraft.thePlayer.rotationPitch = MathHelper.clamp_float(
                     RotationUtils.limitAngleChange(minecraft.thePlayer.rotationPitch, rotationsToEntityExact[1], this.speed.getCurrentValue()), -90.0F, 90.0F
                  );
               }
            } else {
               minecraft.thePlayer.rotationYaw = rotationsToEntityExact[0];
               minecraft.thePlayer.rotationPitch = rotationsToEntityExact[1];
            }
         }
      } else {
         this.entityPlayer = null;
      }
   };

   private boolean isValidTarget(Entity entity4) {
      if (entity4 instanceof EntityPlayer entityPlayer2) {
         if (entity4 == minecraft.thePlayer) {
            return false;
         } else if (this.maxDistance.getCurrentValue() < 1000.0F && entity4.getDistanceToEntity(minecraft.thePlayer) > this.maxDistance.getCurrentValue()) {
            return false;
         } else if (!entity4.isEntityAlive() && this.ignoreRules.isSelected(0)) {
            return false;
         } else if (entity4.isInvisible() && this.ignoreRules.isSelected(1)) {
            return false;
         } else if (!this.addons.isSelected(2) && !minecraft.thePlayer.canEntityBeSeen(entity4)) {
            return false;
         } else if (this.ignoreRules.isSelected(2) && entityPlayer2.getTotalArmorValue() < 1) {
            return false;
         } else {
            return this.ignoreRules.isSelected(3) && ModuleManager.antibot.isEnabled() && ModuleManager.antibot.isPlayerBot((EntityPlayer)entity4)
               ? false
               : !this.visibleCheck.isEnabled() || RotationUtils.canHitEntity(entityPlayer2, this.maxDistance.getCurrentValue());
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onEnable() {
      this.entityPlayer = null;
   }

   public AimBot() {
      super(Category.COMBAT, "AimBot", "");
   }
}
