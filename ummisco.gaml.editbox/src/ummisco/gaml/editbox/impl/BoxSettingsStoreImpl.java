package ummisco.gaml.editbox.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;

import ummisco.gaml.editbox.*;


public class BoxSettingsStoreImpl implements IBoxSettingsStore {

	private static final String FILE_NAMES = "fileNames";
	private static final String TXT_POSTFIX = "$txt";
	private static final String DEFAULT = "default";
	private static final String ENABLED = "enabled";
	protected String providerId;
	protected IPreferenceStore store;
	private Set<String> catalog;
	private Collection<String> defaultCatalog;

	protected IPreferenceStore getStore(){
		if (store == null)
			store = EditBox.getDefault().getPreferenceStore();
		return store;
	}
	
	protected String key(String postfix){
		return providerId+"_"+postfix;
	}
	
	public void setProviderId(String id) {
		this.providerId = id;
	}
	
	public void loadDefaults(IBoxSettings editorsSettings) {
		String defaultName = getStore().getString(key(DEFAULT));
		if (isEmpty(defaultName)) defaultName = providerId;
		load(defaultName,editorsSettings);
	}

	public void load(String name, IBoxSettings editorsSettings) {
		String value = getStore().getString(key(name));
		if (!isEmpty(value))
			editorsSettings.load(value);
		else
			try {
				editorsSettings.load(getClass().getResourceAsStream("/"+name + ".eb"));
			} catch (Exception e) {
				EditBox.logError(this, "Error loading settings: "+name, e);
			}
		editorsSettings.setEnabled(getIsEnabled());
		editorsSettings.setFileNames(getFileNames());
	}

	protected boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	protected boolean getIsEnabled() {
		String key = key(ENABLED);
		if (getStore().contains(key))
			return getStore().getBoolean(key);
		return true;
	}

	public void saveDefaults(IBoxSettings settings) {
		getStore().setValue(key(ENABLED), settings.getEnabled()?"true":"false");
		getStore().setValue(key(DEFAULT),settings.getName());
		store(settings);
	}

	public void store(IBoxSettings settings) {
		String name = settings.getName();
		getStore().setValue(key(name),settings.export());
		setFileNames(settings.getFileNames());
		addToCatalog(name);
		EditBox.getDefault().savePluginPreferences();
	}

	protected void addToCatalog(String name) {
		Set<String> cat = getCatalog();
		if (!cat.contains(name)){
			cat.add(name);
			storeCatalog(cat);
		}
	}

	private void storeCatalog(Set<String> cat) {
		StringBuilder sb = new StringBuilder();
		for(String c: cat){
			if (sb.length()>0) sb.append(",");
			sb.append(c);
		}
		getStore().setValue(key("catalog"), sb.toString());
	}

	public Set<String> getCatalog() {
		if (catalog == null){
			catalog = new LinkedHashSet<String>();
			String cstr = getStore().getString(key("catalog"));
			if (!isEmpty(cstr))
				for (String s : cstr.split(",")) 
					catalog.add(s);
				
		}
		if (defaultCatalog != null && catalog !=null)
			catalog.addAll(defaultCatalog);
		return catalog;
	}
	
	public void setDefaultSettingsCatalog(Collection<String> cat){
		defaultCatalog = cat;
	}
	
	public void remove(String name) {
		if (getCatalog().remove(name))
			storeCatalog(getCatalog());
		getStore().setValue(key(name), "");
		getStore().setValue(key(name+TXT_POSTFIX), "");
		EditBox.getDefault().savePluginPreferences();
	}

	protected void setFileNames(Collection<String> fileNames) {
		StringBuilder sb = new StringBuilder();
		if (fileNames != null) {
			boolean first = true;
			for (String s : fileNames) {
				if (!first)
					sb.append(",");
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
		String key = key(FILE_NAMES);

		if (!getStore().contains(key))
			return null;

		String value = getStore().getString(key);
		List<String> l = new ArrayList<String>();
		if (value != null) {
			StringTokenizer st = new StringTokenizer(value, ",");
			while (st.hasMoreTokens()) {
				String t = st.nextToken().trim();
				if (t.length() > 0)
					l.add(t);
			}
		}

		return l;
	}

}
