package com.tnmserver.countries.tasks;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.tnmserver.countries.Countries;
import com.tnmserver.countries.Permission;

public class AddPerms extends BukkitRunnable {
	
	private String p;
	private String[] perms;
	
	public AddPerms(String toAddTo, String[] permissions){
		p = toAddTo;
		perms = permissions;
	}

	@Override
	public void run() {
		if(perms == null)
			this.cancel();
		for(int x = 0; x < perms.length; x++){
			Countries.perms.playerAdd((World) null, p, Permission.permissionMap.get(perms[x]));
		}
	}

}
