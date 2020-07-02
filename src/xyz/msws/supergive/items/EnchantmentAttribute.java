package xyz.msws.supergive.items;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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

}
