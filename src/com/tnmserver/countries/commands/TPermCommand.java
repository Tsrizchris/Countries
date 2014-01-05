package com.tnmserver.countries.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.sql.Connector;

public class TPermCommand implements CommandExecutor{
	
	private static final String help = "§c====Towny Permission Help====\n" + 
			"§9/tperms - Display this help message.\n" + 
			"§9/tperms group - List all the permission groups in your town.\n" +
			"§9/tperms group add [permission] - Adds permission to group.\n" +
			"§9/tperms group remove [permission] - Removes permssion from group.\n" +
			"§9/tperms group new [name] - Creates blank group with specified name.\n" +
			"§9/tperms group delete [name] - Deletes the specified group.\n" +
			"§9/tperms player [name] add [group] - Adds player to group.\n" +
			"§9/tperms player [name] remove [group] - Removes player from group.\n" +
			"§c====Towny Permission Help====";
	private static final String syntax = "[TPerms] There is an issue in your command syntax, please check again.";
	private static final String noPerm = "[TPerms] You do not have permission to do that.";
	private static final String delims = "[,]+";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String blah, String[] params) {
		if(cmd.getName().equalsIgnoreCase("tperms")){
			if(!(sender instanceof Player)){
				sender.sendMessage("[TPerms] That command is for players only.");
				return true;
			}
			else{
				Player p = (Player) sender;
				try{
				if(params.length == 0){
					sender.sendMessage(help);
					return true;
				}
				else if(params.length == 1){
					if(params[0].equalsIgnoreCase("help") || params[0].equalsIgnoreCase("?")){
						sender.sendMessage(help);
						return true;
					}
					else if(params[0].equalsIgnoreCase("group")){
						if(!(p.hasPermission("tperms.group.see"))){
							p.sendMessage(noPerm);
							return true;
						}
						String statement = "SELECT * FROM ?";
						String[] args = new String[]{TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName()};
						ResultSet rs = Connector.executePrepStatement(statement, args);
						if(rs.isBeforeFirst()){
							String[] names = (String[]) rs.getArray(Countries.GROUP_REFERENCER).getArray();
							p.sendMessage("§c====Groups in the " + TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName() + " Town");
							for(int x = 0; x < names.length; x++){
								p.sendMessage("§9- " + names[x] + "\n");
							}
						}
					}
					else{
						sender.sendMessage(syntax);
					}
				}
				else if(params.length == 3){
					if(params[0].equalsIgnoreCase("group")){
						if(params[1].equalsIgnoreCase("add")){
							
						}
						else if(params[1].equalsIgnoreCase("remove")){
							
						}
						else if(params[1].equalsIgnoreCase("new")){
							if(!(p.hasPermission("tperms.group.new"))){
								p.sendMessage(noPerm);
								return true;
							}
							String statement = "INSERT INTO ? (" + Countries.GROUP_REFERENCER + ") VALUES ('?')";
							String[] args = new String[]{TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName(), params[2]};
							Connector.executePrepStatement(statement, args);
						}
						else if(params[1].equals("delete")){
							if(!(p.hasPermission("tperms.group.delete." + params[2]))){
								p.sendMessage(noPerm);
								return true;
							}
							String statement = "DELETE FROM ? WHERE " + Countries.GROUP_REFERENCER + "='?'";
							String[] args = new String[]{TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName(), params[2]};
							Connector.executePrepStatement(statement, args);
						}
						else{
							sender.sendMessage(syntax);
						}
					}
					else{
						sender.sendMessage(syntax);
					}
				}
				else if(params.length == 4){
					if(params[0].equalsIgnoreCase("player")){
						if(params[2].equalsIgnoreCase("add")){
							if(p.hasPermission("tperms.player.addto." + params[3])){
								if(!(Bukkit.getServer().getPlayer(params[1]).isOnline())){
									p.sendMessage("§9[TPerms] The operation cannot be completed because the player is not online.");
									return true;
								}
								ResultSet rs = Connector.executePrepStatement("SELECT " + Countries.MEMBER_REFERENCER + " FROM ? WHERE " + Countries.GROUP_REFERENCER
										+ "='?'", new String[]{TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName(), params[3]});
								if(rs.isBeforeFirst()){
									String[] players = (String[]) rs.getArray(Countries.MEMBER_REFERENCER).getArray();
									players[0] = players[0] + params[1] + ",";
									Connector.executePrepStatement("UPDATE ? SET " + Countries.MEMBER_REFERENCER + "=? WHERE " + Countries.GROUP_REFERENCER + "='?'", 
											new String[]{TownyUniverse.getDataSource().getResident(p.getName()).getTown().getName(), players[0], params[3]});
									
								}
								else{
									p.sendMessage("§9[TPerms] That group does not exist.");
								}
							}
							else{
								p.sendMessage(noPerm);
							}
						}
						else if(params[2].equalsIgnoreCase("remove")){
							if(p.getName() == params[1]){
								p.sendMessage("§9[TPerms] You can't remove yourself from your own group!");
								return true;
							}
							Object[] message = removeFromGroup(p, params[1], params[3]);
							if(message.length == 2){
								p.sendMessage("§9[TPerms] Your command failed because " + message[1]);
							}
						}
						else{
							sender.sendMessage(syntax);
						}
					}
					else{
						sender.sendMessage(syntax);
					}
				}
				else{
					sender.sendMessage(syntax);
				}
			}catch(NotRegisteredException e){
				p.sendMessage("[TPerms] You aren't part of a town so you can't use this command.");
			} catch (SQLException e) {
				e.printStackTrace();
				p.sendMessage("§4[TPerms] There was an internal error processing your request. Please submit a bug form at http://mcnations.tk");
			}
		}}
		return true;
	}
	
	private Object[] removeFromGroup(Player sender, String toAdd, String group) throws NotRegisteredException, SQLException{
		ResultSet rs = Connector.executePrepStatement("SELECT " + Countries.MEMBER_REFERENCER + " FROM ? WHERE " + Countries.GROUP_REFERENCER + "='?'", 
				new String[]{TownyUniverse.getDataSource().getResident(sender.getName()).getTown().getName(), group});
		if(!rs.isBeforeFirst()){
			return new Object[]{false, "the specified group doesn't exist."};
		}
		ResultSet rs2 = Connector.executePrepStatement("SELECT * FROM ? WHERE " + Countries.MEMBER_REFERENCER + " LIKE '%?%'", 
				new String[]{TownyUniverse.getDataSource().getResident(sender.getName()).getTown().getName(), toAdd});
		if(!rs2.isBeforeFirst()){
			return new Object[]{false, "the player is not a member of the group."};
		}
		if(TownyUniverse.getDataSource().getResident(toAdd).isMayor()){
			return new Object[]{false, "you cannot change the mayor's group."};
		}
		String[] players = (String[]) rs.getArray(Countries.MEMBER_REFERENCER).getArray();
		players = players[0].split(delims);
		ArrayList<String> ps = new ArrayList<String>(Arrays.asList(players));
		ps.remove(toAdd);
		players = (String[]) ps.toArray();
		String cPlayer = "";
		for(int z = 0; z < players.length; z++){
			cPlayer = cPlayer + players[z];
		}
		Connector.executePrepStatement("UPDATE ? SET " + Countries.MEMBER_REFERENCER + "=? WHERE " + Countries.GROUP_REFERENCER + "='?'", 
				new String[]{TownyUniverse.getDataSource().getResident(sender.getName()).getTown().getName(), cPlayer, group});
		String[] perms = (String[]) rs2.getArray(Countries.PERMISSION_REFERENCER).getArray();
		perms = perms[0].split(delims);
		for(int x = 0; x < perms.length; x++){
			Countries.perms.playerRemove((World) null, toAdd, perms[x]);
		}
		return new Object[]{true};
	}
}