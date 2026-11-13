package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.BlockSkull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {
   private static final String[] skullTypes = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (int index = 0; index < skullTypes.length; index++) {
         subItems.add(new ItemStack(itemIn, 1, index));
      }
   }

   @Override
   public boolean updateItemStackNBT(NBTTagCompound nbt) {
      super.updateItemStackNBT(nbt);
      if (nbt.hasKey("SkullOwner", 8) && nbt.getString("SkullOwner").length() > 0) {
         GameProfile gameProfile = new GameProfile((UUID)null, nbt.getString("SkullOwner"));
         gameProfile = TileEntitySkull.updateGameprofile(gameProfile);
         nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameProfile));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      if (stack.getMetadata() == 3 && stack.hasTagCompound()) {
         if (stack.getTagCompound().hasKey("SkullOwner", 8)) {
            return StatCollector.translateToLocalFormatted("item.skull.player.name", stack.getTagCompound().getString("SkullOwner"));
         }

         if (stack.getTagCompound().hasKey("SkullOwner", 10)) {
            NBTTagCompound nBTTagCompound = stack.getTagCompound().getCompoundTag("SkullOwner");
            if (nBTTagCompound.hasKey("Name", 8)) {
               return StatCollector.translateToLocalFormatted("item.skull.player.name", nBTTagCompound.getString("Name"));
            }
         }
      }

      return super.getItemStackDisplayName(stack);
   }

   public ItemSkull() {
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setMaxDamage(0);
      this.getItem();
   }

   @Override
   public String getUnlocalizedName(ItemStack stack) {
      int metadata = stack.getMetadata();
      if (metadata < 0 || metadata >= skullTypes.length) {
         metadata = 0;
      }

      return super.getUnlocalizedName() + "." + skullTypes[metadata];
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)) {
         if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
            return false;
         }

         pos = pos.offset(side);
      }

      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!Blocks.skull.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      if (!worldIn.isRemote) {
         worldIn.setBlockState(pos, Blocks.skull.getDefaultState().withProperty(BlockSkull.FACING, side), 3);
         int i = 0;
         if (side == EnumFacing.UP) {
            i = MathHelper.floor_double(playerIn.rotationYaw * 16.0F / 360.0F + 0.5) & 15;
         }

         if (worldIn.getTileEntity(pos) instanceof TileEntitySkull tileEntitySkull) {
            if (stack.getMetadata() == 3) {
               GameProfile gameProfile = null;
               if (stack.hasTagCompound()) {
                  NBTTagCompound tagCompound = stack.getTagCompound();
                  if (tagCompound.hasKey("SkullOwner", 10)) {
                     gameProfile = NBTUtil.readGameProfileFromNBT(tagCompound.getCompoundTag("SkullOwner"));
                  } else if (tagCompound.hasKey("SkullOwner", 8) && tagCompound.getString("SkullOwner").length() > 0) {
                     gameProfile = new GameProfile((UUID)null, tagCompound.getString("SkullOwner"));
                  }
               }

               tileEntitySkull.setPlayerProfile(gameProfile);
            } else {
               tileEntitySkull.setType(stack.getMetadata());
            }

            tileEntitySkull.setSkullRotation(i);
            Blocks.skull.checkWitherSpawn(worldIn, pos, tileEntitySkull);
         }

         stack.stackSize--;
      }

      return true;
   }
}
