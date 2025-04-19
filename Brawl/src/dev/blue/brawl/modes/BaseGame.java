package dev.blue.brawl.modes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.blue.brawl.BrawlPlugin;
import dev.blue.brawl.events.CountdownBeginEvent;
import dev.blue.brawl.events.GameBeginEvent;
import dev.blue.brawl.events.GameTerminateEvent;
import dev.blue.brawl.events.GameWinEvent;

public abstract class BaseGame {
	protected BrawlPlugin main;
	private boolean running = false;
	private boolean starting = false;
	protected int time = 0;
	private int timerTicks = 10;
	protected HashMap<String, Integer> contestants;
	protected Location lobbySpawnLocation;
	
	public BaseGame(BrawlPlugin main) {
		this.main = main;
		setLobbySpawnLocation(main.getUtils().spawn());
		contestants = new HashMap<String, Integer>();
		
		BukkitRunnable timer = new BukkitRunnable() {
			@Override
			public void run() {
				if(gameIsInStasis()) {
					if(hasEnoughPlayers()) {//at least two players
						runCountdown();
					}
				}else if(countdownIsRunning()) {
					broadcastCountdownTime();
				}else if(countdownHasEnded()) {
					tryToInitiateGame();
				}else if(gameIsRunning()) {
					if(gameIsAbandoned()) {
						endGame(false);
						return;
					}
					if(winConditionIsMet()) {
						endGame(true);
					}
				}
				time++;
			}
		};
		timer.runTaskTimer(main, 0, 20);
	}
	
	public void endGame(boolean win) {
		GameTerminateEvent gte = new GameTerminateEvent(win);
		Bukkit.getPluginManager().callEvent(gte);
		if(gte.winnerExists()) {
			Player winner = getWinner();
			GameWinEvent gwe = new GameWinEvent(winner, getScore(winner));
			Bukkit.getPluginManager().callEvent(gwe);
			rewardWinner(gwe.getWinner(), gwe.getScore());
		}
		resetPlayers(gte.getSpawnLocation());
		enterStasis(gte.getSpawnLocation());
	}
	
	public List<String> getContestants() {
		return List.copyOf(contestants.keySet());
	}
	
	public boolean isContestant(Player p) {
		for(String each:getContestants()) {
			if(each.equalsIgnoreCase(p.getUniqueId().toString())) {
				return true;
			}
		}
		return false;
	}
	
	public void addContestant(Player p) {
		contestants.put(p.getUniqueId().toString(), 0);
	}
	
	public void removeContestant(Player p) {
		contestants.remove(p.getUniqueId().toString());
	}
	
	public void addContestants(Collection<String> players) {
		for(String each:players) {
			contestants.put(each, 0);
		}
	}
	
	public void removeContestants(Collection<String> players) {
		for(String each:players) {
			contestants.remove(each);
		}
	}
	
	public void clearContestants() {
		contestants.clear();
	}
	
