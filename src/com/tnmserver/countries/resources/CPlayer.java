package com.tnmserver.countries.resources;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class CPlayer{
	
	private Player basePlayer;
	private Town town;
	private Resident cResident;

	public CPlayer(Player p){
		basePlayer = p;
		try {
			cResident = TownyUniverse.getDataSource().getResident(p.getName());
			town = cResident.getTown();
		} catch (NotRegisteredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Player getPlayer(){
		return basePlayer;
	}
	
	public Town townBelongTo(){
		return town;
	}
	public Resident getResident(){
		return cResident;
	}
}
