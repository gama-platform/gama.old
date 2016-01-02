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
import java.util.*;
import org.eclipse.core.runtime.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.util.GuiUtils;
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
	public static String CURRENT_PLUGIN_NAME = CORE_PLUGIN;
	public static String ADDITIONS = "gaml.additions.GamlAdditions";
	public static String GRAMMAR_EXTENSION_DEPRECATED = "gaml.grammar.addition";
	public static String GRAMMAR_EXTENSION = "gaml.extension";
	public static String CREATE_EXTENSION = "gama.create";
	public static String MODELS_EXTENSION = "gama.models";
	private static Set<String> plugins = new THashSet();
	private static Map<String, String> pluginsWithModels = new THashMap();

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		// We first retrieve the elements declared as extensions to the GAML language, either with the new or the deprecated extension
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		Set<IExtension> extensions = new HashSet();
		IExtensionPoint p = registry.getExtensionPoint(GRAMMAR_EXTENSION);
		extensions.addAll(Arrays.asList(p.getExtensions()));
		p = registry.getExtensionPoint(GRAMMAR_EXTENSION_DEPRECATED);
		extensions.addAll(Arrays.asList(p.getExtensions()));
		// We retrieve their contributor plugin and add them to the plugins. In addition, we verify if they declare a folder called `models`
		for ( IExtension e : extensions ) {
			IContributor plugin = e.getContributor();
			plugins.add(plugin.getName());
			if ( hasModels(plugin) ) {
				pluginsWithModels.put(plugin.getName(), "models");
			}
		}

		// We remove the core plugin, in order to build it first (important)
		plugins.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		// We then build the other extensions to the language
		for ( String addition : plugins ) {
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
		// We gather all the plugins that explicitly declare models using the non-default scheme (plugin > models ...).
		for ( IConfigurationElement e : registry.getConfigurationElementsFor(MODELS_EXTENSION) ) {
			pluginsWithModels.put(e.getContributor().getName(), e.getAttribute("name"));
		}
		// CRUCIAL INITIALIZATIONS
		AbstractGamlAdditions.buildMetaModel();
		Types.init();
		GuiUtils.debug(">> GAMA total load time " + (System.currentTimeMillis() - start) + " ms.");
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
			URI uri = FileLocator.resolve(url).toURI().normalize();
			file = new File(uri);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file != null && file.exists() && file.isDirectory();
	}

	public static void preBuild(final String s) {
		final long start = System.currentTimeMillis();
		Class<IGamlAdditions> gamlAdditions = null;
		try {
			gamlAdditions = (Class<IGamlAdditions>) Platform.getBundle(s).loadClass(ADDITIONS);
		} catch (ClassNotFoundException e1) {
			GuiUtils.debug(">> Impossible to load additions from " + s + " because of " + e1);
			return;
		}
		IGamlAdditions add = null;
		try {
			add = gamlAdditions.newInstance();
		} catch (InstantiationException e) {
			GuiUtils.debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (IllegalAccessException e) {
			GuiUtils.debug(">> Impossible to access additions from " + s);
			return;
		}
		try {
			add.initialize();
		} catch (SecurityException e) {
			GuiUtils.debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (NoSuchMethodException e) {
			GuiUtils.debug(">> Impossible to instantiate additions from " + s);
			return;
		}
		GuiUtils.debug(">> GAMA bundle loaded in " + (System.currentTimeMillis() - start) + "ms: " + Strings.TAB + s);

	}

	/**
	 * The list of plugins declaring models, together with the inner path to the folder containing model projects
	 * @return
	 */
	public static Map<String, String> getPluginsWithModels() {
		return pluginsWithModels;
	}

}
