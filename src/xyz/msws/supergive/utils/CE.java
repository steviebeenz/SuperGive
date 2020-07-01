package xyz.msws.supergive.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public enum CE {
	;
	private String path;
	private Object value;

	CE(String path, Object... values) {
		this.path = path;

		if (values.length == 1) {
			this.value = values[0];
		} else {
			List<Object> objects = new ArrayList<Object>();
			Collections.addAll(objects, values);
			if (objects.get(objects.size() - 1) == null)
				objects.remove(objects.size() - 1);

			this.value = objects.toArray();
		}
	}

	public String getPath() {
		return path;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static void updateValues(FileConfiguration file) {
		for (CE e : CE.values()) {
			if (file.contains(e.getPath()))
				e.setValue(file.get(e.getPath()));
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> cast) {
		if (cast.equals(Particle.class))
			return (T) Particle.valueOf(getValue(String.class));
		if (cast.equals(Sound.class))
			return (T) Sound.valueOf(getValue(String.class));
		if (cast.equals(Material.class))
			return (T) Material.valueOf(getValue(String.class));
		return cast.cast(this.value);
	}

	public Object getConfigValue(JavaPlugin plugin) {
		return plugin.getConfig().get(this.path);
	}

	public long longValue() {
		return getValue(Number.class).longValue();
	}

	public double doubleValue() {
		return getValue(Number.class).doubleValue();
	}

	public int intValue() {
		return getValue(Number.class).intValue();
	}

	public float floatValue() {
		return getValue(Number.class).floatValue();
	}

}
