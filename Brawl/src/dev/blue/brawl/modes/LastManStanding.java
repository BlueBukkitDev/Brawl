package dev.blue.brawl.modes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.blue.brawl.BrawlPlugin;

public class LastManStanding extends BaseGame {

	public LastManStanding(BrawlPlugin main) {
		super(main);
	}

	@Override
	public boolean winConditionIsMet() {
		return getActivePlayers().size() == 1;
	}

	@Override
	public Player getWinner() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(playerIsActive(each)) {
				return each;
			}
		}
		return null;
	}
}
