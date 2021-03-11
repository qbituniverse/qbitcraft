package qbitcraft.screen;

import java.util.ArrayList;
import java.util.Arrays;

import qbitcraft.core.Game;
import qbitcraft.core.World;
import qbitcraft.core.io.Settings;
import qbitcraft.gfx.Point;
import qbitcraft.gfx.SpriteSheet;
import qbitcraft.screen.entry.BlankEntry;
import qbitcraft.screen.entry.ListEntry;
import qbitcraft.screen.entry.SelectEntry;
import qbitcraft.screen.entry.StringEntry;

public class PlayerDeathDisplay extends Display {
	// this is an IMPORTANT bool, determines if the user should respawn or not. :)
	public static boolean shouldRespawn = true;
	
	public PlayerDeathDisplay() {
		super(false, false);
		
		ArrayList<ListEntry> entries = new ArrayList<>();
		entries.addAll(Arrays.asList(
			new StringEntry("Time: " + InfoDisplay.getTimeString()),
			new StringEntry("Score: " + Game.player.getScore()),
			new BlankEntry()
		));
		
		if(!Settings.get("mode").equals("hardcore")) {
			entries.add(new SelectEntry("Respawn", () -> {
				World.resetGame();
				if (!Game.isValidClient())
					Game.setMenu(null); //sets the menu to nothing
			}));
		}
		
		if(Settings.get("mode").equals("hardcore") || !Game.isValidClient())
			entries.add(new SelectEntry("Quit", () -> Game.setMenu(new TitleDisplay())));
		
		menus = new Menu[]{
			new Menu.Builder(true, 0, RelPos.LEFT, entries)
				.setPositioning(new Point(SpriteSheet.boxWidth, SpriteSheet.boxWidth * 3), RelPos.BOTTOM_RIGHT)
				.setTitle("You died! Aww!")
				.setTitlePos(RelPos.TOP_LEFT)
				.createMenu()
		};
		
	}
}
