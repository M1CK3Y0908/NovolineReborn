package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock {
   @Override
   public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
      for (EnumDyeColor enumDyeColor : EnumDyeColor.values()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         TileEntityBanner.setBaseColorAndPatterns(nbtTagCompound, enumDyeColor.getDyeDamage(), (NBTTagList)null);
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         nbtTagCompound2.setTag("BlockEntityTag", nbtTagCompound);
         ItemStack itemStack = new ItemStack(itemIn, 1, enumDyeColor.getDyeDamage());
         itemStack.setTagCompound(nbtTagCompound2);
         subItems.add(itemStack);
      }
   }

   @Override
   public String getItemStackDisplayName(ItemStack stack) {
      return StatCollector.translateToLocal("item.banner." + this.getBaseColor(stack).getUnlocalizedName() + ".name");
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      NBTTagCompound nBTTagCompound = stack.getSubCompound("BlockEntityTag", false);
      if (nBTTagCompound != null && nBTTagCompound.hasKey("Patterns")) {
         NBTTagList nBTTagList = nBTTagCompound.getTagList("Patterns", 10);

         for (int index = 0; index < nBTTagList.tagCount() && index < 6; index++) {
            NBTTagCompound nBTTagCompound2 = nBTTagList.getCompoundTagAt(index);
            EnumDyeColor dyeColor = EnumDyeColor.byDyeDamage(nBTTagCompound2.getInteger("Color"));
            net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern tileentitybanner$enumbannerpattern = net.minecraft.tileentity.TileEntityBanner.EnumBannerPattern.getPatternByID(
               nBTTagCompound2.getString("Pattern")
            );
            if (tileentitybanner$enumbannerpattern != null) {
               tooltip.add(
                  StatCollector.translateToLocal("item.banner." + tileentitybanner$enumbannerpattern.getPatternName() + "." + dyeColor.getUnlocalizedName())
               );
            }
         }
      }
   }

   @Override
   public int getColorFromItemStack(ItemStack stack, int renderPass) {
      return renderPass == 0 ? 16777215 : this.getBaseColor(stack).getMapColor().colorValue;
   }

   public ItemBanner() {
      super(Blocks.standing_banner);
      this.maxStackSize = 16;
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.getItem();
      this.setMaxDamage(0);
   }

   @Override
   public CreativeTabs getCreativeTab() {
      return CreativeTabs.tabDecorations;
   }

   private EnumDyeColor getBaseColor(ItemStack stack) {
      NBTTagCompound nBTTagCompound = stack.getSubCompound("BlockEntityTag", false);
      EnumDyeColor dyeColor;
      if (nBTTagCompound != null && nBTTagCompound.hasKey("Base")) {
         dyeColor = EnumDyeColor.byDyeDamage(nBTTagCompound.getInteger("Base"));
      } else {
         dyeColor = EnumDyeColor.byDyeDamage(stack.getMetadata());
      }

      return dyeColor;
   }

   @Override
   public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (side == EnumFacing.DOWN) {
         return false;
      }

      if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
         return false;
      }

      pos = pos.offset(side);
      if (!playerIn.canPlayerEdit(pos, side, stack)) {
         return false;
      }

      if (!Blocks.standing_banner.canPlaceBlockAt(worldIn, pos)) {
         return false;
      }

      if (worldIn.isRemote) {
         return true;
      }

      if (side == EnumFacing.UP) {
         int i = MathHelper.floor_double((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F + 0.5) & 15;
         worldIn.setBlockState(pos, Blocks.standing_banner.getDefaultState().withProperty(BlockStandingSign.ROTATION, i), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.wall_banner.getDefaultState().withProperty(BlockWallSign.FACING, side), 3);
      }

      stack.stackSize--;
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityBanner) {
         ((TileEntityBanner)tileEntity).setItemValues(stack);
      }

      return true;
   }
}
