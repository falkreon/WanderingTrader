package blue.endless.wtrader;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.wtrader.provider.ModProvider;

public class DependencyResolver {
	
	public static Collection<ModInfo.Version> resolve(Collection<ModInfo.Version> mods, String mcversion) throws IOException {
		Deque<DependencyNode> unresolved = new ArrayDeque<>();
		Map<String, DependencyNode> byModId = new HashMap<>();
		List<Conflict> conflicts = new ArrayList<>();
		
		DependencyNode modpack = new DependencyNode();
		modpack.version = new ModInfo.Version();
		modpack.version.modId = "modpack";
		modpack.version.fileName = "modpack";
		modpack.version.number = "";
		modpack.version.timestamp = System.currentTimeMillis();
		
		for(ModInfo.Version mod : mods) {
			DependencyNode node = new DependencyNode();
			node.parent = modpack;
			node.version = mod;
			byModId.put(mod.modId, node);
			unresolved.addLast(node);
		}
		
		long iterations = 0;
		long maxIterations = 9000;
		while(!unresolved.isEmpty()) {
			//Pick one and resolve it
			DependencyNode curParent = unresolved.pop();
			System.out.println("    Resolving dependency: "+Jankson.builder().build().toJson(curParent.version));
			Collection<ModInfo.Version> dependencies = resolve(curParent.version, mcversion);
			for(ModInfo.Version dependency : dependencies) {
				
				DependencyNode existing = byModId.get(dependency.modId);
				if (existing!=null) {
					System.out.println("    Found existing dependency "+dependency.fileName);
					
					if (dependency.equals(existing.version)) {
						curParent.children.add(existing);
					} else {
						DependencyNode created = new DependencyNode();
						created.parent = curParent;
						created.version = dependency;
						Conflict conflict = new Conflict("Version conflict for mod '"+curParent.version.modId+"' ("+existing.version.number+" <-> "+created.version.number+")", existing, created);
						conflicts.add(conflict);
					}
				} else {
					System.out.println("    Found new dependency "+dependency.fileName);
					DependencyNode created = new DependencyNode();
					created.parent = curParent;
					created.version = dependency;
					curParent.children.add(created);
					unresolved.addLast(created);
					byModId.put(created.version.modId, created);
				}
			}
			iterations++;
			if (iterations>maxIterations) throw new IOException("Cannot process this dependency graph; it's circular or more than "+maxIterations+" nodes deep.");
		}
		
		ArrayList<ModInfo.Version> result = new ArrayList<>();
		for(DependencyNode node : byModId.values()) {
			result.add(node.version);
		}
		return result;
	}
	
	
	public static Collection<ModInfo.Version> resolve(ModInfo.Version modVersion, String mcversion) throws IOException {
		List<ModInfo.Version> versionMap = new ArrayList<>();
		
		System.out.println("  Fetching partial map for "+modVersion.fileName);
		
		if (modVersion.dependencies.isEmpty()) {
			System.out.println("    No dependencies.");
		} else {
			for(ModInfo.Dependency dependency : modVersion.dependencies) {
				ModInfo.Version resolved = ModProvider.get(dependency.provider).resolve(dependency, mcversion);
				if (resolved==null) {
					throw new IOException("Could not resolve dependency "+Jankson.builder().build().toJson(resolved).toJson(JsonGrammar.JSON5));
				} else {
					versionMap.add(resolved);
				}
			}
		}
		
		return versionMap;
	}
	
	
	public static class Conflict {
		List<DependencyNode> nodes = new ArrayList<>();
		String message;
		
		public Conflict(String message, DependencyNode... nodes) {
			this.message = message;
			for(DependencyNode node : nodes) this.nodes.add(node);
		}
	}
}
