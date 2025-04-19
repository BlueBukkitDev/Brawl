package dev.blue.brawl;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmds implements CommandExecutor {
	BrawlPlugin main;
	public Cmds(BrawlPlugin main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		if(!p.isOp()) {
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("brawl")) {
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("setspawn")) {
					main.getConfig().set("Spawn", p.getLocation());
					main.saveConfig();
					p.sendMessage("§6Spawn has been set!");
					return true;
				}else if(args[0].equalsIgnoreCase("setpots")) {
					main.getConfig().set("EffectsOnStart", p.getActivePotionEffects());
					main.saveConfig();
					p.sendMessage("§6Startup Potions have been set");
					return true;
				}
			}
			if(args.length == 2) {
				if(args[0].equalsIgnoreCase("reset")) {
					for(String each: main.getConfig().getConfigurationSection("Scores").getKeys(false)) {
						if(main.getConfig().getString("Scores."+each+".Name").equalsIgnoreCase(args[1])) {
							main.getConfig().set("Scores."+each+".Level", 0);
							main.saveConfig();
							main.updateLevelDisplay(Bukkit.getPlayer(UUID.fromString(each)));
							p.sendMessage("§6Player "+args[1]+"'s score has been reset.");
							return true;
						}
					}
				}
			}
		}
		return true;
	}
}
