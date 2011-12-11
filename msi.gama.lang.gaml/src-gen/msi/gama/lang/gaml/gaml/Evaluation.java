/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Evaluation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Evaluation#getVar <em>Var</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getEvaluation()
 * @model
 * @generated
 */
public interface Evaluation extends SubStatement
{
  /**
   * Returns the value of the '<em><b>Var</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Var</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Var</em>' containment reference.
   * @see #setVar(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getEvaluation_Var()
   * @model containment="true"
   * @generated
   */
  Expression getVar();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Evaluation#getVar <em>Var</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Var</em>' containment reference.
   * @see #getVar()
   * @generated
   */
  void setVar(Expression value);

} // Evaluation
