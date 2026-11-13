package net.optifine.shaders.gui;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderEnumShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersTex;
import net.optifine.shaders.config.EnumShaderOption;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreenOF {
   protected String screenTitle = "Shaders";
   public static final int EnumOS_WINDOWS = 1;
   private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderEnumShaderOptions());
   private int updateTimer = -1;
   private static float[] QUALITY_MULTIPLIERS = new float[]{
      0.5F, 0.6F, 0.6666667F, 0.75F, 0.8333333F, 0.9F, 1.0F, 1.1666666F, 1.3333334F, 1.5F, 1.6666666F, 1.8F, 2.0F
   };
   private static String[] QUALITY_MULTIPLIER_NAMES = new String[]{
      "0.5x", "0.6x", "0.66x", "0.75x", "0.83x", "0.9x", "1x", "1.16x", "1.33x", "1.5x", "1.66x", "1.8x", "2x"
   };
   private static float QUALITY_MULTIPLIER_DEFAULT = 1.0F;
   private static float[] HAND_DEPTH_VALUES = new float[]{0.0625F, 0.125F, 0.25F};
   public static final int EnumOS_LINUX = 4;
   private static String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
   private static float HAND_DEPTH_DEFAULT = 0.125F;
   private boolean saved = false;
   public static final int EnumOS_UNKNOWN = 0;
   public static final int EnumOS_OSX = 2;
   private GuiSlotShaders shaderList;
   protected GuiScreen parentGui;
   public static final int EnumOS_SOLARIS = 3;

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateTimer--;
   }

   public boolean hasShiftDown() {
      return isShiftKeyDown();
   }

   @Override
   protected void actionPerformedRightClick(GuiButton guiButton) {
      this.actionPerformed(guiButton, true);
   }

   public static String toStringHandDepth(float val) {
      return toStringValue(val, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
   }

   private float getNextValue(float val, float[] values, float valDef, boolean forward, boolean reset) {
      if (reset) {
         return valDef;
      }

      int valueIndex = getValueIndex(val, values);
      if (forward) {
         if (++valueIndex >= values.length) {
            valueIndex = 0;
         }
      } else if (--valueIndex < 0) {
         valueIndex = values.length - 1;
      }

      return values[valueIndex];
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("of.options.shadersTitle");
      if (Shaders.shadersConfig == null) {
         Shaders.loadConfig();
      }

      int i = this.width - 120 - 10;
      int i2 = this.width - 120 - 20;
      this.shaderList = new GuiSlotShaders(this, i2, this.height, 30, this.height - 50, 16);
      this.shaderList.registerScrollButtons(7, 8);
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, i, 30));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, i, 50));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, i, 70));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, i, 90));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, i, 110));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, i, 130));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, i, 150));
      this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, i, 170));
      int i3 = Math.min(150, i2 / 2 - 10);
      int i4 = i2 / 4 - i3 / 2;
      int i5 = this.height - 25;
      this.buttonList.add(new GuiButton(201, i4, i5, i3 - 22 + 1, 20, Lang.get("of.options.shaders.shadersFolder")));
      this.buttonList.add(new GuiButtonDownloadShaders(210, i4 + i3 - 22 - 1, i5));
      this.buttonList.add(new GuiButton(202, i2 / 4 * 3 - i3 / 2, this.height - 25, i3, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(203, i, this.height - 25, 120, 20, Lang.get("of.options.shaders.shaderOptions")));
      this.updateButtons();
   }

   public void updateButtons() {
      boolean shaders2 = Config.isShaders();

      for (GuiButton guiButton : this.buttonList) {
         if (guiButton.id != 201 && guiButton.id != 202 && guiButton.id != 210 && guiButton.id != EnumShaderOption.ANTIALIASING.ordinal()) {
            guiButton.enabled = shaders2;
         }
      }
   }

   public void handleString(String text3, int i, int i2) {
      this.drawCenteredString(this.fontRendererObj, text3, i, i2, 14737632);
   }

   public static String toStringAa(int value) {
      return value == 2 ? "FXAA 2x" : (value == 4 ? "FXAA 4x" : Lang.getOff());
   }

   @Override
   public void startClosing() {
      super.startClosing();
      if (!this.saved) {
         Shaders.storeConfig();
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.shaderList.handleMouseInput();
   }

   public static int getValueIndex(float val, float[] values) {
      for (int index = 0; index < values.length; index++) {
         float f = values[index];
         if (f >= val) {
            return index;
         }
      }

      return values.length - 1;
   }

   public GuiShaders(GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
      this.parentGui = par1GuiScreen;
   }

   private void actionPerformed(GuiButton button, boolean rightClick) {
      if (button.enabled) {
         if (!(button instanceof GuiButtonEnumShaderOption guiButtonEnumShaderOption)) {
            if (!rightClick) {
               switch (button.id) {
                  case 201:
                     switch (getOSType()) {
                        case 1:
                           String text3 = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderPacksDir.getAbsolutePath());

                           try {
                              Runtime.getRuntime().exec(text3);
                              return;
                           } catch (IOException ioException) {
                              ioException.printStackTrace();
                              break;
                           }
                        case 2:
                           try {
                              Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderPacksDir.getAbsolutePath()});
                              return;
                           } catch (IOException ioException2) {
                              ioException2.printStackTrace();
                           }
                     }

                     boolean flag = false;

                     try {
                        Class clazz = Class.forName("java.awt.Desktop");
                        Object object = clazz.getMethod("getDesktop").invoke(null);
                        clazz.getMethod("browse", URI.class).invoke(object, new File(this.mc.mcDataDir, "shaderpacks").toURI());
                     } catch (Throwable throwable) {
                        throwable.printStackTrace();
                        flag = true;
                     }

                     if (flag) {
                        Config.dbg("Opening via system class!");
                        Sys.openURL("file://" + Shaders.shaderPacksDir.getAbsolutePath());
                     }
                     break;
                  case 202:
                     Shaders.storeConfig();
                     this.saved = true;
                     this.mc.displayGuiScreen(this.parentGui);
                     break;
                  case 203:
                     GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, Config.getGameSettings());
                     Config.getMinecraft().displayGuiScreen(guiShaderOptions);
                     break;
                  case 210:
                     try {
                        Class clazz2 = Class.forName("java.awt.Desktop");
                        Object object2 = clazz2.getMethod("getDesktop").invoke(null);
                        clazz2.getMethod("browse", URI.class).invoke(object2, new URI("http://optifine.net/shaderPacks"));
                     } catch (Throwable throwable2) {
                        throwable2.printStackTrace();
                     }
                  case 204:
                  case 205:
                  case 206:
                  case 207:
                  case 208:
                  case 209:
                  default:
                     this.shaderList.actionPerformed(button);
               }
            }
         } else {
            switch (guiButtonEnumShaderOption.getEnumShaderOption()) {
               case ANTIALIASING:
                  Shaders.nextAntialiasingLevel(!rightClick);
                  if (this.hasShiftDown()) {
                     Shaders.configAntialiasingLevel = 0;
                  }

                  Shaders.uninit();
                  break;
               case NORMAL_MAP:
                  Shaders.configNormalMap = !Shaders.configNormalMap;
                  if (this.hasShiftDown()) {
                     Shaders.configNormalMap = true;
                  }

                  Shaders.uninit();
                  this.mc.scheduleResourcesRefresh();
                  break;
               case SPECULAR_MAP:
                  Shaders.configSpecularMap = !Shaders.configSpecularMap;
                  if (this.hasShiftDown()) {
                     Shaders.configSpecularMap = true;
                  }

                  Shaders.uninit();
                  this.mc.scheduleResourcesRefresh();
                  break;
               case RENDER_RES_MUL:
                  float configRenderResMul2 = Shaders.configRenderResMul;
                  float[] fArray = QUALITY_MULTIPLIERS;
                  float f = QUALITY_MULTIPLIER_DEFAULT;
                  boolean flag2 = !rightClick;
                  boolean shiftDown = this.hasShiftDown();
                  boolean flag3 = flag2;
                  float f2 = f;
                  Shaders.configRenderResMul = this.getNextValue(configRenderResMul2, fArray, f2, flag3, shiftDown);
                  Shaders.uninit();
                  Shaders.scheduleResize();
                  break;
               case SHADOW_RES_MUL:
                  float configShadowResMul2 = Shaders.configShadowResMul;
                  float[] fArray2 = QUALITY_MULTIPLIERS;
                  float f3 = QUALITY_MULTIPLIER_DEFAULT;
                  boolean flag4 = !rightClick;
                  boolean shiftDown2 = this.hasShiftDown();
                  boolean flag5 = flag4;
                  float f4 = f3;
                  Shaders.configShadowResMul = this.getNextValue(configShadowResMul2, fArray2, f4, flag5, shiftDown2);
                  Shaders.uninit();
                  Shaders.scheduleResizeShadow();
                  break;
               case HAND_DEPTH_MUL:
                  float configHandDepthMul2 = Shaders.configHandDepthMul;
                  float[] fArray3 = HAND_DEPTH_VALUES;
                  float f5 = HAND_DEPTH_DEFAULT;
                  boolean flag6 = !rightClick;
                  boolean shiftDown3 = this.hasShiftDown();
                  boolean flag7 = flag6;
                  float f6 = f5;
                  Shaders.configHandDepthMul = this.getNextValue(configHandDepthMul2, fArray3, f6, flag7, shiftDown3);
                  Shaders.uninit();
                  break;
               case OLD_HAND_LIGHT:
                  boolean flag8 = !rightClick;
                  Shaders.configOldHandLight.nextValue(flag8);
                  if (this.hasShiftDown()) {
                     Shaders.configOldHandLight.resetValue();
                  }

                  Shaders.uninit();
                  break;
               case OLD_LIGHTING:
                  boolean flag9 = !rightClick;
                  Shaders.configOldLighting.nextValue(flag9);
                  if (this.hasShiftDown()) {
                     Shaders.configOldLighting.resetValue();
                  }

                  Shaders.updateBlockLightLevel();
                  Shaders.uninit();
                  this.mc.scheduleResourcesRefresh();
                  break;
               case TWEAK_BLOCK_DAMAGE:
                  Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                  break;
               case CLOUD_SHADOW:
                  Shaders.configCloudShadow = !Shaders.configCloudShadow;
                  break;
               case TEX_MIN_FIL_B:
                  Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                  Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                  button.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_N:
                  Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                  button.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case TEX_MAG_FIL_S:
                  Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                  button.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                  ShadersTex.updateTextureMinMagFilter();
                  break;
               case SHADOW_CLIP_FRUSTRUM:
                  Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                  button.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                  ShadersTex.updateTextureMinMagFilter();
            }

            guiButtonEnumShaderOption.updateButtonText();
         }
      }
   }

   @Override
   protected void actionPerformed(GuiButton guiButton) {
      this.actionPerformed(guiButton, false);
   }

   public static String toStringOnOff(boolean value) {
      String text = Lang.getOn();
      String text2 = Lang.getOff();
      return value ? text : text2;
   }

   public static int getOSType() {
      String propertyValue = System.getProperty("os.name").toLowerCase();
      return propertyValue.contains("win")
         ? 1
         : (
            propertyValue.contains("mac")
               ? 2
               : (
                  propertyValue.contains("solaris")
                     ? 3
                     : (propertyValue.contains("sunos") ? 3 : (propertyValue.contains("linux") ? 4 : (propertyValue.contains("unix") ? 4 : 0)))
               )
         );
   }

   public static String toStringQuality(float val) {
      return toStringValue(val, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
   }

   public static String toStringValue(float val, float[] values, String[] names) {
      int valueIndex = getValueIndex(val, values);
      return names[valueIndex];
   }

   public Minecraft getMc() {
      return this.mc;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.shaderList.drawScreen(mouseX, mouseY, partialTicks);
      if (this.updateTimer <= 0) {
         this.shaderList.updateList();
         this.updateTimer += 20;
      }

      this.drawCenteredString(this.fontRendererObj, this.screenTitle + " ", this.width / 2, 15, 16777215);
      String text3 = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
      if (this.fontRendererObj.getInt3(text3) < this.width - 5) {
         this.drawCenteredString(this.fontRendererObj, text3, this.width / 2, this.height - 40, 8421504);
      } else {
         this.drawString(this.fontRendererObj, text3, 5, this.height - 40, 8421504);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
   }
}
