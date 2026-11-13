package wtf.novoline.platform;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.bungee.providers.BungeeMovementTransmitter;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MovementTransmitterProvider;

public class ViaPlatformLoaderImpl implements ViaPlatformLoader {
   @Override
   public void load() {
      Via.getManager().getProviders().use(MovementTransmitterProvider.class, new BungeeMovementTransmitter());
      Via.getManager().getProviders().use(VersionProvider.class, new BaseVersionProvider() {
         @Override
         public int getClosestServerProtocol(UserConnection userConnection) throws Exception {
            return userConnection.isClientSide() ? ViaMCP.getViaMCP().getVersion() : super.getClosestServerProtocol(userConnection);
         }
      });
   }

   @Override
   public void unload() {
   }
}
