package net.minecraft.client.stream;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tv.twitch.AuthToken;
import tv.twitch.Core;
import tv.twitch.ErrorCode;
import tv.twitch.StandardCoreAPI;
import tv.twitch.chat.Chat;
import tv.twitch.chat.ChatBadgeData;
import tv.twitch.chat.ChatChannelInfo;
import tv.twitch.chat.ChatEmoticonData;
import tv.twitch.chat.ChatEvent;
import tv.twitch.chat.ChatRawMessage;
import tv.twitch.chat.ChatTokenizationOption;
import tv.twitch.chat.ChatTokenizedMessage;
import tv.twitch.chat.ChatUserInfo;
import tv.twitch.chat.IChatAPIListener;
import tv.twitch.chat.IChatChannelListener;
import tv.twitch.chat.StandardChatAPI;

public class ChatController {
   protected ChatController.Contract contract = null;
   protected String text = "";
   protected String text2 = "";
   protected String text3 = "";
   protected Core core = null;
   private static final Logger logger = LogManager.getLogger();
   protected Chat chat = null;
   protected ChatController.UninitializedType uninitializedType = ChatController.UninitializedType.Uninitialized;
   protected AuthToken authToken = new AuthToken();
   protected HashMap<String, ChatController.IChatChannelListenerImpl> hashMap = new HashMap<>();
   protected int intValue = 128;
   protected ChatController.NoneType noneType = ChatController.NoneType.None;
   protected ChatController.NoneType noneType2 = ChatController.NoneType.None;
   protected ChatEmoticonData chatEmoticonData = null;
   protected int intValue2 = 500;
   protected int intValue3 = 2000;
   protected IChatAPIListener iChatAPIListener = new IChatAPIListener() {
      public void chatInitializationCallback(ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            ChatController.this.chat.setMessageFlushInterval(ChatController.this.intValue2);
            ChatController.this.chat.setUserChangeEventInterval(ChatController.this.intValue3);
            ChatController.this.update3();
            ChatController.this.handleUninitializedType(ChatController.UninitializedType.Initialized);
         } else {
            ChatController.this.handleUninitializedType(ChatController.UninitializedType.Uninitialized);
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleErrorCode5(errorCode);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }
      }

