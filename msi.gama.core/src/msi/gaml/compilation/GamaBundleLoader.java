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

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;
import org.eclipse.core.runtime.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.runtime.GAMA;
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
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		// We retrieve the elements declared as extensions to the GAML language, either with the new or the deprecated extension
		Set<IExtension> extensions = new HashSet<IExtension>();
		IExtensionPoint p = registry.getExtensionPoint(GRAMMAR_EXTENSION);
		extensions.addAll(Arrays.asList(p.getExtensions()));
		p = registry.getExtensionPoint(GRAMMAR_EXTENSION_DEPRECATED);
		extensions.addAll(Arrays.asList(p.getExtensions()));

		// We retrieve their contributor plugin and add them to the GAMA_PLUGINS. In addition, we verify if they declare a folder called `models`
		for ( IExtension e : extensions ) {
			IContributor plugin = e.getContributor();

			GAMA_PLUGINS.add(plugin.getName());
			if ( hasModels(plugin) ) {
				MODEL_PLUGINS.put(plugin.getName(), "models");
			}
		}

		// We remove the core plugin, in order to build it first (important)
		GAMA_PLUGINS.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		// We then build the other extensions to the language
		for ( String addition : GAMA_PLUGINS ) {
			CURRENT_PLUGIN_NAME = addition;
			preBuild(addition);
		}
		CURRENT_PLUGIN_NAME = null;
		// We gather all the extensions to the `create` statement and add them as delegates to CreateStatement
		for ( IConfigurationElement e : registry.getConfigurationElementsFor(CREATE_EXTENSION) ) {
			try {
				// TODO Add the defining plug-in
				CreateStatement.addDelegate((ICreateDelegate) e.createExecutableExtension("class"));
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		// We gather all the GAMA_PLUGINS that explicitly declare models using the non-default scheme (plugin > models ...).
		for ( IConfigurationElement e : registry.getConfigurationElementsFor(MODELS_EXTENSION) ) {
			MODEL_PLUGINS.put(e.getContributor().getName(), e.getAttribute("name"));
		}
		// CRUCIAL INITIALIZATIONS
		AbstractGamlAdditions.buildMetaModel();
		Types.init();

		// We gather all the content types extensions defined in GAMA plugins (not in the other ones)
		IExtensionPoint contentType = registry.getExtensionPoint(CONTENT_EXTENSION);
		Set<IExtension> contentExtensions = new HashSet<IExtension>();
		contentExtensions.addAll(Arrays.asList(contentType.getExtensions()));
		for ( IExtension ext : contentExtensions ) {
			if ( GAMA_PLUGINS.contains(ext.getContributor().getName()) ) {
				IConfigurationElement[] configs = ext.getConfigurationElements();
				for ( IConfigurationElement config : configs ) {
					HANDLED_FILE_EXTENSIONS.addAll(Arrays.asList(config.getAttribute("file-extensions").split(",")));
					// System.out.println(ext.getContributor().getName() + ": " + config.getAttribute("file-extensions"));
				}
			}
		}

		//
		GAMA.getGui().debug(">> GAMA total load time " + (System.currentTimeMillis() - start) + " ms.");
	}

	public static PrintWriter pp = null;

	public static void writeLine(final String s) {
		if ( pp == null ) {
			try {
				pp = new PrintWriter("monLog.txt");
			} catch (FileNotFoundException e) {
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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if ( url == null ) { return false; }
		File file = null;
		try {
			URL new_url = FileLocator.resolve(url);
			String path_s = new_url.getPath().replaceFirst("^/(.:/)", "$1");
			java.nio.file.Path normalizedPath = Paths.get(path_s).normalize();
			file = normalizedPath.toFile();
		} catch (Exception e) {
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
		} catch (ClassNotFoundException e1) {
			GAMA.getGui().debug(">> Impossible to load additions from " + s + " because of " + e1);
			return;
		}
		IGamlAdditions add = null;
		try {
			add = gamlAdditions.newInstance();
		} catch (InstantiationException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (IllegalAccessException e) {
			GAMA.getGui().debug(">> Impossible to access additions from " + s);
			return;
		}
		try {
			add.initialize();
		} catch (SecurityException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (NoSuchMethodException e) {
			GAMA.getGui().debug(">> Impossible to instantiate additions from " + s);
			return;
		}
		GAMA.getGui()
			.debug(">> GAMA bundle loaded in " + (System.currentTimeMillis() - start) + "ms: " + Strings.TAB + s);

	}

	/**
	 * The list of GAMA_PLUGINS declaring models, together with the inner path to the folder containing model projects
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
