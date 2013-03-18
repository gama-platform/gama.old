/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import org.eclipse.emf.ecore.EObject;

/**
 * The class StringBasedExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class StringBasedExpressionDescription extends BasicExpressionDescription {

	String string;

	private StringBasedExpressionDescription(final String s) {
		super((EObject) null);
		string = s;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return LabelExpressionDescription.create(string);
	}

	@Override
	public Set<String> getStrings(IDescription context, boolean skills) {
		// Assuming of the form [aaa, bbb]
		Set<String> result = new HashSet();
		StringBuilder b = new StringBuilder();
		for ( char c : string.toCharArray() ) {
			switch (c) {
				case '[':
				case ' ':
					break;
				case ']':
				case ',': {
					result.add(b.toString());
					b.setLength(0);
					break;
				}
				default:
					b.append(c);
			}
		}
		return result;
	}

	public static IExpressionDescription create(String string2) {
		if ( string2 == null ) { return null; }
		if ( string2.equals(IKeyword.NULL) ) { return new ConstantExpressionDescription(null); }
		if ( string2.equals(IKeyword.FALSE) ) { return new ConstantExpressionDescription(false); }
		if ( string2.equals(IKeyword.TRUE) ) { return new ConstantExpressionDescription(true); }
		// TODO Numbers ?
		return new StringBasedExpressionDescription(string2);
	}

}
