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
import qbitcraft.gfx.Screen;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

/// this is all the spikey stuff (except "cloud cactus")
public class OreTile extends Tile {
	private Sprite sprite;
	private OreType type;
	
	public enum OreType {
        Iron (Items.get("Iron Ore"), 0),
		Lapis (Items.get("Lapis"), 2),
		Gold (Items.get("Gold Ore"), 4),
		Gem (Items.get("Gem"), 6);
		
		private Item drop;
		public final int color;
		
		OreType(Item drop, int color) {
			this.drop = drop;
			this.color = color;
		}
		
		protected Item getOre() {
			return drop.clone();
		}
    }
	
	protected OreTile(OreType o) {
		super((o == OreTile.OreType.Lapis ? "Lapis" : o.name() + " Ore"), new Sprite(24 + o.color, 0, 2, 2, 1));
        this.type = o;
		this.sprite = super.sprite;
	}

	public void render(Screen screen, Level level, int x, int y) {
		sprite.color = DirtTile.dCol(level.depth);
		sprite.render(screen, x*16, y*16);
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
			if (tool.type == ToolType.Pickaxe) {
				if (player.payStamina(6 - tool.level) && tool.payDurability()) {
					hurt(level, xt, yt, 1);
					return true;
				}
			}
		}
		return false;
	}
	
    public Item getOre() {
        return type.getOre();
    }
    
	public void hurt(Level level, int x, int y, int dmg) {
		int damage = level.getData(x, y) + 1;
		int oreH = random.nextInt(10) + 3;
		if (Game.isMode("creative")) dmg = damage = oreH;
		
		level.add(new SmashParticle(x * 16, y * 16));
		Sound.monsterHurt.play();

		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.RED));
		if (dmg > 0) {
			int count = random.nextInt(2) + 0;
			if (damage >= oreH) {
				level.setTile(x, y, Tiles.get("dirt"));
				count += 2;
			} else {
				level.setData(x, y, damage);
			}
			level.dropItem(x*16+8, y*16+8, count, type.getOre());
		}
	}

	public void bumpedInto(Level level, int x, int y, Entity entity) {
		/// this was used at one point to hurt the player if they touched the ore; that's probably why the sprite is so spikey-looking.
	}
}
