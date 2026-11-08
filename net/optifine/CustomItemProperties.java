package net.optifine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockPart;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ModelBlock;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomItemProperties {
    public String name = null;
    public String basePath = null;
    public int type = 1;
    public int[] items = null;
    public String texture = null;
    public Map<String, String> mapTextures = null;
    public RangeListInt damage = null;
    public boolean damagePercent = false;
    public int damageMask = 0;
    public RangeListInt stackSize = null;
    public RangeListInt enchantmentIds = null;
    public RangeListInt enchantmentLevels = null;
    public NbtTagValue[] nbtTagValues = null;
    public int blend = 1;
    public float speed = 0.0F;
    public float rotation = 0.0F;
    public int layer = 0;
    public float duration = 1.0F;
    public int weight = 0;
    public ResourceLocation textureLocation = null;
    public Map mapTextureLocations = null;
    public TextureAtlasSprite sprite = null;
    public Map mapSprites = null;
    public IBakedModel model = null;
    public Map<String, IBakedModel> mapModels = null;
    private int textureWidth = 0;
    private int textureHeight = 0;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_ENCHANTMENT = 2;
    public static final int TYPE_ARMOR = 3;

    public CustomItemProperties(Properties props, String path) {
        this.name = parseName(path);
        this.basePath = parseBasePath(path);
        this.type = this.parseType(props.getProperty("type"));
        this.items = this.parseItems(props.getProperty("items"), props.getProperty("matchItems"));
        this.mapTextures = parseTextures(props, this.basePath);
        this.texture = parseTexture(
            props.getProperty("texture"), props.getProperty("tile"), props.getProperty("source"), path, this.basePath, this.type, this.mapTextures
        );
        String s = props.getProperty("damage");
        if (s != null) {
            this.damagePercent = s.contains("%");
            s = s.replace("%", "");
            this.damage = this.parseRangeListInt(s);
            this.damageMask = this.parseInt(props.getProperty("damageMask"), 0);
        }

        this.stackSize = this.parseRangeListInt(props.getProperty("stackSize"));
        this.enchantmentIds = this.parseRangeListInt(props.getProperty("enchantmentIDs"));
        this.enchantmentLevels = this.parseRangeListInt(props.getProperty("enchantmentLevels"));
        this.nbtTagValues = this.parseNbtTagValues(props);
        this.blend = Blender.parseBlend(props.getProperty("blend"));
        this.speed = this.parseFloat(props.getProperty("speed"), 0.0F);
        this.rotation = this.parseFloat(props.getProperty("rotation"), 0.0F);
        this.layer = this.parseInt(props.getProperty("layer"), 0);
        this.weight = this.parseInt(props.getProperty("weight"), 0);
        this.duration = this.parseFloat(props.getProperty("duration"), 1.0F);
    }

    private static String parseName(String path) {
        String s = path;
        int i = s.lastIndexOf(47);
        if (i >= 0) {
            s = s.substring(i + 1);
        }

        int j = s.lastIndexOf(46);
        if (j >= 0) {
            s = s.substring(0, j);
        }

        return s;
    }

    private static String parseBasePath(String path) {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    private int parseType(String str) {
        if (str == null) {
            return 1;
        }

        if (str.equals("item")) {
            return 1;
        }

        if (str.equals("enchantment")) {
            return 2;
        }

        if (str.equals("armor")) {
            return 3;
        }

        Config.warn("Unknown method: " + str);
        return 0;
    }

    private int[] parseItems(String str, String str2) {
        if (str == null) {
            str = str2;
        }

        if (str == null) {
            return null;
        }

        str = str.trim();
        Set set = new TreeSet();
        String[] astring = Config.tokenize(str, " ");

        for (int i = 0; i < astring.length; i++) {
            String s = astring[i];
            int j = Config.parseInt(s, -1);
            if (j >= 0) {
                set.add(new Integer(j));
            } else {
                if (s.contains("-")) {
                    String[] astring1 = Config.tokenize(s, "-");
                    if (astring1.length == 2) {
                        int k = Config.parseInt(astring1[0], -1);
                        int l = Config.parseInt(astring1[1], -1);
                        if (k >= 0 && l >= 0) {
                            int i1 = Math.min(k, l);
                            int j1 = Math.max(k, l);

                            for (int k1 = i1; k1 <= j1; k1++) {
                                set.add(new Integer(k1));
                            }
                            continue;
                        }
                    }
                }

                Item item = Item.getByNameOrId(s);
                if (item == null) {
                    Config.warn("Item not found: " + s);
                } else {
                    int i2 = Item.getIdFromItem(item);
                    if (i2 <= 0) {
                        Config.warn("Item not found: " + s);
                    } else {
                        set.add(new Integer(i2));
                    }
                }
            }
        }

        Integer[] ainteger = ((Integer[]) set.toArray(new Integer[set.size()]));
        int[] aint = new int[ainteger.length];

        for (int l1 = 0; l1 < aint.length; l1++) {
            aint[l1] = ainteger[l1];
        }

        return aint;
    }

    private static String parseTexture(String texStr, String texStr2, String texStr3, String path, String basePath, int type, Map<String, String> mapTexs) {
        if (texStr == null) {
            texStr = texStr2;
        }

        if (texStr == null) {
            texStr = texStr3;
        }

        if (texStr != null) {
            String s2 = ".png";
            if (texStr.endsWith(s2)) {
                texStr = texStr.substring(0, texStr.length() - s2.length());
            }

            return fixTextureName(texStr, basePath);
        } else {
            if (type == 3) {
                return null;
            }

            if (mapTexs != null) {
                String s = mapTexs.get("texture.bow_standby");
                if (s != null) {
                    return s;
                }
            }

            String s1 = path;
            int i = s1.lastIndexOf(47);
            if (i >= 0) {
                s1 = s1.substring(i + 1);
            }

            int j = s1.lastIndexOf(46);
            if (j >= 0) {
                s1 = s1.substring(0, j);
            }

            return fixTextureName(s1, basePath);
        }
    }

    private static Map parseTextures(Properties props, String basePath) {
        String s = "texture.";
        Map map = getMatchingProperties(props, s);
        if (map.size() <= 0) {
            return null;
        }

        Set set = map.keySet();
        Map map1 = new LinkedHashMap();

        for (String s1 : (Iterable<String>) (Iterable) set) {
            String s2 = (String)map.get(s1);
            s2 = fixTextureName(s2, basePath);
            map1.put(s1, s2);
        }

        return map1;
    }

    private static String fixTextureName(String iconName, String basePath) {
        iconName = TextureUtils.fixResourcePath(iconName, basePath);
        if (!iconName.startsWith(basePath) && !iconName.startsWith("textures/") && !iconName.startsWith("mcpatcher/")) {
            iconName = basePath + "/" + iconName;
        }

        if (iconName.endsWith(".png")) {
            iconName = iconName.substring(0, iconName.length() - 4);
        }

        if (iconName.startsWith("/")) {
            iconName = iconName.substring(1);
        }

        return iconName;
    }

    private int parseInt(String str, int defVal) {
        if (str == null) {
            return defVal;
        } else {
            str = str.trim();
            int i = Config.parseInt(str, Integer.MIN_VALUE);
            if (i == Integer.MIN_VALUE) {
                Config.warn("Invalid integer: " + str);
                return defVal;
            } else {
                return i;
            }
        }
    }

    private float parseFloat(String str, float defVal) {
        if (str == null) {
            return defVal;
        } else {
            str = str.trim();
            float f = Config.parseFloat(str, Float.MIN_VALUE);
            if (f == Float.MIN_VALUE) {
                Config.warn("Invalid float: " + str);
                return defVal;
            } else {
                return f;
            }
        }
    }

    private RangeListInt parseRangeListInt(String str) {
        if (str == null) {
            return null;
        }

        String[] astring = Config.tokenize(str, " ");
        RangeListInt rangelistint = new RangeListInt();

        for (int i = 0; i < astring.length; i++) {
            String s = astring[i];
            RangeInt rangeint = this.parseRangeInt(s);
            if (rangeint == null) {
                Config.warn("Invalid range list: " + str);
                return null;
            }

            rangelistint.addRange(rangeint);
        }

        return rangelistint;
    }

    private RangeInt parseRangeInt(String str) {
        if (str == null) {
            return null;
        }

        str = str.trim();
        int i = str.length() - str.replace("-", "").length();
        if (i > 1) {
            Config.warn("Invalid range: " + str);
            return null;
        }

        String[] astring = Config.tokenize(str, "- ");
        int[] aint = new int[astring.length];

        for (int j = 0; j < astring.length; j++) {
            String s = astring[j];
            int k = Config.parseInt(s, -1);
            if (k < 0) {
                Config.warn("Invalid range: " + str);
                return null;
            }

            aint[j] = k;
        }

        if (aint.length == 1) {
            int i1 = aint[0];
            if (str.startsWith("-")) {
                return new RangeInt(0, i1);
            } else {
                return str.endsWith("-") ? new RangeInt(i1, 65535) : new RangeInt(i1, i1);
            }
        } else if (aint.length == 2) {
            int l = Math.min(aint[0], aint[1]);
            int j1 = Math.max(aint[0], aint[1]);
            return new RangeInt(l, j1);
        } else {
            Config.warn("Invalid range: " + str);
            return null;
        }
    }

    private NbtTagValue[] parseNbtTagValues(Properties props) {
        String s = "nbt.";
        Map map = getMatchingProperties(props, s);
        if (map.size() <= 0) {
            return null;
        }

        List list = new ArrayList();

        for (String s1 : (Iterable<String>) (Iterable) map.keySet()) {
            String s2 = (String)map.get(s1);
            String s3 = s1.substring(s.length());
            NbtTagValue nbttagvalue = new NbtTagValue(s3, s2);
            list.add(nbttagvalue);
        }

        NbtTagValue[] anbttagvalue = ((NbtTagValue[]) list.toArray(new NbtTagValue[list.size()]));
        return anbttagvalue;
    }

    private static Map getMatchingProperties(Properties props, String keyPrefix) {
        Map map = new LinkedHashMap();

        for (String s : (Iterable<String>) (Iterable) props.keySet()) {
            String s1 = props.getProperty(s);
            if (s.startsWith(keyPrefix)) {
                map.put(s, s1);
            }
        }

        return map;
    }

    public boolean isValid(String path) {
        if (this.name == null || this.name.length() <= 0) {
            Config.warn("No name found: " + path);
            return false;
        }

        if (this.basePath == null) {
            Config.warn("No base path found: " + path);
            return false;
        }

        if (this.type == 0) {
            Config.warn("No type defined: " + path);
            return false;
        }

        if (this.type == 1 || this.type == 3) {
            if (this.items == null) {
                this.items = this.detectItems();
            }

            if (this.items == null) {
                Config.warn("No items defined: " + path);
                return false;
            }
        }

        if (this.texture == null && this.mapTextures == null) {
            Config.warn("No texture specified: " + path);
            return false;
        } else if (this.type == 2 && this.enchantmentIds == null) {
            Config.warn("No enchantmentIDs specified: " + path);
            return false;
        } else {
            return true;
        }
    }

    private int[] detectItems() {
        Item item = Item.getByNameOrId(this.name);
        if (item == null) {
            return null;
        }

        int i = Item.getIdFromItem(item);
        return i <= 0 ? null : new int[]{i};
    }

    public void updateIcons(TextureMap textureMap) {
        if (this.texture != null) {
            this.textureLocation = this.getTextureLocation(this.texture);
            if (this.type == 1) {
                ResourceLocation resourcelocation = this.getSpriteLocation(this.textureLocation);
                this.sprite = textureMap.registerSprite(resourcelocation);
            }
        }

        if (this.mapTextures != null) {
            this.mapTextureLocations = new HashMap();
            this.mapSprites = new HashMap();

            for (String s : this.mapTextures.keySet()) {
                String s1 = this.mapTextures.get(s);
                ResourceLocation resourcelocation1 = this.getTextureLocation(s1);
                this.mapTextureLocations.put(s, resourcelocation1);
                if (this.type == 1) {
                    ResourceLocation resourcelocation2 = this.getSpriteLocation(resourcelocation1);
                    TextureAtlasSprite textureatlassprite = textureMap.registerSprite(resourcelocation2);
                    this.mapSprites.put(s, textureatlassprite);
                }
            }
        }
    }

    private ResourceLocation getTextureLocation(String texName) {
        if (texName == null) {
            return null;
        }

        ResourceLocation resourcelocation = new ResourceLocation(texName);
        String s = resourcelocation.getResourceDomain();
        String s1 = resourcelocation.getResourcePath();
        if (!s1.contains("/")) {
            s1 = "textures/items/" + s1;
        }

        String s2 = s1 + ".png";
        ResourceLocation resourcelocation1 = new ResourceLocation(s, s2);
        boolean flag = Config.hasResource(resourcelocation1);
        if (!flag) {
            Config.warn("File not found: " + s2);
        }

        return resourcelocation1;
    }

    private ResourceLocation getSpriteLocation(ResourceLocation resLoc) {
        String s = resLoc.getResourcePath();
        s = StrUtils.removePrefix(s, "textures/");
        s = StrUtils.removeSuffix(s, ".png");
        return new ResourceLocation(resLoc.getResourceDomain(), s);
    }

    public void updateModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator) {
        String[] astring = this.getModelTextures();
        boolean flag = this.isUseTint();
        this.model = makeBakedModel(textureMap, itemModelGenerator, astring, flag);
        if (this.type == 1 && this.mapTextures != null) {
            for (String s : this.mapTextures.keySet()) {
                String s1 = this.mapTextures.get(s);
                String s2 = StrUtils.removePrefix(s, "texture.");
                if (s2.startsWith("bow") || s2.startsWith("fishing_rod")) {
                    String[] astring1 = new String[]{s1};
                    IBakedModel ibakedmodel = makeBakedModel(textureMap, itemModelGenerator, astring1, flag);
                    if (this.mapModels == null) {
                        this.mapModels = new HashMap<>();
                    }

                    this.mapModels.put(s2, ibakedmodel);
                }
            }
        }
    }

    private boolean isUseTint() {
        return true;
    }

    private static IBakedModel makeBakedModel(TextureMap textureMap, ItemModelGenerator itemModelGenerator, String[] textures, boolean useTint) {
        ModelBlock modelblock = makeModelBlock(textures);
        ModelBlock modelblock1 = itemModelGenerator.makeItemModel(textureMap, modelblock);
        return bakeModel(textureMap, modelblock1, useTint);
    }

    private String[] getModelTextures() {
        if (this.type == 1 && this.items.length == 1) {
            Item item = Item.getItemById(this.items[0]);
            if (item == Items.potionitem && this.damage != null && this.damage.getCountRanges() > 0) {
                RangeInt rangeint = this.damage.getRange(0);
                int i = rangeint.getMin();
                boolean flag = (i & 16384) != 0;
                String s5 = this.getMapTexture(this.mapTextures, "texture.potion_overlay", "items/potion_overlay");
                String s6 = null;
                if (flag) {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_splash", "items/potion_bottle_splash");
                } else {
                    s6 = this.getMapTexture(this.mapTextures, "texture.potion_bottle_drinkable", "items/potion_bottle_drinkable");
                }

                return new String[]{s5, s6};
            }

            if (item instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor)item;
                if (itemarmor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                    String s = "leather";
                    String s1 = "helmet";
                    if (itemarmor.armorType == 0) {
                        s1 = "helmet";
                    }

                    if (itemarmor.armorType == 1) {
                        s1 = "chestplate";
                    }

                    if (itemarmor.armorType == 2) {
                        s1 = "leggings";
                    }

                    if (itemarmor.armorType == 3) {
                        s1 = "boots";
                    }

                    String s2 = s + "_" + s1;
                    String s3 = this.getMapTexture(this.mapTextures, "texture." + s2, "items/" + s2);
                    String s4 = this.getMapTexture(this.mapTextures, "texture." + s2 + "_overlay", "items/" + s2 + "_overlay");
                    return new String[]{s3, s4};
                }
            }
        }

        return new String[]{this.texture};
    }

    private String getMapTexture(Map<String, String> map, String key, String def) {
        if (map == null) {
            return def;
        }

        String s = map.get(key);
        return s == null ? def : s;
    }

    private static ModelBlock makeModelBlock(String[] modelTextures) {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("{\"parent\": \"builtin/generated\",\"textures\": {");

        for (int i = 0; i < modelTextures.length; i++) {
            String s = modelTextures[i];
            if (i > 0) {
                stringbuffer.append(", ");
            }

            stringbuffer.append("\"layer" + i + "\": \"" + s + "\"");
        }

        stringbuffer.append("}}");
        String s1 = stringbuffer.toString();
        return ModelBlock.deserialize(s1);
    }

    private static IBakedModel bakeModel(TextureMap textureMap, ModelBlock modelBlockIn, boolean useTint) {
        ModelRotation modelrotation = ModelRotation.X0_Y0;
        boolean flag = false;
        TextureAtlasSprite textureatlassprite = textureMap.getSpriteSafe(modelBlockIn.resolveTextureName("particle"));
        SimpleBakedModel.Builder simplebakedmodel$builder = new SimpleBakedModel.Builder(modelBlockIn).setTexture(textureatlassprite);

        for (BlockPart blockpart : modelBlockIn.getElements()) {
            for (EnumFacing enumfacing : blockpart.mapFaces.keySet()) {
                BlockPartFace blockpartface = blockpart.mapFaces.get(enumfacing);
                if (!useTint) {
                    blockpartface = new BlockPartFace(blockpartface.cullFace, -1, blockpartface.texture, blockpartface.blockFaceUV);
                }

                TextureAtlasSprite textureatlassprite1 = textureMap.getSpriteSafe(modelBlockIn.resolveTextureName(blockpartface.texture));
                BakedQuad bakedquad = makeBakedQuad(blockpart, blockpartface, textureatlassprite1, enumfacing, modelrotation, flag);
                if (blockpartface.cullFace == null) {
                    simplebakedmodel$builder.addGeneralQuad(bakedquad);
                } else {
                    simplebakedmodel$builder.addFaceQuad(modelrotation.rotateFace(blockpartface.cullFace), bakedquad);
                }
            }
        }

        return simplebakedmodel$builder.makeBakedModel();
    }

    private static BakedQuad makeBakedQuad(
        BlockPart blockPart,
        BlockPartFace blockPartFace,
        TextureAtlasSprite textureAtlasSprite,
        EnumFacing enumFacing,
        ModelRotation modelRotation,
        boolean uvLocked
    ) {
        FaceBakery facebakery = new FaceBakery();
        return facebakery.makeBakedQuad(
            blockPart.positionFrom,
            blockPart.positionTo,
            blockPartFace,
            textureAtlasSprite,
            enumFacing,
            modelRotation,
            blockPart.partRotation,
            uvLocked,
            blockPart.shade
        );
    }

    @Override
    public String toString() {
        return "" + this.basePath + "/" + this.name + ", type: " + this.type + ", items: [" + Config.arrayToString(this.items) + "], textture: " + this.texture;
    }

    public float getTextureWidth(TextureManager textureManager) {
        if (this.textureWidth <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureWidth = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                GlStateManager.bindTexture(j);
            }

            if (this.textureWidth <= 0) {
                this.textureWidth = 16;
            }
        }

        return this.textureWidth;
    }

    public float getTextureHeight(TextureManager textureManager) {
        if (this.textureHeight <= 0) {
            if (this.textureLocation != null) {
                ITextureObject itextureobject = textureManager.getTexture(this.textureLocation);
                int i = itextureobject.getGlTextureId();
                int j = GlStateManager.getBoundTexture();
                GlStateManager.bindTexture(i);
                this.textureHeight = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                GlStateManager.bindTexture(j);
            }

            if (this.textureHeight <= 0) {
                this.textureHeight = 16;
            }
        }

        return this.textureHeight;
    }

    public IBakedModel getModel(ModelResourceLocation modelLocation) {
        if (modelLocation != null && this.mapTextures != null) {
            String s = modelLocation.getResourcePath();
            if (this.mapModels != null) {
                IBakedModel ibakedmodel = this.mapModels.get(s);
                if (ibakedmodel != null) {
                    return ibakedmodel;
                }
            }
        }

        return this.model;
    }
}
