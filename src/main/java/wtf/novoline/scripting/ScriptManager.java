package wtf.novoline.scripting;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.tinylog.Logger;
import ru.fals3r.aal.API;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.api.Updatable;
import wtf.novoline.scripting.ScriptContext.Context;

public class ScriptManager extends CopyOnWriteArrayList<Script> {
   public static final String VALUE_2_0_0 = "2.0.0";
   private static final List<ScriptSource> builtinImports = List.of(
      ScriptSource.createClasspathSource("macros.js"),
      ScriptSource.createClasspathSource("minecraft_api.js"),
      ScriptSource.createClasspathSource("rendering_api.js"),
      ScriptSource.createClasspathSource("client_api.js")
   );
   private final ScriptEngine scriptEngine = new NashornScriptEngineFactory()
      .getScriptEngine(new String[]{"--language=es6"}, API.getClassLoader(), new ScriptClassFilter());
   private ScriptWatcherThread scriptWatcherThread;
   private final ScriptContext scriptContext = new ScriptContext();

   public ScriptWatcherThread getScriptWatcherThread() {
      return this.scriptWatcherThread;
   }

   public ScriptManager() {
      this.startWatcher();
      this.installSecurityManager();
   }

   public Script getScript(Path path2) throws IOException {
      List allLines = Files.readAllLines(path2);
      ScriptManager.HeaderData headerData = ScriptManager.HeaderParser.parseScript(allLines);
      return Script.builder().code(String.join("\n", allLines)).imports(headerData.getImports()).metadata(headerData.getMetadata()).path(path2).build();
   }

   public ScriptContext getScriptContext() {
      return this.scriptContext;
   }

   public ScriptEngine getScriptEngine() {
      return this.scriptEngine;
   }

   public void loadScript(Script script2) throws ScriptException {
      this.loadScript(script2, true);
   }

   private void installSecurityManager() {
      System.setSecurityManager(new ScriptSecurityManager());
   }

   public void unloadScript(Script script2) {
      List<wtf.novoline.api.Updatable> operations = script2.getOperations();
      if (operations != null) {
         operations.forEach(Updatable::unregister);
      }

      script2.setLoadingStatus(ScriptLoadState.unloaded());
   }

   public void loadScript(Script script2, boolean flag) throws ScriptException {
      script2.setLoadingStatus(ScriptLoadState.unloaded());
      if (flag) {
         this.add(script2);
      }

      for (ScriptSource scriptSource : script2.getImports()) {
         if (!scriptSource.isResolved()) {
            try {
               scriptSource.resolve();
            } catch (IOException ioException) {
               throw new ScriptException("Unable to resolve import " + scriptSource);
            }
         }
      }

      StringBuilder stringBuilder = new StringBuilder();

      for (ScriptSource scriptSource2 : script2.getImports()) {
         stringBuilder.append(scriptSource2.getResolvedImport()).append("\n");
      }

      stringBuilder.append(script2.getCode());
      this.scriptContext.enterContext(Context.createContext(script2));

      try {
         Bindings bindings = this.scriptEngine.createBindings();
         bindings.put("$interop", this.scriptContext);
         this.scriptEngine.eval(stringBuilder.toString(), bindings);
         script2.setLoadingStatus(ScriptLoadState.loaded());
      } catch (Throwable throwable) {
         this.unloadScript(script2);
         script2.setLoadingStatus(ScriptLoadState.failed(throwable));
         this.scriptContext.exitContext();
         throw new RuntimeException("Unable to initialize script " + script2.getDisplayName(), throwable);
      }

      this.scriptContext.exitContext();
   }

   private void startWatcher() {
      try {
         WatchService watchService = FileSystems.getDefault().newWatchService();
         Directories.SCRIPTS_DIR
            .register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
         (this.scriptWatcherThread = new ScriptWatcherThread(watchService, this)).start();
         Logger.info("Initialized WatcherThread");
      } catch (IOException ioException) {
         Logger.error(ioException, "Unable to activate WatchService");
      }
   }

   static final class HeaderData {
      private List<ScriptSource> imports = new ArrayList<>();
      private ScriptInfo metadata;

      public void setImports(List<ScriptSource> list) {
         this.imports = list;
      }

      public List<ScriptSource> getImports() {
         return this.imports;
      }

      private HeaderData() {
      }

      @Override
      public String toString() {
         return "ScriptManager.Parser.ParserData(imports=" + this.getImports() + ", metadata=" + this.getMetadata() + ")";
      }

