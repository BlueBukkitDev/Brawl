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
	private Scoreboard sb;
	private Objective obj;
	
	public ScoreboardDisplay(BrawlPlugin main) {
		this.main = main;
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		obj = sb.registerNewObjective("score", Criteria.DUMMY, "§dMost Wins");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	/**
	 *Loops through all online players and runs setupLeaderboard() for each of them. 
	 **/
	public void initiateLeaderboard() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			setupLeaderboard(each);
		}
	}
	
	/**
	 *Sets the scoreboard for the given player to be the leaderboard scoreboard, and sets their own level from the file. 
	 **/
	public void setupLeaderboard(Player p) {
		p.setScoreboard(sb);
		Score score = obj.getScore(p.getName());
		score.setScore(main.getUtils().getLevel(p));
	}
	
	public void buildScoreboard() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.setScoreboard(sb);
			Score score = obj.getScore(each.getName());
			score.setScore(main.getUtils().getLevel(each));
		}
	}
	
	public void updateScore(Player p) {
		
	}
}
