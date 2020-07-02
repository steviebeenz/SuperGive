package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFlagAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("flag:"))
			return item;
		try {
			ItemFlag flag = ItemFlag.valueOf(line.substring("flag:".length()).toUpperCase());
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(flag);
			item.setItemMeta(meta);
		} catch (IllegalArgumentException e) {
		}
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		ItemMeta meta = item.getItemMeta();
		StringBuilder result = new StringBuilder();
		for (ItemFlag flag : meta.getItemFlags()) {
			result.append("flag:").append(flag.toString());
		}
		return result.toString();
	}

}
