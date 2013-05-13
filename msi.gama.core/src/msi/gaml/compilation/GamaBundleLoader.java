/**
 * Created by drogoul, 24 janv. 2012
 * 
 */
package msi.gaml.compilation;

import java.util.*;
import msi.gama.common.util.GuiUtils;
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
	public static String EXTENSION = "gaml.grammar.addition";

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		Set<String> plugins = new LinkedHashSet();
		for ( IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION) ) {
			String plugin = e.getContributor().getName();
			if ( !CORE_PLUGIN.equals(plugin) ) {
				plugins.add(plugin);
			}
		}
		preBuild(CORE_PLUGIN);
		for ( String addition : plugins ) {
			preBuild(addition);
		}

		// CRUCIAL INITIALIZATIONS
		AbstractGamlAdditions.buildMetaModel();
		Types.init();

		long end = System.currentTimeMillis();
		GuiUtils.debug("All GAML additions " + " loaded in " + (end - start) + " ms.");
	}

	public static void preBuild(final String s) {
		final long start = System.currentTimeMillis();
		try {
			IGamlAdditions add = (IGamlAdditions) Platform.getBundle(s).loadClass(ADDITIONS).newInstance();
			add.initialize();
			long end = System.currentTimeMillis();
			GuiUtils.debug("GAML plugin " + s + " scanned in " + (end - start) + " ms.");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
