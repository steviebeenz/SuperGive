package xyz.msws.supergive.selectors;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class PermissionSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		if (!arg.startsWith("perm:"))
			return null;
		return Bukkit.getOnlinePlayers().stream().filter(p -> p.hasPermission(arg.substring("perm:".length())))
				.collect(Collectors.toList());
	}

}
