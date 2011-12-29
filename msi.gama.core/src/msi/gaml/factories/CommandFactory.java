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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 8 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.SEQUENCE_COMMAND, ISymbolKind.SINGLE_COMMAND, ISymbolKind.BEHAVIOR,
	ISymbolKind.ACTION })
public class CommandFactory extends SymbolFactory {

	@Override
	protected CommandDescription buildDescription(final ISyntacticElement source, final String kw,
		final List<IDescription> commands, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) throws GamlException {
		return new CommandDescription(kw, superDesc, facets, commands, md.hasScope(), md.hasArgs(),
			source);
	}

	@Override
	protected void privateCompileChildren(final IDescription desc, final ISymbol cs,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		if ( ((CommandDescription) desc).hasArgs() ) {
			compileArgs(desc, cs, factory);
		}
		String actualSpecies = computeSpecies(cs);

		if ( actualSpecies != null ) {
			IType t = desc.getSpeciesContext().getType();
			desc.addTemp(IKeyword.MYSELF, t, t, factory);
			desc.setSuperDescription(desc.getModelDescription()
				.getSpeciesDescription(actualSpecies));
		}
		super.privateCompileChildren(desc, cs, factory);
	}

	private void compileArgs(final IDescription cd, final ISymbol ce,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		Arguments ca = new Arguments();
		boolean isCreate =
			cd.getKeyword().equals(IKeyword.CREATE) || cd.getKeyword().equals(IKeyword.DO);
		Facets cFacets;
		for ( IDescription sd : ((CommandDescription) cd).getArgs() ) {
			cFacets = sd.getFacets();
			String name = sd.getName();
			// TOUJOURS VRAI AU-DESSUS ????
			// Ajour de getSuperDescription() pour éviter que les arguments soient compilés dans le
			// contexte de la commande... A vérifier.
			IExpression e =
				cFacets.compile(IKeyword.VALUE, cd.getSuperDescription(),
					cFacets.compile(IKeyword.DEFAULT, cd.getSuperDescription(), factory), factory);
			ca.put(name, e);
			IType type = sd.getTypeOf(cFacets.getString(IKeyword.TYPE));
			if ( type == Types.NO_TYPE && e != null ) {
				type = e.type();
			}
			if ( !isCreate ) {
				((CommandDescription) cd).addTemp(name, type,
					e == null ? Types.NO_TYPE : e.getContentType(), factory);
			}
			// Special case for create and do, as the "arguments" passed should not be part of the
			// context
		}
		((ICommand.WithArgs) ce).setFormalArgs(ca);
	}

	@Override
	protected IExpression compileFacet(final String tag, final IDescription sd,
		final SymbolMetaDescription md, final IExpressionFactory factory) throws GamlException,
		GamaRuntimeException {
		// String name = sd.getFacet(ISymbol.NAME);
		String type = sd.getFacets().getString(IKeyword.TYPE);
		String contentType = sd.getFacets().getString(IKeyword.AS);
		if ( contentType == null ) {
			contentType = sd.getFacets().getString(IKeyword.SPECIES);
		}
		if ( type == null && contentType != null ) {
			type = IType.LIST_STR;
		}
		IExpression exp =
			((CommandDescription) sd).addNewTempIfNecessary(tag, md, type, contentType, factory);

		if ( exp == null ) {
			exp = super.compileFacet(tag, sd, md, factory);
		}
		return exp;
	}

	private String computeSpecies(final ISymbol ce) {
		IType type = null;
		IExpression speciesFacet = ce.getFacet(IKeyword.SPECIES);
		if ( speciesFacet != null ) {
			IType t = speciesFacet.getContentType();
			if ( t.isSpeciesType() ) {
				type = t;
			}
		}
		if ( type == null ) {
			speciesFacet = ce.getFacet(IKeyword.AS);
			if ( speciesFacet != null ) {
				IType t = speciesFacet.getContentType();
				if ( t.isSpeciesType() ) {
					type = t;
				}
			}
		}
		if ( type == null ) {
			speciesFacet = ce.getFacet(IKeyword.TARGET);
			if ( speciesFacet != null ) {
				IType t = speciesFacet.getContentType();
				if ( t.isSpeciesType() ) {
					type = t;
				}
			}
		}
		return type == null ? null : type.getSpeciesName();
	}

}
