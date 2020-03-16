package blue.endless.wtrader.provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import blue.endless.wtrader.ModInfo;

public abstract class ModProvider {
	private static Map<String, ModProvider> providers = new HashMap<>();
	
	public static ModProvider get(String id) {
		return providers.get(id);
	}
	
	public static void register(String id, ModProvider provider) {
		if (providers.containsKey(id)) {
			throw new IllegalArgumentException("Provider '"+id+"' is already registered!");
		}
		providers.put(id, provider);
	}
	
	/**
	 * If this provider has search functionality, return a list of partial ModInfo objects.
	 * Each ModInfo may need further resolution to be a proper cache entry, but must at a minimum contain
	 * enough information that the latest version can be fetched immediately with no lookups.
	 * 
	 * <p>This operation WILL block on the search, so usually one will want to execute this method
	 * from its own thread.
	 * 
	 * <p>This is an optional operation, and providers which do not support search MAY return an
	 * empty list immediately.
	 * 
	 * @param keyword a keyword to search for, such as the mod's name
	 * @return a list of [potentially partial] ModInfo objects which match the keyword in some way
	 */
	@Nonnull
	public abstract List<ModInfo> search(@Nonnull String keyword);
	
	/**
	 * Performs a query on a full or partial ModInfo, producing completed or updated information.
	 * 
	 * @param mod The ModInfo to update.
	 * @throws IOException if a network error occurs or the resource is unavailable
	 */
	public ModInfo update(@Nonnull ModInfo mod) throws IOException {
		return fetch(mod.providerId);
	}
	
	@Nonnull
	public abstract ModInfo fetch(String providerId) throws IOException;
}