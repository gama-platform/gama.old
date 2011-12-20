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
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see msi.gama.lang.gaml.gaml.GamlPackage
 * @generated
 */
public interface GamlFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  GamlFactory eINSTANCE = msi.gama.lang.gaml.gaml.impl.GamlFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Model</em>'.
   * @generated
   */
  Model createModel();

  /**
   * Returns a new object of class '<em>Import</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Import</em>'.
   * @generated
   */
  Import createImport();

  /**
   * Returns a new object of class '<em>Lang Def</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Lang Def</em>'.
   * @generated
   */
  GamlLangDef createGamlLangDef();

  /**
   * Returns a new object of class '<em>Def Keyword</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Def Keyword</em>'.
   * @generated
   */
  DefKeyword createDefKeyword();

  /**
   * Returns a new object of class '<em>Block</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Block</em>'.
   * @generated
   */
  GamlBlock createGamlBlock();

  /**
   * Returns a new object of class '<em>Def Facet</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Def Facet</em>'.
   * @generated
   */
  DefFacet createDefFacet();

  /**
   * Returns a new object of class '<em>Def Binary Op</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Def Binary Op</em>'.
   * @generated
   */
  DefBinaryOp createDefBinaryOp();

  /**
   * Returns a new object of class '<em>Def Reserved</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Def Reserved</em>'.
   * @generated
   */
  DefReserved createDefReserved();

  /**
   * Returns a new object of class '<em>Def Unit</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Def Unit</em>'.
   * @generated
   */
  DefUnit createDefUnit();

  /**
   * Returns a new object of class '<em>Abstract Gaml Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Abstract Gaml Ref</em>'.
   * @generated
   */
  AbstractGamlRef createAbstractGamlRef();

  /**
   * Returns a new object of class '<em>Keyword Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Keyword Ref</em>'.
   * @generated
   */
  GamlKeywordRef createGamlKeywordRef();

  /**
   * Returns a new object of class '<em>Facet Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Facet Ref</em>'.
   * @generated
   */
  GamlFacetRef createGamlFacetRef();

  /**
   * Returns a new object of class '<em>Binar Op Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Binar Op Ref</em>'.
   * @generated
   */
  GamlBinarOpRef createGamlBinarOpRef();

  /**
   * Returns a new object of class '<em>Unit Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unit Ref</em>'.
   * @generated
   */
  GamlUnitRef createGamlUnitRef();

  /**
   * Returns a new object of class '<em>Reserved Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Reserved Ref</em>'.
   * @generated
   */
  GamlReservedRef createGamlReservedRef();

  /**
   * Returns a new object of class '<em>Statement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Statement</em>'.
   * @generated
   */
  Statement createStatement();

  /**
   * Returns a new object of class '<em>Sub Statement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Sub Statement</em>'.
   * @generated
   */
  SubStatement createSubStatement();

  /**
   * Returns a new object of class '<em>Set Eval</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Set Eval</em>'.
   * @generated
   */
  SetEval createSetEval();

  /**
   * Returns a new object of class '<em>Definition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Definition</em>'.
   * @generated
   */
  Definition createDefinition();

  /**
   * Returns a new object of class '<em>Evaluation</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Evaluation</em>'.
   * @generated
   */
  Evaluation createEvaluation();

  /**
   * Returns a new object of class '<em>Facet Expr</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Facet Expr</em>'.
   * @generated
   */
  FacetExpr createFacetExpr();

  /**
   * Returns a new object of class '<em>Block</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Block</em>'.
   * @generated
   */
  Block createBlock();

  /**
   * Returns a new object of class '<em>Abstract Definition</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Abstract Definition</em>'.
   * @generated
   */
  AbstractDefinition createAbstractDefinition();

  /**
   * Returns a new object of class '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression</em>'.
   * @generated
   */
  Expression createExpression();

  /**
   * Returns a new object of class '<em>Point</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Point</em>'.
   * @generated
   */
  Point createPoint();

  /**
   * Returns a new object of class '<em>Matrix</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Matrix</em>'.
   * @generated
   */
  Matrix createMatrix();

  /**
   * Returns a new object of class '<em>Row</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Row</em>'.
   * @generated
   */
  Row createRow();

  /**
   * Returns a new object of class '<em>Variable Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Variable Ref</em>'.
   * @generated
   */
  VariableRef createVariableRef();

  /**
   * Returns a new object of class '<em>Terminal Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Terminal Expression</em>'.
   * @generated
   */
  TerminalExpression createTerminalExpression();

  /**
   * Returns a new object of class '<em>Assign Plus</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Assign Plus</em>'.
   * @generated
   */
  AssignPlus createAssignPlus();

  /**
   * Returns a new object of class '<em>Assign Min</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Assign Min</em>'.
   * @generated
   */
  AssignMin createAssignMin();

  /**
   * Returns a new object of class '<em>Assign Mult</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Assign Mult</em>'.
   * @generated
   */
  AssignMult createAssignMult();

  /**
   * Returns a new object of class '<em>Assign Div</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Assign Div</em>'.
   * @generated
   */
  AssignDiv createAssignDiv();

  /**
   * Returns a new object of class '<em>Ternary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Ternary</em>'.
   * @generated
   */
  Ternary createTernary();

  /**
   * Returns a new object of class '<em>Or</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or</em>'.
   * @generated
   */
  Or createOr();

  /**
   * Returns a new object of class '<em>And</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And</em>'.
   * @generated
   */
  And createAnd();

  /**
   * Returns a new object of class '<em>Rel Not Eq</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Not Eq</em>'.
   * @generated
   */
  RelNotEq createRelNotEq();

  /**
   * Returns a new object of class '<em>Rel Eq</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Eq</em>'.
   * @generated
   */
  RelEq createRelEq();

  /**
   * Returns a new object of class '<em>Rel Eq Eq</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Eq Eq</em>'.
   * @generated
   */
  RelEqEq createRelEqEq();

  /**
   * Returns a new object of class '<em>Rel Lt Eq</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Lt Eq</em>'.
   * @generated
   */
  RelLtEq createRelLtEq();

  /**
   * Returns a new object of class '<em>Rel Gt Eq</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Gt Eq</em>'.
   * @generated
   */
  RelGtEq createRelGtEq();

  /**
   * Returns a new object of class '<em>Rel Lt</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Lt</em>'.
   * @generated
   */
  RelLt createRelLt();

  /**
   * Returns a new object of class '<em>Rel Gt</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Rel Gt</em>'.
   * @generated
   */
  RelGt createRelGt();

  /**
   * Returns a new object of class '<em>Pair</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pair</em>'.
   * @generated
   */
  Pair createPair();

  /**
   * Returns a new object of class '<em>Plus</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Plus</em>'.
   * @generated
   */
  Plus createPlus();

  /**
   * Returns a new object of class '<em>Minus</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Minus</em>'.
   * @generated
   */
  Minus createMinus();

  /**
   * Returns a new object of class '<em>Multi</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Multi</em>'.
   * @generated
   */
  Multi createMulti();

  /**
   * Returns a new object of class '<em>Div</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Div</em>'.
   * @generated
   */
  Div createDiv();

  /**
   * Returns a new object of class '<em>Binary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Binary</em>'.
   * @generated
   */
  GamlBinary createGamlBinary();

  /**
   * Returns a new object of class '<em>Pow</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Pow</em>'.
   * @generated
   */
  Pow createPow();

  /**
   * Returns a new object of class '<em>Unit</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unit</em>'.
   * @generated
   */
  Unit createUnit();

  /**
   * Returns a new object of class '<em>Unary</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Unary</em>'.
   * @generated
   */
  GamlUnary createGamlUnary();

  /**
   * Returns a new object of class '<em>Member Ref P</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Member Ref P</em>'.
   * @generated
   */
  MemberRefP createMemberRefP();

  /**
   * Returns a new object of class '<em>Member Ref R</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Member Ref R</em>'.
   * @generated
   */
  MemberRefR createMemberRefR();

  /**
   * Returns a new object of class '<em>Function Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Function Ref</em>'.
   * @generated
   */
  FunctionRef createFunctionRef();

  /**
   * Returns a new object of class '<em>Array Ref</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Array Ref</em>'.
   * @generated
   */
  ArrayRef createArrayRef();

  /**
   * Returns a new object of class '<em>Int Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Int Literal</em>'.
   * @generated
   */
  IntLiteral createIntLiteral();

  /**
   * Returns a new object of class '<em>Double Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Double Literal</em>'.
   * @generated
   */
  DoubleLiteral createDoubleLiteral();

  /**
   * Returns a new object of class '<em>Color Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Color Literal</em>'.
   * @generated
   */
  ColorLiteral createColorLiteral();

  /**
   * Returns a new object of class '<em>String Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Literal</em>'.
   * @generated
   */
  StringLiteral createStringLiteral();

  /**
   * Returns a new object of class '<em>Boolean Literal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Boolean Literal</em>'.
   * @generated
   */
  BooleanLiteral createBooleanLiteral();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  GamlPackage getGamlPackage();

} //GamlFactory
