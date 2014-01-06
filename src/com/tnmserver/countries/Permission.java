package com.tnmserver.countries;

import java.util.ArrayList;
import java.util.HashMap;

public class Permission {

	public static final String groupCommandPerm = "tperms.command.list";
	public static final String groupNew = "tperms.command.group.new";
	public static final String groupDelete = "tperms.command.group.delete";
	public static final String groupAdd = "tperms.command.group.add";
	public static final String groupRemove = "tperms.command.group.remove";
	public static final String playerAdd = "tperms.command.player.add";
	public static final String playerRemove = "tperms.command.player.remove";
	
	public static HashMap<String, String> permissionMap = new HashMap<String, String>();
	public static ArrayList<String> assignablePerms = new ArrayList<String>();
	
	public Permission(){
		permissionMap.put("inspect", "coreprotect.inspect");
		permissionMap.put("rollback", "coreprotect.rollback");
		permissionMap.put("lookup", "coreprotect.lookup");
		permissionMap.put("restore", "coreprotect.restore");
		permissionMap.put("ban", "tperms.town.ban");
		permissionMap.put("unban", "tperms.town.unban");
		permissionMap.put("mute", "tperms.town.mute");
		permissionMap.put("unmute", "tperms.town.unmute");
		permissionMap.put("fly", "essentials.fly");
		permissionMap.put("groupnew", "tperms.command.group.new");
		permissionMap.put("groupdelete", "tperms.command.group.delete");
		permissionMap.put("groupadd", "tperms.command.group.add");
		permissionMap.put("groupremove", "tperms.command.group.remove");
		permissionMap.put("playeradd", "tperms.command.player.add");
		permissionMap.put("playerremove", "tperms.command.player.remove");
		
		assignablePerms.add("inspect");
		assignablePerms.add("rollback");
		assignablePerms.add("lookup");
		assignablePerms.add("restore");
		assignablePerms.add("ban");
		assignablePerms.add("mute");
		assignablePerms.add("unmute");
		
	}
	
}
