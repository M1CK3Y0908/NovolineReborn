package net.shadersmod.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.optifine.Config;
import net.optifine.MatchBlock;

public class BlockAlias {
    private int blockId;
    private MatchBlock[] matchBlocks;

    public BlockAlias(int blockId, MatchBlock[] matchBlocks) {
        this.blockId = blockId;
        this.matchBlocks = matchBlocks;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public boolean matches(int id, int metadata) {
        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchblock = this.matchBlocks[i];
            if (matchblock.matches(id, metadata)) {
                return true;
            }
        }

        return false;
    }

    public int[] getMatchBlockIds() {
        Set<Integer> set = new HashSet<>();

        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchblock = this.matchBlocks[i];
            int j = matchblock.getBlockId();
            set.add(j);
        }

        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }

    public MatchBlock[] getMatchBlocks(int matchBlockId) {
        List<MatchBlock> list = new ArrayList<>();

        for (int i = 0; i < this.matchBlocks.length; i++) {
            MatchBlock matchblock = this.matchBlocks[i];
            if (matchblock.getBlockId() == matchBlockId) {
                list.add(matchblock);
            }
        }

        return list.toArray(new MatchBlock[list.size()]);
    }

    @Override
    public String toString() {
        return "block." + this.blockId + "=" + Config.arrayToString(this.matchBlocks);
    }
}
