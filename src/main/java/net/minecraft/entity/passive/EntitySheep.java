package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal {
   private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer playerIn) {
         return false;
      }
   }, 2, 1);
   private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(EnumDyeColor.class);
   private int sheepTimer;
   private EntityAIEatGrass entityAIEatGrass = new EntityAIEatGrass(this);

   static {
      DYE_TO_RGB.put(EnumDyeColor.WHITE, new float[]{1.0F, 1.0F, 1.0F});
      DYE_TO_RGB.put(EnumDyeColor.ORANGE, new float[]{0.85F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.MAGENTA, new float[]{0.7F, 0.3F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.LIGHT_BLUE, new float[]{0.4F, 0.6F, 0.85F});
      DYE_TO_RGB.put(EnumDyeColor.YELLOW, new float[]{0.9F, 0.9F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.LIME, new float[]{0.5F, 0.8F, 0.1F});
      DYE_TO_RGB.put(EnumDyeColor.PINK, new float[]{0.95F, 0.5F, 0.65F});
      DYE_TO_RGB.put(EnumDyeColor.GRAY, new float[]{0.3F, 0.3F, 0.3F});
      DYE_TO_RGB.put(EnumDyeColor.SILVER, new float[]{0.6F, 0.6F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.CYAN, new float[]{0.3F, 0.5F, 0.6F});
      DYE_TO_RGB.put(EnumDyeColor.PURPLE, new float[]{0.5F, 0.25F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BLUE, new float[]{0.2F, 0.3F, 0.7F});
      DYE_TO_RGB.put(EnumDyeColor.BROWN, new float[]{0.4F, 0.3F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.GREEN, new float[]{0.4F, 0.5F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.RED, new float[]{0.6F, 0.2F, 0.2F});
      DYE_TO_RGB.put(EnumDyeColor.BLACK, new float[]{0.1F, 0.1F, 0.1F});
   }

   @Override
   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound("mob.sheep.step", 0.15F, 1.0F);
   }

   @Override
   protected String getHurtSound() {
      return "mob.sheep.say";
   }

   public boolean getSheared() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 16) != 0;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         this.sheepTimer = Math.max(0, this.sheepTimer - 1);
      }

      super.onLivingUpdate();
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setFleeceColor(getRandomSheepColor(this.worldObj.rand));
      return livingdata;
   }

   public static EnumDyeColor getRandomSheepColor(Random random) {
      int i = random.nextInt(100);
      return i < 5
         ? EnumDyeColor.BLACK
         : (
            i < 10
               ? EnumDyeColor.GRAY
               : (
                  i < 15
                     ? EnumDyeColor.SILVER
                     : (i < 18 ? EnumDyeColor.BROWN : (random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE))
               )
         );
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setSheared(tagCompund.getBoolean("Sheared"));
      this.setFleeceColor(EnumDyeColor.byMetadata(tagCompund.getByte("Color")));
   }

   public void setSheared(boolean sheared) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      if (sheared) {
         this.dataWatcher.updateObject(16, (byte)(b0 | 16));
      } else {
         this.dataWatcher.updateObject(16, (byte)(b0 & -17));
      }
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 10) {
         this.sheepTimer = 40;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   @Override
   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setBoolean("Sheared", this.getSheared());
      tagCompound.setByte("Color", (byte)this.getFleeceColor().getMetadata());
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   public void setFleeceColor(EnumDyeColor color) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      this.dataWatcher.updateObject(16, (byte)(b0 & 240 | color.getMetadata() & 15));
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(8.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23F);
   }

   @Override
   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      if (!this.getSheared()) {
         this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 0.0F);
      }

      int i = this.rand.nextInt(2) + 1 + this.rand.nextInt(1 + lootingModifier);

      for (int innerIndex = 0; innerIndex < i; innerIndex++) {
         if (this.isBurning()) {
            this.getEntityItem(Items.cooked_mutton);
         } else {
            this.getEntityItem(Items.mutton);
         }
      }
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.shears && !this.getSheared() && !this.isChild()) {
         if (!this.worldObj.isRemote) {
            this.setSheared(true);
            int i = 1 + this.rand.nextInt(3);

            for (int innerIndex = 0; innerIndex < i; innerIndex++) {
               EntityItem entityItem = this.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, this.getFleeceColor().getMetadata()), 1.0F);
               entityItem.motionY = entityItem.motionY + this.rand.nextFloat() * 0.05F;
               entityItem.motionX = entityItem.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
               entityItem.motionZ = entityItem.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
            }
         }

         itemStack.damageItem(1, player);
         this.playSound("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.interact(player);
   }

   public float getHeadRotationAngleX(float f) {
      if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
         float f2 = (this.sheepTimer - 4 - f) / 32.0F;
         return (float) (Math.PI / 5) + 0.2199115F * MathHelper.sin(f2 * 28.7F);
      } else {
         return this.sheepTimer > 0 ? (float) (Math.PI / 5) : this.rotationPitch / (180.0F / (float)Math.PI);
      }
   }

   private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother) {
      int dyeDamage = ((EntitySheep)father).getFleeceColor().getDyeDamage();
      int dyeDamage2 = ((EntitySheep)mother).getFleeceColor().getDyeDamage();
      this.inventoryCrafting.getStackInSlot(0).setItemDamage(dyeDamage);
      this.inventoryCrafting.getStackInSlot(1).setItemDamage(dyeDamage2);
      ItemStack matchingRecipe = CraftingManager.getInstance().findMatchingRecipe(this.inventoryCrafting, ((EntitySheep)father).worldObj);
      int i;
      if (matchingRecipe != null && matchingRecipe.getItem() == Items.dye) {
         i = matchingRecipe.getMetadata();
      } else {
         i = this.worldObj.rand.nextBoolean() ? dyeDamage : dyeDamage2;
      }

      return EnumDyeColor.byDyeDamage(i);
   }

   @Override
   public float getEyeHeight() {
      return 0.95F * this.height;
   }

   public static float[] getDyeRgb(EnumDyeColor dyeColor) {
      return DYE_TO_RGB.get(dyeColor);
   }

   @Override
   protected void updateAITasks() {
      this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
      super.updateAITasks();
   }

   public float getHeadRotationPointY(float f) {
      return this.sheepTimer <= 0
         ? 0.0F
         : (
            this.sheepTimer >= 4 && this.sheepTimer <= 36
               ? 1.0F
               : (this.sheepTimer < 4 ? (this.sheepTimer - f) / 4.0F : -(this.sheepTimer - 40 - f) / 4.0F)
         );
   }

   public EntitySheep(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.3F);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0));
      this.tasks.addTask(3, new EntityAITempt(this, 1.1, Items.wheat, false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1));
      this.tasks.addTask(5, this.entityAIEatGrass);
      this.tasks.addTask(6, new EntityAIWander(this, 1.0));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(Items.dye, 1, 0));
      this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(Items.dye, 1, 0));
   }

   @Override
   protected Item getDropItem() {
      return Item.getItemFromBlock(Blocks.wool);
   }

   public EntitySheep createChild(EntityAgeable ageable) {
      EntitySheep entitySheep = (EntitySheep)ageable;
      EntitySheep entitySheep2 = new EntitySheep(this.worldObj);
      entitySheep2.setFleeceColor(this.getDyeColorMixFromParents(this, entitySheep));
      return entitySheep2;
   }

   @Override
   protected String getDeathSound() {
      return "mob.sheep.say";
   }

   @Override
   protected String getLivingSound() {
      return "mob.sheep.say";
   }

   public EnumDyeColor getFleeceColor() {
      return EnumDyeColor.byMetadata(this.dataWatcher.getWatchableObjectByte(16) & 15);
   }
}
