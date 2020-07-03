package xyz.msws.supergive.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("damage:"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof Damageable))
			return item;
		try {
			((Damageable) meta).setDamage(Integer.parseInt(line.substring("damage:".length())));
		} catch (NumberFormatException e) {
		}
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof Damageable))
			return null;
		if (((Damageable) meta).getDamage() == 0)
			return null;
		return "damage:" + ((Damageable) meta).getDamage();
	}

}