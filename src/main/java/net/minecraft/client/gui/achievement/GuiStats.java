package net.minecraft.client.gui.achievement;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiStats extends GuiScreen implements IProgressMeter {
   private GuiStats.StatsMobsList mobStats;
   private GuiStats.StatsItem itemStats;
   protected String screenTitle = "Select world";
   private boolean doesGuiPauseGame = true;
   protected GuiScreen parentScreen;
   private GuiStats.StatsBlock blockStats;
   private GuiSlot displaySlot;
   private StatFileWriter statWriter;
   private GuiStats.StatsGeneral generalStats;

   public GuiStats(GuiScreen parentScreen, StatFileWriter statWriter) {
      this.parentScreen = parentScreen;
      this.statWriter = statWriter;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.doesGuiPauseGame) {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
         this.drawCenteredString(
            this.fontRendererObj,
            lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % lanSearchStates.length)],
            this.width / 2,
            this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2,
            16777215
         );
      } else {
         this.displaySlot.drawScreen(mouseX, mouseY, partialTicks);
         this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
         super.drawScreen(mouseX, mouseY, partialTicks);
      }
   }

   @Override
   public void doneLoading() {
      if (this.doesGuiPauseGame) {
         this.createStatsTabs();
         this.createButtons();
         this.displaySlot = this.generalStats;
         this.doesGuiPauseGame = false;
      }
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("gui.stats");
      this.doesGuiPauseGame = true;
      this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.REQUEST_STATS));
   }

   private void drawSprite(int x, int y, int u, int v) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(statIcons);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(x + 0, y + 18, this.zLevel).tex((u + 0) * 0.0078125F, (v + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(x + 18, y + 18, this.zLevel).tex((u + 18) * 0.0078125F, (v + 18) * 0.0078125F).endVertex();
      worldRenderer.pos(x + 18, y + 0, this.zLevel).tex((u + 18) * 0.0078125F, (v + 0) * 0.0078125F).endVertex();
      worldRenderer.pos(x + 0, y + 0, this.zLevel).tex((u + 0) * 0.0078125F, (v + 0) * 0.0078125F).endVertex();
      instance.draw();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (this.displaySlot != null) {
         this.displaySlot.handleMouseInput();
      }
   }

   static void access$000(GuiStats guiStats, int i, int i2, int i3, int i4) {
      guiStats.drawGradientRect(i, i2, i3, i4, -1073741824, -1073741824);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 1) {
            this.displaySlot = this.generalStats;
         } else if (button.id == 3) {
            this.displaySlot = this.itemStats;
         } else if (button.id == 2) {
            this.displaySlot = this.blockStats;
         } else if (button.id == 4) {
            this.displaySlot = this.mobStats;
         } else {
            this.displaySlot.actionPerformed(button);
         }
      }
   }

   public void createButtons() {
      this.buttonList.add(new GuiButton(0, this.width / 2 + 4, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 160, this.height - 52, 80, 20, I18n.format("stat.generalButton")));
      GuiButton guiButton;
      this.buttonList.add(guiButton = new GuiButton(2, this.width / 2 - 80, this.height - 52, 80, 20, I18n.format("stat.blocksButton")));
      GuiButton guiButton2;
      this.buttonList.add(guiButton2 = new GuiButton(3, this.width / 2, this.height - 52, 80, 20, I18n.format("stat.itemsButton")));
      GuiButton guiButton3;
      this.buttonList.add(guiButton3 = new GuiButton(4, this.width / 2 + 80, this.height - 52, 80, 20, I18n.format("stat.mobsButton")));
      if (this.blockStats.getSize() == 0) {
         guiButton.enabled = false;
      }

      if (this.itemStats.getSize() == 0) {
         guiButton2.enabled = false;
      }

      if (this.mobStats.getSize() == 0) {
         guiButton3.enabled = false;
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return !this.doesGuiPauseGame;
   }

   private void drawStatsScreen(int x, int y, Item item) {
      this.drawButtonBackground(x + 1, y + 1);
      GlStateManager.enableRescaleNormal();
      RenderHelper.enableGUIStandardItemLighting();
      this.itemRender.handleItemStack(new ItemStack(item, 1, 0), x + 2, y + 2);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
   }

   public void createStatsTabs() {
      this.generalStats = new GuiStats.StatsGeneral(this.mc);
      this.generalStats.registerScrollButtons(1, 1);
      this.itemStats = new GuiStats.StatsItem(this.mc);
      this.itemStats.registerScrollButtons(1, 1);
      this.blockStats = new GuiStats.StatsBlock(this.mc);
      this.blockStats.registerScrollButtons(1, 1);
      this.mobStats = new GuiStats.StatsMobsList(this.mc);
      this.mobStats.registerScrollButtons(1, 1);
   }

   static void access$100(GuiStats guiStats, int i, int i2, int i3, int i4) {
      guiStats.drawGradientRect(i, i2, i3, i4, -1073741824, -1073741824);
   }

   private void drawButtonBackground(int x, int y) {
      this.drawSprite(x, y, 0, 0);
   }

   abstract class Stats extends GuiSlot {
      protected int sortDirection;
      protected List<StatCrafting> statsHolder;
      protected int hoveredColumn = -1;
      protected int sortedColumn = -1;
      protected Comparator<StatCrafting> statSorter;

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      protected void drawItemTooltip(StatCrafting stat, int x, int y) {
         if (stat != null) {
            Item item2 = stat.getItem();
            String text4 = (I18n.format(new ItemStack(item2).getUnlocalizedName() + ".name") + "").trim();
            if (text4.length() > 0) {
               int i = x + 12;
               int i2 = y - 12;
               int intValue = GuiStats.this.fontRendererObj.getInt3(text4);
               GuiStats.access$000(GuiStats.this, i - 3, i2 - 3, i + intValue + 3, i2 + 8 + 3);
               GuiStats.this.fontRendererObj.drawStringWithShadow(text4, i, i2, -1);
            }
         }
      }

      @Override
      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      protected final StatCrafting getStat(int statIndex) {
         return this.statsHolder.get(statIndex);
      }

      @Override
      protected void drawListHeader(int x, int y, Tessellator tessellator) {
         if (!Mouse.isButtonDown(0)) {
            this.hoveredColumn = -1;
         }

         if (this.hoveredColumn == 0) {
            GuiStats.this.drawSprite(x + 115 - 18, y + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(x + 115 - 18, y + 1, 0, 18);
         }

         if (this.hoveredColumn == 1) {
            GuiStats.this.drawSprite(x + 165 - 18, y + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(x + 165 - 18, y + 1, 0, 18);
         }

         if (this.hoveredColumn == 2) {
            GuiStats.this.drawSprite(x + 215 - 18, y + 1, 0, 0);
         } else {
            GuiStats.this.drawSprite(x + 215 - 18, y + 1, 0, 18);
         }

         if (this.sortedColumn != -1) {
            int i = 79;
            int i2 = 18;
            if (this.sortedColumn == 1) {
               i = 129;
            } else if (this.sortedColumn == 2) {
               i = 179;
            }

            if (this.sortDirection == 1) {
               i2 = 36;
            }

            GuiStats.this.drawSprite(x + i, y + 1, i2, 0);
         }
      }

      protected void setSortColumn(int sortColumn) {
         if (sortColumn != this.sortedColumn) {
            this.sortedColumn = sortColumn;
            this.sortDirection = -1;
         } else if (this.sortDirection == -1) {
            this.sortDirection = 1;
         } else {
            this.sortedColumn = -1;
            this.sortDirection = 0;
         }

         Collections.sort(this.statsHolder, this.statSorter);
      }

      @Override
      protected void drawListDecorations(int mouseX, int mouseY) {
         if (mouseY >= this.top && mouseY <= this.bottom) {
            int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
            int i = this.width / 2 - 92 - 16;
            if (slotIndexFromScreenCoords >= 0) {
               if (mouseX < i + 40 || mouseX > i + 40 + 20) {
                  return;
               }

               StatCrafting stat2 = this.getStat(slotIndexFromScreenCoords);
               this.drawItemTooltip(stat2, mouseX, mouseY);
            } else {
               String text4;
               if (mouseX >= i + 115 - 18 && mouseX <= i + 115) {
                  text4 = this.getColumnLabel(0);
               } else if (mouseX >= i + 165 - 18 && mouseX <= i + 165) {
                  text4 = this.getColumnLabel(1);
               } else {
                  if (mouseX < i + 215 - 18 || mouseX > i + 215) {
                     return;
                  }

                  text4 = this.getColumnLabel(2);
               }

               text4 = (I18n.format(text4) + "").trim();
               if (text4.length() > 0) {
                  int i2 = mouseX + 12;
                  int i3 = mouseY - 12;
                  int intValue = GuiStats.this.fontRendererObj.getInt3(text4);
                  GuiStats.access$100(GuiStats.this, i2 - 3, i3 - 3, i2 + intValue + 3, i3 + 8 + 3);
                  GuiStats.this.fontRendererObj.drawStringWithShadow(text4, i2, i3, -1);
               }
            }
         }
      }

      protected Stats(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 20);
         this.update();
         this.handleInt2(20);
      }

      protected void drawStatValue(StatBase stat, int x, int y, boolean highlight) {
         if (stat != null) {
            String text4 = stat.format(GuiStats.this.statWriter.readStat(stat));
            GuiStats.this.drawString(
               GuiStats.this.fontRendererObj, text4, x - GuiStats.this.fontRendererObj.getInt3(text4), y + 5, highlight ? 16777215 : 9474192
            );
         } else {
            GuiStats.this.drawString(GuiStats.this.fontRendererObj, "-", x - GuiStats.this.fontRendererObj.getInt3("-"), y + 5, highlight ? 16777215 : 9474192);
         }
      }

      protected abstract String getColumnLabel(int i);

      @Override
      protected void handleHeaderClick(int mouseX, int mouseY) {
         this.hoveredColumn = -1;
         if (mouseX >= 79 && mouseX < 115) {
            this.hoveredColumn = 0;
         } else if (mouseX >= 129 && mouseX < 165) {
            this.hoveredColumn = 1;
         } else if (mouseX >= 179 && mouseX < 215) {
            this.hoveredColumn = 2;
         }

         if (this.hoveredColumn >= 0) {
            this.setSortColumn(this.hoveredColumn);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      protected final int getSize() {
         return this.statsHolder.size();
      }
   }

   class StatsBlock extends GuiStats.Stats {
      public StatsBlock(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting2 : StatList.objectMineStats) {
            boolean flag = false;
            int idFromItem = Item.getIdFromItem(statCrafting2.getItem());
            if (GuiStats.this.statWriter.readStat(statCrafting2) > 0) {
               flag = true;
            } else if (StatList.objectUseStats[idFromItem] != null && GuiStats.this.statWriter.readStat(StatList.objectUseStats[idFromItem]) > 0) {
               flag = true;
            } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statWriter.readStat(StatList.objectCraftStats[idFromItem]) > 0) {
               flag = true;
            }

            if (flag) {
               this.statsHolder.add(statCrafting2);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting first, StatCrafting second) {
               int idFromItem2 = Item.getIdFromItem(first.getItem());
               int idFromItem3 = Item.getIdFromItem(second.getItem());
               StatBase statBase2 = null;
               StatBase statBase3 = null;
               if (StatsBlock.this.sortedColumn == 2) {
                  statBase2 = StatList.mineBlockStatArray[idFromItem2];
                  statBase3 = StatList.mineBlockStatArray[idFromItem3];
               } else if (StatsBlock.this.sortedColumn == 0) {
                  statBase2 = StatList.objectCraftStats[idFromItem2];
                  statBase3 = StatList.objectCraftStats[idFromItem3];
               } else if (StatsBlock.this.sortedColumn == 1) {
                  statBase2 = StatList.objectUseStats[idFromItem2];
                  statBase3 = StatList.objectUseStats[idFromItem3];
               }

               if (statBase2 != null || statBase3 != null) {
                  if (statBase2 == null) {
                     return 1;
                  }

                  if (statBase3 == null) {
                     return -1;
                  }

                  int stat2 = GuiStats.this.statWriter.readStat(statBase2);
                  int stat3 = GuiStats.this.statWriter.readStat(statBase3);
                  if (stat2 != stat3) {
                     return (stat2 - stat3) * StatsBlock.this.sortDirection;
                  }
               }

               return idFromItem2 - idFromItem3;
            }
         };
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         StatCrafting statCrafting = this.getStat(entryID);
         Item item = statCrafting.getItem();
         GuiStats.this.drawStatsScreen(x + 40, y, item);
         int idFromItem = Item.getIdFromItem(item);
         this.drawStatValue(StatList.objectCraftStats[idFromItem], x + 115, y, entryID % 2 == 0);
         this.drawStatValue(StatList.objectUseStats[idFromItem], x + 165, y, entryID % 2 == 0);
         this.drawStatValue(statCrafting, x + 215, y, entryID % 2 == 0);
      }

      @Override
      protected void drawListHeader(int x, int y, Tessellator tessellator) {
         super.drawListHeader(x, y, tessellator);
         if (this.hoveredColumn == 0) {
            GuiStats.this.drawSprite(x + 115 - 18 + 1, y + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(x + 115 - 18, y + 1, 18, 18);
         }

         if (this.hoveredColumn == 1) {
            GuiStats.this.drawSprite(x + 165 - 18 + 1, y + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(x + 165 - 18, y + 1, 36, 18);
         }

         if (this.hoveredColumn == 2) {
            GuiStats.this.drawSprite(x + 215 - 18 + 1, y + 1 + 1, 54, 18);
         } else {
            GuiStats.this.drawSprite(x + 215 - 18, y + 1, 54, 18);
         }
      }

      @Override
      protected String getColumnLabel(int index) {
         return index == 0 ? "stat.crafted" : (index == 1 ? "stat.used" : "stat.mined");
      }
   }

   class StatsGeneral extends GuiSlot {
      @Override
      protected int getContentHeight() {
         return this.getSize() * 10;
      }

      @Override
      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }

      public StatsGeneral(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, 10);
         this.update();
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         StatBase statBase = StatList.generalStats.get(entryID);
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, statBase.getStatName().getUnformattedText(), x + 2, y + 1, entryID % 2 == 0 ? 16777215 : 9474192
         );
         String text = statBase.format(GuiStats.this.statWriter.readStat(statBase));
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, text, x + 2 + 213 - GuiStats.this.fontRendererObj.getInt3(text), y + 1, entryID % 2 == 0 ? 16777215 : 9474192
         );
      }

      @Override
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      protected int getSize() {
         return StatList.generalStats.size();
      }
   }

   class StatsItem extends GuiStats.Stats {
      public StatsItem(Minecraft mcIn) {
         super(mcIn);
         this.statsHolder = Lists.newArrayList();

         for (StatCrafting statCrafting2 : StatList.itemStats) {
            boolean flag = false;
            int idFromItem = Item.getIdFromItem(statCrafting2.getItem());
            if (GuiStats.this.statWriter.readStat(statCrafting2) > 0) {
               flag = true;
            } else if (StatList.objectBreakStats[idFromItem] != null && GuiStats.this.statWriter.readStat(StatList.objectBreakStats[idFromItem]) > 0) {
               flag = true;
            } else if (StatList.objectCraftStats[idFromItem] != null && GuiStats.this.statWriter.readStat(StatList.objectCraftStats[idFromItem]) > 0) {
               flag = true;
            }

            if (flag) {
               this.statsHolder.add(statCrafting2);
            }
         }

         this.statSorter = new Comparator<StatCrafting>() {
            public int compare(StatCrafting first, StatCrafting second) {
               int idFromItem2 = Item.getIdFromItem(first.getItem());
               int idFromItem3 = Item.getIdFromItem(second.getItem());
               StatBase statBase2 = null;
               StatBase statBase3 = null;
               if (StatsItem.this.sortedColumn == 0) {
                  statBase2 = StatList.objectBreakStats[idFromItem2];
                  statBase3 = StatList.objectBreakStats[idFromItem3];
               } else if (StatsItem.this.sortedColumn == 1) {
                  statBase2 = StatList.objectCraftStats[idFromItem2];
                  statBase3 = StatList.objectCraftStats[idFromItem3];
               } else if (StatsItem.this.sortedColumn == 2) {
                  statBase2 = StatList.objectUseStats[idFromItem2];
                  statBase3 = StatList.objectUseStats[idFromItem3];
               }

               if (statBase2 != null || statBase3 != null) {
                  if (statBase2 == null) {
                     return 1;
                  }

                  if (statBase3 == null) {
                     return -1;
                  }

                  int stat2 = GuiStats.this.statWriter.readStat(statBase2);
                  int stat3 = GuiStats.this.statWriter.readStat(statBase3);
                  if (stat2 != stat3) {
                     return (stat2 - stat3) * StatsItem.this.sortDirection;
                  }
               }

               return idFromItem2 - idFromItem3;
            }
         };
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         StatCrafting statCrafting = this.getStat(entryID);
         Item item = statCrafting.getItem();
         GuiStats.this.drawStatsScreen(x + 40, y, item);
         int idFromItem = Item.getIdFromItem(item);
         this.drawStatValue(StatList.objectBreakStats[idFromItem], x + 115, y, entryID % 2 == 0);
         this.drawStatValue(StatList.objectCraftStats[idFromItem], x + 165, y, entryID % 2 == 0);
         this.drawStatValue(statCrafting, x + 215, y, entryID % 2 == 0);
      }

      @Override
      protected String getColumnLabel(int index) {
         return index == 1 ? "stat.crafted" : (index == 2 ? "stat.used" : "stat.depleted");
      }

      @Override
      protected void drawListHeader(int x, int y, Tessellator tessellator) {
         super.drawListHeader(x, y, tessellator);
         if (this.hoveredColumn == 0) {
            GuiStats.this.drawSprite(x + 115 - 18 + 1, y + 1 + 1, 72, 18);
         } else {
            GuiStats.this.drawSprite(x + 115 - 18, y + 1, 72, 18);
         }

         if (this.hoveredColumn == 1) {
            GuiStats.this.drawSprite(x + 165 - 18 + 1, y + 1 + 1, 18, 18);
         } else {
            GuiStats.this.drawSprite(x + 165 - 18, y + 1, 18, 18);
         }

         if (this.hoveredColumn == 2) {
            GuiStats.this.drawSprite(x + 215 - 18 + 1, y + 1 + 1, 36, 18);
         } else {
            GuiStats.this.drawSprite(x + 215 - 18, y + 1, 36, 18);
         }
      }
   }

   class StatsMobsList extends GuiSlot {
      private final List<EntityEggInfo> eggInfos = Lists.newArrayList();

      @Override
      protected boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      protected int getSize() {
         return this.eggInfos.size();
      }

      @Override
      protected void drawSlot(int entryID, int x, int y, int z, int mouseXIn, int mouseYIn) {
         EntityEggInfo entitylist$entityegginfo = this.eggInfos.get(entryID);
         String text = I18n.format("entity." + EntityList.getStringFromID(entitylist$entityegginfo.spawnedID) + ".name");
         int stat2 = GuiStats.this.statWriter.readStat(entitylist$entityegginfo.killEntityStat);
         int stat3 = GuiStats.this.statWriter.readStat(entitylist$entityegginfo.entityKilledByStat);
         String text2 = I18n.format("stat.entityKills", stat2, text);
         String text3 = I18n.format("stat.entityKilledBy", text, stat3);
         if (stat2 == 0) {
            text2 = I18n.format("stat.entityKills.none", text);
         }

         if (stat3 == 0) {
            text3 = I18n.format("stat.entityKilledBy.none", text);
         }

         GuiStats.this.drawString(GuiStats.this.fontRendererObj, text, x + 2 - 10, y + 1, 16777215);
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, text2, x + 2, y + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT, stat2 == 0 ? 6316128 : 9474192
         );
         GuiStats.this.drawString(
            GuiStats.this.fontRendererObj, text3, x + 2, y + 1 + GuiStats.this.fontRendererObj.FONT_HEIGHT * 2, stat3 == 0 ? 6316128 : 9474192
         );
      }

      @Override
      protected int getContentHeight() {
         return this.getSize() * GuiStats.this.fontRendererObj.FONT_HEIGHT * 4;
      }

      public StatsMobsList(Minecraft mcIn) {
         super(mcIn, GuiStats.this.width, GuiStats.this.height, 32, GuiStats.this.height - 64, GuiStats.this.fontRendererObj.FONT_HEIGHT * 4);
         this.update();

         for (EntityEggInfo entityEggInfo : EntityList.entityEggs.values()) {
            if (GuiStats.this.statWriter.readStat(entityEggInfo.killEntityStat) > 0 || GuiStats.this.statWriter.readStat(entityEggInfo.entityKilledByStat) > 0) {
               this.eggInfos.add(entityEggInfo);
            }
         }
      }

      @Override
      protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      protected void drawBackground() {
         GuiStats.this.drawDefaultBackground();
      }
   }
}
