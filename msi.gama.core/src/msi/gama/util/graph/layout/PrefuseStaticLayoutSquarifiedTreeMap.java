package msi.gama.util.graph.layout;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import msi.gama.runtime.exceptions.GamaRuntimeException;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;

public class PrefuseStaticLayoutSquarifiedTreeMap extends
		PrefuseStaticLayoutAbstract {

	public static final String NAME = "squarifiedtreemap";
	
	public static final String OPTION_NAME_FRAME = "frame";
	
	@Override
	protected Layout createLayout(long timeout, Map<String,Object> options) {
		
		if (options.containsKey(OPTION_NAME_FRAME)) {
			try {
				
				return new SquarifiedTreeMapLayout(
						PREFUSE_GRAPH,
						(Double)options.get(OPTION_NAME_FRAME)
						);
				
			} catch (ClassCastException e) {
				throw GamaRuntimeException.error("Option "+OPTION_NAME_FRAME+" of this layout is supposed to be an double.");
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
		return new LinkedList<String>() {{
			add(OPTION_NAME_FRAME);
		}};
	}

}
