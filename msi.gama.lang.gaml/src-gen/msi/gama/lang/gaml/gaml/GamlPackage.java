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
   * The feature id for the '<em><b>K</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__K = 0;

  /**
   * The feature id for the '<em><b>F</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__F = 1;

  /**
   * The feature id for the '<em><b>B</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__B = 2;

  /**
   * The feature id for the '<em><b>R</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__R = 3;

  /**
   * The feature id for the '<em><b>U</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF__U = 4;

  /**
   * The number of structural features of the '<em>Lang Def</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_LANG_DEF_FEATURE_COUNT = 5;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefKeywordImpl <em>Def Keyword</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefKeywordImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefKeyword()
   * @generated
   */
  int DEF_KEYWORD = 3;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_KEYWORD__NAME = 0;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_KEYWORD__BLOCK = 1;

  /**
   * The number of structural features of the '<em>Def Keyword</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_KEYWORD_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBlockImpl <em>Block</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlBlockImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBlock()
   * @generated
   */
  int GAML_BLOCK = 4;

  /**
   * The feature id for the '<em><b>Facets</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BLOCK__FACETS = 0;

  /**
   * The feature id for the '<em><b>Childs</b></em>' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BLOCK__CHILDS = 1;

  /**
   * The number of structural features of the '<em>Block</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BLOCK_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefFacetImpl <em>Def Facet</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefFacetImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefFacet()
   * @generated
   */
  int DEF_FACET = 5;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_FACET__NAME = 0;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_FACET__TYPE = 1;

  /**
   * The feature id for the '<em><b>Default</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_FACET__DEFAULT = 2;

  /**
   * The number of structural features of the '<em>Def Facet</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_FACET_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl <em>Def Binary Op</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefBinaryOpImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefBinaryOp()
   * @generated
   */
  int DEF_BINARY_OP = 6;

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
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AbstractDefinitionImpl <em>Abstract Definition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AbstractDefinitionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAbstractDefinition()
   * @generated
   */
  int ABSTRACT_DEFINITION = 22;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_DEFINITION__NAME = 0;

  /**
   * The number of structural features of the '<em>Abstract Definition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_DEFINITION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefReservedImpl <em>Def Reserved</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefReservedImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefReserved()
   * @generated
   */
  int DEF_RESERVED = 7;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED__NAME = ABSTRACT_DEFINITION__NAME;

  /**
   * The feature id for the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED__TYPE = ABSTRACT_DEFINITION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED__VALUE = ABSTRACT_DEFINITION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Def Reserved</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_RESERVED_FEATURE_COUNT = ABSTRACT_DEFINITION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefUnitImpl <em>Def Unit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefUnitImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefUnit()
   * @generated
   */
  int DEF_UNIT = 8;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_UNIT__NAME = 0;

  /**
   * The feature id for the '<em><b>Coef</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_UNIT__COEF = 1;

  /**
   * The number of structural features of the '<em>Def Unit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEF_UNIT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AbstractGamlRefImpl <em>Abstract Gaml Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AbstractGamlRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAbstractGamlRef()
   * @generated
   */
  int ABSTRACT_GAML_REF = 9;

  /**
   * The number of structural features of the '<em>Abstract Gaml Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ABSTRACT_GAML_REF_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl <em>Keyword Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlKeywordRef()
   * @generated
   */
  int GAML_KEYWORD_REF = 10;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_KEYWORD_REF__REF = ABSTRACT_GAML_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Keyword Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_KEYWORD_REF_FEATURE_COUNT = ABSTRACT_GAML_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl <em>Facet Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlFacetRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlFacetRef()
   * @generated
   */
  int GAML_FACET_REF = 11;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_FACET_REF__REF = ABSTRACT_GAML_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Facet Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_FACET_REF_FEATURE_COUNT = ABSTRACT_GAML_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinarOpRefImpl <em>Binar Op Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlBinarOpRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinarOpRef()
   * @generated
   */
  int GAML_BINAR_OP_REF = 12;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINAR_OP_REF__REF = ABSTRACT_GAML_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Binar Op Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINAR_OP_REF_FEATURE_COUNT = ABSTRACT_GAML_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnitRefImpl <em>Unit Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlUnitRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnitRef()
   * @generated
   */
  int GAML_UNIT_REF = 13;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_REF__REF = ABSTRACT_GAML_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Unit Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNIT_REF_FEATURE_COUNT = ABSTRACT_GAML_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlReservedRefImpl <em>Reserved Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlReservedRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlReservedRef()
   * @generated
   */
  int GAML_RESERVED_REF = 14;

  /**
   * The feature id for the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_RESERVED_REF__REF = ABSTRACT_GAML_REF_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Reserved Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_RESERVED_REF_FEATURE_COUNT = ABSTRACT_GAML_REF_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StatementImpl <em>Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StatementImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStatement()
   * @generated
   */
  int STATEMENT = 15;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__FACETS = 0;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT__BLOCK = 1;

  /**
   * The number of structural features of the '<em>Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STATEMENT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.SubStatementImpl <em>Sub Statement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.SubStatementImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getSubStatement()
   * @generated
   */
  int SUB_STATEMENT = 16;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_STATEMENT__FACETS = STATEMENT__FACETS;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_STATEMENT__BLOCK = STATEMENT__BLOCK;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_STATEMENT__KEY = STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Sub Statement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_STATEMENT_FEATURE_COUNT = STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.SetEvalImpl <em>Set Eval</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.SetEvalImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getSetEval()
   * @generated
   */
  int SET_EVAL = 17;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SET_EVAL__FACETS = STATEMENT__FACETS;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SET_EVAL__BLOCK = STATEMENT__BLOCK;

  /**
   * The feature id for the '<em><b>Var</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SET_EVAL__VAR = STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Set Eval</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SET_EVAL_FEATURE_COUNT = STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DefinitionImpl <em>Definition</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DefinitionImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefinition()
   * @generated
   */
  int DEFINITION = 18;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__FACETS = SUB_STATEMENT__FACETS;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__BLOCK = SUB_STATEMENT__BLOCK;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__KEY = SUB_STATEMENT__KEY;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION__NAME = SUB_STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Definition</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DEFINITION_FEATURE_COUNT = SUB_STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.EvaluationImpl <em>Evaluation</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.EvaluationImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getEvaluation()
   * @generated
   */
  int EVALUATION = 19;

  /**
   * The feature id for the '<em><b>Facets</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVALUATION__FACETS = SUB_STATEMENT__FACETS;

  /**
   * The feature id for the '<em><b>Block</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVALUATION__BLOCK = SUB_STATEMENT__BLOCK;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVALUATION__KEY = SUB_STATEMENT__KEY;

  /**
   * The feature id for the '<em><b>Var</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVALUATION__VAR = SUB_STATEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Evaluation</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EVALUATION_FEATURE_COUNT = SUB_STATEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FacetExprImpl <em>Facet Expr</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FacetExprImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFacetExpr()
   * @generated
   */
  int FACET_EXPR = 20;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR__NAME = ABSTRACT_DEFINITION__NAME;

  /**
   * The feature id for the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR__KEY = ABSTRACT_DEFINITION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Expr</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR__EXPR = ABSTRACT_DEFINITION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Facet Expr</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FACET_EXPR_FEATURE_COUNT = ABSTRACT_DEFINITION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.BlockImpl <em>Block</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.BlockImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBlock()
   * @generated
   */
  int BLOCK = 21;

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
  int EXPRESSION = 23;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PointImpl <em>Point</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PointImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPoint()
   * @generated
   */
  int POINT = 24;

  /**
   * The feature id for the '<em><b>X</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__X = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Y</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT__Y = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Point</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POINT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MatrixImpl <em>Matrix</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MatrixImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMatrix()
   * @generated
   */
  int MATRIX = 25;

  /**
   * The feature id for the '<em><b>Rows</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATRIX__ROWS = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Matrix</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MATRIX_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RowImpl <em>Row</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RowImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRow()
   * @generated
   */
  int ROW = 26;

  /**
   * The feature id for the '<em><b>Exprs</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROW__EXPRS = 0;

  /**
   * The number of structural features of the '<em>Row</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ROW_FEATURE_COUNT = 1;

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
  int TERMINAL_EXPRESSION = 28;

  /**
   * The number of structural features of the '<em>Terminal Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERMINAL_EXPRESSION_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AssignPlusImpl <em>Assign Plus</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AssignPlusImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignPlus()
   * @generated
   */
  int ASSIGN_PLUS = 29;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_PLUS__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_PLUS__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assign Plus</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_PLUS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AssignMinImpl <em>Assign Min</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AssignMinImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignMin()
   * @generated
   */
  int ASSIGN_MIN = 30;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MIN__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MIN__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assign Min</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MIN_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AssignMultImpl <em>Assign Mult</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AssignMultImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignMult()
   * @generated
   */
  int ASSIGN_MULT = 31;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MULT__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MULT__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assign Mult</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_MULT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AssignDivImpl <em>Assign Div</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AssignDivImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignDiv()
   * @generated
   */
  int ASSIGN_DIV = 32;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_DIV__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_DIV__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Assign Div</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ASSIGN_DIV_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.TernaryImpl <em>Ternary</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.TernaryImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTernary()
   * @generated
   */
  int TERNARY = 33;

  /**
   * The feature id for the '<em><b>Condition</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERNARY__CONDITION = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>If True</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERNARY__IF_TRUE = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>If False</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERNARY__IF_FALSE = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Ternary</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERNARY_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.OrImpl <em>Or</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.OrImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getOr()
   * @generated
   */
  int OR = 34;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Or</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.AndImpl <em>And</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.AndImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAnd()
   * @generated
   */
  int AND = 35;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>And</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelNotEqImpl <em>Rel Not Eq</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelNotEqImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelNotEq()
   * @generated
   */
  int REL_NOT_EQ = 36;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_NOT_EQ__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_NOT_EQ__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Not Eq</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_NOT_EQ_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelEqImpl <em>Rel Eq</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelEqImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelEq()
   * @generated
   */
  int REL_EQ = 37;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Eq</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelEqEqImpl <em>Rel Eq Eq</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelEqEqImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelEqEq()
   * @generated
   */
  int REL_EQ_EQ = 38;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ_EQ__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ_EQ__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Eq Eq</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_EQ_EQ_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelLtEqImpl <em>Rel Lt Eq</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelLtEqImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelLtEq()
   * @generated
   */
  int REL_LT_EQ = 39;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT_EQ__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT_EQ__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Lt Eq</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT_EQ_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelGtEqImpl <em>Rel Gt Eq</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelGtEqImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelGtEq()
   * @generated
   */
  int REL_GT_EQ = 40;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT_EQ__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT_EQ__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Gt Eq</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT_EQ_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelLtImpl <em>Rel Lt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelLtImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelLt()
   * @generated
   */
  int REL_LT = 41;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Lt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_LT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.RelGtImpl <em>Rel Gt</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.RelGtImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelGt()
   * @generated
   */
  int REL_GT = 42;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Rel Gt</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REL_GT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PairImpl <em>Pair</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PairImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPair()
   * @generated
   */
  int PAIR = 43;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Pair</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PAIR_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PlusImpl <em>Plus</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PlusImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPlus()
   * @generated
   */
  int PLUS = 44;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLUS__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLUS__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Plus</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PLUS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MinusImpl <em>Minus</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MinusImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMinus()
   * @generated
   */
  int MINUS = 45;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MINUS__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MINUS__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Minus</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MINUS_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MultiImpl <em>Multi</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MultiImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMulti()
   * @generated
   */
  int MULTI = 46;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Multi</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MULTI_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DivImpl <em>Div</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DivImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDiv()
   * @generated
   */
  int DIV = 47;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIV__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIV__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Div</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DIV_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.PowImpl <em>Pow</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.PowImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPow()
   * @generated
   */
  int POW = 48;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POW__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POW__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Pow</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int POW_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinaryImpl <em>Binary</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlBinaryImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinary()
   * @generated
   */
  int GAML_BINARY = 49;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Op</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY__OP = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY__RIGHT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Binary</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_BINARY_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.UnitImpl <em>Unit</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.UnitImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getUnit()
   * @generated
   */
  int UNIT = 50;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Unit</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int UNIT_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnaryImpl <em>Unary</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.GamlUnaryImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnary()
   * @generated
   */
  int GAML_UNARY = 51;

  /**
   * The feature id for the '<em><b>Op</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY__OP = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Unary</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GAML_UNARY_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefPImpl <em>Member Ref P</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MemberRefPImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRefP()
   * @generated
   */
  int MEMBER_REF_P = 52;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_P__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_P__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Member Ref P</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_P_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefRImpl <em>Member Ref R</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.MemberRefRImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRefR()
   * @generated
   */
  int MEMBER_REF_R = 53;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_R__LEFT = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_R__RIGHT = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Member Ref R</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_REF_R_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.FunctionRefImpl <em>Function Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.FunctionRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getFunctionRef()
   * @generated
   */
  int FUNCTION_REF = 54;

  /**
   * The feature id for the '<em><b>Func</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__FUNC = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF__ARGS = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Function Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FUNCTION_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ArrayRefImpl <em>Array Ref</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ArrayRefImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArrayRef()
   * @generated
   */
  int ARRAY_REF = 55;

  /**
   * The feature id for the '<em><b>Array</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY_REF__ARRAY = EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Args</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY_REF__ARGS = EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Array Ref</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ARRAY_REF_FEATURE_COUNT = EXPRESSION_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.IntLiteralImpl <em>Int Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.IntLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getIntLiteral()
   * @generated
   */
  int INT_LITERAL = 56;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL__VALUE = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Int Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INT_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl <em>Double Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.DoubleLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDoubleLiteral()
   * @generated
   */
  int DOUBLE_LITERAL = 57;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL__VALUE = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Double Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOUBLE_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl <em>Color Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.ColorLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getColorLiteral()
   * @generated
   */
  int COLOR_LITERAL = 58;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL__VALUE = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Color Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COLOR_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.StringLiteralImpl <em>String Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.StringLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getStringLiteral()
   * @generated
   */
  int STRING_LITERAL = 59;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL__VALUE = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl
   * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBooleanLiteral()
   * @generated
   */
  int BOOLEAN_LITERAL = 60;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL__VALUE = TERMINAL_EXPRESSION_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Boolean Literal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int BOOLEAN_LITERAL_FEATURE_COUNT = TERMINAL_EXPRESSION_FEATURE_COUNT + 1;


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
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getK <em>K</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>K</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getK()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_K();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getF <em>F</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>F</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getF()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_F();

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
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.GamlLangDef#getU <em>U</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>U</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef#getU()
   * @see #getGamlLangDef()
   * @generated
   */
  EReference getGamlLangDef_U();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefKeyword <em>Def Keyword</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Keyword</em>'.
   * @see msi.gama.lang.gaml.gaml.DefKeyword
   * @generated
   */
  EClass getDefKeyword();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DefKeyword#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.DefKeyword#getName()
   * @see #getDefKeyword()
   * @generated
   */
  EAttribute getDefKeyword_Name();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.DefKeyword#getBlock <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Block</em>'.
   * @see msi.gama.lang.gaml.gaml.DefKeyword#getBlock()
   * @see #getDefKeyword()
   * @generated
   */
  EReference getDefKeyword_Block();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlBlock <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Block</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBlock
   * @generated
   */
  EClass getGamlBlock();

  /**
   * Returns the meta object for the reference list '{@link msi.gama.lang.gaml.gaml.GamlBlock#getFacets <em>Facets</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Facets</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBlock#getFacets()
   * @see #getGamlBlock()
   * @generated
   */
  EReference getGamlBlock_Facets();

  /**
   * Returns the meta object for the reference list '{@link msi.gama.lang.gaml.gaml.GamlBlock#getChilds <em>Childs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference list '<em>Childs</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBlock#getChilds()
   * @see #getGamlBlock()
   * @generated
   */
  EReference getGamlBlock_Childs();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefFacet <em>Def Facet</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Facet</em>'.
   * @see msi.gama.lang.gaml.gaml.DefFacet
   * @generated
   */
  EClass getDefFacet();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DefFacet#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.DefFacet#getName()
   * @see #getDefFacet()
   * @generated
   */
  EAttribute getDefFacet_Name();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.DefFacet#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see msi.gama.lang.gaml.gaml.DefFacet#getType()
   * @see #getDefFacet()
   * @generated
   */
  EReference getDefFacet_Type();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.DefFacet#getDefault <em>Default</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Default</em>'.
   * @see msi.gama.lang.gaml.gaml.DefFacet#getDefault()
   * @see #getDefFacet()
   * @generated
   */
  EReference getDefFacet_Default();

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
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.DefReserved#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Type</em>'.
   * @see msi.gama.lang.gaml.gaml.DefReserved#getType()
   * @see #getDefReserved()
   * @generated
   */
  EReference getDefReserved_Type();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.DefReserved#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.DefReserved#getValue()
   * @see #getDefReserved()
   * @generated
   */
  EReference getDefReserved_Value();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.DefUnit <em>Def Unit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Def Unit</em>'.
   * @see msi.gama.lang.gaml.gaml.DefUnit
   * @generated
   */
  EClass getDefUnit();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DefUnit#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.DefUnit#getName()
   * @see #getDefUnit()
   * @generated
   */
  EAttribute getDefUnit_Name();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DefUnit#getCoef <em>Coef</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Coef</em>'.
   * @see msi.gama.lang.gaml.gaml.DefUnit#getCoef()
   * @see #getDefUnit()
   * @generated
   */
  EAttribute getDefUnit_Coef();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AbstractGamlRef <em>Abstract Gaml Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Abstract Gaml Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.AbstractGamlRef
   * @generated
   */
  EClass getAbstractGamlRef();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlKeywordRef <em>Keyword Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Keyword Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlKeywordRef
   * @generated
   */
  EClass getGamlKeywordRef();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.GamlKeywordRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlKeywordRef#getRef()
   * @see #getGamlKeywordRef()
   * @generated
   */
  EReference getGamlKeywordRef_Ref();

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
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.GamlFacetRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlFacetRef#getRef()
   * @see #getGamlFacetRef()
   * @generated
   */
  EReference getGamlFacetRef_Ref();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlBinarOpRef <em>Binar Op Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Binar Op Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinarOpRef
   * @generated
   */
  EClass getGamlBinarOpRef();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.GamlBinarOpRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinarOpRef#getRef()
   * @see #getGamlBinarOpRef()
   * @generated
   */
  EReference getGamlBinarOpRef_Ref();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlUnitRef <em>Unit Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unit Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnitRef
   * @generated
   */
  EClass getGamlUnitRef();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.GamlUnitRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnitRef#getRef()
   * @see #getGamlUnitRef()
   * @generated
   */
  EReference getGamlUnitRef_Ref();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlReservedRef <em>Reserved Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Reserved Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlReservedRef
   * @generated
   */
  EClass getGamlReservedRef();

  /**
   * Returns the meta object for the reference '{@link msi.gama.lang.gaml.gaml.GamlReservedRef#getRef <em>Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the reference '<em>Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlReservedRef#getRef()
   * @see #getGamlReservedRef()
   * @generated
   */
  EReference getGamlReservedRef_Ref();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.SubStatement <em>Sub Statement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Sub Statement</em>'.
   * @see msi.gama.lang.gaml.gaml.SubStatement
   * @generated
   */
  EClass getSubStatement();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.SubStatement#getKey <em>Key</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Key</em>'.
   * @see msi.gama.lang.gaml.gaml.SubStatement#getKey()
   * @see #getSubStatement()
   * @generated
   */
  EReference getSubStatement_Key();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.SetEval <em>Set Eval</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Set Eval</em>'.
   * @see msi.gama.lang.gaml.gaml.SetEval
   * @generated
   */
  EClass getSetEval();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.SetEval#getVar <em>Var</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Var</em>'.
   * @see msi.gama.lang.gaml.gaml.SetEval#getVar()
   * @see #getSetEval()
   * @generated
   */
  EReference getSetEval_Var();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Evaluation <em>Evaluation</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Evaluation</em>'.
   * @see msi.gama.lang.gaml.gaml.Evaluation
   * @generated
   */
  EClass getEvaluation();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Evaluation#getVar <em>Var</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Var</em>'.
   * @see msi.gama.lang.gaml.gaml.Evaluation#getVar()
   * @see #getEvaluation()
   * @generated
   */
  EReference getEvaluation_Var();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AbstractDefinition <em>Abstract Definition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Abstract Definition</em>'.
   * @see msi.gama.lang.gaml.gaml.AbstractDefinition
   * @generated
   */
  EClass getAbstractDefinition();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.AbstractDefinition#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see msi.gama.lang.gaml.gaml.AbstractDefinition#getName()
   * @see #getAbstractDefinition()
   * @generated
   */
  EAttribute getAbstractDefinition_Name();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Point <em>Point</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Point</em>'.
   * @see msi.gama.lang.gaml.gaml.Point
   * @generated
   */
  EClass getPoint();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Point#getX <em>X</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>X</em>'.
   * @see msi.gama.lang.gaml.gaml.Point#getX()
   * @see #getPoint()
   * @generated
   */
  EReference getPoint_X();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Point#getY <em>Y</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Y</em>'.
   * @see msi.gama.lang.gaml.gaml.Point#getY()
   * @see #getPoint()
   * @generated
   */
  EReference getPoint_Y();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Matrix <em>Matrix</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Matrix</em>'.
   * @see msi.gama.lang.gaml.gaml.Matrix
   * @generated
   */
  EClass getMatrix();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Matrix#getRows <em>Rows</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Rows</em>'.
   * @see msi.gama.lang.gaml.gaml.Matrix#getRows()
   * @see #getMatrix()
   * @generated
   */
  EReference getMatrix_Rows();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Row <em>Row</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Row</em>'.
   * @see msi.gama.lang.gaml.gaml.Row
   * @generated
   */
  EClass getRow();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.Row#getExprs <em>Exprs</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Exprs</em>'.
   * @see msi.gama.lang.gaml.gaml.Row#getExprs()
   * @see #getRow()
   * @generated
   */
  EReference getRow_Exprs();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.TerminalExpression <em>Terminal Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Terminal Expression</em>'.
   * @see msi.gama.lang.gaml.gaml.TerminalExpression
   * @generated
   */
  EClass getTerminalExpression();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AssignPlus <em>Assign Plus</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Assign Plus</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignPlus
   * @generated
   */
  EClass getAssignPlus();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignPlus#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignPlus#getLeft()
   * @see #getAssignPlus()
   * @generated
   */
  EReference getAssignPlus_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignPlus#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignPlus#getRight()
   * @see #getAssignPlus()
   * @generated
   */
  EReference getAssignPlus_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AssignMin <em>Assign Min</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Assign Min</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMin
   * @generated
   */
  EClass getAssignMin();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignMin#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMin#getLeft()
   * @see #getAssignMin()
   * @generated
   */
  EReference getAssignMin_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignMin#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMin#getRight()
   * @see #getAssignMin()
   * @generated
   */
  EReference getAssignMin_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AssignMult <em>Assign Mult</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Assign Mult</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMult
   * @generated
   */
  EClass getAssignMult();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignMult#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMult#getLeft()
   * @see #getAssignMult()
   * @generated
   */
  EReference getAssignMult_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignMult#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignMult#getRight()
   * @see #getAssignMult()
   * @generated
   */
  EReference getAssignMult_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.AssignDiv <em>Assign Div</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Assign Div</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignDiv
   * @generated
   */
  EClass getAssignDiv();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignDiv#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignDiv#getLeft()
   * @see #getAssignDiv()
   * @generated
   */
  EReference getAssignDiv_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.AssignDiv#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.AssignDiv#getRight()
   * @see #getAssignDiv()
   * @generated
   */
  EReference getAssignDiv_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Ternary <em>Ternary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ternary</em>'.
   * @see msi.gama.lang.gaml.gaml.Ternary
   * @generated
   */
  EClass getTernary();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Ternary#getCondition <em>Condition</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Condition</em>'.
   * @see msi.gama.lang.gaml.gaml.Ternary#getCondition()
   * @see #getTernary()
   * @generated
   */
  EReference getTernary_Condition();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Ternary#getIfTrue <em>If True</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>If True</em>'.
   * @see msi.gama.lang.gaml.gaml.Ternary#getIfTrue()
   * @see #getTernary()
   * @generated
   */
  EReference getTernary_IfTrue();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Ternary#getIfFalse <em>If False</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>If False</em>'.
   * @see msi.gama.lang.gaml.gaml.Ternary#getIfFalse()
   * @see #getTernary()
   * @generated
   */
  EReference getTernary_IfFalse();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or</em>'.
   * @see msi.gama.lang.gaml.gaml.Or
   * @generated
   */
  EClass getOr();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Or#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Or#getLeft()
   * @see #getOr()
   * @generated
   */
  EReference getOr_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Or#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Or#getRight()
   * @see #getOr()
   * @generated
   */
  EReference getOr_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And</em>'.
   * @see msi.gama.lang.gaml.gaml.And
   * @generated
   */
  EClass getAnd();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.And#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.And#getLeft()
   * @see #getAnd()
   * @generated
   */
  EReference getAnd_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.And#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.And#getRight()
   * @see #getAnd()
   * @generated
   */
  EReference getAnd_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelNotEq <em>Rel Not Eq</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Not Eq</em>'.
   * @see msi.gama.lang.gaml.gaml.RelNotEq
   * @generated
   */
  EClass getRelNotEq();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelNotEq#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelNotEq#getLeft()
   * @see #getRelNotEq()
   * @generated
   */
  EReference getRelNotEq_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelNotEq#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelNotEq#getRight()
   * @see #getRelNotEq()
   * @generated
   */
  EReference getRelNotEq_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelEq <em>Rel Eq</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Eq</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEq
   * @generated
   */
  EClass getRelEq();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelEq#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEq#getLeft()
   * @see #getRelEq()
   * @generated
   */
  EReference getRelEq_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelEq#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEq#getRight()
   * @see #getRelEq()
   * @generated
   */
  EReference getRelEq_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelEqEq <em>Rel Eq Eq</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Eq Eq</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEqEq
   * @generated
   */
  EClass getRelEqEq();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelEqEq#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEqEq#getLeft()
   * @see #getRelEqEq()
   * @generated
   */
  EReference getRelEqEq_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelEqEq#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelEqEq#getRight()
   * @see #getRelEqEq()
   * @generated
   */
  EReference getRelEqEq_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelLtEq <em>Rel Lt Eq</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Lt Eq</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLtEq
   * @generated
   */
  EClass getRelLtEq();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelLtEq#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLtEq#getLeft()
   * @see #getRelLtEq()
   * @generated
   */
  EReference getRelLtEq_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelLtEq#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLtEq#getRight()
   * @see #getRelLtEq()
   * @generated
   */
  EReference getRelLtEq_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelGtEq <em>Rel Gt Eq</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Gt Eq</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGtEq
   * @generated
   */
  EClass getRelGtEq();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelGtEq#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGtEq#getLeft()
   * @see #getRelGtEq()
   * @generated
   */
  EReference getRelGtEq_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelGtEq#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGtEq#getRight()
   * @see #getRelGtEq()
   * @generated
   */
  EReference getRelGtEq_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelLt <em>Rel Lt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Lt</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLt
   * @generated
   */
  EClass getRelLt();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelLt#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLt#getLeft()
   * @see #getRelLt()
   * @generated
   */
  EReference getRelLt_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelLt#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelLt#getRight()
   * @see #getRelLt()
   * @generated
   */
  EReference getRelLt_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.RelGt <em>Rel Gt</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Rel Gt</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGt
   * @generated
   */
  EClass getRelGt();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelGt#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGt#getLeft()
   * @see #getRelGt()
   * @generated
   */
  EReference getRelGt_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.RelGt#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.RelGt#getRight()
   * @see #getRelGt()
   * @generated
   */
  EReference getRelGt_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Pair <em>Pair</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pair</em>'.
   * @see msi.gama.lang.gaml.gaml.Pair
   * @generated
   */
  EClass getPair();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Pair#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Pair#getLeft()
   * @see #getPair()
   * @generated
   */
  EReference getPair_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Pair#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Pair#getRight()
   * @see #getPair()
   * @generated
   */
  EReference getPair_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Plus <em>Plus</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Plus</em>'.
   * @see msi.gama.lang.gaml.gaml.Plus
   * @generated
   */
  EClass getPlus();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Plus#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Plus#getLeft()
   * @see #getPlus()
   * @generated
   */
  EReference getPlus_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Plus#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Plus#getRight()
   * @see #getPlus()
   * @generated
   */
  EReference getPlus_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Minus <em>Minus</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Minus</em>'.
   * @see msi.gama.lang.gaml.gaml.Minus
   * @generated
   */
  EClass getMinus();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Minus#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Minus#getLeft()
   * @see #getMinus()
   * @generated
   */
  EReference getMinus_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Minus#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Minus#getRight()
   * @see #getMinus()
   * @generated
   */
  EReference getMinus_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Multi <em>Multi</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Multi</em>'.
   * @see msi.gama.lang.gaml.gaml.Multi
   * @generated
   */
  EClass getMulti();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Multi#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Multi#getLeft()
   * @see #getMulti()
   * @generated
   */
  EReference getMulti_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Multi#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Multi#getRight()
   * @see #getMulti()
   * @generated
   */
  EReference getMulti_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Div <em>Div</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Div</em>'.
   * @see msi.gama.lang.gaml.gaml.Div
   * @generated
   */
  EClass getDiv();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Div#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Div#getLeft()
   * @see #getDiv()
   * @generated
   */
  EReference getDiv_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Div#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Div#getRight()
   * @see #getDiv()
   * @generated
   */
  EReference getDiv_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Pow <em>Pow</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Pow</em>'.
   * @see msi.gama.lang.gaml.gaml.Pow
   * @generated
   */
  EClass getPow();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Pow#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Pow#getLeft()
   * @see #getPow()
   * @generated
   */
  EReference getPow_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Pow#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Pow#getRight()
   * @see #getPow()
   * @generated
   */
  EReference getPow_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlBinary <em>Binary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Binary</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinary
   * @generated
   */
  EClass getGamlBinary();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.GamlBinary#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinary#getLeft()
   * @see #getGamlBinary()
   * @generated
   */
  EReference getGamlBinary_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.GamlBinary#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Op</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinary#getOp()
   * @see #getGamlBinary()
   * @generated
   */
  EReference getGamlBinary_Op();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.GamlBinary#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlBinary#getRight()
   * @see #getGamlBinary()
   * @generated
   */
  EReference getGamlBinary_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.Unit <em>Unit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unit</em>'.
   * @see msi.gama.lang.gaml.gaml.Unit
   * @generated
   */
  EClass getUnit();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Unit#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.Unit#getLeft()
   * @see #getUnit()
   * @generated
   */
  EReference getUnit_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.Unit#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.Unit#getRight()
   * @see #getUnit()
   * @generated
   */
  EReference getUnit_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.GamlUnary <em>Unary</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Unary</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnary
   * @generated
   */
  EClass getGamlUnary();

  /**
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.GamlUnary#getOp <em>Op</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Op</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnary#getOp()
   * @see #getGamlUnary()
   * @generated
   */
  EAttribute getGamlUnary_Op();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.GamlUnary#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.GamlUnary#getRight()
   * @see #getGamlUnary()
   * @generated
   */
  EReference getGamlUnary_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.MemberRefP <em>Member Ref P</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Ref P</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefP
   * @generated
   */
  EClass getMemberRefP();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.MemberRefP#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefP#getLeft()
   * @see #getMemberRefP()
   * @generated
   */
  EReference getMemberRefP_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.MemberRefP#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefP#getRight()
   * @see #getMemberRefP()
   * @generated
   */
  EReference getMemberRefP_Right();

  /**
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.MemberRefR <em>Member Ref R</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Ref R</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefR
   * @generated
   */
  EClass getMemberRefR();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.MemberRefR#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefR#getLeft()
   * @see #getMemberRefR()
   * @generated
   */
  EReference getMemberRefR_Left();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.MemberRefR#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see msi.gama.lang.gaml.gaml.MemberRefR#getRight()
   * @see #getMemberRefR()
   * @generated
   */
  EReference getMemberRefR_Right();

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
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.FunctionRef#getFunc <em>Func</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Func</em>'.
   * @see msi.gama.lang.gaml.gaml.FunctionRef#getFunc()
   * @see #getFunctionRef()
   * @generated
   */
  EReference getFunctionRef_Func();

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
   * Returns the meta object for class '{@link msi.gama.lang.gaml.gaml.ArrayRef <em>Array Ref</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Array Ref</em>'.
   * @see msi.gama.lang.gaml.gaml.ArrayRef
   * @generated
   */
  EClass getArrayRef();

  /**
   * Returns the meta object for the containment reference '{@link msi.gama.lang.gaml.gaml.ArrayRef#getArray <em>Array</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Array</em>'.
   * @see msi.gama.lang.gaml.gaml.ArrayRef#getArray()
   * @see #getArrayRef()
   * @generated
   */
  EReference getArrayRef_Array();

  /**
   * Returns the meta object for the containment reference list '{@link msi.gama.lang.gaml.gaml.ArrayRef#getArgs <em>Args</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Args</em>'.
   * @see msi.gama.lang.gaml.gaml.ArrayRef#getArgs()
   * @see #getArrayRef()
   * @generated
   */
  EReference getArrayRef_Args();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.IntLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.IntLiteral#getValue()
   * @see #getIntLiteral()
   * @generated
   */
  EAttribute getIntLiteral_Value();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.DoubleLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.DoubleLiteral#getValue()
   * @see #getDoubleLiteral()
   * @generated
   */
  EAttribute getDoubleLiteral_Value();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.ColorLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.ColorLiteral#getValue()
   * @see #getColorLiteral()
   * @generated
   */
  EAttribute getColorLiteral_Value();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.StringLiteral#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.StringLiteral#getValue()
   * @see #getStringLiteral()
   * @generated
   */
  EAttribute getStringLiteral_Value();

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
   * Returns the meta object for the attribute '{@link msi.gama.lang.gaml.gaml.BooleanLiteral#isValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see msi.gama.lang.gaml.gaml.BooleanLiteral#isValue()
   * @see #getBooleanLiteral()
   * @generated
   */
  EAttribute getBooleanLiteral_Value();

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
     * The meta object literal for the '<em><b>K</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__K = eINSTANCE.getGamlLangDef_K();

    /**
     * The meta object literal for the '<em><b>F</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__F = eINSTANCE.getGamlLangDef_F();

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
     * The meta object literal for the '<em><b>U</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_LANG_DEF__U = eINSTANCE.getGamlLangDef_U();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefKeywordImpl <em>Def Keyword</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefKeywordImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefKeyword()
     * @generated
     */
    EClass DEF_KEYWORD = eINSTANCE.getDefKeyword();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEF_KEYWORD__NAME = eINSTANCE.getDefKeyword_Name();

    /**
     * The meta object literal for the '<em><b>Block</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEF_KEYWORD__BLOCK = eINSTANCE.getDefKeyword_Block();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBlockImpl <em>Block</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlBlockImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBlock()
     * @generated
     */
    EClass GAML_BLOCK = eINSTANCE.getGamlBlock();

    /**
     * The meta object literal for the '<em><b>Facets</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BLOCK__FACETS = eINSTANCE.getGamlBlock_Facets();

    /**
     * The meta object literal for the '<em><b>Childs</b></em>' reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BLOCK__CHILDS = eINSTANCE.getGamlBlock_Childs();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefFacetImpl <em>Def Facet</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefFacetImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefFacet()
     * @generated
     */
    EClass DEF_FACET = eINSTANCE.getDefFacet();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEF_FACET__NAME = eINSTANCE.getDefFacet_Name();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEF_FACET__TYPE = eINSTANCE.getDefFacet_Type();

    /**
     * The meta object literal for the '<em><b>Default</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEF_FACET__DEFAULT = eINSTANCE.getDefFacet_Default();

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
     * The meta object literal for the '<em><b>Type</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEF_RESERVED__TYPE = eINSTANCE.getDefReserved_Type();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DEF_RESERVED__VALUE = eINSTANCE.getDefReserved_Value();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DefUnitImpl <em>Def Unit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DefUnitImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDefUnit()
     * @generated
     */
    EClass DEF_UNIT = eINSTANCE.getDefUnit();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEF_UNIT__NAME = eINSTANCE.getDefUnit_Name();

    /**
     * The meta object literal for the '<em><b>Coef</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DEF_UNIT__COEF = eINSTANCE.getDefUnit_Coef();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AbstractGamlRefImpl <em>Abstract Gaml Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AbstractGamlRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAbstractGamlRef()
     * @generated
     */
    EClass ABSTRACT_GAML_REF = eINSTANCE.getAbstractGamlRef();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl <em>Keyword Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlKeywordRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlKeywordRef()
     * @generated
     */
    EClass GAML_KEYWORD_REF = eINSTANCE.getGamlKeywordRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_KEYWORD_REF__REF = eINSTANCE.getGamlKeywordRef_Ref();

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
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_FACET_REF__REF = eINSTANCE.getGamlFacetRef_Ref();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinarOpRefImpl <em>Binar Op Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlBinarOpRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinarOpRef()
     * @generated
     */
    EClass GAML_BINAR_OP_REF = eINSTANCE.getGamlBinarOpRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BINAR_OP_REF__REF = eINSTANCE.getGamlBinarOpRef_Ref();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnitRefImpl <em>Unit Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlUnitRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnitRef()
     * @generated
     */
    EClass GAML_UNIT_REF = eINSTANCE.getGamlUnitRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_UNIT_REF__REF = eINSTANCE.getGamlUnitRef_Ref();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlReservedRefImpl <em>Reserved Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlReservedRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlReservedRef()
     * @generated
     */
    EClass GAML_RESERVED_REF = eINSTANCE.getGamlReservedRef();

    /**
     * The meta object literal for the '<em><b>Ref</b></em>' reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_RESERVED_REF__REF = eINSTANCE.getGamlReservedRef_Ref();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.SubStatementImpl <em>Sub Statement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.SubStatementImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getSubStatement()
     * @generated
     */
    EClass SUB_STATEMENT = eINSTANCE.getSubStatement();

    /**
     * The meta object literal for the '<em><b>Key</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUB_STATEMENT__KEY = eINSTANCE.getSubStatement_Key();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.SetEvalImpl <em>Set Eval</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.SetEvalImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getSetEval()
     * @generated
     */
    EClass SET_EVAL = eINSTANCE.getSetEval();

    /**
     * The meta object literal for the '<em><b>Var</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SET_EVAL__VAR = eINSTANCE.getSetEval_Var();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.EvaluationImpl <em>Evaluation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.EvaluationImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getEvaluation()
     * @generated
     */
    EClass EVALUATION = eINSTANCE.getEvaluation();

    /**
     * The meta object literal for the '<em><b>Var</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EVALUATION__VAR = eINSTANCE.getEvaluation_Var();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AbstractDefinitionImpl <em>Abstract Definition</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AbstractDefinitionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAbstractDefinition()
     * @generated
     */
    EClass ABSTRACT_DEFINITION = eINSTANCE.getAbstractDefinition();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ABSTRACT_DEFINITION__NAME = eINSTANCE.getAbstractDefinition_Name();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PointImpl <em>Point</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PointImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPoint()
     * @generated
     */
    EClass POINT = eINSTANCE.getPoint();

    /**
     * The meta object literal for the '<em><b>X</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference POINT__X = eINSTANCE.getPoint_X();

    /**
     * The meta object literal for the '<em><b>Y</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference POINT__Y = eINSTANCE.getPoint_Y();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MatrixImpl <em>Matrix</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MatrixImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMatrix()
     * @generated
     */
    EClass MATRIX = eINSTANCE.getMatrix();

    /**
     * The meta object literal for the '<em><b>Rows</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MATRIX__ROWS = eINSTANCE.getMatrix_Rows();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RowImpl <em>Row</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RowImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRow()
     * @generated
     */
    EClass ROW = eINSTANCE.getRow();

    /**
     * The meta object literal for the '<em><b>Exprs</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ROW__EXPRS = eINSTANCE.getRow_Exprs();

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
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl <em>Terminal Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.TerminalExpressionImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTerminalExpression()
     * @generated
     */
    EClass TERMINAL_EXPRESSION = eINSTANCE.getTerminalExpression();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AssignPlusImpl <em>Assign Plus</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AssignPlusImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignPlus()
     * @generated
     */
    EClass ASSIGN_PLUS = eINSTANCE.getAssignPlus();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_PLUS__LEFT = eINSTANCE.getAssignPlus_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_PLUS__RIGHT = eINSTANCE.getAssignPlus_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AssignMinImpl <em>Assign Min</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AssignMinImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignMin()
     * @generated
     */
    EClass ASSIGN_MIN = eINSTANCE.getAssignMin();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_MIN__LEFT = eINSTANCE.getAssignMin_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_MIN__RIGHT = eINSTANCE.getAssignMin_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AssignMultImpl <em>Assign Mult</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AssignMultImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignMult()
     * @generated
     */
    EClass ASSIGN_MULT = eINSTANCE.getAssignMult();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_MULT__LEFT = eINSTANCE.getAssignMult_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_MULT__RIGHT = eINSTANCE.getAssignMult_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AssignDivImpl <em>Assign Div</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AssignDivImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAssignDiv()
     * @generated
     */
    EClass ASSIGN_DIV = eINSTANCE.getAssignDiv();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_DIV__LEFT = eINSTANCE.getAssignDiv_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ASSIGN_DIV__RIGHT = eINSTANCE.getAssignDiv_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.TernaryImpl <em>Ternary</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.TernaryImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getTernary()
     * @generated
     */
    EClass TERNARY = eINSTANCE.getTernary();

    /**
     * The meta object literal for the '<em><b>Condition</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TERNARY__CONDITION = eINSTANCE.getTernary_Condition();

    /**
     * The meta object literal for the '<em><b>If True</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TERNARY__IF_TRUE = eINSTANCE.getTernary_IfTrue();

    /**
     * The meta object literal for the '<em><b>If False</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TERNARY__IF_FALSE = eINSTANCE.getTernary_IfFalse();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.OrImpl <em>Or</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.OrImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getOr()
     * @generated
     */
    EClass OR = eINSTANCE.getOr();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR__LEFT = eINSTANCE.getOr_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR__RIGHT = eINSTANCE.getOr_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.AndImpl <em>And</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.AndImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getAnd()
     * @generated
     */
    EClass AND = eINSTANCE.getAnd();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND__LEFT = eINSTANCE.getAnd_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND__RIGHT = eINSTANCE.getAnd_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelNotEqImpl <em>Rel Not Eq</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelNotEqImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelNotEq()
     * @generated
     */
    EClass REL_NOT_EQ = eINSTANCE.getRelNotEq();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_NOT_EQ__LEFT = eINSTANCE.getRelNotEq_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_NOT_EQ__RIGHT = eINSTANCE.getRelNotEq_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelEqImpl <em>Rel Eq</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelEqImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelEq()
     * @generated
     */
    EClass REL_EQ = eINSTANCE.getRelEq();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_EQ__LEFT = eINSTANCE.getRelEq_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_EQ__RIGHT = eINSTANCE.getRelEq_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelEqEqImpl <em>Rel Eq Eq</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelEqEqImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelEqEq()
     * @generated
     */
    EClass REL_EQ_EQ = eINSTANCE.getRelEqEq();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_EQ_EQ__LEFT = eINSTANCE.getRelEqEq_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_EQ_EQ__RIGHT = eINSTANCE.getRelEqEq_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelLtEqImpl <em>Rel Lt Eq</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelLtEqImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelLtEq()
     * @generated
     */
    EClass REL_LT_EQ = eINSTANCE.getRelLtEq();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_LT_EQ__LEFT = eINSTANCE.getRelLtEq_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_LT_EQ__RIGHT = eINSTANCE.getRelLtEq_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelGtEqImpl <em>Rel Gt Eq</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelGtEqImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelGtEq()
     * @generated
     */
    EClass REL_GT_EQ = eINSTANCE.getRelGtEq();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_GT_EQ__LEFT = eINSTANCE.getRelGtEq_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_GT_EQ__RIGHT = eINSTANCE.getRelGtEq_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelLtImpl <em>Rel Lt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelLtImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelLt()
     * @generated
     */
    EClass REL_LT = eINSTANCE.getRelLt();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_LT__LEFT = eINSTANCE.getRelLt_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_LT__RIGHT = eINSTANCE.getRelLt_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.RelGtImpl <em>Rel Gt</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.RelGtImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getRelGt()
     * @generated
     */
    EClass REL_GT = eINSTANCE.getRelGt();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_GT__LEFT = eINSTANCE.getRelGt_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REL_GT__RIGHT = eINSTANCE.getRelGt_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PairImpl <em>Pair</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PairImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPair()
     * @generated
     */
    EClass PAIR = eINSTANCE.getPair();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PAIR__LEFT = eINSTANCE.getPair_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PAIR__RIGHT = eINSTANCE.getPair_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PlusImpl <em>Plus</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PlusImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPlus()
     * @generated
     */
    EClass PLUS = eINSTANCE.getPlus();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PLUS__LEFT = eINSTANCE.getPlus_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PLUS__RIGHT = eINSTANCE.getPlus_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MinusImpl <em>Minus</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MinusImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMinus()
     * @generated
     */
    EClass MINUS = eINSTANCE.getMinus();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MINUS__LEFT = eINSTANCE.getMinus_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MINUS__RIGHT = eINSTANCE.getMinus_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MultiImpl <em>Multi</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MultiImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMulti()
     * @generated
     */
    EClass MULTI = eINSTANCE.getMulti();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MULTI__LEFT = eINSTANCE.getMulti_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MULTI__RIGHT = eINSTANCE.getMulti_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.DivImpl <em>Div</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.DivImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getDiv()
     * @generated
     */
    EClass DIV = eINSTANCE.getDiv();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIV__LEFT = eINSTANCE.getDiv_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DIV__RIGHT = eINSTANCE.getDiv_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.PowImpl <em>Pow</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.PowImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getPow()
     * @generated
     */
    EClass POW = eINSTANCE.getPow();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference POW__LEFT = eINSTANCE.getPow_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference POW__RIGHT = eINSTANCE.getPow_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlBinaryImpl <em>Binary</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlBinaryImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlBinary()
     * @generated
     */
    EClass GAML_BINARY = eINSTANCE.getGamlBinary();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BINARY__LEFT = eINSTANCE.getGamlBinary_Left();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BINARY__OP = eINSTANCE.getGamlBinary_Op();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_BINARY__RIGHT = eINSTANCE.getGamlBinary_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.UnitImpl <em>Unit</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.UnitImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getUnit()
     * @generated
     */
    EClass UNIT = eINSTANCE.getUnit();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference UNIT__LEFT = eINSTANCE.getUnit_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference UNIT__RIGHT = eINSTANCE.getUnit_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.GamlUnaryImpl <em>Unary</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.GamlUnaryImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getGamlUnary()
     * @generated
     */
    EClass GAML_UNARY = eINSTANCE.getGamlUnary();

    /**
     * The meta object literal for the '<em><b>Op</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute GAML_UNARY__OP = eINSTANCE.getGamlUnary_Op();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GAML_UNARY__RIGHT = eINSTANCE.getGamlUnary_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefPImpl <em>Member Ref P</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MemberRefPImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRefP()
     * @generated
     */
    EClass MEMBER_REF_P = eINSTANCE.getMemberRefP();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_REF_P__LEFT = eINSTANCE.getMemberRefP_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_REF_P__RIGHT = eINSTANCE.getMemberRefP_Right();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.MemberRefRImpl <em>Member Ref R</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.MemberRefRImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getMemberRefR()
     * @generated
     */
    EClass MEMBER_REF_R = eINSTANCE.getMemberRefR();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_REF_R__LEFT = eINSTANCE.getMemberRefR_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_REF_R__RIGHT = eINSTANCE.getMemberRefR_Right();

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
     * The meta object literal for the '<em><b>Func</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FUNCTION_REF__FUNC = eINSTANCE.getFunctionRef_Func();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FUNCTION_REF__ARGS = eINSTANCE.getFunctionRef_Args();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.ArrayRefImpl <em>Array Ref</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.ArrayRefImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getArrayRef()
     * @generated
     */
    EClass ARRAY_REF = eINSTANCE.getArrayRef();

    /**
     * The meta object literal for the '<em><b>Array</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARRAY_REF__ARRAY = eINSTANCE.getArrayRef_Array();

    /**
     * The meta object literal for the '<em><b>Args</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ARRAY_REF__ARGS = eINSTANCE.getArrayRef_Args();

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
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INT_LITERAL__VALUE = eINSTANCE.getIntLiteral_Value();

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
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DOUBLE_LITERAL__VALUE = eINSTANCE.getDoubleLiteral_Value();

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
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute COLOR_LITERAL__VALUE = eINSTANCE.getColorLiteral_Value();

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
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_LITERAL__VALUE = eINSTANCE.getStringLiteral_Value();

    /**
     * The meta object literal for the '{@link msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl <em>Boolean Literal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see msi.gama.lang.gaml.gaml.impl.BooleanLiteralImpl
     * @see msi.gama.lang.gaml.gaml.impl.GamlPackageImpl#getBooleanLiteral()
     * @generated
     */
    EClass BOOLEAN_LITERAL = eINSTANCE.getBooleanLiteral();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute BOOLEAN_LITERAL__VALUE = eINSTANCE.getBooleanLiteral_Value();

  }

} //GamlPackage
