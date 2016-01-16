/*********************************************************************************************
 *
 *
 * 'ExperimentsParametersList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;

public class ExperimentsParametersList extends EditorsList<String> {

	public ExperimentsParametersList(final Collection<? extends IParameter> params) {
		super();
		add(params, null);
	}

	@Override
	public String getItemDisplayName(final String obj, final String previousName) {
		return obj;
	}

	@Override
	public GamaColor getItemDisplayColor(final String o) {
		return null;
	}

	@Override
	public void add(final Collection<? extends IParameter> params, final IAgent agent) {
		for ( final IParameter var : params ) {
			IParameterEditor gp = GAMA.getGui().getEditorFactory().create((IAgent) null, var, null);
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(gp.getParam().getName(), gp);
		}
	}

	@Override
	public boolean addItem(final String cat) {
		if ( !categories.containsKey(cat) ) {
			categories.put(cat, new THashMap<String, IParameterEditor>());
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
