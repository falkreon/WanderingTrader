package blue.endless.wtrader.loader;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModSelection;
import blue.endless.wtrader.Modpack;
import blue.endless.wtrader.Progress;
import blue.endless.wtrader.ZipAccess;
import blue.endless.wtrader.provider.curse.CurseModProvider;

public class CurseLoader {
	@Nullable
	public static Modpack load(File f, Consumer<Progress> progressConsumer) throws IOException {
		if (f.getName().endsWith(".zip")) {
			JsonObject obj = ZipAccess.getJsonFile(f, "manifest.json");
			return unpack(obj, progressConsumer);
		} else if (f.getName().endsWith(".json")) {
			try {
				return unpack(Jankson.builder().build().load(f), progressConsumer);
			} catch (SyntaxError e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	private static Modpack unpack(JsonObject obj, Consumer<Progress> progressConsumer) {
		Progress progress = Progress.of("Loading modpack info...");
		progress.max(100);
		progressConsumer.accept(progress);
		
		Modpack pack = new Modpack();
		
		pack.packInfo.name = obj.get(String.class, "name");
		pack.packInfo.version = obj.get(String.class, "version");
		pack.packInfo.authors = obj.get(String.class, "author");
		
		pack.packInfo.mcVersion = obj.getObject("minecraft").get(String.class, "version");
		JsonArray loaders = obj.getObject("minecraft").get(JsonArray.class, "modLoaders");
		for(JsonElement elem : loaders) {
			if (elem instanceof JsonObject) {
				String loaderName = ((JsonObject) elem).get(String.class, "id");
				if (loaderName.startsWith("forge-")) {
					pack.packInfo.loaderVersion = loaderName.substring("forge-".length());
					pack.packInfo.modLoader = "forge";
				}
			}
		}
		
		JsonArray files = obj.get(JsonArray.class, "files");
		progress.max = files.size()+1;
		progress.msg = "Loading first mod...";
		progressConsumer.accept(progress);
		
		for(JsonElement elem : files) {
			
			if (!(elem instanceof JsonObject)) continue;
			JsonObject modEntry = (JsonObject) elem;
			String providerModId = modEntry.get(String.class, "projectID");
			String providerFileId = modEntry.get(String.class, "fileID");
			
			try {
				ModInfo info = CurseModProvider.instance().fetch(providerModId);
				
				ModSelection selection = new ModSelection();
				selection.info = info;
				selection.constraint = ModSelection.Constraint.GREATER_THAN_OR_EQUAL;
				selection.modCacheId = info.id;
				
				//Hook up the Version
				for(ModInfo.Version version : info.versions) {
					if (version.providerFileId.equals(providerFileId)) {
						selection.cachedVersion = version;
						selection.version = version.number;
						selection.timestamp = version.timestamp;
						break;
					}
				}
				
				if (selection.version==null) {
					ModInfo.Version version = CurseModProvider.instance().fetch(providerModId, providerFileId);
					selection.cachedVersion = version;
					selection.version = version.number;
					selection.timestamp = version.timestamp;
				}
				
				pack.mods.add(selection);
				
				progress.value++;
				progress.msg = "Added mod '"+info.name+"' version '"+selection.version+"'";
				progressConsumer.accept(progress);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return pack;
	}
	
	public static void save(Modpack pack, File f) {
		
	}
}
