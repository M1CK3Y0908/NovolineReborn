package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements IMerchant, INpc {
   private EntityPlayer buyingPlayer;
   private int wealth;
   private MerchantRecipeList buyingList;
   private static final EntityVillager.ITradeList[][][][] DEFAULT_TRADE_LIST_MAP = new EntityVillager.ITradeList[][][][]{
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.wheat, new EntityVillager.PriceInfo(18, 22)),
                        new EntityVillager.EmeraldForItems(Items.potato, new EntityVillager.PriceInfo(15, 19)),
                        new EntityVillager.EmeraldForItems(Items.carrot, new EntityVillager.PriceInfo(15, 19)),
                        new EntityVillager.ListItemForEmeralds(Items.bread, new EntityVillager.PriceInfo(-4, -2))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.pumpkin), new EntityVillager.PriceInfo(8, 13)),
                        new EntityVillager.ListItemForEmeralds(Items.pumpkin_pie, new EntityVillager.PriceInfo(-3, -2))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.melon_block), new EntityVillager.PriceInfo(7, 12)),
                        new EntityVillager.ListItemForEmeralds(Items.apple, new EntityVillager.PriceInfo(-5, -7))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.cookie, new EntityVillager.PriceInfo(-6, -10)),
                        new EntityVillager.ListItemForEmeralds(Items.cake, new EntityVillager.PriceInfo(1, 1))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.string, new EntityVillager.PriceInfo(15, 20)),
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ItemAndEmeraldToItem(
                           Items.fish, new EntityVillager.PriceInfo(6, 6), Items.cooked_fish, new EntityVillager.PriceInfo(6, 6)
                        )
                  },
                  {new EntityVillager.ListEnchantedItemForEmeralds(Items.fishing_rod, new EntityVillager.PriceInfo(7, 8))}
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Item.getItemFromBlock(Blocks.wool), new EntityVillager.PriceInfo(16, 22)),
                        new EntityVillager.ListItemForEmeralds(Items.shears, new EntityVillager.PriceInfo(3, 4))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 0), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 1), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 2), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 3), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 4), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 5), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 6), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 7), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 8), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 9), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 10), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 11), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 12), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 13), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 14), new EntityVillager.PriceInfo(1, 2)),
                        new EntityVillager.ListItemForEmeralds(new ItemStack(Item.getItemFromBlock(Blocks.wool), 1, 15), new EntityVillager.PriceInfo(1, 2))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.string, new EntityVillager.PriceInfo(15, 20)),
                        new EntityVillager.ListItemForEmeralds(Items.arrow, new EntityVillager.PriceInfo(-12, -8))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.bow, new EntityVillager.PriceInfo(2, 3)),
                        new EntityVillager.ItemAndEmeraldToItem(
                           Item.getItemFromBlock(Blocks.gravel), new EntityVillager.PriceInfo(10, 10), Items.flint, new EntityVillager.PriceInfo(6, 10)
                        )
                  }
            }
      },
      {
            {
                  {new EntityVillager.EmeraldForItems(Items.paper, new EntityVillager.PriceInfo(24, 36)), new EntityVillager.ListEnchantedBookForEmeralds()},
                  {
                        new EntityVillager.EmeraldForItems(Items.book, new EntityVillager.PriceInfo(8, 10)),
                        new EntityVillager.ListItemForEmeralds(Items.compass, new EntityVillager.PriceInfo(10, 12)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.bookshelf), new EntityVillager.PriceInfo(3, 4))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.written_book, new EntityVillager.PriceInfo(2, 2)),
                        new EntityVillager.ListItemForEmeralds(Items.clock, new EntityVillager.PriceInfo(10, 12)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.glass), new EntityVillager.PriceInfo(-5, -3))
                  },
                  {new EntityVillager.ListEnchantedBookForEmeralds()},
                  {new EntityVillager.ListEnchantedBookForEmeralds()},
                  {new EntityVillager.ListItemForEmeralds(Items.name_tag, new EntityVillager.PriceInfo(20, 22))}
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.rotten_flesh, new EntityVillager.PriceInfo(36, 40)),
                        new EntityVillager.EmeraldForItems(Items.gold_ingot, new EntityVillager.PriceInfo(8, 10))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.redstone, new EntityVillager.PriceInfo(-4, -1)),
                        new EntityVillager.ListItemForEmeralds(
                           new ItemStack(Items.dye, 1, EnumDyeColor.BLUE.getDyeDamage()), new EntityVillager.PriceInfo(-2, -1)
                        )
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.ender_eye, new EntityVillager.PriceInfo(7, 11)),
                        new EntityVillager.ListItemForEmeralds(Item.getItemFromBlock(Blocks.glowstone), new EntityVillager.PriceInfo(-3, -1))
                  },
                  {new EntityVillager.ListItemForEmeralds(Items.experience_bottle, new EntityVillager.PriceInfo(3, 11))}
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_helmet, new EntityVillager.PriceInfo(4, 6))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_chestplate, new EntityVillager.PriceInfo(10, 14))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_chestplate, new EntityVillager.PriceInfo(16, 19))
                  },
                  {
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_boots, new EntityVillager.PriceInfo(5, 7)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_leggings, new EntityVillager.PriceInfo(9, 11)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_helmet, new EntityVillager.PriceInfo(5, 7)),
                        new EntityVillager.ListItemForEmeralds(Items.chainmail_chestplate, new EntityVillager.PriceInfo(11, 15))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.iron_axe, new EntityVillager.PriceInfo(6, 8))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_sword, new EntityVillager.PriceInfo(9, 10))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_sword, new EntityVillager.PriceInfo(12, 15)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_axe, new EntityVillager.PriceInfo(9, 12))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_shovel, new EntityVillager.PriceInfo(5, 7))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.iron_ingot, new EntityVillager.PriceInfo(7, 9)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.iron_pickaxe, new EntityVillager.PriceInfo(9, 11))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.diamond, new EntityVillager.PriceInfo(3, 4)),
                        new EntityVillager.ListEnchantedItemForEmeralds(Items.diamond_pickaxe, new EntityVillager.PriceInfo(12, 15))
                  }
            }
      },
      {
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.porkchop, new EntityVillager.PriceInfo(14, 18)),
                        new EntityVillager.EmeraldForItems(Items.chicken, new EntityVillager.PriceInfo(14, 18))
                  },
                  {
                        new EntityVillager.EmeraldForItems(Items.coal, new EntityVillager.PriceInfo(16, 24)),
                        new EntityVillager.ListItemForEmeralds(Items.cooked_porkchop, new EntityVillager.PriceInfo(-7, -5)),
                        new EntityVillager.ListItemForEmeralds(Items.cooked_chicken, new EntityVillager.PriceInfo(-8, -6))
                  }
            },
            {
                  {
                        new EntityVillager.EmeraldForItems(Items.leather, new EntityVillager.PriceInfo(9, 12)),
                        new EntityVillager.ListItemForEmeralds(Items.leather_leggings, new EntityVillager.PriceInfo(2, 4))
                  },
                  {new EntityVillager.ListEnchantedItemForEmeralds(Items.leather_chestplate, new EntityVillager.PriceInfo(7, 12))},
                  {new EntityVillager.ListItemForEmeralds(Items.saddle, new EntityVillager.PriceInfo(8, 10))}
            }
      }
   };
   private boolean areAdditionalTasksSet;
   private boolean isPlaying;
   private int timeUntilReset;
   private int careerLevel;
   private int randomTickDivider;
   private boolean isWillingToMate;
   private boolean needsInitilization;
   private InventoryBasic villagerInventory = new InventoryBasic("Items", false, 8);
   private String lastBuyingPlayer;
   private int careerId;
   private boolean isMating;
   private boolean isLookingForHome;
   Village villageObj;

   @Override
   public MerchantRecipeList getRecipes(EntityPlayer player) {
      if (this.buyingList == null) {
         this.populateBuyingList();
      }

      return this.buyingList;
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (this.villageObj != null) {
         Entity entity2 = cause.getEntity();
         if (entity2 != null) {
            if (entity2 instanceof EntityPlayer) {
               this.villageObj.setReputationForPlayer(entity2.getName(), -2);
            } else if (entity2 instanceof IMob) {
               this.villageObj.endMatingSeason();
            }
         } else if (this.worldObj.getClosestPlayerToEntity(this, 16.0) != null) {
            this.villageObj.endMatingSeason();
         }
      }

      super.onDeath(cause);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Profession", this.getProfession());
      tagCompound.setInteger("Riches", this.wealth);
      tagCompound.setInteger("Career", this.careerId);
      tagCompound.setInteger("CareerLevel", this.careerLevel);
      tagCompound.setBoolean("Willing", this.isWillingToMate);
      if (this.buyingList != null) {
         tagCompound.setTag("Offers", this.buyingList.getRecipiesAsTags());
      }

      NBTTagList nbtTagList = new NBTTagList();

      for (int index = 0; index < this.villagerInventory.getSizeInventory(); index++) {
         ItemStack itemStack = this.villagerInventory.getStackInSlot(index);
         if (itemStack != null) {
            nbtTagList.appendTag(itemStack.writeToNBT(new NBTTagCompound()));
         }
      }

      tagCompound.setTag("Inventory", nbtTagList);
   }

   @Override
   public boolean allowLeashing() {
      return false;
   }

   public void setProfession(int professionId) {
      this.dataWatcher.updateObject(16, professionId);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setProfession(this.worldObj.rand.nextInt(5));
      this.setAdditionalAItasks();
      return livingdata;
   }

   @Override
   public IChatComponent getDisplayName() {
      String text = this.getCustomNameTag();
      if (text != null && text.length() > 0) {
         ChatComponentText chatComponentText = new ChatComponentText(text);
         chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentText;
      }

      if (this.buyingList == null) {
         this.populateBuyingList();
      }

      String text2 = null;
      switch (this.getProfession()) {
         case 0:
            if (this.careerId == 1) {
               text2 = "farmer";
            } else if (this.careerId == 2) {
               text2 = "fisherman";
            } else if (this.careerId == 3) {
               text2 = "shepherd";
            } else if (this.careerId == 4) {
               text2 = "fletcher";
            }
            break;
         case 1:
            text2 = "librarian";
            break;
         case 2:
            text2 = "cleric";
            break;
         case 3:
            if (this.careerId == 1) {
               text2 = "armor";
            } else if (this.careerId == 2) {
               text2 = "weapon";
            } else if (this.careerId == 3) {
               text2 = "tool";
            }
            break;
         case 4:
            if (this.careerId == 1) {
               text2 = "butcher";
            } else if (this.careerId == 2) {
               text2 = "leather";
            }
      }

      if (text2 != null) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("entity.Villager." + text2);
         chatComponentTranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentTranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentTranslation;
      } else {
         return super.getDisplayName();
      }
   }

   @Override
   public float getEyeHeight() {
      float f = 1.62F;
      if (this.isChild()) {
         f = (float)(f - 0.81);
      }

      return f;
   }

   public void update() {
      this.isWillingToMate = false;
   }

   @Override
   public void setRevengeTarget(EntityLivingBase livingBase) {
      super.setRevengeTarget(livingBase);
      if (this.villageObj != null && livingBase != null) {
         this.villageObj.addOrRenewAgressor(livingBase);
         if (livingBase instanceof EntityPlayer) {
            int i = -1;
            if (this.isChild()) {
               i = -3;
            }

            this.villageObj.setReputationForPlayer(livingBase.getName(), i);
            if (this.isEntityAlive()) {
               this.worldObj.setEntityState(this, (byte)13);
            }
         }
      }
   }

   private boolean hasEnoughFoodItems(int multiplier) {
      boolean flag = this.getProfession() == 0;

      for (int index = 0; index < this.villagerInventory.getSizeInventory(); index++) {
         ItemStack itemStack = this.villagerInventory.getStackInSlot(index);
         if (itemStack != null) {
            if (itemStack.getItem() == Items.bread && itemStack.stackSize >= 3 * multiplier
               || itemStack.getItem() == Items.potato && itemStack.stackSize >= 12 * multiplier
               || itemStack.getItem() == Items.carrot && itemStack.stackSize >= 12 * multiplier) {
               return true;
            }

            if (flag && itemStack.getItem() == Items.wheat && itemStack.stackSize >= 9 * multiplier) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean hasEnoughItemsToBreed() {
      return this.hasEnoughFoodItems(1);
   }

   @Override
   protected void updateEquipmentIfNeeded(EntityItem itemEntity) {
      ItemStack itemStack = itemEntity.getEntityItem();
      Item item = itemStack.getItem();
      if (this.canVillagerPickupItem(item)) {
         ItemStack itemStack2 = this.villagerInventory.addItem(itemStack);
         if (itemStack2 == null) {
            itemEntity.setDead();
         } else {
            itemStack.stackSize = itemStack2.stackSize;
         }
      }
   }

   private void populateBuyingList() {
      EntityVillager.ITradeList[][][] iTradeListsArrays = DEFAULT_TRADE_LIST_MAP[this.getProfession()];
      if (this.careerId != 0 && this.careerLevel != 0) {
         this.careerLevel++;
      } else {
         this.careerId = this.rand.nextInt(iTradeListsArrays.length) + 1;
         this.careerLevel = 1;
      }

      if (this.buyingList == null) {
         this.buyingList = new MerchantRecipeList();
      }

      int i = this.careerId - 1;
      int i2 = this.careerLevel - 1;
      EntityVillager.ITradeList[][] iTradeListsArray = iTradeListsArrays[i];
      if (i2 >= 0 && i2 < iTradeListsArray.length) {
         EntityVillager.ITradeList[] iTradeLists = iTradeListsArray[i2];
         int length2 = iTradeLists.length;

         for (int i3 = 0; i3 < length2; i3++) {
            iTradeLists[i3].modifyMerchantRecipeList(this.buyingList, this.rand);
         }
      }
   }

   private void spawnParticles(EnumParticleTypes particleType) {
      for (int index = 0; index < 5; index++) {
         double d = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         double d3 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               particleType,
               this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
               this.posY + 1.0 + this.rand.nextFloat() * this.height,
               this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
               d,
               d2,
               d3
            );
      }
   }

   @Override
   protected void updateAITasks() {
      if (--this.randomTickDivider <= 0) {
         BlockPos blockPos = new BlockPos(this);
         this.worldObj.getVillageCollection().addToVillagerPositionList(blockPos);
         this.randomTickDivider = 70 + this.rand.nextInt(50);
         this.villageObj = this.worldObj.getVillageCollection().getNearestVillage(blockPos, 32);
         if (this.villageObj == null) {
            this.detachHome();
         } else {
            BlockPos center = this.villageObj.getCenter();
            this.setHomePosAndDistance(center, (int)(this.villageObj.getVillageRadius() * 1.0F));
            if (this.isLookingForHome) {
               this.isLookingForHome = false;
               this.villageObj.update();
            }
         }
      }

      if (!this.isTrading() && this.timeUntilReset > 0) {
         this.timeUntilReset--;
         if (this.timeUntilReset <= 0) {
            if (this.needsInitilization) {
               for (MerchantRecipe merchantRecipe : this.buyingList) {
                  if (merchantRecipe.isRecipeDisabled()) {
                     merchantRecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
                  }
               }

               this.populateBuyingList();
               this.needsInitilization = false;
               if (this.villageObj != null && this.lastBuyingPlayer != null) {
                  this.worldObj.setEntityState(this, (byte)14);
                  this.villageObj.setReputationForPlayer(this.lastBuyingPlayer, 1);
               }
            }

            this.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200, 0));
         }
      }

      super.updateAITasks();
   }

   private void setAdditionalAItasks() {
      if (!this.areAdditionalTasksSet) {
         this.areAdditionalTasksSet = true;
         if (this.isChild()) {
            this.tasks.addTask(8, new EntityAIPlay(this, 0.32));
         } else if (this.getProfession() == 0) {
            this.tasks.addTask(6, new EntityAIHarvestFarmland(this, 0.6));
         }
      }
   }

   @Override
   public void verifySellingItem(ItemStack stack) {
      if (!this.worldObj.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         if (stack != null) {
            this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
         } else {
            this.playSound("mob.villager.no", this.getSoundVolume(), this.getSoundPitch());
         }
      }
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, 0);
   }

   @Override
   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      if (!this.worldObj.isRemote && !this.isDead) {
         EntityWitch entityWitch = new EntityWitch(this.worldObj);
         entityWitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         entityWitch.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityWitch)), (IEntityLivingData)null);
         entityWitch.setNoAI(this.isAIDisabled());
         if (this.hasCustomName()) {
            entityWitch.setCustomNameTag(this.getCustomNameTag());
            entityWitch.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag());
         }

         this.worldObj.spawnEntityInWorld(entityWitch);
         this.setDead();
      }
   }

   @Override
   public void useRecipe(MerchantRecipe recipe) {
      recipe.incrementToolUses();
      this.livingSoundTime = -this.getTalkInterval();
      this.playSound("mob.villager.yes", this.getSoundVolume(), this.getSoundPitch());
      int i = 3 + this.rand.nextInt(4);
      if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
         this.timeUntilReset = 40;
         this.needsInitilization = true;
         this.isWillingToMate = true;
         if (this.buyingPlayer != null) {
            this.lastBuyingPlayer = this.buyingPlayer.getName();
         } else {
            this.lastBuyingPlayer = null;
         }

         i += 5;
      }

      if (recipe.getItemToBuy().getItem() == Items.emerald) {
         this.wealth = this.wealth + recipe.getItemToBuy().stackSize;
      }

      if (recipe.getRewardsExp()) {
         this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY + 0.5, this.posZ, i));
      }
   }

   public void setMating(boolean mating) {
      this.isMating = mating;
   }

   public boolean isFarmItemInInventory() {
      for (int index = 0; index < this.villagerInventory.getSizeInventory(); index++) {
         ItemStack itemStack = this.villagerInventory.getStackInSlot(index);
         if (itemStack != null && (itemStack.getItem() == Items.wheat_seeds || itemStack.getItem() == Items.potato || itemStack.getItem() == Items.carrot)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack currentItem = player.inventory.getCurrentItem();
      if ((currentItem == null || currentItem.getItem() != Items.spawn_egg) && this.isEntityAlive() && !this.isTrading() && !this.isChild()) {
         if (!this.worldObj.isRemote && (this.buyingList == null || this.buyingList.size() > 0)) {
            this.setCustomer(player);
            player.displayVillagerTradeGui(this);
         }

         player.triggerAchievement(StatList.timesTalkedToVillagerStat);
         return true;
      } else {
         return super.interact(player);
      }
   }

   private boolean canVillagerPickupItem(Item itemIn) {
      return itemIn == Items.bread || itemIn == Items.potato || itemIn == Items.carrot || itemIn == Items.wheat || itemIn == Items.wheat_seeds;
   }

   @Override
   public void setRecipes(MerchantRecipeList recipeList) {
   }

   @Override
   protected String getDeathSound() {
      return "mob.villager.death";
   }

   public void setLookingForHome() {
      this.isLookingForHome = true;
   }

   @Override
   public void setCustomer(EntityPlayer player) {
      this.buyingPlayer = player;
   }

   public boolean needsFood() {
      return this.getProfession() == 0 ? !this.hasEnoughFoodItems(5) : !this.hasEnoughFoodItems(1);
   }

   @Override
   protected String getHurtSound() {
      return "mob.villager.hit";
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setProfession(tagCompund.getInteger("Profession"));
      this.wealth = tagCompund.getInteger("Riches");
      this.careerId = tagCompund.getInteger("Career");
      this.careerLevel = tagCompund.getInteger("CareerLevel");
      this.isWillingToMate = tagCompund.getBoolean("Willing");
      if (tagCompund.hasKey("Offers", 10)) {
         NBTTagCompound nBTTagCompound = tagCompund.getCompoundTag("Offers");
         this.buyingList = new MerchantRecipeList(nBTTagCompound);
      }

      NBTTagList nBTTagList = tagCompund.getTagList("Inventory", 10);

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         ItemStack itemStack = ItemStack.loadItemStackFromNBT(nBTTagList.getCompoundTagAt(index));
         if (itemStack != null) {
            this.villagerInventory.addItem(itemStack);
         }
      }

      this.setCanPickUpLoot(true);
      this.setAdditionalAItasks();
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
         return true;
      } else {
         int i = inventorySlot - 300;
         if (i >= 0 && i < this.villagerInventory.getSizeInventory()) {
            this.villagerInventory.setInventorySlotContents(i, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public EntityPlayer getCustomer() {
      return this.buyingPlayer;
   }

   public boolean canAbandonItems() {
      return this.hasEnoughFoodItems(2);
   }

   public void setPlaying(boolean playing) {
      this.isPlaying = playing;
   }

   public EntityVillager(long l, World world2, int i) {
      super(world2);
      this.setProfession(0);
      this.setSize(0.6F, 1.8F);
      ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityZombie.class, 8.0F, 0.6, 0.6));
      this.tasks.addTask(1, new EntityAITradePlayer(this));
      this.tasks.addTask(1, new EntityAILookAtTradePlayer(this));
      this.tasks.addTask(2, new EntityAIMoveIndoors(this));
      this.tasks.addTask(3, new EntityAIRestrictOpenDoor(this));
      this.tasks.addTask(4, new EntityAIOpenDoor(this));
      this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 0.6));
      this.tasks.addTask(6, new EntityAIVillagerMate(this));
      this.tasks.addTask(7, new EntityAIFollowGolem(this));
      this.tasks.addTask(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(9, new EntityAIVillagerInteract(this));
      this.tasks.addTask(9, new EntityAIWander(this, 0.6));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.setCanPickUpLoot(true);
   }

   @Override
   protected String getLivingSound() {
      return this.isTrading() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   public boolean isPlaying() {
      return this.isPlaying;
   }

   @Override
   protected boolean canDespawn() {
      return false;
   }

   @Override
   protected void onGrowingAdult() {
      if (this.getProfession() == 0) {
         this.tasks.addTask(8, new EntityAIHarvestFarmland(this, 0.6));
      }

      super.onGrowingAdult();
   }

   public InventoryBasic getVillagerInventory() {
      return this.villagerInventory;
   }

   public boolean isTrading() {
      return this.buyingPlayer != null;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 12) {
         this.spawnParticles(EnumParticleTypes.HEART);
      } else if (id == 13) {
         this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY);
      } else if (id == 14) {
         this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public boolean getIsWillingToMate(boolean updateFirst) {
      if (!this.isWillingToMate && updateFirst && this.hasEnoughItemsToBreed()) {
         boolean conditionMet = false;

         for (int index = 0; index < this.villagerInventory.getSizeInventory(); index++) {
            ItemStack itemStack = this.villagerInventory.getStackInSlot(index);
            if (itemStack != null) {
               if (itemStack.getItem() == Items.bread && itemStack.stackSize >= 3) {
                  conditionMet = true;
                  this.villagerInventory.decrStackSize(index, 3);
               } else if ((itemStack.getItem() == Items.potato || itemStack.getItem() == Items.carrot) && itemStack.stackSize >= 12) {
                  conditionMet = true;
                  this.villagerInventory.decrStackSize(index, 12);
               }
            }

            if (conditionMet) {
               this.worldObj.setEntityState(this, (byte)18);
               this.isWillingToMate = true;
               break;
            }
         }
      }

      return this.isWillingToMate;
   }

   public boolean isMating() {
      return this.isMating;
   }

   public int getProfession() {
      return Math.max(this.dataWatcher.getWatchableObjectInt(16) % 5, 0);
   }

   public EntityVillager(World worldIn) {
      this(116522934105636L, worldIn, 0);
   }

   public EntityVillager createChild(EntityAgeable ageable) {
      EntityVillager entityVillager = new EntityVillager(this.worldObj);
      entityVillager.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityVillager)), (IEntityLivingData)null);
      return entityVillager;
   }

   @Override
   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5);
   }

   static class EmeraldForItems implements EntityVillager.ITradeList {
      public Item sellItem;
      public EntityVillager.PriceInfo price;

      public EmeraldForItems(Item itemIn, EntityVillager.PriceInfo priceIn) {
         this.sellItem = itemIn;
         this.price = priceIn;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
         int i = 1;
         if (this.price != null) {
            i = this.price.getPrice(random);
         }

         recipeList.add(new MerchantRecipe(new ItemStack(this.sellItem, i, 0), Items.emerald));
      }
   }

   interface ITradeList {
      void modifyMerchantRecipeList(MerchantRecipeList merchantRecipeList, Random random2);
   }

   static class ItemAndEmeraldToItem implements EntityVillager.ITradeList {
      public ItemStack buyingItemStack;
      public EntityVillager.PriceInfo buyingPriceInfo;
      public ItemStack sellingItemstack;
      public EntityVillager.PriceInfo sellingPriceInfo;

      public ItemAndEmeraldToItem(Item buyingItem, EntityVillager.PriceInfo buyingPriceInfo, Item sellingItem, EntityVillager.PriceInfo sellingPriceInfo) {
         this.buyingItemStack = new ItemStack(buyingItem);
         this.buyingPriceInfo = buyingPriceInfo;
         this.sellingItemstack = new ItemStack(sellingItem);
         this.sellingPriceInfo = sellingPriceInfo;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
         int i = 1;
         if (this.buyingPriceInfo != null) {
            i = this.buyingPriceInfo.getPrice(random);
         }

         int i2 = 1;
         if (this.sellingPriceInfo != null) {
            i2 = this.sellingPriceInfo.getPrice(random);
         }

         recipeList.add(
            new MerchantRecipe(
               new ItemStack(this.buyingItemStack.getItem(), i, this.buyingItemStack.getMetadata()),
               new ItemStack(Items.emerald),
               new ItemStack(this.sellingItemstack.getItem(), i2, this.sellingItemstack.getMetadata())
            )
         );
      }
   }

   static class ListEnchantedBookForEmeralds implements EntityVillager.ITradeList {
      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
         Enchantment enchantment2 = Enchantment.enchantmentsBookList[random.nextInt(Enchantment.enchantmentsBookList.length)];
         int randomIntegerInRange = MathHelper.getRandomIntegerInRange(random, enchantment2.getMinLevel(), enchantment2.getMaxLevel());
         ItemStack itemStack = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(enchantment2, randomIntegerInRange));
         int i = 2 + random.nextInt(5 + randomIntegerInRange * 10) + 3 * randomIntegerInRange;
         if (i > 64) {
            i = 64;
         }

         recipeList.add(new MerchantRecipe(new ItemStack(Items.book), new ItemStack(Items.emerald, i), itemStack));
      }
   }

   static class ListEnchantedItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack enchantedItemStack;
      public EntityVillager.PriceInfo priceInfo;

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
         int i = 1;
         if (this.priceInfo != null) {
            i = this.priceInfo.getPrice(random);
         }

         ItemStack itemStack3 = new ItemStack(Items.emerald, i, 0);
         ItemStack itemStack4 = new ItemStack(this.enchantedItemStack.getItem(), 1, this.enchantedItemStack.getMetadata());
         itemStack4 = EnchantmentHelper.addRandomEnchantment(random, itemStack4, 5 + random.nextInt(15));
         recipeList.add(new MerchantRecipe(itemStack3, itemStack4));
      }

      public ListEnchantedItemForEmeralds(Item item2, EntityVillager.PriceInfo priceInfo2) {
         this.enchantedItemStack = new ItemStack(item2);
         this.priceInfo = priceInfo2;
      }
   }

   static class ListItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack itemToBuy;
      public EntityVillager.PriceInfo priceInfo;

      public ListItemForEmeralds(ItemStack stack, EntityVillager.PriceInfo priceInfo) {
         this.itemToBuy = stack;
         this.priceInfo = priceInfo;
      }

      public ListItemForEmeralds(Item par1Item, EntityVillager.PriceInfo priceInfo) {
         this.itemToBuy = new ItemStack(par1Item);
         this.priceInfo = priceInfo;
      }

      @Override
      public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
         int i = 1;
         if (this.priceInfo != null) {
            i = this.priceInfo.getPrice(random);
         }

         ItemStack itemStack3;
         ItemStack itemStack4;
         if (i < 0) {
            itemStack3 = new ItemStack(Items.emerald, 1, 0);
            itemStack4 = new ItemStack(this.itemToBuy.getItem(), -i, this.itemToBuy.getMetadata());
         } else {
            itemStack3 = new ItemStack(Items.emerald, i, 0);
            itemStack4 = new ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.getMetadata());
         }

         recipeList.add(new MerchantRecipe(itemStack3, itemStack4));
      }
   }

   static class PriceInfo extends Tuple<Integer, Integer> {
      public int getPrice(Random rand) {
         return this.getFirst() >= this.getSecond() ? this.getFirst() : this.getFirst() + rand.nextInt(this.getSecond() - this.getFirst() + 1);
      }

      public PriceInfo(int i, int i2) {
         super(i, i2);
      }
   }
}
