package xyz.msws.supergive.selectors;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class WorldSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		if (!sender.hasPermission("supergive.selector.world"))
			return null;
		if (!arg.toLowerCase().startsWith("world:"))
			return null;
		World world = Bukkit.getWorld(arg.substring("world:".length()));
		if (world == null)
			return null;
		if (!sender.hasPermission("supergive.selector.world." + world.getName()))
			return null;
		return world.getEntities();
	}

	@Override
	public String getDescriptor(String arg, CommandSender sender) {
		return "Entities in " + Bukkit.getWorld(arg.substring("world:".length())).getName();
	}

}
