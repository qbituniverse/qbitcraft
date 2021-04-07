package qbitcraft.level.tile;

import qbitcraft.entity.Entity;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.ConnectorSprite;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.level.Level;

public class HoleTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(HoleTile.class, new Sprite(24, 6, 3, 3, 1, 3), new Sprite(27, 6, 2, 2, 1))
	{
		public boolean connectsTo(Tile tile, boolean isSide) {
			return tile.connectsToLiquid();
		}
	};
	
	protected HoleTile(String name) {
		super(name, sprite);
		connectsToSand = true;
		connectsToFluid = true;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e.canSwim();
	}
}
