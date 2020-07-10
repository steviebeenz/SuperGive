package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class ItemFlagAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("flag:"))
			return item;
		ItemFlag flag = Utils.getItemFlag(line.substring("flag:".length()));
		if (flag == null)
			return item;
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(flag);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return null;
		ItemMeta meta = item.getItemMeta();
		StringBuilder result = new StringBuilder();
		for (ItemFlag flag : meta.getItemFlags()) {
			result.append("flag:").append(MSG.normalize(flag.toString())).append(" ");
		}
		return result.toString().trim();
	}

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (!current.toLowerCase().startsWith("flag:")) {
			if ("flag:".startsWith(current.toLowerCase()))
				return Arrays.asList("flag:");
			return null;
		}
		List<String> result = new ArrayList<>();

		for (ItemFlag flag : ItemFlag.values()) {
			String fs = MSG.normalize(flag.toString());
			if (MSG.normalize(("flag:" + fs)).toLowerCase().startsWith(MSG.normalize(current))) {
				result.add("flag:" + fs);
			}
		}

		return result;
	}

}
