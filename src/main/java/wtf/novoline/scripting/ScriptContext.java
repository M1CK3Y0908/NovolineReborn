package wtf.novoline.scripting;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.api.Updatable.EventHandlerRegistration;
import wtf.novoline.api.Updatable.ModuleRegistration;
import wtf.novoline.api.Updatable.SettingRegistration;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.PacketAdapterRegistry;
import wtf.novoline.script.ScriptFunctionUtils;
import wtf.novoline.script.ScriptType;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeAdapter;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.script.TypeStore;
import wtf.novoline.script.ValueUtils;
import wtf.novoline.script.PacketAdapterRegistry.PacketKey;
import wtf.novoline.script.ScriptFunctionUtils.Helper;
import wtf.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import wtf.novoline.script.adapter.PacketTypeAdapter.HandshakingType;
import wtf.novoline.scripting.ScriptEventHandler.HelperChild;
import wtf.novoline.scripting.ScriptEventHandler.HelperChild2;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.player.ItemUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.PolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.RoundedPolygonRenderer;
import wtf.novoline.utils.render.shape.GradientRectRenderer;
import wtf.novoline.utils.render.shape.OutlineRectRenderer;
import wtf.novoline.utils.render.shape.RectRenderer;
import wtf.novoline.utils.render.shape.RoundedRectRenderer;

public class ScriptContext extends HashMap<String, Object> {
   private final TypeRegistry typeRegistry = TypeRegistry.getTypeRegistry();
   private final TypeStore typeStore = this.typeRegistry.getTypeStore();
   private final PacketAdapterRegistry packetAdapterRegistry = this.typeRegistry.getPacketAdapterRegistry();
   private final ScriptEventHandlerList scriptEventHandlerList = new ScriptEventHandlerList();
   private final ScriptEventDispatcher scriptEventDispatcher = new ScriptEventDispatcher();
   private final ScriptEventBridge scriptEventBridge = new ScriptEventBridge(this.scriptEventDispatcher, this.scriptEventHandlerList);
   private ScriptContext.Context context;

   public void exitContext() {
      if (this.context == null) {
         throw new IllegalStateException("Context is already exited");
      }

      this.context = null;
   }

   public static List toList(ScriptObjectMirror scriptObjectMirror) {
      if (!scriptObjectMirror.isArray()) {
         throw new IllegalArgumentException("Object is not an array");
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < scriptObjectMirror.size(); i++) {
         arrayList.add(i, scriptObjectMirror.getSlot(i));
      }

      return arrayList;
   }

   public ScriptEventBridge getScriptEventBridge() {
      return this.scriptEventBridge;
   }

   public void enterContext(ScriptContext.Context context2) {
      if (this.context != null) {
         throw new IllegalStateException("Context is already entered");
      }

      this.context = context2;
   }

   public ScriptEventDispatcher getScriptEventDispatcher() {
      return this.scriptEventDispatcher;
   }

   public ScriptEventHandlerList getScriptEventHandlerList() {
      return this.scriptEventHandlerList;
   }

   public ScriptContext() {
      Novoline.getNovoline().getBus().subscribe(this.scriptEventBridge);
      List.of(new ScriptContext.ClientApi(), new ScriptContext.MinecraftApi(), new ScriptContext.TimeApi()).forEach(this::putAll);
   }

   public final class ClientApi extends ScriptContext.ScriptApi {
      private final TypeAdapter<Module> typeAdapter;
      private final TypeAdapter<CFontRenderer> typeAdapter2;
      private final TypeAdapter<Notification> typeAdapter3;
      private final TypeAdapter<PolygonRenderer> typeAdapter4;
      private final TypeAdapter<RectRenderer> typeAdapter5;
      private final TypeAdapter<Animation> typeAdapter6;

      private void callWithGlobals(ScriptObjectMirror scriptObjectMirror) {
         Helper.minecraftContext.doSync(false);
         ScriptContext.this.scriptEventDispatcher.setGlobals(scriptObjectMirror);
         scriptObjectMirror.call(null);
         Helper.minecraftContext.doSync(true);
      }

      private Map<String, Object> createTransition() {
         return this.typeAdapter6.pack(new Animation());
      }

      private void pushNotification(Map<String, Object> map2) {
         Notification notification2 = this.typeAdapter3.unpack(map2);
         Novoline.getNovoline().getNotificationManager().show(notification2);
      }

