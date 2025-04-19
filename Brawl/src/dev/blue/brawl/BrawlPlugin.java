package dev.blue.brawl;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.blue.brawl.displays.PrestigeDisplay;
import dev.blue.brawl.displays.ScoreboardDisplay;
import dev.blue.brawl.modes.BaseGame;
import dev.blue.brawl.modes.BodyCount;
import dev.blue.brawl.modes.Hourglass;
import dev.blue.brawl.modes.LastManStanding;

public class BrawlPlugin extends JavaPlugin {
	private BaseGame timer;
	private Utils utils;
	public int minimumPlayers;
	public GameMode playmode;
	public PrestigeDisplay prestigeDisplay;
	public ScoreboardDisplay scoreboardDisplay;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		utils = new Utils(this);
		minimumPlayers = getConfig().getInt("MinimumPlayers");
		playmode = GameMode.valueOf(getConfig().getString("GamemodeDuringPlay").toUpperCase());
		setupWorld();
		if(utils.isLastManStanding()) {
			this.timer = new LastManStanding(this);
		}else if(utils.isBodyCount()) {
			this.timer = new BodyCount(this);
		}else if(utils.isHourglass()){
			this.timer = new Hourglass(this);
		}
		Cmds cmds = new Cmds(this);
		getCommand("brawl").setExecutor(cmds);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		prestigeDisplay = new PrestigeDisplay(this);
		prestigeDisplay.resetDisplays();
		scoreboardDisplay = new ScoreboardDisplay(this);
		scoreboardDisplay.initiateLeaderboard();
	}
	
	private void setupWorld() {
		World world = Bukkit.getServer().getWorld("world");
		world.setTime(6000);
		world.setStorm(false);
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
		world.setGameRule(GameRule.DISABLE_RAIDS, true);
		world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
		world.setGameRule(GameRule.DO_FIRE_TICK, false);
		world.setGameRule(GameRule.DO_MOB_LOOT, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		world.setGameRule(GameRule.DO_TILE_DROPS, false);
		world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		world.setGameRule(GameRule.DROWNING_DAMAGE, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
		world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, true);
		world.setGameRule(GameRule.SPAWN_RADIUS, 10);
		world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
	}
	
	public void updateLevelDisplay(Player p) {
		prestigeDisplay.updateDisplay(p);
	}
	
	public ScoreboardDisplay getSB() {
		return scoreboardDisplay;
	}
	
	public BaseGame getGameTimer() {
		return timer;
	}
	
	public void setGameTimer(BaseGame game) {
		this.timer = game;
	}
	
	public Utils getUtils() {
		return utils;
	}
}
