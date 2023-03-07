/*******************************************************************************************************
 *
 * GamaBundleLoader.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation.kernel;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.core.IProvisioningAgentProvider;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.expression.ExpressionUtil;
import org.eclipse.equinox.p2.query.IQuery;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.interfaces.IEventLayerDelegate;
import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.IGamlAdditions;
import msi.gaml.operators.IUnits;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The class GamaBundleLoader.
 *
 * @author drogoul
 * @since 24 janv. 2012
 *
 */
public class GamaBundleLoader {

	static {
		DEBUG.ON();
	}

	/**
	 * Error.
	 *
	 * @param e
	 *            the e
	 */
	public static void ERROR(final String message, final Exception e) {
		DEBUG.ERR(ERROR_MESSAGE);
		ERRORED = true;
		DEBUG.ERR(message, e);
	}

	/** The Constant LINE. */
	public static final String LINE =
			"\n\n****************************************************************************************************\n\n";

	/** The Constant ERROR_MESSAGE. */
	public static final String ERROR_MESSAGE = LINE
			+ "The initialization of GAML artifacts went wrong. If you use the developer version, please clean and recompile all plugins. \nOtherwise report an issue at https://github.com/gama-platform/gama/issues"
			+ LINE;

	/** The loaded. */
	public volatile static boolean LOADED = false;

	/** The errored. */
	public volatile static boolean ERRORED = false;

	/** The core plugin. */
	public static final Bundle CORE_PLUGIN = Platform.getBundle("msi.gama.core");

	/** The core models. */
	public static final Bundle CORE_MODELS = Platform.getBundle("msi.gama.models");

	/** The core tests. */
	public static final String CORE_TESTS = "tests";

	/** The current plugin name. */
	public static String CURRENT_PLUGIN_NAME = CORE_PLUGIN.getSymbolicName();

	/** The additions. */
	public static final String ADDITIONS = "gaml.additions.GamlAdditions";

	/** The Constant ADDITIONS_PACKAGE_BASE. */
	public static final String ADDITIONS_PACKAGE_BASE = "gaml.additions";

	/** The Constant ADDITIONS_CLASS_NAME. */
	public static final String ADDITIONS_CLASS_NAME = "GamlAdditions";

	/** The grammar extension deprecated. */
	public static final String GRAMMAR_EXTENSION_DEPRECATED = "gaml.grammar.addition";

	/** The grammar extension. */
	public static final String GRAMMAR_EXTENSION = "gaml.extension";

	/** The create extension. */
	public static final String CREATE_EXTENSION = "gama.create";

	/** The event layer extension. */
	public static final String EVENT_LAYER_EXTENSION = "gama.event_layer";

	/** The models extension. */
	public static final String MODELS_EXTENSION = "gama.models";

	/** The regular models layout. */
	public static final String REGULAR_MODELS_LAYOUT = "models";

	/** The regular tests layout. */
	public static final String REGULAR_TESTS_LAYOUT = "tests";

	/** The generated tests layout. */
	public static final String GENERATED_TESTS_LAYOUT = "gaml/tests";

	/** The content extension. */
	public static final String CONTENT_EXTENSION = "org.eclipse.core.contenttype.contentTypes";

	/** The gama plugins. */
	private static final List<Bundle> GAMA_PLUGINS = new ArrayList<>();

	/** The Constant GAMA_PLUGINS_NAMES. */
	private static final Set<String> GAMA_PLUGINS_NAMES = new LinkedHashSet<>();

	/** The Constant GAMA_PLUGINS_NAMES. */
	private static final Set<String> GAMA_DISPLAY_PLUGINS_NAMES = new LinkedHashSet<>();

	/** The model plugins. */
	private static final Multimap<Bundle, String> MODEL_PLUGINS = ArrayListMultimap.create();

	/** The test plugins. */
	private static final Multimap<Bundle, String> TEST_PLUGINS = ArrayListMultimap.create();

	/** The handled file extensions. */
	public static final Set<String> HANDLED_FILE_EXTENSIONS = new LinkedHashSet<>();

	/** The Constant SYS_ARCH. */
	public static final String SYS_ARCH = Platform.getOSArch(); // System.getProperty("os.arch");

	/** The Constant SYS_NAME. */
	public static final String SYS_NAME = Platform.getOS();// System.getProperty("os.name");

