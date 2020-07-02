package xyz.msws.supergive.items;

import org.bukkit.Material;
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

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			return null;
		return "name:" + meta.getDisplayName();
	}

}
