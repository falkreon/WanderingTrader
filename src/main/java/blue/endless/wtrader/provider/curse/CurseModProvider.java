package blue.endless.wtrader.provider.curse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;
import blue.endless.wtrader.ModInfo;
import blue.endless.wtrader.RestQuery;
import blue.endless.wtrader.provider.ModProvider;

public class CurseModProvider extends ModProvider {
	private static CurseModProvider INSTANCE;
	private static final String CACHE_FILE_NAME = "cursemods.json";
	private static final int MOD_CATEGORY_ID = 8;
	
	private Map<String, String> idCache = new HashMap<>();
	
	private CurseModProvider() {
		//TODO: Might load and write-through-cache the curseId->modId map here
		
		File modIdCache = new File(CACHE_FILE_NAME);
		if (modIdCache.exists()) {
			loadCache();
		} else {
			saveCache();
		}
	}
	
	private void saveCache() {
		JsonObject obj = new JsonObject();
		for(Map.Entry<String, String> entry : idCache.entrySet()) {
			obj.put(entry.getKey(), JsonPrimitive.of(entry.getValue()));
		}
		
		File cacheFile = new File(CACHE_FILE_NAME);
		FileWriter out;
		try {
			out = new FileWriter(cacheFile);
			obj.toJson(out, JsonGrammar.JSON5, 0);
			out.flush();
			out.close();
		} catch (IOException e) {
			//Can't do much but complain!
			e.printStackTrace();
		}
	}
	
	private void loadCache() {
		try {
			JsonObject obj = Jankson.builder().build().load(new File(CACHE_FILE_NAME));
			idCache.clear(); //If we get here we've safely extracted the data.
			
			for(Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				if (entry.getValue() instanceof JsonPrimitive) {
					idCache.put(entry.getKey(), ((JsonPrimitive) entry.getValue()).asString());
				}
			}
			
		} catch (IOException | SyntaxError e) {
			e.printStackTrace();
		}
	}
	
	public @Nullable String getCacheId(String curseAddonId) {
		return idCache.get(curseAddonId);
	}
	
	public static CurseModProvider instance() {
		if (INSTANCE==null) {
			INSTANCE = new CurseModProvider();
		}
		
		return INSTANCE;
	}
	
	@Override
	public List<ModInfo> search(String keyword) {
		List<ModInfo> searchResults = new ArrayList<>();
		
		try {
			Jankson jankson = Jankson.builder().build();
			keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name()); //Remember folks, sanitize your inputs!
			JsonElement jsonElem = RestQuery.start("https://addons-ecs.forgesvc.net/api/v2/addon/search?gameId=432&searchFilter="+keyword);
			if (jsonElem instanceof JsonArray) {
				JsonArray results = (JsonArray) jsonElem;
				
				for(JsonElement result : results) {
					if (result instanceof JsonObject) {
						Integer categoryId = ((JsonObject) result).recursiveGet(Integer.class, "categorySection.id");
						if (categoryId==null || categoryId.intValue()!=MOD_CATEGORY_ID) continue;
						
						ModInfo info = jankson.fromJson((JsonObject) result, CurseModInfo.class).toModInfo();
						searchResults.add(info);
					}
				}
			}
			
		} catch (IOException error) {
			
		}
		
		return searchResults;
	}
	
	
	
	//@Override
	//public ModInfo update(ModInfo mod) throws IOException {
		
	//	return fetch(mod.providerId);
		/*
		JsonElement elem = RestQuery.start(mod.fetchUrl); //"https://addons-ecs.forgesvc.net/api/v2/addon/"+mod.fetchURL);
		
		if (elem instanceof JsonObject) {
			Integer categoryId = ((JsonObject) elem).recursiveGet(Integer.class, "categorySection.id");
			if (categoryId==null || categoryId.intValue()!=8) throw new IOException("Server responded with a non-mod object (categoryId="+categoryId+").");
			
			System.out.println(elem.toJson(JsonGrammar.JSON5));
			
			ModInfo modInfo = Jankson.builder().build().fromJson((JsonObject) elem, CurseModInfo.class).toModInfo();
			return modInfo;
		} else {
			throw new IOException("Server response was valid json but not a JsonObject (was a "+elem.getClass().getSimpleName()+").");
		}*/
	//}
	
	/**
	 * Tests equality for simple data objects which don't define their own equals() method, by
	 * reflectively comparing their fields for object equality.
	 * 
	 * <p>Note: For this to work, A and B MUST BE THE SAME EXACT CLASS! This also doesn't take into
	 * account inherited fields, it is ONLY for the absolute simplest classes which would probably
	 * be structs or data classes in any other language.
	 * 
	 * <p> TODO: burn this code and forget it forever
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
	
	@Override
	public ModInfo fetch(String providerId) throws IOException {
		JsonElement elem = RestQuery.start("https://addons-ecs.forgesvc.net/api/v2/addon/"+providerId);
		if (elem instanceof JsonObject) {
			Integer categoryId = ((JsonObject) elem).recursiveGet(Integer.class, "categorySection.id");
			if (categoryId==null || categoryId.intValue()!=8) throw new IOException("Server responded with a non-mod object (categoryId="+categoryId+").");
			
			ModInfo modInfo = Jankson.builder().build().fromJson((JsonObject) elem, CurseModInfo.class).toModInfo();
			return modInfo;
		} else {
			throw new IOException("Server response was valid json but not a JsonObject (was a "+elem.getClass().getSimpleName()+").");
		}
	}
}
