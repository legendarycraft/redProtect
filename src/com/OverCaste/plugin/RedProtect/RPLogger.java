package com.OverCaste.plugin.RedProtect;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RPLogger{
	Logger l;
	
	RPLogger(Logger l){
		this.l = l;
	}
	
	public void info(String s){
		l.info("RedProtect: [" + s + "]");
	}
	
	public void warning(String s){
		l.warning("RedProtect: [" + s + "]");
	}
	
	public void severe(String s){
		l.severe("RedProtect: [" + s + "]");
	}
	
	public void log(Level level, String s){
		l.log(level, "RedProtect: [" + s + "]");
	}
	
	public void debug(String s){
		if (RedProtect.debugMessages){
			l.info("RedProtect Debug: [" + s + "]");
		}
	}
}