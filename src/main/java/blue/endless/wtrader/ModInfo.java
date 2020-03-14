package blue.endless.wtrader;

import java.util.ArrayList;
import java.util.List;

public class ModInfo {
	/** If possible, the actual modid String known by the loader. Otherwise, a unique identifier for this mod within the WTrader local database. */
	public String id;
	
	/** A human-readable / long-form name for the mod */
	public String name;
	
	/**
	 * The modloader(s) supported by this mod. Each version's loaders field should contain some subset
	 * of this List.
	 */
	public List<String> loaders = new ArrayList<>();
	
	/** A string identifier, all lower-case, which indicates the type of REST query response to expect. Usually "curse". */
	public String provider;
	/** A URI which can retrieve project and update information to populate this object. */
	public String fetchUrl;
	
	public String authors;
	public String description;
	
	public List<Version> versions = new ArrayList<>();
	
	public static class Version {
		/** The version number or flavor. Usually a semVer string, but is not required to be. */
		public String number;
		
		/** A URL where this version of this mod can be downloaded. */
		public String downloadURL;
		
		/**
		 * The modloader(s) used to load this version of the mod. Usually a single-item list containing either
		 * "fabric" or "forge". Occasionally, cases like shedaniel's "Light Overlay" happen, which
		 * should show up here as ["fabric", "forge", "rift"]. Unfortunately, this data will only
		 * be as good as our data source; Curse lists the releases as Fabric, so that's what will
		 * appear here.
		 */
		public List<String> loaders = new ArrayList<>();
		
		/** A list of dependencies _by_their_cache_name_ and version. */
		public List<String> dependencies = new ArrayList<>();
	}
}
