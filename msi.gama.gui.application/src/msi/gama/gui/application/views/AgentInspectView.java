/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
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
