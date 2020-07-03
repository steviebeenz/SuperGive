package xyz.msws.supergive.items;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import xyz.msws.supergive.utils.MSG;

public class ItemBuilder {

	private List<ItemAttribute> attr = new ArrayList<>();

	public ItemBuilder() {
		attr.add(new NameAttribute());
		attr.add(new UnbreakableAttribute());
		attr.add(new EnchantmentAttribute());
		attr.add(new DamageAttribute());
		attr.add(new OwnerAttribute());
		attr.add(new LoreAttribute());
		attr.add(new ItemFlagAttribute());
		attr.add(new PotionAttribute());
		attr.add(new PatternAttribute());
		attr.add(new StoredEnchantmentAttribute());
		attr.add(new EntityAttribute());
		attr.add(new FireworkAttribute());
	}

	public void addAttribute(ItemAttribute attr) {
		this.attr.add(attr);
	}

	public ItemStack build(String args) {
		ItemStack base = null;
		Material mat = null;

		String matName = args.split(" ")[0];
		int amo = 1;

		List<String> attribuites = new ArrayList<>();

		boolean amoSpecified = args.split(" ").length >= 2 && StringUtils.isNumeric(args.split(" ")[1]);

		if (amoSpecified) {
			amo = Integer.parseInt(args.split(" ")[1]);
		}

		String last = "";
		for (String arg : (String[]) ArrayUtils.subarray(args.split(" "), amoSpecified ? 2 : 1,
				args.split(" ").length)) {
			if (!last.isEmpty() && arg.contains(":")) {
				attribuites.add(last.trim());
				last = arg + " ";
				continue;
			}
			last += arg + " ";
		}
		if (last.contains(":"))
			attribuites.add(last.trim());

		// Reverse order
		for (Material m : Material.values()) { // Check incomplete middles
			if (MSG.normalize(m.toString()).contains(MSG.normalize(matName))) {
				mat = m;
				break;
			}
		}
		for (Material m : Material.values()) { // Check incomplete beginnings
			if (MSG.normalize(m.toString()).startsWith(MSG.normalize(matName))) {
				mat = m;
				break;
			}
		}
		for (Material m : Material.values()) { // Check exact name first
			if (m.toString().replace("_", "").equalsIgnoreCase(matName.replace("_", ""))) {
				mat = m;
				break;
			}
		}

		if (mat == null)
			return null;

		base = new ItemStack(mat, amo);

		for (ItemAttribute at : attr)
			for (String s : attribuites)
				base = at.modify(s, base);

		return base;
	}

	public String toString(ItemStack item) {
		StringBuilder result = new StringBuilder();
		for (ItemAttribute at : attr) {
			String mod = at.getModification(item);
			if (mod == null || mod.isEmpty())
				continue;
			result.append(mod).append(" ");
		}
		return (MSG.normalize(item.getType().toString()) + " " + item.getAmount() + " " + result.toString()).trim();
	}

	public String humanReadable(ItemStack item) {
		StringBuilder result = new StringBuilder();
		if (item == null || item.getType() == Material.AIR)
			return null;
		result.append(MSG.NUMBER)
				.append(item.getAmount() == 1 ? (isVowel(item.getType().toString().charAt(0)) ? "an " : "a ")
						: item.getAmount() + " ")
				.append(MSG.FORMAT_INFO);
		result.append(MSG.camelCase(item.getType().toString()))
				.append((item.getAmount() == 1 || item.getType().toString().toLowerCase().endsWith("s")) ? " " : "s ");
		for (ItemAttribute at : attr) {
			String mod = at.getModification(item);
			if (mod == null || mod.isEmpty())
				continue;
			result.append(mod).append(" ");
		}
		return result.toString().trim();
	}

	private boolean isVowel(char c) {
		for (char v : new char[] { 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U' }) {
			if (v == c)
				return true;
		}
		return false;
	}

	@Test
	public void testItemMaterial() {
		assertTrue(build("diamond").getType() == Material.DIAMOND);
	}

	@Test
	public void testMaterialBeginning() {
		assertTrue(build("diamond_chest").getType() == Material.DIAMOND_CHESTPLATE);
	}

	@Test
	public void testMaterialMiddle() {
		assertTrue(build("amond").getType() == Material.DIAMOND);
	}

	@Test
	public void testItemAmount() {
		assertTrue(build("stone 64").getAmount() == 64);
	}

	@Test
	public void testNull() {
		assertTrue(build("invaliditem") == null);
	}

	@Test
	public void testInvalidAmount() {
		assertTrue(build("stone a").getAmount() == 1);
	}
}
