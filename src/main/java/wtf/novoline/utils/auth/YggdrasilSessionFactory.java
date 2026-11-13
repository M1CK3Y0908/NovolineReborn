package wtf.novoline.utils.auth;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;
import net.minecraft.util.Session;
import wtf.novoline.api.Contract4;
import wtf.novoline.lang.AuthException;

public class YggdrasilSessionFactory implements Contract4 {
   private YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");

   public void setProxy(Proxy proxy) {
      this.yggdrasilAuthenticationService = new YggdrasilAuthenticationService(proxy, null);
   }

   public Session authenticate(String text, String text2) throws AuthException {
      try {
         UserAuthentication userAuthentication = this.yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
         userAuthentication.setUsername(text);
         userAuthentication.setPassword(text2);
         userAuthentication.logIn();
         GameProfile selectedProfile = userAuthentication.getSelectedProfile();
         return new Session(selectedProfile.getName(), selectedProfile.getId().toString(), userAuthentication.getAuthenticatedToken(), "MOJANG");
      } catch (Throwable throwable) {
         throw new AuthException(throwable);
      }
   }
}
