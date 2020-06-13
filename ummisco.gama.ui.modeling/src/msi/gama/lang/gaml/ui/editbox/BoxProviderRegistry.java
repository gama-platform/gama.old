/*********************************************************************************************
 *
 * 'BoxProviderRegistry.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ummisco.gama.ui.modeling.internal.ModelingActivator;

@SuppressWarnings({ "rawtypes" })
public class BoxProviderRegistry {

	private static final String PROIVDERS = "proivders";
	private static final String PROVIDER_ID_ = "ummisco.gaml.editbox.provider.";

	protected Collection<IBoxProvider> providers;

	private static BoxProviderRegistry INSTANCE;

	public static BoxProviderRegistry getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BoxProviderRegistry();
		return INSTANCE;
	}

	public IBoxProvider getGamlProvider() {
		return getInstance().providerForName("GAML");
	}

	public Collection<IBoxProvider> getBoxProviders() {
		if (providers == null) {
			providers = loadProviders();
		}
		if (providers == null) {
			providers = defaultProviders();
		}
		return providers;
	}

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

	public void setProviders(final Collection<IBoxProvider> newProviders) {
		providers = newProviders;
	}

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

	protected Collection<IBoxProvider> defaultProviders() {
		final List<IBoxProvider> result = new ArrayList<IBoxProvider>();
		// order important (see supports())
		result.add(gamlProvider());
		result.add(textProvider());
		return result;
	}

	protected BoxProviderImpl createProvider(final String name) {
		final BoxProviderImpl provider = new BoxProviderImpl();
		provider.setId(PROVIDER_ID_ + name);
		provider.setName(name);
		provider.setBuilders(defaultBuilders());
		provider.setDefaultSettingsCatalog(Arrays.asList("Default"));
		return provider;
	}

	protected BoxProviderImpl gamlProvider() {
		final BoxProviderImpl provider = createProvider("GAML");
		provider.setDefaultSettingsCatalog(Arrays.asList("GAML", "Default", "OnClick", "GreyGradient", "Classic"));
		if (provider.getEditorsBoxSettings().getFileNames() == null) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.gaml"));
		}
		return provider;
	}

	protected BoxProviderImpl textProvider() {
		final BoxProviderImpl provider = createProvider("Text");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
		if (provider.getEditorsBoxSettings().getFileNames() == null) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.txt", "*.*"));
		}
		return provider;
	}

	protected Map<String, Class> defaultBuilders() {
		final Map<String, Class> result = new HashMap<String, Class>();
		result.put("Text", BoxBuilderImpl.class);
		result.put("GAML", JavaBoxBuilder.class);
		// result.put("Markup", MarkupBuilder2.class);
		result.put("Text2", TextBoxBuilder.class);
		return result;
	}

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

	public void removeProvider(final String name) {
		for (final Iterator<IBoxProvider> it = getBoxProviders().iterator(); it.hasNext();) {
			if (it.next().getName().equals(name)) {
				it.remove();
			}
		}
	}

}
