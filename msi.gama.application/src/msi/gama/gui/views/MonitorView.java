/*********************************************************************************************
 *
 *
 * 'MonitorView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.List;
import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.outputs.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.*;
import msi.gaml.types.Types;

/**
 * @author Alexis Drogoul
 */

public class MonitorView extends ExpandableItemsView<MonitorOutput> implements IToolbarDecoratedView.Pausable {

	private static int count = 0;

	public static final String ID = IGui.MONITOR_VIEW_ID;

	// private final ArrayList<MonitorOutput> outputs = new ArrayList();

	@Override
	public void ownCreatePartControl(final Composite parent) {
		// super.ownCreatePartControl(parent);
		displayItems();
	}

	// @Override
	// public void setRefreshRate(final int rate) {
	// if ( rate > 0 ) {
	// setPartName("Monitors");
	// }
	// }

	@Override
	public void addOutput(final IDisplayOutput output) {
		super.addOutput(output);
		addItem((MonitorOutput) output);
	}

	@Override
	public boolean addItem(final MonitorOutput output) {
		if ( output != null ) {
			createItem(parent, output, output.getValue() == null,
				output.getColor() == null ? null : GamaColors.get(output.getColor()));
			//getViewer().setSize(getViewer().computeSize(SWT.DEFAULT, SWT.DEFAULT, true));
			return true;
		}
		return false;

	}

	@Override
	protected Composite createItemContentsFor(final MonitorOutput output) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		compo.setBackground(IGamaColors.WHITE.color());
		GridLayout layout = new GridLayout(2, false);
		// GridData firstColData = new GridData(SWT.FILL, SWT.FILL, true, false);
		// firstColData.widthHint = 60;
		// GridData secondColData = new GridData(SWT.FILL, SWT.FILL, true, false);
		// secondColData.widthHint = 200;
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		final Text titleEditor =
			(Text) EditorFactory.create(compo, "Title:", output.getName(), true, new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) throws GamaRuntimeException {
					output.setName(newValue);
					update(output);
				}

			}).getEditor();

		IExpression expr = GAML.compileExpression(output.getExpressionText(), output.getScope().getSimulationScope());

		Text c = (Text) EditorFactory.createExpression(compo, "Expression:",
			output.getValue() == null ? IExpressionFactory.NIL_EXPR : expr, new EditorListener<IExpression>() {

				@Override
				public void valueModified(final IExpression newValue) throws GamaRuntimeException {
					output.setNewExpression(newValue);
					update(output);
				}

			}, Types.NO_TYPE).getEditor();

		c.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				getViewer().collapseItemWithData(output);
			}

		});
		titleEditor.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent evt) {
				output.setName(titleEditor.getText());
				update(output);
			}
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
		if ( o.isPaused() ) {
			o.setPaused(false);
		}
		update(o);
	}

	@Override
	public void pauseItem(final MonitorOutput o) {
		o.setPaused(true);
		update(o);
	}

	// private final StringBuilder sb = new StringBuilder(100);

	@Override
	public String getItemDisplayName(final MonitorOutput o, final String previousName) {
		Object v = o.getLastValue();
		final StringBuilder sb = new StringBuilder(100);
		sb.setLength(0);
		sb.append(o.getName()).append(ItemList.SEPARATION_CODE)
			.append(v == null ? "nil" : v instanceof IValue ? ((IValue) v).serialize(true) : v.toString());
		if ( o.isPaused() ) {
			sb.append(" (paused)");
		}
		return sb.toString();

	}

	@Override
	public GamaColor getItemDisplayColor(final MonitorOutput o) {
		return o.getColor();
	}

	public static void createNewMonitor() {
		// TODO ADD the possibility to do it in several simulations
		new MonitorOutput("monitor" + count++, "");
	}

	// @Override
	// public void dispose() {
	// reset();
	// super.dispose();
	// }

	@Override
	public void reset() {
		disposeViewer();
		outputs.clear();
	}

	//
	// @Override
	// public void update(final IDisplayOutput displayOutput) {
	// final MonitorOutput output = (MonitorOutput) displayOutput;
	// if ( !outputs.contains(output) ) { return; }
	// super.update(output);
	// }

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
	public List getItems() {
		return outputs;
	}

	@Override
	public void updateItemValues() {}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(IGamaIcons.MENU_ADD_MONITOR.getCode(), "Add new monitor", "Add new monitor", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				createNewMonitor();
			}

		}, SWT.RIGHT);
	}

	// @Override
	// public void outputReloaded(final IDisplayOutput output) {
	//
	// }

	/**
	 * Method pauseChanged()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Pausable#pauseChanged()
	 */
	@Override
	public void pauseChanged() {}

	/**
	 * Method synchronizeChanged()
	 * @see msi.gama.gui.views.IToolbarDecoratedView.Pausable#synchronizeChanged()
	 */
	@Override
	public void synchronizeChanged() {}

	/**
	 * Method handleMenu()
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final MonitorOutput data, final int x, final int y) {
		return null;
	}

}
