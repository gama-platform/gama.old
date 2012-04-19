/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml.util;

import msi.gama.lang.gaml.gaml.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see msi.gama.lang.gaml.gaml.GamlPackage
 * @generated
 */
public class GamlSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static GamlPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GamlSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = GamlPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @parameter ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case GamlPackage.MODEL:
      {
        Model model = (Model)theEObject;
        T result = caseModel(model);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.IMPORT:
      {
        Import import_ = (Import)theEObject;
        T result = caseImport(import_);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_LANG_DEF:
      {
        GamlLangDef gamlLangDef = (GamlLangDef)theEObject;
        T result = caseGamlLangDef(gamlLangDef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DEF_BINARY_OP:
      {
        DefBinaryOp defBinaryOp = (DefBinaryOp)theEObject;
        T result = caseDefBinaryOp(defBinaryOp);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DEF_RESERVED:
      {
        DefReserved defReserved = (DefReserved)theEObject;
        T result = caseDefReserved(defReserved);
        if (result == null) result = caseGamlVarRef(defReserved);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DEF_UNARY:
      {
        DefUnary defUnary = (DefUnary)theEObject;
        T result = caseDefUnary(defUnary);
        if (result == null) result = caseGamlVarRef(defUnary);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.STATEMENT:
      {
        Statement statement = (Statement)theEObject;
        T result = caseStatement(statement);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DEFINITION:
      {
        Definition definition = (Definition)theEObject;
        T result = caseDefinition(definition);
        if (result == null) result = caseStatement(definition);
        if (result == null) result = caseGamlVarRef(definition);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.FACET_REF:
      {
        FacetRef facetRef = (FacetRef)theEObject;
        T result = caseFacetRef(facetRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_FACET_REF:
      {
        GamlFacetRef gamlFacetRef = (GamlFacetRef)theEObject;
        T result = caseGamlFacetRef(gamlFacetRef);
        if (result == null) result = caseFacetRef(gamlFacetRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.FUNCTION_GAML_FACET_REF:
      {
        FunctionGamlFacetRef functionGamlFacetRef = (FunctionGamlFacetRef)theEObject;
        T result = caseFunctionGamlFacetRef(functionGamlFacetRef);
        if (result == null) result = caseFacetRef(functionGamlFacetRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.FACET_EXPR:
      {
        FacetExpr facetExpr = (FacetExpr)theEObject;
        T result = caseFacetExpr(facetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DEFINITION_FACET_EXPR:
      {
        DefinitionFacetExpr definitionFacetExpr = (DefinitionFacetExpr)theEObject;
        T result = caseDefinitionFacetExpr(definitionFacetExpr);
        if (result == null) result = caseFacetExpr(definitionFacetExpr);
        if (result == null) result = caseGamlVarRef(definitionFacetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.NAME_FACET_EXPR:
      {
        NameFacetExpr nameFacetExpr = (NameFacetExpr)theEObject;
        T result = caseNameFacetExpr(nameFacetExpr);
        if (result == null) result = caseDefinitionFacetExpr(nameFacetExpr);
        if (result == null) result = caseFacetExpr(nameFacetExpr);
        if (result == null) result = caseGamlVarRef(nameFacetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.RETURNS_FACET_EXPR:
      {
        ReturnsFacetExpr returnsFacetExpr = (ReturnsFacetExpr)theEObject;
        T result = caseReturnsFacetExpr(returnsFacetExpr);
        if (result == null) result = caseDefinitionFacetExpr(returnsFacetExpr);
        if (result == null) result = caseFacetExpr(returnsFacetExpr);
        if (result == null) result = caseGamlVarRef(returnsFacetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.ACTION_FACET_EXPR:
      {
        ActionFacetExpr actionFacetExpr = (ActionFacetExpr)theEObject;
        T result = caseActionFacetExpr(actionFacetExpr);
        if (result == null) result = caseDefinitionFacetExpr(actionFacetExpr);
        if (result == null) result = caseFacetExpr(actionFacetExpr);
        if (result == null) result = caseGamlVarRef(actionFacetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.FUNCTION_FACET_EXPR:
      {
        FunctionFacetExpr functionFacetExpr = (FunctionFacetExpr)theEObject;
        T result = caseFunctionFacetExpr(functionFacetExpr);
        if (result == null) result = caseFacetExpr(functionFacetExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.BLOCK:
      {
        Block block = (Block)theEObject;
        T result = caseBlock(block);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.EXPRESSION:
      {
        Expression expression = (Expression)theEObject;
        T result = caseExpression(expression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.VARIABLE_REF:
      {
        VariableRef variableRef = (VariableRef)theEObject;
        T result = caseVariableRef(variableRef);
        if (result == null) result = caseExpression(variableRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_VAR_REF:
      {
        GamlVarRef gamlVarRef = (GamlVarRef)theEObject;
        T result = caseGamlVarRef(gamlVarRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.TERMINAL_EXPRESSION:
      {
        TerminalExpression terminalExpression = (TerminalExpression)theEObject;
        T result = caseTerminalExpression(terminalExpression);
        if (result == null) result = caseExpression(terminalExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.TERN_EXP:
      {
        TernExp ternExp = (TernExp)theEObject;
        T result = caseTernExp(ternExp);
        if (result == null) result = caseExpression(ternExp);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.PAIR_EXPR:
      {
        PairExpr pairExpr = (PairExpr)theEObject;
        T result = casePairExpr(pairExpr);
        if (result == null) result = caseExpression(pairExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_BINARY_EXPR:
      {
        GamlBinaryExpr gamlBinaryExpr = (GamlBinaryExpr)theEObject;
        T result = caseGamlBinaryExpr(gamlBinaryExpr);
        if (result == null) result = caseExpression(gamlBinaryExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_UNIT_EXPR:
      {
        GamlUnitExpr gamlUnitExpr = (GamlUnitExpr)theEObject;
        T result = caseGamlUnitExpr(gamlUnitExpr);
        if (result == null) result = caseExpression(gamlUnitExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.GAML_UNARY_EXPR:
      {
        GamlUnaryExpr gamlUnaryExpr = (GamlUnaryExpr)theEObject;
        T result = caseGamlUnaryExpr(gamlUnaryExpr);
        if (result == null) result = caseExpression(gamlUnaryExpr);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.MEMBER_REF:
      {
        MemberRef memberRef = (MemberRef)theEObject;
        T result = caseMemberRef(memberRef);
        if (result == null) result = caseExpression(memberRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.ARRAY:
      {
        Array array = (Array)theEObject;
        T result = caseArray(array);
        if (result == null) result = caseExpression(array);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.POINT:
      {
        Point point = (Point)theEObject;
        T result = casePoint(point);
        if (result == null) result = caseExpression(point);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.FUNCTION_REF:
      {
        FunctionRef functionRef = (FunctionRef)theEObject;
        T result = caseFunctionRef(functionRef);
        if (result == null) result = caseExpression(functionRef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.ARBITRARY_NAME:
      {
        ArbitraryName arbitraryName = (ArbitraryName)theEObject;
        T result = caseArbitraryName(arbitraryName);
        if (result == null) result = caseExpression(arbitraryName);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.UNIT_NAME:
      {
        UnitName unitName = (UnitName)theEObject;
        T result = caseUnitName(unitName);
        if (result == null) result = caseExpression(unitName);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.INT_LITERAL:
      {
        IntLiteral intLiteral = (IntLiteral)theEObject;
        T result = caseIntLiteral(intLiteral);
        if (result == null) result = caseTerminalExpression(intLiteral);
        if (result == null) result = caseExpression(intLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.DOUBLE_LITERAL:
      {
        DoubleLiteral doubleLiteral = (DoubleLiteral)theEObject;
        T result = caseDoubleLiteral(doubleLiteral);
        if (result == null) result = caseTerminalExpression(doubleLiteral);
        if (result == null) result = caseExpression(doubleLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.COLOR_LITERAL:
      {
        ColorLiteral colorLiteral = (ColorLiteral)theEObject;
        T result = caseColorLiteral(colorLiteral);
        if (result == null) result = caseTerminalExpression(colorLiteral);
        if (result == null) result = caseExpression(colorLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.STRING_LITERAL:
      {
        StringLiteral stringLiteral = (StringLiteral)theEObject;
        T result = caseStringLiteral(stringLiteral);
        if (result == null) result = caseTerminalExpression(stringLiteral);
        if (result == null) result = caseExpression(stringLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case GamlPackage.BOOLEAN_LITERAL:
      {
        BooleanLiteral booleanLiteral = (BooleanLiteral)theEObject;
        T result = caseBooleanLiteral(booleanLiteral);
        if (result == null) result = caseTerminalExpression(booleanLiteral);
        if (result == null) result = caseExpression(booleanLiteral);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModel(Model object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Import</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseImport(Import object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Lang Def</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Lang Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlLangDef(GamlLangDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Def Binary Op</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Def Binary Op</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefBinaryOp(DefBinaryOp object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Def Reserved</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Def Reserved</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefReserved(DefReserved object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Def Unary</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Def Unary</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefUnary(DefUnary object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Statement</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Statement</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStatement(Statement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Definition</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Definition</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefinition(Definition object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Facet Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Facet Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFacetRef(FacetRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Facet Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Facet Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlFacetRef(GamlFacetRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function Gaml Facet Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function Gaml Facet Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunctionGamlFacetRef(FunctionGamlFacetRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFacetExpr(FacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Definition Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Definition Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDefinitionFacetExpr(DefinitionFacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Name Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Name Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNameFacetExpr(NameFacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Returns Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Returns Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReturnsFacetExpr(ReturnsFacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Action Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Action Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseActionFacetExpr(ActionFacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function Facet Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunctionFacetExpr(FunctionFacetExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Block</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Block</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBlock(Block object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpression(Expression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Variable Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Variable Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVariableRef(VariableRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Var Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Var Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlVarRef(GamlVarRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Terminal Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Terminal Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTerminalExpression(TerminalExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Tern Exp</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Tern Exp</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTernExp(TernExp object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Pair Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Pair Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePairExpr(PairExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Binary Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Binary Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlBinaryExpr(GamlBinaryExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unit Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unit Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlUnitExpr(GamlUnitExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unary Expr</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unary Expr</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGamlUnaryExpr(GamlUnaryExpr object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Member Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Member Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMemberRef(MemberRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Array</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Array</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArray(Array object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Point</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Point</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePoint(Point object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function Ref</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function Ref</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunctionRef(FunctionRef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Arbitrary Name</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Arbitrary Name</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseArbitraryName(ArbitraryName object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unit Name</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unit Name</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnitName(UnitName object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Int Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Int Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntLiteral(IntLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Double Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Double Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDoubleLiteral(DoubleLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Color Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Color Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseColorLiteral(ColorLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>String Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>String Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStringLiteral(StringLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Boolean Literal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Boolean Literal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBooleanLiteral(BooleanLiteral object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //GamlSwitch
