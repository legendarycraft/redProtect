package com.OverCaste.plugin.RedProtect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;
//import org.bukkit.World;
import org.bukkit.entity.Player;

public class Region implements Serializable {
	private static final long serialVersionUID = 3904371508520551177L;
	private int x[];
	private int z[];
	private int minMbrX = 0;
	private int maxMbrX = 0;
	private int minMbrZ = 0;
	private int maxMbrZ = 0;
	private String name;
	private List<String> owners;
	private List<String> members;
	private String creator = "";
	
	private boolean[] f = {Flags.pvp, Flags.chest, Flags.lever, Flags.button, Flags.door, Flags.mobs, Flags.animals}; //flags, see Flags.java
	
	public void setFlag(int flag, boolean value){
		if(flag > f.length){
			return;
		}
		f[flag] = value;
	}
	//Getters and setters {
	public void setX(int[] x){
		this.x = x;
	}
	public void setZ(int[] z){
		this.z = z;
	}
	public void setOwners(List<String> owners){
		this.owners = owners;
	}
	public void setMembers(List<String> members){
		this.members = members;
	}
	public void setCreator(String s){
		this.creator = s;
	}
	public int[] getX(){
		return x;
	}
	public int[] getZ(){
		return z;
	}
	public String getCreator(){
		return creator;
	}
	public String getName(){
		return name;
	}
	public List<String> getOwners(){
		return owners;
	}
	public List<String> getMembers(){
		return members;
	}
	
	public int getCenterX(){
		return ((minMbrX + maxMbrX)/2);
	}
	
	public int getCenterZ(){
		return Double.valueOf(((double)(minMbrZ + maxMbrZ)/(double)2)).intValue();
	}
	
	public int getMaxMbrX() {
		return maxMbrX;
	}
	
	public int getMinMbrX() {
		return minMbrX;
	}
	
	public int getMaxMbrZ() {
		return maxMbrZ;
	}
	
	public int getMinMbrZ() {
		return minMbrZ;
	}
	
	public String info(){
		String ownerstring = "";
		String memberstring = "";
		for(int i = 0; i < owners.size(); i++){
			ownerstring = ownerstring + ", " + owners.get(i);
		}
		for(int i = 0; i < members.size(); i++){
			memberstring = memberstring + ", " + members.get(i);
		}
		if (owners.size() > 0){
			ownerstring = ownerstring.substring(2);
		} else {
			ownerstring = "None";
		}
		if (members.size() > 0){
			memberstring = memberstring.substring(2);
		} else {
			memberstring = "None";
		}
		return AQUA + "Name: " + GOLD + name + AQUA + ", Creator: " + GOLD + creator + AQUA + ", Center: [" + GOLD + getCenterX() + AQUA + ", " + GOLD + getCenterZ() + AQUA + "], Owners: [" + GOLD + ownerstring + AQUA + "], Members: [" + GOLD + memberstring + AQUA + "].";
	}
	
	/*public boolean rename(String newName){
		World w = RedProtect.rm.getWorld(this);
		if (RedProtect.rm.getRegion(newName, w) != null) return false;
		name = newName;
		return true;
	}*/
	
	public Region(int[] x, int[] z, String name, List<String> owners, List<String> members, String creator, int maxMbrX, int minMbrX, int maxMbrZ, int minMbrZ, boolean[] flags){
		this.x = x;
		this.z = z;
		this.maxMbrX = maxMbrX;
		this.minMbrX = minMbrX;
		this.maxMbrZ = maxMbrZ;
		this.minMbrZ = minMbrZ;
		this.name = name;
		this.owners = owners;
		this.members = members;
		this.creator = creator;
		this.f = flags;
	}
	
	public Region(String name, String[] owners, int[] x, int[] z){
		int xSize = x.length;
		int zSize = z.length;
		this.x = x;
		this.z = z;
		if ((xSize < 4) ||(zSize < 4)){
			throw new Error("You can't generate a polygon with less then 4 points!");
		}
		if (xSize != zSize){
			throw new Error("The X & Y arrays are different sizes!");
		}
		if (xSize == 4){
			//RedProtect.logger.debug("One of these regions, is not like the other ones, one of these regions, is an mbr.");
			this.x = null;
			this.z = null;
		}
		this.owners = new ArrayList<String>(owners.length);
		for(String s : owners) {
			this.owners.add(s);
		}
		this.members = new ArrayList<String>();
		this.name = name;
		this.creator = owners[0];
		maxMbrX = x[0];
		minMbrX = x[0];
		maxMbrZ = z[0];
		minMbrZ = z[0];
		for (int i = 0; i<x.length; i++){
			if(x[i] > maxMbrX){
				maxMbrX = x[i];
				//RedProtect.logger.debug("New maxMbrX: " + x[i] + ", " + i);
			}
			if(x[i] < minMbrX){
				minMbrX = x[i];
				//RedProtect.logger.debug("New minMbrX: " + x[i] + ", " + i);
			}
			if(z[i] > maxMbrZ){
				maxMbrZ = z[i];
			}
			if(z[i] < minMbrZ){
				minMbrZ = z[i];
			}
		}
		//RedProtect.logger.debug("Bounding rect: " + maxMbrX + ", " + minMbrX + ", " + maxMbrZ + ", " + minMbrZ);
	}
	
	/*public void addToRM(World w){
		RedProtect.rm.add(this, w);
	}*/
	
