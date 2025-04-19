package dev.blue.brawl.events;

import java.util.UUID;

import org.bukkit.Location;
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
	private Location respawnLoc;
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
		this.respawnLoc = main.getGameTimer().getLobbySpawnLocation();
		deathMessage = "§7Player "+player.getName()+" was eliminated";
		this.main = main;
	}
	
	/**
	 *@return The player who was eliminated
	 **/
	public Player getPlayer() {
		return player;
	}
	
	/**
	 *Returns the reason for the death.</br> 
	 *If no reason was set in the PlayerCombatEvent, it will return the most recent DamageCause.</br>
	 *If the player died to the Void after taking damage from a different cause, this will return as follows:</br></br>
	 *<code>DAMAGE_CAUSE$VOID</code></br></br>
	 *This allows you to split at '<code>$</code>', allowing you to determine who caused them to die to the void. 
	 **/
	public String getReason() {
		return main.getUtils().getDamageCause(player);
	}
	
	/**
	 *Gets the UUID of the entity responsible for the death. The UUID is returned instead of the entity, because often the 
	 *killer will die and no longer exist when this event is called. Be sure to check if the entity with this UUID exists 
	 *and is alive BEFORE referencing it. </br></br>
	 *@return The UUID of the killer entity
	 **/
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
	
	/**
	 *Sets the location the player will respawn. If you are using a waiting lobby without respawns, set this location to the lobby.
	 **/
	public void setRespawnLocation(Location loc) {
		this.respawnLoc = loc;
	}
	
	/**
	 *@return A respawn <code>Location</code> IF that location has been set while the event was interrupted.
	 **/
	public Location getRespawnLocation() {
		return respawnLoc;
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
