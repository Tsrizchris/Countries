package com.tnmserver.countries.Events;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.sql.Connector;

public class TownyEventsListener implements Listener{

	@EventHandler(priority=EventPriority.MONITOR)
	public void newTown(NewTownEvent evt){
		String statement = "CREATE TABLE ? (" + Countries.IDENTIFIER + " int NOT NULL AUTO_INCREMENT, " + 
			Countries.GROUP_REFERENCER + " varchar(255) NOT NULL, " + 
			Countries.PERMISSION_REFERENCER + " longtext, " + 
			Countries.MEMBER_REFERENCER + "longtext, " + 
			"PRIMARY KEY (" + Countries.IDENTIFIER + "));";
		String[] args = new String[1];
		args[0] = evt.getTown().getName();
		String statement2 = "INSERT INTO ? (" + Countries.GROUP_REFERENCER + ", " +
				Countries.PERMISSION_REFERENCER + ", " + 
				Countries.MEMBER_REFERENCER + ") VALUES ('Mayor', " +
				Countries.commaMayorPerms + ", ?)";
		String[] args2 = new String[2];
		args2[0] = evt.getTown().getName();
		args2[1] = evt.getTown().getMayor().getName();
		try {
			Connector.executePrepStatement(statement, args);
			Connector.executePrepStatement(statement2, args2);
		} catch (SQLException e) {
			System.out.println("There was an error creating the town permission data table, please see below for more info.");
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void townDel(DeleteTownEvent evt){
		try {
			ResultSet set = Connector.executePrepStatement("SELECT * FROM ?", new String[]{evt.getTownName()});
			String[] playerColumn = (String[]) set.getArray(4).getArray();
			String[] permsColumn = (String[]) set.getArray(3).getArray();
			for(int x = 0; x< permsColumn.length; x++){
				String delims = "[,]+";
				String[] players = playerColumn[x].split(delims);
				String[] perms = permsColumn[x].split(delims);
				for(int y = 0; y < players.length; y++){
					for(int z = 0; z < perms.length; z++){
						Countries.perms.playerRemove((World) null, players[y], perms[z]); 
					}
				}
			}
			String statement = "DROP ?";
			String[] args = new String[1];
			args[0] = evt.getTownName();
			Connector.executePrepStatement(statement, args);
		}catch (SQLException e) {
			System.out.println("There was an error deleting the town permission data table, please see below for more info.");
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void renameTown(RenameTownEvent evt){
		String statement = "RENAME TABLE ? TO ?";
		String[] args = new String[2];
		args[0] = evt.getOldName();
		args[1] = evt.getTown().getName();
		try {
			Connector.executePrepStatement(statement, args);
		} catch (SQLException e) {
			System.out.println("There was an error renaming the town permission data table, please see below for more info.");
			e.printStackTrace();
		}
	}
}
