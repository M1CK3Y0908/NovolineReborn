'use strict';

/* Nashorn security fix */
// noinspection ThisExpressionReferencesGlobalObjectJS
Object.defineProperty(this, 'context', {});
// noinspection ThisExpressionReferencesGlobalObjectJS
delete this.__noSuchProperty__;

/* Prototype extensions */
String.prototype.contains = String.prototype.contains || function(str) {
    return this.indexOf(str) >= 0;
};

String.prototype.startsWith = String.prototype.startsWith || function(prefix) {
    return this.indexOf(prefix) === 0;
};

String.prototype.endsWith = String.prototype.endsWith || function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) >= 0;
};

/* Module exports */
var __exports__ = (function() {
    var JavaFloat = Java.type('java.lang.Float');

    /* Scoped helper functions */
    var dtf = function (value) {
        return new JavaFloat(value);
    };

    var populateFeatureProperties = function (feature) {
        feature.on = function (name, priority, eventCallback) {
            client.features.__Feature$on__(feature, name, priority, eventCallback);
        };

        feature.addSetting = function () {
            return client.features.__Feature$addSetting__(feature,
                arguments[0], arguments[1],
                arguments[2], arguments[3]);
        };

        return feature;
    };

    return [
        { /* Client API */
            name    : $interop.C_name,
            version : $interop.C_version,
            build   : $interop.C_build,
            channel : $interop.C_channel,
            /* ----------------------------------------- */
            getLanguage: function() {
                return $interop.C_getLanguage();
            },
            clientChatMessage: function(message) {
                $interop.C_clientChatMessage(message);
            },
            /* ----------------------------------------- */
            features: {
                __Feature$on__: function(feature, name, priority, eventCallback) {
                    // Qualify event name
                    name = name.toString();
                    name = name[0].toUpperCase() + name.slice(1);

                    if(!name.endsWith('Event')) {
                        name += 'Event';
                    }

                    // Register handler
                    $interop.C_registerEventListener(feature['_jObject'], name, priority, eventCallback);
                },
                __Feature$addSetting__: function(feature, type, name, value, overrides) {
                    return $interop.C_registerSetting(feature['_jObject'], type, name, value, overrides || {});
                },
                register: function(name, description) {
                    return populateFeatureProperties($interop.C_registerFeature(name, description));
                },
                getByName: function(name) {
                    return populateFeatureProperties($interop.C_getFeatureByName(name));
                }
            },
            task: {
                createFutureTask: function(delay, overrides) {
                    if(
                        typeof overrides['execute'] === 'undefined' &&
                        typeof overrides['run'] === 'undefined'
                    ) {
                        throw new Error("Future task object must contain at least `execute` or `run`");
                    }

                    $interop.C_Task_queueFutureTask(delay, overrides);
                }
            },
            connection: {
                sendPacket: function(packetObj) {
                    this.__sendPacketValidate__(packetObj, false);
                },
                sendPacketNoEvent: function(packetObj) {
                    this.__sendPacketValidate__(packetObj, true);
                },
                __sendPacketValidate__: function(packetObj, noEvent) {
                    if(typeof packetObj['packetType'] === 'undefined') {
                        packetObj.packetType = 'PLAY'; // assume play if not entered
                    }

                    if(typeof packetObj['packetId'] === 'undefined') {
                        throw new Error('`packetId` is not supplied');
                    }

                    $interop.MC_sendPacketInternal(packetObj, noEvent);
                }
            },
            user: {
                uid: $interop.U_uid,
                username: $interop.U_username
            },
            directories: {
                root: $interop.C_Directories_ROOT,
                storage: $interop.C_Directories_STORAGE,
                scripts: $interop.C_Directories_SCRIPTS
            },
            notifications: {
                push: function(notiObject) {
                    $interop.C_Notifications_push(notiObject);
                }
            },
            render: {
                createRenderer: function (name, params) {
                    return $interop.C_Render_createRenderer(name, params);
                },
                createTransition: function () {
                    return $interop.C_Render_createTransition();
                }
            },
            utils: {
                enchantment: {
                    getArmorScore: function(itemStack) {
                        $interop.U_Enchantment_getArmorScore(itemStack);
                    },
                    getMaterialScore: function(materialName) {
                        $interop.U_Enchantment_getMaterialScore(materialName);
                    },
                    getToolScore: function(itemStack) {
                        $interop.U_Enchantment_getToolScore(itemStack);
                    }
                },
                inventory: {
                    findEmptyHotbarSlot: function() {
                        return $interop.U_Inventory_findEmptyHotbarSlot();
                    },
                    findSlotWithBlocks: function(best) {
                        return $interop.U_Inventory_findSlotWithBlocks(best);
                    },
                    findBestArmorSlot: function(armorType) {
                        return $interop.U_Inventory_findBestArmorSlot(armorType);
                    },
                    getItemByUnlocalizedName: function (name) {
                        return $interop.U_Inventory_getItemByUnlocalizedName(name);
                    }
                    // TODO: findBestToolSlot, findSlotByClass
                },
                math: {
                    sinerp: function() {
                        if(arguments.length === 3) {
                            return $interop.U_Math_sinerp$1(arguments[0], arguments[1], arguments[2]);
                        } else if(arguments.length === 4) {
                            return $interop.U_Math_sinerp$2(arguments[0], arguments[1], arguments[2], arguments[3]);
                        } else {
                            throw new TypeError("sinerp requires either 3 or 4 arguments");
                        }
                    }
                },
                player: {
                    getLastTickDistance: function() {
                        return $interop.U_Player_getLastTickDistance();
                    },
                    getBaseMoveSpeed: function() {
                        if(arguments.length === 2) {
                            return $interop.U_Player_getBaseMoveSpeed$1(arguments[0], arguments[1]);
                        } else if(arguments.length === 1) {
                            return $interop.U_Player_getBaseMoveSpeed$2(arguments[0]);
                        } else {
                            return $interop.U_Player_getBaseMoveSpeed$3();
                        }
                    },
                    isMoving: function() {
                        return $interop.U_Player_isMoving();
                    },
                    getDirection: function() {
                        return $interop.U_Player_getDirection();
                    },
                    setMotionSpeed: function(speed) {
                        $interop.U_Player_setMotionSpeed(speed);
                    },
                    setSpeed: function(speed) {
                        $interop.U_Player_setSpeed(speed);
                    },
                    isBlockUnder: function() {
                        return $interop.U_Player_isBlockUnder();
                    },
                    isReallyOnGround: function() {
                        return $interop.U_Player_isReallyOnGround();
                    },
                    isOnGround: function() {
                        if(arguments.length === 1) {
                            return $interop.U_Player_isOnGround$1(arguments[0]);
                        } else if(arguments.length === 2) {
                            return $interop.U_Player_isOnGround$2(arguments[0], arguments[1]);
                        } else {
                            throw new TypeError("isOnGround requires either 1 or 2 arguments");
                        }
                    },
                    isInsideBlock: function() {
                        return $interop.U_Player_isInsideBlock();
                    },
                    isInLiquid: function() {
                        return $interop.U_Player_isInLiquid();
                    },
                    isOnLiquid: function() {
                        return $interop.U_Player_isOnLiquid();
                    },
                    isVoidBelow: function(x, y, z) {
                        return !$interop.U_Player_isVoidBelow(x, y, z);
                    },
                    teamColor: function(player) {
                        return $interop.U_Player_teamColor(player);
                    },
                    isSameTeam: function(p1, p2) {
                        return $interop.U_Player_isSameTeam(p1, p2);
                    },
                    getBaseMotionY: function() {
                        if(arguments.length === 0) {
                            return $interop.U_Player_getBaseMotionY$1();
                        } else if(arguments.length === 1) {
                            return $interop.U_Player_getBaseMotionY$2(arguments[0]);
                        } else {
                            throw new TypeError("getBaseMotionY requires either 0 or 1 argument");
                        }
                    },
                    clonePlayer: function() {
                        return $interop.U_Player_clonePlayer();
                    }
                },
                combat: {
                    getRotationsToEntity: function () {
                        if(arguments.length === 3) {
                            return $interop.U_Combat_getRotationsToEntity$1(arguments[0], dtf(arguments[1]), dtf(arguments[2]));
                        } else if(arguments.length === 4) {
                            return $interop.U_Combat_getRotationsToEntity$2(arguments[0], dtf(arguments[1]), dtf(arguments[2]), arguments[3]);
                        } else {
                            throw new TypeError("getRotationToEntity requires either 3 or 4 arguments");
                        }
                    },
                    getRotations: function (x, y, z) {
                        return $interop.U_Combat_getRotations(x, y, z);
                    },
                    getRotationsToBlock: function () {
                        if(arguments.length === 1) {
                            return $interop.U_Combat_getRotationToBlock$1(arguments[0]);
                        } else if(arguments.length === 3) {
                            return $interop.U_Combat_getRotationToBlock$2(arguments[0], arguments[1], arguments[2]);
                        } else {
                            throw new TypeError("getRotationsToBlock requires either 1 or 3 arguments");
                        }
                    },
                    getVectorForRotation: function (yaw, pitch) {
                        return $interop.U_Combat_getVectorForRotation(dtf(yaw), dtf(pitch));
                    },
                    updateRotation: function (angle, targetAngle, maxIncrease) {
                        return $interop.U_Combat_updateRotation(dtf(angle), dtf(targetAngle), dtf(maxIncrease));
                    },
                    getMovementYaw: function () {
                        return $interop.U_Combat_getMovementYaw();
                    },
                    getYawToPoint: function (x, z) {
                        return $interop.U_Combat_getYawToPoint(x, z);
                    },
                    rayTrace: function (from, blockReachDistance, yaw, pitch) {
                        return $interop.U_Combat_rayTrace(from, blockReachDistance, dtf(yaw), dtf(pitch));
                    },
                    rayTraceEntities: function (from, reach, yaw, pitch) {
                        return $interop.U_Combat_rayTraceEntities(from, reach, dtf(yaw), dtf(pitch));
                    }
                },
                animations: {
                    updateDelta: function () {
                        $interop.U_Animations_updateDelta();
                    },
                    animate: function () {
                        if(arguments.length === 3) {
                            return $interop.U_Animations_animate$1(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]));
                        } else if(arguments.length === 4) {
                            return $interop.U_Animations_animate$1(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]));
                        } else {
                            throw new TypeError("animate requires either 3 or 4 arguments");
                        }
                    }
                },
                servers: {
                    serverIs: function (serverType) {
                        return $interop.U_Servers_serverIs(serverType);
                    },
                    hypixelGamemodeIs: function (hypixelGamemode) {
                        return $interop.U_Servers_hypixelGamemodeIs(hypixelGamemode);
                    }
                },
                render: {
                    fixedBlendFunc: function () {
                        $interop.U_Render_fixedBlendFunc();
                    },
                    createFrameBuffer: function (framebuffer) {
                        return $interop.U_Render_createFrameBuffer(framebuffer);
                    },
                    drawFramebuffer: function (framebuffer, scaledResolution) {
                        $interop.U_Render_drawFramebuffer(framebuffer, scaledResolution);
                    },
                    drawFullScreenRect: function (scaledResolution) {
                        $interop.U_Render_drawFullScreenRect(scaledResolution);
                    },
                    drawRect: function (x, y, x2, y2, color) {
                        $interop.U_Render_drawRect(dtf(x), dtf(y), dtf(x2), dtf(y2), color);
                    },
                    drawBorderedRect: function (x, y, x2, y2, thickness, inside, outline) {
                        $interop.U_Render_drawBorderedRect(x, y, x2, y2, thickness, inside, outline);
                    },
                    drawScaledCustomSizeModalRect: function (x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight) {
                        $interop.U_Render_drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
                    },
                    drawImage: function (x, y, width, height, color, imageResource) {
                        $interop.U_Render_drawImage(x, y, width, height, color, imageResource);
                    },
                    drawCirclePercent: function () {
                        if(arguments.length === 6) {
                            $interop.U_Render_drawCirclePercent$1(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), dtf(arguments[4]), dtf(arguments[5]));
                        } else if(arguments.length === 7) {
                            $interop.U_Render_drawCirclePercent$2(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), dtf(arguments[4]), dtf(arguments[5]), dtf(arguments[6]));
                        }
                    },
                    drawGradientRect: function (left, top, right, bottom, upColor, downColor) {
                        $interop.U_Render_drawGradientRect(dtf(left), dtf(top), dtf(right), dtf(bottom), upColor, downColor);
                    },
                    drawGradientRectSideways: function (left, top, right, bottom, leftColor, rightColor) {
                        $interop.U_Render_drawGradientRectSideways(dtf(left), dtf(top), dtf(right), dtf(bottom), leftColor, rightColor);
                    },
                    rainbowCircle: function (x, y, r, quality) {
                        $interop.U_Render_rainbowCircle(dtf(x), dtf(y), dtf(r), quality);
                    },
                    drawModalRectWithCustomSizedTexture: function (x, y, u, v, width, height, textureWidth, textureHeight) {
                        $interop.U_Render_drawModalRectWithCustomSizedTexture(x, y, u, v, width, height, textureWidth, textureHeight);
                    },
                    drawTexturedModalRect: function (x, y, textureX, textureY, width, height, atlasSize) {
                        if(arguments.length === 6) {
                            $interop.U_Render_drawTexturedModalRect$1(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), dtf(arguments[4]), dtf(arguments[5]));
                        } else if(arguments.length === 7) {
                            $interop.U_Render_drawTexturedModalRect$2(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), dtf(arguments[4]), dtf(arguments[5]), arguments[6]);
                        }
                    },
                    translateColorAnimated: function (currentColor, endColor, speed) {
                        return $interop.U_Render_translateColorAnimated(currentColor, endColor, speed);
                    },
                    withAlpha: function (color, alpha) {
                        return $interop.U_Render_withAlpha(color, alpha);
                    },
                    resetColor: function () {
                        $interop.U_Render_resetColor();
                    },
                    setupColor: function (color) {
                        $interop.U_Render_setupColor(color);
                    },
                    hexToColor: function (hex) {
                        return $interop.U_Render_hexToColor(hex);
                    },
                    hexToColor10: function (hex) {
                        return $interop.U_Render_hexToColor10(hex);
                    },
                    colorToHex: function (color) {
                        return$interop.U_Render_colorToHex(color);
                    },
                    rainbowColor: function () {
                        if(arguments.length === 5) {
                            return $interop.U_Render_rainbowColor$1(arguments[0], arguments[1], dtf(arguments[2]), dtf(arguments[3]), arguments[4]);
                        } else if(arguments.length === 4) {
                            return $interop.U_Render_rainbowColor$2(arguments[0], dtf(arguments[1]), dtf(arguments[2]), arguments[3]);
                        } else {
                            throw new TypeError("rainbowColor requires either 4 or 5 arguments");
                        }
                    },
                    astolfoColor: function () {
                        if(arguments.length === 5) {
                            return $interop.U_Render_astolfoColor$1(arguments[0], arguments[1], dtf(arguments[2]), dtf(arguments[3]), arguments[4]);
                        } else if(arguments.length === 4) {
                            return $interop.U_Render_astolfoColor$2(arguments[0], dtf(arguments[1]), dtf(arguments[2]), arguments[3]);
                        } else {
                            throw new TypeError("astolfoColor requires either 4 or 5 arguments");
                        }
                    },
                    interpolateColor: function (a, b, percent) {
                        return $interop.U_Render_interpolateColor(a, b, percent)
                    },
                    gradientColor: function (col1, col2, index, count, cycleTime) {
                        return $interop.U_Render_gradientColor(col1, col2, index, count, cycleTime);
                    },
                    fadeColor: function (hex, min, max, index, count, cycleTime) {
                        return $interop.U_Render_fadeColor(hex, min, max, index, count, cycleTime);
                    },
                    drawGradientLine: function (x, y, x2, y2, lineWidth, firstColor, secondColor, smooth) {
                        return $interop.U_Render_drawGradientLine(x, y, x2, y2, lineWidth, firstColor, secondColor, smooth);
                    },
                    drawShadow: function (x1, y1, x2, y2, width, color) {
                        $interop.U_Render_drawShadow(dtf(x1), dtf(y1), dtf(x2), dtf(y2), dtf(width), color);
                    },
                    scissorStart: function (x, y, width, height) {
                        $interop.U_Render_scissorStart(x, y, width, height);
                    },
                    scissorEnd: function () {
                        $interop.U_Render_scissorEnd();
                    },
                    worldToScreen: function () {
                        if(arguments.length === 1) {
                            return $interop.U_Render_worldToScreen$1(arguments[0]);
                        } else if(arguments.length === 3) {
                            return $interop.U_Render_worldToScreen$2(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]));
                        } else {
                            throw new TypeError("worldToScreen requires either 1 or 3 arguments");
                        }
                    },
                    drawLine: function () {
                        if(arguments.length === 5) {
                            $interop.U_Render_drawLine$1(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), arguments[4]);
                        } else if(arguments.length === 6) {
                            $interop.U_Render_drawLine$2(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), arguments[4], arguments[5]);
                        } else if(arguments.length === 7) {
                            $interop.U_Render_drawLine$3(dtf(arguments[0]), dtf(arguments[1]), dtf(arguments[2]), dtf(arguments[3]), dtf(arguments[4]), arguments[5], arguments[6]);
                        } else {
                            throw new TypeError("drawLine requires either 5, 6 or 7 arguments");
                        }
                    },
                    enableOutlineMode: function (color) {
                        $interop.U_Render_enableOutlineMode(color);
                    },
                    disableOutlineMode: function () {
                        $interop.U_Render_disableOutlineMode();
                    },
                    drawSelectionBoundingBox: function (box, r, g, b, a) {
                        $interop.U_Render_drawSelectionBoundingBox(box, r, g, b, a);
                    },
                    renderFilledBox: function (box, r, g, b, a) {
                        $interop.U_Render_renderFilledBox(box, dtf(r), dtf(g), dtf(b), dtf(a));
                    },
                    drawSelectionBox: function (box, color) {
                        $interop.U_Render_drawSelectionBox(box, color, 0);
                    },
                    drawFilledBox: function (box, color) {
                        $interop.U_Render_drawFilledBox(box, color, 0);
                    }
                }
            },
            fonts: {
                createFontRenderer: function (name, size) {
                    return $interop.C_Fonts_createFontRenderer(name, size);
                }
            }
        },
        { /* Event Priorities */
            VERY_LOW:   0,
            LOW:        1,
            NORMAL:     2,
            HIGH:       3,
            VERY_HIGH:  4
        }
    ];
})();

const client = __exports__[0];
const Priorities = __exports__[1];