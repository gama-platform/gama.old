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
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 26 nov. 2008
 * 
 * @todo Description
 */
@handles({ ISymbolKind.VARIABLE })
public class VariableFactory extends SymbolFactory {

	@Override
	protected String getKeyword(final ISyntacticElement cur) {
		if ( cur.getKeyword().equals(PARAMETER) ) { return super.getKeyword(cur); }
		String keyword = cur.getLabel(TYPE);
		if ( keyword == null ) {
			keyword = cur.getKeyword();
		}
		return keyword;
	}

	@Override
	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> children, final IDescription superDesc,
		final SymbolMetaDescription md) {
		if ( keyword.equals(SIGNAL) ) {
			buildSignalDescription(source, keyword, superDesc, md);
		}
		return new VariableDescription(keyword, superDesc, source.getFacets(), children, source, md);
	}

	private void buildSignalDescription(final ISyntacticElement source, final String keyword,
		final IDescription superDesc, final SymbolMetaDescription md) {
		Facets facets = source.getFacets();
		String name = facets.getLabel(NAME);
		String env = facets.getLabel(ENVIRONMENT);
		if ( env == null ) {
			superDesc.flagError("No environment defined for signal " + name);
			return;
		}
		String decay = facets.getLabel(DECAY);
		if ( decay == null ) {
			decay = "0.1";
		}

		final String value = name + " < 0.1 ? 0.0 :" + name + " * ( 1 - " + decay + ")";
		VariableDescription vd =
			(VariableDescription) createDescription(new StringBasedStatementDescription(
				IType.FLOAT_STR, new Facets(NAME, name, TYPE, IType.FLOAT_STR, UPDATE, value, MIN,
					"0")), superDesc, null);

		SpeciesDescription environment = superDesc.getSpeciesDescription(env);
		if ( superDesc.getSpeciesDescription(env) == null || !environment.isGrid() ) {
			superDesc.flagError("Environment " + env + " of signal " + name +
				" cannot be determined.");
		}
		environment.addChild(vd);
	}

	public void addSpeciesNameAsType(final String name) {
		registeredSymbols.put(name, registeredSymbols.get(AGENT));
	}

	@Override
	protected void compileFacet(final String tag, final IDescription sd) {
		super.compileFacet(tag, sd);
		if ( valueFacets.contains(tag) ) {
			IExpression expr = sd.getFacets().getExpr(tag);
			IType t = sd.getContentType();
			if ( (t == null || t == Types.NO_TYPE) && expr != null ) {
				((VariableDescription) sd).setContentType(expr.getContentType());
			}
		}
	}

	@Override
	protected void privateValidate(final IDescription desc) {
		super.privateValidate(desc);
		VariableDescription vd = (VariableDescription) desc;
		assertCanBeFunction(vd);

		if ( !vd.getFacets().equals(KEYWORD, PARAMETER) ) {
			assertCanBeAmong(vd, vd.getType(), vd.getFacets());
			assertValueFacetsAreTheSameType(vd, vd.getFacets());
		} else if ( vd.isParameter() ) {
			assertCanBeParameter(vd);
		}
		// assertCanBeExperimentParameter(vd);
	}

	private void assertCanBeAmong(final VariableDescription vd, final IType type,
		final Facets facets) {
		IExpression amongExpression = facets.getExpr(AMONG);
		if ( amongExpression != null && type.id() != amongExpression.getContentType().id() ) {
			vd.flagError("Variable " + vd.getName() + " of type " + type.toString() +
				" cannot be chosen among " + amongExpression.toGaml(), AMONG);
		}
	}

	private void assertValueFacetsAreTheSameType(final VariableDescription vd, final Facets facets) {
		IType type = null;
		String firstValueFacet = null;
		for ( String s : valueFacets ) {
			IExpression expr = facets.getExpr(s);
			if ( expr != null ) {
				if ( type == null ) {
					type = expr.type();
					firstValueFacet = s;
				} else {
					if ( type != expr.type() ) {
						vd.flagWarning("The types of the facets " + s + " and " + firstValueFacet +
							" are not compatible", s);
					}
				}
			}
		}
	}

	private void assertCanBeFunction(final VariableDescription vd) {
		Facets ff = vd.getFacets();
		if ( ff.containsKey(FUNCTION) &&
			(ff.containsKey(INIT) || ff.containsKey(UPDATE) || ff.containsKey(VALUE)) ) {
			vd.flagError("A function cannot have an 'init' or 'update' facet", FUNCTION);
		}
	}

	private void assertCanBeParameter(final VariableDescription vd) {
		String p = "Parameter '" + vd.getParameterName() + "' ";
		Facets facets = new Facets();
		Facets paramFacets = vd.getFacets();
		if ( paramFacets.equals(KEYWORD, PARAMETER) ) {
			// We are validating an experiment parameter so we fusion the facets of the targeted var
			// and those of the parameter
			String varName = paramFacets.getLabel(VAR);
			VariableDescription targetedVar = vd.getWorldSpecies().getVariable(varName);
			if ( targetedVar == null ) {
				vd.flagError(p + "cannot refer to the non-global variable " + varName, IKeyword.VAR);
				return;
			}
			if ( !vd.getType().equals(Types.NO_TYPE) &&
				vd.getType().id() != targetedVar.getType().id() ) {
				vd.flagError(p + "type must be the same as that of " + varName, IKeyword.TYPE);
			}
			facets.putAll(targetedVar.getFacets());
			facets.putAll(paramFacets);
			assertCanBeAmong(vd, targetedVar.getType(), facets);
			assertValueFacetsAreTheSameType(vd, facets);
		} else {
			facets = paramFacets;
		}

		IExpression min = facets.getExpr(MIN);
		IExpression max = facets.getExpr(MAX);
		if ( facets.getExpr(FUNCTION) != null ) {
			vd.flagError("Functions cannot be used as parameters", FUNCTION);
		}
		if ( min != null && !min.isConst() ) {
			vd.flagError(p + " min value must be constant", MIN);
		}
		if ( max != null && !max.isConst() ) {
			vd.flagError(p + " max value must be constant", MAX);
		}
		if ( facets.getExpr(INIT) == null ) {
			vd.flagError(p + " must have an initial value");
		} else if ( !facets.getExpr(INIT).isConst() ) {
			vd.flagError(p + "initial value must be constant");
		}
		IExpression updateExpression = facets.getExpr(UPDATE, facets.getExpr(VALUE));
		if ( updateExpression != null ) {
			vd.flagError(p + "cannot have an 'update' or 'value' facet");
		} else if ( vd.isNotModifiable() ) {
			vd.flagError(p + " cannot be declared as constant ");
		}
	}

}
