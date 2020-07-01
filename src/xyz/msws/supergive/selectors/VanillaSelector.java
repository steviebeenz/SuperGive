package xyz.msws.supergive.selectors;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class VanillaSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		try {
			return Bukkit.selectEntities(sender, arg);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

}
