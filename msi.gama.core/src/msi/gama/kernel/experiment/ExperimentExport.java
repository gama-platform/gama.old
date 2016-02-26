/*********************************************************************************************
 * 
 *
 * 'ExperimentExport.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.*;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

@facets(value = { @facet(name = IKeyword.NAME, type = IType.STRING, optional = true),
	@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
	@facet(name = IKeyword.FRAMERATE, type = IType.INT, optional = true)}, omissible = IKeyword.VAR)
@symbol(name = { IKeyword.EXPORT }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true, concept = { IConcept.EXPERIMENT })
@inside(kinds = { ISymbolKind.EXPERIMENT })
 
public class ExperimentExport extends Symbol  {

	final IExpression listenedVariable;

	public ExperimentExport(final IDescription sd) throws GamaRuntimeException
	{
		super(sd);
		this.listenedVariable=getFacet(IKeyword.VAR);
	}

	@Override
	public void setChildren(List<? extends ISymbol> children) {
		// TODO Auto-generated method stub
		
	}

	

}
