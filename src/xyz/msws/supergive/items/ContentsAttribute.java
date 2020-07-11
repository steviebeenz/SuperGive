package xyz.msws.supergive.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.utils.MSG;

public class ContentsAttribute implements ItemAttribute {

	private SuperGive plugin;

	public ContentsAttribute(SuperGive plugin) {
		this.plugin = plugin;
	}

	@Override
	public ItemStack modify(String line, ItemStack item) {
		if (!line.toLowerCase().startsWith("contents:#"))
			return item;
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof BlockStateMeta))
			return item;
		BlockState bs = ((BlockStateMeta) meta).getBlockState();
		if (!(bs instanceof Container))
			return item;
		Container container = (Container) bs;

		Loadout ld = plugin.getModule(LoadoutManager.class)
				.getLoadout(line.substring("contents:#".length()).replace(" ", ""));
		if (ld == null) {
			MSG.warn("Unknown loadout: " + line.substring("contents:#".length()).replace(" ", ""));
			return item;
		}
		container.getInventory().setContents(ld.getItems());

		((BlockStateMeta) meta).setBlockState(container);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getModification(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (!(meta instanceof BlockStateMeta))
			return null;
		BlockState bs = ((BlockStateMeta) meta).getBlockState();
		if (!(bs instanceof Container))
			return null;
		Container container = (Container) bs;
		for (Loadout load : plugin.getModule(LoadoutManager.class).getLoadouts()) {
			if (load.getName() == null)
				continue;
			ItemStack[] cont = container.getInventory().getContents();

			boolean loop = false;

			for (int i = 0; i < Math.max(load.getItems().length, cont.length); i++) {
				ItemStack a = load.getItems().length > i ? load.getItems()[i] : null;
				ItemStack b = cont.length > i ? cont[i] : null;
				if (a == b)
					continue;
				if (a == null && b != null) {
					loop = true;
					continue;
				}
				if (a != null && b == null) {
					loop = true;
					continue;
				}
				if (!a.equals(b)) {
					loop = true;
					continue;
				}
			}
			if (loop)
				continue;
			return "contents:#" + load.getName();
		}
		return null;
	}

	@Override
	public List<String> tabComplete(String current, String[] args, CommandSender sender) {
		if (args.length < 2)
			return null;
		if (!current.toLowerCase().startsWith("contents:#")) {
			if ("contents:#".startsWith(current.toLowerCase()))
				return Arrays.asList("contents:#");
			return null;
		}
		List<String> result = new ArrayList<>();
		Set<String> loads = plugin.getModule(LoadoutManager.class).getLoadoutNames();
		for (String load : loads) {
			if (("contents:#" + load.toLowerCase()).startsWith(current.toLowerCase()))
				result.add("contents:#" + load);
		}
		return result;
	}

}
