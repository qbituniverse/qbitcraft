package qbitcraft.level.tile.wool;

import qbitcraft.gfx.Sprite;

public class YellowWoolTile extends Wool {
    private static Sprite sprite = new Sprite(8, 4, 2, 2, 1);

    public YellowWoolTile(String name) {
        super(name, sprite, "Yellow Wool");
    }
}
