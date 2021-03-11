package qbitcraft.screen;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Random;

import qbitcraft.core.Game;
import qbitcraft.core.Network;
import qbitcraft.core.Renderer;
import qbitcraft.core.VersionInfo;
import qbitcraft.core.World;
import qbitcraft.core.io.InputHandler;
import qbitcraft.core.io.Localization;
import qbitcraft.entity.mob.RemotePlayer;
import qbitcraft.gfx.Color;
import qbitcraft.gfx.Font;
import qbitcraft.gfx.Point;
import qbitcraft.gfx.Screen;
import qbitcraft.level.Level;
import qbitcraft.screen.entry.BlankEntry;
import qbitcraft.screen.entry.LinkEntry;
import qbitcraft.screen.entry.ListEntry;
import qbitcraft.screen.entry.SelectEntry;
import qbitcraft.screen.entry.StringEntry;

import org.jetbrains.annotations.NotNull;

public class TitleDisplay extends Display {
	private static final Random random = new Random();
	
	private int rand;
	private int count = 0; // this and reverse are for the logo; they produce the fade-in/out effect.
	private boolean reverse = false;
	
	public TitleDisplay() {
		super(true, false, new Menu.Builder(false, 2, RelPos.CENTER,
			(Network.getLatestVersionCheck() ? new StringEntry("Checking for updates...", Color.BLUE) : new BlankEntry()),
			new BlankEntry(),
			new BlankEntry(),
			new SelectEntry("Play", () -> {
				if(WorldSelectDisplay.getWorldNames().size() > 0)
					Game.setMenu(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER,
						new SelectEntry("Load World", () -> Game.setMenu(new WorldSelectDisplay())),
						new SelectEntry("New World", () -> Game.setMenu(new WorldGenDisplay()))
					).createMenu()));
				else
					Game.setMenu(new WorldGenDisplay());
			}),
			new SelectEntry("Join Online World", () -> Game.setMenu(new MultiplayerDisplay())),
			new SelectEntry("Options", () -> Game.setMenu(new OptionsDisplay())),
			displayFactory("Help",
				new SelectEntry("Instructions", () -> Game.setMenu(new BookDisplay(BookData.instructions))),
				new BlankEntry(),
				new SelectEntry("Storyline Guide (for the weak)", () -> Game.setMenu(new BookDisplay(BookData.storylineGuide))),
				new BlankEntry(),
				new SelectEntry("About", () -> Game.setMenu(new BookDisplay(BookData.about)))
			),
			new SelectEntry("Quit", Game::quit)
			)
			.setPositioning(new Point(Screen.w/2, Screen.h*3/5), RelPos.CENTER)
			.createMenu()
		);
	}
	
	@Override
	public void init(Display parent) {
		super.init(null); // The TitleScreen never has a parent.
		Renderer.readyToRenderGameplay = false;

		// check version
		checkVersion();

		/// this is useful to just ensure that everything is really reset as it should be. 
		if (Game.server != null) {
			if (Game.debug) System.out.println("wrapping up loose server ends");
			Game.server.endConnection();
			Game.server = null;
		}
		if (Game.client != null) {
			if (Game.debug) System.out.println("wrapping up loose client ends");
			Game.client.endConnection();
			Game.client = null;
		}
		Game.ISONLINE = false;

		LocalDateTime time = LocalDateTime.now();
		if (time.getMonth() == Month.DECEMBER) {
			if (time.getDayOfMonth() == 19) rand = 1;
			if (time.getDayOfMonth() == 25) rand = 2;
		} else {
			rand = random.nextInt(splashes.length - 3) + 3;
		}
		
		World.levels = new Level[World.levels.length];
		
		if(Game.player == null || Game.player instanceof RemotePlayer)
			// was online, need to reset player
			World.resetGame(false);
	}
	
	private void checkVersion() {
		if (Network.getLatestVersionCheck()) {
			VersionInfo latestVersion = Network.getLatestVersion();
			if (latestVersion == null) {
				Network.findLatestVersion(this::checkVersion);
			}
			else {
				if(latestVersion.version.compareTo(Game.VERSION) > 0) { // link new version
					menus[0].updateEntry(0, new StringEntry("New: "+latestVersion.releaseName, Color.GREEN));
					// TODO Add QU repo for version checks below
					menus[0].updateEntry(1, new LinkEntry(Color.CYAN, "--Select here to Download--", latestVersion.releaseUrl, "Direct link to latest version: " + latestVersion.releaseUrl + "\nCan also be found here with change log: https://github.com/qbituniverse/qbitcraft/releases"));
				}
				else if(latestVersion.releaseName.length() > 0)
					menus[0].updateEntry(0, new StringEntry("You have the latest version.", Color.DARK_GRAY));
				else
					menus[0].updateEntry(0, new StringEntry("Connection failed, could not check for updates.", Color.RED));
			}
		}
	}
	
	@NotNull
	private static SelectEntry displayFactory(String entryText, ListEntry... entries) {
		return new SelectEntry(entryText, () -> Game.setMenu(new Display(true, new Menu.Builder(false, 2, RelPos.CENTER, entries).createMenu())));
	}
	
	@Override
	public void tick(InputHandler input) {
		if (input.getKey("r").clicked) rand = random.nextInt(splashes.length - 3) + 3;
		
		if (!reverse) {
			count++;
			if (count == 25) reverse = true;
		} else {
			count--;
			if (count == 0) reverse = false;
		}
		
		super.tick(input);
	}
	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		
		int h = 2; // Height of squares (on the spritesheet)
		int w = 15; // Width of squares (on the spritesheet)
		int xo = (Screen.w - w * 8) / 2; // X location of the title
		int yo = 28; // Y location of the title
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				screen.render(xo + x * 8, yo + y * 8, x + y * 32, 0, 3);
			}
		}
		
		boolean isblue = splashes[rand].contains("blue");
		boolean isGreen = splashes[rand].contains("Green");
		boolean isRed = splashes[rand].contains("Red");
		
		/// this isn't as complicated as it looks. It just gets a color based off of count, which oscilates between 0 and 25.
		int bcol = 5 - count / 5; // this number ends up being between 1 and 5, inclusive.
		int splashColor = isblue ? Color.BLUE : isRed ? Color.RED : isGreen ? Color.GREEN : Color.get(1, bcol*51, bcol*51, bcol*25);

		
		Font.drawCentered(splashes[rand], screen, 52, splashColor);
		
		Font.draw("Version " + Game.VERSION, screen, 1, 1, Color.get(1, 51));
		
		
		String upString = "("+Game.input.getMapping("cursor-up")+", "+Game.input.getMapping("cursor-down")+Localization.getLocalized(" to select")+")";
		String selectString = "("+Game.input.getMapping("select")+Localization.getLocalized(" to accept")+")";
		String exitString = "("+Game.input.getMapping("exit")+ Localization.getLocalized(" to return")+")";
		
		Font.drawCentered(upString, screen, Screen.h - 32, Color.get(1, 51));
		Font.drawCentered(selectString, screen, Screen.h - 22, Color.get(1, 51));
		Font.drawCentered(exitString, screen, Screen.h - 12, Color.get(1, 51));
	}
	
	private static final String[] splashes = {
			"Tata's and Leo's Qbitcraft",
			"Tata's and Leo's Qbitcraft",
			"Tata's and Leo's Qbitcraft",
			"Tata's and Leo's Qbitcraft"
	};
}
