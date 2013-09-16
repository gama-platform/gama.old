package msi.gaml.compilation;

/**
 * 
 * The class GamlElementDocumentation. Represents the Java view of annotation @doc
 * 
 * @author drogoul
 * @since 3 avr. 2013
 * 
 */
public class GamlElementDocumentation {

	public final static String NO = "Not documented yet";
	private String main;
	private String deprecated;
	private String returns;
	private String comment;
	String[] special_cases;
	String[] examples;
	String[] see;

	static boolean hasWarnedOnce = false;

	public GamlElementDocumentation(final String[] array) {
		if ( !(array == null || array.length == 0) ) {
			try {
				// See msi.gama.precompiler.GamaProcessor#docToString() method
				int index = 0;
				main = array[index++];
				deprecated = array[index++];
				setReturns(array[index++]);
				comment = array[index++];
				int number = Integer.decode(array[index++]);
				special_cases = new String[number];
				for ( int i = 0; i < number; i++ ) {
					special_cases[i] = array[index++];
				}
				number = Integer.decode(array[index++]);
				examples = new String[number];
				for ( int i = 0; i < number; i++ ) {
					examples[i] = array[index++];
				}
				number = Integer.decode(array[index++]);
				see = new String[number];
				for ( int i = 0; i < number; i++ ) {
					see[i] = array[index++];
				}
			} catch (final Exception e) {
				if ( !hasWarnedOnce ) {
					hasWarnedOnce = true;
					System.err.println("***");
					System.err.println("***");
					System.err.println("***");
					System.err
						.println("*** WARNING :: The annotations are not in sync with the code. Clean the projects and relaunch GAMA.");
					System.err.println("***");
					System.err.println("***");
					System.err.println("***");
				}
			}
		}
	}

	String getReturns() {
		return returns;
	}

	void setReturns(final String returns) {
		this.returns = returns == null ? "" : returns;
	}

	public String getMain() {
		return main;
	}

	public void setMain(final String main) {
		this.main = main == null ? NO : main;
	}

	public String getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(final String deprecated) {
		this.deprecated = deprecated == null ? "" : deprecated;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(final String comment) {
		this.comment = comment == null ? "" : comment;
	}

	public String[] getSpecial_cases() {
		return special_cases;
	}

	public void setSpecial_cases(final String[] special_cases) {
		this.special_cases = special_cases;
	}

	public String[] getExamples() {
		return examples;
	}

	public void setExamples(final String[] examples) {
		this.examples = examples;
	}

	public String[] getSee() {
		return see;
	}

	public void setSee(final String[] see) {
		this.see = see;
	}

}
