package xyz.msws.supergive.selectors;

import java.util.List;

import javax.annotation.Nullable;

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

	List<Entity> getEntities(String arg, @Nullable CommandSender sender);

	String getDescriptor(String arg, @Nullable CommandSender sender);

	/**
	 * Note: not the whole string is provided, a substring of the characters after
	 * the last , is provided
	 * 
	 * @param current
	 * @return
	 */
	List<String> tabComplete(String current);

}
