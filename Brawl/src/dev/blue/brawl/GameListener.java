package dev.blue.brawl;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dev.blue.brawl.events.GameDamageEvent;
import dev.blue.brawl.events.PlayerCombatEvent;
import dev.blue.brawl.events.PlayerDoubleJumpEvent;
import dev.blue.brawl.events.PlayerEliminateEvent;
import dev.blue.brawl.events.PlayerJoinGameEvent;

public class GameListener implements Listener {
	BrawlPlugin main;
	public GameListener(BrawlPlugin main) {
		this.main = main;
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		PlayerJoinGameEvent pjge = new PlayerJoinGameEvent(main, p);
		Bukkit.getPluginManager().callEvent(pjge);
		p.teleport(pjge.getSpawnLocation());
		main.getUtils().resetPots(p);
		if(main.getConfig().getBoolean("Doublejump")) {
			p.setAllowFlight(true);
		}
		if(main.getGameTimer().isContestant(p)) {//game must therefore also still be running, this should allow them to rejoin
			p.setGameMode(main.playmode);
			p.setNoDamageTicks((int) (main.getConfig().getDouble("RespawnInvulnerability")*20));
			AttributeInstance instance = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
			instance.setBaseValue(16);
			main.getSB().setupKillboard(p);
			return;
		}else{
			main.getGameTimer().resetScore(p);
			p.setNoDamageTicks((int) (main.getConfig().getDouble("RespawnInvulnerability")*20));
			if(main.getGameTimer().gameIsRunning()) {
				p.setGameMode(GameMode.SPECTATOR);
				main.getSB().setupKillboard(p);
			}else {
				p.setGameMode(main.playmode);
				main.getGameTimer().addContestant(p);
				main.getSB().setupLeaderboard(p);
			}
		}
		AttributeInstance instance = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		instance.setBaseValue(16);
	}
	
	@EventHandler
	public void onDmg(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		GameDamageEvent gde = new GameDamageEvent(main, (Player)e.getEntity(), e.getCause(), e.getDamage());
		Bukkit.getPluginManager().callEvent(gde);
		if(gde.isCancelled()) {
			e.setCancelled(true);
		}else {
			e.setDamage(gde.getDamage());
		}
	}
	
