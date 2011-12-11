/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
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
package msi.gama.factories;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.*;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.*;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;

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
			desc.addTemp(ISymbol.MYSELF, t, t, factory);
			desc.setSuperDescription(desc.getModelDescription()
				.getSpeciesDescription(actualSpecies));
		}
		super.privateCompileChildren(desc, cs, factory);
	}

	private void compileArgs(final IDescription cd, final ISymbol ce,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		Arguments ca = new Arguments();
		boolean isCreate =
			cd.getKeyword().equals(ISymbol.CREATE) || cd.getKeyword().equals(ISymbol.DO);
		Facets cFacets;
		for ( IDescription sd : ((CommandDescription) cd).getArgs() ) {
			cFacets = sd.getFacets();
			String name = sd.getName();
			// TOUJOURS VRAI AU-DESSUS ????
			// Ajour de getSuperDescription() pour éviter que les arguments soient compilés dans le
			// contexte de la commande... A vérifier.
			IExpression e =
				cFacets.compile(ISymbol.VALUE, cd.getSuperDescription(),
					cFacets.compile(ISymbol.DEFAULT, cd.getSuperDescription(), factory), factory);
			ca.put(name, e);
			IType type = sd.getTypeOf(cFacets.getString(ISymbol.TYPE));
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
		String type = sd.getFacets().getString(ISymbol.TYPE);
		String contentType = sd.getFacets().getString(ISymbol.AS);
		if ( contentType == null ) {
			contentType = sd.getFacets().getString(ISymbol.SPECIES);
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
		IExpression speciesFacet = ce.getFacet(ISymbol.SPECIES);
		if ( speciesFacet != null ) {
			IType t = speciesFacet.getContentType();
			if ( t.isSpeciesType() ) {
				type = t;
			}
		}
		if ( type == null ) {
			speciesFacet = ce.getFacet(ISymbol.AS);
			if ( speciesFacet != null ) {
				IType t = speciesFacet.getContentType();
				if ( t.isSpeciesType() ) {
					type = t;
				}
			}
		}
		if ( type == null ) {
			speciesFacet = ce.getFacet(ISymbol.TARGET);
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
