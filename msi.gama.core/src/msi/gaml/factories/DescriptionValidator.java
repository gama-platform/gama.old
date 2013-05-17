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
import msi.gaml.statements.Facets;
import msi.gaml.types.*;

/**
 * The class DescriptionValidator.
 * 
 * @author drogoul
 * @since 18 avr. 2012
 * 
 */
public class DescriptionValidator {

	public static String[] valueFacetsArray = new String[] { VALUE, INIT, FUNCTION, UPDATE, MIN, MAX };
	public static List<String> valueFacetsList = Arrays.asList(valueFacetsArray);

	/*
	 * Verification done after the facets have been compiled
	 */

	public static void assertActionsAreCompatible(final StatementDescription myAction,
		final StatementDescription parentAction, final String parentName) {
		final String actionName = parentAction.getName();
		IType myType = myAction.getType();
		IType parentType = parentAction.getType();
		if ( parentType != myType ) {
			myAction.error("Return type (" + myType + ") differs from that (" + parentType +
				") of the implementation of  " + actionName + " in " + parentName);
		} else if ( myType.hasContents() ) {
			myType = myAction.getContentType();
			parentType = parentAction.getContentType();
			if ( parentType != myType ) {
				myAction.error("Content type (" + myType + ") differs from that (" + parentType +
					") of the implementation of  " + actionName + " in " + parentName);
			}
		}
		// if ( !parentAction.getArgNames().containsAll(myAction.getArgNames()) ) {
		if ( !new HashSet(parentAction.getArgNames()).equals(new HashSet(myAction.getArgNames())) ) {
			final String error =
				"The list of arguments " + myAction.getArgNames() + " differs from that of the implementation of " +
					actionName + " in " + parentName + " " + parentAction.getArgNames() + "";
			myAction.warning(error, IGamlIssue.DIFFERENT_ARGUMENTS, myAction.getUnderlyingElement(null));
		}

	}

	public static void verifyFacetType(final IDescription desc, final String facet, final IExpression expr,
		final SymbolProto smd, final ModelDescription md, final TypesManager tm) {
		final FacetProto fmd = smd.getPossibleFacets().get(facet);
		if ( fmd == null ) { return; }

		// We have a multi-valued facet
		if ( fmd.values.length > 0 ) {
			verifyFacetIsInValues(desc, facet, expr, fmd.values);
			return;
		}
		// The facet is supposed to be a type (IType.TYPE_ID)
		for ( final int s : fmd.types ) {
			if ( s == IType.TYPE_ID ) {
				verifyFacetIsAType(desc, facet, expr, tm);
				return;
			}
		}

		if ( !fmd.isLabel ) {
			verifyFacetTypeIsCompatible(desc, facet, expr, fmd.types, tm);
		}
	}

	public static void verifyFacetTypeIsCompatible(final IDescription desc, final String facet, final IExpression expr,
		final int[] types, final TypesManager tm) {
		boolean compatible = false;
		final IType actualType = expr.getType();
		for ( final int type : types ) {
			compatible = compatible || actualType.isTranslatableInto(tm.get(type));
			if ( compatible ) {
				break;
			}
		}
		if ( !compatible ) {
			final String[] strings = new String[types.length];
			for ( int i = 0; i < types.length; i++ ) {
				strings[i] = tm.get(types[i]).toString();
			}
			desc.warning(
				"Facet '" + facet + "' is expecting " + Arrays.toString(strings) + " instead of " + actualType,
				IGamlIssue.SHOULD_CAST, facet, tm.get(types[0]).toString());
		}

	}

	public static void verifyFacetIsAType(final IDescription desc, final String facet, final IExpression expr,
		final TypesManager tm) {
		final String tt = expr.literalValue();
		final IType type = tm.get(tt);
		if ( type == Types.NO_TYPE && !UNKNOWN.equals(type.toString()) && !IKeyword.SIGNAL.equals(type.toString()) ) {
			desc.error("Facet '" + facet + "' is expecting a type name. " + type + " is not a type name",
				IGamlIssue.NOT_A_TYPE, facet, tt);
		}
	}

	public static void verifyFacetIsInValues(final IDescription desc, final String facet, final IExpression expr,
		final String[] values) {
		final String s = expr.literalValue();
		boolean compatible = false;
		for ( final String value : values ) {
			compatible = compatible || value.equals(s);
			if ( compatible ) {
				break;
			}
		}
		if ( !compatible ) {
			desc.error("Facet '" + facet + "' is expecting a value among " + Arrays.toString(values) + " instead of " +
				s, facet);
		}
	}

	public static void assertDescriptionIsInsideTheRightSuperDescription(final SymbolProto meta, final IDescription desc) {
		final IDescription sd = desc.getEnclosingDescription();
		if ( !meta.verifyContext(sd) ) {
			desc.error(desc.getKeyword() + " cannot be defined in " + sd.getKeyword(), IGamlIssue.WRONG_CONTEXT,
				desc.getName());
		}
	}

