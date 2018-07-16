/*********************************************************************************************
 *
 * 'GamlNameConverter.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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
		return qualifiedName == null ? "" : qualifiedName.getFirstSegment();
	}

	@Override
	public QualifiedName toQualifiedName(final String string) {
		return (string == null || string.isEmpty()) ? QualifiedName.EMPTY : QualifiedName.create(string);
	}

}
