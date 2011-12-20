/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.gaml.util;

import msi.gama.lang.gaml.gaml.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see msi.gama.lang.gaml.gaml.GamlPackage
 * @generated
 */
public class GamlAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static GamlPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = GamlPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GamlSwitch<Adapter> modelSwitch =
    new GamlSwitch<Adapter>()
    {
      @Override
      public Adapter caseModel(Model object)
      {
        return createModelAdapter();
      }
      @Override
      public Adapter caseImport(Import object)
      {
        return createImportAdapter();
      }
      @Override
      public Adapter caseGamlLangDef(GamlLangDef object)
      {
        return createGamlLangDefAdapter();
      }
      @Override
      public Adapter caseDefKeyword(DefKeyword object)
      {
        return createDefKeywordAdapter();
      }
      @Override
      public Adapter caseGamlBlock(GamlBlock object)
      {
        return createGamlBlockAdapter();
      }
      @Override
      public Adapter caseDefFacet(DefFacet object)
      {
        return createDefFacetAdapter();
      }
      @Override
      public Adapter caseDefBinaryOp(DefBinaryOp object)
      {
        return createDefBinaryOpAdapter();
      }
      @Override
      public Adapter caseDefReserved(DefReserved object)
      {
        return createDefReservedAdapter();
      }
      @Override
      public Adapter caseDefUnit(DefUnit object)
      {
        return createDefUnitAdapter();
      }
      @Override
      public Adapter caseAbstractGamlRef(AbstractGamlRef object)
      {
        return createAbstractGamlRefAdapter();
      }
      @Override
      public Adapter caseGamlKeywordRef(GamlKeywordRef object)
      {
        return createGamlKeywordRefAdapter();
      }
      @Override
      public Adapter caseGamlFacetRef(GamlFacetRef object)
      {
        return createGamlFacetRefAdapter();
      }
      @Override
      public Adapter caseGamlBinarOpRef(GamlBinarOpRef object)
      {
        return createGamlBinarOpRefAdapter();
      }
      @Override
      public Adapter caseGamlUnitRef(GamlUnitRef object)
      {
        return createGamlUnitRefAdapter();
      }
      @Override
      public Adapter caseGamlReservedRef(GamlReservedRef object)
      {
        return createGamlReservedRefAdapter();
      }
      @Override
      public Adapter caseStatement(Statement object)
      {
        return createStatementAdapter();
      }
      @Override
      public Adapter caseSubStatement(SubStatement object)
      {
        return createSubStatementAdapter();
      }
      @Override
      public Adapter caseSetEval(SetEval object)
      {
        return createSetEvalAdapter();
      }
      @Override
      public Adapter caseDefinition(Definition object)
      {
        return createDefinitionAdapter();
      }
      @Override
      public Adapter caseEvaluation(Evaluation object)
      {
        return createEvaluationAdapter();
      }
      @Override
      public Adapter caseFacetExpr(FacetExpr object)
      {
        return createFacetExprAdapter();
      }
      @Override
      public Adapter caseBlock(Block object)
      {
        return createBlockAdapter();
      }
      @Override
      public Adapter caseAbstractDefinition(AbstractDefinition object)
      {
        return createAbstractDefinitionAdapter();
      }
      @Override
      public Adapter caseExpression(Expression object)
      {
        return createExpressionAdapter();
      }
      @Override
      public Adapter casePoint(Point object)
      {
        return createPointAdapter();
      }
      @Override
      public Adapter caseMatrix(Matrix object)
      {
        return createMatrixAdapter();
      }
      @Override
      public Adapter caseRow(Row object)
      {
        return createRowAdapter();
      }
      @Override
      public Adapter caseVariableRef(VariableRef object)
      {
        return createVariableRefAdapter();
      }
      @Override
      public Adapter caseTerminalExpression(TerminalExpression object)
      {
        return createTerminalExpressionAdapter();
      }
      @Override
      public Adapter caseAssignPlus(AssignPlus object)
      {
        return createAssignPlusAdapter();
      }
      @Override
      public Adapter caseAssignMin(AssignMin object)
      {
        return createAssignMinAdapter();
      }
      @Override
      public Adapter caseAssignMult(AssignMult object)
      {
        return createAssignMultAdapter();
      }
      @Override
      public Adapter caseAssignDiv(AssignDiv object)
      {
        return createAssignDivAdapter();
      }
      @Override
      public Adapter caseTernary(Ternary object)
      {
        return createTernaryAdapter();
      }
      @Override
      public Adapter caseOr(Or object)
      {
        return createOrAdapter();
      }
      @Override
      public Adapter caseAnd(And object)
      {
        return createAndAdapter();
      }
      @Override
      public Adapter caseRelNotEq(RelNotEq object)
      {
        return createRelNotEqAdapter();
      }
      @Override
      public Adapter caseRelEq(RelEq object)
      {
        return createRelEqAdapter();
      }
      @Override
      public Adapter caseRelEqEq(RelEqEq object)
      {
        return createRelEqEqAdapter();
      }
      @Override
      public Adapter caseRelLtEq(RelLtEq object)
      {
        return createRelLtEqAdapter();
      }
      @Override
      public Adapter caseRelGtEq(RelGtEq object)
      {
        return createRelGtEqAdapter();
      }
      @Override
      public Adapter caseRelLt(RelLt object)
      {
        return createRelLtAdapter();
      }
      @Override
      public Adapter caseRelGt(RelGt object)
      {
        return createRelGtAdapter();
      }
      @Override
      public Adapter casePair(Pair object)
      {
        return createPairAdapter();
      }
      @Override
      public Adapter casePlus(Plus object)
      {
        return createPlusAdapter();
      }
      @Override
      public Adapter caseMinus(Minus object)
      {
        return createMinusAdapter();
      }
      @Override
      public Adapter caseMulti(Multi object)
      {
        return createMultiAdapter();
      }
      @Override
      public Adapter caseDiv(Div object)
      {
        return createDivAdapter();
      }
      @Override
      public Adapter caseGamlBinary(GamlBinary object)
      {
        return createGamlBinaryAdapter();
      }
      @Override
      public Adapter casePow(Pow object)
      {
        return createPowAdapter();
      }
      @Override
      public Adapter caseUnit(Unit object)
      {
        return createUnitAdapter();
      }
      @Override
      public Adapter caseGamlUnary(GamlUnary object)
      {
        return createGamlUnaryAdapter();
      }
      @Override
      public Adapter caseMemberRefP(MemberRefP object)
      {
        return createMemberRefPAdapter();
      }
      @Override
      public Adapter caseMemberRefR(MemberRefR object)
      {
        return createMemberRefRAdapter();
      }
      @Override
      public Adapter caseFunctionRef(FunctionRef object)
      {
        return createFunctionRefAdapter();
      }
      @Override
      public Adapter caseArrayRef(ArrayRef object)
      {
        return createArrayRefAdapter();
      }
      @Override
      public Adapter caseIntLiteral(IntLiteral object)
      {
        return createIntLiteralAdapter();
      }
      @Override
      public Adapter caseDoubleLiteral(DoubleLiteral object)
      {
        return createDoubleLiteralAdapter();
      }
      @Override
      public Adapter caseColorLiteral(ColorLiteral object)
      {
        return createColorLiteralAdapter();
      }
      @Override
      public Adapter caseStringLiteral(StringLiteral object)
      {
        return createStringLiteralAdapter();
      }
      @Override
      public Adapter caseBooleanLiteral(BooleanLiteral object)
      {
        return createBooleanLiteralAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Model
   * @generated
   */
  public Adapter createModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Import <em>Import</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Import
   * @generated
   */
  public Adapter createImportAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlLangDef <em>Lang Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlLangDef
   * @generated
   */
  public Adapter createGamlLangDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefKeyword <em>Def Keyword</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefKeyword
   * @generated
   */
  public Adapter createDefKeywordAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlBlock <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlBlock
   * @generated
   */
  public Adapter createGamlBlockAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefFacet <em>Def Facet</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefFacet
   * @generated
   */
  public Adapter createDefFacetAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefBinaryOp <em>Def Binary Op</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefBinaryOp
   * @generated
   */
  public Adapter createDefBinaryOpAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefReserved <em>Def Reserved</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefReserved
   * @generated
   */
  public Adapter createDefReservedAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefUnit <em>Def Unit</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefUnit
   * @generated
   */
  public Adapter createDefUnitAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AbstractGamlRef <em>Abstract Gaml Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AbstractGamlRef
   * @generated
   */
  public Adapter createAbstractGamlRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlKeywordRef <em>Keyword Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlKeywordRef
   * @generated
   */
  public Adapter createGamlKeywordRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlFacetRef <em>Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlFacetRef
   * @generated
   */
  public Adapter createGamlFacetRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlBinarOpRef <em>Binar Op Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlBinarOpRef
   * @generated
   */
  public Adapter createGamlBinarOpRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlUnitRef <em>Unit Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlUnitRef
   * @generated
   */
  public Adapter createGamlUnitRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlReservedRef <em>Reserved Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlReservedRef
   * @generated
   */
  public Adapter createGamlReservedRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Statement <em>Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Statement
   * @generated
   */
  public Adapter createStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.SubStatement <em>Sub Statement</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.SubStatement
   * @generated
   */
  public Adapter createSubStatementAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.SetEval <em>Set Eval</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.SetEval
   * @generated
   */
  public Adapter createSetEvalAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Definition <em>Definition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Definition
   * @generated
   */
  public Adapter createDefinitionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Evaluation <em>Evaluation</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Evaluation
   * @generated
   */
  public Adapter createEvaluationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.FacetExpr <em>Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.FacetExpr
   * @generated
   */
  public Adapter createFacetExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Block <em>Block</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Block
   * @generated
   */
  public Adapter createBlockAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AbstractDefinition <em>Abstract Definition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AbstractDefinition
   * @generated
   */
  public Adapter createAbstractDefinitionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Expression
   * @generated
   */
  public Adapter createExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Point <em>Point</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Point
   * @generated
   */
  public Adapter createPointAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Matrix <em>Matrix</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Matrix
   * @generated
   */
  public Adapter createMatrixAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Row <em>Row</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Row
   * @generated
   */
  public Adapter createRowAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.VariableRef <em>Variable Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.VariableRef
   * @generated
   */
  public Adapter createVariableRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.TerminalExpression <em>Terminal Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.TerminalExpression
   * @generated
   */
  public Adapter createTerminalExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AssignPlus <em>Assign Plus</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AssignPlus
   * @generated
   */
  public Adapter createAssignPlusAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AssignMin <em>Assign Min</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AssignMin
   * @generated
   */
  public Adapter createAssignMinAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AssignMult <em>Assign Mult</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AssignMult
   * @generated
   */
  public Adapter createAssignMultAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.AssignDiv <em>Assign Div</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.AssignDiv
   * @generated
   */
  public Adapter createAssignDivAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Ternary <em>Ternary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Ternary
   * @generated
   */
  public Adapter createTernaryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Or
   * @generated
   */
  public Adapter createOrAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.And
   * @generated
   */
  public Adapter createAndAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelNotEq <em>Rel Not Eq</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelNotEq
   * @generated
   */
  public Adapter createRelNotEqAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelEq <em>Rel Eq</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelEq
   * @generated
   */
  public Adapter createRelEqAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelEqEq <em>Rel Eq Eq</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelEqEq
   * @generated
   */
  public Adapter createRelEqEqAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelLtEq <em>Rel Lt Eq</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelLtEq
   * @generated
   */
  public Adapter createRelLtEqAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelGtEq <em>Rel Gt Eq</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelGtEq
   * @generated
   */
  public Adapter createRelGtEqAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelLt <em>Rel Lt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelLt
   * @generated
   */
  public Adapter createRelLtAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.RelGt <em>Rel Gt</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.RelGt
   * @generated
   */
  public Adapter createRelGtAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Pair <em>Pair</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Pair
   * @generated
   */
  public Adapter createPairAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Plus <em>Plus</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Plus
   * @generated
   */
  public Adapter createPlusAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Minus <em>Minus</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Minus
   * @generated
   */
  public Adapter createMinusAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Multi <em>Multi</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Multi
   * @generated
   */
  public Adapter createMultiAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Div <em>Div</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Div
   * @generated
   */
  public Adapter createDivAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlBinary <em>Binary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlBinary
   * @generated
   */
  public Adapter createGamlBinaryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Pow <em>Pow</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Pow
   * @generated
   */
  public Adapter createPowAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Unit <em>Unit</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Unit
   * @generated
   */
  public Adapter createUnitAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlUnary <em>Unary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlUnary
   * @generated
   */
  public Adapter createGamlUnaryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.MemberRefP <em>Member Ref P</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.MemberRefP
   * @generated
   */
  public Adapter createMemberRefPAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.MemberRefR <em>Member Ref R</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.MemberRefR
   * @generated
   */
  public Adapter createMemberRefRAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.FunctionRef <em>Function Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.FunctionRef
   * @generated
   */
  public Adapter createFunctionRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.ArrayRef <em>Array Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.ArrayRef
   * @generated
   */
  public Adapter createArrayRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.IntLiteral <em>Int Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.IntLiteral
   * @generated
   */
  public Adapter createIntLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DoubleLiteral <em>Double Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DoubleLiteral
   * @generated
   */
  public Adapter createDoubleLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.ColorLiteral <em>Color Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.ColorLiteral
   * @generated
   */
  public Adapter createColorLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.StringLiteral <em>String Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.StringLiteral
   * @generated
   */
  public Adapter createStringLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.BooleanLiteral <em>Boolean Literal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.BooleanLiteral
   * @generated
   */
  public Adapter createBooleanLiteralAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //GamlAdapterFactory
