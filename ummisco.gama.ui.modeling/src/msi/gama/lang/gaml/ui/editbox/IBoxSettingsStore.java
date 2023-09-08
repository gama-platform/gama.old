/*******************************************************************************************************
 *
 * IBoxSettingsStore.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.Set;


/**
 * The Interface IBoxSettingsStore.
 */
public interface IBoxSettingsStore {

	/**
	 * Sets the provider id.
	 *
	 * @param id the new provider id
	 */
	void setProviderId(String id);

	/**
	 * Load defaults.
	 *
	 * @param editorsSettings the editors settings
	 */
	void loadDefaults(IBoxSettings editorsSettings);
	
	/**
	 * Load.
	 *
	 * @param name the name
	 * @param editorsSettings the editors settings
	 */
	void load(String name, IBoxSettings editorsSettings);

	/**
	 * Save defaults.
	 *
	 * @param settings the settings
	 */
	void saveDefaults(IBoxSettings settings);

	/**
	 * Gets the catalog.
	 *
	 * @return the catalog
	 */
	public Set<String> getCatalog();

	/**
	 * Removes the.
	 *
	 * @param name the name
	 */
	void remove(String name);
}
