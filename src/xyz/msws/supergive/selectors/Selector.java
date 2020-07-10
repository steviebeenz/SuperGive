package xyz.msws.supergive.selectors;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

/**
 * A selector should convert a string into a list of entities that match the
 * string.
 * 
 * @author imodm
 *
 */
public interface Selector {
	default List<Entity> getEntities(String arg) {
		return getEntities(arg, null);
	}

	List<Entity> getEntities(String arg, CommandSender sender);

	String getDescriptor(String arg, CommandSender sender);

	/**
	 * 
	 * @param current
	 * @return
	 */
	List<String> tabComplete(String current);

}
