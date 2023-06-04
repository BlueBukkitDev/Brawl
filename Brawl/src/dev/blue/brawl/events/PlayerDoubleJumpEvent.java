package dev.blue.brawl.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. 
**/
public class PlayerDoubleJumpEvent extends Event implements Cancellable {
	
	private Player player;
	private boolean cancelled;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	/**
	 *Called when a player double-jumps. </br>
	 *@param player = the player who jumped</br>
	 **/
	public PlayerDoubleJumpEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
}
