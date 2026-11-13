package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import wtf.novoline.enums.RelationType;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.utils.render.RenderUtils;

public class NameTags extends Module {
   public MultiComboSetting nameTags = settingManager.register(new MultiComboSetting(this, "Hide targets").add("Bots", false).add("Self", false));
   public MultiComboSetting elements = settingManager.register(
      new MultiComboSetting(this, "Elements").add("Potions", false).add("Armor & items", false).add("Distance", false).add("Ping", false)
   );
   public MultiComboSetting addons = settingManager.register(
      new MultiComboSetting(this, "Addons").add("Down scale", false).add("Shadow", true).add("Hide tags", false)
   );
   public CheckboxSetting enchantments = settingManager.register(new CheckboxSetting(this, "Enchantments", true) {
      @Override
      public boolean shouldShow() {
         return NameTags.this.elements.isSelected(1);
      }
   });
   public ColorSetting friendColor = settingManager.register(new ColorSetting(this, "Friend color", -2147450880));
   public ColorSetting targetColor = settingManager.register(new ColorSetting(this, "Target color", -2139095040));
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if ((!this.nameTags.isSelected(0) || !ModuleManager.antibot.isPlayerBot(entityPlayer)) && (!this.nameTags.isSelected(1) || entityPlayer != minecraft.thePlayer)) {
            float f = (float)(
               entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerX
            );
            float f2 = (float)(
               entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerY
            );
            float f3 = (float)(
               entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * minecraft.timer.renderPartialTicks - TileEntityRendererDispatcher.staticPlayerZ
            );
            f2 += entityPlayer.getEyeHeight() + 0.8F;
            float[] fArray = RenderUtils.worldToScreen(f, f2, f3);
            if (fArray != null && !(fArray[2] < 0.0F) && !(fArray[2] > 1.0F)) {
               String formattedText = entityPlayer.getDisplayName().getFormattedText();
               NameProtect nameProtect2 = ModuleManager.nameProtect;
               if (nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(2)) {
                  formattedText = nameProtect2.protectText(formattedText);
               }

               formattedText = formattedText + " | §c" + (int)entityPlayer.getHealth() + " ❤";
               if (this.elements.isSelected(2)) {
                  formattedText = formattedText + " §r| " + (int)minecraft.thePlayer.getDistanceToEntity(entityPlayer) + "m";
               }

               if (this.elements.isSelected(3)) {
                  NetworkPlayerInfo playerInfo = minecraft.getNetHandler().getPlayerInfo(entityPlayer.getUniqueID());
                  if (playerInfo != null) {
                     formattedText = formattedText + " §r| " + playerInfo.getResponseTime() + "ms";
                  }
               }

               float f4 = minecraft.fontRendererObj.getInt3(formattedText) / 2.0F;
               float f5 = minecraft.fontRendererObj.FONT_HEIGHT / 2.0F;
               GlStateManager.pushMatrix();
               GlStateManager.translate(fArray[0], fArray[1], 0.0F);
               if (this.addons.isSelected(0)) {
                  GlStateManager.scale(0.5F, 0.5F, 0.5F);
               }

               GlStateManager.translate(-fArray[0], -fArray[1], 0.0F);
               RelationType relationType = novoline.getRelationManager().get(entityPlayer.getName());
               int i = 1342177280;
               if (relationType != null) {
                  switch (relationType) {
                     case FRIEND:
                        i = this.friendColor.getColor();
                        break;
                     case TARGET:
                        i = this.targetColor.getColor();
                  }
               }

               float[] fArray2 = RenderUtils.unpackColor255(i);
               fArray2[3] = Math.max(0.0F, fArray2[3] - 16.0F);
               int i2 = RenderUtils.packColor(fArray2);
               if (this.addons.isSelected(1)) {
                  RenderUtils.drawShadow(fArray[0] - f4 - 2.0F, fArray[1] - f5 - 2.0F, fArray[0] + f4 + 2.0F, fArray[1] + f5 + 2.0F, 3.0F, i2);
               }

               RenderUtils.drawRect(fArray[0] - f4 - 2.0F, fArray[1] - f5 - 2.0F, fArray[0] + f4 + 2.0F, fArray[1] + f5 + 2.0F, i);
               minecraft.fontRendererObj.drawStringWithShadow(formattedText, fArray[0] - f4, fArray[1] - f5, -1);
               int i3 = 0;
               boolean flag = false;
               if (this.elements.isSelected(1)) {
                  ArrayList arrayList = new ArrayList();
                  arrayList.add(entityPlayer.getHeldItem());

                  for (ItemStack itemStack : entityPlayer.inventory.armorInventory) {
                     if (itemStack != null && itemStack.getItem() != null) {
                        arrayList.add(itemStack);
                     }
                  }

                  Iterator iterator2 = arrayList.iterator();
                  int i4 = 0;
                  GlStateManager.enableLighting();
                  GlStateManager.enableDepth();

                  while (iterator2.hasNext()) {
                     ItemStack itemStack2 = (ItemStack)iterator2.next();
                     if (itemStack2 != null && itemStack2.getItem() != null) {
                        flag = true;
                        float f6 = fArray[0] - f4 + i4 * 20;
                        float f7 = fArray[1] - f5 - 20.0F;
                        GlStateManager.translate(f6, f7, 0.0F);
                        minecraft.getRenderItem().renderItemAndEffectIntoGUI(itemStack2, 0, 0);
                        minecraft.getRenderItem().handleFontRenderer2(minecraft.fontRendererObj, itemStack2, 0, 0, null);
                        if (this.enchantments.isEnabled()) {
                           Map enchantments2 = EnchantmentHelper.getEnchantments(itemStack2);
                           if (enchantments2.size() > 0) {
                              if (i3 < enchantments2.size()) {
                                 i3 = enchantments2.size();
                              }

                              int i5 = 0;

                              for (java.util.Map.Entry entry : (Iterable<Map.Entry>) enchantments2.entrySet()) {
                                 if (entry != null) {
                                    Enchantment enchantmentById = Enchantment.getEnchantmentById((Integer)entry.getKey());
                                    if (enchantmentById != null) {
                                       String text = I18n.format(enchantmentById.getName());
                                       String text2 = text.substring(0, Math.min(2, text.length())) + entry.getValue();
                                       if (this.addons.isSelected(0)) {
                                          GlStateManager.scale(0.75F, 0.75F, 0.75F);
                                       }

                                       GlStateManager.disableLighting();
                                       minecraft.fontRendererObj.drawStringWithShadow(text2, 2.0F, -8 - i5, -1);
                                       GlStateManager.enableLighting();
                                       if (this.addons.isSelected(0)) {
                                          GlStateManager.scale(1.3333334F, 1.3333334F, 1.3333334F);
                                       }

                                       i5 += minecraft.fontRendererObj.FONT_HEIGHT;
                                    }
                                 }
                              }
                           }
                        }

                        GlStateManager.translate(-f6, -f7, 0.0F);
                        i4++;
                     }
                  }

                  GlStateManager.disableDepth();
                  GlStateManager.disableLighting();
               }

               if (this.elements.isSelected(0)) {
                  int i6 = 0;

                  for (PotionEffect potionEffect : entityPlayer.getActivePotionEffects()) {
                     String text3 = I18n.format(potionEffect.getEffectName()) + " " + (potionEffect.getAmplifier() + 1) + " §7(" + Potion.getDurationString(potionEffect) + ")";
                     minecraft.fontRendererObj
                        .drawStringWithShadow(
                           text3,
                           fArray[0] - f4,
                           fArray[1] - (flag ? 34 : 18) - i3 * minecraft.fontRendererObj.FONT_HEIGHT - i6 * minecraft.fontRendererObj.FONT_HEIGHT,
                           -1
                        );
                     i6++;
                  }
               }

               GlStateManager.popMatrix();
            }
         }
      }
   };

   public NameTags() {
      super(Category.VISUAL, "NameTags", "Draws player names");
   }
}
