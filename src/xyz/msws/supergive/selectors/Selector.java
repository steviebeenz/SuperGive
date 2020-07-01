package xyz.msws.supergive.selectors;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public interface Selector {
	public default List<Entity> getEntities(String arg) {
		return getEntities(arg, null);
	}

	public List<Entity> getEntities(String arg, CommandSender sender);

}
