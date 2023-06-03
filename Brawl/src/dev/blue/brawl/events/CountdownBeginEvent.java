package dev.blue.brawl.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *Marks the initiation of the game countdown. If this is cancelled, neither the countdown nor the game will begin. 
 **/
public class CountdownBeginEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	private int ticks;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	/**
	 *The CountdownBeginEvent is called when enough players to begin a game have been found online and not in a game.
	 **/
	public CountdownBeginEvent(int timerTicks) {
		ticks = timerTicks;
	}
	
	/**
	 *Returns an int representing the number of seconds or "countdown ticks" that will pass before the game begins.
	 **/
	public int getTicks() {
		return ticks;
	}
	
	/**
	 *Sets the number of seconds or "countdown ticks" that will pass before the game begins.
	 **/
	public void setTicks(int ticks) {
		this.ticks = ticks;
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
