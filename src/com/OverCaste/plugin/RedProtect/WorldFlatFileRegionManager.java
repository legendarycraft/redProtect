package com.OverCaste.plugin.RedProtect;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

public class WorldFlatFileRegionManager implements WorldRegionManager {
	HashMap<Long, LargeChunkObject> regions = new HashMap<Long, LargeChunkObject>();
	World world;
	
	public WorldFlatFileRegionManager(World world) {
		this.world = world;
	}
	
	@Override
	public void add(Region r){
		int cMaxX = LargeChunkObject.convertBlockToLCO(r.getMaxMbrX());
		int cMaxZ = LargeChunkObject.convertBlockToLCO(r.getMaxMbrZ());
		int cMinX = LargeChunkObject.convertBlockToLCO(r.getMinMbrX());
		int cMinZ = LargeChunkObject.convertBlockToLCO(r.getMinMbrZ());
		for(int xl = cMinX; xl<=cMaxX; xl++) {
			for(int zl = cMinZ; zl<=cMaxZ; zl++) {
				LargeChunkObject lco = regions.get(Location2I.getXZLong(xl, zl));
				if(lco == null) {
					lco = new LargeChunkObject();
				}
				lco.addRegion(r);
				
				regions.put(Location2I.getXZLong(xl, zl), lco);
			}
		}
		save();
	}
	
	@Override
	public void remove(Region r){
		int cMaxX = LargeChunkObject.convertBlockToLCO(r.getMaxMbrX());
		int cMaxZ = LargeChunkObject.convertBlockToLCO(r.getMaxMbrZ());
		int cMinX = LargeChunkObject.convertBlockToLCO(r.getMinMbrX());
		int cMinZ = LargeChunkObject.convertBlockToLCO(r.getMinMbrZ());
		for(int xl = cMinX; xl<=cMaxX; xl++) {
			for(int zl = cMinZ; zl<=cMaxZ; zl++) {
				LargeChunkObject lco = regions.get(Location2I.getXZLong(xl, zl));
				if(lco != null) {
					lco.removeRegion(r);
				}
			}
		}
		save();
	}
	
	@Override
	public boolean canBuild(Player p, Block b){
		int bx = b.getX();
		int bz = b.getZ();
		LargeChunkObject lco = regions.get(LargeChunkObject.getBlockLCOLong(bx, bz));
		if(lco == null) {
			return true;
		}
		Iterator<Region> i = lco.regions.iterator();
		while(i.hasNext()){
			Region poly = i.next();
			if (poly.inBoundingRect(bx, bz)) {
				if (poly.intersects(bx, bz)){
					return (poly.canBuild(p));
				}
			}
		}
		return true;
	}
	
	@Override
	public Set<Region> getRegions(Player p) {
		return getRegions(p.getName());
	}
	
	@Override
	public Set<Region> getRegions(String p){
		Set<Region> ls = new HashSet<Region>();
		Iterator<LargeChunkObject> i = regions.values().iterator();
		while(i.hasNext()){
			LargeChunkObject lci = i.next();
			if(lci == null) {
				continue;
			}
			Iterator<Region> ri = lci.regions.iterator();
			while(ri.hasNext()) {
				Region r = ri.next();
				if(r.getCreator().equalsIgnoreCase(p)){
					ls.add(r);
				}
			}
		}
		return ls;
	}
	
	@Override
	public boolean regionExists(Block b){
		return regionExists(b.getX(), b.getZ());
	}
	
