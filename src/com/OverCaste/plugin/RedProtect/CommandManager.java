package com.OverCaste.plugin.RedProtect;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import static com.OverCaste.plugin.RedProtect.RedProtect.*;
import static org.bukkit.ChatColor.*;


public class CommandManager implements CommandExecutor {
	
	static final String NOT_IN_REGION_MESSAGE = ChatColor.RED + "You need to be in a region or define a region to do that!";
	static final String NO_PERMISSION_MESSAGE = ChatColor.RED + "You don't have permission to do that!";
	private static void sendNotInRegionMessage(Player p) {
		p.sendMessage(NOT_IN_REGION_MESSAGE);
	}
	
	private static void sendNoPermissionMessage(Player p) {
		p.sendMessage(NO_PERMISSION_MESSAGE);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)  {
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't use RedProtect from the console!");
			return true;
		}
		Player player = (Player)sender;
		if(args.length == 0){
			//DEBUG: player.getWorld().getBlockAt((int)player.getLocation().getX(), (int)player.getLocation().getY()+1, (int)player.getLocation().getZ()).setType(Material.BOOKSHELF);
			player.sendMessage(AQUA + "redProtect version " + pdf.getVersion());
			player.sendMessage(AQUA + "Developed by (" + GOLD + "ikillforeyou [aka. OverCaste]" + AQUA + ").");
			player.sendMessage(AQUA + "For more information about the commands, type [" + GOLD + "/rp ?" + AQUA + "].");
			player.sendMessage(AQUA + "For a tutorial, type [" + GOLD + "/rp tutorial" + AQUA + "].");
			return true;
		}
		else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("?")||args[0].equalsIgnoreCase("help")){
				player.sendMessage(AQUA + "Available commands to you: ");
				player.sendMessage(AQUA + "------------------------------------");
				if (ph.hasHelpPerm(player, "limit")){
					player.sendMessage(GREEN + "/rp limit");
				}
				if (ph.hasHelpPerm(player, "list")){
					player.sendMessage(GREEN + "/rp list");
				}
				if (ph.hasHelpPerm(player, "delete")){
					player.sendMessage(GREEN + "/rp delete");
				}
				if (ph.hasHelpPerm(player, "info")) {
					player.sendMessage(GREEN + "/rp info");
				}
				if (ph.hasHelpPerm(player, "addmember")){
					player.sendMessage(GREEN + "/rp addmember (player)");
				}
				if (ph.hasHelpPerm(player, "addowner")){
					player.sendMessage(GREEN + "/rp addowner (player)");
				}
				if (ph.hasHelpPerm(player, "removemember")){
					player.sendMessage(GREEN + "/rp removemember (player)");
				}
				if (ph.hasHelpPerm(player, "removeowner")){
					player.sendMessage(GREEN + "/rp removeowner (player)");
				}
				if (ph.hasHelpPerm(player, "rename")){
					player.sendMessage(GREEN + "/rp rename (name)");
				}
				if (ph.hasPerm(player, "redprotect.near")){
					player.sendMessage(GREEN + "/rp near");
				}
				player.sendMessage(GREEN + "/rp flag");
				player.sendMessage(AQUA + "------------------------------------");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("limit")||args[0].equalsIgnoreCase("limitremaining")||args[0].equalsIgnoreCase("remaining")){
				if(ph.hasPerm(player, "redprotect.own.limit")){
					int limit = ph.getPlayerLimit(player);
					if((limit < 0) || (ph.hasPerm(player, "redprotect.unlimited"))){
						player.sendMessage(AQUA + "You have no limit!");
						return true;
					}
					
					int currentUsed = rm.getTotalRegionSize(player.getName());
					player.sendMessage(AQUA + "Your area: (" + GOLD + currentUsed + AQUA + " / " + GOLD + limit  + AQUA + ").");
					return true;
				} else {
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ls")){
				if(ph.hasPerm(player, "redprotect.own.list")){
					Set<Region> regions = rm.getRegions(player);
					if(regions.size() == 0){
						player.sendMessage(AQUA + "You don't have any regions!");
					} else {
						player.sendMessage(AQUA + "Regions you've created:");
						player.sendMessage(AQUA + "------------------------------------");
						Iterator<Region> i = regions.iterator();
						while(i.hasNext()) {
							player.sendMessage(AQUA + i.next().info());
						}
						player.sendMessage(AQUA + "------------------------------------");
					}
					return true;
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("tutorial") || args[0].equalsIgnoreCase("tut")){
				player.sendMessage(AQUA + "Tutorial:");
				player.sendMessage(AQUA + "1. Surround your creation with " + RPUtil.formatName(Material.getMaterial(blockID).name()) + ".");
				player.sendMessage(AQUA + "2. Place a sign next to your region, with [rp] on the first line.");
				player.sendMessage(AQUA + "3. Enter the name you want your region to be on the 2nd line, or nothing for an automatic name.");
				player.sendMessage(AQUA + "4. Enter 2 additional owners, if you want, on lines 3 & 4.");
				//player.sendMessage(AQUA + "To protect your region, surround your creation with " + ", then place a sign with [rp] on the first line next to it.");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("near") || args[0].equalsIgnoreCase("nr")){
				if(ph.hasPerm(player, "redprotect.near")){
					Set<Region> regions = rm.getRegionsNear(player, 30, player.getWorld());
					if(regions.size() == 0){
						player.sendMessage(AQUA + "There are no regions nearby.");
					}else{
						Iterator<Region> i = regions.iterator();
						player.sendMessage(AQUA + "Regions within 40 blocks: ");
						player.sendMessage(AQUA + "------------------------------------");
						while(i.hasNext()){
							Region r = i.next();
							player.sendMessage(AQUA + "Name: " + GOLD + r.getName() + AQUA + ", Center: [" + GOLD + r.getCenterX() + AQUA + ", " + GOLD + r.getCenterZ() + AQUA + "].");
						}
						player.sendMessage(AQUA + "------------------------------------");
					}
				}else{
					player.sendMessage(RED + "You don't have permission to do that.");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("flag")) {
				player.sendMessage(AQUA + "To use the command, type '/rp (flag)'. This will toggle the specific flag if you have permission.");
				player.sendMessage(AQUA + "Type '/rp flag info' to show the status of flags in the region you're currently in.");
				return true;
			}
		}
		else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("redefine")) {
				if(!player.hasPermission("redprotect.admin.redefine")) {
					player.sendMessage(RED + "You don't have permission to do that!");
					return true;
				}
				String name = args[1];
				Region oldRect = rm.getRegion(name, player.getWorld());
				if(oldRect == null) {
					player.sendMessage(RED + "That region doesn't exist!");
					return true;
				}
				RedefineRegionBuilder rb = new RedefineRegionBuilder(player, oldRect, firstLocationSelections.get(player), secondLocationSelections.get(player));
				if(rb.ready()) {
					Region r = rb.build();
					player.sendMessage(GREEN + "Successfully created region: " + r.getName() + ".");
					RedProtect.rm.remove(oldRect);
					RedProtect.rm.add(r, player.getWorld());
				}
				return true;
			}
		}
		else if(args.length <= 3) {
			if(args[0].equalsIgnoreCase("define")) {
				if(!player.hasPermission("redprotect.admin.define")) {
					player.sendMessage(RED + "You don't have permission to do that!");
					return true;
				}
				String name = (args.length >= 2) ? args[1] : "";
				String creator = (args.length == 3) ? args[2] : player.getName().toLowerCase();
				RegionBuilder rb = new DefineRegionBuilder(player, firstLocationSelections.get(player), secondLocationSelections.get(player), name, creator);
				if(rb.ready()) {
					Region r = rb.build();
					player.sendMessage(GREEN + "Successfully created region: " + r.getName() + ".");
					RedProtect.rm.add(r, player.getWorld());
				};
				return true;
			}
		}
		//Things you need to be in a rect to do:
		//Length 1:
		if(args.length <= 0) {
			return false; //wrong number of args.
		}
		if (args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("del")) {
			if(args.length == 1) {
				CommandManager.handleDelete(player, rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 2) {
				CommandManager.handleDelete(player, rm.getRegion(args[1], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("i")||args[0].equalsIgnoreCase("info")) {
			if(args.length == 1) {
				CommandManager.handleInfo(player, rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 2) {
				CommandManager.handleInfo(player, rm.getRegion(args[1], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("am")||args[0].equalsIgnoreCase("addmember")) {
			if(args.length == 2) {
				CommandManager.handleAddMember(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleAddMember(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("ao")||args[0].equalsIgnoreCase("addowner")) {
			if(args.length == 2) {
				CommandManager.handleAddOwner(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleAddOwner(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("rm")||args[0].equalsIgnoreCase("removemember")){
			if(args.length == 2) {
				CommandManager.handleRemoveMember(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleRemoveMember(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("ro")||args[0].equalsIgnoreCase("removeowner")){
			if(args.length == 2) {
				CommandManager.handleRemoveOwner(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleRemoveOwner(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("rn")||args[0].equalsIgnoreCase("rename")){
			if(args.length == 2) {
				CommandManager.handleRename(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleRename(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("fl")||args[0].equalsIgnoreCase("flag")){
			if(args.length == 2) {
				CommandManager.handleFlag(player, args[1], rm.getRegion(player, player.getWorld()));
				return true;
			} else if(args.length == 3) {
				CommandManager.handleFlag(player, args[1], rm.getRegion(args[2], player.getWorld()));
				return true;
			}
			return false;
		}
		if (args[0].equalsIgnoreCase("list")||args[0].equalsIgnoreCase("ls")) {
			if(args.length == 1) {
				CommandManager.handleList(player, player.getName());
				return true;
			}
			if(args.length == 2) {
				CommandManager.handleList(player, args[1]);
			}
			return false;
		}
		return false;
	}
	
	public static void handleDelete(Player p, Region r) {
		if(ph.hasRegionPerm(p, "delete", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			p.sendMessage(ChatColor.AQUA +  "Region successfully deleted.");
			rm.remove(r);
		} else {
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleInfo(Player p, Region r) {
		if (ph.hasRegionPerm(p, "info", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			p.sendMessage(r.info());
		} else {
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleAddMember(Player p, String sVictim, Region r) {
		if (ph.hasRegionPerm(p, "addmember", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			Player pVictim = serv.getPlayerExact(sVictim);
			sVictim = sVictim.toLowerCase();
			if (r.isOwner(sVictim)) {
				r.removeOwner(sVictim);
				r.addMember(sVictim);				if (pVictim != null) {
					if (pVictim.isOnline()) {
						pVictim.sendMessage(AQUA + "You have been demoted to member in: " + GOLD + r.getName() + AQUA + ", by: " + GOLD + p.getName() + AQUA + ".");
					}
				}
				p.sendMessage(AQUA + "Demoted player " + GOLD + sVictim + AQUA + " to member in " + GOLD + r.getName() + AQUA + ".");
			} else {
				if (!r.isMember(sVictim)) {
					r.addMember(sVictim);
					p.sendMessage(AQUA + "Added " + GOLD + sVictim + AQUA + " as a member.");
					if (pVictim != null){
						if (pVictim.isOnline()){
							pVictim.sendMessage(AQUA + "You have been added as a member to region: " + GOLD + r.getName() + AQUA + ", by: " + GOLD + p.getName() + AQUA + ".");
						}
					}
				} else {
					p.sendMessage(RED + sVictim + " is already a member in this region.");
				}
			}
		} else {
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleAddOwner(Player p, String sVictim, Region r) {
		if (ph.hasRegionPerm(p, "addowner", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			Player pVictim = serv.getPlayerExact(sVictim);
			sVictim = sVictim.toLowerCase();
			if (!r.isOwner(sVictim)) {
				r.addOwner(sVictim);
				p.sendMessage(AQUA + "Added " + GOLD + sVictim + AQUA + " as an owner.");
				if (pVictim != null) {
					if (pVictim.isOnline()) {
						pVictim.sendMessage(AQUA + "You have been added as an owner to region: " + GOLD + r.getName() + AQUA + ", by: " + GOLD + p.getName() + AQUA + ".");
					}
				}
			} else {
				p.sendMessage(RED + "That player is already an owner in this region!");
			}
		} else {
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleRemoveMember(Player p, String sVictim, Region r) {
		if (ph.hasRegionPerm(p, "removemember", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			Player pVictim = serv.getPlayerExact(sVictim);
			sVictim = sVictim.toLowerCase();
			if (r.isMember(sVictim) || r.isOwner(sVictim)) {
				p.sendMessage(AQUA + "Removed " + GOLD + sVictim + AQUA + " from this region.");
				r.removeMember(sVictim);
				if (pVictim != null) {
					if (pVictim.isOnline()) {
						pVictim.sendMessage(AQUA + "You have been removed as a member from region: " + GOLD + r.getName() + AQUA + ", by: " + GOLD + p.getName() + AQUA + ".");
					}
				}
			} else {
				p.sendMessage(RED + sVictim + " isn't a member of this region.");
			}
		} else {
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleRemoveOwner(Player p, String sVictim, Region r) {
		if (ph.hasRegionPerm(p, "removeowner", r)) {
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			Player pVictim = serv.getPlayerExact(sVictim);
			sVictim = sVictim.toLowerCase();
			if (r.isOwner(sVictim)) {
				if (r.ownersSize() > 1){
					p.sendMessage(AQUA + "Made " + GOLD + sVictim + AQUA + " a member in this region.");
					r.removeOwner(sVictim);
					r.addMember(sVictim);
					if (pVictim != null) {
						if (pVictim.isOnline()) {
							pVictim.sendMessage(AQUA + "You have been removed as an owner from region: " + GOLD + r.getName() + AQUA + ", by: " + GOLD + p.getName() + AQUA + ".");
						}
					}
				} else {
					p.sendMessage(AQUA + "You can't remove " + GOLD + sVictim + AQUA + ", because they are the last owner in this region.");
				}
			}else{
				p.sendMessage(RED + sVictim + " isn't an owner in this region.");
			}
		}else{
			sendNoPermissionMessage(p);
		}
	}
	
	public static void handleRename(Player p, String newName, Region r) {
		if (ph.hasRegionPerm(p, "rename", r)){
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			if (rm.getRegion(newName, p.getWorld()) != null){
				p.sendMessage(RED + "That name is already taken, please choose another one.");
				return;
			}
			if((newName.length() < 2) || (newName.length() > 16)){
				p.sendMessage(RED + "Invalid name. Please enter a 2-16 character name.");
				return;
			}
			if (newName.contains(" ")){
				p.sendMessage(RED + "The name of the region can't have a space in it.");
				return;
			}
			rm.rename(r, newName, p.getWorld());
			p.sendMessage(AQUA + "Made " + GOLD + newName + AQUA + " the new name for this region.");
		}else{
			p.sendMessage(RED + "You don't have sufficient permission to do that.");
		}
	}
	
	public static void handleFlag(Player p, String flag, Region r) {
		if(flag.equalsIgnoreCase("pvp")) {
			if(ph.hasPerm(p, "redprotect.flag.pvp")) {
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					rm.setFlag(r, 0 /*pvp*/, !r.getFlag(0), p.getWorld());
					p.sendMessage(AQUA + "Flag \"pvp\" has been set to " + r.getFlag(0) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("chest")) {
			if(ph.hasPerm(p, "redprotect.flag.chest")){
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(1 /*pvp*/, !r.getFlag(1));
					p.sendMessage(AQUA + "Flag \"chest\" has been set to " + r.getFlag(1) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("lever")) {
			if(ph.hasPerm(p, "redprotect.flag.lever")){
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(2 /*pvp*/, !r.getFlag(2));
					p.sendMessage(AQUA + "Flag \"lever\" has been set to " + r.getFlag(2) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("button")) {
			if(ph.hasPerm(p, "redprotect.flag.button")){
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(3 /*pvp*/, !r.getFlag(3));
					p.sendMessage(AQUA + "Flag \"button\" has been set to " + r.getFlag(3) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("door")) {
			if(ph.hasPerm(p, "redprotect.flag.door")){
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(4 /*pvp*/, !r.getFlag(4));
					p.sendMessage(AQUA + "Flag \"door\" has been set to " + r.getFlag(4) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("mobs")) {
			if(ph.hasPerm(p, "redprotect.flag.mobs")) {
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(5 /*pvp*/, !r.getFlag(5));
					p.sendMessage(AQUA + "Flag \"mobs\" has been set to " + r.getFlag(5) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("passives")) {
			if(ph.hasPerm(p, "redprotect.flag.passives")) {
				if(r == null) {
					sendNotInRegionMessage(p);
					return;
				}
				if(r.isOwner(p)||ph.hasPerm(p, "redprotect.admin.flag")){
					r.setFlag(6 /*pvp*/, !r.getFlag(6));
					p.sendMessage(AQUA + "Flag \"passives\" has been set to " + r.getFlag(6) + ".");
				} else {
					p.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
				}
			} else {
				p.sendMessage(RED + "You don't have permission to toggle that flag!");
			}
		}
		else if(flag.equalsIgnoreCase("info")||flag.equalsIgnoreCase("i")){
			if(r == null) {
				sendNotInRegionMessage(p);
				return;
			}
			p.sendMessage(AQUA + "Flag values: (" + r.getFlagInfo() + AQUA + ")");
		}
		else {
			p.sendMessage(AQUA + "List of flags: [pvp, chest, lever, button, door, mobs, passives]");
		}
	}
	
	public static void handleList(Player player, String name) {
		if(ph.hasPerm(player, "redprotect.admin.list")) {
			Set<Region> regions = rm.getRegions(name);
			int length = regions.size();
			if(length == 0){
				player.sendMessage(AQUA + "That player has no regions!");
			} else {
				player.sendMessage(AQUA + "Regions created:");
				player.sendMessage(AQUA + "------------------------------------");
				Iterator<Region> i = regions.iterator();
				while(i.hasNext()) {
					player.sendMessage(AQUA + i.next().info());
				}
				player.sendMessage(AQUA + "------------------------------------");
			}
			return;
		} else {
			player.sendMessage(RED + "You don't have sufficient permission to do that.");
			return;
		}
	}
}
