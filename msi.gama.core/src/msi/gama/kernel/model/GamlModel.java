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
package msi.gama.kernel.model;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.metamodel.topology.*;
import msi.gama.outputs.OutputManager;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.util.GamaList;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 19 mai 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.MODEL }, kind = ISymbolKind.MODEL)
@with_sequence
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = IKeyword.VERSION, type = IType.ID, optional = true),
	@facet(name = IKeyword.AUTHOR, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
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
				this.worldSpecies = (ISpecies) s;
				break;
			}
		}

		for ( ISymbol s : children ) {
			if ( s instanceof IExperiment ) {
				addExperiment((IExperiment) s);
				experiments.add((IExperiment) s);
			} else if ( s instanceof OutputManager ) {
				forExperiment.add(s);
			} else if ( s instanceof IEnvironment ) {
				setModelEnvironment((ModelEnvironment) s);
			}
		}
		// Add the default outputs, environment, etc. to all experiments
		for ( IExperiment e : experiments ) {
			e.setChildren(forExperiment);
		}

	}
}
