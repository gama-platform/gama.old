/*********************************************************************************************
 *
 *
 * 'GamaBundleLoader.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.joda.time.Chronology;
import org.joda.time.LocalDateTime;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Dates;
import msi.gaml.operators.Strings;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.Types;

/**
 * The class GamaBundleLoader.
 *
 * @author drogoul
 * @since 24 janv. 2012
 *
 */
public class GamaBundleLoader {

	public static String CORE_PLUGIN = "msi.gama.core";
	public static String CORE_MODELS = "msi.gama.models";
	public static String CURRENT_PLUGIN_NAME = CORE_PLUGIN;
	public static String ADDITIONS = "gaml.additions.GamlAdditions";
	public static String GRAMMAR_EXTENSION_DEPRECATED = "gaml.grammar.addition";
	public static String GRAMMAR_EXTENSION = "gaml.extension";
	public static String CREATE_EXTENSION = "gama.create";
	public static String MODELS_EXTENSION = "gama.models";
	public static String CONTENT_EXTENSION = "org.eclipse.core.contenttype.contentTypes";
	private static Set<String> GAMA_PLUGINS = new THashSet<String>();
	private static Map<String, String> MODEL_PLUGINS = new THashMap<String, String>();
	public static Set<String> HANDLED_FILE_EXTENSIONS = new THashSet<String>();

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		// We retrieve the elements declared as extensions to the GAML language,
		// either with the new or the deprecated extension
		final Set<IExtension> extensions = new HashSet<IExtension>();
		IExtensionPoint p = registry.getExtensionPoint(GRAMMAR_EXTENSION);
		extensions.addAll(Arrays.asList(p.getExtensions()));
		p = registry.getExtensionPoint(GRAMMAR_EXTENSION_DEPRECATED);
		extensions.addAll(Arrays.asList(p.getExtensions()));

		// We retrieve their contributor plugin and add them to the
		// GAMA_PLUGINS. In addition, we verify if they declare a folder called
		// `models`
		for (final IExtension e : extensions) {
			final IContributor plugin = e.getContributor();

			GAMA_PLUGINS.add(plugin.getName());
			if (hasModels(plugin)) {
				MODEL_PLUGINS.put(plugin.getName(), "models");
			}
		}

		// We remove the core plugin, in order to build it first (important)
		GAMA_PLUGINS.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		// We then build the other extensions to the language
		for (final String addition : GAMA_PLUGINS) {
			CURRENT_PLUGIN_NAME = addition;
			preBuild(addition);
		}
		CURRENT_PLUGIN_NAME = null;
		// We gather all the extensions to the `create` statement and add them
		// as delegates to CreateStatement
		for (final IConfigurationElement e : registry.getConfigurationElementsFor(CREATE_EXTENSION)) {
			try {
				// TODO Add the defining plug-in
				CreateStatement.addDelegate((ICreateDelegate) e.createExecutableExtension("class"));
			} catch (final CoreException e1) {
				e1.printStackTrace();
			}
		}
		// We gather all the GAMA_PLUGINS that explicitly declare models using
		// the non-default scheme (plugin > models ...).
		for (final IConfigurationElement e : registry.getConfigurationElementsFor(MODELS_EXTENSION)) {
			MODEL_PLUGINS.put(e.getContributor().getName(), e.getAttribute("name"));
		}
		// CRUCIAL INITIALIZATIONS
		AbstractGamlAdditions.buildMetaModel();
		Types.init();

		// We gather all the content types extensions defined in GAMA plugins
		// (not in the other ones)
		final IExtensionPoint contentType = registry.getExtensionPoint(CONTENT_EXTENSION);
		final Set<IExtension> contentExtensions = new HashSet<IExtension>();
		contentExtensions.addAll(Arrays.asList(contentType.getExtensions()));
		for (final IExtension ext : contentExtensions) {
			if (GAMA_PLUGINS.contains(ext.getContributor().getName())) {
				final IConfigurationElement[] configs = ext.getConfigurationElements();
				for (final IConfigurationElement config : configs) {
					HANDLED_FILE_EXTENSIONS.addAll(Arrays.asList(config.getAttribute("file-extensions").split(",")));
					// System.out.println(ext.getContributor().getName() + ": "
					// + config.getAttribute("file-extensions"));
				}
			}
		}

		// We reinit the type hierarchy to gather additional types
		Types.init();
		performStaticInitializations();
		//
		GAMA.getGui().debug(">> GAMA total load time " + (System.currentTimeMillis() - start) + " ms.");
	}

	private static void performStaticInitializations() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final long start = System.currentTimeMillis();
				Dates.initializeAllFormats();
				GAMA.getGui().debug(">> JodaTime initialized in " + (System.currentTimeMillis() - start) + " ms.");
			}
		}).start();

	}

	public static PrintWriter pp = null;

	public static void writeLine(final String s) {
		if (pp == null) {
			try {
				pp = new PrintWriter("monLog.txt");
			} catch (final FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pp.write(s + "\r\n");
		pp.flush();
	}

	/**
	 * @param contributor
	 * @return
	 */
	private static boolean hasModels(final IContributor c) {
		URL url = null;
		try {
			url = new URL("platform:/plugin/" + c.getName() + "/models");
			url = FileLocator.find(url);
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		if (url == null) {
			return false;
		}
		File file = null;
		try {
			final URL new_url = FileLocator.resolve(url);
			final String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			final java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			file = normalizedPath.toFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return file != null && file.exists() && file.isDirectory();
	}

	@SuppressWarnings("unchecked")
	public static void preBuild(final String s) {
		final long start = System.currentTimeMillis();
		Class<IGamlAdditions> gamlAdditions = null;
		try {
			gamlAdditions = (Class<IGamlAdditions>) Platform.getBundle(s).loadClass(ADDITIONS);
		} catch (final ClassNotFoundException e1) {
			GAMA.getGui().debug(">> Impossible to load additions from " + s + " because of " + e1);
			return;
		}
		IGamlAdditions add = null;
		try {
			add = gamlAdditions.newInstance();
		} catch (final InstantiationException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (final IllegalAccessException e) {
			GAMA.getGui().debug(">> Impossible to access additions from " + s);
			return;
		}
		try {
			add.initialize();
		} catch (final SecurityException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (final NoSuchMethodException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		}
		GAMA.getGui()
				.debug(">> GAMA bundle loaded in " + (System.currentTimeMillis() - start) + "ms: " + Strings.TAB + s);

	}

	/**
	 * The list of GAMA_PLUGINS declaring models, together with the inner path
	 * to the folder containing model projects
	 * 
	 * @return
	 */
	public static Map<String, String> getPluginsWithModels() {
		return MODEL_PLUGINS;
	}

	/**
	 * @param name
	 * @return
	 */
	public static boolean contains(final String name) {

		return name.equals(CORE_PLUGIN) || GAMA_PLUGINS.contains(name);
	}

}
