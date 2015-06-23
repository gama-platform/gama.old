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

import gnu.trove.set.hash.THashSet;
import java.util.Set;
import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.common.util.GuiUtils;
import msi.gaml.operators.Strings;
import msi.gaml.statements.CreateStatement;
import msi.gaml.types.Types;
import org.eclipse.core.runtime.*;

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

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		for ( IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(GRAMMAR_EXTENSION) ) {
			plugins.add(e.getContributor().getName());
		}
		plugins.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		for ( String addition : plugins ) {
			preBuild(addition);
		}
		for ( IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(CREATE_EXTENSION) ) {
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
