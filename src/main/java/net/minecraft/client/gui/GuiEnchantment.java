package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.lwjgl.util.glu.Project;

public class GuiEnchantment extends GuiContainer {
   public int ticks;
   public float targetBookFlip;
   public float bookFlipPrev;
   private Random random = new Random();
   private final InventoryPlayer playerInventory;
   private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
   ItemStack lastItemStack;
   private ContainerEnchantment container;
   private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
   public float bookFlipSpeed;
   private static final ModelBook MODEL_BOOK = new ModelBook();
   private final IWorldNameable displayName;
   public float bookFlip;
   public float bookOpenPrev;
   public float bookOpen;

   @Override
   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, i2, 0, 0, this.xSize, this.ySize);
      GlStateManager.pushMatrix();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.viewport(
         (scaledResolution.getScaledWidth() - 320) / 2 * scaledResolution.getScaleFactor(),
         (scaledResolution.getScaledHeight() - 240) / 2 * scaledResolution.getScaleFactor(),
         320 * scaledResolution.getScaleFactor(),
         240 * scaledResolution.getScaleFactor()
      );
      GlStateManager.translate(-0.34F, 0.23F, 0.0F);
      Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
      float f = 1.0F;
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(0.0F, 3.3F, -16.0F);
      GlStateManager.scale(f, f, f);
      float f2 = 5.0F;
      GlStateManager.scale(f2, f2, f2);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
      GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
      float f3 = this.bookOpenPrev + (this.bookOpen - this.bookOpenPrev) * partialTicks;
      GlStateManager.translate((1.0F - f3) * 0.2F, (1.0F - f3) * 0.1F, (1.0F - f3) * 0.25F);
      GlStateManager.rotate(-(1.0F - f3) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      float f4 = this.bookFlipPrev + (this.bookFlip - this.bookFlipPrev) * partialTicks + 0.25F;
      float f5 = this.bookFlipPrev + (this.bookFlip - this.bookFlipPrev) * partialTicks + 0.75F;
      f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;
      f5 = (f5 - MathHelper.truncateDoubleToInt(f5)) * 1.6F - 0.3F;
      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f5 < 0.0F) {
         f5 = 0.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      if (f5 > 1.0F) {
         f5 = 1.0F;
      }

      GlStateManager.enableRescaleNormal();
      MODEL_BOOK.render((Entity)null, 0.0F, f4, f5, f3, 0.0F, 0.0625F);
      GlStateManager.disableRescaleNormal();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.matrixMode(5889);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.xpSeed);
      int lapisAmount = this.container.getLapisAmount();

      for (int i3 = 0; i3 < 3; i3++) {
         int i4 = i + 60;
         int i5 = i4 + 20;
         String text = EnchantmentNameParts.getInstance().generateNewRandomName();
         this.zLevel = 0.0F;
         this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
         int i6 = this.container.enchantLevels[i3];
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (i6 == 0) {
            this.drawTexturedModalRect(i4, i2 + 14 + 19 * i3, 0, 185, 108, 19);
         } else {
            String text2 = i6 + "";
            FontRenderer standardGalacticFontRenderer2 = this.mc.standardGalacticFontRenderer;
            int i7 = 6839882;
            if ((lapisAmount < i3 + 1 || this.mc.thePlayer.experienceLevel < i6) && !this.mc.thePlayer.capabilities.isCreativeMode) {
               this.drawTexturedModalRect(i4, i2 + 14 + 19 * i3, 0, 185, 108, 19);
               this.drawTexturedModalRect(i4 + 1, i2 + 15 + 19 * i3, 16 * i3, 239, 16, 16);
               standardGalacticFontRenderer2.drawSplitString(text, i5, i2 + 16 + 19 * i3, 86, 3419941);
               i7 = 4226832;
            } else {
               int i8 = mouseX - (i + 60);
               int i9 = mouseY - (i2 + 14 + 19 * i3);
               if (i8 >= 0 && i9 >= 0 && i8 < 108 && i9 < 19) {
                  this.drawTexturedModalRect(i4, i2 + 14 + 19 * i3, 0, 204, 108, 19);
                  i7 = 16777088;
               } else {
                  this.drawTexturedModalRect(i4, i2 + 14 + 19 * i3, 0, 166, 108, 19);
               }

               this.drawTexturedModalRect(i4 + 1, i2 + 15 + 19 * i3, 16 * i3, 223, 16, 16);
               standardGalacticFontRenderer2.drawSplitString(text, i5, i2 + 16 + 19 * i3, 86, i7);
               i7 = 8453920;
            }

            standardGalacticFontRenderer2 = this.mc.fontRendererObj;
            standardGalacticFontRenderer2.drawStringWithShadow(text2, i5 + 86 - standardGalacticFontRenderer2.getInt3(text2), i2 + 16 + 19 * i3 + 7, i7);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      boolean isCreativeMode2 = this.mc.thePlayer.capabilities.isCreativeMode;
      int lapisAmount = this.container.getLapisAmount();

      for (int i = 0; i < 3; i++) {
         int i2 = this.container.enchantLevels[i];
         int i3 = this.container.enchantmentIds[i];
         int i4 = i + 1;
         if (this.isPointInRegion(60, 14 + 19 * i, 108, 17, mouseX, mouseY) && i2 > 0 && i3 >= 0) {
            ArrayList arrayList = Lists.newArrayList();
            if (i3 >= 0 && Enchantment.getEnchantmentById(i3 & 0xFF) != null) {
               String translatedName = Enchantment.getEnchantmentById(i3 & 0xFF).getTranslatedName((i3 & 0xFF00) >> 8);
               arrayList.add(EnumChatFormatting.WHITE.toString() + EnumChatFormatting.ITALIC.toString() + I18n.format("container.enchant.clue", translatedName));
            }

            if (!isCreativeMode2) {
               if (i3 >= 0) {
                  arrayList.add("");
               }

               if (this.mc.thePlayer.experienceLevel < i2) {
                  arrayList.add(EnumChatFormatting.RED.toString() + "Level Requirement: " + this.container.enchantLevels[i]);
               } else {
                  String text;
                  if (i4 == 1) {
                     text = I18n.format("container.enchant.lapis.one");
                  } else {
                     text = I18n.format("container.enchant.lapis.many", i4);
                  }

                  if (lapisAmount >= i4) {
                     arrayList.add(EnumChatFormatting.GRAY.toString() + text);
                  } else {
                     arrayList.add(EnumChatFormatting.RED.toString() + text);
                  }

                  if (i4 == 1) {
                     text = I18n.format("container.enchant.level.one");
                  } else {
                     text = I18n.format("container.enchant.level.many", i4);
                  }

                  arrayList.add(EnumChatFormatting.GRAY.toString() + text);
               }
            }

            this.drawHoveringText(arrayList, mouseX, mouseY);
            break;
         }
      }
   }

   public GuiEnchantment(InventoryPlayer inventory, World worldIn, IWorldNameable displayName) {
      super(new ContainerEnchantment(inventory, worldIn));
      this.playerInventory = inventory;
      this.container = (ContainerEnchantment)this.inventorySlots;
      this.displayName = displayName;
   }

   public void updateAnimation() {
      ItemStack stack = this.inventorySlots.getSlot(0).getStack();
      if (!ItemStack.areItemStacksEqual(stack, this.lastItemStack)) {
         this.lastItemStack = stack;

         do {
            this.targetBookFlip = this.targetBookFlip + (this.random.nextInt(4) - this.random.nextInt(4));
         } while (!(this.bookFlip > this.targetBookFlip + 1.0F) && !(this.bookFlip < this.targetBookFlip - 1.0F));
      }

      this.ticks++;
      this.bookFlipPrev = this.bookFlip;
      this.bookOpenPrev = this.bookOpen;
      boolean flag = false;

      for (int i = 0; i < 3; i++) {
         if (this.container.enchantLevels[i] != 0) {
            flag = true;
         }
      }

      if (flag) {
         this.bookOpen += 0.2F;
      } else {
         this.bookOpen -= 0.2F;
      }

      this.bookOpen = MathHelper.clamp_float(this.bookOpen, 0.0F, 1.0F);
      float f = (this.targetBookFlip - this.bookFlip) * 0.4F;
      float f2 = 0.2F;
      f = MathHelper.clamp_float(f, -f2, f2);
      this.bookFlipSpeed = this.bookFlipSpeed + (f - this.bookFlipSpeed) * 0.9F;
      this.bookFlip = this.bookFlip + this.bookFlipSpeed;
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateAnimation();
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.getInt(this.displayName.getDisplayName().getUnformattedText(), 12, 5, 4210752);
      this.fontRendererObj.getInt(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int i = (this.width - this.xSize) / 2;
      int i2 = (this.height - this.ySize) / 2;

      for (int nestedIndex = 0; nestedIndex < 3; nestedIndex++) {
         int i3 = mouseX - (i + 60);
         int i4 = mouseY - (i2 + 14 + 19 * nestedIndex);
         if (i3 >= 0 && i4 >= 0 && i3 < 108 && i4 < 19 && this.container.enchantItem(this.mc.thePlayer, nestedIndex)) {
            this.mc.playerController.sendEnchantPacket(this.container.windowId, nestedIndex);
         }
      }
   }
}
