package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {
   private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 32);
   private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
   public static TileEntitySkullRenderer instance;
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");

   public void renderSkull(float f, float f2, float f3, EnumFacing enumFacing, float f4, int i, GameProfile gameProfile, int i2) {
      ModelSkeletonHead skeletonHead2 = this.skeletonHead;
      if (i2 >= 0) {
         this.bindTexture(DESTROY_STAGES[i2]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch (i) {
            case 0:
            default:
               this.bindTexture(SKELETON_TEXTURES);
               break;
            case 1:
               this.bindTexture(WITHER_SKELETON_TEXTURES);
               break;
            case 2:
               this.bindTexture(ZOMBIE_TEXTURES);
               skeletonHead2 = this.humanoidHead;
               break;
            case 3:
               skeletonHead2 = this.humanoidHead;
               ResourceLocation defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkinLegacy();
               if (gameProfile != null) {
                  Minecraft minecraft2 = Minecraft.getMinecraft();
                  Map map = minecraft2.getSkinManager().loadSkinFromCache(gameProfile);
                  if (map.containsKey(Type.SKIN)) {
                     defaultSkinLegacy = minecraft2.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
                  } else {
                     defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(gameProfile));
                  }
               }

               this.bindTexture(defaultSkinLegacy);
               break;
            case 4:
               this.bindTexture(CREEPER_TEXTURES);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (enumFacing != EnumFacing.UP) {
         switch (enumFacing) {
            case NORTH:
               GlStateManager.translate(f + 0.5F, f2 + 0.25F, f3 + 0.74F);
               break;
            case SOUTH:
               GlStateManager.translate(f + 0.5F, f2 + 0.25F, f3 + 0.26F);
               f4 = 180.0F;
               break;
            case WEST:
               GlStateManager.translate(f + 0.74F, f2 + 0.25F, f3 + 0.5F);
               f4 = 270.0F;
               break;
            case EAST:
            default:
               GlStateManager.translate(f + 0.26F, f2 + 0.25F, f3 + 0.5F);
               f4 = 90.0F;
         }
      } else {
         GlStateManager.translate(f + 0.5F, f2, f3 + 0.5F);
      }

      float f5 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      skeletonHead2.render((Entity)null, 0.0F, 0.0F, 0.0F, f4, 0.0F, f5);
      GlStateManager.popMatrix();
      if (i2 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }

   @Override
   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void renderTileEntityAt(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage) {
      EnumFacing facing = EnumFacing.getFront(te.getBlockMetadata() & 7);
      this.renderSkull((float)x, (float)y, (float)z, facing, te.getSkullRotation() * 360 / 16.0F, te.getSkullType(), te.getPlayerProfile(), destroyStage);
   }
}
