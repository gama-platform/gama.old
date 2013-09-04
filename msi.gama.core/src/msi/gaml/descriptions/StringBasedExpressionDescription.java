/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
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
	public Set<String> getStrings(final IDescription context, final boolean skills) {
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

	@Override
	public IExpressionDescription cleanCopy() {
		return new StringBasedExpressionDescription(string);
	}

	public static IExpressionDescription create(final String string) {
		if ( string == null ) { return null; }
		String s = string.trim();
		if ( s.equals(IKeyword.NULL) ) { return ConstantExpressionDescription.create((Object) null); }
		if ( s.equals(IKeyword.FALSE) ) { return ConstantExpressionDescription.create(false); }
		if ( s.equals(IKeyword.TRUE) ) { return ConstantExpressionDescription.create(true); }
		if ( StringUtils.isGamaString(s) ) { return LabelExpressionDescription.create(StringUtils.toJavaString(s)); }
		return new StringBasedExpressionDescription(string);
	}

}
