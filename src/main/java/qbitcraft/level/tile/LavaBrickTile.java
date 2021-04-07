package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Mob;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class LavaBrickTile extends Tile {
	private static Sprite sprite = new Sprite(19, 2, 2, 2, 1);
	
	protected LavaBrickTile(String name) {
		super(name, sprite);
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("lava"));
					Sound.monsterHurt.play();
					return true;
				}
			}
		}
		return false;
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		if(entity instanceof Mob)
			((Mob)entity).hurt(this, x, y, 3);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) { return e.canWool(); }
}
