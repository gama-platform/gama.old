/*******************************************************************************************************
 *
 * XMLElements.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
	public static final String ACTIONS = "actions";
	
	/** The Constant ACTION. */
	public static final String ACTION = "action";
	
	/** The Constant ARGS. */
	public static final String ARGS = "args";
	
	/** The Constant ARG. */
	public static final String ARG = "arg";
	
	/** The Constant CATEGORIES. */
	public static final String CATEGORIES = "categories";
	
	/** The Constant CATEGORY. */
	public static final String CATEGORY = "category";
	
	/** The Constant CONSTANTS_CATEGORIES. */
	public static final String CONSTANTS_CATEGORIES = "constantsCategories";
	
	/** The Constant COMBINAISON_IO. */
	public static final String COMBINAISON_IO = "combinaisonIO";
	
	/** The Constant COMMENT. */
	public static final String COMMENT = "comment";
	
	/** The Constant CONCEPT. */
	public static final String CONCEPT = "concept";
	
	/** The Constant CONCEPT_LIST. */
	public static final String CONCEPT_LIST = "conceptList";
	
	/** The Constant CONCEPTS. */
	public static final String CONCEPTS = "concepts";
	
	/** The Constant CONSTANTS. */
	public static final String CONSTANTS = "constants";
	
	/** The Constant CONSTANT. */
	public static final String CONSTANT = "constant";
	
	/** The Constant DOC. */
	public static final String DOC = "doc";
	
	/** The Constant DOCUMENTATION. */
	public static final String DOCUMENTATION = "documentation";
	
	/** The Constant EXAMPLES. */
	public static final String EXAMPLES = "examples";
	
	/** The Constant EXAMPLE. */
	public static final String EXAMPLE = "example";
	
	/** The Constant EXTENSIONS. */
	public static final String EXTENSIONS = "extensions";
	
	/** The Constant EXTENSION. */
	public static final String EXTENSION = "extension";
	
	/** The Constant FILES. */
	public static final String FILES = "files";
	
	/** The Constant FILE. */
	public static final String FILE = "file";	
	
	/** The Constant FACETS. */
	public static final String FACETS = "facets";
	
	/** The Constant FACET. */
	public static final String FACET = "facet";
	
	/** The Constant INSIDE. */
	public static final String INSIDE = "inside";
	
	/** The Constant INSIDE_STAT_KINDS. */
	public static final String INSIDE_STAT_KINDS = "insideStatementKinds";
	
	/** The Constant INSIDE_STAT_KIND. */
	public static final String INSIDE_STAT_KIND = "insideStatementKind";
	
	/** The Constant INSIDE_STAT_SYMBOLS. */
	public static final String INSIDE_STAT_SYMBOLS = "insideStatementSymbols";
	
	/** The Constant INSIDE_STAT_SYMBOL. */
	public static final String INSIDE_STAT_SYMBOL = "insideStatementSymbol";
	
	/** The Constant KINDS. */
	public static final String KINDS = "kinds";
	
	/** The Constant KIND. */
	public static final String KIND = "kind";
	
	/** The Constant OPERATORS. */
	public static final String OPERATORS = "operators";
	
	/** The Constant OPERATOR. */
	public static final String OPERATOR = "operator";
	
	/** The Constant OPERATOR_CATEGORIES. */
	public static final String OPERATOR_CATEGORIES = "operatorCategories";
	
	/** The Constant OPERATORS_CATEGORIES. */
	public static final String OPERATORS_CATEGORIES = "operatorsCategories";
	
	/** The Constant OPERANDS. */
	public static final String OPERANDS = "operands";
	
	/** The Constant OPERAND. */
	public static final String OPERAND = "operand";
	
	/** The Constant RESULT. */
	public static final String RESULT = "result";

	/** The Constant RETURNS. */
	public static final String RETURNS = "returns";	
	
	/** The Constant SEEALSO. */
	public static final String SEEALSO = "seeAlso";
	
	/** The Constant SEE. */
	public static final String SEE = "see";
	
	/** The Constant SKILLS. */
	public static final String SKILLS = "skills";
	
	/** The Constant SKILL. */
	public static final String SKILL = "skill";
	
	/** The Constant SPECIESS. */
	public static final String SPECIESS = "speciess";
	
	/** The Constant SPECIES. */
	public static final String SPECIES = "species";
	
	/** The Constant SPECIES_SKILLS. */
	public static final String SPECIES_SKILLS = "attached_skills";
	
	/** The Constant SPECIES_SKILL. */
	public static final String SPECIES_SKILL = "attached_skill";
	
	/** The Constant STATEMENTS. */
	public static final String STATEMENTS = "statements";
	
	/** The Constant STATEMENT. */
	public static final String STATEMENT = "statement";
	
	/** The Constant STATEMENT_KINDS. */
	public static final String STATEMENT_KINDS = "statementsKinds";
	
	/** The Constant SYMBOLS. */
	public static final String SYMBOLS = "symbols";
	
	/** The Constant SYMBOL. */
	public static final String SYMBOL = "symbol";
	
	/** The Constant TYPES. */
	public static final String TYPES = "types";
	
	/** The Constant TYPE. */
	public static final String TYPE = "type";
	
	/** The Constant USAGES. */
	public static final String USAGES = "usages";
	
	/** The Constant USAGE. */
	public static final String USAGE = "usage";
	
	/** The Constant USAGES_EXAMPLES. */
	public static final String USAGES_EXAMPLES = "usagesExamples";
	
	/** The Constant USAGES_NO_EXAMPLE. */
	public static final String USAGES_NO_EXAMPLE = "usagesNoExample";
	
	/** The Constant VARS. */
	public static final String VARS = "vars";
	
	/** The Constant VAR. */
	public static final String VAR = "var";

	/** The Constant ARCHITECTURES. */
	public static final String ARCHITECTURES = "architectures";
	
	/** The Constant ARCHITECTURE. */
	public static final String ARCHITECTURE = "architecture";

	
	/** The Constant ATT_NAME. */
	public static final String ATT_NAME = "name";	
	
	/** The Constant ATT_DOC_PLUGINNAME. */
	// Attributes of DOC element
	public static final String ATT_DOC_PLUGINNAME = "plugin";

	/** The Constant ATT_CAT_ID. */
	// Attributes of CATEGORY element
	public static final String ATT_CAT_ID = "id";

	/** The Constant ATT_FILE_NAME. */
	// Attributes of FILE element
	public static final String ATT_FILE_NAME = "name";
	
	/** The Constant ATT_FILE_BUFFER_TYPE. */
	public static final String ATT_FILE_BUFFER_TYPE = "buffer_type";
	
	/** The Constant ATT_FILE_BUFFER_INDEX. */
	public static final String ATT_FILE_BUFFER_INDEX = "buffer_index";
	
	/** The Constant ATT_FILE_BUFFER_CONTENT. */
	public static final String ATT_FILE_BUFFER_CONTENT = "buffer_content";
	
	
	// Attributes of OPERATOR element
	/** The Constant ATT_OP_NAME. */
	// public static final String ATT_OP_CATEGORY = "category";
	public static final String ATT_OP_NAME = "name";
	
	/** The Constant ATT_OP_ID. */
	public static final String ATT_OP_ID = "id";
	
	/** The Constant ATT_OP_ALT_NAME. */
	public static final String ATT_OP_ALT_NAME = "alternativeNameOf";
	
	/** The Constant ATT_ALPHABET_ORDER. */
	public static final String ATT_ALPHABET_ORDER = "alphabetOrder";

	/** The Constant ATT_OPERANDS_CLASS. */
	// Attributes of OPERANDS element
	public static final String ATT_OPERANDS_CLASS = "class";
	
	/** The Constant ATT_OPERANDS_CONTENT_TYPE. */
	public static final String ATT_OPERANDS_CONTENT_TYPE = "contentType";
	
	/** The Constant ATT_OPERANDS_RETURN_TYPE. */
	public static final String ATT_OPERANDS_RETURN_TYPE = "returnType";
	
	/** The Constant ATT_OPERANDS_TYPE. */
	public static final String ATT_OPERANDS_TYPE = "type";

	/** The Constant ATT_OPERAND_NAME. */
	// Attributes of OPERAND element
	public static final String ATT_OPERAND_NAME = "name";
	
	/** The Constant ATT_OPERAND_POSITION. */
	public static final String ATT_OPERAND_POSITION = "position";
	
	/** The Constant ATT_OPERAND_TYPE. */
	public static final String ATT_OPERAND_TYPE = "type";

	/** The Constant ATT_USAGE_DESC. */
	// Attributes of USAGE element
	public static final String ATT_USAGE_DESC = "descUsageElt";

	/** The Constant ATT_EXAMPLE_CODE. */
	// Attributes of EXAMPLE element
	public static final String ATT_EXAMPLE_CODE = "code";
	
	/** The Constant ATT_EXAMPLE_IS_TEST_ONLY. */
	public static final String ATT_EXAMPLE_IS_TEST_ONLY = "isTestOnly";
	
	/** The Constant ATT_EXAMPLE_IS_EXECUTABLE. */
	public static final String ATT_EXAMPLE_IS_EXECUTABLE = "isExecutable";
	
	/** The Constant ATT_EXAMPLE_IS_TESTABLE. */
	public static final String ATT_EXAMPLE_IS_TESTABLE = "test";
	
	/** The Constant ATT_EXAMPLE_VAR. */
	public static final String ATT_EXAMPLE_VAR = "var";
	
	/** The Constant ATT_EXAMPLE_EQUALS. */
	public static final String ATT_EXAMPLE_EQUALS = "equals";
	
	/** The Constant ATT_EXAMPLE_IS_NOT. */
	public static final String ATT_EXAMPLE_IS_NOT = "isNot";
	
	/** The Constant ATT_EXAMPLE_RAISES. */
	public static final String ATT_EXAMPLE_RAISES = "raises";
	
	/** The Constant ATT_EXAMPLE_INDEX. */
	public static final String ATT_EXAMPLE_INDEX = "index";
	// public static final String ATT_EXAMPLE_RETURN_TYPE = "returnType";

	/** The Constant ATT_EXAMPLE_TYPE. */
	public static final String ATT_EXAMPLE_TYPE = "type";

	/** The Constant ATT_FACET_NAME. */
	// Attributes of FACET element
	public static final String ATT_FACET_NAME = "name";
	
	/** The Constant ATT_FACET_TYPE. */
	public static final String ATT_FACET_TYPE = "type";
	
	/** The Constant ATT_FACET_VALUES. */
	public static final String ATT_FACET_VALUES = "values";
	
	/** The Constant ATT_FACET_OPTIONAL. */
	public static final String ATT_FACET_OPTIONAL = "optional";
	
	/** The Constant ATT_FACET_OMISSIBLE. */
	public static final String ATT_FACET_OMISSIBLE = "omissible";

	/** The Constant ATT_CST_NAME. */
	// Attributes of CONSTANT element
	public static final String ATT_CST_NAME = "name";
	
	/** The Constant ATT_CST_VALUE. */
	public static final String ATT_CST_VALUE = "value";
	
	/** The Constant ATT_CST_NAMES. */
	public static final String ATT_CST_NAMES = "altNames";

	/** The Constant ATT_RES_MASTER. */
	// Attributes of RESULT element
	public static final String ATT_RES_MASTER = "masterDoc";

	/** The Constant ATT_RET_MASTER. */
	// Attributes of RETURNS element
	public static final String ATT_RET_MASTER = "masterDoc";
	
	
	/** The Constant ATT_SEE_ID. */
	// Attributes of SEE element
	public static final String ATT_SEE_ID = "id";

	/** The Constant ATT_ACTION_NAME. */
	// Attributes of ACTION element
	public static final String ATT_ACTION_NAME = "name";
	
	/** The Constant ATT_ACTION_RETURNTYPE. */
	public static final String ATT_ACTION_RETURNTYPE = "returnType";

	/** The Constant ATT_ARG_NAME. */
	// Attributes of ARG element
	public static final String ATT_ARG_NAME = "name";
	
	/** The Constant ATT_ARG_TYPE. */
	public static final String ATT_ARG_TYPE = "type";
	
	/** The Constant ATT_ARG_OPTIONAL. */
	public static final String ATT_ARG_OPTIONAL = "optional";

	/** The Constant ATT_SKILL_ID. */
	// Attributes of SKILL element
	public static final String ATT_SKILL_ID = "id";
	
	/** The Constant ATT_SKILL_NAME. */
	public static final String ATT_SKILL_NAME = "name";
	
	/** The Constant ATT_SKILL_CLASS. */
	public static final String ATT_SKILL_CLASS = "class";
	
	/** The Constant ATT_SKILL_EXTENDS. */
	public static final String ATT_SKILL_EXTENDS = "extends";

	/** The Constant ATT_ARCHI_ID. */
	// Attributes of ARCHITECTURE element
	public static final String ATT_ARCHI_ID = "id";
	
	/** The Constant ATT_ARCHI_NAME. */
	public static final String ATT_ARCHI_NAME = "name";

	/** The Constant ATT_VAR_NAME. */
	// Attributes of VAR element
	public static final String ATT_VAR_NAME = "name";
	
	/** The Constant ATT_VAR_TYPE. */
	public static final String ATT_VAR_TYPE = "type";
	
	/** The Constant ATT_VAR_CONSTANT. */
	public static final String ATT_VAR_CONSTANT = "constant";
	
	/** The Constant ATT_VAR_DEPENDS_ON. */
	public static final String ATT_VAR_DEPENDS_ON = "depends_on";

	/** The Constant ATT_SPECIES_ID. */
	// Attributes of SPECIES element
	public static final String ATT_SPECIES_ID = "id";
	
	/** The Constant ATT_SPECIES_NAME. */
	public static final String ATT_SPECIES_NAME = "name";
	
	/** The Constant ATT_SPECIES_SKILL. */
	public static final String ATT_SPECIES_SKILL = "name";

	/** The Constant ATT_STAT_ID. */
	// Attributes of STATEMENT element
	public static final String ATT_STAT_ID = "id";
	
	/** The Constant ATT_STAT_NAME. */
	public static final String ATT_STAT_NAME = "name";
	
	/** The Constant ATT_STAT_KIND. */
	public static final String ATT_STAT_KIND = "kind";
	
	/** The Constant ATT_STAT_ALT_NAME_OF. */
	public static final String ATT_STAT_ALT_NAME_OF = "alt_name_of";	

	/** The Constant ATT_TYPE_NAME. */
	// Attributes of TYPE element
	public static final String ATT_TYPE_NAME = "name";
	
	/** The Constant ATT_TYPE_ID. */
	public static final String ATT_TYPE_ID = "id";
	
	/** The Constant ATT_TYPE_KIND. */
	public static final String ATT_TYPE_KIND = "kind";

	/** The Constant ATT_INSIDE_STAT_SYMBOL. */
	// Attributes of INSIDE_STAT element
	public static final String ATT_INSIDE_STAT_SYMBOL = "symbol";

	/** The Constant ATT_KIND_STAT. */
	// Attributes of KIND element
	public static final String ATT_KIND_STAT = "symbol";

}
