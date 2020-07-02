package xyz.msws.supergive.items;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class PatternAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof BannerMeta))
			return item;
		BannerMeta banner = (BannerMeta) meta;
		PatternType type = null;
		DyeColor color = null;

		if (!line.contains(":"))
			return item;
		try {
			type = PatternType.valueOf(line.split(":")[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			return item;
		}
		if (type == null)
			return item;

		try {
			color = DyeColor.valueOf(line.split(":")[1].toUpperCase());
		} catch (IllegalArgumentException e) {
			return item;
		}

		Pattern pattern = new Pattern(color, type);
		banner.addPattern(pattern);
		item.setItemMeta(banner);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof BannerMeta))
			return null;
		BannerMeta banner = (BannerMeta) meta;
		StringBuilder result = new StringBuilder();
		for (Pattern patt : banner.getPatterns()) {
			result.append(patt.getPattern().toString().toLowerCase() + ":" + patt.getColor().toString().toLowerCase())
					.append(" ");
		}
		return result.toString().trim();
	}

}
