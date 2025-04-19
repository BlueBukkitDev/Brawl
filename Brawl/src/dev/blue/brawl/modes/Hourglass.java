package dev.blue.brawl.modes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.blue.brawl.BrawlPlugin;

public class Hourglass extends BaseGame {
	private int timeToWin;

	public Hourglass(BrawlPlugin main) {
		super(main);
		timeToWin = main.getConfig().getInt("TimeToWin")+(main.getConfig().getInt("PerPlayerTimeScaler")*main.getGameTimer().getContestants().size()-2);
	}
	
	@Override
	public void onGameStart() {
		timeToWin = main.getConfig().getInt("TimeToWin")+(main.getConfig().getInt("PerPlayerTimeScaler")*main.getGameTimer().getContestants().size()-2);
	}

	@Override
	public boolean winConditionIsMet() {
		int remainingPlayers = 0;
		for(String each:main.getGameTimer().getContestants()) {
			if(Bukkit.getPlayer(UUID.fromString(each)) != null && Bukkit.getPlayer(UUID.fromString(each)).isOnline()) {
				remainingPlayers++;
			}
		}
		if(remainingPlayers == 1) {
			return true;
		}

		if(time >= timeToWin) {
			return true;
		}
		return false;
	}

	@Override
	public Player getWinner() {
		int remainingPlayers = 0;
		for(String each:main.getGameTimer().getContestants()) {
			if(Bukkit.getPlayer(UUID.fromString(each)) != null && Bukkit.getPlayer(UUID.fromString(each)).isOnline()) {
				remainingPlayers++;
			}
		}
		if(remainingPlayers == 1) {
			main.getGameTimer().enterStasis(lobbySpawnLocation);
			return null;
		}
		Player tentative = null;
		for(Player each:getActivePlayers()) {
			if(tentative == null || getScore(each) > getScore(tentative)) {
				tentative = each;
			}
		}
		return tentative;
	}
}
