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
	public default List<Entity> getEntities(String arg) {
		return getEntities(arg, null);
	}

	public List<Entity> getEntities(String arg, @Nullable CommandSender sender);

	public String getDescriptor(String arg, @Nullable CommandSender sender);
}
