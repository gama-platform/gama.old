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
package msi.gama.gui.parameters;

import java.util.Collection;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.GamaIcons;
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
		populationInspector.setImage(GamaIcons.action_browse);
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
