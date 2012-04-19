/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GamlFactoryImpl extends EFactoryImpl implements GamlFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static GamlFactory init()
  {
    try
    {
      GamlFactory theGamlFactory = (GamlFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.gama.msi/lang/gaml/Gaml"); 
      if (theGamlFactory != null)
      {
        return theGamlFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new GamlFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case GamlPackage.MODEL: return createModel();
      case GamlPackage.IMPORT: return createImport();
      case GamlPackage.GAML_LANG_DEF: return createGamlLangDef();
      case GamlPackage.DEF_BINARY_OP: return createDefBinaryOp();
      case GamlPackage.DEF_RESERVED: return createDefReserved();
      case GamlPackage.DEF_UNARY: return createDefUnary();
      case GamlPackage.STATEMENT: return createStatement();
      case GamlPackage.DEFINITION: return createDefinition();
      case GamlPackage.FACET_REF: return createFacetRef();
      case GamlPackage.GAML_FACET_REF: return createGamlFacetRef();
      case GamlPackage.FUNCTION_GAML_FACET_REF: return createFunctionGamlFacetRef();
      case GamlPackage.FACET_EXPR: return createFacetExpr();
      case GamlPackage.DEFINITION_FACET_EXPR: return createDefinitionFacetExpr();
      case GamlPackage.NAME_FACET_EXPR: return createNameFacetExpr();
      case GamlPackage.RETURNS_FACET_EXPR: return createReturnsFacetExpr();
      case GamlPackage.ACTION_FACET_EXPR: return createActionFacetExpr();
      case GamlPackage.FUNCTION_FACET_EXPR: return createFunctionFacetExpr();
      case GamlPackage.BLOCK: return createBlock();
      case GamlPackage.EXPRESSION: return createExpression();
      case GamlPackage.VARIABLE_REF: return createVariableRef();
      case GamlPackage.GAML_VAR_REF: return createGamlVarRef();
      case GamlPackage.TERMINAL_EXPRESSION: return createTerminalExpression();
      case GamlPackage.TERN_EXP: return createTernExp();
      case GamlPackage.PAIR_EXPR: return createPairExpr();
      case GamlPackage.GAML_BINARY_EXPR: return createGamlBinaryExpr();
      case GamlPackage.GAML_UNIT_EXPR: return createGamlUnitExpr();
      case GamlPackage.GAML_UNARY_EXPR: return createGamlUnaryExpr();
      case GamlPackage.MEMBER_REF: return createMemberRef();
      case GamlPackage.ARRAY: return createArray();
      case GamlPackage.POINT: return createPoint();
      case GamlPackage.FUNCTION_REF: return createFunctionRef();
      case GamlPackage.ARBITRARY_NAME: return createArbitraryName();
      case GamlPackage.UNIT_NAME: return createUnitName();
      case GamlPackage.INT_LITERAL: return createIntLiteral();
      case GamlPackage.DOUBLE_LITERAL: return createDoubleLiteral();
      case GamlPackage.COLOR_LITERAL: return createColorLiteral();
      case GamlPackage.STRING_LITERAL: return createStringLiteral();
      case GamlPackage.BOOLEAN_LITERAL: return createBooleanLiteral();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model createModel()
  {
    ModelImpl model = new ModelImpl();
    return model;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Import createImport()
  {
    ImportImpl import_ = new ImportImpl();
    return import_;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlLangDef createGamlLangDef()
  {
    GamlLangDefImpl gamlLangDef = new GamlLangDefImpl();
    return gamlLangDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DefBinaryOp createDefBinaryOp()
  {
    DefBinaryOpImpl defBinaryOp = new DefBinaryOpImpl();
    return defBinaryOp;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DefReserved createDefReserved()
  {
    DefReservedImpl defReserved = new DefReservedImpl();
    return defReserved;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DefUnary createDefUnary()
  {
    DefUnaryImpl defUnary = new DefUnaryImpl();
    return defUnary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Statement createStatement()
  {
    StatementImpl statement = new StatementImpl();
    return statement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Definition createDefinition()
  {
    DefinitionImpl definition = new DefinitionImpl();
    return definition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FacetRef createFacetRef()
  {
    FacetRefImpl facetRef = new FacetRefImpl();
    return facetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlFacetRef createGamlFacetRef()
  {
    GamlFacetRefImpl gamlFacetRef = new GamlFacetRefImpl();
    return gamlFacetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionGamlFacetRef createFunctionGamlFacetRef()
  {
    FunctionGamlFacetRefImpl functionGamlFacetRef = new FunctionGamlFacetRefImpl();
    return functionGamlFacetRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FacetExpr createFacetExpr()
  {
    FacetExprImpl facetExpr = new FacetExprImpl();
    return facetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DefinitionFacetExpr createDefinitionFacetExpr()
  {
    DefinitionFacetExprImpl definitionFacetExpr = new DefinitionFacetExprImpl();
    return definitionFacetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NameFacetExpr createNameFacetExpr()
  {
    NameFacetExprImpl nameFacetExpr = new NameFacetExprImpl();
    return nameFacetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReturnsFacetExpr createReturnsFacetExpr()
  {
    ReturnsFacetExprImpl returnsFacetExpr = new ReturnsFacetExprImpl();
    return returnsFacetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ActionFacetExpr createActionFacetExpr()
  {
    ActionFacetExprImpl actionFacetExpr = new ActionFacetExprImpl();
    return actionFacetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionFacetExpr createFunctionFacetExpr()
  {
    FunctionFacetExprImpl functionFacetExpr = new FunctionFacetExprImpl();
    return functionFacetExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Block createBlock()
  {
    BlockImpl block = new BlockImpl();
    return block;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression createExpression()
  {
    ExpressionImpl expression = new ExpressionImpl();
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VariableRef createVariableRef()
  {
    VariableRefImpl variableRef = new VariableRefImpl();
    return variableRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlVarRef createGamlVarRef()
  {
    GamlVarRefImpl gamlVarRef = new GamlVarRefImpl();
    return gamlVarRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TerminalExpression createTerminalExpression()
  {
    TerminalExpressionImpl terminalExpression = new TerminalExpressionImpl();
    return terminalExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TernExp createTernExp()
  {
    TernExpImpl ternExp = new TernExpImpl();
    return ternExp;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PairExpr createPairExpr()
  {
    PairExprImpl pairExpr = new PairExprImpl();
    return pairExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlBinaryExpr createGamlBinaryExpr()
  {
    GamlBinaryExprImpl gamlBinaryExpr = new GamlBinaryExprImpl();
    return gamlBinaryExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlUnitExpr createGamlUnitExpr()
  {
    GamlUnitExprImpl gamlUnitExpr = new GamlUnitExprImpl();
    return gamlUnitExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlUnaryExpr createGamlUnaryExpr()
  {
    GamlUnaryExprImpl gamlUnaryExpr = new GamlUnaryExprImpl();
    return gamlUnaryExpr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberRef createMemberRef()
  {
    MemberRefImpl memberRef = new MemberRefImpl();
    return memberRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Array createArray()
  {
    ArrayImpl array = new ArrayImpl();
    return array;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Point createPoint()
  {
    PointImpl point = new PointImpl();
    return point;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public FunctionRef createFunctionRef()
  {
    FunctionRefImpl functionRef = new FunctionRefImpl();
    return functionRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ArbitraryName createArbitraryName()
  {
    ArbitraryNameImpl arbitraryName = new ArbitraryNameImpl();
    return arbitraryName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UnitName createUnitName()
  {
    UnitNameImpl unitName = new UnitNameImpl();
    return unitName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntLiteral createIntLiteral()
  {
    IntLiteralImpl intLiteral = new IntLiteralImpl();
    return intLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoubleLiteral createDoubleLiteral()
  {
    DoubleLiteralImpl doubleLiteral = new DoubleLiteralImpl();
    return doubleLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ColorLiteral createColorLiteral()
  {
    ColorLiteralImpl colorLiteral = new ColorLiteralImpl();
    return colorLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StringLiteral createStringLiteral()
  {
    StringLiteralImpl stringLiteral = new StringLiteralImpl();
    return stringLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public BooleanLiteral createBooleanLiteral()
  {
    BooleanLiteralImpl booleanLiteral = new BooleanLiteralImpl();
    return booleanLiteral;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlPackage getGamlPackage()
  {
    return (GamlPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static GamlPackage getPackage()
  {
    return GamlPackage.eINSTANCE;
  }

} //GamlFactoryImpl
