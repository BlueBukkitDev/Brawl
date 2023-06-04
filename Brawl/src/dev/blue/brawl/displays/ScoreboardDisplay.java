package dev.blue.brawl.displays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import dev.blue.brawl.BrawlPlugin;

public class ScoreboardDisplay {
	
	private BrawlPlugin main;
	private Scoreboard leaderboard;
	private Objective wins;
	private Scoreboard killboard;
	private Objective kills;
	
	
	public ScoreboardDisplay(BrawlPlugin main) {
		this.main = main;
		leaderboard = Bukkit.getScoreboardManager().getNewScoreboard();
		wins = leaderboard.registerNewObjective("wins", Criteria.DUMMY, "§dMost Wins");
		wins.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	/**
	 *Loops through all recorded scores and runs setupLeaderboard() for each of them. 
	 **/
	public void initiateLeaderboard() {
		for(String each:main.getConfig().getConfigurationSection("Scores").getKeys(false)) {
			setupLeaderboard("§6"+main.getConfig().getString("Scores."+each+".Name"), (int)Math.floor(main.getConfig().getDouble("Scores."+each+".Level")*10));
		}
		for(Player each:Bukkit.getOnlinePlayers()) {
			setupLeaderboard(each);
		}
	}
	
	/**
	 *Sets the scoreboard for the given player to be the leaderboard scoreboard, and sets their own level from the file. 
	 **/
	public void setupLeaderboard(Player p) {
		p.setScoreboard(leaderboard);
	}
	
	private void setupLeaderboard(String name, int level) {
		Score score = wins.getScore("§6"+name);
		score.setScore(level);
	}
	
	public void initiateKillboard() {
		killboard = Bukkit.getScoreboardManager().getNewScoreboard();
		kills = killboard.registerNewObjective("score", Criteria.DUMMY, "§dConfirmed Kills");
		kills.setDisplaySlot(DisplaySlot.SIDEBAR);
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.setScoreboard(killboard);
		}
	}
	
	public void setupKillboard(Player p) {
		p.setScoreboard(killboard);
		Score score = kills.getScore("§6"+p.getName());
		score.setScore(main.getUtils().getScore(p));
	}
	
	public void setupKillboardAsSpectator(Player p) {
		killboard.resetScores("§6"+p.getName());
		p.setScoreboard(killboard);
	}
	
	public void updateScore(Player p) {
		Score score = kills.getScore("§6"+p.getName());
		score.setScore(main.getUtils().getScore(p));
	}
}
