/*********************************************************************************************
 *
 * 'XMLElements.java, in plugin ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler.doc.utils;

public interface XMLElements {
	public static final String ACTIONS = "actions";
	public static final String ACTION = "action";
	public static final String ARGS = "args";
	public static final String ARG = "arg";
	public static final String CATEGORIES = "categories";
	public static final String CATEGORY = "category";
	public static final String CONSTANTS_CATEGORIES = "constantsCategories";
	public static final String COMBINAISON_IO = "combinaisonIO";
	public static final String COMMENT = "comment";
	public static final String CONCEPT = "concept";
	public static final String CONCEPT_LIST = "conceptList";
	public static final String CONCEPTS = "concepts";
	public static final String CONSTANTS = "constants";
	public static final String CONSTANT = "constant";
	public static final String DOC = "doc";
	public static final String DOCUMENTATION = "documentation";
	public static final String EXAMPLES = "examples";
	public static final String EXAMPLE = "example";
	public static final String EXTENSIONS = "extensions";
	public static final String EXTENSION = "extension";
	public static final String FILES = "files";
	public static final String FILE = "file";	
	public static final String FACETS = "facets";
	public static final String FACET = "facet";
	public static final String INSIDE = "inside";
	public static final String INSIDE_STAT_KINDS = "insideStatementKinds";
	public static final String INSIDE_STAT_KIND = "insideStatementKind";
	public static final String INSIDE_STAT_SYMBOLS = "insideStatementSymbols";
	public static final String INSIDE_STAT_SYMBOL = "insideStatementSymbol";
	public static final String KINDS = "kinds";
	public static final String KIND = "kind";
	public static final String OPERATORS = "operators";
	public static final String OPERATOR = "operator";
	public static final String OPERATOR_CATEGORIES = "operatorCategories";
	public static final String OPERATORS_CATEGORIES = "operatorsCategories";
	public static final String OPERANDS = "operands";
	public static final String OPERAND = "operand";
	public static final String RESULT = "result";
	public static final String SEEALSO = "seeAlso";
	public static final String SEE = "see";
	public static final String SKILLS = "skills";
	public static final String SKILL = "skill";
	public static final String SPECIESS = "speciess";
	public static final String SPECIES = "species";
	public static final String SPECIES_SKILLS = "attached_skills";
	public static final String SPECIES_SKILL = "attached_skill";
	public static final String STATEMENTS = "statements";
	public static final String STATEMENT = "statement";
	public static final String STATEMENT_KINDS = "statementsKinds";
	public static final String SYMBOLS = "symbols";
	public static final String SYMBOL = "symbol";
	public static final String TYPES = "types";
	public static final String TYPE = "type";
	public static final String USAGES = "usages";
	public static final String USAGE = "usage";
	public static final String USAGES_EXAMPLES = "usagesExamples";
	public static final String USAGES_NO_EXAMPLE = "usagesNoExample";
	public static final String VARS = "vars";
	public static final String VAR = "var";

	public static final String ARCHITECTURES = "architectures";
	public static final String ARCHITECTURE = "architecture";

	
	public static final String ATT_NAME = "name";	
	
	// Attributes of DOC element
	public static final String ATT_DOC_PLUGINNAME = "plugin";

	// Attributes of CATEGORY element
	public static final String ATT_CAT_ID = "id";

	// Attributes of FILE element
	public static final String ATT_FILE_NAME = "name";
	public static final String ATT_FILE_BUFFER_TYPE = "buffer_type";
	public static final String ATT_FILE_BUFFER_INDEX = "buffer_index";
	public static final String ATT_FILE_BUFFER_CONTENT = "buffer_content";
	
	
	// Attributes of OPERATOR element
	// public static final String ATT_OP_CATEGORY = "category";
	public static final String ATT_OP_NAME = "name";
	public static final String ATT_OP_ID = "id";
	public static final String ATT_OP_ALT_NAME = "alternativeNameOf";
	public static final String ATT_ALPHABET_ORDER = "alphabetOrder";

	// Attributes of OPERANDS element
	public static final String ATT_OPERANDS_CLASS = "class";
	public static final String ATT_OPERANDS_CONTENT_TYPE = "contentType";
	public static final String ATT_OPERANDS_RETURN_TYPE = "returnType";
	public static final String ATT_OPERANDS_TYPE = "type";

	// Attributes of OPERAND element
	public static final String ATT_OPERAND_NAME = "name";
	public static final String ATT_OPERAND_POSITION = "position";
	public static final String ATT_OPERAND_TYPE = "type";

	// Attributes of USAGE element
	public static final String ATT_USAGE_DESC = "descUsageElt";

	// Attributes of EXAMPLE element
	public static final String ATT_EXAMPLE_CODE = "code";
	public static final String ATT_EXAMPLE_IS_TEST_ONLY = "isTestOnly";
	public static final String ATT_EXAMPLE_IS_EXECUTABLE = "isExecutable";
	public static final String ATT_EXAMPLE_IS_TESTABLE = "test";
	public static final String ATT_EXAMPLE_VAR = "var";
	public static final String ATT_EXAMPLE_EQUALS = "equals";
	public static final String ATT_EXAMPLE_IS_NOT = "isNot";
	public static final String ATT_EXAMPLE_RAISES = "raises";
	public static final String ATT_EXAMPLE_INDEX = "index";
	// public static final String ATT_EXAMPLE_RETURN_TYPE = "returnType";

	public static final String ATT_EXAMPLE_TYPE = "type";

	// Attributes of FACET element
	public static final String ATT_FACET_NAME = "name";
	public static final String ATT_FACET_TYPE = "type";
	public static final String ATT_FACET_VALUES = "values";
	public static final String ATT_FACET_OPTIONAL = "optional";
	public static final String ATT_FACET_OMISSIBLE = "omissible";

	// Attributes of CONSTANT element
	public static final String ATT_CST_NAME = "name";
	public static final String ATT_CST_VALUE = "value";
	public static final String ATT_CST_NAMES = "altNames";

	// Attributes of RESULT element
	public static final String ATT_RES_MASTER = "masterDoc";

	// Attributes of SEE element
	public static final String ATT_SEE_ID = "id";

	// Attributes of ACTION element
	public static final String ATT_ACTION_NAME = "name";
	public static final String ATT_ACTION_RETURNTYPE = "returnType";

	// Attributes of ARG element
	public static final String ATT_ARG_NAME = "name";
	public static final String ATT_ARG_TYPE = "type";
	public static final String ATT_ARG_OPTIONAL = "optional";

	// Attributes of SKILL element
	public static final String ATT_SKILL_ID = "id";
	public static final String ATT_SKILL_NAME = "name";
	public static final String ATT_SKILL_CLASS = "class";
	public static final String ATT_SKILL_EXTENDS = "extends";

	// Attributes of ARCHITECTURE element
	public static final String ATT_ARCHI_ID = "id";
	public static final String ATT_ARCHI_NAME = "name";

	// Attributes of VAR element
	public static final String ATT_VAR_NAME = "name";
	public static final String ATT_VAR_TYPE = "type";
	public static final String ATT_VAR_CONSTANT = "constant";
	public static final String ATT_VAR_DEPENDS_ON = "depends_on";

	// Attributes of SPECIES element
	public static final String ATT_SPECIES_ID = "id";
	public static final String ATT_SPECIES_NAME = "name";
	public static final String ATT_SPECIES_SKILL = "name";

	// Attributes of STATEMENT element
	public static final String ATT_STAT_ID = "id";
	public static final String ATT_STAT_NAME = "name";
	public static final String ATT_STAT_KIND = "kind";

	// Attributes of TYPE element
	public static final String ATT_TYPE_NAME = "name";
	public static final String ATT_TYPE_ID = "id";
	public static final String ATT_TYPE_KIND = "kind";

	// Attributes of INSIDE_STAT element
	public static final String ATT_INSIDE_STAT_SYMBOL = "symbol";

	// Attributes of KIND element
	public static final String ATT_KIND_STAT = "symbol";

}
