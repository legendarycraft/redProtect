package com.OverCaste.plugin.RedProtect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RegionManager {
	Map<World, WorldRegionManager> regionManagers = new HashMap<World, WorldRegionManager>();

	public void loadAll() throws Exception {
		Iterator<World> worlds = Bukkit.getWorlds().iterator();
		while(worlds.hasNext()) {
			WorldRegionManager mgr;
			World w = worlds.next();
			if(RedProtect.fileType == RedProtect.FILE_TYPE.mysql) {
				mgr = new WorldMySQLRegionManager(w);
			} else {
				mgr = new WorldFlatFileRegionManager(w);
			}
			mgr.load();
			regionManagers.put(w, mgr);
		}
	}
	
	public void load(World w) throws Exception {
		if(regionManagers.containsKey(w)) {
			return;
		}
		WorldRegionManager mgr;
		if(RedProtect.fileType == RedProtect.FILE_TYPE.mysql) {
			mgr = new WorldMySQLRegionManager(w);
		} else {
			mgr = new WorldFlatFileRegionManager(w);
		}
		mgr.load();
		regionManagers.put(w, mgr);
	}
	
	public void unload(World w) {
		if(!regionManagers.containsKey(w)) {
			return;
		}
		WorldRegionManager mgr = regionManagers.get(w);
		mgr.save();
		regionManagers.remove(w);
	}
	
	public void saveAll() {
		Iterator<WorldRegionManager> rms = regionManagers.values().iterator();
		while(rms.hasNext()){
			rms.next().save();
		}
	}
	
	public void save(World w) {
		regionManagers.get(w).save();
	}
	
	public Region getRegion(String name, World w){ 
		return regionManagers.get(w).getRegion(name);
	}

	public int getTotalRegionSize(String name){
		int ret = 0;
		Iterator<WorldRegionManager> rms = regionManagers.values().iterator();
		while(rms.hasNext()){
			ret += rms.next().getTotalRegionSize(name);
		}
		return ret;
	}

	public Set<Region> getWorldRegions(Player player, World w) {
		return regionManagers.get(w).getRegions(player);
	}
	
	public Set<Region> getRegions(String player) {
		Set<Region> ret = new HashSet<Region>();
		Iterator<WorldRegionManager> rms = regionManagers.values().iterator();
		while(rms.hasNext()){
			ret.addAll(rms.next().getRegions(player));
		}
		return ret;
	}
	
	public Set<Region> getRegions(Player player) {
		return getRegions(player.getName());
	}

	public Set<Region> getRegionsNear(Player player, int i, World w){
		return regionManagers.get(w).getRegionsNear(player, i);
	}

	public Set<Region> getRegions(String string, World w){
		return regionManagers.get(w).getRegions(string);
	}

	public Region getRegion(Player player, World w){
		return regionManagers.get(w).getRegion(player);
	}

	public void add(Region region, World w){
		regionManagers.get(w).add(region);
	}

	public void remove(Region reg){
		Iterator<WorldRegionManager> rms = regionManagers.values().iterator();
		while(rms.hasNext()){
			rms.next().remove(reg);
		}
	}

	public boolean canBuild(Player p, Block b, World w){
		return regionManagers.get(w).canBuild(p, b);
	}

	public boolean isSurroundingRegion(Region rect, World w){
		return regionManagers.get(w).isSurroundingRegion(rect);
	}
	
	public boolean regionExists(Block block, World w){
		return regionManagers.get(w).regionExists(block);
	}
	
	public boolean regionExists(int x, int z, World w) {
		return regionManagers.get(w).regionExists(x, z);
	}
	
	public Region getRegion(Location location){
		return regionManagers.get(location.getWorld()).getRegion(location);
	}

	/*public World getWorld(Region region) {
		Iterator<WorldRegionManager> rms = regionManagers.values().iterator();
		while(rms.hasNext()){
			WorldRegionManager rm = rms.next();
			if(rm.regionExists(region)) {
				return rm.getWorld();
			}
		}
		return null;
	}*/

	public void rename(Region rect, String name, World world) {
		WorldRegionManager rm = regionManagers.get(world);
		if(!rm.regionExists(rect)) {
			return;
		}
		rm.setRegionName(rect, name);
	}
	
	public void setFlag(Region rect, int flag, boolean value, World world) {
		WorldRegionManager rm = regionManagers.get(world);
		if(!rm.regionExists(rect)) {
			return;
		}
		rm.setFlagValue(rect, flag, value);
	}
	
	/*public World getWorld(WorldRegionManager rm) {
		if(regionManagers.containsValue(rm)) {
			Iterator<World> ws = Bukkit.getWorlds().iterator();
			while(ws.hasNext()){
				World w = ws.next();
				if(regionManagers.get(w).equals(rm)) {
					return w;
				}
			}
		}
		return null;
	}*/
}
