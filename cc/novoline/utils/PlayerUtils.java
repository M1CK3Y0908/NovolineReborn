package cc.novoline.utils;

import cc.novoline.Novoline;
import cc.novoline.modules.AbstractModule;
import cc.novoline.modules.binds.KeyboardKeybind;
import cc.novoline.modules.configurations.holder.ModuleHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

public final class PlayerUtils {

	private static int pressCount;

	public static void onBindInGui() {
		if(Keyboard.getEventKeyState()) {
			int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

			for(ModuleHolder<?> holder : Novoline.getInstance().getModuleManager().getModuleManager().values()) {
				AbstractModule module = holder.getModule();

				//noinspection InstanceofConcreteClass
				if(module.getKeybind().get() instanceof KeyboardKeybind && module.getKeybind().get().getKey() == k) {
					if(Keyboard.isKeyDown(k)) {
						pressCount++;
					}

					if(pressCount == 1) {
						module.toggle();
					}
				}
			}
		} else {
			pressCount = 0;
		}
	}

	public static boolean inTeam(@NotNull ICommandSender entity0, @NotNull ICommandSender entity1) {
		String s = "\u00a7" + teamColor(entity0);

		return     entity0.getDisplayName().getFormattedText().contains(s)
				&& entity1.getDisplayName().getFormattedText().contains(s);
	}

	public static boolean inTeamWithMinecraftPlayer(@NotNull ICommandSender entity) {
		return inTeam(Minecraft.getInstance().player, entity);
	}

	public static @NotNull String teamColor(@NotNull ICommandSender player) {
		Matcher matcher = Pattern.compile("\u00a7(.).*\u00a7r").matcher(player.getDisplayName().getFormattedText());
		return matcher.find() ? matcher.group(1) : "f";
	}

	public static boolean inFov(float fov, net.minecraft.entity.Entity entity) {
		Minecraft mc = Minecraft.getInstance();
		float[] rot = RotationUtils.toEntityBox(entity);
		if (rot == null) return false;
		float diffYaw = net.minecraft.util.MathHelper.wrapAngleTo180_float(mc.player.rotationYaw - rot[0]);
		return Math.abs(diffYaw) <= fov / 2.0f;
	}

	public static boolean isInvisibleUnarmoured(net.minecraft.entity.EntityLivingBase entity) {
		if (!entity.isInvisible()) {
			return false;
		}
		for (int i = 0; i <= 4; ++i) {
			if (entity.getEquipmentInSlot(i) != null) {
				return false;
			}
		}
		return true;
	}

	public static boolean holdingSword() {
		Minecraft mc = Minecraft.getInstance();
		return mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword;
	}

	public static boolean holdingWeapon() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player.getHeldItem() == null) return false;
		net.minecraft.item.Item item = mc.player.getHeldItem().getItem();
		return item instanceof net.minecraft.item.ItemSword || item instanceof net.minecraft.item.ItemAxe;
	}

	@Contract(value = "-> fail", pure = true)
	private PlayerUtils() {
		throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
}
