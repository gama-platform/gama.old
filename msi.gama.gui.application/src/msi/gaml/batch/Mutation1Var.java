/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.batch;

import java.util.List;
import msi.gama.interfaces.IParameter;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public class Mutation1Var implements Mutation {

	public Mutation1Var() {

	}

	@Override
	public Chromosome mutate(final Chromosome chromosome, final List<IParameter.Batch> variables)
		throws GamaRuntimeException {
		final Chromosome chromoMut = new Chromosome(chromosome);

		final int indexMut = GAMA.getRandom().between(0, chromoMut.getGenes().length - 1);
		final String varStr = chromoMut.getPhenotype()[indexMut];
		IParameter.Batch var = null;
		for ( IParameter.Batch p : variables ) {
			if ( p.getName().equals(varStr) ) {
				var = p;
				break;
			}
		}
		// TODO Lourd et pas du tout optimisé.
		if ( var != null ) {
			chromoMut.setGene(var, indexMut);
		}
		return chromoMut;
	}

}
