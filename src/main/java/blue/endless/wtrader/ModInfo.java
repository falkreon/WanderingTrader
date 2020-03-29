package blue.endless.wtrader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Objects;

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
	/** An implementation-specific string which the provider can use to uniquely identify the mod */
	public String providerModId;
	
	/** A URI which can retrieve project and update information to populate this object. */
	public String fetchUrl;
	
	public String authors;
	public String description;
	
	public List<Version> versions = new ArrayList<>();
	
	public static class Version {
		/** Same as `id` for the containing object; the cache-id for the mod this Version is for */
		public String modId;
		
		public String providerModId;
		public String providerFileId;
		
		/** The version number or flavor. Usually a semVer string, but is not required to be. */
		public String number;
		
		public String fileName;
		
		/** Milliseconds from the epoch of the date *published*, if possible. Failing that, the date *created*. */
		public long timestamp;
		
		/** A URL where this version of this mod can be downloaded. */
		public String downloadUrl;
		
		/** The minecraft version this mod is made for */
		public String mcVersion;
		
		/**
		 * The modloader(s) used to load this version of the mod. Usually a single-item list containing either
		 * "fabric" or "forge". Occasionally, cases like shedaniel's "Light Overlay" happen, which
		 * should show up here as ["fabric", "forge", "rift"]. Unfortunately, this data will only
		 * be as good as our data source; Curse lists the releases as Fabric, so that's what will
		 * appear here.
		 */
		public List<String> loaders = new ArrayList<>();
		
		/** A list of dependencies _by_their_cache_name_ and version. */
		public List<Dependency> dependencies = new ArrayList<>();
		
		public InputStream download() throws IOException {
			return new URL(downloadUrl).openStream();
		}
		
		/**
		 * Gets this version in a way that it can be reconstructed in its exact form from cache items.
		 * You don't want this unless you specifically want to freeze the mod in a pack-list at a specific
		 * version.
		 */
		public String getFrozenCacheLine() {
			return modId+" "+number+" "+timestamp;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Version) {
				return this.modId.equals(((Version) obj).modId) && this.number.equals(((Version) obj).number);
			} else return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(modId, number);
		}
	}
	
	public static class Dependency {
		/** Who can resolve this Dependency */
		public String provider;
		/** The local id for the mod */
		public String cacheId;
		/** The full version name for this file, in the same format that appears in a ModInfo.Version.number */
		//public String version;
		/** The *mod Id* that can be fetched from this mod's provider */
		public String providerModId;
		/** A fileId such that this mod's provider can fetch the exact file information */
		public String providerFileId;
		/** A url where the dependency's artifact can be downloaded */
		public String downloadUrl;
	}
}
