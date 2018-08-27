/*******************************************************************************************************
 *
 * msi.gama.util.graph.layout.PrefuseStaticLayoutForceDirected.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import msi.gama.runtime.IScope;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;

/**
 * @see http://prefuse.org/doc/api/prefuse/action/layout/graph/ForceDirectedLayout.html
 * @author Samuel Thiriot
 *
 */
public class PrefuseStaticLayoutForceDirected extends PrefuseStaticLayoutAbstract {

	public static final String NAME = "forcedirected";

	@Override
	protected Layout createLayout(final IScope scope, final long timeout, final Map<String, Object> options) {
		final ForceDirectedLayout l = new ForceDirectedLayout(PREFUSE_GRAPH, true, false);
		return l;
	}

	@Override
	protected String getLayoutName() {
		return NAME;
	}

	@Override
	protected Collection<String> getLayoutOptions() {
		return Collections.EMPTY_LIST;
	}

}
