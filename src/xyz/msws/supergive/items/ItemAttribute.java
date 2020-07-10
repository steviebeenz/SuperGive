package xyz.msws.supergive.items;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public interface ItemAttribute {
	ItemStack modify(String line, ItemStack item);

	String getModification(ItemStack item);

	List<String> tabComplete(String current, String[] args, CommandSender sender);
}