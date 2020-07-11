package xyz.msws.supergive.loadout;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * @author imodm A simple wrapper to allow for simple giving of items to both
 *         {@link InventoryHolder} and {@link LivingEntity} in one class
 *
 */
public class DynamicHolder {
	private InventoryHolder holder;
	private LivingEntity living;

	public DynamicHolder(InventoryHolder holder) {
		this.holder = holder;
		this.living = null;
	}

	public DynamicHolder(LivingEntity living) {
		this.holder = null;
		this.living = living;
	}

	public boolean isLivingEntity() {
		return living != null;
	}

	public LivingEntity getLiving() {
		return living;
	}

	public InventoryHolder getHolder() {
		return holder;
	}

	public boolean hasInventory() {
		return holder != null;
	}

	public Inventory getInventory() {
		return holder == null ? null : holder.getInventory();
	}

	public void clearInventory() {
		if (holder != null)
			holder.getInventory().clear();
		if (living != null)
			living.getEquipment().clear();
	}

	public void addItem(ItemStack item) {
		if (item == null || item.getType() == Material.AIR)
			return;
		if (holder != null)
			holder.getInventory().addItem(item);
		if (living != null) {
			EntityEquipment eq = living.getEquipment();
			if (eq.getItemInMainHand() == null || eq.getItemInMainHand().getType() == Material.AIR) {
				eq.setItemInMainHand(item);
			} else {
				eq.setItemInOffHand(item);
			}
		}
	}

	public void addItem(ItemStack[] items) {
		for (ItemStack item : items)
			addItem(item);
	}
}
