/*********************************************************************************************
 * 
 *
 * 'PopulationEditor.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.Collection;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.util.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class PopulationEditor extends AbstractEditor {

	// private Button agentChooser;
	Label populationDisplayer;
	Button populationInspector;

	PopulationEditor(final IParameter param) {
		super(param);
	}

	PopulationEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	PopulationEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	PopulationEditor(final Composite parent, final String title, final Object value,
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
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 1;
		layout.marginWidth = 1;
		comp.setLayout(layout);
		populationDisplayer = new Label(comp, SWT.NONE);

		populationInspector = new Button(comp, SWT.FLAT | SWT.PUSH);
		populationInspector.setAlignment(SWT.CENTER);
		populationInspector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( currentValue instanceof Collection ) {
					InspectDisplayOutput.browse((Collection) currentValue);
				}
			}
		});
		populationInspector.setImage(IGamaIcons.MENU_BROWSE.image());
		populationInspector.setText("Browse");
		return populationDisplayer;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		populationDisplayer.setText(currentValue instanceof IPopulation ? ((IPopulation) currentValue).getName()
			: currentValue instanceof IContainer ? ((IContainer) currentValue).toGaml() : "nil");
		internalModification = false;
	}

	@Override
	public Control getEditorControl() {
		return populationDisplayer;
	}

}
