package xyz.msws.supergive.items;

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

}
