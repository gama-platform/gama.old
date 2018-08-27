/*******************************************************************************************************
 *
 * msi.gama.util.graph.layout.PrefuseStaticLayoutRadialTree.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.RadialTreeLayout;

public class PrefuseStaticLayoutRadialTree extends PrefuseStaticLayoutAbstract {

	public static final String NAME = "radialtree";

	public static final String OPTION_NAME_RADIUS = "radius";

	@Override
	protected Layout createLayout(final IScope scope, final long timeout, final Map<String, Object> options) {

		if (options.containsKey(OPTION_NAME_RADIUS)) {
			try {

				return new RadialTreeLayout(PREFUSE_GRAPH, (Integer) options.get(OPTION_NAME_RADIUS));

			} catch (final ClassCastException e) {
				throw GamaRuntimeException
						.error("Option " + OPTION_NAME_RADIUS + " of this layout is supposed to be an integer.", scope);
			}
		} else {
			return new RadialTreeLayout(PREFUSE_GRAPH);
		}

	}

	@Override
	protected String getLayoutName() {
		return NAME;
	}

	@Override
	protected Collection<String> getLayoutOptions() {
		return new LinkedList<String>() {
			{
				add(OPTION_NAME_RADIUS);
			}
		};
	}

}
