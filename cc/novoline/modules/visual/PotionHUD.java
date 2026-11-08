package cc.novoline.modules.visual;

import cc.novoline.events.EventTarget;
import cc.novoline.events.events.Render2DEvent;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.EnumModuleType;
import cc.novoline.modules.ModuleManager;
import cc.novoline.utils.fonts.api.FontRenderer;
import cc.novoline.utils.fonts.impl.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public final class PotionHUD extends AbstractModule {

    private final FontRenderer font = Fonts.SF.SF_21.SF_21;

    public PotionHUD(@NonNull ModuleManager moduleManager) {
        super(moduleManager, "PotionHUD", "Potion HUD", Keyboard.KEY_NONE, EnumModuleType.VISUALS, "Displays active potion effects");
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

        drawPotionHUD(event, moduleList);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void drawPotionHUD(Render2DEvent event, ModuleList moduleList) {
        if (mc.player == null) {
            return;
        }

        float y = 0;
        int counter = 1;
        float j = Minecraft.getSystemTime();

        Information info = getModule(Information.class);
        boolean hasUserInfo = info != null && info.isEnabled() && info.getInfoElements().contains("UserInfo");

        for (PotionEffect effect : mc.player.getActivePotionEffects()) {
            final Potion potion = Potion.potionTypes[effect.getPotionID()];
            final StringBuilder pTypeBuilder = new StringBuilder(I18n.format(potion.getName()));

            switch (effect.getAmplifier()) {
                case 1:
                    pTypeBuilder.append(" II");
                    break;
                case 2:
                    pTypeBuilder.append(" III");
                    break;
                case 3:
                    pTypeBuilder.append(" IV");
                    break;
                case 4:
                    pTypeBuilder.append(" V");
                    break;
            }

            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                pTypeBuilder.append("\2477\2476 ").append(Potion.getDurationString(effect));
            } else if (effect.getDuration() < 300) {
                pTypeBuilder.append("\2477\247c ").append(Potion.getDurationString(effect));
            } else if (effect.getDuration() > 600) {
                pTypeBuilder.append("\2477\2477 ").append(Potion.getDurationString(effect));
            }

            final int yChat = mc.ingameGUI.getChatGUI().getChatOpen() && !hasUserInfo ? 5 : -10;
            final String pType = String.valueOf(pTypeBuilder);
            final Color color = new Color(potion.getLiquidColor());
            final float[] newColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), new float[3]);

            int rgbColor = moduleList.getArrayListColor(counter, 255, newColor[0], j);

            final ScaledResolution resolution = event.getResolution();
            final int scaledWidth = (int) (resolution.getScaledWidthStatic(mc) * moduleList.getScale());
            final int scaledHeight = (int) (resolution.getScaledHeightStatic(mc) * moduleList.getScale());

            if (moduleList.getHudFont().equalsIgnoreCase("Client")) {
                float v1 = scaledHeight - font.getHeight() + y - (hasUserInfo ? font.getHeight() + 14 : 12.5F) - yChat;
                font.drawString(pType, (float) (scaledWidth - font.stringWidth(pType) - 2), v1, rgbColor, true);
            } else {
                mc.fontRendererObj.drawStringWithShadow(pType, scaledWidth - mc.fontRendererObj.getStringWidth(pType) - 1,
                        scaledHeight - mc.fontRendererObj.getHeight() + y - (hasUserInfo ? 23.5F : 13F) - yChat, rgbColor);
            }

            counter++;
            j -= 300;
            y -= moduleList.getHudFont().equals("Vanilla") ? mc.fontRendererObj.getHeight() + 1 : font.getHeight() + 1;
        }
    }
}
