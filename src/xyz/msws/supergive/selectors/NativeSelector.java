package xyz.msws.supergive.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

/**
 * Combines all current selectors into one
 * 
 * @author imodm
 *
 */
public class NativeSelector implements Selector {

	private List<Selector> selectors = new ArrayList<>(); // List to keep order

	public NativeSelector() {
		selectors.add(new VanillaSelector());
		selectors.add(new AnnotatedSelector());
		selectors.add(new PermissionSelector());
		selectors.add(new NameSelector());
		selectors.add(new RadiusSelector());
		selectors.add(new WorldSelector());
	}

	@Override
	/**
	 * First selector should be widest to smallest
	 */
	public List<Entity> getEntities(String arg, CommandSender sender) {
		List<Entity> result = new ArrayList<>();
		for (String string : arg.split(",")) {
			for (Selector sel : selectors) {
				List<Entity> res = sel.getEntities(string, sender);
				if (res == null || res.isEmpty())
					continue;
				if (result.isEmpty()) {
					result = res;
				} else {
					// Filter entities that aren't included
					result = result.stream().filter(re -> res.contains(re)).collect(Collectors.toList());
				}
			}
		}
		return result;
	}

	public void addSelector(Selector sel) {
		selectors.add(sel);
	}

	public List<Selector> getSelectors() {
		return selectors;
	}

	@Override
	public String getDescriptor(String arg, CommandSender sender) {
		StringBuilder msg = new StringBuilder();
		List<Entity> result = new ArrayList<>();
		for (String string : arg.split(",")) {
			for (Selector sel : selectors) {
				List<Entity> res = sel.getEntities(string, sender);
				if (res == null || res.isEmpty())
					continue;
				if (result.isEmpty()) {
					result = res;
					msg.append(sel.getDescriptor(string, sender));
				} else {
					// Filter entities that aren't included
					result = result.stream().filter(re -> res.contains(re)).collect(Collectors.toList());
					msg.append(" that are ").append(sel.getDescriptor(string, sender));
				}
			}
		}
		return msg.toString();
	}

	@Override
	public List<String> tabComplete(String current) {
		List<String> result = new ArrayList<>();
		for (String string : current.split(",")) {
			for (Selector sel : selectors) {
				List<String> r = sel.tabComplete(string);
				if (r == null || r.isEmpty())
					continue;
				result.addAll(r);
			}
		}
		return result;
	}

}
