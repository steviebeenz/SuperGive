package xyz.msws.supergive.modules.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.utils.MSG;

public class LoadoutCommand extends BukkitCommand {

	private SuperGive plugin;
	private LoadoutManager lm;

	public LoadoutCommand(SuperGive plugin, String name) {
		super(name);
		this.setAliases(Arrays.asList("ld"));
		this.setPermission("supergive.command.loadout");
		this.setDescription("Manage loadouts");
		this.setUsage("[create/delete/modify]");
		this.plugin = plugin;
		lm = plugin.getModule(LoadoutManager.class);
	}

	private Map<UUID, String> loadouts = new HashMap<>();
	private Map<UUID, ItemStack[]> items = new HashMap<>();

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender))
			return true;
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				MSG.tell(sender, "SuperGive", "Please specify a sub-command.");
				return true;
			}
			Player player = (Player) sender;

			if (lm.getLoadouts().isEmpty()) {
				MSG.tell(sender, "SuperGive", "There are no loadouts set yet, create one with &e/loadout create&7.");
				return true;
			}
			MSG.tell(sender, " ");
			MSG.tell(sender, "SuperGive", "Listing all loadouts available.");

			for (String load : lm.getLoadouts()) {
				Loadout l = lm.getLoadout(load);

				ComponentBuilder give = new ComponentBuilder();
				give = give.append("GIVE").color(ChatColor.GOLD).bold(true)
						.event(new ClickEvent(Action.RUN_COMMAND, "/give @me #" + load))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								TextComponent.fromLegacyText(MSG.color("&7Click to run &e/give &b@me &a#" + load))));
				give = give.append(" EDIT", FormatRetention.NONE).color(ChatColor.GREEN).bold(true)
						.event(new ClickEvent(Action.RUN_COMMAND, "/loadout edit " + load))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								TextComponent.fromLegacyText(MSG.color("&7Click to run &e/loadout edit " + load))));
				give = give.append(" ", FormatRetention.NONE).append("DELETE").color(ChatColor.RED).bold(true)
						.event(new ClickEvent(Action.RUN_COMMAND, "/loadout delete " + load))
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent
								.fromLegacyText(MSG.color("&7Click to run &e/loadout &cdelete &e" + load))));
				give = give.append(" - ", FormatRetention.NONE).color(ChatColor.GRAY).append(load, FormatRetention.NONE)
						.color(ChatColor.YELLOW).append(" (" + l.getItems().length + " items)", FormatRetention.NONE)
						.color(ChatColor.GRAY)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
								MSG.color(MSG.FORMAT_INFO + load + " contains:\n" + l.loreReadable()))));
				player.spigot().sendMessage(give.create());
			}
			return true;
		}
		Player player;
		String name;
		switch (args[0].toLowerCase()) {
			case "create":
				if (!sender.hasPermission("supergive.command.loadout.create")) {
					MSG.tell(sender, "SuperGive", "You do not have the valid permissions to create a loadout.");
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "You must be a player to create a loadout.");
					return true;
				}
				player = (Player) sender;
				if (args.length <= 1) {
					MSG.tell(sender, "SuperGive", "You must specify a name for the loadout.");
					return true;
				}

				name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));
				if (lm.getLoadout(MSG.normalize(name)) != null) {
					MSG.tell(sender, "SuperGive", "That loadout already exists.");
					return true;
				}

				Loadout loadout = new Loadout(player.getInventory().getContents());
				loadout.setName(name);
				lm.addLoadout(MSG.normalize(name), loadout);
				MSG.tell(sender, "SuperGive",
						"Successfully created the " + MSG.FORMAT_INFO + name + MSG.DEFAULT + " loadout:");
				for (ItemStack item : loadout.getItems()) {
					if (item == null || item.getType() == Material.AIR)
						continue;
					MSG.tell(sender, "&7- " + plugin.getBuilder().humanReadable(item));
				}
				return true;
			case "edit":
				if (!sender.hasPermission("supergive.command.loadout.edit")) {
					MSG.tell(sender, "SuperGive", "You do not have the valid permissions to edit any loadouts.");
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "You must be a player to edit a loadout.");
					return true;
				}
				player = (Player) sender;
				if (args.length <= 1) {
					if (loadouts.containsKey(player.getUniqueId())) {
						if (!items.containsKey(player.getUniqueId())) {
							MSG.tell(sender, "SuperGive",
									"A potential error occured when restoring your old items, please report this to MSWS if you see this message.");
						}
						// End editing of loadout
						Loadout newLoad = new Loadout(player.getInventory().getContents());
						newLoad.setName(lm.getLoadout(loadouts.get(player.getUniqueId())).getName());
						lm.addLoadout(loadouts.get(player.getUniqueId()), newLoad);
						player.getInventory().clear();
						player.getInventory().setContents(items.get(player.getUniqueId()));
						items.remove(player.getUniqueId());
						loadouts.remove(player.getUniqueId());
						return true;
					}
					MSG.tell(sender, "SuperGive", "You must specify a name for the loadout.");
					return true;
				}

				if (loadouts.containsKey(player.getUniqueId())) {
					MSG.tell(sender, "SuperGive", "You are already currently editing the " + MSG.FORMAT_INFO
							+ loadouts.get(player.getUniqueId()) + MSG.DEFAULT + " loadout.");
					return true;
				}

				name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

				Loadout load = lm.getLoadout(name);
				if (load == null) {
					MSG.tell(sender, "SuperGive", "Unknown loadout.");
					return true;
				}
				if (!sender.hasPermission("supergive.command.loadout.edit." + name)) {
					MSG.tell(sender, "SuperGive",
							"You do not have the valid permissions to edit the &e" + name + "&7 loadout.");
					return true;
				}

				items.put(player.getUniqueId(), player.getInventory().getContents());
				player.getInventory().clear();
				load.give(player);

				MSG.tell(sender, "SuperGive",
						"You are now editing the " + MSG.FORMAT_INFO + name + " " + MSG.DEFAULT + "loadout.");
				MSG.tell(sender, "SuperGive", "Type &e/" + commandLabel + " edit &7to save the loadout, or &e/"
						+ commandLabel + " cancel &7to cancel editing.");
				loadouts.put(player.getUniqueId(), name);
				break;
			case "delete":
				if (!sender.hasPermission("supergive.command.loadout.delete")) {
					MSG.tell(sender, "SuperGive", "You do not have the valid permissions to delete any loadouts.");
					return true;
				}
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "You must be a player to create a loadout.");
					return true;
				}
				player = (Player) sender;
				if (args.length <= 1) {
					MSG.tell(sender, "SuperGive", "You must specify a name for the loadout.");
					return true;
				}

				name = String.join(" ", (String[]) ArrayUtils.subarray(args, 1, args.length));

				if (lm.getLoadout(name) == null) {
					MSG.tell(sender, "SuperGive", "Unknown loadout name.");
					return true;
				}
				if (!sender.hasPermission("supergive.command.loadout.delete." + name)) {
					MSG.tell(sender, "SuperGive",
							"You do not have the valid permissions to delete the &e" + name + "&7loadout.");
					return true;
				}
				lm.deleteLoadout(name);
				MSG.tell(sender, "SuperGive",
						"Successfully deleted the " + MSG.FORMAT_INFO + name + " " + MSG.DEFAULT + "loadout.");
				break;
			case "cancel":
				if (!(sender instanceof Player)) {
					MSG.tell(sender, "SuperGive", "You are not editing a loadout (better not be >:c).");
					return true;
				}
				player = (Player) sender;
				if (!(loadouts.containsKey(player.getUniqueId()))) {
					MSG.tell(sender, "SuperGive", "You are not editing a loadout.");
					return true;
				}
				MSG.tell(sender, "SuperGive", "You cancelled and reverted all changes to " + MSG.FORMAT_INFO
						+ loadouts.get(player.getUniqueId()) + MSG.DEFAULT + ".");
				loadouts.remove(player.getUniqueId());
				player.getInventory().setContents(items.get(player.getUniqueId()));
				break;
		}
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();
		if (args.length == 1) {
			for (String s : new String[] { "create", "edit", "delete" }) {
				if (s.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(s);
			}
			if (sender instanceof Player && loadouts.containsKey(((Player) sender).getUniqueId())) {
				if ("cancel".startsWith(args[0].toLowerCase()))
					result.add("cancel");
			}
		}
		if (args.length == 2 && (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("delete"))) {
			for (String s : lm.getLoadouts()) {
				if (s.toLowerCase().startsWith(args[1].toLowerCase()))
					result.add(s);
			}
		}
		return result;
	}

}
