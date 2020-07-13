package xyz.msws.supergive.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Global utility class for commonly used methods
 * 
 * @author imodm
 *
 */
public class Utils {

	public static GameMode getGameMode(String mode) {
		String result = getOption(mode, GameMode.values());
		return result == null ? null : GameMode.valueOf(result);
	}

	public static EntityType getEntityType(String type) {
		String result = getOption(type, EntityType.values());
		return result == null ? null : EntityType.valueOf(result);
	}

	public static PotionEffectType getPotionEffect(String type) {
		String result = getOption(type, Arrays.asList(PotionEffectType.values()).stream().filter(p -> p != null)
				.map(potion -> potion.getName()).collect(Collectors.toList()));
		return result == null ? null : PotionEffectType.getByName(result);
	}

	public static ItemFlag getItemFlag(String flag) {
		String result = getOption(flag, ItemFlag.values());
		return result == null ? null : ItemFlag.valueOf(result);
	}

	public static String getOption(String key, List<? extends Object> options) {
		List<String> values = options.stream().map(m -> m.toString()).collect(Collectors.toList());
		for (String s : values) {
			if (MSG.normalize(key).equals(MSG.normalize(s)))
				return s;
		}
		for (String s : values) {
			if (MSG.normalize(key).startsWith(MSG.normalize(s)))
				return s;
		}
		for (String s : values) {
			if (MSG.normalize(key).contains(MSG.normalize(s)))
				return s;
		}
		return null;
	}

	public static String getOption(String key, Object[] options) {
		return getOption(key, Arrays.asList(options));
	}

	@SuppressWarnings("deprecation")
	public static Enchantment getEnchantment(String ench) {
		try {
			return Enchantment.getByKey(NamespacedKey.minecraft(ench.toUpperCase()));
		} catch (IllegalArgumentException e) {
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getKey().getKey()).equalsIgnoreCase(MSG.normalize(ench)))
					return en;
			}
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getKey().getKey()).startsWith(MSG.normalize(ench)))
					return en;
			}
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getKey().getKey()).contains(MSG.normalize(ench)))
					return en;
			}
		} catch (NoClassDefFoundError e) {
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getName()).equalsIgnoreCase(MSG.normalize(ench)))
					return en;
			}
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getName()).startsWith(MSG.normalize(ench)))
					return en;
			}
			for (Enchantment en : Enchantment.values()) {
				if (MSG.normalize(en.getName()).contains(MSG.normalize(ench)))
					return en;
			}
		}
		return null;
	}

	public static boolean containsUnsafeEnchantments(ItemStack item) {
		for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
			if (!entry.getKey().canEnchantItem(item))
				return true;
			if (entry.getKey().getMaxLevel() < entry.getValue())
				return true;
		}
		return false;
	}
}
