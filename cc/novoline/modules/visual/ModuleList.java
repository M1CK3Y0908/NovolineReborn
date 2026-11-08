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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cc.novoline.gui.screen.setting.SettingType.*;
import static cc.novoline.modules.configurations.property.object.PropertyFactory.*;
import static java.lang.Integer.compare;
import static org.lwjgl.opengl.GL11.*;

public final class ModuleList extends AbstractModule {

    private final int colorDelay = 11;
    private final int colorLength = 110;
    private final cc.novoline.utils.fonts.api.FontRenderer font = Fonts.SF.SF_21.SF_21;

    /* properties */
    @Property("scale")
    private final FloatProperty fontScale = createFloat(1.0F).minimum(0.4F).maximum(3.0F);

    @Property("hud-font")
    private final StringProperty hudFont = createString("Client").acceptableValues("Client", "Vanilla");

    @Property("suffix-type")
    private final StringProperty suffixType = createString("Simple").acceptableValues("Simple", "Dash", "Bracket", "None");

    @Property("background-alpha")
    private final IntProperty backgroundAlpha = createInt(50).minimum(1).maximum(255);

    @Property("background-mode")
    private final StringProperty backgroundMode = createString("Bar").acceptableValues("Outline", "Simple", "Split", "Bar");

    @Property("color-mode")
    private final StringProperty colorMode = createString("Static").acceptableValues("Rainbow", "Astolfo", "Static", "Dynamic", "Type");

    @Property("color")
    private final ColorProperty color = createColor(0xFF8A8AFF);

    @Property("scoreboard-pos")
    private final IntProperty scoreboardPos = createInt(0).minimum(-150).maximum(200);

    @Property("scoreboard")
    private final BooleanProperty scoreboard = PropertyFactory.booleanTrue();

    @Property("bossbar")
    private final BooleanProperty bossbar = PropertyFactory.booleanTrue();

    @Property("notifications-sounds")
    private final BooleanProperty nSounds = PropertyFactory.booleanFalse();

    public ModuleList(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "ModuleList", "Module List", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays active modules");
        this.hidden.set(true);
        Manager.put(new Setting("ML_FONT", "Font", COMBOBOX, this, hudFont));
        Manager.put(new Setting("ML_SUFFIX_TYPE", "Suffix", COMBOBOX, this, suffixType));
        Manager.put(new Setting("ML_BACKGROUND_MODE", "Background", COMBOBOX, this, backgroundMode));
        Manager.put(new Setting("ML_COLOR_MODE", "Color Mode", COMBOBOX, this, colorMode));
        Manager.put(new Setting("ML_COLOR", "Color", COLOR_PICKER, this, color, null));
        Manager.put(new Setting("ML_FONT_SCALE", "Scale", SLIDER, this, fontScale, 0.1, () -> hudFont.equals("Client")));
        Manager.put(new Setting("ML_BACKGROUND_ALPHA", "Background Alpha", SLIDER, this, backgroundAlpha, 5));
        Manager.put(new Setting("ML_SCOREBOARD_POS", "Scoreboard Height", SLIDER, this, scoreboardPos, 5, scoreboard::get));
        Manager.put(new Setting("ML_SCOREBOARD", "Scoreboard", CHECKBOX, this, scoreboard));
        Manager.put(new Setting("ML_BOSSBAR", "Bossbar", CHECKBOX, this, bossbar));
        Manager.put(new Setting("ML_NOTIFICATION_SOUNDS", "Notification sounds", CHECKBOX, this, nSounds));
    }

