package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
   private final ModelRenderer headModel;

   public LayerCustomHead(ModelRenderer modelRenderer) {
      this.headModel = modelRenderer;
   }

   @Override
   public void doRenderLayer(
      EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      ItemStack itemStack = entitylivingbaseIn.getCurrentArmor(3);
      if (itemStack != null && itemStack.getItem() != null) {
         Item item = itemStack.getItem();
         Minecraft minecraft = Minecraft.getMinecraft();
         GlStateManager.pushMatrix();
         if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         boolean flag = entitylivingbaseIn instanceof EntityVillager
            || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager();
         if (!flag && entitylivingbaseIn.isChild()) {
            float f6 = 2.0F;
            float f7 = 1.4F;
            GlStateManager.scale(f7 / f6, f7 / f6, f7 / f6);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         }

         this.headModel.postRender(0.0625F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (item instanceof ItemBlock) {
            float f8 = 0.625F;
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(f8, -f8, -f8);
            if (flag) {
               GlStateManager.translate(0.0F, 0.1875F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemStack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.HEAD);
         } else if (item == Items.skull) {
            float f9 = 1.1875F;
            GlStateManager.scale(f9, -f9, -f9);
            if (flag) {
               GlStateManager.translate(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameProfile = null;
            if (itemStack.hasTagCompound()) {
               NBTTagCompound nBTTagCompound = itemStack.getTagCompound();
               if (nBTTagCompound.hasKey("SkullOwner", 10)) {
                  gameProfile = NBTUtil.readGameProfileFromNBT(nBTTagCompound.getCompoundTag("SkullOwner"));
               } else if (nBTTagCompound.hasKey("SkullOwner", 8)) {
                  String text = nBTTagCompound.getString("SkullOwner");
                  if (!StringUtils.isNullOrEmpty(text)) {
                     gameProfile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, text));
                     nBTTagCompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameProfile));
                  }
               }
            }

            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemStack.getMetadata(), gameProfile, -1);
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
