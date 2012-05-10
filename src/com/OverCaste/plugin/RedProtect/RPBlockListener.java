package com.OverCaste.plugin.RedProtect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
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
	public void onSignChange(SignChangeEvent event){
		int c = 0;
		Block b = event.getBlock();
		if (event.isCancelled()||(b == null)){
			event.setLine(0, "[?]");
			return;
		}
		Block last = b;
		Block current = b;
		Block next = null;
		Block first = null;
		ArrayList<Integer> px = new ArrayList<Integer>();
		ArrayList<Integer> pz = new ArrayList<Integer>();
		Block bFirst1 = null;
		Block bFirst2 = null;
		List<Block> redstone = new ArrayList<Block>();
		String name = event.getLine(1);
		int oldFacing = 0;
		int curFacing = 0;
		World w = b.getWorld();
		Player p = event.getPlayer();
		if (event.getLine(0).equalsIgnoreCase("[Protect]") || event.getLine(0).equalsIgnoreCase("[p]") || event.getLine(0).equalsIgnoreCase("[rp]")){
			if (name.equals("")){
				for(int i = 0; true; i++){
					if(p.getName().length() > 13){
						name = p.getName().substring(0, 13) + "_" + i;
					} else{
						name = p.getName() + "_" + i;
					}
					if (RedProtect.rm.getRegion(name, w) == null){
						if(name.length() > 16){
							setErrorSign(event);
							p.sendMessage(RED + "Couldn't generate automatic region name, please name it yourself.");
							return;
						}
						break;
					}
				}
			}
			if (!RedProtect.ph.hasPerm(p, "redprotect.create")){
				setErrorSign(event);
				p.sendMessage(RED + "You don't have permission to make regions!");
				return;
			}
			if (RedProtect.rm.getRegion(name, w) != null){
				setErrorSign(event);
				p.sendMessage(RED + "That name is already taken, please choose another one.");
				return;
			}
			if((name.length() < 2) || (name.length() > 16)){
				setErrorSign(event);
				p.sendMessage(RED + "Invalid name, place a 2-16 character name in the 2nd row.");
				return;
			}
			if (name.contains(" ")){
				setErrorSign(event);
				p.sendMessage(RED + "The name of the region can't have a space in it.");
				return;
			}
			while (c++ < RedProtect.maxScan){
				int count = 0;
				int x = current.getX();
				int y = current.getY();
				int z = current.getZ();
				int blockSize = 6;
				Block[] block;
				if((RedProtect.blockID == 55)){
					block = new Block[12];
					blockSize = 12;
					block[0] = w.getBlockAt(x+1, y, z);
					block[1] = w.getBlockAt(x-1, y, z);
					block[2] = w.getBlockAt(x, y, z+1);
					block[3] = w.getBlockAt(x, y, z-1);
					block[4] = w.getBlockAt(x+1, y+1, z);
					block[5] = w.getBlockAt(x-1, y+1, z);
					block[6] = w.getBlockAt(x, y+1, z+1);
					block[7] = w.getBlockAt(x, y+1, z-1);
					block[8] = w.getBlockAt(x+1, y-1, z);
					block[9] = w.getBlockAt(x-1, y-1, z);
					block[10] = w.getBlockAt(x, y-1, z+1);
					block[11] = w.getBlockAt(x, y-1, z-1);
				}else if((RedProtect.blockID == 85)||(RedProtect.blockID == 101)||(RedProtect.blockID == 113)){
					block = new Block[6];
					blockSize = 6;
					block[0] = w.getBlockAt(x+1, y, z);
					block[1] = w.getBlockAt(x-1, y, z);
					block[2] = w.getBlockAt(x, y, z+1);
					block[3] = w.getBlockAt(x, y, z-1);
					block[4] = w.getBlockAt(x, y-1, z);
					block[5] = w.getBlockAt(x, y+1, z);
				}else{
					//RedProtect.logger.warning("The block ID you have chosen isn't valid!");
					block = new Block[6];
					blockSize = 6;
					block[0] = w.getBlockAt(x+1, y, z);
					block[1] = w.getBlockAt(x-1, y, z);
					block[2] = w.getBlockAt(x, y, z+1);
					block[3] = w.getBlockAt(x, y, z-1);
					block[4] = w.getBlockAt(x, y-1, z);
					block[5] = w.getBlockAt(x, y+1, z);
				}

				for (int i = 0; i < blockSize; i++){
					boolean validBlock = false;
					if ((RedProtect.blockID == 85)&&((block[i].getType().equals(Material.FENCE))||(block[i].getType().equals(Material.FENCE_GATE)))){
						validBlock = true;
					}else{
						validBlock = (block[i].getTypeId() == RedProtect.blockID);
					}
					if (validBlock && !block[i].getLocation().equals(last.getLocation())){
						count++;
						next = block[i];
						curFacing = i % 4;
						if (c == 2){
							if (count == 1){
								bFirst1 = block[i];
							}
							if (count == 2){
								bFirst2 = block[i];
							}
						}
					}
				}
				if (count == 1){
					if (c != 1){
						redstone.add(current);
						if (current.equals(first)){
							if (px.size() == pz.size()){
								//Finished
								String[] owners = new String[3];
								owners[0] = (p.getName().toLowerCase());
								for (int i = 2; i<4; i++){
									String s = event.getLine(i).toLowerCase();
									//RedProtect.logger.debug("s: " + s);
									if (!((s.equals("")||(s.contains(" "))))){
										//RedProtect.logger.debug("name: " + p.getName());
										if(s.equalsIgnoreCase(p.getName())){
											p.sendMessage(YELLOW + "[RP]: You don't need to enter your name manually, it's added automatically.");
										}else{
											owners[i-2] = s;
										}
									}
								}
								int[] rx = new int[px.size()];
								int[] rz = new int[pz.size()];
								for (int i = 0; i<px.size(); i++){
									rx[i] = px.get(i);
									rz[i] = pz.get(i);
								}
								Region rect = new Region(name, owners, rx, rz);
								//Make sure you aren't overlapping another region: {
								int l = redstone.size();
								for (int i = 0; i<l; i++){
									if (RedProtect.rm.regionExists(redstone.get(i), w)){
										p.sendMessage(RED + "You're overlapping another region.");
										//DEBUG: p.sendMessage("Overlap 1: (" + redstone.get(i).getX() + ", " + redstone.get(i).getZ() + ").");
										setErrorSign(event);
										rect.delete();
										return;
									}
								}
								// } Make sure you haven't completely surrounded another region: {
								if (RedProtect.rm.isSurroundingRegion(rect, w)){
									//DEBUG: p.sendMessage("1: ");
									p.sendMessage(RED + "You're overlapping another region.");
									setErrorSign(event);
									rect.delete();
									return;
								}
								// }
								// Limits {
								int pLimit = RedProtect.ph.getPlayerLimit(p);
								if (pLimit >= 0){
									//DEBUG: RedProtect.logger.debug("limitAmount: " + RedProtect.limitAmount);
									//DEBUG: RedProtect.logger.debug("totalRegionSize: " + RegionManager.getTotalRegionSize(p.getName()));
									//DEBUG: RedProtect.logger.debug("regionArea: " + poly.getArea());
									int totalArea = RedProtect.rm.getTotalRegionSize(p.getName());
									if (((totalArea + rect.getArea()) > pLimit) && (!RedProtect.ph.hasPerm(p, "RedProtect.unlimited"))){
										p.sendMessage(RED + "You can't make any more regions because you've reached the maximum area alotted per player.");
										rect.delete();
										return;
									}
									p.sendMessage(AQUA + "Your area left: " + GOLD + (pLimit-(totalArea + rect.getArea())) + AQUA + ".");
								}
								// }
								if (RedProtect.removeBlocks){
									b.setType(Material.AIR);
									for (int i = 0; i<l; i++){
										redstone.get(i).setType(Material.AIR);
									}
								}
								RedProtect.rm.add(rect, w);
								event.setLine(0, GREEN + "[RP]: Done.");
								p.sendMessage(AQUA + "Created a region with name: " + GOLD + name + AQUA + ", with you as owner.");
							}else{
								RedProtect.logger.severe("px.size and pz.size are not the same size! Report this problem!");
							}
							break;
						}
					}
				}else{
					if ((c == 2) && (count == 2)){
						//First block
						redstone.add(current);
						first = current;
						int x1 = bFirst1.getX();
						int z1 = bFirst1.getZ();
						int x2 = bFirst2.getX();
						int z2 = bFirst2.getZ();
						int distx = Math.abs(x1 - x2);
						int distz = Math.abs(z1 - z2);
						if (!(((distx == 2) && (distz == 0)) || ((distz == 2) && (distx == 0)))){
							//!First block angle isn't a straight line.
							//RedProtect.logger.debug("Angle, DistX: " + distx + "DistZ: " + distz);
							px.add(current.getX());
							pz.add(current.getZ());
						}
					}else{
						if (c != 1){
							setErrorSign(event);
							p.sendMessage(RED + "Error in your area at: (" + "x: " + current.getX() + ", y: " + current.getY() + ", z: " + current.getZ() + "). Press f3 to look there for those coordinates and make sure there isn't 3 blocks touching.");
							last = null;
							break;
						}
					}
				}
				if ((oldFacing != curFacing) && c > 2){
					px.add(current.getX());
					pz.add(current.getZ());
					//DEBUG: current.getRelative(BlockFace.UP).setType(Material.BOOKSHELF);
				}
				last = current;
				if(next == null){
					p.sendMessage(RED + "Put your sign next to the block you want. There is no viable block next to your sign.");
					setErrorSign(event);
					return;
				}
				current = next;
				oldFacing = curFacing;
			}
		}
	}
	
	void setErrorSign(SignChangeEvent e){
		e.setLine(0, RED + "[RP]: Error");
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e){
		Block b = e.getBlock();
		Player p = e.getPlayer();
		if (!RedProtect.rm.canBuild(p, b, p.getWorld())){
			p.sendMessage(RED + "You can't build here!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		Block b = e.getBlock();
		if (!RedProtect.rm.canBuild(p, b, p.getWorld())){
			p.sendMessage(RED + "You can't build here!");
			e.setCancelled(true);
		}
	}
	
}