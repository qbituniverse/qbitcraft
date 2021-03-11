package qbitcraft.level.tile;

import qbitcraft.core.Game;
import qbitcraft.entity.Entity;
import qbitcraft.entity.mob.AirWizard;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.level.Level;

public class InfiniteFallTile extends Tile {
	
	protected InfiniteFallTile(String name) {
		super(name, (Sprite)null);
	}

	public void render(Screen screen, Level level, int x, int y) {}

	public void tick(Level level, int xt, int yt) {}

	public boolean mayPass(Level level, int x, int y, Entity e) {
		return e instanceof AirWizard || e instanceof Player && ( ((Player) e).skinon || Game.isMode("creative") );
	}
}
