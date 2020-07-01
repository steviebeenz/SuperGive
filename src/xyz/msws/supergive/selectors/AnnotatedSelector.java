package xyz.msws.supergive.selectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import xyz.msws.supergive.utils.MSG;
import xyz.msws.supergive.utils.Utils;

public class AnnotatedSelector implements Selector {

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		if (!arg.startsWith("@"))
			return null;
		String key = MSG.normalize(arg.substring(1));
		List<Entity> result = new ArrayList<>();

		switch (key) {
			case "world":
				if (!(sender instanceof Player))
					return null;
				return ((Player) sender).getWorld().getEntities();
			case "worldplayers":
				if (!(sender instanceof Player))
					return null;
				return ((Player) sender).getWorld().getEntities().stream().filter(e -> e.getType() == EntityType.PLAYER)
						.collect(Collectors.toList());
			case "all":
			case "everyone":
				for (World w : Bukkit.getWorlds()) {
					result.addAll(w.getEntities());
				}
				return result;
			case "allplayers":
				return new ArrayList<>(Bukkit.getOnlinePlayers());
			case "me":
				if (!(sender instanceof Entity))
					return null;
				return Arrays.asList(((Entity) sender));
			default:
				if (key.startsWith("world")) {
					// Sender can specify world-limited targets
					if (!(sender instanceof Player))
						return null;
					World w = ((Player) sender).getWorld();
					GameMode mode = Utils.getGameMode(key.substring("world".length()));
					if (mode != null) {
						return w.getPlayers().stream().filter(p -> p.getGameMode() == mode)
								.collect(Collectors.toList());
					}
					EntityType type = Utils.getEntityType(key.substring("world".length()));
					if (type != null)
						return w.getEntities().stream().filter(e -> e.getType() == type).collect(Collectors.toList());
				}
				GameMode mode = Utils.getGameMode(key);
				if (mode != null)
					return Bukkit.getOnlinePlayers().stream().filter(p -> p.getGameMode() == mode)
							.collect(Collectors.toList());

				EntityType type = Utils.getEntityType(key);
				if (type != null) {
					for (World w : Bukkit.getWorlds())
						result.addAll(
								w.getEntities().stream().filter(e -> e.getType() == type).collect(Collectors.toList()));

					return result;
				}
		}
		return null;
	}

}
