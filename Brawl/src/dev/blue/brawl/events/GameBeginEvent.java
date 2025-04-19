package dev.blue.brawl.events;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.blue.brawl.BrawlPlugin;
import dev.blue.brawl.modes.BaseGame;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. 
**/
public class GameBeginEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	private boolean hasEnoughPlayers;
	private List<Player> players;
	private BaseGame game;
	private Location loc;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public GameBeginEvent(BrawlPlugin main, boolean hasEnoughPlayers, List<Player> players, BaseGame game) {
		this.hasEnoughPlayers = hasEnoughPlayers;
		this.players = players;
		this.game = game;
		this.loc = main.getGameTimer().getLobbySpawnLocation();
	}
	
	public BaseGame getGame() {
		return game;
	}
	
	public boolean hasEnoughPlayers() {
		return hasEnoughPlayers;
	}
	
	public List<Player> getActivePlayers() {
		return players;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = true;
	}
	
	public void setPregameSpawnLocation(Location loc) {
		this.loc = loc;
	}
	
	public Location getPregameSpawnLocation() {
		return loc;
	}
}
