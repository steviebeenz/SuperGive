package xyz.msws.supergive.items;

import org.bukkit.inventory.ItemStack;

public interface ItemAttribute {
	public ItemStack modify(String line, ItemStack item);

	public String getModification(ItemStack item);
}
