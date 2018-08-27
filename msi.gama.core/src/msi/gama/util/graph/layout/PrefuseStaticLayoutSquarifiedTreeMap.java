/*******************************************************************************************************
 *
 * msi.gama.util.graph.layout.PrefuseStaticLayoutSquarifiedTreeMap.java, in plugin msi.gama.core,
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
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;

public class PrefuseStaticLayoutSquarifiedTreeMap extends PrefuseStaticLayoutAbstract {

	public static final String NAME = "squarifiedtreemap";

	public static final String OPTION_NAME_FRAME = "frame";

	@Override
	protected Layout createLayout(final IScope scope, final long timeout, final Map<String, Object> options) {

		if (options.containsKey(OPTION_NAME_FRAME)) {
			try {

				return new SquarifiedTreeMapLayout(PREFUSE_GRAPH, (Double) options.get(OPTION_NAME_FRAME));

			} catch (final ClassCastException e) {
				throw GamaRuntimeException
						.error("Option " + OPTION_NAME_FRAME + " of this layout is supposed to be an double.", scope);
			}
		} else {
			return new SquarifiedTreeMapLayout(PREFUSE_GRAPH);
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
				add(OPTION_NAME_FRAME);
			}
		};
	}

}
