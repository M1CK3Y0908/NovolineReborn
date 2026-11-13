var Vec3 = function(x, y, z) {
    return $interop.MC_createVec3(x, y, z);
};

var Vec3i = function(x, y, z) {
    return $interop.MC_createVec3i(x, y, z);
};

var BlockPos = function(x, y, z) {
    return $interop.MC_createBlockPos(x, y, z);
};

var ScaledResolution = function(mc) {
    return $interop.MC_createScaledResolution(mc);
};

var AxisAlignedBB = function(bp1, bp2) {
    return $interop.MC_createAxisAlignedBB(bp1, bp2);
};

var PacketBuffer = function(wrappedByteBuf) {
    return $interop.MC_createPacketBuffer(wrappedByteBuf);
};

var Framebuffer = function(displayWidth, displayHeight, useDepth) {
    return $interop.MC_createFramebuffer(displayWidth, displayHeight, useDepth);
};

var EnumFacing = {
    UP   : $interop.MC_getEnumFacing("UP"   ),
    DOWN : $interop.MC_getEnumFacing("DOWN" ),
    NORTH: $interop.MC_getEnumFacing("NORTH"),
    SOUTH: $interop.MC_getEnumFacing("SOUTH"),
    EAST : $interop.MC_getEnumFacing("EAST" ),
    WEST : $interop.MC_getEnumFacing("WEST" )
};

var MathHelper = {
    clamp_double: function (current, min, max) {
        return $interop.MC_MathHelper_clampDouble(current, min, max);
    },
    clamp_int: function (current, min, max) {
        return $interop.MC_MathHelper_clampInt(current, min, max);
    },
    cos: function (value) {
        return $interop.MC_MathHelper_cos(value);
    },
    sin: function (value) {
        return $interop.MC_MathHelper_sin(value);
    },
    sqrt_double: function (value) {
        return $interop.MC_MathHelper_sqrtDouble(value);
    },
    floor_double: function (value) {
        return $interop.MC_MathHelper_floorDouble(value);
    },
    floor_double_long: function (value) {
        return $interop.MC_MathHelper_floorDoubleLong(value);
    },
    abs: function (value) {
        return $interop.MC_MathHelper_abs(value);
    },
    abs_int: function (value) {
        return $interop.MC_MathHelper_absInt(value);
    },
    ceiling_double_int: function (value) {
        return $interop.MC_MathHelper_ceilingDoubleInt(value);
    },
    denormalizeClamp: function (lowerBnd, upperBnd, slide) {
        return $interop.MC_MathHelper_denormalizeClamp(lowerBnd, upperBnd, slide);
    },
    abs_max: function (n1, n2) {
        return $interop.MC_MathHelper_absMax(n1, n2);
    },
    wrapAngleTo180_double: function (value) {
        return $interop.MC_MathHelper_wrapAngleTo180Double(value);
    },
    roundUpToPowerOfTwo: function (value) {
        return $interop.MC_MathHelper_roundUpToPowerOfTwo(value);
    },
    roundUp: function (value, places) {
        return $interop.MC_MathHelper_roundUp(value, places);
    },
    atan2: function (n1, n2) {
        return $interop.MC_MathHelper_atan2(n1, n2);
    },
    hsvToRGB: function (h, s, v) {
        return $interop.MC_MathHelper_hsvToRGB(h, s, v);
    }
};

