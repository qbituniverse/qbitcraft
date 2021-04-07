package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.mob.Mob;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.level.Level;

public class SaplingTile extends Tile {
	private static Sprite sprite = new Sprite(12, 1, 1);
	
	private Tile onType;
	private Tile growsTo;
	
	protected SaplingTile(String name, Tile onType, Tile growsTo) {
		super(name, sprite);
		this.onType = onType;
		this.growsTo = growsTo;
		connectsToSand = onType.connectsToSand;
		connectsToGrass = onType.connectsToGrass;
		connectsToFluid = onType.connectsToFluid;
		maySpawn = true;
	}

	public void render(Screen screen, Level level, int x, int y) {
		onType.render(screen, level, x, y);
		
		sprite.render(screen, x*16, y*16);
	}

	public void tick(Level level, int x, int y) {
		int age = level.getData(x, y) + 1;
		if (age > 100) {
			level.setTile(x, y, growsTo);
		} else {
			level.setData(x, y, age);
		}
	}

	public boolean hurt(Level level, int x, int y, Mob source, int dmg, Direction attackDir) {
		level.setTile(x, y, onType);
		Sound.monsterHurt.play();
		return true;
	}
}
