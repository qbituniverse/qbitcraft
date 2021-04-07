package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class FloorTile extends Tile {
	private Sprite sprite;
	
	protected Material type;
	
	protected FloorTile(Material type) {
		super((type == Material.Wood ? "Wood Planks" : type == Material.Obsidian ? "Obsidian" : type.name()+" Bricks"), (Sprite)null);
		this.type = type;
		maySpawn = true;
		switch(type) {
			case Wood:
				sprite = new Sprite(5, 14, 2, 2, 1, 0);
				break;
			case Stone:
				sprite = new Sprite(15, 14, 2, 2, 1, 0);
				break;
			case Obsidian:
				sprite = new Sprite(25, 14, 2, 2, 1, 0);
				break;
		}
		super.sprite = sprite;
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					if (level.depth == 1) {
						level.setTile(xt, yt, Tiles.get("Cloud"));
					} else {
						level.setTile(xt, yt, Tiles.get("hole"));
					}
					Item drop;
					switch(type) {
						case Wood: drop = Items.get("Plank"); break;
						default: drop = Items.get(type.name()+" Brick"); break;
					}
					Sound.monsterHurt.play();
					level.dropItem(xt*16+8, yt*16+8, drop);
					return true;
				}
			}
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return true;
	}
}
