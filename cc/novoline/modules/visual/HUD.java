package cc.novoline.modules.visual;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.gui.screen.setting.Manager;
import cc.novoline.gui.screen.setting.Setting;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.modules.configurations.annotation.Property;
import cc.novoline.modules.configurations.property.object.*;
import cc.novoline.utils.ScaleUtils;
import cc.novoline.utils.fonts.impl.Fonts;
import cc.novoline.utils.notifications.NotificationRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cc.novoline.gui.screen.setting.SettingType.*;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.*;
import static java.lang.Integer.compare;
import static org.lwjgl.opengl.GL11.*;

public final class HUD extends AbstractModule {

    private int colorDelay = 11;
    private int colorLength = 110;
    private List<String> lastMessages = new CopyOnWriteArrayList<>();
    private cc.novoline.utils.fonts.api.FontRenderer font = Fonts.SF.SF_21.SF_21;

    /* properties @off */
    @Property("scoreboard")
    private final IntProperty scoreboard = createInt(0).minimum(-150).maximum(200);
    @Property("scale")
    private final FloatProperty fontScale = createFloat(1f).minimum(0.4f).maximum(3f);
    @Property("hud-elements")
    private final ListProperty<String> hudElements = createList("ModuleList", "Scoreboard", "Bossbar")
            .acceptableValues("ModuleList", "UserInfo", "HotBar", "TargetsList", "ArmorHUD", "Scoreboard", "Bossbar", "PotionHUD", "Coords", "Speed", "Name", "Time", "FPS", "Inventory");
    @Property("hud-font")
    private final StringProperty hudFont = createString("Client").acceptableValues("Client", "Vanilla");
    @Property("suffix-type")
    private final StringProperty suffixType = createString("Simple").acceptableValues("Simple", "Dash", "Bracket", "None");
    @Property("background-alpha")
    private final IntProperty backgroundAlpha = createInt(50).minimum(1).maximum(255);
    @Property("background-mode")
    private final StringProperty backgroundMode = createString("Bar").acceptableValues("Outline", "Simple", "Split", "Bar");
    @Property("hud-mode")
    private final StringProperty hudMode = createString("Static").acceptableValues("Rainbow", "Astolfo", "Static", "Dynamic", "Type");
    @Property("color")
    private final ColorProperty color = createColor(0xFF8A8AFF);
    @Property("notifications-sounds")
    private final BooleanProperty nSounds = PropertyFactory.booleanFalse();

    /* constructors @on */
    public HUD(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "HUD", "HUD", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Literally overlay", null);
        Manager.put(new Setting("HUDELEMENTS", "Elements", SELECTBOX, this, hudElements));
        Manager.put(new Setting("HUD_FONT", "Font", COMBOBOX, this, hudFont));
        Manager.put(new Setting("SUFFIX_TYPE", "Suffix", COMBOBOX, this, suffixType, () -> hudElements.get().contains("ModuleList")));
        Manager.put(new Setting("BACKGROUND_MODE", "Background", COMBOBOX, this, backgroundMode));
        Manager.put(new Setting("POTION_MODE", "ModuleList", COMBOBOX, this, hudMode, () -> hudElements.get().contains("ModuleList")));
        Manager.put(new Setting("HUD_COLOR", "Color", COLOR_PICKER, this, color, null));
        Manager.put(new Setting("FONT_SCALE", "HUD Scale", SLIDER, this, fontScale, 0.1, () -> hudElements.get().contains("ModuleList") && hudFont.equals("Client")));
        Manager.put(new Setting("SCOREBOARD_POS", "Scoreboard Height", SLIDER, this, scoreboard, 5, () -> hudElements.get().contains("Scoreboard")));
        Manager.put(new Setting("BACKGROUND_ALPHA", "Background Alpha", SLIDER, this, backgroundAlpha, 5, () -> hudElements.get().contains("ModuleList")));
        Manager.put(new Setting("NOTIFICATION-SOUNDS", "Notification sounds", CHECKBOX, this, nSounds));
    }

    @Override
    public void onEnable() {
    }

