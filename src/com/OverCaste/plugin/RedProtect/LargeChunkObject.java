package com.OverCaste.plugin.RedProtect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LargeChunkObject {
	Set<Region> regions;
	
	public LargeChunkObject(HashMap<String, Region> regionValues, Set<String> values) {
		regions = new HashSet<Region>(values.size());
		for(String s : values) {
			regions.add(regionValues.get(s));
		}
	}
	
	public LargeChunkObject() {
		regions = new HashSet<Region>();
	}

	public void addRegion(Region r) {
		if(regions == null) {
			regions = new HashSet<Region>(10);
		}
		regions.add(r);
	}
	
	public void removeRegion(Region r) {
		if(regions == null) {
			return;
		}
		regions.remove(r);
		if(regions.size() <= 0) {
			regions = null;
		}
	}
	
	public boolean isNull() {
		return (regions == null);
	}
	
	public static int convertBlockToLCO(int i) {
		int ie = (i/512);
		if(ie < 0) {
			ie-= 1;
		}
		return ie;
	}
	
	public static long getBlockLCOLong(int x, int z) {
		int xe = (x/512);
		if(xe < 0) {
			xe-= 1;
		}
		int ze = (z/512);
		if(ze < 0) {
			ze-= 1;
		}
		return Location2I.getXZLong(xe, ze);
	}
	
	public static long getChunkLCOLong(int x, int z) {
		int xe = (x/32);
		if(xe < 0) {
			xe-= 1;
		}
		int ze = (x/32);
		if(ze < 0) {
			ze-= 1;
		}
		return Location2I.getXZLong(xe, ze);
	}
}
