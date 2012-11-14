package com.OverCaste.plugin.RedProtect;

import java.io.File;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RedProtect extends JavaPlugin{
	public static PluginDescriptionFile pdf;
	PluginManager pm;
	static RedProtect plugin;
	RPBlockListener bListener;
	RPPlayerListener pListener;
	RPEntityListener eListener;
	RPWorldListener wListener;
	CommandManager cManager;
	static RegionManager rm;
	static RPPermissionHandler ph;
	static RPLogger logger = null;
	static final String lineSeparator = System.getProperty("line.separator");
	static Server serv;
	
	static final HashMap<Player, Location> firstLocationSelections = new HashMap<Player, Location>();
	static final HashMap<Player, Location> secondLocationSelections = new HashMap<Player, Location>();
	
	//Paths{
	static final String pathMain = "plugins" + File.separator + "redProtect" + File.separator;
	static final String pathData = pathMain + File.separator + "data" + File.separator;
	static final String pathConfig = pathMain + File.separator + "Config.txt";
	static final String pathFlagConfig = pathMain + File.separator + "Flags.txt";
	//}
	//Configuration{
	static enum FILE_TYPE{
		yml,
		ymlgz,
		oos,
		oosgz,
		mysql
	}
	static FILE_TYPE fileType = FILE_TYPE.yml;
	static boolean removeBlocks = false;
	static boolean debugMessages = false;
	static int limitAmount = 400;
	static int blockID = 55;
	static int maxScan = 600;
	static int heightStart = 50;
	static String mysqlUserName = "root";
	static String mysqlUserPass = "pass";
	static String mysqlDatabaseName = "mcRedProtect";
	static String mysqlHost = "localhost";
	static boolean backup = true;
	//}
	static int adminWandID = Material.FEATHER.getId();
	static int infoWandID = Material.STRING.getId();

	@Override
	public void onDisable() {
		rm.saveAll();
		logger.info(pdf.getName() + " disabled.");
	}

	@Override
	public void onEnable() {
		try {
			RedProtect.plugin = this;
			initVars();
			RPUtil.init(this);
			ConfigurationManager.initFiles(this);
			rm.loadAll();
			pm.registerEvents(bListener, this);
			pm.registerEvents(pListener, this);
			pm.registerEvents(eListener, this);
			pm.registerEvents(wListener, this);
			getCommand("RedProtect").setExecutor(cManager);
			System.out.println(pdf.getFullName() + " enabled.");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error enabling redProtect, plugin will shut down.");
			setEnabled(false);
		}
	}
	
	protected void setEnabled(boolean val){
		super.setEnabled(val);
	}
	
	void initVars() throws Exception {
		serv = getServer();
		logger = new RPLogger(serv.getLogger());
		pdf = this.getDescription();
		pm = serv.getPluginManager();
		bListener = new RPBlockListener(this);
		pListener = new RPPlayerListener(this);
		eListener = new RPEntityListener(this);
		wListener = new RPWorldListener(this);
		cManager = new CommandManager();
		ph = new RPPermissionHandler();
		rm = new RegionManager();
		/*switch(fileType){
		case yml:
		case ymlgz:
		case oosgz:
		case oos:
			rm = new WorldFlatFileRegionManager();
			break;
		case mysql:
			rm = new WorldMySQLRegionManager();
			break;
		}*/
	}

	public RegionManager getGlobalRegionManager() {
		return rm;
	}
}