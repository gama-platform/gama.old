/*******************************************************************************************************
 *
 * PopulationEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.ValuedDisplayOutputFactory;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.species.ISpecies;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class PopulationEditor.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PopulationEditor extends AbstractEditor<IContainer> {

	/** The population displayer. */
	Text populationDisplayer;

	/**
	 * Instantiates a new population editor.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param param the param
	 * @param l the l
	 */
	PopulationEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	@Override
	public Control createCustomParameterControl(final Composite compo) {
		populationDisplayer = new Text(compo, SWT.READ_ONLY);
		populationDisplayer.setEnabled(false);
		final GridData data = new GridData(GridData.FILL, GridData.CENTER, true, false);
		populationDisplayer.setLayoutData(data);
		return populationDisplayer;
	}

	@Override
	protected void displayParameterValue() {
		internalModification = true;
		final String s = currentValue instanceof IPopulation ? ((IPopulation) currentValue).getName()
				: currentValue == null ? "nil" : currentValue instanceof ISpecies
						? currentValue.getGamlType().toString() : currentValue.serialize(true);
		populationDisplayer.setText(s);
		populationDisplayer.setToolTipText(s);
		internalModification = false;
	}

	@Override
	protected void applyBrowse() {
		if (currentValue instanceof Collection) { ValuedDisplayOutputFactory.browse((Collection) currentValue); }
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { BROWSE };
	}

}
