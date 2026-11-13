package wtf.novoline;

import com.google.common.collect.Iterables;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.bus.Bus;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import java.awt.Font;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.opengl.Display;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
import ru.fals3r.aal.API;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.GetRoomsRecentMessagesRequest;
import wtf.novoline.client.IrcConnection;
import wtf.novoline.client.PatchUsersSelfPresenceRequest;
import wtf.novoline.client.ApiRequestHandler.BearerTokenHandler;
import wtf.novoline.client.discord.ClientProperties;
import wtf.novoline.client.discord.GatewayChatMessage;
import wtf.novoline.client.discord.GatewayClient;
import wtf.novoline.client.discord.GatewayClientBuilder;
import wtf.novoline.client.discord.GatewayEventAdapter;
import wtf.novoline.client.discord.MessageEvent;
import wtf.novoline.client.discord.PersistenceProfileLoadedEvent;
import wtf.novoline.client.discord.ReadyEvent;
import wtf.novoline.client.discord.RoomListLoadedEvent;
import wtf.novoline.command.CommandManager;
import wtf.novoline.enums.ClientState;
import wtf.novoline.enums.UserListType;
import wtf.novoline.event.Event;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.BackgroundManager.AbstractTextureChild;
import wtf.novoline.gui.clickgui.ClickGuiScreen;
import wtf.novoline.gui.menu.MenuClickGuiScreen;
import wtf.novoline.lang.AuthException;
import wtf.novoline.module.KeybindHandler;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.hud.HudManager;
import wtf.novoline.platform.ViaMCP;
import wtf.novoline.scripting.Script;
import wtf.novoline.scripting.ScriptInfo;
import wtf.novoline.scripting.ScriptManager;
import wtf.novoline.setting.SettingManager;
import wtf.novoline.util.AuthenticationResponse;
import wtf.novoline.util.RestClientOptions;
import wtf.novoline.util.Room;
import wtf.novoline.utils.ClientPreferences;
import wtf.novoline.utils.GreetingMessage;
import wtf.novoline.utils.MacroManager;
import wtf.novoline.utils.PresenceUpdater;
import wtf.novoline.utils.RelationManager;
import wtf.novoline.utils.TaskScheduler;
import wtf.novoline.utils.account.Account;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.account.BanDetector;
import wtf.novoline.utils.account.LoginHandler;
import wtf.novoline.utils.account.WebViewLauncher;
import wtf.novoline.utils.config.ConfigRegistry;
import wtf.novoline.utils.cosmetic.CapeCosmetic;
import wtf.novoline.utils.hypixel.GameTracker;
import wtf.novoline.utils.hypixel.HypixelApiClient;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.language.Language.RuRuType;
import wtf.novoline.utils.notification.NotificationManager;
import wtf.novoline.utils.proxy.ProxyList;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FramebufferManager;
import java.io.InputStream;
import wtf.novoline.support.SneakyThrow;

public class Novoline {
   public static final String VALUE_3_0 = "3.0";
   private Bus<? super Event> bus;
   private ClientPreferences clientPreferences;
   private static final String GATEWAY_KEY = "GEZDyKg*EeS33&m2vZuTTiByQX#NDYZs";
   private TaskScheduler taskScheduler;
   public static final String BUILD_TAG = "burattino@3.0";
   private ScriptManager scriptManager;
   public static final String VALUE_030122 = "030122";
   private ModuleManager moduleManager;
   public static final String NOVOLINE = "Novoline";
   public static final String RELEASE = "Release";
   private NotificationManager notificationManager;
   private HudManager hudManager;
   private MenuClickGuiScreen menuClickGuiScreen;
   private AltManagerScreen altManagerScreen;
   public static final String SENTRY_DSN = "https://5e40be09bd864a9d8922aee0eb9d85d9@o913306.ingest.sentry.io/6067196";
   private static final Novoline novoline = new Novoline();
   private ProxyList proxyList;
   private MacroManager macroManager;
   public static final TaggedLogger taggedLogger = Logger.tag("[Novoline] ");
   private CommandManager commandManager;
   private RuRuType ruRuType;
   private SettingManager settingManager;
   public static String username = null;
   private ClickGuiScreen clickGuiScreen;
   private HypixelApiClient hypixelApiClient;
   private ConfigRegistry configRegistry;
   public static String userId = null;
   public static boolean initialized = false;
   private IrcConnection ircConnection;
   private RelationManager relationManager;
   private AccountManager accountManager;
   public static final FramebufferManager framebufferManager = new FramebufferManager();
   private ServerData serverData;

