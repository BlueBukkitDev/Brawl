package dev.blue.brawl.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
*Marks the initiation of a game. Game state will return to "waiting for players" if the minimum requirement is not met. 
**/
public class GameWinEvent extends Event {
	
	private boolean hasEnoughPlayers;
	private Player winner;
	private int score;
	private static final HandlerList HANDLERS = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
	    return HANDLERS;
	}
	
	/**
	 *Called when a game ends and there is a winner. </br>
	 *@param winner = the player who won</br>
	 *@param score = the number of points accrued before winning
	 **/
	public GameWinEvent(Player winner, int score) {
		this.winner = winner;
		this.score = score;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public void setWinner(Player p) {
		this.winner = p;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public boolean hasEnoughPlayers() {
		return hasEnoughPlayers;
	}
}
