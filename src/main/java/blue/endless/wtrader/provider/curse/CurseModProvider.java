package blue.endless.wtrader.provider.curse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.ModProvider;
import blue.endless.wtrader.RestQuery;
import blue.endless.wtrader.ModInfo.Version;

public class CurseModProvider implements ModProvider {

	@Override
	public List<ModInfo> search(String keyword) {
		
		return new ArrayList<>();
	}

	@Override
	public InputStream download(ModInfo mod, Version version) throws IOException, IllegalArgumentException {
		
		throw new IOException("Could not find the file.");
	}

	@Override
	public List<ModInfo> update(List<ModInfo> mods) throws IOException, IllegalArgumentException {
		
		List<ModInfo> updated = new ArrayList<>();
		
		for(ModInfo mod : mods) {
			try {
				JsonElement elem = RestQuery.start(mod.fetchUrl); //"https://addons-ecs.forgesvc.net/api/v2/addon/"+mod.fetchURL);
				System.out.println(elem.toJson(JsonGrammar.JSON5));
				if (elem instanceof JsonObject) {
					ModInfo modInfo = Jankson.builder().build().fromJson((JsonObject) elem, CurseModInfo.class).toModInfo();
				} else {
					
				}
				
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Tests equality for simple data objects which don't define their own equals() method, by
	 * reflectively comparing their fields for object equality.
	 * 
	 * <p>Note: For this to work, A and B MUST BE THE SAME EXACT CLASS! This also doesn't take into
	 * account inherited fields, it is ONLY for the absolute simplest classes which would probably
	 * be structs or data classes in any other language.
	 */
	private static <T> boolean deepEquals(T a, T b) {
		Class<?> clazz = a.getClass();
		for(Field field : clazz.getDeclaredFields()) {
			boolean accessible = field.isAccessible();
			try {
				if (!accessible) field.setAccessible(true);
				if (!Objects.equals(field.get(a), field.get(b))) return false;
				if (!accessible) field.setAccessible(false);
			} catch (Throwable t) {
				//Skip this field
			}
		}
		
		return true;
	}
}