    public List<String> getLastMessages() {
        return lastMessages;
    }

    public int getArrayStatic(int counter, int alpha) {
        final float[] hudHSB = color.getHSB();
        Color color = Color.getHSBColor(hudHSB[0], hudHSB[1], hudHSB[2]);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }

    public FloatProperty getFontScale() {
        return fontScale;
    }

    public float getScale() {
        float s = 1 / getFontScale().get();
        switch (Minecraft.getInstance().gameSettings.guiScale) {
            case 0:
                return s / 2;
            case 1:
                return s / 0.5f;
            case 3:
                return s / 1.5f;
            default:
                return s;
        }
    }

    public int getArrayDynamic(float counter, int alpha) {
        float brightness = 1.0F - MathHelper.abs(MathHelper.sin(counter % 6000F / 6000F * (float) Math.PI * 2.0F) * 0.6F);
        final float[] hudHSB = color.getHSB();
        Color color = Color.getHSBColor(hudHSB[0], hudHSB[1], brightness);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }

    public int getArrayRainbow(int counter, int alpha) {
        final int width = colorLength;

        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / colorDelay;
        rainbowState %= 360;

        final float[] colors = color.getHSB();
        Color color = Color.getHSBColor((float) (rainbowState / 360), colors[1], colors[2]);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }

    public int getArrayType(int counter, float hue, int alpha) {
        final float[] hsb = color.getHSB();
        Color color = Color.getHSBColor(hue, hsb[1], hsb[2]);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }

