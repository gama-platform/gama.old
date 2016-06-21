package ummisco.gama.ui.modeling.editbox;

import java.util.Set;


public interface IBoxSettingsStore {

	void setProviderId(String id);

	void loadDefaults(IBoxSettings editorsSettings);
	void load(String name, IBoxSettings editorsSettings);

	void saveDefaults(IBoxSettings settings);

	public Set<String> getCatalog();

	void remove(String name);
}
