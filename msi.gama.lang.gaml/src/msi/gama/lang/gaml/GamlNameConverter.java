package msi.gama.lang.gaml;

import org.eclipse.xtext.naming.IQualifiedNameConverter.DefaultImpl;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.util.Strings;

public class GamlNameConverter extends DefaultImpl {

	@Override
	public String toString(final QualifiedName qualifiedName) {
		try {
			return super.toString(qualifiedName);
		} catch (IllegalArgumentException e) {
			return "";
		}

	}

	@Override
	public QualifiedName toQualifiedName(final String qualifiedNameAsString) {
		try {
			return super.toQualifiedName(qualifiedNameAsString);
		} catch (IllegalArgumentException e) {
			return QualifiedName.create(Strings.EMPTY_ARRAY);
		}
	}

}
