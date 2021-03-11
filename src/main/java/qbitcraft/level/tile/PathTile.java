package qbitcraft.level.tile;

import qbitcraft.core.io.Sound;
import qbitcraft.entity.Direction;
import qbitcraft.entity.mob.Player;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Sprite;
import qbitcraft.item.Item;
import qbitcraft.item.Items;
import qbitcraft.item.ToolItem;
import qbitcraft.item.ToolType;
import qbitcraft.level.Level;

public class PathTile extends Tile {
    private static Sprite sprite = new Sprite(14, 4, 2, 2, 1);

    public PathTile(String name) {
        super(name, sprite);
        connectsToGrass = true;
        maySpawn = true;
    }

    public boolean interact(Level level, int xt, int yt, Player player, Item item, Direction attackDir) {
        if (item instanceof ToolItem) {
            ToolItem tool = (ToolItem) item;
            if (tool.type == ToolType.Shovel) {
                if (player.payStamina(4 - tool.level) && tool.payDurability()) {
                    level.setTile(xt, yt, Tiles.get("hole"));
                    Sound.monsterHurt.play();
                    level.dropItem(xt * 16 + 8, yt * 16 + 8, Items.get("dirt"));
                    return true;
                }
            }
        }
        return false;
    }
}