      public void chatShutdownCallback(ErrorCode errorCode2) {
         if (ErrorCode.succeeded(errorCode2)) {
            ErrorCode errorCode3 = ChatController.this.core.shutdown();
            if (ErrorCode.failed(errorCode3)) {
               String string = ErrorCode.getString(errorCode3);
               ChatController.this.handleString(String.format("Error shutting down the Twitch sdk: %s", string));
            }

            ChatController.this.handleUninitializedType(ChatController.UninitializedType.Uninitialized);
         } else {
            ChatController.this.handleUninitializedType(ChatController.UninitializedType.Initialized);
            ChatController.this.handleString(String.format("Error shutting down Twith chat: %s", errorCode2));
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleErrorCode6(errorCode2);
            }
         } catch (Exception exception2) {
            ChatController.this.handleString(exception2.toString());
         }
      }

      public void chatEmoticonDataDownloadCallback(ErrorCode errorCode4) {
         if (ErrorCode.succeeded(errorCode4)) {
            ChatController.this.update4();
         }
      }
   };

   public void setText2(String text4) {
      this.text2 = text4;
   }

   protected void update3() {
      if (this.noneType2 != ChatController.NoneType.None && this.chatEmoticonData == null) {
         ErrorCode errorCode = this.chat.downloadEmoticonData();
         if (ErrorCode.failed(errorCode)) {
            String string = ErrorCode.getString(errorCode);
            this.handleString(String.format("Error trying to download emoticon data: %s", string));
         }
      }
   }

   public boolean isActive() {
      if (this.uninitializedType != ChatController.UninitializedType.Initialized) {
         return false;
      } else {
         ErrorCode errorCode = this.chat.shutdown();
         if (ErrorCode.failed(errorCode)) {
            String string = ErrorCode.getString(errorCode);
            this.handleString(String.format("Error shutting down chat: %s", string));
            return false;
         } else {
            this.update();
            this.handleUninitializedType(ChatController.UninitializedType.ShuttingDown);
            return true;
         }
      }
   }

   protected void handleString(String text4) {
      logger.error(TwitchStream.marker, "[Chat controller] {}", new Object[]{text4});
   }

   public boolean checkString(String text4) {
      if (this.uninitializedType != ChatController.UninitializedType.Initialized) {
         return false;
      } else if (!this.hashMap.containsKey(text4)) {
         this.handleString("Not in channel: " + text4);
         return false;
      } else {
         return this.hashMap.get(text4).isActive2();
      }
   }

   public boolean isActive2() {
      if (this.uninitializedType != ChatController.UninitializedType.Uninitialized) {
         return false;
      }

      this.handleUninitializedType(ChatController.UninitializedType.Initializing);
      ErrorCode errorCode = this.core.initialize(this.text2, (String)null);
      if (ErrorCode.failed(errorCode)) {
         this.handleUninitializedType(ChatController.UninitializedType.Uninitialized);
         String string = ErrorCode.getString(errorCode);
         this.handleString(String.format("Error initializing Twitch sdk: %s", string));
         return false;
      }

      this.noneType2 = this.noneType;
      HashSet hashSet = new HashSet();
      switch (this.noneType) {
         case None:
            hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE);
            break;
         case Url:
            hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS);
            break;
         case TextureAtlas:
            hashSet.add(ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES);
      }

      errorCode = this.chat.initialize(hashSet, this.iChatAPIListener);
      if (ErrorCode.failed(errorCode)) {
         this.core.shutdown();
         this.handleUninitializedType(ChatController.UninitializedType.Uninitialized);
         String string2 = ErrorCode.getString(errorCode);
         this.handleString(String.format("Error initializing Twitch chat: %s", string2));
         return false;
      } else {
         this.handleUninitializedType(ChatController.UninitializedType.Initialized);
         return true;
      }
   }

   public ChatController() {
      this.core = Core.getInstance();
      if (this.core == null) {
         this.core = new Core(new StandardCoreAPI());
      }

      this.chat = new Chat(new StandardChatAPI());
   }

   protected void update() {
      if (this.chatEmoticonData != null) {
         ErrorCode errorCode = this.chat.clearEmoticonData();
         if (ErrorCode.succeeded(errorCode)) {
            this.chatEmoticonData = null;

            try {
               if (this.contract != null) {
                  this.contract.update17();
               }
            } catch (Exception exception) {
               this.handleString(exception.toString());
            }
         } else {
            this.handleString("Error clearing emoticon data: " + ErrorCode.getString(errorCode));
         }
      }
   }

   public ChatController.CreatedType getCreatedType(String text4) {
      return !this.hashMap.containsKey(text4) ? ChatController.CreatedType.Disconnected : this.hashMap.get(text4).getCreatedType();
   }

   public void setContract(ChatController.Contract contract2) {
      this.contract = contract2;
   }

   protected void update4() {
      if (this.chatEmoticonData == null) {
         this.chatEmoticonData = new ChatEmoticonData();
         ErrorCode emoticonData = this.chat.getEmoticonData(this.chatEmoticonData);
         if (ErrorCode.succeeded(emoticonData)) {
            try {
               if (this.contract != null) {
                  this.contract.update14();
               }
            } catch (Exception exception) {
               this.handleString(exception.toString());
            }
         } else {
            this.handleString("Error preparing emoticon data: " + ErrorCode.getString(emoticonData));
         }
      }
   }

   public boolean checkString3(String text4) {
      return this.checkString2(text4);
   }

   public void update5() {
      if (this.getUninitializedType() != ChatController.UninitializedType.Uninitialized) {
         this.isActive();
         if (this.getUninitializedType() == ChatController.UninitializedType.ShuttingDown) {
            while (this.getUninitializedType() != ChatController.UninitializedType.Uninitialized) {
               try {
                  Thread.sleep(200L);
                  this.update2();
               } catch (InterruptedException interruptedException) {
               }
            }
         }
      }
   }

   protected boolean checkString2(String text4) {
      if (this.uninitializedType != ChatController.UninitializedType.Initialized) {
         return false;
      }

      if (this.hashMap.containsKey(text4)) {
         this.handleString("Already in channel: " + text4);
         return false;
      }

      if (text4 != null && !text4.equals("")) {
         ChatController.IChatChannelListenerImpl iChatChannelListenerImpl = new ChatController.IChatChannelListenerImpl(text4);
         this.hashMap.put(text4, iChatChannelListenerImpl);
         boolean active = iChatChannelListenerImpl.isActive();
         if (!active) {
            this.hashMap.remove(text4);
         }

         return active;
      } else {
         return false;
      }
   }

   public boolean checkString4(String text4) {
      return !this.hashMap.containsKey(text4) ? false : this.hashMap.get(text4).getCreatedType() == ChatController.CreatedType.Connected;
   }

   public boolean checkString5(String text4, String text5) {
      if (this.uninitializedType != ChatController.UninitializedType.Initialized) {
         return false;
      } else if (!this.hashMap.containsKey(text4)) {
         this.handleString("Not in channel: " + text4);
         return false;
      } else {
         return this.hashMap.get(text4).checkString(text5);
      }
   }

   public void setText(String text4) {
      this.text = text4;
   }

   public ChatController.UninitializedType getUninitializedType() {
      return this.uninitializedType;
   }

   protected void handleUninitializedType(ChatController.UninitializedType uninitializedType2) {
      if (uninitializedType2 != this.uninitializedType) {
         this.uninitializedType = uninitializedType2;

         try {
            if (this.contract != null) {
               this.contract.handleUninitializedType3(uninitializedType2);
            }
         } catch (Exception exception) {
            this.handleString(exception.toString());
         }
      }
   }

   public void setAuthToken(AuthToken authToken2) {
      this.authToken = authToken2;
   }

   public void update2() {
      if (this.uninitializedType != ChatController.UninitializedType.Uninitialized) {
         ErrorCode errorCode = this.chat.flushEvents();
         if (ErrorCode.failed(errorCode)) {
            String string = ErrorCode.getString(errorCode);
            this.handleString(String.format("Error flushing chat events: %s", string));
         }
      }
   }

   public interface Contract {
      void handleString5(String text4);

      void update14();

      void handleString11(String text4, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3);

      void handleString10(String text4, ChatTokenizedMessage[] chatTokenizedMessages);

      void update17();

      void handleString3(String text4);

      void handleString7(String text4, ChatRawMessage[] chatRawMessages);

      void handleUninitializedType3(ChatController.UninitializedType uninitializedType2);

      void handleString4(String text4, String text5);

      void handleErrorCode6(ErrorCode errorCode);

      void handleErrorCode5(ErrorCode errorCode);

      void handleString(String text4);

      void handleString6(String text4);
   }

   public enum CreatedType {
      Created,
      Connecting,
      Connected,
      Disconnecting,
      Disconnected;
   }

   public class IChatChannelListenerImpl implements IChatChannelListener {
      protected String text = null;
      protected boolean flag = false;
      protected ChatController.CreatedType createdType = ChatController.CreatedType.Created;
      protected List<ChatUserInfo> list = Lists.newArrayList();
      protected LinkedList<ChatRawMessage> linkedList = new LinkedList<>();
      protected LinkedList<ChatTokenizedMessage> linkedList2 = new LinkedList<>();
      protected ChatBadgeData chatBadgeData = null;

      protected void handleString2(String text4) {
         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString(text4);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }
      }

      public void chatClearCallback(String text4, String text5) {
         this.handleString3(text5);
      }

      public void chatBadgeDataDownloadCallback(String text4, ErrorCode errorCode) {
         if (ErrorCode.succeeded(errorCode)) {
            this.update4();
         }
      }

      public ChatController.CreatedType getCreatedType() {
         return this.createdType;
      }

      public boolean isActive() {
         this.flag = false;
         ErrorCode errorCode = ChatController.this.chat.connect(this.text, ChatController.this.text, ChatController.this.authToken.data, this);
         if (ErrorCode.failed(errorCode)) {
            String string = ErrorCode.getString(errorCode);
            ChatController.this.handleString(String.format("Error connecting: %s", string));
            this.handleString2(this.text);
            return false;
         } else {
            this.handleCreatedType(ChatController.CreatedType.Connecting);
            this.update2();
            return true;
         }
      }

      protected void update() {
         if (this.chatBadgeData != null) {
            ErrorCode errorCode = ChatController.this.chat.clearBadgeData(this.text);
            if (ErrorCode.succeeded(errorCode)) {
               this.chatBadgeData = null;

               try {
                  if (ChatController.this.contract != null) {
                     ChatController.this.contract.handleString3(this.text);
                  }
               } catch (Exception exception) {
                  ChatController.this.handleString(exception.toString());
               }
            } else {
               ChatController.this.handleString("Error releasing badge data: " + ErrorCode.getString(errorCode));
            }
         }
      }

      public IChatChannelListenerImpl(String text4) {
         this.text = text4;
      }

      private void update3() {
         if (this.createdType != ChatController.CreatedType.Disconnected) {
            this.handleCreatedType(ChatController.CreatedType.Disconnected);
            this.handleString2(this.text);
            this.update();
         }
      }

      public boolean checkString(String text4) {
         if (this.createdType != ChatController.CreatedType.Connected) {
            return false;
         } else {
            ErrorCode errorCode = ChatController.this.chat.sendMessage(this.text, text4);
            if (ErrorCode.failed(errorCode)) {
               String string = ErrorCode.getString(errorCode);
               ChatController.this.handleString(String.format("Error sending chat message: %s", string));
               return false;
            } else {
               return true;
            }
         }
      }

      protected void update2() {
         if (ChatController.this.noneType2 != ChatController.NoneType.None && this.chatBadgeData == null) {
            ErrorCode errorCode = ChatController.this.chat.downloadBadgeData(this.text);
            if (ErrorCode.failed(errorCode)) {
               String string = ErrorCode.getString(errorCode);
               ChatController.this.handleString(String.format("Error trying to download badge data: %s", string));
            }
         }
      }

      public boolean isActive2() {
         switch (this.createdType) {
            case Connected:
            case Connecting:
               ErrorCode errorCode = ChatController.this.chat.disconnect(this.text);
               if (ErrorCode.failed(errorCode)) {
                  String string = ErrorCode.getString(errorCode);
                  ChatController.this.handleString(String.format("Error disconnecting: %s", string));
                  return false;
               }

               this.handleCreatedType(ChatController.CreatedType.Disconnecting);
               return true;
            case Created:
            case Disconnected:
            case Disconnecting:
            default:
               return false;
         }
      }

      protected void handleString(String text4) {
         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString5(text4);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }
      }

      public void chatChannelMembershipCallback(String text4, ChatEvent chatEvent, ChatChannelInfo chatChannelInfo) {
         switch (chatEvent) {
            case TTV_CHAT_JOINED_CHANNEL:
               this.handleCreatedType(ChatController.CreatedType.Connected);
               this.handleString(text4);
               break;
            case TTV_CHAT_LEFT_CHANNEL:
               this.update3();
         }
      }

      public void chatChannelUserChangeCallback(String text4, ChatUserInfo[] chatUserInfos, ChatUserInfo[] chatUserInfos2, ChatUserInfo[] chatUserInfos3) {
         for (int i = 0; i < chatUserInfos2.length; i++) {
            int i2 = this.list.indexOf(chatUserInfos2[i]);
            if (i2 >= 0) {
               this.list.remove(i2);
            }
         }

         for (int i3 = 0; i3 < chatUserInfos3.length; i3++) {
            int i4 = this.list.indexOf(chatUserInfos3[i3]);
            if (i4 >= 0) {
               this.list.remove(i4);
            }

            this.list.add(chatUserInfos3[i3]);
         }

         for (int i5 = 0; i5 < chatUserInfos.length; i5++) {
            this.list.add(chatUserInfos[i5]);
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString11(this.text, chatUserInfos, chatUserInfos2, chatUserInfos3);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }
      }

      protected void update4() {
         if (this.chatBadgeData == null) {
            this.chatBadgeData = new ChatBadgeData();
            ErrorCode badgeData = ChatController.this.chat.getBadgeData(this.text, this.chatBadgeData);
            if (ErrorCode.succeeded(badgeData)) {
               try {
                  if (ChatController.this.contract != null) {
                     ChatController.this.contract.handleString6(this.text);
                  }
               } catch (Exception exception) {
                  ChatController.this.handleString(exception.toString());
               }
            } else {
               ChatController.this.handleString("Error preparing badge data: " + ErrorCode.getString(badgeData));
            }
         }
      }

      public void chatChannelTokenizedMessageCallback(String text4, ChatTokenizedMessage[] chatTokenizedMessages) {
         for (int i = 0; i < chatTokenizedMessages.length; i++) {
            this.linkedList2.addLast(chatTokenizedMessages[i]);
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString10(this.text, chatTokenizedMessages);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }

         while (this.linkedList2.size() > ChatController.this.intValue) {
            this.linkedList2.removeFirst();
         }
      }

      public void chatStatusCallback(String text4, ErrorCode errorCode) {
         if (!ErrorCode.succeeded(errorCode)) {
            ChatController.this.hashMap.remove(text4);
            this.update3();
         }
      }

      public void handleString3(String text4) {
         if (ChatController.this.noneType2 == ChatController.NoneType.None) {
            this.linkedList.clear();
            this.linkedList2.clear();
         } else {
            if (this.linkedList.size() > 0) {
               ListIterator listIterator2 = this.linkedList.listIterator();

               while (listIterator2.hasNext()) {
                  if (((ChatRawMessage)listIterator2.next()).userName.equals(text4)) {
                     listIterator2.remove();
                  }
               }
            }

            if (this.linkedList2.size() > 0) {
               ListIterator listIterator3 = this.linkedList2.listIterator();

               while (listIterator3.hasNext()) {
                  if (((ChatTokenizedMessage)listIterator3.next()).displayName.equals(text4)) {
                     listIterator3.remove();
                  }
               }
            }
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString4(this.text, text4);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }
      }

      public void chatChannelRawMessageCallback(String text4, ChatRawMessage[] chatRawMessages) {
         for (int i = 0; i < chatRawMessages.length; i++) {
            this.linkedList.addLast(chatRawMessages[i]);
         }

         try {
            if (ChatController.this.contract != null) {
               ChatController.this.contract.handleString7(this.text, chatRawMessages);
            }
         } catch (Exception exception) {
            ChatController.this.handleString(exception.toString());
         }

         while (this.linkedList.size() > ChatController.this.intValue) {
            this.linkedList.removeFirst();
         }
      }

      protected void handleCreatedType(ChatController.CreatedType createdType2) {
         if (createdType2 != this.createdType) {
            this.createdType = createdType2;
         }
      }
   }

   public enum NoneType {
      None,
      Url,
      TextureAtlas;
   }

   public enum UninitializedType {
      Uninitialized,
      Initializing,
      Initialized,
      ShuttingDown;
   }
}
