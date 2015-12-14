/*********************************************************************************************
 *
 *
 * 'AgentAttributesEditorsList.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.*;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;

public class AgentAttributesEditorsList extends EditorsList<IAgent> {

	private static final String DEAD_MARKER = " dead at step ";
	private static final String AGENT_MARKER = "Agent" + ItemList.SEPARATION_CODE;
	private static final Set<String> HIDDEN =
		new HashSet(Arrays.asList(IKeyword.PEERS, IKeyword.MEMBERS, IKeyword.AGENTS));

	@Override
	public String getItemDisplayName(final IAgent ag, final String name) {
		if ( name == null ) { return AGENT_MARKER + ag.getName(); }
		if ( ag.dead() && !name.contains(DEAD_MARKER) ) {
			long cycle = GAMA.getClock().getCycle();
			String result = AGENT_MARKER + ItemList.ERROR_CODE +
				name.substring(name.indexOf(ItemList.SEPARATION_CODE) + 1) + DEAD_MARKER + cycle;
			return result;
		}
		return name;
	}

	@Override
	public void add(final Collection<? extends IParameter> params, final IAgent agent) {
		if ( addItem(agent) ) {
			if ( !agent.dead() ) {
				for ( final IParameter var : params ) {
					if ( !HIDDEN.contains(var.getName()) ) {
						IParameterEditor gp = EditorFactory.getInstance().create(agent, var, null);
						categories.get(agent).put(gp.getParam().getName(), gp);
					}
				}
			}
		}
	}

	@Override
	public boolean addItem(final IAgent agent) {
		if ( !categories.containsKey(agent) ) {
			categories.put(agent, new THashMap<String, IParameterEditor>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for ( Map.Entry<IAgent, Map<String, IParameterEditor>> entry : categories.entrySet() ) {
			if ( !entry.getKey().dead() ) {
				for ( IParameterEditor gp : entry.getValue().values() ) {
					gp.updateValue();
				};
			}
		}
	}

}
