package blue.endless.wtrader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModProvider {
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
	public List<ModInfo> search(@Nonnull String keyword);
	
	/**
	 * Download the artifact for a specific version of a mod which is available through this provider.
	 * @param mod The mod to download
	 * @param version The version to download, or null for the latest version - alpha or otherwise.
	 * @throws IOException if a network error occurs or the resource is unavailable
	 * @throws IllegalArgumentException if the mod is not supplied by this provider
	 * @return An InputStream of the jar artifact.
	 */
	@Nonnull
	public InputStream download(@Nonnull ModInfo mod, @Nullable ModInfo.Version version) throws IOException, IllegalArgumentException;
	
	/**
	 * Performs a query on a full or partial ModInfo, producing completed or updated information.
	 * If the information hasn't changed, the provider MAY omit it from the returned list. If the
	 * query indicates that changes happened or more complete information is available than was
	 * provided, it MUST be included in the returned list.
	 * 
	 * @param mods The ModInfo(s) to update. The provider SHOULD batch queries if it is able to.
	 * @throws IOException if a network error occurs or the resources are unavailable
	 * @throws IllegalArgumentException if the mods are not supplied by this provider
	 * @return A list of ModInfos representing updated information.
	 */
	@Nonnull
	public List<ModInfo> update(@Nonnull List<ModInfo> mods) throws IOException, IllegalArgumentException;
}
