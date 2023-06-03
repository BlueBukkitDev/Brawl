package dev.blue.brawl.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. 
**/
public class GameWinEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	private boolean hasEnoughPlayers;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public GameWinEvent() {
		
	}
	
	public boolean hasEnoughPlayers() {
		return hasEnoughPlayers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = true;
	}
}
