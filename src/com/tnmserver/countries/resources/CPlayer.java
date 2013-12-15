package com.tnmserver.countries.resources;

import org.bukkit.entity.Player;

public class CPlayer{
	
	private Player basePlayer;

	public CPlayer(Player p){
		basePlayer = p;
	}
	
	public Player getPlayer(){
		return basePlayer;
	}
}
