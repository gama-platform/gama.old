package msi.gama.precompiler.doc.utils;

public class XMLElements {
	public static final String DOC = "doc";
	public static final String OPERATORS_CATEGORIES = "operatorsCategories";
	public static final String CATEGORY = "category";
	public static final String OPERATORS = "operators";
	public static final String OPERATOR = "operator";
	public static final String COMBINAISON_IO = "combinaisonIO";
	public static final String OPERANDS = "operands";
	public static final String OPERAND = "operand";
	public static final String DOCUMENTATION = "documentation";
	public static final String RESULT = "result";
	public static final String USAGES = "usages";
	public static final String USAGE = "usage";
	public static final String EXAMPLES = "examples";
	public static final String EXAMPLE = "example";

	// Attributes of CATEGORY element
	public static final String ATT_CAT_ID = "id";

	// Attributes of OPERATOR element
	public static final String ATT_OP_CATEGORY = "category";
	public static final String ATT_OP_NAME = "name";
	public static final String ATT_OP_ID = "id";
	
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
	public static final String ATT_EXAMPLE_VAR = "var";	
	public static final String ATT_EXAMPLE_EQUALS = "equals";	
	public static final String ATT_EXAMPLE_IS_NOT = "isNot";	
	public static final String ATT_EXAMPLE_RAISES = "raises";	
	
	public static final String ATT_EXAMPLE_TYPE = "type";	
}
