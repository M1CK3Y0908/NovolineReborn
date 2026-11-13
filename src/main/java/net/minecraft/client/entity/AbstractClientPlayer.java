package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {
   private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
   private ResourceLocation locationOfCape = null;
   private NetworkPlayerInfo playerInfo;
   private long reloadCapeTimeMs = 0L;
   private boolean elytraOfCape = false;
   private String nameClear = null;

   public ResourceLocation getLocationOfCape() {
      return this.locationOfCape;
   }

   public boolean hasPlayerInfo() {
      return this.getPlayerInfo() != null;
   }

   public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
      TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
      ITextureObject textureObject = textureManager.getTexture(resourceLocationIn);
      if (textureObject == null) {
         textureObject = new ThreadDownloadImageData(
            (File)null,
            String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)),
            DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)),
            new ImageBufferDownload()
         );
         textureManager.loadTexture(resourceLocationIn, textureObject);
      }

      return (ThreadDownloadImageData)textureObject;
   }

   public NetworkPlayerInfo getPlayerInfo() {
      if (this.playerInfo == null) {
         this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
      }

      return this.playerInfo;
   }

   public long getReloadCapeTimeMs() {
      return this.reloadCapeTimeMs;
   }

   public void setReloadCapeTimeMs(long arg1) {
      this.reloadCapeTimeMs = arg1;
   }

   public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
      super(worldIn, playerProfile);
      this.nameClear = playerProfile.getName();
      if (this.nameClear != null && !this.nameClear.isEmpty()) {
         this.nameClear = StringUtils.stripControlCodes(this.nameClear);
      }

      CapeUtils.downloadCape(this);
      PlayerConfigurations.getPlayerConfiguration(this);
   }

   @Override
   public Vec3 getLook(float partialTicks) {
      return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
   }

   public String getNameClear() {
      return this.nameClear;
   }

   @Override
   public boolean isSpectator() {
      NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
      return networkPlayerInfo != null && networkPlayerInfo.getGameType() == net.minecraft.world.WorldSettings.GameType.SPECTATOR;
   }

   public void setLocationOfCape(ResourceLocation arg1) {
      this.locationOfCape = arg1;
   }

   public boolean hasSkin() {
      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo != null && networkPlayerInfo.hasLocationSkin();
   }

   public static ResourceLocation getLocationSkin(String username) {
      return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
   }

   public ResourceLocation getLocationSkin() {
      NetworkPlayerInfo playerInfo2 = this.getPlayerInfo();
      return playerInfo2 == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : playerInfo2.getLocationSkin();
   }

   public boolean isElytraOfCape() {
      return this.elytraOfCape;
   }

   public ResourceLocation getLocationCape() {
      if (!Config.isShowCapes()) {
         return null;
      }

      if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
         CapeUtils.reloadCape(this);
         this.reloadCapeTimeMs = 0L;
      }

      if (this.locationOfCape != null) {
         return this.locationOfCape;
      }

      NetworkPlayerInfo playerInfo2 = this.getPlayerInfo();
      return playerInfo2 == null ? null : playerInfo2.getLocationCape();
   }

   public float getFovModifier() {
      float f = 1.0F;
      if (this.capabilities.isFlying) {
         f *= 1.1F;
      }

      IAttributeInstance entityAttribute = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      f = (float)(f * ((entityAttribute.getAttributeValue() / this.capabilities.getWalkSpeed() + 1.0) / 2.0));
      if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
         f = 1.0F;
      }

      if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
         float f2 = this.getItemInUseDuration() / 20.0F;
         if (f2 > 1.0F) {
            f2 = 1.0F;
         } else {
            f2 *= f2;
         }

         f *= 1.0F - f2 * 0.15F;
      }

      return Reflector.reflectorMethod62.isActive() ? Reflector.createFloat2(Reflector.reflectorMethod62, this, f) : f;
   }

   public String getSkinType() {
      NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
      return networkPlayerInfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkPlayerInfo.getSkinType();
   }

   public void setElytraOfCape(boolean arg1) {
      this.elytraOfCape = arg1;
   }
}