	public void delete(){
		RedProtect.rm.remove(this);
	}
	
	public int getArea(){
		if (this.x == null){
			return ((maxMbrX-minMbrX)*(maxMbrZ-minMbrZ));
		}else{
			int area = 0;
			for (int i = 0; i<x.length; i++){
				int j = ((i + 1) % x.length);
				area += ((x[i]*z[j])-(z[i]*x[j]));
			}
			area = Math.abs(area / 2 );
			return area;
		}
	}
	
	/*public boolean intersects(int bx, int bz, String w){
		int i;
		int x1,x2,y1,y2;
		int xnew,xold,ynew,yold;
	    xold=x[size-1];
	    yold=z[size-1];
	    for (i=0 ; i < size ; i++) {
	    	xnew=x[i];
	        ynew=z[i];
	        if (xnew > xold) {
	             x1=xold;
	             x2=xnew;
	             y1=yold;
	             y2=ynew;
	        }
	        else {
	             x1=xnew;
	             x2=xold;
	             y1=ynew;
	             y2=yold;
	        }
	        if (((xnew < bx) == (bx <= xold))
	        		&& (bz-y1)*(x2-x1)
	        		< (y2-y1)*(bx-x1)) {
	        	return true;
	        }
	        xold=xnew;
	        yold=ynew;
	     }
		return false;
	}*/ //PiP #1: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html
	
	/*public boolean intersects(int bx, int bz, String w){
		String world = RedProtect.rm.getWorld(this).getName();
		if (!world.equalsIgnoreCase(w))
			return false;
		return intersects(bx, bz);
	} //PiP: http://www.visibone.com/inpoly/inpoly.c.txt*/
	
	public boolean inBoundingRect(int bx, int bz) {
		return ((bx<=maxMbrX)&&(bx>=minMbrX)&&(bz<=maxMbrZ)&&(bz>=minMbrZ));
	}
	
	public boolean intersects(int bx, int bz) {
		if (this.x == null) {
			//RedProtect.logger.info("x = null. =(");
			return true;
		}
		int i, j;
		boolean ret = false;
		for (i = 0, j = x.length-1; i < x.length; j = i++) {
			if ((((z[i] <= bz) && (bz < z[j])) ||
		             ((z[j] <= bz) && (bz < z[i]))) &&
		            (bx < (x[j] - x[i]) * (bz - z[i]) / (z[j] - z[i]) + x[i])){
				ret = !ret;
			}
		}
		return ret;
	} //PiP: http://paulbourke.net/geometry/insidepoly/
	
	public boolean isOwner(String p){
		p = p.toLowerCase();
		return owners.contains(p);
	}
	
	public boolean isOwner(Player player) {
		return owners.contains(player.getName().toLowerCase());
	}
	
	public boolean isMember(String p){
		p = p.toLowerCase();
		return members.contains(p);
	}
	
	public boolean isMember(Player player) {
		return members.contains(player.getName().toLowerCase());
	}
	
	public void addMember(String p){
		p = p.toLowerCase();
		if (!members.contains(p) && !owners.contains(p)){
			members.add(p);
		}
	}
	
	public void addOwner(String p){
		p = p.toLowerCase();
		if (members.contains(p)){
			members.remove(p);
		}
		if (!owners.contains(p)){
			owners.add(p);
		}
	}
	
	public void removeMember(String p){
		p = p.toLowerCase();
		if (members.contains(p)){
			members.remove(p);
		}
		if (owners.contains(p)){
			owners.remove(p);
		}
	}
	
	public void removeOwner(String p){
		p = p.toLowerCase();
		if (owners.contains(p)){
			owners.remove(p);
		}
	}
	
	public boolean getFlag(int flag){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		return f[flag];
	}
	
	public boolean canBuild(Player p){
		if(p.getLocation().getY() < RedProtect.heightStart) return true; //For mining and stuff.
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canPVP(Player p){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		if(f[0]){ //if flag 0, pvp, allowed
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canChest(Player p){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		if(f[1]){
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canLever(Player p){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		if(f[2]){
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canButton(Player p){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		if(f[3]){
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canDoor(Player p){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		if(f[4]){
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	public boolean canMobs(){
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		return f[5];
	}
	
	public boolean canAnimals(Player p){
		if(f[5]){
			return true;
		}
		return (isOwner(p) || isMember(p) || RedProtect.ph.hasPerm(p, "redprotect.bypass"));
	}
	
	private void checkNullFlags() {
		if(f == null){
			f = new boolean[6];
			f[0] = Flags.pvp;
			f[1] = Flags.chest;
			f[2] = Flags.lever;
			f[3] = Flags.button;
			f[4] = Flags.door;
			f[5] = Flags.mobs;
			f[6] = Flags.animals;
		}
	}
	
	public int ownersSize(){
		return owners.size();
	}
	public String getFlagInfo() {
		checkNullFlags(); //TODO: Remove in 5 versions or so.
		return(AQUA + "Player vs Player: " + GOLD + f[0] + AQUA + ", Chest opening: " + GOLD + f[1] + AQUA + ", Lever flipping: " + GOLD + f[2] + AQUA + ", Button pushing: " + GOLD + f[3] + AQUA + ", Door toggling: " + GOLD + f[4] + AQUA + ", Monster spawning: " + GOLD + f[5] + AQUA + ", Animal hurting: " + GOLD + f[6]);
	}
	public void setName(String name) {
		this.name = name;
	}
}
