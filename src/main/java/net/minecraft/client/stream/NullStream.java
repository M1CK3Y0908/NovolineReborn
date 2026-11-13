package net.minecraft.client.stream;

import net.minecraft.client.stream.IStream.ErrorType;
import tv.twitch.ErrorCode;
import tv.twitch.broadcast.IngestServer;
import tv.twitch.chat.ChatUserInfo;

public class NullStream implements IStream {
   private final Throwable throwable;

   @Override
   public boolean isActive7() {
      return false;
   }

   @Override
   public boolean isActive4() {
      return false;
   }

   @Override
   public boolean isActive6() {
      return false;
   }

   @Override
   public void handleBoolean(boolean flag) {
   }

   @Override
   public void update() {
   }

   @Override
   public void update5() {
   }

   @Override
   public IngestServerTester getIngestServerTester() {
      return null;
   }

   @Override
   public void update3() {
   }

   @Override
   public boolean isActive2() {
      return false;
   }

   @Override
   public void update15() {
   }

   @Override
   public boolean isActive5() {
      return false;
   }

   @Override
   public void update13() {
   }

   @Override
   public ErrorType getErrorType() {
      return ErrorType.ERROR;
   }

   @Override
   public IngestServer[] getArray() {
      return new IngestServer[0];
   }

   @Override
   public void update12() {
   }

   @Override
   public void handleMetadata(Metadata metadata) {
   }

   @Override
   public boolean isActive() {
      return false;
   }

   @Override
   public boolean isActive10() {
      return false;
   }

   @Override
   public int getInt() {
      return 0;
   }

   @Override
   public void update16() {
   }

   @Override
   public void handleMetadata2(Metadata metadata, long l) {
   }

   @Override
   public boolean isActive3() {
      return false;
   }

   @Override
   public boolean isActive9() {
      return false;
   }

   @Override
   public void update6() {
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   @Override
   public void handleString2(String text) {
   }

   public NullStream(Throwable throwable2) {
      this.throwable = throwable2;
   }

   @Override
   public void update8() {
   }

   @Override
   public void update11() {
   }

   @Override
   public ErrorCode getErrorCode() {
      return null;
   }

   @Override
   public ChatUserInfo getChatUserInfo(String text) {
      return null;
   }

   @Override
   public String getString() {
      return null;
   }
}
