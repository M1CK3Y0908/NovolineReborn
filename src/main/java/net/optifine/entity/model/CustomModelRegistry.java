package net.optifine.entity.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.model.ModelBase;
import net.minecraft.src.Config;

public class CustomModelRegistry {
   private static Map<String, ModelAdapter> mapModelAdapters = makeMapModelAdapters();

   private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter) {
      addModelAdapter(map, modelAdapter, modelAdapter.getName());
      String[] aliases = modelAdapter.getAliases();
      if (aliases != null) {
         for (int i = 0; i < aliases.length; i++) {
            String text = aliases[i];
            addModelAdapter(map, modelAdapter, text);
         }
      }

      ModelBase modelBase = modelAdapter.makeModel();
      String[] modelRendererNames = modelAdapter.getModelRendererNames();

      for (int i2 = 0; i2 < modelRendererNames.length; i2++) {
         String text2 = modelRendererNames[i2];
         if (modelAdapter.getModelRenderer(modelBase, text2) == null) {
            Config.warn("Model renderer not found, model: " + modelAdapter.getName() + ", name: " + text2);
         }
      }
   }

   private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter, String name) {
      if (map.containsKey(name)) {
         Config.warn("Model adapter already registered for id: " + name + ", class: " + modelAdapter.getEntityClass().getName());
      }

      map.put(name, modelAdapter);
   }

   public static String[] getModelNames() {
      Set<String> set = mapModelAdapters.keySet();
      return set.toArray(new String[set.size()]);
   }

   private static Map<String, ModelAdapter> makeMapModelAdapters() {
      LinkedHashMap linkedHashMap = new LinkedHashMap();
      addModelAdapter(linkedHashMap, new ModelAdapterArmorStand());
      addModelAdapter(linkedHashMap, new ModelAdapterBat());
      addModelAdapter(linkedHashMap, new ModelAdapterBlaze());
      addModelAdapter(linkedHashMap, new ModelAdapterBoat());
      addModelAdapter(linkedHashMap, new ModelAdapterCaveSpider());
      addModelAdapter(linkedHashMap, new ModelAdapterChicken());
      addModelAdapter(linkedHashMap, new ModelAdapterCow());
      addModelAdapter(linkedHashMap, new ModelAdapterCreeper());
      addModelAdapter(linkedHashMap, new ModelAdapterDragon());
      addModelAdapter(linkedHashMap, new ModelAdapterEnderCrystal());
      addModelAdapter(linkedHashMap, new ModelAdapterEnderman());
      addModelAdapter(linkedHashMap, new ModelAdapterEndermite());
      addModelAdapter(linkedHashMap, new ModelAdapterGhast());
      addModelAdapter(linkedHashMap, new ModelAdapterGuardian());
      addModelAdapter(linkedHashMap, new ModelAdapterHorse());
      addModelAdapter(linkedHashMap, new ModelAdapterIronGolem());
      addModelAdapter(linkedHashMap, new ModelAdapterLeadKnot());
      addModelAdapter(linkedHashMap, new ModelAdapterMagmaCube());
      addModelAdapter(linkedHashMap, new ModelAdapterMinecart());
      addModelAdapter(linkedHashMap, new ModelAdapterMinecartTnt());
      addModelAdapter(linkedHashMap, new ModelAdapterMinecartMobSpawner());
      addModelAdapter(linkedHashMap, new ModelAdapterMooshroom());
      addModelAdapter(linkedHashMap, new ModelAdapterOcelot());
      addModelAdapter(linkedHashMap, new ModelAdapterPig());
      addModelAdapter(linkedHashMap, new ModelAdapterPigZombie());
      addModelAdapter(linkedHashMap, new ModelAdapterRabbit());
      addModelAdapter(linkedHashMap, new ModelAdapterSheep());
      addModelAdapter(linkedHashMap, new ModelAdapterSilverfish());
      addModelAdapter(linkedHashMap, new ModelAdapterSkeleton());
      addModelAdapter(linkedHashMap, new ModelAdapterSlime());
      addModelAdapter(linkedHashMap, new ModelAdapterSnowman());
      addModelAdapter(linkedHashMap, new ModelAdapterSpider());
      addModelAdapter(linkedHashMap, new ModelAdapterSquid());
      addModelAdapter(linkedHashMap, new ModelAdapterVillager());
      addModelAdapter(linkedHashMap, new ModelAdapterWitch());
      addModelAdapter(linkedHashMap, new ModelAdapterWither());
      addModelAdapter(linkedHashMap, new ModelAdapterWitherSkull());
      addModelAdapter(linkedHashMap, new ModelAdapterWolf());
      addModelAdapter(linkedHashMap, new ModelAdapterZombie());
      addModelAdapter(linkedHashMap, new ModelAdapterSheepWool());
      addModelAdapter(linkedHashMap, new ModelAdapterBanner());
      addModelAdapter(linkedHashMap, new ModelAdapterBook());
      addModelAdapter(linkedHashMap, new ModelAdapterChest());
      addModelAdapter(linkedHashMap, new ModelAdapterChestLarge());
      addModelAdapter(linkedHashMap, new ModelAdapterEnderChest());
      addModelAdapter(linkedHashMap, new ModelAdapterHeadHumanoid());
      addModelAdapter(linkedHashMap, new ModelAdapterHeadSkeleton());
      addModelAdapter(linkedHashMap, new ModelAdapterSign());
      return linkedHashMap;
   }

   public static ModelAdapter getModelAdapter(String name) {
      return mapModelAdapters.get(name);
   }
}
