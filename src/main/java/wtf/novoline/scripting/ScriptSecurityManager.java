package wtf.novoline.scripting;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Permission;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.objectweb.asm.Type;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.utils.ReflectionUtils;
import wtf.novoline.utils.ReflectionUtils.Contract;

public class ScriptSecurityManager extends SecurityManager {
   private static final String ORG_OPENJDK_NASHORN_INTERNAL_SCRIPTS_JS = "org.openjdk.nashorn.internal.scripts.JS";
   private static final StackWalker stackWalker = StackWalker.getInstance(
      Set.of(StackWalker.Option.RETAIN_CLASS_REFERENCE, StackWalker.Option.SHOW_HIDDEN_FRAMES)
   );
   private static final Method[] methodArray = new Method[]{
      ReflectionUtils.lookupContractMethod(ScriptSecurityManager.class, "94395de167d6b46b9f079d7c5ecfef9e")
   };
   private static final SecurityException securityException = new SecurityException(
      "A script has attempted to run code it's not supposed to. If you believe that that's an issue, file a bug report."
   );

   @Override
   public void checkCreateClassLoader() {
   }

   @Override
   public void checkRead(String text, Object object) {
      if (this.isCalledFromScript() && this.isForbiddenPath(text)) {
         throw securityException;
      }
   }

   @Override
   public void checkDelete(String text) {
      if (this.isCalledFromScript() && this.isForbiddenPath(text)) {
         throw securityException;
      }
   }

   private boolean isTrustedFrame(StackWalker.StackFrame stackFrame) {
      String className = stackFrame.getClassName();

      for (Method method : methodArray) {
         if (stackFrame.getClassName().equals(method.getDeclaringClass().getName())
            && stackFrame.getMethodName().equals(method.getName())
            && Type.getMethodDescriptor(method).equals(stackFrame.getMethodType().descriptorString())) {
            return true;
         }
      }

      return className.equals("jdk.internal.loader.Resource") || className.equals("jdk.internal.loader.URLClassPath");
   }

   @Override
   public void checkExec(String text) {
      if (this.isCalledFromScript()) {
         throw securityException;
      }
   }

   private boolean isCalledFromScript() {
      List<StackWalker.StackFrame> list = stackWalker.walk(Stream::toList);
      return list.stream().noneMatch(this::isTrustedFrame) && list.stream().anyMatch(this::isScriptFrame);
   }

   @Contract("94395de167d6b46b9f079d7c5ecfef9e")
   private boolean isForbiddenPath(String text) {
      Path path = Paths.get(text).normalize().toAbsolutePath();
      return Files.isSymbolicLink(path) || !path.startsWith(Directories.ROOT_DIR) || path.equals(Directories.STORAGE_DIR.resolve("alts.novo"));
   }

   @Override
   public void checkWrite(String text) {
      if (this.isCalledFromScript() && this.isForbiddenPath(text)) {
         throw securityException;
      }
   }

   @Override
   public void checkPermission(Permission permission, Object object) {
   }

   @Override
   public void checkPermission(Permission permission) {
   }

   @Override
   public void checkRead(String text) {
      if (this.isCalledFromScript() && this.isForbiddenPath(text)) {
         throw securityException;
      }
   }

   private boolean isScriptFrame(StackWalker.StackFrame stackFrame) {
      Class superclass = stackFrame.getDeclaringClass().getSuperclass();
      return superclass != null && superclass.getName().equals("org.openjdk.nashorn.internal.scripts.JS");
   }
}
