package qbitcraft.item;

import java.util.ArrayList;

import qbitcraft.core.Game;
import qbitcraft.entity.Direction;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Sprite;
import qbitcraft.level.Level;
import qbitcraft.level.tile.Tile;
import qbitcraft.level.tile.Tiles;

public class BucketItem extends StackableItem {
	
	public enum Fill {
		Empty (Tiles.get("hole"), 2),
		Water (Tiles.get("water"), 0),
		Lava (Tiles.get("lava"), 1);
		
		public Tile contained;
		public int offset;

		Fill(Tile contained, int offset) {
			this.contained = contained;
			this.offset = offset;
		}
	}
	
	protected static ArrayList<Item> getAllInstances() {
		ArrayList<Item> items = new ArrayList<>();
		
		for(Fill fill: Fill.values())
			items.add(new BucketItem(fill));
		
		return items;
	}
	
	private static Fill getFilling(Tile tile) {
		for(Fill fill: Fill.values())
			if(fill.contained.id == tile.id)
				return fill;
		
		return null;
	}
	
	private Fill filling;
	
	private BucketItem(Fill fill) { this(fill, 1); }
	private BucketItem(Fill fill, int count) {
		super(fill.name() + " Bucket", new Sprite(fill.offset, 6, 0), count);
		this.filling = fill;
	}
	
	public boolean interactOn(Tile tile, Level level, int xt, int yt, Player player, Direction attackDir) {
		Fill fill = getFilling(tile);
		if(fill == null) return false;

		if(filling != Fill.Empty) {
			if(fill == Fill.Empty) {
				level.setTile(xt, yt, filling.contained);
				if (!Game.isMode("creative")) player.activeItem = editBucket(player, Fill.Empty);
				return true;
			} else if (fill == Fill.Lava && filling == Fill.Water) {
				level.setTile(xt, yt, Tiles.get("Obsidian"));
				if (!Game.isMode("creative")) player.activeItem = editBucket(player, Fill.Empty);
				return true;
			}
		} else { // this is an empty bucket
			level.setTile(xt, yt, Tiles.get("hole"));
			if(!Game.isMode("creative")) player.activeItem = editBucket(player, fill);
			return true;
		}
		
		return false;
	}
	
	/** This method exists due to the fact that buckets are stackable, but only one should be changed at one time. */
	private BucketItem editBucket(Player player, Fill newFill) {
		if (count == 0) return null; // this honestly should never happen...
		if (count == 1) return new BucketItem(newFill);
		
		// this item object is a stack of buckets.
		count--;
		player.getInventory().add(new BucketItem(newFill));
		return this;
	}
	
	public boolean equals(Item other) {
		return super.equals(other) && filling == ((BucketItem)other).filling;
	}
	
	@Override
	public int hashCode() { return super.hashCode() + filling.offset * 31; }
	
	public BucketItem clone() {
		return new BucketItem(filling, count);
	}
}