	/** The Constant SYS_VERS. */
	public static final String SYS_VERS = System.getProperty("os.version");

	/** The Constant SYS_JAVA. */
	public static final String SYS_JAVA = System.getProperty("java.version");

	/**
	 * Pre build contributions.
	 *
	 * @throws Exception
	 *             the exception
	 */
	public static void preBuildContributions() throws Exception {

		DEBUG.LOG(DEBUG.PAD("> GAMA: version " + GAMA.VERSION_NUMBER, 55, ' ') + DEBUG.PAD(" loading on", 15, '_') + " "
				+ SYS_NAME + " " + SYS_VERS + ", " + SYS_ARCH + ", JDK " + SYS_JAVA);
		listFeatures();

		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		// We retrieve the elements declared as extensions to the GAML language,
		// either with the new or the deprecated extension
		final Set<IExtension> extensions = new LinkedHashSet<>();
		try {
			IExtensionPoint p = registry.getExtensionPoint(GRAMMAR_EXTENSION);
			extensions.addAll(Arrays.asList(p.getExtensions()));
			p = registry.getExtensionPoint(GRAMMAR_EXTENSION_DEPRECATED);
			extensions.addAll(Arrays.asList(p.getExtensions()));
		} catch (final InvalidRegistryObjectException e) {

			ERROR("Error in retrieving GAMA plugins. One is invalid. ", e);
		}

		DEBUG.TIMER(DEBUG.PAD("> GAML: All " + extensions.size() + " plugins with language additions", 55, ' ')
				+ DEBUG.PAD(" loaded in", 15, '_'), () -> {

					// We retrieve their contributor plugin and add them to the
					// GAMA_PLUGINS. In addition, we verify if they declare a folder called
					// `models` or `tests` or if they have generated tests
					// TEST_PLUGINS.put(CORE_MODELS, REGULAR_TESTS_LAYOUT);
					MODEL_PLUGINS.put(CORE_MODELS, REGULAR_MODELS_LAYOUT);
					for (final IExtension e : extensions) {
						final IContributor plugin = e.getContributor();
						final Bundle bundle = Platform.getBundle(plugin.getName());

						GAMA_PLUGINS.add(bundle);
						GAMA_PLUGINS_NAMES.add(bundle.getSymbolicName());
						if (bundle.getEntry(REGULAR_MODELS_LAYOUT) != null) {
							MODEL_PLUGINS.put(bundle, REGULAR_MODELS_LAYOUT);
						}
						if (bundle.getEntry(
								REGULAR_TESTS_LAYOUT) != null) { TEST_PLUGINS.put(bundle, REGULAR_TESTS_LAYOUT); }
						if (bundle.getEntry(GENERATED_TESTS_LAYOUT) != null) {
							TEST_PLUGINS.put(bundle, GENERATED_TESTS_LAYOUT);
						}
					}
					GAMA_PLUGINS.sort(Comparator.comparing(Bundle::getSymbolicName));
					// We remove the core plugin, in order to build it first (important)
					GAMA_PLUGINS.remove(CORE_PLUGIN);
					try {
						preBuild(CORE_PLUGIN);
					} catch (final Exception e2) {
						ERROR("Error in loading core GAML language definition. ", e2);
						// We exit in case the core cannot be built, as there is no point in continuing past this point
						System.exit(0);
						return;
					}
					// if (!FLAGS.PRODUCE_ICONS) {}

					// We then build the other extensions to the language
					for (final Bundle addition : GAMA_PLUGINS) {
						CURRENT_PLUGIN_NAME = addition.getSymbolicName();
						try {
							preBuild(addition);
						} catch (final Exception e1) {
							ERROR("Error in loading plugin " + CORE_PLUGIN.getSymbolicName() + ". ", e1);
							// We do not systematically exit in case of additional plugins failing to load, so as to
							// give the platform a chance to execute even in case of errors (to save files, to
							// remove
							// offending plugins, etc.)
							continue;
						}
					}
					CURRENT_PLUGIN_NAME = null;
					// We gather all the extensions to the `create` statement and add them
					// as delegates to CreateStatement. If an exception occurs, we discard it
					for (final IConfigurationElement e : registry.getConfigurationElementsFor(CREATE_EXTENSION)) {
						ICreateDelegate cd = null;
						try {
							// TODO Add the defining plug-in
							cd = (ICreateDelegate) e.createExecutableExtension("class");
							if (cd != null) { CreateStatement.addDelegate(cd); }
						} catch (final Exception e1) {
							ERROR("Error in loading CreateStatement delegate from "
									+ e.getDeclaringExtension().getContributor().getName(), e1);
							// We do not systematically exit in case of additional plugins failing to load, so as to
							// give the platform a chance to execute even in case of errors (to save files, to
							// remove
							// offending plugins, etc.)
							continue;
						}
					}

					// We gather all the extensions to the `event` statement and add them
					// as delegates to EventLayerStatement
					for (final IConfigurationElement e : registry.getConfigurationElementsFor(EVENT_LAYER_EXTENSION)) {
						try {
							// TODO Add the defining plug-in
							EventLayerStatement.addDelegate((IEventLayerDelegate) e.createExecutableExtension("class"));
						} catch (final Exception e1) {
							ERROR("Error in loading EventLayerStatement delegate : "
									+ e.getDeclaringExtension().getContributor().getName(), e1);
							// We do not systematically exit in case of additional plugins failing to load, so as to
							// give the platform a chance to execute even in case of errors (to save files, to
							// remove
							// offending plugins, etc.)
							continue;
						}
					}

					// We gather all the GAMA_PLUGINS that explicitly declare models using
					// the non-default scheme (plugin > models ...).
					for (final IConfigurationElement e : registry.getConfigurationElementsFor(MODELS_EXTENSION)) {
						MODEL_PLUGINS.put(Platform.getBundle(e.getContributor().getName()), e.getAttribute("name"));
					}

					// We gather all the content types extensions defined in GAMA plugins
					// (not in the other ones)
					final IExtensionPoint contentType = registry.getExtensionPoint(CONTENT_EXTENSION);
					final Set<IExtension> contentExtensions = new HashSet<>();
					contentExtensions.addAll(Arrays.asList(contentType.getExtensions()));
					for (final IExtension ext : contentExtensions) {
						final IConfigurationElement[] configs = ext.getConfigurationElements();
						for (final IConfigurationElement config : configs) {
							final String s = config.getAttribute("file-extensions");
							if (s != null) { HANDLED_FILE_EXTENSIONS.addAll(Arrays.asList(s.split(","))); }
						}
					}

					// CRUCIAL INITIALIZATIONS
					LOADED = true;
					// We init the meta-model of GAMA (i.e. abstract agent, model, experiment species)
					GamaMetaModel.INSTANCE.build();

					// We init the type hierarchy, the units and the agent representing the GAMA platform
					Types.init();
					IUnits.init();
					GamaMetaModel.INSTANCE.getPlatformSpeciesDescription().validate();
				});

	}

