package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public abstract class Enchantment {
   private static final Enchantment[] enchantmentsList = new Enchantment[256];
   private static final Map<ResourceLocation, Enchantment> locationEnchantments = Maps.newHashMap();
   public final int effectId;
   public static final Enchantment protection = new EnchantmentProtection(0, new ResourceLocation("protection"), 10, 0);
   public static final Enchantment fireProtection = new EnchantmentProtection(1, new ResourceLocation("fire_protection"), 5, 1);
   public static final Enchantment featherFalling = new EnchantmentProtection(2, new ResourceLocation("feather_falling"), 5, 2);
   public static final Enchantment blastProtection = new EnchantmentProtection(3, new ResourceLocation("blast_protection"), 2, 3);
   public static final Enchantment projectileProtection = new EnchantmentProtection(4, new ResourceLocation("projectile_protection"), 5, 4);
   public static final Enchantment respiration = new EnchantmentOxygen(new ResourceLocation("respiration"));
   public static final Enchantment aquaAffinity = new EnchantmentWaterWorker(new ResourceLocation("aqua_affinity"));
   public static final Enchantment thorns = new EnchantmentThorns(new ResourceLocation("thorns"));
   public static final Enchantment depthStrider = new EnchantmentWaterWalker(new ResourceLocation("depth_strider"));
   public static final Enchantment sharpness = new EnchantmentDamage(16, new ResourceLocation("sharpness"), 10, 0);
   private final int weight;
   public static final Enchantment smite = new EnchantmentDamage(17, new ResourceLocation("smite"), 5, 1);
   public static final Enchantment baneOfArthropods = new EnchantmentDamage(18, new ResourceLocation("bane_of_arthropods"), 5, 2);
   public static final Enchantment knockback = new EnchantmentKnockback(new ResourceLocation("knockback"));
   public static final Enchantment fireAspect = new EnchantmentFireAspect(new ResourceLocation("fire_aspect"));
   public static final Enchantment looting = new EnchantmentLootBonus(21, new ResourceLocation("looting"), 2, EnumEnchantmentType.WEAPON);
   public static final Enchantment efficiency = new EnchantmentDigging(new ResourceLocation("efficiency"));
   public static final Enchantment silkTouch = new EnchantmentUntouching(new ResourceLocation("silk_touch"));
   public static final Enchantment unbreaking = new EnchantmentDurability(new ResourceLocation("unbreaking"));
   public static final Enchantment fortune = new EnchantmentLootBonus(35, new ResourceLocation("fortune"), 2, EnumEnchantmentType.DIGGER);
   public static final Enchantment power = new EnchantmentArrowDamage(new ResourceLocation("power"));
   public static final Enchantment punch = new EnchantmentArrowKnockback(new ResourceLocation("punch"));
   public static final Enchantment flame = new EnchantmentArrowFire(new ResourceLocation("flame"));
   public static final Enchantment infinity = new EnchantmentArrowInfinite(new ResourceLocation("infinity"));
   public static final Enchantment luckOfTheSea = new EnchantmentLootBonus(61, new ResourceLocation("luck_of_the_sea"), 2, EnumEnchantmentType.FISHING_ROD);
   protected String name;
   public EnumEnchantmentType type;
   public static final Enchantment lure = new EnchantmentFishingSpeed(62, new ResourceLocation("lure"), 2, EnumEnchantmentType.FISHING_ROD);
   public static final Enchantment[] enchantmentsBookList;

   public boolean canApplyTogether(Enchantment ench) {
      return this != ench;
   }

   protected Enchantment(int enchID, ResourceLocation enchName, int enchWeight, EnumEnchantmentType enchType) {
      this.effectId = enchID;
      this.weight = enchWeight;
      this.type = enchType;
      if (enchantmentsList[enchID] != null) {
         throw new IllegalArgumentException("Duplicate enchantment id!");
      }

      enchantmentsList[enchID] = this;
      locationEnchantments.put(enchName, this);
   }

   public static Set<ResourceLocation> getRegisteredResourceLocations() {
      return locationEnchantments.keySet();
   }

   public boolean canApply(ItemStack stack) {
      return this.type.canEnchantItem(stack.getItem());
   }

   public int getWeight() {
      return this.weight;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + enchantmentLevel * 10;
   }

   public static Enchantment getEnchantmentById(int enchID) {
      return enchID >= 0 && enchID < enchantmentsList.length ? enchantmentsList[enchID] : null;
   }

   public int getMaxLevel() {
      return 1;
   }

   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
   }

   static {
      ArrayList arrayList = Lists.newArrayList();

      for (Enchantment enchantment2 : enchantmentsList) {
         if (enchantment2 != null) {
            arrayList.add(enchantment2);
         }
      }

      enchantmentsBookList = (Enchantment[]) arrayList.toArray(new Enchantment[arrayList.size()]);
   }

   public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType) {
      return 0.0F;
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return 0;
   }

   public static Enchantment getEnchantmentByLocation(String location) {
      return locationEnchantments.get(new ResourceLocation(location));
   }

   public String getTranslatedName(int level) {
      return StatCollector.translateToLocal(this.getName()) + " " + StatCollector.translateToLocal("enchantment.level." + level);
   }

   public void onUserHurt(EntityLivingBase user, Entity attacker, int level) {
   }

   public String getName() {
      return "enchantment." + this.name;
   }

   public int getMinLevel() {
      return 1;
   }

   public Enchantment setName(String enchName) {
      this.name = enchName;
      return this;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 5;
   }
}
