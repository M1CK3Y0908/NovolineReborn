package net.optifine.reflect;

import com.google.common.base.Optional;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.model.ModelBanner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBat;
import net.minecraft.client.model.ModelBlaze;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.model.ModelGuardian;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelLeashKnot;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.model.ModelWither;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderLeashKnot;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.tileentity.TileEntityBannerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnchantmentTableRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEnderChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.api.ClassNamed;
import wtf.novoline.gui.NovolineMainMenu;
import wtf.novoline.utils.ArrayUtils;

public class Reflector {
   private static final Logger logger = LogManager.getLogger();
   private static boolean flag = checkString("*** Reflector Forge ***");
   public static ReflectorClass reflectorClass = new ReflectorClass("mods.betterfoliage.client.BetterFoliageClient");
   public static ReflectorClass reflectorClass2 = new ReflectorClass("net.minecraftforge.fml.common.asm.transformers.BlamingTransformer");
   public static ReflectorMethod reflectorMethod = new ReflectorMethod(reflectorClass2, "onCrash");
   public static ReflectorClass reflectorClass3 = new ReflectorClass("net.minecraftforge.event.world.ChunkWatchEvent$UnWatch");
   public static ReflectorConstructor reflectorConstructor = new ReflectorConstructor(
      reflectorClass3, new Class[]{ChunkCoordIntPair.class, EntityPlayerMP.class}
   );
   public static ReflectorClass reflectorClass4 = new ReflectorClass("net.minecraftforge.fml.relauncher.CoreModManager");
   public static ReflectorMethod reflectorMethod2 = new ReflectorMethod(reflectorClass4, "onCrash");
   public static ReflectorClass reflectorClass5 = new ReflectorClass("net.minecraftforge.common.DimensionManager");
   public static ReflectorMethod reflectorMethod3 = new ReflectorMethod(reflectorClass5, "createProviderFor");
   public static ReflectorMethod reflectorMethod4 = new ReflectorMethod(reflectorClass5, "getStaticDimensionIDs");
   public static ReflectorClass reflectorClass6 = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Pre");
   public static ReflectorConstructor reflectorConstructor2 = new ReflectorConstructor(
      reflectorClass6, new Class[]{GuiScreen.class, int.class, int.class, float.class}
   );
   public static ReflectorClass reflectorClass7 = new ReflectorClass("net.minecraftforge.client.event.GuiScreenEvent$DrawScreenEvent$Post");
   public static ReflectorConstructor reflectorConstructor3 = new ReflectorConstructor(
      reflectorClass7, new Class[]{GuiScreen.class, int.class, int.class, float.class}
   );
   public static ReflectorClass reflectorClass8 = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$CameraSetup");
   public static ReflectorConstructor reflectorConstructor4 = new ReflectorConstructor(
      reflectorClass8, new Class[]{EntityRenderer.class, Entity.class, Block.class, double.class, float.class, float.class, float.class}
   );
   public static ReflectorField reflectorField = new ReflectorField(reflectorClass8, "yaw");
   public static ReflectorField reflectorField2 = new ReflectorField(reflectorClass8, "pitch");
   public static ReflectorField reflectorField3 = new ReflectorField(reflectorClass8, "roll");
   public static ReflectorClass reflectorClass9 = new ReflectorClass("net.minecraftforge.client.event.EntityViewRenderEvent$FogColors");
   public static ReflectorConstructor reflectorConstructor5 = new ReflectorConstructor(
      reflectorClass9, new Class[]{EntityRenderer.class, Entity.class, Block.class, double.class, float.class, float.class, float.class}
   );
   public static ReflectorField reflectorField4 = new ReflectorField(reflectorClass9, "red");
   public static ReflectorField reflectorField5 = new ReflectorField(reflectorClass9, "green");
   public static ReflectorField reflectorField6 = new ReflectorField(reflectorClass9, "blue");
   public static ReflectorClass reflectorClass10 = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event");
   public static ReflectorMethod reflectorMethod5 = new ReflectorMethod(reflectorClass10, "isCanceled");
   public static ReflectorClass reflectorClass11 = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.EventBus");
   public static ReflectorMethod reflectorMethod6 = new ReflectorMethod(reflectorClass11, "post");
   public static ReflectorClass reflectorClass12 = new ReflectorClass("net.minecraftforge.fml.common.eventhandler.Event$Result");
   public static ReflectorField reflectorField7 = new ReflectorField(reflectorClass12, "DENY");
   public static ReflectorField reflectorField8 = new ReflectorField(reflectorClass12, "ALLOW");
   public static ReflectorField reflectorField9 = new ReflectorField(reflectorClass12, "DEFAULT");
   public static ReflectorClass reflectorClass13 = new ReflectorClass("net.minecraftforge.common.property.ExtendedBlockState");
   public static ReflectorConstructor reflectorConstructor6 = new ReflectorConstructor(
      reflectorClass13, new Class[]{Block.class, IProperty[].class, ClassNamed[].class}
   );
   public static ReflectorClass reflectorClass14 = new ReflectorClass("net.minecraftforge.fml.client.FMLClientHandler");
   public static ReflectorMethod reflectorMethod7 = new ReflectorMethod(reflectorClass14, "instance");
   public static ReflectorMethod reflectorMethod8 = new ReflectorMethod(reflectorClass14, "handleLoadingScreen");
   public static ReflectorMethod reflectorMethod9 = new ReflectorMethod(reflectorClass14, "isLoading");
   public static ReflectorMethod reflectorMethod10 = new ReflectorMethod(reflectorClass14, "trackBrokenTexture");
   public static ReflectorMethod reflectorMethod11 = new ReflectorMethod(reflectorClass14, "trackMissingTexture");
   public static ReflectorClass reflectorClass15 = new ReflectorClass("net.minecraftforge.fml.common.FMLCommonHandler");
   public static ReflectorMethod reflectorMethod12 = new ReflectorMethod(reflectorClass15, "callFuture");
   public static ReflectorMethod reflectorMethod13 = new ReflectorMethod(reflectorClass15, "enhanceCrashReport");
   public static ReflectorMethod reflectorMethod14 = new ReflectorMethod(reflectorClass15, "getBrandings");
   public static ReflectorMethod reflectorMethod15 = new ReflectorMethod(reflectorClass15, "handleServerAboutToStart");
   public static ReflectorMethod reflectorMethod16 = new ReflectorMethod(reflectorClass15, "handleServerStarting");
   public static ReflectorMethod reflectorMethod17 = new ReflectorMethod(reflectorClass15, "instance");
   public static ReflectorClass reflectorClass16 = new ReflectorClass(BiomeGenBase.class);
   public static ReflectorMethod reflectorMethod18 = new ReflectorMethod(reflectorClass16, "getWaterColorMultiplier");
   public static ReflectorClass reflectorClass17 = new ReflectorClass(Block.class);
   public static ReflectorMethod reflectorMethod19 = new ReflectorMethod(reflectorClass17, "addDestroyEffects");
   public static ReflectorMethod reflectorMethod20 = new ReflectorMethod(reflectorClass17, "addHitEffects");
   public static ReflectorMethod reflectorMethod21 = new ReflectorMethod(reflectorClass17, "canCreatureSpawn");
   public static ReflectorMethod reflectorMethod22 = new ReflectorMethod(reflectorClass17, "canRenderInLayer", new Class[]{EnumWorldBlockLayer.class});
   public static ReflectorMethod reflectorMethod23 = new ReflectorMethod(reflectorClass17, "doesSideBlockRendering");
   public static ReflectorMethod reflectorMethod24 = new ReflectorMethod(reflectorClass17, "getBedDirection");
   public static ReflectorMethod reflectorMethod25 = new ReflectorMethod(reflectorClass17, "getExtendedState");
   public static ReflectorMethod reflectorMethod26 = new ReflectorMethod(reflectorClass17, "getLightOpacity", new Class[]{IBlockAccess.class, BlockPos.class});
   public static ReflectorMethod reflectorMethod27 = new ReflectorMethod(reflectorClass17, "getLightValue", new Class[]{IBlockAccess.class, BlockPos.class});
   public static ReflectorMethod reflectorMethod28 = new ReflectorMethod(reflectorClass17, "hasTileEntity", new Class[]{IBlockState.class});
   public static ReflectorMethod reflectorMethod29 = new ReflectorMethod(reflectorClass17, "isAir");
   public static ReflectorMethod reflectorMethod30 = new ReflectorMethod(reflectorClass17, "isBed");
   public static ReflectorMethod reflectorMethod31 = new ReflectorMethod(reflectorClass17, "isBedFoot");
   public static ReflectorMethod reflectorMethod32 = new ReflectorMethod(reflectorClass17, "isSideSolid");
   public static ReflectorClass reflectorClass18 = new ReflectorClass(ChunkCache.class);
   public static ReflectorMethod reflectorMethod33 = new ReflectorMethod(reflectorClass18, "isSideSolid");
   public static ReflectorClass reflectorClass19 = new ReflectorClass(Entity.class);
   public static ReflectorMethod reflectorMethod34 = new ReflectorMethod(reflectorClass19, "canRiderInteract");
   public static ReflectorField reflectorField10 = new ReflectorField(reflectorClass19, "captureDrops");
   public static ReflectorField reflectorField11 = new ReflectorField(reflectorClass19, "capturedDrops");
   public static ReflectorMethod reflectorMethod35 = new ReflectorMethod(reflectorClass19, "shouldRenderInPass");
   public static ReflectorMethod reflectorMethod36 = new ReflectorMethod(reflectorClass19, "shouldRiderSit");
   public static ReflectorClass reflectorClass20 = new ReflectorClass("net.minecraftforge.event.ForgeEventFactory");
   public static ReflectorMethod reflectorMethod37 = new ReflectorMethod(reflectorClass20, "canEntityDespawn");
   public static ReflectorMethod reflectorMethod38 = new ReflectorMethod(reflectorClass20, "canEntitySpawn");
   public static ReflectorMethod reflectorMethod39 = new ReflectorMethod(
      reflectorClass20, "doSpecialSpawn", new Class[]{EntityLiving.class, World.class, float.class, float.class, float.class}
   );
   public static ReflectorMethod reflectorMethod40 = new ReflectorMethod(reflectorClass20, "getMaxSpawnPackSize");
   public static ReflectorMethod reflectorMethod41 = new ReflectorMethod(reflectorClass20, "renderBlockOverlay");
   public static ReflectorMethod reflectorMethod42 = new ReflectorMethod(reflectorClass20, "renderFireOverlay");
   public static ReflectorMethod reflectorMethod43 = new ReflectorMethod(reflectorClass20, "renderWaterOverlay");
   public static ReflectorClass reflectorClass21 = new ReflectorClass("net.minecraftforge.common.ForgeHooks");
   public static ReflectorMethod reflectorMethod44 = new ReflectorMethod(reflectorClass21, "onLivingAttack");
   public static ReflectorMethod reflectorMethod45 = new ReflectorMethod(reflectorClass21, "onLivingDeath");
   public static ReflectorMethod reflectorMethod46 = new ReflectorMethod(reflectorClass21, "onLivingDrops");
   public static ReflectorMethod reflectorMethod47 = new ReflectorMethod(reflectorClass21, "onLivingFall");
   public static ReflectorMethod reflectorMethod48 = new ReflectorMethod(reflectorClass21, "onLivingHurt");
   public static ReflectorMethod reflectorMethod49 = new ReflectorMethod(reflectorClass21, "onLivingJump");
   public static ReflectorMethod reflectorMethod50 = new ReflectorMethod(reflectorClass21, "onLivingSetAttackTarget");
   public static ReflectorMethod reflectorMethod51 = new ReflectorMethod(reflectorClass21, "onLivingUpdate");
   public static ReflectorClass reflectorClass22 = new ReflectorClass("net.minecraftforge.client.ForgeHooksClient");
   public static ReflectorMethod reflectorMethod52 = new ReflectorMethod(reflectorClass22, "applyTransform", new Class[]{Matrix4f.class, Optional.class});
   public static ReflectorMethod reflectorMethod53 = new ReflectorMethod(reflectorClass22, "dispatchRenderLast");
   public static ReflectorMethod reflectorMethod54 = new ReflectorMethod(reflectorClass22, "drawScreen");
   public static ReflectorMethod reflectorMethod55 = new ReflectorMethod(reflectorClass22, "fillNormal");
   public static ReflectorMethod reflectorMethod56 = new ReflectorMethod(reflectorClass22, "handleCameraTransforms");
   public static ReflectorMethod reflectorMethod57 = new ReflectorMethod(reflectorClass22, "getArmorModel");
   public static ReflectorMethod reflectorMethod58 = new ReflectorMethod(reflectorClass22, "getArmorTexture");
   public static ReflectorMethod reflectorMethod59 = new ReflectorMethod(reflectorClass22, "getFogDensity");
   public static ReflectorMethod reflectorMethod60 = new ReflectorMethod(reflectorClass22, "getFOVModifier");
   public static ReflectorMethod reflectorMethod61 = new ReflectorMethod(reflectorClass22, "getMatrix", new Class[]{ModelRotation.class});
   public static ReflectorMethod reflectorMethod62 = new ReflectorMethod(reflectorClass22, "getOffsetFOV");
   public static ReflectorMethod reflectorMethod63 = new ReflectorMethod(reflectorClass22, "loadEntityShader");
   public static ReflectorMethod reflectorMethod64 = new ReflectorMethod(reflectorClass22, "onDrawBlockHighlight");
   public static ReflectorMethod reflectorMethod65 = new ReflectorMethod(reflectorClass22, "onFogRender");
   public static ReflectorMethod reflectorMethod66 = new ReflectorMethod(reflectorClass22, "onTextureStitchedPre");
   public static ReflectorMethod reflectorMethod67 = new ReflectorMethod(reflectorClass22, "onTextureStitchedPost");
   public static ReflectorMethod reflectorMethod68 = new ReflectorMethod(reflectorClass22, "orientBedCamera");
   public static ReflectorMethod reflectorMethod69 = new ReflectorMethod(reflectorClass22, "putQuadColor");
   public static ReflectorMethod reflectorMethod70 = new ReflectorMethod(reflectorClass22, "renderFirstPersonHand");
   public static ReflectorMethod reflectorMethod71 = new ReflectorMethod(reflectorClass22, "renderMainMenu");
   public static ReflectorMethod reflectorMethod72 = new ReflectorMethod(reflectorClass22, "setRenderLayer");
   public static ReflectorMethod reflectorMethod73 = new ReflectorMethod(reflectorClass22, "setRenderPass");
   public static ReflectorMethod reflectorMethod74 = new ReflectorMethod(reflectorClass22, "transform");
   public static ReflectorClass reflectorClass23 = new ReflectorClass(Item.class);
   public static ReflectorField reflectorField12 = new ReflectorField(reflectorClass23, "delegate");
   public static ReflectorMethod reflectorMethod75 = new ReflectorMethod(reflectorClass23, "getDurabilityForDisplay");
   public static ReflectorMethod reflectorMethod76 = new ReflectorMethod(reflectorClass23, "getModel");
   public static ReflectorMethod reflectorMethod77 = new ReflectorMethod(reflectorClass23, "onEntitySwing");
   public static ReflectorMethod reflectorMethod78 = new ReflectorMethod(reflectorClass23, "shouldCauseReequipAnimation");
   public static ReflectorMethod reflectorMethod79 = new ReflectorMethod(reflectorClass23, "showDurabilityBar");
   public static ReflectorClass reflectorClass24 = new ReflectorClass("net.minecraftforge.common.ForgeModContainer");
   public static ReflectorField reflectorField13 = new ReflectorField(reflectorClass24, "forgeLightPipelineEnabled");
   public static ReflectorClass reflectorClass25 = new ReflectorClass(PotionEffect.class);
   public static ReflectorMethod reflectorMethod80 = new ReflectorMethod(reflectorClass25, "isCurativeItem");
   public static ReflectorClass reflectorClass26 = new ReflectorClass(TileEntity.class);
   public static ReflectorMethod reflectorMethod81 = new ReflectorMethod(reflectorClass26, "canRenderBreaking");
   public static ReflectorMethod reflectorMethod82 = new ReflectorMethod(reflectorClass26, "getRenderBoundingBox");
   public static ReflectorMethod reflectorMethod83 = new ReflectorMethod(reflectorClass26, "hasFastRenderer");
   public static ReflectorMethod reflectorMethod84 = new ReflectorMethod(reflectorClass26, "shouldRenderInPass");
   public static ReflectorClass reflectorClass27 = new ReflectorClass(EnumUsage.class);
   public static ReflectorMethod reflectorMethod85 = new ReflectorMethod(reflectorClass27, "preDraw");
   public static ReflectorMethod reflectorMethod86 = new ReflectorMethod(reflectorClass27, "postDraw");
   public static ReflectorClass reflectorClass28 = new ReflectorClass(World.class);
   public static ReflectorMethod reflectorMethod87 = new ReflectorMethod(reflectorClass28, "countEntities", new Class[]{EnumCreatureType.class, boolean.class});
   public static ReflectorMethod reflectorMethod88 = new ReflectorMethod(reflectorClass28, "getPerWorldStorage");
   public static ReflectorClass reflectorClass29 = new ReflectorClass(WorldProvider.class);
   public static ReflectorMethod reflectorMethod89 = new ReflectorMethod(reflectorClass29, "getCloudRenderer");
   public static ReflectorMethod reflectorMethod90 = new ReflectorMethod(reflectorClass29, "getSkyRenderer");
   public static ReflectorMethod reflectorMethod91 = new ReflectorMethod(reflectorClass29, "getWeatherRenderer");
   public static ReflectorMethod reflectorMethod92 = new ReflectorMethod(reflectorClass29, "getSaveFolder");
   public static ReflectorClass reflectorClass30 = new ReflectorClass("net.minecraftforge.fml.client.GuiModList");
   public static ReflectorConstructor reflectorConstructor7 = new ReflectorConstructor(reflectorClass30, new Class[]{GuiScreen.class});
   public static ReflectorClass reflectorClass31 = new ReflectorClass("net.minecraftforge.client.model.IColoredBakedQuad");
   public static ReflectorClass reflectorClass32 = new ReflectorClass("net.minecraftforge.common.property.IExtendedBlockState");
   public static ReflectorMethod reflectorMethod93 = new ReflectorMethod(reflectorClass32, "getClean");
   public static ReflectorClass reflectorClass33 = new ReflectorClass("net.minecraftforge.client.model.IModel");
   public static ReflectorMethod reflectorMethod94 = new ReflectorMethod(reflectorClass33, "getTextures");
   public static ReflectorClass reflectorClass34 = new ReflectorClass("net.minecraftforge.client.IRenderHandler");
   public static ReflectorMethod reflectorMethod95 = new ReflectorMethod(reflectorClass34, "render");
   public static ReflectorClass reflectorClass35 = new ReflectorClass("net.minecraftforge.client.ItemModelMesherForge");
   public static ReflectorConstructor reflectorConstructor8 = new ReflectorConstructor(reflectorClass35, new Class[]{ModelManager.class});
   public static ReflectorClass reflectorClass36 = new ReflectorClass("net.minecraft.launchwrapper.Launch");
   public static ReflectorField reflectorField14 = new ReflectorField(reflectorClass36, "blackboard");
   public static ReflectorClass reflectorClass37 = new ReflectorClass("net.minecraftforge.client.model.pipeline.LightUtil");
   public static ReflectorField reflectorField15 = new ReflectorField(reflectorClass37, "itemConsumer");
   public static ReflectorMethod reflectorMethod96 = new ReflectorMethod(reflectorClass37, "putBakedQuad");
   public static ReflectorMethod reflectorMethod97 = new ReflectorMethod(reflectorClass37, "renderQuadColor");
   public static ReflectorField reflectorField16 = new ReflectorField(reflectorClass37, "tessellator");
   public static ReflectorClass reflectorClass38 = new ReflectorClass("net.minecraftforge.fml.common.Loader");
   public static ReflectorMethod reflectorMethod98 = new ReflectorMethod(reflectorClass38, "getActiveModList");
   public static ReflectorMethod reflectorMethod99 = new ReflectorMethod(reflectorClass38, "instance");
   public static ReflectorClass reflectorClass39 = new ReflectorClass("net.minecraftforge.common.MinecraftForge");
   public static ReflectorField reflectorField17 = new ReflectorField(reflectorClass39, "EVENT_BUS");
   public static ReflectorClass reflectorClass40 = new ReflectorClass("net.minecraftforge.client.MinecraftForgeClient");
   public static ReflectorMethod reflectorMethod100 = new ReflectorMethod(reflectorClass40, "getRenderPass");
   public static ReflectorMethod reflectorMethod101 = new ReflectorMethod(reflectorClass40, "onRebuildChunk");
   public static ReflectorClass reflectorClass41 = new ReflectorClass("net.minecraftforge.fml.common.ModContainer");
   public static ReflectorMethod reflectorMethod102 = new ReflectorMethod(reflectorClass41, "getModId");
   public static ReflectorClass reflectorClass42 = new ReflectorClass("net.minecraftforge.client.model.ModelLoader");
   public static ReflectorField reflectorField18 = new ReflectorField(reflectorClass42, "stateModels");
   public static ReflectorMethod reflectorMethod103 = new ReflectorMethod(reflectorClass42, "onRegisterItems");
   public static ReflectorMethod reflectorMethod104 = new ReflectorMethod(reflectorClass42, "getInventoryVariant");
   public static ReflectorField reflectorField19 = new ReflectorField(reflectorClass42, "textures");
   public static ReflectorClass reflectorClass43 = new ReflectorClass("net.minecraftforge.client.model.ModelLoader$VanillaLoader");
   public static ReflectorField reflectorField20 = new ReflectorField(reflectorClass43, "instance");
   public static ReflectorMethod reflectorMethod105 = new ReflectorMethod(reflectorClass43, "loadModel");
   public static ReflectorClass reflectorClass44 = new ReflectorClass("net.minecraftforge.client.event.RenderBlockOverlayEvent$OverlayType");
   public static ReflectorField reflectorField21 = new ReflectorField(reflectorClass44, "BLOCK");
   public static ReflectorClass reflectorClass45 = new ReflectorClass("net.minecraftforge.fml.client.registry.RenderingRegistry");
   public static ReflectorMethod reflectorMethod106 = new ReflectorMethod(reflectorClass45, "loadEntityRenderers", new Class[]{RenderManager.class, Map.class});
   public static ReflectorClass reflectorClass46 = new ReflectorClass("net.minecraftforge.client.event.RenderItemInFrameEvent");
   public static ReflectorConstructor reflectorConstructor9 = new ReflectorConstructor(
      reflectorClass46, new Class[]{EntityItemFrame.class, RenderItemFrame.class}
   );
   public static ReflectorClass reflectorClass47 = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Pre");
   public static ReflectorConstructor reflectorConstructor10 = new ReflectorConstructor(
      reflectorClass47, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass reflectorClass48 = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Post");
   public static ReflectorConstructor reflectorConstructor11 = new ReflectorConstructor(
      reflectorClass48, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass reflectorClass49 = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Pre");
   public static ReflectorConstructor reflectorConstructor12 = new ReflectorConstructor(
      reflectorClass49, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass reflectorClass50 = new ReflectorClass("net.minecraftforge.client.event.RenderLivingEvent$Specials$Post");
   public static ReflectorConstructor reflectorConstructor13 = new ReflectorConstructor(
      reflectorClass50, new Class[]{EntityLivingBase.class, RendererLivingEntity.class, double.class, double.class, double.class}
   );
   public static ReflectorClass reflectorClass51 = new ReflectorClass("net.minecraftforge.fml.client.SplashProgress");
   public static ReflectorClass reflectorClass52 = new ReflectorClass("net.minecraftforge.event.world.WorldEvent$Load");
   public static ReflectorConstructor reflectorConstructor14 = new ReflectorConstructor(reflectorClass52, new Class[]{World.class});
   private static boolean flag2 = checkString("*** Reflector Vanilla ***");
   public static ReflectorClass reflectorClass53 = new ReflectorClass(ChunkProviderClient.class);
   public static ReflectorField reflectorField22 = new ReflectorField(reflectorClass53, LongHashMap.class);
   public static ReflectorClass reflectorClass54 = new ReflectorClass(EntityVillager.class);
   public static ReflectorField reflectorField23 = new ReflectorField(
      new FieldLocatorTypes(
         EntityVillager.class, new Class[0], int.class, new Class[]{int.class, boolean.class, boolean.class, InventoryBasic.class}, "EntityVillager.careerId"
      )
   );
   public static ReflectorField reflectorField24 = new ReflectorField(
      new FieldLocatorTypes(
         EntityVillager.class, new Class[]{int.class}, int.class, new Class[]{boolean.class, boolean.class, InventoryBasic.class}, "EntityVillager.careerLevel"
      )
   );
   public static ReflectorClass reflectorClass55 = new ReflectorClass(GuiBeacon.class);
   public static ReflectorField reflectorField25 = new ReflectorField(reflectorClass55, IInventory.class);
   public static ReflectorClass reflectorClass56 = new ReflectorClass(GuiBrewingStand.class);
   public static ReflectorField reflectorField26 = new ReflectorField(reflectorClass56, IInventory.class);
   public static ReflectorClass reflectorClass57 = new ReflectorClass(GuiChest.class);
   public static ReflectorField reflectorField27 = new ReflectorField(reflectorClass57, IInventory.class, 1);
   public static ReflectorClass reflectorClass58 = new ReflectorClass(GuiEnchantment.class);
   public static ReflectorField reflectorField28 = new ReflectorField(reflectorClass58, IWorldNameable.class);
   public static ReflectorClass reflectorClass59 = new ReflectorClass(GuiFurnace.class);
   public static ReflectorField reflectorField29 = new ReflectorField(reflectorClass59, IInventory.class);
   public static ReflectorClass reflectorClass60 = new ReflectorClass(GuiHopper.class);
   public static ReflectorField reflectorField30 = new ReflectorField(reflectorClass60, IInventory.class, 1);
   public static ReflectorClass reflectorClass61 = new ReflectorClass(NovolineMainMenu.class);
   public static ReflectorField reflectorField31 = new ReflectorField(reflectorClass61, String.class);
   public static ReflectorClass reflectorClass62 = new ReflectorClass(Minecraft.class);
   public static ReflectorField reflectorField32 = new ReflectorField(reflectorClass62, DefaultResourcePack.class);
   public static ReflectorClass reflectorClass63 = new ReflectorClass(ModelHumanoidHead.class);
   public static ReflectorField reflectorField33 = new ReflectorField(reflectorClass63, ModelRenderer.class);
   public static ReflectorClass reflectorClass64 = new ReflectorClass(ModelBat.class);
   public static ReflectorFields reflectorFields = new ReflectorFields(reflectorClass64, ModelRenderer.class, 6);
   public static ReflectorClass reflectorClass65 = new ReflectorClass(ModelBlaze.class);
   public static ReflectorField reflectorField34 = new ReflectorField(reflectorClass65, ModelRenderer.class);
   public static ReflectorField reflectorField35 = new ReflectorField(reflectorClass65, ModelRenderer[].class);
   public static ReflectorClass reflectorClass66 = new ReflectorClass(ModelBlock.class);
   public static ReflectorField reflectorField36 = new ReflectorField(reflectorClass66, ResourceLocation.class);
   public static ReflectorField reflectorField37 = new ReflectorField(reflectorClass66, Map.class);
   public static ReflectorClass reflectorClass67 = new ReflectorClass(ModelDragon.class);
   public static ReflectorFields reflectorFields2 = new ReflectorFields(reflectorClass67, ModelRenderer.class, 12);
   public static ReflectorClass reflectorClass68 = new ReflectorClass(ModelEnderCrystal.class);
   public static ReflectorFields reflectorFields3 = new ReflectorFields(reflectorClass68, ModelRenderer.class, 3);
   public static ReflectorClass reflectorClass69 = new ReflectorClass(RenderEnderCrystal.class);
   public static ReflectorField reflectorField38 = new ReflectorField(reflectorClass69, ModelBase.class, 0);
   public static ReflectorClass reflectorClass70 = new ReflectorClass(ModelEnderMite.class);
   public static ReflectorField reflectorField39 = new ReflectorField(reflectorClass70, ModelRenderer[].class);
   public static ReflectorClass reflectorClass71 = new ReflectorClass(ModelGhast.class);
   public static ReflectorField reflectorField40 = new ReflectorField(reflectorClass71, ModelRenderer.class);
   public static ReflectorField reflectorField41 = new ReflectorField(reflectorClass71, ModelRenderer[].class);
   public static ReflectorClass reflectorClass72 = new ReflectorClass(ModelGuardian.class);
   public static ReflectorField reflectorField42 = new ReflectorField(reflectorClass72, ModelRenderer.class, 0);
   public static ReflectorField reflectorField43 = new ReflectorField(reflectorClass72, ModelRenderer.class, 1);
   public static ReflectorField reflectorField44 = new ReflectorField(reflectorClass72, ModelRenderer[].class, 0);
   public static ReflectorField reflectorField45 = new ReflectorField(reflectorClass72, ModelRenderer[].class, 1);
   public static ReflectorClass reflectorClass73 = new ReflectorClass(ModelHorse.class);
   public static ReflectorFields reflectorFields4 = new ReflectorFields(reflectorClass73, ModelRenderer.class, 39);
   public static ReflectorClass reflectorClass74 = new ReflectorClass(RenderLeashKnot.class);
   public static ReflectorField reflectorField46 = new ReflectorField(reflectorClass74, ModelLeashKnot.class);
   public static ReflectorClass reflectorClass75 = new ReflectorClass(ModelMagmaCube.class);
   public static ReflectorField reflectorField47 = new ReflectorField(reflectorClass75, ModelRenderer.class);
   public static ReflectorField reflectorField48 = new ReflectorField(reflectorClass75, ModelRenderer[].class);
   public static ReflectorClass reflectorClass76 = new ReflectorClass(ModelOcelot.class);
   public static ReflectorFields reflectorFields5 = new ReflectorFields(reflectorClass76, ModelRenderer.class, 8);
   public static ReflectorClass reflectorClass77 = new ReflectorClass(ModelRabbit.class);
   public static ReflectorFields reflectorFields6 = new ReflectorFields(reflectorClass77, ModelRenderer.class, 12);
   public static ReflectorClass reflectorClass78 = new ReflectorClass(ModelSilverfish.class);
   public static ReflectorField reflectorField49 = new ReflectorField(reflectorClass78, ModelRenderer[].class, 0);
   public static ReflectorField reflectorField50 = new ReflectorField(reflectorClass78, ModelRenderer[].class, 1);
   public static ReflectorClass reflectorClass79 = new ReflectorClass(ModelSlime.class);
   public static ReflectorFields reflectorFields7 = new ReflectorFields(reflectorClass79, ModelRenderer.class, 4);
   public static ReflectorClass reflectorClass80 = new ReflectorClass(ModelSquid.class);
   public static ReflectorField reflectorField51 = new ReflectorField(reflectorClass80, ModelRenderer.class);
   public static ReflectorField reflectorField52 = new ReflectorField(reflectorClass80, ModelRenderer[].class);
   public static ReflectorClass reflectorClass81 = new ReflectorClass(ModelWitch.class);
   public static ReflectorField reflectorField53 = new ReflectorField(reflectorClass81, ModelRenderer.class, 0);
   public static ReflectorField reflectorField54 = new ReflectorField(reflectorClass81, ModelRenderer.class, 1);
   public static ReflectorClass reflectorClass82 = new ReflectorClass(ModelWither.class);
   public static ReflectorField reflectorField55 = new ReflectorField(reflectorClass82, ModelRenderer[].class, 0);
   public static ReflectorField reflectorField56 = new ReflectorField(reflectorClass82, ModelRenderer[].class, 1);
   public static ReflectorClass reflectorClass83 = new ReflectorClass(ModelWolf.class);
   public static ReflectorField reflectorField57 = new ReflectorField(reflectorClass83, ModelRenderer.class, 6);
   public static ReflectorField reflectorField58 = new ReflectorField(reflectorClass83, ModelRenderer.class, 7);
   public static ReflectorClass reflectorClass84 = new ReflectorClass("optifine.OptiFineClassTransformer");
   public static ReflectorField reflectorField59 = new ReflectorField(reflectorClass84, "instance");
   public static ReflectorMethod reflectorMethod107 = new ReflectorMethod(reflectorClass84, "getOptiFineResource");
   public static ReflectorClass reflectorClass85 = new ReflectorClass(RenderBoat.class);
   public static ReflectorField reflectorField60 = new ReflectorField(reflectorClass85, ModelBase.class);
   public static ReflectorClass reflectorClass86 = new ReflectorClass(RenderMinecart.class);
   public static ReflectorField reflectorField61 = new ReflectorField(reflectorClass86, ModelBase.class);
   public static ReflectorClass reflectorClass87 = new ReflectorClass(RenderWitherSkull.class);
   public static ReflectorField reflectorField62 = new ReflectorField(reflectorClass87, ModelSkeletonHead.class);
   public static ReflectorClass reflectorClass88 = new ReflectorClass(TileEntityBannerRenderer.class);
   public static ReflectorField reflectorField63 = new ReflectorField(reflectorClass88, ModelBanner.class);
   public static ReflectorClass reflectorClass89 = new ReflectorClass(TileEntityBeacon.class);
   public static ReflectorField reflectorField64 = new ReflectorField(reflectorClass89, String.class);
   public static ReflectorClass reflectorClass90 = new ReflectorClass(TileEntityBrewingStand.class);
   public static ReflectorField reflectorField65 = new ReflectorField(reflectorClass90, String.class);
   public static ReflectorClass reflectorClass91 = new ReflectorClass(TileEntityChestRenderer.class);
   public static ReflectorField reflectorField66 = new ReflectorField(reflectorClass91, ModelChest.class, 0);
   public static ReflectorField reflectorField67 = new ReflectorField(reflectorClass91, ModelChest.class, 1);
   public static ReflectorClass reflectorClass92 = new ReflectorClass(TileEntityEnchantmentTable.class);
   public static ReflectorField reflectorField68 = new ReflectorField(reflectorClass92, String.class);
   public static ReflectorClass reflectorClass93 = new ReflectorClass(TileEntityEnchantmentTableRenderer.class);
   public static ReflectorField reflectorField69 = new ReflectorField(reflectorClass93, ModelBook.class);
   public static ReflectorClass reflectorClass94 = new ReflectorClass(TileEntityEnderChestRenderer.class);
   public static ReflectorField reflectorField70 = new ReflectorField(reflectorClass94, ModelChest.class);
   public static ReflectorClass reflectorClass95 = new ReflectorClass(TileEntityFurnace.class);
   public static ReflectorField reflectorField71 = new ReflectorField(reflectorClass95, String.class);
   public static ReflectorClass reflectorClass96 = new ReflectorClass(TileEntitySignRenderer.class);
   public static ReflectorField reflectorField72 = new ReflectorField(reflectorClass96, ModelSign.class);
   public static ReflectorClass reflectorClass97 = new ReflectorClass(TileEntitySkullRenderer.class);
   public static ReflectorField reflectorField73 = new ReflectorField(reflectorClass97, ModelSkeletonHead.class, 0);
   public static ReflectorField reflectorField74 = new ReflectorField(reflectorClass97, ModelSkeletonHead.class, 1);

   private static boolean checkString(String text) {
      logger.info("[OptiFine] " + text);
      return true;
   }

   public static double createDouble(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? 0.0 : (Double)method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
         return 0.0;
      }
   }

   public static void handleReflectorMethod(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         if (method2 == null) {
            return;
         }

         method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
      }
   }

   public static boolean checkObject(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? false : (Boolean)method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
         return false;
      }
   }

