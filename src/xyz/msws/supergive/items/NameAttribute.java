package xyz.msws.supergive.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.utils.MSG;

public class NameAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("name:"))
			return item;
		String name = line.substring("name:".length());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MSG.color("&r" + name));
		item.setItemMeta(meta);
		return item;
	}
}