	public void resetPlayers(Location spawn) {
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(spawn != null) {
				each.teleport(spawn);
			}else {
				each.teleport(main.getGameTimer().lobbySpawnLocation);
			}
			each.setGameMode(main.playmode);
			each.setNoDamageTicks((int) (main.getConfig().getDouble("RespawnInvulnerability")*20));
			main.getUtils().resetPots(each);
			resetScore(each);
		}
		clearContestants();
	}
	
	public void rewardWinner(Player winner, int score) {
		if(winner != null) {
			winner.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 1, false, false, false));
			main.getUtils().incrementLevel(winner);
			main.updateLevelDisplay(winner);
			Bukkit.broadcastMessage("§6"+winner.getDisplayName()+" won with "+score+" points");
		}else {
			Bukkit.broadcastMessage("§6 Game has ended with no clear winner");
		}
	}
	
	public abstract Player getWinner();
	
	public abstract boolean winConditionIsMet();
	
	public abstract void onGameStart();
	
	public List<Player> getActivePlayers() {
		List<Player> actives = new ArrayList<Player>();
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(playerIsActive(each)) {
				actives.add(each);
			}
		}
		return actives;
	}
	
	public void tryToInitiateGame() {
		GameBeginEvent gbe = new GameBeginEvent(main, hasEnoughPlayers(), getPlayablePlayers(), this);
		Bukkit.getPluginManager().callEvent(gbe);
		if(!gbe.isCancelled() && gbe.hasEnoughPlayers()) {
			initiateGame();
		}else if(!gbe.isCancelled() && !gbe.hasEnoughPlayers()) {
			enterStasis(gbe.getPregameSpawnLocation());
		}else if(gbe.isCancelled()) {
			enterStasisQuietly(gbe.getPregameSpawnLocation());
		}
	}
	
	public boolean gameIsAbandoned() {
		return Bukkit.getOnlinePlayers().size() <= 1;
	}
	
	/**
	 *Same as enterStasis(), except this runs no announcement. Should be called when the start event is cancelled. 
	 **/
	public void enterStasisQuietly(Location loc) {
		running = false;
		starting = false;
		resetPlayers(loc);
		main.getSB().initiateLeaderboard();
		time = 0;
	}
	
	public void enterStasis(Location loc) {
		running = false;
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.sendTitle("", "Waiting for players...", 0, 0, 20);
		}
		enterStasisQuietly(loc);
	}
	
	public void initiateGame() {
		main.getSB().initiateKillboard();
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(each.getGameMode() == main.playmode) {
				addContestant(each);
			}
		}
		running = true;
		starting = false;
		time = 0;
		onGameStart();
	}
	
	public void broadcastCountdownTime() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.sendTitle("Starting in", (timerTicks-time)+"...", 0, 0, 20);
			each.playSound(each.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.BLOCKS, 1, 1);
		}
	}
	
	public boolean countdownHasEnded() {
		return time >= timerTicks && !running;
	}
	
	public boolean countdownIsRunning() {
		return time < timerTicks && !running;
	}
	
	public boolean countdownHasBegun() {
		return starting;
	}
	
	public void runCountdown() {
		CountdownBeginEvent cbe = new CountdownBeginEvent(timerTicks);
		Bukkit.getPluginManager().callEvent(cbe);
		if(!cbe.isCancelled()) {
			timerTicks = cbe.getTicks();
			starting = true;
			time = 0;
		}
	}
	
	public boolean gameIsInStasis() {
		return !running && !starting;
	}
	
	public boolean playerIsActive(Player p) {
		return p.getGameMode() == main.playmode;
	}
	
	private List<Player> getPlayablePlayers() {
		List<Player> players = new ArrayList<Player>();
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(each.getGameMode() != GameMode.CREATIVE) {
				players.add(each);
			}
		}
		return players;
	}
	
	public boolean hasEnoughPlayers() {
		int playablePlayers = getPlayablePlayers().size();
		if((time > 2*60 || Bukkit.getOnlinePlayers().size() >= main.minimumPlayers) && playablePlayers > 1) {
			return true;
		}
		return false;
	}
	
	public boolean gameIsRunning() {
		return running;
	}
	
	public boolean gameIsStarting() {
		return starting;
	}

	public Location getLobbySpawnLocation() {
		return lobbySpawnLocation;
	}

	/**
	 *Set this value if you want to control where the players spawn in by default after death, on game start, on void damage, etc. 
	 **/
	public void setLobbySpawnLocation(Location lobbySpawnLocation) {
		this.lobbySpawnLocation = lobbySpawnLocation;
	}
	
	public void incrementScore(Player p) {
		if(p != null && contestants.containsKey(p.getUniqueId().toString())) {
			contestants.put(p.getUniqueId().toString(), getScore(p)+1);
		}
	}
	
	public void incrementScore(Player p, int score) {
		if(p != null && contestants.containsKey(p.getUniqueId().toString())) {
			contestants.put(p.getUniqueId().toString(), getScore(p)+score);
		}
	}
	
	public int getScore(Player p) {
		if(p != null && contestants.containsKey(p.getUniqueId().toString())) {
			return contestants.get(p.getUniqueId().toString());
		}
		return 0;
	}
	
	public void resetScore(Player p) {
		if(p != null && contestants.containsKey(p.getUniqueId().toString())) {
			contestants.put(p.getUniqueId().toString(), 0);
		}
	}
}