package xyz.msws.supergive.items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class PotionAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.contains(":"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof PotionMeta))
			return item;
		PotionMeta potion = (PotionMeta) meta;
		PotionEffectType type = Utils.getPotionEffect(line.split(":")[0]);

		if (type == null)
			return item;
		try {
			PotionEffect eff = new PotionEffect(type, Integer.parseInt(line.split(":")[1]),
					line.split(":").length > 2 ? Integer.parseInt(line.split(":")[2]) : 1);
			potion.addCustomEffect(eff, true);
		} catch (IndexOutOfBoundsException | NumberFormatException e) {
			MSG.warn("Potentially invalid potion format: " + line);
		}
		item.setItemMeta(potion);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof PotionMeta))
			return "";
		PotionMeta potion = (PotionMeta) meta;
		StringBuilder result = new StringBuilder();
		for (PotionEffect effect : potion.getCustomEffects()) {
			result.append(effect.getType().getName()).append(":").append(effect.getDuration()).append(":")
					.append(effect.getAmplifier()).append(" ");
		}
		return result.toString().trim();
	}

}
