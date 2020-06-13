/*********************************************************************************************
 *
 * 'IGamlDescription.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The interface IGamlDescription. Represents objects that can be presented in the online documentation.
 *
 * @author drogoul
 * @since 27 avr. 2012
 *
 */
public interface IGamlDescription extends INamed {

	/**
	 * Returns the title of this object (ie. the first line in the online documentation)
	 *
	 * @return a string representing the title of this object (default is its name)
	 */
	default String getTitle() {
		return getName();
	}

	/**
	 * Returns the documentation attached to this object
	 *
	 * @return a string that represents the documentation of this object
	 */
	default String getDocumentation() {
		return "";
	}

	/**
	 * Returns the plugin in which this object has been defined (if it has one)
	 *
	 * @return a string containing the identifier of the plugin in which this object has been defined, or null
	 */
	default String getDefiningPlugin() {
		// Null by default
		return null;
	}

}
