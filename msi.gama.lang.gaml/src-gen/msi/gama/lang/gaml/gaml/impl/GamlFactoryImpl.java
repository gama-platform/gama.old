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
      case GamlPackage.DEF_KEYWORD: return createDefKeyword();
      case GamlPackage.GAML_BLOCK: return createGamlBlock();
      case GamlPackage.DEF_FACET: return createDefFacet();
      case GamlPackage.DEF_BINARY_OP: return createDefBinaryOp();
      case GamlPackage.DEF_RESERVED: return createDefReserved();
      case GamlPackage.DEF_UNIT: return createDefUnit();
      case GamlPackage.ABSTRACT_GAML_REF: return createAbstractGamlRef();
      case GamlPackage.GAML_KEYWORD_REF: return createGamlKeywordRef();
      case GamlPackage.GAML_FACET_REF: return createGamlFacetRef();
      case GamlPackage.GAML_BINAR_OP_REF: return createGamlBinarOpRef();
      case GamlPackage.GAML_UNIT_REF: return createGamlUnitRef();
      case GamlPackage.GAML_RESERVED_REF: return createGamlReservedRef();
      case GamlPackage.STATEMENT: return createStatement();
      case GamlPackage.SUB_STATEMENT: return createSubStatement();
      case GamlPackage.SET_EVAL: return createSetEval();
      case GamlPackage.DEFINITION: return createDefinition();
      case GamlPackage.EVALUATION: return createEvaluation();
      case GamlPackage.FACET_EXPR: return createFacetExpr();
      case GamlPackage.BLOCK: return createBlock();
      case GamlPackage.ABSTRACT_DEFINITION: return createAbstractDefinition();
      case GamlPackage.EXPRESSION: return createExpression();
      case GamlPackage.POINT: return createPoint();
      case GamlPackage.MATRIX: return createMatrix();
      case GamlPackage.ROW: return createRow();
      case GamlPackage.VARIABLE_REF: return createVariableRef();
      case GamlPackage.TERMINAL_EXPRESSION: return createTerminalExpression();
      case GamlPackage.ASSIGN_PLUS: return createAssignPlus();
      case GamlPackage.ASSIGN_MIN: return createAssignMin();
      case GamlPackage.ASSIGN_MULT: return createAssignMult();
      case GamlPackage.ASSIGN_DIV: return createAssignDiv();
      case GamlPackage.TERNARY: return createTernary();
      case GamlPackage.OR: return createOr();
      case GamlPackage.AND: return createAnd();
      case GamlPackage.REL_NOT_EQ: return createRelNotEq();
      case GamlPackage.REL_EQ: return createRelEq();
      case GamlPackage.REL_EQ_EQ: return createRelEqEq();
      case GamlPackage.REL_LT_EQ: return createRelLtEq();
      case GamlPackage.REL_GT_EQ: return createRelGtEq();
      case GamlPackage.REL_LT: return createRelLt();
      case GamlPackage.REL_GT: return createRelGt();
      case GamlPackage.PAIR: return createPair();
      case GamlPackage.PLUS: return createPlus();
      case GamlPackage.MINUS: return createMinus();
      case GamlPackage.MULTI: return createMulti();
      case GamlPackage.DIV: return createDiv();
      case GamlPackage.POW: return createPow();
      case GamlPackage.GAML_BINARY: return createGamlBinary();
      case GamlPackage.UNIT: return createUnit();
      case GamlPackage.GAML_UNARY: return createGamlUnary();
      case GamlPackage.MEMBER_REF_P: return createMemberRefP();
      case GamlPackage.MEMBER_REF_R: return createMemberRefR();
      case GamlPackage.FUNCTION_REF: return createFunctionRef();
      case GamlPackage.ARRAY_REF: return createArrayRef();
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
  public DefKeyword createDefKeyword()
  {
    DefKeywordImpl defKeyword = new DefKeywordImpl();
    return defKeyword;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlBlock createGamlBlock()
  {
    GamlBlockImpl gamlBlock = new GamlBlockImpl();
    return gamlBlock;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DefFacet createDefFacet()
  {
    DefFacetImpl defFacet = new DefFacetImpl();
    return defFacet;
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
  public DefUnit createDefUnit()
  {
    DefUnitImpl defUnit = new DefUnitImpl();
    return defUnit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbstractGamlRef createAbstractGamlRef()
  {
    AbstractGamlRefImpl abstractGamlRef = new AbstractGamlRefImpl();
    return abstractGamlRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlKeywordRef createGamlKeywordRef()
  {
    GamlKeywordRefImpl gamlKeywordRef = new GamlKeywordRefImpl();
    return gamlKeywordRef;
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
  public GamlBinarOpRef createGamlBinarOpRef()
  {
    GamlBinarOpRefImpl gamlBinarOpRef = new GamlBinarOpRefImpl();
    return gamlBinarOpRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlUnitRef createGamlUnitRef()
  {
    GamlUnitRefImpl gamlUnitRef = new GamlUnitRefImpl();
    return gamlUnitRef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlReservedRef createGamlReservedRef()
  {
    GamlReservedRefImpl gamlReservedRef = new GamlReservedRefImpl();
    return gamlReservedRef;
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
  public SubStatement createSubStatement()
  {
    SubStatementImpl subStatement = new SubStatementImpl();
    return subStatement;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SetEval createSetEval()
  {
    SetEvalImpl setEval = new SetEvalImpl();
    return setEval;
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
  public Evaluation createEvaluation()
  {
    EvaluationImpl evaluation = new EvaluationImpl();
    return evaluation;
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
  public AbstractDefinition createAbstractDefinition()
  {
    AbstractDefinitionImpl abstractDefinition = new AbstractDefinitionImpl();
    return abstractDefinition;
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
  public Matrix createMatrix()
  {
    MatrixImpl matrix = new MatrixImpl();
    return matrix;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Row createRow()
  {
    RowImpl row = new RowImpl();
    return row;
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
  public AssignPlus createAssignPlus()
  {
    AssignPlusImpl assignPlus = new AssignPlusImpl();
    return assignPlus;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AssignMin createAssignMin()
  {
    AssignMinImpl assignMin = new AssignMinImpl();
    return assignMin;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AssignMult createAssignMult()
  {
    AssignMultImpl assignMult = new AssignMultImpl();
    return assignMult;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AssignDiv createAssignDiv()
  {
    AssignDivImpl assignDiv = new AssignDivImpl();
    return assignDiv;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Ternary createTernary()
  {
    TernaryImpl ternary = new TernaryImpl();
    return ternary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Or createOr()
  {
    OrImpl or = new OrImpl();
    return or;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public And createAnd()
  {
    AndImpl and = new AndImpl();
    return and;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelNotEq createRelNotEq()
  {
    RelNotEqImpl relNotEq = new RelNotEqImpl();
    return relNotEq;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelEq createRelEq()
  {
    RelEqImpl relEq = new RelEqImpl();
    return relEq;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelEqEq createRelEqEq()
  {
    RelEqEqImpl relEqEq = new RelEqEqImpl();
    return relEqEq;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelLtEq createRelLtEq()
  {
    RelLtEqImpl relLtEq = new RelLtEqImpl();
    return relLtEq;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelGtEq createRelGtEq()
  {
    RelGtEqImpl relGtEq = new RelGtEqImpl();
    return relGtEq;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelLt createRelLt()
  {
    RelLtImpl relLt = new RelLtImpl();
    return relLt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RelGt createRelGt()
  {
    RelGtImpl relGt = new RelGtImpl();
    return relGt;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pair createPair()
  {
    PairImpl pair = new PairImpl();
    return pair;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Plus createPlus()
  {
    PlusImpl plus = new PlusImpl();
    return plus;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Minus createMinus()
  {
    MinusImpl minus = new MinusImpl();
    return minus;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multi createMulti()
  {
    MultiImpl multi = new MultiImpl();
    return multi;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Div createDiv()
  {
    DivImpl div = new DivImpl();
    return div;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pow createPow()
  {
    PowImpl pow = new PowImpl();
    return pow;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlBinary createGamlBinary()
  {
    GamlBinaryImpl gamlBinary = new GamlBinaryImpl();
    return gamlBinary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Unit createUnit()
  {
    UnitImpl unit = new UnitImpl();
    return unit;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlUnary createGamlUnary()
  {
    GamlUnaryImpl gamlUnary = new GamlUnaryImpl();
    return gamlUnary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberRefP createMemberRefP()
  {
    MemberRefPImpl memberRefP = new MemberRefPImpl();
    return memberRefP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MemberRefR createMemberRefR()
  {
    MemberRefRImpl memberRefR = new MemberRefRImpl();
    return memberRefR;
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
  public ArrayRef createArrayRef()
  {
    ArrayRefImpl arrayRef = new ArrayRefImpl();
    return arrayRef;
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
