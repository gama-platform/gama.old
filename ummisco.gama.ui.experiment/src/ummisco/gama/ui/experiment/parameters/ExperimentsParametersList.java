/*********************************************************************************************
 *
 * 'ExperimentsParametersList.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.experiment.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import msi.gama.kernel.experiment.IExperimentDisplayable;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.interfaces.EditorListener.Command;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.EditorFactory;

@SuppressWarnings ({ "rawtypes" })
public class ExperimentsParametersList extends EditorsList<String> {
	final IScope scope;

	public ExperimentsParametersList(final IScope scope,
			final Collection<? extends IExperimentDisplayable> paramsAndCommands) {
		super();
		this.scope = scope;
		add(paramsAndCommands, null);
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
	public void add(final Collection<? extends IExperimentDisplayable> params, final IAgent agent) {
		for (final IExperimentDisplayable var : params) {
			final IParameterEditor gp;
			if (var instanceof IParameter)
				gp = EditorFactory.getInstance().create(scope, (IAgent) null, (IParameter) var, null);
			else
				gp = EditorFactory.getInstance().create(scope, (UserCommandStatement) var,
						(Command) e -> GAMA.getExperiment().getAgent().executeAction(scope -> {
							final Object result = ((UserCommandStatement) var).executeOn(scope);
							GAMA.getExperiment().refreshAllOutputs();
							return result;
						}));
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(var.getName(), gp);
		}
	}

	@Override
	public boolean addItem(final String cat) {
		if (!categories.containsKey(cat)) {
			categories.put(cat, new HashMap<String, IParameterEditor<?>>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues() {
		for (final Map.Entry<String, Map<String, IParameterEditor<?>>> entry : categories.entrySet()) {
			for (final IParameterEditor gp : entry.getValue().values()) {
				gp.updateValue(true);
			}
			;
		}
	}

	/**
	 * Method handleMenu()
	 * 
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final String data, final int x, final int y) {
		return null;
	}

}
