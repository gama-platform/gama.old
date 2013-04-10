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
package msi.gama.outputs.layers;

import java.util.List;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IAspect;
import msi.gaml.types.IType;

/**
 * Written by Marilleau Modified on 16 novembre 2012
 * @todo Description
 * 
 */
@symbol(name = IKeyword.EVENT, kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = { IKeyword.DISPLAY })
@facets(value = {@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.ACTION, type = IType.STRING, optional = false)}, omissible = IKeyword.NAME)
public class EventLayerStatement extends AgentLayerStatement {
	public static int MOUSE_PRESSED = 0;
	public static int MOUSE_RELEASED = 1;
	
	private int actionType = -1;
	
	public EventLayerStatement(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(/* context, */desc);
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		IExpression eventType = getFacet(IKeyword.NAME);
		IExpression actionName = getFacet(IKeyword.ACTION);
		if ( eventType == null || actionName == null ) 
		{ 
			throw new GamaRuntimeException("Missing properties " + IKeyword.NAME + " and " + IKeyword.ACTION);
		}
	}

	
	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);

	}

	@Override
	public boolean agentsHaveChanged() {
		return true;
		// return population.populationHasChanged();
	}

	@Override
	public List<? extends IAgent> computeAgents(final IScope sim) {
		return GamaList.EMPTY_LIST;
	}

	@Override
	public short getType() {
		return EVENT;
	}
	
	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
		return "SpeciesDisplayLayer species: " + this.getFacet(IKeyword.NAME).literalValue();
	}
}
