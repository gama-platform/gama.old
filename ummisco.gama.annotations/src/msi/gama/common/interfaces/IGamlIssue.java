/*********************************************************************************************
 *
 * 'IGamlIssue.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The class IGamlIssue.
 *
 * @author drogoul
 * @since 26 avr. 2012
 *
 */
public interface IGamlIssue {

	String DOUBLED_CODE = "***DOUBLED***";
	String AS_ARRAY = "gaml.as.array.issue";
	String DIFFERENT_ARGUMENTS = "gaml.different.arguments.issue";
	String DUPLICATE_DEFINITION = "gaml.duplicate.definition.issue";
	String DUPLICATE_KEYWORD = "gaml.duplicate.keyword.issue";
	String DUPLICATE_NAME = "gaml.duplicate.name.issue";
	String GENERAL = "gaml.general.issue";
	String IS_A_TYPE = "gaml.is.a.type.issue";
	String IS_RESERVED = "gaml.is.reserved.issue";
	// Obsolete public static String IS_A_UNIT = "gaml.is.a.unit.issue";
	String MISSING_ACTION = "gaml.missing.action.issue";
	String MISSING_ARGUMENT = "gaml.missing.argument.issue";
	String MISSING_DEFINITION = "gaml.missing.definition.issue";
	String MISSING_NAME = "gaml.missing.name.issue";
	// String MISSING_TYPE = "gaml.missing.type.issue";
	// Obsolete public static String NO_ENVIRONMENT = "gaml.no.environment.issue";
	String NO_INIT = "gaml.no.init.issue";
	String NOT_A_TYPE = "gaml.not.a.type.issue";
	// String NOT_AN_AGENT = "gaml.not.an.agent.issue";
	String NOT_AMONG = "gaml.not.among.issue";
	String NOT_CONST = "gaml.not.const.issue";
	String REDEFINES = "gaml.redefinition.info";
	String REMOVE_CONST = "gaml.remove.const.issue";
	String REMOVE_VALUE = "gaml.remove.value.issue";
	// public static String REMOVE_FUNCTION = "gaml.remove.function.issue";
	String SHADOWS_NAME = "gaml.shadows.name.issue";
	String SHOULD_CAST = "gaml.casting.issue";
	String UNKNOWN_ACTION = "gaml.unknown.action.issue";
	String UNKNOWN_ARGUMENT = "gaml.unknonw.argument.issue";
	String UNKNOWN_BEHAVIOR = "gaml.unknown.behavior.issue";
	String UNKNOWN_OPERATOR = "gaml.unknown.operator.issue";
	String UNKNOWN_ENVIRONMENT = "gaml.unknown.environment.issue";
	String UNKNOWN_FIELD = "gaml.unknown.field.issue";
	String UNKNOWN_KEYWORD = "gaml.unknown.keyword.issue";
	String UNKNOWN_NUMBER = "gaml.unknown.number.issue";
	String UNKNOWN_SKILL = "gaml.unknown.skill.issue";
	String UNKNOWN_SPECIES = "gaml.unknown.species.issue";
	// String UNKNOWN_SUBSPECIES = "gaml.unknown.subspecies.issue";
	String UNKNOWN_VAR = "gaml.unknown.var.issue";
	// public static String UNMATCHED_BINARY = "gaml.unmatched.unary.issue";
	String UNMATCHED_OPERANDS = "gaml.unmatched.operands.issue";
	// String UNMATCHED_UNARY = "gaml.unmatched.unary.issue";
	String UNMATCHED_TYPES = "gaml.unmatched.types.issue";
	String WRONG_CONTEXT = "gaml.wrong.context.issue";
	String WRONG_PARENT = "gaml.wrong.parent.issue";
	String WRONG_REDEFINITION = "gaml.wrong.redefinition.issue";
	String WRONG_TYPE = "gaml.wrong.type.issue";
	String NOT_A_UNIT = "gaml.not.a.unit.issue";
	String MISSING_FACET = "gaml.missing.facet.issue";
	String UNKNOWN_FACET = "gaml.unknown.facet.issue";
	String IMPORT_ERROR = "gaml.import.has.error";
	String MISSING_RETURN = "gaml.missing.return.issue";
	String DEPRECATED = "gaml.deprecated.code.issue";
	String UNUSED = "gaml.unused.code.issue";
	String CONFLICTING_FACETS = "gaml.conflicting.facets";
	String WRONG_VALUE = "gaml.wrong.value";

}
