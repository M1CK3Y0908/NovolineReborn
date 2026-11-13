package net.minecraft.init;

import com.mojang.authlib.GameProfile;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   private static final PrintStream SYSOUT = System.out;
   private static boolean alreadyRegistered = false;
   private static final Logger LOGGER = LogManager.getLogger();

   public static void printToSYSOUT(String text2) {
      SYSOUT.println(text2);
   }

   public static boolean isRegistered() {
      return alreadyRegistered;
   }

   private static void redirectOutputToLog() {
      System.setErr(new LoggingPrintStream("STDERR", System.err));
      System.setOut(new LoggingPrintStream("STDOUT", SYSOUT));
   }

   static void registerDispenserBehaviors() {
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.arrow, new BehaviorProjectileDispense() {
         @Override
         protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
            EntityArrow entityArrow = new EntityArrow(worldIn, position.getX(), position.getY(), position.getZ());
            entityArrow.canBePickedUp = 1;
            return entityArrow;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.egg, new BehaviorProjectileDispense() {
         @Override
         protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityEgg(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.snowball, new BehaviorProjectileDispense() {
         @Override
         protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntitySnowball(worldIn, position.getX(), position.getY(), position.getZ());
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.experience_bottle, new BehaviorProjectileDispense() {
         @Override
         protected float getProjectileVelocity() {
            return super.getProjectileVelocity() * 1.25F;
         }

         @Override
         protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
            return new EntityExpBottle(worldIn, position.getX(), position.getY(), position.getZ());
         }

         @Override
         protected float getProjectileInaccuracy() {
            return super.getProjectileInaccuracy() * 0.5F;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.potionitem, new IBehaviorDispenseItem() {
         private final BehaviorDefaultDispenseItem defaultBehavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispense(IBlockSource source, final ItemStack stack) {
            return ItemPotion.isSplash(stack.getMetadata()) ? (new BehaviorProjectileDispense() {
               @Override
               protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
                  return new EntityPotion(worldIn, position.getX(), position.getY(), position.getZ(), stack.copy());
               }

               @Override
               protected float getProjectileVelocity() {
                  return super.getProjectileVelocity() * 1.25F;
               }

               @Override
               protected float getProjectileInaccuracy() {
                  return super.getProjectileInaccuracy() * 0.5F;
               }
            }).dispense(source, stack) : this.defaultBehavior.dispense(source, stack);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.spawn_egg, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            double xCoordinate = source.getX() + facing.getFrontOffsetX();
            double yCoordinate = source.getBlockPos().getY() + 0.2F;
            double zCoordinate = source.getZ() + facing.getFrontOffsetZ();
            Entity entity = ItemMonsterPlacer.spawnCreature(source.getWorld(), stack.getMetadata(), xCoordinate, yCoordinate, zCoordinate);
            if (entity instanceof EntityLivingBase && stack.hasDisplayName()) {
               ((EntityLiving)entity).setCustomNameTag(stack.getDisplayName());
            }

            stack.splitStack(1);
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fireworks, new BehaviorDefaultDispenseItem() {
         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            double xCoordinate = source.getX() + facing.getFrontOffsetX();
            double yCoordinate = source.getBlockPos().getY() + 0.2F;
            double zCoordinate = source.getZ() + facing.getFrontOffsetZ();
            EntityFireworkRocket entityFireworkRocket = new EntityFireworkRocket(source.getWorld(), xCoordinate, yCoordinate, zCoordinate, stack);
            source.getWorld().spawnEntityInWorld(entityFireworkRocket);
            stack.splitStack(1);
            return stack;
         }

         @Override
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1002, source.getBlockPos(), 0);
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.fire_charge, new BehaviorDefaultDispenseItem() {
         @Override
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1009, source.getBlockPos(), 0);
         }

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            IPosition position = BlockDispenser.getDispensePosition(source);
            double xCoordinate = position.getX() + facing.getFrontOffsetX() * 0.3F;
            double yCoordinate = position.getY() + facing.getFrontOffsetY() * 0.3F;
            double zCoordinate = position.getZ() + facing.getFrontOffsetZ() * 0.3F;
            World world = source.getWorld();
            Random random = world.rand;
            double d = random.nextGaussian() * 0.05 + facing.getFrontOffsetX();
            double d2 = random.nextGaussian() * 0.05 + facing.getFrontOffsetY();
            double d3 = random.nextGaussian() * 0.05 + facing.getFrontOffsetZ();
            world.spawnEntityInWorld(new EntitySmallFireball(world, xCoordinate, yCoordinate, zCoordinate, d, d2, d3));
            stack.splitStack(1);
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.boat, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem defaultBehavior = new BehaviorDefaultDispenseItem();

         @Override
         protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
         }

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            EnumFacing facing2 = BlockDispenser.getFacing(source.getBlockMetadata());
            World world2 = source.getWorld();
            double d4 = source.getX() + facing2.getFrontOffsetX() * 1.125F;
            double d5 = source.getY() + facing2.getFrontOffsetY() * 1.125F;
            double d6 = source.getZ() + facing2.getFrontOffsetZ() * 1.125F;
            BlockPos blockPos2 = source.getBlockPos().offset(facing2);
            Material material2 = world2.getBlockState(blockPos2).getBlock().getMaterial();
            double d7;
            if (Material.water.equals(material2)) {
               d7 = 1.0;
            } else {
               if (!Material.air.equals(material2) || !Material.water.equals(world2.getBlockState(blockPos2.down()).getBlock().getMaterial())) {
                  return this.defaultBehavior.dispense(source, stack);
               }

               d7 = 0.0;
            }

            EntityBoat entityBoat = new EntityBoat(world2, d4, d5 + d7, d6);
            world2.spawnEntityInWorld(entityBoat);
            stack.splitStack(1);
            return stack;
         }
      });
      BehaviorDefaultDispenseItem behaviorDefaultDispenseItem = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem defaultBehavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            ItemBucket itemBucket = (ItemBucket)stack.getItem();
            BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (itemBucket.tryPlaceContainedLiquid(source.getWorld(), blockPos)) {
               stack.setItem(Items.bucket);
               stack.stackSize = 1;
               return stack;
            } else {
               return this.defaultBehavior.dispense(source, stack);
            }
         }
      };
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.lava_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.water_bucket, behaviorDefaultDispenseItem);
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.bucket, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem defaultBehavior = new BehaviorDefaultDispenseItem();

         @Override
         public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world3 = source.getWorld();
            BlockPos blockPos3 = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            IBlockState blockState = world3.getBlockState(blockPos3);
            Block block2 = blockState.getBlock();
            Material material3 = block2.getMaterial();
            Item item2;
            if (Material.water.equals(material3) && block2 instanceof BlockLiquid && blockState.getValue(BlockLiquid.LEVEL) == 0) {
               item2 = Items.water_bucket;
            } else {
               if (!Material.lava.equals(material3) || !(block2 instanceof BlockLiquid) || blockState.getValue(BlockLiquid.LEVEL) != 0) {
                  return super.dispenseStack(source, stack);
               }

               item2 = Items.lava_bucket;
            }

            world3.setBlockToAir(blockPos3);
            if (--stack.stackSize == 0) {
               stack.setItem(item2);
               stack.stackSize = 1;
            } else if (source.<TileEntityDispenser>getBlockTileEntity().addItemStack(new ItemStack(item2)) < 0) {
               this.defaultBehavior.dispense(source, new ItemStack(item2));
            }

            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.flint_and_steel, new BehaviorDefaultDispenseItem() {
         private boolean successful = true;

         @Override
         protected void playDispenseSound(IBlockSource source) {
            if (this.successful) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }

         @Override
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            if (world.isAirBlock(blockPos)) {
               world.setBlockState(blockPos, Blocks.fire.getDefaultState());
               if (stack.attemptDamageItem(1, world.rand)) {
                  stack.stackSize = 0;
               }
            } else if (world.getBlockState(blockPos).getBlock() == Blocks.tnt) {
               Blocks.tnt.onBlockDestroyedByPlayer(world, blockPos, Blocks.tnt.getDefaultState().withProperty(BlockTNT.EXPLODE, true));
               world.setBlockToAir(blockPos);
            } else {
               this.successful = false;
            }

            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.dye, new BehaviorDefaultDispenseItem() {
         private boolean successful = true;

         @Override
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            if (EnumDyeColor.WHITE == EnumDyeColor.byDyeDamage(stack.getMetadata())) {
               World world = source.getWorld();
               BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
               if (ItemDye.applyBonemeal(stack, world, blockPos)) {
                  if (!world.isRemote) {
                     world.playAuxSFX(2005, blockPos, 0);
                  }
               } else {
                  this.successful = false;
               }

               return stack;
            } else {
               return super.dispenseStack(source, stack);
            }
         }

         @Override
         protected void playDispenseSound(IBlockSource source) {
            if (this.successful) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.tnt), new BehaviorDefaultDispenseItem() {
         @Override
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            EntityTNTPrimed entityTNTPrimed = new EntityTNTPrimed(world, blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, (EntityLivingBase)null);
            world.spawnEntityInWorld(entityTNTPrimed);
            world.playSoundAtEntity(entityTNTPrimed, "game.tnt.primed", 1.0F, 1.0F);
            stack.stackSize--;
            return stack;
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Items.skull, new BehaviorDefaultDispenseItem() {
         private boolean successful = true;

         @Override
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            EnumFacing facing = BlockDispenser.getFacing(source.getBlockMetadata());
            BlockPos blockPos = source.getBlockPos().offset(facing);
            BlockSkull blockSkull = Blocks.skull;
            if (!world.isAirBlock(blockPos) || !blockSkull.canDispenserPlace(world, blockPos, stack)) {
               this.successful = false;
            } else if (!world.isRemote) {
               world.setBlockState(blockPos, blockSkull.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP), 3);
               TileEntity tileEntity = world.getTileEntity(blockPos);
               if (tileEntity instanceof TileEntitySkull) {
                  if (stack.getMetadata() == 3) {
                     GameProfile gameProfile = null;
                     if (stack.hasTagCompound()) {
                        NBTTagCompound nBTTagCompound = stack.getTagCompound();
                        if (nBTTagCompound.hasKey("SkullOwner", 10)) {
                           gameProfile = NBTUtil.readGameProfileFromNBT(nBTTagCompound.getCompoundTag("SkullOwner"));
                        } else if (nBTTagCompound.hasKey("SkullOwner", 8)) {
                           String text = nBTTagCompound.getString("SkullOwner");
                           if (!StringUtils.isNullOrEmpty(text)) {
                              gameProfile = new GameProfile((UUID)null, text);
                           }
                        }
                     }

                     ((TileEntitySkull)tileEntity).setPlayerProfile(gameProfile);
                  } else {
                     ((TileEntitySkull)tileEntity).setType(stack.getMetadata());
                  }

                  ((TileEntitySkull)tileEntity).setSkullRotation(facing.getOpposite().getHorizontalIndex() * 4);
                  Blocks.skull.checkWitherSpawn(world, blockPos, (TileEntitySkull)tileEntity);
               }

               stack.stackSize--;
            }

            return stack;
         }

         @Override
         protected void playDispenseSound(IBlockSource source) {
            if (this.successful) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
      BlockDispenser.dispenseBehaviorRegistry.putObject(Item.getItemFromBlock(Blocks.pumpkin), new BehaviorDefaultDispenseItem() {
         private boolean successful = true;

         @Override
         protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            World world = source.getWorld();
            BlockPos blockPos = source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
            BlockPumpkin blockPumpkin = (BlockPumpkin)Blocks.pumpkin;
            if (world.isAirBlock(blockPos) && blockPumpkin.canDispenserPlace(world, blockPos)) {
               if (!world.isRemote) {
                  world.setBlockState(blockPos, blockPumpkin.getDefaultState(), 3);
               }

               stack.stackSize--;
            } else {
               this.successful = false;
            }

            return stack;
         }

         @Override
         protected void playDispenseSound(IBlockSource source) {
            if (this.successful) {
               source.getWorld().playAuxSFX(1000, source.getBlockPos(), 0);
            } else {
               source.getWorld().playAuxSFX(1001, source.getBlockPos(), 0);
            }
         }
      });
   }

   public static void register() {
      if (!alreadyRegistered) {
         alreadyRegistered = true;
         if (LOGGER.isDebugEnabled()) {
            redirectOutputToLog();
         }

         Block.registerBlocks();
         BlockFire.init();
         Item.registerItems();
         StatList.init();
         registerDispenserBehaviors();
      }
   }
}
