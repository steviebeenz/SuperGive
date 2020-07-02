package xyz.msws.supergive.modules.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.msws.supergive.SuperGive;
import xyz.msws.supergive.utils.MSG;

public class GenerateCommand extends BukkitCommand {

	private SuperGive plugin;

	protected GenerateCommand(SuperGive plugin, String name) {
		super(name);
		this.plugin = plugin;
		this.setPermission("supergive.command.generate");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!testPermission(sender))
			return true;
		if (!(sender instanceof Player)) {
			MSG.tell(sender, "SuperGive", "You must be a player to use this command.");
			return true;
		}
		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || item.getType() == Material.AIR) {
			MSG.tell(sender, "SuperGive", "You must have an item in your hand to generate the command for it.");
			return true;
		}
		String str = "&7/give @self &e" + plugin.getBuilder().toString(item);

		MSG.tell(sender, "SuperGive", "The item in your hand can be generated with the following line:");
		BaseComponent[] result = new ComponentBuilder(MSG.color(str))
				.event(new ClickEvent(Action.SUGGEST_COMMAND, ChatColor.stripColor(MSG.color(str))))
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						TextComponent.fromLegacyText(MSG.color("&7Click to prepare command"))))
				.create();
		player.spigot().sendMessage(result);
		return true;
	}

}
