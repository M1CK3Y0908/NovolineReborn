package net.minecraft.block;

import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import wtf.novoline.Novoline;
import wtf.novoline.event.BlockCollisionEvent;

public class Block {
   protected int lightValue;
   protected boolean needsRandomTick;
   protected float blockResistance;
   protected boolean enableStats = true;
   public Block.SoundType stepSound;
   protected boolean isBlockContainer;
   private static final ResourceLocation AIR_ID = new ResourceLocation("air");
   public float blockParticleGravity;
   public static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> blockRegistry = new RegistryNamespacedDefaultedByKey<>(AIR_ID);
   protected double maxZ;
   public static final ObjectIntIdentityMap<IBlockState> BLOCK_STATE_IDS = new ObjectIntIdentityMap<>();
   public float slipperiness;
   public static final Block.SoundType soundTypeStone = new Block.SoundType("stone", 1.0F, 1.0F);
   protected double minX;
   protected double maxX;
   protected final Material blockMaterial;
   public static final Block.SoundType soundTypeWood = new Block.SoundType("wood", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeGravel = new Block.SoundType("gravel", 1.0F, 1.0F);
   public static final Block.SoundType soundTypeGrass = new Block.SoundType("grass", 1.0F, 1.0F);
   protected final MapColor blockMapColor;
   public static final Block.SoundType soundTypePiston = new Block.SoundType("stone", 1.0F, 1.0F);
   protected float blockHardness;
   public static final Block.SoundType soundTypeMetal = new Block.SoundType("stone", 1.0F, 1.5F);
   protected boolean fullBlock;
   public static final Block.SoundType soundTypeGlass = new Block.SoundType("stone", 1.0F, 1.0F) {
      @Override
      public String getBreakSound() {
         return "dig.glass";
      }

      @Override
      public String getPlaceSound() {
         return "step.stone";
      }
   };
   private CreativeTabs displayOnCreativeTab;
   protected int lightOpacity;
   protected double minZ;
   public static final Block.SoundType soundTypeCloth = new Block.SoundType("cloth", 1.0F, 1.0F);
   protected boolean translucent;
   public static final Block.SoundType soundTypeSand = new Block.SoundType("sand", 1.0F, 1.0F);
   private String unlocalizedName;
   protected boolean useNeighborBrightness;
   public static final Block.SoundType soundTypeSnow = new Block.SoundType("snow", 1.0F, 1.0F);
   protected final BlockState blockState;
   public static final Block.SoundType soundTypeLadder = new Block.SoundType("ladder", 1.0F, 1.0F) {
      @Override
      public String getBreakSound() {
         return "dig.wood";
      }
   };
   public static final Block.SoundType soundTypeAnvil = new Block.SoundType("anvil", 0.3F, 1.0F) {
      @Override
      public String getPlaceSound() {
         return "random.anvil_land";
      }

      @Override
      public String getBreakSound() {
         return "dig.stone";
      }
   };
   public static final Block.SoundType SLIME_SOUND = new Block.SoundType("slime", 1.0F, 1.0F) {
      @Override
      public String getStepSound() {
         return "mob.slime.small";
      }

      @Override
      public String getBreakSound() {
         return "mob.slime.big";
      }

      @Override
      public String getPlaceSound() {
         return "mob.slime.big";
      }
   };
   private IBlockState defaultBlockState;
   protected double maxY;
   protected double minY;

   public int quantityDroppedWithBonus(int fortune, Random random) {
      return this.quantityDropped(random);
   }

   protected void dropXpOnBlockBreak(World worldIn, BlockPos pos, int amount) {
      if (!worldIn.isRemote) {
         while (amount > 0) {
            int xpSplit = EntityXPOrb.getXPSplit(amount);
            amount -= xpSplit;
            worldIn.spawnEntityInWorld(new EntityXPOrb(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xpSplit));
         }
      }
   }

   public final void dropBlockAsItem(World worldIn, BlockPos pos, IBlockState state, int forture) {
      this.dropBlockAsItemWithChance(worldIn, pos, state, 1.0F, forture);
   }

   public void fillWithRain(World worldIn, BlockPos pos) {
   }

   public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return worldIn.getBlockState(pos).getBlock().getMaterial().isSolid();
   }

   protected Block setHardness(float hardness) {
      this.blockHardness = hardness;
      if (this.blockResistance < hardness * 5.0F) {
         this.blockResistance = hardness * 5.0F;
      }

      return this;
   }

   public final double getBlockBoundsMinY() {
      return this.minY;
   }

   public static boolean isEqualTo(Block blockIn, Block other) {
      return blockIn != null && other != null ? (blockIn == other ? true : blockIn.isAssociatedBlock(other)) : false;
   }

   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(this);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState();
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   public static Block getBlockFromName(String name) {
      ResourceLocation resourceLocation = new ResourceLocation(name);
      if (blockRegistry.containsKey(resourceLocation)) {
         return blockRegistry.getObject(resourceLocation);
      }

      try {
         return blockRegistry.getObjectById(Integer.parseInt(name));
      } catch (NumberFormatException numberFormatException) {
         return null;
      }
   }

   public boolean isNormalCube() {
      return this.blockMaterial.isOpaque() && this.isFullCube() && !this.canProvidePower();
   }

