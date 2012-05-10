package com.OverCaste.plugin.RedProtect;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.nijiko.permissions.PermissionHandler;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class RPPermissionHandler {
	static Permission permType = Permission.OP;
	RedProtect plugin;
	//Important permission stuff: {
	PermissionHandler p3Handler = null;
	WorldsHolder groupManagerHandler = null;
	PermissionManager pexHandler = null;
	//}
	public RPPermissionHandler(RedProtect plugin){
		this.plugin = plugin;
	}
	
	public void initPermissions(){
		Plugin p;
		//SuperPerms
		permType = Permission.BUKKIT_PERMISSIONS;
		//P3
		if ((p = plugin.pm.getPlugin("Permissions")) != null){
			if(p instanceof com.nijikokun.bukkit.Permissions.Permissions){ 
				p3Handler = ((com.nijikokun.bukkit.Permissions.Permissions) p).getHandler();
				if (RedProtect.preferredPerms.equals(Permission.PERMISSIONS_3) || RedProtect.preferredPerms.equals(Permission.DETECT)){
					permType = Permission.PERMISSIONS_3;
					RedProtect.logger.info("Found and will use 'Permissions 3.'");
				}
			}
		}
		//bPerms
		if ((p = plugin.pm.getPlugin("bPermissions")) != null){
			if(p instanceof de.bananaco.bpermissions.imp.Permissions) {
				try{
					if (RedProtect.preferredPerms.equals(Permission.bPERMISSIONS) || RedProtect.preferredPerms.equals(Permission.DETECT)){
						permType = Permission.bPERMISSIONS;
						if (!permType.equals(Permission.BUKKIT_PERMISSIONS)){
							RedProtect.logger.info("Found and will instead use 'bPermissions.'");
						}else{
							RedProtect.logger.info("Found and will use 'bPermissions.'");
						}
					}
				} catch (Exception e){}
			}
		}
		//GM
		if ((p = plugin.pm.getPlugin("GroupManager")) != null){
			if(p instanceof GroupManager){ 
				groupManagerHandler = ((GroupManager) p).getWorldsHolder();
				if (RedProtect.preferredPerms.equals(Permission.GROUP_MANAGER) || RedProtect.preferredPerms.equals(Permission.DETECT)){
					permType = Permission.GROUP_MANAGER;
					if (!permType.equals(Permission.BUKKIT_PERMISSIONS)){
						RedProtect.logger.info("Found and will instead use 'Group Manager.'");
					}else{
						RedProtect.logger.info("Found and will use 'Group Manager.'");
					}
				}
			}
		}
		//PEX
		if ((p = plugin.pm.getPlugin("PermissionsEx")) != null){
			if(p instanceof PermissionsEx){ 
				pexHandler = PermissionsEx.getPermissionManager();
				if (RedProtect.preferredPerms.equals(Permission.PERMISSIONS_EX) || RedProtect.preferredPerms.equals(Permission.DETECT)){
					permType = Permission.PERMISSIONS_EX;
					if (!permType.equals(Permission.BUKKIT_PERMISSIONS)){
						RedProtect.logger.info("Found and will instead use 'PermissionsEX.'");
					}else{
						RedProtect.logger.info("Found and will use 'PermissionsEX.'");
					}
				}
			}
		}
		if (permType.equals(Permission.BUKKIT_PERMISSIONS)){
			if (RedProtect.preferredPerms.equals(Permission.DETECT)){
				RedProtect.logger.info("No compatible permissions plugin detected, using BukkitPermissions.");
			}else{
				RedProtect.logger.info("Using BukkitPermissions.");
			}
		}
	}
	
	public boolean hasPerm(Player p, String s){
		switch (permType){
		case BUKKIT_PERMISSIONS:
			return p.hasPermission(s);
		case PERMISSIONS_3:
			return p3Handler.has(p, s);
		case bPERMISSIONS:
			return p.hasPermission(s);
		case GROUP_MANAGER:
			return groupManagerHandler.getWorldPermissions(p).has(p, s);
		case PERMISSIONS_EX:
			return pexHandler.has(p, s);
		case OP:
			return p.isOp();
		}
		return false;
	}
	
	public boolean hasPerm(String pl, String s){
		Player p = plugin.getServer().getPlayerExact(pl);
		switch (permType){
		case BUKKIT_PERMISSIONS:
			return p.hasPermission(s);
		case PERMISSIONS_3:
			return p3Handler.has(p, s);
		case bPERMISSIONS:
			return p.hasPermission(s);
		case GROUP_MANAGER:
			return groupManagerHandler.getWorldPermissions(p).has(p, s);
		case PERMISSIONS_EX:
			return pexHandler.has(p, s);
		case OP:
			return p.isOp();
		}
		return false;
	}
	
	public boolean hasRegionPerm(Player p, String s, Region poly){
		String adminperm = "redprotect.admin." + s;
		String userperm = "redprotect.own." + s;
		if (poly == null){
			return (hasPerm(p, adminperm)||hasPerm(p, userperm));
		}else{
			return hasPerm(p, adminperm)||(hasPerm(p, userperm)&&poly.isOwner(p));
		}
	}
	
	public boolean helpHasPerm(Player p, String s){
		String adminperm = "redprotect.admin." + s;
		String userperm = "redprotect.own." + s;
		return (hasPerm(p, adminperm) || hasPerm(p, userperm));
	}
	
	public int getPlayerLimit(Player p){
		if(permType.equals(Permission.PERMISSIONS_EX)){
			return pexHandler.getUser(p).getOptionInteger("maxregionsize", p.getWorld().getName(), RedProtect.limitAmount);
		}
		if(permType.equals(Permission.bPERMISSIONS)) {
			String s = ApiLayer.getValue(p.getWorld().getName(), CalculableType.USER, p.getName(), "maxregionsize");
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				return RedProtect.limitAmount;
			}
		}
		return RedProtect.limitAmount;
	}
	
	enum Permission{
		OP,
		BUKKIT_PERMISSIONS,
		PERMISSIONS_3,
		bPERMISSIONS,
		GROUP_MANAGER,
		PERMISSIONS_EX,
		DETECT
	}
}
