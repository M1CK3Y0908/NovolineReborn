package net.optifine.http;

public interface IFileDownloadListener {
   void fileDownloadFinished(String text, byte[] bArray, Throwable throwable);
}
