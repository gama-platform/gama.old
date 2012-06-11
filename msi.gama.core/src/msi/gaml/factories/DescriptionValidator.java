/**
 * Created by drogoul, 18 avr. 2012
 * 
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * The class DescriptionValidator.
 * 
 * @author drogoul
 * @since 18 avr. 2012
 * 
 */
public class DescriptionValidator {

	public static String[] valueFacetsArray = new String[] { VALUE, INIT, FUNCTION, UPDATE, MIN,
		MAX };
	public static List<String> valueFacetsList = Arrays.asList(valueFacetsArray);

	/*
	 * Verification done after the facets have been compiled
	 */

	public static void verifyFacetsType(final IDescription desc) {
		SymbolProto smd = desc.getMeta();
		ModelDescription md = desc.getModelDescription();
		TypesManager tm = md.getTypesManager();
		for ( Map.Entry<String, IExpressionDescription> entry : desc.getFacets().entrySet() ) {
			if ( entry == null ) {
				continue;
			}
			String facetName = entry.getKey();
			IExpressionDescription ed = entry.getValue();
			if ( ed == null ) {
				continue;
			}
			IExpression expr = ed.getExpression();
			if ( expr == null ) {
				continue;
			}
			verifyFacetType(desc, facetName, expr, smd, md, tm);

		}
	}

	public static void verifyFacetType(final IDescription desc, final String facet,
		final IExpression expr, final SymbolProto smd, final ModelDescription md,
		final TypesManager tm) {
		FacetProto fmd = smd.getPossibleFacets().get(facet);
		if ( fmd == null ) { return; }

		// We have a multi-valued facet
		if ( fmd.values.length > 0 ) {
			verifyFacetIsInValues(desc, facet, expr, fmd.values);
			return;
		}
		// The facet is supposed to be a type (IType.TYPE_ID)
		List<String> types = fmd.types;
		if ( types.contains(IType.TYPE_ID) ) {
			verifyFacetIsAType(desc, facet, expr, tm);
			return;
		}

		if ( !fmd.isLabel ) {
			verifyFacetTypeIsCompatible(desc, facet, expr, types, tm);
		}
	}

	public static void verifyFacetTypeIsCompatible(final IDescription desc, final String facet,
		final IExpression expr, final List<String> types, final TypesManager tm) {
		boolean compatible = false;
		IType actualType = expr.getType();
		for ( String type : types ) {
			compatible = compatible || tm.get(type).isAssignableFrom(actualType);
			if ( compatible ) {
				break;
			}
		}
		if ( !compatible ) {
			desc.flagWarning("Facet '" + facet + "' is expecting " + types + " instead of " +
				actualType, IGamlIssue.SHOULD_CAST, facet, types.get(0));
		}

	}

	public static void verifyFacetIsAType(final IDescription desc, final String facet,
		final IExpression expr, final TypesManager tm) {
		String type = expr.literalValue();
		if ( tm.get(type) == Types.NO_TYPE && !IType.NONE_STR.equals(type) &&
			!IKeyword.SIGNAL.equals(type) ) {
			desc.flagError("Facet '" + facet + "' is expecting a type name. " + type +
				" is not a type name", IGamlIssue.NOT_A_TYPE, facet, type);
		}
	}

	public static void verifyFacetIsInValues(final IDescription desc, final String facet,
		final IExpression expr, final String[] values) {
		String s = expr.literalValue();
		boolean compatible = false;
		for ( String value : values ) {
			compatible = compatible || value.equals(s);
			if ( compatible ) {
				break;
			}
		}
		if ( !compatible ) {
			desc.flagError(
				"Facet '" + facet + "' is expecting a value among " + Arrays.toString(values) +
					" instead of " + s, facet);
		}
	}

	public static void assertNameIsUniqueInSuperDescription(final IDescription desc) {
		IDescription sd = desc.getSuperDescription();
		if ( sd == null ) { return; }
		for ( IDescription child : sd.getChildren() ) {
			if ( child.getMeta() == desc.getMeta() && child != desc ) {
				String name = child.getName();
				if ( name == null ) {
					continue;
				}
				if ( name.equals(desc.getName()) ) {
					String error =
						"The " + desc.getKeyword() + " '" + desc.getName() +
							"' is defined twice. Only one definition is allowed.";
					child.flagError(error, IGamlIssue.DUPLICATE_NAME, null, desc.getKeyword(),
						desc.getName());
					desc.flagError(error, IGamlIssue.DUPLICATE_NAME, null, desc.getKeyword(),
						desc.getName());
				}
			}
		}
	}

