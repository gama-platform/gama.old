/*******************************************************************************************************
 *
 * BoxProviderRegistry.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ummisco.gama.ui.modeling.internal.ModelingActivator;

/**
 * The Class BoxProviderRegistry.
 */
@SuppressWarnings({ "rawtypes" })
public class BoxProviderRegistry {

	/** The Constant PROIVDERS. */
	private static final String PROIVDERS = "proivders";
	
	/** The Constant PROVIDER_ID_. */
	private static final String PROVIDER_ID_ = "ummisco.gaml.editbox.provider.";

	/** The providers. */
	protected Collection<IBoxProvider> providers;

	/** The instance. */
	private static BoxProviderRegistry INSTANCE;

	/**
	 * Gets the single instance of BoxProviderRegistry.
	 *
	 * @return single instance of BoxProviderRegistry
	 */
	public static BoxProviderRegistry getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BoxProviderRegistry();
		return INSTANCE;
	}

	/**
	 * Gets the gaml provider.
	 *
	 * @return the gaml provider
	 */
	public IBoxProvider getGamlProvider() {
		return getInstance().providerForName("GAML");
	}

	/**
	 * Gets the box providers.
	 *
	 * @return the box providers
	 */
	public Collection<IBoxProvider> getBoxProviders() {
		if (providers == null) {
			providers = loadProviders();
		}
		if (providers == null) {
			providers = defaultProviders();
		}
		return providers;
	}

	/**
	 * Load providers.
	 *
	 * @return the collection
	 */
	protected Collection<IBoxProvider> loadProviders() {
		List<IBoxProvider> result = null;
		final String pSetting = ModelingActivator.getInstance().getPreferenceStore().getString(PROIVDERS);
		if (pSetting != null && pSetting.length() > 0) {
			final String[] split = pSetting.split(",");
			if (split.length > 0) {
				result = new ArrayList<IBoxProvider>();

				for (final String s : split) {
					if (s.trim().length() > 0) {
						result.add(createProvider(s.trim()));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Sets the providers.
	 *
	 * @param newProviders the new providers
	 */
	public void setProviders(final Collection<IBoxProvider> newProviders) {
		providers = newProviders;
	}

	/**
	 * Store providers.
	 */
	public void storeProviders() {
		if (providers != null) {
			final StringBuilder sb = new StringBuilder();
			for (final IBoxProvider p : providers) {
				if (sb.length() != 0) {
					sb.append(",");
				}
				sb.append(p.getName());
			}
			ModelingActivator.getInstance().getPreferenceStore().setValue(PROIVDERS, sb.toString());
		}
	}

	/**
	 * Default providers.
	 *
	 * @return the collection
	 */
	protected Collection<IBoxProvider> defaultProviders() {
		final List<IBoxProvider> result = new ArrayList<IBoxProvider>();
		// order important (see supports())
		result.add(gamlProvider());
		result.add(textProvider());
		return result;
	}

	/**
	 * Creates the provider.
	 *
	 * @param name the name
	 * @return the box provider impl
	 */
	protected BoxProviderImpl createProvider(final String name) {
		final BoxProviderImpl provider = new BoxProviderImpl();
		provider.setId(PROVIDER_ID_ + name);
		provider.setName(name);
		provider.setBuilders(defaultBuilders());
		provider.setDefaultSettingsCatalog(Arrays.asList("Default"));
		return provider;
	}

	/**
	 * Gaml provider.
	 *
	 * @return the box provider impl
	 */
	protected BoxProviderImpl gamlProvider() {
		final BoxProviderImpl provider = createProvider("GAML");
		provider.setDefaultSettingsCatalog(Arrays.asList("GAML", "Default", "OnClick", "GreyGradient", "Classic"));
		if (provider.getEditorsBoxSettings().getFileNames() == null) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.gaml"));
		}
		return provider;
	}

	/**
	 * Text provider.
	 *
	 * @return the box provider impl
	 */
	protected BoxProviderImpl textProvider() {
		final BoxProviderImpl provider = createProvider("Text");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
		if (provider.getEditorsBoxSettings().getFileNames() == null) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.txt", "*.*"));
		}
		return provider;
	}

	/**
	 * Default builders.
	 *
	 * @return the map
	 */
	protected Map<String, Class> defaultBuilders() {
		final Map<String, Class> result = new HashMap<String, Class>();
		result.put("Text", BoxBuilderImpl.class);
		result.put("GAML", JavaBoxBuilder.class);
		// result.put("Markup", MarkupBuilder2.class);
		result.put("Text2", TextBoxBuilder.class);
		return result;
	}

	/**
	 * Provider for name.
	 *
	 * @param name the name
	 * @return the i box provider
	 */
	public IBoxProvider providerForName(final String name) {
		final Collection<IBoxProvider> providers = getBoxProviders();
		for (final IBoxProvider provider : providers) {
			if (provider.getName().equals(name)) {
				return provider;
			}
		}
		final IBoxProvider provider = createProvider(name);
		providers.add(provider);
		return provider;
	}

	/**
	 * Removes the provider.
	 *
	 * @param name the name
	 */
	public void removeProvider(final String name) {
		for (final Iterator<IBoxProvider> it = getBoxProviders().iterator(); it.hasNext();) {
			if (it.next().getName().equals(name)) {
				it.remove();
			}
		}
	}

}
