package wtf.novoline.utils.proxy;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class ProxyList extends ArrayList<ProxyEntry> {
   @SerializedName("current")
   private ProxyEntry current;
   @SerializedName("enabled")
   private boolean enabled;

   public void setEnabled(boolean flag) {
      this.enabled = flag;
   }

   public void setCurrent(ProxyEntry proxyEntry) {
      this.current = proxyEntry;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public ProxyEntry getCurrent() {
      return this.current;
   }
}
