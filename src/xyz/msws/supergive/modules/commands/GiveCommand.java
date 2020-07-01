package xyz.msws.supergive.modules.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.items.ItemBuilder;
import xyz.msws.supergive.selectors.Selector;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Sounds;

/**
 * Provides a much better integration of the /give command for Minecraft.
 * Possibles usages are:
 * 
 * give MSWS diamondsword <br>
 * give @all dirt give radius:15 stone 5 give <br>
 * give @e[type=skeleton] bow power:5 <br>
 * give perm:rank.admin diamondsword name:&cEnchanted Sword <br>
 * give MSWS @hand <br>
 * give @world stone 64 <br>
 * give MSWS bow unbreakable <br>
 * give MSWS skull owner:Notch protection:3 <br>
 * 
 * @author imodm
 *
 */
public class GiveCommand extends BukkitCommand {

	private Selector selector;
	private ItemBuilder builder;

	protected GiveCommand(SuperGive plugin, String name) {
		super(name);
		this.setPermission("supergive.command.give");
		this.setAliases(Arrays.asList("g", "sg", "supergive"));
		selector = plugin.getSelector();
		builder = plugin.getBuilder();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length < 2) {
			MSG.tell(sender, "SuperGive",
					args.length == 0 ? "You must specify a &etarget &7and &aitem&7." : "You must specify an &aitem&7.");
			return true;
		}

		List<Entity> targets = selector.getEntities(args[0], sender);
		if (targets == null) {
			MSG.tell(sender, "SuperGive",
					"An invalid target was specified, please ensure you have typed it correctly.");
			return true;
		}

		List<InventoryHolder> holders = targets.stream().filter(e -> e instanceof InventoryHolder)
				.map(e -> (InventoryHolder) e).collect(Collectors.toList());

		int skip = holders.size() - targets.size();

		if (holders.isEmpty()) {
			MSG.tell(sender, "SuperGive",
					"Unable to find any valid entities that matched the specified critera, please retry or widen your selection.");
			return true;
		}

		ItemStack item = builder.build(String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length)));

		List<ItemStack> items = new ArrayList<>();
		switch (args[1].toLowerCase()) {
			case "@hand":
				if (sender instanceof Player)
					items = Arrays.asList(((Player) sender).getInventory().getItemInMainHand());
				break;
			case "@inventory":
				items = Arrays.asList(((Player) sender).getInventory().getContents());
				break;
			default:
				if (item == null) {
					MSG.tell(sender, "SuperGive", "Unknown item specified.");
				}
				items = Arrays.asList(item);
		}

		for (InventoryHolder ent : holders) {
			InventoryHolder holder = (InventoryHolder) ent;
			if (holder instanceof Player)
				((Player) holder).playSound(((Player) holder).getLocation(), Sounds.ITEM_PICKUP.bukkitSound(), 2, 1);
			holder.getInventory().addItem(items.toArray(new ItemStack[0]));
		}

		MSG.tell(sender, "SuperGive", "Successfully gave " + MSG.NUMBER + holders.size() + MSG.DEFAULT + " "
				+ (holders.size() == 1 ? "target" : "targets") + " items.");
		if (skip > 0)
			MSG.tell(sender, "SuperGive", "&eWarning: " + MSG.NUMBER + skip + " " + MSG.DEFAULT
					+ (skip == 1 ? "entity" : "entities") + " were invalid targets.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();
		if (!(sender.hasPermission(this.getPermission())))
			return result;
		if (args.length == 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(p.getName());
			}
			for (String s : new String[] { "all", "everyone", "me", "world", "worldplayers", "allplayers", "survival",
					"creative", "adventure", "spectator", "worldsurvival", "worldcreative", "worldadventure",
					"worldspectator" }) {
				if (("@" + s).toLowerCase().startsWith(args[0].toLowerCase()))
					result.add("@" + s);
			}
			if (args[0].length() > 3)
				for (EntityType type : EntityType.values()) {
					String t = MSG.normalize(type.toString());
					if (("@" + t).startsWith(args[0])) {
						if (sender instanceof Player) {
							if (((Player) sender).getWorld().getEntities().stream()
									.anyMatch(e -> e.getType() == type && e instanceof InventoryHolder)) {
								result.add("@" + t);
							}
						} else {
							result.add("@" + t);
						}
					}
				}
		}
		if (args.length == 2) {
			for (Material mat : Material.values()) {
				if (MSG.normalize(mat.getKey().getKey()).startsWith(args[1].toLowerCase())) {
					result.add(MSG.normalize(mat.getKey().getKey()));
				}
			}
		}
		if (args.length > 2) {
			for (String res : new String[] { "name:", "lore:", "unbreakable:" }) {
				if (res.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(res);
			}
			if (args[args.length - 1].length() >= 3)
				for (Enchantment ench : Enchantment.values()) {
					if (MSG.normalize(ench.getKey().getKey()).startsWith(MSG.normalize(args[args.length - 1])))
						result.add(MSG.normalize(ench.getKey().getKey() + ":"));
				}
		}
		return result;
	}

}
