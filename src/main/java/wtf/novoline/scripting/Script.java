package wtf.novoline.scripting;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import wtf.novoline.api.Updatable;

public class Script {
   private final String internalId = RandomStringUtils.randomAlphanumeric(32);
   private final List<Updatable> operations = new ArrayList<>();
   private ScriptLoadState loadingStatus;
   private final Path path;
   private String code;
   private final ScriptInfo metadata;
   private final List<ScriptSource> imports;

   @Override
   public int hashCode() {
      String internalId2 = this.getInternalId();
      int i = 59 + (internalId2 == null ? 43 : internalId2.hashCode());
      ScriptLoadState loadingStatus2 = this.getLoadingStatus();
      i = i * 59 + (loadingStatus2 == null ? 43 : loadingStatus2.hashCode());
      Path path2 = this.getPath();
      i = i * 59 + (path2 == null ? 43 : path2.hashCode());
      String code2 = this.getCode();
      i = i * 59 + (code2 == null ? 43 : code2.hashCode());
      ScriptInfo metadata2 = this.getMetadata();
      i = i * 59 + (metadata2 == null ? 43 : metadata2.hashCode());
      List imports2 = this.getImports();
      i = i * 59 + (imports2 == null ? 43 : imports2.hashCode());
      List operations2 = this.getOperations();
      return i * 59 + (operations2 == null ? 43 : operations2.hashCode());
   }

   public void addOperation(Updatable updatable) {
      this.operations.add(updatable);
      updatable.register();
   }

   public ScriptLoadState getLoadingStatus() {
      return this.loadingStatus;
   }

   public ScriptInfo getMetadata() {
      return this.metadata;
   }

   public void setLoadingStatus(ScriptLoadState scriptLoadState) {
      this.loadingStatus = scriptLoadState;
   }

   public List<ScriptSource> getImports() {
      return this.imports;
   }

   public String getInternalId() {
      return this.internalId;
   }

   @Override
   public String toString() {
      return "Script(internalId="
         + this.getInternalId()
         + ", loadingStatus="
         + this.getLoadingStatus()
         + ", path="
         + this.getPath()
         + ", code="
         + this.getCode()
         + ", metadata="
         + this.getMetadata()
         + ", imports="
         + this.getImports()
         + ", operations="
         + this.getOperations()
         + ")";
   }

   public List<Updatable> getOperations() {
      return this.operations;
   }

   public String getCode() {
      return this.code;
   }

   public Path getPath() {
      return this.path;
   }

   protected boolean canEqual(Object object) {
      return object instanceof Script;
   }

   Script(ScriptLoadState scriptLoadState, Path path2, String text, ScriptInfo scriptInfo, List<ScriptSource> list) {
      this.loadingStatus = scriptLoadState;
      this.path = path2;
      this.code = text;
      this.metadata = scriptInfo;
      this.imports = list;
   }

   public void setCode(String text) {
      this.code = text;
   }

   public static Script.ScriptBuilder builder() {
      return new Script.ScriptBuilder();
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof Script script)) {
         return false;
      } else {
         if (!script.canEqual(this)) {
            return false;
         }

         String internalId2 = this.getInternalId();
         String internalId3 = script.getInternalId();
         if (internalId2 == null ? internalId3 == null : internalId2.equals(internalId3)) {
            ScriptLoadState loadingStatus2 = this.getLoadingStatus();
            ScriptLoadState loadingStatus3 = script.getLoadingStatus();
            if (loadingStatus2 == null ? loadingStatus3 == null : loadingStatus2.equals(loadingStatus3)) {
               Path path2 = this.getPath();
               Path path3 = script.getPath();
               if (path2 == null ? path3 == null : path2.equals(path3)) {
                  String code2 = this.getCode();
                  String code3 = script.getCode();
                  if (code2 == null ? code3 == null : code2.equals(code3)) {
                     ScriptInfo metadata2 = this.getMetadata();
                     ScriptInfo metadata3 = script.getMetadata();
                     if (metadata2 == null ? metadata3 == null : metadata2.equals(metadata3)) {
                        List imports2 = this.getImports();
                        List imports3 = script.getImports();
                        if (imports2 == null ? imports3 == null : imports2.equals(imports3)) {
                           List operations2 = this.getOperations();
                           List operations3 = script.getOperations();
                           return operations2 == null ? operations3 == null : operations2.equals(operations3);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public String getDisplayName() {
      return this.metadata == null ? this.path.getFileName().toString() : this.metadata.getName();
   }

   public static class ScriptBuilder {
      private Path path;
      private String code;
      private List<ScriptSource> imports;
      private ScriptLoadState loadingStatus;
      private ScriptInfo metadata;

      public Script.ScriptBuilder path(Path path2) {
         this.path = path2;
         return this;
      }

      public Script.ScriptBuilder metadata(ScriptInfo scriptInfo) {
         this.metadata = scriptInfo;
         return this;
      }

      ScriptBuilder() {
      }

      public Script.ScriptBuilder loadingStatus(ScriptLoadState scriptLoadState) {
         this.loadingStatus = scriptLoadState;
         return this;
      }

      public Script.ScriptBuilder code(String text) {
         this.code = text;
         return this;
      }

      @Override
      public String toString() {
         return "Script.ScriptBuilder(loadingStatus="
            + this.loadingStatus
            + ", path="
            + this.path
            + ", code="
            + this.code
            + ", metadata="
            + this.metadata
            + ", imports="
            + this.imports
            + ")";
      }

      public Script.ScriptBuilder imports(List<ScriptSource> list) {
         this.imports = list;
         return this;
      }

      public Script build() {
         return new Script(this.loadingStatus, this.path, this.code, this.metadata, this.imports);
      }
   }
}
