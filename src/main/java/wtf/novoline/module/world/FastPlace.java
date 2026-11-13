package wtf.novoline.module.world;

import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.MapBuilder;

public class FastPlace extends Module {
   private final NumberSetting fastPlace = settingManager.register(new NumberSetting(this, "Delay", 0, 0, 4));
   private final MultiComboSetting items = settingManager.register(
      new MultiComboSetting(this, "Items")
         .add("Blocks", true)
         .add("Pearls", true)
         .add("Eggs", true)
         .add("Snowballs", true)
         .add("Potions", true)
         .add("Other items", true)
   );
   private static final Map<Class<? extends Item>, Integer> itemOptionIndex = MapBuilder.<Class<? extends Item>, Integer>create()
      .with(ItemBlock.class, 0)
      .with(ItemEnderPearl.class, 1)
      .with(ItemEgg.class, 2)
      .with(ItemSnowball.class, 3)
      .with(ItemPotion.class, 4);

   public FastPlace() {
      super(Category.WORLD, "FastPlace", "Decreases block place delay timer");
   }

   public NumberSetting getFastPlace() {
      return this.fastPlace;
   }

   public MultiComboSetting getItems() {
      return this.items;
   }

   public boolean hasEffect(ItemStack stack) {
      if (stack == null) {
         return false;
      }

      Item item2 = stack.getItem();
      return !(item2 instanceof ItemFishingRod)
         && !(item2 instanceof ItemPotion itemPotion && itemPotion.getEffects(stack) != null && itemPotion.getEffects(stack).stream().noneMatch(PotionEffect::isIsSplashPotion))
         && this.items.isSelected(itemOptionIndex.getOrDefault(item2.getClass(), 5));
   }
}
