package gama_analyzer;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.IList;

public class GroupIdRule {

	public GroupIdRule() {}

	public IList<IAgent> update(IScope scope, IList<IAgent> liste) {	
		return liste;
	}
}
