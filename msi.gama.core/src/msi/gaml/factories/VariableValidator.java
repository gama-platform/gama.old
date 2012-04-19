/**
 * Created by drogoul, 18 avr. 2012
 * 
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IUnits;
import msi.gaml.commands.Facets;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * The class VariableValidator.
 * 
 * @author drogoul
 * @since 18 avr. 2012
 * 
 */
public class VariableValidator extends DescriptionValidator {

	/**
	 * @param vd
	 */
	public static void assertNameIsNotTypeOrSpecies(final IDescription vd) {
		String type =
			"It cannot be used as a " +
				(vd instanceof VariableDescription ? "variable" : vd.getKeyword()) + " name.";
		if ( vd.getTypeOf(vd.getName()) != Types.NO_TYPE ) {
			vd.flagError(vd.getName() + " is a type name. " + type);
		}
	}

	public static void assertFacetsAreOfType(final IDescription vd, final IType type,
		final String ... facets) {
		for ( String s : facets ) {
			IExpression expr = vd.getFacets().getExpr(s);
			if ( expr == null ) {
				continue;
			}
			if ( expr.type() != type ) {
				vd.flagWarning("Facet " + s + " of type " + expr.type().toString() +
					" should be of type " + type.toString(), s);
			}
		}
	}

	public static void assertNameIsNotReserved(final IDescription vd) {
		String name = vd.getName();
		String type =
			"It cannot be used as a " +
				(vd instanceof VariableDescription ? "variable" : vd.getKeyword()) + " name.";
		if ( name == null ) {
			vd.flagError("The attribute 'name' is missing");
		} else if ( IExpressionParser.RESERVED.contains(name) ) {
			vd.flagError(name + " is a reserved keyword. " + type + " Reserved keywords are: " +
				IExpressionParser.RESERVED);
		}/*
		 * else if ( IExpressionParser.BINARIES.containsKey(name) ) {
		 * flagError(name + " is a binary operator name. It cannot be used as a variable name");
		 * } else if ( IExpressionParser.UNARIES.containsKey(name) ) {
		 * flagError(name + " is a unary operator name. It cannot be used as a variable name");
		 * }
		 */else if ( IUnits.UNITS.containsKey(name) ) {
			vd.flagError(name + " is a unit name. " + type + " Units in GAML are :" +
				String.valueOf(IUnits.UNITS.keySet()));
		}
	}

	public static void assertCanBeAmong(final IDescription vd, final IType type, final Facets facets) {
		IExpression amongExpression = facets.getExpr(AMONG);
		if ( amongExpression != null && type != amongExpression.getContentType() ) {
			vd.flagError("Variable " + vd.getName() + " of type " + type.toString() +
				" cannot be chosen among " + amongExpression.toGaml(), AMONG);
		}
	}

	public static void assertValueFacetsAreTheSameType(final VariableDescription vd,
		final Facets facets) {
		IType type = null;
		String firstValueFacet = null;
		for ( String s : valueFacetsArray ) {
			IExpression expr = facets.getExpr(s);
			if ( expr != null ) {
				if ( type == null ) {
					type = expr.type();
					firstValueFacet = s;
				} else {
					if ( type != expr.type() ) {
						vd.flagWarning("The types of  facets '" + s + "' and '" + firstValueFacet +
							"' are not the same", s);
					}
				}
			}
		}
	}

	public static void assertCanBeFunction(final VariableDescription vd) {
		Facets ff = vd.getFacets();
		if ( ff.containsKey(FUNCTION) &&
			(ff.containsKey(INIT) || ff.containsKey(UPDATE) || ff.containsKey(VALUE)) ) {
			vd.flagError("A function cannot have an 'init' or 'update' facet", FUNCTION);
		}
	}

	public static void assertCanBeParameter(final VariableDescription vd) {
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
			assertFacetsAreOfType(vd, targetedVar.getType(), valueFacetsArray);
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
