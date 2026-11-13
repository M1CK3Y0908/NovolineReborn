package wtf.novoline.script.adapter;

import net.minecraft.client.gui.FontRenderer;
import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;

public final class FontRendererTypeAdapter extends AbstractTypeAdapter<FontRenderer> {
   public ScriptValue put(ScriptValue scriptValue, FontRenderer fontRenderer) {
      scriptValue.putFunction4("drawString", fontRenderer::getInt);
      scriptValue.putFunction4("drawStringWithShadow", fontRenderer::drawStringWithShadow);
      scriptValue.putFunction("getStringWidth", fontRenderer::getInt3);
      scriptValue.putBiFunction("listFormattedStringToWidth", fontRenderer::listFormattedStringToWidth);
      scriptValue.putBiFunction("trimStringToWidth", (object, object2) -> fontRenderer.trimStringToWidth((String)object, (Integer)object2, false));
      scriptValue.putBiFunction("trimStringToWidthReverse", (object, object2) -> fontRenderer.trimStringToWidth((String)object, (Integer)object2, true));
      scriptValue.putFunction("getCharWidth", fontRenderer::getCharWidth);
      scriptValue.putFunction("getColorCode", fontRenderer::getColorCode);
      scriptValue.putSupplier("getUnicodeFlag", fontRenderer::getUnicodeFlag);
      return scriptValue;
   }

   public FontRendererTypeAdapter(TypeRegistry typeRegistry) {
      super("FontRenderer", typeRegistry);
   }
}