      private Map<String, Object> C_registerSetting(Module module2, String text, String text2, Object object, final ScriptObjectMirror scriptObjectMirror) {
         Class<?> clazz;

         ModuleSetting moduleSetting = switch (text) {
            case "multi_combo" -> {
               clazz = MultiComboSetting.class;
               ScriptObjectMirror scriptObjectMirror2 = (ScriptObjectMirror)object;
               MultiComboSetting multiComboSetting = new MultiComboSetting(module2, text2) {
                  @Override
                  public void interactCallback() {
                     ClientApi.this.callInteractCallback(scriptObjectMirror);
                  }

                  @Override
                  public boolean shouldShow() {
                     return ClientApi.this.callShouldShow(scriptObjectMirror);
                  }
               };

               for (String text3 : scriptObjectMirror2.keySet()) {
                  boolean flag = (Boolean)scriptObjectMirror2.get(text3);
                  multiComboSetting.add(text3, flag);
               }

               yield multiComboSetting;
            }
            case "checkbox" -> {
               clazz = CheckboxSetting.class;
               yield new CheckboxSetting(module2, text2, (Boolean)object) {
                  @Override
                  public void interactCallback() {
                     ClientApi.this.callInteractCallback(scriptObjectMirror);
                  }

                  @Override
                  public boolean shouldShow() {
                     return ClientApi.this.callShouldShow(scriptObjectMirror);
                  }
               };
            }
            case "slider" -> {
               clazz = NumberSetting.class;
               List list = ScriptContext.toList((ScriptObjectMirror)object);
               if (list.get(0) instanceof Double && list.get(1) instanceof Double && list.get(2) instanceof Double) {
                  yield new NumberSetting(
                     module2,
                     text2,
                     ValueUtils.createBigDecimal(list.get(0)).floatValue(),
                     ValueUtils.createBigDecimal(list.get(1)).floatValue(),
                     ValueUtils.createBigDecimal(list.get(2)).floatValue()
                  ) {
                     @Override
                     public boolean shouldShow() {
                        return ClientApi.this.callShouldShow(scriptObjectMirror);
                     }

                     @Override
                     public void interactCallback() {
                        ClientApi.this.callInteractCallback(scriptObjectMirror);
                     }
                  };
               }

               if (!(list.get(0) instanceof Integer) || !(list.get(1) instanceof Integer) || !(list.get(2) instanceof Integer)) {
                  throw new IllegalArgumentException("Mismatching array types: expected all elements to be either decimal or integer");
               }

               yield new NumberSetting(module2, text2, (Integer)list.get(0), (Integer)list.get(1), (Integer)list.get(2)) {
                  @Override
                  public void interactCallback() {
                     ClientApi.this.callInteractCallback(scriptObjectMirror);
                  }

                  @Override
                  public boolean shouldShow() {
                     return ClientApi.this.callShouldShow(scriptObjectMirror);
                  }
               };
            }
            case "combo" -> {
               clazz = ComboSetting.class;
               ScriptObjectMirror scriptObjectMirror3 = (ScriptObjectMirror)object;
               ScriptObjectMirror scriptObjectMirror4 = (ScriptObjectMirror)scriptObjectMirror3.get("values");
               Integer integer2 = (Integer)scriptObjectMirror3.get("default");
               String[] texts = (String[]) ScriptContext.toList(scriptObjectMirror4).stream().map(String.class::cast).toArray(String[]::new);
               yield new ComboSetting(module2, text2, integer2, texts) {
                  @Override
                  public boolean shouldShow() {
                     return ClientApi.this.callShouldShow(scriptObjectMirror);
                  }

                  @Override
                  public void interactCallback() {
                     ClientApi.this.callInteractCallback(scriptObjectMirror);
                  }
               };
            }
            case "color" -> {
               clazz = ColorSetting.class;
               Double dValue = (Double)object;
               yield new ColorSetting(module2, text2, dValue.intValue()) {
                  @Override
                  public boolean shouldShow() {
                     return ClientApi.this.callShouldShow(scriptObjectMirror);
                  }

                  @Override
                  public void interactCallback() {
                     ClientApi.this.callInteractCallback(scriptObjectMirror);
                  }
               };
            }
            default -> throw new IllegalArgumentException("Invalid setting type: " + text);
         };
         ScriptContext.this.context.script.addOperation(new SettingRegistration(moduleSetting));
         TypeAdapter typeAdapter10 = ScriptContext.this.typeRegistry.getTypeAdapter(clazz);
         return typeAdapter10.packWithSubTypes(moduleSetting);
      }

      private void callInteractCallback(ScriptObjectMirror scriptObjectMirror) {
         if (scriptObjectMirror.containsKey("interactCallback")) {
            scriptObjectMirror.callMember("interactCallback");
         }
      }

      private boolean callShouldShow(ScriptObjectMirror scriptObjectMirror) {
         return !scriptObjectMirror.containsKey("shouldShow") || (Boolean)scriptObjectMirror.callMember("shouldShow");
      }

      private Map<String, Object> createRenderer(String text, Map<String, Object> map2) {
         ScriptValue scriptValue = ScriptValue.createScriptValue(map2);

         return switch (text) {
            case "ColoredPolygonRenderer" -> this.typeAdapter4.pack(new FilledPolygonRenderer());
            case "ColoredRectRenderer" -> this.typeAdapter5.pack(new OutlineRectRenderer(scriptValue.getFloat("alpha")));
            case "TexturedPolygonRenderer" -> this.typeAdapter4
               .pack(new RoundedPolygonRenderer(scriptValue.getFloat("textureWidth"), scriptValue.getFloat("textureHeight")));
            case "InverseTexturedRectRenderer", "TexturedRectRenderer" -> {
               float floatValue = scriptValue.getFloat("textureWidth");
               float floatValue2 = scriptValue.getFloat("textureHeight");
               boolean flag = scriptValue.checkString("useAlpha");
               boolean flag2 = text.equals("InverseTexturedRectRenderer");
               yield this.typeAdapter5
                  .pack(
                     scriptValue.containsKey("useAlpha")
                        ? (flag2 ? new GradientRectRenderer(floatValue, floatValue2, flag) : new RoundedRectRenderer(floatValue, floatValue2, flag))
                        : (flag2 ? new GradientRectRenderer(floatValue, floatValue2) : new RoundedRectRenderer(floatValue, floatValue2))
                  );
            }
            default -> throw new IllegalArgumentException("Unsupported renderer: " + text);
         };
      }

