package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.utils.CColor;

public class FireworkAttribute implements ItemAttribute {

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.startsWith("firework:"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof FireworkMeta))
			return item;
		FireworkMeta fire = (FireworkMeta) meta;

		String cString = line.substring("firework:".length()).split(",")[0],
				fString = line.substring("firework:".length()).split(",").length > 1 ? line.split(",")[1] : null;

		List<Color> colors = new ArrayList<>();
		for (String c : cString.split(" ")) {
			Color col = getColor(c);
			if (col != null)
				colors.add(getColor(c));
		}

		FireworkEffect.Builder effect = FireworkEffect.builder().withColor(colors);
		if (fString != null) {
			colors = new ArrayList<>();
			for (String c : fString.split(" ")) {
				Color col = getColor(c);
				if (col != null)
					colors.add(getColor(c));
			}
			effect.withFade(colors);
		}

		for (String s : line.split(",")) {
			switch (s.toLowerCase()) {
				case "flicker":
					effect.withFlicker();
					break;
				case "trail":
					effect.withTrail();
					break;
				default:
					if (s.startsWith("power")) {
						try {
							fire.setPower(Integer.parseInt(s.substring("power".length()).trim()));
						} catch (NumberFormatException e) {
						}
					}
					try {
						Type type = Type.valueOf(s.toUpperCase());
						effect.with(type);
					} catch (IllegalArgumentException e) {
					}
					break;
			}
		}

		fire.addEffect(effect.build());
		item.setItemMeta(fire);
		return item;
	}

	private Color getColor(String line) {
		if (!line.contains("|")) {
			try {
				CColor custom = null;
				custom = CColor.valueOf(line.toUpperCase());
				return custom.bukkit();
			} catch (IllegalArgumentException e) {
			}

		}
		try {
			String rs = line.split("\\|")[0];
			String gs = line.split("\\|")[1];
			String bs = line.split("\\|")[2];
			return Color.fromRGB(Integer.parseInt(rs), Integer.parseInt(gs), Integer.parseInt(bs));
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return null;
		}

	}

	private String colorToString(Color c) {
		CColor cc = CColor.fromBukkit(c);
		if (cc != null)
			return cc.toString();
		return c.getRed() + "|" + c.getGreen() + "|" + c.getBlue();
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof FireworkMeta))
			return null;
		FireworkMeta fire = (FireworkMeta) meta;
		StringBuilder result = new StringBuilder("firework:");
		for (FireworkEffect effect : fire.getEffects()) {
			StringBuilder eff = new StringBuilder(String.join(" ",
					effect.getColors().stream().map(c -> colorToString(c)).collect(Collectors.toList())));
			result.append(eff).append(",");
			if (!effect.getFadeColors().isEmpty()) {
				result.append(String.join(" ",
						effect.getFadeColors().stream().map(c -> colorToString(c)).collect(Collectors.toList())))
						.append(",");
			}
			result.append(effect.getType() + ",");
			if (effect.hasFlicker())
				result.append("flicker,");
			if (effect.hasTrail())
				result.append("trail,");
		}
		result.append("power").append(fire.getPower());

		return result.toString();
	}

}
