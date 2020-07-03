package xyz.msws.supergive.modules.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;

import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.items.ItemBuilder;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.selectors.Selector;
import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Sounds;
import xyz.msws.supergive.utils.Utils;

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
	private SuperGive plugin;

	protected GiveCommand(SuperGive plugin, String name) {
		super(name);
		this.plugin = plugin;
		this.setPermission("supergive.command.give");
		this.setAliases(Arrays.asList("g", "sg", "supergive"));
		this.setUsage("<command> [target] [item] <args>");
		this.setDescription("An advanced and greatly improved give command");
		selector = plugin.getSelector();
		builder = plugin.getBuilder();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender))
			return true;
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

		if (targets.isEmpty()) {
			MSG.tell(sender, "SuperGive", "No entities matched the specified criteria.");
			return true;
		}

		ItemStack item = builder.build(String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length)));

		Loadout loadout = null;
		switch (args[1].toLowerCase()) {
			case "@hand":
				if (!sender.hasPermission("supergive.command.give.hand")) {
					MSG.tell(sender, "SuperGive", "You do not have the proper permission to give this item.");
					return true;
				}
				if (sender instanceof Player) {
					loadout = new Loadout(((Player) sender).getInventory().getItemInMainHand());
					item = ((Player) sender).getInventory().getItemInMainHand();
				}
				break;
			case "@inventory":
				if (!sender.hasPermission("supergive.command.give.inventory")) {
					MSG.tell(sender, "SuperGive", "You do not have the proper permission to give this loadout.");
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "You have too many items in your non-existent inventory.");
					return true;
				}
				loadout = new Loadout(((Player) sender).getInventory().getContents());
				break;
			case "@block":
				if (!sender.hasPermission("supergive.command.give.block")) {
					MSG.tell(sender, "SuperGive", "You do not have the proper permission to give this loadout.");
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "Huh... not sure what you want me to do here.");
					return true;
				}
				RayTraceResult result = ((Player) sender).rayTraceBlocks(20);
				if (result == null || result.getHitBlock() == null) {
					MSG.tell(sender, "SuperGive", "Please look at a block that can hold items.");
					return true;
				}
				Block target = result.getHitBlock();
				if (!(target.getState() instanceof Container)) {
					MSG.tell(sender, "SuperGive",
							"&e" + MSG.camelCase(target.getType().toString()) + "s&7 cannot hold items.");
					return true;
				}

				loadout = new Loadout(((Container) target.getState()).getInventory().getContents());
				break;
			default:
				if (args[1].startsWith("#")) {
					loadout = plugin.getModule(LoadoutManager.class).matchLoadout(args[1].substring(1));
					if (loadout == null) {
						MSG.tell(sender, "SuperGive", "Unknown loadout specified.");
						return true;
					}
					if (!sender.hasPermission("supergive.command.give.loadout." + args[1].substring(1))) {
						MSG.tell(sender, "SuperGive", "You do not have the proper permissions to give that loadout.");
						return true;
					}
					break;
				}
				if (item == null) {
					MSG.tell(sender, "SuperGive", "Unknown item specified.");
					return true;
				}
				loadout = new Loadout(item);
		}

		if (loadout == null) {
			MSG.tell(sender, "SuperGive", "Unable to parse item.");
			return true;
		}

		if (item != null && item.getType() == Material.AIR) {
			MSG.tell(sender, "SuperGive", "You successfully did nothing.");
			return true;
		}

		if (item != null) {
			if (!sender.hasPermission("supergive.command.give." + MSG.normalize(item.getType().toString()))) {
				MSG.tell(sender, "SuperGive", "You do not have the proper permission to give this item.");
				return true;
			}
			if (!sender.hasPermission("supergive.command.give.unsafeenchants")
					&& Utils.containsUnsafeEnchantments(item)) {
				MSG.tell(sender, "SuperGive", "You do not have the proper permission to give this item.");
				return true;
			}
		}

		for (Entity ent : targets) {
			if (ent instanceof Player)
				((Player) ent).playSound(((Player) ent).getLocation(), Sounds.ITEM_PICKUP.bukkitSound(), 2, 1);
			if (ent instanceof CommandSender)
				MSG.tell(((CommandSender) ent), "SuperGive", MSG.STAFF + sender.getName() + " " + MSG.DEFAULT
						+ "has given you " + MSG.FORMAT_INFO + loadout.humanReadable() + MSG.DEFAULT + ".");
			loadout.give(ent);
		}

		MSG.tell(sender, "SuperGive", "Successfully gave " + MSG.SUBJECT + selector.getDescriptor(args[0], sender)
				+ MSG.FORMAT_INFO + " " + loadout.humanReadable() + MSG.DEFAULT + ".");
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

			String current = args[0].split(",")[args[0].split(",").length - 1];
			String prev = "";
			if (args[0].split(",").length > 1) {
				prev = String.join(",", args).substring(0, String.join(",", args).lastIndexOf(",") + 1);
			}

			if (current.startsWith("@"))
				for (String s : new String[] { "players", "everyone", "me", "world", "worldplayers", "all", "survival",
						"creative", "adventure", "spectator", "worldsurvival", "worldcreative", "worldadventure",
						"worldspectator" }) {
					if (("@" + s).toLowerCase().startsWith(current.toLowerCase()))
						result.add(prev + "@" + s);
				}

			for (String s : new String[] { "radius:", "world:", "perm:", "@" }) {
				if (s.toLowerCase().startsWith(current.toLowerCase()))
					result.add(prev + s);
			}
			if (current.length() > 3) {
				for (EntityType type : EntityType.values()) {
					String t = MSG.normalize(type.toString());
					if (("@" + t).startsWith(current)) {
						if (sender instanceof Player) {
							if (((Player) sender).getWorld().getEntities().stream()
									.anyMatch(e -> e.getType() == type && e instanceof InventoryHolder
											|| e instanceof LivingEntity)) {
								result.add(prev + "@" + t);
							}
						} else {
							result.add(prev + "@" + t);
						}
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
			for (String s : new String[] { "@hand", "@inventory", "@block" }) {
				if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
					result.add(s);
				}
			}
			if ("#".toLowerCase().startsWith(args[1].toLowerCase()))
				result.add("#");
			if (args[1].startsWith("#")) {
				for (String s : plugin.getModule(LoadoutManager.class).getLoadouts()) {
					if (!sender.hasPermission("supergive.command.give.loadout." + s))
						continue;
					if (("#" + s).toLowerCase().startsWith(args[1].toLowerCase()))
						result.add("#" + s);
				}
			}
		}
		if (args.length > 2) {
			for (String res : new String[] { "name:", "lore:", "unbreakable:", "damage:", "flag:" }) {
				boolean cont = true;
				for (String arg : args) {
					if (arg.toLowerCase().contains(res)) {
						cont = false;
						break;
					}
				}
				if (!cont)
					continue;
				if (res.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
					result.add(res);
			}
			if (MSG.normalize(args[1]).equalsIgnoreCase("enchantedbook")) {
				if ("stored:".startsWith(args[args.length - 1]))
					result.add("stored:");
				if (args[args.length - 1].startsWith("stored:")) {
					for (Enchantment ench : Enchantment.values()) {
						if (("stored:" + MSG.normalize(ench.getKey().getKey()))
								.startsWith(MSG.normalize(args[args.length - 1])))
							result.add("stored:" + MSG.normalize(ench.getKey().getKey()) + ":");
					}
				}
			}
			if (MSG.normalize(args[1]).equalsIgnoreCase("playerhead")) {
				boolean cont = true;
				for (String arg : args) {
					if (arg.toLowerCase().contains("owner:")) {
						cont = false;
						break;
					}
				}
				if (cont) {
					if ("owner:".startsWith(args[args.length - 1].toLowerCase()))
						result.add("owner:");
				}
			}
			if (args[args.length - 1].toLowerCase().startsWith("flag:")) {
				for (ItemFlag flag : ItemFlag.values()) {
					String fs = MSG.normalize(flag.toString());
					if (MSG.normalize(("flag:" + fs)).toLowerCase().startsWith(MSG.normalize(args[args.length - 1]))) {
						result.add("flag:" + fs);
					}
				}
			}
			if (args[args.length - 1].length() >= 3) {
				for (Enchantment ench : Enchantment.values()) {
					if (MSG.normalize(ench.getKey().getKey()).startsWith(MSG.normalize(args[args.length - 1])))
						result.add(MSG.normalize(ench.getKey().getKey()) + ":");
				}
				if (args[1].toLowerCase().contains("potion"))
					for (PotionEffectType type : PotionEffectType.values()) {
						if (MSG.normalize(type.getName()).startsWith(MSG.normalize(args[args.length - 1])))
							result.add(MSG.normalize(type.getName()) + ":");
					}
			}
			if (args[1].toLowerCase().contains("banner")) {
				for (PatternType type : PatternType.values()) {
					if (type.toString().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
						result.add(type.toString().toLowerCase() + ":");
					}
				}
				if (args[args.length - 1].contains(":")) {
					String current = args[args.length - 1].split(":").length > 1 ? (args[args.length - 1]).split(":")[1]
							: "";
					String prev = args[args.length - 1].split(":")[0] + ":";

					for (DyeColor color : DyeColor.values()) {
						if (color.toString().toLowerCase().startsWith(current.toLowerCase()))
							result.add(prev + color.toString().toLowerCase());
					}
				}
			}
			if (args[1].toLowerCase().contains("spawner")) {
				boolean cont = true;
				for (String arg : args) {
					if (arg.toLowerCase().contains("spawner:")) {
						cont = false;
						break;
					}
				}
				if (cont) {
					if ("spawner:".startsWith(args[args.length - 1].toLowerCase()))
						result.add("spawner:");
				}
				if (args[args.length - 1].toLowerCase().startsWith("spawner:"))
					for (EntityType type : EntityType.values()) {
						if (("spawner:" + MSG.normalize(type.toString())).startsWith(args[args.length - 1]))
							result.add("spawner:" + MSG.normalize(type.toString()));
					}
			}

		}
		return result;
	}

}
