package qbitcraft.level.tile;

import qbitcraft.core.Game;
import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.Mob;
import qbitcraft.entity.mob.Player;
import qbitcraft.entity.particle.SmashParticle;
import qbitcraft.entity.particle.TextParticle;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.ConnectorSprite;
import qbitcraft.gfx.Screen;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class TreeTile extends Tile {
	
	protected TreeTile(String name) {
		super(name, (ConnectorSprite)null);
		connectsToGrass = true;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		Tiles.get("grass").render(screen, level, x, y);
		
		boolean u = level.getTile(x, y - 1) == this;
		boolean l = level.getTile(x - 1, y) == this;
		boolean r = level.getTile(x + 1, y) == this;
		boolean d = level.getTile(x, y + 1) == this;
		boolean ul = level.getTile(x - 1, y - 1) == this;
		boolean ur = level.getTile(x + 1, y - 1) == this;
		boolean dl = level.getTile(x - 1, y + 1) == this;
		boolean dr = level.getTile(x + 1, y + 1) == this;

		if (u && ul && l) {
			screen.render(x * 16 + 0, y * 16 + 0, 1 + 1 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 0, y * 16 + 0, 0 + 0 * 32, 0, 1);
		}
		if (u && ur && r) {
			screen.render(x * 16 + 8, y * 16 + 0, 1 + 2 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 8, y * 16 + 0, 1 + 0 * 32, 0, 1);
		}
		if (d && dl && l) {
			screen.render(x * 16 + 0, y * 16 + 8, 1 + 2 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 0, y * 16 + 8, 0 + 1 * 32, 0, 1);
		}
		if (d && dr && r) {
			screen.render(x * 16 + 8, y * 16 + 8, 1 + 1 * 32, 0, 1);
		} else {
			screen.render(x * 16 + 8, y * 16 + 8, 1 + 3 * 32, 0, 1);
		}
	}

	public void tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt);
		if (damage > 0) level.setData(xt, yt, damage - 1);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}
	
	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		hurt(level, x, y, dmg);
		return true;
	}
	
	@Override
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if(Game.isMode("creative"))
			return false; // go directly to hurt method
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Axe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
					return true;
				}
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		if (random.nextInt(100) == 0)
			level.dropItem(x*16+8, y*16+8, Items.get("Apple"));
		
		int damage = level.getData(x, y) + dmg;
		int treeHealth = 20;
		if (Game.isMode("creative")) dmg = damage = treeHealth;
		
		level.add(new SmashParticle(x*16, y*16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x*16+8, y*16+8, Color.RED));
		if (damage >= treeHealth) {
			level.dropItem(x*16+8, y*16+8, 1, 2, Items.get("Wood"));
			level.dropItem(x*16+8, y*16+8, 1, 2, Items.get("Acorn"));
			level.setTile(x, y, Tiles.get("grass"));
		} else {
			level.setData(x, y, damage);
		}
	}
}
