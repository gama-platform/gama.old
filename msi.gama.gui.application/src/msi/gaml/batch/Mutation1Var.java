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
