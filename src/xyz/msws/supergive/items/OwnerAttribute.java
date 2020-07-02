package xyz.msws.supergive.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class OwnerAttribute implements ItemAttribute {

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("owner:"))
			return item;
		if (item.getType() != Material.PLAYER_HEAD)
			return item;
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(line.substring("owner:".length())));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() != Material.PLAYER_HEAD)
			return null;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof SkullMeta))
			return null;
		SkullMeta skull = (SkullMeta) meta;
		return "owner:" + skull.getOwningPlayer().getName();
	}

}