	@Override
	public boolean regionExists(int x, int z) {
		LargeChunkObject lco = regions.get(LargeChunkObject.getBlockLCOLong(x, z));
		if(lco == null || lco.regions == null) {
			return false;
		}
		if(lco != null) {
			Iterator<Region> i = lco.regions.iterator();
			while(i.hasNext()){
				Region poly = i.next();
				if (poly.inBoundingRect(x, z)) {
					if (poly.intersects(x, z)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public Region getRegion(Location l) {
		int x = l.getBlockX();
		int z = l.getBlockZ();
		return getRegion(x, z);
	}
	
	private Region getRegion(int x, int z){
		LargeChunkObject lco = regions.get(LargeChunkObject.getBlockLCOLong(x, z));
		if(lco == null) {
			return null;
		}
		Iterator<Region> i = lco.regions.iterator();
		while(i.hasNext()){
			Region poly = i.next();
			if (poly.inBoundingRect(x, z)) {
				if (poly.intersects(x, z)){
					return poly;
				}
			}
		}
		return null;
	}
	
	@Override
	public Region getRegion(Player p){
		return getRegion(p.getLocation());
	}
	
	@Override
	public Region getRegion(String name){
		if (name == null) return null;
		Iterator<LargeChunkObject> i = regions.values().iterator();
		while(i.hasNext()){
			LargeChunkObject lci = i.next();
			if(lci == null) {
				continue;
			}
			Iterator<Region> ri = lci.regions.iterator();
			while(ri.hasNext()) {
				Region r = ri.next();
				if(r.getName().equals(name)){
					return r;
				}
			}
		}
		return null;
	}
	
	@Override
	public void save(){
		try {
			RedProtect.logger.debug("RegionManager.Save(): File type is " + RedProtect.fileType.toString());
			String world = getWorld().getName();
			File datf = new File(RedProtect.pathData, "data_" + world + ".regions");
			if(!datf.exists()){
				datf.createNewFile();
			}
			HashMap<Long, Set<String>> lcos = new HashMap<Long, Set<String>>(regions.size());
			for(Entry<Long, LargeChunkObject> e : regions.entrySet()) {
				if(e == null || e.getValue() == null || e.getValue().regions == null) {
					continue;
				}
				HashSet<String> newRegions = new HashSet<String>();
				for(Region r : e.getValue().regions) {
					newRegions.add(r.getName());
				}
				lcos.put(e.getKey(), newRegions);
			}
			HashMap<String, Region> newRegions = new HashMap<String, Region>();
			for(LargeChunkObject lco : regions.values()) {
				for(Region r : lco.regions) {
					newRegions.put(r.getName(), r);
				}
			}
			switch(RedProtect.fileType){
			case yml:
				backupRegions(datf);
				Yaml yml = new Yaml();
				yml.dump(newRegions, new FileWriter(datf));
				break;
			case ymlgz:
				backupRegions(datf);
				Yaml ymlgz = new Yaml();
				ymlgz.dump(regions, new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(datf))));
				break;
			case oos:
				backupRegions(datf);
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datf));
				oos.writeObject(lcos);
				oos.writeObject(newRegions);
				oos.close();
				break;
			case oosgz:
				backupRegions(datf);
				ObjectOutputStream oosgz = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(datf)));
				oosgz.writeObject(lcos);
				oosgz.writeObject(newRegions);
				oosgz.close();
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void backupRegions(File datf){
		if(!RedProtect.backup)
			return;
		File dataBackup = new File(RedProtect.pathData, "data_" + getWorld().getName() + ".regions.backup");
		dataBackup.delete();
		datf.renameTo(dataBackup);
		try {
			datf.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getTotalRegionSize(String p){
		if (p == null) return 0;
		int total = 0;
		Iterator<LargeChunkObject> i = regions.values().iterator();
		while(i.hasNext()){
			LargeChunkObject lci = i.next();
			Iterator<Region> ri = lci.regions.iterator();
			while(ri.hasNext()) {
				Region r = ri.next();
				if(r.getCreator().equalsIgnoreCase(p)){
					total++;
				}
			}
		}
		return total;
	}
	
	@Override
	public boolean isSurroundingRegion(Region p){
		if (p == null) return false;
		Iterator<LargeChunkObject> i = regions.values().iterator();
		while(i.hasNext()){
			LargeChunkObject lci = i.next();
			Iterator<Region> ri = lci.regions.iterator();
			while(ri.hasNext()) {
				Region r = ri.next();
				if (r != null){
					if (p.inBoundingRect(r.getCenterX(), r.getCenterZ())) {
						if (p.intersects(r.getCenterX(), r.getCenterZ())){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@Override
	public void load(){
		String world = getWorld().getName();
		load(RedProtect.pathData + "data_" + world + ".regions");
	}
	
	@SuppressWarnings("unchecked")
	private void load(String path){
		String world = getWorld().getName();
		String datbackf = (RedProtect.pathData + "data_" + world + ".regions.backup");
		if(!(new File(path).exists())) {
			try {
				new File(path).createNewFile();
				RedProtect.logger.info("Created new world region file: (" + path + ").");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (!RPUtil.isFileEmpty(path)){
				ObjectInputStream ois = null;
				switch(RedProtect.fileType) {
				case yml:
					RedProtect.logger.debug("RegionManager.load() File type: yml");
					Yaml yml = new Yaml();
					Object oyml;
					if ((oyml = yml.load(new FileInputStream(path))) instanceof HashMap<?, ?>){
						regions = (HashMap<Long, LargeChunkObject>)oyml;
					}
					break;
				case ymlgz:
					RedProtect.logger.debug("RegionManager.load() File type: ymlgz");
					Yaml ymlgz = new Yaml();
					Object oymlgz;
					if ((oymlgz = ymlgz.load(new GZIPInputStream(new FileInputStream(path)))) instanceof HashMap<?, ?>){
						regions = (HashMap<Long, LargeChunkObject>)oymlgz;
					}
					break;
				case oosgz:
					ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream(path)));
				case oos:
					RedProtect.logger.debug("RegionManager.load() File type: oos");
					if(ois == null) {
						ois = new ObjectInputStream(new FileInputStream(path));
					}
					Object oois = ois.readObject();
					HashMap<Long, Set<String>> lcos;
					if ((oois) instanceof HashMap<?, ?>) {
						lcos = (HashMap<Long, Set<String>>)oois;
					} else {
						lcos = null;
					}
					
					oois = ois.readObject();
					HashMap<String, Region> newRegions;
					if ((oois) instanceof HashMap<?, ?>) {
						newRegions = (HashMap<String, Region>)oois;
					} else {
						newRegions = null;
					}
					ois.close();
					regions = new HashMap<Long, LargeChunkObject>(lcos.size());
					for(Entry<Long, Set<String>> ss : lcos.entrySet()) {
						regions.put(ss.getKey(), new LargeChunkObject(newRegions, ss.getValue()));
					}
				}
			}else{
				if(RedProtect.backup && backupExists() && !path.equalsIgnoreCase(datbackf)){
					load(datbackf);
					RedProtect.logger.info("Main data file is blank, Reading from backup.");
					return;
				}
				RedProtect.logger.info("Creating a new data file.");
				regions = new HashMap<Long, LargeChunkObject>();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ZipException e) {
			if(RedProtect.backup && backupExists() && !path.equalsIgnoreCase(datbackf)){
				load(datbackf);
				RedProtect.logger.info("The data file is corrupt. Loading from backup.");
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean backupExists(){
		String world = getWorld().getName();
		String datbackf = (RedProtect.pathData + "data_" + world + ".regions.backup");
		return new File(datbackf).exists();
	}

	@Override
	public Set<Region> getRegionsNear(Player player, int area) {
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();
		int cmaxX = LargeChunkObject.convertBlockToLCO(px+40);
		int cmaxZ = LargeChunkObject.convertBlockToLCO(pz+40);
		int cminX = LargeChunkObject.convertBlockToLCO(px-40);
		int cminZ = LargeChunkObject.convertBlockToLCO(pz-40);
		Set<Region> ret = new HashSet<Region>();
		for(int xl = cminX; xl<=cmaxX; xl++) {
			for(int zl = cminZ; zl<=cmaxZ; zl++) {
				LargeChunkObject lco = regions.get(Location2I.getXZLong(xl, zl));
				if(lco == null || lco.regions == null) {
					continue;
				}
				Iterator<Region> i = lco.regions.iterator();
				while(i.hasNext()){
					Region poly = i.next();
					if(((Math.abs(poly.getCenterX() - px) <= 40) && (Math.abs(poly.getCenterZ() - pz) <= 40))){
						ret.add(poly);
					}
				}
			}
		}
		return ret;
	}

	@Override
	public boolean regionExists(Region region) {
		if(region == null) {
			return false;
		}
		Iterator<LargeChunkObject> i = regions.values().iterator();
		while(i.hasNext()){
			LargeChunkObject lci = i.next();
			Iterator<Region> ri = lci.regions.iterator();
			while(ri.hasNext()) {
				Region r = ri.next();
				if (r != null){
					if(r.getName().equalsIgnoreCase(region.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public World getWorld() {
		return this.world;
	}

	@Override
	public void setFlagValue(Region region, int flag, boolean value) {
		region.setFlag(flag, value);
	}

	@Override
	public void setRegionName(Region region, String name) {
		region.setName(name);
	}
}