	/**
	 * Pre build.
	 *
	 * @param bundle
	 *            the bundle
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings ("unchecked")
	public static void preBuild(final Bundle bundle) throws Exception {
		DEBUG.TIMER_WITH_EXCEPTIONS(
				DEBUG.PAD("> GAMA: Plugin " + bundle.getSymbolicName(), 55, ' ') + DEBUG.PAD(" loaded in", 15, '_'),
				() -> {
					String shortcut = bundle.getSymbolicName();
					shortcut = shortcut.substring(shortcut.lastIndexOf('.') + 1);
					GamaClassLoader.getInstance().addBundle(bundle);
					Class<IGamlAdditions> clazz = null;
					String classPath = ADDITIONS_PACKAGE_BASE + "." + shortcut + "." + ADDITIONS_CLASS_NAME;
					String error =
							">> Impossible to load additions from " + bundle + " because " + classPath + " cannot be ";
					try {
						clazz = (Class<IGamlAdditions>) bundle.loadClass(classPath);
						clazz.getConstructor().newInstance().initialize();
					} catch (final ClassNotFoundException e) {
						throw new WrappedException(error + "found.", e);
					} catch (final SecurityException | NoSuchMethodException e) {
						throw new WrappedException(error + "initialized.", e);
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						throw new WrappedException(error + "instantiated.", e);
					}
				});
	}

	/**
	 * The list of GAMA_PLUGINS declaring models, together with the inner path to the folder containing model projects
	 *
	 * @return
	 */
	public static Multimap<Bundle, String> getPluginsWithModels() { return MODEL_PLUGINS; }

