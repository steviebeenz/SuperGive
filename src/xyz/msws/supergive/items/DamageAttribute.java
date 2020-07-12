package xyz.msws.supergive.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Adds support for specifying damage on {@link Damageable} items.
 * 
 * @author imodm
 *
 */
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

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (!"damage:".startsWith(current.toLowerCase()))
			return null;
		return Arrays.asList("damage:");
	}

	@Override
	public String getPermission() {
		return "supergive.attribute.damage";
	}

}
