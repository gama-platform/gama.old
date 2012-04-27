/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Arg Pair Expr</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.ArgPairExpr#getArg <em>Arg</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getArgPairExpr()
 * @model
 * @generated
 */
public interface ArgPairExpr extends PairExpr
{
  /**
   * Returns the value of the '<em><b>Arg</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Arg</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Arg</em>' attribute.
   * @see #setArg(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getArgPairExpr_Arg()
   * @model
   * @generated
   */
  String getArg();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.ArgPairExpr#getArg <em>Arg</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Arg</em>' attribute.
   * @see #getArg()
   * @generated
   */
  void setArg(String value);

} // ArgPairExpr
