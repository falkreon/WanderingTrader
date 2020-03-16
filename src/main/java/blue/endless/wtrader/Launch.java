package blue.endless.wtrader;

import blue.endless.wtrader.provider.cache.CacheModProvider;
import blue.endless.wtrader.provider.curse.CurseModProvider;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class Launch {
	public static void main(String[] args) {
		ModProvider.register("cache", CacheModProvider.instance());
		ModProvider.register("curse", CurseModProvider.instance());
		
		OptionParser optionParser = new OptionParser();
		optionParser.accepts("gui");
		
		OptionSet options = optionParser.parse(args);
		
		if (options.has("gui")) {
			TraderGui gui = new TraderGui();
			gui.setVisible(true);
		} else {
			System.out.println("Running in console / headless mode. Use --gui to enable graphical mode.");
		}
	}
}

/*
API information from Gaz492 (Gaz492/TwitchAPI on github)
examples mine
 
Base API Address:
	https://addons-ecs.forgesvc.net/api/v2/
	
	
Addon lookup by id
	addon/
	
	(example: https://addons-ecs.forgesvc.net/api/v2/addon/222880 for Thermal Foundation)
	
	can also be a POST operation carrying a json array of addon IDs, which will return an array of mod json objects
	
Addon lookup by anything else
	addon/search
	
	as in
	https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId={categoryID}&gameId={gameId}&gameVersion={gameVersion}&index={index}&pageSize={pageSize}5&searchFilter={searchFilter}&sectionId={sectionId}&sort={sort}
	
	GameID 432 is Minecraft, you should be able to get away with just specifying that and the modname as searchFilter for a mod name lookup
	
	for example, at the time of this writing:
			https://addons-ecs.forgesvc.net/api/v2/addon/search?gameId=432&searchFilter=libgui
	Correctly brings up the project with modId 326869 along with its description, authors, and latest release file IDs
*/