   public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
      list.add(new ItemStack(itemIn, 1, 0));
   }

   public static void registerBlocks() {
      registerBlock(0, AIR_ID, new BlockAir().setUnlocalizedName("air"));
      registerBlock(1, "stone", new BlockStone().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stone"));
      registerBlock(2, "grass", new BlockGrass().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("grass"));
      registerBlock(3, "dirt", new BlockDirt().setHardness(0.5F).setStepSound(soundTypeGravel).setUnlocalizedName("dirt"));
      Block block2 = new Block(Material.rock)
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("stonebrick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(4, "cobblestone", block2);
      Block block3 = new BlockPlanks().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("wood");
      registerBlock(5, "planks", block3);
      registerBlock(6, "sapling", new BlockSapling().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("sapling"));
      registerBlock(
         7,
         "bedrock",
         new Block(Material.rock)
            .setBlockUnbreakable()
            .setResistance(6000000.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("bedrock")
            .disableStats()
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         8, "flowing_water", new BlockDynamicLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats()
      );
      registerBlock(9, "water", new BlockStaticLiquid(Material.water).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats());
      registerBlock(10, "flowing_lava", new BlockDynamicLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats());
      registerBlock(11, "lava", new BlockStaticLiquid(Material.lava).setHardness(100.0F).setLightLevel(1.0F).setUnlocalizedName("lava").disableStats());
      registerBlock(12, "sand", new BlockSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("sand"));
      registerBlock(13, "gravel", new BlockGravel().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("gravel"));
      registerBlock(14, "gold_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreGold"));
      registerBlock(15, "iron_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreIron"));
      registerBlock(16, "coal_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreCoal"));
      registerBlock(17, "log", new BlockOldLog().setUnlocalizedName("log"));
      registerBlock(18, "leaves", new BlockOldLeaf().setUnlocalizedName("leaves"));
      registerBlock(19, "sponge", new BlockSponge().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("sponge"));
      registerBlock(20, "glass", new BlockGlass(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("glass"));
      registerBlock(21, "lapis_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreLapis"));
      registerBlock(
         22,
         "lapis_block",
         new Block(Material.iron, MapColor.lapisColor)
            .setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("blockLapis")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(23, "dispenser", new BlockDispenser().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dispenser"));
      Block block4 = new BlockSandStone().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("sandStone");
      registerBlock(24, "sandstone", block4);
      registerBlock(25, "noteblock", new BlockNote().setHardness(0.8F).setUnlocalizedName("musicBlock"));
      registerBlock(26, "bed", new BlockBed().setStepSound(soundTypeWood).setHardness(0.2F).setUnlocalizedName("bed").disableStats());
      registerBlock(27, "golden_rail", new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("goldenRail"));
      registerBlock(28, "detector_rail", new BlockRailDetector().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("detectorRail"));
      registerBlock(29, "sticky_piston", new BlockPistonBase(true).setUnlocalizedName("pistonStickyBase"));
      registerBlock(30, "web", new BlockWeb().setLightOpacity(1).setHardness(4.0F).setUnlocalizedName("web"));
      registerBlock(31, "tallgrass", new BlockTallGrass().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tallgrass"));
      registerBlock(32, "deadbush", new BlockDeadBush().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("deadbush"));
      registerBlock(33, "piston", new BlockPistonBase(false).setUnlocalizedName("pistonBase"));
      registerBlock(34, "piston_head", new BlockPistonExtension().setUnlocalizedName("pistonBase"));
      registerBlock(35, "wool", new BlockColored(Material.cloth).setHardness(0.8F).setStepSound(soundTypeCloth).setUnlocalizedName("cloth"));
      registerBlock(36, "piston_extension", new BlockPistonMoving());
      registerBlock(37, "yellow_flower", new BlockYellowFlower().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower1"));
      registerBlock(38, "red_flower", new BlockRedFlower().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("flower2"));
      Block block5 = new BlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setLightLevel(0.125F).setUnlocalizedName("mushroom");
      registerBlock(39, "brown_mushroom", block5);
      Block block6 = new BlockMushroom().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("mushroom");
      registerBlock(40, "red_mushroom", block6);
      registerBlock(
         41,
         "gold_block",
         new Block(Material.iron, MapColor.goldColor)
            .setHardness(3.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockGold")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         42,
         "iron_block",
         new Block(Material.iron, MapColor.ironColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockIron")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         43,
         "double_stone_slab",
         new BlockDoubleStoneSlab().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
      );
      registerBlock(
         44, "stone_slab", new BlockHalfStoneSlab().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab")
      );
      Block block7 = new Block(Material.rock, MapColor.redColor)
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("brick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(45, "brick_block", block7);
      registerBlock(46, "tnt", new BlockTNT().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("tnt"));
      registerBlock(47, "bookshelf", new BlockBookshelf().setHardness(1.5F).setStepSound(soundTypeWood).setUnlocalizedName("bookshelf"));
      registerBlock(
         48,
         "mossy_cobblestone",
         new Block(Material.rock)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("stoneMoss")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(49, "obsidian", new BlockObsidian().setHardness(50.0F).setResistance(2000.0F).setStepSound(soundTypePiston).setUnlocalizedName("obsidian"));
      registerBlock(50, "torch", new BlockTorch().setHardness(0.0F).setLightLevel(0.9375F).setStepSound(soundTypeWood).setUnlocalizedName("torch"));
      registerBlock(51, "fire", new BlockFire().setHardness(0.0F).setLightLevel(1.0F).setStepSound(soundTypeCloth).setUnlocalizedName("fire").disableStats());
      registerBlock(52, "mob_spawner", new BlockMobSpawner().setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("mobSpawner").disableStats());
      registerBlock(
         53,
         "oak_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.OAK)).setUnlocalizedName("stairsWood")
      );
      registerBlock(54, "chest", new BlockChest(0).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chest"));
      registerBlock(
         55, "redstone_wire", new BlockRedstoneWire().setHardness(0.0F).setStepSound(soundTypeStone).setUnlocalizedName("redstoneDust").disableStats()
      );
      registerBlock(56, "diamond_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreDiamond"));
      registerBlock(
         57,
         "diamond_block",
         new Block(Material.iron, MapColor.diamondColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockDiamond")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(58, "crafting_table", new BlockWorkbench().setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("workbench"));
      registerBlock(59, "wheat", new BlockCrops().setUnlocalizedName("crops"));
      Block block8 = new BlockFarmland().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("farmland");
      registerBlock(60, "farmland", block8);
      registerBlock(
         61,
         "furnace",
         new BlockFurnace(false).setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("furnace").setCreativeTab(CreativeTabs.tabDecorations)
      );
      registerBlock(
         62, "lit_furnace", new BlockFurnace(true).setHardness(3.5F).setStepSound(soundTypePiston).setLightLevel(0.875F).setUnlocalizedName("furnace")
      );
      registerBlock(63, "standing_sign", new BlockStandingSign().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats());
      registerBlock(64, "wooden_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorOak").disableStats());
      registerBlock(65, "ladder", new BlockLadder().setHardness(0.4F).setStepSound(soundTypeLadder).setUnlocalizedName("ladder"));
      registerBlock(66, "rail", new BlockRail().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("rail"));
      registerBlock(67, "stone_stairs", new BlockStairs(block2.getDefaultState()).setUnlocalizedName("stairsStone"));
      registerBlock(68, "wall_sign", new BlockWallSign().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("sign").disableStats());
      registerBlock(69, "lever", new BlockLever().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("lever"));
      registerBlock(
         70,
         "stone_pressure_plate",
         new BlockPressurePlate(Material.rock, BlockPressurePlate.Sensitivity.MOBS)
            .setHardness(0.5F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("pressurePlateStone")
      );
      registerBlock(71, "iron_door", new BlockDoor(Material.iron).setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("doorIron").disableStats());
      registerBlock(
         72,
         "wooden_pressure_plate",
         new BlockPressurePlate(Material.wood, BlockPressurePlate.Sensitivity.EVERYTHING)
            .setHardness(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("pressurePlateWood")
      );
      registerBlock(
         73,
         "redstone_ore",
         new BlockRedstoneOre(false)
            .setHardness(3.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("oreRedstone")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         74,
         "lit_redstone_ore",
         new BlockRedstoneOre(true).setLightLevel(0.625F).setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreRedstone")
      );
      registerBlock(75, "unlit_redstone_torch", new BlockRedstoneTorch(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("notGate"));
      registerBlock(
         76,
         "redstone_torch",
         new BlockRedstoneTorch(true)
            .setHardness(0.0F)
            .setLightLevel(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("notGate")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(77, "stone_button", new BlockButtonStone().setHardness(0.5F).setStepSound(soundTypePiston).setUnlocalizedName("button"));
      registerBlock(78, "snow_layer", new BlockSnow().setHardness(0.1F).setStepSound(soundTypeSnow).setUnlocalizedName("snow").setLightOpacity(0));
      registerBlock(79, "ice", new BlockIce().setHardness(0.5F).setLightOpacity(3).setStepSound(soundTypeGlass).setUnlocalizedName("ice"));
      registerBlock(80, "snow", new BlockSnowBlock().setHardness(0.2F).setStepSound(soundTypeSnow).setUnlocalizedName("snow"));
      registerBlock(81, "cactus", new BlockCactus().setHardness(0.4F).setStepSound(soundTypeCloth).setUnlocalizedName("cactus"));
      registerBlock(82, "clay", new BlockClay().setHardness(0.6F).setStepSound(soundTypeGravel).setUnlocalizedName("clay"));
      registerBlock(83, "reeds", new BlockReed().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("reeds").disableStats());
      registerBlock(84, "jukebox", new BlockJukebox().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("jukebox"));
      registerBlock(
         85,
         "fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.OAK.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("fence")
      );
      Block block9 = new BlockPumpkin().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkin");
      registerBlock(86, "pumpkin", block9);
      registerBlock(87, "netherrack", new BlockNetherrack().setHardness(0.4F).setStepSound(soundTypePiston).setUnlocalizedName("hellrock"));
      registerBlock(88, "soul_sand", new BlockSoulSand().setHardness(0.5F).setStepSound(soundTypeSand).setUnlocalizedName("hellsand"));
      registerBlock(
         89, "glowstone", new BlockGlowstone(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setLightLevel(1.0F).setUnlocalizedName("lightgem")
      );
      registerBlock(90, "portal", new BlockPortal().setHardness(-1.0F).setStepSound(soundTypeGlass).setLightLevel(0.75F).setUnlocalizedName("portal"));
      registerBlock(91, "lit_pumpkin", new BlockPumpkin().setHardness(1.0F).setStepSound(soundTypeWood).setLightLevel(1.0F).setUnlocalizedName("litpumpkin"));
      registerBlock(92, "cake", new BlockCake().setHardness(0.5F).setStepSound(soundTypeCloth).setUnlocalizedName("cake").disableStats());
      registerBlock(
         93, "unpowered_repeater", new BlockRedstoneRepeater(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("diode").disableStats()
      );
      registerBlock(
         94, "powered_repeater", new BlockRedstoneRepeater(true).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("diode").disableStats()
      );
      registerBlock(
         95, "stained_glass", new BlockStainedGlass(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("stainedGlass")
      );
      registerBlock(
         96, "trapdoor", new BlockTrapDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("trapdoor").disableStats()
      );
      registerBlock(97, "monster_egg", new BlockSilverfish().setHardness(0.75F).setUnlocalizedName("monsterStoneEgg"));
      Block block10 = new BlockStoneBrick().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stonebricksmooth");
      registerBlock(98, "stonebrick", block10);
      registerBlock(
         99,
         "brown_mushroom_block",
         new BlockHugeMushroom(Material.wood, MapColor.dirtColor, block5).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom")
      );
      registerBlock(
         100,
         "red_mushroom_block",
         new BlockHugeMushroom(Material.wood, MapColor.redColor, block6).setHardness(0.2F).setStepSound(soundTypeWood).setUnlocalizedName("mushroom")
      );
      registerBlock(
         101,
         "iron_bars",
         new BlockPane(Material.iron, true).setHardness(5.0F).setResistance(10.0F).setStepSound(soundTypeMetal).setUnlocalizedName("fenceIron")
      );
      registerBlock(102, "glass_pane", new BlockPane(Material.glass, false).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinGlass"));
      Block block11 = new BlockMelon().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("melon");
      registerBlock(103, "melon_block", block11);
      registerBlock(104, "pumpkin_stem", new BlockStem(block9).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem"));
      registerBlock(105, "melon_stem", new BlockStem(block11).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("pumpkinStem"));
      registerBlock(106, "vine", new BlockVine().setHardness(0.2F).setStepSound(soundTypeGrass).setUnlocalizedName("vine"));
      registerBlock(
         107,
         "fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.OAK).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("fenceGate")
      );
      registerBlock(108, "brick_stairs", new BlockStairs(block7.getDefaultState()).setUnlocalizedName("stairsBrick"));
      registerBlock(
         109,
         "stone_brick_stairs",
         new BlockStairs(block10.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT))
            .setUnlocalizedName("stairsStoneBrickSmooth")
      );
      registerBlock(110, "mycelium", new BlockMycelium().setHardness(0.6F).setStepSound(soundTypeGrass).setUnlocalizedName("mycel"));
      registerBlock(111, "waterlily", new BlockLilyPad().setHardness(0.0F).setStepSound(soundTypeGrass).setUnlocalizedName("waterlily"));
      Block block12 = new BlockNetherBrick()
         .setHardness(2.0F)
         .setResistance(10.0F)
         .setStepSound(soundTypePiston)
         .setUnlocalizedName("netherBrick")
         .setCreativeTab(CreativeTabs.tabBlock);
      registerBlock(112, "nether_brick", block12);
      registerBlock(
         113,
         "nether_brick_fence",
         new BlockFence(Material.rock, MapColor.netherrackColor)
            .setHardness(2.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("netherFence")
      );
      registerBlock(114, "nether_brick_stairs", new BlockStairs(block12.getDefaultState()).setUnlocalizedName("stairsNetherBrick"));
      registerBlock(115, "nether_wart", new BlockNetherWart().setUnlocalizedName("netherStalk"));
      registerBlock(116, "enchanting_table", new BlockEnchantmentTable().setHardness(5.0F).setResistance(2000.0F).setUnlocalizedName("enchantmentTable"));
      registerBlock(117, "brewing_stand", new BlockBrewingStand().setHardness(0.5F).setLightLevel(0.125F).setUnlocalizedName("brewingStand"));
      registerBlock(118, "cauldron", new BlockCauldron().setHardness(2.0F).setUnlocalizedName("cauldron"));
      registerBlock(119, "end_portal", new BlockEndPortal(Material.portal).setHardness(-1.0F).setResistance(6000000.0F));
      registerBlock(
         120,
         "end_portal_frame",
         new BlockEndPortalFrame()
            .setStepSound(soundTypeGlass)
            .setLightLevel(0.125F)
            .setHardness(-1.0F)
            .setUnlocalizedName("endPortalFrame")
            .setResistance(6000000.0F)
            .setCreativeTab(CreativeTabs.tabDecorations)
      );
      registerBlock(
         121,
         "end_stone",
         new Block(Material.rock, MapColor.sandColor)
            .setHardness(3.0F)
            .setResistance(15.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("whiteStone")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         122,
         "dragon_egg",
         new BlockDragonEgg().setHardness(3.0F).setResistance(15.0F).setStepSound(soundTypePiston).setLightLevel(0.125F).setUnlocalizedName("dragonEgg")
      );
      registerBlock(
         123,
         "redstone_lamp",
         new BlockRedstoneLight(false)
            .setHardness(0.3F)
            .setStepSound(soundTypeGlass)
            .setUnlocalizedName("redstoneLight")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(124, "lit_redstone_lamp", new BlockRedstoneLight(true).setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("redstoneLight"));
      registerBlock(
         125, "double_wooden_slab", new BlockDoubleWoodSlab().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
      );
      registerBlock(
         126, "wooden_slab", new BlockHalfWoodSlab().setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("woodSlab")
      );
      registerBlock(127, "cocoa", new BlockCocoa().setHardness(0.2F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("cocoa"));
      registerBlock(
         128,
         "sandstone_stairs",
         new BlockStairs(block4.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH)).setUnlocalizedName("stairsSandStone")
      );
      registerBlock(129, "emerald_ore", new BlockOre().setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("oreEmerald"));
      registerBlock(
         130,
         "ender_chest",
         new BlockEnderChest().setHardness(22.5F).setResistance(1000.0F).setStepSound(soundTypePiston).setUnlocalizedName("enderChest").setLightLevel(0.5F)
      );
      registerBlock(131, "tripwire_hook", new BlockTripWireHook().setUnlocalizedName("tripWireSource"));
      registerBlock(132, "tripwire", new BlockTripWire().setUnlocalizedName("tripWire"));
      registerBlock(
         133,
         "emerald_block",
         new Block(Material.iron, MapColor.emeraldColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockEmerald")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(
         134,
         "spruce_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.SPRUCE)).setUnlocalizedName("stairsWoodSpruce")
      );
      registerBlock(
         135,
         "birch_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.BIRCH)).setUnlocalizedName("stairsWoodBirch")
      );
      registerBlock(
         136,
         "jungle_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.JUNGLE)).setUnlocalizedName("stairsWoodJungle")
      );
      registerBlock(137, "command_block", new BlockCommandBlock().setBlockUnbreakable().setResistance(6000000.0F).setUnlocalizedName("commandBlock"));
      registerBlock(138, "beacon", new BlockBeacon().setUnlocalizedName("beacon").setLightLevel(1.0F));
      registerBlock(139, "cobblestone_wall", new BlockWall(block2).setUnlocalizedName("cobbleWall"));
      registerBlock(140, "flower_pot", new BlockFlowerPot().setHardness(0.0F).setStepSound(soundTypeStone).setUnlocalizedName("flowerPot"));
      registerBlock(141, "carrots", new BlockCarrot().setUnlocalizedName("carrots"));
      registerBlock(142, "potatoes", new BlockPotato().setUnlocalizedName("potatoes"));
      registerBlock(143, "wooden_button", new BlockButtonWood().setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("button"));
      registerBlock(144, "skull", new BlockSkull().setHardness(1.0F).setStepSound(soundTypePiston).setUnlocalizedName("skull"));
      registerBlock(145, "anvil", new BlockAnvil().setHardness(5.0F).setStepSound(soundTypeAnvil).setResistance(2000.0F).setUnlocalizedName("anvil"));
      registerBlock(146, "trapped_chest", new BlockChest(1).setHardness(2.5F).setStepSound(soundTypeWood).setUnlocalizedName("chestTrap"));
      registerBlock(
         147,
         "light_weighted_pressure_plate",
         new BlockPressurePlateWeighted(Material.iron, 15, MapColor.goldColor)
            .setHardness(0.5F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("weightedPlate_light")
      );
      registerBlock(
         148,
         "heavy_weighted_pressure_plate",
         new BlockPressurePlateWeighted(Material.iron).setHardness(0.5F).setStepSound(soundTypeWood).setUnlocalizedName("weightedPlate_heavy")
      );
      registerBlock(
         149,
         "unpowered_comparator",
         new BlockRedstoneComparator(false).setHardness(0.0F).setStepSound(soundTypeWood).setUnlocalizedName("comparator").disableStats()
      );
      registerBlock(
         150,
         "powered_comparator",
         new BlockRedstoneComparator(true).setHardness(0.0F).setLightLevel(0.625F).setStepSound(soundTypeWood).setUnlocalizedName("comparator").disableStats()
      );
      registerBlock(151, "daylight_detector", new BlockDaylightDetector(false));
      registerBlock(
         152,
         "redstone_block",
         new BlockCompressedPowered(Material.iron, MapColor.tntColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypeMetal)
            .setUnlocalizedName("blockRedstone")
            .setCreativeTab(CreativeTabs.tabRedstone)
      );
      registerBlock(
         153,
         "quartz_ore",
         new BlockOre(MapColor.netherrackColor).setHardness(3.0F).setResistance(5.0F).setStepSound(soundTypePiston).setUnlocalizedName("netherquartz")
      );
      registerBlock(154, "hopper", new BlockHopper().setHardness(3.0F).setResistance(8.0F).setStepSound(soundTypeMetal).setUnlocalizedName("hopper"));
      Block block13 = new BlockQuartz().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("quartzBlock");
      registerBlock(155, "quartz_block", block13);
      registerBlock(
         156,
         "quartz_stairs",
         new BlockStairs(block13.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.DEFAULT)).setUnlocalizedName("stairsQuartz")
      );
      registerBlock(157, "activator_rail", new BlockRailPowered().setHardness(0.7F).setStepSound(soundTypeMetal).setUnlocalizedName("activatorRail"));
      registerBlock(158, "dropper", new BlockDropper().setHardness(3.5F).setStepSound(soundTypePiston).setUnlocalizedName("dropper"));
      registerBlock(
         159,
         "stained_hardened_clay",
         new BlockColored(Material.rock).setHardness(1.25F).setResistance(7.0F).setStepSound(soundTypePiston).setUnlocalizedName("clayHardenedStained")
      );
      registerBlock(
         160, "stained_glass_pane", new BlockStainedGlassPane().setHardness(0.3F).setStepSound(soundTypeGlass).setUnlocalizedName("thinStainedGlass")
      );
      registerBlock(161, "leaves2", new BlockNewLeaf().setUnlocalizedName("leaves"));
      registerBlock(162, "log2", new BlockNewLog().setUnlocalizedName("log"));
      registerBlock(
         163,
         "acacia_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.ACACIA)).setUnlocalizedName("stairsWoodAcacia")
      );
      registerBlock(
         164,
         "dark_oak_stairs",
         new BlockStairs(block3.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK)).setUnlocalizedName("stairsWoodDarkOak")
      );
      registerBlock(165, "slime", new BlockSlime().setUnlocalizedName("slime").setStepSound(SLIME_SOUND));
      registerBlock(166, "barrier", new BlockBarrier().setUnlocalizedName("barrier"));
      registerBlock(
         167,
         "iron_trapdoor",
         new BlockTrapDoor(Material.iron).setHardness(5.0F).setStepSound(soundTypeMetal).setUnlocalizedName("ironTrapdoor").disableStats()
      );
      registerBlock(
         168, "prismarine", new BlockPrismarine().setHardness(1.5F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("prismarine")
      );
      registerBlock(
         169,
         "sea_lantern",
         new BlockSeaLantern(Material.glass).setHardness(0.3F).setStepSound(soundTypeGlass).setLightLevel(1.0F).setUnlocalizedName("seaLantern")
      );
      registerBlock(
         170, "hay_block", new BlockHay().setHardness(0.5F).setStepSound(soundTypeGrass).setUnlocalizedName("hayBlock").setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(171, "carpet", new BlockCarpet().setHardness(0.1F).setStepSound(soundTypeCloth).setUnlocalizedName("woolCarpet").setLightOpacity(0));
      registerBlock(
         172, "hardened_clay", new BlockHardenedClay().setHardness(1.25F).setResistance(7.0F).setStepSound(soundTypePiston).setUnlocalizedName("clayHardened")
      );
      registerBlock(
         173,
         "coal_block",
         new Block(Material.rock, MapColor.blackColor)
            .setHardness(5.0F)
            .setResistance(10.0F)
            .setStepSound(soundTypePiston)
            .setUnlocalizedName("blockCoal")
            .setCreativeTab(CreativeTabs.tabBlock)
      );
      registerBlock(174, "packed_ice", new BlockPackedIce().setHardness(0.5F).setStepSound(soundTypeGlass).setUnlocalizedName("icePacked"));
      registerBlock(175, "double_plant", new BlockDoublePlant());
      registerBlock(
         176,
         "standing_banner",
         new BlockBanner.BlockBannerStanding().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("banner").disableStats()
      );
      registerBlock(
         177, "wall_banner", new BlockBanner.BlockBannerHanging().setHardness(1.0F).setStepSound(soundTypeWood).setUnlocalizedName("banner").disableStats()
      );
      registerBlock(178, "daylight_detector_inverted", new BlockDaylightDetector(true));
      Block block14 = new BlockRedSandstone().setStepSound(soundTypePiston).setHardness(0.8F).setUnlocalizedName("redSandStone");
      registerBlock(179, "red_sandstone", block14);
      registerBlock(
         180,
         "red_sandstone_stairs",
         new BlockStairs(block14.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH))
            .setUnlocalizedName("stairsRedSandStone")
      );
      registerBlock(
         181,
         "double_stone_slab2",
         new BlockDoubleStoneSlabNew().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab2")
      );
      registerBlock(
         182, "stone_slab2", new BlockHalfStoneSlabNew().setHardness(2.0F).setResistance(10.0F).setStepSound(soundTypePiston).setUnlocalizedName("stoneSlab2")
      );
      registerBlock(
         183,
         "spruce_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.SPRUCE)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("spruceFenceGate")
      );
      registerBlock(
         184,
         "birch_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.BIRCH).setHardness(2.0F).setResistance(5.0F).setStepSound(soundTypeWood).setUnlocalizedName("birchFenceGate")
      );
      registerBlock(
         185,
         "jungle_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.JUNGLE)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("jungleFenceGate")
      );
      registerBlock(
         186,
         "dark_oak_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.DARK_OAK)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("darkOakFenceGate")
      );
      registerBlock(
         187,
         "acacia_fence_gate",
         new BlockFenceGate(BlockPlanks.EnumType.ACACIA)
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("acaciaFenceGate")
      );
      registerBlock(
         188,
         "spruce_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.SPRUCE.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("spruceFence")
      );
      registerBlock(
         189,
         "birch_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.BIRCH.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("birchFence")
      );
      registerBlock(
         190,
         "jungle_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.JUNGLE.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("jungleFence")
      );
      registerBlock(
         191,
         "dark_oak_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.DARK_OAK.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("darkOakFence")
      );
      registerBlock(
         192,
         "acacia_fence",
         new BlockFence(Material.wood, BlockPlanks.EnumType.ACACIA.getMapColor())
            .setHardness(2.0F)
            .setResistance(5.0F)
            .setStepSound(soundTypeWood)
            .setUnlocalizedName("acaciaFence")
      );
      registerBlock(
         193, "spruce_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorSpruce").disableStats()
      );
      registerBlock(
         194, "birch_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorBirch").disableStats()
      );
      registerBlock(
         195, "jungle_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorJungle").disableStats()
      );
      registerBlock(
         196, "acacia_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorAcacia").disableStats()
      );
      registerBlock(
         197, "dark_oak_door", new BlockDoor(Material.wood).setHardness(3.0F).setStepSound(soundTypeWood).setUnlocalizedName("doorDarkOak").disableStats()
      );
      blockRegistry.validateKey();

      for (Block block15 : blockRegistry) {
         if (block15.blockMaterial == Material.air) {
            block15.useNeighborBrightness = false;
         } else {
            boolean flag = false;
            boolean flag2 = block15 instanceof BlockStairs;
            boolean flag3 = block15 instanceof BlockSlab;
            boolean flag4 = block15 == block8;
            boolean translucent2 = block15.translucent;
            boolean flag5 = block15.lightOpacity == 0;
            if (flag2 || flag3 || flag4 || translucent2 || flag5) {
               flag = true;
            }

            block15.useNeighborBrightness = flag;
         }
      }

      for (Block block16 : blockRegistry) {
         UnmodifiableIterator unmodifiableIterator = block16.getBlockState().getValidStates().iterator();

         while (unmodifiableIterator.hasNext()) {
            IBlockState iBlockState = (IBlockState)unmodifiableIterator.next();
            int i = blockRegistry.getIDForObject(block16) << 4 | block16.getMetaFromState(iBlockState);
            BLOCK_STATE_IDS.put(iBlockState, i);
         }
      }
   }

   public final double getBlockBoundsMinX() {
      return this.minX;
   }

   public float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos) {
      float blockHardness2 = this.getBlockHardness(worldIn, pos);
      return blockHardness2 < 0.0F
         ? 0.0F
         : (
            !playerIn.canHarvestBlock(this)
               ? playerIn.getToolDigEfficiency(this) / blockHardness2 / 100.0F
               : playerIn.getToolDigEfficiency(this) / blockHardness2 / 30.0F
         );
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
   }

   public boolean hasComparatorInputOverride() {
      return false;
   }

   public boolean requiresUpdates() {
      return true;
   }

   protected Block setBlockUnbreakable() {
      this.setHardness(-1.0F);
      return this;
   }

   public boolean canSpawnInBlock() {
      return !this.blockMaterial.isSolid() && !this.blockMaterial.isLiquid();
   }

   protected Block setLightOpacity(int opacity) {
      this.lightOpacity = opacity;
      return this;
   }

   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      return new AxisAlignedBB(
         pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
      );
   }

   public float getExplosionResistance(Entity exploder) {
      return this.blockResistance / 5.0F;
   }

   public final int colorMultiplier(IBlockAccess worldIn, BlockPos pos) {
      return this.colorMultiplier(worldIn, pos, 0);
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, Entity entityIn) {
   }

   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
   }

   public boolean isFullCube() {
      return true;
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
      player.triggerAchievement(StatList.mineBlockStatArray[getIdFromBlock(this)]);
      player.addExhaustion(0.025F);
      if (this.canSilkHarvest() && EnchantmentHelper.getSilkTouchModifier(player)) {
         ItemStack itemStack = this.createStackedBlock(state);
         if (itemStack != null) {
            spawnAsEntity(worldIn, pos, itemStack);
         }
      } else {
         int fortuneModifier = EnchantmentHelper.getFortuneModifier(player);
         this.dropBlockAsItem(worldIn, pos, state, fortuneModifier);
      }
   }

   protected Block setLightLevel(float value) {
      this.lightValue = (int)(15.0F * value);
      return this;
   }

   public boolean getTickRandomly() {
      return this.needsRandomTick;
   }

   public int getLightOpacity() {
      return this.lightOpacity;
   }

   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
   }

   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return 0;
   }

   public static Block getBlockFromItem(Item itemIn) {
      return itemIn instanceof ItemBlock ? ((ItemBlock)itemIn).getBlock() : null;
   }

   public boolean isTranslucent() {
      return this.translucent;
   }

   public boolean isAssociatedBlock(Block other) {
      return this == other;
   }

   public boolean isFlowerPot() {
      return false;
   }

   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
   }

   public boolean isCollidable() {
      return true;
   }

   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
   }

   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return 0;
   }

   public boolean isBlockNormalCube() {
      return this.blockMaterial.blocksMovement() && this.isFullCube();
   }

   public boolean isFullBlock() {
      return this.fullBlock;
   }

   public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion) {
      return motion;
   }

   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return 16777215;
   }

   protected Block setTickRandomly(boolean shouldTick) {
      this.needsRandomTick = shouldTick;
      return this;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state;
   }

   protected boolean canSilkHarvest() {
      return this.isFullCube() && !this.isBlockContainer;
   }

   protected final void setDefaultState(IBlockState state) {
      this.defaultBlockState = state;
   }

   public static IBlockState getStateById(int id) {
      int i = id & 4095;
      int i2 = id >> 12 & 15;
      return getBlockById(i).getStateFromMeta(i2);
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
   }

   private boolean isVecInsideYZBounds(Vec3 point) {
      return point == null ? false : point.yCoord >= this.minY && point.yCoord <= this.maxY && point.zCoord >= this.minZ && point.zCoord <= this.maxZ;
   }

   public MapColor getMapColor(IBlockState state) {
      return this.blockMapColor;
   }

   public Block setUnlocalizedName(String name) {
      this.unlocalizedName = name;
      return this;
   }

   public float getBlockHardness(World worldIn, BlockPos pos) {
      return this.blockHardness;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public int getMobilityFlag() {
      return this.blockMaterial.getMaterialMobility();
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + ".name");
   }

   public int getDamageValue(World worldIn, BlockPos pos) {
      return this.damageDropped(worldIn.getBlockState(pos));
   }

   public boolean canProvidePower() {
      return false;
   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getStateFromMeta(meta);
   }

   public static int getIdFromBlock(Block blockIn) {
      return blockRegistry.getIDForObject(blockIn);
   }

   public int tickRate(World worldIn) {
      return 10;
   }

   public final IBlockState getDefaultState() {
      return this.defaultBlockState;
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         int i = this.quantityDroppedWithBonus(fortune, worldIn.rand);

         for (int innerIndex = 0; innerIndex < i; innerIndex++) {
            if (worldIn.rand.nextFloat() <= chance) {
               Item item = this.getItemDropped(state, worldIn.rand, fortune);
               if (item != null) {
                  spawnAsEntity(worldIn, pos, new ItemStack(item, 1, this.damageDropped(state)));
               }
            }
         }
      }
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
   }

   public final double getBlockBoundsMaxZ() {
      return this.maxZ;
   }

   public int getLightValue() {
      return this.lightValue;
   }

   public boolean isOpaqueCube() {
      return true;
   }

   protected Block setStepSound(Block.SoundType sound) {
      this.stepSound = sound;
      return this;
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return !this.blockMaterial.blocksMovement();
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      return false;
   }

   private static void registerBlock(int id, ResourceLocation textualID, Block block_) {
      blockRegistry.register(id, textualID, block_);
   }

   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getBlock().blockMaterial.isReplaceable();
   }

   public Block(Material blockMaterialIn, MapColor blockMapColorIn) {
      this.stepSound = soundTypeStone;
      this.blockParticleGravity = 1.0F;
      this.slipperiness = 0.6F;
      this.blockMaterial = blockMaterialIn;
      this.blockMapColor = blockMapColorIn;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.fullBlock = this.isOpaqueCube();
      this.lightOpacity = this.isOpaqueCube() ? 255 : 0;
      this.translucent = !blockMaterialIn.blocksLight();
      this.blockState = this.createBlockState();
      this.setDefaultState(this.blockState.getBaseState());
   }

   protected final void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public Block setCreativeTab(CreativeTabs tab) {
      this.displayOnCreativeTab = tab;
      return this;
   }

   protected Block(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
   }

   public boolean getUseNeighborBrightness() {
      return this.useNeighborBrightness;
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.SOLID;
   }

   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      AxisAlignedBB collisionBoundingBox = this.getCollisionBoundingBox(worldIn, pos, state);
      if (collidingEntity == Minecraft.getMinecraft().thePlayer) {
         BlockCollisionEvent blockCollisionEvent = new BlockCollisionEvent(pos, state, collisionBoundingBox);
         Novoline.getNovoline().getBus().post(blockCollisionEvent);
         if (blockCollisionEvent.isCancelled()) {
            return;
         }

         collisionBoundingBox = blockCollisionEvent.axisAlignedBB;
      }

      if (collisionBoundingBox != null && mask.intersectsWith(collisionBoundingBox)) {
         list.add(collisionBoundingBox);
      }
   }

   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      start = start.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
      end = end.addVector(-pos.getX(), -pos.getY(), -pos.getZ());
      Vec3 vec3 = start.getIntermediateWithXValue(end, this.minX);
      Vec3 vec37 = start.getIntermediateWithXValue(end, this.maxX);
      Vec3 vec38 = start.getIntermediateWithYValue(end, this.minY);
      Vec3 vec39 = start.getIntermediateWithYValue(end, this.maxY);
      Vec3 vec310 = start.getIntermediateWithZValue(end, this.minZ);
      Vec3 vec311 = start.getIntermediateWithZValue(end, this.maxZ);
      if (!this.isVecInsideYZBounds(vec3)) {
         vec3 = null;
      }

      if (!this.isVecInsideYZBounds(vec37)) {
         vec37 = null;
      }

      if (!this.isVecInsideXZBounds(vec38)) {
         vec38 = null;
      }

      if (!this.isVecInsideXZBounds(vec39)) {
         vec39 = null;
      }

      if (!this.isVecInsideXYBounds(vec310)) {
         vec310 = null;
      }

      if (!this.isVecInsideXYBounds(vec311)) {
         vec311 = null;
      }

      Vec3 vec32 = null;
      if (vec3 != null) {
         vec32 = vec3;
      }

      if (vec37 != null && (vec32 == null || start.squareDistanceTo(vec37) < start.squareDistanceTo(vec32))) {
         vec32 = vec37;
      }

      if (vec38 != null && (vec32 == null || start.squareDistanceTo(vec38) < start.squareDistanceTo(vec32))) {
         vec32 = vec38;
      }

      if (vec39 != null && (vec32 == null || start.squareDistanceTo(vec39) < start.squareDistanceTo(vec32))) {
         vec32 = vec39;
      }

      if (vec310 != null && (vec32 == null || start.squareDistanceTo(vec310) < start.squareDistanceTo(vec32))) {
         vec32 = vec310;
      }

      if (vec311 != null && (vec32 == null || start.squareDistanceTo(vec311) < start.squareDistanceTo(vec32))) {
         vec32 = vec311;
      }

      if (vec32 == null) {
         return null;
      }

      EnumFacing facing = null;
      if (vec32 == vec3) {
         facing = EnumFacing.WEST;
      }

      if (vec32 == vec37) {
         facing = EnumFacing.EAST;
      }

      if (vec32 == vec38) {
         facing = EnumFacing.DOWN;
      }

      if (vec32 == vec39) {
         facing = EnumFacing.UP;
      }

      if (vec32 == vec310) {
         facing = EnumFacing.NORTH;
      }

      if (vec32 == vec311) {
         facing = EnumFacing.SOUTH;
      }

      return new MovingObjectPosition(vec32.addVector(pos.getX(), pos.getY(), pos.getZ()), facing, pos);
   }

   public boolean canDropFromExplosion(Explosion explosionIn) {
      return true;
   }

   protected Block disableStats() {
      this.enableStats = false;
      return this;
   }

   public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos) {
      Block block2 = worldIn.getBlockState(pos).getBlock();
      int combinedLight = worldIn.getCombinedLight(pos, block2.getLightValue());
      if (combinedLight == 0 && block2 instanceof BlockSlab) {
         pos = pos.down();
         block2 = worldIn.getBlockState(pos).getBlock();
         return worldIn.getCombinedLight(pos, block2.getLightValue());
      } else {
         return combinedLight;
      }
   }

   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return new AxisAlignedBB(
         pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
      );
   }

   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.DOWN && this.minY > 0.0
         ? true
         : (
            side == EnumFacing.UP && this.maxY < 1.0
               ? true
               : (
                  side == EnumFacing.NORTH && this.minZ > 0.0
                     ? true
                     : (
                        side == EnumFacing.SOUTH && this.maxZ < 1.0
                           ? true
                           : (
                              side == EnumFacing.WEST && this.minX > 0.0
                                 ? true
                                 : (side == EnumFacing.EAST && this.maxX < 1.0 ? true : !worldIn.getBlockState(pos).getBlock().isOpaqueCube())
                           )
                     )
               )
         );
   }

   public final double getBlockBoundsMaxX() {
      return this.maxX;
   }

   public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
   }

   public boolean isVisuallyOpaque() {
      return this.blockMaterial.blocksMovement() && this.isFullCube();
   }

   public float getAmbientOcclusionLightValue() {
      return this.isBlockNormalCube() ? 0.2F : 1.0F;
   }

   private boolean isVecInsideXZBounds(Vec3 point) {
      return point == null ? false : point.xCoord >= this.minX && point.xCoord <= this.maxX && point.zCoord >= this.minZ && point.zCoord <= this.maxZ;
   }

   public Block.EnumOffsetType getOffsetType() {
      return Block.EnumOffsetType.NONE;
   }

   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return 0;
   }

   private boolean isVecInsideXYBounds(Vec3 point) {
      return point == null ? false : point.xCoord >= this.minX && point.xCoord <= this.maxX && point.yCoord >= this.minY && point.yCoord <= this.maxY;
   }

   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      return false;
   }

   public final double getBlockBoundsMaxY() {
      return this.maxY;
   }

   public void onLanded(World worldIn, Entity entityIn) {
      entityIn.motionY = 0.0;
   }

   public boolean canReplace(World worldIn, BlockPos pos, EnumFacing side, ItemStack stack) {
      return this.canPlaceBlockOnSide(worldIn, pos, side);
   }

   public static Block getBlockById(int id) {
      return blockRegistry.getObjectById(id);
   }

   public boolean hasTileEntity() {
      return this.isBlockContainer;
   }

   protected BlockState createBlockState() {
      return new BlockState(this);
   }

   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return this.isCollidable();
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public int getMetaFromState(IBlockState state) {
      if (state != null && !state.getPropertyNames().isEmpty()) {
         throw new IllegalArgumentException("Don't know how to convert " + state + " back into data...");
      } else {
         return 0;
      }
   }

   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return this.canPlaceBlockAt(worldIn, pos);
   }

   public String getUnlocalizedName() {
      return "tile." + this.unlocalizedName;
   }

   protected ItemStack createStackedBlock(IBlockState state) {
      int i = 0;
      Item item = Item.getItemFromBlock(this);
      if (item != null && item.getHasSubtypes()) {
         i = this.getMetaFromState(state);
      }

      return new ItemStack(item, 1, i);
   }

   public IBlockState getStateForEntityRender(IBlockState state) {
      return state;
   }

   protected Block setResistance(float resistance) {
      this.blockResistance = resistance * 3.0F;
      return this;
   }

   public int getRenderColor(IBlockState state) {
      return 16777215;
   }

   public boolean getEnableStats() {
      return this.enableStats;
   }

   public Material getMaterial() {
      return this.blockMaterial;
   }

   public void setBlockBoundsForItemRender() {
   }

   public int getRenderType() {
      return 3;
   }

   @Override
   public String toString() {
      return "Block{" + blockRegistry.getNameForObject(this) + "}";
   }

   public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
      if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
         float f = 0.5F;
         double d = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
         double d2 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
         double d3 = worldIn.rand.nextFloat() * f + (1.0F - f) * 0.5;
         EntityItem entityItem = new EntityItem(worldIn, pos.getX() + d, pos.getY() + d2, pos.getZ() + d3, stack);
         entityItem.setDefaultPickupDelay();
         worldIn.spawnEntityInWorld(entityItem);
      }
   }

   public static int getStateId(IBlockState state) {
      Block block = state.getBlock();
      return getIdFromBlock(block) + (block.getMetaFromState(state) << 12);
   }

   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return false;
   }

   private static void registerBlock(int id, String textualID, Block block_) {
      registerBlock(id, new ResourceLocation(textualID), block_);
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
      this.updateTick(worldIn, pos, state, random);
   }

   public int quantityDropped(Random random) {
      return 1;
   }

   public CreativeTabs getCreativeTabToDisplayOn() {
      return this.displayOnCreativeTab;
   }

   public final double getBlockBoundsMinZ() {
      return this.minZ;
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.fall(fallDistance, 1.0F);
   }

   public enum EnumOffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class SoundType {
      public final String soundName;
      public final float volume;
      public final float frequency;

      public float getVolume() {
         return this.volume;
      }

      public String getPlaceSound() {
         return this.getBreakSound();
      }

      public float getFrequency() {
         return this.frequency;
      }

      public String getBreakSound() {
         return "dig." + this.soundName;
      }

      public SoundType(String name, float volume, float frequency) {
         this.soundName = name;
         this.volume = volume;
         this.frequency = frequency;
      }

      public String getStepSound() {
         return "step." + this.soundName;
      }
   }
}
