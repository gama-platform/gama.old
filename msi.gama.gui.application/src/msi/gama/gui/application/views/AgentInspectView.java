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
package msi.gama.gui.application.views;

import msi.gama.gui.parameters.AgentAttributesEditorsList;
import msi.gama.gui.util.events.GamaSelectionListener;
import msi.gama.interfaces.*;
import msi.gama.util.GamaList;

public class AgentInspectView extends AttributesEditorsView<IAgent> implements
	GamaSelectionListener {

	public static final String ID = "msi.gama.gui.application.view.AgentInspectView";

	@Override
	public void selectionChanged(final Object entity) {
		if ( entity == null ) {
			reset();
		} else {
			addItem((IAgent) entity);
		}
	}

	@Override
	public boolean areItemsClosable() {
		return true;
	}

	@Override
	public boolean addItem(final IAgent agent) {
		if ( editors == null ) {
			editors = new AgentAttributesEditorsList();
		}
		if ( !editors.getCategories().containsKey(agent) ) {
			editors.add(new GamaList<IParameter>(agent.getSpecies().getVars()), agent);
			createItem(agent, true);
			return true;
		}
		return false;
	}

}
