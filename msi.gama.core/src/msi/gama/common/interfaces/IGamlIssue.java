/*********************************************************************************************
 *
 *
 * 'IGamlIssue.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
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

	public static String AS_ARRAY = "gaml.as.array.issue";
	public static String DIFFERENT_ARGUMENTS = "gaml.different.arguments.issue";
	public static String DUPLICATE_DEFINITION = "gaml.duplicate.definition.issue";
	public static String DUPLICATE_KEYWORD = "gaml.duplicate.keyword.issue";
	public static String DUPLICATE_NAME = "gaml.duplicate.name.issue";
	public static String GENERAL = "gaml.general.issue";
	public static String IS_A_TYPE = "gaml.is.a.type.issue";
	public static String IS_RESERVED = "gaml.is.reserved.issue";
	// Obsolete public static String IS_A_UNIT = "gaml.is.a.unit.issue";
	public static String MISSING_ACTION = "gaml.missing.action.issue";
	public static String MISSING_ARGUMENT = "gaml.missing.argument.issue";
	public static String MISSING_DEFINITION = "gaml.missing.definition.issue";
	public static String MISSING_NAME = "gaml.missing.name.issue";
	public static String MISSING_TYPE = "gaml.missing.type.issue";
	// Obsolete public static String NO_ENVIRONMENT = "gaml.no.environment.issue";
	public static String NO_INIT = "gaml.no.init.issue";
	public static String NOT_A_TYPE = "gaml.not.a.type.issue";
	public static String NOT_AN_AGENT = "gaml.not.an.agent.issue";
	public static String NOT_AMONG = "gaml.not.among.issue";
	public static String NOT_CONST = "gaml.not.const.issue";
	public static String REDEFINES = "gaml.redefinition.info";
	public static String REMOVE_CONST = "gaml.remove.const.issue";
	public static String REMOVE_VALUE = "gaml.remove.value.issue";
	public static String REMOVE_FUNCTION = "gaml.remove.function.issue";
	public static String SHADOWS_NAME = "gaml.shadows.name.issue";
	public static String SHOULD_CAST = "gaml.casting.issue";
	public static String UNKNOWN_ACTION = "gaml.unknown.action.issue";
	public static String UNKNOWN_ARGUMENT = "gaml.unknonw.argument.issue";
	public static String UNKNOWN_BEHAVIOR = "gaml.unknown.behavior.issue";
	public static String UNKNOWN_BINARY = "gaml.unknown.unary.issue";
	public static String UNKNOWN_ENVIRONMENT = "gaml.unknown.environment.issue";
	public static String UNKNOWN_FIELD = "gaml.unknown.field.issue";
	public static String UNKNOWN_KEYWORD = "gaml.unknown.keyword.issue";
	public static String UNKNOWN_NUMBER = "gaml.unknown.number.issue";
	public static String UNKNOWN_SKILL = "gaml.unknown.skill.issue";
	public static String UNKNOWN_SUBSPECIES = "gaml.unknown.subspecies.issue";
	public static String UNKNOWN_UNARY = "gaml.unknown.unary.issue";
	public static String UNKNOWN_VAR = "gaml.unknown.var.issue";
	public static String UNMATCHED_BINARY = "gaml.unmatched.unary.issue";
	public static String UNMATCHED_OPERANDS = "gaml.unmatched.operands.issue";
	public static String UNMATCHED_UNARY = "gaml.unmatched.unary.issue";
	public static String UNMATCHED_TYPES = "gaml.unmatched.types.issue";
	public static String WRONG_CONTEXT = "gaml.wrong.context.issue";
	public static String WRONG_PARENT = "gaml.wrong.parent.issue";
	public static String WRONG_REDEFINITION = "gaml.wrong.redefinition.issue";
	public static String WRONG_TYPE = "gaml.wrong.type.issue";
	public static String NOT_A_UNIT = "gaml.not.a.unit.issue";
	public static String MISSING_FACET = "gaml.missing.facet.issue";
	public static String UNKNOWN_FACET = "gaml.unknown.facet.issue";
	public static String IMPORT_ERROR = "gaml.import.has.error";
	public static String MISSING_RETURN = "gaml.missing.return.issue";
	public static String DEPRECATED = "gaml.deprecated.code.issue";
	public static String UNUSED = "gaml.unused.code.issue";
	public static String CONFLICTING_FACETS = "gaml.conflicting.facets";
	public static String WRONG_VALUE = "gaml.wrong.value";

}
