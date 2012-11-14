package com.OverCaste.plugin.RedProtect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import static com.OverCaste.plugin.RedProtect.RedProtect.mysqlUserName;
import static com.OverCaste.plugin.RedProtect.RedProtect.mysqlUserPass;
import static com.OverCaste.plugin.RedProtect.RedProtect.mysqlDatabaseName;
import static com.OverCaste.plugin.RedProtect.RedProtect.logger;

public class WorldMySQLRegionManager implements WorldRegionManager {
	static String url = "jdbc:mysql://localhost/";
	final static String baseurl = "jdbc:mysql://";
	final static String driver = "com.mysql.jdbc.Driver";
	static String dbname;
	static boolean dbexists = false;
	Connection dbcon = null;
	
	public WorldMySQLRegionManager(World w) throws Exception{
		//DEBUG: RedProtect.logger.info("Initializing a WorldMYSQLRegionManager.");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			RedProtect.logger.severe("Couldn't find the driver for MySQL! " + driver + ".");
			RedProtect.plugin.setEnabled(false);
			return;
		}
		dbname = mysqlDatabaseName + "_" + w.getName();
		Statement st = null;
		try {
	        if(!checkDBExists()) {
	        	Connection con = DriverManager.getConnection(url, mysqlUserName, mysqlUserPass);
	        	st = con.createStatement();
	        	st.executeUpdate("CREATE DATABASE " + dbname);
	        	logger.info("Created database \"" + dbname + "\"!");
			    st.close();
			    st = null;
			    con = DriverManager.getConnection(url + dbname, mysqlUserName, mysqlUserPass);
			    st = con.createStatement();
			    //st.executeUpdate("CREATE TABLE Region(uid int AUTO_INCREMENT PRIMARY KEY, world_uid int, name varchar(16), creator varchar(16))");
			    st.executeUpdate("CREATE TABLE Region" +
			    		"(" +
			    		"uid int AUTO_INCREMENT PRIMARY KEY," +
			    		" name varchar(16)," +
			    		" creator varchar(16)," +
			    		" maxMbrX int," +
			    		" minMbrX int," +
			    		" maxMbrZ int," +
			    		" minMbrZ int," +
			    		" centerX int," +
			    		" centerZ int," +
			    		" pvp boolean," +
			    		" chest boolean," +
			    		" lever boolean," +
			    		" button boolean," +
			    		" door boolean," +
			    		" mobs boolean," +
			    		" animals boolean" +
			    		")");
			    st.close();
			    st = null;
			    logger.info("Created table: 'Region'!");
			    st = con.createStatement();
			    st.executeUpdate("CREATE TABLE Owner(uid int AUTO_INCREMENT PRIMARY KEY, name varchar(16))");
			    st.close();
			    st = null;
			    logger.info("Created table: 'Owner'!");
			    st = con.createStatement();
			    st.executeUpdate("CREATE TABLE Member(uid int AUTO_INCREMENT PRIMARY KEY, name varchar(16))");
			    st.close();
			    st = null;
			    logger.info("Created table: 'Member'!");
			    st = con.createStatement();
			    st.executeUpdate("CREATE TABLE Region_Members(region_uid int, member_uid int)");
			    st.close();
			    st = null;
			    logger.info("Created table: 'Region_Members'!");
			    st = con.createStatement();
			    st.executeUpdate("CREATE TABLE Region_Owners(region_uid int, owner_uid int)");
			    st.close();
			    st = null;
			    logger.info("Created table: 'Region_Owners'!");
			    st = con.createStatement();
			    st.executeUpdate("CREATE TABLE Region_Points(region_uid int, x int, z int, seq_no int)");
			    st.close();
			    logger.info("Created table: 'Region_Points'!");
	        }
	        dbcon = DriverManager.getConnection(url + dbname, mysqlUserName, mysqlUserPass);
	        /*Iterator<World> i = Bukkit.getWorlds().iterator();
	        while(i.hasNext()) {
	        	String name = i.next().getName();
	        	if(getWorldUID(name) == 0) { //World doesn't exist
	        		addWorld(name); //Add world.
	        	}
	        }*/
		} catch (CommunicationsException e) {
			logger.severe("Couldn't connect to mysql! Make sure you have mysql turned on and installed properly, and the service is started.");
			throw new Exception("Couldn't connect to mysql!");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.severe("There was an error while parsing SQL, redProtect will shut down to avoid further damage.");
			throw new Exception("SQLException!");
		} finally {
			if (st != null) st.close();
		}
	}
	
	private boolean checkDBExists() throws SQLException{
		if(dbexists) {
			return true;
		}
		Connection con = DriverManager.getConnection(url, mysqlUserName, mysqlUserPass);
		DatabaseMetaData meta = con.getMetaData();
		ResultSet rs = meta.getCatalogs();
        while (rs.next()) {
            String listOfDatabases=rs.getString("TABLE_CAT");
            if(listOfDatabases.equalsIgnoreCase(dbname)){
            	dbexists = true;
            	return true;
            }
        }
        return false;
	}
	
	int getRegionUID(String region) {
		Statement stmt;
		int ret = -1;
		try {
			stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT uid from Region where name = '" + region + "'");
			int i = 0;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several columns with the same region name detected!");
				}
				ret = rs.getInt("uid");
				i++;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	int getMemberUID(String member) {
		Statement stmt;
		int ret = -1;
		try {
			stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT uid from Member where name = '" + member + "'");
			int i = 0;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several columns with the same member detected!");
				}
				ret = rs.getInt("uid");
				i++;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	String getMemberName(int uid) {
		Statement stmt;
		String ret = null;
		try {
			stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT name from Member where uid = '" + uid + "'");
			int i = 0;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several members with the same unique identifiers detected!");
				}
				ret = rs.getString("name");
				i++;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	int getOwnerUID(String owner) {
		Statement stmt;
		int ret = -1;
		try {
			stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT uid from Owner where name = '" + owner + "'");
			int i = 0;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several columns with the same owner detected!");
				}
				ret = rs.getInt("uid");
				i++;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	String getOwnerName(int uid) {
		Statement stmt;
		String ret = null;
		try {
			stmt = dbcon.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT name from Owner where uid = '" + uid + "'");
			int i = 0;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several owners with the same unique identifiers detected!");
				}
				ret = rs.getString("name");
				i++;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	void addOwner(String owner) {
		if(owner == null) return;
		if(getOwnerUID(owner) != -1) return;
		try {
			Statement st = dbcon.createStatement();
			st.executeUpdate("INSERT INTO Owner (name) values (\"" + owner  + "\")");
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void addMember(String member) {
		if(getMemberUID(member) != -1) return;
		try {
			Statement st = dbcon.createStatement();
			st.executeUpdate("INSERT INTO Member (name) values (\"" + member + "\")");
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void add(Region r){
		if (!regionExists(r)){
			try {
				/*
				 * name
				 * x[]
				 * z[]
				 * owners[]
				 * members[]
				 * creator
				 */
				int uid = -1;
				Statement st = dbcon.createStatement();
				st.executeUpdate("INSERT INTO Region (name,creator,maxMbrX,minMbrX,maxMbrZ,minMbrZ,centerX,centerZ,pvp,chest,lever,button,door,mobs,animals) VALUES (" +
						"\"" + r.getName() + "\", " +
						"\"" + r.getCreator() + "\", " +
						r.getMaxMbrX() + ", " +
						r.getMinMbrX() + ", " +
						r.getMaxMbrZ() + ", " +
						r.getMinMbrZ() + ", " +
						r.getCenterX() + ", " +
						r.getCenterZ() + ", " +
						r.getFlag(0) + ", " +
						r.getFlag(1) + ", " +
						r.getFlag(2) + ", " +
						r.getFlag(3) + ", " +
						r.getFlag(4) + ", " +
						r.getFlag(5) + ", " +
						r.getFlag(6) +
					")"
					, Statement.RETURN_GENERATED_KEYS); //"INSERT INTO Region (name,creator,maxMbrX,minMbrX,maxMbrZ,minMbrZ) VALUES ("r.getName()", "r.getCreator()", r.maxmbrx, r.minmbrx, r.maxmbrz, r.minmbrz)
				ResultSet rs = st.getGeneratedKeys();
				if(rs.next()) {
					uid = rs.getInt(1);
				} else {
					RedProtect.logger.warning("Couldn't generate Primary Key for SQLManager.add(Region r). Region " + r.getName() + " will not be saved.");
					return;
				}
				st.close();
				rs.close();
				
				if(r.getX() != null) {
					int size = r.getX().length;
					for(int i = 0; i<size; i++) {
						st = dbcon.createStatement();
						st.executeUpdate("INSERT INTO Region_Points VALUES (" +
								uid + "," +
								r.getX()[i] + ", " +
								r.getZ()[i] + ", " +
								i + ")"
							); //INSERT INTO Region_Points VALUES (uid, r.getX()[i], r.getZ()[i], i)
						st.close();
					}
				}
				
				for(String member : r.getMembers()) {
					addMember(member);
					int muid = getMemberUID(member);
					st = dbcon.createStatement();
					st.executeUpdate("INSERT INTO Region_Members (region_uid,member_uid) VALUES (" +
							uid + "," +
							muid + ")"
						); //INSERT INTO Region_Members VALUES (uid, muid)
					st.close();
				}
				
				for(String owner : r.getOwners()) {
					addOwner(owner);
					int ouid = getOwnerUID(owner);
					st = dbcon.createStatement();
					st.executeUpdate("INSERT INTO Region_Owners (region_uid,owner_uid) VALUES (" +
							uid + "," +
							ouid + ")"
						); //INSERT INTO Region_Members VALUES (uid, muid)
					st.close();
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void remove(Region r){
		if(regionExists(r)) {
			int uid = getRegionUID(r.getName());
			try {
				Statement st = dbcon.createStatement();
				st.executeUpdate("DELETE FROM Region_Points WHERE region_uid = " + uid);
				st.close();
				st = dbcon.createStatement();
				st.executeUpdate("DELETE FROM Region_Owners WHERE region_uid = " + uid);
				st.close();
				st = dbcon.createStatement();
				st.executeUpdate("DELETE FROM Region_Members WHERE region_uid = " + uid);
				st.close();
				st = dbcon.createStatement();
				st.executeUpdate("DELETE FROM Region WHERE name = \"" + r.getName() + "\"");
				st.close();
				for(String member : r.getMembers()) {
					if(getTotalMemberRegionSize(member) == 0) {
						Statement rst = dbcon.createStatement();
						rst.executeUpdate("DELETE FROM Member WHERE name = \"" + member + "\"");
					}
				}
				for(String owner : r.getOwners()) {
					if(getTotalOwnerRegionSize(owner) == 0) {
						Statement rst = dbcon.createStatement();
						rst.executeUpdate("DELETE FROM Owner WHERE name = \"" + owner + "\"");
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Set<Region> getRegionsIntersecting(int bx, int bz) {
		Set<Region> ret = new HashSet<Region>();
		try {
			String name;
			Statement st = dbcon.createStatement();
			//if ((bx>maxMbrX)||(bx<minMbrX)||(bz>maxMbrZ)||(bz<minMbrZ))
			ResultSet rs = st.executeQuery("SELECT name FROM Region WHERE " + bx + "<=maxMbrX AND " + bx + ">=minMbrX AND " + bz + "<=maxMbrZ AND" + bz + ">=minMbrZ");
			while(rs.next()) {
				name = rs.getString("name");
				ret.add(getRegion(name));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		RedProtect.logger.debug("Rects intersecting " + bx + ", " + bz + ": ");
		for(Region r : ret) {
			RedProtect.logger.debug(r.getName() + r.info());
		}
		return ret;
	}
	
	@Override
	public boolean canBuild(Player p, Block b){
		int bx = b.getX();
		int bz = b.getZ();
		Iterator<Region> i = getRegionsIntersecting(bx, bz).iterator();
		while(i.hasNext()){
			Region poly = i.next();
			if (poly.intersects(bx, bz)){
				return (poly.canBuild(p));
			}
		}
		return true;
	}
	
	@Override
	public Set<Region> getRegions(Player p) {
		return getRegions(p.getName());
	}
	
	@Override
	public Set<Region> getRegions(String p){
		Set<Region> ls = new HashSet<Region>();
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT name from Region where creator = \"" + p + "\"");
			while(rs.next()) {
				ls.add(getRegion(rs.getString("name")));
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ls;
	}
	
	@Override
	public boolean regionExists(Block b){
		return regionExists(b.getX(), b.getZ());
	}
	
	@Override
	public boolean regionExists(int x, int z) {
		Iterator<Region> i = getRegionsIntersecting(x, z).iterator();
		while(i.hasNext()){
			Region poly = i.next();
			if (poly.intersects(x, z)){
				return true;
			}
		}
		return false;
	}
	
	public boolean regionExists(String name) {
		int total = 0;
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) from Region where name = \"" + name + "\"");
			if(rs.next()){
				total = rs.getInt("COUNT(*)");
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return (total > 0);
	}
	
	@Override
	public boolean regionExists(Region region) {
		return regionExists(region.getName());
	}
	
	@Override
	public Region getRegion(Location l) {
		int x = new Double(l.getX()-1).intValue();
		int z = new Double(l.getZ()-1).intValue(); //TODO: Possible bukkit fix later?
		return getRegion(x, z);
	}
	
	private Region getRegion(int x, int z){
		Iterator<Region> i = getRegionsIntersecting(x, z).iterator();
		while(i.hasNext()){
			Region poly = i.next();
			if (poly.intersects(x, z)){
				return poly;
			}
		}
		return null;
	}
	
	@Override
	public Region getRegion(Player p){
		return getRegion(p.getLocation());
	}
	
	@Override
	public Region getRegion(String rname){
		Region ret = null;
		//int[] x, int[] z, String name, List<String> owners, List<String> members, String creator
		if(!regionExists(rname)) {
			return null;
		}
		int regionUID = getRegionUID(rname);
		int[] x;
		int[] z;
		LinkedList<String> owners = new LinkedList<String>();
		List<String> members = new ArrayList<String>();
		int maxMbrX = 0;
		int minMbrX = 0;
		int maxMbrZ = 0;
		int minMbrZ = 0;
		boolean[] flags = new boolean[7];
		String creator = null;
		try {
			Statement st = dbcon.createStatement();
			ArrayList<Integer> xa = new ArrayList<Integer>();
			ArrayList<Integer> za = new ArrayList<Integer>();
			ResultSet rs = st.executeQuery("SELECT x, z, seq_no FROM Region_Points WHERE region_uid = '" + regionUID + "'");
			while(rs.next()) {
				int rsx = rs.getInt("x");
				int rsz = rs.getInt("z");
				int rssq = rs.getInt("seq_no");
				xa.add(rssq, rsx);
				za.add(rssq, rsz);
			}
			if(xa.isEmpty()) {
				x = null;
			} else {
				x = RPUtil.toIntArray(xa);
			}
			if(za.isEmpty()) {
				z = null;
			} else {
				z = RPUtil.toIntArray(za);
			}
			rs.close();
			st.close();
			st = dbcon.createStatement();
			rs = st.executeQuery("SELECT owner_uid FROM Region_Owners WHERE region_uid = '" + regionUID + "'");
			while(rs.next()) {
				owners.add(getOwnerName(rs.getInt("owner_uid")));
			}
			rs.close();
			rs = st.executeQuery("SELECT member_uid FROM Region_Members WHERE region_uid = '" + regionUID + "'");
			while(rs.next()) {
				members.add(getMemberName(rs.getInt("member_uid")));
			}
			rs.close();
			st.close();
			st = dbcon.createStatement();
			rs = st.executeQuery("SELECT creator, maxMbrX, minMbrX, maxMbrZ, minMbrZ, pvp, chest, lever, button, door, mobs, animals from Region WHERE uid = '" + regionUID + "'");
			int i = 0;
			boolean regionValuesSet = false;
			while(rs.next()) {
				if(i != 0) {
					RedProtect.logger.warning("Several columns with the same region name detected! (getRegion.1)");
				}
				creator = rs.getString("creator");
				maxMbrX = rs.getInt("maxMbrX");
				minMbrX = rs.getInt("minMbrX");
				maxMbrZ = rs.getInt("maxMbrZ");
				minMbrZ = rs.getInt("minMbrZ");
				flags[0] = rs.getBoolean("pvp");
				flags[1] = rs.getBoolean("chest");
				flags[2] = rs.getBoolean("lever");
				flags[3] = rs.getBoolean("button");
				flags[4] = rs.getBoolean("door");
				flags[5] = rs.getBoolean("mobs");
				flags[6] = rs.getBoolean("animals");
				regionValuesSet = true;
				i++;
			}
			st.close();
			rs.close();
			if(regionValuesSet)
				ret = new Region(x, z, rname, owners, members, creator, maxMbrX, minMbrX, maxMbrZ, minMbrZ, flags);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	@Override
	public int getTotalRegionSize(String p) {
		if (p == null) return 0;
		int total = 0;
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) from Region where creator = \"" + p + "\"");
			if(rs.next()){
				total = rs.getInt("COUNT(*)");
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return total;
	}
	
	public int getTotalMemberRegionSize(String p) {
		if (p == null) return 0;
		int total = 0;
		int pid = getMemberUID(p);
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) from Region_Members where member_uid = " + pid);
			if(rs.next()){
				total = rs.getInt("COUNT(*)");
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return total;
	}
	
	public int getTotalOwnerRegionSize(String p) {
		if (p == null) return 0;
		int total = 0;
		int pid = getOwnerUID(p);
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT COUNT(*) from Region_Owners where owner_uid = " + pid);
			if(rs.next()){
				total = rs.getInt("COUNT(*)");
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return total;
	}
	
	@Override
	public boolean isSurroundingRegion(Region p){
		return false;
	}
	
	@Override
	public  void load(){}

	@Override
	public void save(){} //sql doesn't require save or load messages
	
	@Override
	public Set<Region> getRegionsNear(Player player, int radius) {
		int px = (int)player.getLocation().getX();
		int pz = (int)player.getLocation().getZ();
		//Iterator<Region> i = getRegionsIntersecting(px, pz).iterator();
		Set<Region> ret = new HashSet<Region>();
		try {
			Statement st = dbcon.createStatement();
			ResultSet rs = st.executeQuery("SELECT name FROM Region where ABS(centerX-" + px + ")<" + radius+1 + " AND ABS(centerZ-" + pz + ")<" + radius+1);
			while(rs.next()) {
				ret.add(getRegion(rs.getString("name")));
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/*while(i.hasNext()){
			Region poly = i.next();
			if(((Math.abs(poly.getCenterX() - px) <= radius) && (Math.abs(poly.getCenterZ() - pz) <= radius))){
				ret.add(poly);
			}
		}*/
		return ret;
	}

	@Override
	public void setFlagValue(Region region, int flag, boolean value) {
		region.setFlag(flag, value);
	}

	@Override
	public void setRegionName(Region rect, String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<Region> getPossibleIntersectingRegions(Region r) {
		//TODO stuff
		return null;
	}
}
