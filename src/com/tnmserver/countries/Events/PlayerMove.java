package com.tnmserver.countries.Events;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tnmserver.countries.Countries;
import com.tnmserver.countries.sql.Connector;

public class PlayerMove implements Listener{

	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent evt){
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
						for(int x = 0; x < perms.length; x++){
							Countries.perms.playerRemove((World) null, evt.getPlayer().getName(), perms[x]); 
						}
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
						for(int x = 0; x < perms.length; x++){
							Countries.perms.playerAdd((World) null, evt.getPlayer().getName(), perms[x]); 
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
