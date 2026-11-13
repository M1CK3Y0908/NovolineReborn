package wtf.novoline.script.adapter;

import wtf.novoline.script.AbstractTypeAdapter;
import wtf.novoline.script.MappingUtils;
import wtf.novoline.script.ScriptValue;
import wtf.novoline.script.TypeRegistry;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.animation.Easing;
import java.util.Map;

public class AnimationTypeAdapter extends AbstractTypeAdapter<Animation> {
   public ScriptValue put(ScriptValue scriptValue, Animation animation2) {
      scriptValue.putConsumer("start", map -> {
         ScriptValue scriptValue2 = ScriptValue.createScriptValue((Map) map);
         double doubleValue = scriptValue2.getDouble("start");
         double doubleValue2 = scriptValue2.getDouble("end");
         long longValue = scriptValue2.getLong("duration");

         Easing easing = switch (scriptValue2.getString("type")) {
            case "linear" -> Easing.linear;
            case "quad_in" -> Easing.quadIn;
            case "quad_out" -> Easing.quadOut;
            case "quad_in_out" -> Easing.quadInOut;
            case "cubic_in" -> Easing.cubicIn;
            case "cubic_out" -> Easing.cubicOut;
            case "cubic_in_out" -> Easing.cubicInOut;
            case "quartic_in" -> Easing.quarticIn;
            case "quartic_out" -> Easing.quarticOut;
            case "quartic_in_out" -> Easing.quarticInOut;
            case "quintic_in" -> Easing.quinticIn;
            case "quintic_out" -> Easing.quinticOut;
            case "quintic_in_out" -> Easing.quinticInOut;
            case "sine_in" -> Easing.sineIn;
            case "sine_out" -> Easing.sineOut;
            case "sine_in_out" -> Easing.sineInOut;
            case "expo_in" -> Easing.expoIn;
            case "expo_out" -> Easing.expoOut;
            case "expo_in_out" -> Easing.expoInOut;
            case "circ_in" -> Easing.circIn;
            case "circ_out" -> Easing.circOut;
            case "circ_in_out" -> Easing.circInOut;
            case "elastic_in" -> Easing.elasticIn;
            case "elastic_out" -> Easing.elasticOut;
            case "elastic_in_out" -> Easing.elasticInOut;
            case "back_in" -> Easing.backIn;
            case "back_out" -> Easing.backOut;
            case "back_in_out" -> Easing.backInOut;
            case "bounce_in" -> Easing.bounceIn;
            case "bounce_out" -> Easing.bounceOut;
            case "bounce_in_out" -> Easing.bounceInOut;
            default -> throw new IllegalArgumentException("Unrecognized animation type: " + scriptValue2.getString("type"));
         };
         if (scriptValue.containsKey("delay")) {
            animation2.start(doubleValue, doubleValue2, longValue, scriptValue.getLong("delay"), easing);
         } else {
            animation2.start(doubleValue, doubleValue2, longValue, easing);
         }
      });
      scriptValue.putRunnable("stop", animation2::stop);
      scriptValue.putRunnable("unlock", animation2::unlock);
      scriptValue.putRunnable("restart", animation2::restart);
      scriptValue.putRunnable("pause", animation2::pause);
      scriptValue.putRunnable("resume", animation2::resume);
      scriptValue.putSupplier("update", MappingUtils.mappingSupplier(Animation.class, animation2::update));
      scriptValue.putRunnable("flip", animation2::flip);
      scriptValue.putConsumer("setFlipped", animation2::setFlipped);
      scriptValue.putSupplier("hasFinished", animation2::hasFinished);
      scriptValue.putSupplier("isAnimating", animation2::isAnimating);
      scriptValue.putSupplier("isPaused", animation2::isPaused);
      scriptValue.putSupplier("getStartTime", animation2::getStartTime);
      scriptValue.putSupplier("getStart", animation2::getStart);
      scriptValue.putSupplier("getEnd", animation2::getEnd);
      scriptValue.putSupplier("getValue", animation2::getValue);
      scriptValue.putSupplier("getDuration", animation2::getDuration);
      scriptValue.putSupplier("getDelay", animation2::getDelay);
      return scriptValue;
   }

   public AnimationTypeAdapter(TypeRegistry typeRegistry) {
      super("Transition", typeRegistry);
   }
}
