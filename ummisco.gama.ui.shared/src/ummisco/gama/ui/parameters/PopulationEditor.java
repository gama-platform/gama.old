/*********************************************************************************************
 *
 * 'PopulationEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PopulationEditor extends AbstractEditor<IContainer> {

	Text populationDisplayer;

	PopulationEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	PopulationEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	PopulationEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	PopulationEditor(final IScope scope, final Composite parent, final String title, final Object value,
			final EditorListener<IContainer> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
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
	public Control getEditorControl() {
		return populationDisplayer;
	}

	@Override
	protected void applyBrowse() {
		if (currentValue instanceof Collection) {
			ValuedDisplayOutputFactory.browse((Collection) currentValue);
		}
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { BROWSE };
	}

}