    public int getArrayAstolfo(int counter, int alpha) {
        final int width = colorLength;
        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / colorDelay;
        rainbowState %= 360;
        final float hue = (float) (rainbowState / 360) < 0.5 ? -((float) (rainbowState / 360)) : (float) (rainbowState / 360);
        final float[] colors = color.getHSB();
        Color color = Color.getHSBColor(hue, colors[1], colors[2]);
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha).getRGB();
    }

    public int getHUDColor() {
        final float[] hsb = color.getHSB();
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB();
    }

    public int getArrayListColor(int counter, int alpha, float hue, float brightnesscounter) {
        int color = getArrayRainbow(counter, alpha);

        switch (hudMode.get().toLowerCase()) {
            case "type":
                color = getArrayType(counter, hue, alpha);
                break;

            case "astolfo":
                color = getArrayAstolfo(counter, alpha);
                break;

            case "static":
                color = getArrayStatic(counter, alpha);
                break;

            case "dynamic":
                color = getArrayDynamic(brightnesscounter, alpha);
        }

        return color;
    }

    @EventTarget
    public void drawHUD(Render2DEvent event) {
        glPushMatrix();
        ScaleUtils.scale(mc);
        if (mc.currentScreen == null) {
            NotificationRenderer.draw(event.getResolution());
        }
        glPopMatrix();

        glPushMatrix();
        GL11.glScaled(fontScale.get(), fontScale.get(), fontScale.get());

        if (hudElements.contains("ModuleList") && !mc.gameSettings.showDebugInfo) {
            drawArrayList(event);
        }

        glColor4f(1, 1, 1, 1);
        glPopMatrix();
    }

    public List<AbstractModule> getModules() {
        final FontRenderer font = mc.fontRendererObj;
        final String hudFont = this.hudFont.get();
        Stream<AbstractModule> stream = novoline.getModuleManager().getAbstractModules().stream();

        if (hudFont.equalsIgnoreCase("Vanilla")) {
            stream = stream.sorted((mod1, mod2) -> compare(font.getStringWidth(mod2.getFinalDisplayName()),
                    font.getStringWidth(mod1.getFinalDisplayName())));
        } else {
            stream = stream.sorted((mod1, mod2) -> compare(this.font.stringWidth(mod2.getFinalDisplayName()),
                    this.font.stringWidth(mod1.getFinalDisplayName())));
        }
        return stream.filter(m -> !m.isHidden()).collect(Collectors.toList());
    }

    public float getHeight(AbstractModule module) {
        float y = 2;
        for (AbstractModule abstractModule : getModules()) {
            if (abstractModule == module) {
                return y;
            }

            if (hudFont.equalsIgnoreCase("Vanilla")) {
                y += mc.fontRendererObj.getHeight() + 2;
            } else {
                y += font.getHeight() + 1;
            }
        }

        return 2;
    }

    @EventTarget
    public void onUpdate(Render2DEvent eventUpdate) {
        float y = hudFont.equals("Vanilla") ? -mc.fontRendererCrack.getHeight() + 1 : -font.getHeight() + 1;

        for (AbstractModule m : getModules()) {
            double clamp = MathHelper.clamp_double(mc.getDebugFPS() / 30, 1, 9999);

            if (m.isEnabled()) {
                m.offsetX = (float) (m.offsetX + (hudFont.equalsIgnoreCase("Vanilla") ?
                        mc.fontRendererObj.getStringWidth(m.getFinalDisplayName()) : font.stringWidth(m.getFinalDisplayName()) - m.offsetX) * (0.2 / clamp));
            } else if (m.offsetX < 0) {
                novoline.getModuleManager().getAbstractModules().remove(m);
            } else if (m.offsetX > -2) {
                m.offsetX = (float) (m.offsetX + (-2 - m.offsetX) * (0.2 / clamp));
            }
            m.offsetX = MathHelper.clamp_float(m.offsetX, -2, hudFont.equalsIgnoreCase("Vanilla") ?
                    mc.fontRendererObj.getStringWidth(m.getFinalDisplayName()) : font.stringWidth(m.getFinalDisplayName()));

            if (hudFont.equalsIgnoreCase("Vanilla")) {
                y += mc.fontRendererObj.getHeight() + 1.0F;
            } else {
                y += font.getHeight() + 1F;
            }

            m.offsetY = (float) (m.offsetY + (y - m.offsetY) * (0.2 / clamp));
        }
    }

    private void drawArrayList(Render2DEvent event) {
        int counter = 1;
        float j = Minecraft.getSystemTime();
        float y;
        final cc.novoline.utils.fonts.api.FontRenderer cFont = font;
        final String hudFont = this.hudFont.get();

        final List<AbstractModule> modules = getModules();

        if (isEnabled() && hudElements.get().contains("ModuleList")) {
            for (AbstractModule module : modules) {
                y = module.offsetY;
                /* Позиция для рисования шрифта */
                float x = (event.getResolution().getScaledWidthStatic(mc) * getScale()) - module.offsetX - 2.0F;
                /* Цвет бг */
                int bgColor = new Color(0, 0, 0, backgroundAlpha.get()).getRGB();
                /* Цвет шрифта */
                int modColor = getArrayListColor(counter, 255, module.categoryColor(), j);
                /* Ширина окна майна */
                float width = event.getResolution().getScaledWidthStatic(mc) * getScale();
                /* Высота Шрифта */
                float fontHeight = hudFont.equalsIgnoreCase("Vanilla") ? mc.fontRendererObj.getHeight() : cFont.getHeight();
                /* Отступы */
                float bgMargin = backgroundMode.get().equalsIgnoreCase("bar") ? 2.5f : 1.5f;

                if (backgroundMode.get().equalsIgnoreCase("outline") || backgroundMode.get().equalsIgnoreCase("split")) {
                    Gui.drawRect(
                            width - (module.offsetX + 6) - bgMargin,
                            y - (modules.indexOf(module) == 0 ? 2 : 0),
                            width - (module.offsetX + 5) - bgMargin,
                            y + fontHeight + 2, modColor);
                }
                if (backgroundMode.get().equalsIgnoreCase("outline")) {
                    float difference;

                    if (modules.indexOf(module) == modules.size() - 1) {
                        // нижняя часть
                        Gui.drawRect(
                                width - (module.offsetX + 6) - bgMargin,
                                y + fontHeight + 2,
                                width,
                                y + fontHeight + 3, modColor);
                    } else {
                        if (hudFont.equalsIgnoreCase("Vanilla")) {
                            difference = Math.abs(mc.fontRendererObj.getStringWidth(modules.get(modules.indexOf(module)).getFinalDisplayName()) -
                                    mc.fontRendererObj.getStringWidth(modules.get(modules.indexOf(module) + 1).getFinalDisplayName()));
                        } else {
                            difference = Math.abs(cFont.stringWidth(modules.get(modules.indexOf(module)).getFinalDisplayName()) -
                                    cFont.stringWidth(modules.get(modules.indexOf(module) + 1).getFinalDisplayName()));
                        }

                        // нижняя часть
                        Gui.drawRect(
                                width - (module.offsetX + 5) - bgMargin,
                                y + fontHeight + 1,
                                width - (module.offsetX + 5) - bgMargin + difference,
                                y + fontHeight + 2, modColor);
                    }
                }

                // сам бг
                Gui.drawRect(
                        width - (module.offsetX + 4) - bgMargin - (backgroundMode.equalsIgnoreCase("outline") || backgroundMode.equalsIgnoreCase("split") ? 1 : 0),
                        y - (modules.indexOf(module) == 0 ? 3 : 0),
                        width,
                        y + fontHeight + (modules.indexOf(module) == modules.size() - 1 ? 2 : 1), bgColor);

                // Текст
                if (hudFont.equalsIgnoreCase("Vanilla")) {
                    mc.fontRendererObj.drawStringWithShadow(module.getFinalDisplayName(), x - bgMargin, y + 1.0F, modColor);
                } else {
                    cFont.drawString(module.getFinalDisplayName(), x - bgMargin, y + 1.0F, modColor, true);
                }

                if (backgroundMode.get().equalsIgnoreCase("Bar")) {
                    Gui.drawRect(width - 1.0F, y - 1, width, y + fontHeight + (modules.indexOf(module) == modules.size() - 1 ? 2 : 1), modColor);
                }
                j -= 300;
                counter++;
            }
        }
    }

    /* Delegator methods for backward compatibility */
    public IntProperty getInvx() {
        InventoryHUD inv = getModule(InventoryHUD.class);
        return inv != null ? inv.getInvx() : null;
    }

    public IntProperty getInvy() {
        InventoryHUD inv = getModule(InventoryHUD.class);
        return inv != null ? inv.getInvy() : null;
    }

    public void drawInventory() {
        InventoryHUD inv = getModule(InventoryHUD.class);
        if (inv != null) {
            inv.drawInventory();
        }
    }

    public IntProperty getTlx() {
        TargetList tl = getModule(TargetList.class);
        return tl != null ? tl.getTlx() : null;
    }

    public IntProperty getTly() {
        TargetList tl = getModule(TargetList.class);
        return tl != null ? tl.getTly() : null;
    }

    public void renderTargetsList() {
        TargetList tl = getModule(TargetList.class);
        if (tl != null) {
            tl.renderTargetsList();
        }
    }

    public double[] getTargetListWidthAndHeight() {
        TargetList tl = getModule(TargetList.class);
        return tl != null ? tl.getTargetListWidthAndHeight() : new double[]{100, 13};
    }

    public StringProperty getClientName() {
        Watermark wm = getModule(Watermark.class);
        return wm != null ? wm.getClientName() : null;
    }

    public IntProperty getScoreboard() {
        return scoreboard;
    }

    public ListProperty<String> getHudElements() {
        return hudElements;
    }

    public StringProperty getHudFont() {
        return hudFont;
    }

    public StringProperty getSuffixType() {
        return suffixType;
    }

    public IntProperty getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public StringProperty getBackgroundMode() {
        return backgroundMode;
    }

    public BooleanProperty getnSounds() {
        return nSounds;
    }

    public StringProperty getHudMode() {
        return hudMode;
    }

    public Color getColor() {
        float[] hsb = color.getHSB();
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

    public cc.novoline.utils.fonts.api.FontRenderer getFont() {
        return font;
    }

    public boolean getChatStyle() {
        return false;
    }

    public boolean getScoreBoardStyle() {
        return false;
    }
}
