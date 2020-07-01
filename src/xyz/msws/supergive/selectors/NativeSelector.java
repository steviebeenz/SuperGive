package xyz.msws.supergive.selectors;

import java.util.ArrayList;
import java.util.List;

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
		selectors.add(new NameSelector());
	}

	@Override
	public List<Entity> getEntities(String arg, CommandSender sender) {
		for (Selector sel : selectors) {
			List<Entity> result = sel.getEntities(arg, sender);
			if (result != null)
				return result;
		}
		return null;
	}

	public void addSelector(Selector sel) {
		selectors.add(sel);
	}

	public List<Selector> getSelectors() {
		return selectors;
	}

}
