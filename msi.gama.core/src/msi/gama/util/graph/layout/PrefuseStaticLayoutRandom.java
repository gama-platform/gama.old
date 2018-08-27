/*******************************************************************************************************
 *
 * msi.gama.util.graph.layout.PrefuseStaticLayoutRandom.java, in plugin msi.gama.core,
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
import prefuse.action.layout.RandomLayout;

public class PrefuseStaticLayoutRandom extends PrefuseStaticLayoutAbstract {

	public static final String NAME = "random";

	@Override
	protected Layout createLayout(final IScope scope, final long timeout, final Map<String, Object> options) {
		return new RandomLayout(PREFUSE_GRAPH);
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
