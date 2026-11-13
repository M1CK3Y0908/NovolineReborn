package wtf.novoline.scripting;

import java.util.Set;
import org.openjdk.nashorn.api.scripting.ClassFilter;

public class ScriptClassFilter implements ClassFilter {
   private static final Set<String> allowedPrefixes = Set.of(
      "java.time.",
      "java.net.http.",
      "java.nio.",
      "java.util.UUID",
      "org.lwjgl.opengl.",
      "org.lwjgl.input.",
      "io.netty.buffer.Unpooled",
      "java.util.concurrent.ThreadLocalRandom",
      "java.lang.Float"
   );

   @Override
   public boolean exposeToScripts(String text) {
      return allowedPrefixes.stream().anyMatch(text::startsWith);
   }
}
