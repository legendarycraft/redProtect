package com.OverCaste.plugin.RedProtect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import static org.bukkit.ChatColor.*;

import com.OverCaste.plugin.RedProtect.RPPermissionHandler.Permission;

public class RedProtect extends JavaPlugin{
	public PluginDescriptionFile pdf;
	PluginManager pm;
	static RedProtect plugin;
	RPBlockListener bListener;
	RPPlayerListener pListener;
	RPEntityListener eListener;
	RPWorldListener wListener;
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
	static Permission preferredPerms = Permission.BUKKIT_PERMISSIONS;
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
	static int adminWandID = 288;

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
			initFiles();
			ph.initPermissions();
			rm.loadAll();
			pm.registerEvents(bListener, this);
			pm.registerEvents(pListener, this);
			pm.registerEvents(eListener, this);
			pm.registerEvents(wListener, this);
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
	
	void initVars(){
		serv = getServer();
		logger = new RPLogger(serv.getLogger());
		pdf = this.getDescription();
		pm = serv.getPluginManager();
		bListener = new RPBlockListener(this);
		pListener = new RPPlayerListener(this);
		eListener = new RPEntityListener(this);
		wListener = new RPWorldListener(this);
		ph = new RPPermissionHandler(this);
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
	
	void initFiles(){
		try {
			File main = new File(pathMain);
			File data = new File(pathData);
			File config = new File(pathConfig);
			File flagConfig = new File(pathFlagConfig);
			BufferedWriter fr;
			//Create: {
			if (!main.exists()){
				main.mkdir();
				logger.info("Created folder: " + pathMain);
			}
			if (!data.exists()){
				data.mkdir();
				logger.info("Created folder: " + pathData);
			}
			if (!config.exists()){
				logger.info("Created file: " + pathConfig);
				config.createNewFile();
				fr = new BufferedWriter(new FileWriter(config));
				fr.write("#This is the configuration file, feel free to edit it." + lineSeparator);
				fr.write("#Types: Integer: Number without period; Boolean: True or false; Struct: One of the described strings." + lineSeparator);
				fr.write("#---------" + lineSeparator);
				fr.write("#The data type for the regions file, 'ymlgz', 'yml', 'oos', 'oosgz', 'mysql', oosgz is recommended for normal use. (Struct)" + lineSeparator);
				fr.write("#WARNING: YML IS NOT SUPPORTED DUE TO ERRORS IN BUKKIT. MYSQL WILL COME SOON USE OOS FOR NOW." + lineSeparator);
				fr.write("file-type: oosgz"+ lineSeparator);
				fr.write("#MySQL DB info, don't mess with this unless you're using mysql. (Leave password empty if you're not using one.)" + lineSeparator);
				fr.write("mysql-db-name: redProtect" + lineSeparator);
				fr.write("mysql-user-name: root" + lineSeparator);
				fr.write("mysql-user-pass:  " + lineSeparator);
				fr.write("mysql-host: localhost" + lineSeparator);
				fr.write("#If the redstone and sign should be removed once the Region is successfully created. (Boolean)" + lineSeparator);
				fr.write("remove-blocks: true"+ lineSeparator);
				fr.write("#If debug messages should be printed to console. (Boolean)" + lineSeparator);
				fr.write("debug-messages: false"+ lineSeparator);
				fr.write("#The preferred permissions system, 'bPerms', 'Perms3', 'PEX', 'GM', 'OP', 'SuperPerms', 'Detect' (Struct)" + lineSeparator);
				fr.write("preferred-permissions: Detect"+ lineSeparator);
				fr.write("#Limit the amount of blocks a player without RedProtect.unlimited can protect at one time. -1 for unlimited. (Integer)" + lineSeparator);
				fr.write("limit-amount: 400"+ lineSeparator);
				fr.write("#Height the region starts at, it goes from sky to this value, so 0 would be full sky to bedrock, and 40 would be sky to half way through terrain." + lineSeparator);
				fr.write("height-start: 0" + lineSeparator);
				fr.write("#The ID of the block that you construct regions out of. EX: 55 = Redstone, 85 = Fence (Integer)" + lineSeparator);
				fr.write("block-id: 55"+ lineSeparator);
				fr.write("#The maximum amount of redstone blocks the loop will scan. [Don't make this -1, it's to stop infinite loops.] (Integer)" + lineSeparator);
				fr.write("max-scan: 600"+ lineSeparator);
				fr.write("#Should we backup the database between saves in-case of interruption?" + lineSeparator);
				fr.write("backup: true" + lineSeparator);
				fr.write("#The ID of the selector wand." + lineSeparator);
				fr.write("adminWandID: 288" + lineSeparator);
				fr.close();
			}
			if(!flagConfig.exists()){
				flagConfig.createNewFile();
				fr = new BufferedWriter(new FileWriter(flagConfig));
				fr.write("#This is the flag defaults configuration, feel free to edit it." + lineSeparator);
				fr.write("#The flag can have either true or false default value. Users with required permission can manually toggle these in their own regions." + lineSeparator);
				fr.write("#---------" + lineSeparator);
				fr.write("pvp: false" + lineSeparator);
				fr.write("chest: false" + lineSeparator);
				fr.write("lever: true" + lineSeparator);
				fr.write("button: true" + lineSeparator);
				fr.write("door: false" + lineSeparator);
				fr.write("mobs: true" + lineSeparator);
				fr.close();
			}
			//End of create }
			//Read{
			Properties props = new Properties();
			FileInputStream propfis = new FileInputStream(pathConfig);
			props.load(propfis);
			String dat = "";
			if((dat = (String)props.getProperty("debug-messages")) != null){
				if (dat.equalsIgnoreCase("true")){
					debugMessages = true;
				}
				else if (dat.equalsIgnoreCase("false")){
					debugMessages = false;
				}
				else{
					logger.severe("There is a major error in your configuration, 'debug-messages' isn't an acceptable value.");
					this.setEnabled(false);
				}
			}else{
				logger.severe("Configuration option not found: debug-messages! Defaulting to false.");
			}
			if((dat = (String)props.getProperty("file-type")) != null){
				if (dat.equalsIgnoreCase("yml")){
					logger.debug("Selected mode is yml.");
					fileType = FILE_TYPE.yml;
				}
				else if (dat.equalsIgnoreCase("ymlgz")){
					logger.debug("Selected mode is ymlgz.");
					fileType = FILE_TYPE.ymlgz;
				}
				else if (dat.equalsIgnoreCase("oos")){
					logger.debug("Selected mode is oos.");
					fileType = FILE_TYPE.oos;
				}
				else if (dat.equalsIgnoreCase("oosgz")){
					logger.debug("Selected mode is oosgz.");
					fileType = FILE_TYPE.oosgz;
				}
				else if (dat.equalsIgnoreCase("mysql")){
					logger.debug("Selected mode is mysql.");
					fileType = FILE_TYPE.mysql;
				}
				else{
					logger.severe("There is a major error in your configuration, 'file-type' isn't an acceptable value.");
					this.setEnabled(false);
				}
			}else{
				logger.warning("Configuration option not found: file-type! Defaulting to ymlgz.");
			}
			if((dat = (String)props.getProperty("remove-blocks")) != null){
				if (dat.equalsIgnoreCase("true")){
					removeBlocks = true;
				}
				else if (dat.equalsIgnoreCase("false")){
					removeBlocks = false;
				}
				else{
					logger.severe("There is a major error in your configuration, 'remove-blocks' isn't an acceptable value.");
					this.setEnabled(false);
				}
			}
			if((dat = (String)props.getProperty("preferred-permissions")) != null){
				if (dat.equalsIgnoreCase("bPerms")){
					logger.debug("Selected permissions is bPermissions");
					preferredPerms = Permission.bPERMISSIONS;
				}
				else if (dat.equalsIgnoreCase("PEX")){
					preferredPerms = Permission.PERMISSIONS_EX;
				}
				else if (dat.equalsIgnoreCase("GM")){
					preferredPerms = Permission.GROUP_MANAGER;
				}
				else if (dat.equalsIgnoreCase("Perms3")){
					preferredPerms = Permission.PERMISSIONS_3;
				}
				else if (dat.equalsIgnoreCase("OP")){
					preferredPerms = Permission.OP;
				}
				else if (dat.equalsIgnoreCase("SuperPerms")){
					preferredPerms = Permission.BUKKIT_PERMISSIONS;
				}
				else if (dat.equalsIgnoreCase("Detect")){
					logger.debug("Selected permissions is DETECT");
					preferredPerms = Permission.DETECT;
				}
				else{
					logger.warning("There is an error in your configuration, 'preferred-permissions' isn't an acceptable value. Defaulting to SuperPermissions.");
				}
			}else{
				logger.warning("Configuration option not found: file-type! Defaulting to SuperPermissions.");
			}
			if((dat = (String)props.getProperty("block-id")) != null){
				try{
					blockID = Integer.parseInt(dat);
				}
				catch (NumberFormatException e){
					blockID = 55;
					logger.warning("There is an error in your configuration, 'block-id' isn't a valid integer. Defaulting to Redstone.");
				}
			}else{
				logger.warning("Configuration option not found: block-id! Defaulting to Redstone.");
			}
			if((dat = (String)props.getProperty("limit-amount")) != null){
				try{
					limitAmount = Integer.parseInt(dat);
				}
				catch (NumberFormatException e){
					limitAmount = 400;
					logger.warning("There is an error in your configuration, 'limit-amount' isn't a valid integer. Defaulting to 400.");
				}
			}else{
				logger.warning("Configuration option not found: limit-amount! Defaulting to 400.");
			}
			
			if((dat = (String)props.getProperty("height-start")) != null){
				try{
					heightStart = Integer.parseInt(dat);
				}
				catch (NumberFormatException e){
					heightStart = 0;
					logger.warning("There is an error in your configuration, 'height-start' isn't a valid integer. Defaulting to 0.");
				}
			}else{
				logger.warning("Configuration option not found: height-start! Defaulting to 0.");
			}
			if((dat = (String)props.getProperty("max-scan")) != null){
				try{
					maxScan = Integer.parseInt(dat);
				}
				catch (NumberFormatException e){
					maxScan = 600;
					logger.warning("There is an error in your configuration, 'max-scan' isn't a valid integer. Defaulting to 600.");
				}
			}else{
				logger.warning("Configuration option not found: max-scan! Defaulting to 600.");
			}
			if((dat = (String)props.getProperty("mysql-db-name")) != null){
				mysqlDatabaseName = dat;
			}
			if((dat = (String)props.getProperty("mysql-user-name")) != null){
				mysqlUserName = dat;
			}
			if((dat = (String)props.getProperty("mysql-user-pass")) != null){
				mysqlUserPass = dat;
			}
			if((dat = (String)props.getProperty("mysql-host")) != null){
				mysqlHost = dat;
			}
			if((dat = (String)props.getProperty("backup")) != null){
				if(dat.equalsIgnoreCase("true")||dat.equalsIgnoreCase("yes")){
					backup = true;
				}else{
					backup = false;
				}
			}
			if((dat = (String)props.getProperty("adminWandID")) != null){
				try {
					adminWandID = Integer.parseInt(dat);
				}
				catch(NumberFormatException e) {
					logger.warning("Configuration value 'adminWandID' isn't a valid integer!");
				}
			}
			propfis.close();
			dat = "";
			props = new Properties();
			propfis = new FileInputStream(pathFlagConfig);
			props.load(propfis);
			if((dat = props.getProperty("pvp")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.pvp = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.pvp = false;
				}
			} else {
				logger.warning("Configuration value \"pvp\" isn't initalized, defaulting to false.");
			}
			if((dat = props.getProperty("chest")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.chest = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.chest = false;
				}
			} else {
				logger.warning("Configuration value \"chest\" isn't initalized, defaulting to false.");
			}
			if((dat = props.getProperty("lever")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.lever = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.lever = false;
				}
			} else {
				logger.warning("Configuration value \"lever\" isn't initalized, defaulting to true.");
			}
			if((dat = props.getProperty("button")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.button = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.button = false;
				}
			} else {
				logger.warning("Configuration value \"button\" isn't initalized, defaulting to true.");
			}
			if((dat = props.getProperty("door")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.door = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.door = false;
				}
			} else {
				logger.warning("Configuration value \"door\" isn't initalized, defaulting to false.");
			}
			if((dat = props.getProperty("mobs")) != null) {
				if(dat.equalsIgnoreCase("true")){
					Flags.mobs = true;
				}
				else if(dat.equalsIgnoreCase("false")) {
					Flags.mobs = false;
				}
			} else {
				logger.warning("Configuration value \"mobs\" isn't initalized, defaulting to true.");
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)  {
		if (!(sender instanceof Player)){
			sender.sendMessage("You can't use RedProtect from the console!");
			return true;
		}
		Player player = (Player)sender;
		boolean inRect = false;
		if(args.length == 0){
			//DEBUG: player.getWorld().getBlockAt((int)player.getLocation().getX(), (int)player.getLocation().getY()+1, (int)player.getLocation().getZ()).setType(Material.BOOKSHELF);
			player.sendMessage(AQUA + pdf.getName() + ", version " + pdf.getVersion());
			player.sendMessage(AQUA + "Developed by (" + GOLD + "ikillforeyou [aka. OverCaste]" + AQUA + ").");
			player.sendMessage(AQUA + "For more information about the commands, type [" + GOLD + "/rp ?" + AQUA + "].");
			player.sendMessage(AQUA + "For a tutorial, type [" + GOLD + "/rp tutorial" + AQUA + "].");
			return true;
		}
		else if (args.length == 1){
			if (args[0].equalsIgnoreCase("?")||args[0].equalsIgnoreCase("help")){
				player.sendMessage(AQUA + "Available commands to you: ");
				player.sendMessage(AQUA + "------------------------------------");
				if (ph.helpHasPerm(player, "limit")){
					player.sendMessage(GREEN + "/rp limit");
				}
				if (ph.helpHasPerm(player, "list")){
					player.sendMessage(GREEN + "/rp list");
				}
				if (ph.helpHasPerm(player, "delete")){
					player.sendMessage(GREEN + "/rp delete");
				}
				if (ph.helpHasPerm(player, "info")){
					player.sendMessage(GREEN + "/rp info");
				}
				if (ph.helpHasPerm(player, "addmember")){
					player.sendMessage(GREEN + "/rp addmember (player)");
				}
				if (ph.helpHasPerm(player, "addowner")){
					player.sendMessage(GREEN + "/rp addowner (player)");
				}
				if (ph.helpHasPerm(player, "removemember")){
					player.sendMessage(GREEN + "/rp removemember (player)");
				}
				if (ph.helpHasPerm(player, "removeowner")){
					player.sendMessage(GREEN + "/rp removeowner (player)");
				}
				if (ph.helpHasPerm(player, "rename")){
					player.sendMessage(GREEN + "/rp rename (name)");
				}
				if (ph.hasPerm(player, "redprotect.near")){
					player.sendMessage(GREEN + "/rp near");
				}
				player.sendMessage(GREEN + "/rp flag");
				player.sendMessage(AQUA + "------------------------------------");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("limit")||args[0].equalsIgnoreCase("limitremaining")||args[0].equalsIgnoreCase("remaining")){
				if(ph.hasPerm(player, "redprotect.own.limit")){
					int limit = ph.getPlayerLimit(player);
					if((limit < 0) || (ph.hasPerm(player, "redprotect.unlimited"))){
						player.sendMessage(AQUA + "You have no limit!");
						return true;
					}
					
					int currentUsed = rm.getTotalRegionSize(player.getName());
					player.sendMessage(AQUA + "Your area: (" + GOLD + currentUsed + AQUA + " / " + GOLD + limit  + AQUA + ").");
					return true;
				} else {
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ls")){
				if(ph.hasPerm(player, "redprotect.own.list")){
					Set<Region> regions = rm.getRegions(player);
					if(regions.size() == 0){
						player.sendMessage(AQUA + "You don't have any regions!");
					} else {
						player.sendMessage(AQUA + "Regions you've created:");
						player.sendMessage(AQUA + "------------------------------------");
						Iterator<Region> i = regions.iterator();
						while(i.hasNext()) {
							player.sendMessage(AQUA + i.next().info());
						}
						player.sendMessage(AQUA + "------------------------------------");
					}
					return true;
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
					return true;
				}
			}
			
			if(args[0].equalsIgnoreCase("tutorial") || args[0].equalsIgnoreCase("tut")){
				player.sendMessage(AQUA + "Tutorial:");
				player.sendMessage(AQUA + "1. Surround your creation with " + RPUtil.formatName(Material.getMaterial(blockID).name()) + ".");
				player.sendMessage(AQUA + "2. Place a sign next to your region, with [rp] on the first line.");
				player.sendMessage(AQUA + "3. Enter the name you want your region to be on the 2nd line, or nothing for an automatic name.");
				player.sendMessage(AQUA + "4. Enter 2 additional owners, if you want, on lines 3 & 4.");
				//player.sendMessage(AQUA + "To protect your region, surround your creation with " + ", then place a sign with [rp] on the first line next to it.");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("near") || args[0].equalsIgnoreCase("nr")){
				if(ph.hasPerm(player, "redprotect.near")){
					Set<Region> regions = rm.getRegionsNear(player, 30, player.getWorld());
					if(regions.size() == 0){
						player.sendMessage(AQUA + "There are no regions nearby.");
					}else{
						Iterator<Region> i = regions.iterator();
						player.sendMessage(AQUA + "Regions within 40 blocks: ");
						player.sendMessage(AQUA + "------------------------------------");
						while(i.hasNext()){
							Region r = i.next();
							player.sendMessage(AQUA + "Name: " + GOLD + r.getName() + AQUA + ", Center: [" + GOLD + r.getCenterX() + AQUA + ", " + GOLD + r.getCenterZ() + AQUA + "].");
						}
						player.sendMessage(AQUA + "------------------------------------");
					}
				}else{
					player.sendMessage(RED + "You don't have permission to do that.");
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("flag")) {
				player.sendMessage(AQUA + "To use the command, type '/rp (flag)'. This will toggle the specific flag if you have permission.");
				player.sendMessage(AQUA + "Type '/rp flag info' to show the status of flags in the region you're currently in.");
				return true;
			}
		}
		else if(args.length <= 3) {
			if(args[0].equalsIgnoreCase("define")) {
				if(!player.hasPermission("redprotect.admin.define")) {
					player.sendMessage(RED + "You don't have permission to do that!");
					return true;
				}
				String name = (args.length >= 2) ? args[1] : "";
				String creator = (args.length == 3) ? args[2] : player.getName();
				if (name.equals("")){
					for(int i = 0; true; i++){
						if(player.getName().length() > 13){
							name = player.getName().substring(0, 13) + "_" + i;
						} else{
							name = player.getName() + "_" + i;
						}
						if (RedProtect.rm.getRegion(name, player.getWorld()) == null){
							if(name.length() > 16){
								player.sendMessage(RED + "Couldn't generate automatic region name, please name it yourself.");
								return true;
							}
							break;
						}
					}
				}
				Location l1, l2;
				if(((l1 = firstLocationSelections.get(player)) == null)||((l2 = secondLocationSelections.get(player)) == null)) {
					player.sendMessage(RED + "One or both of your selection positions aren't set!");
					return true;
				}
				if (RedProtect.rm.getRegion(name, player.getWorld()) != null){
					player.sendMessage(RED + "That name is already taken, please choose another one.");
					return true;
				}
				if((name.length() < 2) || (name.length() > 16)) {
					player.sendMessage(RED + "Invalid name, place a 2-16 character name in the 2nd row.");
					return true;
				}
				String[] creators = player.getName().equalsIgnoreCase(creator) ? new String[] {player.getName().toLowerCase()} : new String[] {creator.toLowerCase(), player.getName().toLowerCase()};
				Region rect = new Region(name, creators, new int[] {l1.getBlockX(), l1.getBlockX(), l2.getBlockX(), l2.getBlockX()}, new int[] {l1.getBlockZ(), l1.getBlockZ(), l2.getBlockZ(), l2.getBlockZ()});
				int minX, minZ, maxX, maxZ;
				if(l2.getBlockX() < l1.getBlockX()) {
					minX = l2.getBlockX();
					maxX = l1.getBlockX();
				}
				else {
					maxX = l2.getBlockX();
					minX = l1.getBlockX();
				}
				if(l2.getBlockZ() < l1.getBlockZ()) {
					minZ = l2.getBlockZ();
					maxZ = l1.getBlockZ();
				}
				else {
					maxZ = l2.getBlockZ();
					minZ = l1.getBlockZ();
				}
				for(int xl = minX; xl<=maxX; xl++) {
					if (RedProtect.rm.regionExists(xl, minZ, player.getWorld())||RedProtect.rm.regionExists(xl, maxZ, player.getWorld())){
						player.sendMessage(RED + "You're overlapping another region.");
						rect.delete();
						return true;
					}
				}
				for(int zl = minZ; zl<=maxZ; zl++) {
					if (RedProtect.rm.regionExists(minX, zl, player.getWorld())||RedProtect.rm.regionExists(maxX, zl, player.getWorld())){
						player.sendMessage(RED + "You're overlapping another region.");
						rect.delete();
						return true;
					}
				}
				// } Make sure you haven't completely surrounded another region: {
				if (RedProtect.rm.isSurroundingRegion(rect, player.getWorld())){
					//DEBUG: p.sendMessage("1: ");
					player.sendMessage(RED + "You're overlapping another region.");
					rect.delete();
					return true;
				}
				player.sendMessage(GREEN + "Successfully created region: " + rect.getName() + ".");
				RedProtect.rm.add(rect, player.getWorld());
				return true;
			}
		}
		//Things you need to be in a rect to do:
		Region rect = rm.getRegion(player, player.getWorld());
		if (rect != null){
			inRect = true;
		}
		//Length 1:
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("delete")||args[0].equalsIgnoreCase("del")){
				if (ph.hasRegionPerm(player, "delete", rect)){
					if (inRect){
						player.sendMessage(AQUA +  "Region successfully deleted.");
						rm.remove(rect);
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("i")||args[0].equalsIgnoreCase("info")){
				if (ph.hasRegionPerm(player, "info", rect)){
					if (inRect){
						player.sendMessage(rect.info());
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
		}
		//Length 2:
		if (args.length == 2){
			Player victim = serv.getPlayerExact(args[1]);
			if (args[0].equalsIgnoreCase("am")||args[0].equalsIgnoreCase("addmember")){
				if (ph.hasRegionPerm(player, "addmember", rect)){
					if (inRect){
						args[1] = args[1].toLowerCase();
						if (rect.isOwner(args[1])){
							rect.removeOwner(args[1]);
							rect.addMember(args[1]);
							if (victim != null){
								if (victim.isOnline()){
									victim.sendMessage(AQUA + "You have been demoted to member in: " + GOLD + rect.getName() + AQUA + ", by: " + GOLD + player.getName() + AQUA + ".");
								}
							}
							player.sendMessage(AQUA + "Demoted player " + GOLD + args[1] + AQUA + " to member in " + GOLD + rect.getName() + AQUA + ".");
						}else{
							if (!rect.isMember(args[1])){
								rect.addMember(args[1]);
								player.sendMessage(AQUA + "Added " + GOLD + args[1] + AQUA + " as a member.");
								if (victim != null){
									if (victim.isOnline()){
										victim.sendMessage(AQUA + "You have been added as a member to region: " + GOLD + rect.getName() + AQUA + ", by: " + GOLD + player.getName() + AQUA + ".");
									}
								}
							}else{
								player.sendMessage(RED + args[1] + " is already a member in this region.");
							}
						}
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("ao")||args[0].equalsIgnoreCase("addowner")){
				if (ph.hasRegionPerm(player, "addowner", rect)){
					if (inRect){
						args[1] = args[1].toLowerCase();
						if (!rect.isOwner(args[1])){
							rect.addOwner(args[1]);
							player.sendMessage(AQUA + "Added " + GOLD + args[1] + AQUA + " as an owner.");
							if (victim != null){
								if (victim.isOnline()){
									victim.sendMessage(AQUA + "You have been added as an owner to region: " + GOLD + rect.getName() + AQUA + ", by: " + GOLD + player.getName() + AQUA + ".");
								}
							}
						}else{
							player.sendMessage(RED + "That player is already an owner in this region!");
						}
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("rm")||args[0].equalsIgnoreCase("removemember")){
				if (ph.hasRegionPerm(player, "removemember", rect)){
					if (inRect){
						args[1] = args[1].toLowerCase();
						if (rect.isMember(args[1]) || rect.isOwner(args[1])){
							player.sendMessage(AQUA + "Removed " + GOLD + args[1] + AQUA + " from this region.");
							rect.removeMember(args[1]);
						}else{
							player.sendMessage(RED + args[1] + " isn't a member of this region.");
						}
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("ro")||args[0].equalsIgnoreCase("removeowner")){
				if (ph.hasRegionPerm(player, "removeowner", rect)){
					if (inRect){
						args[1] = args[1].toLowerCase();
						if (rect.isOwner(args[1])){
							if (rect.ownersSize() > 1){
								player.sendMessage(AQUA + "Made " + GOLD + args[1] + AQUA + " a member in this region.");
								rect.removeOwner(args[1]);
								rect.addMember(args[1]);
							}else{
								player.sendMessage(AQUA + "You can't remove " + GOLD + args[1] + AQUA + ", because they are the last owner in this region.");
							}
						}else{
							player.sendMessage(RED + args[1] + " isn't an owner in this region.");
						}
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if (args[0].equalsIgnoreCase("rn")||args[0].equalsIgnoreCase("rename")){
				if (ph.hasRegionPerm(player, "rename", rect)){
					if (inRect){
						if (rm.getRegion(args[1], player.getWorld()) != null){
							player.sendMessage(RED + "That name is already taken, please choose another one.");
							return true;
						}
						if((args[1].length() < 2) || (args[1].length() > 16)){
							player.sendMessage(RED + "Invalid name. Please enter a 2-16 character name.");
							return true;
						}
						if (args[1].contains(" ")){
							player.sendMessage(RED + "The name of the region can't have a space in it.");
							return true;
						}
						rm.rename(rect, args[1], player.getWorld());
						player.sendMessage(AQUA + "Made " + GOLD + args[1] + AQUA + " the new name for this region.");
					}else{
						player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
					}
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
				}
				return true;
			}
			if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("ls")){
				if(ph.hasPerm(player, "redprotect.admin.list")){
					Set<Region> regions = rm.getRegions(args[1]);
					int length = regions.size();
					if(length == 0){
						player.sendMessage(AQUA + "That player has no regions!");
					} else {
						player.sendMessage(AQUA + "Regions created:");
						player.sendMessage(AQUA + "------------------------------------");
						Iterator<Region> i = regions.iterator();
						while(i.hasNext()) {
							player.sendMessage(AQUA + i.next().info());
						}
						player.sendMessage(AQUA + "------------------------------------");
					}
					return true;
				}else{
					player.sendMessage(RED + "You don't have sufficient permission to do that.");
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("flag")||args[0].equalsIgnoreCase("fl")) {
				if(args[1].equalsIgnoreCase("pvp")) {
					if(ph.hasPerm(player, "redprotect.flag.pvp")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rm.setFlag(rect, 0 /*pvp*/, !rect.getFlag(0), player.getWorld());
								player.sendMessage(AQUA + "Flag \"pvp\" has been set to " + rect.getFlag(0) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("chest")) {
					if(ph.hasPerm(player, "redprotect.flag.chest")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(1 /*pvp*/, !rect.getFlag(1));
								player.sendMessage(AQUA + "Flag \"chest\" has been set to " + rect.getFlag(1) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("lever")) {
					if(ph.hasPerm(player, "redprotect.flag.lever")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(2 /*pvp*/, !rect.getFlag(2));
								player.sendMessage(AQUA + "Flag \"lever\" has been set to " + rect.getFlag(2) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("button")) {
					if(ph.hasPerm(player, "redprotect.flag.button")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(3 /*pvp*/, !rect.getFlag(3));
								player.sendMessage(AQUA + "Flag \"button\" has been set to " + rect.getFlag(3) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("door")) {
					if(ph.hasPerm(player, "redprotect.flag.door")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(4 /*pvp*/, !rect.getFlag(4));
								player.sendMessage(AQUA + "Flag \"door\" has been set to " + rect.getFlag(4) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("mobs")) {
					if(ph.hasPerm(player, "redprotect.flag.mobs")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(5 /*pvp*/, !rect.getFlag(5));
								player.sendMessage(AQUA + "Flag \"mobs\" has been set to " + rect.getFlag(5) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("animals")) {
					if(ph.hasPerm(player, "redprotect.flag.animals")){
						if(inRect) {
							if(rect.isOwner(player)||ph.hasPerm(player, "redprotect.admin.flag")){
								rect.setFlag(5 /*pvp*/, !rect.getFlag(6));
								player.sendMessage(AQUA + "Flag \"animals\" has been set to " + rect.getFlag(6) + ".");
							} else {
								player.sendMessage(AQUA + "You don't have permission to toggle that flag in this region!");
							}
						} else {
							player.sendMessage(RED + "You need to be standing inside of a region to use that command!");
						}
					} else {
						player.sendMessage(RED + "You don't have permission to toggle that flag!");
					}
				}
				else if(args[1].equalsIgnoreCase("info")||args[1].equalsIgnoreCase("i")){
					player.sendMessage(AQUA + "Flag values: (" + rect.getFlagInfo() + AQUA + ")");
				}
				else {
					player.sendMessage(AQUA + "List of flags: [pvp, chest, lever, button, door, mobs, animals]");
				}
				return true;
			}
		}
		return false;
	}

	public RegionManager getGlobalRegionManager() {
		return rm;
	}
}