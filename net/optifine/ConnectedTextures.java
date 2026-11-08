package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedTextures {
    private static Map[] spriteQuadMaps = null;
    private static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
    private static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
    private static boolean multipass = false;
    private static final int Y_NEG_DOWN = 0;
    private static final int Y_POS_UP = 1;
    private static final int Z_NEG_NORTH = 2;
    private static final int Z_POS_SOUTH = 3;
    private static final int X_NEG_WEST = 4;
    private static final int X_POS_EAST = 5;
    private static final int Y_AXIS = 0;
    private static final int Z_AXIS = 1;
    private static final int X_AXIS = 2;
    private static final String[] propSuffixes = new String[]{
        "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };
    private static final int[] ctmIndexes = new int[]{
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        0,
        0,
        0,
        0,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        0,
        0,
        0,
        0,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        0,
        0,
        0,
        0,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        46,
        0,
        0,
        0,
        0,
        0
    };
    public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
    private static TextureAtlasSprite emptySprite = null;

    public static synchronized BakedQuad getConnectedTexture(
        IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv
    ) {
        TextureAtlasSprite textureatlassprite = quad.getSprite();
        if (textureatlassprite == null) {
            return quad;
        }

        Block block = blockState.getBlock();
        EnumFacing enumfacing = quad.getFace();
        if (block instanceof BlockPane && textureatlassprite.getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
            IBlockState iblockstate = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
            if (iblockstate == blockState) {
                return getQuad(emptySprite, block, blockState, quad);
            }
        }

        TextureAtlasSprite textureatlassprite1 = getConnectedTextureMultiPass(blockAccess, blockState, blockPos, enumfacing, textureatlassprite, renderEnv);
        return textureatlassprite1 == textureatlassprite ? quad : getQuad(textureatlassprite1, block, blockState, quad);
    }

    private static BakedQuad getQuad(TextureAtlasSprite sprite, Block block, IBlockState blockState, BakedQuad quadIn) {
        if (spriteQuadMaps == null) {
            return quadIn;
        }

        int i = sprite.getIndexInMap();
        if (i >= 0 && i < spriteQuadMaps.length) {
            Map map = spriteQuadMaps[i];
            if (map == null) {
                map = new IdentityHashMap(1);
                spriteQuadMaps[i] = map;
            }

            BakedQuad bakedquad = (BakedQuad)map.get(quadIn);
            if (bakedquad == null) {
                bakedquad = makeSpriteQuad(quadIn, sprite);
                map.put(quadIn, bakedquad);
            }

            return bakedquad;
        } else {
            return quadIn;
        }
    }

    private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite) {
        int[] aint = (int[])quad.getVertexData().clone();
        TextureAtlasSprite textureatlassprite = quad.getSprite();

        for (int i = 0; i < 4; i++) {
            fixVertex(aint, i, textureatlassprite, sprite);
        }

        return new BakedQuad(aint, quad.getTintIndex(), quad.getFace(), sprite);
    }

    private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo) {
        int i = data.length / 4;
        int j = i * vertex;
        float f = Float.intBitsToFloat(data[j + 4]);
        float f1 = Float.intBitsToFloat(data[j + 4 + 1]);
        double d0 = spriteFrom.getSpriteU16(f);
        double d1 = spriteFrom.getSpriteV16(f1);
        data[j + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(d0));
        data[j + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(d1));
    }

    private static TextureAtlasSprite getConnectedTextureMultiPass(
        IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, TextureAtlasSprite icon, RenderEnv renderEnv
    ) {
        TextureAtlasSprite textureatlassprite = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, icon, true, renderEnv);
        if (!multipass) {
            return textureatlassprite;
        }

        if (textureatlassprite == icon) {
            return textureatlassprite;
        }

        TextureAtlasSprite textureatlassprite1 = textureatlassprite;

        for (int i = 0; i < 3; i++) {
            TextureAtlasSprite textureatlassprite2 = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, textureatlassprite1, false, renderEnv);
            if (textureatlassprite2 == textureatlassprite1) {
                break;
            }

            textureatlassprite1 = textureatlassprite2;
        }

        return textureatlassprite1;
    }

    public static TextureAtlasSprite getConnectedTextureSingle(
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        EnumFacing facing,
        TextureAtlasSprite icon,
        boolean checkBlocks,
        RenderEnv renderEnv
    ) {
        Block block = blockState.getBlock();
        if (!(blockState instanceof BlockStateBase)) {
            return icon;
        }

        BlockStateBase blockstatebase = (BlockStateBase)blockState;
        if (tileProperties != null) {
            int i = icon.getIndexInMap();
            if (i >= 0 && i < tileProperties.length) {
                ConnectedProperties[] aconnectedproperties = tileProperties[i];
                if (aconnectedproperties != null) {
                    int j = getSide(facing);

                    for (int k = 0; k < aconnectedproperties.length; k++) {
                        ConnectedProperties connectedproperties = aconnectedproperties[k];
                        if (connectedproperties != null && connectedproperties.matchesBlockId(blockstatebase.getBlockId())) {
                            TextureAtlasSprite textureatlassprite = getConnectedTexture(
                                connectedproperties, blockAccess, blockstatebase, blockPos, j, icon, renderEnv
                            );
                            if (textureatlassprite != null) {
                                return textureatlassprite;
                            }
                        }
                    }
                }
            }
        }

        if (blockProperties != null && checkBlocks) {
            int l = renderEnv.getBlockId();
            if (l >= 0 && l < blockProperties.length) {
                ConnectedProperties[] aconnectedproperties1 = blockProperties[l];
                if (aconnectedproperties1 != null) {
                    int i1 = getSide(facing);

                    for (int j1 = 0; j1 < aconnectedproperties1.length; j1++) {
                        ConnectedProperties connectedproperties1 = aconnectedproperties1[j1];
                        if (connectedproperties1 != null && connectedproperties1.matchesIcon(icon)) {
                            TextureAtlasSprite textureatlassprite1 = getConnectedTexture(
                                connectedproperties1, blockAccess, blockstatebase, blockPos, i1, icon, renderEnv
                            );
                            if (textureatlassprite1 != null) {
                                return textureatlassprite1;
                            }
                        }
                    }
                }
            }
        }

        return icon;
    }

    public static int getSide(EnumFacing facing) {
        if (facing == null) {
            return -1;
        }

        switch (facing) {
            case DOWN:
                return 0;
            case UP:
                return 1;
            case EAST:
                return 5;
            case WEST:
                return 4;
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            default:
                return -1;
        }
    }

    private static EnumFacing getFacing(int side) {
        switch (side) {
            case 0:
                return EnumFacing.DOWN;
            case 1:
                return EnumFacing.UP;
            case 2:
                return EnumFacing.NORTH;
            case 3:
                return EnumFacing.SOUTH;
            case 4:
                return EnumFacing.WEST;
            case 5:
                return EnumFacing.EAST;
            default:
                return EnumFacing.UP;
        }
    }

    private static TextureAtlasSprite getConnectedTexture(
        ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, RenderEnv renderEnv
    ) {
        int i = 0;
        int j = blockState.getMetadata();
        int k = j;
        Block block = blockState.getBlock();
        if (block instanceof BlockRotatedPillar) {
            i = getWoodAxis(side, j);
            if (cp.getMetadataMax() <= 3) {
                k &= 3;
            }
        }

        if (block instanceof BlockQuartz) {
            i = getQuartzAxis(side, j);
            if (cp.getMetadataMax() <= 2 && k > 2) {
                k = 2;
            }
        }

        if (!cp.matchesBlock(blockState.getBlockId(), k)) {
            return null;
        }

        if (side >= 0 && cp.faces != 63) {
            int l = side;
            if (i != 0) {
                l = fixSideByAxis(side, i);
            }

            if ((1 << l & cp.faces) == 0) {
                return null;
            }
        }

        int i1 = blockPos.getY();
        if (cp.heights != null && !cp.heights.isInRange(i1)) {
            return null;
        }

        if (cp.biomes != null) {
            BiomeGenBase biomegenbase = blockAccess.getBiomeGenForCoords(blockPos);
            if (!cp.matchesBiome(biomegenbase)) {
                return null;
            }
        }

        switch (cp.method) {
            case 1:
                return getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, i, side, icon, j, renderEnv);
            case 2:
                return getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 3:
                return getConnectedTextureTop(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 4:
                return getConnectedTextureRandom(cp, blockAccess, blockState, blockPos, side);
            case 5:
                return getConnectedTextureRepeat(cp, blockPos, side);
            case 6:
                return getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 7:
                return getConnectedTextureFixed(cp);
            case 8:
                return getConnectedTextureHorizontalVertical(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            case 9:
                return getConnectedTextureVerticalHorizontal(cp, blockAccess, blockState, blockPos, i, side, icon, j);
            default:
                return null;
        }
    }

    private static int fixSideByAxis(int side, int vertAxis) {
        switch (vertAxis) {
            case 0:
                return side;
            case 1:
                switch (side) {
                    case 0:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 1;
                    case 3:
                        return 0;
                    default:
                        return side;
                }
            case 2:
                switch (side) {
                    case 0:
                        return 4;
                    case 1:
                        return 5;
                    case 2:
                    case 3:
                    default:
                        return side;
                    case 4:
                        return 1;
                    case 5:
                        return 0;
                }
            default:
                return side;
        }
    }

    private static int getWoodAxis(int side, int metadata) {
        int i = (metadata & 12) >> 2;
        switch (i) {
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    private static int getQuartzAxis(int side, int metadata) {
        switch (metadata) {
            case 3:
                return 2;
            case 4:
                return 1;
            default:
                return 0;
        }
    }

    private static TextureAtlasSprite getConnectedTextureRandom(
        ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side
    ) {
        if (cp.tileIcons.length == 1) {
            return cp.tileIcons[0];
        }

        int i = side / cp.symmetry * cp.symmetry;
        if (cp.linked) {
            BlockPos blockpos = blockPos.down();

            for (IBlockState iblockstate = blockAccess.getBlockState(blockpos);
                iblockstate.getBlock() == blockState.getBlock();
                iblockstate = blockAccess.getBlockState(blockpos)
            ) {
                blockPos = blockpos;
                blockpos = blockPos.down();
                if (blockpos.getY() < 0) {
                    break;
                }
            }
        }

        int l = Config.getRandom(blockPos, i) & Integer.MAX_VALUE;
        int i1 = 0;
        if (cp.weights == null) {
            i1 = l % cp.tileIcons.length;
        } else {
            int j = l % cp.sumAllWeights;
            int[] aint = cp.sumWeights;

            for (int k = 0; k < aint.length; k++) {
                if (j < aint[k]) {
                    i1 = k;
                    break;
                }
            }
        }

        return cp.tileIcons[i1];
    }

    private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp) {
        return cp.tileIcons[0];
    }

    private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp, BlockPos blockPos, int side) {
        if (cp.tileIcons.length == 1) {
            return cp.tileIcons[0];
        }

        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();
        int l = 0;
        int i1 = 0;
        switch (side) {
            case 0:
                l = i;
                i1 = k;
                break;
            case 1:
                l = i;
                i1 = k;
                break;
            case 2:
                l = -i - 1;
                i1 = -j;
                break;
            case 3:
                l = i;
                i1 = -j;
                break;
            case 4:
                l = k;
                i1 = -j;
                break;
            case 5:
                l = -k - 1;
                i1 = -j;
        }

        l %= cp.width;
        i1 %= cp.height;
        if (l < 0) {
            l += cp.width;
        }

        if (i1 < 0) {
            i1 += cp.height;
        }

        int j1 = i1 * cp.width + l;
        return cp.tileIcons[j1];
    }

    private static TextureAtlasSprite getConnectedTextureCtm(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata,
        RenderEnv renderEnv
    ) {
        boolean[] aboolean = renderEnv.getBorderFlags();
        switch (side) {
            case 0:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                break;
            case 1:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                break;
            case 2:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            case 3:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            case 4:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            case 5:
                aboolean[0] = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                aboolean[1] = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                aboolean[2] = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                aboolean[3] = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
        }

        int i = 0;
        if (aboolean[0] & !aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i = 3;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i = 1;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i = 12;
        } else if (!aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i = 36;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
            i = 2;
        } else if (!aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i = 24;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
            i = 15;
        } else if (aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
            i = 39;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i = 13;
        } else if (!aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i = 37;
        } else if (!aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i = 25;
        } else if (aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
            i = 27;
        } else if (aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
            i = 38;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
            i = 14;
        } else if (aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
            i = 26;
        }

        if (i == 0) {
            return cp.tileIcons[i];
        }

        if (!Config.isConnectedTexturesFancy()) {
            return cp.tileIcons[i];
        }

        boolean[] aboolean1 = aboolean;
        switch (side) {
            case 0:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
                break;
            case 1:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().south(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().south(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().north(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().north(), side, icon, metadata);
                break;
            case 2:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
                break;
            case 3:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().down(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().down(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.east().up(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.west().up(), side, icon, metadata);
                break;
            case 4:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
                break;
            case 5:
                aboolean1[0] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().north(), side, icon, metadata);
                aboolean1[1] = !isNeighbour(cp, blockAccess, blockState, blockPos.down().south(), side, icon, metadata);
                aboolean1[2] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().north(), side, icon, metadata);
                aboolean1[3] = !isNeighbour(cp, blockAccess, blockState, blockPos.up().south(), side, icon, metadata);
        }

        if (i == 13 && aboolean1[0]) {
            i = 4;
        } else if (i == 15 && aboolean1[1]) {
            i = 5;
        } else if (i == 37 && aboolean1[2]) {
            i = 16;
        } else if (i == 39 && aboolean1[3]) {
            i = 17;
        } else if (i == 14 && aboolean1[0] && aboolean1[1]) {
            i = 7;
        } else if (i == 25 && aboolean1[0] && aboolean1[2]) {
            i = 6;
        } else if (i == 27 && aboolean1[3] && aboolean1[1]) {
            i = 19;
        } else if (i == 38 && aboolean1[3] && aboolean1[2]) {
            i = 18;
        } else if (i == 14 && !aboolean1[0] && aboolean1[1]) {
            i = 31;
        } else if (i == 25 && aboolean1[0] && !aboolean1[2]) {
            i = 30;
        } else if (i == 27 && !aboolean1[3] && aboolean1[1]) {
            i = 41;
        } else if (i == 38 && aboolean1[3] && !aboolean1[2]) {
            i = 40;
        } else if (i == 14 && aboolean1[0] && !aboolean1[1]) {
            i = 29;
        } else if (i == 25 && !aboolean1[0] && aboolean1[2]) {
            i = 28;
        } else if (i == 27 && aboolean1[3] && !aboolean1[1]) {
            i = 43;
        } else if (i == 38 && !aboolean1[3] && aboolean1[2]) {
            i = 42;
        } else if (i == 26 && aboolean1[0] && aboolean1[1] && aboolean1[2] && aboolean1[3]) {
            i = 46;
        } else if (i == 26 && !aboolean1[0] && aboolean1[1] && aboolean1[2] && aboolean1[3]) {
            i = 9;
        } else if (i == 26 && aboolean1[0] && !aboolean1[1] && aboolean1[2] && aboolean1[3]) {
            i = 21;
        } else if (i == 26 && aboolean1[0] && aboolean1[1] && !aboolean1[2] && aboolean1[3]) {
            i = 8;
        } else if (i == 26 && aboolean1[0] && aboolean1[1] && aboolean1[2] && !aboolean1[3]) {
            i = 20;
        } else if (i == 26 && aboolean1[0] && aboolean1[1] && !aboolean1[2] && !aboolean1[3]) {
            i = 11;
        } else if (i == 26 && !aboolean1[0] && !aboolean1[1] && aboolean1[2] && aboolean1[3]) {
            i = 22;
        } else if (i == 26 && !aboolean1[0] && aboolean1[1] && !aboolean1[2] && aboolean1[3]) {
            i = 23;
        } else if (i == 26 && aboolean1[0] && !aboolean1[1] && aboolean1[2] && !aboolean1[3]) {
            i = 10;
        } else if (i == 26 && aboolean1[0] && !aboolean1[1] && !aboolean1[2] && aboolean1[3]) {
            i = 34;
        } else if (i == 26 && !aboolean1[0] && aboolean1[1] && aboolean1[2] && !aboolean1[3]) {
            i = 35;
        } else if (i == 26 && aboolean1[0] && !aboolean1[1] && !aboolean1[2] && !aboolean1[3]) {
            i = 32;
        } else if (i == 26 && !aboolean1[0] && aboolean1[1] && !aboolean1[2] && !aboolean1[3]) {
            i = 33;
        } else if (i == 26 && !aboolean1[0] && !aboolean1[1] && aboolean1[2] && !aboolean1[3]) {
            i = 44;
        } else if (i == 26 && !aboolean1[0] && !aboolean1[1] && !aboolean1[2] && aboolean1[3]) {
            i = 45;
        }

        return cp.tileIcons[i];
    }

    private static boolean isNeighbour(
        ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata
    ) {
        IBlockState iblockstate = iblockaccess.getBlockState(blockPos);
        if (blockState == iblockstate) {
            return true;
        }

        if (cp.connect == 2) {
            if (iblockstate == null) {
                return false;
            }

            if (iblockstate == AIR_DEFAULT_STATE) {
                return false;
            }

            TextureAtlasSprite textureatlassprite = getNeighbourIcon(iblockaccess, blockPos, iblockstate, side);
            return textureatlassprite == icon;
        } else if (cp.connect == 3) {
            if (iblockstate == null) {
                return false;
            } else {
                return iblockstate == AIR_DEFAULT_STATE ? false : iblockstate.getBlock().getMaterial() == blockState.getBlock().getMaterial();
            }
        } else {
            return false;
        }
    }

    private static TextureAtlasSprite getNeighbourIcon(IBlockAccess iblockaccess, BlockPos blockPos, IBlockState neighbourState, int side) {
        neighbourState = neighbourState.getBlock().getActualState(neighbourState, iblockaccess, blockPos);
        IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(neighbourState);
        if (ibakedmodel == null) {
            return null;
        }

        if (Reflector.ForgeBlock_getExtendedState.exists()) {
            neighbourState = (IBlockState)Reflector.call(
                neighbourState.getBlock(), Reflector.ForgeBlock_getExtendedState, neighbourState, iblockaccess, blockPos
            );
        }

        EnumFacing enumfacing = getFacing(side);
        List list = ibakedmodel.getFaceQuads(enumfacing);
        if (list == null) {
            return null;
        }

        if (Config.isBetterGrass()) {
            list = BetterGrass.getFaceQuads(iblockaccess, neighbourState, blockPos, enumfacing, list);
        }

        if (list.size() > 0) {
            BakedQuad bakedquad1 = (BakedQuad)list.get(0);
            return bakedquad1.getSprite();
        }

        List list1 = ibakedmodel.getGeneralQuads();
        if (list1 == null) {
            return null;
        }

        for (int i = 0; i < list1.size(); i++) {
            BakedQuad bakedquad = (BakedQuad)list1.get(i);
            if (bakedquad.getFace() == enumfacing) {
                return bakedquad.getSprite();
            }
        }

        return null;
    }

    private static TextureAtlasSprite getConnectedTextureHorizontal(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata
    ) {
        boolean flag;
        boolean flag1;
        flag = false;
        flag1 = false;
        label43:
        switch (vertAxis) {
            case 0:
                switch (side) {
                    case 0:
                    case 1:
                        return null;
                    case 2:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        break label43;
                    case 3:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label43;
                    case 4:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break label43;
                    case 5:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                    default:
                        break label43;
                }
            case 1:
                switch (side) {
                    case 0:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label43;
                    case 1:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
                        break label43;
                    case 2:
                    case 3:
                        return null;
                    case 4:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break label43;
                    case 5:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                    default:
                        break label43;
                }
            case 2:
                switch (side) {
                    case 0:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break;
                    case 1:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                        break;
                    case 2:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        break;
                    case 3:
                        flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                        flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                        break;
                    case 4:
                    case 5:
                        return null;
                }
        }

        int i = 3;
        byte b0;
        if (flag) {
            if (flag1) {
                b0 = 1;
            } else {
                b0 = 2;
            }
        } else if (flag1) {
            b0 = 0;
        } else {
            b0 = 3;
        }

        return cp.tileIcons[b0];
    }

    private static TextureAtlasSprite getConnectedTextureVertical(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata
    ) {
        boolean flag = false;
        boolean flag1 = false;
        switch (vertAxis) {
            case 0:
                if (side == 1 || side == 0) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.down(), side, icon, metadata);
                flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            case 1:
                if (side == 3 || side == 2) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.north(), side, icon, metadata);
                break;
            case 2:
                if (side == 5 || side == 4) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.west(), side, icon, metadata);
                flag1 = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
        }

        int i = 3;
        byte b0;
        if (flag) {
            if (flag1) {
                b0 = 1;
            } else {
                b0 = 2;
            }
        } else if (flag1) {
            b0 = 0;
        } else {
            b0 = 3;
        }

        return cp.tileIcons[b0];
    }

    private static TextureAtlasSprite getConnectedTextureHorizontalVertical(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata
    ) {
        TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
        TextureAtlasSprite textureatlassprite = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        } else {
            TextureAtlasSprite textureatlassprite1 = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
            if (textureatlassprite1 == atextureatlassprite[0]) {
                return atextureatlassprite[4];
            } else if (textureatlassprite1 == atextureatlassprite[1]) {
                return atextureatlassprite[5];
            } else {
                return textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1;
            }
        }
    }

    private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata
    ) {
        TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
        TextureAtlasSprite textureatlassprite = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
        if (textureatlassprite != null && textureatlassprite != icon && textureatlassprite != atextureatlassprite[3]) {
            return textureatlassprite;
        } else {
            TextureAtlasSprite textureatlassprite1 = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
            if (textureatlassprite1 == atextureatlassprite[0]) {
                return atextureatlassprite[4];
            } else if (textureatlassprite1 == atextureatlassprite[1]) {
                return atextureatlassprite[5];
            } else {
                return textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6] : textureatlassprite1;
            }
        }
    }

    private static TextureAtlasSprite getConnectedTextureTop(
        ConnectedProperties cp,
        IBlockAccess blockAccess,
        IBlockState blockState,
        BlockPos blockPos,
        int vertAxis,
        int side,
        TextureAtlasSprite icon,
        int metadata
    ) {
        boolean flag = false;
        switch (vertAxis) {
            case 0:
                if (side == 1 || side == 0) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.up(), side, icon, metadata);
                break;
            case 1:
                if (side == 3 || side == 2) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.south(), side, icon, metadata);
                break;
            case 2:
                if (side == 5 || side == 4) {
                    return null;
                }

                flag = isNeighbour(cp, blockAccess, blockState, blockPos.east(), side, icon, metadata);
        }

        return flag ? cp.tileIcons[0] : null;
    }

    public static void updateIcons(TextureMap textureMap) {
        blockProperties = (ConnectedProperties[][])null;
        tileProperties = (ConnectedProperties[][])null;
        spriteQuadMaps = null;
        if (Config.isConnectedTextures()) {
            IResourcePack[] airesourcepack = Config.getResourcePacks();

            for (int i = airesourcepack.length - 1; i >= 0; i--) {
                IResourcePack iresourcepack = airesourcepack[i];
                updateIcons(textureMap, iresourcepack);
            }

            updateIcons(textureMap, Config.getDefaultResourcePack());
            ResourceLocation resourcelocation = new ResourceLocation("mcpatcher/ctm/default/empty");
            emptySprite = textureMap.registerSprite(resourcelocation);
            spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
            if (blockProperties.length <= 0) {
                blockProperties = (ConnectedProperties[][])null;
            }

            if (tileProperties.length <= 0) {
                tileProperties = (ConnectedProperties[][])null;
            }
        }
    }

    private static void updateIconEmpty(TextureMap textureMap) {
    }

    public static void updateIcons(TextureMap textureMap, IResourcePack rp) {
        String[] astring = ResUtils.collectFiles(rp, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
        Arrays.sort(astring);
        List list = makePropertyList(tileProperties);
        List list1 = makePropertyList(blockProperties);

        for (int i = 0; i < astring.length; i++) {
            String s = astring[i];
            Config.dbg("ConnectedTextures: " + s);

            try {
                ResourceLocation resourcelocation = new ResourceLocation(s);
                InputStream inputstream = rp.getInputStream(resourcelocation);
                if (inputstream == null) {
                    Config.warn("ConnectedTextures file not found: " + s);
                } else {
                    Properties properties = new Properties();
                    properties.load(inputstream);
                    ConnectedProperties connectedproperties = new ConnectedProperties(properties, s);
                    if (connectedproperties.isValid(s)) {
                        connectedproperties.updateIcons(textureMap);
                        addToTileList(connectedproperties, list);
                        addToBlockList(connectedproperties, list1);
                    }
                }
            } catch (FileNotFoundException filenotfoundexception) {
                Config.warn("ConnectedTextures file not found: " + s);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        blockProperties = propertyListToArray(list1);
        tileProperties = propertyListToArray(list);
        multipass = detectMultipass();
        Config.dbg("Multipass connected textures: " + multipass);
    }

    private static List makePropertyList(ConnectedProperties[][] propsArr) {
        List list = new ArrayList();
        if (propsArr != null) {
            for (int i = 0; i < propsArr.length; i++) {
                ConnectedProperties[] aconnectedproperties = propsArr[i];
                List list1 = null;
                if (aconnectedproperties != null) {
                    list1 = new ArrayList<>(Arrays.asList(aconnectedproperties));
                }

                list.add(list1);
            }
        }

        return list;
    }

    private static boolean detectMultipass() {
        List list = new ArrayList();

        for (int i = 0; i < tileProperties.length; i++) {
            ConnectedProperties[] aconnectedproperties = tileProperties[i];
            if (aconnectedproperties != null) {
                list.addAll(Arrays.asList(aconnectedproperties));
            }
        }

        for (int k = 0; k < blockProperties.length; k++) {
            ConnectedProperties[] aconnectedproperties2 = blockProperties[k];
            if (aconnectedproperties2 != null) {
                list.addAll(Arrays.asList(aconnectedproperties2));
            }
        }

        ConnectedProperties[] aconnectedproperties1 = ((ConnectedProperties[]) list.toArray(new ConnectedProperties[list.size()]));
        Set set1 = new HashSet();
        Set set = new HashSet();

        for (int j = 0; j < aconnectedproperties1.length; j++) {
            ConnectedProperties connectedproperties = aconnectedproperties1[j];
            if (connectedproperties.matchTileIcons != null) {
                set1.addAll(Arrays.asList(connectedproperties.matchTileIcons));
            }

            if (connectedproperties.tileIcons != null) {
                set.addAll(Arrays.asList(connectedproperties.tileIcons));
            }
        }

        set1.retainAll(set);
        return !set1.isEmpty();
    }

    private static ConnectedProperties[][] propertyListToArray(List list) {
        ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            List listx = (List)list.get(i);
            if (listx != null) {
                ConnectedProperties[] aconnectedproperties1 = ((ConnectedProperties[]) listx.toArray(new ConnectedProperties[listx.size()]));
                aconnectedproperties[i] = aconnectedproperties1;
            }
        }

        return aconnectedproperties;
    }

    private static void addToTileList(ConnectedProperties cp, List tileList) {
        if (cp.matchTileIcons != null) {
            for (int i = 0; i < cp.matchTileIcons.length; i++) {
                TextureAtlasSprite textureatlassprite = cp.matchTileIcons[i];
                if (!(textureatlassprite instanceof TextureAtlasSprite)) {
                    Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureatlassprite + ", name: " + textureatlassprite.getIconName());
                } else {
                    TextureAtlasSprite textureatlassprite1 = textureatlassprite;
                    int j = textureatlassprite1.getIndexInMap();
                    if (j < 0) {
                        Config.warn("Invalid tile ID: " + j + ", icon: " + textureatlassprite1.getIconName());
                    } else {
                        addToList(cp, tileList, j);
                    }
                }
            }
        }
    }

    private static void addToBlockList(ConnectedProperties cp, List blockList) {
        if (cp.matchBlocks != null) {
            for (int i = 0; i < cp.matchBlocks.length; i++) {
                int j = cp.matchBlocks[i].getBlockId();
                if (j < 0) {
                    Config.warn("Invalid block ID: " + j);
                } else {
                    addToList(cp, blockList, j);
                }
            }
        }
    }

    private static void addToList(ConnectedProperties cp, List list, int id) {
        while (id >= list.size()) {
            list.add(null);
        }

        List listx = (List)list.get(id);
        if (listx == null) {
            listx = new ArrayList();
            list.set(id, listx);
        }

        listx.add(cp);
    }

    private static String[] getDefaultCtmPaths() {
        List list = new ArrayList();
        String s = "mcpatcher/ctm/default/";
        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
            list.add(s + "glass.properties");
            list.add(s + "glasspane.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
            list.add(s + "bookshelf.properties");
        }

        if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
            list.add(s + "sandstone.properties");
        }

        String[] astring = new String[]{
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
        };

        for (int i = 0; i < astring.length; i++) {
            String s1 = astring[i];
            if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + s1 + ".png"))) {
                list.add(s + i + "_glass_" + s1 + "/glass_" + s1 + ".properties");
                list.add(s + i + "_glass_" + s1 + "/glass_pane_" + s1 + ".properties");
            }
        }

        return ((String[]) list.toArray(new String[list.size()]));
    }

    public static int getPaneTextureIndex(boolean linkP, boolean linkN, boolean linkYp, boolean linkYn) {
        if (linkN && linkP) {
            if (linkYp) {
                return linkYn ? 34 : 50;
            } else {
                return linkYn ? 18 : 2;
            }
        } else if (linkN && !linkP) {
            if (linkYp) {
                return linkYn ? 35 : 51;
            } else {
                return linkYn ? 19 : 3;
            }
        } else if (!linkN && linkP) {
            if (linkYp) {
                return linkYn ? 33 : 49;
            } else {
                return linkYn ? 17 : 1;
            }
        } else if (linkYp) {
            return linkYn ? 32 : 48;
        } else {
            return linkYn ? 16 : 0;
        }
    }

    public static int getReversePaneTextureIndex(int texNum) {
        int i = texNum % 16;
        if (i == 1) {
            return texNum + 2;
        } else {
            return i == 3 ? texNum - 2 : texNum;
        }
    }

    public static TextureAtlasSprite getCtmTexture(ConnectedProperties cp, int ctmIndex, TextureAtlasSprite icon) {
        if (cp.method != 1) {
            return icon;
        } else if (ctmIndex >= 0 && ctmIndex < ctmIndexes.length) {
            int i = ctmIndexes[ctmIndex];
            TextureAtlasSprite[] atextureatlassprite = cp.tileIcons;
            return i >= 0 && i < atextureatlassprite.length ? atextureatlassprite[i] : icon;
        } else {
            return icon;
        }
    }
}
