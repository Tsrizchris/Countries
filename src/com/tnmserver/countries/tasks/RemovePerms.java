package com.tnmserver.countries.tasks;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.tnmserver.countries.Countries;
import com.tnmserver.countries.Permission;

public class RemovePerms extends BukkitRunnable{
	
	private String p;
	private String[] perms;
	
	public RemovePerms(String toRemoveFrom, String[] permissions){
		p = toRemoveFrom;
		perms = permissions;
	}

	@Override
	public void run() {
		if(perms == null)
			this.cancel();
		for(int x = 0; x < perms.length; x++){
			Countries.perms.playerRemove((World) null, p, Permission.permissionMap.get(perms[x]));
		}
	}

}
