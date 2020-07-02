package xyz.msws.supergive.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;

public class Utils {
	public static boolean has(CommandSender sender, String perm, boolean verbose) {
		Preconditions.checkNotNull(sender);
		Preconditions.checkNotNull(perm);
		Preconditions.checkNotNull(verbose);

		if (verbose && !sender.hasPermission(perm)) {
			MSG.tell(sender, "Permissions", MSG.ERROR + "You do not have permission.");
			return false;
		}
		return sender.hasPermission(perm);
	}

	public static boolean isLookingAt(Player player, Location target) {
		Location eye = player.getEyeLocation();
		Vector toEntity = target.toVector().subtract(eye.toVector());
		double dot = toEntity.normalize().dot(eye.getDirection());

		return dot > 0.99D;
	}

	public static GameMode getGameMode(String mode) {
		for (GameMode m : GameMode.values()) {
			if (MSG.normalize(m.toString()).startsWith(MSG.normalize(mode.toLowerCase())))
				return m;
		}
		return null;
	}

	public static EntityType getEntityType(String type) {
		for (EntityType t : EntityType.values()) {
			if (MSG.normalize(t.toString()).equals(MSG.normalize(type)))
				return t;
		}
		for (EntityType t : EntityType.values()) {
			if (MSG.normalize(t.toString()).startsWith(MSG.normalize(type)))
				return t;
		}
		for (EntityType t : EntityType.values()) {
			if (MSG.normalize(t.toString()).contains(MSG.normalize(type)))
				return t;
		}
		return null;
	}

	public static Player matchPlayer(CommandSender sender, String name, boolean verbose) {
		List<Player> matches = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(name))
				return p;
			if (p.getName().toLowerCase().contains(name.toLowerCase())) {
				matches.add(p);
			}
		}

		if (matches.size() == 1)
			return matches.get(0);

		if (!verbose)
			return null;

		if (matches.size() == 0) {
			MSG.tell(sender, "Online Player Search",
					"Could not find any players that matched " + MSG.FORMAT_INFO + name);
			return null;
		}

		String separator = MSG.FORMAT_SEPARATOR + ", " + MSG.PLAYER;

		String match = "[" + MSG.PLAYER;
		for (Player p : matches)
			match += p.getName() + separator;

		if (match.length() > separator.length())
			match = match.substring(0, match.length() - separator.length());
		MSG.tell(sender, "Online Player Search", MSG.FORMAT_INFO + name + " " + MSG.DEFAULT + "matched " + MSG.NUMBER
				+ matches.size() + " " + MSG.DEFAULT + "names. " + match);
		return null;
	}

	public static Enchantment getEnchantment(String ench) {
		switch (MSG.normalize(ench)) {
			case "power":
				return Enchantment.ARROW_DAMAGE;
			case "infinity":
			case "infinite":
				return Enchantment.ARROW_INFINITE;
			case "flame":
				return Enchantment.ARROW_FIRE;
			case "sharpness":
				return Enchantment.DAMAGE_ALL;
			case "bane":
				return Enchantment.DAMAGE_ARTHROPODS;
			case "smite":
				return Enchantment.DAMAGE_UNDEAD;
			case "efficiency":
				return Enchantment.DIG_SPEED;
			default:
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
				}
				break;
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
