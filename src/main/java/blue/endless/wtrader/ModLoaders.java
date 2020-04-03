package blue.endless.wtrader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;

/** Keeps track of valid fabric/forge versions, and maps mcVersions to loader versions */
public class ModLoaders {
	private static ModLoaders INSTANCE;
	
	public static ModLoaders instance() {
		if (INSTANCE==null) {
			INSTANCE = new ModLoaders();
			INSTANCE.load();
		}
		
		return INSTANCE;
	}
	
	
	private VersionData versions;
	private File dataFile = new File(".", "modloaders.json");
	
	
	private void refresh() {
		Jankson jankson = Jankson.builder().build();
		
		//Check for minecraft versions supported by fabric
		try {
			JsonElement elem = RestQuery.start("https://meta.fabricmc.net/v2/versions/game/");
			if (elem instanceof JsonArray) {
				for(JsonElement versionElem : (JsonArray) elem) {
					if (versionElem instanceof JsonObject) {
						String mcVersion = ((JsonObject) versionElem).get(String.class, "version");
						
						if (!versions.fabric.mcVersions.contains(mcVersion)) versions.fabric.mcVersions.add(mcVersion);
					}
				}
			} else {
				throw new IOException("Expected an array of minecraft versions supported by fabric, but server replied with "+elem.getClass().getSimpleName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Get the most recent 5 fabric-loader versions. Generally you'll only need the newest.
		try {
			JsonElement elem = RestQuery.start("https://meta.fabricmc.net/v2/versions/loader/?limit=5");
			if (elem instanceof JsonArray) {
				for(JsonElement versionElem : (JsonArray) elem) {
					if (versionElem instanceof JsonObject) {
						FabricLoaderVersion loaderVersion = jankson.fromJson((JsonObject) versionElem, FabricLoaderVersion.class);
						
						versions.fabric.loaderVersions.remove(loaderVersion); //remove an old copy if it exists
						versions.fabric.loaderVersions.add(loaderVersion);
					}
				}
			} else {
				throw new IOException("Expected an array of fabric loader versions, but server replied with "+elem.getClass().getSimpleName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Check curse for modloader versions
		try {
			JsonElement elem = RestQuery.start("https://addons-ecs.forgesvc.net/api/v2/minecraft/modloader");
			if (elem instanceof JsonArray) {
				for(JsonElement versionElem : (JsonArray) elem) {
					if (versionElem instanceof JsonObject) {
						JsonObject obj = (JsonObject) versionElem;
						
						ForgeVersion version = new ForgeVersion();
						String mcVersion =obj.get(String.class, "gameVersion");
						version.name = obj.get(String.class, "name");
						if (version.name.startsWith("forge-")) version.name = version.name.substring("forge-".length());
						version.timestamp = Instant.parse(obj.get(String.class, "dateModified")).toEpochMilli();
						version.recommended = obj.get(Boolean.class, "recommended");
						
						List<ForgeVersion> versionList = versions.forge.get(mcVersion);
						if (versionList==null) {
							versionList = new ArrayList<>();
							versions.forge.put(mcVersion, versionList);
						}
						versionList.remove(version); //remove an old copy if it exists
						versionList.add(version);
					}
				}
			} else {
				throw new IOException("Expected an array of fabric loader versions, but server replied with "+elem.getClass().getSimpleName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		save();
	}
	
	private void load() {
		Jankson jankson = Jankson.builder().build();
		if (dataFile.exists()) {
			try {
				JsonObject obj = jankson.load(dataFile);
				versions = new VersionData();
				versions.fabric = jankson.fromJson(obj.getObject("fabric"), FabricVersions.class);
				JsonObject forgeObj = obj.getObject("forge");
				//Manually unpack forge because jankson has a nested generics bug
				for(Map.Entry<String, JsonElement> mcVersionEntry : forgeObj.entrySet()) {
					if (!(mcVersionEntry.getValue() instanceof JsonArray)) continue;
					String mcVersion = mcVersionEntry.getKey();
					ForgeVersion[] forgeVersions = jankson.getMarshaller().marshall(ForgeVersion[].class, mcVersionEntry.getValue());
					
					versions.forge.put(mcVersion, Lists.newArrayList(forgeVersions));
				}
				
			} catch (IOException | SyntaxError e) {
				e.printStackTrace();
			}
		}
		
		if (versions==null) {
			versions = new VersionData();
			refresh();
			save();
		}
	}
	
	private void save() {
		Jankson jankson = Jankson.builder().build();
		JsonElement flat = jankson.toJson(versions);
		try {
			FileWriter writer = new FileWriter(dataFile);
			flat.toJson(writer, JsonGrammar.JSON5, 0);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getMCVersions(String launcherName) {
		switch(launcherName) {
		case "fabric":
			return ImmutableList.copyOf(versions.fabric.mcVersions);
		case "forge":
			return ImmutableList.copyOf(versions.forge.keySet());
		default:
			return ImmutableList.of();
		}
	}
	
	public List<String> getLoaderVersions(String launcherName, String mcVersion) {
		switch(launcherName) {
		case "fabric":
			//Return the names of all known fabric versions because they're all valid.
			ArrayList<String> result = new ArrayList<>();
			for(FabricLoaderVersion version : versions.fabric.loaderVersions) {
				result.add(version.version);
			}
			return result;
		case "forge":
			ArrayList<String> forgeResult = new ArrayList<>();
			List<ForgeVersion> forgeVersions = versions.forge.get(mcVersion);
			if (forgeVersions==null) return ImmutableList.of();
			for(ForgeVersion version : forgeVersions) {
				forgeResult.add(version.name);
			}
			return forgeResult;
		default:
			return ImmutableList.of();
		}
	}
	
	public String getRecommendedVersion(String launcherName, String mcVersion) {
		switch(launcherName) {
		case "fabric":
			int bestBuild = -1;
			String bestBuildName = "";
			for(FabricLoaderVersion version : versions.fabric.loaderVersions) {
				if (version.build > bestBuild) {
					bestBuild = version.build;
					bestBuildName = version.version;
				}
			}
			return bestBuildName;
		case "forge":
			//Look for a "recommended" build
			List<ForgeVersion> forgeVersions = versions.forge.get(mcVersion);
			if (forgeVersions==null) return "";
			for(ForgeVersion forgeVersion : forgeVersions) {
				if (forgeVersion.recommended) return forgeVersion.name;
			}
			return "";
		default:
			return "";
		}
	}
	
	private static class VersionData {
		public FabricVersions fabric = new FabricVersions();
		public Map<String, List<ForgeVersion>> forge = new HashMap<>();
	}
	
	private static class FabricVersions {
		@Comment("Minecraft versions which are supported by fabric")
		public List<String> mcVersions = new ArrayList<>();
		
		@Comment("Every loader version is compatible with every Minecraft version")
		public List<FabricLoaderVersion> loaderVersions = new ArrayList<>();
	}
	
	public static class FabricLoaderVersion {
		private String separator;
		private boolean stable;
		private int build;
		private String maven;
		private String version;
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof FabricLoaderVersion) &&
					obj!=null &&
					Objects.equal(((FabricLoaderVersion) obj).version, version);
		}
		
		@Override
		public int hashCode() {
			return version.hashCode();
		}
	}
	
	
	
	public static class ForgeVersion {
		public String name;
		public long timestamp;
		public boolean recommended;
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ForgeVersion) &&
					obj != null &&
					Objects.equal(((ForgeVersion) obj).name, name);
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
}
