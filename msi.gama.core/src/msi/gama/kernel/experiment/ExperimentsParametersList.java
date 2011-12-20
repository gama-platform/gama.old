/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;

public class ExperimentsParametersList extends EditorsList<String> {

	public ExperimentsParametersList(final List<? extends IParameter> params) {
		super();
		add(params, null);
	}

	@Override
	public String getItemDisplayName(final String obj, final String previousName) {
		return obj;
	}

	@Override
	public void add(final List<? extends IParameter> params, final IAgent agent) {
		for ( final IParameter var : params ) {
			IParameterEditor gp = GuiUtils.getEditorFactory().create((IAgent) null, var);
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(gp.getParam().getName(), gp);
		}
	}

	@Override
	public boolean addItem(final String cat) {
		if ( !categories.containsKey(cat) ) {
			categories.put(cat, new HashMap<String, IParameterEditor>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for ( Map.Entry<String, Map<String, IParameterEditor>> entry : categories.entrySet() ) {
			for ( IParameterEditor gp : entry.getValue().values() ) {
				gp.updateValue();
			};
		}
	}

}