	public static void assertKeywordIsUniqueInSuperDescription(final IDescription desc) {
		IDescription sd = desc.getSuperDescription();
		if ( sd == null ) { return; }
		for ( IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(desc.getKeyword()) && child != desc ) {
				String error =
					"The statement " + desc.getKeyword() +
						" is defined twice. Only one definition is allowed.";
				child.flagError(error, IGamlIssue.DUPLICATE_KEYWORD, null, desc.getKeyword());
				desc.flagError(error, IGamlIssue.DUPLICATE_KEYWORD, null, desc.getKeyword());

			}
		}
	}

	public static void assertAssignmentIsOk(final IDescription cd) {
		IExpression expr =
			cd.getFacets().getExpr(IKeyword.VAR, cd.getFacets().getExpr(IKeyword.NAME));
		if ( !(expr instanceof IVarExpression) ) {
			cd.flagError("This expression is not a reference to a variable ", IKeyword.NAME);
		} else {
			IExpression value = cd.getFacets().getExpr(VALUE);
			if ( value != null && value.getType() != Types.NO_TYPE &&
				!expr.getType().isAssignableFrom(value.getType()) ) {
				cd.flagWarning("Variable " + expr.toGaml() + " of type " + expr.getType() +
					" is assigned a value of type " + value.getType().toString() +
					", which will be automatically casted.", IGamlIssue.SHOULD_CAST,
					IKeyword.VALUE, expr.getType().toString());
			}
		}

	}

	public static void assertMicroSpeciesIsVisible(final IDescription cd,
		final String facetContainingSpecies) {
		String microSpeciesName = cd.getFacets().getLabel(facetContainingSpecies);
		if ( microSpeciesName != null ) {
			SpeciesDescription macroSpecies = cd.getSpeciesContext();
			SpeciesDescription microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);
			if ( microSpecies == null ) {
				cd.flagError(macroSpecies.getName() + " species doesn't contain " +
					microSpeciesName + " as micro-species", IGamlIssue.UNKNOWN_SUBSPECIES,
					facetContainingSpecies, microSpeciesName);
			}
		}
	}

	public static void assertFacetValueIsUniqueInSuperDescription(final IDescription desc,
		final String facet, final IExpression value) {
		IDescription sd = desc.getSuperDescription();
		IDescription previous = null;
		if ( sd == null ) { return; }
		String stringValue = value.toGaml();
		for ( IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(desc.getKeyword()) && child != previous ) {
				IExpression v = child.getFacets().getExpr(facet);
				if ( v == null ) {
					continue;
				}
				if ( v.toGaml().equals(stringValue) ) {
					if ( previous == null ) {
						previous = child;
					} else {
						String error =
							"A " + desc.getKeyword() + " with '" + facet + "= " + stringValue +
								"' is defined twice. Only one definition is allowed.";
						child.flagError(error, IGamlIssue.DUPLICATE_DEFINITION, facet, stringValue);
						previous.flagError(error, IGamlIssue.DUPLICATE_DEFINITION, facet,
							stringValue);
					}
				}
			}
		}

	}

	public static void assertAtLeastOneChildWithFacetValueInSuperDescription(
		final IDescription desc, final String facet, final IExpression value) {
		IDescription sd = desc.getSuperDescription();
		if ( sd == null ) { return; }
		String stringValue = value.toGaml();
		for ( IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(desc.getKeyword()) ) {
				IExpression v = child.getFacets().getExpr(facet);
				if ( v == null ) {
					continue;
				}
				if ( v.toGaml().equals(stringValue) ) { return; }
			}
		}
		String error =
			"No " + desc.getKeyword() + " with '" + facet + "= " + stringValue +
				"' has been defined. ";
		sd.flagError(error, IGamlIssue.MISSING_DEFINITION, null, desc.getKeyword(), facet,
			stringValue);
	}

	public static void assertBehaviorIsExisting(final IDescription desc, final String facet) {
		String behavior = desc.getFacets().getLabel(facet);
		SpeciesDescription sd = desc.getSpeciesContext();
		if ( !sd.hasBehavior(behavior) ) {
			desc.flagError("Behavior " + behavior + " does not exist in " + sd.getName(),
				IGamlIssue.UNKNOWN_BEHAVIOR, facet, behavior, sd.getName());
		}
	}

	public static void assertActionIsExisting(final IDescription desc, final String facet) {
		String action = desc.getFacets().getLabel(facet);
		SpeciesDescription sd = desc.getSpeciesContext();
		if ( sd == null ) { return; }
		if ( !sd.hasAction(action) ) {
			desc.flagError("Action " + action + " does not exist in " + sd.getName(),
				IGamlIssue.UNKNOWN_ACTION, facet, action, sd.getName());
		}

	}

}
