package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.utils.MSG;

public class LoreAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("lore:"))
			return item;
		List<String> lore = new ArrayList<>();
		for (String s : line.substring("lore:".length()).split(",")) {
			lore.add(MSG.color(MSG.RESET + s));
		}
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasLore())
			return null;
		return "lore:" + String.join(",", meta.getLore());
	}
}
