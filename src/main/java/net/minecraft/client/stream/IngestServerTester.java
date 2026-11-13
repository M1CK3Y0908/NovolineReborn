package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.List;
import tv.twitch.AuthToken;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.ArchivingState;
import tv.twitch.broadcast.AudioParams;
import tv.twitch.broadcast.ChannelInfo;
import tv.twitch.broadcast.EncodingCpuUsage;
import tv.twitch.broadcast.FrameBuffer;
import tv.twitch.broadcast.GameInfoList;
import tv.twitch.broadcast.IStatCallbacks;
import tv.twitch.broadcast.IStreamCallbacks;
import tv.twitch.broadcast.IngestList;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.broadcast.PixelFormat;
import tv.twitch.broadcast.RTMPState;
import tv.twitch.broadcast.StartFlags;
import tv.twitch.broadcast.StatType;
import tv.twitch.broadcast.Stream;
import tv.twitch.broadcast.StreamInfo;
import tv.twitch.broadcast.UserInfo;
import tv.twitch.broadcast.VideoParams;

public class IngestServerTester {
   protected IngestServerTester.Contract contract = null;
   protected Stream stream = null;
   protected IngestList ingestList = null;
   protected IngestServerTester.UninitalizedType uninitalizedType = IngestServerTester.UninitalizedType.Uninitalized;
   protected long longValue = 8000L;
   protected long longValue2 = 2000L;
   protected long longValue3 = 0L;
   protected RTMPState rTMPState = RTMPState.Invalid;
   protected VideoParams videoParams = null;
   protected AudioParams audioParams = null;
   protected long longValue4 = 0L;
   protected List<FrameBuffer> list = null;
   protected boolean flag = false;
   protected IStreamCallbacks iStreamCallbacks = null;
   protected IStatCallbacks iStatCallbacks = null;
   protected IngestServer ingestServer = null;
   protected boolean flag2 = false;
   protected boolean flag3 = false;
   protected int intValue = -1;
   protected int intValue2 = 0;
   protected long longValue5 = 0L;
   protected float floatValue = 0.0F;
   protected float floatValue2 = 0.0F;
   protected boolean flag4 = false;
   protected boolean flag5 = false;
   protected boolean flag6 = false;
   protected IStreamCallbacks iStreamCallbacks2 = new IStreamCallbacks() {
      public void bufferUnlockCallback(long l) {
      }

      public void getUserInfoCallback(ErrorCode errorCode, UserInfo userInfo) {
      }

      public void loginCallback(ErrorCode errorCode2, ChannelInfo channelInfo) {
      }

      public void sendEndSpanMetaDataCallback(ErrorCode errorCode3) {
      }

      public void stopCallback(ErrorCode errorCode4) {
         if (ErrorCode.failed(errorCode4)) {
            System.out.println("IngestTester.stopCallback failed to stop - " + IngestServerTester.this.ingestServer.serverName + ": " + errorCode4.toString());
         }

         IngestServerTester.this.flag6 = false;
         IngestServerTester.this.flag4 = false;
         IngestServerTester.this.handleUninitalizedType(IngestServerTester.UninitalizedType.DoneTestingServer);
         IngestServerTester.this.ingestServer = null;
         if (IngestServerTester.this.flag2) {
            IngestServerTester.this.handleUninitalizedType(IngestServerTester.UninitalizedType.Cancelling);
         }
      }

      public void sendStartSpanMetaDataCallback(ErrorCode errorCode5) {
      }

      public void setStreamInfoCallback(ErrorCode errorCode6) {
      }

      public void getArchivingStateCallback(ErrorCode errorCode7, ArchivingState archivingState) {
      }

      public void getGameNameListCallback(ErrorCode errorCode8, GameInfoList gameInfoList) {
      }

      public void startCallback(ErrorCode errorCode9) {
         IngestServerTester.this.flag5 = false;
         if (ErrorCode.succeeded(errorCode9)) {
            IngestServerTester.this.flag4 = true;
            IngestServerTester.this.longValue4 = System.currentTimeMillis();
            IngestServerTester.this.handleUninitalizedType(IngestServerTester.UninitalizedType.ConnectingToServer);
         } else {
            IngestServerTester.this.flag = false;
            IngestServerTester.this.handleUninitalizedType(IngestServerTester.UninitalizedType.DoneTestingServer);
         }
      }

      public void runCommercialCallback(ErrorCode errorCode10) {
      }

      public void getIngestServersCallback(ErrorCode errorCode11, IngestList ingestList2) {
      }

      public void sendActionMetaDataCallback(ErrorCode errorCode12) {
      }

      public void requestAuthTokenCallback(ErrorCode errorCode13, AuthToken authToken) {
      }

      public void getStreamInfoCallback(ErrorCode errorCode14, StreamInfo streamInfo) {
      }
   };
   protected IStatCallbacks iStatCallbacks2 = new IStatCallbacks() {
      public void statCallback(StatType statType, long l) {
         switch (statType) {
            case TTV_ST_RTMPSTATE:
               IngestServerTester.this.rTMPState = RTMPState.lookupValue((int)l);
               break;
            case TTV_ST_RTMPDATASENT:
               IngestServerTester.this.longValue3 = l;
         }
      }
   };

