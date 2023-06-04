package dev.blue.brawl.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import dev.blue.brawl.BrawlPlugin;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. </br>
*
**/
public class PlayerCombatEvent extends Event implements Cancellable {
	
	private boolean cancelled = false;
	private Player player;
	private Entity attacker;
	private double damage;
	private int combatCooldown;
	private BrawlPlugin main;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	public PlayerCombatEvent(BrawlPlugin main, Player player, Entity attacker, double damage) {
		this.player = player;
		this.attacker = attacker;
		this.damage = damage;
		this.main = main;
		combatCooldown = main.getUtils().combatCooldown;
	}
	
	/**
	 *Sets a cause to be referenced later by the PlayerEliminateEvent; this is purely a quality-of-life method.
	 **/
	public void setCause(String cause) {
		main.getUtils().setDamageCause(player, cause);
	}
	
	public double getDamage() {
		return damage;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public int getCombatCooldown() {
		return combatCooldown;
	}
	
	public void setCombatCooldown(int seconds) {
		combatCooldown = seconds;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Entity getAttacker() {
		return attacker;
	}
	
	public void setAttacker(Entity attacker) {
		this.attacker = attacker;
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
