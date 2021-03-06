package qbitcraft.core;

import qbitcraft.core.io.Settings;
import qbitcraft.entity.furniture.Bed;
import qbitcraft.entity.mob.Player;
import qbitcraft.entity.mob.RemotePlayer;
import qbitcraft.level.Level;
import qbitcraft.saveload.Load;
import qbitcraft.saveload.Save;
import qbitcraft.screen.LoadingDisplay;
import qbitcraft.screen.PlayerDeathDisplay;
import qbitcraft.screen.WorldGenDisplay;
import qbitcraft.screen.WorldSelectDisplay;

import org.jetbrains.annotations.Nullable;

public class World extends Game {
	private World() {}
	
	public static final int[] idxToDepth = {-3, -2, -1, 0, 1, -4}; /// this is to map the level depths to each level's index in Game's levels array. This must ALWAYS be the same length as the levels array, of course.
	public static final int minLevelDepth, maxLevelDepth;
	static {
		int min, max;
		min = max = idxToDepth[0];
		for(int depth: idxToDepth) {
			if(depth < min)
				min = depth;
			if(depth > max)
				max = depth;
		}
		minLevelDepth = min;
		maxLevelDepth = max;
	}
	
	static int worldSize = 128; // The size of the world
	public static int lvlw = worldSize; // The width of the world
	public static int lvlh = worldSize; // The height of the world
	
	static int playerDeadTime; // the time after you die before the dead menu shows up.
	static int pendingLevelChange; // used to determine if the player should change levels or not.
	@Nullable
	public static Action onChangeAction; // allows action to be stored during a change schedule that should only occur once the screen is blacked out.
	
	/// SCORE MODE
	
	/** This is for a contained way to find the index in the levels array of a level, based on it's depth. This is also helpful because add a new level in the future could change this. */
	public static int lvlIdx(int depth) {
		if(depth > maxLevelDepth) return lvlIdx(minLevelDepth);
		if(depth < minLevelDepth) return lvlIdx(maxLevelDepth);
		
		if(depth == -4) return 5;
		
		return depth + 3;
	}
	
	
	
	
	/** This method is used when respawning, and by initWorld to reset the vars. It does not generate any new terrain. */
	public static void resetGame() { resetGame(true); }
	public static void resetGame(boolean keepPlayer) {
		if(debug) System.out.println("Resetting...");
		playerDeadTime = 0;
		currentLevel = 3;
		Updater.asTick = 0;
		Updater.notifications.clear();
		
		// adds a new player
		if(isValidServer()) {
			player = null;
			return;
		}
		if(keepPlayer) {
			if(player instanceof RemotePlayer)
				player = new RemotePlayer(true, (RemotePlayer) player);
			else
				player = new Player(player, input);
		} else
			player = new Player(null, input);
		
		if (levels[currentLevel] == null) return;
		
		// "shouldRespawn" is false on hardcore, or when making a new world.
		if (PlayerDeathDisplay.shouldRespawn) { // respawn, don't regenerate level.
			//if (debug) System.out.println("Current Level = " + currentLevel);
			if(!isValidClient()) {
				Level level = levels[currentLevel];
				player.respawn(level);
				//if (debug) System.out.println("respawned player in current world");
				level.add(player); // adds the player to the current level (always surface here)
			} else {
				client.requestRespawn();
			}
		}
	}
	
