package dev.blue.brawl.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import dev.blue.brawl.BrawlPlugin;

public class GameDamageEvent extends Event implements Cancellable {
	private Player p;
	private Location loc;
	private boolean cancelled;
	private DamageCause cause;
	private double dmg;
	
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	/**
	 *Gets called by the EntityDamageEvent, only if the entity being damaged is a player. 
	 **/
	public GameDamageEvent(BrawlPlugin main, Player p, DamageCause cause, double dmg) {
		this.p = p;
		this.loc = main.getGameTimer().getLobbySpawnLocation();
		this.setCause(cause);
		this.dmg = dmg;
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

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public DamageCause getCause() {
		return cause;
	}

	public void setCause(DamageCause cause) {
		this.cause = cause;
	}

	public double getDamage() {
		return dmg;
	}

	public void setDamage(double dmg) {
		this.dmg = dmg;
	}
}
