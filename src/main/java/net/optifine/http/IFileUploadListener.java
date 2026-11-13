package net.optifine.http;

public interface IFileUploadListener {
   void fileUploadFinished(String text, byte[] bArray, Throwable throwable);
}
