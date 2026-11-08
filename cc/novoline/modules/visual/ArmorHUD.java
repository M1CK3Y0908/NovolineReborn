package cc.novoline.modules.visual;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.utils.fonts.impl.Fonts;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import static net.minecraft.block.material.Material.water;
import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting;
import static net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting;

public final class ArmorHUD extends AbstractModule {

    public ArmorHUD(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "ArmorHUD", "Armor HUD", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays equipped armor and held items");
        this.hidden.set(true);
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        ModuleList moduleList = getModule(ModuleList.class);
        if (moduleList == null) {
            return;
        }

        float scale = moduleList.getFontScale().get();

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);

        drawArmorHUD(event, moduleList);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawArmorHUD(Render2DEvent event, ModuleList moduleList) {
        final int color = moduleList.getHUDColor();
        glPushMatrix();

        final List<ItemStack> stuff = new ObjectArrayList<>();
        final boolean onWater = mc.player != null && mc.player.isEntityAlive() && mc.player.isInsideOfMaterial(water);

        if (mc.player != null) {
            for (int index = 3; index >= 0; --index) {
                final ItemStack armor = mc.player.inventory.armorInventory[index];
                if (armor != null) stuff.add(armor);
            }

            if (mc.player.getCurrentEquippedItem() != null) stuff.add(mc.player.getCurrentEquippedItem());
        }

        int split = -3;
        int split2 = -3;

        for (ItemStack item : stuff) {
            if (mc.world != null) {
                enableGUIStandardItemLighting();
                split += 16;
                split2 += 32;
            }

            final int damage = item.getMaxDamage() - item.getItemDamage();
            final String hudFont = moduleList.getHudFont().get();

            final ScaledResolution resolution = event.getResolution();
            final int scaledWidth = (int) (resolution.getScaledWidthStatic(mc) * moduleList.getScale());
            final int scaledHeight = (int) (resolution.getScaledHeightStatic(mc) * moduleList.getScale());

            if (!item.isStackable()) {
                disableStandardItemLighting();
                if (!(item.getItem() instanceof ItemPotion)) {
                    if (hudFont.equalsIgnoreCase("Client")) {
                        float v1 = (float) (scaledHeight - (onWater ? 68 : 58));
                        Fonts.SFTHIN.SFTHIN_10.SFTHIN_10.drawString(String.valueOf(damage), split + scaledWidth / 2F - 1.5F, v1, color, true);
                    } else {
                        glPushMatrix();
                        glScalef(0.5F, 0.5F, 0.5F);
                        mc.fontRendererObj.drawStringWithShadow(String.valueOf(damage), split2 + scaledWidth - 4F,
                                (float) (scaledHeight * 2 - (onWater ? 139 : 119)), color);
                        glScalef(2.0F, 2.0F, 2.0F);
                        glPopMatrix();
                    }
                }
            }

            pushMatrix();
            disableAlpha();
            clear(256);
            enableBlend();

            mc.getRenderItem().zLevel = -150.0F;
            mc.getRenderItem().renderItemAndEffectIntoGUI(item, split + (int) (scaledWidth / 2F) - 4F,
                    scaledHeight - (onWater ? 66.5F : 56.5F));

            if (item.isStackable()) {
                disableStandardItemLighting();
                if (hudFont.equalsIgnoreCase("Client")) {
                    float v1 = (float) (scaledHeight - (onWater ? 68 : 58));
                    Fonts.SFTHIN.SFTHIN_10.SFTHIN_10.drawString(String.valueOf(item.stackSize), split + scaledWidth / 2F - 1.5F, v1, color, true);
                } else {
                    glPushMatrix();
                    glScalef(0.5F, 0.5F, 0.5F);
                    mc.fontRendererObj.drawStringWithShadow(String.valueOf(item.stackSize), split2 + scaledWidth - 4F,
                            (float) (scaledHeight * 2 - (onWater ? 139 : 119)), color);
                    glScalef(2.0F, 2.0F, 2.0F);
                    glPopMatrix();
                }
            }

            mc.getRenderItem().zLevel = 0.0F;

            enableBlend();

            final float z = 0.5F;

            scale(z, z, z);
            disableDepth();
            disableLighting();
            enableDepth();
            scale(2.0F, 2.0F, 2.0F);
            enableAlpha();
            popMatrix();
            item.getEnchantmentTagList();
        }

        glPopMatrix();
    }
}