   public static float createFloat(Object object, ReflectorField reflectorField75, float f) {
      try {
         Field field = reflectorField75.getField();
         return field == null ? f : field.getFloat(object);
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return f;
      }
   }

   public static void handleObject(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         if (object == null) {
            return;
         }

         Method method2 = reflectorMethod108.getMethod();
         if (method2 == null) {
            return;
         }

         method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
      }
   }

   public static String createString(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? null : (String)method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
         return null;
      }
   }

   public static boolean checkObject4(Object object, ReflectorField reflectorField75, Object object2) {
      try {
         Field field = reflectorField75.getField();
         if (field == null) {
            return false;
         }

         field.set(object, object2);
         return true;
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return false;
      }
   }

   public static int createInt3(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? 0 : (Integer)method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
         return 0;
      }
   }

   public static boolean checkObject5(Object object, ReflectorField reflectorField75, int i) {
      try {
         Field field = reflectorField75.getField();
         if (field == null) {
            return false;
         }

         field.setInt(object, i);
         return true;
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return false;
      }
   }

   public static boolean checkObject2(Object object) {
      if (object == null) {
         return false;
      }

      Object object2 = createObject(reflectorField17);
      if (object2 == null) {
         return false;
      }

      Object object3 = createObject4(object2, reflectorMethod6, object);
      return !(object3 instanceof Boolean) ? false : (Boolean)object3;
   }

   public static int createInt2(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? 0 : (Integer)method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
         return 0;
      }
   }

   public static int createInt(Object object, ReflectorField reflectorField75, int i) {
      try {
         Field field = reflectorField75.getField();
         return field == null ? i : field.getInt(object);
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return i;
      }
   }

   private static void handleThrowable2(Throwable throwable, ReflectorConstructor reflectorConstructor15, Object[] objects) {
      if (throwable instanceof InvocationTargetException) {
         Log.handleString2("", throwable);
      } else {
         Log.handleString3("*** Exception outside of constructor ***");
         Log.handleString3("Constructor deactivated: " + reflectorConstructor15.getConstructor());
         reflectorConstructor15.update();
         if (throwable instanceof IllegalArgumentException) {
            Log.handleString3("*** IllegalArgumentException ***");
            Log.handleString3("Constructor: " + reflectorConstructor15.getConstructor());
            Log.handleString3("Parameter classes: " + ArrayUtils.joinWithComma(createArray(objects)));
            Log.handleString3("Parameters: " + ArrayUtils.joinWithComma(objects));
         }

         Log.handleString("", throwable);
      }
   }

   public static boolean checkObject3(Object object, ReflectorField reflectorField75) {
      try {
         Field field = reflectorField75.getField();
         return field == null ? true : field.getBoolean(object);
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return true;
      }
   }

   public static boolean checkArray(Class[] clazzs, Class[] clazzs2) {
      if (clazzs.length != clazzs2.length) {
         return false;
      }

      for (int i = 0; i < clazzs2.length; i++) {
         Class clazz = clazzs[i];
         Class clazz2 = clazzs2[i];
         if (clazz != clazz2) {
            return false;
         }
      }

      return true;
   }

   public static Object createObject(ReflectorField reflectorField75) {
      return createObject6(null, reflectorField75);
   }

   public static Object createObject6(Object object, ReflectorField reflectorField75) {
      try {
         Field field = reflectorField75.getField();
         return field == null ? null : field.get(object);
      } catch (Throwable throwable) {
         Log.handleString2("", throwable);
         return null;
      }
   }

   public static Object createObject4(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? null : method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
         return null;
      }
   }

   private static void handleThrowable(Throwable throwable, Object object, ReflectorMethod reflectorMethod108, Object[] objects) {
      if (throwable instanceof InvocationTargetException) {
         Throwable cause = throwable.getCause();
         if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
         }

         Log.handleString2("", throwable);
      } else {
         Log.handleString3("*** Exception outside of method ***");
         Log.handleString3("Method deactivated: " + reflectorMethod108.getMethod());
         reflectorMethod108.update2();
         if (throwable instanceof IllegalArgumentException) {
            Log.handleString3("*** IllegalArgumentException ***");
            Log.handleString3("Method: " + reflectorMethod108.getMethod());
            Log.handleString3("Object: " + object);
            Log.handleString3("Parameter classes: " + ArrayUtils.joinWithComma(createArray(objects)));
            Log.handleString3("Parameters: " + ArrayUtils.joinWithComma(objects));
         }

         Log.handleString("", throwable);
      }
   }

   public static Object createObject5(ReflectorConstructor reflectorConstructor15, Object... objects) {
      Constructor constructor2 = reflectorConstructor15.getConstructor();
      if (constructor2 == null) {
         return null;
      }

      try {
         return constructor2.newInstance(objects);
      } catch (Throwable throwable) {
         handleThrowable2(throwable, reflectorConstructor15, objects);
         return null;
      }
   }

   public static boolean checkReflectorConstructor(ReflectorConstructor reflectorConstructor15, Object... objects) {
      Object object = createObject5(reflectorConstructor15, objects);
      return object == null ? false : checkObject2(object);
   }

   public static String createString2(Object object, ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? null : (String)method2.invoke(object, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, object, reflectorMethod108, objects);
         return null;
      }
   }

   public static boolean checkReflectorField(ReflectorField reflectorField75, Object object) {
      return checkObject4(null, reflectorField75, object);
   }

   public static Object createObject3(Object object, ReflectorFields reflectorFields8, int i) {
      ReflectorField reflectorField75 = reflectorFields8.getReflectorField(i);
      return reflectorField75 == null ? null : createObject6(object, reflectorField75);
   }

   public static Object createObject2(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? null : method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
         return null;
      }
   }

   public static float createFloat2(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? 0.0F : (Float)method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
         return 0.0F;
      }
   }

   private static Object[] createArray(Object[] objects) {
      if (objects == null) {
         return new Class[0];
      }

      Class[] clazzs = new Class[objects.length];

      for (int i = 0; i < clazzs.length; i++) {
         Object object = objects[i];
         if (object != null) {
            clazzs[i] = object.getClass();
         }
      }

      return clazzs;
   }

   public static boolean checkReflectorMethod(ReflectorMethod reflectorMethod108, Object... objects) {
      try {
         Method method2 = reflectorMethod108.getMethod();
         return method2 == null ? false : (Boolean)method2.invoke(null, objects);
      } catch (Throwable throwable) {
         handleThrowable(throwable, null, reflectorMethod108, objects);
         return false;
      }
   }

   static class IResolvableImpl implements IResolvable {
      final String val$str;

      @Override
      public void update3() {
         Reflector.logger.info("[OptiFine] " + this.val$str);
      }

      IResolvableImpl(String text) {
         this.val$str = text;
      }
   }
}
