package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsProperties {
    public String name = null;
    public String basePath = null;
    public ResourceLocation[] resourceLocations = null;
    public RandomMobsRule[] rules = null;

    public RandomMobsProperties(String path, ResourceLocation[] variants) {
        ConnectedParser connectedparser = new ConnectedParser("RandomMobs");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.resourceLocations = variants;
    }

    public RandomMobsProperties(Properties props, String path, ResourceLocation baseResLoc) {
        ConnectedParser connectedparser = new ConnectedParser("RandomMobs");
        this.name = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.rules = this.parseRules(props, path, baseResLoc, connectedparser);
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc, EntityLiving el) {
        if (this.rules != null) {
            for (int i = 0; i < this.rules.length; i++) {
                RandomMobsRule randommobsrule = this.rules[i];
                if (randommobsrule.matches(el)) {
                    return randommobsrule.getTextureLocation(loc, el.randomMobsId);
                }
            }
        }

        if (this.resourceLocations != null) {
            int j = el.randomMobsId;
            int k = j % this.resourceLocations.length;
            return this.resourceLocations[k];
        } else {
            return loc;
        }
    }

    private RandomMobsRule[] parseRules(Properties props, String path, ResourceLocation baseResLoc, ConnectedParser cp) {
        List list = new ArrayList();
        int i = props.size();

        for (int j = 0; j < i; j++) {
            int k = j + 1;
            String s = props.getProperty("skins." + k);
            if (s != null) {
                int[] aint = cp.parseIntList(s);
                int[] aint1 = cp.parseIntList(props.getProperty("weights." + k));
                BiomeGenBase[] abiomegenbase = cp.parseBiomes(props.getProperty("biomes." + k));
                RangeListInt rangelistint = cp.parseRangeListInt(props.getProperty("heights." + k));
                if (rangelistint == null) {
                    rangelistint = this.parseMinMaxHeight(props, k);
                }

                NbtTagValue nbttagvalue = cp.parseNbtTagValue("name", props.getProperty("name." + k));
                VillagerProfession[] avillagerprofession = cp.parseProfessions(props.getProperty("professions." + k));
                RandomMobsRule randommobsrule = new RandomMobsRule(baseResLoc, k, aint, aint1, abiomegenbase, rangelistint, nbttagvalue, avillagerprofession);
                if (randommobsrule.isValid(path)) {
                    list.add(randommobsrule);
                }
            }
        }

        return ((RandomMobsRule[]) list.toArray(new RandomMobsRule[list.size()]));
    }

    private RangeListInt parseMinMaxHeight(Properties props, int index) {
        String s = props.getProperty("minHeight." + index);
        String s1 = props.getProperty("maxHeight." + index);
        if (s == null && s1 == null) {
            return null;
        }

        int i = 0;
        if (s != null) {
            i = Config.parseInt(s, -1);
            if (i < 0) {
                Config.warn("Invalid minHeight: " + s);
                return null;
            }
        }

        int j = 256;
        if (s1 != null) {
            j = Config.parseInt(s1, -1);
            if (j < 0) {
                Config.warn("Invalid maxHeight: " + s1);
                return null;
            }
        }

        if (j < 0) {
            Config.warn("Invalid minHeight, maxHeight: " + s + ", " + s1);
            return null;
        } else {
            RangeListInt rangelistint = new RangeListInt();
            rangelistint.addRange(new RangeInt(i, j));
            return rangelistint;
        }
    }

    public boolean isValid(String path) {
        if (this.resourceLocations == null && this.rules == null) {
            Config.warn("No skins specified: " + path);
            return false;
        }

        if (this.rules != null) {
            for (int i = 0; i < this.rules.length; i++) {
                RandomMobsRule randommobsrule = this.rules[i];
                if (!randommobsrule.isValid(path)) {
                    return false;
                }
            }
        }

        if (this.resourceLocations != null) {
            for (int j = 0; j < this.resourceLocations.length; j++) {
                ResourceLocation resourcelocation = this.resourceLocations[j];
                if (!Config.hasResource(resourcelocation)) {
                    Config.warn("Texture not found: " + resourcelocation.getResourcePath());
                    return false;
                }
            }
        }

        return true;
    }
}