	public static void assertNameIsUniqueInSuperDescription(final IDescription desc) {
		final IDescription sd = desc.getEnclosingDescription();
		final String the_name = desc.getFacets().getLabel(NAME);
		if ( sd == null ) { return; }
		for ( final IDescription child : sd.getChildren() ) {
			if ( child.getMeta() == desc.getMeta() && child != desc ) {
				final String name = child.getFacets().getLabel(NAME);
				if ( name == null ) {
					continue;
				}
				if ( name.equals(the_name) ) {
					final String error =
						"The " + desc.getKeyword() + " '" + desc.getName() +
							"' is defined twice. Only one definition is allowed.";
					child.error(error, IGamlIssue.DUPLICATE_NAME, child.getUnderlyingElement(null), desc.getKeyword(),
						desc.getName());
					desc.error(error, IGamlIssue.DUPLICATE_NAME, desc.getUnderlyingElement(null), desc.getKeyword(),
						desc.getName());
				}
			}
		}
	}

	public static void assertKeywordIsUniqueInSuperDescription(final IDescription desc) {
		final IDescription sd = desc.getEnclosingDescription();
		final String keyword = desc.getKeyword();
		if ( sd == null ) { return; }
		for ( final IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(keyword) && child != desc ) {
				final String error =
					keyword + " is defined twice. Only one definition is allowed directly in " + sd.getKeyword();
				child.error(error, IGamlIssue.DUPLICATE_KEYWORD, child.getUnderlyingElement(null), keyword);
				desc.error(error, IGamlIssue.DUPLICATE_KEYWORD, desc.getUnderlyingElement(null), keyword);

			}
		}
	}

	public static void assertReturnedValueIsOk(final StatementDescription cd) {
		final Set<StatementDescription> returns = new LinkedHashSet();
		cd.collectChildren(RETURN, returns);
		final IType at = cd.getType();
		if ( at == Types.NO_TYPE ) { return; }
		if ( returns.isEmpty() ) {
			cd.error("Action " + cd.getName() + " must return a result of type " + at, IGamlIssue.MISSING_RETURN);
			return;
		}
		for ( final StatementDescription ret : returns ) {
			final IExpression ie = ret.getFacets().getExpr(VALUE);
			if ( ie == null ) {
				continue;
			}
			if ( ie.equals(IExpressionFactory.NIL_EXPR) ) {
				if ( at.getDefault() != null ) {
					ret.error("'nil' is not an acceptable " + at);
				} else {
					continue;
				}
			} else {
				final IType rt = ie.getType();
				if ( !rt.isTranslatableInto(at) ) {
					ret.error("Cannot convert from " + rt + " to " + at, IGamlIssue.SHOULD_CAST, VALUE, at.toString());
				}
			}
		}
		// FIXME This assertion is still simple (i.e. the tree is not verified to ensure that every
		// branch returns something)
	}

	public static void assertAssignmentIsOk(final IDescription cd) {
		final IExpression expr = cd.getFacets().getExpr(VAR, cd.getFacets().getExpr(NAME));
		if ( !(expr instanceof IVarExpression) ) {
			cd.error("The expression " + cd.getFacets().getLabel(VAR) + " is not a reference to a variable ", VAR);
		} else {
			final IExpression value = cd.getFacets().getExpr(VALUE);
			if ( value != null && value.getType() != Types.NO_TYPE &&
				!value.getType().isTranslatableInto(expr.getType()) ) {
				cd.warning("Variable " + expr.toGaml() + " of type " + expr.getType() +
					" is assigned a value of type " + value.getType().toString() +
					", which will be automatically casted.", IGamlIssue.SHOULD_CAST, IKeyword.VALUE, expr.getType()
					.toString());
			}
			// AD 19/1/13: test of the constants
			if ( ((IVarExpression) expr).isNotModifiable() ) {
				cd.error("The variable " + expr.toGaml() +
					" is a constant or a function and cannot be assigned a value.", IKeyword.NAME);
			}
		}
	}

	public static void assertContainerAssignmentIsOk(final IDescription cd) {
		final Facets f = cd.getFacets();
		final IExpression item = f.getExpr(ITEM, f.getExpr(EDGE, f.getExpr(VERTEX)));
		final IExpression list = f.getExpr(TO, f.getExpr(FROM, f.getExpr(IN)));
		final IExpression index = f.getExpr(AT);
		final IExpression whole = f.getExpr(ALL);
		final String keyword = cd.getKeyword();
		final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
		if ( item == null && !all && !keyword.equals(REMOVE) || list == null ) {
			cd.error("The assignment appears uncomplete", IGamlIssue.GENERAL);
			return;
		}
		if ( keyword.equals(ADD) || keyword.equals(REMOVE) ) {
			final IType containerType = list.getType();
			if ( containerType.isFixedLength() ) {
				cd.error("Impossible to add/remove to/from " + list.toGaml(), IGamlIssue.WRONG_TYPE);
				return;
			}
		}
		final IType contentType = list.getContentType();
		IType valueType = Types.NO_TYPE;
		if ( item == null ) {
			if ( whole != null && !whole.literalValue().equals(TRUE) ) {
				valueType = whole.getContentType();
			} else {
				valueType = contentType;
			}
		} else {
			if ( all && item.getType().isTranslatableInto(Types.get(IType.CONTAINER)) ) {
				valueType = item.getContentType();
			} else {
				valueType = item.getType();
			}
		}

		if ( contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType) ) {
			cd.warning("The type of the contents of " + list.toGaml() + " (" + contentType + ") does not match with " +
				valueType, IGamlIssue.SHOULD_CAST, item == null ? IKeyword.ALL : IKeyword.ITEM, contentType.toString());
		}
		final IType keyType = list.getKeyType();
		if ( index != null && keyType != Types.NO_TYPE && !keyType.isTranslatableInto(index.getType()) ) {
			cd.warning("The type of the index of " + list.toGaml() + " (" + keyType +
				") does not match with the type of " + index.toGaml() + " (" + index.getType() + ")",
				IGamlIssue.SHOULD_CAST, IKeyword.AT, keyType.toString());
		}
	}

	public static void assertMicroSpeciesIsVisible(final IDescription cd, final String facetContainingSpecies) {
		final String microSpeciesName = cd.getFacets().getLabel(facetContainingSpecies);
		if ( microSpeciesName != null ) {
			final SpeciesDescription macroSpecies = cd.getSpeciesContext();
			final TypeDescription microSpecies = macroSpecies.getMicroSpecies(microSpeciesName);
			if ( microSpecies == null ) {
				cd.error(macroSpecies.getName() + " species doesn't contain " + microSpeciesName + " as micro-species",
					IGamlIssue.UNKNOWN_SUBSPECIES, facetContainingSpecies, microSpeciesName);
			}
		}
	}

	public static void assertFacetValueIsUniqueInSuperDescription(final IDescription desc, final String facet,
		final IExpression value) {
		final IDescription sd = desc.getEnclosingDescription();
		IDescription previous = null;
		if ( sd == null ) { return; }
		final String stringValue = value.toGaml();
		for ( final IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(desc.getKeyword()) && child != previous ) {
				final IExpression v = child.getFacets().getExpr(facet);
				if ( v == null ) {
					continue;
				}
				if ( v.toGaml().equals(stringValue) ) {
					if ( previous == null ) {
						previous = child;
					} else {
						final String error =
							"A " + desc.getKeyword() + " with '" + facet + "= " + stringValue +
								"' is defined twice. Only one definition is allowed.";
						child.error(error, IGamlIssue.DUPLICATE_DEFINITION, facet, stringValue);
						previous.error(error, IGamlIssue.DUPLICATE_DEFINITION, facet, stringValue);
					}
				}
			}
		}

	}

	public static void assertAtLeastOneChildWithFacetValueInSuperDescription(final IDescription desc,
		final String facet, final IExpression value) {
		final IDescription sd = desc.getEnclosingDescription();
		if ( sd == null ) { return; }
		final String stringValue = value.toGaml();
		for ( final IDescription child : sd.getChildren() ) {
			if ( child.getKeyword().equals(desc.getKeyword()) ) {
				final IExpression v = child.getFacets().getExpr(facet);
				if ( v == null ) {
					continue;
				}
				if ( v.toGaml().equals(stringValue) ) { return; }
			}
		}
		final String error =
			"No " + desc.getKeyword() + " with '" + facet + "= " + stringValue + "' has been defined. ";
		sd.error(error, IGamlIssue.MISSING_DEFINITION, sd.getUnderlyingElement(null), desc.getKeyword(), facet,
			stringValue);
	}

	public static void assertBehaviorIsExisting(final IDescription desc, final String facet) {
		final String behavior = desc.getFacets().getLabel(facet);
		final SpeciesDescription sd = desc.getSpeciesContext();
		if ( !sd.hasBehavior(behavior) ) {
			desc.error("Behavior " + behavior + " does not exist in " + sd.getName(), IGamlIssue.UNKNOWN_BEHAVIOR,
				facet, behavior, sd.getName());
		}
	}

	public static void assertActionIsExisting(final IDescription desc, final String facet) {
		final String action = desc.getFacets().getLabel(facet);
		final SpeciesDescription sd = desc.getSpeciesContext();
		if ( sd == null ) { return; }
		if ( !sd.hasAction(action) ) {
			// desc.error("Action " + action + " does not exist in " + sd.getName(),
			// IGamlIssue.UNKNOWN_ACTION, facet, action, sd.getName());
		}

	}

}
