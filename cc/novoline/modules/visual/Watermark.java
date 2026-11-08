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
import cc.novoline.modules.configurations.property.object.BooleanProperty;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import cc.novoline.modules.configurations.property.object.StringProperty;
import cc.novoline.utils.fonts.impl.Fonts;
import net.minecraft.client.Minecraft;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.minecraft.util.EnumChatFormatting.GRAY;
import static net.minecraft.util.EnumChatFormatting.WHITE;

public final class Watermark extends AbstractModule {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    @Property("client-name")
    private final StringProperty clientName = PropertyFactory.createString("Novoline");

    @Property("show-time")
    private final BooleanProperty showTime = PropertyFactory.booleanTrue();

    public Watermark(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Watermark", "Watermark", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Watermark and time display");
        this.hidden.set(true);
        Manager.put(new Setting("WM_CLIENT_NAME", "Client Name", SettingType.TEXTBOX, this, "Client name", clientName));
        Manager.put(new Setting("WM_SHOW_TIME", "Show Time", SettingType.CHECKBOX, this, showTime));
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        HUD hud = getModule(HUD.class);
        if (hud == null) {
            return;
        }

        float scale = hud.getFontScale().get();

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);

        drawWatermark(hud);

        if (showTime.get() && !isExhibition()) {
            drawTime(hud);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawWatermark(HUD hud) {
        long j = Minecraft.getSystemTime();
        final String nameValue = this.clientName.get();
        String name = !nameValue.isEmpty() ? nameValue.charAt(0) + nameValue.substring(1) : "Novoline";
        int x = 1;

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            for (int i = 0; i < name.length(); i++) {
                Fonts.SF.SF_20.SF_20.drawString(String.valueOf(name.charAt(i)), x, 4.5F, hud.getArrayDynamic(j, 255), true);
                x += Fonts.SF.SF_20.SF_20.charWidth(name.charAt(i));
                j -= 300;
            }
        } else {
            for (int i = 0; i < name.length(); i++) {
                mc.fontRendererObj.drawString(String.valueOf(name.charAt(i)), x, 4.5F, hud.getArrayDynamic(j, 255), true);
                x += mc.fontRendererObj.getCharWidth(name.charAt(i));
                j -= 300;
            }
        }
    }

    private void drawTime(HUD hud) {
        int x = 3, pos = 1;
        final String nameValue = this.clientName.get();
        String name = !nameValue.isEmpty() ? nameValue.charAt(0) + nameValue.substring(1) : "Novoline";

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            for (int i = 0; i < name.length(); i++) {
                pos += Fonts.SF.SF_20.SF_20.charWidth(name.charAt(i));
            }
            x = !nameValue.isEmpty() ? pos + 2 : 40;
            Fonts.SF.SF_20.SF_20.drawString(GRAY + "(" + WHITE + dateFormat.format(new Date()) + GRAY + ")", (float) x, 4.0F, hud.getHUDColor(), true);
        } else {
            for (int i = 0; i < name.length(); i++) {
                pos += mc.fontRendererObj.getCharWidth(name.charAt(i));
            }
            x = !nameValue.isEmpty() ? pos + 2 : 44;
            mc.fontRendererObj.drawStringWithShadow(GRAY + "(" + WHITE + dateFormat.format(new Date()) + GRAY + ")", x, 4.5F, hud.getHUDColor());
        }
    }

    private boolean isExhibition() {
        return clientName.get().equalsIgnoreCase("Exhibition");
    }

    public StringProperty getClientName() {
        return clientName;
    }

    public BooleanProperty getShowTime() {
        return showTime;
    }
}
