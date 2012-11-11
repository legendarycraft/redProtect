package com.OverCaste.plugin.RedProtect;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface WorldRegionManager {

	abstract void load();

	abstract void save();

	abstract Region getRegion(String name);

	abstract int getTotalRegionSize(String name);

	abstract Set<Region> getRegions(Player player);

	abstract Set<Region> getRegionsNear(Player player, int i);

	abstract Set<Region> getRegions(String string);

	abstract Region getRegion(Player player);

	abstract void add(Region polygon);

	abstract void remove(Region polygon);

	abstract boolean canBuild(Player p, Block b);

	abstract boolean isSurroundingRegion(Region poly);

	abstract boolean regionExists(Block block);

	abstract Region getRegion(Location location);

	abstract boolean regionExists(Region region);

	abstract void setFlagValue(Region region, int flag, boolean value);

	abstract void setRegionName(Region rect, String name);

	abstract boolean regionExists(int x, int z);

	abstract Set<Region> getPossibleIntersectingRegions(Region r);
}