var GlStateManager = {
    pushAttrib: function () {
        $interop.MC_GlStateManager_pushAttrib();
    },
    popAttrib: function () {
        $interop.MC_GlStateManager_popAttrib();
    },
    disableAlpha: function () {
        $interop.MC_GlStateManager_disableAlpha();
    },
    enableAlpha: function () {
        $interop.MC_GlStateManager_enableAlpha();
    },
    alphaFunc: function (func, ref) {
        $interop.MC_GlStateManager_alphaFunc(func, ref);
    },
    enableLighting: function () {
        $interop.MC_GlStateManager_enableLighting();
    },
    disableLighting: function () {
        $interop.MC_GlStateManager_disableLighting();
    },
    enableLight: function (light) {
        $interop.MC_GlStateManager_enableLight(light);
    },
    disableLight: function (light) {
        $interop.MC_GlStateManager_disableLight(light);
    },
    enableColorMaterial: function () {
        $interop.MC_GlStateManager_enableColorMaterial();
    },
    disableColorMaterial: function () {
        $interop.MC_GlStateManager_disableColorMaterial();
    },
    colorMaterial: function (face, mode) {
        $interop.MC_GlStateManager_colorMaterial(face, mode);
    },
    disableDepth: function () {
        $interop.MC_GlStateManager_disableDepth();
    },
    enableDepth: function () {
        $interop.MC_GlStateManager_enableDepth();
    },
    depthFunc: function (func) {
        $interop.MC_GlStateManager_depthFunc(func);
    },
    depthMask: function (flag) {
        $interop.MC_GlStateManager_depthMask(flag);
    },
    disableBlend: function () {
        $interop.MC_GlStateManager_disableBlend();
    },
    enableBlend: function () {
        $interop.MC_GlStateManager_enableBlend();
    },
    blendFunc: function (srcFactor, dstFactor) {
        $interop.MC_GlStateManager_blendFunc(srcFactor, dstFactor);
    },
    tryBlendFuncSeparate: function (srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha) {
        $interop.MC_GlStateManager_tryBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
    },
    enableFog: function () {
        $interop.MC_GlStateManager_enableFog();
    },
    disableFog: function () {
        $interop.MC_GlStateManager_disableFog();
    },
    setFog: function (param) {
        $interop.MC_GlStateManager_setFog(param);
    },
    setFogDensity: function (param) {
        $interop.MC_GlStateManager_setFogDensity(param);
    },
    setFogStart: function (param) {
        $interop.MC_GlStateManager_setFogStart(param);
    },
    setFogEnd: function (param) {
        $interop.MC_GlStateManager_setFogEnd(param);
    },
    enableCull: function () {
        $interop.MC_GlStateManager_enableCull();
    },
    disableCull: function () {
        $interop.MC_GlStateManager_disableCull();
    },
    cullFace: function (mode) {
        $interop.MC_GlStateManager_cullFace(mode);
    },
    enablePolygonOffset: function () {
        $interop.MC_GlStateManager_enablePolygonOffset();
    },
    disablePolygonOffset: function () {
        $interop.MC_GlStateManager_disablePolygonOffset();
    },
    doPolygonOffset: function (factor, units) {
        $interop.MC_GlStateManager_doPolygonOffset(factor, units);
    },
    enableColorLogic: function () {
        $interop.MC_GlStateManager_enableColorLogic();
    },
    disableColorLogic: function () {
        $interop.MC_GlStateManager_disableColorLogic();
    },
    colorLogicOp: function (opcode) {
        $interop.MC_GlStateManager_colorLogicOp(opcode);
    },
    enableTexture2D: function () {
        $interop.MC_GlStateManager_enableTexture2D();
    },
    disableTexture2D: function () {
        $interop.MC_GlStateManager_disableTexture2D();
    },
    generateTexture: function () {
        return $interop.MC_GlStateManager_generateTexture();
    },
    deleteTexture: function (texture) {
        $interop.MC_GlStateManager_deleteTexture(texture);
    },
    bindTexture: function (texture) {
        $interop.MC_GlStateManager_bindTexture(texture);
    },
    enableNormalize: function () {
        $interop.MC_GlStateManager_enableNormalize();
    },
    disableNormalize: function () {
        $interop.MC_GlStateManager_disableNormalize();
    },
    shadeModel: function (mode) {
        $interop.MC_GlStateManager_shadeModel(mode);
    },
    enableRescaleNormal: function () {
        $interop.MC_GlStateManager_enableRescaleNormal();
    },
    disableRescaleNormal: function () {
        $interop.MC_GlStateManager_disableRescaleNormal();
    },
    viewport: function (x, y, width, height) {
        $interop.MC_GlStateManager_viewport(x, y, width, height);
    },
    colorMask: function (red, green, blue, alpha) {
        $interop.MC_GlStateManager_colorMask(red, green, blue, alpha);
    },
    clearDepth: function (depth) {
        $interop.MC_GlStateManager_clearDepth(depth);
    },
    clearColor: function (red, green, blue, alpha) {
        $interop.MC_GlStateManager_clearColor(red, green, blue, alpha);
    },
    clear: function (mask) {
        $interop.MC_GlStateManager_clear(mask);
    },
    matrixMode: function (mode) {
        $interop.MC_GlStateManager_matrixMode(mode);
    },
    loadIdentity: function () {
        $interop.MC_GlStateManager_loadIdentity();
    },
    pushMatrix: function () {
        $interop.MC_GlStateManager_pushMatrix();
    },
    popMatrix: function () {
        $interop.MC_GlStateManager_popMatrix();
    },
    rotate: function (angle, x, y, z) {
        $interop.MC_GlStateManager_rotate(angle, x, y, z);
    },
    scale: function (x, y, z) {
        $interop.MC_GlStateManager_scale(x, y, z);
    },
    translate: function (x, y, z) {
        $interop.MC_GlStateManager_translate(x, y, z);
    },
    color: function (r, g, b, a) {
        $interop.MC_GlStateManager_color(r, g, b, a);
    },
    resetColor: function () {
        $interop.MC_GlStateManager_resetColor();
    }
};