	@EventHandler
	public void onGameDmg(GameDamageEvent e) {
		if(!main.getGameTimer().gameIsRunning()) {
			e.setCancelled(true);
		}
		if(e.getCause() == DamageCause.FALL) {
			if(!main.getConfig().getBoolean("FallDamage")) {
				e.setCancelled(true);
				return;
			}
		}
		if(e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.LAVA) {
			if(!main.getConfig().getBoolean("FireOrLavaDamage")) {
				e.setCancelled(true);
				return;
			}
		}
		if(e.getCause() == DamageCause.FIRE_TICK) {
			return;
		}
		if(e.getCause() == DamageCause.VOID) {
			e.setDamage(e.getDamage()*10);
			main.getUtils().setDamageCause(e.getPlayer(), main.getUtils().getDamageCause(e.getPlayer())+"%"+e.getCause().toString());
		}
		
		if(e.getCause() == DamageCause.ENTITY_ATTACK || e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
			return;//this should pass the event on to the entitydamageentity event
		}
		BukkitRunnable allowDmg = new BukkitRunnable() {
			@Override
			public void run() {
				e.getPlayer().setNoDamageTicks(0);
				e.getPlayer().setMaximumNoDamageTicks(0);
			}
		};
		allowDmg.runTaskLater(main, 1);
		int living = 0;
		if(main.getGameTimer().gameIsStarting()) {
			e.setCancelled(true);
			if(e.getCause() == DamageCause.VOID) {
				e.getPlayer().teleport(main.getGameTimer().getLobbySpawnLocation());
			}
		}
		for(Player each:Bukkit.getOnlinePlayers()) {
			if(each.getGameMode() == main.playmode) {
				living++;
			}
		}
		if(living <= 1 && main.getGameTimer().gameIsRunning() && main.getGameTimer().winConditionIsMet()) {//This protects the final player from ever dying.
			e.setCancelled(true);
			if(e.getCause() == DamageCause.VOID) {
				e.getPlayer().teleport(main.getGameTimer().getLobbySpawnLocation());
			}
		}
	}
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		Entity attacker = e.getDamager();
		PlayerCombatEvent pce = new PlayerCombatEvent(main, p, attacker, e.getFinalDamage());
		Bukkit.getPluginManager().callEvent(pce);
		if(pce.isCancelled()) {
			return;
		}else {
			main.getUtils().setAttacker(pce.getPlayer(), pce.getAttacker());
			e.setDamage(pce.getDamage());
			main.getUtils().incrementDamageClock(pce.getPlayer());
			int clock = main.getUtils().getDamageClock(pce.getPlayer());
			BukkitRunnable exitCombat = new BukkitRunnable() {
				@Override
				public void run() {
					if(main.getUtils().getDamageClock(pce.getPlayer()) != clock) {
						return;
					}
					main.getUtils().setAttacker(pce.getPlayer(), null);
				}
			};
			exitCombat.runTaskLater(main, 20*pce.getCombatCooldown());
		}
	}
	
	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(main.getGameTimer().isContestant(e.getPlayer())) {
			Bukkit.getPluginManager().callEvent(new PlayerDeathEvent(e.getPlayer(), DamageSource.builder(DamageType.OUT_OF_WORLD).build(), new ArrayList<ItemStack>(), 0, 0, 0, 0, "QUIT"));
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		PlayerEliminateEvent pee = new PlayerEliminateEvent(main, p, main.getUtils().getAttacker(p));
		Bukkit.getPluginManager().callEvent(pee);
		if(e.getDeathMessage().equalsIgnoreCase("QUIT")) {
			//Do something fancy here
		}
		if(pee.isCancelled()) {
			pee.getPlayer().teleport(pee.getRespawnLocation());
			pee.getPlayer().setGameMode(main.playmode);
			pee.getPlayer().setNoDamageTicks((int) (main.getConfig().getDouble("RespawnInvulnerability")*20));
		}else {
			e.setDeathMessage(pee.getDeathMessage());
			e.setDroppedExp(0);
			claimKill(pee, p);
			if(main.getUtils().isLastManStanding()) {
				main.getGameTimer().removeContestant(p);
				BukkitRunnable respawn = new BukkitRunnable() {
					@Override
					public void run() {
						if(main.getGameTimer().gameIsRunning()) {
							pee.getPlayer().teleport(pee.getRespawnLocation());
							pee.getPlayer().setGameMode(GameMode.SPECTATOR);
						}
					}
				};
				respawn.runTaskLater(main, 1);
				if(main.getGameTimer().gameIsStarting()) {
					pee.getPlayer().setGameMode(main.playmode);
				}
				int living = 0;
				for(Player each:Bukkit.getOnlinePlayers()) {
					if(each.getGameMode() == main.playmode) {
						living++;
					}
				}
				if(living <= 1) {
					//end game instantly, as opposed to waiting for the next game tick. 
				}
			}else if(main.getUtils().isBodyCount()) {
				pee.getPlayer().setGameMode(GameMode.SPECTATOR);
				BukkitRunnable respawn = new BukkitRunnable() {
					@Override
					public void run() {
						if(main.getGameTimer().gameIsRunning()) {
							pee.getPlayer().teleport(pee.getRespawnLocation());
							pee.getPlayer().setGameMode(main.playmode);
							pee.getPlayer().setNoDamageTicks((int) (main.getConfig().getDouble("RespawnInvulnerability")*20));
						}
					}
				};
				respawn.runTaskLater(main, 20);
				if(main.getGameTimer().gameIsStarting()) {
					pee.getPlayer().setGameMode(main.playmode);
				}
				main.getUtils().setAttacker(p, null);//Resets their combat after death. Hopefully. 
			}
		}
	}
	
	/**
	 * Claim Kill only happens when a player kills another and not themselves, since suicide is not rewarded. 
	 */
	private void claimKill(PlayerEliminateEvent pee, Player killed) {
		if(main.getUtils().getAttacker(pee.getPlayer()) != null) {
			Entity entity = Bukkit.getEntity(main.getUtils().getAttacker(pee.getPlayer()));
			if(entity != null) {//Here we determine that there was an attacker, and whether it should get a score
				Player attacker = null;
				if(entity instanceof Player) {
					attacker = (Player) entity;
				}else if(entity instanceof Projectile) {
					Projectile proj = (Projectile) entity;
					if(proj.getShooter() instanceof Player) {
						attacker = (Player) proj.getShooter();
					}
				}
				if(attacker != null) {
					if(attacker.getUniqueId() != pee.getPlayer().getUniqueId()) {
						main.getGameTimer().incrementScore(attacker);
						attacker.sendTitle("", "§a§l+1", 0, 5, 10);
						main.getSB().updateScore(attacker);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDoubleJump(PlayerToggleFlightEvent e) {
		if(e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		if(main.getConfig().getBoolean("Doublejump")) {
			Player p = e.getPlayer();
			p.setAllowFlight(false);
			if(p.getGameMode() == main.playmode) {
				e.setCancelled(true);
				PlayerDoubleJumpEvent pdje = new PlayerDoubleJumpEvent(p);
				Bukkit.getPluginManager().callEvent(pdje);
				if(!pdje.isCancelled()) {
					pdje.getPlayer().setVelocity(pdje.getPlayer().getLocation().getDirection().multiply(1.3));
				}
			}
		}else {
			e.getPlayer().setAllowFlight(false);
			e.getPlayer().setFlying(false);
		}
	}
	
	@EventHandler
	public void onHitGround(PlayerMoveEvent event) {
		if(main.getConfig().getBoolean("Doublejump")) {
			Player p = event.getPlayer();
			BigDecimal big = BigDecimal.valueOf(p.getLocation().getY());
			big = big.subtract(new BigDecimal(big.intValue()));
			Double yDecimal = big.doubleValue();
			if ((!p.getLocation().getBlock().getRelative(BlockFace.DOWN).isPassable() || !p.getLocation().getBlock().isPassable()) && yDecimal < 0.51) {
				p.setAllowFlight(true);
			}
		}
	}
	
	@EventHandler
	public void onGameMode(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		if(e.getNewGameMode() == GameMode.CREATIVE) {
			p.setAllowFlight(true);
		}else {
			if(main.getConfig().getBoolean("Doublejump")) {
				p.setAllowFlight(true);
				p.setFlying(false);
			}else {
				p.setAllowFlight(false);
				p.setFlying(false);
			}
		}
	}
}