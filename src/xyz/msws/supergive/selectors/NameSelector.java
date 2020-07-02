package xyz.msws.supergive.selectors;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NameSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		for (Player p : Bukkit.getOnlinePlayers()) { // Avoid clashing with VanillaSelector
			if (p.getName().equalsIgnoreCase(arg))
				return null;
		}

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().toLowerCase().startsWith(arg.toLowerCase()))
				return Arrays.asList(p);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().toLowerCase().contains(arg.toLowerCase()))
				return Arrays.asList(p);
		}
		return null;
	}

	@Override
	public String getDescriptor(String arg, CommandSender sender) {
		return getEntities(arg, sender).get(0).getName();
	}

}
