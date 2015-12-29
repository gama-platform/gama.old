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
import java.util.Set;
import org.eclipse.core.runtime.*;
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
	public static String ADDITIONS = "gaml.additions.GamlAdditions";
	public static String GRAMMAR_EXTENSION = "gaml.grammar.addition";
	public static String CREATE_EXTENSION = "gama.create";
	private static Set<String> plugins = new THashSet();
	private static Set<String> pluginsWithModels = new THashSet();

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		for ( IConfigurationElement e : Platform.getExtensionRegistry()
			.getConfigurationElementsFor(GRAMMAR_EXTENSION) ) {
			plugins.add(e.getContributor().getName());
			if ( hasModels(e.getContributor()) ) {
				pluginsWithModels.add(e.getContributor().getName());
			}
		}
		plugins.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		for ( String addition : plugins ) {
			preBuild(addition);
		}
		for ( IConfigurationElement e : Platform.getExtensionRegistry()
			.getConfigurationElementsFor(CREATE_EXTENSION) ) {
			try {
				CreateStatement.addDelegate((ICreateDelegate) e.createExecutableExtension("class"));
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
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

}
