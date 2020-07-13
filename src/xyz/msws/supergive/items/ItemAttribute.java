package xyz.msws.supergive.items;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author imodm
 *
 */
public interface ItemAttribute {
	/**
	 * Modifies the itemstack given the string.
	 * 
	 * @param line
	 * @param item
	 * @return
	 */
	ItemStack modify(String line, ItemStack item);

	/**
	 * Returns a corresponding string that would represent the current modification
	 * of an itemstack.
	 * 
	 * @param item
	 * @return
	 */
	String getModification(ItemStack item);
	
	String humanReadable(ItemStack item);

	/**
	 * Returns the permission needed to use this attribute.
	 * 
	 * @return
	 */
	String getPermission();

	/**
	 * Get tab completion possibilities from a string.
	 * 
	 * @param current
	 * @param args
	 * @param sender
	 * @return
	 */
	List<String> tabComplete(String current, String[] args, CommandSender sender);
}