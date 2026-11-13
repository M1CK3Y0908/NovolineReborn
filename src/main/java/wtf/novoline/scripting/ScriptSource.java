package wtf.novoline.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public abstract class ScriptSource {
   private String resolvedImport;

   public abstract void resolve() throws IOException;

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ScriptSource scriptSource)) {
         return false;
      } else {
         if (!scriptSource.canEqual(this)) {
            return false;
         }

         String resolvedImport2 = this.getResolvedImport();
         String resolvedImport3 = scriptSource.getResolvedImport();
         return resolvedImport2 == null ? resolvedImport3 == null : resolvedImport2.equals(resolvedImport3);
      }
   }

   public void setResolvedImport(String text) {
      this.resolvedImport = text;
   }

   public static ScriptSource createFileSource(Path path2) {
      return new ScriptSource.ScriptSourceChild(path2);
   }

   @Override
   public String toString() {
      return "Import(resolvedImport=" + this.getResolvedImport() + ")";
   }

   public static ScriptSource createClasspathSource(String text) {
      return new ScriptSource.ScriptSourceChild2(text);
   }

   public boolean isResolved() {
      return this.resolvedImport != null;
   }

   protected boolean canEqual(Object object) {
      return object instanceof ScriptSource;
   }

   public String getResolvedImport() {
      return this.resolvedImport;
   }

   @Override
   public int hashCode() {
      String resolvedImport2 = this.getResolvedImport();
      return 59 + (resolvedImport2 == null ? 43 : resolvedImport2.hashCode());
   }

   static class ScriptSourceChild extends ScriptSource {
      private final Path path;

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptSource.ScriptSourceChild scriptSourceChild)) {
            return false;
         } else {
            if (!scriptSourceChild.canEqual(this)) {
               return false;
            }

            if (!super.equals(object)) {
               return false;
            }

            Path path2 = this.getPath();
            Path path3 = scriptSourceChild.getPath();
            return path2 == null ? path3 == null : path2.equals(path3);
         }
      }

      @Override
      public int hashCode() {
         int i = super.hashCode();
         Path path2 = this.getPath();
         return i * 59 + (path2 == null ? 43 : path2.hashCode());
      }

      @Override
      public void resolve() throws IOException {
         InputStream inputStream = Objects.requireNonNull(Files.newInputStream(this.path), "importStream");
         this.setResolvedImport(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
      }

      @Override
      public String toString() {
         return "file:" + this.path.getFileName();
      }

      public Path getPath() {
         return this.path;
      }

      @Override
      protected boolean canEqual(Object object) {
         return object instanceof ScriptSource.ScriptSourceChild;
      }

      public ScriptSourceChild(Path path2) {
         this.path = path2;
      }
   }

   static class ScriptSourceChild2 extends ScriptSource {
      private final String resourceName;

      public String getResourceName() {
         return this.resourceName;
      }

      @Override
      public String toString() {
         return "classpath:" + this.resourceName;
      }

      @Override
      public void resolve() throws IOException {
         ResourceLocation resourceLocation = new ResourceLocation("novoline", "scripts/" + this.resourceName);
         InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
         this.setResolvedImport(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
      }

      public ScriptSourceChild2(String text) {
         this.resourceName = text;
      }

      @Override
      public int hashCode() {
         int i = super.hashCode();
         String resourceName2 = this.getResourceName();
         return i * 59 + (resourceName2 == null ? 43 : resourceName2.hashCode());
      }

      @Override
      protected boolean canEqual(Object object) {
         return object instanceof ScriptSource.ScriptSourceChild2;
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptSource.ScriptSourceChild2 scriptSourceChild2)) {
            return false;
         } else {
            if (!scriptSourceChild2.canEqual(this)) {
               return false;
            }

            if (!super.equals(object)) {
               return false;
            }

            String resourceName2 = this.getResourceName();
            String resourceName3 = scriptSourceChild2.getResourceName();
            return resourceName2 == null ? resourceName3 == null : resourceName2.equals(resourceName3);
         }
      }
   }
}
