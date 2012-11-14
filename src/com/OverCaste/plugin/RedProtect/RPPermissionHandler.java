package com.OverCaste.plugin.RedProtect;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class RPPermissionHandler {
	//Important permission stuff: {
	final Chat permission;
	//}
	public RPPermissionHandler() throws Exception {
		RegisteredServiceProvider<Chat> provider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
		if(provider == null) {
			RedProtect.logger.warning("Vault not found, player limits will be set to the default.");
			permission = null;
			return;
		}
		permission = provider.getProvider();
	}
	
	public boolean hasPerm(Player p, String perm){
		return p.hasPermission(perm);
	}
	
	public boolean hasPerm(String pl, String perm){
		Player p = Bukkit.getServer().getPlayerExact(pl);
		if(p == null) {
			return false;
		}
		return p.hasPermission(perm);
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
	
	public boolean hasHelpPerm(Player p, String s){
		String adminperm = "redprotect.admin." + s;
		String userperm = "redprotect.own." + s;
		return (hasPerm(p, adminperm) || hasPerm(p, userperm));
	}
	
	public int getPlayerLimit(Player p){
		if(permission == null) return RedProtect.limitAmount;
		return permission.getPlayerInfoInteger(p, "maxregionsize", RedProtect.limitAmount);
	}
}
