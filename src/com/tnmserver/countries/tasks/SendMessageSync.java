package com.tnmserver.countries.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SendMessageSync extends BukkitRunnable{
	
	private Player p;
	private String msg;
	
	public SendMessageSync(Player player, String message){
		p = player;
		msg = message;
	}

	@Override
	public void run() {
		p.sendMessage(msg);
	}

}
