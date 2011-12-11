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
package msi.gama.gui.application.views;

import java.util.*;
import java.util.List;
import msi.gama.gui.application.GUI;
import msi.gama.gui.parameters.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.MonitorOutput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * @author Alexis Drogoul
 */

public class MonitorView extends ExpandableItemsView<MonitorOutput> {

	private static int count = 0;

	public static final String ID = "msi.gama.gui.application.view.MonitorView";

	ArrayList<MonitorOutput> outputs = new ArrayList();

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
			(Text) EditorFactory.create(compo, "Title:", output.getViewName(), true,
				new EditorListener<String>() {

					@Override
					public void valueModified(final String newValue) throws GamaRuntimeException,
						GamlException {
						output.setName(newValue);
						update(output);
					}

				}).getEditor();

		EditorFactory.createExpression(
			compo,
			"Expression:",
			(output.getValue() == null ? "Enter a new GAML expression..." : output
				.getExpressionText()), new EditorListener<IExpression>() {

				@Override
				public void valueModified(final IExpression newValue) throws GamaRuntimeException,
					GamlException {
					output.setNewExpression(newValue);
					update(output);
					getViewer().collapseItemWithData(output);
				}

			}, Types.get(IType.NONE)).getEditor();

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
			GUI.closeViewOf(o);
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

	StringBuilder sb = new StringBuilder();

	@Override
	public String getItemDisplayName(final MonitorOutput o, final String previousName) {
		Object v = o.getLastValue();
		sb.setLength(0);
		sb.append(o.getViewName()).append(ItemList.SEPARATION_CODE)
			.append(v == null ? "nil" : v instanceof IValue ? ((IValue) v).toGaml() : v.toString());
		if ( o.isPaused() ) {
			sb.append(" (paused)");
		}
		return sb.toString();

	}

	public static void createNewMonitor() {
		ISimulation simulation = GAMA.getFrontmostSimulation();
		try {
			new MonitorOutput("monitor" + count++, null, simulation, true);
		} catch (GamlException e) {
			e.printStackTrace();
		}
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
