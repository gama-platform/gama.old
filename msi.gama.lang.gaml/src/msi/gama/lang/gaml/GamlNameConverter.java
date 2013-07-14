package msi.gama.lang.gaml;

import java.util.regex.Pattern;
import org.eclipse.xtext.naming.IQualifiedNameConverter.DefaultImpl;
import org.eclipse.xtext.naming.*;

public class GamlNameConverter extends DefaultImpl {

	final static StringBuilder builder = new StringBuilder();

	@Override
	public String toString(final QualifiedName qualifiedName) {
		if ( qualifiedName == null ) { return ""; }
		//
		// if ( qualifiedName.getSegmentCount() == 1 ) { return qualifiedName.getFirstSegment(); }
		// builder.setLength(0);
		// boolean isFirst = true;
		// for ( String segment : qualifiedName.getSegments() ) {
		// if ( !isFirst ) {
		// builder.append(delimiter);
		// }
		// isFirst = false;
		// builder.append(segment);
		// }
		// return builder.toString();
		return super.toString(qualifiedName);
	}

	final static String delimiter = ".";
	final static String splitter = Pattern.quote(".");

	@Override
	public QualifiedName toQualifiedName(final String qualifiedNameAsString) {
		// return QualifiedName.create(qualifiedNameAsString);
		// if ( qualifiedNameAsString == null ) { return QualifiedName.EMPTY; };
		if ( qualifiedNameAsString == null ) { return QualifiedName.EMPTY; }
		if ( qualifiedNameAsString.equals("") ) { return QualifiedName.EMPTY; }
		return super.toQualifiedName(qualifiedNameAsString);
		// return QualifiedName.create(qualifiedNameAsString.split(splitter));
	}

}
