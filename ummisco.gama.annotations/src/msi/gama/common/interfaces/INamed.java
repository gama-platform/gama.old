/*********************************************************************************************
 * 
 * 
 * 'INamed.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.common.interfaces;

/**
 * Written by drogoul Modified on 10 nov. 2009
 * 
 * @todo Description
 */
public interface INamed extends IGamlable {

	public static java.util.Comparator<? super INamed> COMPARATOR = new java.util.Comparator<INamed>() {

		@Override
		public int compare(final INamed a, final INamed b) {
			return a.getName().compareToIgnoreCase(b.getName());
		}

	};

	public String getName();

	public void setName(String newName);

}
