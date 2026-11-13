package wtf.novoline.platform;

import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.libs.gson.JsonObject;

public class ViaInjectorImpl implements ViaInjector {
   @Override
   public String getDecoderName() {
      return "via-decoder";
   }

   @Override
   public void inject() {
   }

   @Override
   public void uninject() {
   }

   @Override
   public String getEncoderName() {
      return "via-encoder";
   }

   @Override
   public JsonObject getDump() {
      return new JsonObject();
   }

   @Override
   public int getServerProtocolVersion() {
      return 47;
   }
}
