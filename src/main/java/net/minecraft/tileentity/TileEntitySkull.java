package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity {
   private int skullRotation;
   private int skullType;
   private GameProfile playerProfile = null;

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setByte("SkullType", (byte)(this.skullType & 0xFF));
      compound.setByte("Rot", (byte)(this.skullRotation & 0xFF));
      if (this.playerProfile != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         NBTUtil.writeGameProfile(nbtTagCompound, this.playerProfile);
         compound.setTag("Owner", nbtTagCompound);
      }
   }

   public int getSkullType() {
      return this.skullType;
   }

   public void setPlayerProfile(GameProfile playerProfile) {
      this.skullType = 3;
      this.playerProfile = playerProfile;
      this.updatePlayerProfile();
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 4, nbtTagCompound);
   }

   public GameProfile getPlayerProfile() {
      return this.playerProfile;
   }

   private void updatePlayerProfile() {
      this.playerProfile = updateGameprofile(this.playerProfile);
      this.markDirty();
   }

   public static GameProfile updateGameprofile(GameProfile input) {
      if (input != null && !StringUtils.isNullOrEmpty(input.getName())) {
         if (input.isComplete() && input.getProperties().containsKey("textures")) {
            return input;
         }

         if (MinecraftServer.getServer() == null) {
            return input;
         }

         GameProfile gameProfileForUsername = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(input.getName());
         if (gameProfileForUsername == null) {
            return input;
         }

         if ((Property)Iterables.getFirst(gameProfileForUsername.getProperties().get("textures"), null) == null) {
            gameProfileForUsername = MinecraftServer.getServer().getMinecraftSessionService().fillProfileProperties(gameProfileForUsername, true);
         }

         return gameProfileForUsername;
      } else {
         return input;
      }
   }

   public int getSkullRotation() {
      return this.skullRotation;
   }

   public void setType(int type) {
      this.skullType = type;
      this.playerProfile = null;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.skullType = compound.getByte("SkullType");
      this.skullRotation = compound.getByte("Rot");
      if (this.skullType == 3) {
         if (compound.hasKey("Owner", 10)) {
            this.playerProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
         } else if (compound.hasKey("ExtraType", 8)) {
            String text = compound.getString("ExtraType");
            if (!StringUtils.isNullOrEmpty(text)) {
               this.playerProfile = new GameProfile((UUID)null, text);
               this.updatePlayerProfile();
            }
         }
      }
   }

   public void setSkullRotation(int rotation) {
      this.skullRotation = rotation;
   }
}
