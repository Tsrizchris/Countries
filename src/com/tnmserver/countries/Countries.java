package com.tnmserver.countries;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Countries extends JavaPlugin {
	
	public static Permission perms = null;

	@Override
	public void onEnable(){
		initVaultPerms();
		loadConfig();
	}
	
	private void initVaultPerms(){
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if(rsp != null){
			perms = rsp.getProvider();
		}
		else{
			getServer().getLogger().log(Level.SEVERE, "Could not hook into Vault for Perms. Plugin disabling");
			getPluginLoader().disablePlugin(this);
		}
	}
	
	private void loadConfig(){
		File dFolder = this.getDataFolder();
		if(dFolder.exists()){
			File config = new File(dFolder, "config.yml");
			if(!config.exists()){
				this.saveDefaultConfig();
			}
		}
		else{
			try {
				dFolder.createNewFile();
				this.saveDefaultConfig();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
