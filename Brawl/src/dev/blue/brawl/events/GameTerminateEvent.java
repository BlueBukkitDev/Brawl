package dev.blue.brawl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTerminateEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	private boolean winnerExists;

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public GameTerminateEvent(boolean winnerExists) {
		this.winnerExists = winnerExists;
	}
	
	public boolean winnerExists() {
		return winnerExists;
	}
}
