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
package ummisco.gama.ui.experiment.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.EditorFactory;

@SuppressWarnings({ "rawtypes" })
public class ExperimentsParametersList extends EditorsList<String> {
	final IScope scope;

	public ExperimentsParametersList(final IScope scope, final Collection<? extends IParameter> params) {
		super();
		this.scope = scope;
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
		for (final IParameter var : params) {
			final IParameterEditor gp = EditorFactory.getInstance().create(scope, (IAgent) null, var, null);
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(gp.getParam().getName(), gp);
		}
	}

	@Override
	public boolean addItem(final String cat) {
		if (!categories.containsKey(cat)) {
			categories.put(cat, new HashMap<String, IParameterEditor>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for (final Map.Entry<String, Map<String, IParameterEditor>> entry : categories.entrySet()) {
			for (final IParameterEditor gp : entry.getValue().values()) {
				gp.updateValue();
			}
			;
		}
	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object,
	 *      int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final String data, final int x, final int y) {
		return null;
	}

}
