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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

@facets(value = { @facet(name = IKeyword.NAME, type = IType.STRING, optional = true),
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
		@facet(name = IKeyword.FRAMERATE, type = IType.INT, optional = true) }, omissible = IKeyword.VAR)
@symbol(name = { IKeyword.EXPORT }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true, concept = {
		IConcept.EXPERIMENT })
@inside(kinds = { ISymbolKind.EXPERIMENT })

public class ExperimentExport extends Symbol {

	final IExpression listenedVariable;

	public ExperimentExport(final IDescription sd) throws GamaRuntimeException {
		super(sd);
		this.listenedVariable = getFacet(IKeyword.VAR);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		// TODO Auto-generated method stub

	}

}
