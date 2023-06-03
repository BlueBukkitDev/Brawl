package dev.blue.brawl;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

public class Utils {
	private BrawlPlugin main;
	private NamespacedKey key_score;
	private NamespacedKey key_level;
	private NamespacedKey key_attacker;
	private NamespacedKey key_dmgclock;
	private NamespacedKey key_dmgcause;
	
	public Utils(BrawlPlugin main) {
		this.main = main;
		key_score = new NamespacedKey(main, "brawl-score");
		key_level = new NamespacedKey(main, "brawl-level");
		key_attacker = new NamespacedKey(main, "brawl-attacker");
		key_dmgclock = new NamespacedKey(main, "brawl-dmgclock");
		key_dmgcause = new NamespacedKey(main, "brawl-dmgcause");
	}
	
	public void setDamageCause(Player p, String cause) {
		p.getPersistentDataContainer().set(key_dmgcause, PersistentDataType.STRING, cause);
	}
	
	public String getDamageCause(Player p) {
		if(p.getPersistentDataContainer().has(key_dmgcause, PersistentDataType.STRING)) {
			return p.getPersistentDataContainer().get(key_dmgcause, PersistentDataType.STRING);
		}else return "NULL";
	}
	
	public boolean isLastManStanding() {
		return main.getConfig().getString("GameMode").equalsIgnoreCase("LastManStanding");
	}
	
	public boolean isBodyCount() {
		return main.getConfig().getString("GameMode").equalsIgnoreCase("BodyCount");
	}
	
	public void incrementDamageClock(Player p) {
		if(!p.getPersistentDataContainer().has(key_dmgclock, PersistentDataType.INTEGER)) {
			p.getPersistentDataContainer().set(key_dmgclock, PersistentDataType.INTEGER, 0);
			return;
		}
		p.getPersistentDataContainer().set(key_dmgclock, PersistentDataType.INTEGER, p.getPersistentDataContainer().get(key_dmgclock, PersistentDataType.INTEGER)+1);
	}
	
	public int getDamageClock(Player p) {
		if(!p.getPersistentDataContainer().has(key_dmgclock, PersistentDataType.INTEGER)) {
			return 0;
		}
		return p.getPersistentDataContainer().get(key_dmgclock, PersistentDataType.INTEGER);
	}
	
	/**
	 *Stores the attacker's UUID in the PersistentDataContainer of the attacked Player.
	 **/
	public void setAttacker(Player attacked, Entity attacker) {
		if(attacker == null) {
			attacked.getPersistentDataContainer().set(key_attacker, PersistentDataType.STRING, "NA");
			return;
		}
		String uid = attacker.getUniqueId().toString();
		attacked.getPersistentDataContainer().set(key_attacker, PersistentDataType.STRING, uid);
	}
	
	/**
	 *Returns the UUID of the entity responsible for the most recent damage dealt to Player "attacked", as stored in their PersistentDataContainer. 
	 **/
	public UUID getAttacker(Player attacked) {
		if(!attacked.getPersistentDataContainer().has(key_attacker, PersistentDataType.STRING)) {
			return null;
		}
		String content = attacked.getPersistentDataContainer().get(key_attacker, PersistentDataType.STRING);
		if(content.equalsIgnoreCase("NA")) {
			return null;
		}
		try {
			UUID uid = UUID.fromString(content);
			return uid;
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	public void incrementScore(Player p) {
		if(p != null) {
			p.getPersistentDataContainer().set(key_score, PersistentDataType.INTEGER, getScore(p)+1);
		}
	}
	
	public int getScore(Player p) {
		if(p != null) {
			return p.getPersistentDataContainer().get(key_score, PersistentDataType.INTEGER);
		}
		return 0;
	}
	
	public void resetScore(Player p) {
		if(p != null) {
			p.getPersistentDataContainer().set(key_score, PersistentDataType.INTEGER, 0);
		}
	}
	
	public void incrementLevel(Player p) {
		if(p != null) {
			p.getPersistentDataContainer().set(key_level, PersistentDataType.DOUBLE, getExactLevel(p)+0.1);
			main.getConfig().set("Scores."+p.getUniqueId().toString()+".Level", getExactLevel(p));
			main.getConfig().set("Scores."+p.getUniqueId().toString()+".Name", p.getName());
			if((p.getExp()+0.1) >= 1) {
				p.setLevel(p.getLevel()+1);
				p.setExp(0);
				return;
			}
			p.setExp((float)(p.getExp()+0.1));
		}
	}
	
	public int getLevel(Player p) {
		if(p != null) {
			if(p.getPersistentDataContainer().has(key_level, PersistentDataType.DOUBLE)) {
				return (int)Math.floor(p.getPersistentDataContainer().get(key_level, PersistentDataType.DOUBLE));
			}
		}
		return 0;
	}
	
	public double getExactLevel(Player p) {
		if(p != null) {
			if(p.getPersistentDataContainer().has(key_level, PersistentDataType.DOUBLE)) {
				return p.getPersistentDataContainer().get(key_level, PersistentDataType.DOUBLE);
			}
		}
		return 0.0;
	}
	
	public Location spawn() {
		if(main.getConfig().getLocation("Spawn") == null) {
			return Bukkit.getServer().getWorld("world").getSpawnLocation();
		}
		return main.getConfig().getLocation("Spawn");
	}
	
	@SuppressWarnings("unchecked")
	public void resetPots(Player p) {
		for (PotionEffect each : p.getActivePotionEffects()) {
	        p.removePotionEffect(each.getType());
		}
		for(PotionEffect each:(List<PotionEffect>)main.getConfig().getList("EffectsOnStart")) {
			p.addPotionEffect(each);
		}
	}
}
