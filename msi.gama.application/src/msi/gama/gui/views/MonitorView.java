/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.outputs.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author Alexis Drogoul
 */

public class MonitorView extends ExpandableItemsView<MonitorOutput> {

	private static int count = 0;

	public static final String ID = GuiUtils.MONITOR_VIEW_ID;

	private final ArrayList<MonitorOutput> outputs = new ArrayList();

	@Override
	public void ownCreatePartControl(final Composite parent) {
		super.ownCreatePartControl(parent);
		displayItems();
	}

	@Override
	public void setRefreshRate(final int rate) {
		if ( rate > 0 ) {
			setPartName("Monitors");
		}
	}

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {
		return new Integer[] { PAUSE, REFRESH, SEP, NEW_MONITOR };
	}

	@Override
	public void setOutput(final IDisplayOutput output) {
		// always consider the latest monitor as the output
		addItem((MonitorOutput) output);
		super.setOutput(output);
	}

	@Override
	public boolean addItem(final MonitorOutput output) {
		if ( output != null && !outputs.contains(output) ) {
			createItem(output, output.getValue() == null);
			return true;
		}
		return false;

	}

	@Override
	protected Composite createItemContentsFor(final MonitorOutput output) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		GridData firstColData = new GridData(SWT.FILL, SWT.FILL, true, false);
		firstColData.widthHint = 60;
		GridData secondColData = new GridData(SWT.FILL, SWT.FILL, true, false);
		secondColData.widthHint = 200;
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		final Text titleEditor =
			(Text) EditorFactory.create(compo, "Title:", output.getViewName(), true, new EditorListener<String>() {

				@Override
				public void valueModified(final String newValue) throws GamaRuntimeException {
					output.setName(newValue);
					update(output);
				}

			}).getEditor();

		Text c =
			(Text) EditorFactory.createExpression(compo, "Expression:",
				output.getValue() == null ? "Enter a new GAML expression..." : output.getExpressionText(),
				new EditorListener<IExpression>() {

					@Override
					public void valueModified(final IExpression newValue) throws GamaRuntimeException {
						output.setNewExpression(newValue);
						update(output);
					}

				}, Types.get(IType.NONE)).getEditor();

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
		outputs.add(output);
		// update(output);
		return compo;
	}

	@Override
	public void removeItem(final MonitorOutput o) {
		o.close();
		outputs.remove(o);
		if ( outputs.isEmpty() ) {
			GuiUtils.closeViewOf(o);
		}
	}

	@Override
	public void resumeItem(final MonitorOutput o) {
		if ( o.isPaused() ) {
			o.resume();
		}
		update(o);
	}

	@Override
	public void pauseItem(final MonitorOutput o) {
		o.pause();
		update(o);
	}

	// private final StringBuilder sb = new StringBuilder(100);

	@Override
	public String getItemDisplayName(final MonitorOutput o, final String previousName) {
		Object v = o.getLastValue();
		final StringBuilder sb = new StringBuilder(100);
		sb.setLength(0);
		sb.append(o.getViewName()).append(ItemList.SEPARATION_CODE)
			.append(v == null ? "nil" : v instanceof IValue ? ((IValue) v).toGaml() : v.toString());
		if ( o.isPaused() ) {
			sb.append(" (paused)");
		}
		return sb.toString();

	}

	public static void createNewMonitor() {
		new MonitorOutput("monitor" + count++, null);
	}

	@Override
	public void dispose() {
		reset();
		super.dispose();
	}

	@Override
	public void reset() {
		disposeViewer();
		outputs.clear();
	}

	@Override
	public void update(final IDisplayOutput displayOutput) {
		final MonitorOutput output = (MonitorOutput) displayOutput;
		if ( !outputs.contains(output) ) { return; }
		super.update(output);
	}

	@Override
	public void focusItem(final MonitorOutput data) {
		output = data;
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
	public List<MonitorOutput> getItems() {
		return outputs;
	}

	@Override
	public void updateItemValues() {}

}
