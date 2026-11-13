package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiWinGame extends GuiScreen {
   private int creditsHeight;
   private float SCROLL_SPEED = 0.5F;
   private List<String> creditsLines;
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation MINECRAFT_LOGO = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
   private int creditsScrollTicks;

   private void handleFloat(float f) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      int width2 = this.width;
      float f2 = 0.0F - (this.creditsScrollTicks + f) * 0.5F * this.SCROLL_SPEED;
      float f3 = this.height - (this.creditsScrollTicks + f) * 0.5F * this.SCROLL_SPEED;
      float f4 = 0.015625F;
      float f5 = (this.creditsScrollTicks + f - 0.0F) * 0.02F;
      float f6 = (this.creditsHeight + this.height + this.height + 24) / this.SCROLL_SPEED;
      float f7 = (f6 - 20.0F - (this.creditsScrollTicks + f)) * 0.005F;
      if (f7 < f5) {
         f5 = f7;
      }

      if (f5 > 1.0F) {
         f5 = 1.0F;
      }

      f5 *= f5;
      f5 = f5 * 96.0F / 255.0F;
      worldRenderer.pos(0.0, this.height, this.zLevel).tex(0.0, f2 * f4).color(f5, f5, f5, 1.0F).endVertex();
      worldRenderer.pos(width2, this.height, this.zLevel).tex(width2 * f4, f2 * f4).color(f5, f5, f5, 1.0F).endVertex();
      worldRenderer.pos(width2, 0.0, this.zLevel).tex(width2 * f4, f3 * f4).color(f5, f5, f5, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.0, f3 * f4).color(f5, f5, f5, 1.0F).endVertex();
      instance.draw();
   }

   @Override
   public void initGui() {
      if (this.creditsLines == null) {
         this.creditsLines = Lists.newArrayList();

         try {
            String text = "" + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + EnumChatFormatting.GREEN + EnumChatFormatting.AQUA;
            InputStream inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/end.txt")).getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
            Random random = new Random(8124371L);

            String text2;
            while ((text2 = bufferedReader.readLine()) != null) {
               text2 = text2.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());

               while (text2.contains(text)) {
                  int i = text2.indexOf(text);
                  String text3 = text2.substring(0, i);
                  String text4 = text2.substring(i + text.length());
                  text2 = text3 + EnumChatFormatting.WHITE + EnumChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, random.nextInt(4) + 3) + text4;
               }

               this.creditsLines.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(text2, 274));
               this.creditsLines.add("");
            }

            inputStream.close();

            for (int i2 = 0; i2 < 8; i2++) {
               this.creditsLines.add("");
            }

            inputStream = this.mc.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));

            while ((text2 = bufferedReader.readLine()) != null) {
               text2 = text2.replaceAll("PLAYERNAME", this.mc.getSession().getUsername());
               text2 = text2.replaceAll("\t", "    ");
               this.creditsLines.addAll(this.mc.fontRendererObj.listFormattedStringToWidth(text2, 274));
               this.creditsLines.add("");
            }

            inputStream.close();
            this.creditsHeight = this.creditsLines.size() * 12;
         } catch (Exception exception) {
            logger.error("Couldn't load credits", exception);
         }
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return true;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.sendRespawnPacket();
      }
   }

   private void sendRespawnPacket() {
      this.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   @Override
   public void updateScreen() {
      MusicTicker musicTicker = this.mc.getMusicTicker();
      SoundHandler soundHandler = this.mc.getSoundHandler();
      if (this.creditsScrollTicks == 0) {
         musicTicker.stopMusic();
         musicTicker.playMusic(net.minecraft.client.audio.MusicTicker.MusicType.CREDITS);
         soundHandler.resumeSounds();
      }

      soundHandler.update();
      this.creditsScrollTicks++;
      float f = (this.creditsHeight + this.height + this.height + 24) / this.SCROLL_SPEED;
      if (this.creditsScrollTicks > f) {
         this.sendRespawnPacket();
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.handleFloat(partialTicks);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      int i = this.width / 2 - 137;
      int i2 = this.height + 50;
      float f = -(this.creditsScrollTicks + partialTicks) * this.SCROLL_SPEED;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, f, 0.0F);
      this.mc.getTextureManager().bindTexture(MINECRAFT_LOGO);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(i, i2, 0, 0, 155, 44);
      this.drawTexturedModalRect(i + 155, i2, 0, 45, 155, 44);
      int i3 = i2 + 200;

      for (int i4 = 0; i4 < this.creditsLines.size(); i4++) {
         if (i4 == this.creditsLines.size() - 1) {
            float f2 = i3 + f - (this.height / 2 - 6);
            if (f2 < 0.0F) {
               GlStateManager.translate(0.0F, -f2, 0.0F);
            }
         }

         if (i3 + f + 12.0F + 8.0F > 0.0F && i3 + f < this.height) {
            String text = this.creditsLines.get(i4);
            if (text.startsWith("[C]")) {
               this.fontRendererObj
                  .drawStringWithShadow(text.substring(3), i + (274 - this.fontRendererObj.getInt3(text.substring(3))) / 2, i3, 16777215);
            } else {
               this.fontRendererObj.fontRandom.setSeed(i4 * 4238972211L + this.creditsScrollTicks / 4);
               this.fontRendererObj.drawStringWithShadow(text, i, i3, 16777215);
            }
         }

         i3 += 12;
      }

      GlStateManager.popMatrix();
      this.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(0, 769);
      int width2 = this.width;
      int height2 = this.height;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, height2, this.zLevel).tex(0.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width2, height2, this.zLevel).tex(1.0, 1.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(width2, 0.0, this.zLevel).tex(1.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldRenderer.pos(0.0, 0.0, this.zLevel).tex(0.0, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      instance.draw();
      GlStateManager.disableBlend();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
