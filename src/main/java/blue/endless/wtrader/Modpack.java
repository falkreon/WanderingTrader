package blue.endless.wtrader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.api.SyntaxError;

public class Modpack {
	private transient File location;
	
	public PackInfo packInfo = new PackInfo();
	public List<ModSelection> mods = new ArrayList<>();
	
	
	public void save() {
		saveAs(location);
	}
	
	public void saveAs(File f) {
		try {
			FileWriter writer = new FileWriter(f);
			Jankson.builder().build().toJson(this).toJson(writer, JsonGrammar.JSON5, 0);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			pack.packInfo = jankson.fromJson(jankson.load(infoFile), PackInfo.class);
			
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
	
	public void setSaveLocation(File f) {
		this.location = f;
	}

	public File getSaveLocation() {
		return location;
	}
	
	/*
	
	public static class ModItem {
		public String comment;
		public ModSelection selection;
		
		@Serializer
		public JsonElement toJson(Marshaller marshaller) {
			if (selection==null) {
				return JsonPrimitive.of(comment);
			} else {
				return marshaller.serialize(selection);
			}
		}
		
		@Deserializer
		public static ModItem deserialize(String s) {
			System.out.println("Deserializing "+s);
			ModItem result = new ModItem();
			result.comment = s;
			return result;
		}
		
		@Deserializer
		public static ModItem deserialize(JsonObject obj, Marshaller marshaller) {
			System.out.println("Deserializing "+obj.toJson());
			
			ModSelection selection = marshaller.marshall(ModSelection.class, obj);
			
			ModItem result = new ModItem();
			result.selection = selection;
			return result;
		}
	}*/
}
