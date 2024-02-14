/*******************************************************************************************************
 *
 * XMLElements.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler.doc.utils;

/**
 * The Interface XMLElements.
 */
public interface XMLElements {

	/** The Constant ACTIONS. */
	String ACTIONS = "actions";

	/** The Constant ACTION. */
	String ACTION = "action";

	/** The Constant ARGS. */
	String ARGS = "args";

	/** The Constant ARG. */
	String ARG = "arg";

	/** The Constant CATEGORIES. */
	String CATEGORIES = "categories";

	/** The Constant CATEGORY. */
	String CATEGORY = "category";

	/** The Constant CONSTANTS_CATEGORIES. */
	String CONSTANTS_CATEGORIES = "constantsCategories";

	/** The Constant COMBINAISON_IO. */
	String COMBINAISON_IO = "combinaisonIO";

	/** The Constant COMMENT. */
	String COMMENT = "comment";

	/** The Constant CONCEPT. */
	String CONCEPT = "concept";

	/** The Constant CONCEPT_LIST. */
	String CONCEPT_LIST = "conceptList";

	/** The Constant CONCEPTS. */
	String CONCEPTS = "concepts";

	/** The Constant CONSTANTS. */
	String CONSTANTS = "constants";

	/** The Constant CONSTANT. */
	String CONSTANT = "constant";

	/** The Constant DOC. */
	String DOC = "doc";

	/** The Constant DOCUMENTATION. */
	String DOCUMENTATION = "documentation";

	/** The Constant EXAMPLES. */
	String EXAMPLES = "examples";

	/** The Constant EXAMPLE. */
	String EXAMPLE = "example";

	/** The Constant EXTENSIONS. */
	String EXTENSIONS = "extensions";

	/** The Constant EXTENSION. */
	String EXTENSION = "extension";

	/** The Constant FILES. */
	String FILES = "files";

	/** The Constant FILE. */
	String FILE = "file";

	/** The Constant FACETS. */
	String FACETS = "facets";

	/** The Constant FACET. */
	String FACET = "facet";

	/** The Constant INSIDE. */
	String INSIDE = "inside";

	/** The Constant INSIDE_STAT_KINDS. */
	String INSIDE_STAT_KINDS = "insideStatementKinds";

	/** The Constant INSIDE_STAT_KIND. */
	String INSIDE_STAT_KIND = "insideStatementKind";

	/** The Constant INSIDE_STAT_SYMBOLS. */
	String INSIDE_STAT_SYMBOLS = "insideStatementSymbols";

	/** The Constant INSIDE_STAT_SYMBOL. */
	String INSIDE_STAT_SYMBOL = "insideStatementSymbol";

	/** The Constant KINDS. */
	String KINDS = "kinds";

	/** The Constant KIND. */
	String KIND = "kind";

	/** The Constant OPERATORS. */
	String OPERATORS = "operators";

	/** The Constant OPERATOR. */
	String OPERATOR = "operator";

	/** The Constant OPERATOR_CATEGORIES. */
	String OPERATOR_CATEGORIES = "operatorCategories";

	/** The Constant OPERATORS_CATEGORIES. */
	String OPERATORS_CATEGORIES = "operatorsCategories";

	/** The Constant OPERANDS. */
	String OPERANDS = "operands";

	/** The Constant OPERAND. */
	String OPERAND = "operand";

	/** The Constant RESULT. */
	String RESULT = "result";

	/** The Constant RETURNS. */
	String RETURNS = "returns";

	/** The Constant SEEALSO. */
	String SEEALSO = "seeAlso";

	/** The Constant SEE. */
	String SEE = "see";

	/** The Constant SKILLS. */
	String SKILLS = "skills";

	/** The Constant SKILL. */
	String SKILL = "skill";

	/** The Constant SPECIESS. */
	String SPECIESS = "speciess";

	/** The Constant SPECIES. */
	String SPECIES = "species";

	/** The Constant SPECIES_SKILLS. */
	String SPECIES_SKILLS = "attached_skills";

	/** The Constant SPECIES_SKILL. */
	String SPECIES_SKILL = "attached_skill";

	/** The Constant STATEMENTS. */
	String STATEMENTS = "statements";

	/** The Constant STATEMENT. */
	String STATEMENT = "statement";

	/** The Constant STATEMENT_KINDS. */
	String STATEMENT_KINDS = "statementsKinds";

	/** The Constant SYMBOLS. */
	String SYMBOLS = "symbols";

	/** The Constant SYMBOL. */
	String SYMBOL = "symbol";

	/** The Constant TYPES. */
	String TYPES = "types";

	/** The Constant TYPE. */
	String TYPE = "type";

