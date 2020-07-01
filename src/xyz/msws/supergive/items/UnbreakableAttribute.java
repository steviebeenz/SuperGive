package xyz.msws.supergive.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("unbreakable:"))
			return item;
		boolean val = Boolean.valueOf(line.substring("unbreakable:".length()));

		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(val);
		item.setItemMeta(meta);
		return item;
	}

}
