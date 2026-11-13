package net.minecraft.client.stream;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.BroadcastController.Contract;
import net.minecraft.client.stream.ChatController.CreatedType;
import net.minecraft.client.stream.ChatController.UninitializedType;
import net.minecraft.client.stream.IStream.ErrorType;
import net.minecraft.client.stream.IngestServerTester.UninitalizedType;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.lwjgl.opengl.GL11;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.VideoParams;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class TwitchStream
   implements Contract,
   net.minecraft.client.stream.ChatController.Contract,
   net.minecraft.client.stream.IngestServerTester.Contract,
   IStream {
   private String text;
   private boolean flag;
   private final IChatComponent iChatComponent = new ChatComponentText("Twitch");
   private boolean flag2;
   private final Map<String, ChatUserInfo> map = Maps.newHashMap();
   private int intValue = 30;
   private static final Logger logger = LogManager.getLogger();
   private long longValue = 0L;
   public static final Marker marker = MarkerManager.getMarker("STREAM");
   private boolean flag3 = false;
   private boolean flag4;
   private ErrorType errorType = ErrorType.ERROR;
   private final Minecraft minecraft;
   private static boolean flag5;
   private final BroadcastController broadcastController;
   private boolean flag6;
   private final ChatController chatController;
   private Framebuffer framebuffer;

   @Override
   public boolean isActive7() {
      return this.broadcastController.isActive8();
   }

   public TwitchStream(Minecraft minecraft2, final Property property) {
      this.minecraft = minecraft2;
      this.broadcastController = new BroadcastController();
      this.chatController = new ChatController();
      this.broadcastController.setContract(this);
      this.chatController.setContract(this);
      this.broadcastController.setText2("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.chatController.setText2("nmt37qblda36pvonovdkbopzfzw3wlq");
      this.iChatComponent.getChatStyle().setColor(EnumChatFormatting.DARK_PURPLE);
      if (property != null && !Strings.isNullOrEmpty(property.getValue()) && OpenGlHelper.framebufferSupported) {
         Thread thread = new Thread("Twitch authenticator") {
            @Override
            public void run() {
               try {
                  URL url = new URL("https://api.twitch.tv/kraken?oauth_token=" + URLEncoder.encode(property.getValue(), "UTF-8"));
                  String text2 = HttpUtil.get(url);
                  JsonObject jsonObject = JsonUtils.getJsonObject(JsonUtils.getJsonObject(new JsonParser().parse(text2), "Response"), "token");
                  if (JsonUtils.getBoolean(jsonObject, "valid")) {
                     String string = JsonUtils.getString(jsonObject, "user_name");
                     TwitchStream.logger.debug(TwitchStream.marker, "Authenticated with twitch; username is {}", new Object[]{string});
                     AuthToken authToken = new AuthToken();
                     authToken.data = property.getValue();
                     TwitchStream.this.broadcastController.checkString2(string, authToken);
                     TwitchStream.this.chatController.setText(string);
                     TwitchStream.this.chatController.setAuthToken(authToken);
                     Runtime.getRuntime().addShutdownHook(new Thread("Twitch shutdown hook") {
                        @Override
                        public void run() {
                           TwitchStream.this.update12();
                        }
                     });
                     TwitchStream.this.broadcastController.isActive();
                     TwitchStream.this.chatController.isActive2();
                  } else {
                     TwitchStream.this.errorType = ErrorType.INVALID_TOKEN;
                     TwitchStream.logger.error(TwitchStream.marker, "Given twitch access token is invalid");
                  }
               } catch (IOException ioException) {
                  TwitchStream.this.errorType = ErrorType.ERROR;
                  TwitchStream.logger.error(TwitchStream.marker, "Could not authenticate with twitch", ioException);
               }
            }
         };
         thread.setDaemon(true);
         thread.start();
      }
   }

   @Override
   public ErrorCode getErrorCode() {
      return !flag5 ? ErrorCode.TTV_EC_OS_TOO_OLD : this.broadcastController.getErrorCode2();
   }

   @Override
   public boolean isActive10() {
      return this.text != null && this.text.equals(this.broadcastController.getChannelInfo().name);
   }

   @Override
   public void handleString4(String text2, String text3) {
   }

   @Override
   public boolean isActive6() {
      return flag5 && this.broadcastController.isFlag2();
   }

   public static int createInt2(float f) {
      return MathHelper.floor_float(230.0F + f * 3270.0F);
   }

   @Override
   public void update7() {
      logger.info(marker, "Logged out of twitch");
   }

   @Override
   public void handleString6(String text2) {
   }

   @Override
   public void update3() {
      if (this.isActive()) {
         float streamSystemVolume2 = this.minecraft.gameSettings.streamSystemVolume;
         boolean flag7 = this.flag4 || streamSystemVolume2 <= 0.0F;
         this.broadcastController.handleFloat2(flag7 ? 0.0F : streamSystemVolume2);
         this.broadcastController.handleFloat(this.isActive2() ? 0.0F : this.minecraft.gameSettings.streamMicVolume);
      }
   }

   @Override
   public void handleErrorCode4(ErrorCode errorCode, GameInfo[] gameInfos) {
   }

   @Override
   public void handleErrorCode5(ErrorCode errorCode) {
      if (ErrorCode.failed(errorCode)) {
         logger.error(marker, "Chat failed to initialize");
      }
   }

   @Override
   public void update11() {
      if (this.broadcastController.isActive6() && !this.broadcastController.isActive12()) {
         long l = System.nanoTime();
         long l2 = 1000000000 / this.intValue;
         long l3 = l - this.longValue;
         if (l3 >= l2) {
            FrameBuffer frameBuffer = this.broadcastController.getFrameBuffer();
            Framebuffer framebuffer2 = this.minecraft.getFramebuffer();
            this.framebuffer.bindFramebuffer(true);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.viewport(0, 0, this.framebuffer.framebufferWidth, this.framebuffer.framebufferHeight);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            float framebufferWidth2 = this.framebuffer.framebufferWidth;
            float framebufferHeight2 = this.framebuffer.framebufferHeight;
            float f = (float)framebuffer2.framebufferWidth / framebuffer2.framebufferTextureWidth;
            float f2 = (float)framebuffer2.framebufferHeight / framebuffer2.framebufferTextureHeight;
            framebuffer2.bindFramebufferTexture();
            GL11.glTexParameterf(3553, 10241, 9729.0F);
            GL11.glTexParameterf(3553, 10240, 9729.0F);
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(0.0, framebufferHeight2, 0.0).tex(0.0, f2).endVertex();
            worldRenderer.pos(framebufferWidth2, framebufferHeight2, 0.0).tex(f, f2).endVertex();
            worldRenderer.pos(framebufferWidth2, 0.0, 0.0).tex(f, 0.0).endVertex();
            worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex();
            instance.draw();
            framebuffer2.unbindFramebufferTexture();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5889);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            this.broadcastController.handleFrameBuffer(frameBuffer);
            this.framebuffer.unbindFramebuffer();
            this.broadcastController.getErrorCode(frameBuffer);
            this.longValue = l;
         }
      }
   }

   @Override
   public boolean isActive() {
      return this.broadcastController.isActive6();
   }

   @Override
   public void handleString5(String text2) {
      logger.debug(marker, "Chat connected");
   }

   @Override
   public void update10() {
      this.update3();
      logger.info(marker, "Broadcast to Twitch has started");
   }

   @Override
   public void update8() {
      if (this.broadcastController.isActive5()) {
         logger.info(marker, "Stopped streaming to Twitch");
      } else {
         logger.warn(marker, "Could not stop streaming to Twitch");
      }
   }

   @Override
   public void update6() {
      if (this.broadcastController.isActive4()) {
         logger.debug(marker, "Requested commercial from Twitch");
      } else {
         logger.warn(marker, "Could not request commercial from Twitch");
      }
   }

   static {
      try {
         if (Util.getOSType() == EnumOS.WINDOWS) {
            System.loadLibrary("avutil-ttv-51");
            System.loadLibrary("swresample-ttv-0");
            System.loadLibrary("libmp3lame-ttv");
            if (System.getProperty("os.arch").contains("64")) {
               System.loadLibrary("libmfxsw64");
            } else {
               System.loadLibrary("libmfxsw32");
            }
         }

         flag5 = true;
      } catch (Throwable throwable) {
         flag5 = false;
      }
   }

   public static int createInt(float f) {
      return MathHelper.floor_float(10.0F + f * 50.0F);
   }

   @Override
   public void handleErrorCode8(ErrorCode errorCode, AuthToken authToken) {
   }

   @Override
   public ChatUserInfo getChatUserInfo(String text2) {
      return this.map.get(text2);
   }

   @Override
   public void handleString2(String text2) {
      this.chatController.checkString5(this.text, text2);
   }

   @Override
   public void handleString7(String text2, ChatRawMessage[] chatRawMessages) {
      for (ChatRawMessage chatRawMessage : chatRawMessages) {
         this.handleString8(chatRawMessage.userName, chatRawMessage);
         if (this.checkSet(chatRawMessage.modes, chatRawMessage.subscriptions, this.minecraft.gameSettings.streamChatUserFilter)) {
            ChatComponentText chatComponentText = new ChatComponentText(chatRawMessage.userName);
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
               "chat.stream." + (chatRawMessage.action ? "emote" : "text"), this.iChatComponent, chatComponentText, EnumChatFormatting.getTextWithoutFormattingCodes(chatRawMessage.message)
            );
            if (chatRawMessage.action) {
               chatComponentTranslation.getChatStyle().setItalic(true);
            }

            ChatComponentText chatComponentText2 = new ChatComponentText("");
            chatComponentText2.appendSibling(new ChatComponentTranslation("stream.userinfo.chatTooltip"));

            for (IChatComponent iChatComponent2 : (Iterable<IChatComponent>) GuiTwitchUserMode.createList(chatRawMessage.modes, chatRawMessage.subscriptions, (IStream)null)) {
               chatComponentText2.appendText("\n");
               chatComponentText2.appendSibling(iChatComponent2);
            }

            chatComponentText.getChatStyle().setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, chatComponentText2));
            chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.TWITCH_USER_INFO, chatRawMessage.userName));
            this.minecraft.ingameGUI.getChatGUI().printChatMessage(chatComponentTranslation);
         }
      }
   }

   @Override
   public void handleString(String text2) {
      logger.debug(marker, "Chat disconnected");
      this.map.clear();
   }

   @Override
   public void update13() {
      IngestServerTester ingestServerTester = this.broadcastController.getIngestServerTester2();
      if (ingestServerTester != null) {
         ingestServerTester.setContract(this);
      }
   }

   @Override
   public String getString() {
      return this.text;
   }

   @Override
   public void update4() {
      logger.info(marker, "Broadcast to Twitch has stopped");
   }

   @Override
   public void handleMetadata2(Metadata metadata2, long l) {
      if (this.isActive() && this.flag6) {
         long longValue = this.broadcastController.getLong2();
         String string = metadata2.getString();
         String string2 = metadata2.getString2();
         long longValue2 = this.broadcastController.getLong(metadata2.getText(), longValue + l, string, string2);
         if (longValue2 < 0L) {
            logger.warn(marker, "Could not send stream metadata sequence from {} to {}: {}", longValue + l, longValue + 0L, metadata2);
         } else if (this.broadcastController.checkString3(metadata2.getText(), longValue + 0L, longValue2, string, string2)) {
            logger.debug(marker, "Sent stream metadata sequence from {} to {}: {}", longValue + l, longValue + 0L, metadata2);
         } else {
            logger.warn(marker, "Half-sent stream metadata sequence from {} to {}: {}", longValue + l, longValue + 0L, metadata2);
         }
      }
   }

   protected void update2() {
      UninitializedType uninitializedType = this.chatController.getUninitializedType();
      String name2 = this.broadcastController.getChannelInfo().name;
      this.text = name2;
      if (uninitializedType != UninitializedType.Initialized) {
         logger.warn("Invalid twitch chat state {}", new Object[]{uninitializedType});
      } else if (this.chatController.getCreatedType(this.text) == CreatedType.Disconnected) {
         this.chatController.checkString3(name2);
      } else {
         logger.warn("Invalid twitch chat state {}", new Object[]{uninitializedType});
      }
   }

   @Override
   public ErrorType getErrorType() {
      return this.errorType;
   }

   @Override
   public void update17() {
   }

   @Override
   public void handleErrorCode3(ErrorCode errorCode) {
      if (errorCode == ErrorCode.TTV_EC_SOUNDFLOWER_NOT_INSTALLED) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stream.unavailable.soundflower.chat.link");
         chatComponentTranslation.getChatStyle()
            .setChatClickEvent(
               new ClickEvent(
                  net.minecraft.event.ClickEvent.Action.OPEN_URL,
                  "https://help.mojang.com/customer/portal/articles/1374877-configuring-soundflower-for-streaming-on-apple-computers"
               )
            );
         chatComponentTranslation.getChatStyle().setUnderlined(true);
         ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("stream.unavailable.soundflower.chat", chatComponentTranslation);
         chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
         this.minecraft.ingameGUI.getChatGUI().printChatMessage(chatComponentTranslation2);
      } else {
         ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("stream.unavailable.unknown.chat", ErrorCode.getString(errorCode));
         chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
         this.minecraft.ingameGUI.getChatGUI().printChatMessage(chatComponentTranslation3);
      }
   }

   @Override
   public boolean isActive9() {
      return this.broadcastController.isFlag3();
   }

   @Override
   public void update15() {
      GameSettings gameSettings2 = this.minecraft.gameSettings;
      VideoParams videoParams = this.broadcastController
         .getVideoParams(
            createInt2(gameSettings2.streamKbps),
            createInt(gameSettings2.streamFps),
            createFloat(gameSettings2.streamBytesPerPixel),
            (float)this.minecraft.displayWidth / this.minecraft.displayHeight
         );
      switch (gameSettings2.streamCompression) {
         case 0:
            videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_LOW;
            break;
         case 1:
            videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_MEDIUM;
            break;
         case 2:
            videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
      }

      if (this.framebuffer == null) {
         this.framebuffer = new Framebuffer(videoParams.outputWidth, videoParams.outputHeight, false);
      } else {
         this.framebuffer.createBindFramebuffer(videoParams.outputWidth, videoParams.outputHeight);
      }

      if (gameSettings2.streamPreferredServer != null && gameSettings2.streamPreferredServer.length() > 0) {
         for (IngestServer ingestServer : this.getArray()) {
            if (ingestServer.serverUrl.equals(gameSettings2.streamPreferredServer)) {
               this.broadcastController.setIngestServer(ingestServer);
               break;
            }
         }
      }

      this.intValue = videoParams.targetFps;
      this.flag6 = gameSettings2.useNativeTransport;
      this.broadcastController.checkVideoParams(videoParams);
      logger.info(
         marker,
         "Streaming at {}/{} at {} kbps to {}",
         videoParams.outputWidth,
         videoParams.outputHeight,
         videoParams.maxKbps,
         this.broadcastController.getIngestServer().serverUrl
      );
      this.broadcastController.checkString4((String)null, "Minecraft", (String)null);
   }

   @Override
   public void handleUninitializedType2(net.minecraft.client.stream.BroadcastController.UninitializedType uninitializedType) {
      logger.debug(marker, "Broadcast state changed to {}", new Object[]{uninitializedType});
      if (uninitializedType == net.minecraft.client.stream.BroadcastController.UninitializedType.Initialized) {
         this.broadcastController.handleUninitializedType(net.minecraft.client.stream.BroadcastController.UninitializedType.Authenticated);
      }
   }

   @Override
   public void handleIngestList(IngestList ingestList) {
   }

   @Override
   public boolean isActive2() {
      boolean flag7 = this.minecraft.gameSettings.streamMicToggleBehavior == 1;
      return this.flag4 || this.minecraft.gameSettings.streamMicVolume <= 0.0F || flag7 != this.flag;
   }

   public static float createFloat(float f) {
      return 0.1F + f * 0.1F;
   }

   @Override
   public void handleUninitializedType3(UninitializedType uninitializedType) {
   }

   @Override
   public boolean isActive3() {
      return this.flag2;
   }

   @Override
   public void update12() {
      logger.debug(marker, "Shutdown streaming");
      this.broadcastController.update4();
      this.chatController.update5();
   }

   @Override
   public IngestServerTester getIngestServerTester() {
      return this.broadcastController.getIngestServerTester();
   }

   @Override
   public IngestServer[] getArray() {
      return this.broadcastController.getIngestList().getServers();
   }

   @Override
   public void handleErrorCode6(ErrorCode errorCode) {
      if (ErrorCode.failed(errorCode)) {
         logger.error(marker, "Chat failed to shutdown");
      }
   }

   @Override
   public void update16() {
      this.broadcastController.isActive2();
      this.flag4 = true;
      this.update3();
   }

   @Override
   public void handleStreamInfo(StreamInfo streamInfo) {
      logger.debug(marker, "Stream info updated; {} viewers on stream ID {}", streamInfo.viewers, streamInfo.streamId);
   }

   @Override
   public void update() {
      int streamChatEnabled2 = this.minecraft.gameSettings.streamChatEnabled;
      boolean flag7 = this.text != null && this.chatController.checkString4(this.text);
      boolean flag8 = this.chatController.getUninitializedType() == UninitializedType.Initialized
         && (this.text == null || this.chatController.getCreatedType(this.text) == CreatedType.Disconnected);
      if (streamChatEnabled2 == 2) {
         if (flag7) {
            logger.debug(marker, "Disconnecting from twitch chat per user options");
            this.chatController.checkString(this.text);
         }
      } else if (streamChatEnabled2 == 1) {
         if (flag8 && this.broadcastController.isFlag3()) {
            logger.debug(marker, "Connecting to twitch chat per user options");
            this.update2();
         }
      } else if (streamChatEnabled2 == 0) {
         if (flag7 && !this.isActive()) {
            logger.debug(marker, "Disconnecting from twitch chat as user is no longer streaming");
            this.chatController.checkString(this.text);
         } else if (flag8 && this.isActive()) {
            logger.debug(marker, "Connecting to twitch chat as user is streaming");
            this.update2();
         }
      }

      this.broadcastController.update3();
      this.chatController.update2();
   }

   private boolean checkSet(Set set, Set set2, int i) {
      return set.contains(ChatUserMode.TTV_CHAT_USERMODE_BANNED)
         ? false
         : (
            set.contains(ChatUserMode.TTV_CHAT_USERMODE_ADMINSTRATOR)
               ? true
               : (
                  set.contains(ChatUserMode.TTV_CHAT_USERMODE_MODERATOR)
                     ? true
                     : (
                        set.contains(ChatUserMode.TTV_CHAT_USERMODE_STAFF)
                           ? true
                           : (i == 0 ? true : (i == 1 ? set2.contains(ChatUserSubscription.TTV_CHAT_USERSUB_SUBSCRIBER) : false))
                     )
               )
         );
   }

   @Override
   public int getInt() {
      return this.isActive() ? this.broadcastController.getStreamInfo().viewers : 0;
   }

   @Override
   public void handleIngestServerTester(IngestServerTester ingestServerTester, UninitalizedType uninitalizedType) {
      logger.debug(marker, "Ingest test state changed to {}", new Object[]{uninitalizedType});
      if (uninitalizedType == UninitalizedType.Finished) {
         this.flag3 = true;
      }
   }

   @Override
   public void update5() {
      this.broadcastController.isActive9();
      this.flag4 = false;
      this.update3();
   }

   @Override
   public void update14() {
   }

   @Override
   public void handleBoolean(boolean flag7) {
      this.flag = flag7;
      this.update3();
   }

   @Override
   public void handleString11(String text2, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3) {
      for (ChatUserInfo chatUserInfo : chatUserInfos2) {
         this.map.remove(chatUserInfo.displayName);
      }

      for (ChatUserInfo chatUserInfo2 : chatUserInfos3) {
         this.map.put(chatUserInfo2.displayName, chatUserInfo2);
      }

      for (ChatUserInfo chatUserInfo3 : chatUserInfos) {
         this.map.put(chatUserInfo3.displayName, chatUserInfo3);
      }
   }

   @Override
   public void handleErrorCode(ErrorCode errorCode) {
      logger.warn(marker, "Issue submitting frame: {} (Error code {})", ErrorCode.getString(errorCode), errorCode.getValue());
      this.minecraft
         .ingameGUI
         .getChatGUI()
         .printChatMessageWithOptionalDeletion(new ChatComponentText("Issue streaming frame: " + errorCode + " (" + ErrorCode.getString(errorCode) + ")"), 2);
   }

   @Override
   public void handleString10(String text2, ChatTokenizedMessage[] chatTokenizedMessages) {
   }

   @Override
   public boolean isActive5() {
      return this.broadcastController.isActive12();
   }

   @Override
   public void handleErrorCode2(ErrorCode errorCode) {
      if (ErrorCode.succeeded(errorCode)) {
         logger.debug(marker, "Login attempt successful");
         this.flag2 = true;
      } else {
         logger.warn(marker, "Login attempt unsuccessful: {} (error code {})", ErrorCode.getString(errorCode), errorCode.getValue());
         this.flag2 = false;
      }
   }

   private void handleString8(String text2, ChatRawMessage chatRawMessage) {
      ChatUserInfo chatUserInfo = this.map.get(text2);
      if (chatUserInfo == null) {
         chatUserInfo = new ChatUserInfo();
         chatUserInfo.displayName = text2;
         this.map.put(text2, chatUserInfo);
      }

      chatUserInfo.subscriptions = chatRawMessage.subscriptions;
      chatUserInfo.modes = chatRawMessage.modes;
      chatUserInfo.nameColorARGB = chatRawMessage.nameColorARGB;
   }

   @Override
   public void handleString3(String text2) {
   }

   @Override
   public void handleMetadata(Metadata metadata2) {
      if (this.isActive() && this.flag6) {
         long longValue = this.broadcastController.getLong2();
         if (!this.broadcastController.checkString(metadata2.getText(), longValue + 0L, metadata2.getString(), metadata2.getString2())) {
            logger.warn(marker, "Couldn't send stream metadata action at {}: {}", longValue + 0L, metadata2);
         } else {
            logger.debug(marker, "Sent stream metadata action at {}: {}", longValue + 0L, metadata2);
         }
      }
   }

   @Override
   public boolean isActive4() {
      return this.broadcastController.isActive11();
   }
}
