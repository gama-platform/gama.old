package ummisco.gaml.editbox.impl;

import java.util.*;
import org.eclipse.ui.*;
import ummisco.gaml.editbox.*;

public class BoxProviderRegistry {

	private static final String PROIVDERS = "proivders";
	private static final String PROVIDER_ID_ = "ummisco.gaml.editbox.provider.";

	protected Collection<IBoxProvider> providers;
	protected Map<IWorkbenchPart, IBoxDecorator> decorators;
	protected Map<IPartService, IPartListener2> partListeners;

	public Collection<IBoxProvider> getBoxProviders() {
		if ( providers == null ) {
			providers = loadProviders();
		}
		if ( providers == null ) {
			providers = defaultProviders();
		}
		return providers;
	}

	protected Collection<IBoxProvider> loadProviders() {
		List<IBoxProvider> result = null;
		String pSetting = EditBox.getDefault().getPreferenceStore().getString(PROIVDERS);
		if ( pSetting != null && pSetting.length() > 0 ) {
			String[] split = pSetting.split(",");
			if ( split.length > 0 ) {
				result = new ArrayList<IBoxProvider>();
			}
			for ( String s : split ) {
				if ( s.trim().length() > 0 ) {
					result.add(createProvider(s.trim()));
				}
			}
		}
		return result;
	}

	public void setProvideres(final Collection<IBoxProvider> newProviders) {
		providers = newProviders;
	}

	public void storeProviders() {
		if ( providers != null ) {
			StringBuilder sb = new StringBuilder();
			for ( IBoxProvider p : providers ) {
				if ( sb.length() != 0 ) {
					sb.append(",");
				}
				sb.append(p.getName());
			}
			EditBox.getDefault().getPreferenceStore().setValue(PROIVDERS, sb.toString());
		}
	}

	protected Collection<IBoxProvider> defaultProviders() {
		List<IBoxProvider> result = new ArrayList<IBoxProvider>();
		// order important (see supports())
		result.add(gamlProvider());
		// result.add(pythonProvider());
		// result.add(markupProvider());
		result.add(textProvider());
		return result;
	}

	protected BoxProviderImpl createProvider(final String name) {
		BoxProviderImpl provider = new BoxProviderImpl();
		provider.setId(PROVIDER_ID_ + name);
		provider.setName(name);
		provider.setBuilders(defaultBuilders());
		provider.setDefaultSettingsCatalog(Arrays.asList("Default"));
		return provider;
	}

	// protected BoxProviderImpl markupProvider() {
	// BoxProviderImpl provider = createProvider("Markup");
	// if ( provider.getEditorsBoxSettings().getFileNames() == null ) {
	// provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.*ml", "*.jsp"));
	// }
	// return provider;
	// }

	protected BoxProviderImpl gamlProvider() {
		BoxProviderImpl provider = createProvider("GAML");
		provider.setDefaultSettingsCatalog(Arrays.asList("GAML", "Default", "OnClick", "GreyGradient", "Classic"));
		if ( provider.getEditorsBoxSettings().getFileNames() == null ) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.gaml"));
		}
		return provider;
	}

	//
	// protected BoxProviderImpl pythonProvider() {
	// BoxProviderImpl provider = createProvider("python");
	// provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
	// if ( provider.getEditorsBoxSettings().getFileNames() == null ) {
	// provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.py"));
	// }
	// return provider;
	// }

	protected BoxProviderImpl textProvider() {
		BoxProviderImpl provider = createProvider("Text");
		provider.setDefaultSettingsCatalog(Arrays.asList("Default", "Whitebox"));
		if ( provider.getEditorsBoxSettings().getFileNames() == null ) {
			provider.getEditorsBoxSettings().setFileNames(Arrays.asList("*.txt", "*.*"));
		}
		return provider;
	}

	protected Map<String, Class> defaultBuilders() {
		Map<String, Class> result = new HashMap<String, Class>();
		result.put("Text", BoxBuilderImpl.class);
		result.put("GAML", JavaBoxBuilder.class);
		// result.put("Markup", MarkupBuilder2.class);
		result.put("Text2", TextBoxBuilder.class);
		return result;
	}

	public IBoxDecorator getDecorator(final IWorkbenchPart part) {
		return getDecorators().get(part);
	}

	protected Map<IWorkbenchPart, IBoxDecorator> getDecorators() {
		if ( decorators == null ) {
			decorators = new HashMap<IWorkbenchPart, IBoxDecorator>();
		}
		return decorators;
	}

	public IBoxDecorator removeDecorator(final IWorkbenchPart part) {
		return getDecorators().remove(part);
	}

	public void addDecorator(final IBoxDecorator decorator, final IWorkbenchPart part) {
		getDecorators().put(part, decorator);
	}

	public void releaseDecorators() {
		if ( decorators != null ) {
			for ( Map.Entry<IWorkbenchPart, IBoxDecorator> e : decorators.entrySet() ) {
				e.getValue().getProvider().releaseDecorator(e.getValue());
			}
			decorators.clear();
		}
	}

	public IBoxProvider getBoxProvider(final IWorkbenchPart part) {
		for ( IBoxProvider p : getBoxProviders() ) {
			if ( p.supports(part) ) { return p; }
		}
		return null;
	}

	public IBoxProvider providerForName(final String name) {
		Collection<IBoxProvider> providers = getBoxProviders();
		for ( IBoxProvider provider : providers ) {
			if ( provider.getName().equals(name) ) { return provider; }
		}
		IBoxProvider provider = createProvider(name);
		providers.add(provider);
		return provider;
	}

	public void removeProvider(final String name) {
		for ( Iterator<IBoxProvider> it = getBoxProviders().iterator(); it.hasNext(); ) {
			if ( it.next().getName().equals(name) ) {
				it.remove();
			}
		}
	}

	public void setPartListener(final IPartService partService, final IPartListener2 listener) {
		if ( partService == null ) { return; }
		if ( partListeners == null ) {
			partListeners = new HashMap<IPartService, IPartListener2>();
		}
		IPartListener2 oldListener = partListeners.get(partService);
		if ( oldListener != null ) {
			partService.removePartListener(oldListener);
		}
		partService.addPartListener(listener);
		partListeners.put(partService, listener);
	}

	public void removePartListener(final IPartService partService) {
		if ( partService == null || partListeners == null ) { return; }
		IPartListener2 oldListener = partListeners.remove(partService);
		if ( oldListener != null ) {
			partService.removePartListener(oldListener);
		}
	}
}
