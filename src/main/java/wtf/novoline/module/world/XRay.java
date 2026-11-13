package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.novoline.Novoline;
import wtf.novoline.command.Command;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderBlockEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.time.Stopwatch;

public class XRay extends Module {
   private long lastScanTime;
   private final Map<Block, Integer> blockColors = new HashMap<>();
   private static final Novoline clientInstance = Novoline.getNovoline();
   public Set<Block> visibleBlocks = new HashSet<>();
   private boolean scanFinished;
   private BlockPos blockPos;
   private final List<BlockPos> foundBlocks = new CopyOnWriteArrayList<>();
   private final List<BlockPos> scanQueue = new ArrayList<>();
   private final List<BlockPos> adjacentQueue = new CopyOnWriteArrayList<>();
   public final CheckboxSetting scanner = settingManager.register(new CheckboxSetting(this, "Scanner", false));
   private final ComboSetting scanType = settingManager.register(new ComboSetting(this, "Scan type", 0, "Selected", "Ores", "All") {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final CheckboxSetting saveScannedBlocks = settingManager.register(new CheckboxSetting(this, "Save scanned blocks", true) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final CheckboxSetting scanForClusters = settingManager.register(new CheckboxSetting(this, "Scan for clusters", true) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final CheckboxSetting scanForCaves = settingManager.register(new CheckboxSetting(this, "Scan for caves", true) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final NumberSetting scanSpeed = settingManager.register(new NumberSetting(this, "Scan speed", 5, 1, 20) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final NumberSetting scanXzRadius = settingManager.register(new NumberSetting(this, "Scan XZ Radius", 40, 0, 100) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final NumberSetting scanYUpRadius = settingManager.register(new NumberSetting(this, "Scan Y-Up Radius", 10, 0, 100) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   private final NumberSetting scanYDownRadius = settingManager.register(new NumberSetting(this, "Scan Y-Down Radius", 10, 0, 100) {
      @Override
      public boolean shouldShow() {
         return XRay.this.scanner.isEnabled();
      }
   });
   @EventLink
   private final Listener<RenderBlockEvent> onRenderBlock = renderBlockEvent -> {
      if (!this.scanner.isEnabled()) {
         if (!this.visibleBlocks.contains(renderBlockEvent.iBlockState.getBlock())) {
            renderBlockEvent.cancel();
            return;
         }

         renderBlockEvent.shouldRender = true;
      }
   };
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (this.scanner.isEnabled()) {
         this.foundBlocks
            .forEach(
               blockPos2 -> {
                  IBlockState blockState = minecraft.theWorld.getBlockState(blockPos2);
                  Integer integer = this.blockColors.get(blockState.getBlock());
                  RenderUtils.drawBoxOutlineAtRenderPos(
                     blockState.getBlock().getSelectedBoundingBox(minecraft.theWorld, blockPos2), integer != null ? integer : -1, renderWorldEvent.partialTicks
                  );
               }
            );
      }

      if (!this.scanFinished && this.blockPos != null) {
         RenderUtils.drawBoxOutlineAtRenderPos(
            minecraft.theWorld.getBlockState(this.blockPos).getBlock().getSelectedBoundingBox(minecraft.theWorld, this.blockPos), -65536, renderWorldEvent.partialTicks
         );
      }
   };
   private final Stopwatch scanTimer = new Stopwatch();
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.scanner.isEnabled()) {
         Set set = this.foundBlocks
            .stream()
            .filter(blockPos2 -> !this.visibleBlocks.contains(minecraft.theWorld.getBlockState(blockPos2).getBlock()))
            .collect(Collectors.toSet());
         this.foundBlocks.removeAll(set);
         if (!this.scanFinished) {
            if (this.scanQueue.size() < 1) {
               this.setSuffix("Done");
               this.scanFinished = true;
               double d = (System.currentTimeMillis() - this.lastScanTime) / 1000.0;
               Novoline.getNovoline().sendChatMessage("XRay: All blocks was scanned! (In: " + ((int)(d * 100.0) + 4) / 5 * 5 / 100.0F + " seconds)");
            } else {
               this.setSuffix(String.valueOf(this.scanQueue.size() + this.adjacentQueue.size()));
               if (this.adjacentQueue.size() > 0) {
                  Set set2 = this.adjacentQueue.stream().limit((int)this.scanSpeed.getCurrentValue()).collect(Collectors.toSet());

                  for (BlockPos blockPos3 : (Iterable<BlockPos>) set2) {
                     this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, blockPos3, EnumFacing.UP));
                     this.blockPos = blockPos3;
                  }

                  this.adjacentQueue.removeAll(set2);
               } else {
                  Set set3 = this.scanQueue.stream().limit((int)this.scanSpeed.getCurrentValue()).collect(Collectors.toSet());

                  for (BlockPos blockPos4 : (Iterable<BlockPos>) set3) {
                     this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, blockPos4, EnumFacing.UP));
                     this.blockPos = blockPos4;
                  }

                  this.scanQueue.removeAll(set3);
               }
            }
         }
      } else {
         this.setSuffix(null);
      }

      this.scanTimer.reset();
   };
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (this.scanner.isEnabled() && !this.scanFinished && packetReceiveEvent.packet instanceof S23PacketBlockChange s23PacketBlockChange) {
         Block block2 = s23PacketBlockChange.getBlockState().getBlock();
         if (this.visibleBlocks.contains(block2)) {
            BlockPos blockPosition = s23PacketBlockChange.getBlockPosition();
            this.foundBlocks.add(blockPosition);
            if (this.scanForClusters.isEnabled()) {
               for (int i = 0; i <= 1; i++) {
                  for (int i2 = 0; i2 <= 1; i2++) {
                     for (int i3 = 0; i3 <= 1; i3++) {
                        BlockPos blockPos3 = blockPosition.add(i == 0 ? -1 : 1, i2 == 0 ? -1 : 1, i3 == 0 ? -1 : 1);
                        if (minecraft.theWorld.getChunkFromBlockCoords(blockPos3).isLoaded() && !this.foundBlocks.contains(blockPos3)) {
                           this.adjacentQueue.add(blockPos3);
                        }
                     }
                  }
               }

               for (EnumFacing enumFacing : EnumFacing.VALUES) {
                  BlockPos blockPos4 = blockPosition.add(enumFacing.getDirectionVec());
                  if (minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded() && !this.foundBlocks.contains(blockPos4)) {
                     this.adjacentQueue.add(blockPos4);
                  }
               }
            }
         }
      }
   };

