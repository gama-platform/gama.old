package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;

/**
 * @see http://prefuse.org/doc/api/prefuse/action/layout/graph/ForceDirectedLayout.html
 * @author Samuel Thiriot
 *
 */
public class PrefuseStaticLayoutForceDirected extends
		PrefuseStaticLayoutAbstract {

	public static final String NAME = "forcedirected";
	
	@Override
	protected Layout createLayout(long timeout, Map<String,Object> options) {
		ForceDirectedLayout l = new ForceDirectedLayout(PREFUSE_GRAPH, true, false);
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
