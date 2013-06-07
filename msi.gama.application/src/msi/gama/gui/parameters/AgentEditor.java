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
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gaml.types.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class AgentEditor extends AbstractEditor {

	private Button agentChooser;
	Label agentDisplayer;
	Button agentInspector;
	String species;

	AgentEditor(final IParameter param) {
		super(param);
	}

	AgentEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	AgentEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		species = param.getType().toString();
	}

	AgentEditor(final Composite parent, final String title, final Object value,
		final EditorListener<java.util.List> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		currentValue = getOriginalValue();
		Composite comp = new Composite(compo, SWT.None);
		comp.setLayoutData(getParameterGridData());
		final GridLayout layout = new GridLayout(3, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		agentDisplayer = new Label(comp, SWT.NONE);

		agentInspector = new Button(comp, SWT.FLAT | SWT.PUSH);
		agentInspector.setAlignment(SWT.CENTER);
		agentInspector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( currentValue instanceof IAgent ) {
					IAgent a = (IAgent) currentValue;
					if ( a != null && !a.dead() ) {
						GuiUtils.setSelectedAgent(a);
					}
				}
			}
		});
		agentInspector.setImage(SwtGui.agentImage);
		agentInspector.setText("Inspect");

		agentChooser = new Button(comp, SWT.FLAT | SWT.PUSH);
		agentChooser.setAlignment(SWT.CENTER);
		agentChooser.addSelectionListener(this);
		agentChooser.setImage(SwtGui.editImage);
		agentChooser.setText("Change...");

		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		agentChooser.setLayoutData(d);
		return agentDisplayer;
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		Menu old = agentChooser.getMenu();
		agentChooser.setMenu(null);
		if ( old != null ) {
			old.dispose();
		}
		// FIXME Not adapted to multiple scales !
		Menu dropMenu =
			AgentsMenu.createSpeciesSubMenu(agentChooser, GAMA.getSimulation().getMicroPopulation(species),
				new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						MenuItem mi = (MenuItem) e.widget;
						IAgent a = (IAgent) mi.getData("agent");
						if ( a != null && !a.dead() ) {
							modifyAndDisplayValue(a);
						}
					}

				});
		agentChooser.setMenu(dropMenu);
		dropMenu.setVisible(true);

	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		agentDisplayer.setText(currentValue instanceof IAgent ? ((IAgent) currentValue).getName() : "No agent");
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return agentDisplayer;
	}

	@Override
	public IType getExpectedType() {
		return GAML.getModelContext().getTypeNamed(species);
	}

}
