package com.OverCaste.plugin.RedProtect;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import static org.bukkit.ChatColor.*;

public class RPBlockListener implements Listener {
	RedProtect plugin;
	public RPBlockListener(RedProtect plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if (e.isCancelled()||(b == null)){
			setErrorSign(e, p, "The block you placed was null!");
			return;
		}
		String[] lines = e.getLines();
		String line = lines[0].toLowerCase();
		if(!(line.equals("[rp]") || line.equals("[p]")||line.equals("[protect]"))) {
			return; //not a [rp] sign
		}
		if(lines.length != 4) {
			setErrorSign(e, p, "The number of lines on your sign is wrong!");
			return;
		}
		if (!RedProtect.ph.hasPerm(p, "redprotect.create")){
			setErrorSign(e, p, "You don't have permission to make regions!");
			return;
		}
		RegionBuilder rb = new EncompassRegionBuilder(e);
		if(rb.ready()) {
			Region r = rb.build();
			p.getWorld().getBlockAt(r.getCenterX(), 70, r.getCenterZ()).setTypeId(1);
			e.setLine(0, GREEN + "[RP]: Done.");
			p.sendMessage(AQUA + "Created a region with name: " + GOLD + r.getName() + AQUA + ", with you as owner.");
			RedProtect.rm.add(r, p.getWorld());
		}
	}
	
	void setErrorSign(SignChangeEvent e, Player p, String error) {
		e.setLine(0, RED + "[RP]: Error");
		p.sendMessage(ChatColor.RED + "[RP] ERROR:" + error);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e) {
		try {
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if (!RedProtect.rm.canBuild(p, b, p.getWorld())){
			p.sendMessage(RED + "You can't build here!");
			e.setCancelled(true);
		}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (!RedProtect.rm.canBuild(p, b, p.getWorld())){
			p.sendMessage(RED + "You can't build here!");
			e.setCancelled(true);
		}
	}
	
}