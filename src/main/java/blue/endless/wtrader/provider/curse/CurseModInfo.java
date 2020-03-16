package blue.endless.wtrader.provider.curse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.provider.cache.CacheModProvider;

public class CurseModInfo {
	/** The unique numeric ID for the mod */
	public String id;
	public String name;
	/** Description of the mod */
	public String summary;
	public List<Attribution> authors = new ArrayList<>();
	public List<Attachment> attachments = new ArrayList<>();
	public List<Release> latestFiles = new ArrayList<>();
	public String slug;
	
	/**
	 * Attributions in Curse act like a many-to-many table: Each record has foreign keys to a user and a project, as well
	 * as a little bit of data either cached or captured by the query that generates these objects.
	 */
	public static class Attribution {
		/** FOREIGN KEY into projects table */
		public String projectId;
		/** FOREGIN KEY into curse-users table */
		public String userId;
		/** FOREGIN KEY into twitch-users table */
		public String twitchId;
		
		
		/** Curse display name of the user */
		public String name;
		/** Profile url of the user */
		public String url;
		/** Unknown. May be an autoincrement primary key in the relationships table, as it's often large sequential numbers within a project. */
		public String id;
		/** Probably a numeric key into an enum for project roles */
		public String projectTitleId;
		/** The human-readable project role for this relationship, e.g. "Author", "Artist" */
		public String projectTitleTitle;
	}
	
	/**
	 * Currently curse uses this for image attachments.
	 */
	public static class Attachment {
		/** PRIMARY KEY in the attachments table probably */
		public String id;
		/** Will be the same as the project it's contained in - this is just cruft that comes from turning a SQL query into a json object */
		public String projectId;
		public String description;
		/** True if this image is displayed on the project page */
		public boolean isDefault;
		public String thumbnailUrl;
		public String title;
		public String url;
		/** This seems to be numeric but let's not take any chances. */
		public String status;
	}
	
	public static class Release {
		public String id;
		public String displayName;
		public String fileName;
		public String fileDate;
		public long fileLength;
		public int releaseType;
		public int fileStatus;
		public String downloadUrl;
		public boolean isAlternate;
		public String alternateFileId;
		public List<Dependency> dependencies;
		public List<Module> modules;
	}
	
	public static class Module {
		public String foldername;
		public String fingerprint;
		public int type;
	}
	
	public static class Dependency {
		String id;
		String addonId;
		int type;
		String fileId;
	}
	
	public ModInfo toModInfo() {
		ModInfo result = new ModInfo();
		
		result.id = slug;
		result.name = name;
		result.provider = "curse";
		result.providerId = id;
		
		StringBuilder resultAuthors = new StringBuilder();
		for(Attribution attribution : authors) {
			if (resultAuthors.length()>0) {
				resultAuthors.append(", ");
			}
			
			resultAuthors.append(attribution.name);
		}
		result.authors = resultAuthors.toString();
		result.fetchUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/"+id;
		result.description = summary;
		
		Set<String> allLoaders = new HashSet<>();
		
		for(Release release : latestFiles) {
			ModInfo.Version version = new ModInfo.Version();
			version.fileName = release.fileName;
			version.downloadUrl = release.downloadUrl;
			version.number = release.displayName; //But only as a last resort!
			version.timestamp = Instant.parse(release.fileDate).toEpochMilli();
			
			//This is a much better heuristic, since this is how gradle and maven expect to package artifacts and shuttle them around
			//Note that this will necessarily capture the artifact *classifier* as part of the version. We have no way of separating them.
			//Some modders put their versions as "MC1.12.2-1.0.8", some use "1.0.8-MC1.12.2", and some use the actually-parseable semver "1.0.8+MC1.12.2".
			//Some omit the minecraft version. Someone could get really funny and use "xenial-xerus" as their version name, entirely unnumbered.
			//This is all part of the Flavor Dimension problem ( see https://developer.android.com/studio/build/build-variants#product-flavors )
			//So we will refuse to parse version strings, and instead do lookups for their timestamps and use those for version comparison.
			int firstSeparator = version.fileName.indexOf('-');
			if (firstSeparator!=-1 && version.fileName.length()>firstSeparator+1) {
				String maybeVersionNumber = version.fileName.substring(firstSeparator+1);
				if (maybeVersionNumber.endsWith(".jar")) {
					maybeVersionNumber = maybeVersionNumber.substring(0, maybeVersionNumber.length()-4);
				}
				
				if (!maybeVersionNumber.isEmpty()) version.number = maybeVersionNumber;
			}
			
			for(Module module: release.modules) {
				if (module.foldername!=null && module.foldername.equals("mcmod.info")) {
					version.loaders.add("forge");
					allLoaders.add("forge");
				} else if (module.foldername!=null && module.foldername.equals("fabric.mod.json")) {
					version.loaders.add("fabric");
					allLoaders.add("fabric");
				}
			}
			
			//Magic Launcher and its ilk
			if (allLoaders.isEmpty() && release.fileName.endsWith(".zip")) {
				allLoaders.add("jarmod");
			}
			
			for(Dependency dependency : release.dependencies) {
				ModInfo.Dependency wtDep = new ModInfo.Dependency();
				wtDep.providerFileId = dependency.fileId;
				wtDep.providerModId = dependency.addonId;
				
				//If it's already in the cache, grab it
				wtDep.cacheId = CurseModProvider.instance().getCacheId(dependency.addonId); //Will overwrite null with null if it's not in the cache
				//We don't have a reverse-search of fileId->version but that's not super important for the purpose of resolving mods
				
				version.dependencies.add(wtDep);
			}
			
			result.versions.add(version);
			
		}
		result.loaders.addAll(allLoaders);
		
		return result;
	}
}
