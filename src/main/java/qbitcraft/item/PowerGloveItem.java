package qbitcraft.item;

import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.furniture.Furniture;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Sprite;

public class PowerGloveItem extends Item {
	
	public PowerGloveItem() {
		super("Power Glove", new Sprite(0, 12, 0));
	}
	
	public boolean interact(Player player, Entity entity, Direction attackDir) {
		if (entity instanceof Furniture) { // If the power glove is used on a piece of furniture...
			Furniture f = (Furniture) entity;
			f.take(player); // Takes (picks up) the furniture
			return true;
		}
		return false; // method returns false if we were not given a furniture entity.
	}
	
	public PowerGloveItem clone() {
		return new PowerGloveItem();
	}
}