	/** This method is used to create a brand new world, or to load an existing one from a file.
	 * For the loading screen updates to work, it it assumed that *this* is called by a thread *other* than the one rendering the current *menu*.
	 **/
	public static void initWorld() { // this is a full reset; everything.
		if(debug) System.out.println("Resetting world...");
		
		/*if(isValidServer()) {
			System.err.println("Cannot initialize world while acting as a server runtime; not running initWorld().");
			return;
		}*/
		
		PlayerDeathDisplay.shouldRespawn = false;
		resetGame();
		player = new Player(null, input);
		Bed.removePlayers();
		Updater.gameTime = 0;
		Updater.gamespeed = 1;
		
		Updater.changeTimeOfDay(Updater.Time.Morning); // resets tickCount; game starts in the day, so that it's nice and bright.
		gameOver = false;
		
		levels = new Level[6];
		
		Updater.scoreTime = (Integer) Settings.get("scoretime") * 60 * Updater.normSpeed;
		
		LoadingDisplay.setPercentage(0); // this actually isn't necessary, I think; it's just in case.
		
		if(!isValidClient()) {
			if(debug) System.out.println("Initializing world non-client...");
			
			if(WorldSelectDisplay.loadedWorld()) {
				Load loader = new Load(WorldSelectDisplay.getWorldName());
				if(isValidServer() && loader.getWorldVersion().compareTo(Game.VERSION) < 0) {
					new Save(player, true); // overwrite the old player save, to update it.
					new Save(WorldSelectDisplay.getWorldName()); // save the main world
				}
			} else {
				worldSize = (Integer) Settings.get("size");
				
				float loadingInc = 100f / (maxLevelDepth - minLevelDepth + 1); // the .002 is for floating point errors, in case they occur.
				for (int i = maxLevelDepth; i >= minLevelDepth; i--) {
					// i = level depth; the array starts from the top because the parent level is used as a reference, so it should be constructed first. It is expected that the highest level will have a null parent.
					if(debug) System.out.println("Loading level " + i + "...");
					LoadingDisplay.setMessage(Level.getDepthString(i));
					levels[lvlIdx(i)] = new Level(worldSize, worldSize, i, levels[lvlIdx(i+1)], !WorldSelectDisplay.loadedWorld());
					
					LoadingDisplay.progress(loadingInc);
				}
				
				if(debug) System.out.println("Level loading complete.");
				
				Level level = levels[currentLevel]; // sets level to the current level (3; surface)
				Updater.pastDay1 = false;
				player.findStartPos(level, WorldGenDisplay.getSeed()); // finds the start level for the player
				level.add(player);
			}
			
			Renderer.readyToRenderGameplay = true;
		} else {
			levels = new Level[6];
			currentLevel = 3;
		}
		
		PlayerDeathDisplay.shouldRespawn = true;
		
		if(debug) System.out.println("World initialized.");
	}
	
	
	
	
	/** This method is called when you interact with stairs, this will give you the transition effect. While changeLevel(int) just changes the level. */
	public static void scheduleLevelChange(int dir) { scheduleLevelChange(dir, null); }
	public static void scheduleLevelChange(int dir, @Nullable Action changeAction) {
		if(!isValidServer()) {
			onChangeAction = changeAction;
			pendingLevelChange = dir;
		}
	}
	
	/** This method changes the level that the player is currently on.
	 * It takes 1 integer variable, which is used to tell the game which direction to go.
	 * For example, 'changeLevel(1)' will make you go up a level,
	 while 'changeLevel(-1)' will make you go down a level. */
	public static void changeLevel(int dir) {
		if(isValidServer()) {
			System.out.println("Server tried to change level.");
			return;
		}
		
		if(onChangeAction != null && !Game.isConnectedClient()) {
			onChangeAction.act();
			onChangeAction = null;
		}
		
		if(isConnectedClient())
			levels[currentLevel].clearEntities(); // clear all the entities from the last level, so that no artifacts remain. They're loaded dynamically, anyway.
		else
			levels[currentLevel].remove(player); // removes the player from the current level.
		
		int nextLevel = currentLevel + dir;
		if (nextLevel <= -1) nextLevel = levels.length-1; // fix accidental level underflow
		if (nextLevel >= levels.length) nextLevel = 0; // fix accidental level overflow
		//level = levels[currentLevel]; // sets the level to the current level
		if(Game.debug) System.out.println(Network.onlinePrefix()+"setting level from "+currentLevel+" to "+nextLevel);
		currentLevel = nextLevel;
		
		player.x = (player.x >> 4) * 16 + 8; // sets the player's x coord (to center yourself on the stairs)
		player.y = (player.y >> 4) * 16 + 8; // sets the player's y coord (to center yourself on the stairs)
		
		if(isConnectedClient()/* && levels[currentLevel] == null*/) {
			Renderer.readyToRenderGameplay = false;
			client.requestLevel(currentLevel);
		} else
			levels[currentLevel].add(player); // adds the player to the level.
	}
}
