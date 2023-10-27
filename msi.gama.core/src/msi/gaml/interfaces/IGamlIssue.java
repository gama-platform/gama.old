/*******************************************************************************************************
 *
 * IGamlIssue.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.interfaces;

/**
 * The class IGamlIssue.
 *
 * @author drogoul
 * @since 26 avr. 2012
 *
 */
public interface IGamlIssue {

	/** The doubled code. */
	String DOUBLED_CODE = "***DOUBLED***";

	/** The as array. */
	String AS_ARRAY = "gaml.as.array.issue";

	/** The different arguments. */
	String DIFFERENT_ARGUMENTS = "gaml.different.arguments.issue";

	/** The duplicate definition. */
	String DUPLICATE_DEFINITION = "gaml.duplicate.definition.issue";

	/** The duplicate keyword. */
	String DUPLICATE_KEYWORD = "gaml.duplicate.keyword.issue";

	/** The duplicate name. */
	String DUPLICATE_NAME = "gaml.duplicate.name.issue";

	/** The general. */
	String GENERAL = "gaml.general.issue";

	/** The is a type. */
	String IS_A_TYPE = "gaml.is.a.type.issue";

	/** The is reserved. */
	String IS_RESERVED = "gaml.is.reserved.issue";

	/** The missing action. */
	// Obsolete public static String IS_A_UNIT = "gaml.is.a.unit.issue";
	String MISSING_ACTION = "gaml.missing.action.issue";

	/** The missing argument. */
	String MISSING_ARGUMENT = "gaml.missing.argument.issue";

	/** The missing definition. */
	String MISSING_DEFINITION = "gaml.missing.definition.issue";

	/** The missing name. */
	String MISSING_NAME = "gaml.missing.name.issue";
	// String MISSING_TYPE = "gaml.missing.type.issue";
	/** The no init. */
	// Obsolete public static String NO_ENVIRONMENT = "gaml.no.environment.issue";
	String NO_INIT = "gaml.no.init.issue";

	/** The not a type. */
	String NOT_A_TYPE = "gaml.not.a.type.issue";

	/** The not among. */
	// String NOT_AN_AGENT = "gaml.not.an.agent.issue";
	String NOT_AMONG = "gaml.not.among.issue";

	/** The not const. */
	String NOT_CONST = "gaml.not.const.issue";

	/** The redefines. */
	String REDEFINES = "gaml.redefinition.info";

	/** The remove const. */
	String REMOVE_CONST = "gaml.remove.const.issue";

	/** The remove value. */
	String REMOVE_VALUE = "gaml.remove.value.issue";

	/** The shadows name. */
	// public static String REMOVE_FUNCTION = "gaml.remove.function.issue";
	String SHADOWS_NAME = "gaml.shadows.name.issue";

	/** The should cast. */
	String SHOULD_CAST = "gaml.casting.issue";

	/** The unknown action. */
	String UNKNOWN_ACTION = "gaml.unknown.action.issue";

	/** The unknown argument. */
	String UNKNOWN_ARGUMENT = "gaml.unknonw.argument.issue";

	/** The unknown behavior. */
	String UNKNOWN_BEHAVIOR = "gaml.unknown.behavior.issue";

	/** The unknown operator. */
	String UNKNOWN_OPERATOR = "gaml.unknown.operator.issue";

	/** The unknown environment. */
	String UNKNOWN_ENVIRONMENT = "gaml.unknown.environment.issue";

	/** The unknown field. */
	String UNKNOWN_FIELD = "gaml.unknown.field.issue";

	/** The unknown keyword. */
	String UNKNOWN_KEYWORD = "gaml.unknown.keyword.issue";

	/** The unknown number. */
	String UNKNOWN_NUMBER = "gaml.unknown.number.issue";

	/** The unknown skill. */
	String UNKNOWN_SKILL = "gaml.unknown.skill.issue";

	/** The unknown species. */
	String UNKNOWN_SPECIES = "gaml.unknown.species.issue";

	/** The unknown var. */
	// String UNKNOWN_SUBSPECIES = "gaml.unknown.subspecies.issue";
	String UNKNOWN_VAR = "gaml.unknown.var.issue";

	/** The unmatched operands. */
	// public static String UNMATCHED_BINARY = "gaml.unmatched.unary.issue";
	String UNMATCHED_OPERANDS = "gaml.unmatched.operands.issue";

	/** The unmatched types. */
	// String UNMATCHED_UNARY = "gaml.unmatched.unary.issue";
	String UNMATCHED_TYPES = "gaml.unmatched.types.issue";

	/** The wrong context. */
	String WRONG_CONTEXT = "gaml.wrong.context.issue";

	/** The wrong parent. */
	String WRONG_PARENT = "gaml.wrong.parent.issue";

	/** The wrong redefinition. */
	String WRONG_REDEFINITION = "gaml.wrong.redefinition.issue";

	/** The wrong type. */
	String WRONG_TYPE = "gaml.wrong.type.issue";

	/** The not a unit. */
	String NOT_A_UNIT = "gaml.not.a.unit.issue";

	/** The missing facet. */
	String MISSING_FACET = "gaml.missing.facet.issue";

	/** The unknown facet. */
	String UNKNOWN_FACET = "gaml.unknown.facet.issue";

	/** The import error. */
	String IMPORT_ERROR = "gaml.import.has.error";

	/** The missing return. */
	String MISSING_RETURN = "gaml.missing.return.issue";

	/** The deprecated. */
	String DEPRECATED = "gaml.deprecated.code.issue";

	/** The unused. */
	String UNUSED = "gaml.unused.code.issue";

	/** The conflicting facets. */
	String CONFLICTING_FACETS = "gaml.conflicting.facets";

	/** The wrong value. */
	String WRONG_VALUE = "gaml.wrong.value";

	/** The missing plugin. */
	String MISSING_PLUGIN = "gaml.missing.plugin";

}
