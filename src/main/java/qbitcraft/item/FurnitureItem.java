package qbitcraft.item;

import java.util.ArrayList;

import qbitcraft.core.Game;
import qbitcraft.entity.Direction;
import qbitcraft.entity.furniture.*;
import qbitcraft.entity.mob.*;
import qbitcraft.gfx.Sprite;
import qbitcraft.level.Level;
import qbitcraft.level.tile.Tile;

public class FurnitureItem extends Item {
	
	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();
		
		/// there should be a spawner for each level of mob, or at least make the level able to be changed.
		items.add(new FurnitureItem(new Spawner(new Cow())));
		items.add(new FurnitureItem(new Spawner(new Pig())));
		items.add(new FurnitureItem(new Spawner(new Sheep())));
		items.add(new FurnitureItem(new Spawner(new Slime(1))));
		items.add(new FurnitureItem(new Spawner(new Zombie(1))));
		items.add(new FurnitureItem(new Spawner(new Creeper(1))));
		items.add(new FurnitureItem(new Spawner(new Skeleton(1))));
		items.add(new FurnitureItem(new Spawner(new Snake(1))));
		items.add(new FurnitureItem(new Spawner(new Knight(1))));
		items.add(new FurnitureItem(new Spawner(new AirWizard(false))));
		
		items.add(new FurnitureItem(new Chest()));
		// add the various types of crafting furniture
		for(Crafter.Type type: Crafter.Type.values()) {
			items.add(new FurnitureItem(new Crafter(type)));
		}
		// add the various lanterns
		for(Lantern.Type type: Lantern.Type.values()) {
			items.add(new FurnitureItem(new Lantern(type)));
		}
		
		items.add(new FurnitureItem(new Tnt()));
		items.add(new FurnitureItem(new Bed()));
		
		return items;
	}
	
	public Furniture furniture; // the furniture of this item
	public boolean placed; // value if the furniture has been placed or not.
	
	private static int getSpritePos(int fpos) {
		int x = fpos%32;
		int y = fpos/32;
		return ((x-8)/2) + y*32;
	}
	
	public FurnitureItem(Furniture furniture) {
		super(furniture.name, new Sprite(getSpritePos(furniture.sprite.getPos()), 0));
		this.furniture = furniture; // Assigns the furniture to the item
		placed = false;
	}
	
	/** Determines if you can attack enemies with furniture (you can't) */
	public boolean canAttack() {
		return false;
	}
	
	/** What happens when you press the "Attack" key with the furniture in your hands */
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		if (tile.mayPass(level, xt, yt, furniture)) { // If the furniture can go on the tile
			// Placed furniture's X and Y positions
			furniture.x = xt * 16 + 8;
			furniture.y = yt * 16 + 8;
			level.add(furniture); // adds the furniture to the world
			if(Game.isMode("creative"))
				furniture = furniture.clone();
			else
				placed = true; // the value becomes true, which removes it from the player's active item
			
			return true;
		}
		return false;
	}
	
	public boolean isDepleted() {
		return placed;
	}
	
	public FurnitureItem clone() {
		return new FurnitureItem(furniture.clone());
	}
}