// ==============

var C00PacketKeepAlive = function(key) {
    this.packetId = 0x00;
    this.packetType = 'PLAY';
    this.key = key;
};

var C01PacketChatMessage = function(message) {
    this.packetId = 0x01;
    this.packetType = 'PLAY';
    this.message = message;
};

var C02PacketUseEntity = function() {
    this.packetId = 0x02;
    this.packetType = 'PLAY';

    if (arguments.length === 2) {
        this.entity = arguments[0];

        if (arguments[1].hasOwnProperty('_jObject')) {
            this.hitVec = arguments[1];
        } else {
            this.action = arguments[1];
        }
    }
};

var C03PacketPlayer = function(onGround) {
    this.packetId = 0x03;
    this.packetType = 'PLAY';
    this.onGround = onGround;
};

var C04PacketPlayerPosition = function(posX, posY, posZ, onGround) {
    this.packetId = 0x04;
    this.packetType = 'PLAY';
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.onGround = onGround;
};

var C05PacketPlayerLook = function(yaw, pitch, onGround) {
    this.packetId = 0x05;
    this.packetType = 'PLAY';
    this.yaw = yaw;
    this.pitch = pitch;
    this.onGround = onGround;
};

var C06PacketPlayerPosLook = function(posX, posY, posZ, yaw, pitch, onGround) {
    this.packetId = 0x06;
    this.packetType = 'PLAY';
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.yaw = yaw;
    this.pitch = pitch;
    this.onGround = onGround;
};

var C07PacketPlayerDigging = function(status, pos, facing) {
    this.packetId = 0x07;
    this.packetType = 'PLAY';
    this.status = status;
    this.pos = pos;
    this.facing = facing;
};

var C08PacketPlayerBlockPlacement = function(pos, placedBlockDirection, stack, facingX, facingY, facingZ) {
    this.packetId = 0x08;
    this.packetType = 'PLAY';
    this.pos = pos;
    this.placedBlockDirection = placedBlockDirection;
    this.stack = stack;
    this.facingX = facingX;
    this.facingY = facingY;
    this.facingZ = facingZ;
};

var C09PacketHeldItemChange = function(slotId) {
    this.packetId = 0x09;
    this.packetType = 'PLAY';
    this.slotId = slotId;
};

