package dev.blue.brawl;

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
		if(cmd.getName().equalsIgnoreCase("setspawn")) {
			if(args.length == 0) {
				main.getConfig().set("Spawn", p.getLocation());
				main.saveConfig();
				p.sendMessage("§6Spawn has been set!");
			}
		}else if(cmd.getName().equalsIgnoreCase("setpots")) {
			if(args.length == 0) {
				main.getConfig().set("EffectsOnStart", p.getActivePotionEffects());
				main.saveConfig();
				p.sendMessage("§6Startup Potions have been set");
			}
		}
		return true;
	}
}