   public SettingManager getSettingManager() {
      return this.settingManager;
   }

   public void initEnvironment() {
      Display.setTitle(String.format("%s %s — novoline.wtf", "Novoline", "3.0"));
      Novoline.Directories.createDirectories();
      this.initLanguage();

      try {
         ViaMCP.getViaMCP().start();
      } catch (Exception exception) {
         Logger.error(exception, "Failed to initialize NovolinePortal");
      }

      this.clientPreferences = new ClientPreferences();
   }

   public void initialize(String text2) {
      String[] texts = text2.split("\\|<\\|>\\|");
      username = texts[0];
      userId = texts[1];
      this.bus = new EventBus<>();
      this.taskScheduler = new TaskScheduler();
      this.settingManager = new SettingManager();
      this.commandManager = new CommandManager();
      this.macroManager = new MacroManager();
      this.moduleManager = new ModuleManager();
      this.hudManager = new HudManager();
      this.configRegistry = new ConfigRegistry();
      this.hypixelApiClient = new HypixelApiClient();
      this.accountManager = new AccountManager();
      this.relationManager = new RelationManager();
      this.proxyList = new ProxyList();
      this.scriptManager = new ScriptManager();
      this.notificationManager = new NotificationManager();
      this.loadAllScripts();
      this.restoreSession();
      this.ircConnection = this.connectIrc();
      this.menuClickGuiScreen = new MenuClickGuiScreen();
      this.clickGuiScreen = new ClickGuiScreen();
      this.altManagerScreen = new AltManagerScreen();
      this.configRegistry.getConfigManager().loadGlobal();
      this.configRegistry.getConfigManager().load(true, "proxies", ConfigRegistry.proxiesSection);
      if (this.clientPreferences.getLanguage() != null) {
         this.ruRuType = this.clientPreferences.getLanguage();
      }

      try {
         WebViewLauncher.extract();
      } catch (Throwable throwable2) {
         Logger.error(throwable2, "Couldn't extract WebView");
      }

      this.bus.subscribe(new HypixelState());
      this.bus.subscribe(new GameTracker());
      this.bus.subscribe(new BanDetector(this.accountManager));
      this.bus.subscribe(new GreetingMessage());
      this.bus.subscribe(new KeybindHandler());
      this.bus.subscribe(new PresenceUpdater());
      initialized = true;
   }

   public void sendChatMessage(String text2) {
      Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§8[§bNovoline§8] §7" + text2));
   }

   private void initLanguage() {
      Language.loadAll();
      String text2 = Locale.getDefault().toLanguageTag();
      this.ruRuType = RuRuType.createRuRuType(text2);
   }

   public HudManager getHudManager() {
      return this.hudManager;
   }

   public void saveAll() {
      try {
         this.configRegistry.getConfigManager().saveGlobal();
         this.configRegistry.getConfigManager().save(true, "proxies", ConfigRegistry.proxiesSection);
         this.accountManager.save();
         this.relationManager.save();
         ModuleManager.killsults.saveMessages();
         ModuleManager.spammer.saveMessages();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save data");
      }
   }

   public ServerData getServerData() {
      return this.serverData;
   }

   public RuRuType getRuRuType() {
      return this.ruRuType;
   }

   public RelationManager getRelationManager() {
      return this.relationManager;
   }

