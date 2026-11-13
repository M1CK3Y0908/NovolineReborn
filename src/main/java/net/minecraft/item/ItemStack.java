package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import java.util.Map;

public final class ItemStack {
   private Block canDestroyCacheBlock = null;
   private boolean canDestroyCacheResult = false;
   private Block canPlaceOnCacheBlock = null;
   public int animationsToGo;
   private boolean canPlaceOnCacheResult = false;
   public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.###");
   private Item item;
   private NBTTagCompound stackTagCompound;
   public int stackSize;
   private EntityItemFrame itemFrame;
   private int itemDamage;

   public boolean isItemEnchantable() {
      return !this.getItem().isItemTool(this) ? false : !this.isItemEnchanted();
   }

   public Item getItem() {
      return this.item;
   }

   public int getMaxItemUseDuration() {
      return this.getItem().getMaxItemUseDuration(this);
   }

   public boolean canEditBlocks() {
      return this.getItem().canItemEditBlocks();
   }

   public void setTagInfo(String key, NBTBase value) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      this.stackTagCompound.setTag(key, value);
   }

   public ItemStack copy() {
      ItemStack itemStack = new ItemStack(this.item, this.stackSize, this.itemDamage);
      if (this.stackTagCompound != null) {
         itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      return itemStack;
   }

   public boolean getHasSubtypes() {
      return this.item.getHasSubtypes();
   }

   public boolean isItemDamaged() {
      return this.isItemStackDamageable() && this.itemDamage > 0;
   }

   public void onPlayerStoppedUsing(World worldIn, EntityPlayer playerIn, int timeLeft) {
      this.getItem().onPlayerStoppedUsing(this, worldIn, playerIn, timeLeft);
   }

   public ItemStack(Block blockIn, int amount) {
      this(blockIn, amount, 0);
   }

   public boolean hasEffect() {
      return this.getItem().hasEffect(this);
   }

   @Override
   public String toString() {
      return this.stackSize + "x" + this.item.getUnlocalizedName() + "@" + this.itemDamage;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      if (nbt.hasKey("id", 8)) {
         this.item = Item.getByNameOrId(nbt.getString("id"));
      } else {
         this.item = Item.getItemById(nbt.getShort("id"));
      }

      this.stackSize = nbt.getByte("Count");
      this.itemDamage = nbt.getShort("Damage");
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }

      if (nbt.hasKey("tag", 10)) {
         this.stackTagCompound = nbt.getCompoundTag("tag");
         if (this.item != null) {
            this.item.updateItemStackNBT(this.stackTagCompound);
         }
      }
   }

   public ItemStack(Item itemIn, int amount, int meta) {
      this.item = itemIn;
      this.stackSize = amount;
      this.itemDamage = meta;
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }
   }

   public boolean getIsItemStackEqual(ItemStack itemStack) {
      return this.isItemStackEqual(itemStack);
   }

   public float getStrVsBlock(Block blockIn) {
      return this.getItem().getStrVsBlock(this, blockIn);
   }

   public boolean isItemEnchanted() {
      return this.stackTagCompound != null && this.stackTagCompound.hasKey("ench", 9);
   }

   public void setItemFrame(EntityItemFrame frame) {
      this.itemFrame = frame;
   }

   public boolean isOnItemFrame() {
      return this.itemFrame != null;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers() {
      Multimap<String, AttributeModifier> multimap;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         NBTTagList tagList = this.stackTagCompound.getTagList("AttributeModifiers", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            AttributeModifier attributeModifierFromNBT = SharedMonsterAttributes.readAttributeModifierFromNBT(compoundTagAt);
            if (attributeModifierFromNBT != null && attributeModifierFromNBT.getID().getLeastSignificantBits() != 0L && attributeModifierFromNBT.getID().getMostSignificantBits() != 0L) {
               multimap.put(compoundTagAt.getString("AttributeName"), attributeModifierFromNBT);
            }
         }
      } else {
         multimap = this.getItem().getItemAttributeModifiers();
      }

      return (Multimap<String, AttributeModifier>)multimap;
   }

   public void setRepairCost(int cost) {
      if (!this.hasTagCompound()) {
         this.stackTagCompound = new NBTTagCompound();
      }

      this.stackTagCompound.setInteger("RepairCost", cost);
   }

   public EnumAction getItemUseAction() {
      return this.getItem().getItemUseAction(this);
   }

   public String getDisplayName() {
      String itemStackDisplayName = this.getItem().getItemStackDisplayName(this);
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
         NBTTagCompound compoundTag = this.stackTagCompound.getCompoundTag("display");
         if (compoundTag.hasKey("Name", 8)) {
            itemStackDisplayName = compoundTag.getString("Name");
         }
      }

      return itemStackDisplayName;
   }

   public boolean isItemEqual(ItemStack other) {
      return other != null && this.item == other.item && this.itemDamage == other.itemDamage;
   }

   public ItemStack(Item itemIn) {
      this(itemIn, 1);
   }

   public ItemStack useItemRightClick(World worldIn, EntityPlayer playerIn) {
      return this.getItem().onItemRightClick(this, worldIn, playerIn);
   }

   public ItemStack(Block blockIn) {
      this(blockIn, 1);
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isItemStackDamageable() || !this.isItemDamaged());
   }

   public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
      if (this.item.hitEntity(this, entityIn, playerIn)) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }
   }

   public ItemStack(Item itemIn, int amount) {
      this(itemIn, amount, 0);
   }

   public int getMaxStackSize() {
      return this.getItem().getItemStackLimit();
   }

   public static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemStackEqual(stackB) : false);
   }

   public boolean canDestroy(Block blockIn) {
      if (blockIn == this.canDestroyCacheBlock) {
         return this.canDestroyCacheResult;
      }

      this.canDestroyCacheBlock = blockIn;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9)) {
         NBTTagList tagList = this.stackTagCompound.getTagList("CanDestroy", 8);

         for (int i = 0; i < tagList.tagCount(); i++) {
            if (Block.getBlockFromName(tagList.getStringTagAt(i)) == blockIn) {
               this.canDestroyCacheResult = true;
               return true;
            }
         }
      }

      this.canDestroyCacheResult = false;
      return false;
   }

   public NBTTagList getEnchantmentTagList() {
      return this.stackTagCompound == null ? null : this.stackTagCompound.getTagList("ench", 10);
   }

   public void addEnchantment(Enchantment ench, int level) {
      if (this.stackTagCompound == null) {
         this.setTagCompound(new NBTTagCompound());
      }

      if (!this.stackTagCompound.hasKey("ench", 9)) {
         this.stackTagCompound.setTag("ench", new NBTTagList());
      }

      NBTTagList nBTTagList = this.stackTagCompound.getTagList("ench", 10);
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setShort("id", (short)ench.effectId);
      nbtTagCompound.setShort("lvl", (byte)level);
      nBTTagList.appendTag(nbtTagCompound);
   }

   public EntityItemFrame getItemFrame() {
      return this.itemFrame;
   }

   public IChatComponent getChatComponent() {
      ChatComponentText chatComponentText = new ChatComponentText(this.getDisplayName());
      if (this.hasDisplayName()) {
         chatComponentText.getChatStyle().setItalic(true);
      }

      IChatComponent chatComponent = new ChatComponentText("[").appendSibling(chatComponentText).appendText("]");
      if (this.item != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         this.writeToNBT(nbtTagCompound);
         chatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_ITEM, new ChatComponentText(nbtTagCompound.toString())));
         chatComponent.getChatStyle().setColor(this.getRarity().rarityColor);
      }

      return chatComponent;
   }

   public boolean hasDisplayName() {
      return this.stackTagCompound == null
         ? false
         : (!this.stackTagCompound.hasKey("display", 10) ? false : this.stackTagCompound.getCompoundTag("display").hasKey("Name", 8));
   }

   public void clearCustomName() {
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey("display", 10)) {
         NBTTagCompound nBTTagCompound = this.stackTagCompound.getCompoundTag("display");
         nBTTagCompound.removeTag("Name");
         if (nBTTagCompound.hasNoTags()) {
            this.stackTagCompound.removeTag("display");
            if (this.stackTagCompound.hasNoTags()) {
               this.setTagCompound((NBTTagCompound)null);
            }
         }
      }
   }

   public String getUnlocalizedName() {
      return this.item.getUnlocalizedName(this);
   }

   public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
      playerIn.addStat(StatList.objectCraftStats[Item.getIdFromItem(this.item)], amount);
      this.item.onCreated(this, worldIn, playerIn);
   }

   public void setItem(Item newItem) {
      this.item = newItem;
   }

   public NBTTagCompound getSubCompound(String key, boolean create) {
      if (this.stackTagCompound != null && this.stackTagCompound.hasKey(key, 10)) {
         return this.stackTagCompound.getCompoundTag(key);
      } else if (create) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         this.setTagInfo(key, nbtTagCompound);
         return nbtTagCompound;
      } else {
         return null;
      }
   }

   public ItemStack(Block blockIn, int amount, int meta) {
      this(Item.getItemFromBlock(blockIn), amount, meta);
   }

   public void onBlockDestroyed(World worldIn, Block blockIn, BlockPos pos, EntityPlayer playerIn) {
      if (this.item.onBlockDestroyed(this, worldIn, blockIn, pos, playerIn)) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      ResourceLocation resourceLocation = Item.itemRegistry.getNameForObject(this.item);
      nbt.setString("id", resourceLocation == null ? "minecraft:air" : resourceLocation.toString());
      nbt.setByte("Count", (byte)this.stackSize);
      nbt.setShort("Damage", (short)this.itemDamage);
      if (this.stackTagCompound != null) {
         nbt.setTag("tag", this.stackTagCompound);
      }

      return nbt;
   }

   public int getMetadata() {
      return this.itemDamage;
   }

   public int getMaxDamage() {
      return this.item.getMaxDamage();
   }

   public ItemStack splitStack(int amount) {
      ItemStack itemStack = new ItemStack(this.item, amount, this.itemDamage);
      if (this.stackTagCompound != null) {
         itemStack.stackTagCompound = (NBTTagCompound)this.stackTagCompound.copy();
      }

      this.stackSize -= amount;
      return itemStack;
   }

   public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn) {
      return this.item.itemInteractionForEntity(this, playerIn, entityIn);
   }

   public NBTTagCompound getTagCompound() {
      return this.stackTagCompound;
   }

   public int getItemDamage() {
      return this.itemDamage;
   }

   public void setItemDamage(int meta) {
      this.itemDamage = meta;
      if (this.itemDamage < 0) {
         this.itemDamage = 0;
      }
   }

   public ItemStack setStackDisplayName(String displayName) {
      if (this.stackTagCompound == null) {
         this.stackTagCompound = new NBTTagCompound();
      }

      if (!this.stackTagCompound.hasKey("display", 10)) {
         this.stackTagCompound.setTag("display", new NBTTagCompound());
      }

      this.stackTagCompound.getCompoundTag("display").setString("Name", displayName);
      return this;
   }

   public static ItemStack loadItemStackFromNBT(NBTTagCompound nbt) {
      ItemStack itemStack = new ItemStack();
      itemStack.readFromNBT(nbt);
      return itemStack.getItem() != null ? itemStack : null;
   }

   public boolean attemptDamageItem(int amount, Random rand) {
      if (!this.isItemStackDamageable()) {
         return false;
      }

      if (amount > 0) {
         int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, this);
         int i = 0;

         for (int nestedIndex = 0; enchantmentLevel > 0 && nestedIndex < amount; nestedIndex++) {
            if (EnchantmentDurability.negateDamage(this, enchantmentLevel, rand)) {
               i++;
            }
         }

         amount -= i;
         if (amount <= 0) {
            return false;
         }
      }

      this.itemDamage += amount;
      return this.itemDamage > this.getMaxDamage();
   }

   public EnumRarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isItemStackDamageable() {
      return this.item == null ? false : (this.item.getMaxDamage() <= 0 ? false : !this.hasTagCompound() || !this.getTagCompound().getBoolean("Unbreakable"));
   }

   public static ItemStack copyItemStack(ItemStack stack) {
      return stack == null ? null : stack.copy();
   }

   private ItemStack() {
   }

   public ItemStack onItemUseFinish(World worldIn, EntityPlayer playerIn) {
      return this.getItem().onItemUseFinish(this, worldIn, playerIn);
   }

   public List<String> getTooltip(EntityPlayer playerIn, boolean advanced) {
      ArrayList arrayList = Lists.newArrayList();
      String displayName2 = this.getDisplayName();
      if (this.hasDisplayName()) {
         displayName2 = EnumChatFormatting.ITALIC + displayName2;
      }

      displayName2 = displayName2 + EnumChatFormatting.RESET;
      if (advanced) {
         String text2 = "";
         if (displayName2.length() > 0) {
            displayName2 = displayName2 + " (";
            text2 = ")";
         }

         int idFromItem = Item.getIdFromItem(this.item);
         if (this.getHasSubtypes()) {
            displayName2 = displayName2 + String.format("#%04d/%d%s", idFromItem, this.itemDamage, text2);
         } else {
            displayName2 = displayName2 + String.format("#%04d%s", idFromItem, text2);
         }
      } else if (!this.hasDisplayName() && this.item == Items.filled_map) {
         displayName2 = displayName2 + " #" + this.itemDamage;
      }

      arrayList.add(displayName2);
      int i = 0;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("HideFlags", 99)) {
         i = this.stackTagCompound.getInteger("HideFlags");
      }

      if ((i & 32) == 0) {
         this.item.addInformation(this, playerIn, arrayList, advanced);
      }

      if (this.hasTagCompound()) {
         if ((i & 1) == 0) {
            NBTTagList enchantmentTagList = this.getEnchantmentTagList();
            if (enchantmentTagList != null) {
               for (int i2 = 0; i2 < enchantmentTagList.tagCount(); i2++) {
                  short shortValue = enchantmentTagList.getCompoundTagAt(i2).getShort("id");
                  short shortValue2 = enchantmentTagList.getCompoundTagAt(i2).getShort("lvl");
                  if (Enchantment.getEnchantmentById(shortValue) != null) {
                     arrayList.add(Enchantment.getEnchantmentById(shortValue).getTranslatedName(shortValue2));
                  }
               }
            }
         }

         if (this.stackTagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = this.stackTagCompound.getCompoundTag("display");
            if (compoundTag.hasKey("color", 3)) {
               if (advanced) {
                  arrayList.add("Color: #" + Integer.toHexString(compoundTag.getInteger("color")).toUpperCase());
               } else {
                  arrayList.add(EnumChatFormatting.ITALIC + StatCollector.translateToLocal("item.dyed"));
               }
            }

            if (compoundTag.getTagId("Lore") == 9) {
               NBTTagList tagList = compoundTag.getTagList("Lore", 8);
               if (tagList.tagCount() > 0) {
                  for (int i3 = 0; i3 < tagList.tagCount(); i3++) {
                     arrayList.add("" + EnumChatFormatting.DARK_PURPLE + EnumChatFormatting.ITALIC + tagList.getStringTagAt(i3));
                  }
               }
            }
         }
      }

      Multimap attributeModifiers = this.getAttributeModifiers();
      if (!attributeModifiers.isEmpty() && (i & 2) == 0) {
         arrayList.add("");

         for (Entry entry : (Iterable<Map.Entry>) attributeModifiers.entries()) {
            AttributeModifier attributeModifier = (AttributeModifier)entry.getValue();
            double amount2 = attributeModifier.getAmount();
            if (attributeModifier.getID() == Item.itemModifierUUID) {
               amount2 += EnchantmentHelper.getModifierForCreature(this, EnumCreatureAttribute.UNDEFINED);
            }

            double d2;
            if (attributeModifier.getOperation() != 1 && attributeModifier.getOperation() != 2) {
               d2 = amount2;
            } else {
               d2 = amount2 * 100.0;
            }

            if (amount2 > 0.0) {
               arrayList.add(
                  EnumChatFormatting.BLUE
                     + StatCollector.translateToLocalFormatted(
                        "attribute.modifier.plus." + attributeModifier.getOperation(),
                        DECIMALFORMAT.format(d2),
                        StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                     )
               );
            } else if (amount2 < 0.0) {
               d2 *= -1.0;
               arrayList.add(
                  EnumChatFormatting.RED
                     + StatCollector.translateToLocalFormatted(
                        "attribute.modifier.take." + attributeModifier.getOperation(),
                        DECIMALFORMAT.format(d2),
                        StatCollector.translateToLocal("attribute.name." + (String)entry.getKey())
                     )
               );
            }
         }
      }

      if (this.hasTagCompound() && this.getTagCompound().getBoolean("Unbreakable") && (i & 4) == 0) {
         arrayList.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("item.unbreakable"));
      }

      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanDestroy", 9) && (i & 8) == 0) {
         NBTTagList tagList2 = this.stackTagCompound.getTagList("CanDestroy", 8);
         if (tagList2.tagCount() > 0) {
            arrayList.add("");
            arrayList.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canBreak"));

            for (int i4 = 0; i4 < tagList2.tagCount(); i4++) {
               Block blockFromName = Block.getBlockFromName(tagList2.getStringTagAt(i4));
               if (blockFromName != null) {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + blockFromName.getLocalizedName());
               } else {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9) && (i & 16) == 0) {
         NBTTagList tagList3 = this.stackTagCompound.getTagList("CanPlaceOn", 8);
         if (tagList3.tagCount() > 0) {
            arrayList.add("");
            arrayList.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.canPlace"));

            for (int i5 = 0; i5 < tagList3.tagCount(); i5++) {
               Block blockFromName2 = Block.getBlockFromName(tagList3.getStringTagAt(i5));
               if (blockFromName2 != null) {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + blockFromName2.getLocalizedName());
               } else {
                  arrayList.add(EnumChatFormatting.DARK_GRAY + "missingno");
               }
            }
         }
      }

      if (advanced) {
         if (this.isItemDamaged()) {
            arrayList.add("Durability: " + (this.getMaxDamage() - this.getItemDamage()) + " / " + this.getMaxDamage());
         }

         arrayList.add(EnumChatFormatting.DARK_GRAY + Item.itemRegistry.getNameForObject(this.item).toString());
         if (this.hasTagCompound()) {
            arrayList.add(EnumChatFormatting.DARK_GRAY + "NBT: " + this.getTagCompound().getKeySet().size() + " tag(s)");
         }
      }

      return arrayList;
   }

   public void setTagCompound(NBTTagCompound nbt) {
      this.stackTagCompound = nbt;
   }

   public boolean canHarvestBlock(Block blockIn) {
      return this.item.canHarvestBlock(blockIn);
   }

   public boolean hasTagCompound() {
      return this.stackTagCompound != null;
   }

   public void damageItem(int amount, EntityLivingBase entityIn) {
      if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).capabilities.isCreativeMode)
         && this.isItemStackDamageable()
         && this.attemptDamageItem(amount, entityIn.getRNG())) {
         entityIn.renderBrokenItemStack(this);
         this.stackSize--;
         if (entityIn instanceof EntityPlayer entityPlayer) {
            entityPlayer.triggerAchievement(StatList.objectBreakStats[Item.getIdFromItem(this.item)]);
            if (this.stackSize == 0 && this.getItem() instanceof ItemBow) {
               entityPlayer.destroyCurrentEquippedItem();
            }
         }

         if (this.stackSize < 0) {
            this.stackSize = 0;
         }

         this.itemDamage = 0;
      }
   }

   public void handleWorld(World world2, Entity entity2, boolean flag) {
      if (this.animationsToGo > 0) {
         this.animationsToGo--;
      }

      this.item.noop(this, world2, entity2, flag);
   }

   public boolean onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      boolean conditionMet = this.getItem().onItemUse(this, playerIn, worldIn, pos, side, hitX, hitY, hitZ);
      if (conditionMet) {
         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this.item)]);
      }

      return conditionMet;
   }

   public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null
         ? true
         : (
            stackA == null || stackB == null
               ? false
               : (
                  stackA.stackTagCompound == null && stackB.stackTagCompound != null
                     ? false
                     : stackA.stackTagCompound == null || stackA.stackTagCompound.equals(stackB.stackTagCompound)
               )
         );
   }

   public boolean canPlaceOn(Block blockIn) {
      if (blockIn == this.canPlaceOnCacheBlock) {
         return this.canPlaceOnCacheResult;
      }

      this.canPlaceOnCacheBlock = blockIn;
      if (this.hasTagCompound() && this.stackTagCompound.hasKey("CanPlaceOn", 9)) {
         NBTTagList tagList = this.stackTagCompound.getTagList("CanPlaceOn", 8);

         for (int i = 0; i < tagList.tagCount(); i++) {
            if (Block.getBlockFromName(tagList.getStringTagAt(i)) == blockIn) {
               this.canPlaceOnCacheResult = true;
               return true;
            }
         }
      }

      this.canPlaceOnCacheResult = false;
      return false;
   }

   public int getRepairCost() {
      return this.hasTagCompound() && this.stackTagCompound.hasKey("RepairCost", 3) ? this.stackTagCompound.getInteger("RepairCost") : 0;
   }

   public static boolean areItemsEqual(ItemStack stackA, ItemStack stackB) {
      return stackA == null && stackB == null ? true : (stackA != null && stackB != null ? stackA.isItemEqual(stackB) : false);
   }

   private boolean isItemStackEqual(ItemStack other) {
      return this.stackSize != other.stackSize
         ? false
         : (
            this.item != other.item
               ? false
               : (
                  this.itemDamage != other.itemDamage
                     ? false
                     : (
                        this.stackTagCompound == null && other.stackTagCompound != null
                           ? false
                           : this.stackTagCompound == null || this.stackTagCompound.equals(other.stackTagCompound)
                     )
               )
         );
   }
}
