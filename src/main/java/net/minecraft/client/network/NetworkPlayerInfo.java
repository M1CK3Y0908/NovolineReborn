package net.minecraft.client.network;

import com.google.common.base.Objects;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.client.IrcConnection;
import wtf.novoline.client.discord.GatewayUser;
import wtf.novoline.cosmetic.CapeCosmeticChild1;
import wtf.novoline.enums.CapeType;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.visual.SkinChanger;
import wtf.novoline.util.Profile;

public class NetworkPlayerInfo {
   private ResourceLocation locationSkin;
   private boolean playerTexturesLoaded = false;
   private int lastHealth = 0;
   private String skinType;
   private IChatComponent displayName;
   private int displayHealth = 0;
   private long lastHealthTime = 0L;
   private net.minecraft.world.WorldSettings.GameType gameType;
   private long healthBlinkTime = 0L;
   private long renderVisibilityId = 0L;
   private int responseTime;
   private ResourceLocation locationCape;
   private final GameProfile gameProfile;

   public long getLastHealthTime() {
      return this.lastHealthTime;
   }

   public void setDisplayName(IChatComponent displayNameIn) {
      this.displayName = displayNameIn;
   }

   public ResourceLocation getLocationSkin() {
      if (this.locationSkin == null) {
         this.loadPlayerTextures();
      }

      SkinChanger skinChanger2 = ModuleManager.skinChanger;
      if (skinChanger2.isEnabled()) {
         EntityPlayerSP thePlayer2 = Minecraft.getMinecraft().thePlayer;
         NetworkPlayerInfo playerInfo = thePlayer2.sendQueue.getPlayerInfo(thePlayer2.getUniqueID());
         if (playerInfo != null && playerInfo.getGameProfile().equals(this.getGameProfile()) || skinChanger2.getSkinChanger().isEnabled()) {
            return skinChanger2.getResourceLocation();
         }
      }

      return (ResourceLocation)Objects.firstNonNull(this.locationSkin, DefaultPlayerSkin.getDefaultSkin(this.gameProfile.getId()));
   }

   public NetworkPlayerInfo(GameProfile gameProfile2) {
      this.gameProfile = gameProfile2;
   }

   public void setRenderVisibilityId(long renderVisibilityId) {
      this.renderVisibilityId = renderVisibilityId;
   }

   public ResourceLocation getLocationCape() {
      if (this.locationCape == null) {
         this.loadPlayerTextures();
      }

      return this.locationCape;
   }

   public void setLastHealthTime(long lastHealthTime) {
      this.lastHealthTime = lastHealthTime;
   }

   public String getSkinType() {
      return this.skinType == null ? DefaultPlayerSkin.getSkinType(this.gameProfile.getId()) : this.skinType;
   }

   public long getHealthBlinkTime() {
      return this.healthBlinkTime;
   }

   public boolean hasLocationSkin() {
      return this.locationSkin != null;
   }

   public NetworkPlayerInfo(net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData addPlayerData) {
      this.gameProfile = addPlayerData.getProfile();
      this.gameType = addPlayerData.getGameMode();
      this.responseTime = addPlayerData.getPing();
      this.displayName = addPlayerData.getDisplayName();
   }

   public void setHealthBlinkTime(long healthBlinkTime) {
      this.healthBlinkTime = healthBlinkTime;
   }

   public void setLastHealth(int lastHealth) {
      this.lastHealth = lastHealth;
   }

   public int getLastHealth() {
      return this.lastHealth;
   }

   public IChatComponent getDisplayName() {
      return this.displayName;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   protected void loadPlayerTextures() {
      synchronized (this) {
         if (!this.playerTexturesLoaded) {
            this.playerTexturesLoaded = true;
            IrcConnection ircConnection = Novoline.getNovoline().getIrcConnection();
            GatewayUser byName = ircConnection.getGatewayClient().getGatewayUserList().getByName(this.gameProfile.getName());
            boolean flag = true;
            if (byName != null) {
               Profile profile = ircConnection.getGatewayClient().getGatewayPresenceMap().getProfile(byName);
               if (profile != null) {
                  CapeCosmeticChild1 capeCosmeticChild1 = profile.getCosmetics()
                     .stream()
                     .filter(cosmetic2 -> cosmetic2.getType() == CapeType.CAPE)
                     .map(CapeCosmeticChild1.class::cast)
                     .findFirst()
                     .orElse(null);
                  if (capeCosmeticChild1 != null) {
                     this.locationCape = new ResourceLocation("nos", "texture/" + capeCosmeticChild1.getTexHash());
                     flag = false;
                  }
               }
            }

            boolean flag2 = flag;
            Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, (type, resourceLocation, minecraftProfileTexture) -> {
               switch (type) {
                  case SKIN:
                     this.locationSkin = resourceLocation;
                     this.skinType = minecraftProfileTexture.getMetadata("model");
                     if (this.skinType == null) {
                        this.skinType = "default";
                     }
                     break;
                  case CAPE:
                     if (flag2) {
                        this.locationCape = resourceLocation;
                     }
               }
            }, true);
         }
      }
   }

   protected void setGameType(net.minecraft.world.WorldSettings.GameType gameType2) {
      this.gameType = gameType2;
   }

   protected void setResponseTime(int i) {
      this.responseTime = i;
   }

   public void setDisplayHealth(int displayHealth) {
      this.displayHealth = displayHealth;
   }

   public net.minecraft.world.WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public int getResponseTime() {
      return this.responseTime;
   }

   public long getRenderVisibilityId() {
      return this.renderVisibilityId;
   }

   public ScorePlayerTeam getPlayerTeam() {
      return Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
   }

   public int getDisplayHealth() {
      return this.displayHealth;
   }
}
