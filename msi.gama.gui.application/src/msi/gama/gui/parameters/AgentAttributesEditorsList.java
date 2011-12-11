/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.*;
import msi.gama.gui.application.views.ItemList;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;

public class AgentAttributesEditorsList extends EditorsList<IAgent> {

	public static final String DEAD_MARKER = " dead at step ";
	public static final String AGENT_MARKER = "Agent" + ItemList.SEPARATION_CODE;

	@Override
	public String getItemDisplayName(final IAgent ag, final String name) {
		if ( name == null ) { return AGENT_MARKER + ag.getName(); }
		if ( ag.dead() && !name.contains(DEAD_MARKER) ) {
			long cycle = GAMA.getFrontmostSimulation().getScheduler().getCycle();
			String result =
				AGENT_MARKER + ItemList.ERROR_CODE +
					name.substring(name.indexOf(ItemList.SEPARATION_CODE) + 1) + DEAD_MARKER +
					cycle;
			return result;
		}
		return name;
	}

	@Override
	public void add(final List<? extends IParameter> params, final IAgent agent) {
		if ( addItem(agent) ) {
			if ( !agent.dead() ) {
				for ( final IParameter var : params ) {
					AbstractEditor gp = EditorFactory.create(agent, var);
					categories.get(agent).put(gp.getParam().getName(), gp);
				}
			}
		}
	}

	@Override
	public boolean addItem(final IAgent agent) {
		if ( !categories.containsKey(agent) ) {
			categories.put(agent, new HashMap<String, AbstractEditor>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for ( Map.Entry<IAgent, Map<String, AbstractEditor>> entry : categories.entrySet() ) {
			if ( !entry.getKey().dead() ) {
				for ( AbstractEditor gp : entry.getValue().values() ) {
					gp.updateValue();
				};
			}
		}
	}

}
