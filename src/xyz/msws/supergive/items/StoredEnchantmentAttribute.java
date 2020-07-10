package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class StoredEnchantmentAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("stored:"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof EnchantmentStorageMeta))
			return item;
		EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
		Enchantment ench = Utils.getEnchantment(line.split(":")[1]);
		if (ench == null) {
			MSG.warn("Unknown enchantment: " + line);
			return item;
		}
		int level = 1;
		try {
			level = line.split(":").length > 2 ? Integer.parseInt(line.split(":")[2]) : 1;
		} catch (NumberFormatException e) {
			MSG.warn("Invalid enchantment level for " + line);
		}
		book.addEnchant(ench, level, true);
		item.setItemMeta(book);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof EnchantmentStorageMeta))
			return null;
		EnchantmentStorageMeta book = (EnchantmentStorageMeta) meta;
		StringBuilder builder = new StringBuilder();
		for (Entry<Enchantment, Integer> entry : book.getEnchants().entrySet()) {
			builder.append("stored:" + MSG.normalize(entry.getKey().getKey().getKey())).append(":")
					.append(entry.getValue()).append(" ");
		}
		return builder.toString().trim();
	}

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (args.length < 2)
			return null;
		if (!MSG.normalize(args[1]).equalsIgnoreCase("enchantedbook"))
			return null;
		List<String> result = new ArrayList<>();

		if ("stored:".startsWith(current))
			result.add("stored:");
		if (args[args.length - 1].startsWith("stored:")) {
			for (Enchantment ench : Enchantment.values()) {
				if (("stored:" + MSG.normalize(ench.getKey().getKey()))
						.startsWith(MSG.normalize(args[args.length - 1])))
					result.add("stored:" + MSG.normalize(ench.getKey().getKey()) + ":");
			}
		}
		return result;
	}

}
