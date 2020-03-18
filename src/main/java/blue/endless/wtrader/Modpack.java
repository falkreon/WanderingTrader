package blue.endless.wtrader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;

public class Modpack {
	private transient File location;
	
	private PackInfo packInfo = new PackInfo();
	private List<ModItem> mods = new ArrayList<>();
	
	
	public void save() {
		saveAs(location);
	}
	
	public void saveAs(File f) {
		
	}
	
	public PackInfo getInfo() {
		return packInfo;
	}
	
	public static Modpack load(File f) throws IOException {
		Modpack pack = new Modpack();
		pack.location = f;
		
		//the following will also fail if !f.exists()
		if (!f.isDirectory()) throw new IOException("The modpack's folder doesn't exist.");
		
		File infoFile = new File(f, "modpack.json");
		if (!infoFile.exists()) throw new IOException("There is no modpack at this location");
		
		Jankson jankson = Jankson.builder().build();
		try {
			jankson.fromJson(jankson.load(infoFile), PackInfo.class);
			
			return pack;
		} catch (SyntaxError e) {
			throw new IOException("The modpack.json had errors: "+e.getCompleteMessage());
		}
		
	}
	
	public static Modpack loadOrCreate(File f) throws IOException {
		if (f.isDirectory() && new File(f, "modpack.json").exists()) {
			return load(f);
		} else {
			Modpack pack = new Modpack();
			if (f.mkdirs()) {
				pack.location = f;
				PackInfo info = new PackInfo();
				info.version = "1.0";
				info.modLoader = "fabric";
				//TODO: Defaults for loaderVersion
				
				pack.packInfo = info;
				
				return pack;
				
			} else throw new IOException("Could not make the modpack folder.");
		}
	}
	
	public static class ModItem {
		String comment;
		ModSelection selection;
	}
}
