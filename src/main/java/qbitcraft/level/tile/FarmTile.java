package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class FarmTile extends Tile {
	private static Sprite sprite = new Sprite(12, 0, 2, 2, 1, true, new int[][] {{1, 0}, {0, 1}});
	
	protected FarmTile(String name) {
		super(name, sprite);
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					Sound.monsterHurt.play();
					return true;
				}
			}
		}
		return false;
	}

	public void tick(Level level, int xt, int yt) {
		int age = level.getData(xt, yt);
		if (age < 5) level.setData(xt, yt, age + 1);
	}

	public void steppedOn(Level level, int xt, int yt, Entity entity) {
		if (random.nextInt(60) != 0) return;
		if (level.getData(xt, yt) < 5) return;
		level.setTile(xt, yt, Tiles.get("dirt"));
	}
}
