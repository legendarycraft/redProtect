package com.OverCaste.plugin.RedProtect;

public class Location2I {
	final int x;
	final int z;
	
	public Location2I(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash *= 17 + x;
		hash *= 29 + z;
		return hash;
	}
	
	public long longValue() {
		return (((long)x<<16) | z);
	}
	
	public static long getXZLong(int x, int z) {
		return (((long)x<<16) | z);
	}
	
	public Location2I getLocationFromLong(long l) {
		return new Location2I((int)(l>>16), (int)(l&Integer.MAX_VALUE));
	}
}