      @Override
      public int hashCode() {
         List imports2 = this.getImports();
         int i = 59 + (imports2 == null ? 43 : imports2.hashCode());
         ScriptInfo metadata2 = this.getMetadata();
         return i * 59 + (metadata2 == null ? 43 : metadata2.hashCode());
      }

      public static ScriptManager.HeaderData create() {
         return new ScriptManager.HeaderData();
      }

      @Override
      public boolean equals(Object object) {
         if (object == this) {
            return true;
         } else if (!(object instanceof ScriptManager.HeaderData headerData)) {
            return false;
         } else {
            List imports2 = this.getImports();
            List imports3 = headerData.getImports();
            if (imports2 == null ? imports3 == null : imports2.equals(imports3)) {
               ScriptInfo metadata2 = this.getMetadata();
               ScriptInfo metadata3 = headerData.getMetadata();
               return metadata2 == null ? metadata3 == null : metadata2.equals(metadata3);
            } else {
               return false;
            }
         }
      }

      public void setMetadata(ScriptInfo scriptInfo) {
         this.metadata = scriptInfo;
      }

      public ScriptInfo getMetadata() {
         return this.metadata;
      }
   }

   record HeaderEntry(String key, String value) {
      public String getValue() {
         return this.value;
      }

      public String getKey() {
         return this.key;
      }
   }

   static final class HeaderParser {
      private static final String IMPORTS = "imports";
      private static final String SCRIPT_INFO = "script_info";
      private static final Pattern pattern = Pattern.compile("///([\\w]*)=\\{(.*)}");
      private static final Pattern pattern2 = Pattern.compile("(\\w+)=(.*)");

      public static ScriptManager.HeaderData parseScript(Iterable iterable) {
         ScriptManager.HeaderData headerData = ScriptManager.HeaderData.create();
         headerData.getImports().addAll(ScriptManager.builtinImports);
         Iterator iterator2 = iterable.iterator();

         while (iterator2.hasNext()) {
            Matcher matcher3 = createMatcher(((String)iterator2.next()).trim());
            if (matcher3.matches()) {
               ScriptManager.HeaderEntry headerEntry = createHeaderEntry(matcher3);
               switch (headerEntry.getKey()) {
                  case "script_info":
                     String[] texts = headerEntry.getValue().split(", ");
                     Map map2 = Arrays.stream(texts)
                        .map(pattern2::matcher)
                        .filter(Matcher::matches)
                        .collect(Collectors.toMap(matcher2 -> matcher2.group(1), matcher2 -> matcher2.group(2)));
                     if (!map2.containsKey("author") || !map2.containsKey("name") || !map2.containsKey("description") || !map2.containsKey("version")) {
                        throw new IllegalArgumentException("Parameters don't contain the required arguments: author, name, description, version");
                     }

                     headerData.setMetadata(
                        ScriptInfo.builder()
                           .author((String)map2.get("author"))
                           .name((String)map2.get("name"))
                           .description((String)map2.get("description"))
                           .version((String)map2.get("version"))
                           .build()
                     );
                     break;
                  case "imports":
                     String[] texts2 = headerEntry.getValue().replace(" ", "").split(",");
                     ArrayList arrayList = new ArrayList();

                     for (String text : texts2) {
                        ScriptSource scriptSource;
                        if (text.startsWith("classpath:")) {
                           text = text.substring("classpath:".length());
                           if (text.contains("..")) {
                              throw new IllegalArgumentException("Relative paths not supported");
                           }

                           scriptSource = ScriptSource.createClasspathSource(text);
                        } else {
                           Path path2 = Paths.get(text).normalize();
                           scriptSource = ScriptSource.createFileSource(Directories.SCRIPTS_DIR.resolve(path2));
                        }

                        arrayList.add(scriptSource);
                     }

                     headerData.getImports().addAll(arrayList);
               }
            }
         }

         return headerData;
      }

      private static Matcher createMatcher(CharSequence charSequence) {
         return pattern.matcher(charSequence);
      }

      private static ScriptManager.HeaderEntry createHeaderEntry(Matcher matcher3) {
         if (matcher3.matches()) {
            return new ScriptManager.HeaderEntry(matcher3.group(1), matcher3.group(2));
         } else {
            throw new IllegalArgumentException("Invalid variable declaration");
         }
      }
   }
}
