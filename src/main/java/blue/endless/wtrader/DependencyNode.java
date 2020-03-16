package blue.endless.wtrader;

import java.util.ArrayList;
import java.util.List;

/**
 * This class describes an ephemeral node for dependency resolution. Parents are mods that depend on
 * this mod; children are mods that this mod depends on.
 * 
 * <p>DependencyNodes are employed in the "unflattening" process, where new mods are discovered, unpacked,
 * and added to the list as automatic items.
 */
public class DependencyNode {
	public DependencyNode parent;
	ModInfo.Version version;
	public List<DependencyNode> children = new ArrayList<>();
}
