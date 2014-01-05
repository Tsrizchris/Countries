package com.tnmserver.countries;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.tnmserver.countries.sql.Connector;

public class Countries extends JavaPlugin {
	
	public static Permission perms = null;
	private Connector c;
	
	public static final String GROUP_REFERENCER = "group_name";
	public static final String MEMBER_REFERENCER = "player_list";
	public static final String PERMISSION_REFERENCER = "permissions";
	public static final String IDENTIFIER = "ID";
	public static String[] defaultMayorPerms;
	public static String commaMayorPerms;

	@Override
	public void onEnable(){
		initVaultPerms();
		loadConfig();
		c = new Connector(this);
		defaultMayorPerms = (String[]) this.getConfig().getStringList("default-mayor-permissions").toArray();
		for(int x = 0; x < defaultMayorPerms.length; x++){
			commaMayorPerms = commaMayorPerms + defaultMayorPerms[x] + ",";
		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void onDisable(){
		try {
			c.getConnection().close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
