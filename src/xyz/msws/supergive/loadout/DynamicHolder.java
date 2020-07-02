package xyz.msws.supergive.loadout;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Preconditions;

/**
 * @author imodm
 *
 */
public class DynamicHolder {
	private Entity ent;

	public DynamicHolder(Entity ent) {
		Preconditions.checkArgument(ent instanceof InventoryHolder || ent instanceof LivingEntity);
		this.ent = ent;
	}

	public void clearInventory() {
		if (ent instanceof LivingEntity) {
			((LivingEntity) ent).getEquipment().clear();
			return;
		}

		if (ent instanceof InventoryHolder) {
			((InventoryHolder) ent).getInventory().clear();
			return;
		}
	}

	public void addItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return;
		if (ent instanceof InventoryHolder) {
			((InventoryHolder) ent).getInventory().addItem(item);
			return;
		}
		if (ent instanceof LivingEntity) {
			((LivingEntity) ent).getEquipment().setItemInMainHand(item);
			return;
		}

	}

	public void addItem(ItemStack[] items) {
		for (ItemStack item : items)
			addItem(item);
	}
}