   public HypixelApiClient getHypixelApiClient() {
      return this.hypixelApiClient;
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public void setServerData(ServerData serverData2) {
      this.serverData = serverData2;
   }

   public static Novoline getNovoline() {
      return novoline;
   }

   public ConfigRegistry getConfigRegistry() {
      return this.configRegistry;
   }

   private void loadAllScripts() {
      try {
         Files.list(Novoline.Directories.SCRIPTS_DIR).filter(path -> Files.isRegularFile(path)).forEach(path -> {
            try {
               ScriptInfo metadata = ((Script)CompletableFuture.<Script>supplyAsync(() -> {
                  try {
                     Script script2 = this.scriptManager.getScript(path);
                     this.scriptManager.loadScript(this.scriptManager.getScript(path));
                     return script2;
                  } catch (Throwable throwable2) {
                     throw new RuntimeException(throwable2);
                  }
               }).get(15L, TimeUnit.SECONDS)).getMetadata();
               Logger.info("Loaded script {} v{} by {}", metadata.getName(), metadata.getVersion(), metadata.getAuthor());
            } catch (Throwable throwable3) {
               Logger.error(throwable3, "Couldn't load script {}", path.getFileName().toString());
            }
         });
      } catch (IOException ioException) {
         Logger.error(ioException, "Couldn't list script files");
      }

      Logger.info("Powered by NovoScript v2.0.0");
   }

   public ClientPreferences getClientPreferences() {
      return this.clientPreferences;
   }

   public MenuClickGuiScreen getMenuClickGuiScreen() {
      return this.menuClickGuiScreen;
   }

   public ProxyList getProxyList() {
      return this.proxyList;
   }

   public AltManagerScreen getAltManagerScreen() {
      return this.altManagerScreen;
   }

   public void setClientPreferences(ClientPreferences clientPreferences2) {
      this.clientPreferences = clientPreferences2;
   }

   public MacroManager getMacroManager() {
      return this.macroManager;
   }

   public Bus<? super Event> getBus() {
      return this.bus;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public TaskScheduler getTaskScheduler() {
      return this.taskScheduler;
   }

   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public void setRuRuType(RuRuType ruRuType2) {
      this.ruRuType = ruRuType2;
   }

   private IrcConnection connectIrc() {
      RestClientOptions restClientOptions = new RestClientOptions().baseUri("https://nos.novoline.wtf/api/v1");
      ApiClient apiClient = new ApiClient(restClientOptions);

      AuthenticationResponse authenticationResponse;
      try {
         authenticationResponse = (AuthenticationResponse)apiClient.authenticate(System.getProperty("novo.delsyToken")).get();
      } catch (Exception exception) {
         Logger.error(exception, "Unable to authenticate for NOS");
         new RuntimeException(exception);
         return null;
      }

      String jwtToken = authenticationResponse.getJwtToken();
      apiClient.setApiRequestHandler(new BearerTokenHandler(jwtToken));
      GatewayEventAdapter gatewayEventAdapter = new GatewayEventAdapter() {
         private final Set<String> set = ConcurrentHashMap.newKeySet();
         private final Queue<Novoline.PendingTexture> queue = new ConcurrentLinkedQueue<>();
         private final ExecutorService executorService = Executors.newFixedThreadPool(8);
         @EventLink
         private final Listener<RunTickEvent> onRunTick = runTickEvent -> {
            if (!this.queue.isEmpty()) {
               while (!this.queue.isEmpty()) {
                  Novoline.PendingTexture pendingTexture = this.queue.poll();
                  if (!Minecraft.getMinecraft().getTextureManager().loadTexture(pendingTexture.getResourceLocation(), pendingTexture.getAbstractTexture())) {
                     this.set.remove(pendingTexture.getText());
                  } else {
                     Novoline.taggedLogger.debug("Loaded texture {}", new Object[]{pendingTexture.getText()});
                  }
               }
            }
         };

         @Override
         public void onPersistenceProfileLoaded(PersistenceProfileLoadedEvent persistenceProfileLoadedEvent) {
            persistenceProfileLoadedEvent.getCollection()
               .stream()
               .flatMap(profile -> profile.getCosmetics().stream())
               .filter(cosmetic2 -> cosmetic2 instanceof CapeCosmetic)
               .map(CapeCosmetic.class::cast)
               .map(CapeCosmetic::getTexHash)
               .distinct()
               .filter(string -> !this.set.contains(string))
               .forEach(string -> this.executorService.submit(() -> this.loadTexture(string)));
         }

         @Override
         public void onRoomListLoaded(RoomListLoadedEvent roomListLoadedEvent) {
            roomListLoadedEvent.getCollection()
               .forEach(
                  room -> Novoline.this.ircConnection
                     .getApiClient()
                     .getRoomRecentMessages(GetRoomsRecentMessagesRequest.createGetRoomsRecentMessagesRequest(room.getId()))
                     .whenComplete(
                        (collection, throwable) -> {
                           if (throwable != null) {
                              Logger.error(throwable, "Unable to load recent messages in Room {}", room.getId());
                           } else {
                              Map map2 = collection.stream()
                                 .sorted(Comparator.comparingLong(gatewayChatMessage -> gatewayChatMessage.getId().getTimestamp()))
                                 .collect(
                                    Collectors.toMap(GatewayChatMessage::getId, Function.identity(), (gatewayChatMessage, gatewayChatMessage2) -> gatewayChatMessage, LinkedHashMap::new)
                                 );
                              if (room.getMessages() == null) {
                                 room.setMessages(new LinkedHashMap<>());
                              }

                              room.getMessages().putAll(map2);
                           }
                        }
                     )
               );
         }

         @Override
         public void onMessage(MessageEvent messageEvent) {
            GatewayChatMessage gatewayChatMessage3 = messageEvent.getGatewayChatMessage();
            if (Minecraft.getMinecraft().thePlayer != null
               && GuiNewChat.selectedRoom > 0
               && ((Room)Iterables.get(Novoline.this.ircConnection.getGatewayClient().getRooms().values(), GuiNewChat.selectedRoom - 1))
                  .getId()
                  .equals(messageEvent.getRoom().getId())) {
               GuiNewChat.roomLines.add(GuiChat.createAnimatedLine(gatewayChatMessage3));
            }
         }

         @Override
         public void onReady(ReadyEvent readyEvent) {
            Novoline.this.ircConnection.getApiClient().updatePresence(PatchUsersSelfPresenceRequest.builder().presenceType(ClientState.IN_MENU).build());
         }

         private void loadTexture(String text2) {
            Novoline.taggedLogger.trace("Loading texture {}", new Object[]{text2});
            this.set.add(text2);
            Novoline.this.ircConnection.getApiClient().getCosmeticTexture(text2).whenComplete((inputStream, throwable) -> {
               if (throwable != null) {
                  Novoline.taggedLogger.error((Throwable)throwable, "Exception occurred while requesting texture {}", text2);
                  this.set.remove(text2);
               } else {
                  try {
                     byte[] allBytes = ((InputStream) inputStream).readAllBytes();
                     ResourceLocation resourceLocation3 = new ResourceLocation("nos", "texture/" + text2);
                     AbstractTextureChild abstractTextureChild = new AbstractTextureChild(allBytes, resourceLocation3);
                     this.queue.add(new Novoline.PendingTexture(resourceLocation3, abstractTextureChild, text2));
                  } catch (Exception exception2) {
                     Novoline.taggedLogger.error(exception2, "Failed to read texture {}", text2);
                     this.set.remove(text2);
                  }
               }
            });
         }
      };
      this.bus.subscribe(gatewayEventAdapter);
      GatewayClient gatewayClient = new GatewayClientBuilder(URI.create("wss://nos.novoline.wtf/gateway"))
         .bearerToken(jwtToken)
         .clientProperties(ClientProperties.builder().clientVersion("3.0").clientChannel("Release").build())
         .listener(gatewayEventAdapter)
         .userLists(UserListType.ROOM_EVENTS, UserListType.PRESENCE_EVENTS, UserListType.USER_LIST)
         .salt("GEZDyKg*EeS33&m2vZuTTiByQX#NDYZs")
         .getGatewayClient();
      return new IrcConnection(gatewayClient, apiClient);
   }

   public NotificationManager getNotificationManager() {
      return this.notificationManager;
   }

   public void reportException(Throwable throwable2) {
      User user2 = new User();
      user2.setId(userId);
      user2.setUsername(username);
      Sentry.setTag("cpu", OpenGlHelper.getCpu());
      Sentry.setTag("client.version", "3.0");
      Sentry.setTag("client.build", "030122");
      Sentry.setUser(user2);
      Sentry.captureException(throwable2);
   }

   public ClickGuiScreen getClickGuiScreen() {
      return this.clickGuiScreen;
   }

   private void restoreSession() {
      Account account2 = this.accountManager.getAccount();
      if (account2 != null && account2.getAccountId() != null) {
         try {
            LoginHandler.onLoggedIn(account2.login());

            for (int i = 0; i < this.accountManager.size(); i++) {
               Account account3 = this.accountManager.get(i);
               if (account3.getAccountId() != null && account2.getAccountId().equals(account3.getAccountId())) {
                  this.accountManager.setIntValue(i);
                  break;
               }
            }
         } catch (AuthException authException) {
            LoginHandler.reportLoginFailure(authException);
         }
      }
   }

   public AccountManager getAccountManager() {
      return this.accountManager;
   }

   public IrcConnection getIrcConnection() {
      return this.ircConnection;
   }

   public static class Directories {
      public static final Path ROOT_DIR = Paths.get(System.getProperty("user.dir"), "Novoline");
      public static final Path STORAGE_DIR = ROOT_DIR.resolve("storage");
      public static final Path CONFIGS_DIR = ROOT_DIR.resolve("configs");
      public static final Path SCRIPTS_DIR = ROOT_DIR.resolve("scripts");

      public static void createDirectories() {
      try {
         for (Field field : Novoline.Directories.class.getFields()) {
            if (field.getType() == Path.class) {
               Files.createDirectories((Path)field.get(null));
            }
         }
      } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }
   }

   public static class Fonts {
      public static final ResourceLocation MUSEO_SANS_900 = new ResourceLocation("novoline", "fonts/museosanscyrl900.ttf");
      public static final ResourceLocation MUSEO_SANS_700 = new ResourceLocation("novoline", "fonts/museosanscyrl700.ttf");
      public static final ResourceLocation MUSEO_SANS_500 = new ResourceLocation("novoline", "fonts/museosanscyrl500.ttf");
      public static CFontRenderer montserratRegularSize18;
      public static CFontRenderer rubikRegularSize18;
      public static CFontRenderer museoSans500Size14;
      public static CFontRenderer pixelSize8;
      public static final ResourceLocation MONTSERRAT_REGULAR = new ResourceLocation("novoline", "fonts/montserrat-regular.ttf");
      public static CFontRenderer museoSans900Size32;
      public static final ResourceLocation RUBIK_SEMIBOLD = new ResourceLocation("novoline", "fonts/rubik-semibold.ttf");
      public static CFontRenderer rubikSemiboldSize18;
      public static final ResourceLocation RUBIK_REGULAR = new ResourceLocation("novoline", "fonts/rubik-regular.ttf");
      public static CFontRenderer rubikSemiboldSize24;
      public static final ResourceLocation ROBOTO_REGULAR = new ResourceLocation("novoline", "fonts/roboto-regular.ttf");
      public static CFontRenderer museoSans900Size36;
      public static final ResourceLocation PIXEL = new ResourceLocation("novoline", "fonts/pixel.ttf");
      public static CFontRenderer rubikRegularSize14;
      public static CFontRenderer museoSans500Size11;
      public static CFontRenderer rubikSemiboldSize16;
      public static CFontRenderer museoSans900Size12;
      public static CFontRenderer museoSans900Size16;
      public static CFontRenderer museoSans700Size14;
      public static CFontRenderer museoSans500Size14Tab;
      public static CFontRenderer rubikRegularSize16;
      private static final Map<ResourceLocation, Font> fontCache = new WeakHashMap<>();
      public static CFontRenderer robotoRegularSize14;

      private static Font loadTTF(ResourceLocation resourceLocation3) {
         return fontCache.computeIfAbsent(resourceLocation3, resourceLocation2 -> {
            try {
               IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation2);
               return Font.createFont(0, resource.getInputStream());
            } catch (Exception exception) {
               throw new RuntimeException("Couldn't create font @ " + resourceLocation3, exception);
            }
         });
      }

      public static void loadFonts() {
         museoSans900Size36 = createCFontRenderer(MUSEO_SANS_900, 36.0F);
         museoSans900Size32 = createCFontRenderer(MUSEO_SANS_900, 32.0F);
         museoSans900Size16 = createCFontRenderer(MUSEO_SANS_900, 16.0F);
         museoSans900Size12 = createCFontRenderer(MUSEO_SANS_900, 12.0F);
         museoSans700Size14 = createCFontRenderer(MUSEO_SANS_700, 14.0F);
         museoSans500Size14Tab = createCFontRenderer(MUSEO_SANS_500, 14.0F);
         museoSans500Size14 = createCFontRenderer(MUSEO_SANS_500, 14.0F);
         museoSans500Size11 = createCFontRenderer(MUSEO_SANS_500, 11.0F);
         montserratRegularSize18 = createCFontRenderer(MONTSERRAT_REGULAR, 18.0F);
         rubikSemiboldSize18 = createCFontRenderer(RUBIK_SEMIBOLD, 18.0F);
         rubikSemiboldSize24 = createCFontRenderer(RUBIK_SEMIBOLD, 24.0F);
         rubikSemiboldSize16 = createCFontRenderer(RUBIK_SEMIBOLD, 16.0F);
         rubikRegularSize18 = createCFontRenderer(RUBIK_REGULAR, 18.0F);
         rubikRegularSize16 = createCFontRenderer(RUBIK_REGULAR, 16.0F);
         rubikRegularSize14 = createCFontRenderer(RUBIK_REGULAR, 14.0F);
         robotoRegularSize14 = createCFontRenderer(ROBOTO_REGULAR, 14.0F);
         pixelSize8 = createCFontRenderer(PIXEL, 8.0F);
      }

      public static CFontRenderer createCFontRenderer(ResourceLocation resourceLocation3, float f) {
         Font font2 = loadTTF(resourceLocation3).deriveFont(f);
         return new CFontRenderer(font2);
      }
   }

   record PendingTexture(ResourceLocation resourceLocation, AbstractTexture abstractTexture, String text) {
      public ResourceLocation getResourceLocation() {
         return this.resourceLocation;
      }

      public AbstractTexture getAbstractTexture() {
         return this.abstractTexture;
      }

      public String getText() {
         return this.text;
      }
   }
}
