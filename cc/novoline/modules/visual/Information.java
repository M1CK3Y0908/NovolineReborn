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
import cc.novoline.modules.configurations.property.object.ListProperty;
import cc.novoline.modules.configurations.property.object.PropertyFactory;
import cc.novoline.utils.fonts.api.FontRenderer;
import cc.novoline.utils.fonts.impl.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import static net.minecraft.util.EnumChatFormatting.*;

public final class Information extends AbstractModule {

    private final FontRenderer font = Fonts.SF.SF_21.SF_21;

    @Property("info-elements")
    private final ListProperty<String> infoElements = PropertyFactory.createList("FPS", "Speed", "Coords", "UserInfo")
            .acceptableValues("FPS", "Speed", "Coords", "UserInfo");

    public Information(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "Information", "Information", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays FPS, Speed, Coordinates and User Info");
        this.hidden.set(true);
        Manager.put(new Setting("INFO_ELEMENTS", "Elements", SettingType.SELECTBOX, this, infoElements));
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

        if (infoElements.contains("UserInfo")) {
            drawUserInfo(event, hud);
        }

        if (infoElements.contains("FPS")) {
            drawFPS(event, hud);
        }

        if (infoElements.contains("Coords")) {
            drawCoordinates(event, hud);
        }

        if (infoElements.contains("Speed")) {
            drawSpeed(event, hud);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawUserInfo(Render2DEvent render2DEvent, HUD hud) {
        String build = "Build - " + RESET + novoline.getVersion();
        String prefix = GRAY + build + GRAY + " | " + "UID - ";
        String uid = "M1CK3Y0908";

        long j = Minecraft.getSystemTime();

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            float uidWidth = 0;
            for (int i = 0; i < uid.length(); i++) {
                uidWidth += font.charWidth(uid.charAt(i));
            }
            float totalWidth = font.stringWidth(prefix) + uidWidth;
            float x = (float) ((render2DEvent.getResolution().getScaledWidthStatic(mc) * hud.getScale()) - totalWidth - 2);
            float y = (float) ((render2DEvent.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - font.getHeight() - 2);

            font.drawString(prefix, x, y, 0xFFFFFF, true);
            x += font.stringWidth(prefix);

            for (int i = 0; i < uid.length(); i++) {
                font.drawString(String.valueOf(uid.charAt(i)), x, y, hud.getArrayDynamic(j, 255), true);
                x += font.charWidth(uid.charAt(i));
                j -= 300;
            }
        } else {
            int uidWidth = 0;
            for (int i = 0; i < uid.length(); i++) {
                uidWidth += mc.fontRendererObj.getCharWidth(uid.charAt(i));
            }
            int totalWidth = mc.fontRendererObj.getStringWidth(prefix) + uidWidth;
            float x = (render2DEvent.getResolution().getScaledWidthStatic(mc) * hud.getScale()) - totalWidth - 1;
            float y = (render2DEvent.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - mc.fontRendererObj.getHeight() - 1;

            mc.fontRendererObj.drawStringWithShadow(prefix, x, y, 0xFFFFFF);
            x += mc.fontRendererObj.getStringWidth(prefix);

            for (int i = 0; i < uid.length(); i++) {
                mc.fontRendererObj.drawString(String.valueOf(uid.charAt(i)), x, y, hud.getArrayDynamic(j, 255), true);
                x += mc.fontRendererObj.getCharWidth(uid.charAt(i));
                j -= 300;
            }
        }
    }

    private void drawCoordinates(Render2DEvent event, HUD hud) {
        String xyz = String.format("\u00A7rXYZ:\u00A7f %.0f %.0f %.0f", mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ);

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            font.drawString(xyz, 1.0F, (event.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - (hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() + 1 : font.getHeight() * 1.2F), hud.getHUDColor(), true);
        } else {
            mc.fontRendererObj.drawStringWithShadow(xyz, 2.0F, (event.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - 10.0F, hud.getHUDColor());
        }
    }

    private void drawFPS(@NonNull Render2DEvent event, HUD hud) {
        final float y = (event.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - (infoElements.contains("Coords") ? infoElements.contains("Speed") ? hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() * 3 + 2 : font.getHeight() * 3.2F + 3 : hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() * 2 + 2 : font.getHeight() * 2.2F + 1 : infoElements.contains("Speed") ? hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() * 2 + 2 : font.getHeight() * 2.2F + 2 : hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() + 2 : font.getHeight() * 1.2F + 1);
        final String fps = RESET + "FPS: " + WHITE + mc.getDebugFPS();

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            font.drawString(fps, 1.0F, y, hud.getHUDColor(), true);
        } else {
            mc.fontRendererObj.drawStringWithShadow(fps, 2.0F, y, hud.getHUDColor());
        }
    }

    private void drawSpeed(@NonNull Render2DEvent event, HUD hud) {
        final float y = (event.getResolution().getScaledHeightStatic(mc) * hud.getScale()) - (infoElements.contains("Coords") ? hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() * 2 + 2 : font.getHeight() * 2.2F + 2 : hud.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() * 2 + 2 : font.getHeight() * 1.2F + 1);
        String speed = String.format("%.2f", mc.player.getLastTickDistance() * 20 * (mc.timer.timerSpeed < 1 ? 1 : mc.timer.timerSpeed));

        if (hud.getHudFont().equalsIgnoreCase("Client")) {
            font.drawString("Speed: " + EnumChatFormatting.WHITE + speed + " b/s", 1.0F, y, hud.getHUDColor(), true);
        } else {
            mc.fontRendererObj.drawStringWithShadow("Speed: " + EnumChatFormatting.WHITE + speed + " b/s", 2.0F, y, hud.getHUDColor());
        }
    }

    public ListProperty<String> getInfoElements() {
        return infoElements;
    }
}
