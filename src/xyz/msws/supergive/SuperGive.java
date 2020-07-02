package xyz.msws.supergive;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.msws.supergive.items.ItemBuilder;
import xyz.msws.supergive.loadout.Loadout;
import xyz.msws.supergive.loadout.LoadoutManager;
import xyz.msws.supergive.modules.AbstractModule;
import xyz.msws.supergive.modules.ModulePriority;
import xyz.msws.supergive.modules.commands.CommandModule;
import xyz.msws.supergive.selectors.NativeSelector;
import xyz.msws.supergive.selectors.Selector;

public class SuperGive extends JavaPlugin {
	private Set<AbstractModule> modules = new HashSet<>();

	private ItemBuilder builder;
	private Selector selector;

	private static SuperGive instance;

	@Override
	public void onEnable() {
		instance = this;
		File conf = new File(this.getDataFolder(), "config.yml");
		if (!conf.exists())
			saveResource("config.yml", false);

		modules.add(new CommandModule(this));
		modules.add(new LoadoutManager(this));

		this.builder = new ItemBuilder();
		this.selector = new NativeSelector();

		ConfigurationSerialization.registerClass(Loadout.class, "Loadout");
		enableModules();
	}

	public static SuperGive getPlugin() {
		return instance;
	}

	public ItemBuilder getBuilder() {
		return builder;
	}

	public Selector getSelector() {
		return selector;
	}

	@Override
	public void onDisable() {
		disableModules();
	}

	private void enableModules() {
		for (ModulePriority priority : ModulePriority.values()) {
			enableModules(priority);
		}
	}

	private void enableModules(ModulePriority priority) {
		for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
				.collect(Collectors.toSet())) {
			mod.initialize();
			mod.setEnabled(true);
		}
	}

	private void disableModules() {
		List<ModulePriority> reverse = Arrays.asList(ModulePriority.values());
		Collections.reverse(reverse);
		for (ModulePriority priority : reverse) {
			disableModules(priority);
		}
	}

	private void disableModules(ModulePriority priority) {
		for (AbstractModule mod : modules.stream().filter(mod -> mod.getPriority() == priority)
				.collect(Collectors.toSet())) {
			mod.disable();
			mod.setEnabled(false);
		}
	}

	public void enableModule(AbstractModule module) {
		module.initialize();
		module.setEnabled(true);
		modules.add(module);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractModule> T getModule(Class<T> mod) {
		for (AbstractModule m : getModules())
			if (mod.isAssignableFrom(m.getClass()))
				return (T) m;
		return null;
	}

	public AbstractModule getModule(String string) {
		for (AbstractModule mod : getModules())
			if (mod.getId().equals(string))
				return mod;
		return null;
	}

	public Set<AbstractModule> getModules() {
		return modules;
	}

}
