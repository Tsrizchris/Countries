package com.tnmserver.countries.Events;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.RenameTownEvent;
import com.palmergames.bukkit.towny.event.TownRemoveResidentEvent;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.sql.Connector;
import com.tnmserver.countries.tasks.AddPerms;
import com.tnmserver.countries.tasks.RemovePerms;

public class TownyEventsListener implements Listener{
	
	private Countries plugin;
	
	public TownyEventsListener(Countries pl){
		plugin = pl;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void newTown(NewTownEvent evt){
		new NewTown(evt, plugin).runTaskAsynchronously(plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void townDel(DeleteTownEvent evt){
		new DeleteTown(evt, plugin).runTaskAsynchronously(plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void renameTown(RenameTownEvent evt){
		new RenameTown(evt).runTaskAsynchronously(plugin);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void removeResident(TownRemoveResidentEvent evt){
		new RemoveResident(evt, plugin).runTaskAsynchronously(plugin);
	}
}
class NewTown extends BukkitRunnable{

	private Countries plugin;
	private NewTownEvent evt;
	
	public NewTown(NewTownEvent event, Countries pl){
		plugin = pl;
		evt = event;
	}
	
	@Override
	public void run() {
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
		new AddPerms(evt.getTown().getMayor().getName(), Countries.defaultMayorPerms).runTask(plugin);
	}
	
}

class DeleteTown extends BukkitRunnable{
	private Countries plugin;
	private DeleteTownEvent evt;
	
	public DeleteTown(DeleteTownEvent event, Countries pl){
		plugin = pl;
		evt = event;
	}

	@Override
	public void run() {
		try {
			ResultSet set = Connector.executePrepStatement("SELECT * FROM ?", new String[]{evt.getTownName()});
			String[] playerColumn = (String[]) set.getArray(Countries.MEMBER_REFERENCER).getArray();
			String[] permsColumn = (String[]) set.getArray(Countries.PERMISSION_REFERENCER).getArray();
			for(int x = 0; x< permsColumn.length; x++){
				String delims = "[,]+";
				String[] players = playerColumn[x].split(delims);
				String[] perms = permsColumn[x].split(delims);
				for(int y = 0; y < players.length; y++){
					new RemovePerms(players[y], perms).runTask(plugin);
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
}

class RenameTown extends BukkitRunnable{
	private RenameTownEvent evt;
	
	public RenameTown(RenameTownEvent event){
		evt = event;
	}
	@Override
	public void run() {
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

class RemoveResident extends BukkitRunnable{
	private TownRemoveResidentEvent evt;
	private Countries plugin;
	
	public RemoveResident(TownRemoveResidentEvent event, Countries pl){
		evt = event;
		plugin = pl;
	}
	
	@Override
	public void run() {
		try {
			ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE ? LIKE %?%", new String[]{
				evt.getTown().getName(), Countries.MEMBER_REFERENCER, evt.getResident().getName()
			});
			if(!rs.isBeforeFirst()){
				this.cancel();
			}
			String cPerms = rs.getString(Countries.PERMISSION_REFERENCER);
			String[] perms = cPerms.split("[, ]+");
			new RemovePerms(evt.getResident().getName(), perms).runTask(plugin);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}