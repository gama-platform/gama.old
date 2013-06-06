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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Written by drogoul Modified on 18 mai 2011
 * 
 * @todo Description
 * 
 */
public class DynamicAgentInspectView extends AgentInspectView {

	public static final String ID = GuiUtils.DYNAMIC_VIEW_ID;

	List<IAgent> currentAgents = new GamaList();
	boolean locked;

	@Override
	public void update(final IDisplayOutput output) {
		if ( locked ) {
			super.update(output);
			return;
		}
		final InspectDisplayOutput out = (InspectDisplayOutput) output;
		if ( out.getValue() == null ) { return; }
		final List<IAgent> result = out.getLastValue();
		if ( result == null ) {
			reset();
			return;
		}
		Collections.sort(result);
		if ( result.equals(currentAgents) ) {
			super.update(output);
			return;
		}

		// TODO Faire en sorte de ne pas tout supprimer... A voir plus tard.

		reset();
		currentAgents = result;
		displayItems();
	}

	@Override
	public List<IAgent> getItems() {
		return currentAgents;
	}

	private void createExpressionComposite(final Composite intermediate) {
		final Composite searchComposite = new Composite(intermediate, SWT.BORDER_SOLID);
		searchComposite.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		searchComposite.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 5;
		searchComposite.setLayout(layout);
		final Label lock = new Label(searchComposite, SWT.NONE);
		lock.setImage(SwtGui.lock);
		lock.setToolTipText("Lock the current expression results (the list of agents will not be changed)");

		final ExpressionEditor ed =
			EditorFactory.createExpression(searchComposite, "Agents to inspect:", "",
				new EditorListener<IExpression>() {

					@Override
					public void valueModified(final IExpression newValue) {
						if ( output == null ) { return; }
						try {
							((InspectDisplayOutput) output).setNewExpression(newValue);
						} catch (final GamaRuntimeException e) {
							e.printStackTrace();
						}
						update(output);
					}
				}, Types.get(IType.LIST));

		ed.getEditor().setToolTipText("Enter a GAML expression returning one or several agents ");
		ed.getEditor().setBackground(searchComposite.getBackground());
		lock.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				locked = !locked;
				lock.setImage(locked ? SwtGui.unlock : SwtGui.lock);
				ed.getEditor().setEnabled(!locked);
			}

		});
		searchComposite.pack();
		((Text) ed.getEditor()).setText("");
	}

	@Override
	public void ownCreatePartControl(final Composite view) {
		super.ownCreatePartControl(view);
		final Composite intermediate = new Composite(view, SWT.NONE);
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;
		intermediate.setLayout(parentLayout);
		createExpressionComposite(intermediate);
		view.pack();
		view.layout();
		parent = intermediate;
	}

	@Override
	public boolean areItemsClosable() {
		return false;
	}

	@Override
	public void selectionChanged(final Object entity) {
		if ( entity == null ) {
			super.selectionChanged(null);
		}
	}

}
