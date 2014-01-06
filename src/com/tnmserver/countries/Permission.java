package com.tnmserver.countries;

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
	
	public Permission(){
		permissionMap.put("inspect", "coreprotect.inspect");
		permissionMap.put("rollback", "coreprotect.rollback");
		permissionMap.put("lookup", "coreprotect.lookup");
		permissionMap.put("restore", "coreprotect.restore");
		permissionMap.put("ban", "tperms.town.ban");
		permissionMap.put("mute", "tperms.town.mute");
		permissionMap.put("unmute", "tperms.town.unmute");
	}
	
}
