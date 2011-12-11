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
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.util.GamaList;
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

	public static final String ID = "msi.gama.gui.application.view.DynamicAgentInspectView";

	List<IAgent> currentAgents = new GamaList();
	boolean locked;

	@Override
	public void update(final IDisplayOutput output) {
		if ( locked ) {
			super.update(output);
			return;
		}
		InspectDisplayOutput out = (InspectDisplayOutput) output;
		if ( out.getValue() == null ) { return; }
		List<IAgent> result = out.getLastValue();
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

	@Override
	public void ownCreatePartControl(final Composite view) {
		super.ownCreatePartControl(view);
		Composite intermediate = new Composite(view, SWT.NONE);
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;
		intermediate.setLayout(parentLayout);
		Composite searchComposite = new Composite(intermediate, SWT.BORDER_SOLID);
		searchComposite.setBackground(GUI.getDisplay().getSystemColor(
			SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		searchComposite.setForeground(GUI.getDisplay().getSystemColor(
			SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 5;
		searchComposite.setLayout(layout);
		final Label lock = new Label(searchComposite, SWT.NONE);
		lock.setImage(GUI.lock);
		lock.setToolTipText("Lock the current expression results (the list of agents will not be changed)");

		final ExpressionEditor ed =
			EditorFactory.createExpression(searchComposite, "Agents to inspect:", "",
				new EditorListener<IExpression>() {

					@Override
					public void valueModified(final IExpression newValue) {
						try {
							((InspectDisplayOutput) output).setNewExpression(newValue);
						} catch (GamaRuntimeException e) {
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
				lock.setImage(locked ? GUI.unlock : GUI.lock);
				ed.getEditor().setEnabled(!locked);
			}

		});
		searchComposite.pack();
		view.pack();
		((Text) ed.getEditor()).setText("");
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
