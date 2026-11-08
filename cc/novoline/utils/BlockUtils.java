package cc.novoline.utils;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class BlockUtils {

    private static final Minecraft mc = Minecraft.getInstance();

    private BlockUtils() {
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return getBlock(new BlockPos(x, y, z));
    }

    public static BlockPos below(double d) {
        return new BlockPos(mc.player.posX, mc.player.posY - d, mc.player.posZ);
    }

    public static boolean isReplaceable(BlockPos pos) {
        Block block = getBlock(pos);
        return block == Blocks.air || block == Blocks.fire || block instanceof BlockLiquid || block.getMaterial().isReplaceable();
    }

    public static boolean isContainer(Block block) {
        return block instanceof BlockChest || block instanceof BlockFurnace || block instanceof BlockEnderChest || block instanceof BlockDispenser || block instanceof BlockHopper || block instanceof BlockBrewingStand;
    }

    public static boolean isInteractable(Block block) {
        if (isContainer(block) || block == Blocks.bed || block == Blocks.lever || block == Blocks.crafting_table || block == Blocks.enchanting_table) {
            return true;
        }
        if (block instanceof BlockTrapDoor || block instanceof BlockFence || block instanceof BlockFenceGate) {
            return true;
        }
        if (block instanceof BlockDoor) {
            return block != Blocks.iron_door;
        }
        return false;
    }

    public static boolean samePos(BlockPos a, BlockPos b) {
        return a != null && b != null && a.getX() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    public static Vec3 hitVec(BlockPos pos, EnumFacing facing) {
        double x = (double) pos.getX() + 0.5;
        double y = (double) pos.getY() + 0.5;
        double z = (double) pos.getZ() + 0.5;
        if (facing != null) {
            x += (double) facing.getDirectionVec().getX() * 0.5;
            y += (double) facing.getDirectionVec().getY() * 0.5;
            z += (double) facing.getDirectionVec().getZ() * 0.5;
        }
        return new Vec3(x, y, z);
    }

    public static BlockPos placedPos(MovingObjectPosition hit) {
        if (hit == null || hit.getBlockPos() == null) return null;
        return hit.facing != null ? hit.getBlockPos().offset(hit.facing) : hit.getBlockPos();
    }

    public static boolean isScaffoldBlock(ItemStack stack) {
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemBlock)) {
            return false;
        }
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        if (block == null) return false;
        if (block instanceof BlockContainer || block instanceof BlockFalling || block instanceof BlockLeaves) {
            return false;
        }
        return block.isFullCube() || block.isFullBlock();
    }

    public static int countBlocks() {
        int count = 0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (isScaffoldBlock(stack)) {
                count += stack.stackSize;
            }
        }
        return count;
    }
}
