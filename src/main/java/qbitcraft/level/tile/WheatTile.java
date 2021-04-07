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

public class WheatTile extends Tile {
	
	protected WheatTile(String name) {
		super(name, (Sprite)null);
	}

	public void render(Screen screen, Level level, int x, int y) {
		int age = level.getData(x, y);
		int icon = age / 10;

		Tiles.get("farmland").render(screen, level, x, y);

		screen.render(x * 16 + 0, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
		screen.render(x * 16 + 8, y * 16 + 0, 13 + 0 * 32 + icon, 0, 1);
		screen.render(x * 16 + 0, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
		screen.render(x * 16 + 8, y * 16 + 8, 13 + 0 * 32 + icon, 1, 1);
	}
	
	public boolean IfWater(Level level, int xs, int ys) {
		Tile[] areaTiles = level.getAreaTiles(xs, ys, 1);
		for(Tile t: areaTiles)
			if(t == Tiles.get("Water"))
				return true;
		
		return false;
	}
	
	public void tick(Level level, int xt, int yt) {
		if (random.nextInt(2) == 0) return;

		int age = level.getData(xt, yt);
		if (!IfWater(level, xt, yt)) {
			if (age < 50) level.setData(xt, yt, age + 1);
		} else if (IfWater(level, xt, yt)) {
			if (age < 50) level.setData(xt, yt, age + 2);
		}
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
	
	public void steppedOn(Level level, int xt, int yt, Entity entity) {
		if (random.nextInt(60) != 0) return;
		if (level.getData(xt, yt) < 2) return;
		harvest(level, xt, yt, entity);
	}
	
	@Override
	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		harvest(level, x, y, source);
		return true;
	}
	
	private void harvest(Level level, int x, int y, Entity entity) {
		int age = level.getData(x, y);
		
		level.dropItem(x*16+8, y*16+8, 1, 2, Items.get("seeds"));
		
		int count = 0;
		if (age >= 50) {
			count = random.nextInt(3) + 2;
		} else if (age >= 40) {
			count = random.nextInt(2) + 1;
		}
		
		level.dropItem(x*16+8, y*16+8, count, Items.get("Wheat"));
		
		if (age >= 50 && entity instanceof Player) {
			((Player)entity).addScore(random.nextInt(5) + 1);
		}
		level.setTile(x, y, Tiles.get("dirt"));
	}
}
