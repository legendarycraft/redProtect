package com.OverCaste.plugin.RedProtect;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public class RPEntityListener implements Listener {
	RedProtect plugin;
	
	public RPEntityListener(RedProtect plugin){
		this.plugin = plugin;
	}
	
	static final String noPvPMsg = (ChatColor.RED + "You can't PvP in this region!");
	static final String noAnimalMsg = (ChatColor.RED + "You can't kill animals in this region!");
	
	/*@EventHandler(priority = EventPriority.NORMAL)
	public void onEndermanPickup(EndermanPickupEvent e) {
		Region r = RedProtect.rm.getRegion(e.getEntity().getLocation());
		if(r != null) {
			e.setCancelled(true); //no enderman griefing.
		}
	}*/
	
	/*@EventHandler(priority = EventPriority.NORMAL)
	public void onEndermanPlace(EndermanPlaceEvent e) {
		Region r = RedProtect.rm.getRegion(e.getEntity().getLocation());
		if(r != null) {
			e.setCancelled(true); //no enderman griefing.
		}
	}*/
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent e) {
		Entity target = e.getTarget();
		if(target == null) return;
		Region r = RedProtect.rm.getRegion(target.getLocation());
		if(r != null) {
			if(!r.canMobs()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		Entity e = event.getEntity();
		if(e == null) return;
		if(e instanceof Monster){ //ees monster
			Region r = RedProtect.rm.getRegion(e.getLocation());
			if(r != null) {
				if(!r.canMobs()){
					if(event.getSpawnReason().equals(SpawnReason.NATURAL)){ //mob eggs, spawners, bed monsters, are all fine.
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent event) {
    	if(event.isCancelled()) return;
    	if(event instanceof EntityDamageByEntityEvent){
	    	EntityDamageByEntityEvent de = (EntityDamageByEntityEvent) event;
	    	Entity e1 = de.getEntity();
	    	Entity e2 = de.getDamager();
	    	if(e2 == null) {
	    		return;
	    	}
	    	if(e2 instanceof Arrow) {
	    		Arrow a = (Arrow) e2;
	    		e2 = a.getShooter();
	    		a = null;
	    	}
	        //if(!(event.getEntity() instanceof Player)) return;
	        //No damage to you
	        //Player player = (Player)event.getEntity();
	        Region r1 = RedProtect.rm.getRegion(e1.getLocation());
	        Region r2 = RedProtect.rm.getRegion(e2.getLocation());
	        if(e1 instanceof Player) { //e1 is a player
	        	//Player p1 = (Player)e1;
	        	if(e2 instanceof Player){ //e1 is player, e2 is player (pvp)
	        		Player p2 = (Player)e2;
	        		if(r1 != null) { //r1 exists
	        			if(r2 != null) { //r1 exists, r2 exists
	        				if((!r1.canPVP(p2))||(!r2.canPVP(p2))) { //if attacker can't attack in both regions cancel.
	        					event.setCancelled(true);
	        					p2.sendMessage(noPvPMsg);
	        				}
	        			} else { //r1 exists, r2 doesn't
	        				if(!r1.canPVP(p2)) {
	        					event.setCancelled(true);
	        					p2.sendMessage(noPvPMsg);
	        				}
	        			}
	        		} else { //r1 doesn't exist
	        			if(r2 != null) { //r1 doesn't exist, r2 does
	        				if(!r2.canPVP(p2)) {
	        					event.setCancelled(true);
	        					p2.sendMessage(noPvPMsg);
	        				}
	        			} else { //r1 doesn't exist, r2 doesn't either.
	        				//nothing to do here.
	        			}
	        		}
	        	}
	        }
			else if(e1 instanceof Animals) { //Animal
				Region r = RedProtect.rm.getRegion(e1.getLocation());
				if(r != null) {
					if(e2 instanceof Player) {
						Player p = (Player)e2;
						if(!r.canAnimals(p)) {
							event.setCancelled(true);
							p.sendMessage(noAnimalMsg);
						}
					}
				}
			}
    	}
    }
}
