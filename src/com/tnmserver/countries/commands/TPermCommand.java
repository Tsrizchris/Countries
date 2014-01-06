package com.tnmserver.countries.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.tnmserver.countries.Countries;

public class TPermCommand implements CommandExecutor{
	
	private Countries plugin;
	
	public TPermCommand(Countries p){
		plugin = p;
	}
	
	private static final String help = "§c====Towny Permission Help====\n" + 
			"§9/tperms - Display this help message.\n" + 
			"§9/tperms group - List all the permission groups in your town.\n" +
			"§9/tperms group [group] add [permission] - Adds permission to group.\n" +
			"§9/tperms group [group] remove [permission] - Removes permssion from group.\n" +
			"§9/tperms group new [name] - Creates blank group with specified name.\n" +
			"§9/tperms group delete [name] - Deletes the specified group.\n" +
			"§9/tperms player [name] add [group] - Adds player to group.\n" +
			"§9/tperms player [name] remove [group] - Removes player from group.\n" +
			"§c====Towny Permission Help====";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String blah, String[] params) {
		if(cmd.getName().equalsIgnoreCase("tperms")){
			if(!(sender instanceof Player)){
				sender.sendMessage("[TPerms] That command is for players only.");
			}
			else{
				if(params.length == 0 || params[0].equalsIgnoreCase("help") || params[0].equalsIgnoreCase("?")){
					sender.sendMessage(help);
				}
				else{
					new SQLCommandExecutor((Player) sender, params, plugin).runTaskAsynchronously(plugin);
				}
			}
		}
		return true;
	}
}