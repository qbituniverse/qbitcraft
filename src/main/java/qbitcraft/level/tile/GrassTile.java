package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.ConnectorSprite;
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class GrassTile extends Tile {
	private static ConnectorSprite sprite = new ConnectorSprite(GrassTile.class, new Sprite(0, 6, 3, 3, 1, 3), new Sprite(3, 6, 2, 2, 1))
	{
		public boolean connectsTo(Tile tile, boolean isSide) {
			if(!isSide) return true;
			return tile.connectsToGrass;
		}
	};
	
	protected GrassTile(String name) {
		super(name, sprite);
		csprite.sides = csprite.sparse;
		connectsToGrass = true;
		maySpawn = true;
	}

	public void tick(Level level, int xt, int yt) {
		// TODO revise this method.
		if (random.nextInt(40) != 0) return;
		
		int xn = xt;
		int yn = yt;
		
		if (random.nextBoolean()) xn += random.nextInt(2) * 2 - 1;
		else yn += random.nextInt(2) * 2 - 1;

		if (level.getTile(xn, yn) == Tiles.get("dirt")) {
			level.setTile(xn, yn, this);
		}
	}

	@Override
	public void render(Screen screen, Level level, int x, int y) {
		sprite.sparse.color = DirtTile.dCol(level.depth);
		sprite.render(screen, level, x, y);
	}

	public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem) item;
			if (tool.type == ToolType.Shovel) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					Sound.monsterHurt.play();
					if (random.nextInt(5) == 0) { // 20% chance to drop seeds
						level.dropItem(xt*16+8, yt*16+8, 2, Items.get("seeds"));
					}
					return true;
				}
			}
			if (tool.type == ToolType.Hoe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("dirt"));
					Sound.monsterHurt.play();
					if (random.nextInt(5) != 0) { // 80% chance to drop seeds
						level.dropItem(xt*16+8, yt*16+8, Items.get("seeds"));
					}
					return true;
				}
			}
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(4 - tool.level) && tool.payDurability()) {
					level.setTile(xt, yt, Tiles.get("path"));
					Sound.monsterHurt.play();
				}
			}
		}
		return false;
	}
}
