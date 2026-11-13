package wtf.novoline.script.adapter;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.utils.render.CFontRenderer;

public class CFontRendererTypeAdapter extends AbstractTypeAdapter<CFontRenderer> {
   public final BiConsumer<CFontRenderer, Map<String, Object>> biConsumer = (cFontRenderer, map) -> {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      Optional optionalEnum = scriptValue.getOptionalEnum("mode", CFontRendererTypeAdapter.RegularType.class);
      String string = scriptValue.getString("text");
      float floatValue = scriptValue.getFloat("x");
      float floatValue2 = scriptValue.getFloat("y");
      int intValue = scriptValue.getInt("color");
      optionalEnum.ifPresentOrElse((regularType) -> {
         switch (((CFontRendererTypeAdapter.RegularType) regularType)) {
            case REGULAR:
               cFontRenderer.drawString(string, floatValue, floatValue2, intValue);
               break;
            case WITH_SHADOW:
               cFontRenderer.drawStringWithShadow(string, floatValue, floatValue2, intValue);
               break;
            case CENTERED:
               cFontRenderer.drawCenteredString(string, floatValue, floatValue2, intValue);
               break;
            case CENTERED_WITH_SHADOW:
               cFontRenderer.drawCenteredStringWithShadow(string, floatValue, floatValue2, intValue);
               break;
            case NICE_SHADOW:
               cFontRenderer.drawStringWithNiceShadow(string, floatValue, floatValue2, intValue);
               break;
            case NICE_SHADOW_NO_COLOR:
               cFontRenderer.drawStringWithNiceShadowNoColor(string, floatValue, floatValue2, intValue);
               break;
            default:
               throw new IllegalArgumentException("Unsupported mode: " + optionalEnum);
         }
      }, () -> {
         throw new IllegalArgumentException("Unrecognized mode: " + scriptValue.getString("mode"));
      });
   };

   private Consumer<Map<String, Object>> createDrawString(CFontRenderer cFontRenderer2) {
      return map -> this.biConsumer.accept(cFontRenderer2, map);
   }

   public ScriptValue put(ScriptValue scriptValue, CFontRenderer cFontRenderer2) {
      scriptValue.putSupplier("getFont", cFontRenderer2::getFont);
      scriptValue.putSupplier("getHeight", cFontRenderer2::getHeight);
      scriptValue.putSupplier("isAntiAlias", cFontRenderer2::isFlag);
      scriptValue.putFunction("getStringWidth", cFontRenderer2::getStringWidth);
      scriptValue.putBiFunction("wrapWords", cFontRenderer2::wrapWords);
      scriptValue.putBiFunction("formatString", cFontRenderer2::formatString);
      scriptValue.putConsumer("drawString", this.createDrawString(cFontRenderer2));
      scriptValue.put("imgSize", cFontRenderer2.imgSize);
      scriptValue.put("tex", cFontRenderer2.dynamicTexture);
      return scriptValue;
   }

   public CFontRendererTypeAdapter(TypeRegistry typeRegistry) {
      super("CFontRenderer", typeRegistry);
   }

   public enum RegularType {
      REGULAR,
      WITH_SHADOW,
      CENTERED,
      CENTERED_WITH_SHADOW,
      NICE_SHADOW,
      NICE_SHADOW_NO_COLOR;
   }
}
