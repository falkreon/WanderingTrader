package blue.endless.wtrader.provider.curse;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
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
		public List<String> gameVersion;
		
		public ModInfo.Version toVersion() {
			ModInfo.Version version = new ModInfo.Version();
			version.modId = id;
			version.fileName = fileName;
			version.downloadUrl = downloadUrl;
			version.timestamp = Instant.parse(fileDate).toEpochMilli();
			if (gameVersion.size()==1) {
				version.mcVersion = gameVersion.get(0);
			}
			
			version.number = CurseModProvider.extractModVersion(fileName, displayName);
			
			for(Module module: modules) {
				if (module.foldername!=null && module.foldername.equals("mcmod.info")) {
					version.loaders.add("forge");
				} else if (module.foldername!=null && module.foldername.equals("fabric.mod.json")) {
					version.loaders.add("fabric");
				}
			}
			
			//Magic Launcher and its ilk
			if (version.loaders.isEmpty() && fileName.endsWith(".zip")) {
				version.loaders.add("jarmod");
			}
			
			for(Dependency dependency : dependencies) {
				if (dependency.type!=3) continue;
				ModInfo.Dependency wtDep = new ModInfo.Dependency();
				wtDep.provider = "curse";
				wtDep.providerModId = dependency.addonId;
				
				version.dependencies.add(wtDep);
			}
			
			return version;
		}
	}
	
	public static class Module {
		public String foldername;
		public String fingerprint;
		public int type;
	}
	
	public static class Dependency {
		//String id;
		String addonId;
		int type;
		//String fileId;
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
		
		for(Release release : latestFiles) {
			ModInfo.Version version = release.toVersion();
			result.versions.add(version);
			
			for(String s : result.loaders) {
				if (!result.loaders.contains(s)) result.loaders.add(s);
			}
		}
		
		return result;
	}
}
