/*********************************************************************************************
 *
 * 'AvailableGraphLayouts.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.layout;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Lists all the layouts available, independantly of the underlying libraries. If you add a layout, add it there.
 *
 * @author Samuel Thiriot
 *
 */
public class AvailableGraphLayouts {

	private static final Map<String, Class<? extends IStaticLayout>> name2layout =
			new HashMap<String, Class<? extends IStaticLayout>>() {

				{
					// we store both the default version (ex. forcedirected is implemented by default by prefuse,
					// but also a prefixed version for disambiguation (like "prefuse.forcedirected")

					// default
					put("fruchtermanreingold", PrefuseStaticLayoutFruchtermanReingoldLayout.class);
					put("forcedirected", PrefuseStaticLayoutForceDirected.class);
					put("random", PrefuseStaticLayoutRandom.class);
					put("squarifiedtreemap", PrefuseStaticLayoutSquarifiedTreeMap.class);
					put("radialtree", PrefuseStaticLayoutRadialTree.class);
					put("circle", PrefuseStaticLayoutCircle.class);

					// prefuse
					put("prefuse.fruchtermanreingold", PrefuseStaticLayoutFruchtermanReingoldLayout.class);
					put("prefuse.forcedirected", PrefuseStaticLayoutForceDirected.class);
					put("prefuse.random", PrefuseStaticLayoutRandom.class);
					put("prefuse.squarifiedtreemap", PrefuseStaticLayoutSquarifiedTreeMap.class);
					put("prefuse.radialtree", PrefuseStaticLayoutRadialTree.class);
					put("prefuse.circle", PrefuseStaticLayoutCircle.class);

				}
			};

	public static Set<String> getAvailableLayouts() {
		return name2layout.keySet();
	}

	private static Map<String, IStaticLayout> name2singleton = new HashMap<String, IStaticLayout>();

	public static IStaticLayout getStaticLayout(final IScope scope, final String name) {
		IStaticLayout res = name2singleton.get(name);

		if (res == null) {
			// no singleton created
			final Class<? extends IStaticLayout> classLayout = name2layout.get(name);
			if (classLayout == null) { throw GamaRuntimeException.error(
					"unknown layout name: " + name + "; please choose one of " + getAvailableLayouts().toString(),
					scope); }
			try {
				res = classLayout.newInstance();
			} catch (final InstantiationException e) {
				throw GamaRuntimeException.create(e, scope);
			} catch (final IllegalAccessException e) {
				throw GamaRuntimeException.create(e, scope);
			}
			name2singleton.put(name, res);
		}

		return res;
	}

}
