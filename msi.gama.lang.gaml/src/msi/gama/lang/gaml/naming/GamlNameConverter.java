/*********************************************************************************************
 * 
 *
 * 'GamlNameConverter.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.naming;

import org.eclipse.xtext.naming.IQualifiedNameConverter.DefaultImpl;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

@Singleton
public class GamlNameConverter extends DefaultImpl {

	@Override
	public String toString(final QualifiedName qualifiedName) {
		if (qualifiedName == null) {
			return "";
		}
		return qualifiedName.getFirstSegment();
	}

	@Override
	public QualifiedName toQualifiedName(final String qualifiedNameAsString) {
		if (qualifiedNameAsString == null) {
			return QualifiedName.EMPTY;
		}
		if (qualifiedNameAsString.equals("")) {
			return QualifiedName.EMPTY;
		}
		return QualifiedName.create(qualifiedNameAsString);
	}

}