      public ClientApi() {
         ScriptValue scriptValue = ScriptValue.createScriptValue(this);
         scriptValue.putBiFunction("C_registerFeature", this::registerFeature);
         scriptValue.putFunction("C_getFeatureByName", this::getFeatureByName);
         scriptValue.putFunction5("C_registerSetting", this::C_registerSetting);
         scriptValue.putConsumer4("C_registerEventListener", this::registerEventHandler);
         scriptValue.putBiFunction("C_Fonts_createFontRenderer", this::createFontRenderer);
         scriptValue.putBiConsumer("C_Task_queueFutureTask", this::queueFutureTask);
         scriptValue.putConsumer("C_Notifications_push", this::pushNotification);
         scriptValue.putBiFunction("C_Render_createRenderer", this::createRenderer);
         scriptValue.putConsumer("C_clientChatMessage", Novoline.getNovoline()::sendChatMessage);
         scriptValue.putSupplier("C_Render_createTransition", this::createTransition);
         scriptValue.put("U_username", Novoline.username);
         scriptValue.put("U_uid", Novoline.userId);
         scriptValue.put("C_name", "Novoline");
         scriptValue.put("C_channel", "Release");
         scriptValue.put("C_version", "3.0");
         scriptValue.put("C_build", Novoline.BUILD_DATE);
         scriptValue.putSupplier("C_getLanguage", () -> ValueUtils.lowerCaseName(Novoline.getNovoline().getRuRuType()));
         scriptValue.put("C_Directories_ROOT", Directories.ROOT_DIR);
         scriptValue.put("C_Directories_STORAGE", Directories.STORAGE_DIR);
         scriptValue.put("C_Directories_SCRIPTS", Directories.SCRIPTS_DIR);
         scriptValue.putFunction("U_Enchantment_getArmorScore", MappingUtils.m2oMappingFunction(ItemStack.class, ItemUtils::getArmorScore));
         scriptValue.putFunction("U_Enchantment_getMaterialScore", ItemUtils::getMaterialScore);
         scriptValue.putFunction("U_Enchantment_getToolScore", MappingUtils.m2oMappingFunction(ItemStack.class, ItemUtils::getToolScore));
         scriptValue.putSupplier("U_Inventory_findEmptyHotbarSlot", InventoryUtils::findEmptyHotbarSlot);
         scriptValue.putFunction("U_Inventory_findSlotWithBlocks", InventoryUtils::findSlotWithBlocks);
         scriptValue.putFunction("U_Inventory_findBestArmorSlot", InventoryUtils::findBestArmorSlot);
         scriptValue.putFunction3("U_Math_sinerp$1", InterpolationUtils::oscillate);
         scriptValue.putFunction4("U_Math_sinerp$2", InterpolationUtils::oscillateWithOffset);
         scriptValue.putSupplier("U_Player_getLastTickDistance", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::getLastTickDistance));
         scriptValue.putBiFunction("U_Player_getBaseMoveSpeed$1", ScriptFunctionUtils.biFunction(Helper.minecraftContext, PlayerUtils::getSprintSpeed));
         scriptValue.putFunction("U_Player_getBaseMoveSpeed$2", ScriptFunctionUtils.function(Helper.minecraftContext, PlayerUtils::getBaseMoveSpeed));
         scriptValue.putSupplier("U_Player_getBaseMoveSpeed$3", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::getDefaultMoveSpeed));
         scriptValue.putSupplier("U_Player_isMoving", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::isMoving));
         scriptValue.putSupplier("U_Player_getDirection", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::getMoveDirectionRadians));
         scriptValue.putConsumer("U_Player_setMotionSpeed", ScriptFunctionUtils.consumer(Helper.minecraftContext, PlayerUtils::setMotionInMoveDirection));
         scriptValue.putConsumer("U_Player_setSpeed", ScriptFunctionUtils.consumer(Helper.minecraftContext, PlayerUtils::setSpeed));
         scriptValue.putSupplier("U_Player_isBlockUnder", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::hasBlockBelow));
         scriptValue.putSupplier("U_Player_isReallyOnGround", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::isOnSolidGround));
         scriptValue.putFunction("U_Player_isOnGround$1", ScriptFunctionUtils.function(Helper.minecraftContext, PlayerUtils::hasCollisionBelow));
         scriptValue.putBiFunction(
            "U_Player_isOnGround$2",
            ScriptFunctionUtils.biFunction(Helper.minecraftContext, MappingUtils.om2oMappingFunction(Entity.class, PlayerUtils::hasCollisionBelowEntity))
         );
         scriptValue.putSupplier("U_Player_isInsideBlock", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::isInsideBlock));
         scriptValue.putSupplier("U_Player_isInLiquid", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::isInsideLiquid));
         scriptValue.putSupplier("U_Player_isOnLiquid", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::isStandingOnLiquid));
         scriptValue.putFunction3("U_Player_isVoidBelow", ScriptFunctionUtils.triFunction(Helper.minecraftContext, PlayerUtils::hasBlockBelowAt));
         scriptValue.putFunction(
            "U_Player_teamColor",
            ScriptFunctionUtils.function(Helper.minecraftContext, MappingUtils.m2oMappingFunction(EntityPlayer.class, PlayerUtils::getTeamColorCode))
         );
         scriptValue.putBiFunction(
            "U_Player_isSameTeam",
            ScriptFunctionUtils.biFunction(
               Helper.minecraftContext, MappingUtils.mm2oMappingFunction(EntityPlayer.class, EntityPlayer.class, PlayerUtils::isSameTeam)
            )
         );
         scriptValue.putSupplier("U_Player_getBaseMotionY$1", ScriptFunctionUtils.supplier(Helper.minecraftContext, PlayerUtils::getJumpMotion));
         scriptValue.putFunction("U_Player_getBaseMotionY$2", ScriptFunctionUtils.function(Helper.minecraftContext, PlayerUtils::addJumpBoost));
         scriptValue.putSupplier(
            "U_Player_clonePlayer",
            ScriptFunctionUtils.supplier(Helper.minecraftContext, MappingUtils.mappingSupplier(EntityPlayer.class, PlayerUtils::getEntityOtherPlayerMP))
         );
         scriptValue.putFunction3(
            "U_Combat_getRotationsToEntity$1",
            ScriptFunctionUtils.triFunction(
               Helper.minecraftContext, MappingUtils.moo2oMappingFunction(EntityLivingBase.class, RotationUtils::getRotationsToEntityExact)
            )
         );
         scriptValue.putFunction4(
            "U_Combat_getRotationsToEntity$2",
            ScriptFunctionUtils.quadFunction(
               Helper.minecraftContext, MappingUtils.mooo2oMappingFunction(EntityLivingBase.class, RotationUtils::getRotationsToEntity)
            )
         );
         scriptValue.putFunction3(
            "U_Combat_getRotationToBlock$2",
            ScriptFunctionUtils.triFunction(
               Helper.minecraftContext, MappingUtils.mmo2oMappingFunction(Vec3.class, EnumFacing.class, RotationUtils::getRotationsToFace)
            )
         );
         scriptValue.putFunction3("U_Combat_getRotations", ScriptFunctionUtils.triFunction(Helper.minecraftContext, RotationUtils::getRotationsToPoint));
         scriptValue.putFunction(
            "U_Combat_getRotationToBlock$1",
            ScriptFunctionUtils.function(Helper.minecraftContext, MappingUtils.m2oMappingFunction(Vec3.class, RotationUtils::getRotationsTo))
         );
         scriptValue.putBiFunction(
            "U_Combat_getVectorForRotation",
            ScriptFunctionUtils.biFunction(Helper.minecraftContext, MappingUtils.oo2mMappingFunction(Vec3.class, RotationUtils::getVectorForRotation))
         );
         scriptValue.putFunction3("U_Combat_updateRotation", ScriptFunctionUtils.triFunction(Helper.minecraftContext, RotationUtils::limitAngleChange));
         scriptValue.putSupplier("U_Combat_getMovementYaw", ScriptFunctionUtils.supplier(Helper.minecraftContext, RotationUtils::getMoveYawOffset));
         scriptValue.putBiFunction("U_Combat_getYawToPoint", ScriptFunctionUtils.biFunction(Helper.minecraftContext, RotationUtils::getYawToPoint));
         scriptValue.putFunction4(
            "U_Combat_rayTrace",
            ScriptFunctionUtils.quadFunction(
               Helper.minecraftContext, MappingUtils.mooo2mMappingFunction(Entity.class, MovingObjectPosition.class, RotationUtils::rayTraceBlocks)
            )
         );
         scriptValue.putFunction4(
            "U_Combat_rayTraceEntities",
            ScriptFunctionUtils.quadFunction(
               Helper.minecraftContext, MappingUtils.mooo2mMappingFunction(Entity.class, MovingObjectPosition.class, RotationUtils::rayTraceBlocks)
            )
         );
         scriptValue.putRunnable("U_Animations_updateDelta", SmoothingUtils::updateDelta);
         scriptValue.putFunction3("U_Animations_animate$1", SmoothingUtils::approach);
         scriptValue.putFunction4("U_Animations_animate$1", SmoothingUtils::approachWithMinimumStep);
         scriptValue.putFunction("U_Servers_serverIs", (String string) -> HypixelState.isServerType(ServerType.valueOf(string.toUpperCase(Locale.ROOT))));
         scriptValue.putFunction("U_Servers_hypixelGamemodeIs", (String string) -> HypixelState.isGameType(HypixelGameType.valueOf(string.toUpperCase(Locale.ROOT))));
         scriptValue.putRunnable("U_Render_fixedBlendFunc", RenderUtils::applyBlendFunc);
         scriptValue.putFunction("U_Render_createFrameBuffer", MappingUtils.m2mMappingFunction(Framebuffer.class, Framebuffer.class, RenderUtils::ensureFramebuffer));
         scriptValue.putBiConsumer("U_Render_drawFramebuffer", MappingUtils.mmMappingConsumer(Framebuffer.class, ScaledResolution.class, RenderUtils::drawFramebuffer));
         scriptValue.putConsumer("U_Render_drawFullScreenRect", MappingUtils.mappingConsumer(ScaledResolution.class, RenderUtils::drawFullscreenQuad));
         scriptValue.putConsumer5("U_Render_drawRect", RenderUtils::drawRect);
         scriptValue.putConsumer7("U_Render_drawBorderedRect", RenderUtils::drawBorderedRect);
         scriptValue.putConsumer10("U_Render_drawScaledCustomSizeModalRect", RenderUtils::drawScaledTexturedRect);
         scriptValue.<Float, Float, Float, Float, Integer, Integer>putConsumer6("U_Render_drawCirclePercent$1", RenderUtils::drawArc);
         scriptValue.<Float, Float, Float, Float, Integer, Integer, Boolean>putConsumer7("U_Render_drawCirclePercent$2", RenderUtils::drawArc);
         scriptValue.putConsumer6("U_Render_drawGradientRect", RenderUtils::drawVerticalGradientRect);
         scriptValue.putConsumer6("U_Render_drawGradientRectSideways", RenderUtils::drawHorizontalGradientRect);
         scriptValue.putConsumer8("U_Render_drawModalRectWithCustomSizedTexture", RenderUtils::drawTexturedRect);
         scriptValue.putConsumer6("U_Render_drawTexturedModalRect$1", RenderUtils::drawTexturedRect256);
         scriptValue.putConsumer7("U_Render_drawTexturedModalRect$2", RenderUtils::drawTexturedRectSquareTexture);
         scriptValue.putFunction3("U_Render_translateColorAnimated", RenderUtils::smoothBlendColors);
         scriptValue.putBiFunction("U_Render_withAlpha", RenderUtils::withAlpha);
         scriptValue.putRunnable("U_Render_resetColor", RenderUtils::resetColor);
         scriptValue.putConsumer("U_Render_setupColor", RenderUtils::setColor);
         scriptValue.putFunction("U_Render_hexToColor", RenderUtils::unpackColor255);
         scriptValue.putFunction("U_Render_hexToColor10", RenderUtils::unpackColorNormalized);
         scriptValue.putFunction("U_Render_colorToHex", RenderUtils::packColor);
         scriptValue.putConsumer5("U_Render_rainbowColor$1", RenderUtils::indexedHsbColor);
         scriptValue.putConsumer4("U_Render_rainbowColor$2", RenderUtils::timedHsbColor);
         scriptValue.putConsumer5("U_Render_astolfoColor$1", RenderUtils::indexedHsbColorMirrored);
         scriptValue.putConsumer4("U_Render_astolfoColor$2", RenderUtils::timedHsbColorMirrored);
         scriptValue.putFunction3("U_Render_interpolateColor", RenderUtils::lerpColor);
         scriptValue.putFunction5("U_Render_gradientColor", RenderUtils::pulseBetweenColors);
         scriptValue.putFunction6("U_Render_fadeColor", RenderUtils::pulseBrightness);
         scriptValue.putConsumer8("U_Render_drawGradientLine", RenderUtils::drawGradientLine);
         scriptValue.<Float, Float, Float, Float, Float, Integer>putConsumer6("U_Render_drawShadow", RenderUtils::drawShadow);
         scriptValue.putConsumer4("U_Render_scissorStart", RenderUtils::enableScissor);
         scriptValue.putRunnable("U_Render_scissorEnd", RenderUtils::disableScissor);
         scriptValue.putFunction("U_Render_worldToScreen$1", MappingUtils.m2oMappingFunction(Vec3.class, RenderUtils::worldToScreen));
         scriptValue.<Float, Float, Float, float[]>putFunction3("U_Render_worldToScreen$2", RenderUtils::worldToScreen);
         scriptValue.<Float, Float, Float, Float, Integer>putConsumer5("U_Render_drawLine$1", RenderUtils::drawLine);
         scriptValue.<Float, Float, Float, Float, Integer, Boolean>putConsumer6("U_Render_drawLine$2", RenderUtils::drawLine);
         scriptValue.<Float, Float, Float, Float, Float, Integer, Boolean>putConsumer7("U_Render_drawLine$3", RenderUtils::drawLine);
         scriptValue.putConsumer("U_Render_enableOutlineMode", RenderUtils::setTextureEnvColor);
         scriptValue.putRunnable("U_Render_disableOutlineMode", RenderUtils::setTextureEnvModulate);
         scriptValue.putConsumer5("U_Render_drawSelectionBoundingBox", MappingUtils.mooooMappingConsumer(AxisAlignedBB.class, RenderUtils::drawBoxOutline));
         scriptValue.putConsumer5("U_Render_renderFilledBox", MappingUtils.mooooMappingConsumer(AxisAlignedBB.class, RenderUtils::drawBoxFilled));
         scriptValue.putConsumer3("U_Render_drawSelectionBox", MappingUtils.mooMappingConsumer(AxisAlignedBB.class, RenderUtils::drawBoxOutlineAtRenderPos));
         scriptValue.putConsumer3("U_Render_drawFilledBox", MappingUtils.mooMappingConsumer(AxisAlignedBB.class, RenderUtils::drawBoxFilledAndOutlined));
         this.typeAdapter = ScriptContext.this.typeRegistry.getTypeAdapter(Module.class);
         this.typeAdapter2 = ScriptContext.this.typeRegistry.getTypeAdapter(CFontRenderer.class);
         this.typeAdapter3 = ScriptContext.this.typeRegistry.getTypeAdapter(Notification.class);
         this.typeAdapter4 = ScriptContext.this.typeRegistry.getTypeAdapter(PolygonRenderer.class);
         this.typeAdapter5 = ScriptContext.this.typeRegistry.getTypeAdapter(RectRenderer.class);
         this.typeAdapter6 = ScriptContext.this.typeRegistry.getTypeAdapter(Animation.class);
      }

      private Map<String, Object> createFontRenderer(String text, int i) {
         CFontRenderer cFontRenderer = Fonts.createCFontRenderer(switch (text) {
            case "RubikRegular" -> Fonts.RUBIK_REGULAR;
            case "RubikSemibold" -> Fonts.RUBIK_SEMIBOLD;
            case "PixelRegular" -> Fonts.PIXEL;
            case "RobotoRegular" -> Fonts.ROBOTO_REGULAR;
            case "MontserratRegular" -> Fonts.MONTSERRAT_REGULAR;
            case "MuseoSandMedium" -> Fonts.MUSEO_SANS_500;
            case "MuseoSansBold" -> Fonts.MUSEO_SANS_700;
            case "MuseoSansBlack" -> Fonts.MUSEO_SANS_900;
            default -> throw new IllegalArgumentException("Unrecognized font name: " + text);
         }, i);
         return this.typeAdapter2.pack(cFontRenderer);
      }

      private void queueFutureTask(int i, final ScriptObjectMirror scriptObjectMirror) {
         ScheduledTask scheduledTask = new ScheduledTask(i) {
            @Override
            public void run() {
               if (scriptObjectMirror.containsKey("execute")) {
                  ScriptObjectMirror scriptObjectMirror2 = (ScriptObjectMirror)scriptObjectMirror.get("execute");
                  ClientApi.this.callWithGlobals(scriptObjectMirror2);
               }
            }

            @Override
            public void onWaiting() {
               if (scriptObjectMirror.containsKey("run")) {
                  ScriptObjectMirror scriptObjectMirror3 = (ScriptObjectMirror)scriptObjectMirror.get("run");
                  ClientApi.this.callWithGlobals(scriptObjectMirror3);
               }
            }
         };
         Novoline.getNovoline().getTaskScheduler().schedule(scheduledTask);
      }

      private void registerEventHandler(Module module2, String text, int i, ScriptObjectMirror scriptObjectMirror) {
         Script script2 = ScriptContext.this.context.getScript();

         wtf.novoline.scripting.ScriptEventHandler.Helper helper = switch (text) {
            case "EnableEvent" -> HelperChild2.ENABLE;
            case "DisableEvent" -> HelperChild2.DISABLE;
            case "ToggleEvent" -> HelperChild2.TOGGLE;
            default -> {
               ScriptType byName = ScriptContext.this.typeRegistry.getTypeStore().getByName(text);
               if (byName == null) {
                  throw new IllegalArgumentException("Couldn't locate event: " + text);
               }

               yield HelperChild.forClass(byName.getClassValue());
            }
         };
         ScriptEventHandler scriptEventHandler = new ScriptEventHandler(script2, module2, helper, i, scriptObjectMirror);
         script2.addOperation(new EventHandlerRegistration(scriptEventHandler));
      }

      private Map<String, Object> getFeatureByName(String text) {
         Module module2 = ModuleManager.modulesByName.get(text.toLowerCase(Locale.ROOT));
         if (module2 == null) {
            return null;
         }

         TypeAdapter typeAdapter10 = ScriptContext.this.typeRegistry.getTypeAdapter(module2.getClass());
         return typeAdapter10 != null ? typeAdapter10.pack(module2) : this.typeAdapter.pack(module2);
      }

      private Map<String, Object> registerFeature(String text, String text2) {
         final Script script2 = ScriptContext.this.context.getScript();
         Module module2 = new Module(Category.SCRIPTS, text, text2) {
            @Override
            protected void onDisable() {
               ScriptContext.this.scriptEventHandlerList
                  .sortByPriority(ScriptContext.this.scriptEventHandlerList.findExactByScript(script2.getInternalId(), HelperChild2.DISABLE))
                  .forEach(ScriptContext.this.scriptEventDispatcher::dispatchToggle);
            }

            @Override
            protected void onEnable() {
               ScriptContext.this.scriptEventHandlerList
                  .sortByPriority(ScriptContext.this.scriptEventHandlerList.findExactByScript(script2.getInternalId(), HelperChild2.ENABLE))
                  .forEach(ScriptContext.this.scriptEventDispatcher::dispatchToggle);
            }

            @Override
            protected void onToggled() {
               ScriptContext.this.scriptEventHandlerList
                  .sortByPriority(ScriptContext.this.scriptEventHandlerList.findExactByScript(script2.getInternalId(), HelperChild2.TOGGLE))
                  .forEach(ScriptContext.this.scriptEventDispatcher::dispatchToggle);
            }
         };
         script2.addOperation(new ModuleRegistration(module2));
         return this.typeAdapter.pack(module2);
      }
   }

   public static final class Context {
      private final Script script;

      @Override
      public int hashCode() {
         Script script2 = this.getScript();
         return 59 + (script2 == null ? 43 : script2.hashCode());
      }

      public static ScriptContext.Context createContext(Script script2) {
         return new ScriptContext.Context(script2);
      }

      public Script getScript() {
         return this.script;
      }

      @Override
      public String toString() {
         return "JSInterop.Context(script=" + this.getScript() + ")";
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptContext.Context context2)) {
            return false;
         } else {
            Script script2 = this.getScript();
            Script script3 = context2.getScript();
            return script2 == null ? script3 == null : script2.equals(script3);
         }
      }

      private Context(Script script2) {
         this.script = script2;
      }
   }

   public final class MinecraftApi extends ScriptContext.ScriptApi {
      private final TypeAdapter<Vec3> typeAdapter;
      private final TypeAdapter<Vec3i> typeAdapter2;
      private final TypeAdapter<BlockPos> typeAdapter3;
      private final TypeAdapter<EnumFacing> typeAdapter4;
      private final TypeAdapter<Minecraft> typeAdapter5;
      private final TypeAdapter<ScaledResolution> typeAdapter6;
      private final TypeAdapter<AxisAlignedBB> typeAdapter7;
      private final TypeAdapter<PacketBuffer> typeAdapter8;
      private final TypeAdapter<Framebuffer> typeAdapter9;

      private Map<String, Object> createPacketBuffer(ByteBuf byteBuf) {
         PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
         return this.typeAdapter8.pack(packetBuffer);
      }

      private Map<String, Object> createBlockPos(double d, double d2, double d3) {
         return this.typeAdapter3.pack(new BlockPos(d, d2, d3));
      }

      private Map<String, Object> createVec3i(int i, int i2, int i3) {
         return this.typeAdapter2.pack(new Vec3i(i, i2, i3));
      }

      private Map<String, Object> createScaledResolution(Map<String, Object> map2) {
         Minecraft minecraft2 = this.typeAdapter5.unpack(map2);
         return this.typeAdapter6.pack(new ScaledResolution(minecraft2));
      }

      public MinecraftApi() {
         ScriptValue scriptValue = ScriptValue.createScriptValue(this);
         scriptValue.putFunction3("MC_createVec3", this::createVec3);
         scriptValue.putFunction3("MC_createVec3i", this::createVec3i);
         scriptValue.putFunction3("MC_createBlockPos", this::createBlockPos);
         scriptValue.putFunction("MC_getEnumFacing", this::getEnumFacing);
         scriptValue.putFunction("MC_createScaledResolution", this::createScaledResolution);
         scriptValue.putBiFunction("MC_createAxisAlignedBB", this::createAxisAlignedBB);
         scriptValue.putBiConsumer("MC_sendPacketInternal", this::sendPacket);
         scriptValue.putFunction("MC_createPacketBuffer", this::createPacketBuffer);
         scriptValue.putFunction3("MC_createFramebuffer", this::createFramebuffer);
         scriptValue.putFunction3("MC_MathHelper_clampDouble", MathHelper::clamp_double);
         scriptValue.putFunction3("MC_MathHelper_clampInt", MathHelper::clamp_int);
         scriptValue.putFunction("MC_MathHelper_cos", MathHelper::cos);
         scriptValue.putFunction("MC_MathHelper_sin", MathHelper::sin);
         scriptValue.putFunction("MC_MathHelper_sqrtDouble", MathHelper::sqrt_double);
         scriptValue.putFunction("MC_MathHelper_floorDouble", MathHelper::floor_double);
         scriptValue.putFunction("MC_MathHelper_floorDoubleLong", MathHelper::floor_double_long);
         scriptValue.putFunction("MC_MathHelper_abs", MathHelper::abs);
         scriptValue.putFunction("MC_MathHelper_absInt", MathHelper::abs_int);
         scriptValue.putFunction("MC_MathHelper_ceilingDoubleInt", MathHelper::ceiling_double_int);
         scriptValue.putFunction3("MC_MathHelper_denormalizeClamp", MathHelper::denormalizeClamp);
         scriptValue.putBiFunction("MC_MathHelper_absMax", MathHelper::abs_max);
         scriptValue.putFunction("MC_MathHelper_wrapAngleTo180Double", MathHelper::wrapAngleTo180_double);
         scriptValue.putFunction("MC_MathHelper_roundUpToPowerOfTwo", MathHelper::roundUpToPowerOfTwo);
         scriptValue.putBiFunction("MC_MathHelper_roundUp", MathHelper::roundUp);
         scriptValue.putBiFunction("MC_MathHelper_atan2", MathHelper::atan2);
         scriptValue.putFunction3("MC_MathHelper_hsvToRGB", MathHelper::hsvToRGB);
         scriptValue.putRunnable("MC_GlStateManager_pushAttrib", GlStateManager::pushAttrib);
         scriptValue.putRunnable("MC_GlStateManager_popAttrib", GlStateManager::popAttrib);
         scriptValue.putRunnable("MC_GlStateManager_disableAlpha", GlStateManager::disableAlpha);
         scriptValue.putRunnable("MC_GlStateManager_enableAlpha", GlStateManager::enableAlpha);
         scriptValue.putBiConsumer("MC_GlStateManager_alphaFunc", GlStateManager::alphaFunc);
         scriptValue.putRunnable("MC_GlStateManager_enableLighting", GlStateManager::enableLighting);
         scriptValue.putRunnable("MC_GlStateManager_disableLighting", GlStateManager::disableLighting);
         scriptValue.putConsumer("MC_GlStateManager_enableLight", GlStateManager::enableLight);
         scriptValue.putConsumer("MC_GlStateManager_disableLight", GlStateManager::disableLight);
         scriptValue.putRunnable("MC_GlStateManager_enableColorMaterial", GlStateManager::enableColorMaterial);
         scriptValue.putRunnable("MC_GlStateManager_disableColorMaterial", GlStateManager::disableColorMaterial);
         scriptValue.putBiConsumer("MC_GlStateManager_colorMaterial", GlStateManager::colorMaterial);
         scriptValue.putRunnable("MC_GlStateManager_disableDepth", GlStateManager::disableDepth);
         scriptValue.putRunnable("MC_GlStateManager_enableDepth", GlStateManager::enableDepth);
         scriptValue.putConsumer("MC_GlStateManager_depthFunc", GlStateManager::depthFunc);
         scriptValue.putConsumer("MC_GlStateManager_depthMask", GlStateManager::depthMask);
         scriptValue.putRunnable("MC_GlStateManager_disableBlend", GlStateManager::disableBlend);
         scriptValue.putRunnable("MC_GlStateManager_enableBlend", GlStateManager::enableBlend);
         scriptValue.putBiConsumer("MC_GlStateManager_blendFunc", GlStateManager::blendFunc);
         scriptValue.putConsumer4("MC_GlStateManager_tryBlendFuncSeparate", GlStateManager::tryBlendFuncSeparate);
         scriptValue.putRunnable("MC_GlStateManager_enableFog", GlStateManager::enableFog);
         scriptValue.putRunnable("MC_GlStateManager_disableFog", GlStateManager::disableFog);
         scriptValue.putConsumer("MC_GlStateManager_setFog", GlStateManager::setFog);
         scriptValue.putConsumer("MC_GlStateManager_setFogDensity", GlStateManager::setFogDensity);
         scriptValue.putConsumer("MC_GlStateManager_setFogStart", GlStateManager::setFogStart);
         scriptValue.putConsumer("MC_GlStateManager_setFogEnd", GlStateManager::setFogEnd);
         scriptValue.putRunnable("MC_GlStateManager_enableCull", GlStateManager::enableCull);
         scriptValue.putRunnable("MC_GlStateManager_disableCull", GlStateManager::disableCull);
         scriptValue.putConsumer("MC_GlStateManager_cullFace", GlStateManager::cullFace);
         scriptValue.putRunnable("MC_GlStateManager_enablePolygonOffset", GlStateManager::enablePolygonOffset);
         scriptValue.putRunnable("MC_GlStateManager_disablePolygonOffset", GlStateManager::disablePolygonOffset);
         scriptValue.putBiConsumer("MC_GlStateManager_doPolygonOffset", GlStateManager::doPolygonOffset);
         scriptValue.putRunnable("MC_GlStateManager_enableColorLogic", GlStateManager::enableColorLogic);
         scriptValue.putRunnable("MC_GlStateManager_disableColorLogic", GlStateManager::disableColorLogic);
         scriptValue.putConsumer("MC_GlStateManager_colorLogicOp", GlStateManager::colorLogicOp);
         scriptValue.putRunnable("MC_GlStateManager_enableTexture2D", GlStateManager::enableTexture2D);
         scriptValue.putRunnable("MC_GlStateManager_disableTexture2D", GlStateManager::disableTexture2D);
         scriptValue.putSupplier("MC_GlStateManager_generateTexture", GlStateManager::generateTexture);
         scriptValue.putConsumer("MC_GlStateManager_deleteTexture", GlStateManager::deleteTexture);
         scriptValue.putConsumer("MC_GlStateManager_bindTexture", GlStateManager::bindTexture);
         scriptValue.putRunnable("MC_GlStateManager_enableNormalize", GlStateManager::enableNormalize);
         scriptValue.putRunnable("MC_GlStateManager_disableNormalize", GlStateManager::disableNormalize);
         scriptValue.putConsumer("MC_GlStateManager_shadeModel", GlStateManager::shadeModel);
         scriptValue.putRunnable("MC_GlStateManager_enableRescaleNormal", GlStateManager::enableRescaleNormal);
         scriptValue.putRunnable("MC_GlStateManager_disableRescaleNormal", GlStateManager::disableRescaleNormal);
         scriptValue.putConsumer4("MC_GlStateManager_viewport", GlStateManager::viewport);
         scriptValue.putConsumer4("MC_GlStateManager_colorMask", GlStateManager::colorMask);
         scriptValue.putConsumer("MC_GlStateManager_clearDepth", GlStateManager::clearDepth);
         scriptValue.putConsumer4("MC_GlStateManager_clearColor", GlStateManager::clearColor);
         scriptValue.putConsumer("MC_GlStateManager_clear", GlStateManager::clear);
         scriptValue.putConsumer("MC_GlStateManager_matrixMode", GlStateManager::matrixMode);
         scriptValue.putRunnable("MC_GlStateManager_loadIdentity", GlStateManager::loadIdentity);
         scriptValue.putRunnable("MC_GlStateManager_pushMatrix", GlStateManager::pushMatrix);
         scriptValue.putRunnable("MC_GlStateManager_popMatrix", GlStateManager::popMatrix);
         scriptValue.putConsumer4("MC_GlStateManager_rotate", GlStateManager::rotate);
         scriptValue.<Double, Double, Double>putConsumer3("MC_GlStateManager_scale", GlStateManager::scale);
         scriptValue.<Double, Double, Double>putConsumer3("MC_GlStateManager_translate", GlStateManager::translate);
         scriptValue.<Float, Float, Float, Float>putConsumer4("MC_GlStateManager_color", GlStateManager::color);
         scriptValue.putRunnable("MC_GlStateManager_resetColor", GlStateManager::resetColor);
         this.typeAdapter = ScriptContext.this.typeRegistry.getTypeAdapter(Vec3.class);
         this.typeAdapter2 = ScriptContext.this.typeRegistry.getTypeAdapter(Vec3i.class);
         this.typeAdapter3 = ScriptContext.this.typeRegistry.getTypeAdapter(BlockPos.class);
         this.typeAdapter4 = ScriptContext.this.typeRegistry.getTypeAdapter(EnumFacing.class);
         this.typeAdapter5 = ScriptContext.this.typeRegistry.getTypeAdapter(Minecraft.class);
         this.typeAdapter6 = ScriptContext.this.typeRegistry.getTypeAdapter(ScaledResolution.class);
         this.typeAdapter7 = ScriptContext.this.typeRegistry.getTypeAdapter(AxisAlignedBB.class);
         this.typeAdapter8 = ScriptContext.this.typeRegistry.getTypeAdapter(PacketBuffer.class);
         this.typeAdapter9 = ScriptContext.this.typeRegistry.getTypeAdapter(Framebuffer.class);
      }

      private Map<String, Object> createAxisAlignedBB(Map<String, Object> map2, Map<String, Object> map3) {
         BlockPos blockPos = this.typeAdapter3.unpack(map2);
         BlockPos blockPos2 = this.typeAdapter3.unpack(map3);
         return this.typeAdapter7.pack(new AxisAlignedBB(blockPos, blockPos2));
      }

      private void sendPacket(Map<String, Object> map2, boolean flag) {
         ScriptValue scriptValue = ScriptValue.createScriptValue(map2);
         PacketKey packetKey = new PacketKey(
            scriptValue.getInt("packetId"), (HandshakingType)scriptValue.getEnum("packetType", HandshakingType.class), ClientboundType.SERVERBOUND
         );
         AbstractTypeAdapter abstractTypeAdapter = ScriptContext.this.packetAdapterRegistry.get(packetKey);
         if (abstractTypeAdapter == null) {
            throw new IllegalArgumentException("Could not find packet " + packetKey.getPacketId() + " mapper");
         }

         Packet packet2;
         if (scriptValue.containsKey("_jObject")) {
            packet2 = (Packet)abstractTypeAdapter.apply(scriptValue, (Packet)scriptValue.getJavaObject());
         } else {
            packet2 = (Packet)abstractTypeAdapter.unpack(map2);
         }

         if (flag) {
            Minecraft.getMinecraft().getNetHandler().addToSendQueueUnregistered(packet2);
         } else {
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet2);
         }
      }

      private Map<String, Object> getEnumFacing(String text) {
         return this.typeAdapter4.pack(EnumFacing.valueOf(text));
      }

      private Map<String, Object> createFramebuffer(int i, int i2, boolean flag) {
         Framebuffer framebuffer = new Framebuffer(i, i2, flag);
         return this.typeAdapter9.pack(framebuffer);
      }

      private Map<String, Object> createVec3(double d, double d2, double d3) {
         return this.typeAdapter.pack(new Vec3(d, d2, d3));
      }
   }

   abstract static class ScriptApi extends HashMap<String, Object> {
      private ScriptApi() {
      }
   }

   public final class TimeApi extends ScriptContext.ScriptApi {
      public TimeApi() {
         ScriptValue scriptValue = ScriptValue.createScriptValue(this);
         scriptValue.putSupplier("milliTime", () -> System.nanoTime() / 1000000L);
         scriptValue.putSupplier("nanoTime", System::nanoTime);
         scriptValue.putSupplier("currentMillisTime", System::currentTimeMillis);
      }
   }
}
