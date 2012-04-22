/**
 * <copyright>
 * </copyright>
 *
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
      public Adapter caseDefUnary(DefUnary object)
      {
        return createDefUnaryAdapter();
      }
      @Override
      public Adapter caseStatement(Statement object)
      {
        return createStatementAdapter();
      }
      @Override
      public Adapter caseDefinition(Definition object)
      {
        return createDefinitionAdapter();
      }
      @Override
      public Adapter caseFacetRef(FacetRef object)
      {
        return createFacetRefAdapter();
      }
      @Override
      public Adapter caseGamlFacetRef(GamlFacetRef object)
      {
        return createGamlFacetRefAdapter();
      }
      @Override
      public Adapter caseFunctionGamlFacetRef(FunctionGamlFacetRef object)
      {
        return createFunctionGamlFacetRefAdapter();
      }
      @Override
      public Adapter caseFacetExpr(FacetExpr object)
      {
        return createFacetExprAdapter();
      }
      @Override
      public Adapter caseDefinitionFacetExpr(DefinitionFacetExpr object)
      {
        return createDefinitionFacetExprAdapter();
      }
      @Override
      public Adapter caseNameFacetExpr(NameFacetExpr object)
      {
        return createNameFacetExprAdapter();
      }
      @Override
      public Adapter caseReturnsFacetExpr(ReturnsFacetExpr object)
      {
        return createReturnsFacetExprAdapter();
      }
      @Override
      public Adapter caseActionFacetExpr(ActionFacetExpr object)
      {
        return createActionFacetExprAdapter();
      }
      @Override
      public Adapter caseFunctionFacetExpr(FunctionFacetExpr object)
      {
        return createFunctionFacetExprAdapter();
      }
      @Override
      public Adapter caseBlock(Block object)
      {
        return createBlockAdapter();
      }
      @Override
      public Adapter caseExpression(Expression object)
      {
        return createExpressionAdapter();
      }
      @Override
      public Adapter caseVariableRef(VariableRef object)
      {
        return createVariableRefAdapter();
      }
      @Override
      public Adapter caseGamlVarRef(GamlVarRef object)
      {
        return createGamlVarRefAdapter();
      }
      @Override
      public Adapter caseTerminalExpression(TerminalExpression object)
      {
        return createTerminalExpressionAdapter();
      }
      @Override
      public Adapter caseTernExp(TernExp object)
      {
        return createTernExpAdapter();
      }
      @Override
      public Adapter casePairExpr(PairExpr object)
      {
        return createPairExprAdapter();
      }
      @Override
      public Adapter caseGamlBinaryExpr(GamlBinaryExpr object)
      {
        return createGamlBinaryExprAdapter();
      }
      @Override
      public Adapter caseGamlUnitExpr(GamlUnitExpr object)
      {
        return createGamlUnitExprAdapter();
      }
      @Override
      public Adapter caseGamlUnaryExpr(GamlUnaryExpr object)
      {
        return createGamlUnaryExprAdapter();
      }
      @Override
      public Adapter caseMemberRef(MemberRef object)
      {
        return createMemberRefAdapter();
      }
      @Override
      public Adapter caseArray(Array object)
      {
        return createArrayAdapter();
      }
      @Override
      public Adapter casePoint(Point object)
      {
        return createPointAdapter();
      }
      @Override
      public Adapter caseFunction(Function object)
      {
        return createFunctionAdapter();
      }
      @Override
      public Adapter caseArbitraryName(ArbitraryName object)
      {
        return createArbitraryNameAdapter();
      }
      @Override
      public Adapter caseUnitName(UnitName object)
      {
        return createUnitNameAdapter();
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefUnary <em>Def Unary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefUnary
   * @generated
   */
  public Adapter createDefUnaryAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.FacetRef <em>Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.FacetRef
   * @generated
   */
  public Adapter createFacetRefAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.FunctionGamlFacetRef <em>Function Gaml Facet Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.FunctionGamlFacetRef
   * @generated
   */
  public Adapter createFunctionGamlFacetRefAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.DefinitionFacetExpr <em>Definition Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.DefinitionFacetExpr
   * @generated
   */
  public Adapter createDefinitionFacetExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.NameFacetExpr <em>Name Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.NameFacetExpr
   * @generated
   */
  public Adapter createNameFacetExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.ReturnsFacetExpr <em>Returns Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.ReturnsFacetExpr
   * @generated
   */
  public Adapter createReturnsFacetExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.ActionFacetExpr <em>Action Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.ActionFacetExpr
   * @generated
   */
  public Adapter createActionFacetExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.FunctionFacetExpr <em>Function Facet Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.FunctionFacetExpr
   * @generated
   */
  public Adapter createFunctionFacetExprAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlVarRef <em>Var Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlVarRef
   * @generated
   */
  public Adapter createGamlVarRefAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.TernExp <em>Tern Exp</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.TernExp
   * @generated
   */
  public Adapter createTernExpAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.PairExpr <em>Pair Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.PairExpr
   * @generated
   */
  public Adapter createPairExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlBinaryExpr <em>Binary Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlBinaryExpr
   * @generated
   */
  public Adapter createGamlBinaryExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlUnitExpr <em>Unit Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlUnitExpr
   * @generated
   */
  public Adapter createGamlUnitExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.GamlUnaryExpr <em>Unary Expr</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.GamlUnaryExpr
   * @generated
   */
  public Adapter createGamlUnaryExprAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.MemberRef <em>Member Ref</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.MemberRef
   * @generated
   */
  public Adapter createMemberRefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Array <em>Array</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Array
   * @generated
   */
  public Adapter createArrayAdapter()
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
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.Function <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.Function
   * @generated
   */
  public Adapter createFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.ArbitraryName <em>Arbitrary Name</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.ArbitraryName
   * @generated
   */
  public Adapter createArbitraryNameAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link msi.gama.lang.gaml.gaml.UnitName <em>Unit Name</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see msi.gama.lang.gaml.gaml.UnitName
   * @generated
   */
  public Adapter createUnitNameAdapter()
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
