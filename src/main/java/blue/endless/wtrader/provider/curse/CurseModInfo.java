package blue.endless.wtrader.provider.curse;

import java.util.ArrayList;
import java.util.List;

import blue.endless.wtrader.ModInfo;

public class CurseModInfo {
	public String id;
	public String name;
	/** Description of the mod */
	public String summary;
	public List<Attribution> authors = new ArrayList<>();
	public List<Attachment> attachments = new ArrayList<>();
	public String slug;
	
	/**
	 * Attributions in Curse act like a many-to-many table: Each record has foreign keys to a user and a project, as well
	 * as a little bit of data either cached or captured by the query that generates these objects.
	 */
	public static class Attribution {
		/** FOREIGN KEY into projects table */
		public String projectId;
		/** FOREGIN KEY into curse-users table */
		public String userId;
		/** FOREGIN KEY into twitch-users table */
		public String twitchId;
		
		
		/** Curse display name of the user */
		public String name;
		/** Profile url of the user */
		public String url;
		/** Unknown. May be an autoincrement primary key in the relationships table, as it's often large sequential numbers within a project. */
		public String id;
		/** Probably a numeric key into an enum for project roles */
		public String projectTitleId;
		/** The human-readable project role for this relationship, e.g. "Author", "Artist" */
		public String projectTitleTitle;
	}
	
	/**
	 * Currently curse uses this for image attachments.
	 */
	public static class Attachment {
		/** PRIMARY KEY in the attachments table probably */
		public String id;
		/** Will be the same as the project it's contained in - this is just cruft that comes from turning a SQL query into a json object */
		public String projectId;
		public String description;
		/** True if this image is displayed on the project page */
		public boolean isDefault;
		public String thumbnailUrl;
		public String title;
		public String url;
		/** This seems to be numeric but let's not take any chances. */
		public String status;
	}
	
	public ModInfo toModInfo() {
		ModInfo result = new ModInfo();
		
		result.id = slug;
		result.name = name;
		result.provider = "curse";
		StringBuilder resultAuthors = new StringBuilder();
		for(Attribution attribution : authors) {
			if (resultAuthors.length()>0) {
				resultAuthors.append(", ");
			}
			
			resultAuthors.append(attribution.name);
		}
		result.authors = resultAuthors.toString();
		result.fetchUrl = "https://addons-ecs.forgesvc.net/api/v2/addon/"+id;
		result.description = summary;
		
		return result;
	}
}
