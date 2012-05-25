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
		for ( IConfigurationElement e : Platform.getExtensionRegistry()
			.getConfigurationElementsFor(EXTENSION) ) {
			String plugin = e.getContributor().getName();
			if ( !CORE_PLUGIN.equals(plugin) ) {
				plugins.add(plugin);
			}
		}
		preBuild(CORE_PLUGIN);
		for ( String addition : plugins ) {
			preBuild(addition);
		}
		Types.init();

		long end = System.currentTimeMillis();
		GuiUtils.debug("All GAML additions " + " loaded in " + (end - start) + " ms.");
	}

	public static void preBuild(final String s) {
		ClassLoader cl = GamaClassLoader.getInstance().addBundle(Platform.getBundle(s));
		final long start = System.currentTimeMillis();
		try {
			IGamlAdditions additions = (IGamlAdditions) cl.loadClass(ADDITIONS).newInstance();
			additions.initialize();
			long end = System.currentTimeMillis();
			GuiUtils.debug("GAML plugin " + s + " scanned in " + (end - start) + " ms.");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
