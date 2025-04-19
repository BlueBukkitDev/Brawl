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
	private NamespacedKey key_attacker;
	private NamespacedKey key_dmgclock;
	private NamespacedKey key_dmgcause;
	public int combatCooldown = 10;
	
	public Utils(BrawlPlugin main) {
		this.main = main;
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
	
	public boolean isHourglass() {
		return main.getConfig().getString("GameMode").equalsIgnoreCase("Hourglass");
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
	
	public void incrementLevel(Player p) {
		if(p != null) {
			main.getConfig().set("Scores."+p.getUniqueId().toString()+".Wins", getLevel(p)+1);
			main.getConfig().set("Scores."+p.getUniqueId().toString()+".Name", p.getName());
			main.saveConfig();
			//if((p.getExp()+0.1) >= 1) {
			//	p.setLevel(p.getLevel()+1);
			//	p.setExp(0);
			//	return;
			//}
			//p.setExp((float)(p.getExp()+0.1));
		}
	}
	
	public int getLevel(Player p) {
		if(p != null) {
			if(main.getConfig().getConfigurationSection("Scores."+p.getUniqueId().toString()) == null) {
				main.getConfig().set("Scores."+p.getUniqueId().toString()+".Wins", 0);
				main.getConfig().set("Scores."+p.getUniqueId().toString()+".Name", p.getName());
				main.saveConfig();
			}
			return (int) Math.floor(main.getConfig().getDouble("Scores."+p.getUniqueId().toString()+".Wins"));
		}
		return 0;
	}
	
	public Location spawn() {
		if(main.getConfig().getLocation("Spawn") == null) {
			return Bukkit.getServer().getWorld("world").getSpawnLocation();
		}
		return main.getConfig().getLocation("Spawn");
	}
	
	@SuppressWarnings("unchecked")
	public void resetPots(Player p) {
		if(!main.getConfig().getBoolean("ResetPots")) {
			return;
		}
		for (PotionEffect each : p.getActivePotionEffects()) {
	        p.removePotionEffect(each.getType());
		}
		for(PotionEffect each:(List<PotionEffect>)main.getConfig().getList("EffectsOnStart")) {
			p.addPotionEffect(each);
		}
	}
}
