/*******************************************************************************************************
 *
 * ModelingActivator.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.modeling.internal;

import java.util.Collections;
import java.util.Map;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;

import msi.gama.lang.gaml.GamlRuntimeModule;
import msi.gama.lang.gaml.ui.GamlUiModule;
import ummisco.gama.dev.utils.DEBUG;

/**
 * This class was generated. Customizations should only happen in a newly introduced subclass.
 */
public class ModelingActivator extends AbstractUIPlugin {

	static {
		DEBUG.OFF();
	}

	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "ummisco.gama.ui.modeling";

	/** The Constant MSI_GAMA_LANG_GAML_GAML. */
	public static final String MSI_GAMA_LANG_GAML_GAML = "msi.gama.lang.gaml.Gaml";

	// private static final Logger logger = Logger.getLogger(ModelingActivator.class);

	/** The instance. */
	private static ModelingActivator INSTANCE;

	/** The injectors. */
	private final Map<String, Injector> injectors =
			Collections.synchronizedMap(Maps.<String, Injector> newHashMapWithExpectedSize(1));

	@Override
	public void start(final BundleContext context) throws Exception {
		DEBUG.OUT("Initialization of GAML XText UI activator begins");
		super.start(context);
		INSTANCE = this;
		DEBUG.OUT("Initialization of GAML XText UI activator finished");

	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		injectors.clear();
		INSTANCE = null;
		super.stop(context);
	}

	/**
	 * Gets the single instance of ModelingActivator.
	 *
	 * @return single instance of ModelingActivator
	 */
	public static ModelingActivator getInstance() { return INSTANCE; }

	/**
	 * Gets the injector.
	 *
	 * @param language
	 *            the language
	 * @return the injector
	 */
	public Injector getInjector(final String language) {
		synchronized (injectors) {
			Injector injector = injectors.get(language);
			if (injector == null) { injectors.put(language, injector = createInjector(language)); }
			return injector;
		}
	}

	/**
	 * Creates the injector.
	 *
	 * @param language
	 *            the language
	 * @return the injector
	 */
	protected Injector createInjector(final String language) {
		try {
			com.google.inject.Module runtimeModule = getRuntimeModule(language);
			com.google.inject.Module sharedStateModule = getSharedStateModule();
			com.google.inject.Module uiModule = getUiModule(language);
			com.google.inject.Module mergedModule = Modules2.mixin(runtimeModule, sharedStateModule, uiModule);
			return Guice.createInjector(mergedModule);
		} catch (Exception e) {
			DEBUG.ERR("Failed to create injector for " + language);
			DEBUG.ERR(e.getMessage(), e);
			throw new RuntimeException("Failed to create injector for " + language, e);
		}
	}

	/**
	 * Gets the runtime module.
	 *
	 * @param grammar
	 *            the grammar
	 * @return the runtime module
	 */
	protected com.google.inject.Module getRuntimeModule(final String grammar) {
		if (MSI_GAMA_LANG_GAML_GAML.equals(grammar)) return new GamlRuntimeModule();
		throw new IllegalArgumentException(grammar);
	}

	/**
	 * Gets the ui module.
	 *
	 * @param grammar
	 *            the grammar
	 * @return the ui module
	 */
	protected com.google.inject.Module getUiModule(final String grammar) {
		if (MSI_GAMA_LANG_GAML_GAML.equals(grammar)) return new GamlUiModule(this);
		throw new IllegalArgumentException(grammar);
	}

	/**
	 * Gets the shared state module.
	 *
	 * @return the shared state module
	 */
	protected com.google.inject.Module getSharedStateModule() { return new SharedStateModule(); }

}
