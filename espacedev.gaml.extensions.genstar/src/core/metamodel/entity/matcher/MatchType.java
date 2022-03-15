package core.metamodel.entity.matcher;

/**
 * Type of match to be expected
 * 
 * @author kevinchapuis
 *
 */
public enum MatchType {
	
	ALL, ANY, NONE;

	public static MatchType getDefault() {
		return ALL;
	}
}
