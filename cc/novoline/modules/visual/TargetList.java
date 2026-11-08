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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;
import java.util.stream.Collectors;

import static cc.novoline.modules.PlayerManager.EnumPlayerType.TARGET;

public final class TargetList extends AbstractModule {

    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    @Property("tlist-x")
    private final IntProperty tlx = PropertyFactory.createInt((int) screenSize.getWidth() / 4).minimum(1).maximum((int) screenSize.getWidth() / 2);

    @Property("tlist-y")
    private final IntProperty tly = PropertyFactory.createInt((int) screenSize.getHeight() / 4).minimum(1).maximum((int) screenSize.getHeight() / 2);

    public TargetList(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "TargetList", "Target List", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays list of nearby targets");
        this.hidden.set(true);
        Manager.put(new Setting("TLIST_X", "X", SettingType.SLIDER, this, tlx, 5));
        Manager.put(new Setting("TLIST_Y", "Y", SettingType.SLIDER, this, tly, 5));
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (!(mc.currentScreen instanceof GuiChat)) {
            renderTargetsList();
        }
    }

    public void renderTargetsList() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        int yHeight = 13;
        int x = tlx.get();
        int y = tly.get();
        final List<Entity> targets = getLoadedTargets();
        int width = 0;

        for (Entity target : targets) {
            yHeight += mc.fontRendererCrack.getHeight() + 1;
            int stringWidth = mc.fontRendererCrack.getStringWidth(target.getName());

            if (stringWidth > width) {
                width = stringWidth;
            }
        }

        width = Math.max(100, width + 25);
        width += width == 100 ? 0 : 30;
        yHeight -= mc.fontRendererCrack.getHeight() + 1;

        if (targets.isEmpty()) {
            yHeight -= 2;
        }

        Gui.drawRect(x, y - 13, x + width, y + yHeight, new Color(29, 29, 29, 255).getRGB());
        Gui.drawRect(x + 1, y, x + width - 1, y + yHeight - 1, new Color(40, 40, 40, 255).getRGB());
        mc.fontRendererCrack.drawString("Your Targets", x + 3, y - 10, 0xffffffff, true);
        int yOffset = y;

        for (Entity target : targets) {
            mc.fontRendererCrack.drawString(target.getName(), x + 3, yOffset + 2, new Color(200, 200, 200, 255).getRGB());
            String s = (int) mc.player.getDistanceToEntity(target) + "m";
            mc.fontRendererCrack.drawString(s, x + width - 1 - mc.fontRendererCrack.getStringWidth(s) - 1, yOffset + 2, new Color(200, 200, 200, 255).getRGB());
            yOffset += mc.fontRendererCrack.getHeight() + 1;
        }
    }

    public double[] getTargetListWidthAndHeight() {
        if (mc.fontRendererCrack == null) {
            return new double[]{100, 13};
        }

        final List<Entity> targets = getLoadedTargets();
        int yHeight = 13;
        int width = 0;

        for (Entity target : targets) {
            yHeight += mc.fontRendererCrack.getHeight() + 1;
            int stringWidth = mc.fontRendererCrack.getStringWidth(target.getName());

            if (stringWidth > width) {
                width = stringWidth;
            }
        }

        width = Math.max(100, width + 25);
        width += width == 100 ? 0 : 30;
        yHeight -= mc.fontRendererCrack.getHeight() + 1;
        return new double[]{width, yHeight};
    }

    public List<Entity> getLoadedTargets() {
        if (mc.world == null) {
            return new ObjectArrayList<>();
        }
        final List<String> players = novoline.getPlayerManager().whoHas(TARGET);
        return mc.world.getLoadedEntityList().stream().filter(e -> players.contains(e.getName().toLowerCase()))
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    public IntProperty getTlx() {
        return tlx;
    }

    public IntProperty getTly() {
        return tly;
    }
}
