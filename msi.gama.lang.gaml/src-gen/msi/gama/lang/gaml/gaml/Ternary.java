/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ternary</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Ternary#getCondition <em>Condition</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Ternary#getIfTrue <em>If True</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Ternary#getIfFalse <em>If False</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getTernary()
 * @model
 * @generated
 */
public interface Ternary extends Expression
{
  /**
   * Returns the value of the '<em><b>Condition</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Condition</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Condition</em>' containment reference.
   * @see #setCondition(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTernary_Condition()
   * @model containment="true"
   * @generated
   */
  Expression getCondition();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Ternary#getCondition <em>Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Condition</em>' containment reference.
   * @see #getCondition()
   * @generated
   */
  void setCondition(Expression value);

  /**
   * Returns the value of the '<em><b>If True</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>If True</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>If True</em>' containment reference.
   * @see #setIfTrue(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTernary_IfTrue()
   * @model containment="true"
   * @generated
   */
  Expression getIfTrue();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Ternary#getIfTrue <em>If True</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>If True</em>' containment reference.
   * @see #getIfTrue()
   * @generated
   */
  void setIfTrue(Expression value);

  /**
   * Returns the value of the '<em><b>If False</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>If False</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>If False</em>' containment reference.
   * @see #setIfFalse(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTernary_IfFalse()
   * @model containment="true"
   * @generated
   */
  Expression getIfFalse();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Ternary#getIfFalse <em>If False</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>If False</em>' containment reference.
   * @see #getIfFalse()
   * @generated
   */
  void setIfFalse(Expression value);

} // Ternary
