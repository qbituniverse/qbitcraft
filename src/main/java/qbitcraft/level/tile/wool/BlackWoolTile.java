package qbitcraft.level.tile.wool;

import qbitcraft.gfx.Sprite;

public class BlackWoolTile extends Wool {
    private static Sprite sprite = new Sprite(10, 4, 2, 2, 1);

    public BlackWoolTile(String name) {
        super(name, sprite, "Black Wool");
    }
}
