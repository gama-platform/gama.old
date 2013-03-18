/**
 * Created by drogoul, 18 avr. 2012
 * 
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.Facets;
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
		IType t = vd.getTypeNamed(vd.getName());
		if ( t != Types.NO_TYPE ) {
			String species = t.isSpeciesType() ? "species" : "type";
			vd.flagError(vd.getName() + " is a " + species + " name. " + type,
				IGamlIssue.IS_A_TYPE, NAME, vd.getName());
		}
	}

	public static void assertNameIsNotType(final IDescription vd) {
		String type =
			"It cannot be used as a " +
				(vd instanceof VariableDescription ? "variable" : vd.getKeyword()) + " name.";
		IType t = vd.getTypeNamed(vd.getName());
		if ( t == Types.NO_TYPE || t.isSpeciesType() ) { return; }
		vd.flagError(vd.getName() + " is a type name. " + type, IGamlIssue.IS_A_TYPE, NAME,
			vd.getName());
	}

	public static void assertFacetsAreOfType(final IDescription vd, final IType type,
		final String ... facets) {
		for ( String s : facets ) {
			IExpression expr = vd.getFacets().getExpr(s);
			if ( expr == null ) {
				continue;
			}
			if ( expr.getType() != type && expr.getType() != Types.NO_TYPE && type != Types.NO_TYPE ) {
				vd.flagWarning("Facet " + s + " of type " + expr.getType().toString() +
					" should be of type " + type.toString(), IGamlIssue.SHOULD_CAST, s,
					type.toString());
			}
		}
	}

	public static void assertNameIsNotReserved(final IDescription vd) {
		String name = vd.getName();
		String type =
			"It cannot be used as a " +
				(vd instanceof VariableDescription ? "variable" : vd.getKeyword()) + " name.";
		if ( name == null ) {
			vd.flagError("The attribute 'name' is missing", IGamlIssue.MISSING_NAME);
		} else if ( IExpressionCompiler.RESERVED.contains(name) ) {
			vd.flagError(name + " is a reserved keyword. " + type + " Reserved keywords are: " +
				IExpressionCompiler.RESERVED, IGamlIssue.IS_RESERVED, NAME, name);
		}
	}

	public static void assertCanBeAmong(final IDescription vd, final IType type, final Facets facets) {
		IExpression amongExpression = facets.getExpr(AMONG);
		if ( amongExpression != null && type != amongExpression.getContentType() ) {
			vd.flagError("Variable " + vd.getName() + " of type " + type.toString() +
				" cannot be chosen among " + amongExpression.toGaml(), IGamlIssue.NOT_AMONG, AMONG);
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
					type = expr.getType();
					firstValueFacet = s;
				} else {
					if ( type != expr.getType() ) {
						vd.flagWarning("The types of  facets '" + s + "' and '" + firstValueFacet +
							"' are not the same", IGamlIssue.SHOULD_CAST, s, type.toString());
					}
				}
			}
		}
	}

	public static void assertValueOrFunctionIsNotConst(final VariableDescription vd) {
		Facets ff = vd.getFacets();
		if ( vd.isNotModifiable() ) {
			if ( ff.containsKey(CONST) && ff.getLabel(CONST).equals(TRUE) ) {
				if ( ff.containsKey(VALUE) | ff.containsKey(UPDATE) ) {
					vd.flagWarning(
						"A constant variable cannot have an update value (use init or <- instead)",
						IGamlIssue.REMOVE_CONST, UPDATE);
				} else if ( ff.containsKey(FUNCTION) ) {
					vd.flagError(
						"A constant variable cannot be a function (use init or <- instead)",
						IGamlIssue.REMOVE_CONST, FUNCTION);
				}
			}
		}
	}

	public static void assertCanBeFunction(final VariableDescription vd) {
		Facets ff = vd.getFacets();
		if ( ff.containsKey(FUNCTION) &&
			(ff.containsKey(INIT) || ff.containsKey(UPDATE) || ff.containsKey(VALUE)) ) {
			vd.flagError("A function cannot have an 'init' or 'update' facet",
				IGamlIssue.REMOVE_VALUE, FUNCTION);
		}
	}

	public static void assertCanBeParameter(final VariableDescription vd) {
		String p = "Parameter '" + vd.getParameterName() + "' ";
		// Facets facets = new Facets();
		Facets facets = vd.getFacets();
		if ( facets.equals(KEYWORD, PARAMETER) ) {

			String varName = facets.getLabel(VAR);
			VariableDescription targetedVar = vd.getWorldSpecies().getVariable(varName);
			if ( targetedVar == null ) {
				vd.flagError(p + "cannot refer to the non-global variable " + varName,
					IGamlIssue.UNKNOWN_VAR, IKeyword.VAR);
				return;
			}
			if ( !vd.getType().equals(Types.NO_TYPE) &&
				vd.getType().id() != targetedVar.getType().id() ) {
				vd.flagError(p + "type must be the same as that of " + varName,
					IGamlIssue.UNMATCHED_TYPES, IKeyword.TYPE);
			}
			assertCanBeAmong(vd, targetedVar.getType(), facets);
			assertFacetsAreOfType(vd, targetedVar.getType(), valueFacetsArray);
			assertValueFacetsAreTheSameType(vd, facets);
		}

		IExpression min = facets.getExpr(MIN);
		IExpression max = facets.getExpr(MAX);
		if ( facets.getExpr(FUNCTION) != null ) {
			vd.flagError("Functions cannot be used as parameters", IGamlIssue.REMOVE_FUNCTION,
				FUNCTION);
		}
		if ( min != null && !min.isConst() ) {
			vd.flagError(p + " min value must be constant", IGamlIssue.NOT_CONST, MIN);
		}
		if ( max != null && !max.isConst() ) {
			vd.flagError(p + " max value must be constant", IGamlIssue.NOT_CONST, MAX);
		}
		if ( facets.getExpr(INIT) == null ) {
			vd.flagError(p + " must have an initial value", IGamlIssue.NO_INIT, null, vd.getType()
				.toString());
		} else if ( !facets.getExpr(INIT).isConst() ) {
			vd.flagError(p + "initial value must be constant", IGamlIssue.NOT_CONST, INIT);
		}
		IExpression updateExpression = facets.getExpr(UPDATE, facets.getExpr(VALUE));
		if ( updateExpression != null ) {
			vd.flagError(p + "cannot have an 'update' or 'value' facet", IGamlIssue.REMOVE_VALUE);
		} else if ( vd.isNotModifiable() ) {
			vd.flagError(p + " cannot be declared as constant ", IGamlIssue.REMOVE_CONST);
		}
	}

}
