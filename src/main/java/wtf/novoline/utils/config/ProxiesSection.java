package wtf.novoline.utils.config;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import wtf.novoline.Novoline;
import wtf.novoline.utils.proxy.ProxyEntry;
import wtf.novoline.utils.proxy.ProxyList;

public class ProxiesSection extends ConfigSection {
   public ProxiesSection() {
      super("proxies");
   }

   private ProxyList getProxyList() {
      return Novoline.getNovoline().getProxyList();
   }

   @Override
   public JsonObject serialize() {
      ProxyList proxyList = this.getProxyList();
      JsonObject jsonObject = new JsonObject();
      jsonObject.add("list", gson.toJsonTree(proxyList));
      jsonObject.add("current", gson.toJsonTree(proxyList.getCurrent()));
      jsonObject.addProperty("enabled", proxyList.isEnabled());
      return jsonObject;
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      ProxyList proxyList = this.getProxyList();
      if (jsonObject.has("list")) {
         TypeToken typeToken = new TypeToken<ArrayList<ProxyEntry>>() {};
         proxyList.addAll(gson.fromJson(jsonObject.get("list").getAsJsonArray(), typeToken.getType()));
      }

      if (jsonObject.has("current")) {
         proxyList.setCurrent(gson.fromJson(jsonObject.get("current"), ProxyEntry.class));
      }

      if (jsonObject.has("enabled")) {
         proxyList.setEnabled(jsonObject.get("enabled").getAsBoolean());
      }
   }
}
