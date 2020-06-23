/*********************************************************************************************
 *
 * 'BoxSettingsStoreImpl.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

import ummisco.gama.ui.modeling.internal.ModelingActivator;

public class BoxSettingsStoreImpl implements IBoxSettingsStore {

	private static final String FILE_NAMES = "fileNames";
	private static final String TXT_POSTFIX = "$txt";
	private static final String DEFAULT = "default";
	private static final String ENABLED = "enabled";
	protected String providerId;
	protected IPreferenceStore store;
	private Set<String> catalog;
	private Collection<String> defaultCatalog;

	protected IPreferenceStore getStore() {
		if (store == null) {
			store = ModelingActivator.getInstance().getPreferenceStore();
		}
		return store;
	}

	protected String key(final String postfix) {
		return providerId + "_" + postfix;
	}

	@Override
	public void setProviderId(final String id) {
		this.providerId = id;
	}

	@Override
	public void loadDefaults(final IBoxSettings editorsSettings) {
		String defaultName = getStore().getString(key(DEFAULT));
		if (isEmpty(defaultName)) {
			defaultName = providerId;
		}
		load(defaultName, editorsSettings);
	}

	@Override
	public void load(final String name, final IBoxSettings editorsSettings) {
		final String value = getStore().getString(key(name));
		if (!isEmpty(value)) {
			editorsSettings.load(value);
		} else {
			try {
				editorsSettings.load(getClass().getResourceAsStream("/" + name + ".eb"));
			} catch (final Exception e) {
				// EditBox.logError(this, "Error loading settings: " + name, e);
			}
		}
		editorsSettings.setEnabled(getIsEnabled());
		editorsSettings.setFileNames(getFileNames());
	}

	protected boolean isEmpty(final String s) {
		return s == null || s.length() == 0;
	}

	protected boolean getIsEnabled() {
		final String key = key(ENABLED);
		if (getStore().contains(key)) { return getStore().getBoolean(key); }
		return true;
	}

	@Override
	public void saveDefaults(final IBoxSettings settings) {
		getStore().setValue(key(ENABLED), settings.getEnabled() ? "true" : "false");
		getStore().setValue(key(DEFAULT), settings.getName());
		store(settings);
	}

	@SuppressWarnings ("deprecation")
	public void store(final IBoxSettings settings) {
		final String name = settings.getName();
		getStore().setValue(key(name), settings.export());
		setFileNames(settings.getFileNames());
		addToCatalog(name);
		ModelingActivator.getInstance().savePluginPreferences();
	}

	protected void addToCatalog(final String name) {
		final Set<String> cat = getCatalog();
		if (!cat.contains(name)) {
			cat.add(name);
			storeCatalog(cat);
		}
	}

	private void storeCatalog(final Set<String> cat) {
		final StringBuilder sb = new StringBuilder();
		for (final String c : cat) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(c);
		}
		getStore().setValue(key("catalog"), sb.toString());
	}

	@Override
	public Set<String> getCatalog() {
		if (catalog == null) {
			catalog = new LinkedHashSet<>();
			final String cstr = getStore().getString(key("catalog"));
			if (!isEmpty(cstr)) {
				for (final String s : cstr.split(",")) {
					catalog.add(s);
				}
			}

		}
		if (defaultCatalog != null && catalog != null) {
			catalog.addAll(defaultCatalog);
		}
		return catalog;
	}

	public void setDefaultSettingsCatalog(final Collection<String> cat) {
		defaultCatalog = cat;
	}

	@SuppressWarnings ("deprecation")
	@Override
	public void remove(final String name) {
		if (getCatalog().remove(name)) {
			storeCatalog(getCatalog());
		}
		getStore().setValue(key(name), "");
		getStore().setValue(key(name + TXT_POSTFIX), "");
		ModelingActivator.getInstance().savePluginPreferences();
	}

	protected void setFileNames(final Collection<String> fileNames) {
		final StringBuilder sb = new StringBuilder();
		if (fileNames != null) {
			boolean first = true;
			for (final String s : fileNames) {
				if (!first) {
					sb.append(",");
				}
				sb.append(s);
				first = false;
			}
		}
		getStore().setValue(key(FILE_NAMES), sb.toString());
	}

	/*
	 * @return null if settings never stored before
	 */
	protected Collection<String> getFileNames() {
		final String key = key(FILE_NAMES);

		if (!getStore().contains(key)) { return null; }

		final String value = getStore().getString(key);
		final List<String> l = new ArrayList<>();
		if (value != null) {
			final StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				final String t = st.nextToken().trim();
				if (t.length() > 0) {
					l.add(t);
				}
			}
		}

		return l;
	}

}
