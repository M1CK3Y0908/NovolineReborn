package net.optifine;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

public class RandomMobsRule {
    private ResourceLocation baseResLoc = null;
    private int index;
    private int[] skins = null;
    private ResourceLocation[] resourceLocations = null;
    private int[] weights = null;
    private BiomeGenBase[] biomes = null;
    private RangeListInt heights = null;
    private NbtTagValue nbtName = null;
    public int[] sumWeights = null;
    public int sumAllWeights = 1;
    private VillagerProfession[] professions = null;

    public RandomMobsRule(
        ResourceLocation baseResLoc,
        int index,
        int[] skins,
        int[] weights,
        BiomeGenBase[] biomes,
        RangeListInt heights,
        NbtTagValue nbtName,
        VillagerProfession[] professions
    ) {
        this.baseResLoc = baseResLoc;
        this.index = index;
        this.skins = skins;
        this.weights = weights;
        this.biomes = biomes;
        this.heights = heights;
        this.nbtName = nbtName;
        this.professions = professions;
    }

    public boolean isValid(String path) {
        if (this.skins == null || this.skins.length == 0) {
            Config.warn("Invalid skins for rule: " + this.index);
            return false;
        }

        if (this.resourceLocations != null) {
            return true;
        }

        this.resourceLocations = new ResourceLocation[this.skins.length];
        ResourceLocation resourcelocation = RandomMobs.getMcpatcherLocation(this.baseResLoc);
        if (resourcelocation == null) {
            Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
            return false;
        }

        for (int i = 0; i < this.resourceLocations.length; i++) {
            int j = this.skins[i];
            if (j <= 1) {
                this.resourceLocations[i] = this.baseResLoc;
            } else {
                ResourceLocation resourcelocation1 = RandomMobs.getLocationIndexed(resourcelocation, j);
                if (resourcelocation1 == null) {
                    Config.warn("Invalid path: " + this.baseResLoc.getResourcePath());
                    return false;
                }

                if (!Config.hasResource(resourcelocation1)) {
                    Config.warn("Texture not found: " + resourcelocation1.getResourcePath());
                    return false;
                }

                this.resourceLocations[i] = resourcelocation1;
            }
        }

        if (this.weights != null) {
            if (this.weights.length > this.resourceLocations.length) {
                Config.warn("More weights defined than skins, trimming weights: " + path);
                int[] aint = new int[this.resourceLocations.length];
                System.arraycopy(this.weights, 0, aint, 0, aint.length);
                this.weights = aint;
            }

            if (this.weights.length < this.resourceLocations.length) {
                Config.warn("Less weights defined than skins, expanding weights: " + path);
                int[] aint1 = new int[this.resourceLocations.length];
                System.arraycopy(this.weights, 0, aint1, 0, this.weights.length);
                int l = MathUtils.getAverage(this.weights);

                for (int j1 = this.weights.length; j1 < aint1.length; j1++) {
                    aint1[j1] = l;
                }

                this.weights = aint1;
            }

            this.sumWeights = new int[this.weights.length];
            int k = 0;

            for (int i1 = 0; i1 < this.weights.length; i1++) {
                if (this.weights[i1] < 0) {
                    Config.warn("Invalid weight: " + this.weights[i1]);
                    return false;
                }

                k += this.weights[i1];
                this.sumWeights[i1] = k;
            }

            this.sumAllWeights = k;
            if (this.sumAllWeights <= 0) {
                Config.warn("Invalid sum of all weights: " + k);
                this.sumAllWeights = 1;
            }
        }

        if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
            Config.warn("Invalid professions or careers: " + path);
            return false;
        } else {
            return true;
        }
    }

    public boolean matches(EntityLiving el) {
        if (!Matches.biome(el.spawnBiome, this.biomes)) {
            return false;
        }

        if (this.heights != null && el.spawnPosition != null) {
            return this.heights.isInRange(el.spawnPosition.getY());
        }

        if (this.nbtName != null) {
            String s = el.hasCustomName() ? el.getCustomNameTag() : null;
            if (!this.nbtName.matchesValue(s)) {
                return false;
            }
        }

        if (this.professions != null && el instanceof EntityVillager) {
            EntityVillager entityvillager = (EntityVillager)el;
            int i = entityvillager.getProfession();
            int j = Reflector.getFieldValueInt(entityvillager, Reflector.EntityVillager_careerId, -1);
            if (i < 0 || j < 0) {
                return false;
            }

            boolean flag = false;

            for (int k = 0; k < this.professions.length; k++) {
                VillagerProfession villagerprofession = this.professions[k];
                if (villagerprofession.matches(i, j)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        return true;
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc, int randomId) {
        if (this.resourceLocations != null && this.resourceLocations.length != 0) {
            int i = 0;
            if (this.weights == null) {
                i = randomId % this.resourceLocations.length;
            } else {
                int j = randomId % this.sumAllWeights;

                for (int k = 0; k < this.sumWeights.length; k++) {
                    if (this.sumWeights[k] > j) {
                        i = k;
                        break;
                    }
                }
            }

            return this.resourceLocations[i];
        } else {
            return loc;
        }
    }
}
