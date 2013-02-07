/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see msi.gama.lang.gaml.gaml.GamlFactory
 * @model kind="package"
 * @generated
 */
public interface GamlPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "gaml";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.gama.msi/lang/gaml/Gaml";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "gaml";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  GamlPackage eINSTANCE = msi.gama.lang.gaml.gaml.impl.GamlPackageImpl.init();

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ModelImpl <em>Model</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ModelImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getModel()
   * @generated
   */
  int MODEL = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__NAME = 0;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__IMPORTS = 1;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__STATEMENTS = 2;

  /**
   * The number of structural features of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ImportImpl <em>Import</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ImportImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getImport()
   * @generated
   */
  int IMPORT = 1;

  /**
   * The feature id for the '<em><b>Import URI</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT__IMPORT_URI = 0;

  /**
   * The number of structural features of the '<em>Import</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int IMPORT_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl <em>Var Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlVarRef()
   * @generated
   */
  int GAML_VAR_REF = 12;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF__KEY = 0;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF__EXPR = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF__NAME = 2;

  /**
   * The number of structural features of the '<em>Var Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StatementImpl <em>Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StatementImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStatement()
   * @generated
   */
  int STATEMENT = 2;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__KEY = GAML_VAR_REF__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__EXPR = GAML_VAR_REF__EXPR;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__NAME = GAML_VAR_REF__NAME;

  /**
   * The feature id for the '<em><b>Function</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__FUNCTION = GAML_VAR_REF_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__BLOCK = GAML_VAR_REF_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Else</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__ELSE = GAML_VAR_REF_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__FACETS = GAML_VAR_REF_FEATURE_COUNT + 3;

  /**
   * The feature id for the '<em><b>Of</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__OF = GAML_VAR_REF_FEATURE_COUNT + 4;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__ARGS = GAML_VAR_REF_FEATURE_COUNT + 5;

  /**
   * The feature id for the '<em><b>Params</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__PARAMS = GAML_VAR_REF_FEATURE_COUNT + 6;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__VALUE = GAML_VAR_REF_FEATURE_COUNT + 7;

  /**
   * The number of structural features of the '<em>Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT_FEATURE_COUNT = GAML_VAR_REF_FEATURE_COUNT + 8;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ContentsImpl <em>Contents</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ContentsImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getContents()
   * @generated
   */
  int CONTENTS = 3;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTENTS__TYPE = 0;

  /**
   * The feature id for the '<em><b>Type2</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTENTS__TYPE2 = 1;

  /**
   * The number of structural features of the '<em>Contents</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONTENTS_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ExpressionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 9;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__LEFT = 0;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__OP = 1;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__RIGHT = 2;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ParametersImpl <em>Parameters</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ParametersImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getParameters()
   * @generated
   */
  int PARAMETERS = 4;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERS__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERS__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERS__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Params</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERS__PARAMS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Parameters</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETERS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ActionArgumentsImpl <em>Action Arguments</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ActionArgumentsImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getActionArguments()
   * @generated
   */
  int ACTION_ARGUMENTS = 5;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTION_ARGUMENTS__ARGS = 0;

  /**
   * The number of structural features of the '<em>Action Arguments</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTION_ARGUMENTS_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ArgumentDefinitionImpl <em>Argument Definition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ArgumentDefinitionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArgumentDefinition()
   * @generated
   */
  int ARGUMENT_DEFINITION = 6;

  /**
   * The feature id for the '<em><b>Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARGUMENT_DEFINITION__TYPE = 0;

  /**
   * The feature id for the '<em><b>Of</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARGUMENT_DEFINITION__OF = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARGUMENT_DEFINITION__NAME = 2;

  /**
   * The feature id for the '<em><b>Default</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARGUMENT_DEFINITION__DEFAULT = 3;

  /**
   * The number of structural features of the '<em>Argument Definition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARGUMENT_DEFINITION_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FacetImpl <em>Facet</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FacetImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacet()
   * @generated
   */
  int FACET = 7;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET__KEY = GAML_VAR_REF__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET__EXPR = GAML_VAR_REF__EXPR;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET__NAME = GAML_VAR_REF__NAME;

  /**
   * The number of structural features of the '<em>Facet</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_FEATURE_COUNT = GAML_VAR_REF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.BlockImpl <em>Block</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.BlockImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBlock()
   * @generated
   */
  int BLOCK = 8;

  /**
   * The feature id for the '<em><b>Function</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BLOCK__FUNCTION = 0;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BLOCK__STATEMENTS = 1;

  /**
   * The number of structural features of the '<em>Block</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BLOCK_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PairExprImpl <em>Pair Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PairExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPairExpr()
   * @generated
   */
  int PAIR_EXPR = 10;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR_EXPR__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR_EXPR__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR_EXPR__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Pair Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR_EXPR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ExpressionListImpl <em>Expression List</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ExpressionListImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getExpressionList()
   * @generated
   */
  int EXPRESSION_LIST = 11;

  /**
   * The feature id for the '<em><b>Exprs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_LIST__EXPRS = 0;

  /**
   * The number of structural features of the '<em>Expression List</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_LIST_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl <em>Terminal Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTerminalExpression()
   * @generated
   */
  int TERMINAL_EXPRESSION = 13;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION__VALUE = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Terminal Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StringEvaluatorImpl <em>String Evaluator</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StringEvaluatorImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStringEvaluator()
   * @generated
   */
  int STRING_EVALUATOR = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_EVALUATOR__NAME = MODEL__NAME;

  /**
   * The feature id for the '<em><b>Imports</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_EVALUATOR__IMPORTS = MODEL__IMPORTS;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_EVALUATOR__STATEMENTS = MODEL__STATEMENTS;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_EVALUATOR__EXPR = MODEL_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Evaluator</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_EVALUATOR_FEATURE_COUNT = MODEL_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.TernExpImpl <em>Tern Exp</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.TernExpImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTernExp()
   * @generated
   */
  int TERN_EXP = 15;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERN_EXP__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERN_EXP__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERN_EXP__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>If False</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERN_EXP__IF_FALSE = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Tern Exp</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERN_EXP_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ArgPairExprImpl <em>Arg Pair Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ArgPairExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArgPairExpr()
   * @generated
   */
  int ARG_PAIR_EXPR = 16;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARG_PAIR_EXPR__LEFT = PAIR_EXPR__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARG_PAIR_EXPR__OP = PAIR_EXPR__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARG_PAIR_EXPR__RIGHT = PAIR_EXPR__RIGHT;

  /**
   * The feature id for the '<em><b>Arg</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARG_PAIR_EXPR__ARG = PAIR_EXPR_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Arg Pair Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARG_PAIR_EXPR_FEATURE_COUNT = PAIR_EXPR_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl <em>Binary Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinaryExpr()
   * @generated
   */
  int GAML_BINARY_EXPR = 17;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY_EXPR__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY_EXPR__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY_EXPR__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Binary Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY_EXPR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnitExprImpl <em>Unit Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlUnitExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnitExpr()
   * @generated
   */
  int GAML_UNIT_EXPR = 18;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_EXPR__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_EXPR__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_EXPR__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Unit Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_EXPR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnaryExprImpl <em>Unary Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlUnaryExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnaryExpr()
   * @generated
   */
  int GAML_UNARY_EXPR = 19;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY_EXPR__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY_EXPR__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY_EXPR__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Unary Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY_EXPR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AccessImpl <em>Access</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AccessImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAccess()
   * @generated
   */
  int ACCESS = 20;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS__ARGS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Access</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCESS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefImpl <em>Member Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MemberRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRef()
   * @generated
   */
  int MEMBER_REF = 21;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Member Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ArrayImpl <em>Array</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ArrayImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArray()
   * @generated
   */
  int ARRAY = 22;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Exprs</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY__EXPRS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Array</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PointImpl <em>Point</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PointImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPoint()
   * @generated
   */
  int POINT = 23;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Z</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__Z = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Point</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionImpl <em>Function</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FunctionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunction()
   * @generated
   */
  int FUNCTION = 24;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION__ARGS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Function</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ParameterImpl <em>Parameter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ParameterImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getParameter()
   * @generated
   */
  int PARAMETER = 25;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Built In Facet Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER__BUILT_IN_FACET_KEY = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Parameter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARAMETER_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.UnitNameImpl <em>Unit Name</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.UnitNameImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getUnitName()
   * @generated
   */
  int UNIT_NAME = 26;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME__RIGHT = EXPRESSION__RIGHT;

  /**
   * The number of structural features of the '<em>Unit Name</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.VariableRefImpl <em>Variable Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.VariableRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getVariableRef()
   * @generated
   */
  int VARIABLE_REF = 27;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_REF__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_REF__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_REF__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_REF__REF = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Variable Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int VARIABLE_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.IntLiteralImpl <em>Int Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.IntLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getIntLiteral()
   * @generated
   */
  int INT_LITERAL = 28;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL__LEFT = TERMINAL_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL__OP = TERMINAL_EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL__RIGHT = TERMINAL_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL__VALUE = TERMINAL_EXPRESSION__VALUE;

  /**
   * The number of structural features of the '<em>Int Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl <em>Double Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDoubleLiteral()
   * @generated
   */
  int DOUBLE_LITERAL = 29;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL__LEFT = TERMINAL_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL__OP = TERMINAL_EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL__RIGHT = TERMINAL_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL__VALUE = TERMINAL_EXPRESSION__VALUE;

  /**
   * The number of structural features of the '<em>Double Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl <em>Color Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getColorLiteral()
   * @generated
   */
  int COLOR_LITERAL = 30;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL__LEFT = TERMINAL_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL__OP = TERMINAL_EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL__RIGHT = TERMINAL_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL__VALUE = TERMINAL_EXPRESSION__VALUE;

  /**
   * The number of structural features of the '<em>Color Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StringLiteralImpl <em>String Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StringLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStringLiteral()
   * @generated
   */
  int STRING_LITERAL = 31;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL__LEFT = TERMINAL_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL__OP = TERMINAL_EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL__RIGHT = TERMINAL_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL__VALUE = TERMINAL_EXPRESSION__VALUE;

  /**
   * The number of structural features of the '<em>String Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBooleanLiteral()
   * @generated
   */
  int BOOLEAN_LITERAL = 32;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL__LEFT = TERMINAL_EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL__OP = TERMINAL_EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL__RIGHT = TERMINAL_EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL__VALUE = TERMINAL_EXPRESSION__VALUE;

  /**
   * The number of structural features of the '<em>Boolean Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;


  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Model</em>'.
   * @see msi.gama.lang.gaml.gaml.Model
   * @generated
   */
  EClass getModel();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Model#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.Model#getName()
   * @see #getModel()
   * @generated
   */
  EAttribute getModel_Name();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Model#getImports <em>Imports</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Imports</em>'.
   * @see msi.gama.lang.gaml.gaml.Model#getImports()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Imports();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Model#getStatements <em>Statements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Statements</em>'.
   * @see msi.gama.lang.gaml.gaml.Model#getStatements()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Statements();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Import</em>'.
   * @see msi.gama.lang.gaml.gaml.Import
   * @generated
   */
  EClass getImport();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Import#getImportURI <em>Import URI</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Import URI</em>'.
   * @see msi.gama.lang.gaml.gaml.Import#getImportURI()
   * @see #getImport()
   * @generated
   */
  EAttribute getImport_ImportURI();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Statement <em>Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Statement</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement
   * @generated
   */
  EClass getStatement();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getFunction <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Function</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getFunction()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Function();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getBlock <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Block</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getBlock()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Block();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getElse <em>Else</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Else</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getElse()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Else();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Statement#getFacets <em>Facets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Facets</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getFacets()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Facets();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getOf <em>Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Of</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getOf()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Of();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getArgs()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Args();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getParams <em>Params</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Params</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getParams()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Params();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getValue()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Value();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Contents <em>Contents</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Contents</em>'.
   * @see msi.gama.lang.gaml.gaml.Contents
   * @generated
   */
  EClass getContents();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Contents#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see msi.gama.lang.gaml.gaml.Contents#getType()
   * @see #getContents()
   * @generated
   */
  EAttribute getContents_Type();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Contents#getType2 <em>Type2</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type2</em>'.
   * @see msi.gama.lang.gaml.gaml.Contents#getType2()
   * @see #getContents()
   * @generated
   */
  EAttribute getContents_Type2();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Parameters <em>Parameters</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parameters</em>'.
   * @see msi.gama.lang.gaml.gaml.Parameters
   * @generated
   */
  EClass getParameters();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Parameters#getParams <em>Params</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Params</em>'.
   * @see msi.gama.lang.gaml.gaml.Parameters#getParams()
   * @see #getParameters()
   * @generated
   */
  EReference getParameters_Params();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ActionArguments <em>Action Arguments</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Action Arguments</em>'.
   * @see msi.gama.lang.gaml.gaml.ActionArguments
   * @generated
   */
  EClass getActionArguments();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.ActionArguments#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.ActionArguments#getArgs()
   * @see #getActionArguments()
   * @generated
   */
  EReference getActionArguments_Args();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ArgumentDefinition <em>Argument Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Argument Definition</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgumentDefinition
   * @generated
   */
  EClass getArgumentDefinition();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.ArgumentDefinition#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Type</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgumentDefinition#getType()
   * @see #getArgumentDefinition()
   * @generated
   */
  EAttribute getArgumentDefinition_Type();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.ArgumentDefinition#getOf <em>Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Of</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgumentDefinition#getOf()
   * @see #getArgumentDefinition()
   * @generated
   */
  EReference getArgumentDefinition_Of();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.ArgumentDefinition#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgumentDefinition#getName()
   * @see #getArgumentDefinition()
   * @generated
   */
  EAttribute getArgumentDefinition_Name();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.ArgumentDefinition#getDefault <em>Default</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Default</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgumentDefinition#getDefault()
   * @see #getArgumentDefinition()
   * @generated
   */
  EReference getArgumentDefinition_Default();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Facet <em>Facet</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Facet</em>'.
   * @see msi.gama.lang.gaml.gaml.Facet
   * @generated
   */
  EClass getFacet();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Block <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Block</em>'.
   * @see msi.gama.lang.gaml.gaml.Block
   * @generated
   */
  EClass getBlock();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Block#getFunction <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Function</em>'.
   * @see msi.gama.lang.gaml.gaml.Block#getFunction()
   * @see #getBlock()
   * @generated
   */
  EReference getBlock_Function();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Block#getStatements <em>Statements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Statements</em>'.
   * @see msi.gama.lang.gaml.gaml.Block#getStatements()
   * @see #getBlock()
   * @generated
   */
  EReference getBlock_Statements();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see msi.gama.lang.gaml.gaml.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Expression#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Expression#getLeft()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Left();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Expression#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see msi.gama.lang.gaml.gaml.Expression#getOp()
   * @see #getExpression()
   * @generated
   */
  EAttribute getExpression_Op();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Expression#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Expression#getRight()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.PairExpr <em>Pair Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pair Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.PairExpr
   * @generated
   */
  EClass getPairExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ExpressionList <em>Expression List</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression List</em>'.
   * @see msi.gama.lang.gaml.gaml.ExpressionList
   * @generated
   */
  EClass getExpressionList();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.ExpressionList#getExprs <em>Exprs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Exprs</em>'.
   * @see msi.gama.lang.gaml.gaml.ExpressionList#getExprs()
   * @see #getExpressionList()
   * @generated
   */
  EReference getExpressionList_Exprs();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlVarRef <em>Var Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Var Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef
   * @generated
   */
  EClass getGamlVarRef();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.GamlVarRef#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef#getKey()
   * @see #getGamlVarRef()
   * @generated
   */
  EAttribute getGamlVarRef_Key();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.GamlVarRef#getExpr <em>Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef#getExpr()
   * @see #getGamlVarRef()
   * @generated
   */
  EReference getGamlVarRef_Expr();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.GamlVarRef#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef#getName()
   * @see #getGamlVarRef()
   * @generated
   */
  EAttribute getGamlVarRef_Name();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.TerminalExpression <em>Terminal Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Terminal Expression</em>'.
   * @see msi.gama.lang.gaml.gaml.TerminalExpression
   * @generated
   */
  EClass getTerminalExpression();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.TerminalExpression#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.TerminalExpression#getValue()
   * @see #getTerminalExpression()
   * @generated
   */
  EAttribute getTerminalExpression_Value();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.StringEvaluator <em>String Evaluator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Evaluator</em>'.
   * @see msi.gama.lang.gaml.gaml.StringEvaluator
   * @generated
   */
  EClass getStringEvaluator();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.StringEvaluator#getExpr <em>Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.StringEvaluator#getExpr()
   * @see #getStringEvaluator()
   * @generated
   */
  EReference getStringEvaluator_Expr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.TernExp <em>Tern Exp</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Tern Exp</em>'.
   * @see msi.gama.lang.gaml.gaml.TernExp
   * @generated
   */
  EClass getTernExp();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.TernExp#getIfFalse <em>If False</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>If False</em>'.
   * @see msi.gama.lang.gaml.gaml.TernExp#getIfFalse()
   * @see #getTernExp()
   * @generated
   */
  EReference getTernExp_IfFalse();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ArgPairExpr <em>Arg Pair Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Arg Pair Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgPairExpr
   * @generated
   */
  EClass getArgPairExpr();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.ArgPairExpr#getArg <em>Arg</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Arg</em>'.
   * @see msi.gama.lang.gaml.gaml.ArgPairExpr#getArg()
   * @see #getArgPairExpr()
   * @generated
   */
  EAttribute getArgPairExpr_Arg();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlBinaryExpr <em>Binary Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Binary Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinaryExpr
   * @generated
   */
  EClass getGamlBinaryExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlUnitExpr <em>Unit Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unit Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnitExpr
   * @generated
   */
  EClass getGamlUnitExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlUnaryExpr <em>Unary Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unary Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnaryExpr
   * @generated
   */
  EClass getGamlUnaryExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Access <em>Access</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Access</em>'.
   * @see msi.gama.lang.gaml.gaml.Access
   * @generated
   */
  EClass getAccess();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Access#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.Access#getArgs()
   * @see #getAccess()
   * @generated
   */
  EReference getAccess_Args();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.MemberRef <em>Member Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRef
   * @generated
   */
  EClass getMemberRef();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Array <em>Array</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Array</em>'.
   * @see msi.gama.lang.gaml.gaml.Array
   * @generated
   */
  EClass getArray();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Array#getExprs <em>Exprs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Exprs</em>'.
   * @see msi.gama.lang.gaml.gaml.Array#getExprs()
   * @see #getArray()
   * @generated
   */
  EReference getArray_Exprs();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Point <em>Point</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Point</em>'.
   * @see msi.gama.lang.gaml.gaml.Point
   * @generated
   */
  EClass getPoint();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Point#getZ <em>Z</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Z</em>'.
   * @see msi.gama.lang.gaml.gaml.Point#getZ()
   * @see #getPoint()
   * @generated
   */
  EReference getPoint_Z();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Function <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function</em>'.
   * @see msi.gama.lang.gaml.gaml.Function
   * @generated
   */
  EClass getFunction();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Function#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.Function#getArgs()
   * @see #getFunction()
   * @generated
   */
  EReference getFunction_Args();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Parameter <em>Parameter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parameter</em>'.
   * @see msi.gama.lang.gaml.gaml.Parameter
   * @generated
   */
  EClass getParameter();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Parameter#getBuiltInFacetKey <em>Built In Facet Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Built In Facet Key</em>'.
   * @see msi.gama.lang.gaml.gaml.Parameter#getBuiltInFacetKey()
   * @see #getParameter()
   * @generated
   */
  EAttribute getParameter_BuiltInFacetKey();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.UnitName <em>Unit Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unit Name</em>'.
   * @see msi.gama.lang.gaml.gaml.UnitName
   * @generated
   */
  EClass getUnitName();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.VariableRef <em>Variable Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Variable Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.VariableRef
   * @generated
   */
  EClass getVariableRef();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.VariableRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.VariableRef#getRef()
   * @see #getVariableRef()
   * @generated
   */
  EReference getVariableRef_Ref();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.IntLiteral <em>Int Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Int Literal</em>'.
   * @see msi.gama.lang.gaml.gaml.IntLiteral
   * @generated
   */
  EClass getIntLiteral();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DoubleLiteral <em>Double Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Double Literal</em>'.
   * @see msi.gama.lang.gaml.gaml.DoubleLiteral
   * @generated
   */
  EClass getDoubleLiteral();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ColorLiteral <em>Color Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Color Literal</em>'.
   * @see msi.gama.lang.gaml.gaml.ColorLiteral
   * @generated
   */
  EClass getColorLiteral();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.StringLiteral <em>String Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Literal</em>'.
   * @see msi.gama.lang.gaml.gaml.StringLiteral
   * @generated
   */
  EClass getStringLiteral();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.BooleanLiteral <em>Boolean Literal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Boolean Literal</em>'.
   * @see msi.gama.lang.gaml.gaml.BooleanLiteral
   * @generated
   */
  EClass getBooleanLiteral();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  GamlFactory getGamlFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ModelImpl <em>Model</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ModelImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getModel()
     * @generated
     */
    EClass MODEL = eINSTANCE.getModel();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODEL__NAME = eINSTANCE.getModel_Name();

    /**
     * The meta object literal for the '<em><b>Imports</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__IMPORTS = eINSTANCE.getModel_Imports();

    /**
     * The meta object literal for the '<em><b>Statements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__STATEMENTS = eINSTANCE.getModel_Statements();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ImportImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getImport()
     * @generated
     */
    EClass IMPORT = eINSTANCE.getImport();

    /**
     * The meta object literal for the '<em><b>Import URI</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute IMPORT__IMPORT_URI = eINSTANCE.getImport_ImportURI();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.StatementImpl <em>Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.StatementImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStatement()
     * @generated
     */
    EClass STATEMENT = eINSTANCE.getStatement();

    /**
     * The meta object literal for the '<em><b>Function</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__FUNCTION = eINSTANCE.getStatement_Function();

    /**
     * The meta object literal for the '<em><b>Block</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__BLOCK = eINSTANCE.getStatement_Block();

    /**
     * The meta object literal for the '<em><b>Else</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__ELSE = eINSTANCE.getStatement_Else();

    /**
     * The meta object literal for the '<em><b>Facets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__FACETS = eINSTANCE.getStatement_Facets();

    /**
     * The meta object literal for the '<em><b>Of</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__OF = eINSTANCE.getStatement_Of();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__ARGS = eINSTANCE.getStatement_Args();

    /**
     * The meta object literal for the '<em><b>Params</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__PARAMS = eINSTANCE.getStatement_Params();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__VALUE = eINSTANCE.getStatement_Value();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ContentsImpl <em>Contents</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ContentsImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getContents()
     * @generated
     */
    EClass CONTENTS = eINSTANCE.getContents();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTENTS__TYPE = eINSTANCE.getContents_Type();

    /**
     * The meta object literal for the '<em><b>Type2</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONTENTS__TYPE2 = eINSTANCE.getContents_Type2();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ParametersImpl <em>Parameters</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ParametersImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getParameters()
     * @generated
     */
    EClass PARAMETERS = eINSTANCE.getParameters();

    /**
     * The meta object literal for the '<em><b>Params</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARAMETERS__PARAMS = eINSTANCE.getParameters_Params();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ActionArgumentsImpl <em>Action Arguments</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ActionArgumentsImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getActionArguments()
     * @generated
     */
    EClass ACTION_ARGUMENTS = eINSTANCE.getActionArguments();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACTION_ARGUMENTS__ARGS = eINSTANCE.getActionArguments_Args();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ArgumentDefinitionImpl <em>Argument Definition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ArgumentDefinitionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArgumentDefinition()
     * @generated
     */
    EClass ARGUMENT_DEFINITION = eINSTANCE.getArgumentDefinition();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ARGUMENT_DEFINITION__TYPE = eINSTANCE.getArgumentDefinition_Type();

    /**
     * The meta object literal for the '<em><b>Of</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARGUMENT_DEFINITION__OF = eINSTANCE.getArgumentDefinition_Of();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ARGUMENT_DEFINITION__NAME = eINSTANCE.getArgumentDefinition_Name();

    /**
     * The meta object literal for the '<em><b>Default</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARGUMENT_DEFINITION__DEFAULT = eINSTANCE.getArgumentDefinition_Default();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FacetImpl <em>Facet</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FacetImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacet()
     * @generated
     */
    EClass FACET = eINSTANCE.getFacet();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.BlockImpl <em>Block</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.BlockImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBlock()
     * @generated
     */
    EClass BLOCK = eINSTANCE.getBlock();

    /**
     * The meta object literal for the '<em><b>Function</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BLOCK__FUNCTION = eINSTANCE.getBlock_Function();

    /**
     * The meta object literal for the '<em><b>Statements</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference BLOCK__STATEMENTS = eINSTANCE.getBlock_Statements();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ExpressionImpl <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ExpressionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getExpression()
     * @generated
     */
    EClass EXPRESSION = eINSTANCE.getExpression();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__LEFT = eINSTANCE.getExpression_Left();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EXPRESSION__OP = eINSTANCE.getExpression_Op();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__RIGHT = eINSTANCE.getExpression_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PairExprImpl <em>Pair Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PairExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPairExpr()
     * @generated
     */
    EClass PAIR_EXPR = eINSTANCE.getPairExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ExpressionListImpl <em>Expression List</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ExpressionListImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getExpressionList()
     * @generated
     */
    EClass EXPRESSION_LIST = eINSTANCE.getExpressionList();

    /**
     * The meta object literal for the '<em><b>Exprs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION_LIST__EXPRS = eINSTANCE.getExpressionList_Exprs();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl <em>Var Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlVarRef()
     * @generated
     */
    EClass GAML_VAR_REF = eINSTANCE.getGamlVarRef();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GAML_VAR_REF__KEY = eINSTANCE.getGamlVarRef_Key();

    /**
     * The meta object literal for the '<em><b>Expr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_VAR_REF__EXPR = eINSTANCE.getGamlVarRef_Expr();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GAML_VAR_REF__NAME = eINSTANCE.getGamlVarRef_Name();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl <em>Terminal Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTerminalExpression()
     * @generated
     */
    EClass TERMINAL_EXPRESSION = eINSTANCE.getTerminalExpression();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TERMINAL_EXPRESSION__VALUE = eINSTANCE.getTerminalExpression_Value();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.StringEvaluatorImpl <em>String Evaluator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.StringEvaluatorImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStringEvaluator()
     * @generated
     */
    EClass STRING_EVALUATOR = eINSTANCE.getStringEvaluator();

    /**
     * The meta object literal for the '<em><b>Expr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STRING_EVALUATOR__EXPR = eINSTANCE.getStringEvaluator_Expr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.TernExpImpl <em>Tern Exp</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.TernExpImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTernExp()
     * @generated
     */
    EClass TERN_EXP = eINSTANCE.getTernExp();

    /**
     * The meta object literal for the '<em><b>If False</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TERN_EXP__IF_FALSE = eINSTANCE.getTernExp_IfFalse();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ArgPairExprImpl <em>Arg Pair Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ArgPairExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArgPairExpr()
     * @generated
     */
    EClass ARG_PAIR_EXPR = eINSTANCE.getArgPairExpr();

    /**
     * The meta object literal for the '<em><b>Arg</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ARG_PAIR_EXPR__ARG = eINSTANCE.getArgPairExpr_Arg();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl <em>Binary Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinaryExpr()
     * @generated
     */
    EClass GAML_BINARY_EXPR = eINSTANCE.getGamlBinaryExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnitExprImpl <em>Unit Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlUnitExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnitExpr()
     * @generated
     */
    EClass GAML_UNIT_EXPR = eINSTANCE.getGamlUnitExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnaryExprImpl <em>Unary Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlUnaryExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnaryExpr()
     * @generated
     */
    EClass GAML_UNARY_EXPR = eINSTANCE.getGamlUnaryExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AccessImpl <em>Access</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AccessImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAccess()
     * @generated
     */
    EClass ACCESS = eINSTANCE.getAccess();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCESS__ARGS = eINSTANCE.getAccess_Args();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefImpl <em>Member Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MemberRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRef()
     * @generated
     */
    EClass MEMBER_REF = eINSTANCE.getMemberRef();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ArrayImpl <em>Array</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ArrayImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArray()
     * @generated
     */
    EClass ARRAY = eINSTANCE.getArray();

    /**
     * The meta object literal for the '<em><b>Exprs</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARRAY__EXPRS = eINSTANCE.getArray_Exprs();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PointImpl <em>Point</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PointImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPoint()
     * @generated
     */
    EClass POINT = eINSTANCE.getPoint();

    /**
     * The meta object literal for the '<em><b>Z</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference POINT__Z = eINSTANCE.getPoint_Z();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionImpl <em>Function</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FunctionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunction()
     * @generated
     */
    EClass FUNCTION = eINSTANCE.getFunction();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FUNCTION__ARGS = eINSTANCE.getFunction_Args();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ParameterImpl <em>Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ParameterImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getParameter()
     * @generated
     */
    EClass PARAMETER = eINSTANCE.getParameter();

    /**
     * The meta object literal for the '<em><b>Built In Facet Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute PARAMETER__BUILT_IN_FACET_KEY = eINSTANCE.getParameter_BuiltInFacetKey();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.UnitNameImpl <em>Unit Name</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.UnitNameImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getUnitName()
     * @generated
     */
    EClass UNIT_NAME = eINSTANCE.getUnitName();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.VariableRefImpl <em>Variable Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.VariableRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getVariableRef()
     * @generated
     */
    EClass VARIABLE_REF = eINSTANCE.getVariableRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference VARIABLE_REF__REF = eINSTANCE.getVariableRef_Ref();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.IntLiteralImpl <em>Int Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.IntLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getIntLiteral()
     * @generated
     */
    EClass INT_LITERAL = eINSTANCE.getIntLiteral();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl <em>Double Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDoubleLiteral()
     * @generated
     */
    EClass DOUBLE_LITERAL = eINSTANCE.getDoubleLiteral();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl <em>Color Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getColorLiteral()
     * @generated
     */
    EClass COLOR_LITERAL = eINSTANCE.getColorLiteral();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.StringLiteralImpl <em>String Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.StringLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStringLiteral()
     * @generated
     */
    EClass STRING_LITERAL = eINSTANCE.getStringLiteral();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBooleanLiteral()
     * @generated
     */
    EClass BOOLEAN_LITERAL = eINSTANCE.getBooleanLiteral();

  }

} //GamlPackage
