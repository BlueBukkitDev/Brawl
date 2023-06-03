package dev.blue.brawl.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.blue.brawl.BrawlPlugin;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. 
**/
public class PlayerEliminateEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	private Player player;
	private UUID killer;
	private String deathMessage;
	private BrawlPlugin main;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public PlayerEliminateEvent(BrawlPlugin main, Player player, UUID uuid) {
		this.player = player;
		this.killer = uuid;
		deathMessage = "§7Player "+player.getName()+" was eliminated";
		this.main = main;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getReason() {
		return main.getUtils().getDamageCause(player);
	}
	
	public UUID getKiller() {
		return killer;
	}
	
	public void setKiller(UUID uid) {
		this.killer = uid;
	}
	
	public String getDeathMessage() {
		return deathMessage;
	}
	
	public void setDeathMessage(String message) {
		this.deathMessage = message;
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
