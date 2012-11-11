package com.OverCaste.plugin.RedProtect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class RPPlayerListener implements Listener {
	RedProtect plugin;
	public RPPlayerListener(RedProtect plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		if (b == null) return;
		Region r = null;
		Material itemInHand = p.getItemInHand().getType();
		
		if(p.getItemInHand().getTypeId() == RedProtect.adminWandID) {
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(p.hasPermission("redprotect.magicwand")) {
					RedProtect.secondLocationSelections.put(p, b.getLocation());
					p.sendMessage(ChatColor.AQUA + "Set the second magic wand location to (" + ChatColor.GOLD + b.getLocation().getBlockX() + ChatColor.AQUA + ", " + ChatColor.GOLD + b.getLocation().getBlockY() + ChatColor.AQUA + ", " + ChatColor.GOLD + b.getLocation().getBlockZ() + ChatColor.AQUA + ").");
					e.setCancelled(true);
					return;
				}
			}
			else if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if(p.hasPermission("redprotect.magicwand")) {
					RedProtect.firstLocationSelections.put(p, b.getLocation());
					p.sendMessage(ChatColor.AQUA + "Set the first magic wand location to (" + ChatColor.GOLD + b.getLocation().getBlockX() + ChatColor.AQUA + ", " + ChatColor.GOLD + b.getLocation().getBlockY() + ChatColor.AQUA + ", " + ChatColor.GOLD + b.getLocation().getBlockZ() + ChatColor.AQUA + ").");
					e.setCancelled(true);
					return;
				}
			}
		}
		if(p.getItemInHand().getTypeId() == RedProtect.infoWandID) {
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				r = RedProtect.rm.getRegion(p.getLocation());
			}
			else if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				r = RedProtect.rm.getRegion(b.getLocation());
			}
			if(p.hasPermission("redprotect.infowand")) {
				if(r == null) {
					p.sendMessage(ChatColor.RED + "There is no region at that block's location!");
				} else {
					p.sendMessage(ChatColor.AQUA + "--------------- [" + ChatColor.GOLD + r.getName() + ChatColor.AQUA + "] ---------------");
					p.sendMessage(r.info());
					p.sendMessage(r.getFlagInfo());
				}
				e.setCancelled(true);
				return;
			}
		}
		
		if (b.getType().equals(Material.CHEST)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canChest(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't open this chest!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Opened locked chest in " + r.getCreator() +  "'s region.");
				}
			}
		}
		
		else if (b.getType().equals(Material.DISPENSER)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canChest(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't open this dispenser!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Opened locked dispenser in " + r.getCreator() +  "'s region.");
				}
			}
		}
		
		else if (b.getType().equals(Material.FURNACE)||b.getType().equals(Material.BURNING_FURNACE)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canChest(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't open this furnace!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Opened locked furnace in " + r.getCreator() +  "'s region.");
				}
			}
		}
		
		else if (b.getType().equals(Material.LEVER)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canLever(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't toggle this lever!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Toggled locked lever in " + r.getCreator() +  "'s region.");
				}
			}
		}
		
		else if (b.getType().equals(Material.STONE_BUTTON)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canButton(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't activate this button!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Activated locked button in " + r.getCreator() +  "'s region.");
				}
			}
		}
		
		else if (b.getType().equals(Material.WOODEN_DOOR)){
			r = RedProtect.rm.getRegion(b.getLocation());
			if(r == null) return;
			if(!r.canDoor(p)){
				if(!RedProtect.ph.hasPerm(p, "redprotect.bypass")){
					p.sendMessage(ChatColor.RED + "You can't open this door!");
					e.setCancelled(true);
				}else{
					p.sendMessage(ChatColor.YELLOW + "Opened locked door in " + r.getCreator() +  "'s region.");
				}
			}
		}

		if (itemInHand.equals(Material.FLINT_AND_STEEL)||itemInHand.equals(Material.WATER_BUCKET)||itemInHand.equals(Material.LAVA_BUCKET)||itemInHand.equals(Material.PAINTING)||itemInHand.equals(Material.ITEM_FRAME)){
			if (!RedProtect.rm.canBuild(p, b, b.getWorld())){
				p.sendMessage(ChatColor.RED + "You can't use that here!");
				e.setCancelled(true);
			}
		}
	}
}