   @Override
   protected void onEnable() {
      if (minecraft.thePlayer != null) {
         this.scanQueue.clear();
         this.blockPos = null;
         if (!this.saveScannedBlocks.isEnabled()) {
            this.foundBlocks.clear();
         }

         BlockPos blockPos3 = new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
         this.scanFinished = false;
         int i = (int)this.scanXzRadius.getCurrentValue();
         int i2 = (int)this.scanYUpRadius.getCurrentValue();

         for (int i3 = -((int)this.scanYDownRadius.getCurrentValue()); i3 <= i2; i3++) {
            for (int i4 = -i; i4 <= i; i4++) {
               for (int i5 = -i; i5 <= i; i5++) {
                  BlockPos blockPos4 = blockPos3.add(i4, i3, i5);
                  IBlockState blockState = minecraft.theWorld.getBlockState(blockPos4);
                  int currentElement = this.scanType.getCurrentElement();
                  if (currentElement == 1 && blockState.getBlock() instanceof BlockOre
                     || currentElement == 2 && blockState.getBlock().isFullCube()
                     || this.visibleBlocks.contains(blockState.getBlock())) {
                     if (this.scanForCaves.isEnabled() && this.isExposed(blockPos4) && this.visibleBlocks.contains(blockState.getBlock())) {
                        this.foundBlocks.add(blockPos4);
                        if (this.scanForClusters.isEnabled()) {
                           for (int i6 = 0; i6 <= 1; i6++) {
                              for (int i7 = 0; i7 <= 1; i7++) {
                                 for (int i8 = 0; i8 <= 1; i8++) {
                                    BlockPos blockPos5 = blockPos4.add(i6 == 0 ? -1 : 1, i7 == 0 ? -1 : 1, i8 == 0 ? -1 : 1);
                                    if (minecraft.theWorld.getChunkFromBlockCoords(blockPos5).isLoaded() && !this.foundBlocks.contains(blockPos5)) {
                                       this.adjacentQueue.add(blockPos5);
                                    }
                                 }
                              }
                           }

                           for (EnumFacing enumFacing : EnumFacing.VALUES) {
                              BlockPos blockPos6 = blockPos4.add(enumFacing.getDirectionVec());
                              if (minecraft.theWorld.getChunkFromBlockCoords(blockPos6).isLoaded() && !this.foundBlocks.contains(blockPos6)) {
                                 this.adjacentQueue.add(blockPos6);
                              }
                           }
                        }
                     } else {
                        this.scanQueue.add(blockPos4);
                     }
                  }
               }
            }
         }

         this.scanQueue.removeAll(this.scanQueue.stream().filter(this.foundBlocks::contains).collect(Collectors.toSet()));
         this.scanQueue
            .sort(
               Comparator.comparingInt(
                  blockPos2 -> (int)Math.sqrt(blockPos2.distanceSq((int)minecraft.thePlayer.posX, (int)minecraft.thePlayer.posY, (int)minecraft.thePlayer.posZ))
               )
            );
         this.lastScanTime = System.currentTimeMillis();
      }
   }

   static Minecraft access$000() {
      return minecraft;
   }

