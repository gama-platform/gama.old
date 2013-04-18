package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.Layout;

/**
 * @see http://prefuse.org/doc/api/prefuse/action/layout/CircleLayout.html
 * @author Samuel Thiriot
 */
public class PrefuseStaticLayoutCircle extends
		PrefuseStaticLayoutAbstract {

	public static final String NAME = "circle";
	
	@Override
	protected Layout createLayout(long timeout, Map<String,Object> options) {
		CircleLayout l = new CircleLayout(PREFUSE_GRAPH);
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
