/*********************************************************************************************
 *
 * 'INamed.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

/**
 * Interface INamed. Represents objects that can be provided with a name.
 * 
 * @author A. Drogoul
 * @since 10 nov. 2009
 */
public interface INamed extends IGamlable {

	public static java.util.Comparator<? super INamed> COMPARATOR =
			(a, b) -> a.getName().compareToIgnoreCase(b.getName());

	default String getName() {
		return toString();
	}

	default void setName(final String newName) {}

}
