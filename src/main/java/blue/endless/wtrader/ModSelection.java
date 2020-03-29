package blue.endless.wtrader;

import java.time.Instant;
import java.util.regex.Pattern;

public class ModSelection {
	public String modCacheId;
	public String version;
	public Constraint constraint;
	public long timestamp;
	public ModInfo info;
	public transient ModInfo.Version cachedVersion;
	
	public static ModSelection fromModpackLine(String s) {
		ModSelection selection = new ModSelection();
		String[] parts = s.split(Pattern.quote(" "));
		if (parts.length==0) throw new IllegalArgumentException("Cannot parse an empty modpack line.");
		selection.modCacheId = parts[0];
		if (parts.length>1) {
			for(int i=1; i<parts.length; i++) {
				String part = parts[i];
				
				//recognize some important prefixes
				if (part.startsWith("<")) {
					part = part.substring(1);
					selection.applyConstraint(Constraint.LESS_THAN);
				} else if (part.startsWith("<=")) {
					part = part.substring(2);
					selection.applyConstraint(Constraint.LESS_THAN_OR_EQUAL);
				} else if (part.startsWith("=")) {
					parts[1] = part.substring(1);
					selection.applyConstraint(Constraint.EQUAL);
				} else if (part.startsWith(">=")) {
					part = part.substring(2);
					selection.applyConstraint(Constraint.GREATER_THAN_OR_EQUAL);
				} else if (part.startsWith(">")) {
					part = part.substring(1);
					selection.applyConstraint(Constraint.GREATER_THAN);
				}
				
				if (part.startsWith("@")) {
					part = part.substring(1);
					selection.applyTimestamp(part);
				} else {
					if (!part.isEmpty() && selection.version==null) {
						selection.version = part;
					}
				}
			}
		}
		
		return selection;
	}
	
	private void clearConstraint() {
		this.constraint = null;
	}
	
	/**
	 * Applies a Constraint (>, >=, =, <=, <) to this ModSelection. Only the first constraint applied
	 * will have any effect. If you want to apply it anyway, call {@link #clearConstraint()} first.
	 */
	private void applyConstraint(Constraint constraint) {
		if (this.constraint==null) {
			this.constraint = constraint;
		} else {
			//Do nothing
		}
	}
	
	private void applyTimestamp(String timestamp) {
		if (this.timestamp==0) {
			try {
				this.timestamp = Instant.parse(timestamp).toEpochMilli();
			} catch (Exception exception) {}
		}
	}
	
	
	public static enum Constraint {
		LESS_THAN,
		LESS_THAN_OR_EQUAL,
		EQUAL,
		GREATER_THAN_OR_EQUAL,
		GREATER_THAN;
	}
}
