/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Point</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Point#getX <em>X</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Point#getY <em>Y</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getPoint()
 * @model
 * @generated
 */
public interface Point extends Expression
{
  /**
   * Returns the value of the '<em><b>X</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>X</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>X</em>' containment reference.
   * @see #setX(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getPoint_X()
   * @model containment="true"
   * @generated
   */
  Expression getX();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Point#getX <em>X</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>X</em>' containment reference.
   * @see #getX()
   * @generated
   */
  void setX(Expression value);

  /**
   * Returns the value of the '<em><b>Y</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Y</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Y</em>' containment reference.
   * @see #setY(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getPoint_Y()
   * @model containment="true"
   * @generated
   */
  Expression getY();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Point#getY <em>Y</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Y</em>' containment reference.
   * @see #getY()
   * @generated
   */
  void setY(Expression value);

} // Point
