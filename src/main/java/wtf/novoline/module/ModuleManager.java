package wtf.novoline.module;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.combat.AimBot;
import wtf.novoline.module.combat.Antibot;
import wtf.novoline.module.combat.AutoClicker;
import wtf.novoline.module.combat.AutoHead;
import wtf.novoline.module.combat.AutoPotion;
import wtf.novoline.module.combat.BowAimbot;
import wtf.novoline.module.combat.Criticals;
import wtf.novoline.module.combat.FastBow;
import wtf.novoline.module.combat.KeepSprint;
import wtf.novoline.module.combat.Killaura;
import wtf.novoline.module.combat.Reach;
import wtf.novoline.module.combat.TargetStrafe;
import wtf.novoline.module.combat.WTap;
import wtf.novoline.module.hud.ArrayList;
import wtf.novoline.module.hud.ClickGui;
import wtf.novoline.module.hud.HUD;
import wtf.novoline.module.hud.Notifications;
import wtf.novoline.module.hud.TargetESP;
import wtf.novoline.module.hud.TargetHUD;
import wtf.novoline.module.hud.Watermark;
import wtf.novoline.module.misc.AutoHypixel;
import wtf.novoline.module.misc.AutoRegister;
import wtf.novoline.module.misc.BetterMinecraft;
import wtf.novoline.module.misc.ChatBypass;
import wtf.novoline.module.misc.Derp;
import wtf.novoline.module.misc.Disabler;
import wtf.novoline.module.misc.DiscordRPC;
import wtf.novoline.module.misc.HypixelStats;
import wtf.novoline.module.misc.Killsults;
import wtf.novoline.module.misc.LagDetector;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.module.misc.NoCloseGui;
import wtf.novoline.module.misc.PingSpoof;
import wtf.novoline.module.misc.Spammer;
import wtf.novoline.module.movement.Fly;
import wtf.novoline.module.movement.Jesus;
import wtf.novoline.module.movement.LongJump;
import wtf.novoline.module.movement.Phase;
import wtf.novoline.module.movement.Speed;
import wtf.novoline.module.movement.Sprint;
import wtf.novoline.module.movement.Step;
import wtf.novoline.module.player.AntiVoid;
import wtf.novoline.module.player.AutoArmor;
import wtf.novoline.module.player.AutoTool;
import wtf.novoline.module.player.Blink;
import wtf.novoline.module.player.FastEat;
import wtf.novoline.module.player.FreeCam;
import wtf.novoline.module.player.InventoryManager;
import wtf.novoline.module.player.InventoryMove;
import wtf.novoline.module.player.NoFall;
import wtf.novoline.module.player.NoRotate;
import wtf.novoline.module.player.NoSlowDown;
import wtf.novoline.module.player.Timer;
import wtf.novoline.module.player.Velocity;
import wtf.novoline.module.visual.Animations;
import wtf.novoline.module.visual.CameraClip;
import wtf.novoline.module.visual.Chams;
import wtf.novoline.module.visual.ChinaHat;
import wtf.novoline.module.visual.Crosshair;
import wtf.novoline.module.visual.CustomModel;
import wtf.novoline.module.visual.DamageParticles;
import wtf.novoline.module.visual.FreeLook;
import wtf.novoline.module.visual.ItemESP;
import wtf.novoline.module.visual.ItemPhysics;
import wtf.novoline.module.visual.JumpCircle;
import wtf.novoline.module.visual.MobESP;
import wtf.novoline.module.visual.NameTags;
import wtf.novoline.module.visual.NightVision;
import wtf.novoline.module.visual.NoHurtCam;
import wtf.novoline.module.visual.NoRender;
import wtf.novoline.module.visual.PlayerESP;
import wtf.novoline.module.visual.Radar;
import wtf.novoline.module.visual.SeeBarriers;
import wtf.novoline.module.visual.SeeInvisibles;
import wtf.novoline.module.visual.SkinChanger;
import wtf.novoline.module.visual.StorageESP;
import wtf.novoline.module.visual.Trails;
import wtf.novoline.module.visual.Weather;
import wtf.novoline.module.visual.WorldColor;
import wtf.novoline.module.visual.WorldTime;
import wtf.novoline.module.world.AntiCactus;
import wtf.novoline.module.world.Breaker;
import wtf.novoline.module.world.BridgeAssist;
import wtf.novoline.module.world.ChestStealer;
import wtf.novoline.module.world.FastPlace;
import wtf.novoline.module.world.LightningTracker;
import wtf.novoline.module.world.SafeWalk;
import wtf.novoline.module.world.Scaffold;
import wtf.novoline.module.world.SpeedMine;
import wtf.novoline.module.world.XRay;
import wtf.novoline.utils.render.CFontRenderer;

