package com.tnmserver.countries.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.palmergames.bukkit.towny.object.TownyUniverse;

public class PlayerMove implements Listener{

	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent evt){
		if(!evt.isCancelled()){
			String tOne = TownyUniverse.getTownName(evt.getFrom());
			String tTwo = TownyUniverse.getTownName(evt.getTo());
			if((tOne == null && tTwo == null) || tOne==tTwo){
				return;
			}
			
		}
	}
	
}
