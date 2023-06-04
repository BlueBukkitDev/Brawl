package dev.blue.brawl.modes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
	private int time = 0;
	private int timerTicks = 10;
	protected List<String> contestants;
	
	public BaseGame(BrawlPlugin main) {
		this.main = main;
		contestants = new ArrayList<String>();
		
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
			GameWinEvent gwe = new GameWinEvent(winner, main.getUtils().getScore(winner));
			Bukkit.getPluginManager().callEvent(gwe);
			rewardWinner(gwe.getWinner(), gwe.getScore());
			resetPlayers();
			enterStasis();
		}else {
			resetPlayers();
			enterStasis();
		}
	}
	
	public List<String> getContestants() {
		return contestants;
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
		contestants.add(p.getUniqueId().toString());
	}
	
	public void removeContestant(Player p) {
		contestants.remove(p.getUniqueId().toString());
	}
	
	public void addContestants(Collection<String> players) {
		contestants.addAll(players);
	}
	
	public void removeContestants(Collection<String> players) {
		contestants.removeAll(players);
	}
	
	public void clearContestants() {
		contestants.clear();
	}
	
	public void resetPlayers() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.teleport(main.getUtils().spawn());
			each.setGameMode(GameMode.SURVIVAL);
			each.setNoDamageTicks(30);
			main.getUtils().resetPots(each);
			main.getUtils().resetScore(each);
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
		GameBeginEvent gbe = new GameBeginEvent(hasEnoughPlayers());
		Bukkit.getPluginManager().callEvent(gbe);
		if(!gbe.isCancelled() && gbe.hasEnoughPlayers()) {
			initiateGame();
		}else if(!gbe.isCancelled() && !gbe.hasEnoughPlayers()) {
			enterStasis();
		}else if(gbe.isCancelled()) {
			enterStasisQuietly();
		}
	}
	
	public boolean gameIsAbandoned() {
		return Bukkit.getOnlinePlayers().size() <= 1;
	}
	
	/**
	 *Same as enterStasis(), except this runs no announcement. Should be called when the start event is cancelled. 
	 **/
	public void enterStasisQuietly() {
		running = false;
		starting = false;
		resetPlayers();
		main.getSB().initiateLeaderboard();
		time = 0;
	}
	
	public void enterStasis() {
		for(Player each:Bukkit.getOnlinePlayers()) {
			each.sendTitle("", "Waiting for players...", 0, 0, 20);
		}
		main.getSB().initiateLeaderboard();
		running = false;
		starting = false;
		resetPlayers();
		time = 0;
	}
	
	public void initiateGame() {
		main.getSB().initiateKillboard();
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(each.getGameMode() == GameMode.SURVIVAL) {
				addContestant(each);
			}
		}
		running = true;
		starting = false;
		time = 0;
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
		return p.getGameMode() == GameMode.SURVIVAL;
	}
	
	public boolean hasEnoughPlayers() {
		int playablePlayers = 0;
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(each.getGameMode() != GameMode.CREATIVE) {
				playablePlayers ++;
			}
		}
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
}