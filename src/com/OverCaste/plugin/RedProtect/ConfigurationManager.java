package com.OverCaste.plugin.RedProtect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Properties;

import com.OverCaste.plugin.RedProtect.RedProtect.FILE_TYPE;

import static com.OverCaste.plugin.RedProtect.RedProtect.*;

public class ConfigurationManager {
	static void initFiles(RedProtect plugin){
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
				fr.write("adminWandID: " + adminWandID + lineSeparator);
				fr.write("#The ID of the information wand." + lineSeparator);
				fr.write("infoWandID: " + infoWandID + lineSeparator);
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
					plugin.setEnabled(false);
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
					plugin.setEnabled(false);
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
					plugin.setEnabled(false);
				}
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
			if((dat = (String)props.getProperty("infoWandID")) != null){
				try {
					infoWandID = Integer.parseInt(dat);
				}
				catch(NumberFormatException e) {
					logger.warning("Configuration value 'infoWandID' isn't a valid integer!");
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
}
