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
   * The feature id for the '<em><b>Gaml</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__GAML = 2;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL__STATEMENTS = 3;

  /**
   * The number of structural features of the '<em>Model</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODEL_FEATURE_COUNT = 4;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl <em>Lang Def</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlLangDef()
   * @generated
   */
  int GAML_LANG_DEF = 2;

  /**
   * The feature id for the '<em><b>B</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__B = 0;

  /**
   * The feature id for the '<em><b>R</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__R = 1;

  /**
   * The feature id for the '<em><b>Unaries</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__UNARIES = 2;

  /**
   * The number of structural features of the '<em>Lang Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl <em>Def Binary Op</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefBinaryOp()
   * @generated
   */
  int DEF_BINARY_OP = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_BINARY_OP__NAME = 0;

  /**
   * The number of structural features of the '<em>Def Binary Op</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_BINARY_OP_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl <em>Var Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlVarRef()
   * @generated
   */
  int GAML_VAR_REF = 19;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF__NAME = 0;

  /**
   * The number of structural features of the '<em>Var Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_VAR_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefReservedImpl <em>Def Reserved</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefReservedImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefReserved()
   * @generated
   */
  int DEF_RESERVED = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED__NAME = GAML_VAR_REF__NAME;

  /**
   * The number of structural features of the '<em>Def Reserved</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED_FEATURE_COUNT = GAML_VAR_REF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefUnaryImpl <em>Def Unary</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefUnaryImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefUnary()
   * @generated
   */
  int DEF_UNARY = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_UNARY__NAME = GAML_VAR_REF__NAME;

  /**
   * The number of structural features of the '<em>Def Unary</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_UNARY_FEATURE_COUNT = GAML_VAR_REF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StatementImpl <em>Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StatementImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStatement()
   * @generated
   */
  int STATEMENT = 6;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__KEY = 0;

  /**
   * The feature id for the '<em><b>Ref</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__REF = 1;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__EXPR = 2;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__FACETS = 3;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__BLOCK = 4;

  /**
   * The feature id for the '<em><b>Else</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__ELSE = 5;

  /**
   * The number of structural features of the '<em>Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT_FEATURE_COUNT = 6;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefinitionImpl <em>Definition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefinitionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefinition()
   * @generated
   */
  int DEFINITION = 7;

  /**
   * The feature id for the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__KEY = STATEMENT__KEY;

  /**
   * The feature id for the '<em><b>Ref</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__REF = STATEMENT__REF;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__EXPR = STATEMENT__EXPR;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__FACETS = STATEMENT__FACETS;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__BLOCK = STATEMENT__BLOCK;

  /**
   * The feature id for the '<em><b>Else</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__ELSE = STATEMENT__ELSE;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__NAME = STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Definition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FEATURE_COUNT = STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FacetRefImpl <em>Facet Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FacetRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacetRef()
   * @generated
   */
  int FACET_REF = 8;

  /**
   * The feature id for the '<em><b>Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_REF__REF = 0;

  /**
   * The number of structural features of the '<em>Facet Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_REF_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl <em>Facet Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlFacetRef()
   * @generated
   */
  int GAML_FACET_REF = 9;

  /**
   * The feature id for the '<em><b>Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_FACET_REF__REF = FACET_REF__REF;

  /**
   * The number of structural features of the '<em>Facet Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_FACET_REF_FEATURE_COUNT = FACET_REF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionGamlFacetRefImpl <em>Function Gaml Facet Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FunctionGamlFacetRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionGamlFacetRef()
   * @generated
   */
  int FUNCTION_GAML_FACET_REF = 10;

  /**
   * The feature id for the '<em><b>Ref</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_GAML_FACET_REF__REF = FACET_REF__REF;

  /**
   * The number of structural features of the '<em>Function Gaml Facet Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_GAML_FACET_REF_FEATURE_COUNT = FACET_REF_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FacetExprImpl <em>Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacetExpr()
   * @generated
   */
  int FACET_EXPR = 11;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR__KEY = 0;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR__EXPR = 1;

  /**
   * The number of structural features of the '<em>Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefinitionFacetExprImpl <em>Definition Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefinitionFacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefinitionFacetExpr()
   * @generated
   */
  int DEFINITION_FACET_EXPR = 12;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FACET_EXPR__KEY = FACET_EXPR__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FACET_EXPR__EXPR = FACET_EXPR__EXPR;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FACET_EXPR__NAME = FACET_EXPR_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Definition Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FACET_EXPR_FEATURE_COUNT = FACET_EXPR_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.NameFacetExprImpl <em>Name Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.NameFacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getNameFacetExpr()
   * @generated
   */
  int NAME_FACET_EXPR = 13;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_FACET_EXPR__KEY = DEFINITION_FACET_EXPR__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_FACET_EXPR__EXPR = DEFINITION_FACET_EXPR__EXPR;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_FACET_EXPR__NAME = DEFINITION_FACET_EXPR__NAME;

  /**
   * The number of structural features of the '<em>Name Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NAME_FACET_EXPR_FEATURE_COUNT = DEFINITION_FACET_EXPR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ReturnsFacetExprImpl <em>Returns Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ReturnsFacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getReturnsFacetExpr()
   * @generated
   */
  int RETURNS_FACET_EXPR = 14;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RETURNS_FACET_EXPR__KEY = DEFINITION_FACET_EXPR__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RETURNS_FACET_EXPR__EXPR = DEFINITION_FACET_EXPR__EXPR;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RETURNS_FACET_EXPR__NAME = DEFINITION_FACET_EXPR__NAME;

  /**
   * The number of structural features of the '<em>Returns Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RETURNS_FACET_EXPR_FEATURE_COUNT = DEFINITION_FACET_EXPR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionFacetExprImpl <em>Function Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FunctionFacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionFacetExpr()
   * @generated
   */
  int FUNCTION_FACET_EXPR = 15;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_FACET_EXPR__KEY = FACET_EXPR__KEY;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_FACET_EXPR__EXPR = FACET_EXPR__EXPR;

  /**
   * The number of structural features of the '<em>Function Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_FACET_EXPR_FEATURE_COUNT = FACET_EXPR_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.BlockImpl <em>Block</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.BlockImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBlock()
   * @generated
   */
  int BLOCK = 16;

  /**
   * The feature id for the '<em><b>Statements</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BLOCK__STATEMENTS = 0;

  /**
   * The number of structural features of the '<em>Block</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BLOCK_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ExpressionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 17;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.VariableRefImpl <em>Variable Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.VariableRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getVariableRef()
   * @generated
   */
  int VARIABLE_REF = 18;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl <em>Terminal Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTerminalExpression()
   * @generated
   */
  int TERMINAL_EXPRESSION = 20;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.TernExpImpl <em>Tern Exp</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.TernExpImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTernExp()
   * @generated
   */
  int TERN_EXP = 21;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PairExprImpl <em>Pair Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PairExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPairExpr()
   * @generated
   */
  int PAIR_EXPR = 22;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl <em>Binary Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlBinaryExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinaryExpr()
   * @generated
   */
  int GAML_BINARY_EXPR = 23;

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
  int GAML_UNIT_EXPR = 24;

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
  int GAML_UNARY_EXPR = 25;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefImpl <em>Member Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MemberRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRef()
   * @generated
   */
  int MEMBER_REF = 26;

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
  int ARRAY = 27;

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
   * The feature id for the '<em><b>Exprs</b></em>' containment reference list.
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
  int POINT = 28;

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
   * The number of structural features of the '<em>Point</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionRefImpl <em>Function Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FunctionRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionRef()
   * @generated
   */
  int FUNCTION_REF = 29;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__ARGS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Function Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ArbitraryNameImpl <em>Arbitrary Name</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ArbitraryNameImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArbitraryName()
   * @generated
   */
  int ARBITRARY_NAME = 30;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARBITRARY_NAME__LEFT = EXPRESSION__LEFT;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARBITRARY_NAME__OP = EXPRESSION__OP;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARBITRARY_NAME__RIGHT = EXPRESSION__RIGHT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARBITRARY_NAME__NAME = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Arbitrary Name</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARBITRARY_NAME_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.UnitNameImpl <em>Unit Name</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.UnitNameImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getUnitName()
   * @generated
   */
  int UNIT_NAME = 31;

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
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME__NAME = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Unit Name</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_NAME_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.IntLiteralImpl <em>Int Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.IntLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getIntLiteral()
   * @generated
   */
  int INT_LITERAL = 32;

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
  int DOUBLE_LITERAL = 33;

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
  int COLOR_LITERAL = 34;

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
  int STRING_LITERAL = 35;

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
  int BOOLEAN_LITERAL = 36;

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
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Model#getGaml <em>Gaml</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Gaml</em>'.
   * @see msi.gama.lang.gaml.gaml.Model#getGaml()
   * @see #getModel()
   * @generated
   */
  EReference getModel_Gaml();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlLangDef <em>Lang Def</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Lang Def</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef
   * @generated
   */
  EClass getGamlLangDef();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getB <em>B</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>B</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getB()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_B();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getR <em>R</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>R</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getR()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_R();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getUnaries <em>Unaries</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Unaries</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getUnaries()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_Unaries();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefBinaryOp <em>Def Binary Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Binary Op</em>'.
   * @see msi.gama.lang.gaml.gaml.DefBinaryOp
   * @generated
   */
  EClass getDefBinaryOp();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DefBinaryOp#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.DefBinaryOp#getName()
   * @see #getDefBinaryOp()
   * @generated
   */
  EAttribute getDefBinaryOp_Name();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefReserved <em>Def Reserved</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Reserved</em>'.
   * @see msi.gama.lang.gaml.gaml.DefReserved
   * @generated
   */
  EClass getDefReserved();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefUnary <em>Def Unary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Unary</em>'.
   * @see msi.gama.lang.gaml.gaml.DefUnary
   * @generated
   */
  EClass getDefUnary();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.Statement#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Key</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getKey()
   * @see #getStatement()
   * @generated
   */
  EAttribute getStatement_Key();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getRef()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Ref();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Statement#getExpr <em>Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.Statement#getExpr()
   * @see #getStatement()
   * @generated
   */
  EReference getStatement_Expr();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Definition <em>Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Definition</em>'.
   * @see msi.gama.lang.gaml.gaml.Definition
   * @generated
   */
  EClass getDefinition();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.FacetRef <em>Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Facet Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.FacetRef
   * @generated
   */
  EClass getFacetRef();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.FacetRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.FacetRef#getRef()
   * @see #getFacetRef()
   * @generated
   */
  EAttribute getFacetRef_Ref();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlFacetRef <em>Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Facet Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlFacetRef
   * @generated
   */
  EClass getGamlFacetRef();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.FunctionGamlFacetRef <em>Function Gaml Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function Gaml Facet Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.FunctionGamlFacetRef
   * @generated
   */
  EClass getFunctionGamlFacetRef();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.FacetExpr <em>Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Facet Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.FacetExpr
   * @generated
   */
  EClass getFacetExpr();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.FacetExpr#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Key</em>'.
   * @see msi.gama.lang.gaml.gaml.FacetExpr#getKey()
   * @see #getFacetExpr()
   * @generated
   */
  EReference getFacetExpr_Key();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.FacetExpr#getExpr <em>Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.FacetExpr#getExpr()
   * @see #getFacetExpr()
   * @generated
   */
  EReference getFacetExpr_Expr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefinitionFacetExpr <em>Definition Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Definition Facet Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.DefinitionFacetExpr
   * @generated
   */
  EClass getDefinitionFacetExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.NameFacetExpr <em>Name Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Name Facet Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.NameFacetExpr
   * @generated
   */
  EClass getNameFacetExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ReturnsFacetExpr <em>Returns Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Returns Facet Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.ReturnsFacetExpr
   * @generated
   */
  EClass getReturnsFacetExpr();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.FunctionFacetExpr <em>Function Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function Facet Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.FunctionFacetExpr
   * @generated
   */
  EClass getFunctionFacetExpr();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlVarRef <em>Var Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Var Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef
   * @generated
   */
  EClass getGamlVarRef();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.PairExpr <em>Pair Expr</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pair Expr</em>'.
   * @see msi.gama.lang.gaml.gaml.PairExpr
   * @generated
   */
  EClass getPairExpr();

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
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Array#getExprs <em>Exprs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Exprs</em>'.
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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.FunctionRef <em>Function Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Function Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.FunctionRef
   * @generated
   */
  EClass getFunctionRef();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.FunctionRef#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.FunctionRef#getArgs()
   * @see #getFunctionRef()
   * @generated
   */
  EReference getFunctionRef_Args();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ArbitraryName <em>Arbitrary Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Arbitrary Name</em>'.
   * @see msi.gama.lang.gaml.gaml.ArbitraryName
   * @generated
   */
  EClass getArbitraryName();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.ArbitraryName#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.ArbitraryName#getName()
   * @see #getArbitraryName()
   * @generated
   */
  EAttribute getArbitraryName_Name();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.UnitName#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.UnitName#getName()
   * @see #getUnitName()
   * @generated
   */
  EAttribute getUnitName_Name();

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
     * The meta object literal for the '<em><b>Gaml</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODEL__GAML = eINSTANCE.getModel_Gaml();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl <em>Lang Def</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlLangDef()
     * @generated
     */
    EClass GAML_LANG_DEF = eINSTANCE.getGamlLangDef();

    /**
     * The meta object literal for the '<em><b>B</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__B = eINSTANCE.getGamlLangDef_B();

    /**
     * The meta object literal for the '<em><b>R</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__R = eINSTANCE.getGamlLangDef_R();

    /**
     * The meta object literal for the '<em><b>Unaries</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__UNARIES = eINSTANCE.getGamlLangDef_Unaries();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl <em>Def Binary Op</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefBinaryOp()
     * @generated
     */
    EClass DEF_BINARY_OP = eINSTANCE.getDefBinaryOp();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEF_BINARY_OP__NAME = eINSTANCE.getDefBinaryOp_Name();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefReservedImpl <em>Def Reserved</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefReservedImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefReserved()
     * @generated
     */
    EClass DEF_RESERVED = eINSTANCE.getDefReserved();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefUnaryImpl <em>Def Unary</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefUnaryImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefUnary()
     * @generated
     */
    EClass DEF_UNARY = eINSTANCE.getDefUnary();

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
     * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STATEMENT__KEY = eINSTANCE.getStatement_Key();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__REF = eINSTANCE.getStatement_Ref();

    /**
     * The meta object literal for the '<em><b>Expr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__EXPR = eINSTANCE.getStatement_Expr();

    /**
     * The meta object literal for the '<em><b>Facets</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference STATEMENT__FACETS = eINSTANCE.getStatement_Facets();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefinitionImpl <em>Definition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefinitionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefinition()
     * @generated
     */
    EClass DEFINITION = eINSTANCE.getDefinition();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FacetRefImpl <em>Facet Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FacetRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacetRef()
     * @generated
     */
    EClass FACET_REF = eINSTANCE.getFacetRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute FACET_REF__REF = eINSTANCE.getFacetRef_Ref();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl <em>Facet Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlFacetRef()
     * @generated
     */
    EClass GAML_FACET_REF = eINSTANCE.getGamlFacetRef();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionGamlFacetRefImpl <em>Function Gaml Facet Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FunctionGamlFacetRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionGamlFacetRef()
     * @generated
     */
    EClass FUNCTION_GAML_FACET_REF = eINSTANCE.getFunctionGamlFacetRef();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FacetExprImpl <em>Facet Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FacetExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacetExpr()
     * @generated
     */
    EClass FACET_EXPR = eINSTANCE.getFacetExpr();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FACET_EXPR__KEY = eINSTANCE.getFacetExpr_Key();

    /**
     * The meta object literal for the '<em><b>Expr</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FACET_EXPR__EXPR = eINSTANCE.getFacetExpr_Expr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefinitionFacetExprImpl <em>Definition Facet Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefinitionFacetExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefinitionFacetExpr()
     * @generated
     */
    EClass DEFINITION_FACET_EXPR = eINSTANCE.getDefinitionFacetExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.NameFacetExprImpl <em>Name Facet Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.NameFacetExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getNameFacetExpr()
     * @generated
     */
    EClass NAME_FACET_EXPR = eINSTANCE.getNameFacetExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ReturnsFacetExprImpl <em>Returns Facet Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ReturnsFacetExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getReturnsFacetExpr()
     * @generated
     */
    EClass RETURNS_FACET_EXPR = eINSTANCE.getReturnsFacetExpr();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionFacetExprImpl <em>Function Facet Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FunctionFacetExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionFacetExpr()
     * @generated
     */
    EClass FUNCTION_FACET_EXPR = eINSTANCE.getFunctionFacetExpr();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl <em>Var Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlVarRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlVarRef()
     * @generated
     */
    EClass GAML_VAR_REF = eINSTANCE.getGamlVarRef();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PairExprImpl <em>Pair Expr</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PairExprImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPairExpr()
     * @generated
     */
    EClass PAIR_EXPR = eINSTANCE.getPairExpr();

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
     * The meta object literal for the '<em><b>Exprs</b></em>' containment reference list feature.
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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionRefImpl <em>Function Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.FunctionRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionRef()
     * @generated
     */
    EClass FUNCTION_REF = eINSTANCE.getFunctionRef();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FUNCTION_REF__ARGS = eINSTANCE.getFunctionRef_Args();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ArbitraryNameImpl <em>Arbitrary Name</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ArbitraryNameImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArbitraryName()
     * @generated
     */
    EClass ARBITRARY_NAME = eINSTANCE.getArbitraryName();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ARBITRARY_NAME__NAME = eINSTANCE.getArbitraryName_Name();

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
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute UNIT_NAME__NAME = eINSTANCE.getUnitName_Name();

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
