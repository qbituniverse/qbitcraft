package qbitcraft.level.tile.wool;

import qbitcraft.gfx.Sprite;

public class GreenWoolTile extends Wool {
    private static Sprite sprite = new Sprite(10, 2, 2, 2, 1);

    public GreenWoolTile(String name) {
        super(name, sprite, "Green Wool");
    }
}
