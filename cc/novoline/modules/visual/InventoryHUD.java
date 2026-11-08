package cc.novoline.modules.visual;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.gui.screen.setting.SettingType;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.IntProperty;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public final class InventoryHUD extends AbstractModule {

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    @Property("inv-x")
    private final IntProperty invx = PropertyFactory.createInt((int) screenSize.getWidth() / 4).minimum(1).maximum((int) screenSize.getWidth() / 2);

    @Property("inv-y")
    private final IntProperty invy = PropertyFactory.createInt((int) screenSize.getHeight() / 4).minimum(1).maximum((int) screenSize.getHeight() / 2);

    public InventoryHUD(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "InventoryHUD", "Inventory HUD", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays inventory contents on screen");
        this.hidden.set(true);
        Manager.put(new Setting("INV_X", "X", SettingType.SLIDER, this, invx, 5));
        Manager.put(new Setting("INV_Y", "Y", SettingType.SLIDER, this, invy, 5));
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!(mc.currentScreen instanceof GuiChat)) {
            drawInventory();
        }
    }

    public void drawInventory() {
        if (mc.player == null || mc.player.inventoryContainer == null) {
            return;
        }

        if (!(mc.currentScreen instanceof GuiChat)) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepth();
            int x = invx.get();
            int y = invy.get();
            Gui.drawRect(x, y, x + 167, y + 73, new Color(29, 29, 29, 255).getRGB());
            Gui.drawRect(x + 1, y + 13, x + 166, y + 72, new Color(40, 40, 40, 255).getRGB());
            mc.fontRendererCrack.drawString("Your Inventory", x + 3, y + 3, 0xffffffff, true);

            boolean hasStacks = false;
            for (int i1 = 9; i1 < mc.player.inventoryContainer.inventorySlots.size() - 9; ++i1) {
                Slot slot = mc.player.inventoryContainer.inventorySlots.get(i1);
                if (slot.getHasStack()) hasStacks = true;
                int i = slot.xDisplayPosition;
                int j = slot.yDisplayPosition;
                mc.getRenderItem().renderItemAndEffectIntoGUI(slot.getStack(), x + i - 4, y + j - 68);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererCrack, slot.getStack(), x + i - 4, y + j - 68, null);
            }
            if (mc.currentScreen instanceof GuiInventory) {
                mc.fontRendererCrack.drawString("Already in inventory",
                        x + 167 / 2 - mc.fontRendererCrack.getStringWidth("Already in inventory") / 2,
                        y + 72 / 2,
                        0xffffffff,
                        true);
            } else if (!hasStacks) {
                mc.fontRendererCrack.drawString("Empty...",
                        x + 167 / 2 - mc.fontRendererCrack.getStringWidth("Empty...") / 2,
                        y + 72 / 2,
                        0xffffffff,
                        true);
            }
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
        }
    }

    public IntProperty getInvx() {
        return invx;
    }

    public IntProperty getInvy() {
        return invy;
    }
}
