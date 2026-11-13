package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.utils.render.PolygonRenderer;

public class PolygonRendererTypeAdapter extends AbstractTypeAdapter<PolygonRenderer> {
   public PolygonRendererTypeAdapter(TypeRegistry typeRegistry) {
      super("PolygonRenderer", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, PolygonRenderer polygonRenderer) {
      scriptValue.putConsumer6(
         "putRoundedRect",
         (Double d, Double d2, Double d3, Double d4, Double d5, Integer integer) -> polygonRenderer.putRoundedRect(
            d.floatValue(), d2.floatValue(), d3.floatValue(), d4.floatValue(), d5.floatValue(), integer
         )
      );
      scriptValue.putConsumer9(
         "putCustomRoundedRect",
         (Double d, Double d2, Double d3, Double d4, Double d5, Double d6, Double d7, Double d8, Integer integer) -> polygonRenderer.putCustomRoundedRect(
            d.floatValue(),
            d2.floatValue(),
            d3.floatValue(),
            d4.floatValue(),
            d5.floatValue(),
            d6.floatValue(),
            d7.floatValue(),
            d8.floatValue(),
            integer
         )
      );
      scriptValue.putConsumer7(
         "putOctagon",
         (Double d, Double d2, Double d3, Double d4, Double d5, Double d6, Integer integer) -> polygonRenderer.putOctagon(
            d.floatValue(), d2.floatValue(), d3.floatValue(), d4.floatValue(), d5.floatValue(), d6.floatValue(), integer
         )
      );
      scriptValue.putRunnable("draw", polygonRenderer::draw);
      return scriptValue;
   }
}
