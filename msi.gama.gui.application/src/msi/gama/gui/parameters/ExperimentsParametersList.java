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
package msi.gama.gui.parameters;

import java.util.*;
import msi.gama.interfaces.*;

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
			AbstractEditor gp = EditorFactory.create((IAgent) null, var);
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(gp.getParam().getName(), gp);
		}
	}

	@Override
	public boolean addItem(final String cat) {
		if ( !categories.containsKey(cat) ) {
			categories.put(cat, new HashMap<String, AbstractEditor>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for ( Map.Entry<String, Map<String, AbstractEditor>> entry : categories.entrySet() ) {
			for ( AbstractEditor gp : entry.getValue().values() ) {
				gp.updateValue();
			};
		}
	}

}
