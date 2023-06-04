package dev.blue.brawl.modes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.blue.brawl.BrawlPlugin;

public class BodyCount extends BaseGame {
	
	private int killsToWin;

	public BodyCount(BrawlPlugin main) {
		super(main);
		killsToWin = main.getConfig().getInt("KillsToWin");
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
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(main.getUtils().getScore(each) >= killsToWin) {
				return true;
			}
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
			main.getGameTimer().enterStasis();
			return null;
		}
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(main.getUtils().getScore(each) >= killsToWin) {
				return each;
			}
		}
		return null;
	}
}
