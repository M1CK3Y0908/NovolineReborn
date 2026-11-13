package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.MessageLevel;
import tv.twitch.StandardCoreAPI;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioDeviceType;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.DesktopStreamAPI;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.StreamInfoForSetting;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;
import wtf.novoline.util.RingBuffer;

public class BroadcastController {
   protected final int intValue = 30;
   protected final int intValue2 = 3;
   private String text = null;
   protected BroadcastController.Contract contract = null;
   private static final Logger logger = LogManager.getLogger();
   protected String text2 = "";
   protected String text3 = "";
   protected String text4 = "";
   private ErrorCode errorCode;
   protected boolean flag = true;
   protected Core core = null;
   protected Stream stream = null;
   protected List<FrameBuffer> list = Lists.newArrayList();
   protected List<FrameBuffer> list2 = Lists.newArrayList();
   protected boolean flag2 = false;
   protected boolean flag3 = false;
   protected boolean flag4 = false;
   protected BroadcastController.UninitializedType uninitializedType = BroadcastController.UninitializedType.Uninitialized;
   protected String text5 = null;
   protected VideoParams videoParams = null;
   protected AudioParams audioParams = null;
   protected IngestList ingestList = new IngestList(new IngestServer[0]);
   protected IngestServer ingestServer = null;
   protected AuthToken authToken = new AuthToken();
   protected ChannelInfo channelInfo = new ChannelInfo();
   protected UserInfo userInfo = new UserInfo();
   protected StreamInfo streamInfo = new StreamInfo();
   protected ArchivingState archivingState = new ArchivingState();
   protected long longValue = 0L;
   protected IngestServerTester ingestServerTester = null;
   protected IStreamCallbacks iStreamCallbacks = new IStreamCallbacks() {
      public void stopCallback(ErrorCode errorCode2) {
         if (ErrorCode.succeeded(errorCode2)) {
            BroadcastController.this.videoParams = null;
            BroadcastController.this.audioParams = null;
            BroadcastController.this.update();

            try {
               if (BroadcastController.this.contract != null) {
                  BroadcastController.this.contract.update4();
               }
            } catch (Exception exception) {
               BroadcastController.this.handleString2(exception.toString());
            }

            if (BroadcastController.this.flag3) {
               BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.ReadyToBroadcast);
            } else {
               BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.Initialized);
            }
         } else {
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.ReadyToBroadcast);
            String string = ErrorCode.getString(errorCode2);
            BroadcastController.this.handleString2(String.format("stopCallback got failure: %s", string));
         }
      }

      public void sendStartSpanMetaDataCallback(ErrorCode errorCode3) {
         if (ErrorCode.failed(errorCode3)) {
            String string2 = ErrorCode.getString(errorCode3);
            BroadcastController.this.handleString2(String.format("sendStartSpanMetaDataCallback got failure: %s", string2));
         }
      }

      public void runCommercialCallback(ErrorCode errorCode4) {
         if (ErrorCode.failed(errorCode4)) {
            String string3 = ErrorCode.getString(errorCode4);
            BroadcastController.this.handleString(String.format("RunCommercialCallback got failure: %s", string3));
         }
      }

      public void requestAuthTokenCallback(ErrorCode errorCode5, AuthToken authToken2) {
         if (ErrorCode.succeeded(errorCode5)) {
            BroadcastController.this.authToken = authToken2;
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.Authenticated);
         } else {
            BroadcastController.this.authToken.data = "";
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.Initialized);
            String string4 = ErrorCode.getString(errorCode5);
            BroadcastController.this.handleString2(String.format("RequestAuthTokenDoneCallback got failure: %s", string4));
         }

         try {
            if (BroadcastController.this.contract != null) {
               BroadcastController.this.contract.handleErrorCode8(errorCode5, authToken2);
            }
         } catch (Exception exception2) {
            BroadcastController.this.handleString2(exception2.toString());
         }
      }

      public void sendEndSpanMetaDataCallback(ErrorCode errorCode6) {
         if (ErrorCode.failed(errorCode6)) {
            String string5 = ErrorCode.getString(errorCode6);
            BroadcastController.this.handleString2(String.format("sendEndSpanMetaDataCallback got failure: %s", string5));
         }
      }

      public void sendActionMetaDataCallback(ErrorCode errorCode7) {
         if (ErrorCode.failed(errorCode7)) {
            String string6 = ErrorCode.getString(errorCode7);
            BroadcastController.this.handleString2(String.format("sendActionMetaDataCallback got failure: %s", string6));
         }
      }

      public void getArchivingStateCallback(ErrorCode errorCode8, ArchivingState archivingState2) {
         BroadcastController.this.archivingState = archivingState2;
         if (ErrorCode.failed(errorCode8)) {
         }
      }

      public void bufferUnlockCallback(long l) {
         FrameBuffer frameBuffer = FrameBuffer.lookupBuffer(l);
         BroadcastController.this.list2.add(frameBuffer);
      }

      public void loginCallback(ErrorCode errorCode9, ChannelInfo channelInfo2) {
         if (ErrorCode.succeeded(errorCode9)) {
            BroadcastController.this.channelInfo = channelInfo2;
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.LoggedIn);
            BroadcastController.this.flag3 = true;
         } else {
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.Initialized);
            BroadcastController.this.flag3 = false;
            String string7 = ErrorCode.getString(errorCode9);
            BroadcastController.this.handleString2(String.format("LoginCallback got failure: %s", string7));
         }

         try {
            if (BroadcastController.this.contract != null) {
               BroadcastController.this.contract.handleErrorCode2(errorCode9);
            }
         } catch (Exception exception3) {
            BroadcastController.this.handleString2(exception3.toString());
         }
      }

      public void getUserInfoCallback(ErrorCode errorCode10, UserInfo userInfo2) {
         BroadcastController.this.userInfo = userInfo2;
         if (ErrorCode.failed(errorCode10)) {
            String string8 = ErrorCode.getString(errorCode10);
            BroadcastController.this.handleString2(String.format("UserInfoDoneCallback got failure: %s", string8));
         }
      }

      public void startCallback(ErrorCode errorCode11) {
         if (ErrorCode.succeeded(errorCode11)) {
            try {
               if (BroadcastController.this.contract != null) {
                  BroadcastController.this.contract.update10();
               }
            } catch (Exception exception4) {
               BroadcastController.this.handleString2(exception4.toString());
            }

            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.Broadcasting);
         } else {
            BroadcastController.this.videoParams = null;
            BroadcastController.this.audioParams = null;
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.ReadyToBroadcast);

            try {
               if (BroadcastController.this.contract != null) {
                  BroadcastController.this.contract.handleErrorCode3(errorCode11);
               }
            } catch (Exception exception5) {
               BroadcastController.this.handleString2(exception5.toString());
            }

            String string9 = ErrorCode.getString(errorCode11);
            BroadcastController.this.handleString2(String.format("startCallback got failure: %s", string9));
         }
      }

      public void getGameNameListCallback(ErrorCode errorCode12, GameInfoList gameInfoList) {
         if (ErrorCode.failed(errorCode12)) {
            String string10 = ErrorCode.getString(errorCode12);
            BroadcastController.this.handleString2(String.format("GameNameListCallback got failure: %s", string10));
         }

         try {
            if (BroadcastController.this.contract != null) {
               BroadcastController.this.contract.handleErrorCode4(errorCode12, gameInfoList == null ? new GameInfo[0] : gameInfoList.list);
            }
         } catch (Exception exception6) {
            BroadcastController.this.handleString2(exception6.toString());
         }
      }

      public void getStreamInfoCallback(ErrorCode errorCode13, StreamInfo streamInfo2) {
         if (ErrorCode.succeeded(errorCode13)) {
            BroadcastController.this.streamInfo = streamInfo2;

            try {
               if (BroadcastController.this.contract != null) {
                  BroadcastController.this.contract.handleStreamInfo(streamInfo2);
               }
            } catch (Exception exception7) {
               BroadcastController.this.handleString2(exception7.toString());
            }
         } else {
            String string11 = ErrorCode.getString(errorCode13);
            BroadcastController.this.handleString(String.format("StreamInfoDoneCallback got failure: %s", string11));
         }
      }

      public void getIngestServersCallback(ErrorCode errorCode14, IngestList ingestList2) {
         if (ErrorCode.succeeded(errorCode14)) {
            BroadcastController.this.ingestList = ingestList2;
            BroadcastController.this.ingestServer = BroadcastController.this.ingestList.getDefaultServer();
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.ReceivedIngestServers);

            try {
               if (BroadcastController.this.contract != null) {
                  BroadcastController.this.contract.handleIngestList(ingestList2);
               }
            } catch (Exception exception8) {
               BroadcastController.this.handleString2(exception8.toString());
            }
         } else {
            String string12 = ErrorCode.getString(errorCode14);
            BroadcastController.this.handleString2(String.format("IngestListCallback got failure: %s", string12));
            BroadcastController.this.handleUninitializedType(BroadcastController.UninitializedType.LoggingIn);
         }
      }

      public void setStreamInfoCallback(ErrorCode errorCode15) {
         if (ErrorCode.failed(errorCode15)) {
            String string13 = ErrorCode.getString(errorCode15);
            BroadcastController.this.handleString(String.format("SetStreamInfoCallback got failure: %s", string13));
         }
      }
   };
   private static final RingBuffer<String> ringBuffer = new RingBuffer<>(String.class);
   protected IStatCallbacks iStatCallbacks = new IStatCallbacks() {
      public void statCallback(StatType statType, long l) {
      }
   };

   public boolean isActive10() {
      if (this.isActive11()) {
         return false;
      }

      if (this.isActive6()) {
         this.stream.stop(false);
      }

      this.text5 = "";
      this.authToken = new AuthToken();
      if (!this.flag3) {
         return false;
      }

      this.flag3 = false;
      if (!this.flag4) {
         try {
            if (this.contract != null) {
               this.contract.update7();
            }
         } catch (Exception exception) {
            this.handleString2(exception.toString());
         }
      }

      this.handleUninitializedType(BroadcastController.UninitializedType.Initialized);
      return true;
   }

   protected PixelFormat getPixelFormat() {
      return PixelFormat.TTV_PF_RGBA;
   }

   public IngestServer getIngestServer() {
      return this.ingestServer;
   }

   protected boolean checkErrorCode(ErrorCode errorCode2) {
      if (ErrorCode.failed(errorCode2)) {
         this.handleString2(ErrorCode.getString(errorCode2));
         return false;
      } else {
         return true;
      }
   }

   protected void handleString(String text6) {
      ringBuffer.add("<Warning> " + text6);
      logger.warn(TwitchStream.marker, "[Broadcast controller] {}", new Object[]{text6});
   }

   public VideoParams getVideoParams(int i, int i2, float f, float f2) {
      int[] maxResolution = this.stream.getMaxResolution(i, i2, f, f2);
      VideoParams videoParams2 = new VideoParams();
      videoParams2.maxKbps = i;
      videoParams2.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
      videoParams2.pixelFormat = this.getPixelFormat();
      videoParams2.targetFps = i2;
      videoParams2.outputWidth = maxResolution[0];
      videoParams2.outputHeight = maxResolution[1];
      videoParams2.disableAdaptiveBitrate = false;
      videoParams2.verticalFlip = false;
      return videoParams2;
   }

   public boolean isActive2() {
      if (!this.isActive6()) {
         return false;
      }

      ErrorCode errorCode2 = this.stream.pauseVideo();
      if (ErrorCode.failed(errorCode2)) {
         this.isActive5();
         String string = ErrorCode.getString(errorCode2);
         this.handleString2(String.format("Error pausing stream: %s\n", string));
      } else {
         this.handleUninitializedType(BroadcastController.UninitializedType.Paused);
      }

      return ErrorCode.succeeded(errorCode2);
   }

   public void setText2(String text6) {
      this.text2 = text6;
   }

   public FrameBuffer getFrameBuffer() {
      if (this.list2.size() == 0) {
         this.handleString2(String.format("Out of free buffers, this should never happen"));
         return null;
      } else {
         FrameBuffer frameBuffer = this.list2.get(this.list2.size() - 1);
         this.list2.remove(this.list2.size() - 1);
         return frameBuffer;
      }
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isActive11() {
      return this.uninitializedType == BroadcastController.UninitializedType.IngestTesting;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public StreamInfo getStreamInfo() {
      return this.streamInfo;
   }

   public void update4() {
      if (this.uninitializedType != BroadcastController.UninitializedType.Uninitialized) {
         if (this.ingestServerTester != null) {
            this.ingestServerTester.update5();
         }

         for (; this.ingestServerTester != null; this.update3()) {
            try {
               Thread.sleep(200L);
            } catch (Exception exception) {
               this.handleString2(exception.toString());
            }
         }

         this.isActive7();
      }
   }

   public boolean isActive8() {
      return this.uninitializedType == BroadcastController.UninitializedType.ReadyToBroadcast;
   }

   public boolean checkString2(String text6, AuthToken authToken2) {
      if (this.isActive11()) {
         return false;
      }

      this.isActive10();
      if (text6 == null || text6.isEmpty()) {
         this.handleString2("Username must be valid");
         return false;
      }

      if (authToken2 != null && authToken2.data != null && !authToken2.data.isEmpty()) {
         this.text5 = text6;
         this.authToken = authToken2;
         if (this.isFlag2()) {
            this.handleUninitializedType(BroadcastController.UninitializedType.Authenticated);
         }

         return true;
      } else {
         this.handleString2("Auth token must be valid");
         return false;
      }
   }

   public IngestServerTester getIngestServerTester() {
      return this.ingestServerTester;
   }

   public ChannelInfo getChannelInfo() {
      return this.channelInfo;
   }

   public void handleFloat2(float f) {
      this.stream.setVolume(AudioDeviceType.TTV_PLAYBACK_DEVICE, f);
   }

   public boolean checkString(String text6, long l, String text7, String text8) {
      ErrorCode errorCode2 = this.stream.sendActionMetaData(this.authToken, text6, l, text7, text8);
      if (ErrorCode.failed(errorCode2)) {
         String string = ErrorCode.getString(errorCode2);
         this.handleString2(String.format("Error while sending meta data: %s\n", string));
         return false;
      } else {
         return true;
      }
   }

   public ErrorCode getErrorCode(FrameBuffer frameBuffer) {
      if (this.isActive12()) {
         this.isActive9();
      } else if (!this.isActive6()) {
         return ErrorCode.TTV_EC_STREAM_NOT_STARTED;
      }

      ErrorCode errorCode2 = this.stream.submitVideoFrame(frameBuffer);
      if (errorCode2 != ErrorCode.TTV_EC_SUCCESS) {
         String string = ErrorCode.getString(errorCode2);
         if (ErrorCode.succeeded(errorCode2)) {
            this.handleString(String.format("Warning in SubmitTexturePointer: %s\n", string));
         } else {
            this.handleString2(String.format("Error in SubmitTexturePointer: %s\n", string));
            this.isActive5();
         }

         if (this.contract != null) {
            this.contract.handleErrorCode(errorCode2);
         }
      }

      return errorCode2;
   }

   public long getLong(String text6, long l, String text7, String text8) {
      long l2 = this.stream.sendStartSpanMetaData(this.authToken, text6, l, text7, text8);
      if (l2 == -1L) {
         this.handleString2(String.format("Error in SendStartSpanMetaData\n"));
      }

      return l2;
   }

   public IngestServerTester getIngestServerTester2() {
      if (!this.isActive8() || this.ingestList == null) {
         return null;
      }

      if (this.isActive11()) {
         return null;
      }

      this.ingestServerTester = new IngestServerTester(this.stream, this.ingestList);
      this.ingestServerTester.update3();
      this.handleUninitializedType(BroadcastController.UninitializedType.IngestTesting);
      return this.ingestServerTester;
   }

   public IngestList getIngestList() {
      return this.ingestList;
   }

   protected boolean isActive3() {
      for (int i = 0; i < 3; i++) {
         FrameBuffer frameBuffer = this.stream.allocateFrameBuffer(this.videoParams.outputWidth * this.videoParams.outputHeight * 4);
         if (!frameBuffer.getIsValid()) {
            this.handleString2(String.format("Error while allocating frame buffer"));
            return false;
         }

         this.list.add(frameBuffer);
         this.list2.add(frameBuffer);
      }

      return true;
   }

   public void setIngestServer(IngestServer ingestServer2) {
      this.ingestServer = ingestServer2;
   }

   public long getLong2() {
      return this.stream.getStreamTime();
   }

   public BroadcastController() {
      this.core = Core.getInstance();
      if (Core.getInstance() == null) {
         this.core = new Core(new StandardCoreAPI());
      }

      this.stream = new Stream(new DesktopStreamAPI());
   }

   protected void update2() {
      long l = System.nanoTime();
      long l2 = (l - this.longValue) / 1000000000L;
      if (l2 >= 30L) {
         this.longValue = l;
         ErrorCode streamInfo2 = this.stream.getStreamInfo(this.authToken, this.text5);
         if (ErrorCode.failed(streamInfo2)) {
            String string = ErrorCode.getString(streamInfo2);
            this.handleString2(String.format("Error in TTV_GetStreamInfo: %s", string));
         }
      }
   }

   protected void handleUninitializedType(BroadcastController.UninitializedType uninitializedType2) {
      if (uninitializedType2 != this.uninitializedType) {
         this.uninitializedType = uninitializedType2;

         try {
            if (this.contract != null) {
               this.contract.handleUninitializedType2(uninitializedType2);
            }
         } catch (Exception exception) {
            this.handleString2(exception.toString());
         }
      }
   }

   public void setContract(BroadcastController.Contract contract2) {
      this.contract = contract2;
   }

   public ErrorCode getErrorCode2() {
      return this.errorCode;
   }

   public boolean checkVideoParams(VideoParams videoParams2) {
      if (videoParams2 != null && this.isActive8()) {
         this.videoParams = videoParams2.clone();
         this.audioParams = new AudioParams();
         this.audioParams.audioEnabled = this.flag && this.alwaysTrue();
         this.audioParams.enableMicCapture = this.audioParams.audioEnabled;
         this.audioParams.enablePlaybackCapture = this.audioParams.audioEnabled;
         this.audioParams.enablePassthroughAudio = false;
         if (!this.isActive3()) {
            this.videoParams = null;
            this.audioParams = null;
            return false;
         } else {
            ErrorCode errorCode2 = this.stream.start(videoParams2, this.audioParams, this.ingestServer, StartFlags.None, true);
            if (ErrorCode.failed(errorCode2)) {
               this.update();
               String string = ErrorCode.getString(errorCode2);
               this.handleString2(String.format("Error while starting to broadcast: %s", string));
               this.videoParams = null;
               this.audioParams = null;
               return false;
            } else {
               this.handleUninitializedType(BroadcastController.UninitializedType.Starting);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean checkString3(String text6, long l, long l2, String text7, String text8) {
      if (l2 == -1L) {
         this.handleString2(String.format("Invalid sequence id: %d\n", l2));
         return false;
      } else {
         ErrorCode errorCode2 = this.stream.sendEndSpanMetaData(this.authToken, text6, l, l2, text7, text8);
         if (ErrorCode.failed(errorCode2)) {
            String string = ErrorCode.getString(errorCode2);
            this.handleString2(String.format("Error in SendStopSpanMetaData: %s\n", string));
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean isActive9() {
      if (!this.isActive12()) {
         return false;
      }

      this.handleUninitializedType(BroadcastController.UninitializedType.Broadcasting);
      return true;
   }

   public void handleFrameBuffer(FrameBuffer frameBuffer) {
      try {
         this.stream.captureFrameBuffer_ReadPixels(frameBuffer);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Trying to submit a frame to Twitch");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Broadcast State");
         crashReportCategory.addCrashSection("Last reported errors", Arrays.toString(ringBuffer.toArray()));
         crashReportCategory.addCrashSection("Buffer", frameBuffer);
         crashReportCategory.addCrashSection("Free buffer count", this.list2.size());
         crashReportCategory.addCrashSection("Capture buffer count", this.list.size());
         throw new ReportedException(crashReport);
      }
   }

   public boolean isActive12() {
      return this.uninitializedType == BroadcastController.UninitializedType.Paused;
   }

   protected boolean alwaysTrue() {
      return true;
   }

   public void update3() {
      if (this.stream != null && this.flag2) {
         ErrorCode errorCode2 = this.stream.pollTasks();
         this.checkErrorCode(errorCode2);
         if (this.isActive11()) {
            this.ingestServerTester.update4();
            if (this.ingestServerTester.isActive()) {
               this.ingestServerTester = null;
               this.handleUninitializedType(BroadcastController.UninitializedType.ReadyToBroadcast);
            }
         }

         switch (this.uninitializedType) {
            case Authenticated:
               this.handleUninitializedType(BroadcastController.UninitializedType.LoggingIn);
               errorCode2 = this.stream.login(this.authToken);
               if (ErrorCode.failed(errorCode2)) {
                  String string = ErrorCode.getString(errorCode2);
                  this.handleString2(String.format("Error in TTV_Login: %s\n", string));
               }
               break;
            case LoggedIn:
               this.handleUninitializedType(BroadcastController.UninitializedType.FindingIngestServer);
               errorCode2 = this.stream.getIngestServers(this.authToken);
               if (ErrorCode.failed(errorCode2)) {
                  this.handleUninitializedType(BroadcastController.UninitializedType.LoggedIn);
                  String string2 = ErrorCode.getString(errorCode2);
                  this.handleString2(String.format("Error in TTV_GetIngestServers: %s\n", string2));
               }
               break;
            case ReceivedIngestServers:
               this.handleUninitializedType(BroadcastController.UninitializedType.ReadyToBroadcast);
               errorCode2 = this.stream.getUserInfo(this.authToken);
               if (ErrorCode.failed(errorCode2)) {
                  String string3 = ErrorCode.getString(errorCode2);
                  this.handleString2(String.format("Error in TTV_GetUserInfo: %s\n", string3));
               }

               this.update2();
               errorCode2 = this.stream.getArchivingState(this.authToken);
               if (ErrorCode.failed(errorCode2)) {
                  String string4 = ErrorCode.getString(errorCode2);
                  this.handleString2(String.format("Error in TTV_GetArchivingState: %s\n", string4));
               }
            case Starting:
            case Stopping:
            case FindingIngestServer:
            case Authenticating:
            case Initialized:
            case Uninitialized:
            case IngestTesting:
            default:
               break;
            case Paused:
            case Broadcasting:
               this.update2();
         }
      }
   }

   protected void handleString2(String text6) {
      this.text = text6;
      ringBuffer.add("<Error> " + text6);
      logger.error(TwitchStream.marker, "[Broadcast controller] {}", new Object[]{text6});
   }

   public boolean isActive6() {
      return this.uninitializedType == BroadcastController.UninitializedType.Broadcasting
         || this.uninitializedType == BroadcastController.UninitializedType.Paused;
   }

   public boolean checkString4(String text6, String text7, String text8) {
      if (!this.flag3) {
         return false;
      }

      if (text6 == null || text6.equals("")) {
         text6 = this.text5;
      }

      if (text7 == null) {
         text7 = "";
      }

      if (text8 == null) {
         text8 = "";
      }

      StreamInfoForSetting streamInfoForSetting = new StreamInfoForSetting();
      streamInfoForSetting.streamTitle = text8;
      streamInfoForSetting.gameName = text7;
      ErrorCode errorCode2 = this.stream.setStreamInfo(this.authToken, text6, streamInfoForSetting);
      this.checkErrorCode(errorCode2);
      return ErrorCode.succeeded(errorCode2);
   }

   public void handleFloat(float f) {
      this.stream.setVolume(AudioDeviceType.TTV_RECORDER_DEVICE, f);
   }

   public boolean isActive5() {
      if (!this.isActive6()) {
         return false;
      } else {
         ErrorCode errorCode2 = this.stream.stop(true);
         if (ErrorCode.failed(errorCode2)) {
            String string = ErrorCode.getString(errorCode2);
            this.handleString2(String.format("Error while stopping the broadcast: %s", string));
            return false;
         } else {
            this.handleUninitializedType(BroadcastController.UninitializedType.Stopping);
            return ErrorCode.succeeded(errorCode2);
         }
      }
   }

   public boolean isActive7() {
      if (!this.flag2) {
         return true;
      }

      if (this.isActive11()) {
         return false;
      }

      this.flag4 = true;
      this.isActive10();
      this.stream.setStreamCallbacks((IStreamCallbacks)null);
      this.stream.setStatCallbacks((IStatCallbacks)null);
      ErrorCode errorCode2 = this.core.shutdown();
      this.checkErrorCode(errorCode2);
      this.flag2 = false;
      this.flag4 = false;
      this.handleUninitializedType(BroadcastController.UninitializedType.Uninitialized);
      return true;
   }

   public boolean isActive() {
      if (this.flag2) {
         return false;
      } else {
         this.stream.setStreamCallbacks(this.iStreamCallbacks);
         ErrorCode errorCode2 = this.core.initialize(this.text2, System.getProperty("java.library.path"));
         if (!this.checkErrorCode(errorCode2)) {
            this.stream.setStreamCallbacks((IStreamCallbacks)null);
            this.errorCode = errorCode2;
            return false;
         } else {
            errorCode2 = this.core.setTraceLevel(MessageLevel.TTV_ML_ERROR);
            if (!this.checkErrorCode(errorCode2)) {
               this.stream.setStreamCallbacks((IStreamCallbacks)null);
               this.core.shutdown();
               this.errorCode = errorCode2;
               return false;
            } else if (ErrorCode.succeeded(errorCode2)) {
               this.flag2 = true;
               this.handleUninitializedType(BroadcastController.UninitializedType.Initialized);
               return true;
            } else {
               this.errorCode = errorCode2;
               this.core.shutdown();
               return false;
            }
         }
      }
   }

   public boolean isActive4() {
      if (!this.isActive6()) {
         return false;
      }

      ErrorCode errorCode2 = this.stream.runCommercial(this.authToken);
      this.checkErrorCode(errorCode2);
      return ErrorCode.succeeded(errorCode2);
   }

   protected void update() {
      for (int i = 0; i < this.list.size(); i++) {
         this.list.get(i).free();
      }

      this.list2.clear();
      this.list.clear();
   }

   public interface Contract {
      void handleStreamInfo(StreamInfo streamInfo2);

      void handleErrorCode3(ErrorCode errorCode2);

      void handleErrorCode4(ErrorCode errorCode2, GameInfo[] gameInfos);

      void update10();

      void handleUninitializedType2(BroadcastController.UninitializedType uninitializedType2);

      void handleErrorCode2(ErrorCode errorCode2);

      void update4();

      void handleErrorCode8(ErrorCode errorCode2, AuthToken authToken2);

      void update7();

      void handleErrorCode(ErrorCode errorCode2);

      void handleIngestList(IngestList ingestList2);
   }

   public enum UninitializedType {
      Uninitialized,
      Initialized,
      Authenticating,
      Authenticated,
      LoggingIn,
      LoggedIn,
      FindingIngestServer,
      ReceivedIngestServers,
      ReadyToBroadcast,
      Starting,
      Broadcasting,
      Stopping,
      Paused,
      IngestTesting;
   }
}
