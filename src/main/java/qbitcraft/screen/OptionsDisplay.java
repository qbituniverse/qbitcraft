package qbitcraft.screen;

import qbitcraft.core.Game;
import qbitcraft.core.io.Localization;
import qbitcraft.core.io.Settings;
import qbitcraft.saveload.Save;
import qbitcraft.screen.entry.SelectEntry;

public class OptionsDisplay extends Display {
	
	public OptionsDisplay() {
		super(true, new Menu.Builder(false, 6, RelPos.LEFT,
				Settings.getEntry("diff"),
				Settings.getEntry("fps"),
				Settings.getEntry("sound"),
				Settings.getEntry("autosave"),
				Settings.getEntry("skinon"),
				new SelectEntry("Change Key Bindings", () -> Game.setMenu(new KeyInputDisplay())),
				Settings.getEntry("language"),
				Settings.getEntry("textures")
			)
			.setTitle("Options")
			.createMenu()
		);
	}
	
	@Override
	public void onExit() {
		Localization.changeLanguage((String)Settings.get("language"));
		new Save();
		Game.MAX_FPS = (int)Settings.get("fps");
	}
}
