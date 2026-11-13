package net.minecraft.server.management;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreYggdrasilConverter {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File OLD_IPBAN_FILE = new File("banned-ips.txt");
   public static final File OLD_PLAYERBAN_FILE = new File("banned-players.txt");
   public static final File OLD_OPS_FILE = new File("ops.txt");
   public static final File OLD_WHITELIST_FILE = new File("white-list.txt");

   public static String getStringUUIDFromName(String text) {
      if (!StringUtils.isNullOrEmpty(text) && text.length() <= 16) {
         final MinecraftServer minecraftServer = MinecraftServer.getServer();
         GameProfile gameProfile = minecraftServer.getPlayerProfileCache().getGameProfileForUsername(text);
         if (gameProfile != null && gameProfile.getId() != null) {
            return gameProfile.getId().toString();
         } else if (!minecraftServer.isSinglePlayer() && minecraftServer.isServerInOnlineMode()) {
            final List<GameProfile> list = Lists.newArrayList();
            ProfileLookupCallback profileLookupCallback = new ProfileLookupCallback() {
               @Override
               public void onProfileLookupFailed(GameProfile arg1, Exception arg2) {
                  PreYggdrasilConverter.LOGGER.warn("Could not lookup user whitelist entry for " + arg1.getName(), arg2);
               }

               @Override
               public void onProfileLookupSucceeded(GameProfile arg1) {
                  minecraftServer.getPlayerProfileCache().addEntry(arg1);
                  list.add(arg1);
               }
            };
            lookupNames(minecraftServer, Lists.newArrayList(new String[]{text}), profileLookupCallback);
            return list.size() > 0 && list.get(0).getId() != null ? list.get(0).getId().toString() : "";
         } else {
            return EntityPlayer.getUUID(new GameProfile((UUID)null, text)).toString();
         }
      } else {
         return text;
      }
   }

   private static void lookupNames(MinecraftServer server, Collection<String> names, ProfileLookupCallback callback) {
      String[] texts = (String[])Iterators.toArray(Iterators.filter(names.iterator(), new Predicate<String>() {
         public boolean apply(String arg1) {
            return !StringUtils.isNullOrEmpty(arg1);
         }
      }), String.class);
      if (server.isServerInOnlineMode()) {
         server.getGameProfileRepository().findProfilesByNames(texts, Agent.MINECRAFT, callback);
      } else {
         for (String text : texts) {
            UUID uuid = EntityPlayer.getUUID(new GameProfile((UUID)null, text));
            GameProfile gameProfile2 = new GameProfile(uuid, text);
            callback.onProfileLookupSucceeded(gameProfile2);
         }
      }
   }
}
