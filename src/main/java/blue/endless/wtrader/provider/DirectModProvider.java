package blue.endless.wtrader.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blue.endless.wtrader.ModInfo;

public class DirectModProvider extends ModProvider {
	private static DirectModProvider INSTANCE;
	
	public static DirectModProvider instance() {
		if (INSTANCE==null) {
			INSTANCE = new DirectModProvider();
		}
		return INSTANCE;
	}
	
	private DirectModProvider() {}
	
	@Override
	public List<ModInfo> search(String keyword, String loader, String mcVersion) {
		return new ArrayList<>(); //Cannot search a direct provider
	}

	@Override
	public ModInfo fetch(String providerFileId) throws IOException {
		providerFileId = providerFileId.trim();
		String fileName = extractFileName(providerFileId);
		
		ModInfo info = new ModInfo();
		
		info.id = ModProvider.modNameFromFileName(fileName);
		info.name = info.id;
		info.provider = "direct";
		info.providerModId = info.id;
		info.fetchUrl = "";
		
		ModInfo.Version onlyVersion = new ModInfo.Version();
		onlyVersion.fileName = fileName;
		onlyVersion.number = ModProvider.modVersionFromFileName(onlyVersion.fileName, "1.0");
		onlyVersion.providerModId = info.providerModId;
		onlyVersion.providerFileId = providerFileId;
		onlyVersion.downloadUrl = providerFileId;
		
		info.versions.add(onlyVersion);
		
		return info;
	}

	@Override
	public ModInfo.Version resolve(ModInfo.Dependency unresolved, String mcversion) throws IOException {
		ModInfo.Version version = new ModInfo.Version();
		version.fileName = extractFileName(unresolved.providerFileId);
		version.number = ModProvider.modVersionFromFileName(version.fileName, "1.0");
		version.providerModId = unresolved.providerModId;
		version.providerFileId = unresolved.providerFileId;
		version.downloadUrl = unresolved.providerFileId;
		
		version.mcVersion = mcversion; //Always report the version as correct!
		
		return version;
	}
	
	public static String extractFileName(String url) {
		//Probably unnecessary, but get rid of the protocol first, so we can deal with *just* the resource name and the path.
		int protocol = url.indexOf("://");
		if (protocol!=-1) url = url.substring(protocol+3);
		
		/* Grab the last element of the remaining url, *even if it's the resource/server name*.
		 * 
		 * For example, someone might host a webserver at "https://ender.io" that replies to connections
		 * with the most recent EIO artifact instead of markup. In that case, we would prefer that the
		 * filename register as "ender.io" instead of nothing.
		 */
		int lastSlash = url.lastIndexOf("/");
		String lastElement = (lastSlash==-1) ? url : url.substring(lastSlash+1);
		
		return lastElement;
	}
}
