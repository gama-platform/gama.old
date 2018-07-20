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
import msi.gaml.operators.Cast;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.Types;
import ummisco.gama.ui.interfaces.EditorListener.Command;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;

@SuppressWarnings ({ "rawtypes" })
public class ExperimentsParametersList extends EditorsList<String> {

	@FunctionalInterface
	static interface EditorVisitor {
		abstract void visit(IParameterEditor ed);
	}

	final IScope scope;
	final Map<String, Boolean> activations = new HashMap<>();

	public ExperimentsParametersList(final IScope scope,
			final Collection<? extends IExperimentDisplayable> paramsAndCommands) {
		super();
		this.scope = scope;
		add(paramsAndCommands, null);
	}

	@Override
	public boolean isEnabled(final AbstractEditor<?> gpParam) {
		final IParameter p = gpParam.getParam();
		if (p == null) { return true; }
		final Boolean b = activations.get(p.getName());
		return b == null ? true : b;
	}

	@Override
	public String getItemDisplayName(final String obj, final String previousName) {
		return obj;
	}

	@Override
	public GamaColor getItemDisplayColor(final String o) {
		return null;
	}

	public IParameterEditor getEditorForVar(final String var) {
		for (final Map<String, IParameterEditor<?>> m : categories.values()) {
			for (final IParameterEditor<?> ed : m.values()) {
				final IParameter param = ed.getParam();
				if (param != null && param.getName().equals(var)) { return ed; }
			}
		}
		return null;
	}

	@Override
	public void add(final Collection<? extends IExperimentDisplayable> params, final IAgent agent) {
		for (final IExperimentDisplayable var : params) {
			final IParameterEditor gp;
			if (var instanceof IParameter) {
				final IParameter param = (IParameter) var;
				gp = EditorFactory.getInstance().create(scope, (IAgent) null, param, null);
				if (param.getType().equals(Types.BOOL)) {
					final String[] enablements = param.getEnablement();
					final String[] disablements = param.getDisablement();
					if (enablements.length > 0) {
						final boolean value = Cast.asBool(scope, param.getInitialValue(scope));
						for (final String other : enablements) {
							activations.put(other, value);
						}
						param.addChangeListener((scope, val) -> {
							for (final String enabled : enablements) {
								final IParameterEditor ed = getEditorForVar(enabled);
								if (ed != null) {
									ed.setActive((Boolean) val);
								}
							}
						});
					}
					if (disablements.length > 0) {
						final boolean value = Cast.asBool(scope, param.getInitialValue(scope));
						for (final String other : disablements) {
							activations.put(other, !value);
						}
						param.addChangeListener((scope, val) -> {
							for (final String disabled : disablements) {
								final IParameterEditor ed = getEditorForVar(disabled);
								if (ed != null) {
									ed.setActive(!(Boolean) val);
								}
							}
						});
					}
				}
			} else {
				gp = EditorFactory.getInstance().create(scope, (UserCommandStatement) var,
						(Command) e -> GAMA.getExperiment().getAgent().executeAction(scope -> {
							final Object result = scope.execute((UserCommandStatement) var).getValue();
							GAMA.getExperiment().refreshAllOutputs();
							return result;
						}));
			}
			String cat = var.getCategory();
			cat = cat == null ? "General" : cat;
			addItem(cat);
			categories.get(cat).put(var.getName(), gp);

		}
	}

	void visitEditors(final EditorVisitor visitor) {
		categories.forEach((s, m) -> {
			m.forEach((n, p) -> {
				visitor.visit(p);
			});
		});
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