   protected void handleIngestServer(IngestServer ingestServer2) {
      if (this.flag5) {
         this.flag3 = true;
      } else if (this.flag4) {
         this.flag6 = true;
         ErrorCode errorCode = this.stream.stop(true);
         if (ErrorCode.failed(errorCode)) {
            this.iStreamCallbacks2.stopCallback(ErrorCode.TTV_EC_SUCCESS);
            System.out.println("Stop failed: " + errorCode.toString());
         }

         this.stream.pollStats();
      } else {
         this.iStreamCallbacks2.stopCallback(ErrorCode.TTV_EC_SUCCESS);
      }
   }

   public IngestServerTester(Stream stream2, IngestList ingestList2) {
      this.stream = stream2;
      this.ingestList = ingestList2;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public IngestServer getIngestServer() {
      return this.ingestServer;
   }

   public void update3() {
      if (this.uninitalizedType == IngestServerTester.UninitalizedType.Uninitalized) {
         this.intValue = 0;
         this.flag2 = false;
         this.flag3 = false;
         this.flag4 = false;
         this.flag5 = false;
         this.flag6 = false;
         this.iStatCallbacks = this.stream.getStatCallbacks();
         this.stream.setStatCallbacks(this.iStatCallbacks2);
         this.iStreamCallbacks = this.stream.getStreamCallbacks();
         this.stream.setStreamCallbacks(this.iStreamCallbacks2);
         this.videoParams = new VideoParams();
         this.videoParams.targetFps = 60;
         this.videoParams.maxKbps = 3500;
         this.videoParams.outputWidth = 1280;
         this.videoParams.outputHeight = 720;
         this.videoParams.pixelFormat = PixelFormat.TTV_PF_BGRA;
         this.videoParams.encodingCpuUsage = EncodingCpuUsage.TTV_ECU_HIGH;
         this.videoParams.disableAdaptiveBitrate = true;
         this.videoParams.verticalFlip = false;
         this.stream.getDefaultParams(this.videoParams);
         this.audioParams = new AudioParams();
         this.audioParams.audioEnabled = false;
         this.audioParams.enableMicCapture = false;
         this.audioParams.enablePlaybackCapture = false;
         this.audioParams.enablePassthroughAudio = false;
         this.list = Lists.newArrayList();

         for (int i = 0; i < 3; i++) {
            FrameBuffer frameBuffer = this.stream.allocateFrameBuffer(this.videoParams.outputWidth * this.videoParams.outputHeight * 4);
            if (!frameBuffer.getIsValid()) {
               this.update2();
               this.handleUninitalizedType(IngestServerTester.UninitalizedType.Failed);
               return;
            }

            this.list.add(frameBuffer);
            this.stream.randomizeFrameBuffer(frameBuffer);
         }

         this.handleUninitalizedType(IngestServerTester.UninitalizedType.Starting);
         this.longValue4 = System.currentTimeMillis();
      }
   }

   public void setContract(IngestServerTester.Contract contract2) {
      this.contract = contract2;
   }

   protected void handleUninitalizedType(IngestServerTester.UninitalizedType uninitalizedType2) {
      if (uninitalizedType2 != this.uninitalizedType) {
         this.uninitalizedType = uninitalizedType2;
         if (this.contract != null) {
            this.contract.handleIngestServerTester(this, uninitalizedType2);
         }
      }
   }

   protected void update2() {
      this.ingestServer = null;
      if (this.list != null) {
         for (int i = 0; i < this.list.size(); i++) {
            this.list.get(i).free();
         }

         this.list = null;
      }

      if (this.stream.getStatCallbacks() == this.iStatCallbacks2) {
         this.stream.setStatCallbacks(this.iStatCallbacks);
         this.iStatCallbacks = null;
      }

      if (this.stream.getStreamCallbacks() == this.iStreamCallbacks2) {
         this.stream.setStreamCallbacks(this.iStreamCallbacks);
         this.iStreamCallbacks = null;
      }
   }

   public int getIntValue() {
      return this.intValue;
   }

   public void update4() {
      if (!this.isActive() && this.uninitalizedType != IngestServerTester.UninitalizedType.Uninitalized && !this.flag5 && !this.flag6) {
         switch (this.uninitalizedType) {
            case Starting:
            case DoneTestingServer:
               if (this.ingestServer == null) {
                  this.longValue4 = 0L;
                  this.flag3 = false;
                  this.flag = true;
                  if (this.uninitalizedType != IngestServerTester.UninitalizedType.Starting) {
                     this.intValue++;
                  }

                  if (this.intValue < this.ingestList.getServers().length) {
                     this.ingestServer = this.ingestList.getServers()[this.intValue];
                     this.checkIngestServer(this.ingestServer);
                  } else {
                     this.handleUninitalizedType(IngestServerTester.UninitalizedType.Finished);
                  }
                  break;
               }

               if (this.flag3 || !this.flag) {
                  this.ingestServer.bitrateKbps = 0.0F;
               }

               this.handleIngestServer(this.ingestServer);
               break;
            case ConnectingToServer:
            case TestingServer:
               this.checkIngestServer2(this.ingestServer);
               break;
            case Cancelling:
               this.handleUninitalizedType(IngestServerTester.UninitalizedType.Cancelled);
         }

         this.update();
         if (this.uninitalizedType == IngestServerTester.UninitalizedType.Cancelled || this.uninitalizedType == IngestServerTester.UninitalizedType.Finished) {
            this.update2();
         }
      }
   }

   protected boolean checkIngestServer2(IngestServer ingestServer2) {
      if (this.flag3 || this.flag2 || this.getLong() >= this.longValue) {
         this.handleUninitalizedType(IngestServerTester.UninitalizedType.DoneTestingServer);
         return true;
      }

      if (this.flag5 || this.flag6) {
         return true;
      }

      if (ErrorCode.failed(this.stream.submitVideoFrame(this.list.get(this.intValue2)))) {
         this.flag = false;
         this.handleUninitalizedType(IngestServerTester.UninitalizedType.DoneTestingServer);
         return false;
      }

      this.intValue2 = (this.intValue2 + 1) % this.list.size();
      this.stream.pollStats();
      if (this.rTMPState == RTMPState.SendVideo) {
         this.handleUninitalizedType(IngestServerTester.UninitalizedType.TestingServer);
         long longValue = this.getLong();
         if (longValue > 0L && this.longValue3 > this.longValue5) {
            ingestServer2.bitrateKbps = (float)(this.longValue3 * 8L) / (float)this.getLong();
            this.longValue5 = this.longValue3;
         }
      }

      return true;
   }

   protected boolean checkIngestServer(IngestServer ingestServer2) {
      this.flag = true;
      this.longValue3 = 0L;
      this.rTMPState = RTMPState.Idle;
      this.ingestServer = ingestServer2;
      this.flag5 = true;
      this.handleUninitalizedType(IngestServerTester.UninitalizedType.ConnectingToServer);
      if (ErrorCode.failed(this.stream.start(this.videoParams, this.audioParams, ingestServer2, StartFlags.TTV_Start_BandwidthTest, true))) {
         this.flag5 = false;
         this.flag = false;
         this.handleUninitalizedType(IngestServerTester.UninitalizedType.DoneTestingServer);
         return false;
      } else {
         this.longValue5 = this.longValue3;
         ingestServer2.bitrateKbps = 0.0F;
         this.intValue2 = 0;
         return true;
      }
   }

   protected long getLong() {
      return System.currentTimeMillis() - this.longValue4;
   }

   public boolean isActive() {
      return this.uninitalizedType == IngestServerTester.UninitalizedType.Finished
         || this.uninitalizedType == IngestServerTester.UninitalizedType.Cancelled
         || this.uninitalizedType == IngestServerTester.UninitalizedType.Failed;
   }

   public void update5() {
      if (!this.isActive() && !this.flag2) {
         this.flag2 = true;
         if (this.ingestServer != null) {
            this.ingestServer.bitrateKbps = 0.0F;
         }
      }
   }

   protected void update() {
      float f = (float)this.getLong();
      switch (this.uninitalizedType) {
         case Starting:
         case ConnectingToServer:
         case Uninitalized:
         case Finished:
         case Cancelled:
         case Failed:
            this.floatValue2 = 0.0F;
            break;
         case DoneTestingServer:
            this.floatValue2 = 1.0F;
            break;
         case TestingServer:
         case Cancelling:
         default:
            this.floatValue2 = f / (float)this.longValue;
      }

      switch (this.uninitalizedType) {
         case Finished:
         case Cancelled:
         case Failed:
            this.floatValue = 1.0F;
            break;
         default:
            this.floatValue = (float)this.intValue / this.ingestList.getServers().length;
            this.floatValue = this.floatValue + this.floatValue2 / this.ingestList.getServers().length;
      }
   }

   public interface Contract {
      void handleIngestServerTester(IngestServerTester ingestServerTester, IngestServerTester.UninitalizedType uninitalizedType2);
   }

   public enum UninitalizedType {
      Uninitalized,
      Starting,
      ConnectingToServer,
      TestingServer,
      DoneTestingServer,
      Finished,
      Cancelling,
      Cancelled,
      Failed;
   }
}