   @Override
   protected void onToggled() {
      if (!this.scanner.isEnabled()) {
         minecraft.renderGlobal.loadRenderers();
      }
   }

   private boolean isExposed(BlockPos blockPos3) {
      BlockPos blockPos4 = blockPos3.add(-1, 0, 0);
      if (!minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded()) {
         return true;
      }

      blockPos4 = blockPos3.add(1, 0, 0);
      if (!minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded()) {
         return true;
      }

      blockPos4 = blockPos3.add(0, -1, 0);
      if (!minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded()) {
         return true;
      }

      blockPos4 = blockPos3.add(0, 1, 0);
      if (!minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded()) {
         return true;
      }

      blockPos4 = blockPos3.add(0, 0, -1);
      if (!minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded()) {
         return true;
      }

      blockPos4 = blockPos3.add(0, 0, 1);
      return !minecraft.theWorld.getBlockState(blockPos4).getBlock().isFullCube() && minecraft.theWorld.getChunkFromBlockCoords(blockPos4).isLoaded();
   }

   public XRay() {
      super(Category.WORLD, "XRay", "See blocks through blocks");
      this.blockColors.put(Blocks.lit_redstone_ore, -33684);
      this.blockColors.put(Blocks.redstone_ore, -33684);
      this.blockColors.put(Blocks.emerald_ore, -10552159);
      this.blockColors.put(Blocks.diamond_ore, -14039553);
      this.blockColors.put(Blocks.lapis_ore, -16750672);
      this.blockColors.put(Blocks.gold_ore, -12469);
      this.blockColors.put(Blocks.iron_ore, -1394537);
      this.blockColors.put(Blocks.coal_ore, -7303024);
      this.visibleBlocks.add(Blocks.diamond_ore);
      this.visibleBlocks.add(Blocks.iron_ore);
      this.visibleBlocks.add(Blocks.coal_ore);
      this.visibleBlocks.add(Blocks.gold_ore);
      this.visibleBlocks.add(Blocks.redstone_ore);
      this.visibleBlocks.add(Blocks.lapis_ore);
      this.visibleBlocks.add(Blocks.emerald_ore);
      clientInstance.getCommandManager().register(new Command("add/remove blocks from xray", "xray") {
         private void handleBlock(Block block2, boolean flag) {
            boolean flag2 = false;
            String localizedName = block2.getLocalizedName();
            if (flag) {
               if (XRay.this.visibleBlocks.remove(block2)) {
                  XRay.clientInstance.sendChatMessage(String.format("'§3%s§7' removed from xray list.", localizedName));
                  flag2 = true;
               } else {
                  XRay.clientInstance.sendChatMessage(String.format("'§3%s§7' wasn't found in xray block list.", localizedName));
               }
            } else if (XRay.this.visibleBlocks.add(block2)) {
               XRay.clientInstance.sendChatMessage(String.format("'§3%s§7' added to xray list.", localizedName));
               flag2 = true;
            } else {
               XRay.clientInstance.sendChatMessage(String.format("XRay list already contains '§3%s§7'.", localizedName));
            }

            if (flag2) {
               Novoline.getNovoline().getConfigRegistry().getConfigManager().saveGlobal();
               if (XRay.this.isEnabled()) {
                  XRay.access$000().renderGlobal.loadRenderers();
               }
            }
         }

         @Override
         public void execute(String[] texts) {
            if (texts.length == 2 && texts[1].equalsIgnoreCase("list")) {
               if (!XRay.this.visibleBlocks.isEmpty()) {
                  for (Block block3 : XRay.this.visibleBlocks) {
                     XRay.clientInstance.sendChatMessage(block3.getLocalizedName());
                  }
               } else {
                  XRay.clientInstance.sendChatMessage("XRay block list is empty.");
               }
            } else {
               if (texts.length == 3) {
                  String text = texts[2];
                  String text2 = texts[1].toLowerCase();
                  boolean flag3 = false;
                  if (text2.equals("add") || (flag3 = text2.equals("remove"))) {
                     try {
                        Block blockById = Block.getBlockById(Integer.parseInt(text));
                        if (blockById != null) {
                           this.handleBlock(blockById, flag3);
                        } else {
                           XRay.clientInstance.sendChatMessage("§cWrong block id!");
                        }
                     } catch (Exception exception) {
                        Block blockFromName = Block.getBlockFromName(text);
                        if (blockFromName != null) {
                           this.handleBlock(blockFromName, flag3);
                        } else {
                           XRay.clientInstance.sendChatMessage("§cWrong block name!");
                        }
                     }

                     return;
                  }
               }

               XRay.clientInstance.sendChatMessage(Language.translate("command.wrong_arguments"));
               XRay.clientInstance.sendChatMessage("§c.xray <add/remove> <block_id/block_name>");
               XRay.clientInstance.sendChatMessage("§c.xray list");
            }
         }
      });
   }
}
