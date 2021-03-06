package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class DirtTile extends Tile {
	private static Sprite[] levelSprite = new Sprite[4];
	static {
		levelSprite[0] = new Sprite(12, 2, 2, 2, 1);
		levelSprite[1] = new Sprite(14, 2, 2, 2, 1);
		levelSprite[2] = new Sprite(12, 4, 2, 2, 1);
	}
	
	protected DirtTile(String name) {
		super(name, levelSprite[0]);
		maySpawn = true;
	}

	protected static int dCol(int depth) {
		switch(depth) {
			case 0: return Color.get(1, 129, 105, 83); // surface.
			case -4: return Color.get(1, 76, 30, 100); // dungeons.
			default: return Color.get(1, 102); // caves.
		}
	}

	protected static int dIdx(int depth) {
		switch(depth) {
			case 0: return 0; // surface
			case -4: return 2; // dungeons
			default: return 1; // caves
		}
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		levelSprite[dIdx(level.depth)].render(screen, x*16, y*16, 0);
	}
	
	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("hole"));
					Sound.monsterHurt.play();
					level.dropItem(xt*16+8, yt*16+8, Items.get("dirt"));
					return true;
				}
			}
			if (tool.type == ToolType.Hoe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("farmland"));
					Sound.monsterHurt.play();
					return true;
				}
			}
		}
		return false;
	}
}
