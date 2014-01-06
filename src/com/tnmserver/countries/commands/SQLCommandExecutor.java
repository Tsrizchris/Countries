package com.tnmserver.countries.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.Permission;
import com.tnmserver.countries.sql.Connector;
import com.tnmserver.countries.tasks.AddPerms;
import com.tnmserver.countries.tasks.RemovePerms;
import com.tnmserver.countries.tasks.SendMessageSync;

public class SQLCommandExecutor extends BukkitRunnable {

	private Player sender;
	private String[] args;
	
	private static final String syntax = "§9[TPerms] There is an issue in your command syntax, please check again.";
	private static final String noPerm = "§9[TPerms] You do not have permission to do that.";
	private static final String delims = "[, ]+";
	private Countries plugin;
	
	public SQLCommandExecutor(Player sender, String[] args, Countries p){
		this.sender = sender;
		this.args = args;
		plugin = p;
	}
	
	@Override
	public void run() {
		try{
			Town town = TownyUniverse.getDataSource().getResident(sender.getName()).getTown();
			if(town == null){
				sendMessage(sender, "§9[TPerms] You cannot use this command if you are not part of a town.");
				this.cancel();
			}
			if(args.length == 1){
				if(args[0].equalsIgnoreCase("group")){
					if(sender.hasPermission(Permission.groupCommandPerm)){
						ResultSet rs = Connector.executePrepStatement("SELECT * FROM ?", 
							new String[]{town.getName()});
						if(rs.isBeforeFirst()){
							sendMessage(sender, "§9====Groups in the Town of " + town.getName() + "====");
							String[] groups = (String[]) rs.getArray(Countries.GROUP_REFERENCER).getArray();
							for(int x = 0; x < groups.length; x++){
								sendMessage(sender, "§9- " + groups[x]);
							}
						}
						else
							sendMessage(sender, "§9[TPerms] There are no groups in your town.");
					}
					else
						sendMessage(sender, noPerm);
				}
				else
					sendMessage(sender, syntax);
			}
			else if(args.length == 3){
				if(args[1].equalsIgnoreCase("new")){
					if(sender.hasPermission(Permission.groupNew)){
						ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE ?='?'", new String[]{town.getName(),
								Countries.GROUP_REFERENCER, args[2]});
						if(rs.isBeforeFirst()){
							sendMessage(sender, "§9[TPerms] That group already exists, please choose a new name.");
							this.cancel();
						}
						else{
							Connector.executePrepStatement("INSERT INTO ? (?) VALUES (?)", new String[]{town.getName(),
								Countries.GROUP_REFERENCER, args[2]});
						}
					}
					else
						sendMessage(sender, noPerm);
				}
				else if(args[1].equalsIgnoreCase("delete")){
					if(sender.hasPermission(Permission.groupDelete)){
						ResultSet rs = Connector.executePrepStatement("SELECT ? FROM ? WHERE ?='?'", new String[]{
								Countries.MEMBER_REFERENCER, town.getName(), Countries.GROUP_REFERENCER, args[2]
						});
						if(rs.getString(Countries.MEMBER_REFERENCER) == null || rs.getString(Countries.MEMBER_REFERENCER).equalsIgnoreCase("")){
							Connector.executePrepStatement("DELETE FROM ? WHERE ?='?'", new String[]{
									town.getName(), Countries.GROUP_REFERENCER, args[2]
							});
						}
						else{
							sendMessage(sender, "§9[TPerms] You can only delete a group if there is no one in it.");
						}
					}
					else
						sendMessage(sender, noPerm);
				}
				else
					sendMessage(sender, syntax);
			
			}
			else if(args.length == 4){
				if(args[0].equalsIgnoreCase("group")){
					if(args[0].equalsIgnoreCase("add")){
						ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE ?='?'", new String[]{
								town.getName(), Countries.GROUP_REFERENCER, args[1]
							});
							if(!rs.isBeforeFirst()){
								sendMessage(sender, "§9[TPerms] That  group does not exist!");
								this.cancel();
							}
							if(!Permission.permissionMap.containsKey(args[3])){
								sendMessage(sender, "§9[TPerms] You cannot assign that permission!");
								this.cancel();
							}
							if(rs.getString(Countries.PERMISSION_REFERENCER).contains(args[3])){
								sendMessage(sender, "§9[TPerms] That permission is already assigned to that group!");
								this.cancel();
							}
							String cPerms = rs.getString(Countries.PERMISSION_REFERENCER);
							ArrayList<String> perms = new ArrayList<String>(Arrays.asList(cPerms.split(delims)));
							perms.add(args[3]);
							String[] aPerms = (String[]) perms.toArray();
							cPerms = "";
							for(int x = 0; x < aPerms.length; x++){
								if(x == 0)
									cPerms = aPerms[x];
								else
									cPerms = cPerms.concat(",").concat(aPerms[x]);
							}
							Connector.executePrepStatement("UPDATE ? SET ?=? WHERE ?='?'", new String[]{
								town.getName(), Countries.PERMISSION_REFERENCER, cPerms, Countries.GROUP_REFERENCER, args[1]
							});
					}
					else if(args[1].equalsIgnoreCase("remove")){
						ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE ?='?'", new String[]{
							town.getName(), Countries.GROUP_REFERENCER, args[1]
						});
						if(!rs.isBeforeFirst()){
							sendMessage(sender, "§9[TPerms] That  group does not exist!");
							this.cancel();
						}
						if(!Permission.permissionMap.containsKey(args[3])){
							sendMessage(sender, "§9[TPerms] You cannot assign that permission!");
							this.cancel();
						}
						if(!rs.getString(Countries.PERMISSION_REFERENCER).contains(args[3])){
							sendMessage(sender, "§9[TPerms] That permission is not assigned to that group!");
							this.cancel();
						}
						String cPerms = rs.getString(Countries.PERMISSION_REFERENCER);
						ArrayList<String> perms = new ArrayList<String>(Arrays.asList(cPerms.split(delims)));
						perms.remove(args[3]);
						String[] aPerms = (String[]) perms.toArray();
						cPerms = "";
						for(int x = 0; x < aPerms.length; x++){
							if(x == 0)
								cPerms = aPerms[x];
							else
								cPerms = cPerms.concat(",").concat(aPerms[x]);
						}
						Connector.executePrepStatement("UPDATE ? SET ?=? WHERE ?='?'", new String[]{
							town.getName(), Countries.PERMISSION_REFERENCER, cPerms, Countries.GROUP_REFERENCER, args[1]
						});
						String cPlayers = rs.getString(Countries.MEMBER_REFERENCER);
						String [] players = cPlayers.split(delims);
						for(int x = 0; x < players.length; x++){
							new RemovePerms(players[x], new String[]{args[3]}).runTask(plugin);
						}
					}
					else{
						sendMessage(sender, syntax);
					}
				}
				else if(args[0].equalsIgnoreCase("player")){
					if(!TownyUniverse.getDataSource().getResident(args[1]).getTown().getName().equalsIgnoreCase(town.getName())){
						sendMessage(sender, "§9[TPerms] You can only promote a member of your town!");
						this.cancel();
					}
					if(args[2].equalsIgnoreCase("add")){
						if(sender.getName().equalsIgnoreCase(args[1])){
							sendMessage(sender, "§9[TPerms] You cannot add yourself to any other group!");
							this.cancel();
						}
						if(!Bukkit.getServer().getPlayer(args[1]).isOnline()){
							sendMessage(sender, "§9[TPerms] The player has to be online for the command to process!");
							this.cancel();
						}
						
						Location pLoc = Bukkit.getServer().getPlayer(args[1]).getLocation();
						if(!TownyUniverse.getTownName(pLoc).equalsIgnoreCase(town.getName())){
							sendMessage(sender, "§9[TPerms] The player must be in your town for you to do that!");
						}
						removeFromGroup(sender, args);
						ResultSet permsRS = Connector.executePrepStatement("SELECT * FROM ? WHERE ?='?'", new String[]{
							town.getName(), Countries.GROUP_REFERENCER, args[3]
						});
						if(!permsRS.isBeforeFirst()){
							sendMessage(sender, "§9[TPerms] That group does not exist!");
							this.cancel();
						}
						if(permsRS.getString(Countries.MEMBER_REFERENCER).contains(args[1])){
							sendMessage(sender, "§9[TPerms] That person is already a member of that group!");
							this.cancel();
						}
						String cPerms = permsRS.getString(Countries.PERMISSION_REFERENCER);
						String[] perms = cPerms.split(delims);
						new AddPerms(args[1], perms).runTask(plugin);
					}
					else if(args[2].equalsIgnoreCase("remove")){
						if(sender.getName().equalsIgnoreCase(args[1])){
							sendMessage(sender, "§9[TPerms] You cannot remove yourself from your own group!");
							this.cancel();
						}
						if(!Bukkit.getServer().getPlayer(args[1]).isOnline()){
							sendMessage(sender, "§9[TPerms] The player has to be online for the command to process!");
							this.cancel();
						}
						ResultSet permsRS = Connector.executePrepStatement("SELECT * FROM ? WHERE ?='?'", new String[]{
							town.getName(), Countries.GROUP_REFERENCER, args[3]
						});
						if(!permsRS.isBeforeFirst()){
							sendMessage(sender, "§9[TPerms] That group does not exist!");
							this.cancel();
						}
						Location pLoc = Bukkit.getServer().getPlayer(args[1]).getLocation();
						if(!TownyUniverse.getTownName(pLoc).equalsIgnoreCase(town.getName())){
							sendMessage(sender, "§9[TPerms] The player must be in your town for you to do that!");
						}
						boolean q = removeFromGroup(sender, args);
						if(!q)
							sendMessage(sender, "§9[TPerms] That player is not in that group!");
					}
					else
						sendMessage(sender, syntax);
				}
				else
					sendMessage(sender, syntax);
			}
			else{
				sendMessage(sender, syntax);
			}
		}catch(SQLException e){
			sendMessage(sender, "§9[TPerms] There was an error processing your command. Please fill out a bug report at http://mcnations.tk");
		} catch (NotRegisteredException e) {
			sendMessage(sender, "§9[TPerms] You cannot use this command if you are not part of a town.");
		}
	}

	private void sendMessage(Player p, String message){
		new SendMessageSync(p, message).runTask(plugin);
	}
	
	private boolean removeFromGroup(Player sender, String[] args) throws SQLException, NotRegisteredException{
		Town town = TownyUniverse.getDataSource().getResident(sender.getName()).getTown();
		ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE ? LIKE %?%", new String[]{
				town.getName(), Countries.MEMBER_REFERENCER, args[1]
		});
		if(!rs.isBeforeFirst()){
			return false;
		}
		else{
			String cList = rs.getString(Countries.MEMBER_REFERENCER);
			String[] players = cList.split(delims);
			ArrayList<String> pList = new ArrayList<String>(Arrays.asList(players));
			pList.remove(args[1]);
			players = (String[]) pList.toArray();
			cList = "";
			for(int x = 0 ; x < players.length; x++){
				if(x == 0){
					cList = players[x];
				}
				else{
					cList = cList + "," + players[x];
				}
			}
			Location pLoc = Bukkit.getServer().getPlayer(args[1]).getLocation();
			if(TownyUniverse.getTownName(pLoc) == town.getName()){
				ResultSet rs2 = Connector.executePrepStatement("SELECT ? FROM ? WHERE ?='?'", new String[]{
					Countries.PERMISSION_REFERENCER, town.getName(), Countries.GROUP_REFERENCER, args[3]
				});
				if(rs2 != null){
					String cPerms = rs2.getString(Countries.PERMISSION_REFERENCER);
					String[] perms = cPerms.split(delims);
					new RemovePerms(args[1], perms).runTask(plugin);
				}
			}
			Connector.executePrepStatement("UPDATE ? SET ?=? WHERE ?='?'", new String[]{
					town.getName(), Countries.MEMBER_REFERENCER, cList, Countries.GROUP_REFERENCER, args[3]
			});
			return true;
		}
	}
	
}
