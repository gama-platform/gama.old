/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

/**
 * The class StringBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class StringBasedExpressionDescription extends BasicExpressionDescription {

	String string;

	public StringBasedExpressionDescription(final String s) {
		super(null);
		string = s;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return new LabelExpressionDescription(string);
	}

}
