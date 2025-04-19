package dev.blue.brawl.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.blue.brawl.BrawlPlugin;

public class PlayerJoinGameEvent extends Event {
	private Player p;
	private Location loc;
	
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public PlayerJoinGameEvent(BrawlPlugin main, Player p) {
		this.p = p;
		this.loc = main.getGameTimer().getLobbySpawnLocation();
	}
	
	/**
	 *@return The player who joined
	 **/
	public Player getPlayer() {
		return p;
	}
	
	public void setSpawnLocation(Location loc) {
		this.loc = loc;
	}
	
	public Location getSpawnLocation() {
		return loc;
	}
}
