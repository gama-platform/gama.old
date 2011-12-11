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
package msi.gaml.kernel;

import java.util.List;

import msi.gama.environment.ModelEnvironment;
import msi.gama.interfaces.*;
import msi.gama.kernel.AbstractModel;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.outputs.OutputManager;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 19 mai 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { ISymbol.MODEL }, kind = ISymbolKind.MODEL)
@with_sequence
@facets({ @facet(name = ISymbol.NAME, type = IType.ID, optional = true),
	@facet(name = IModel.VERSION, type = IType.ID, optional = true),
	@facet(name = IModel.AUTHOR, type = IType.ID, optional = true) })
public class GamlModel extends AbstractModel {

	public GamlModel(final IDescription desc) {
		super(desc);
		setName(desc.getName());
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) throws GamlException {
		GamaList forExperiment = new GamaList();
		GamaList<IExperiment> experiments = new GamaList();
		
		// add the world_species first
		for ( ISymbol s : children ) {
			if ( s instanceof ISpecies ) {
				this.worldSpecies = ((ISpecies) s);
				break;
			}
		}
		
		for ( ISymbol s : children ) {
			if ( s instanceof IExperiment ) {
				addExperiment((IExperiment) s);
				experiments.add((IExperiment) s);
			} else if ( s instanceof OutputManager ) {
				forExperiment.add(s);
			} else if ( s instanceof ModelEnvironment ) {
				setModelEnvironment((ModelEnvironment) s);
			}
		}

		for ( IExperiment e : experiments ) {
			e.setChildren(forExperiment);
		}
		
		// Add the default outputs, environment, etc. to all experiments
		// IExperiment sim = getExperiment(IModel.DEFAULT_EXPERIMENT);
		// sim.setChildren(forExperiment);

	}
}