var C0APacketAnimation = function() {
    this.packetId = 0x0A;
    this.packetType = 'PLAY';
};

var C0BPacketEntityAction = function() {
    this.packetId = 0x0B;
    this.packetType = 'PLAY';

    if(arguments.length >= 2) {
        this.entity = arguments[0];
        this.action = arguments[1];

        if(arguments.length === 3) {
            this.auxData = arguments[2];
        }
    }
};

var C0CPacketInput = function(strafeSpeed, forwardSpeed, jumping, sneaking) {
    this.packetId = 0x0C;
    this.packetType = 'PLAY';
    this.strafeSpeed = strafeSpeed;
    this.forwardSpeed = forwardSpeed;
    this.jumping = jumping;
    this.sneaking = sneaking;
};

var C0DPacketCloseWindow = function(windowId) {
    this.packetId = 0x0D;
    this.packetType = 'PLAY';
    this.windowId = windowId;
};

var C0EPacketClickWindow = function(windowId, slotId, usedButton, mode, clickedItem, actionNumber) {
    this.packetId = 0x0E;
    this.packetType = 'PLAY';
    this.windowId = windowId;
    this.slotId = slotId;
    this.usedButton = usedButton;
    this.mode = mode;
    this.clickedItem = clickedItem;
    this.actionNumber = actionNumber;
};

var C0FPacketConfirmTransaction = function(windowId, uid, accepted) {
    this.packetId = 0x0F;
    this.packetType = 'PLAY';
    this.windowId = windowId;
    this.uid = uid;
    this.accepted = accepted;
};

var C10PacketCreativeInventoryAction = function (slotId, stack) {
    this.packetId = 0x10;
    this.packetType = 'PLAY';
    this.slotId = slotId;
    this.stack = stack;
};

var C11PacketEnchantItem = function (windowId, button) {
    this.packetId = 0x11;
    this.packetType = 'PLAY';
    this.windowId = windowId;
    this.button = button;
};

var C12PacketUpdateSign = function (lines, pos) {
    this.packetId = 0x12;
    this.packetType = 'PLAY';
    this.lines = lines;
    this.pos = pos;
};

var C13PacketPlayerAbilities = function (flySpeed, walkSpeed, flying, allowFlying, creativeMode, invulnerable) {
    this.packetId = 0x13;
    this.packetType = 'PLAY';
    this.flySpeed = flySpeed;
    this.walkSpeed = walkSpeed;
    this.flying = flying;
    this.allowFlying = allowFlying;
    this.creativeMode = creativeMode;
    this.invulnerable = invulnerable;
};

var C14PacketTabComplete = function () {
    this.packetId = 0x14;
    this.packetType = 'PLAY';

    if(arguments.length === 1) {
        this.msg = arguments[0];
    } else if(arguments.length === 2) {
        this.msg = arguments[0];
        this.target = arguments[1];
    }
};

var C15PacketClientSettings = function (lang, view, chatVisibility, enableColors, modelPartFlags) {
    this.packetId = 0x15;
    this.packetType = 'PLAY';
    this.lang = lang;
    this.view = view;
    this.chatVisibility = chatVisibility;
    this.enableColors = enableColors;
    this.modelPartFlags = modelPartFlags;
};

var C16PacketClientStatus = function (status) {
    this.packetId = 0x16;
    this.packetType = 'PLAY';
    this.status = status;
};

var C17PacketCustomPayload = function (channel, buffer) {
    this.packetId = 0x17;
    this.packetType = 'PLAY';
    this.channel = channel;
    this.buffer = buffer;
};

var C18PacketSpectate = function (uuid) {
    this.packetId = 0x18;
    this.packetType = 'PLAY';
    this.uuid = uuid;
};

var C19PacketResourcePackStatus = function (hash, status) {
    this.packetId = 0x19;
    this.packetType = 'PLAY';
    this.hash = hash;
    this.status = status;
};
