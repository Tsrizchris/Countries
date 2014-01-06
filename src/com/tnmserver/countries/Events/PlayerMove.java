package com.tnmserver.countries.Events;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.sql.Connector;
import com.tnmserver.countries.tasks.AddPerms;
import com.tnmserver.countries.tasks.RemovePerms;

public class PlayerMove implements Listener{

	private Countries plugin;
	
	public PlayerMove(Countries pl){
		plugin = pl;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent evt){
		new CheckPromoDemo(evt, plugin).runTaskAsynchronously(plugin);
	}
	
}

class CheckPromoDemo extends BukkitRunnable{

	private PlayerMoveEvent evt;
	private Countries plugin;
	
	public CheckPromoDemo(PlayerMoveEvent event, Countries pl){
		evt = event;
		plugin = pl;
	}
	
	@Override
	public void run() {
		if(!evt.isCancelled()){
			String tOne = TownyUniverse.getTownName(evt.getFrom());
			String tTwo = TownyUniverse.getTownName(evt.getTo());
			if((tOne == null && tTwo == null) || tOne==tTwo){
				return;
			}
			if(tOne != null){
				try {
					ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE " + Countries.MEMBER_REFERENCER + " LIKE '%?%'"
							, new String[]{tOne, evt.getPlayer().getName()});
					if(rs.isBeforeFirst()){
						String[] permlist = (String[]) rs.getArray(Countries.PERMISSION_REFERENCER).getArray();
						String delim = "[,]+";
						String[] perms = permlist[0].split(delim);
						new RemovePerms(evt.getPlayer().getName(), perms).runTask(plugin);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(tTwo != null){
				try {
					ResultSet rs = Connector.executePrepStatement("SELECT * FROM ? WHERE " + Countries.MEMBER_REFERENCER + " LIKE '%?%'",
							new String[]{tTwo, evt.getPlayer().getName()});
					if(rs.isBeforeFirst()){
						String[] permlist = (String[]) rs.getArray(Countries.PERMISSION_REFERENCER).getArray();
						String delim = "[,]+";
						String[] perms = permlist[0].split(delim);
						new AddPerms(evt.getPlayer().getName(), perms).runTask(plugin);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	
}