	/** The Constant USAGES. */
	String USAGES = "usages";

	/** The Constant USAGE. */
	String USAGE = "usage";

	/** The Constant USAGES_EXAMPLES. */
	String USAGES_EXAMPLES = "usagesExamples";

	/** The Constant USAGES_NO_EXAMPLE. */
	String USAGES_NO_EXAMPLE = "usagesNoExample";

	/** The Constant VARS. */
	String VARS = "vars";

	/** The Constant VAR. */
	String VAR = "var";

	/** The Constant ARCHITECTURES. */
	String ARCHITECTURES = "architectures";

	/** The Constant ARCHITECTURE. */
	String ARCHITECTURE = "architecture";

	/** The Constant ATT_NAME. */
	String ATT_NAME = "name";

	/** The Constant ATT_DOC_PLUGINNAME. */
	// Attributes of DOC element
	String ATT_DOC_PLUGINNAME = "plugin";

	/** The Constant ATT_CAT_ID. */
	// Attributes of CATEGORY element
	String ATT_CAT_ID = "id";

	/** The Constant ATT_FILE_NAME. */
	// Attributes of FILE element
	String ATT_FILE_NAME = "name";

	/** The Constant ATT_FILE_BUFFER_TYPE. */
	String ATT_FILE_BUFFER_TYPE = "buffer_type";

	/** The Constant ATT_FILE_BUFFER_INDEX. */
	String ATT_FILE_BUFFER_INDEX = "buffer_index";

	/** The Constant ATT_FILE_BUFFER_CONTENT. */
	String ATT_FILE_BUFFER_CONTENT = "buffer_content";

	// Attributes of OPERATOR element
	/** The Constant ATT_OP_NAME. */
	// public static final String ATT_OP_CATEGORY = "category";
	String ATT_OP_NAME = "name";

	/** The Constant ATT_OP_ID. */
	String ATT_OP_ID = "id";

	/** The Constant ATT_OP_ALT_NAME. */
	String ATT_OP_ALT_NAME = "alternativeNameOf";

	/** The Constant ATT_ALPHABET_ORDER. */
	String ATT_ALPHABET_ORDER = "alphabetOrder";

	/** The Constant ATT_OPERANDS_CLASS. */
	// Attributes of OPERANDS element
	String ATT_OPERANDS_CLASS = "class";

	/** The Constant ATT_OPERANDS_CONTENT_TYPE. */
	String ATT_OPERANDS_CONTENT_TYPE = "contentType";

	/** The Constant ATT_OPERANDS_RETURN_TYPE. */
	String ATT_OPERANDS_RETURN_TYPE = "returnType";

	/** The Constant ATT_OPERANDS_TYPE. */
	String ATT_OPERANDS_TYPE = "type";

	/** The Constant ATT_OPERAND_NAME. */
	// Attributes of OPERAND element
	String ATT_OPERAND_NAME = "name";

	/** The Constant ATT_OPERAND_POSITION. */
	String ATT_OPERAND_POSITION = "position";

	/** The Constant ATT_OPERAND_TYPE. */
	String ATT_OPERAND_TYPE = "type";

	/** The Constant ATT_USAGE_DESC. */
	// Attributes of USAGE element
	String ATT_USAGE_DESC = "descUsageElt";

	/** The Constant ATT_EXAMPLE_CODE. */
	// Attributes of EXAMPLE element
	String ATT_EXAMPLE_CODE = "code";

	/** The Constant ATT_EXAMPLE_IS_TEST_ONLY. */
	String ATT_EXAMPLE_IS_TEST_ONLY = "isTestOnly";

	/** The Constant ATT_EXAMPLE_IS_EXECUTABLE. */
	String ATT_EXAMPLE_IS_EXECUTABLE = "isExecutable";

	/** The Constant ATT_EXAMPLE_IS_TESTABLE. */
	String ATT_EXAMPLE_IS_TESTABLE = "test";

	/** The Constant ATT_EXAMPLE_VAR. */
	String ATT_EXAMPLE_VAR = "var";

	/** The Constant ATT_EXAMPLE_EQUALS. */
	String ATT_EXAMPLE_EQUALS = "equals";

	/** The Constant ATT_EXAMPLE_IS_NOT. */
	String ATT_EXAMPLE_IS_NOT = "isNot";

	/** The Constant ATT_EXAMPLE_RAISES. */
	String ATT_EXAMPLE_RAISES = "raises";

	/** The Constant ATT_EXAMPLE_INDEX. */
	String ATT_EXAMPLE_INDEX = "index";
	// public static final String ATT_EXAMPLE_RETURN_TYPE = "returnType";

	/** The Constant ATT_EXAMPLE_TYPE. */
	String ATT_EXAMPLE_TYPE = "type";

