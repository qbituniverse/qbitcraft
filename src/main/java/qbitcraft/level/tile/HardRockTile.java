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
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class HardRockTile extends Tile {
	// theoretically the full sprite should never be used, so we can use a placeholder
	private static ConnectorSprite sprite = new ConnectorSprite(HardRockTile.class, new Sprite(18, 9, 3, 3, 1, 3), new Sprite(21, 10, 2, 2, 1, 3), Sprite.missingTexture(2, 2));
	
	protected HardRockTile(String name) {
		super(name, sprite);
	}
	
	public boolean mayPass(Level level, int x, int y, Entity e) {
		return false;
	}

	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		hurt(level, x, y, 0);
		return true;
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if(Game.isMode("creative"))
			return false; // go directly to hurt method
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Pickaxe && tool.level == 4) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, random.nextInt(10) + (tool.level) * 5 + 10);
					return true;
				}
			} else {
				Game.notifications.add("Gem Pickaxe Required.");
			}
		}
		return false;
	}

	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + dmg;
		int hrHealth = 200;
		if (Game.isMode("creative")) dmg = damage = hrHealth;
		level.add(new SmashParticle(x * 16, y * 16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (damage >= hrHealth) {
			level.setTile(x, y, Tiles.get("dirt"));
			level.dropItem(x*16+8, y*16+8, 1, 3, Items.get("Stone"));
			level.dropItem(x*16+8, y*16+8, 0, 1, Items.get("coal"));
		} else {
			level.setData(x, y, damage);
		}
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dCol(level.depth);
		super.render(screen, level, x, y);
	}

	public void tick(Level level, int xt, int yt) {
		int damage = level.getData(xt, yt);
		if (damage > 0) level.setData(xt, yt, damage - 1);
	}
}
