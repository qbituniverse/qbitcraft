package qbitcraft.level.tile.wool;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;
import qbitcraft.level.tile.Tile;
import qbitcraft.level.tile.Tiles;

public class Wool extends Tile {
	private String woolType;

	public Wool(String name, Sprite sprite, String woolType) {
		super(name, sprite);
		this.woolType = woolType;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(3 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("hole"));
					Sound.monsterHurt.play();
					level.dropItem(xt*16+8, yt*16+8, Items.get(woolType));
					return true;
				}
			}
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e.canWool();
	}
}
