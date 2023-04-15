/*******************************************************************************************************
 *
 * BoxProviderImpl.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.misc.TextMatcher;

/**
 * The Class BoxProviderImpl.
 */
@SuppressWarnings ({ "rawtypes" })
public class BoxProviderImpl implements IBoxProvider {

	/** The id. */
	protected String id;
	
	/** The name. */
	protected String name;
	
	/** The editors settings. */
	protected IBoxSettings editorsSettings;
	
	/** The settings store. */
	protected BoxSettingsStoreImpl settingsStore;
	
	/** The builders. */
	protected Map<String, Class> builders;
	
	/** The default settings catalog. */
	protected Collection<String> defaultSettingsCatalog;
	
	/** The matchers. */
	private ArrayList<Matcher> matchers;

	@Override
	public BoxSettingsStoreImpl getSettingsStore() {
		if (settingsStore == null) {
			settingsStore = createSettingsStore();
			settingsStore.setProviderId(id);
		}
		return settingsStore;
	}

	@Override
	public IBoxSettings getEditorsBoxSettings() {
		if (editorsSettings == null) {
			editorsSettings = createSettings0();
			getSettingsStore().loadDefaults(editorsSettings);
			editorsSettings.addPropertyChangeListener(event -> {
				final String p = event.getProperty();
				if (p != null && (p.equals(IBoxSettings.PropertiesKeys.FileNames.name())
						|| p.equals(IBoxSettings.PropertiesKeys.ALL.name()))) {
					matchers = null;
				}
			});
		}
		return editorsSettings;
	}

	@Override
	public IBoxDecorator decorate(final IWorkbenchPart editorPart) {
		if (!(editorPart instanceof IBoxEnabledEditor)) { return null; }
		final IBoxSettings settings = getEditorsBoxSettings();
		if (!settings.getEnabled()) { return null; }
		((IBoxEnabledEditor) editorPart).createDecorator();
		return ((IBoxEnabledEditor) editorPart).getDecorator();
	}

	@Override
	public boolean supports(final IWorkbenchPart editorPart) {
		return editorPart.getAdapter(Control.class) instanceof StyledText
				&& (supportsFile(editorPart.getTitle()) || supportsFile(editorPart.getTitleToolTip()));
	}

	/**
	 * Supports file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	protected boolean supportsFile(final String fileName) {
		if (fileName != null) {
			for (final Matcher matcher : getMatchers()) {
				if (matcher.matches(fileName)) { return true; }
			}
		}
		return false;
	}

	/**
	 * Gets the matchers.
	 *
	 * @return the matchers
	 */
	protected Collection<Matcher> getMatchers() {
		if (matchers == null) {
			matchers = new ArrayList<>();
			final Collection<String> fileNames = getEditorsBoxSettings().getFileNames();
			if (fileNames != null) {
				for (final String pattern : fileNames) {
					matchers.add(new Matcher(pattern));
				}
			}
		}
		return matchers;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the id.
	 *
	 * @param newId the new id
	 */
	public void setId(final String newId) {
		id = newId;
	}

	/**
	 * Sets the name.
	 *
	 * @param newName the new name
	 */
	public void setName(final String newName) {
		name = newName;
	}

	/**
	 * Creates the settings store.
	 *
	 * @return the box settings store impl
	 */
	protected BoxSettingsStoreImpl createSettingsStore() {
		final BoxSettingsStoreImpl result = new BoxSettingsStoreImpl();
		result.setDefaultSettingsCatalog(defaultSettingsCatalog);
		return result;
	}

	/**
	 * Sets the default settings catalog.
	 *
	 * @param cat the new default settings catalog
	 */
	public void setDefaultSettingsCatalog(final Collection<String> cat) {
		defaultSettingsCatalog = cat;
	}

	@Override
	public IBoxSettings createSettings() {
		final BoxSettingsImpl result = createSettings0();
		result.copyFrom(getEditorsBoxSettings());
		return result;
	}

	/**
	 * Creates the settings 0.
	 *
	 * @return the box settings impl
	 */
	protected BoxSettingsImpl createSettings0() {
		return new BoxSettingsImpl();
	}

	@Override
	public IBoxDecorator createDecorator() {
		final BoxDecoratorImpl result = new BoxDecoratorImpl();
		result.setProvider(this);
		return result;
	}

	@Override
	public Collection<String> getBuilders() {
		return builders != null ? builders.keySet() : null;
	}

	/**
	 * Sets the builders.
	 *
	 * @param newBuilders the new builders
	 */
	public void setBuilders(final Map<String, Class> newBuilders) {
		builders = newBuilders;
	}

	@Override
	public IBoxBuilder createBoxBuilder(final String name) {
		Class c = null;
		if (name != null && builders != null) {
			c = builders.get(name);
		}
		if (c == null) { return new BoxBuilderImpl(); }
		try {
			return (IBoxBuilder) c.newInstance();
		} catch (final Exception e) {
			// EditBox.logError(this, "Cannot create box builder: " + name, e);
		}
		return null;
	}

	/**
	 * The Class Matcher.
	 */
	class Matcher {

		/** The m. */
		TextMatcher m;

		/**
		 * Instantiates a new matcher.
		 *
		 * @param pattern the pattern
		 */
		Matcher(final String pattern) {
			m = new TextMatcher(pattern, true, false);
		}

		/**
		 * Matches.
		 *
		 * @param text the text
		 * @return true, if successful
		 */
		boolean matches(final String text) {
			return m.match(text);
		}
	}
}
