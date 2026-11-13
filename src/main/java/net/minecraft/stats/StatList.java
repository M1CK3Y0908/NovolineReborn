package net.minecraft.stats;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

public class StatList {
   protected static Map<String, StatBase> oneShotStats = Maps.newHashMap();
   public static List<StatBase> allStats = Lists.newArrayList();
   public static List<StatBase> generalStats = Lists.newArrayList();
   public static List<StatCrafting> itemStats = Lists.newArrayList();
   public static List<StatCrafting> objectMineStats = Lists.newArrayList();
   public static StatBase leaveGameStat = new StatBasic("stat.leaveGame", new ChatComponentTranslation("stat.leaveGame")).initIndependentStat().registerStat();
   public static StatBase minutesPlayedStat = new StatBasic("stat.playOneMinute", new ChatComponentTranslation("stat.playOneMinute"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase timeSinceDeathStat = new StatBasic("stat.timeSinceDeath", new ChatComponentTranslation("stat.timeSinceDeath"), StatBase.timeStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceWalkedStat = new StatBasic("stat.walkOneCm", new ChatComponentTranslation("stat.walkOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceCrouchedStat = new StatBasic("stat.crouchOneCm", new ChatComponentTranslation("stat.crouchOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceSprintedStat = new StatBasic("stat.sprintOneCm", new ChatComponentTranslation("stat.sprintOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceSwumStat = new StatBasic("stat.swimOneCm", new ChatComponentTranslation("stat.swimOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceFallenStat = new StatBasic("stat.fallOneCm", new ChatComponentTranslation("stat.fallOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceClimbedStat = new StatBasic("stat.climbOneCm", new ChatComponentTranslation("stat.climbOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceFlownStat = new StatBasic("stat.flyOneCm", new ChatComponentTranslation("stat.flyOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceDoveStat = new StatBasic("stat.diveOneCm", new ChatComponentTranslation("stat.diveOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByMinecartStat = new StatBasic(
         "stat.minecartOneCm", new ChatComponentTranslation("stat.minecartOneCm"), StatBase.distanceStatType
      )
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByBoatStat = new StatBasic("stat.boatOneCm", new ChatComponentTranslation("stat.boatOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByPigStat = new StatBasic("stat.pigOneCm", new ChatComponentTranslation("stat.pigOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase distanceByHorseStat = new StatBasic("stat.horseOneCm", new ChatComponentTranslation("stat.horseOneCm"), StatBase.distanceStatType)
      .initIndependentStat()
      .registerStat();
   public static StatBase jumpStat = new StatBasic("stat.jump", new ChatComponentTranslation("stat.jump")).initIndependentStat().registerStat();
   public static StatBase dropStat = new StatBasic("stat.drop", new ChatComponentTranslation("stat.drop")).initIndependentStat().registerStat();
   public static StatBase damageDealtStat = new StatBasic("stat.damageDealt", new ChatComponentTranslation("stat.damageDealt"), StatBase.damageStatType)
      .registerStat();
   public static StatBase damageTakenStat = new StatBasic("stat.damageTaken", new ChatComponentTranslation("stat.damageTaken"), StatBase.damageStatType)
      .registerStat();
   public static StatBase deathsStat = new StatBasic("stat.deaths", new ChatComponentTranslation("stat.deaths")).registerStat();
   public static StatBase mobKillsStat = new StatBasic("stat.mobKills", new ChatComponentTranslation("stat.mobKills")).registerStat();
   public static StatBase animalsBredStat = new StatBasic("stat.animalsBred", new ChatComponentTranslation("stat.animalsBred")).registerStat();
   public static StatBase playerKillsStat = new StatBasic("stat.playerKills", new ChatComponentTranslation("stat.playerKills")).registerStat();
   public static StatBase fishCaughtStat = new StatBasic("stat.fishCaught", new ChatComponentTranslation("stat.fishCaught")).registerStat();
   public static StatBase junkFishedStat = new StatBasic("stat.junkFished", new ChatComponentTranslation("stat.junkFished")).registerStat();
   public static StatBase treasureFishedStat = new StatBasic("stat.treasureFished", new ChatComponentTranslation("stat.treasureFished")).registerStat();
   public static StatBase timesTalkedToVillagerStat = new StatBasic("stat.talkedToVillager", new ChatComponentTranslation("stat.talkedToVillager"))
      .registerStat();
   public static StatBase timesTradedWithVillagerStat = new StatBasic("stat.tradedWithVillager", new ChatComponentTranslation("stat.tradedWithVillager"))
      .registerStat();
   public static StatBase cakeSlicesEatenStat = new StatBasic("stat.cakeSlicesEaten", new ChatComponentTranslation("stat.cakeSlicesEaten")).registerStat();
   public static StatBase cauldronFilledStat = new StatBasic("stat.cauldronFilled", new ChatComponentTranslation("stat.cauldronFilled")).registerStat();
   public static StatBase cauldronUsedStat = new StatBasic("stat.cauldronUsed", new ChatComponentTranslation("stat.cauldronUsed")).registerStat();
   public static StatBase armorCleanedStat = new StatBasic("stat.armorCleaned", new ChatComponentTranslation("stat.armorCleaned")).registerStat();
   public static StatBase bannerCleanedStat = new StatBasic("stat.bannerCleaned", new ChatComponentTranslation("stat.bannerCleaned")).registerStat();
   public static StatBase brewingStandInteractionStat = new StatBasic(
         "stat.brewingstandInteraction", new ChatComponentTranslation("stat.brewingstandInteraction")
      )
      .registerStat();
   public static StatBase beaconInteractionStat = new StatBasic("stat.beaconInteraction", new ChatComponentTranslation("stat.beaconInteraction"))
      .registerStat();
   public static StatBase dropperInspectedStat = new StatBasic("stat.dropperInspected", new ChatComponentTranslation("stat.dropperInspected")).registerStat();
   public static StatBase hopperInspectedStat = new StatBasic("stat.hopperInspected", new ChatComponentTranslation("stat.hopperInspected")).registerStat();
   public static StatBase dispenserInspectedStat = new StatBasic("stat.dispenserInspected", new ChatComponentTranslation("stat.dispenserInspected"))
      .registerStat();
   public static StatBase noteblockPlayedStat = new StatBasic("stat.noteblockPlayed", new ChatComponentTranslation("stat.noteblockPlayed")).registerStat();
   public static StatBase noteblockTunedStat = new StatBasic("stat.noteblockTuned", new ChatComponentTranslation("stat.noteblockTuned")).registerStat();
   public static StatBase flowerPottedStat = new StatBasic("stat.flowerPotted", new ChatComponentTranslation("stat.flowerPotted")).registerStat();
   public static StatBase trappedChestTriggeredStat = new StatBasic("stat.trappedChestTriggered", new ChatComponentTranslation("stat.trappedChestTriggered"))
      .registerStat();
   public static StatBase enderChestOpenedStat = new StatBasic("stat.enderchestOpened", new ChatComponentTranslation("stat.enderchestOpened")).registerStat();
   public static StatBase itemEnchantedStat = new StatBasic("stat.itemEnchanted", new ChatComponentTranslation("stat.itemEnchanted")).registerStat();
   public static StatBase recordPlayedStat = new StatBasic("stat.recordPlayed", new ChatComponentTranslation("stat.recordPlayed")).registerStat();
   public static StatBase furnaceInteractionStat = new StatBasic("stat.furnaceInteraction", new ChatComponentTranslation("stat.furnaceInteraction"))
      .registerStat();
   public static StatBase craftingTableInteractionStat = new StatBasic(
         "stat.craftingTableInteraction", new ChatComponentTranslation("stat.workbenchInteraction")
      )
      .registerStat();
   public static StatBase chestOpenedStat = new StatBasic("stat.chestOpened", new ChatComponentTranslation("stat.chestOpened")).registerStat();
   public static final StatBase[] mineBlockStatArray = new StatBase[4096];
   public static final StatBase[] objectCraftStats = new StatBase[32000];
   public static final StatBase[] objectUseStats = new StatBase[32000];
   public static final StatBase[] objectBreakStats = new StatBase[32000];

   private static void initItemDepleteStats() {
      for (Item item2 : Item.itemRegistry) {
         if (item2 != null) {
            int idFromItem = Item.getIdFromItem(item2);
            String itemName = getItemName(item2);
            if (itemName != null && item2.isDamageable()) {
               objectBreakStats[idFromItem] = new StatCrafting(
                     "stat.breakItem.", itemName, new ChatComponentTranslation("stat.breakItem", new ItemStack(item2).getChatComponent()), item2
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectBreakStats);
   }

   private static String getItemName(Item item) {
      ResourceLocation resourceLocation = Item.itemRegistry.getNameForObject(item);
      return resourceLocation != null ? resourceLocation.toString().replace(':', '.') : null;
   }

   public static StatBase getStatEntityKilledBy(net.minecraft.entity.EntityList.EntityEggInfo eggInfo) {
      String text = EntityList.getStringFromID(eggInfo.spawnedID);
      return text == null
         ? null
         : new StatBase(
               "stat.entityKilledBy." + text, new ChatComponentTranslation("stat.entityKilledBy", new ChatComponentTranslation("entity." + text + ".name"))
            )
            .registerStat();
   }

   private static void mergeStatBases(StatBase[] statBaseIn, Block sourceBlock, Block targetBlock) {
      int idFromBlock = Block.getIdFromBlock(sourceBlock);
      int idFromBlock2 = Block.getIdFromBlock(targetBlock);
      if (statBaseIn[idFromBlock] != null && statBaseIn[idFromBlock2] == null) {
         statBaseIn[idFromBlock2] = statBaseIn[idFromBlock];
      } else {
         allStats.remove(statBaseIn[idFromBlock]);
         objectMineStats.remove(statBaseIn[idFromBlock]);
         generalStats.remove(statBaseIn[idFromBlock]);
         statBaseIn[idFromBlock] = statBaseIn[idFromBlock2];
      }
   }

   private static void initMiningStats() {
      for (Block block2 : Block.blockRegistry) {
         Item itemFromBlock = Item.getItemFromBlock(block2);
         if (itemFromBlock != null) {
            int idFromBlock = Block.getIdFromBlock(block2);
            String itemName = getItemName(itemFromBlock);
            if (itemName != null && block2.getEnableStats()) {
               mineBlockStatArray[idFromBlock] = new StatCrafting(
                     "stat.mineBlock.", itemName, new ChatComponentTranslation("stat.mineBlock", new ItemStack(block2).getChatComponent()), itemFromBlock
                  )
                  .registerStat();
               objectMineStats.add((StatCrafting)mineBlockStatArray[idFromBlock]);
            }
         }
      }

      replaceAllSimilarBlocks(mineBlockStatArray);
   }

   private static void replaceAllSimilarBlocks(StatBase[] statBases) {
      mergeStatBases(statBases, Blocks.water, Blocks.flowing_water);
      mergeStatBases(statBases, Blocks.lava, Blocks.flowing_lava);
      mergeStatBases(statBases, Blocks.lit_pumpkin, Blocks.pumpkin);
      mergeStatBases(statBases, Blocks.lit_furnace, Blocks.furnace);
      mergeStatBases(statBases, Blocks.lit_redstone_ore, Blocks.redstone_ore);
      mergeStatBases(statBases, Blocks.powered_repeater, Blocks.unpowered_repeater);
      mergeStatBases(statBases, Blocks.powered_comparator, Blocks.unpowered_comparator);
      mergeStatBases(statBases, Blocks.redstone_torch, Blocks.unlit_redstone_torch);
      mergeStatBases(statBases, Blocks.lit_redstone_lamp, Blocks.redstone_lamp);
      mergeStatBases(statBases, Blocks.double_stone_slab, Blocks.stone_slab);
      mergeStatBases(statBases, Blocks.double_wooden_slab, Blocks.wooden_slab);
      mergeStatBases(statBases, Blocks.double_stone_slab2, Blocks.stone_slab2);
      mergeStatBases(statBases, Blocks.grass, Blocks.dirt);
      mergeStatBases(statBases, Blocks.farmland, Blocks.dirt);
   }

   private static void initStats() {
      for (Item item2 : Item.itemRegistry) {
         if (item2 != null) {
            int idFromItem = Item.getIdFromItem(item2);
            String itemName = getItemName(item2);
            if (itemName != null) {
               objectUseStats[idFromItem] = new StatCrafting(
                     "stat.useItem.", itemName, new ChatComponentTranslation("stat.useItem", new ItemStack(item2).getChatComponent()), item2
                  )
                  .registerStat();
               if (!(item2 instanceof ItemBlock)) {
                  itemStats.add((StatCrafting)objectUseStats[idFromItem]);
               }
            }
         }
      }

      replaceAllSimilarBlocks(objectUseStats);
   }

   public static StatBase getOneShotStat(String statId) {
      return oneShotStats.get(statId);
   }

   public static StatBase getStatKillEntity(net.minecraft.entity.EntityList.EntityEggInfo eggInfo) {
      String text = EntityList.getStringFromID(eggInfo.spawnedID);
      return text == null
         ? null
         : new StatBase("stat.killEntity." + text, new ChatComponentTranslation("stat.entityKill", new ChatComponentTranslation("entity." + text + ".name")))
            .registerStat();
   }

   private static void initCraftableStats() {
      HashSet hashSet = Sets.newHashSet();

      for (IRecipe iRecipe : CraftingManager.getInstance().getRecipeList()) {
         if (iRecipe.getRecipeOutput() != null) {
            hashSet.add(iRecipe.getRecipeOutput().getItem());
         }
      }

      for (ItemStack itemStack : FurnaceRecipes.instance().getSmeltingList().values()) {
         hashSet.add(itemStack.getItem());
      }

      for (Item item2 : (Iterable<Item>) hashSet) {
         if (item2 != null) {
            int idFromItem = Item.getIdFromItem(item2);
            String itemName = getItemName(item2);
            if (itemName != null) {
               objectCraftStats[idFromItem] = new StatCrafting(
                     "stat.craftItem.", itemName, new ChatComponentTranslation("stat.craftItem", new ItemStack(item2).getChatComponent()), item2
                  )
                  .registerStat();
            }
         }
      }

      replaceAllSimilarBlocks(objectCraftStats);
   }

   public static void init() {
      initMiningStats();
      initStats();
      initItemDepleteStats();
      initCraftableStats();
      AchievementList.init();
      EntityList.initializeEntities();
   }
}