    public int getArrayStatic(int counter, int alpha) {
        final float[] hudHSB = color.getHSB();
        Color c = Color.getHSBColor(hudHSB[0], hudHSB[1], hudHSB[2]);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public FloatProperty getFontScale() {
        return fontScale;
    }

    public float getScale() {
        float s = 1.0F / getFontScale().get();
        switch (Minecraft.getInstance().gameSettings.guiScale) {
            case 0:
                return s / 2.0F;
            case 1:
                return s / 0.5F;
            case 3:
                return s / 1.5F;
            default:
                return s;
        }
    }

    public int getArrayDynamic(float counter, int alpha) {
        float brightness = 1.0F - MathHelper.abs(MathHelper.sin(counter % 6000.0F / 6000.0F * (float) Math.PI * 2.0F) * 0.6F);
        final float[] hudHSB = color.getHSB();
        Color c = Color.getHSBColor(hudHSB[0], hudHSB[1], brightness);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public int getArrayRainbow(int counter, int alpha) {
        final int width = colorLength;
        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / colorDelay;
        rainbowState %= 360;

        final float[] colors = color.getHSB();
        Color c = Color.getHSBColor((float) (rainbowState / 360), colors[1], colors[2]);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public int getArrayType(int counter, float hue, int alpha) {
        final float[] hsb = color.getHSB();
        Color c = Color.getHSBColor(hue, hsb[1], hsb[2]);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public int getArrayAstolfo(int counter, int alpha) {
        final int width = colorLength;
        double rainbowState = Math.ceil(System.currentTimeMillis() - (long) counter * width) / colorDelay;
        rainbowState %= 360;
        final float hue = (float) (rainbowState / 360) < 0.5F ? -((float) (rainbowState / 360)) : (float) (rainbowState / 360);
        final float[] colors = color.getHSB();
        Color c = Color.getHSBColor(hue, colors[1], colors[2]);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public int getHUDColor() {
        final float[] hsb = color.getHSB();
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]).getRGB();
    }

    public int getArrayListColor(int counter, int alpha, float hue, float brightnesscounter) {
        int c = getArrayRainbow(counter, alpha);

        switch (colorMode.get().toLowerCase()) {
            case "type":
                c = getArrayType(counter, hue, alpha);
                break;
            case "astolfo":
                c = getArrayAstolfo(counter, alpha);
                break;
            case "static":
                c = getArrayStatic(counter, alpha);
                break;
            case "dynamic":
                c = getArrayDynamic(brightnesscounter, alpha);
                break;
        }

        return c;
    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        glPushMatrix();
        ScaleUtils.scale(mc);
        if (mc.currentScreen == null) {
            NotificationRenderer.draw(event.getResolution());
        }
        glPopMatrix();

        if (mc.gameSettings.showDebugInfo) {
            return;
        }

        glPushMatrix();
        GL11.glScaled(fontScale.get(), fontScale.get(), fontScale.get());

        drawArrayList(event);

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glPopMatrix();
    }

    public List<AbstractModule> getModules() {
        final FontRenderer fontObj = mc.fontRendererObj;
        final String currentFont = this.hudFont.get();
        Stream<AbstractModule> stream = novoline.getModuleManager().getAbstractModules().stream();

        if (currentFont.equalsIgnoreCase("Vanilla")) {
            stream = stream.sorted((mod1, mod2) -> compare(fontObj.getStringWidth(mod2.getFinalDisplayName()),
                    fontObj.getStringWidth(mod1.getFinalDisplayName())));
        } else {
            stream = stream.sorted((mod1, mod2) -> compare(this.font.stringWidth(mod2.getFinalDisplayName()),
                    this.font.stringWidth(mod1.getFinalDisplayName())));
        }
        return stream.filter(m -> !m.isHidden()).collect(Collectors.toList());
    }

    public float getHeight(AbstractModule module) {
        float y = 2.0F;
        for (AbstractModule abstractModule : getModules()) {
            if (abstractModule == module) {
                return y;
            }

            if (hudFont.equalsIgnoreCase("Vanilla")) {
                y += mc.fontRendererObj.getHeight() + 2.0F;
            } else {
                y += font.getHeight() + 1.0F;
            }
        }

        return 2.0F;
    }

    @EventTarget
    public void onUpdate(Render2DEvent eventUpdate) {
        float y = hudFont.equals("Vanilla") ? -mc.fontRendererCrack.getHeight() + 1.0F : -font.getHeight() + 1.0F;

        for (AbstractModule m : getModules()) {
            double clamp = MathHelper.clamp_double(mc.getDebugFPS() / 30.0, 1.0, 9999.0);

            if (m.isEnabled()) {
                m.offsetX = (float) (m.offsetX + (hudFont.equalsIgnoreCase("Vanilla") ?
                        mc.fontRendererObj.getStringWidth(m.getFinalDisplayName()) : font.stringWidth(m.getFinalDisplayName()) - m.offsetX) * (0.2 / clamp));
            } else if (m.offsetX < 0) {
                novoline.getModuleManager().getAbstractModules().remove(m);
            } else if (m.offsetX > -2) {
                m.offsetX = (float) (m.offsetX + (-2.0F - m.offsetX) * (0.2 / clamp));
            }
            m.offsetX = MathHelper.clamp_float(m.offsetX, -2.0F, hudFont.equalsIgnoreCase("Vanilla") ?
                    mc.fontRendererObj.getStringWidth(m.getFinalDisplayName()) : font.stringWidth(m.getFinalDisplayName()));

            if (hudFont.equalsIgnoreCase("Vanilla")) {
                y += mc.fontRendererObj.getHeight() + 1.0F;
            } else {
                y += font.getHeight() + 1.0F;
            }

            m.offsetY = (float) (m.offsetY + (y - m.offsetY) * (0.2 / clamp));
        }
    }

    private void drawArrayList(Render2DEvent event) {
        int counter = 1;
        float j = Minecraft.getSystemTime();
        float y;
        final cc.novoline.utils.fonts.api.FontRenderer cFont = font;
        final String currentFont = this.hudFont.get();

        final List<AbstractModule> modules = getModules();

        for (AbstractModule module : modules) {
            y = module.offsetY;
            float x = (event.getResolution().getScaledWidthStatic(mc) * getScale()) - module.offsetX - 2.0F;
            int bgColor = new Color(0, 0, 0, backgroundAlpha.get()).getRGB();
            int modColor = getArrayListColor(counter, 255, module.categoryColor(), j);
            float width = event.getResolution().getScaledWidthStatic(mc) * getScale();
            float fontHeight = currentFont.equalsIgnoreCase("Vanilla") ? mc.fontRendererObj.getHeight() : cFont.getHeight();
            float bgMargin = backgroundMode.get().equalsIgnoreCase("bar") ? 2.5F : 1.5F;

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
                    Gui.drawRect(
                            width - (module.offsetX + 6) - bgMargin,
                            y + fontHeight + 2,
                            width,
                            y + fontHeight + 3, modColor);
                } else {
                    if (currentFont.equalsIgnoreCase("Vanilla")) {
                        difference = Math.abs(mc.fontRendererObj.getStringWidth(modules.get(modules.indexOf(module)).getFinalDisplayName()) -
                                mc.fontRendererObj.getStringWidth(modules.get(modules.indexOf(module) + 1).getFinalDisplayName()));
                    } else {
                        difference = Math.abs(cFont.stringWidth(modules.get(modules.indexOf(module)).getFinalDisplayName()) -
                                cFont.stringWidth(modules.get(modules.indexOf(module) + 1).getFinalDisplayName()));
                    }

                    Gui.drawRect(
                            width - (module.offsetX + 5) - bgMargin,
                            y + fontHeight + 1,
                            width - (module.offsetX + 5) - bgMargin + difference,
                            y + fontHeight + 2, modColor);
                }
            }

            Gui.drawRect(
                    width - (module.offsetX + 4) - bgMargin - (backgroundMode.equalsIgnoreCase("outline") || backgroundMode.equalsIgnoreCase("split") ? 1 : 0),
                    y - (modules.indexOf(module) == 0 ? 3 : 0),
                    width,
                    y + fontHeight + (modules.indexOf(module) == modules.size() - 1 ? 2 : 1), bgColor);

            if (currentFont.equalsIgnoreCase("Vanilla")) {
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

    public IntProperty getScoreboardPos() {
        return scoreboardPos;
    }

    public IntProperty getScoreboard() {
        return scoreboardPos;
    }

    public BooleanProperty getScoreboardProperty() {
        return scoreboard;
    }

    public boolean isShowScoreboard() {
        return scoreboard.get();
    }

    public BooleanProperty getBossbarProperty() {
        return bossbar;
    }

    public boolean isShowBossbar() {
        return bossbar.get();
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

    public StringProperty getColorMode() {
        return colorMode;
    }

    public StringProperty getHudMode() {
        return colorMode;
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
