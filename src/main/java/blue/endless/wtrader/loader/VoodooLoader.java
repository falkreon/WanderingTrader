package blue.endless.wtrader.loader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModSelection;
import blue.endless.wtrader.Modpack;
import blue.endless.wtrader.Progress;
import blue.endless.wtrader.provider.DirectModProvider;
import blue.endless.wtrader.provider.ModProvider;
import blue.endless.wtrader.provider.curse.CurseModProvider;

public class VoodooLoader {
	public static Modpack load(File f, Consumer<Progress> progressConsumer) throws IOException {
		Progress progress = Progress.of("Loading pack info...")
				.min(0)
				.max(100)
				.value(0);
		progressConsumer.accept(progress);
		
		String preprocessed = org.hjson.JsonValue.readHjson(new FileReader(f)).toString();
		Jankson jankson = Jankson.builder().build();
		JsonObject packObj;
		try {
			packObj = jankson.load(preprocessed);
		} catch (SyntaxError e) {
			throw new IOException(e);
		}
		
		Modpack pack = new Modpack();
		pack.setSaveLocation(null);
		pack.packInfo.name = packObj.get(String.class, "id");
		if (pack.packInfo.name.endsWith(",")) {
			pack.packInfo.name = pack.packInfo.name.substring(0, pack.packInfo.name.length()-1);
		}
		pack.packInfo.description = packObj.get(String.class, "title");
		pack.packInfo.version = packObj.get(String.class, "version");
		pack.packInfo.mcVersion = packObj.get(String.class, "mcVersion");
		pack.packInfo.loaderVersion = packObj.get(String.class, "forge");
		pack.packInfo.modLoader = "forge";
		String authorsString = "";
		String[] authors = packObj.get(String[].class, "authors");
		for(int i=0; i<authors.length; i++) {
			authorsString += authors[i];
			if (i!=authors.length-1) authorsString+=", ";
		}
		pack.packInfo.authors = authorsString;
		
		//pack.packInfo.authors = packObj.get(String.class, "authors");
		
		//Look for Mods
		File modsFolder = new File(f.getParentFile(), "mods");
		if (modsFolder.isDirectory()) {
			//Map<String, ModInfo.Version> id = new HashMap<>();
			
			int totalFiles = modsFolder.list().length;
			progress.msg = "Starting...";
			progress.max(totalFiles+1);
			progressConsumer.accept(progress);
			
			for(File modLock : modsFolder.listFiles()) {
				progress.value++;
				if (modLock.isFile() && (modLock.getName().endsWith(".lock.hjson") || modLock.getName().endsWith(".lock.json"))) {
					progress.msg = "Loading "+modLock.getName();
					progressConsumer.accept(progress);
					
					System.out.println("Loading in '"+modLock.getName()+"'...");
					try {
						System.out.println("HJSON output: "+org.hjson.JsonValue.readHjson(new FileReader(modLock)).toString());
						JsonObject modObject = jankson.load(org.hjson.JsonValue.readHjson(new FileReader(modLock)).toString());
						System.out.println(modObject);
						
						String provider = "";
						if (modObject.containsKey("provider")) {
							//voodoo <= 0.4.9 schema
							provider = modObject.get(String.class, "provider").trim();
							if (provider.endsWith(",")) provider = provider.substring(0, provider.length()-1);
						} else {
							//voodoo >= 0.5.0 schema
							provider = modObject.get(String.class, "type");
							switch(provider) {
							case "voodoo.data.lock.LockEntry.Curse": provider = "CURSE"; break;
							case "voodoo.data.lock.lockEntry.Direct": provider = "DIRECT"; break;
							default: break;
							}
						}
						
						if (provider.equals("CURSE")) {
							ModInfo.Dependency mod = new ModInfo.Dependency();
							mod.provider = "curse";
							mod.providerModId = modObject.get(String.class, "projectID");
							mod.providerFileId = modObject.get(String.class, "fileID");
							
							ModInfo modInfo = CurseModProvider.instance().fetch(mod.providerModId);
							
							ModInfo.Version selected = null;
							for(ModInfo.Version version : modInfo.versions) {
								if (mod.providerFileId.equals(version.providerFileId)) {
									selected = version;
									break;
								}
							}
							
							if (selected==null) {
								try {
									selected = CurseModProvider.instance().fetch(mod.providerModId, mod.providerFileId);
									modInfo.versions.add(selected);
								} catch (Throwable t) {
									System.out.println("DROPPING "+f.getName()+" because its FileID can't be found!");
									continue;
								}
							}
							
							if (selected!=null) {
								ModSelection selection = new ModSelection();
								selection.info = modInfo;
								selection.cachedVersion = selected;
								
								selection.timestamp = selected.timestamp;
								selection.modCacheId = modInfo.id;
								selection.version = selected.number;
								selection.constraint = ModSelection.Constraint.GREATER_THAN_OR_EQUAL;
								
								//Modpack.ModItem item = new Modpack.ModItem();
								//item.selection = selection;
								pack.mods.add(selection);
							}
						} else if (provider.equals("DIRECT")) {
							ModInfo info = DirectModProvider.instance().fetch(modObject.get(String.class, "url"));
							
							ModInfo.Version onlyVersion = info.versions.get(0);
							onlyVersion.fileName = modObject.get(String.class, "fileName");
							if (onlyVersion.fileName.endsWith(",")) onlyVersion.fileName = onlyVersion.fileName.substring(0, onlyVersion.fileName.length()-1);
							onlyVersion.number = ModProvider.modVersionFromFileName(onlyVersion.fileName, "1.0");
							onlyVersion.modId = ModProvider.modNameFromFileName(onlyVersion.fileName);
							
							onlyVersion.mcVersion = pack.packInfo.mcVersion;
							onlyVersion.loaders.add(pack.packInfo.modLoader);
							
							info.id = onlyVersion.modId;
							info.name = info.id;
							info.loaders.addAll(onlyVersion.loaders);
							
							ModSelection selection = new ModSelection();
							selection.info = info;
							selection.cachedVersion = onlyVersion;
							selection.timestamp = System.currentTimeMillis();
							selection.modCacheId = info.id;
							selection.constraint = ModSelection.Constraint.GREATER_THAN_OR_EQUAL;
							
							//Modpack.ModItem item = new Modpack.ModItem();
							//item.selection = selection;
							pack.mods.add(selection);
						} else {
							progress.msg = "dropping unknown provider "+provider;
							progressConsumer.accept(progress);
							System.out.println("DROPPING unknown provider "+provider);
						}
					} catch (Throwable t) {
						//TODO: Tell progressConsumer about this?
						System.out.println("Could not load mod into pack");
						t.printStackTrace();
						System.exit(-1);
					}
				}
			}
		}
		
		return pack;
	}
}
