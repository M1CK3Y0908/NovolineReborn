package net.minecraft.client.stream;

import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public interface IStream {
   boolean isActive9();

   IStream.ErrorType getErrorType();

   void update6();

   void update13();

   void update();

   ChatUserInfo getChatUserInfo(String text);

   void handleMetadata(Metadata metadata);

   void update8();

   ErrorCode getErrorCode();

   boolean isActive3();

   boolean isActive2();

   IngestServer[] getArray();

   String getString();

   int getInt();

   boolean isActive();

   void handleMetadata2(Metadata metadata, long l);

   boolean isActive6();

   boolean isActive10();

   void update16();

   boolean isActive7();

   boolean isActive4();

   void update11();

   void update5();

   void update3();

   boolean isActive5();

   void update15();

   IngestServerTester getIngestServerTester();

   void handleBoolean(boolean flag);

   void update12();

   void handleString2(String text);

   enum ErrorType {
      ERROR,
      INVALID_TOKEN;
   }
}
