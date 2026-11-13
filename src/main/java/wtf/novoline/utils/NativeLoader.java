package wtf.novoline.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLoader {
   private static final Path path;

   public static Path loadLibrary(String text) throws IOException, UnsatisfiedLinkError {
      Path path2 = extract(text);
      System.load(path2.toAbsolutePath().toString());
      return path2;
   }

   public static Path extract(String text) throws IOException {
      String text2 = text;
      if (text.lastIndexOf("/") > 0) {
         text2 = text.substring(text.lastIndexOf("/"));
      }

      if (text2.startsWith("/")) {
         text2 = text2.substring(1);
      }

      Path file2 = Files.createFile(path.resolve(text2));

      try (InputStream resourceAsStream = NativeLoader.class.getResourceAsStream(text)) {
         if (resourceAsStream == null) {
            throw new IllegalArgumentException("Couldn't locate " + text + " in the classpath");
         }

         Files.copy(resourceAsStream, file2, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException ioException) {
         Files.deleteIfExists(file2);
         throw ioException;
      } finally {
         file2.toFile().deleteOnExit();
      }

      return file2;
   }

   static {
      try {
         Path tempDirectory = Files.createTempDirectory("native-utils");
         tempDirectory.toFile().deleteOnExit();
         path = tempDirectory.toAbsolutePath();
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      }
   }
}