	/** The Constant ATT_FACET_NAME. */
	// Attributes of FACET element
	String ATT_FACET_NAME = "name";

	/** The Constant ATT_FACET_TYPE. */
	String ATT_FACET_TYPE = "type";

	/** The Constant ATT_FACET_VALUES. */
	String ATT_FACET_VALUES = "values";

	/** The Constant ATT_FACET_OPTIONAL. */
	String ATT_FACET_OPTIONAL = "optional";

	/** The Constant ATT_FACET_OMISSIBLE. */
	String ATT_FACET_OMISSIBLE = "omissible";

	/** The Constant ATT_CST_NAME. */
	// Attributes of CONSTANT element
	String ATT_CST_NAME = "name";

	/** The Constant ATT_CST_VALUE. */
	String ATT_CST_VALUE = "value";

	/** The Constant ATT_CST_NAMES. */
	String ATT_CST_NAMES = "altNames";

	/** The Constant ATT_RES_MASTER. */
	// Attributes of RESULT element
	String ATT_RES_MASTER = "masterDoc";

	/** The Constant ATT_RET_MASTER. */
	// Attributes of RETURNS element
	String ATT_RET_MASTER = "masterDoc";

	/** The Constant ATT_SEE_ID. */
	// Attributes of SEE element
	String ATT_SEE_ID = "id";

	/** The Constant ATT_ACTION_NAME. */
	// Attributes of ACTION element
	String ATT_ACTION_NAME = "name";

	/** The Constant ATT_ACTION_RETURNTYPE. */
	String ATT_ACTION_RETURNTYPE = "returnType";

	/** The Constant ATT_ARG_NAME. */
	// Attributes of ARG element
	String ATT_ARG_NAME = "name";

	/** The Constant ATT_ARG_TYPE. */
	String ATT_ARG_TYPE = "type";

	/** The Constant ATT_ARG_OPTIONAL. */
	String ATT_ARG_OPTIONAL = "optional";

	/** The Constant ATT_SKILL_ID. */
	// Attributes of SKILL element
	String ATT_SKILL_ID = "id";

	/** The Constant ATT_SKILL_NAME. */
	String ATT_SKILL_NAME = "name";

	/** The Constant ATT_SKILL_CLASS. */
	String ATT_SKILL_CLASS = "class";

	/** The Constant ATT_SKILL_EXTENDS. */
	String ATT_SKILL_EXTENDS = "extends";

	/** The Constant ATT_ARCHI_ID. */
	// Attributes of ARCHITECTURE element
	String ATT_ARCHI_ID = "id";

	/** The Constant ATT_ARCHI_NAME. */
	String ATT_ARCHI_NAME = "name";

	/** The Constant ATT_VAR_NAME. */
	// Attributes of VAR element
	String ATT_VAR_NAME = "name";

	/** The Constant ATT_VAR_TYPE. */
	String ATT_VAR_TYPE = "type";

	/** The Constant ATT_VAR_CONSTANT. */
	String ATT_VAR_CONSTANT = "constant";

	/** The Constant ATT_VAR_DEPENDS_ON. */
	String ATT_VAR_DEPENDS_ON = "depends_on";

	/** The Constant ATT_SPECIES_ID. */
	// Attributes of SPECIES element
	String ATT_SPECIES_ID = "id";

	/** The Constant ATT_SPECIES_NAME. */
	String ATT_SPECIES_NAME = "name";

	/** The Constant ATT_SPECIES_SKILL. */
	String ATT_SPECIES_SKILL = "name";

	/** The Constant ATT_STAT_ID. */
	// Attributes of STATEMENT element
	String ATT_STAT_ID = "id";

	/** The Constant ATT_STAT_NAME. */
	String ATT_STAT_NAME = "name";

	/** The Constant ATT_STAT_KIND. */
	String ATT_STAT_KIND = "kind";

	/** The Constant ATT_STAT_ALT_NAME_OF. */
	String ATT_STAT_ALT_NAME_OF = "alt_name_of";

	/** The Constant ATT_TYPE_NAME. */
	// Attributes of TYPE element
	String ATT_TYPE_NAME = "name";

	/** The Constant ATT_TYPE_ID. */
	String ATT_TYPE_ID = "id";

	/** The Constant ATT_TYPE_KIND. */
	String ATT_TYPE_KIND = "kind";

	/** The Constant ATT_INSIDE_STAT_SYMBOL. */
	// Attributes of INSIDE_STAT element
	String ATT_INSIDE_STAT_SYMBOL = "symbol";

	/** The Constant ATT_KIND_STAT. */
	// Attributes of KIND element
	String ATT_KIND_STAT = "symbol";

}