	/**
	 * Gets the plugins with tests.
	 *
	 * @return the plugins with tests
	 */
	public static Multimap<Bundle, String> getPluginsWithTests() { return TEST_PLUGINS; }

	/**
	 * Find feature of.
	 *
	 * @param plugin
	 *            the plugin
	 * @return the string
	 */
	public static String findFeatureOf(final String plugin) {

		return plugin;
	}

	/**
	 * Find and list features.
	 *
	 * @return the sets the
	 */
	// public static Set<String> findAndListFeatures() {
	// Set<String> result = Sets.newHashSet();
	// for (IBundleGroupProvider provider : Platform.getBundleGroupProviders()) {
	// for (IBundleGroup feature : provider.getBundleGroups()) {
	// final String providerName = feature.getProviderName();
	// final String featureId = feature.getIdentifier();
	// result.add(featureId);
	// DEBUG.LOG(DEBUG.PAD("> GAMA: Feature " + featureId, 45) + DEBUG.PAD("from", 15) + providerName);
	// // for (Bundle bundle : feature.getBundles()) {
	// //
	// // }
	// }
	// }
	// return result;
	// }

	/**
	 * Find features.
	 *
	 * @return the sets the
	 * @throws ProvisionException
	 *             the provision exception
	 */
	static Set<IInstallableUnit> listFeatures() throws ProvisionException {
		// 1. initialize necessary p2 services
		BundleContext ctx = FrameworkUtil.getBundle(GamaBundleLoader.class).getBundleContext();
		if (ctx == null) return Collections.EMPTY_SET;
		ServiceReference<IProvisioningAgentProvider> ref = ctx.getServiceReference(IProvisioningAgentProvider.class);
		if (ref == null) return Collections.EMPTY_SET;
		IProvisioningAgentProvider agentProvider = ctx.getService(ref);
		if (agentProvider == null) return Collections.EMPTY_SET;
		IProvisioningAgent provisioningAgent = agentProvider.createAgent(null);
		if (provisioningAgent == null) return Collections.EMPTY_SET;
		IProfileRegistry registry = (IProfileRegistry) provisioningAgent.getService(IProfileRegistry.SERVICE_NAME);
		if (registry == null) return Collections.EMPTY_SET;
		IProfile profile = registry.getProfile(IProfileRegistry.SELF);
		if (profile == null) return Collections.EMPTY_SET;
		// 2. create a query (check QueryUtil for options)
		IQuery<IInstallableUnit> query = QueryUtil.createMatchQuery(ExpressionUtil.TRUE_EXPRESSION); // QueryUtil.createIUGroupQuery();
		// 3. perform query
		IQueryResult<IInstallableUnit> queryResult = profile.query(query, null);
		List<IInstallableUnit> list = queryResult.toSet().stream()
				.filter(f -> f.getId().contains("gama") || f.getId().contains("gaml")).toList();
		DEBUG.LOG(DEBUG.PAD("> GAMA: Features installed", 55) + DEBUG.PAD(" total", 15, '_') + " " + list.size());
		// for (IInstallableUnit feature : list) {
		// final String version = feature.getVersion().toString();
		// final String featureId = feature.getId();
		// DEBUG.LOG(DEBUG.PAD("> GAMA: Feature " + featureId, 45) + DEBUG.PAD("version", 15) + version);
		// }
		return queryResult.toSet();
	}

	/**
	 * Gaml plugin exists.
	 *
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	public static boolean gamlPluginExists(final String s) {
		return GAMA_PLUGINS_NAMES.contains(s);
	}

	/**
	 * Checks if is display plugin.
	 *
	 * @param s
	 *            the s
	 * @return true, if is display plugin
	 */
	public static boolean isDisplayPlugin(final String s) {
		return GAMA_DISPLAY_PLUGINS_NAMES.contains(s);
	}

	/**
	 * Adds the display plugin.
	 *
	 * @param plugin
	 *            the plugin
	 */
	public static void addDisplayPlugin(final String plugin) {
		GAMA_DISPLAY_PLUGINS_NAMES.add(plugin);

	}

}