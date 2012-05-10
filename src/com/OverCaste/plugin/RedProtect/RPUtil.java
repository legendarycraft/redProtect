package com.OverCaste.plugin.RedProtect;

//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.Server;

public class RPUtil {
	public static RedProtect plugin;
	
	public static void init(RedProtect plugin){
		RPUtil.plugin = plugin;
	}
	
	/*public static Block getMainBlock(Block block){
		if (block.getType().equals(Material.SIGN_POST)){
			return block;
		}
		
	    byte face = block.getData();
	    
	    switch (face){
	    case 5:
	    	return block.getRelative(BlockFace.NORTH);
	    case 3:
	    	return block.getRelative(BlockFace.EAST);
	    case 4:
	    	return block.getRelative(BlockFace.SOUTH);
	    case 2:
	    	return block.getRelative(BlockFace.WEST);
	    }
	    return null;
	}*/
	
	static public boolean isFileEmpty(String s){
		File f = new File(s);
		if (!f.isFile()){
			return true;
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(s);
			int b = fis.read();
			if (b != -1){
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	static String formatName(String name){
		String ret;
		String s = name.substring(1).toLowerCase();
		String fs = name.substring(0, 1).toUpperCase();
		ret = fs + s;
		ret = ret.replace("_", " ");
		return ret;
	}
	
	static Server getServer(){
		return plugin.getServer();
	}
	
	static int[] toIntArray(List<Integer> list)  {
	    int[] ret = new int[list.size()];
	    int i = 0;
	    for (Integer e : list)  
	        ret[i++] = e.intValue();
	    return ret;
	}
}
