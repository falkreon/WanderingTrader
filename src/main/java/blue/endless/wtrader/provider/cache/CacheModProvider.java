package blue.endless.wtrader.provider.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.provider.ModProvider;

public final class CacheModProvider extends ModProvider {
	private static CacheModProvider INSTANCE;
	
	private File baseDirectory;
	private Jankson jankson;
	
	private CacheModProvider() {
		baseDirectory = new File("mods");
		if (!baseDirectory.exists()) baseDirectory.mkdir();
		jankson = Jankson.builder().build();
	}
	
	public static CacheModProvider instance() {
		if (INSTANCE==null) {
			INSTANCE = new CacheModProvider();
		}
		
		return INSTANCE;
	}
	
	@Override
	public List<ModInfo> search(String keyword) {
		List<ModInfo> results = new ArrayList<>();
		
		ModInfo result;
		try {
			result = fetch(keyword);
			if (result!=null) results.add(result);
		} catch (IOException e) {}
		
		
		return results;
	}

	@Override
	public ModInfo update(ModInfo mod) throws IOException {
		return mod;
		//the cache doesn't provide *new* information.
	}
	
	private @Nullable ModInfo fetch(File f) {
		try {
			return jankson.fromJson(jankson.load(f), ModInfo.class);
		} catch (IOException | SyntaxError error) {
			return null;
		}
	}

	@Override
	public ModInfo fetch(String providerId) throws IOException {
		File f = new File(baseDirectory, providerId+".json");
		return (f.exists()) ? fetch(f) : null;
	}
}
