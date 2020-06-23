/*********************************************************************************************
 *
 * 'IBoxSettingsStore.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.Set;


public interface IBoxSettingsStore {

	void setProviderId(String id);

	void loadDefaults(IBoxSettings editorsSettings);
	void load(String name, IBoxSettings editorsSettings);

	void saveDefaults(IBoxSettings settings);

	public Set<String> getCatalog();

	void remove(String name);
}