public class ModuleManager {
   public static List<Module> modules = new LinkedList<>();
   public static List<Module> modulesByWidth = new CopyOnWriteArrayList<>();
   public static List<Module> modulesByWidthReversed = new CopyOnWriteArrayList<>();
   public static Map<Class<? extends Module>, Module> modulesByClass = new HashMap<>();
   public static Map<String, Module> modulesByName = new HashMap<>();
   public static final Killaura killaura = new Killaura();
   public static final Antibot antibot = new Antibot();
   public static final Criticals criticals = new Criticals();
   public static final AutoHead autoHead = new AutoHead();
   public static final TargetStrafe targetStrafe = new TargetStrafe();
   public static final AutoPotion autoPotion = new AutoPotion();
   public static final WTap wTap = new WTap();
   public static final AutoClicker autoClicker = new AutoClicker();
   public static final AimBot aimBot = new AimBot();
   public static final BowAimbot bowAimbot = new BowAimbot();
   public static final Reach reach = new Reach();
   public static final FastBow fastBow = new FastBow();
   public static final KeepSprint keepSprint = new KeepSprint();
   public static final ArrayList arrayList = new ArrayList();
   public static final Watermark watermark = new Watermark();
   public static final ClickGui clickGui = new ClickGui();
   public static final Notifications notifications = new Notifications();
   public static final TargetHUD targetHUD = new TargetHUD();
   public static final TargetESP targetESP = new TargetESP();
   public static final HUD hUD = new HUD();
   public static final BetterMinecraft betterMinecraft = new BetterMinecraft();
   public static final Disabler disabler = new Disabler();
   public static final HypixelStats hypixelStats = new HypixelStats();
   public static final AutoHypixel autoHypixel = new AutoHypixel();
   public static final LagDetector lagDetector = new LagDetector();
   public static final DiscordRPC discordRPC = new DiscordRPC();
   public static final Killsults killsults = new Killsults();
   public static final PingSpoof pingSpoof = new PingSpoof();
   public static final ChatBypass chatBypass = new ChatBypass();
   public static final Spammer spammer = new Spammer();
   public static final NameProtect nameProtect = new NameProtect();
   public static final AutoRegister autoRegister = new AutoRegister();
   public static final Derp derp = new Derp();
   public static final NoCloseGui noCloseGui = new NoCloseGui();
   public static final Sprint sprint = new Sprint();
   public static final Speed speed = new Speed();
   public static final Fly fly = new Fly();
   public static final LongJump longJump = new LongJump();
   public static final Phase phase = new Phase();
   public static final Step step = new Step();
   public static final Jesus jesus = new Jesus();
   public static final InventoryMove inventoryMove = new InventoryMove();
   public static final Timer timer = new Timer();
   public static final NoFall noFall = new NoFall();
   public static final Velocity velocity = new Velocity();
   public static final AntiVoid antiVoid = new AntiVoid();
   public static final NoRotate noRotate = new NoRotate();
   public static final NoSlowDown noSlowDown = new NoSlowDown();
   public static final AutoTool autoTool = new AutoTool();
   public static final InventoryManager inventoryManager = new InventoryManager();
   public static final AutoArmor autoArmor = new AutoArmor();
   public static final Blink blink = new Blink();
   public static final FreeCam freeCam = new FreeCam();
   public static final FastEat fastEat = new FastEat();
   public static final Crosshair crosshair = new Crosshair();
   public static final CameraClip cameraClip = new CameraClip();
   public static final NoRender noRender = new NoRender();
   public static final JumpCircle jumpCircle = new JumpCircle();
   public static final Trails trails = new Trails();
   public static final CustomModel customModel = new CustomModel();
   public static final NightVision nightVision = new NightVision();
   public static final NoHurtCam noHurtCam = new NoHurtCam();
   public static final SeeInvisibles seeInvisibles = new SeeInvisibles();
   public static final PlayerESP playerESP = new PlayerESP();
   public static final ChinaHat chinaHat = new ChinaHat();
   public static final SeeBarriers seeBarriers = new SeeBarriers();
   public static final SkinChanger skinChanger = new SkinChanger();
   public static final DamageParticles damageParticles = new DamageParticles();
   public static final WorldTime worldTime = new WorldTime();
   public static final Weather weather = new Weather();
   public static final Animations animations = new Animations();
   public static final StorageESP storageESP = new StorageESP();
   public static final Chams chams = new Chams();
   public static final ItemPhysics itemPhysics = new ItemPhysics();
   public static final Radar radar = new Radar();
   public static final NameTags nameTags = new NameTags();
   public static final FreeLook freeLook = new FreeLook();
   public static final MobESP mobESP = new MobESP();
   public static final ItemESP itemESP = new ItemESP();
   public static final WorldColor worldColor = new WorldColor();
   public static final ChestStealer chestStealer = new ChestStealer();
   public static final Scaffold scaffold = new Scaffold();
   public static final Breaker breaker = new Breaker();
   public static final AntiCactus antiCactus = new AntiCactus();
   public static final FastPlace fastPlace = new FastPlace();
   public static final SpeedMine speedMine = new SpeedMine();
   public static final XRay xRay = new XRay();
   public static final LightningTracker lightningTracker = new LightningTracker();
   public static final SafeWalk safeWalk = new SafeWalk();
   public static final BridgeAssist bridgeAssist = new BridgeAssist();

