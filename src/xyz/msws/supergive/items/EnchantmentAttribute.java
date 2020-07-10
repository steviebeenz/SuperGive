package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class EnchantmentAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.contains(":"))
			return item;
		Enchantment ench = Utils.getEnchantment(line.split(":")[0]);
		if (ench == null)
			return item;
		try {
			item.addUnsafeEnchantment(ench, Integer.parseInt(line.split(":")[1]));
		} catch (NumberFormatException e) {
		}
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		if (item.getEnchantments().isEmpty())
			return null;
		StringBuilder result = new StringBuilder();
		for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
			result.append(ench.getKey().getKey().getKey()).append(":").append(ench.getValue()).append(" ");
		}
		return result.toString().trim();
	}

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (current.length() < 3)
			return null;
		List<String> result = new ArrayList<>();
		for (Enchantment ench : Enchantment.values()) {
			String n = ench.getKey().getKey();
			if (MSG.normalize(n).startsWith(MSG.normalize(current)))
				result.add(MSG.normalize(n) + ":");
		}
		return result;
	}

}
