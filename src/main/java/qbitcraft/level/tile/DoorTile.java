package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Mob;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class DoorTile extends Tile {
	private Sprite closedSprite;
	private Sprite openSprite;
	
	protected Material type;
	
	protected DoorTile(Material type) {
		super(type.name() + " Door", (Sprite)null);
		this.type = type;
		switch(type) {
			case Wood:
				closedSprite = new Sprite(5, 16, 2, 2, 1);
				openSprite = new Sprite(3, 16, 2, 2, 1);
				break;
			case Stone:
				closedSprite = new Sprite(15, 16, 2, 2, 1);
				openSprite = new Sprite(13, 16, 2, 2, 1);
				break;
			case Obsidian:
				closedSprite = new Sprite(25, 16, 2, 2, 1);
				openSprite = new Sprite(23, 16, 2, 2, 1);
				break;
		}
		sprite = closedSprite;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		boolean closed = level.getData(x, y) == 0;
		Sprite curSprite = closed?closedSprite:openSprite;
		curSprite.render(screen, x*16, y*16);
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get(id+3)); // will get the corresponding floor tile.
					Sound.monsterHurt.play();
					level.dropItem(xt*16+8, yt*16+8, Items.get(type.name() + " Door"));
					return true;
				}
			}
		}
		return false;
	}

	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		if(source instanceof Player) {
			boolean closed = level.getData(x, y) == 0;
			level.setData(x, y, closed?1:0);
		}
		return false;
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		boolean closed = level.getData(x, y) == 0;
		return !closed;
	}
}