   public void updateCachedFeatures() {
      boolean selected = arrayList.arrayList.isSelected(0);
      CFontRenderer cFontRenderer = Fonts.rubikRegularSize16;
      modulesByWidth.sort(
         (module2, module3) -> Integer.compare(
            cFontRenderer.getStringWidth(module3.getName() + (selected ? " " : module3.getSuffix())),
            cFontRenderer.getStringWidth(module2.getName() + (selected ? " " : module2.getSuffix()))
         )
      );
      modulesByWidthReversed = Lists.reverse(modulesByWidth);
   }

   public ModuleManager() {
      for (Field field : ModuleManager.class.getDeclaredFields()) {
         if (this.extendsModule(field.getType())) {
            try {
               this.register((Module)field.get(null));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }

      this.updateCachedFeatures();
      modules.forEach(Module::onInitialized);
   }

   public Module register(Module module4) {
      modules.add(module4);
      modulesByWidth.add(module4);
      modulesByClass.put((Class<? extends Module>)module4.getClass(), module4);
      modulesByName.put(module4.getName().toLowerCase(Locale.ROOT), module4);
      return module4;
   }

   private boolean extendsModule(Class clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.equals(Module.class)) {
         return true;
      } else {
         Class superclass = clazz.getSuperclass();
         if (superclass == null) {
            return false;
         } else {
            return superclass.equals(Module.class) ? true : this.extendsModule(superclass);
         }
      }
   }

   public void unregister(Module module4) {
      if (modules.remove(module4)) {
         if (!(modulesByWidth.remove(module4) & modulesByClass.remove(module4.getClass(), module4) & modulesByName.remove(module4.getName().toLowerCase(Locale.ROOT), module4))
            )
          {
            throw new IllegalStateException("Corrupted feature registry");
         }
      }
   }
}
