/*********************************************************************************************
 *
 * 'PrefuseStaticLayoutCircle.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import msi.gama.runtime.IScope;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.Layout;

/**
 * @see http://prefuse.org/doc/api/prefuse/action/layout/CircleLayout.html
 * @author Samuel Thiriot
 */
public class PrefuseStaticLayoutCircle extends PrefuseStaticLayoutAbstract {

	public static final String NAME = "circle";

	@Override
	protected Layout createLayout(final IScope scope, final long timeout, final Map<String, Object> options) {
		final CircleLayout l = new CircleLayout(PREFUSE_GRAPH);
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
