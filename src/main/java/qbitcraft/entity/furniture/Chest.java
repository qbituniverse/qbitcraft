package qbitcraft.entity.furniture;

import java.io.File;
import java.io.IOException;
import java.util.List;

import qbitcraft.core.FileHandler;
import qbitcraft.core.Game;
import qbitcraft.entity.ItemHolder;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Inventory;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.saveload.Load;
import qbitcraft.screen.ContainerDisplay;

public class Chest extends Furniture implements ItemHolder {
	private Inventory inventory; // Inventory of the chest

	public Chest() { this("Chest"); }

	/**
	 * Creates a chest with a custom name.
	 * @param name Name of chest.
	 */
	public Chest(String name) {
		super(name, new Sprite(10, 26, 2, 2, 2), 3, 3); // Name of the chest
		
		inventory = new Inventory(); // initialize the inventory.
	}
	
	/** This is what occurs when the player uses the "Menu" command near this */
	public boolean use(Player player) {
		Game.setMenu(new ContainerDisplay(player, this));
		return true;
	}

	public void populateInvRandom(String lootTable, int depth) {
		try {
			String[] lines = Load.loadFile("/chestloot/" + lootTable + ".txt").toArray(new String[]{});

			for (String line : lines) {
				//System.out.println(line);
				String[] data = line.split(",");
				if (!line.startsWith(":")) {
					inventory.tryAdd(Integer.parseInt(data[0]), Items.get(data[1]), data.length < 3 ? 1 : Integer.parseInt(data[2]));
				} else if (inventory.invSize() == 0) {
					// adds the "fallback" items to ensure there's some stuff
					String[] fallbacks = line.substring(1).split(":");
					for (String item : fallbacks) {
						inventory.add(Items.get(item.split(",")[0]), Integer.parseInt(item.split(",")[1]));
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Couldn't read loot table \"" + lootTable + ".txt" + "\"");
			e.printStackTrace();
		}
	}
	
	@Override
	public void take(Player player) {
		if(inventory.invSize() == 0)
			super.take(player);
	}
	
	@Override
	protected String getUpdateString() {
		String updates = super.getUpdateString()+";";
		updates += "inventory,"+inventory.getItemData();
		return updates;
	}
	
	@Override
	protected boolean updateField(String fieldName, String val) {
		if(super.updateField(fieldName, val)) return true;
		switch(fieldName) {
			case "inventory":
				inventory.updateInv(val);
				if(Game.getMenu() instanceof ContainerDisplay)
					((ContainerDisplay)Game.getMenu()).onInvUpdate(this);
				return true;
		}
		return false;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
	
	@Override
	public void die() {
		if(level != null) {
			List<Item> items = inventory.getItems();
			level.dropItem(x, y, items.toArray(new Item[items.size()]));
		}
		super.die();
	}
}
