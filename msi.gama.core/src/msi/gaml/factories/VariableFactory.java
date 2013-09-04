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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.factories.DescriptionValidator.*;
import static msi.gaml.factories.VariableValidator.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.compilation.SyntacticElement;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 26 nov. 2008
 * 
 * @todo Description
 */
@factory(handles = { ISymbolKind.Variable.CONTAINER, ISymbolKind.Variable.NUMBER, ISymbolKind.Variable.REGULAR,
	ISymbolKind.Variable.SIGNAL, ISymbolKind.PARAMETER })
public class VariableFactory extends SymbolFactory {

	public VariableFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected IDescription buildDescription(final ISyntacticElement source, final Facets facets,
		final IChildrenProvider cp, final IDescription superDesc, final SymbolProto md) {
		final String keyword = source.getKeyword();
		if ( keyword.equals(SIGNAL) ) {
			buildSignalDescription(source, facets, keyword, superDesc);
		} else if ( keyword.equals(PARAMETER) ) {
			// We copy the relevant facets from the targeted var of the parameter
			final String varName = facets.getLabel(VAR);
			final VariableDescription targetedVar = superDesc.getModelDescription().getVariable(varName);
			if ( targetedVar != null ) {
				facets.complementWith(targetedVar.getFacets());
			}
			// We should remove the facets that are not relevant to parameters (see if another way
			// is possible)
			final Set<String> possibleFacets = md.getPossibleFacets().keySet();
			for ( int i = 0; i < facets.entrySet().length; i++ ) {
				final Facet f = facets.entrySet()[i];
				if ( f != null && !possibleFacets.contains(f.getKey()) ) {
					facets.entrySet()[i] = null;
				}
			}
		}
		return new VariableDescription(keyword, superDesc, cp, source.getElement(), facets);
	}

	private void buildSignalDescription(final ISyntacticElement source, final Facets facets, final String keyword,
		final IDescription superDesc) {
		final String name = facets.getLabel(NAME);
		final String env = facets.getLabel(ENVIRONMENT);
		if ( env == null ) {
			superDesc.error("No environment defined for signal " + name, IGamlIssue.NO_ENVIRONMENT);
			return;
		}
		String decay = facets.getLabel(DECAY);
		if ( decay == null ) {
			decay = "0.1";
		}
		final String value = name + " < 0.1 ? 0.0 :" + name + " * ( 1 - " + decay + ")";
		final VariableDescription vd =
			(VariableDescription) create(new SyntacticElement(IKeyword.FLOAT, new Facets(NAME, name, TYPE,
				IKeyword.FLOAT, UPDATE, value, MIN, "0.0")), superDesc, null);
		final SpeciesDescription environment = superDesc.getSpeciesDescription(env);
		if ( environment == null || !environment.isGrid() ) {
			superDesc.error("Environment " + env + " of signal " + name + " cannot be determined.",
				IGamlIssue.UNKNOWN_ENVIRONMENT, ENVIRONMENT, env);
		}
		if ( environment != null ) {
			environment.addChild(vd);
		}
	}

	@Override
	protected void compileFacet(final String tag, final IDescription sd, final SymbolProto md) {
		super.compileFacet(tag, sd, md);
		if ( valueFacetsList.contains(tag) ) {
			final IExpression expr = sd.getFacets().getExpr(tag);
			final IType t = sd.getContentType();
			if ( (t == null || t == Types.NO_TYPE) && expr != null ) {
				((VariableDescription) sd).setContentType(expr.getContentType());
			}
		}
	}

	@Override
	protected VariableDescription privateValidate(final IDescription desc) {
		super.privateValidate(desc);
		final VariableDescription vd = (VariableDescription) desc;
		assertNameIsNotType(vd);
		assertNameIsNotReserved(vd);
		assertCanBeFunction(vd);
		assertValueOrFunctionIsNotConst(vd);
		if ( !vd.getFacets().equals(KEYWORD, PARAMETER) ) {
			assertCanBeAmong(vd, vd.getType(), vd.getFacets());
			assertValueFacetsAreTheSameType(vd, vd.getFacets());
			assertFacetsAreOfType(vd, vd.getType(), valueFacetsArray);
		} else if ( vd.isParameter() ) {
			assertCanBeParameter(vd);
		}
		return vd;
	}

}
