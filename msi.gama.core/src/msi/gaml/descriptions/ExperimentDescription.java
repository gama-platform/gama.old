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
package msi.gaml.descriptions;

import msi.gaml.factories.IChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import org.eclipse.emf.ecore.EObject;

public class ExperimentDescription extends SpeciesDescription {

	// final ModelDescription model;

	// We assume experiments are firstly created *within* a model, in which case we can gather the enclosing argument
	// and keep it for when the relationship will be reversed (i.e. when the model will be *inside* the experiment)
	public ExperimentDescription(final String keyword, final IDescription enclosing, final IChildrenProvider cp,
		final EObject source, final Facets facets) {
		super(keyword, null, enclosing, null, cp, source, facets);
		// ModelDescription.ROOT.getTypesManager().getSpecies(IKeyword.EXPERIMENT),
		// setParent(ModelDescription.ROOT.getTypesManager().getSpecies(IKeyword.EXPERIMENT));
	}

	@Override
	protected void addVariable(final VariableDescription var) {
		if ( !var.getKeyword().equals(PARAMETER) ) {
			if ( var.getName().equals(SIMULATION) ) {
				// We are dealing with the built-in variable. We gather the precise type of the model itself and pass it
				// to the variable (instead of its generic "agent" type).
				final IType t = enclosing.getModelDescription().getType();
				var.setType(t);
			}
			super.addVariable(var);
		}
	}

	@Override
	public String getTitle() {
		return getName();
	}

	@Override
	public boolean isExperiment() {
		return true;
	}
	//
	// @Override
	// public void inheritFromParent() {
	// super.inheritFromParent();
	// GuiUtils.debug("ExperimentDescription.inheritFromParent: " + getName() + " from " + parent.getName());
	// }

}
