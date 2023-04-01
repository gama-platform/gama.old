/*******************************************************************************************************
 *
 * MonitorView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import static ummisco.gama.ui.resources.GamaColors.get;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import msi.gama.common.interfaces.IValue;
import msi.gama.common.interfaces.ItemList;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.outputs.ValuedDisplayOutputFactory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.COUNTER;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.EditorsGroup;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.ExpandableItemsView;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * @author Alexis Drogoul
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class MonitorView extends ExpandableItemsView<MonitorOutput> implements IToolbarDecoratedView.Pausable {

	@Override
	public void ownCreatePartControl(final Composite parent) {
		displayItems();
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void addOutput(final IDisplayOutput output) {
		super.addOutput(output);
		addItem((MonitorOutput) output);
	}

	@Override
	public boolean addItem(final MonitorOutput output) {
		if (output != null) {
			createItem(getParentComposite(), output, output.getValue() == null,
					output.getColor(null) == null ? null : get(output.getColor(null)));
			return true;
		}
		return false;

	}

	@Override
	protected Composite createItemContentsFor(final MonitorOutput output) {
		final EditorsGroup compo = new EditorsGroup(getViewer(), SWT.NONE);
		final Text titleEditor =
				(Text) EditorFactory.create(output.getScope(), compo, "Title:", output.getName(), true, newValue -> {
					output.setName(newValue);
					update(output);
				}).getEditor();

		IExpression expr;
		try {
			expr = GAML.compileExpression(output.getExpressionText(), output.getScope().getSimulation(), true);
		} catch (GamaRuntimeException e1) {
			// The expression is maybe dedicated to experiments (and not simulations) ?
			expr = GAML.compileExpression(output.getExpressionText(), output.getScope().getExperiment(), true);
		}

		final Text c = (Text) EditorFactory.createExpression(output.getScope(), compo, "Expression:",
				output.getValue() == null ? IExpressionFactory.NIL_EXPR : expr, newValue -> {
					output.setNewExpression((IExpression) newValue);
					update(output);
				}, Types.NO_TYPE).getEditor();

		c.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				getViewer().collapseItemWithData(output);
			}

		});
		titleEditor.addModifyListener(evt -> {
			output.setName(titleEditor.getText());
			update(output);
		});
		// outputs.add(output);
		// update(output);
		return compo;
	}

	@Override
	public void removeItem(final MonitorOutput o) {
		o.close();
		removeOutput(o);
	}

	@Override
	public void resumeItem(final MonitorOutput o) {
		if (o.isPaused()) { o.setPaused(false); }
		update(o);
	}

	@Override
	public void pauseItem(final MonitorOutput o) {
		o.setPaused(true);
		update(o);
	}

	@Override
	public String getItemDisplayName(final MonitorOutput o, final String previousName) {
		final StringBuilder sb = new StringBuilder(100);
		sb.setLength(0);
		sb.append(o.getName()).append(ItemList.SEPARATION_CODE).append(getValueAsString(o));
		if (o.isPaused()) { sb.append(" (paused)"); }
		return sb.toString();

	}

	/**
	 * Gets the value as string.
	 *
	 * @param o
	 *            the o
	 * @return the value as string
	 */
	public String getValueAsString(final MonitorOutput o) {
		final Object v = o.getLastValue();
		return v == null ? "nil" : v instanceof IValue ? ((IValue) v).serialize(true) : v.toString();
	}

	@Override
	public GamaColor getItemDisplayColor(final MonitorOutput o) {
		return o.getColor(null);
	}

	/**
	 * Creates the new monitor.
	 *
	 * @param scope
	 *            the scope
	 */
	@SuppressWarnings ("unused")
	public static void createNewMonitor(final IScope scope) {
		// TODO ADD the possibility to do it in several simulations
		new MonitorOutput(scope, "monitor" + COUNTER.COUNT(), "");
	}

	@Override
	public void reset() {
		disposeViewer();
		outputs.clear();
	}

	@Override
	public void focusItem(final MonitorOutput data) {
		outputs.remove(data);
		outputs.add(0, data);
	}

	@Override
	protected boolean areItemsClosable() {
		return true;
	}

	@Override
	protected boolean areItemsPausable() {
		return true;
	}

	@Override
	public List getItems() { return outputs; }

	@Override
	public void updateItemValues(final boolean synchronously) {}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.MENU_ADD_MONITOR, "Add new monitor", "Add new monitor",
				e -> createNewMonitor(getOutput().getScope()), SWT.RIGHT);
	}

	// @Override
	// public void outputReloaded(final IDisplayOutput output) {
	//
	// }

	/**
	 * Method pauseChanged()
	 *
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView.Pausable#pauseChanged()
	 */
	@Override
	public void pauseChanged() {}

	/**
	 * Method handleMenu()
	 *
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final MonitorOutput data, final int x, final int y) {
		final Map<String, Runnable> menu = new HashMap();
		final IExpression exp = data.getValue();
		if (exp == null) return null;
		final IType<?> type = exp.getGamlType();
		menu.put("Copy to clipboard", () -> { WorkbenchHelper.copy(getValueAsString(data)); });
		if (type.isNumber() || type.isContainer() && type.getContentType().isNumber()) {
			// menu.put("Open chart", () -> {});
			menu.put("Save as CSV", () -> { data.saveHistory(); });
		} else if (type.isAgentType()) {
			menu.put("Inspect", () -> { data.getScope().getGui().setSelectedAgent((IAgent) data.getLastValue()); });
		} else if (type.isContainer() && type.getContentType().isAgentType()) {
			menu.put("Browse",
					() -> { ValuedDisplayOutputFactory.browse((Collection<? extends IAgent>) data.getLastValue()); });
		}
		return menu;
	}

}
