/*******************************************************************************************************
 *
 * ExperimentsParametersList.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.experiment.parameters;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ICategory;
import msi.gama.kernel.experiment.IExperimentDisplayable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.TextStatement;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Cast;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.interfaces.EditorListener.Command;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.MonitorDisplayer;

/**
 * The Class ExperimentsParametersList.
 */
@SuppressWarnings ({ "rawtypes" })
public class ExperimentsParametersList extends EditorsList<String> {

	/** The scope. */
	final IScope scope;

	/** The activations. */
	final Map<String, Boolean> activations = new HashMap<>();

	/** The monitors. */
	final Map<MonitorOutput, MonitorDisplayer> monitors = new HashMap<>();

	/** The categories. */
	final Map<String, EditorsCategory> categories = new LinkedHashMap<>();

	/**
	 * Instantiates a new experiments parameters list.
	 *
	 * @param scope
	 *            the scope
	 * @param paramsAndCommands
	 *            the params and commands
	 */
	public ExperimentsParametersList(final IScope scope,
			final Collection<? extends IExperimentDisplayable> paramsAndCommands) {
		this.scope = scope;
		add(paramsAndCommands, null);
	}

	@Override
	public boolean isEnabled(final IParameterEditor<?> gpParam) {
		final IParameter p = gpParam.getParam();
		if (p == null) return true;
		final Boolean b = activations.get(p.getName());
		return b == null ? true : b;
	}

	@Override
	public String getItemDisplayName(final String obj, final String previousName) {
		return obj;
	}

	@Override
	public GamaColor getItemDisplayColor(final String o) {
		EditorsCategory ec = categories.get(o);
		return ec == null ? null : ec.color();
	}

	/**
	 * Gets the editor for var.
	 *
	 * @param var
	 *            the var
	 * @return the editor for var
	 */
	private IParameterEditor getEditorForVar(final String var) {
		for (final Map<String, IParameterEditor<?>> m : sections.values()) {
			for (final IParameterEditor<?> ed : m.values()) {
				final IParameter param = ed.getParam();
				if (param != null && param.getName().equals(var)) return ed;
			}
		}
		return null;
	}

	@Override
	public void add(final Collection<? extends IExperimentDisplayable> params, final IAgent agent) {
		for (final IExperimentDisplayable var : params) {
			if (var instanceof ICategory cat) {
				addCategory(cat.getName(), cat.getColor(scope), cat.isExpanded(scope));
			} else if (var instanceof IParameter param) {
				addEditor(var, EditorFactory.getInstance().create(scope, (IAgent) null, param, null));
				final String[] enablements = param.getEnablement();
				final String[] disablements = param.getDisablement();
				final String[] refreshments = param.getRefreshment();
				if (enablements.length > 0) {
					final boolean value = Cast.asBool(scope, param.getInitialValue(scope));
					for (final String other : enablements) { activations.put(other, value); }
					param.addChangedListener((scope, val) -> {
						for (final String enabled : enablements) {
							final IParameterEditor ed = getEditorForVar(enabled);
							if (ed != null) { ed.setActive(Cast.asBool(scope, val)); }
						}
					});
				}
				if (disablements.length > 0) {
					final boolean value = Cast.asBool(scope, param.getInitialValue(scope));
					for (final String other : disablements) { activations.put(other, !value); }
					param.addChangedListener((scope, val) -> {
						for (final String disabled : disablements) {
							final IParameterEditor ed = getEditorForVar(disabled);
							if (ed != null) { ed.setActive(!Cast.asBool(scope, val)); }
						}
					});
				}
				if (refreshments.length > 0) {
					param.addChangedListener((scope, val) -> {
						for (final String other : refreshments) {
							final IParameterEditor ed = getEditorForVar(other);
							if (ed != null) { ed.updateWithValueOfParameter(false, true); }
						}
					});
				}
			} else if (var instanceof TextStatement text) {
				addEditor(var, EditorFactory.getInstance().create(scope, text));
			} else if (var instanceof MonitorOutput monitor) {
				addMonitor(monitor);
			} else if (var instanceof UserCommandStatement command) {
				addEditor(var, EditorFactory.getInstance().create(scope, command,
						(Command) e -> GAMA.getExperiment().getAgent().executeAction(scope -> {
							final Object result = scope.execute(command).getValue();
							final IExperimentPlan exp = GAMA.getExperiment();
							if (exp != null) { // in case the experiment is killed in the meantime
								exp.refreshAllOutputs();
							}
							return result;
						})));
			}

		}
	}

	/**
	 * Adds the monitor.
	 *
	 * @param var
	 *            the var
	 * @return the monitor displayer
	 */
	public MonitorDisplayer addMonitor(final MonitorOutput var) {
		MonitorDisplayer result = EditorFactory.getInstance().create(scope, var);
		monitors.put(var, result);
		return result;
	}

	/**
	 * Removes the monitor.
	 *
	 * @param var
	 *            the var
	 * @return the monitor displayer
	 */
	public MonitorDisplayer removeMonitor(final MonitorOutput var) {
		return monitors.remove(var);
	}

	/**
	 * Adds the editor.
	 *
	 * @param var
	 *            the var
	 * @param gp
	 *            the gp
	 */
	private void addEditor(final IExperimentDisplayable var, final IParameterEditor gp) {
		String cat = var.getCategory();
		addItem(cat);
		sections.get(cat).put(var.getName(), gp);
	}

	/**
	 * Adds the category. Used for categories defined by themselves
	 *
	 * @param name
	 *            the name
	 * @param color
	 *            the color
	 * @param b
	 */
	public void addCategory(final String name, final GamaColor color, final boolean expanded) {
		categories.put(name, new EditorsCategory(name, color, expanded));
	}

	/**
	 * Adds the category. Used for categories defined in parameters
	 *
	 * @param name
	 *            the name
	 */
	public void addCategory(final String name) {
		if (!categories.containsKey(name)) {
			categories.put(name,
					new EditorsCategory(name, null, GamaPreferences.Runtime.CORE_EXPAND_PARAMS.getValue()));
		}
	}

	/**
	 * Gets the item expanded.
	 *
	 * @param object
	 *            the object
	 * @return the item expanded
	 */
	@Override
	public boolean getItemExpanded(final String object) {
		// By default
		EditorsCategory cat = categories.get(object);
		if (cat == null) return super.getItemExpanded(object);
		return cat.expanded();
	}

	@Override
	public boolean addItem(final String cat) {
		addCategory(cat);
		if (!sections.containsKey(cat)) {
			sections.put(cat, new HashMap<String, IParameterEditor<?>>());
			return true;
		}
		return false;
	}

	@Override
	public void updateItemValues(final boolean synchronously) {
		for (final Map.Entry<String, Map<String, IParameterEditor<?>>> entry : sections.entrySet()) {
			for (final IParameterEditor gp : entry.getValue().values()) {
				gp.updateWithValueOfParameter(synchronously, false);
			}
		}
		updateMonitors(synchronously);
	}

	/**
	 * Update monitors.
	 *
	 * @param synchronously
	 *            the synchronously
	 */
	public void updateMonitors(final boolean synchronously) {
		monitors.forEach((s, md) -> { md.updateWithValueOfParameter(synchronously, false); });
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

	/**
	 * Checks for monitors.
	 *
	 * @return true, if successful
	 */
	public boolean hasMonitors() {
		return monitors.size() > 0;
	}

	/**
	 * Gets the monitors.
	 *
	 * @return the monitors
	 */
	public Map<MonitorOutput, MonitorDisplayer> getMonitors() { return monitors; }

}
